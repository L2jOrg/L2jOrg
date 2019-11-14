package org.l2j.gameserver.datatables;

import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.commons.database.DatabaseFactory;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.impl.EnchantItemHPBonusData;
import org.l2j.gameserver.engine.items.DocumentEngine;
import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.gameserver.enums.ItemSkillType;
import org.l2j.gameserver.idfactory.IdFactory;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.EventMonster;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.conditions.*;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.item.OnItemCreate;
import org.l2j.gameserver.model.holders.ItemSkillHolder;
import org.l2j.gameserver.model.items.*;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.items.type.WeaponType;
import org.l2j.gameserver.model.stats.Stats;
import org.l2j.gameserver.model.stats.functions.FuncTemplate;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * This class serves as a container for all item templates in the game.
 */
public final class ItemTable extends GameXmlReader {
    private static Logger LOGGER = LoggerFactory.getLogger(ItemTable.class);
    private static Logger LOGGER_ITEMS = LoggerFactory.getLogger("item");

    private final IntMap<ItemTemplate> items = new HashIntMap<>(12160);

    @Deprecated
    public static final Map<String, Long> SLOTS = new HashMap<>();

    static {
        SLOTS.put("shirt", (long) ItemTemplate.SLOT_UNDERWEAR);
        SLOTS.put("lbracelet", (long) ItemTemplate.SLOT_L_BRACELET);
        SLOTS.put("rbracelet", (long) ItemTemplate.SLOT_R_BRACELET);
        SLOTS.put("talisman", (long) ItemTemplate.SLOT_TALISMAN);
        SLOTS.put("chest", (long) ItemTemplate.SLOT_CHEST);
        SLOTS.put("fullarmor", (long) ItemTemplate.SLOT_FULL_ARMOR);
        SLOTS.put("head", (long) ItemTemplate.SLOT_HEAD);
        SLOTS.put("hair", (long) ItemTemplate.SLOT_HAIR);
        SLOTS.put("hairall", (long) ItemTemplate.SLOT_HAIRALL);
        SLOTS.put("underwear", (long) ItemTemplate.SLOT_UNDERWEAR);
        SLOTS.put("back", (long) ItemTemplate.SLOT_BACK);
        SLOTS.put("neck", (long) ItemTemplate.SLOT_NECK);
        SLOTS.put("legs", (long) ItemTemplate.SLOT_LEGS);
        SLOTS.put("feet", (long) ItemTemplate.SLOT_FEET);
        SLOTS.put("gloves", (long) ItemTemplate.SLOT_GLOVES);
        SLOTS.put("chest,legs", (long) ItemTemplate.SLOT_CHEST | ItemTemplate.SLOT_LEGS);
        SLOTS.put("belt", (long) ItemTemplate.SLOT_BELT);
        SLOTS.put("rhand", (long) ItemTemplate.SLOT_R_HAND);
        SLOTS.put("lhand", (long) ItemTemplate.SLOT_L_HAND);
        SLOTS.put("lrhand", (long) ItemTemplate.SLOT_LR_HAND);
        SLOTS.put("rear;lear", (long) ItemTemplate.SLOT_R_EAR | ItemTemplate.SLOT_L_EAR);
        SLOTS.put("rfinger;lfinger", (long) ItemTemplate.SLOT_R_FINGER | ItemTemplate.SLOT_L_FINGER);
        SLOTS.put("wolf", (long) ItemTemplate.SLOT_WOLF);
        SLOTS.put("greatwolf", (long) ItemTemplate.SLOT_GREATWOLF);
        SLOTS.put("hatchling", (long) ItemTemplate.SLOT_HATCHLING);
        SLOTS.put("strider", (long) ItemTemplate.SLOT_STRIDER);
        SLOTS.put("babypet", (long) ItemTemplate.SLOT_BABYPET);
        SLOTS.put("brooch", (long) ItemTemplate.SLOT_BROOCH);
        SLOTS.put("brooch_jewel", (long) ItemTemplate.SLOT_BROOCH_JEWEL);
        SLOTS.put("agathion", ItemTemplate.SLOT_AGATHION);
        SLOTS.put("artifactbook", ItemTemplate.SLOT_ARTIFACT_BOOK);
        SLOTS.put("artifact", ItemTemplate.SLOT_ARTIFACT);
        SLOTS.put("none", (long) ItemTemplate.SLOT_NONE);

        // retail compatibility
        SLOTS.put("onepiece", (long) ItemTemplate.SLOT_FULL_ARMOR);
        SLOTS.put("hair2", (long) ItemTemplate.SLOT_HAIR2);
        SLOTS.put("dhair", (long) ItemTemplate.SLOT_HAIRALL);
        SLOTS.put("alldress", (long) ItemTemplate.SLOT_ALLDRESS);
        SLOTS.put("waist", (long) ItemTemplate.SLOT_BELT);
    }

