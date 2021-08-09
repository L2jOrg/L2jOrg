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
package org.l2j.gameserver.model.actor.instance;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.data.database.dao.PetDAO;
import org.l2j.gameserver.data.database.data.PetData;
import org.l2j.gameserver.data.database.data.PetSkillData;
import org.l2j.gameserver.data.sql.impl.PlayerSummonTable;
import org.l2j.gameserver.data.sql.impl.SummonEffectsTable;
import org.l2j.gameserver.data.sql.impl.SummonEffectsTable.SummonEffect;
import org.l2j.gameserver.data.xml.impl.LevelData;
import org.l2j.gameserver.data.xml.impl.PetDataTable;
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.gameserver.enums.PartyDistributionType;
import org.l2j.gameserver.handler.IItemHandler;
import org.l2j.gameserver.handler.ItemHandler;
import org.l2j.gameserver.instancemanager.ItemsOnGroundManager;
import org.l2j.gameserver.model.PetLevelData;
import org.l2j.gameserver.model.PetTemplate;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.stat.PetStats;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.item.BodyPart;
import org.l2j.gameserver.model.item.CommonItem;
import org.l2j.gameserver.engine.item.Weapon;
import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.model.item.container.PetInventory;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.model.skills.EffectScope;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.*;
import org.l2j.gameserver.settings.CharacterSettings;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.taskmanager.DecayTaskManager;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.world.zone.ZoneType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.database.DatabaseAccess.getDAO;


/**
 * @author JoeAlisson
 */
public class Pet extends Summon {
    protected static final Logger LOGGER = LoggerFactory.getLogger(Pet.class);

    private final PetTemplate petTemplate;
    private final PetInventory _inventory;
    private final int _controlObjectId;
    private final boolean _mountable;

    private PetLevelData _leveldata;
    private Future<?> _feedTask;
    private int _curFed;
    private boolean _respawned;

    /**
     * The Experience before the last Death Penalty
     */
    private long _expBeforeDeath = 0;
    private int _curWeightPenalty = 0;

    public Pet(NpcTemplate template, Player owner, Item control) {
        this(template, owner, control, (byte) (template.getDisplayId() == 12564 ? owner.getLevel() : template.getLevel()));
    }

    public Pet(NpcTemplate template, Player owner, Item control, byte level) {
        super(template, owner);
        setInstanceType(InstanceType.L2PetInstance);
        final int npcId = template.getId();
        petTemplate = PetDataTable.getInstance().getPetTemplate(npcId);

        level = (byte) Math.max(level, petTemplate.getMinLevel());
        getStats().setLevel(level);

        _leveldata = PetDataTable.getInstance().getPetLevelData(npcId, level);
        _controlObjectId = control.getObjectId();
        _inventory = new PetInventory(this);
        _inventory.restore();
        _mountable = PetDataTable.isMountable(npcId);
    }

    public final PetLevelData getPetLevelData() {
        return _leveldata;
    }

    public final PetTemplate getPetData() {
        return petTemplate;
    }

    public final void setPetData(PetLevelData value) {
        _leveldata = value;
    }

    @Override
    public PetStats getStats() {
        return (PetStats) super.getStats();
    }

    @Override
    public void initCharStat() {
        setStat(new PetStats(this));
    }

    public boolean isRespawned() {
        return _respawned;
    }

    @Override
    public int getSummonType() {
        return 2;
    }

    @Override
    public int getControlObjectId() {
        return _controlObjectId;
    }

    public Item getControlItem() {
        return getOwner().getInventory().getItemByObjectId(_controlObjectId);
    }

    public int getCurrentFed() {
        return _curFed;
    }

    public void setCurrentFed(int num) {
        if (num <= 0) {
            sendPacket(new ExChangeNpcState(getObjectId(), 0x64));
        } else if (_curFed <= 0) {
            sendPacket(new ExChangeNpcState(getObjectId(), 0x65));
        }
        _curFed = Math.min(num, getMaxFed());
    }

