package org.l2j.gameserver.datatables;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.Config;
import org.l2j.commons.threading.ThreadPoolManager;
import org.l2j.gameserver.data.xml.impl.EnchantItemHPBonusData;
import org.l2j.gameserver.engines.DocumentEngine;
import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.gameserver.idfactory.IdFactory;
import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.actor.L2Attackable;
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.actor.instance.L2EventMonsterInstance;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.item.OnItemCreate;
import org.l2j.gameserver.model.itemcontainer.Inventory;
import org.l2j.gameserver.model.items.L2Armor;
import org.l2j.gameserver.model.items.L2EtcItem;
import org.l2j.gameserver.model.items.L2Item;
import org.l2j.gameserver.model.items.L2Weapon;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.util.GMAudit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

/**
 * This class serves as a container for all item templates in the game.
 */
public class ItemTable {
    private static Logger LOGGER = LoggerFactory.getLogger(ItemTable.class);
    private static Logger LOGGER_ITEMS = LoggerFactory.getLogger("item");

    public static final Map<String, Long> SLOTS = new HashMap<>();

    static {
        SLOTS.put("shirt", (long) L2Item.SLOT_UNDERWEAR);
        SLOTS.put("lbracelet", (long) L2Item.SLOT_L_BRACELET);
        SLOTS.put("rbracelet", (long) L2Item.SLOT_R_BRACELET);
        SLOTS.put("talisman", (long) L2Item.SLOT_DECO);
        SLOTS.put("chest", (long) L2Item.SLOT_CHEST);
        SLOTS.put("fullarmor", (long) L2Item.SLOT_FULL_ARMOR);
        SLOTS.put("head", (long) L2Item.SLOT_HEAD);
        SLOTS.put("hair", (long) L2Item.SLOT_HAIR);
        SLOTS.put("hairall", (long) L2Item.SLOT_HAIRALL);
        SLOTS.put("underwear", (long) L2Item.SLOT_UNDERWEAR);
        SLOTS.put("back", (long) L2Item.SLOT_BACK);
        SLOTS.put("neck", (long) L2Item.SLOT_NECK);
        SLOTS.put("legs", (long) L2Item.SLOT_LEGS);
        SLOTS.put("feet", (long) L2Item.SLOT_FEET);
        SLOTS.put("gloves", (long) L2Item.SLOT_GLOVES);
        SLOTS.put("chest,legs", (long) L2Item.SLOT_CHEST | L2Item.SLOT_LEGS);
        SLOTS.put("belt", (long) L2Item.SLOT_BELT);
        SLOTS.put("rhand", (long) L2Item.SLOT_R_HAND);
        SLOTS.put("lhand", (long) L2Item.SLOT_L_HAND);
        SLOTS.put("lrhand", (long) L2Item.SLOT_LR_HAND);
        SLOTS.put("rear;lear", (long) L2Item.SLOT_R_EAR | L2Item.SLOT_L_EAR);
        SLOTS.put("rfinger;lfinger", (long) L2Item.SLOT_R_FINGER | L2Item.SLOT_L_FINGER);
        SLOTS.put("wolf", (long) L2Item.SLOT_WOLF);
        SLOTS.put("greatwolf", (long) L2Item.SLOT_GREATWOLF);
        SLOTS.put("hatchling", (long) L2Item.SLOT_HATCHLING);
        SLOTS.put("strider", (long) L2Item.SLOT_STRIDER);
        SLOTS.put("babypet", (long) L2Item.SLOT_BABYPET);
        SLOTS.put("brooch", (long) L2Item.SLOT_BROOCH);
        SLOTS.put("brooch_jewel", (long) L2Item.SLOT_BROOCH_JEWEL);
        SLOTS.put("agathion", L2Item.SLOT_AGATHION);
        SLOTS.put("artifactbook", L2Item.SLOT_ARTIFACT_BOOK);
        SLOTS.put("artifact", L2Item.SLOT_ARTIFACT);
        SLOTS.put("none", (long) L2Item.SLOT_NONE);

        // retail compatibility
        SLOTS.put("onepiece", (long) L2Item.SLOT_FULL_ARMOR);
        SLOTS.put("hair2", (long) L2Item.SLOT_HAIR2);
        SLOTS.put("dhair", (long) L2Item.SLOT_HAIRALL);
        SLOTS.put("alldress", (long) L2Item.SLOT_ALLDRESS);
        SLOTS.put("deco1", (long) L2Item.SLOT_DECO);
        SLOTS.put("waist", (long) L2Item.SLOT_BELT);
    }

    private final Map<Integer, L2EtcItem> _etcItems = new HashMap<>();
    private final Map<Integer, L2Armor> _armors = new HashMap<>();
    private final Map<Integer, L2Weapon> _weapons = new HashMap<>();
    private L2Item[] _allTemplates;

    private ItemTable() {
        load();
    }