    private final IntMap<EtcItem> etcItems = new HashIntMap<>();
    private final IntMap<Armor> armors = new HashIntMap<>();
    private final IntMap<Weapon> weapons = new HashIntMap<>();

    private ItemTemplate[] allTemplates;

    private ItemTable() {
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/items/items.xsd");
    }

    public void load() {
        items.clear();
        parseDatapackDirectory("data/items", true);
        int highest = 0;
        armors.clear();
        etcItems.clear();
        weapons.clear();
        for (ItemTemplate item : DocumentEngine.getInstance().loadItems()) {
            if (highest < item.getId()) {
                highest = item.getId();
            }
            if (item instanceof EtcItem) {
                etcItems.put(item.getId(), (EtcItem) item);
            } else if (item instanceof Armor) {
                armors.put(item.getId(), (Armor) item);
            } else {
                weapons.put(item.getId(), (Weapon) item);
            }
        }
        buildFastLookupTable(highest);
        LOGGER.info("Loaded {} Etc Items", etcItems.size());
        LOGGER.info("Loaded {} Armor Items", armors.size() );
        LOGGER.info("Loaded {} Weapon Items", weapons.size());
        LOGGER.info("Loaded {} Items in total.", etcItems.size() + armors.size() + weapons.size());
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
        var weapon = new Weapon(parseInt(attrs, "id"), parseString(attrs, "name"), parseEnum(attrs, WeaponType.class, "type"));

        weapon.setBodyPart(parseEnum(attrs, BodyPart.class, "body-part"));
        weapon.setIcon(parseString(attrs, "icon"));
        weapon.setDisplayId(parseInt(attrs, "display-id"));
        weapon.setMagic(parseBoolean(attrs, "magic"));

        forEach(weaponNode,node ->{
            switch (node.getNodeName()) {
                case "attributes" -> parseWeaponAttributes(weapon, node);
                case "damage" -> parseWeaponDamage(weapon, node);
                case "consume" -> parseWeaponConsume(weapon, node);
                case "restriction" -> parseItemRestriction(weapon, node);
                case "conditions" -> parseItemCondition(weapon, node);
                case "stats" -> parseItemStats(weapon, node);
                case "skills"-> parseItemSkills(weapon, node);
            }
        } );
    }

    private void parseItemSkills(Weapon weapon, Node node) {
        forEach(node, "skill", skillNode -> {
            var attr = skillNode.getAttributes();
            var type = parseEnum(attr, ItemSkillType.class, "type");
            weapon.addSkill(new ItemSkillHolder(parseInt(attr, "id"), parseInt(attr, "level"), type, parseInt(attr, "chance"), parseInt(attr, "type-value")));
        });

    }

    private void parseItemStats(Weapon weapon, Node node) {
        forEach(node, "stat", statNode -> {
            var attr = statNode.getAttributes();
            var type = Stats.valueOfXml(parseString(attr, "type"));
            var value = parseDouble(attr, "value");
            weapon.addFunctionTemplate(new FuncTemplate(null, null, "add", 0x00, type, value));
        });
    }

