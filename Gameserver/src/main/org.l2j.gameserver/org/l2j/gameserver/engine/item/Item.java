/*
 * Copyright © 2019-2021 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.engine.item;

import io.github.joealisson.primitive.HashIntSet;
import io.github.joealisson.primitive.IntSet;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.database.dao.ItemDAO;
import org.l2j.gameserver.data.database.data.ItemData;
import org.l2j.gameserver.data.database.data.ItemOnGroundData;
import org.l2j.gameserver.data.xml.impl.AugmentationEngine;
import org.l2j.gameserver.data.xml.impl.BlessedItemOptionsData;
import org.l2j.gameserver.data.xml.impl.EnchantItemOptionsData;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.gameserver.enums.ItemSkillType;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.instancemanager.ItemsOnGroundManager;
import org.l2j.gameserver.instancemanager.SiegeGuardManager;
import org.l2j.gameserver.model.DropProtection;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.VariationInstance;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.conditions.Condition;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerAugment;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerItemPickup;
import org.l2j.gameserver.model.events.impl.item.OnItemBypassEvent;
import org.l2j.gameserver.model.events.impl.item.OnItemTalk;
import org.l2j.gameserver.model.holders.ItemSkillHolder;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.item.*;
import org.l2j.gameserver.model.item.container.WarehouseType;
import org.l2j.gameserver.model.item.type.*;
import org.l2j.gameserver.model.options.BlessedOptions;
import org.l2j.gameserver.model.options.EnchantOptions;
import org.l2j.gameserver.model.options.Options;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.*;
import org.l2j.gameserver.settings.CharacterSettings;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.util.GMAudit;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.world.WorldRegion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static java.lang.Math.max;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.commons.util.Util.doIfNonNull;

/**
 * @author JoeAlisson
 */
public final class Item extends WorldObject {

    private static final Logger LOGGER = LoggerFactory.getLogger(Item.class);
    private static final Logger LOG_ITEMS = LoggerFactory.getLogger("item");

    public static final int[] DEFAULT_ENCHANT_OPTIONS = new int[]{0, 0, 0};

    private final ItemTemplate template;
    private final ItemData data;
    private final ReentrantLock dbLock = new ReentrantLock();
    private final DropProtection dropProtection = new DropProtection();
    private final List<Options> enchantOptions = new ArrayList<>();

    private ItemChangeType lastChange = ItemChangeType.MODIFIED;
    private VariationInstance augmentation = null;
    private EnsoulOption ensoulOption;
    private EnsoulOption ensoulSpecialOption;
    private ScheduledFuture<?> itemLootShedule;
    private ScheduledFuture<?> lifeTimeTask;
    private int isBlessed;
    private final List<BlessedOptions> blessedOptions = Collections.synchronizedList(new ArrayList<BlessedOptions>());
    private int dropperObjectId;

    private long dropTime;
    private boolean published;
    private boolean isProtected;
    private boolean existsInDb; // if a record exists in DB.
    private boolean storedInDb; // if DB data is up-to-date.

    /**
     * Constructor of the Item from the objetId and the description of the item given by the ItemTemplate.
     *
     * @param objectId : int designating the ID of the object in the world
     * @param template : ItemTemplate containing informations of the item
     */
    Item(int objectId, ItemTemplate template) {
        super(objectId);
        this.template = template;
        setInstanceType(InstanceType.L2ItemInstance);
        setName(template.getName());
        data = ItemData.of(objectId, template.getId());
        data.setLoc(ItemLocation.VOID);
        data.setTime(template.getDuration() == -1 ? -1 : System.currentTimeMillis() + (this.template.getDuration() * 60 * 1000));
        scheduleLifeTimeTask();
    }

    public Item(ItemOnGroundData data) {
        this(ItemData.of(data));
        dropTime = data.getDropTime();
        isProtected = dropTime == -1;
        setSpawned(true);
        setXYZ(data.getX(), data.getY(), data.getZ());
        existsInDb = false;
        storedInDb = false;
    }