    private void load() {
        int highest = 0;
        _armors.clear();
        _etcItems.clear();
        _weapons.clear();
        for (L2Item item : DocumentEngine.getInstance().loadItems()) {
            if (highest < item.getId()) {
                highest = item.getId();
            }
            if (item instanceof L2EtcItem) {
                _etcItems.put(item.getId(), (L2EtcItem) item);
            } else if (item instanceof L2Armor) {
                _armors.put(item.getId(), (L2Armor) item);
            } else {
                _weapons.put(item.getId(), (L2Weapon) item);
            }
        }
        buildFastLookupTable(highest);
        LOGGER.info("Loaded {} Etc Items", _etcItems.size());
        LOGGER.info("Loaded {} Armor Items", _armors.size() );
        LOGGER.info("Loaded {} Weapon Items", _weapons.size());
        LOGGER.info("Loaded {} Items in total.", _etcItems.size() + _armors.size() + _weapons.size());
    }

    /**
     * Builds a variable in which all items are putting in in function of their ID.
     *
     * @param size
     */
    private void buildFastLookupTable(int size) {
        // Create a FastLookUp Table called _allTemplates of size : value of the highest item ID
        LOGGER.info("Highest item id used: {}", size);
        _allTemplates = new L2Item[size + 1];

        // Insert armor item in Fast Look Up Table
        for (L2Armor item : _armors.values()) {
            _allTemplates[item.getId()] = item;
        }

        // Insert weapon item in Fast Look Up Table
        for (L2Weapon item : _weapons.values()) {
            _allTemplates[item.getId()] = item;
        }

        // Insert etcItem item in Fast Look Up Table
        for (L2EtcItem item : _etcItems.values()) {
            _allTemplates[item.getId()] = item;
        }
    }

    /**
     * Returns the item corresponding to the item ID
     *
     * @param id : int designating the item
     * @return L2Item
     */
    public L2Item getTemplate(int id) {
        if ((id >= _allTemplates.length) || (id < 0)) {
            return null;
        }

        return _allTemplates[id];
    }

    /**
     * Create the L2ItemInstance corresponding to the Item Identifier and quantitiy add logs the activity. <B><U> Actions</U> :</B>
     * <li>Create and Init the L2ItemInstance corresponding to the Item Identifier and quantity</li>
     * <li>Add the L2ItemInstance object to _allObjects of L2world</li>
     * <li>Logs Item creation according to log settings</li>
     *
     * @param process   : String Identifier of process triggering this action
     * @param itemId    : int Item Identifier of the item to be created
     * @param count     : int Quantity of items to be created for stackable items
     * @param actor     : L2Character requesting the item creation
     * @param reference : Object Object referencing current action like NPC selling item or previous item in transformation
     * @return L2ItemInstance corresponding to the new item
     */
    public L2ItemInstance createItem(String process, int itemId, long count, L2Character actor, Object reference) {
        // Create and Init the L2ItemInstance corresponding to the Item Identifier
        final L2ItemInstance item = new L2ItemInstance(IdFactory.getInstance().getNextId(), itemId);

        if (process.equalsIgnoreCase("loot") && !Config.AUTO_LOOT_ITEM_IDS.contains(itemId)) {
            ScheduledFuture<?> itemLootShedule;
            if ((reference instanceof L2Attackable) && ((L2Attackable) reference).isRaid()) // loot privilege for raids
            {
                final L2Attackable raid = (L2Attackable) reference;
                // if in CommandChannel and was killing a World/RaidBoss
                if ((raid.getFirstCommandChannelAttacked() != null) && !Config.AUTO_LOOT_RAIDS) {
                    item.setOwnerId(raid.getFirstCommandChannelAttacked().getLeaderObjectId());
                    itemLootShedule = ThreadPoolManager.getInstance().schedule(new ResetOwner(item), Config.LOOT_RAIDS_PRIVILEGE_INTERVAL);
                    item.setItemLootShedule(itemLootShedule);
                }
            } else if (!Config.AUTO_LOOT || ((reference instanceof L2EventMonsterInstance) && ((L2EventMonsterInstance) reference).eventDropOnGround())) {
                item.setOwnerId(actor.getObjectId());
                itemLootShedule = ThreadPoolManager.getInstance().schedule(new ResetOwner(item), 15000);
                item.setItemLootShedule(itemLootShedule);
            }
        }

        // Add the L2ItemInstance object to _allObjects of L2world
        L2World.getInstance().addObject(item);

        // Set Item parameters
        if (item.isStackable() && (count > 1)) {
            item.setCount(count);
        }

        if (Config.LOG_ITEMS && !process.equals("Reset")) {
            if (!Config.LOG_ITEMS_SMALL_LOG || (Config.LOG_ITEMS_SMALL_LOG && (item.isEquipable() || (item.getId() == Inventory.ADENA_ID)))) {
                if (item.getEnchantLevel() > 0) {
                    LOGGER_ITEMS.info("CREATE:" + String.valueOf(process) // in case of null
                            + ", item " + item.getObjectId() //
                            + ":+" + item.getEnchantLevel() //
                            + " " + item.getItem().getName() //
                            + "(" + item.getCount() //
                            + "), " + String.valueOf(actor) // in case of null
                            + ", " + String.valueOf(reference)); // in case of null
                } else {
                    LOGGER_ITEMS.info("CREATE:" + String.valueOf(process) // in case of null
                            + ", item " + item.getObjectId() //
                            + ":" + item.getItem().getName() //
                            + "(" + item.getCount() //
                            + "), " + String.valueOf(actor) // in case of null
                            + ", " + String.valueOf(reference)); // in case of null
                }
            }
        }

        if ((actor != null) && actor.isGM()) {
            String referenceName = "no-reference";
            if (reference instanceof L2Object) {
                referenceName = (((L2Object) reference).getName() != null ? ((L2Object) reference).getName() : "no-name");
            } else if (reference instanceof String) {
                referenceName = (String) reference;
            }
            final String targetName = (actor.getTarget() != null ? actor.getTarget().getName() : "no-target");
            if (Config.GMAUDIT) {
                GMAudit.auditGMAction(actor.getName() + " [" + actor.getObjectId() + "]" //
                        , String.valueOf(process) // in case of null
                                + "(id: " + itemId //
                                + " count: " + count //
                                + " name: " + item.getItemName() //
                                + " objId: " + item.getObjectId() + ")" //
                        , targetName //
                        , "L2Object referencing this action is: " + referenceName);
            }
        }

        // Notify to scripts
        EventDispatcher.getInstance().notifyEventAsync(new OnItemCreate(process, item, actor, reference), item.getItem());
        return item;
    }