    /**
     * Returns the pet's currently equipped weapon instance (if any).
     */
    @Override
    public Item getActiveWeaponInstance() {
        if (_inventory != null) {
            return _inventory.getItems(item -> (item.getItemLocation() == ItemLocation.PET_EQUIP) && (item.getBodyPart() == BodyPart.RIGHT_HAND)).stream().findAny().orElse(null);
        }
        return null;
    }

    /**
     * Returns the pet's currently equipped weapon (if any).
     */
    @Override
    public Weapon getActiveWeaponItem() {
        final Item weapon = getActiveWeaponInstance();
        if (weapon == null) {
            return null;
        }

        return (Weapon) weapon.getTemplate();
    }

    @Override
    public Weapon getSecondaryWeaponItem() {
        // temporary? unavailable
        return null;
    }

    @Override
    public PetInventory getInventory() {
        return _inventory;
    }

    /**
     * Destroys item from inventory and send a Server->Client InventoryUpdate packet to the Player.
     *
     * @param process     : String Identifier of process triggering this action
     * @param objectId    : int Item Instance identifier of the item to be destroyed
     * @param count       : int Quantity of items to be destroyed
     * @param reference   : WorldObject Object referencing current action like NPC selling item or previous item in transformation
     * @param sendMessage : boolean Specifies whether to send message to Client about this action
     * @return boolean informing if the action was successfull
     */
    @Override
    public boolean destroyItem(String process, int objectId, long count, WorldObject reference, boolean sendMessage) {
        final Item item = _inventory.destroyItem(process, objectId, count, getOwner(), reference);

        if (item == null) {
            if (sendMessage) {
                sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT);
            }

            return false;
        }

        // Send Pet inventory update packet
        final PetInventoryUpdate petIU = new PetInventoryUpdate();
        petIU.addItem(item);
        sendPacket(petIU);