    public Item(ItemData data) {
        super(data.getObjectId());
        template = ItemEngine.getInstance().getTemplate(data.getItemId());
        if (isNull(template)) {
            throw new IllegalArgumentException("itemId is not a valid identifier");
        }
        setInstanceType(InstanceType.L2ItemInstance);
        setName(template.getName());
        this.data = data;

        existsInDb = true;
        storedInDb = true;

        if(isEquipable()) {
            restoreAugmentation();
            restoreSpecialAbilities(data);
        }

        setIsBlessed(data.getIsBlessed());

        scheduleLifeTimeTask();

    }

    private void restoreAugmentation() {
        doIfNonNull(getDAO(ItemDAO.class).findItemVariationByItem(objectId), data -> augmentation = new VariationInstance(data));
    }

    private void restoreSpecialAbilities(ItemData data) {
        if(data.getEnsoul() != 0) {
            ensoulOption = ItemEnsoulEngine.getInstance().getOption(data.getEnsoul());
        }
        if(data.getSpecialEnsoul() != 0) {
            ensoulSpecialOption = ItemEnsoulEngine.getInstance().getOption(data.getSpecialEnsoul());
        }
    }

    /**
     * Remove a Item from the world and send server->client GetItem packets.<BR>
     * <BR>
     * <B><U> Actions</U> :</B><BR>
     * <BR>
     * <li>Send a Server->Client Packet GetItem to player that pick up and its _knowPlayers member</li>
     * <li>Remove the WorldObject from the world</li><BR>
     * <BR>
     * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T REMOVE the object from _allObjects of World </B></FONT><BR>
     * <BR>
     * <B><U> Example of use </U> :</B><BR>
     * <BR>
     * <li>Do Pickup Item : PCInstance and Pet</li><BR>
     * <BR>
     *
     * @param creature Character that pick up the item
     */
    public final void pickupMe(Creature creature) {
        final WorldRegion oldRegion = getWorldRegion();
        creature.broadcastPacket(new GetItem(this, creature.getObjectId()));
        synchronized (this) {
            setSpawned(false);
        }

        // if this item is a mercenary ticket, remove the spawns!
        final Castle castle = CastleManager.getInstance().getCastle(this);
        if (nonNull(castle) && nonNull(SiegeGuardManager.getInstance().getSiegeGuardByItem(castle.getId(), getId()))) {
            SiegeGuardManager.getInstance().removeTicket(this);
            ItemsOnGroundManager.getInstance().removeObject(this);
        }

        World.getInstance().removeVisibleObject(this, oldRegion);

        if (creature instanceof Player player){
            EventDispatcher.getInstance().notifyEventAsync(new OnPlayerItemPickup(player, this), template);
        }
    }

    public void changeOwner(String process, int ownerId, Player creator, WorldObject reference) {
        changeOwner(ownerId);

        var generalSettings = getSettings(GeneralSettings.class);
        if (generalSettings.logItems()) {
            if (!generalSettings.smallLogItems() || template.isEquipable() || template.getId() == CommonItem.ADENA) {
                LOG_ITEMS.info("SETOWNER: {}, +{} item {} ({}), {}, {}", process, this, data.getEnchantLevel(), data.getCount(), creator, reference);
            }
        }

        auditItem(process, getCount(), creator, reference);
    }

    public void changeOwner(int ownerId) {
        if (ownerId == data.getOwnerId()) {
            return;
        }

        removeSkillsFromOwner();

        data.setOwnerId(ownerId);
        storedInDb = false;

        giveSkillsToOwner();
    }

    private void removeSkillsFromOwner() {
        if (!hasPassiveSkills()) {
            return;
        }

        doIfNonNull(getActingPlayer(), player -> {

            IntSet removedSkills = new HashIntSet();
            template.forEachSkill(ItemSkillType.NORMAL, Skill::isPassive, skill -> {
                var oldSkill = player.removeSkill(skill, false, true);
                if(nonNull(oldSkill)) {
                    removedSkills.add(oldSkill.getId());
                }
            });

            if(!removedSkills.isEmpty()) {
                player.getInventory().forEachItem(hasRemovedSkill(removedSkills), Item::giveSkillsToOwner);
            }
        });
    }

