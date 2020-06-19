/*
 * Copyright © 2019-2020 L2JOrg
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

import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.data.database.dao.PetDAO;
import org.l2j.gameserver.data.xml.impl.*;
import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.gameserver.enums.ItemSkillType;
import org.l2j.gameserver.idfactory.IdFactory;
import org.l2j.gameserver.model.ExtractableProduct;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.EventMonster;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.commission.CommissionItemType;
import org.l2j.gameserver.model.conditions.*;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.item.OnItemCreate;
import org.l2j.gameserver.model.holders.ItemSkillHolder;
import org.l2j.gameserver.model.item.*;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.item.type.*;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.model.stats.functions.FuncTemplate;
import org.l2j.gameserver.settings.CharacterSettings;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GMAudit;
import org.l2j.gameserver.util.GameXmlReader;
import org.l2j.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.concurrent.ScheduledFuture;

import static java.util.Objects.*;
import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.gameserver.util.GameUtils.isGM;

/**
 * This class serves as a container for all item templates in the game.
 *
 * @author JoeAlisson
 */
public final class ItemEngine extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemEngine.class);
    private static final Logger LOGGER_ITEMS = LoggerFactory.getLogger("item");

    private final IntMap<ItemTemplate> items = new HashIntMap<>(13700);

    private ItemEngine() {
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/items/items.xsd");
    }

    public void load() {
        items.clear();
        parseDatapackDirectory("data/items", true);
        LOGGER.info("Loaded {} Items", items.size());
        releaseResources();
    }

    @Override
    protected void parseDocument(Document doc, File f) {
        forEach(doc, "list", list -> forEach(list, node -> {
            switch (node.getNodeName()) {
                case "item" -> parseItem(node);
                case "armor" -> parseArmor(node);
                case "weapon" -> parseWeapon(node);
            }
        }));
    }

    private void parseWeapon(Node weaponNode) {
        var attrs = weaponNode.getAttributes();
        var weapon = new Weapon(parseInt(attrs, "id"), parseString(attrs, "name"), parseEnum(attrs, WeaponType.class, "type"), parseEnum(attrs, BodyPart.class, "body-part"));

        weapon.setIcon(parseString(attrs, "icon"));
        weapon.setDisplayId(parseInt(attrs, "display-id", weapon.getId()));
        weapon.setMagic(parseBoolean(attrs, "magic"));

        forEach(weaponNode,node ->{
            switch (node.getNodeName()) {
                case "attributes" -> parseWeaponAttributes(weapon, node);
                case "crystal" -> parseCrystalType(weapon, node);
                case "damage" -> parseWeaponDamage(weapon, node);
                case "consume" -> parseWeaponConsume(weapon, node);
                case "restriction" -> parseItemRestriction(weapon, node);
                case "conditions" -> parseItemCondition(weapon, node);
                case "stats" -> parseItemStats(weapon, node);
                case "skills"-> parseItemSkills(weapon, node);
            }
        });

        items.put(weapon.getId(), weapon);
    }

    private void parseCrystalType(ItemTemplate weapon, Node node) {
        var attr = node.getAttributes();
        weapon.setCrystalType(parseEnum(attr, CrystalType.class, "type", CrystalType.NONE));
        weapon.setCrystalCount(parseInt(attr, "count"));
    }

    private void parseItemSkills(ItemTemplate item, Node node) {
        forEach(node, "skill", skillNode -> {
            var attr = skillNode.getAttributes();
            var type = parseEnum(attr, ItemSkillType.class, "type");
            item.addSkill(new ItemSkillHolder(parseInt(attr, "id"), parseInt(attr, "level"), type, parseInt(attr, "chance"), parseInt(attr, "value")));
        });

    }

    private void parseItemStats(ItemTemplate item, Node node) {
        forEach(node, "stat", statNode -> {
            var attr = statNode.getAttributes();
            var type = parseEnum(attr, Stat.class, "type");
            var value = parseDouble(attr, "value");
            item.addFunctionTemplate(new FuncTemplate(null, null, "add", 0x00, type, value));
        });
    }

    private void parseItemCondition(ItemTemplate item, Node node) {
        Condition condition = null;
        for(var n = node.getFirstChild(); nonNull(n); n = n.getNextSibling()) {

            var temp = switch (n.getNodeName()) {
                case "player" -> parsePlayerCondition(n);
                default -> condition;
            };

            condition = and(condition, temp);
        }

        if(nonNull(condition)) {
            var attr = node.getAttributes();
            var msg = parseString(attr, "msg");
            var msgId = parseInteger(attr, "msg-id");
            if(nonNull(msg)) {
                condition.setMessage(msg);
            } else if(nonNull(msgId)) {
                condition.setMessageId(msgId);
                if(parseBoolean(attr, "add-name")) {
                    condition.addName();
                }
            }
            item.attachCondition(condition);
        }
    }

    private Condition parsePlayerCondition(Node playerNode) {
        var attrs = playerNode.getAttributes();
        Condition playerCondition = null;
        for (var i = 0; i < attrs.getLength(); i++) {
            var attr =  attrs.item(i);
            playerCondition = switch (attr.getNodeName()) {
                case "level-min" -> and(playerCondition, new ConditionPlayerMinLevel(parseInt(attr)));
                case "level-max" -> and(playerCondition, new ConditionPlayerMaxLevel(parseInt(attr)));
                case "chaotic" ->  and(playerCondition, ConditionPlayerChaotic.of(parseBoolean(attr)));
                case "hero" -> and(playerCondition, ConditionPlayerIsHero.of(parseBoolean(attr)));
                case "pledge-class" -> and(playerCondition, new ConditionPlayerPledgeClass(parseInt(attr)));
                case "castle" -> and(playerCondition, new ConditionPlayerHasCastle(parseInt(attr)));
                case "sex" -> and(playerCondition, ConditionPlayerSex.of(parseInt(attr)));
                case "flying" -> and(playerCondition, ConditionPlayerFlyMounted.of(parseBoolean(attr)));
                case "zone" -> and(playerCondition, new ConditionPlayerInsideZoneId(parseIntList(attr)));
                default -> playerCondition;
            };
        }
        return playerCondition;
    }

    private Condition and(Condition c, Condition c2) {
        if(isNull(c)) {
            return c2;
        }

        if(isNull(c2)) {
            return c;
        }

        if(c instanceof ConditionLogicAnd) {
            ((ConditionLogicAnd) c).add(c2);
            return c;
        }

        if(c2 instanceof ConditionLogicAnd) {
            ((ConditionLogicAnd) c2).add(c);
            return c2;
        }

        return new ConditionLogicAnd(c, c2);
    }

    private void parseItemRestriction(ItemTemplate item, Node node) {
        var attr = node.getAttributes();
        item.setFreightable(parseBoolean(attr, "freightable"));
        item.setOlympiadRestricted(parseBoolean(attr, "olympiad-restricted"));
        item.setStackable(parseBoolean(attr, "stackable"));
        item.setDestroyable(parseBoolean(attr, "destroyable"));
        item.setTradable(parseBoolean(attr, "tradable"));
        item.setDropable(parseBoolean(attr, "dropable"));
        item.setSellable(parseBoolean(attr, "sellable"));
        item.setDepositable(parseBoolean(attr, "depositable"));
    }

    private void parseWeaponConsume(Weapon weapon, Node node) {
        var attr = node.getAttributes();
        weapon.setSoulshots(parseInt(attr, "soulshots"));
        weapon.setSpiritshots(parseInt(attr, "spiritshots"));
        weapon.setManaConsume(parseInt(attr, "mana"));
    }

    private void parseWeaponDamage(Weapon weapon, Node node) {
        var attr = node.getAttributes();
        weapon.setDamageRadius(parseInt(attr, "radius"));
        weapon.setDamageAngle(parseInt(attr, "angle"));
    }

    private void parseWeaponAttributes(Weapon weapon, Node node) {
        var attr = node.getAttributes();
        parseCommonAttributes(weapon, node);
        weapon.setEnchantable(parseBoolean(attr, "enchant-enabled"));
        weapon.setChangeWeapon(parseInt(attr, "change-weapon"));
        weapon.setCanAttack(parseBoolean(attr, "can-attack"));
        weapon.setRestrictSkills(parseBoolean(attr, "restrict-skills"));
        weapon.setEquipReuseDelay(parseInt(attr, "equip-reuse-delay"));
    }

    private void parseArmor(Node armorNode) {
        var attrs = armorNode.getAttributes();
        var armor = new Armor(parseInt(attrs, "id"), parseString(attrs, "name"), parseEnum(attrs, ArmorType.class, "type"), parseEnum(attrs, BodyPart.class, "body-part"));

        armor.setIcon(parseString(attrs, "icon"));
        armor.setDisplayId(parseInt(attrs, "display-id", armor.getId()));

        forEach(armorNode,node ->{
            switch (node.getNodeName()) {
                case "attributes" -> parseArmorAttributes(armor, node);
                case "crystal" -> parseCrystalType(armor, node);
                case "restriction" -> parseItemRestriction(armor, node);
                case "conditions" -> parseItemCondition(armor, node);
                case "stats" -> parseItemStats(armor, node);
                case "skills"-> parseItemSkills(armor, node);
            }
        } );

        items.put(armor.getId(), armor);
    }

    private void parseArmorAttributes(Armor armor, Node node) {
        parseCommonAttributes(armor, node);
        var attr = node.getAttributes();
        armor.setEnchantable(parseBoolean(attr, "enchant-enabled"));
        armor.setEquipReuseDelay(parseInt(attr, "equip-reuse-delay"));
    }

    private void parseItem(Node itemNode) {
        var attrs = itemNode.getAttributes();
        var item = new EtcItem(parseInt(attrs, "id"), parseString(attrs, "name"), parseEnum(attrs, EtcItemType.class, "type", EtcItemType.NONE));
        item.setIcon(parseString(attrs, "icon"));
        item.setDisplayId(parseInt(attrs, "display-id", item.getId()));

        forEach(itemNode, node ->{
            switch (node.getNodeName()) {
                case "attributes" -> parseItemAttributes(item, node);
                case "restriction" -> parseItemRestriction(item, node);
                case "crystal" -> parseItemCrystal(item, node);
                case "action" -> parseItemAction(item, node);
                case "skill-reducer" -> parseSkillReducer(item, node);
                case "extract" -> parseItemExtract(item, node);
                case "transformation-book" -> parseTransformationBook(item, node);
                case "conditions" -> parseItemCondition(item, node);
            }
        } );
        item.fillType2();
        items.put(item.getId(), item);
    }

    private void parseItemCrystal(EtcItem item, Node node) {
        item.setCrystalType(parseEnum(node.getAttributes(), CrystalType.class, "type", CrystalType.NONE));
    }

    private void parseTransformationBook(EtcItem item, Node node) {
        item.setHandler("TransformationBook");
        item.addSkill(new ItemSkillHolder(parseInt(node.getAttributes(), "skill"), 1, ItemSkillType.NORMAL, 100, 0));
    }

    private void parseItemExtract(EtcItem item, Node node) {
        item.setHandler("ExtractableItems");
        forEach(node, "item", itemNode -> {
            var attr = itemNode.getAttributes();
            item.addCapsuledItem(new ExtractableProduct(parseInt(attr, "id"), parseInt(attr, "min-count"), parseInt(attr, "min-count"),
                    parseDouble(attr, "chance"), parseInt(attr, "min-enchant"), parseInt(attr, "max-enchant")));
        });
    }

    private void parseSkillReducer(EtcItem item, Node node) {
        var attr = node.getAttributes();
        item.setHandler(parseString(attr, "type"));
        parseItemSkills(item, node);

    }

    private void parseItemAction(EtcItem item, Node node) {
        var attr = node.getAttributes();
        item.setHandler(parseString(attr, "handler"));
    }

    private void parseItemAttributes(EtcItem item, Node node) {
        parseCommonAttributes(item, node);
        var attr = node.getAttributes();
        item.setImmediateEffect(parseBoolean(attr, "immediate-effect"));
        item.setExImmediateEffect(parseBoolean(attr, "ex-immediate-effect"));
        item.setQuestItem(parseBoolean(attr,"quest-item"));
        item.setInfinite(parseBoolean(attr, "infinite"));
        item.setSelfResurrection(parseBoolean(attr, "self-resurrection"));
        item.setAction(parseEnum(attr, ActionType.class, "action"));
        item.setAutoUseType(parseEnum(attr, AutoUseType.class, "auto-use"));
    }

    private void parseCommonAttributes(ItemTemplate item, Node node) {
        var attr = node.getAttributes();
        item.setWeight(parseInt(attr, "weight"));
        item.setPrice(parseLong(attr, "price"));
        item.setCommissionType(parseEnum(attr, CommissionItemType.class, "commission-type", CommissionItemType.OTHER_ITEM));
        item.setReuseDelay(parseInt(attr, "reuse-delay"));
        item.setReuseGroup(parseInt(attr, "reuse-group"));
        item.setDuration(parselong(attr, "duration"));
        item.setForNpc(parseBoolean(attr, "for-npc"));
    }

    /**
     * Returns the item corresponding to the item ID
     *
     * @param id : int designating the item
     * @return ItemTemplate
     */
    public ItemTemplate getTemplate(int id) {
        return items.get(id);
    }

    /**
     * Create the Item corresponding to the Item Identifier and quantitiy add logs the activity. <B><U> Actions</U> :</B>
     * <li>Create and Init the Item corresponding to the Item Identifier and quantity</li>
     * <li>Add the Item object to _allObjects of L2world</li>
     * <li>Logs Item creation according to log settings</li>
     *
     * @param process   : String Identifier of process triggering this action
     * @param itemId    : int Item Identifier of the item to be created
     * @param count     : int Quantity of items to be created for stackable items
     * @param actor     : Creature requesting the item creation
     * @param reference : Object Object referencing current action like NPC selling item or previous item in transformation
     * @return Item corresponding to the new item
     */
    public Item createItem(String process, int itemId, long count, Creature actor, Object reference) {
        var template = items.get(itemId);
        requireNonNull(template, "The itemId should be a existent template id");

        final Item item = new Item(IdFactory.getInstance().getNextId(), template);

        // TODO Extract this block
        var characterSettings = getSettings(CharacterSettings.class);
        if (process.equalsIgnoreCase("loot") && !characterSettings.isAutoLoot(itemId)) {
            ScheduledFuture<?> itemLootShedule;
            if ((reference instanceof Attackable) && ((Attackable) reference).isRaid()) // loot privilege for raids
            {
                final Attackable raid = (Attackable) reference;
                // if in CommandChannel and was killing a World/RaidBoss
                if ((raid.getFirstCommandChannelAttacked() != null) && !characterSettings.autoLootRaid()) {
                    item.setOwnerId(raid.getFirstCommandChannelAttacked().getLeaderObjectId());
                    itemLootShedule = ThreadPool.schedule(new ResetOwner(item), characterSettings.raidLootPrivilegeTime());
                    item.setItemLootShedule(itemLootShedule);
                }
            } else if (!characterSettings.autoLoot() || ((reference instanceof EventMonster) && ((EventMonster) reference).eventDropOnGround())) {
                item.setOwnerId(actor.getObjectId());
                itemLootShedule = ThreadPool.schedule(new ResetOwner(item), 15000);
                item.setItemLootShedule(itemLootShedule);
            }
        }

        World.getInstance().addObject(item);

        if (item.isStackable() && (count > 1)) {
            item.setCount(count);
        }

        var generalSettings = getSettings(GeneralSettings.class);
        if (generalSettings.logItems() && !process.equals("Reset")) {
            if (!generalSettings.smallLogItems() || item.isEquipable() || item.getId() == CommonItem.ADENA) {
                LOGGER_ITEMS.info("CREATE: {}, item {}:+{} {} ({}), Previous count{}, {}", process, item.getObjectId(), item.getEnchantLevel(), item.getTemplate().getName(), item.getCount(), actor, reference);
            }
        }

        auditGM(process, itemId, count, actor, reference, item);

        EventDispatcher.getInstance().notifyEventAsync(new OnItemCreate(process, item, actor, reference), item.getTemplate());
        return item;
    }

    private void auditGM(String process, int itemId, long count, Creature actor, Object reference, Item item) {
        if (isGM(actor) && getSettings(GeneralSettings.class).auditGM()) {

            String referenceName = "no-reference";
            if (reference instanceof WorldObject) {
                referenceName = requireNonNullElse(((WorldObject) reference).getName(), "no-name");
            } else if (reference instanceof String) {
                referenceName = reference.toString();
            }

            final String targetName = (actor.getTarget() != null ? actor.getTarget().getName() : "no-target");
                GMAudit.auditGMAction(actor.toString(), String.format("%s (id: %d count: %d name: %s objectId: %d)", process, itemId, count, item.getName(), item.getObjectId()), targetName,
                        "WorldObject referencing this action is: " + referenceName);
        }
    }

    /**
     * Destroys the Item.<br>
     * <B><U> Actions</U> :</B>
     * <ul>
     * <li>Sets Item parameters to be unusable</li>
     * <li>Removes the Item object to _allObjects of L2world</li>
     * <li>Logs Item deletion according to log settings</li>
     * </ul>
     *
     * @param process   a string identifier of process triggering this action.
     * @param item      the item instance to be destroyed.
     * @param actor     the player requesting the item destroy.
     * @param reference the object referencing current action like NPC selling item or previous item in transformation.
     */
    public void destroyItem(String process, Item item, Player actor, Object reference) {
        synchronized (item) {
            final long old = item.getCount();
            item.setCount(0);
            item.setOwnerId(0);
            item.setItemLocation(ItemLocation.VOID);
            item.setLastChange(Item.REMOVED);

            World.getInstance().removeObject(item);
            IdFactory.getInstance().releaseId(item.getObjectId());

            var generalSettings = getSettings(GeneralSettings.class);
            if (generalSettings.logItems()) {
                if (!generalSettings.smallLogItems() || item.isEquipable() || item.getId() == CommonItem.ADENA) {
                    LOGGER_ITEMS.info("DELETE: {}, item {}:+{} {} ({}), Previous Count ({}), {}, {}", process, item.getObjectId(), item.getEnchantLevel(), item.getTemplate().getName(), item.getCount(),old ,actor, reference);
                }
            }

            auditGM(process, item.getId(), item.getCount(), actor, reference, item);
            getDAO(PetDAO.class).deleteByItem(item.getObjectId());
        }
    }

    public void reload() {
        load();
    }

    public Collection<ItemTemplate> getAllItems() {
        return items.values();
    }

    private static class ResetOwner implements Runnable {

        Item _item;
        private ResetOwner(Item item) {
            _item = item;
        }

        @Override
        public void run() {
            _item.setOwnerId(0);
            _item.setItemLootShedule(null);
        }

    }

    public static void init() {
        getInstance().load();
        EnchantItemEngine.init();
        EnchantItemOptionsData.init();
        ItemCrystallizationData.init();
        AugmentationEngine.init();
        VariationData.init();
        EnsoulData.init();
    }

    public static ItemEngine getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final ItemEngine INSTANCE = new ItemEngine();
    }
}