        if (sendMessage) {
            if (count > 1) {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S2_S1_S_DISAPPEARED);
                sm.addItemName(item.getId());
                sm.addLong(count);
                sendPacket(sm);
            } else {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_DISAPPEARED);
                sm.addItemName(item.getId());
                sendPacket(sm);
            }
        }
        return true;
    }

    /**
     * Destroy item from inventory by using its <B>itemId</B> and send a Server->Client InventoryUpdate packet to the Player.
     *
     * @param process     : String Identifier of process triggering this action
     * @param itemId      : int Item identifier of the item to be destroyed
     * @param count       : int Quantity of item to be destroyed
     * @param reference   : WorldObject Object referencing current action like NPC selling item or previous item in transformation
     * @param sendMessage : boolean Specifies whether to send message to Client about this action
     * @return boolean informing if the action was successfull
     */
    @Override
    public boolean destroyItemByItemId(String process, int itemId, long count, WorldObject reference, boolean sendMessage) {
        final Item item = _inventory.destroyItemByItemId(process, itemId, count, getOwner(), reference);

        if (item == null) {
            if (sendMessage) {
                sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT);
            }
            return false;
        }

        // Send Pet inventory update packet
        final PetInventoryUpdate petIU = new PetInventoryUpdate();
        petIU.addItem(item);
        sendPacket(petIU);

        if (sendMessage) {
            if (count > 1) {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S2_S1_S_DISAPPEARED);
                sm.addItemName(item.getId());
                sm.addLong(count);
                sendPacket(sm);
            } else {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_DISAPPEARED);
                sm.addItemName(item.getId());
                sendPacket(sm);
            }
        }

        return true;
    }

    @Override
    public void doPickupItem(WorldObject object) {
        if (isDead()) {
            return;
        }

        getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
        broadcastPacket(new StopMove(this));

        if (!GameUtils.isItem(object)) {
            // dont try to pickup anything that is not an item :)
            LOGGER.warn(this + " trying to pickup wrong target." + object);
            sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        final boolean follow = getFollowStatus();
        final Item target = (Item) object;

        SystemMessage smsg;
        synchronized (target) {
            // Check if the target to pick up is visible
            if (!target.isSpawned()) {
                // Send a Server->Client packet ActionFailed to this Player
                sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }

            if (!target.getDropProtection().tryPickUp(this)) {
                sendPacket(ActionFailed.STATIC_PACKET);
                smsg = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_FAILED_TO_PICK_UP_S1);
                smsg.addItemName(target);
                sendPacket(smsg);
                return;
            }

            if (((isInParty() && (getParty().getDistributionType() == PartyDistributionType.FINDERS_KEEPERS)) || !isInParty()) && !_inventory.validateCapacity(target)) {
                sendPacket(ActionFailed.STATIC_PACKET);
                sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_ANY_MORE_ITEMS);
                return;
            }

            if ((target.getOwnerId() != 0) && (target.getOwnerId() != getOwner().getObjectId()) && !getOwner().isInLooterParty(target.getOwnerId())) {
                if (target.getId() == CommonItem.ADENA) {
                    smsg = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_FAILED_TO_PICK_UP_S1_ADENA);
                    smsg.addLong(target.getCount());
                } else if (target.getCount() > 1) {
                    smsg = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_FAILED_TO_PICK_UP_S2_S1_S);
                    smsg.addItemName(target);
                    smsg.addLong(target.getCount());
                } else {
                    smsg = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_FAILED_TO_PICK_UP_S1);
                    smsg.addItemName(target);
                }
                sendPacket(ActionFailed.STATIC_PACKET);
                sendPacket(smsg);
                return;
            }

            if ((target.getItemLootShedule() != null) && ((target.getOwnerId() == getOwner().getObjectId()) || getOwner().isInLooterParty(target.getOwnerId()))) {
                target.resetOwnerTimer();
            }

            // Remove from the ground!
            target.pickupMe(this);

            if (GeneralSettings.saveDroppedItems()) {
                ItemsOnGroundManager.getInstance().removeObject(target);
            }
        }

        // Herbs
        if (target.getTemplate().hasExImmediateEffect()) {
            final IItemHandler handler = ItemHandler.getInstance().getHandler(target.getEtcItem());
            if (handler == null) {
                LOGGER.warn("No item handler registered for item ID: " + target.getId() + ".");
            } else {
                handler.useItem(this, target, false);
            }

            ItemEngine.getInstance().destroyItem("Consume", target, getOwner(), null);
            broadcastStatusUpdate();
        } else {
            if (target.getId() == CommonItem.ADENA) {
                smsg = SystemMessage.getSystemMessage(SystemMessageId.YOUR_PET_PICKED_UP_S1_ADENA);
                smsg.addLong(target.getCount());
                sendPacket(smsg);
            } else if (target.getEnchantLevel() > 0) {
                smsg = SystemMessage.getSystemMessage(SystemMessageId.YOUR_PET_PICKED_UP_S1_S2);
                smsg.addInt(target.getEnchantLevel());
                smsg.addItemName(target);
                sendPacket(smsg);
            } else if (target.getCount() > 1) {
                smsg = SystemMessage.getSystemMessage(SystemMessageId.YOUR_PET_PICKED_UP_S2_S1_S);
                smsg.addLong(target.getCount());
                smsg.addItemName(target);
                sendPacket(smsg);
            } else {
                smsg = SystemMessage.getSystemMessage(SystemMessageId.YOUR_PET_PICKED_UP_S1);
                smsg.addItemName(target);
                sendPacket(smsg);
            }

            // If owner is in party and it isnt finders keepers, distribute the item instead of stealing it -.-
            if (getOwner().isInParty() && (getOwner().getParty().getDistributionType() != PartyDistributionType.FINDERS_KEEPERS)) {
                getOwner().getParty().distributeItem(getOwner(), target);
            } else {
                final Item item = _inventory.addItem("Pickup", target, getOwner(), this);
                if (item != null) {
                    sendItemList();
                }
            }
        }

        getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);

        if (follow) {
            followOwner();
        }
    }

    @Override
    public void deleteMe(Player owner) {
        _inventory.transferItemsToOwner();
        super.deleteMe(owner);
        destroyControlItem(owner, false); // this should also delete the pet from the db
        PlayerSummonTable.getInstance().getPets().remove(getOwner().getObjectId());
    }

    @Override
    public boolean doDie(Creature killer) {
        if (!super.doDie(killer, true)) {
            return false;
        }
        final Player owner = getOwner();
        if ((owner != null) && !owner.isInDuel() && (!isInsideZone(ZoneType.PVP) || isInsideZone(ZoneType.SIEGE))) {
            deathPenalty();
        }

        stopFeed();
        sendPacket(SystemMessageId.THE_PET_HAS_BEEN_KILLED_IF_YOU_DON_T_RESURRECT_IT_WITHIN_24_HOURS_THE_PET_S_BODY_WILL_DISAPPEAR_ALONG_WITH_ALL_THE_PET_S_ITEMS);
        return true;
    }

    @Override
    public void doRevive() {
        getOwner().removeReviving();

        super.doRevive();

        // stopDecay
        DecayTaskManager.getInstance().cancel(this);
        startFeed();
        if (!isHungry()) {
            setRunning();
        }
        getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
    }

    @Override
    public void doRevive(double revivePower) {
        // Restore the pet's lost experience,
        // depending on the % return of the skill used (based on its power).
        restoreExp(revivePower);
        doRevive();
    }

    /**
     * Transfers item to another inventory
     *
     * @param process   string identifier of process triggering this action
     * @param objectId  Item Identifier of the item to be transfered
     * @param count     Quantity of items to be transfered
     * @param  target
     * @param actor     the player requesting the item transfer
     * @param reference Object referencing current action like NPC selling item or previous item in transformation
     * @return Item corresponding to the new item or the updated item in inventory
     */
    public Item transferItem(String process, int objectId, long count, Inventory target, Player actor, WorldObject reference) {
        final Item oldItem = _inventory.getItemByObjectId(objectId);
        final Item playerOldItem = target.getItemByItemId(oldItem.getId());
        final Item newItem = _inventory.transferItem(process, objectId, count, target, actor, reference);

        if (newItem == null) {
            return null;
        }

        // Send inventory update packet
        final PetInventoryUpdate petIU = new PetInventoryUpdate();
        if ((oldItem.getCount() > 0) && (oldItem != newItem)) {
            petIU.addModifiedItem(oldItem);
        } else {
            petIU.addRemovedItem(oldItem);
        }
        sendPacket(petIU);

        // Send target update packet
        if ((playerOldItem != null) && newItem.isStackable())
        {
            final InventoryUpdate iu = new InventoryUpdate();
            iu.addModifiedItem(newItem);
            sendInventoryUpdate(iu);
        }

        return newItem;
    }

    /**
     * Remove the Pet from DB and its associated item from the player inventory
     *
     * @param owner  The owner from whose inventory we should delete the item
     * @param evolve
     */
    public void destroyControlItem(Player owner, boolean evolve) {
        // remove the pet instance from world
        World.getInstance().removePet(owner.getObjectId());

        // delete from inventory
        try {
            Item removedItem;
            if (evolve) {
                removedItem = owner.getInventory().destroyItem("Evolve", _controlObjectId, 1, getOwner(), this);
            } else {
                removedItem = owner.getInventory().destroyItem("PetDestroy", _controlObjectId, 1, getOwner(), this);
                if (removedItem != null) {
                    final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_DISAPPEARED);
                    sm.addItemName(removedItem);
                    owner.sendPacket(sm);
                }
            }

            if (removedItem == null) {
                LOGGER.warn("Couldn't destroy pet control item for " + owner + " pet: " + this + " evolve: " + evolve);
            } else {
                final InventoryUpdate iu = new InventoryUpdate();
                iu.addRemovedItem(removedItem);

                owner.sendInventoryUpdate(iu);
                owner.broadcastUserInfo();
            }
        } catch (Exception e) {
            LOGGER.warn("Error while destroying control item: " + e.getMessage(), e);
        }

        getDAO(PetDAO.class).deleteByItem(_controlObjectId);
    }

    /**
     * @return Returns the mount able.
     */
    @Override
    public boolean isMountable() {
        return _mountable;
    }

    @Override
    public void setRestoreSummon(boolean val) {
        _restoreSummon = val;
    }

    @Override
    public final boolean stopSkillEffects(boolean removed, int skillId) {
        boolean stopped = super.stopSkillEffects(removed, skillId);
        final Collection<SummonEffect> effects = SummonEffectsTable.getInstance().getPetEffects().get(getControlObjectId());
        if ((effects != null) && !effects.isEmpty()) {
            for (SummonEffect effect : effects) {
                if (effect.getSkill().getId() == skillId) {
                    SummonEffectsTable.getInstance().getPetEffects().get(getControlObjectId()).remove(effect);
                    stopped = true;
                }
            }
        }
        return stopped;
    }

    @Override
    public void storeMe() {
        if (_controlObjectId == 0) {
            // this is a summon, not a pet, don't store anything
            return;
        }

        if (!CharacterSettings.restoreSummonOnReconnect()) {
            _restoreSummon = false;
        }

        getDAO(PetDAO.class).save(PetData.of(this, _restoreSummon));
        _respawned = true;

        if (_restoreSummon) {
            PlayerSummonTable.getInstance().getPets().put(getOwner().getObjectId(), getControlObjectId());
        } else {
            PlayerSummonTable.getInstance().getPets().remove(getOwner().getObjectId());
        }

        final Item itemInst = getControlItem();
        if ((itemInst != null) && (itemInst.getEnchantLevel() != getStats().getLevel())) {
            itemInst.changeEnchantLevel(getStats().getLevel());
            itemInst.updateDatabase();
        }
    }

    @Override
    public void storeEffect(boolean storeEffects) {
        SummonEffectsTable.getInstance().getPetEffects().getOrDefault(getControlObjectId(), Collections.emptyList()).clear();
        getDAO(PetDAO.class).deletePetSkillsSave(_controlObjectId);

        if (storeEffects) {
            int buffIndex = 0;

            var effects = getEffectList().getEffects();

            if(effects.isEmpty()) {
                return;
            }

            final List<PetSkillData> storedSkills = new ArrayList<>(effects.size());

            for (BuffInfo info : effects) {
                final Skill skill = info.getSkill();

                // Do not store those effects.
                if (skill.isDeleteAbnormalOnLeave() || skill.isToggle() || skill.getAbnormalType() == AbnormalType.LIFE_FORCE_OTHERS) {
                    continue;
                }


                // Dances and songs are not kept in retail.
                if (skill.isDance() && !CharacterSettings.storeDances()) {
                    continue;
                }

                storedSkills.add(PetSkillData.of(_controlObjectId, skill.getId(), skill.getLevel(), info.getTime(), ++buffIndex));
                SummonEffectsTable.getInstance().getPetEffects().computeIfAbsent(_controlObjectId, k -> ConcurrentHashMap.newKeySet()).add(new SummonEffect(skill, info.getTime()));
            }

            if(!storedSkills.isEmpty()) {
                getDAO(PetDAO.class).save(storedSkills);
            }

        }
    }

    @Override
    public void restoreEffects() {
        for (PetSkillData data : getDAO(PetDAO.class).restorePetSkills(_controlObjectId)) {
            final Skill skill = SkillEngine.getInstance().getSkill(data.getSkillId(), data.getSkillLevel());
            if (skill == null) {
                continue;
            }

            if (skill.hasEffects(EffectScope.GENERAL)) {
                SummonEffectsTable.getInstance().getPetEffects().computeIfAbsent(getControlObjectId(), k -> ConcurrentHashMap.newKeySet()).add(new SummonEffect(skill, data.getRemainingTime()));
            }
        }
        getDAO(PetDAO.class).deletePetSkillsSave(_controlObjectId);

        if (nonNull(SummonEffectsTable.getInstance().getPetEffects().get(getControlObjectId()))) {
            for (SummonEffect se : SummonEffectsTable.getInstance().getPetEffects().get(getControlObjectId())) {
                if (nonNull(se)) {
                    se.getSkill().applyEffects(this, this, false, se.getEffectCurTime());
                }
            }
        }
    }

    public synchronized void stopFeed() {
        if (_feedTask != null) {
            _feedTask.cancel(false);
            _feedTask = null;
        }
    }

    public synchronized void startFeed() {
        // stop feeding task if its active

        stopFeed();
        if (!isDead() && (getOwner().getPet() == this)) {
            _feedTask = ThreadPool.scheduleAtFixedRate(new FeedTask(), 10000, 10000);
        }
    }

    @Override
    public synchronized void unSummon(Player owner) {
        stopFeed();
        stopHpMpRegeneration();
        super.unSummon(owner);

        if (!isDead()) {
            if (_inventory != null) {
                _inventory.deleteMe();
            }
            World.getInstance().removePet(owner.getObjectId());
        }
    }

    /**
     * Restore the specified % of experience this Pet has lost.<BR>
     * <BR>
     */
    public void restoreExp(double restorePercent) {
        if (_expBeforeDeath > 0) {
            // Restore the specified % of lost experience.
            getStats().addExp(Math.round(((_expBeforeDeath - getStats().getExp()) * restorePercent) / 100));
            _expBeforeDeath = 0;
        }
    }

    private void deathPenalty() {
        // TODO: Need Correct Penalty

        final int lvl = getStats().getLevel();
        final double percentLost = (-0.07 * lvl) + 6.5;

        // Calculate the Experience loss
        final long lostExp = Math.round(((getStats().getExpForLevel(lvl + 1) - getStats().getExpForLevel(lvl)) * percentLost) / 100);

        // Get the Experience before applying penalty
        _expBeforeDeath = getStats().getExp();

        // Set the new Experience value of the Pet
        getStats().addExp(-lostExp);
    }

    @Override
    public void addExpAndSp(double addToExp, double addToSp) {
        if (getId() == 12564) // TODO: Remove this stupid hardcode.
        {
            getStats().addExpAndSp(addToExp * Config.SINEATER_XP_RATE, addToSp);
        } else {
            getStats().addExpAndSp(addToExp * Config.PET_XP_RATE, addToSp);
        }
    }

    @Override
    public long getExpForThisLevel() {
        if (getLevel() >= LevelData.getInstance().getMaxLevel()) {
            return 0;
        }
        return getStats().getExpForLevel(getLevel());
    }

    @Override
    public long getExpForNextLevel() {
        if (getLevel() >= LevelData.getInstance().getMaxLevel()) {
            return 0;
        }
        return getStats().getExpForLevel(getLevel() + 1);
    }

    @Override
    public final int getLevel() {
        return getStats().getLevel();
    }

    public int getMaxFed() {
        return getStats().getMaxFeed();
    }

    @Override
    public int getCriticalHit() {
        return getStats().getCriticalHit();
    }

    @Override
    public int getMAtk() {
        return getStats().getMAtk();
    }

    @Override
    public int getMDef() {
        return getStats().getMDef();
    }

    @Override
    public final int getSkillLevel(int skillId) {
        if (getKnownSkill(skillId) == null) {
            return 0;
        }

        final int lvl = getLevel();
        return lvl > 70 ? 7 + ((lvl - 70) / 5) : lvl / 10;
    }

    public void updateRefOwner(Player owner) {
        final int oldOwnerId = getOwner().getObjectId();

        setOwner(owner);
        World.getInstance().removePet(oldOwnerId);
        World.getInstance().addPet(oldOwnerId, this);
    }

    public int getInventoryLimit() {
        return Config.INVENTORY_MAXIMUM_PET;
    }

    public void refreshOverloaded() {
        final int maxLoad = getMaxLoad();
        if (maxLoad > 0) {
            final long weightproc = (((getCurrentLoad() - getBonusWeightPenalty()) * 1000) / maxLoad);
            int newWeightPenalty;
            if ((weightproc < 500) || getOwner().getDietMode()) {
                newWeightPenalty = 0;
            } else if (weightproc < 666) {
                newWeightPenalty = 1;
            } else if (weightproc < 800) {
                newWeightPenalty = 2;
            } else if (weightproc < 1000) {
                newWeightPenalty = 3;
            } else {
                newWeightPenalty = 4;
            }

            if (_curWeightPenalty != newWeightPenalty) {
                _curWeightPenalty = newWeightPenalty;
                if (newWeightPenalty > 0) {
                    addSkill(SkillEngine.getInstance().getSkill(4270, newWeightPenalty));
                    setIsOverloaded(getCurrentLoad() >= maxLoad);
                } else {
                    removeSkill(getKnownSkill(4270), true);
                    setIsOverloaded(false);
                }
            }
        }
    }

    @Override
    public void updateAndBroadcastStatus(int val) {
        refreshOverloaded();
        super.updateAndBroadcastStatus(val);
    }

    @Override
    public void sendInfo(Player player) {
        super.sendInfo(player);
        if(player == getOwner()) {
            sendItemList();
        }
    }

    public void sendItemList() {
        sendPacket(new PetItemList(this));
    }

    @Override
    public final boolean isHungry() {
        return _curFed < ((getPetData().getHungryLimit() / 100f) * getPetLevelData().getPetMaxFeed());
    }

    /**
     * Verifies if a pet can be controlled by it's owner.<br>
     * Starving pets cannot be controlled.
     *
     * @return {@code true} if the per cannot be controlled
     */
    public boolean isUncontrollable() {
        return _curFed <= 0;
    }

    @Override
    public final int getWeapon() {
        final Item weapon = _inventory.getPaperdollItem(InventorySlot.RIGHT_HAND);
        if (weapon != null) {
            return weapon.getId();
        }
        return 0;
    }

    @Override
    public final int getArmor() {
        final Item weapon = _inventory.getPaperdollItem(InventorySlot.CHEST);
        if (weapon != null) {
            return weapon.getId();
        }
        return 0;
    }

    @Override
    public short getSoulShotsPerHit() {
        return getPetLevelData().getPetSoulShot();
    }

    @Override
    public short getSpiritShotsPerHit() {
        return getPetLevelData().getPetSpiritShot();
    }

    @Override
    public void setName(String name) {
        final Item controlItem = getControlItem();
        if (controlItem != null) {
            if (controlItem.getType2() == (name == null ? 1 : 0)) {
                controlItem.updateDatabase();
                final InventoryUpdate iu = new InventoryUpdate();
                iu.addModifiedItem(controlItem);
                sendInventoryUpdate(iu);
            }
        } else {
            LOGGER.warn("Pet control item null, for pet: " + toString());
        }
        super.setName(name);
    }

    public boolean canEatFoodId(int itemId) {
        return petTemplate.getFood().contains(itemId);
    }

    @Override
    public final double getRunSpeed() {
        return super.getRunSpeed() * (isUncontrollable() ? 0.5d : 1.0d);
    }

    @Override
    public final double getWalkSpeed() {
        return super.getWalkSpeed() * (isUncontrollable() ? 0.5d : 1.0d);
    }

    @Override
    public final double getMovementSpeedMultiplier() {
        return super.getMovementSpeedMultiplier() * (isUncontrollable() ? 0.5d : 1.0d);
    }

    @Override
    public final double getMoveSpeed() {
        if (isInsideZone(ZoneType.WATER)) {
            return isRunning() ? getSwimRunSpeed() : getSwimWalkSpeed();
        }
        return isRunning() ? getRunSpeed() : getWalkSpeed();
    }

    /**
     * Manage Feeding Task.<BR>
     * Feed or kill the pet depending on hunger level.<br>
     * If pet has food in inventory and feed level drops below 55% then consume food from inventory.<br>
     * Send a broadcastStatusUpdate packet for this Pet
     */
    class FeedTask implements Runnable {
        @Override
        public void run() {
            try {
                final Summon pet = getOwner().getPet();
                if (pet == null || pet.getObjectId() != getObjectId()) {
                    stopFeed();
                    return;
                } else if (_curFed > getFeedConsume()) {
                    setCurrentFed(_curFed - getFeedConsume());
                } else {
                    setCurrentFed(0);
                }

                broadcastStatusUpdate();

                var foodIds = getPetData().getFood();
                if (foodIds.isEmpty()) {
                    if (isUncontrollable()) {
                        // Owl Monk remove PK
                        if ((getTemplate().getId() == 16050) && (getOwner() != null)) {
                            getOwner().setPkKills(Math.max(0, getOwner().getPkKills() - Rnd.get(1, 6)));
                        }
                        sendPacket(SystemMessageId.THE_PET_IS_NOW_LEAVING);
                        deleteMe(getOwner());
                    } else if (isHungry()) {
                        sendPacket(SystemMessageId.THERE_IS_NOT_MUCH_TIME_REMAINING_UNTIL_THE_PET_LEAVES);
                    }
                    return;
                }

                Item food = null;
                var it = foodIds.iterator();
                while(it.hasNext()) {
                    food = _inventory.getItemByItemId(it.nextInt());
                    if (food != null) {
                        break;
                    }
                }

                if ((food != null) && isHungry()) {
                    final IItemHandler handler = ItemHandler.getInstance().getHandler(food.getEtcItem());
                    if (handler != null) {
                        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOUR_PET_WAS_HUNGRY_SO_IT_ATE_S1);
                        sm.addItemName(food.getId());
                        sendPacket(sm);
                        handler.useItem(Pet.this, food, false);
                    }
                }

                if (isUncontrollable()) {
                    sendPacket(SystemMessageId.YOUR_PET_IS_STARVING_AND_WILL_NOT_OBEY_UNTIL_IT_GETS_IT_S_FOOD_FEED_YOUR_PET);
                }
            } catch (Exception e) {
                LOGGER.error("Pet [ObjectId: " + getObjectId() + "] a feed task error has occurred", e);
            }
        }

        private int getFeedConsume() {
            // if pet is attacking
            if (isAttackingNow()) {
                return getPetLevelData().getPetFeedBattle();
            }
            return getPetLevelData().getPetFeedNormal();
        }
    }

    public static Pet spawnPet(NpcTemplate template, Player owner, Item control) {
        if (World.getInstance().findPet(owner.getObjectId()) != null) {
            return null; // owner has a pet listed in world
        }
        final PetTemplate petTemplate = PetDataTable.getInstance().getPetTemplate(template.getId());

        final Pet pet = restore(control, template, owner);
        // add the pet instance to world
        pet.setTitle(owner.getName());
        if (petTemplate.isSyncLevel() && (pet.getLevel() != owner.getLevel())) {
            final byte availableLevel = (byte) Math.min(petTemplate.getMaxLevel(), owner.getLevel());
            pet.getStats().setLevel(availableLevel);
            pet.getStats().setExp(pet.getStats().getExpForLevel(availableLevel));
        }
        World.getInstance().addPet(owner.getObjectId(), pet);
        return pet;
    }

    private static Pet restore(Item control, NpcTemplate template, Player owner) {
        var data = getDAO(PetDAO.class).findPetByControlItem(control.getObjectId());
        if(isNull(data)) {
            return new Pet(template, owner, control);
        }

        Pet pet = new Pet(template, owner, control, data.getLevel());

        pet._respawned = true;
        pet.setName(data.getName());

        long exp = data.getExp();
        final PetLevelData info = PetDataTable.getInstance().getPetLevelData(pet.getId(), pet.getLevel());
        // DS: update experience based by level
        // Avoiding pet delevels due to exp per level values changed.
        if (nonNull(info) && (exp < info.getPetMaxExp())) {
            exp = info.getPetMaxExp();
        }

        pet.getStats().setExp(exp);
        pet.getStats().setSp(data.getSp());

        pet.getStatus().setCurrentHp(data.getCurHp());
        pet.getStatus().setCurrentMp(data.getCurMp());
        pet.getStatus().setCurrentCp(pet.getMaxCp());
        if (data.getCurHp() < 1) {
            pet.setIsDead(true);
            pet.stopHpMpRegeneration();
        }

        pet.setCurrentFed(data.getFed());
        return pet;
    }

}