    public void giveSkillsToOwner() {
        if (!hasPassiveSkills()) {
            return;
        }

        doIfNonNull(getActingPlayer(), player -> template.forEachSkill(ItemSkillType.NORMAL, holder -> {
            final Skill skill = holder.getSkill();
            if (skill.isPassive()) {
                player.addSkill(skill, false);
            }
        }));
    }

    public void changeItemLocation(ItemLocation loc) {
        changeItemLocation(loc, 0);
    }

    public void changeItemLocation(ItemLocation loc, int locData) {
        if ((loc == data.getLoc()) && (locData == data.getLocData())) {
            return;
        }

        removeSkillsFromOwner();

        data.setLoc(loc);
        data.setLocData(locData);
        storedInDb = false;

        giveSkillsToOwner();
    }

    public void changeCount(String process, long count, Player creator, Object reference) {
        if (count == 0) {
            return;
        }
        final long old = data.getCount();
        final long max = data.getItemId() == CommonItem.ADENA ? getSettings(CharacterSettings.class).maxAdena() : Long.MAX_VALUE;

        if (count > 0 && data.getCount() > (max - count)) {
            setCount(max);
        } else {
            setCount(data.getCount() + count);
        }

        storedInDb = false;

        var generalSettings = getSettings(GeneralSettings.class);
        if (generalSettings.logItems() && (process != null)) {
            if (!generalSettings.smallLogItems() || template.isEquipable() || template.getId() == CommonItem.ADENA) {
                LOG_ITEMS.info("CHANGE: {}, +{} item {} ({}), prev count {}, {}, {}", process, this, data.getEnchantLevel(), data.getCount(), old, creator, reference);
            }
        }

        auditItem(process, count, creator, reference);
    }

    private void auditItem(String process, long count, Player creator, Object reference) {
        if (nonNull(creator) && creator.isGM() && getSettings(GeneralSettings.class).auditGM()) {
            String referenceName = "no-reference";
            if (reference instanceof WorldObject) {
                referenceName = (((WorldObject) reference).getName() != null ? ((WorldObject) reference).getName() : "no-name");
            } else if (reference instanceof String) {
                referenceName = (String) reference;
            }
            final String targetName = (creator.getTarget() != null ? creator.getTarget().getName() : "no-target");
            GMAudit.auditGMAction(creator.getName() + " [" + creator.getObjectId() + "]", process + "(id: " + data.getItemId() + " objId: " + getObjectId() + " name: " + getName() + " count: " + count + ")", targetName, "WorldObject referencing this action is: " + referenceName);
        }
    }

    public void changeCountWithoutTrace(long count, Player creator, Object reference) {
        changeCount(null, count, creator, reference);
    }

    public boolean isAvailable(Player player, boolean allowAdena, boolean allowNonTradeable) {
        final Summon pet = player.getPet();

        return !isEquipped() && !isQuestItem()
                && ( (template.getType2() != ItemTemplate.TYPE2_MONEY) || (template.getType1() != ItemTemplate.TYPE1_SHIELD_ARMOR)) // not money, not shield
                && ((pet == null) || (getObjectId() != pet.getControlObjectId())) // Not Control item of currently summoned pet
                && !player.isProcessingItem(getObjectId()) // Not momentarily used enchant scroll
                && (allowAdena || (data.getItemId() != CommonItem.ADENA)) // Not Adena
                && (!player.isCastingNow(s -> s.getSkill().getItemConsumeId() != data.getItemId()))
                && (allowNonTradeable || (isTradeable() && (!((template.getItemType() == EtcItemType.PET_COLLAR) && player.havePetInvItems()))));
    }

    public void updateEnchantLevel(int value) {
        changeEnchantLevel(data.getEnchantLevel() + value);
    }

    public void changeEnchantLevel(int enchantLevel) {
        if (data.getEnchantLevel() == enchantLevel) {
            return;
        }
        clearEnchantStats();
        data.setEnchantLevel(enchantLevel);
        applyEnchantStats();
        storedInDb = false;
    }

    public void applyAugmentationBonus(Player player) {
        if(nonNull(augmentation)) {
            augmentation.applyBonus(player);
        }
    }

    public void removeAugmentationBonus(Player player) {
        if(nonNull(augmentation)) {
            augmentation.removeBonus(player);
        }
    }