    public L2ItemInstance createItem(String process, int itemId, int count, L2PcInstance actor) {
        return createItem(process, itemId, count, actor, null);
    }

    /**
     * Destroys the L2ItemInstance.<br>
     * <B><U> Actions</U> :</B>
     * <ul>
     * <li>Sets L2ItemInstance parameters to be unusable</li>
     * <li>Removes the L2ItemInstance object to _allObjects of L2world</li>
     * <li>Logs Item deletion according to log settings</li>
     * </ul>
     *
     * @param process   a string identifier of process triggering this action.
     * @param item      the item instance to be destroyed.
     * @param actor     the player requesting the item destroy.
     * @param reference the object referencing current action like NPC selling item or previous item in transformation.
     */
    public void destroyItem(String process, L2ItemInstance item, L2PcInstance actor, Object reference) {
        synchronized (item) {
            final long old = item.getCount();
            item.setCount(0);
            item.setOwnerId(0);
            item.setItemLocation(ItemLocation.VOID);
            item.setLastChange(L2ItemInstance.REMOVED);

            L2World.getInstance().removeObject(item);
            IdFactory.getInstance().releaseId(item.getObjectId());

            if (Config.LOG_ITEMS) {
                if (!Config.LOG_ITEMS_SMALL_LOG || (Config.LOG_ITEMS_SMALL_LOG && (item.isEquipable() || (item.getId() == Inventory.ADENA_ID)))) {
                    if (item.getEnchantLevel() > 0) {
                        LOGGER_ITEMS.info("DELETE:" + String.valueOf(process) // in case of null
                                + ", item " + item.getObjectId() //
                                + ":+" + item.getEnchantLevel() //
                                + " " + item.getItem().getName() //
                                + "(" + item.getCount() //
                                + "), PrevCount(" + old //
                                + "), " + String.valueOf(actor) // in case of null
                                + ", " + String.valueOf(reference)); // in case of null
                    } else {
                        LOGGER_ITEMS.info("DELETE:" + String.valueOf(process) // in case of null
                                + ", item " + item.getObjectId() //
                                + ":" + item.getItem().getName() //
                                + "(" + item.getCount() //
                                + "), PrevCount(" + old //
                                + "), " + String.valueOf(actor) // in case of null
                                + ", " + String.valueOf(reference)); // in case of null
                    }
                }
            }

            if ((actor != null) && actor.isGM()) {
                String referenceName = "no-reference";
                if (reference instanceof L2Object) {
                    referenceName = (((L2Object) reference).getName() != null ? ((L2Object) reference).getName() : "no-name");
                } else if (reference instanceof String) {
                    referenceName = (String) reference;
                }
                final String targetName = (actor.getTarget() != null ? actor.getTarget().getName() : "no-target");
                if (Config.GMAUDIT) {
                    GMAudit.auditGMAction(actor.getName() + " [" + actor.getObjectId() + "]" //
                            , String.valueOf(process) // in case of null
                                    + "(id: " + item.getId() //
                                    + " count: " + item.getCount() //
                                    + " itemObjId: " //
                                    + item.getObjectId() + ")" //
                            , targetName //
                            , "L2Object referencing this action is: " + referenceName);
                }
            }

            // if it's a pet control item, delete the pet as well
            if (item.getItem().isPetItem()) {
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

    public L2Item[] getAllItems() {
        return _allTemplates;
    }

    protected static class ResetOwner implements Runnable {

        L2ItemInstance _item;
        public ResetOwner(L2ItemInstance item) {
            _item = item;
        }

        @Override
        public void run() {
            _item.setOwnerId(0);
            _item.setItemLootShedule(null);
        }

    }

    public static ItemTable getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final ItemTable INSTANCE = new ItemTable();
    }
}