    private void parseItemCondition(Weapon weapon, Node node) {
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
            weapon.attachCondition(condition);
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

    private void parseItemRestriction(Weapon weapon, Node node) {
        var attr = node.getAttributes();
        weapon.setFreightable(parseBoolean(attr, "freightable"));
        weapon.setOlympiadRestricted(parseBoolean(attr, "olympiad-restricted"));
        weapon.setCocRestricted(parseBoolean(attr, "coc-restricted"));
        weapon.setStackable(parseBoolean(attr, "stackable"));
        weapon.setDestroyable(parseBoolean(attr, "destroyable"));
        weapon.setTradable(parseBoolean(attr, "tradable"));
        weapon.setDropable(parseBoolean(attr, "dropable"));
        weapon.setSellable(parseBoolean(attr, "sellable"));
    }

    private void parseWeaponConsume(Weapon weapon, Node node) {
        var attr = node.getAttributes();
        weapon.setSoulshots(parseInt(attr, "soulshots"));
        weapon.setSpiritshots(parseInt(attr, "spiritshots"));
        weapon.setManaConsume(parseInt(attr, "mana"));
    }

    private void parseWeaponDamage(Weapon weapon, Node node) {

    }

    private void parseWeaponAttributes(Weapon weapon, Node node) {

    }

    private void parseArmor(Node armorNode) {

    }

    private void parseItem(Node itemNode) {

    }

    /**
     * Builds a variable in which all items are putting in in function of their ID.
     *
     * @param size
     */
    private void buildFastLookupTable(int size) {
        // Create a FastLookUp Table called _allTemplates of size : value of the highest item ID
        LOGGER.info("Highest item id used: {}", size);
        allTemplates = new ItemTemplate[size + 1];

        // Insert armor item in Fast Look Up Table
        for (Armor item : armors.values()) {
            allTemplates[item.getId()] = item;
        }

        // Insert weapon item in Fast Look Up Table
        for (Weapon item : weapons.values()) {
            allTemplates[item.getId()] = item;
        }

        // Insert etcItem item in Fast Look Up Table
        for (EtcItem item : etcItems.values()) {
            allTemplates[item.getId()] = item;
        }
    }

    /**
     * Returns the item corresponding to the item ID
     *
     * @param id : int designating the item
     * @return ItemTemplate
     */
    public ItemTemplate getTemplate(int id) {
        if ((id >= allTemplates.length) || (id < 0)) {
            return null;
        }

        return allTemplates[id];
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
        // Create and Init the Item corresponding to the Item Identifier
        final Item item = new Item(IdFactory.getInstance().getNextId(), itemId);

        if (process.equalsIgnoreCase("loot") && !Config.AUTO_LOOT_ITEM_IDS.contains(itemId)) {
            ScheduledFuture<?> itemLootShedule;
            if ((reference instanceof Attackable) && ((Attackable) reference).isRaid()) // loot privilege for raids
            {
                final Attackable raid = (Attackable) reference;
                // if in CommandChannel and was killing a World/RaidBoss
                if ((raid.getFirstCommandChannelAttacked() != null) && !Config.AUTO_LOOT_RAIDS) {
                    item.setOwnerId(raid.getFirstCommandChannelAttacked().getLeaderObjectId());
                    itemLootShedule = ThreadPool.schedule(new ResetOwner(item), Config.LOOT_RAIDS_PRIVILEGE_INTERVAL);
                    item.setItemLootShedule(itemLootShedule);
                }
            } else if (!Config.AUTO_LOOT || ((reference instanceof EventMonster) && ((EventMonster) reference).eventDropOnGround())) {
                item.setOwnerId(actor.getObjectId());
                itemLootShedule = ThreadPool.schedule(new ResetOwner(item), 15000);
                item.setItemLootShedule(itemLootShedule);
            }
        }

        // Add the Item object to _allObjects of L2world
        World.getInstance().addObject(item);

        // Set Item parameters
        if (item.isStackable() && (count > 1)) {
            item.setCount(count);
        }

        if (Config.LOG_ITEMS && !process.equals("Reset")) {
            if (!Config.LOG_ITEMS_SMALL_LOG || item.isEquipable() || item.getId() == CommonItem.ADENA) {
                if (item.getEnchantLevel() > 0) {
                    LOGGER_ITEMS.info("CREATE:" + process //
                            + ", item " + item.getObjectId() //
                            + ":+" + item.getEnchantLevel() //
                            + " " + item.getTemplate().getName() //
                            + "(" + item.getCount() //
                            + "), " + actor
                            + ", " + reference);
                } else {
                    LOGGER_ITEMS.info("CREATE:" + process //
                            + ", item " + item.getObjectId() //
                            + ":" + item.getTemplate().getName() //
                            + "(" + item.getCount() //
                            + "), " + actor //
                            + ", " + reference); //
                }
            }
        }

        if ((actor != null) && actor.isGM()) {
            String referenceName = "no-reference";
            if (reference instanceof WorldObject) {
                referenceName = (((WorldObject) reference).getName() != null ? ((WorldObject) reference).getName() : "no-name");
            } else if (reference instanceof String) {
                referenceName = (String) reference;
            }
            final String targetName = (actor.getTarget() != null ? actor.getTarget().getName() : "no-target");
            if (getSettings(GeneralSettings.class).auditGM()) {
                GMAudit.auditGMAction(actor.getName() + " [" + actor.getObjectId() + "]" //
                        , String.valueOf(process) // in case of null
                                + "(id: " + itemId //
                                + " count: " + count //
                                + " name: " + item.getItemName() //
                                + " objId: " + item.getObjectId() + ")" //
                        , targetName //
                        , "WorldObject referencing this action is: " + referenceName);
            }
        }

        // Notify to scripts
        EventDispatcher.getInstance().notifyEventAsync(new OnItemCreate(process, item, actor, reference), item.getTemplate());
        return item;
    }

    public Item createItem(String process, int itemId, int count, Player actor) {
        return createItem(process, itemId, count, actor, null);
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

            if (Config.LOG_ITEMS) {
                if (!Config.LOG_ITEMS_SMALL_LOG || item.isEquipable() || item.getId() == CommonItem.ADENA) {
                    if (item.getEnchantLevel() > 0) {
                        LOGGER_ITEMS.info("DELETE:" + String.valueOf(process) // in case of null
                                + ", item " + item.getObjectId() //
                                + ":+" + item.getEnchantLevel() //
                                + " " + item.getTemplate().getName() //
                                + "(" + item.getCount() //
                                + "), PrevCount(" + old //
                                + "), " + String.valueOf(actor) // in case of null
                                + ", " + String.valueOf(reference)); // in case of null
                    } else {
                        LOGGER_ITEMS.info("DELETE:" + String.valueOf(process) // in case of null
                                + ", item " + item.getObjectId() //
                                + ":" + item.getTemplate().getName() //
                                + "(" + item.getCount() //
                                + "), PrevCount(" + old //
                                + "), " + String.valueOf(actor) // in case of null
                                + ", " + String.valueOf(reference)); // in case of null
                    }
                }
            }

            if ((actor != null) && actor.isGM()) {
                String referenceName = "no-reference";
                if (reference instanceof WorldObject) {
                    referenceName = (((WorldObject) reference).getName() != null ? ((WorldObject) reference).getName() : "no-name");
                } else if (reference instanceof String) {
                    referenceName = (String) reference;
                }
                final String targetName = (actor.getTarget() != null ? actor.getTarget().getName() : "no-target");
                if (getSettings(GeneralSettings.class).auditGM()) {
                    GMAudit.auditGMAction(actor.getName() + " [" + actor.getObjectId() + "]" //
                            , String.valueOf(process) // in case of null
                                    + "(id: " + item.getId() //
                                    + " count: " + item.getCount() //
                                    + " itemObjId: " //
                                    + item.getObjectId() + ")" //
                            , targetName //
                            , "WorldObject referencing this action is: " + referenceName);
                }
            }

            // if it's a pet control item, delete the pet as well
            if (item.getTemplate().isPetItem()) {
                try (Connection con = DatabaseFactory.getInstance().getConnection();
                     PreparedStatement statement = con.prepareStatement("DELETE FROM pets WHERE item_obj_id=?")) {
                    // Delete the pet in db
                    statement.setInt(1, item.getObjectId());
                    statement.execute();
                } catch (Exception e) {
                    LOGGER.warn("Could not delete pet objectid:", e);
                }
            }
        }
    }

    public void reload() {
        load();
        EnchantItemHPBonusData.getInstance().load();
    }

    public ItemTemplate[] getAllItems() {
        return allTemplates;
    }

    protected static class ResetOwner implements Runnable {

        Item _item;
        public ResetOwner(Item item) {
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
    }

    public static ItemTable getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final ItemTable INSTANCE = new ItemTable();
    }
}