    public boolean setAugmentation(VariationInstance augmentation, boolean updateDatabase) {
        if (nonNull(this.augmentation)) {
            LOGGER.warn("Augment set for {}, owner{}", this, getActingPlayer());
            return false;
        }

        this.augmentation = augmentation;
        if (updateDatabase) {
            updateItemVariation();
        }
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerAugment(getActingPlayer(), this, augmentation, true), template);
        return true;
    }

    private void updateItemVariation() {
        if(nonNull(augmentation)) {
            getDAO(ItemDAO.class).save(augmentation.getData());
        } else {
            getDAO(ItemDAO.class).deleteVariations(objectId);
        }
    }

    public void removeAugmentation() {
        if (isNull(augmentation)) {
            return;
        }

        final VariationInstance augment = augmentation;
        augmentation = null;

        getDAO(ItemDAO.class).deleteVariations(objectId);
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerAugment(getActingPlayer(), this, augment, false), template);
    }

    public final void dropMe(Creature dropper, int x, int y, int z) {
        ThreadPool.execute(new ItemDropTask(this, dropper, x, y, z));
    }

    public void updateDatabase() {
        updateDatabase(false);
    }

    public void updateDatabase(boolean force) {
        dbLock.lock();

        try {
            if (existsInDb) {
                if (cannotBeStored()) {
                    removeFromDb();
                } else if (!Config.LAZY_ITEMS_UPDATE || force) {
                    updateInDb();
                }
            } else {
                if (cannotBeStored()) {
                    return;
                }
                insertIntoDb();
            }
        } finally {
            dbLock.unlock();
        }
    }

    private boolean cannotBeStored() {
        return objectId == 0 || data.getOwnerId() == 0 || data.getLoc() == ItemLocation.VOID || data.getLoc() == ItemLocation.REFUND || (data.getCount() == 0 && data.getLoc() != ItemLocation.LEASE);
    }

    private void removeFromDb() {
        getDAO(ItemDAO.class).deleteItem(objectId);
        existsInDb = false;
        storedInDb = false;
    }

    private void updateInDb() {
        if (storedInDb) {
            return;
        }
        getDAO(ItemDAO.class).save(data);
        updateItemVariation();
    }

    private void insertIntoDb() {
        getDAO(ItemDAO.class).save(data);
        existsInDb = true;
        storedInDb = true;
        if (nonNull(augmentation)) {
            updateItemVariation();
        }
    }

    public void resetOwnerTimer() {
        if (itemLootShedule != null) {
            itemLootShedule.cancel(true);
            itemLootShedule = null;
        }
    }

    public boolean isAvailable() {
        if (!template.isConditionAttached() || data.getLoc() == ItemLocation.PET || data.getLoc() == ItemLocation.PET_EQUIP) {
            return true;
        }

        Creature owner = getActingPlayer();
        if (nonNull(owner)) {
            for (Condition condition : template.getConditions()) {
                if (condition == null) {
                    continue;
                }
                try {
                    if (!condition.test(owner, owner, null, null)) {
                        return false;
                    }
                }
                catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
        return true;
    }

    public void endOfLife() {
        final Player player = getActingPlayer();
        if (nonNull(player)) {
            if (isEquipped()) {
                final InventoryUpdate iu = new InventoryUpdate(player.getInventory().unEquipItemInSlotAndRecord(InventorySlot.fromId(getLocationSlot())));
                player.sendInventoryUpdate(iu);
            }

            if (data.getLoc() != ItemLocation.WAREHOUSE) {
                player.getInventory().destroyItem("Item", this, player, null);
                player.sendInventoryUpdate(new InventoryUpdate(this));
            } else {
                player.getWarehouse().destroyItem("Item", this, player, null);
            }
            player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_EXPIRED).addItemName(data.getItemId()));
        }
    }

    public void scheduleLifeTimeTask() {
        if (!isTimeLimitedItem()) {
            return;
        }

        if (nonNull(lifeTimeTask)) {
            lifeTimeTask.cancel(true);
        }
        lifeTimeTask = ThreadPool.schedule(this::endOfLife, Math.max(getRemainingTime(), 2000));
    }

    @Override
    public void sendInfo(Player activeChar) {
        if (dropperObjectId != 0) {
            activeChar.sendPacket(new DropItem(this, dropperObjectId));
        } else {
            activeChar.sendPacket(new SpawnItem(this));
        }
    }

    @Override
    public boolean decayMe() {
        if (getSettings(GeneralSettings.class).saveDroppedItems()) {
            ItemsOnGroundManager.getInstance().removeObject(this);
        }

        return super.decayMe();
    }

    private Predicate<Item> hasRemovedSkill(IntSet removedSkills) {
        return item -> item != this && item.hasPassiveSkills() && item.template.checkAnySkill(ItemSkillType.NORMAL, sk -> removedSkills.contains(sk.getSkillId()) );
    }

    private boolean hasPassiveSkills() {
        return (template.getItemType() == EtcItemType.RUNE || template.getItemType() == EtcItemType.NONE) && (data.getLoc() == ItemLocation.INVENTORY) && (data.getOwnerId() > 0) && (template.getSkills(ItemSkillType.NORMAL) != null);
    }

    public void onBypassFeedback(Player player, String command) {
        if (command.startsWith("Quest")) {
            final String questName = command.substring(6);
            String event = null;
            final int idx = questName.indexOf(' ');
            if (idx > 0) {
                event = questName.substring(idx).trim();
            }

            if (event != null) {
                EventDispatcher.getInstance().notifyEventAsync(new OnItemBypassEvent(this, player, event), template);
            } else {
                EventDispatcher.getInstance().notifyEventAsync(new OnItemTalk(this, player), template);
            }
        }
    }

    public int[] getEnchantOptions() {
        final EnchantOptions op = EnchantItemOptionsData.getInstance().getOptions(this);
        if (op != null) {
            return op.getOptions();
        }
        return DEFAULT_ENCHANT_OPTIONS;
    }

    public EnsoulOption getSpecialAbility() {
        return ensoulOption;
    }

    public EnsoulOption getAdditionalSpecialAbility() {
        return ensoulSpecialOption;
    }

    /**
     * Return blessed weapon or not
     *
     * @return boolean
     */
    public int getIsBlessed() {
        return this.isBlessed;
    }

    public void setIsBlessed(int blessed) {
        this.data.setIsBlessed(blessed);
        getDAO(ItemDAO.class).updateIsBlessed(objectId, blessed);
        this.isBlessed = blessed;
    }

    public void addSpecialAbility(EnsoulOption option, EnsoulType type, boolean updateInDB) {
        if (type == EnsoulType.COMMON)  {
            if(nonNull(ensoulOption)) {
                removeSpecialAbility(ensoulOption, EnsoulType.COMMON);
            }
            ensoulOption = option;
            data.setEnsoul(option.id());
            if(updateInDB) {
                getDAO(ItemDAO.class).updateEnsoul(objectId, ensoulOption.id());
            }
        } else if (type == EnsoulType.SPECIAL) {
            if(nonNull(ensoulSpecialOption)) {
                removeSpecialAbility(ensoulSpecialOption, EnsoulType.SPECIAL);
            }
            ensoulSpecialOption = option;
            data.setSpecialEnsoul(option.id());
            if(updateInDB) {
                getDAO(ItemDAO.class).updateSpecialEnsoul(objectId, ensoulSpecialOption.id());
            }
        }
    }

    private void removeSpecialAbility(EnsoulOption option, EnsoulType type) {
        final Player player = getActingPlayer();
        if (nonNull(player)) {
            player.removeSkill(option.skill().getSkillId());
        }
        if (type == EnsoulType.COMMON) {
            getDAO(ItemDAO.class).updateEnsoul(objectId, 0);
        } else if (type == EnsoulType.SPECIAL) {
            getDAO(ItemDAO.class).updateSpecialEnsoul(objectId, 0);
        }
    }

    public void removeSpecialAbility(EnsoulType type) {
        if (type == EnsoulType.COMMON) {
            if (nonNull(ensoulOption)) {
                removeSpecialAbility(ensoulOption, EnsoulType.COMMON);
                ensoulOption = null;
                data.setEnsoul(0);
            }
        } else if (type == EnsoulType.SPECIAL) {
            if(nonNull(ensoulSpecialOption)) {
                removeSpecialAbility(ensoulSpecialOption, EnsoulType.SPECIAL);
                ensoulSpecialOption = null;
                data.setSpecialEnsoul(0);
            }
        }
    }

    public void clearSpecialAbilities() {
        clearSpecialAbility(ensoulOption);
        clearSpecialAbility(ensoulSpecialOption);
    }

    public void applyBlessedOptions(int enchant) {
        if (!isEquipped() || getIsBlessed() != 1) {
            return;
        }

        var options = BlessedItemOptionsData.getInstance().getBlessedOptions(this.template.getItemGrade(), (WeaponType) this.getItemType(), enchant);

        for(BlessedOptions blessedOption : options) {
            this.blessedOptions.add(blessedOption);
            applyBlessedOption(blessedOption);
        }
    }

    public void clearBlessedOptions() {
        synchronized(blessedOptions)
        {
            for(BlessedOptions blessedOption : blessedOptions) {
                clearBlessedOption(blessedOption);
            }
            blessedOptions.clear();
        }
    }

    public void applyBlessedOption(BlessedOptions option) {
        final Skill skill = option.getSkill().getSkill();

        if (skill != null) {
            final Player player = getActingPlayer();
            if (player != null) {
                if (player.getSkillLevel(skill.getId()) != skill.getLevel()) {
                    player.addSkill(skill, false);
                }
            }
        }
    }

    private void clearBlessedOption(BlessedOptions option) {
        final Skill skill = option.getSkill().getSkill();
        if (skill != null) {
            final Player player = getActingPlayer();
            if (player != null) {
                player.removeSkill(skill, false, true);
            }
        }
    }
    public void applySpecialAbilities() {
        if (!isEquipped()) {
            return;
        }

        applySpecialAbility(ensoulOption);
        applySpecialAbility(ensoulSpecialOption);
    }

    private void applySpecialAbility(EnsoulOption option) {
        if(isNull(option)) {
            return;
        }
        final Skill skill = option.toSkill();
        if (skill != null) {
            final Player player = getActingPlayer();
            if (player != null) {
                if (player.getSkillLevel(skill.getId()) != skill.getLevel()) {
                    player.addSkill(skill, false);
                }
            }
        }
    }

    private void clearSpecialAbility(EnsoulOption option) {
        if(isNull(option)) {
            return;
        }
        final Skill skill = option.toSkill();
        if (skill != null) {
            final Player player = getActingPlayer();
            if (player != null) {
                player.removeSkill(skill, false, true);
            }
        }
    }

    /**
     * Clears all the enchant bonuses if item is enchanted and containing bonuses for enchant value.
     */
    public void clearEnchantStats() {
        final Player player = getActingPlayer();
        if (player == null) {
            enchantOptions.clear();
            return;
        }

        for (Options op : enchantOptions) {
            op.remove(player);
        }
        enchantOptions.clear();
    }

    /**
     * Clears and applies all the enchant bonuses if item is enchanted and containing bonuses for enchant value.
     */
    public void applyEnchantStats() {
        final Player player = getActingPlayer();
        if (!isEquipped() || (player == null) || (getEnchantOptions() == DEFAULT_ENCHANT_OPTIONS)) {
            return;
        }

        for (int id : getEnchantOptions()) {
            final Options options = AugmentationEngine.getInstance().getOptions(id);
            if (options != null) {
                options.apply(player);
                enchantOptions.add(options);
            } else if (id != 0) {
                LOGGER.info("applyEnchantStats: Couldn't find option: " + id);
            }
        }
    }

    public void deleteMe() {
        if ((lifeTimeTask != null) && !lifeTimeTask.isDone()) {
            lifeTimeTask.cancel(false);
            lifeTimeTask = null;
        }
    }

    public int getOwnerId() {
        return data.getOwnerId();
    }

    public ItemLocation getItemLocation() {
        return data.getLoc();
    }

    public long getCount() {
        return data.getCount();
    }

    public void setCount(long count) {
        if (data.getCount() == count) {
            return;
        }

        data.setCount(max(0, count));
        storedInDb = false;
    }

    public boolean isEnchantable() {
        if (data.getLoc() == ItemLocation.INVENTORY || data.getLoc() == ItemLocation.PAPERDOLL) {
            return template.isEnchantable();
        }
        return false;
    }

    public boolean isEquipable() {
        return template instanceof EquipableItem;
    }

    public boolean isEquipped() {
        return data.getLoc() == ItemLocation.PAPERDOLL || data.getLoc() == ItemLocation.PET_EQUIP;
    }

    public int getLocationSlot() {
        return data.getLocData();
    }

    /**
     * @Deprecated
     */
    public ItemTemplate getTemplate() {
        return template;
    }

    public int getCustomType1() {
        return template.getType1();
    }

    public int getType2() {
        return template.getType2();
    }

    public long getDropTime() {
        return dropTime;
    }

    public void setDropTime(long time) {
        dropTime = time;
    }

    public ItemType getItemType() {
        return template.getItemType();
    }

    @Override
    public int getId() {
        return data.getItemId();
    }

    public int getDisplayId() {
        return template.getDisplayId();
    }

    public boolean isEtcItem() {
        return template instanceof EtcItem;
    }

    public boolean isWeapon() {
        return template instanceof Weapon;
    }

    public boolean isArmor() {
        return template instanceof Armor;
    }

    public EtcItem getEtcItem() {
        if (template instanceof EtcItem) {
            return (EtcItem) template;
        }
        return null;
    }

    public final int getCrystalCount() {
        return template.getCrystalCount(data.getEnchantLevel());
    }

    public long getReferencePrice() {
        return template.getReferencePrice();
    }

    public int getReuseDelay() {
        return template.getReuseDelay();
    }

    public int getSharedReuseGroup() {
        return template.getSharedReuseGroup();
    }

    public ItemChangeType getLastChange() {
        return lastChange;
    }

    public void setLastChange(ItemChangeType lastChange) {
        this.lastChange = lastChange;
    }

    public boolean isStackable() {
        return template.isStackable();
    }

    public boolean isDropable() {
        return !isAugmented() && template.isDropable();
    }

    public boolean isDestroyable() {
        return template.isDestroyable();
    }

    public boolean isTradeable() {
        return !isAugmented() && template.isTradeable();
    }

    public boolean isSellable() {
        return !isAugmented() && template.isSellable();
    }

    public boolean isDepositable(WarehouseType type) {
        if(isEquipped() || !template.isDepositable() ) {
            return false;
        }

        return switch (type) {
            case CLAN, CASTLE -> isTradeable();
            case FREIGHT -> isFreightable();
            default -> true;
        };
    }

    public boolean isPotion() {
        return template.isPotion();
    }

    public boolean isElixir() {
        return template.isElixir();
    }

    public boolean isScroll() {
        return template.isScroll();
    }

    public boolean isHeroItem() {
        return template.isHeroItem();
    }

    public int getEnchantLevel() {
        return data.getEnchantLevel();
    }

    public boolean isEnchanted() {
        return data.getEnchantLevel() > 0;
    }

    public boolean isAugmented() {
        return nonNull(augmentation);
    }

    public VariationInstance getAugmentation() {
        return augmentation;
    }

    @Override
    public boolean isAutoAttackable(Creature attacker) {
        return false;
    }

    @Override
    public String toString() {
        return template + "[" + getObjectId() + "]";
    }

    public ScheduledFuture<?> getItemLootShedule() {
        return itemLootShedule;
    }

    public void setItemLootShedule(ScheduledFuture<?> sf) {
        itemLootShedule = sf;
    }

    public boolean isProtected() {
        return isProtected;
    }

    public void setProtected(boolean isProtected) {
        this.isProtected = isProtected;
    }

    public boolean isTimeLimitedItem() {
        return data.getTime() > 0;
    }

    public long getRemainingTime() {
        return data.getTime() - System.currentTimeMillis();
    }

    public void setDropperObjectId(int id) {
        dropperObjectId = id;
    }

    public final DropProtection getDropProtection() {
        return dropProtection;
    }

    public boolean isPublished() {
        return published;
    }

    public void publish() {
        published = true;
    }

    public boolean isQuestItem() {
        return template.isQuestItem();
    }

    public boolean isFreightable() {
        return template.isFreightable();
    }

    @Override
    public Player getActingPlayer() {
        return World.getInstance().findPlayer(getOwnerId());
    }

    @Override
    public void setHeading(int heading) {
    }

    public int getEquipReuseDelay() {
        return template.getEquipReuseDelay();
    }

    public BodyPart getBodyPart() {
        return template instanceof EquipableItem ? template.getBodyPart() : BodyPart.NONE;
    }

    public int getItemMask() {
        return template.getItemMask();
    }

    public void forEachSkill(ItemSkillType type, Consumer<ItemSkillHolder> action) {
        template.forEachSkill(type, action);
    }

    public double getStats(Stat stat, int defaultValue) {
        return template.getStats(stat, defaultValue);
    }

    public boolean isAutoPotion() {
        return template instanceof EtcItem && ((EtcItem) template).isAutoPotion();
    }

    public boolean isAutoSupply() {
        return template instanceof EtcItem && ((EtcItem) template).isAutoSupply();
    }

    public ActionType getAction() {
        return template.getDefaultAction();
    }

    public List<ItemSkillHolder> getSkills(ItemSkillType type) {
        return template.getSkills(type);
    }

    public boolean isSelfResurrection() {
        return template instanceof EtcItem etcTemplate && etcTemplate.isSelfResurrection();
    }

    public CrystalType getCrystalType() {
        return template.getCrystalType();
    }

    public boolean isMagicWeapon() {
        return template instanceof Weapon w && w.isMagicWeapon();
    }

    public boolean isInfinite() {
        return template instanceof EtcItem etcItem && etcItem.isInfinite();
    }

    /**
     * Init a dropped Item and add it in the world as a visible object.<BR>
     * <BR>
     * <B><U> Actions</U> :</B><BR>
     * <BR>
     * <li>Set the x,y,z position of the Item dropped and update its _worldregion</li>
     * <li>Add the Item dropped to _visibleObjects of its WorldRegion</li>
     * <li>Add the Item dropped in the world as a <B>visible</B> object</li><BR>
     * <BR>
     * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T ADD the object to _allObjects of World </B></FONT><BR>
     * <BR>
     * <B><U> Example of use </U> :</B><BR>
     * <BR>
     * <li>Drop item</li>
     * <li>Call Pet</li><BR>
     */
    public class ItemDropTask implements Runnable {
        private final Creature _dropper;
        private final Item _item;
        private int _x, _y, _z;

        public ItemDropTask(Item item, Creature dropper, int x, int y, int z) {
            _x = x;
            _y = y;
            _z = z;
            _dropper = dropper;
            _item = item;
        }

        @Override
        public final void run() {
            if (_dropper != null) {
                final Instance instance = _dropper.getInstanceWorld();
                final Location dropDest = GeoEngine.getInstance().canMoveToTargetLoc(_dropper.getX(), _dropper.getY(), _dropper.getZ(), _x, _y, _z, instance);
                _x = dropDest.getX();
                _y = dropDest.getY();
                _z = dropDest.getZ();
                setInstance(instance); // Inherit instancezone when dropped in visible world
            } else {
                setInstance(null); // No dropper? Make it a global item...
            }

            synchronized (_item) {
                // Set the x,y,z position of the Item dropped and update its _worldregion
                _item.setSpawned(true);
                _item.setXYZ(_x, _y, _z);
            }

            _item.setDropTime(System.currentTimeMillis());
            _item.setDropperObjectId(_dropper != null ? _dropper.getObjectId() : 0); // Set the dropper Id for the knownlist packets in sendInfo

            // Add the Item dropped in the world as a visible object
            World.getInstance().addVisibleObject(_item, _item.getWorldRegion());
            if (getSettings(GeneralSettings.class).saveDroppedItems()) {
                ItemsOnGroundManager.getInstance().save(_item);
            }
            _item.setDropperObjectId(0); // Set the dropper Id back to 0 so it no longer shows the drop packet
        }
    }
}
