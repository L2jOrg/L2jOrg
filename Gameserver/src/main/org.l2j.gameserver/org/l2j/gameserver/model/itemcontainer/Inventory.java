package org.l2j.gameserver.model.itemcontainer;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.api.item.InventoryListener;
import org.l2j.gameserver.data.xml.impl.ArmorSetsData;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.gameserver.model.ArmorSet;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.VariationInstance;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.items.BodyPart;
import org.l2j.gameserver.model.items.ItemTemplate;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.items.type.EtcItemType;
import org.l2j.gameserver.model.items.type.WeaponType;
import org.l2j.gameserver.network.serverpackets.ExUserInfoEquipSlot;
import org.l2j.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.lang.Math.min;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.util.Util.falseIfNullOrElse;
import static org.l2j.commons.util.Util.isBetween;
import static org.l2j.gameserver.model.items.BodyPart.*;
import static org.l2j.gameserver.model.items.CommonItem.WEDDING_BOUQUET;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * This class manages inventory
 *
 * @version $Revision: 1.13.2.9.2.12 $ $Date: 2005/03/29 23:15:15 $ rewritten 23.2.2006 by Advi
 * @author JoeAlisson
 */
public abstract class Inventory extends ItemContainer {
    // Common Items
    public static final int BEAUTY_TICKET_ID = 36308;

    public static final long MAX_ADENA = Config.MAX_ADENA;

    public static final int PAPERDOLL_UNDER = 0;
    public static final int PAPERDOLL_HEAD = 1;
    public static final int PAPERDOLL_HAIR = 2;
    public static final int PAPERDOLL_HAIR2 = 3;
    public static final int PAPERDOLL_NECK = 4;
    public static final int PAPERDOLL_RHAND = 5;
    public static final int PAPERDOLL_CHEST = 6;
    public static final int PAPERDOLL_LHAND = 7;
    public static final int PAPERDOLL_REAR = 8;
    public static final int PAPERDOLL_LEAR = 9;
    public static final int PAPERDOLL_GLOVES = 10;
    public static final int PAPERDOLL_LEGS = 11;
    public static final int PAPERDOLL_FEET = 12;
    public static final int PAPERDOLL_RFINGER = 13;
    public static final int PAPERDOLL_LFINGER = 14;
    public static final int PAPERDOLL_LBRACELET = 15;
    public static final int PAPERDOLL_RBRACELET = 16;
    public static final int PAPERDOLL_AGATHION1 = 17;
    public static final int PAPERDOLL_AGATHION2 = 18;
    public static final int PAPERDOLL_AGATHION3 = 19;
    public static final int PAPERDOLL_AGATHION4 = 20;
    public static final int PAPERDOLL_AGATHION5 = 21;
    public static final int TALISMAN1 = 22;
    public static final int TALISMAN2 = 23;
    public static final int TALISMAN3 = 24;
    public static final int TALISMAN4 = 25;
    public static final int TALISMAN5 = 26;
    public static final int TALISMAN6 = 27;
    public static final int PAPERDOLL_CLOAK = 28;
    public static final int PAPERDOLL_BELT = 29;
    public static final int PAPERDOLL_BROOCH = 30;
    public static final int PAPERDOLL_BROOCH_JEWEL1 = 31;
    public static final int PAPERDOLL_BROOCH_JEWEL2 = 32;
    public static final int PAPERDOLL_BROOCH_JEWEL3 = 33;
    public static final int PAPERDOLL_BROOCH_JEWEL4 = 34;
    public static final int PAPERDOLL_BROOCH_JEWEL5 = 35;
    public static final int PAPERDOLL_BROOCH_JEWEL6 = 36;
    public static final int PAPERDOLL_ARTIFACT_BOOK = 37;
    public static final int PAPERDOLL_ARTIFACT1 = 38; // Artifact Balance
    public static final int PAPERDOLL_ARTIFACT2 = 39; // Artifact Balance
    public static final int PAPERDOLL_ARTIFACT3 = 40; // Artifact Balance
    public static final int PAPERDOLL_ARTIFACT4 = 41; // Artifact Balance
    public static final int PAPERDOLL_ARTIFACT5 = 42; // Artifact Balance
    public static final int PAPERDOLL_ARTIFACT6 = 43; // Artifact Balance
    public static final int PAPERDOLL_ARTIFACT7 = 44; // Artifact Balance
    public static final int PAPERDOLL_ARTIFACT8 = 45; // Artifact Balance
    public static final int PAPERDOLL_ARTIFACT9 = 46; // Artifact Balance
    public static final int PAPERDOLL_ARTIFACT10 = 47; // Artifact Balance
    public static final int PAPERDOLL_ARTIFACT11 = 48; // Artifact Balance
    public static final int PAPERDOLL_ARTIFACT12 = 49; // Artifact Balance
    public static final int PAPERDOLL_ARTIFACT13 = 50; // Artifact Spirit
    public static final int PAPERDOLL_ARTIFACT14 = 51; // Artifact Spirit
    public static final int PAPERDOLL_ARTIFACT15 = 52; // Artifact Spirit
    public static final int PAPERDOLL_ARTIFACT16 = 53; // Artifact Protection
    public static final int PAPERDOLL_ARTIFACT17 = 54; // Artifact Protection
    public static final int PAPERDOLL_ARTIFACT18 = 55; // Artifact Protection
    public static final int PAPERDOLL_ARTIFACT19 = 56; // Artifact Support
    public static final int PAPERDOLL_ARTIFACT20 = 57; // Artifact Support
    public static final int PAPERDOLL_ARTIFACT21 = 58; // Artifact Support
    public static final int PAPERDOLL_TOTALSLOTS = 59;
    // Speed percentage mods
    public static final double MAX_ARMOR_WEIGHT = 12000;
    protected static final Logger LOGGER = LoggerFactory.getLogger(Inventory.class);
    private final Item[] paperdoll;
    private final Set<InventoryListener> listeners;
    // protected to be accessed from child classes only
    protected int _totalWeight;
    // used to quickly check for using of item of special type
    private int _wearedMask;
    private int blockedItemSlotsMask;

    /**
     * Constructor of the inventory
     */
    protected Inventory() {
        paperdoll = new Item[PAPERDOLL_TOTALSLOTS];
        listeners = new HashSet<>();

        // common
        ServiceLoader.load(InventoryListener.class).forEach(this::addPaperdollListener);
    }

    protected abstract ItemLocation getEquipLocation();

    /**
     * Returns the instance of new ChangeRecorder
     *
     * @return ChangeRecorder
     */
    private ChangeRecorder newRecorder() {
        return new ChangeRecorder(this);
    }

    /**
     * Drop item from inventory and updates database
     *
     * @param process   : String Identifier of process triggering this action
     * @param item      : Item to be dropped
     * @param actor     : Player Player requesting the item drop
     * @param reference : Object Object referencing current action like NPC selling item or previous item in transformation
     * @return Item corresponding to the destroyed item or the updated item in inventory
     */
    public Item dropItem(String process, Item item, Player actor, Object reference) {
        if (item == null) {
            return null;
        }

        synchronized (item) {
            if (!items.containsKey(item.getObjectId())) {
                return null;
            }

            removeItem(item);
            item.setOwnerId(process, 0, actor, reference);
            item.setItemLocation(ItemLocation.VOID);
            item.setLastChange(Item.REMOVED);

            item.updateDatabase();
            refreshWeight();
        }
        return item;
    }

    /**
     * Drop item from inventory by using its <B>objectID</B> and updates database
     *
     * @param process   : String Identifier of process triggering this action
     * @param objectId  : int Item Instance identifier of the item to be dropped
     * @param count     : int Quantity of items to be dropped
     * @param actor     : Player Player requesting the item drop
     * @param reference : Object Object referencing current action like NPC selling item or previous item in transformation
     * @return Item corresponding to the destroyed item or the updated item in inventory
     */
    public Item dropItem(String process, int objectId, long count, Player actor, Object reference) {
        Item item = getItemByObjectId(objectId);
        if (item == null) {
            return null;
        }

        synchronized (item) {
            if (!items.containsKey(item.getObjectId())) {
                return null;
            }

            // Adjust item quantity and create new instance to drop
            // Directly drop entire item
            if (item.getCount() > count) {
                item.changeCount(process, -count, actor, reference);
                item.setLastChange(Item.MODIFIED);
                item.updateDatabase();

                item = ItemEngine.getInstance().createItem(process, item.getId(), count, actor, reference);
                item.updateDatabase();
                refreshWeight();
                return item;
            }
        }
        return dropItem(process, item, actor, reference);
    }

    /**
     * Adds item to inventory for further adjustments and Equip it if necessary (itemlocation defined)
     *
     * @param item : Item to be added from inventory
     */
    @Override
    protected void addItem(Item item) {
        super.addItem(item);
        if (item.isEquipped()) {
            equipItem(item);
        }
    }

    /**
     * Removes item from inventory for further adjustments.
     *
     * @param item : Item to be removed from inventory
     */
    @Override
    protected boolean removeItem(Item item) {
        // Unequip item if equiped
        for (int i = 0; i < paperdoll.length; i++) {
            if (paperdoll[i] == item) {
                unEquipItemInSlot(i);
            }
        }
        return super.removeItem(item);
    }

    /**
     * @param slot the slot.
     * @return the item in the paperdoll slot
     */
    public Item getPaperdollItem(int slot) {
        return paperdoll[slot];
    }

    /**
     * @param slot the slot.
     * @return {@code true} if specified paperdoll slot is empty, {@code false} otherwise
     */
    public boolean isPaperdollSlotEmpty(int slot) {
        return paperdoll[slot] == null;
    }

    /**
     * Returns the item in the paperdoll ItemTemplate slot
     *
     * @param bodyPart identifier
     * @return Item
     */
    public Item getItemByBodyPart(BodyPart bodyPart) {
        if(bodyPart.paperdool() == -1) {
            return null;
        }
        return paperdoll[bodyPart.paperdool()];
    }

    /**
     * Returns the ID of the item in the paperdoll slot
     *
     * @param slot : int designating the slot
     * @return int designating the ID of the item
     */
    public int getPaperdollItemId(int slot) {
        final Item item = paperdoll[slot];
        if (item != null) {
            return item.getId();
        }
        return 0;
    }

    /**
     * Returns the ID of the item in the paperdoll slot
     *
     * @param slot : int designating the slot
     * @return int designating the ID of the item
     */
    public int getPaperdollItemDisplayId(int slot) {
        final Item item = paperdoll[slot];
        return (item != null) ? item.getDisplayId() : 0;
    }

    public VariationInstance getPaperdollAugmentation(int slot) {
        final Item item = paperdoll[slot];
        return (item != null) ? item.getAugmentation() : null;
    }

    /**
     * Returns the objectID associated to the item in the paperdoll slot
     *
     * @param slot : int pointing out the slot
     * @return int designating the objectID
     */
    public int getPaperdollObjectId(int slot) {
        final Item item = paperdoll[slot];
        return (item != null) ? item.getObjectId() : 0;
    }

    /**
     * Adds new inventory's paperdoll listener.
     *
     * @param listener the new listener
     */
    protected void addPaperdollListener(InventoryListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a paperdoll listener.
     *
     * @param listener the listener to be deleted
     */
    public synchronized void removePaperdollListener(InventoryListener listener) {
        listeners.remove(listener);
    }

    /**
     * Equips an item in the given slot of the paperdoll.<br>
     * <U><I>Remark :</I></U> The item <B>must be</B> in the inventory already.
     *
     * @param slot : int pointing out the slot of the paperdoll
     * @param item : Item pointing out the item to add in slot
     * @return Item designating the item placed in the slot before
     */
    public synchronized Item setPaperdollItem(int slot, Item item) {
        final Item old = paperdoll[slot];
        if (old != item) {
            if (old != null) {
                paperdoll[slot] = null;
                // Put old item from paperdoll slot to base location
                old.setItemLocation(getBaseLocation());
                old.setLastChange(Item.MODIFIED);
                // Get the mask for paperdoll
                int mask = 0;
                for (int i = 0; i < PAPERDOLL_TOTALSLOTS; i++) {
                    final Item pi = paperdoll[i];
                    if (pi != null) {
                        mask |= pi.getTemplate().getItemMask();
                    }
                }
                _wearedMask = mask;
                // Notify all paperdoll listener in order to unequip old item in slot
                for (InventoryListener listener : listeners) {
                    if (listener == null) {
                        continue;
                    }

                    listener.notifyUnequiped(slot, old, this);
                }
                old.updateDatabase();
            }
            // Add new item in slot of paperdoll
            if (item != null) {
                paperdoll[slot] = item;
                item.setItemLocation(getEquipLocation(), slot);
                item.setLastChange(Item.MODIFIED);
                _wearedMask |= item.getTemplate().getItemMask();
                for (InventoryListener listener : listeners) {
                    if (listener == null) {
                        continue;
                    }

                    listener.notifyEquiped(slot, item, this);
                }
                item.updateDatabase();
            }

            if (isPlayer(getOwner())) {
                getOwner().sendPacket(new ExUserInfoEquipSlot(getOwner().getActingPlayer()));
            }
        }
        return old;
    }

    /**
     * @return the mask of wore item
     */
    public int getWearedMask() {
        return _wearedMask;
    }

    /**
     * Unequips item in body slot and returns alterations.<BR>
     * <B>If you dont need return value use {@link Inventory#unEquipItemInBodySlot(BodyPart)} instead</B>
     *
     * @param slot : int designating the slot of the paperdoll
     * @return Item[] : list of changes
     *
     * TODO use bodyPart instead of slot
     */
    public Set<Item> unEquipItemInBodySlotAndRecord(BodyPart slot) {
        final ChangeRecorder recorder = newRecorder();

        try {
            unEquipItemInBodySlot(slot);
        } finally {
            removePaperdollListener(recorder);
        }
        return recorder.getChangedItems();
    }

    /**
     * Sets item in slot of the paperdoll to null value
     *
     * @param pdollSlot : int designating the slot
     * @return Item designating the item in slot before change
     */
    public Item unEquipItemInSlot(int pdollSlot) {
        return setPaperdollItem(pdollSlot, null);
    }

    /**
     * Unequips item in slot and returns alterations<BR>
     * <B>If you dont need return value use {@link Inventory#unEquipItemInSlot(int)} instead</B>
     *
     * @param slot : int designating the slot
     * @return Item[] : list of items altered
     */
    public Set<Item> unEquipItemInSlotAndRecord(int slot) {
        final ChangeRecorder recorder = newRecorder();

        try {
            unEquipItemInSlot(slot);
            if (isPlayer(getOwner())) {
                ((Player) getOwner()).refreshExpertisePenalty();
            }
        } finally {
            removePaperdollListener(recorder);
        }
        return recorder.getChangedItems();
    }

    /**
     * Unequips item in slot (i.e. equips with default value)
     *
     * @param slot : int designating the slot
     * @return {@link Item} designating the item placed in the slot
     */
    public Item unEquipItemInBodySlot(BodyPart slot) {
        int pdollSlot = switch (slot) {
            case HAIR_ALL -> {
                setPaperdollItem(HAIR.paperdool(), null);
                yield HAIR.paperdool();
            }
            case TWO_HAND -> RIGHT_HAND.paperdool();
            case ALL_DRESS, FULL_ARMOR -> CHEST.paperdool();
            default -> slot.paperdool();
        };

        if (pdollSlot >= 0) {
            final Item old = setPaperdollItem(pdollSlot, null);
            if (old != null) {
                if (isPlayer(getOwner())) {
                    ((Player) getOwner()).refreshExpertisePenalty();
                }
            }
            return old;
        }
        return null;
    }

    /**
     * Equips item and returns list of alterations<BR>
     * <B>If you don't need return value use {@link Inventory#equipItem(Item)} instead</B>
     *
     * @param item : Item corresponding to the item
     * @return Item[] : list of alterations
     */
    public Set<Item> equipItemAndRecord(Item item) {
        final ChangeRecorder recorder = newRecorder();

        try {
            equipItem(item);
        } finally {
            removePaperdollListener(recorder);
        }
        return recorder.getChangedItems();
    }

    /**
     * Equips item in slot of paperdoll.
     *
     * @param item : Item designating the item and slot used.
     */
    public void equipItem(Item item) {
        if (isPlayer(getOwner())) {
            final Player player = (Player) getOwner();
            if (!player.canOverrideCond(PcCondOverride.ITEM_CONDITIONS) && !player.isHero() && item.isHeroItem()) {
                return;
            }
        }

        var bodyPart = item.getBodyPart();

        if(bodyPart.isAnyOf(BodyPart.TWO_HAND, LEFT_HAND, BodyPart.RIGHT_HAND, BodyPart.LEGS, BodyPart.FEET, BodyPart.GLOVES, BodyPart.HEAD)) {
            var formal = getPaperdollItem(BodyPart.CHEST.paperdool());
            if(nonNull(formal) && item.getId() != WEDDING_BOUQUET && formal.getBodyPart() == BodyPart.ALL_DRESS) {
                return;
            }
        }

        switch (bodyPart) {
            case TWO_HAND -> equipTwoHand(item);
            case LEFT_HAND -> equipLeftHand(item);
            case EAR -> equipEar(item);
            case FINGER -> equipFinger(item);
            case FULL_ARMOR -> equipFullArmor(item);
            case LEGS -> equipLeg(item);
            case HAIR -> equipHair(item);
            case HAIR2 -> equipHair2(item);
            case HAIR_ALL -> equipHairAll(item);
            case TALISMAN -> equipTalisman(item);
            case ALL_DRESS -> equipAllDress(item);
            case BROOCH_JEWEL -> equipBroochJewel(item);
            case AGATHION -> equipAgathion(item);
            case ARTIFACT -> equipArtifact(item);
            case NECK, RIGHT_HAND, CHEST, FEET, GLOVES, HEAD, UNDERWEAR, BACK, LEFT_BRACELET, RIGHT_BRACELET, BELT, BROOCH, ARTIFACT_BOOK -> setPaperdollItem(bodyPart.paperdool(), item);
            default -> LOGGER.warn("Unknown body slot {} for Item ID: {}", bodyPart, item.getId());
        }
    }

    private void equipAllDress(Item item) {
        setPaperdollItem(LEGS.paperdool(), null);
        setPaperdollItem(LEFT_HAND.paperdool(), null);
        setPaperdollItem(RIGHT_HAND.paperdool(), null);
        setPaperdollItem(HEAD.paperdool(), null);
        setPaperdollItem(FEET.paperdool(), null);
        setPaperdollItem(GLOVES.paperdool(), null);
        setPaperdollItem(CHEST.paperdool(), item);
    }

    private void equipHairAll(Item item) {
        setPaperdollItem(HAIR2.paperdool(), null);
        setPaperdollItem(HAIR.paperdool(), item);
    }

    private void equipHair2(Item item) {
        var hair = getPaperdollItem(HAIR_ALL.paperdool());
        if (nonNull(hair) && (hair.getBodyPart() == BodyPart.HAIR_ALL)) {
            setPaperdollItem(HAIR.paperdool(), null);
        }
        setPaperdollItem(HAIR2.paperdool(), item);
    }

    private void equipHair(Item item) {
        var hair = getPaperdollItem(HAIR_ALL.paperdool());
        if (nonNull(hair) && hair.getBodyPart() == BodyPart.HAIR_ALL) {
            setPaperdollItem(HAIR2.paperdool(), null);
        }
        setPaperdollItem(HAIR.paperdool(), item);
    }

    private void equipLeg(Item item) {
        var chest = getPaperdollItem(CHEST.paperdool());
        if (nonNull(chest) && chest.getBodyPart() == BodyPart.FULL_ARMOR) {
            setPaperdollItem(CHEST.paperdool(), null);
        }
        setPaperdollItem(LEGS.paperdool(), item);
    }

    private void equipFullArmor(Item item) {
        setPaperdollItem(LEGS.paperdool(), null);
        setPaperdollItem(CHEST.paperdool(), item);
    }

    private void equipFinger(Item item) {
        if(isNull( getPaperdollItem(LEFT_FINGER.paperdool())) || nonNull(getPaperdollItem(RIGHT_FINGER.paperdool()))) {
            setPaperdollItem(LEFT_FINGER.paperdool(), item);
        } else {
            setPaperdollItem(RIGHT_FINGER.paperdool(), item);
        }
    }

    private void equipEar(Item item) {
        if(isNull( getPaperdollItem(LEFT_EAR.paperdool())) || nonNull( getPaperdollItem(RIGHT_EAR.paperdool()))) {
            setPaperdollItem(LEFT_EAR.paperdool(), item);
        } else {
            setPaperdollItem(RIGHT_EAR.paperdool(), item);
        }
    }

    private void equipLeftHand(Item item) {
        var rightHand = getPaperdollItem(RIGHT_HAND.paperdool());

        if(nonNull(rightHand) && rightHand.getBodyPart() == TWO_HAND && !(isEquipArrow(rightHand, item) || isEquipBolt(rightHand, item) || isEquipLure(rightHand, item))) {
            setPaperdollItem(RIGHT_HAND.paperdool(), null);
        }
        setPaperdollItem(LEFT_HAND.paperdool(), item);
    }

    private boolean isEquipLure(Item rightHand, Item item) {
        return rightHand.getItemType() == WeaponType.FISHING_ROD && item.getItemType() == EtcItemType.LURE;
    }

    private boolean isEquipBolt(Item rightHand, Item item) {
        return (rightHand.getItemType() == WeaponType.CROSSBOW || rightHand.getItemType() == WeaponType.TWO_HAND_CROSSBOW) && item.getItemType() == EtcItemType.BOLT;
    }

    private boolean isEquipArrow(Item rightHand, Item item) {
        return rightHand.getItemType() == WeaponType.BOW && item.getItemType() == EtcItemType.ARROW;
    }

    private void equipTwoHand(Item item) {
        setPaperdollItem(LEFT_HAND.paperdool(), null);
        setPaperdollItem(RIGHT_HAND.paperdool(), item);
    }

    private void equipArtifact(Item item) {
        if (getArtifactSlots() == 0) {
            return;
        }

        // FIXME non existent ids
        // Balance Artifact Equip
        if(isBetween(item.getId(), 48969, 48985)) {
            if(checkEquipArtifact(item, BodyPart.balanceArtifact(), 4 * getArtifactSlots())) {
                return;
            }
            setPaperdollItem(BodyPart.balanceArtifact(), item);
        }
        // Spirit Artifact Equip
        else if(isBetween(item.getId(), 48957, 48960)) {
            if(checkEquipArtifact(item, BodyPart.spiritArtifact(), getArtifactSlots())) {
                return;
            }
            setPaperdollItem(BodyPart.spiritArtifact(), item);
        }
        // Protection Artifact Equip
        else if(isBetween(item.getId(), 48961, 48964)) {
            if(checkEquipArtifact(item, BodyPart.protectionArtifact(), getArtifactSlots())) {
                return;
            }
            setPaperdollItem(BodyPart.protectionArtifact(), item);
        }
        // Support Artifact Equip
        else if(isBetween(item.getId(), 48965, 48968)) {
            if(checkEquipArtifact(item, BodyPart.supportArtifact(), getArtifactSlots())) {
                return;
            }
            setPaperdollItem(BodyPart.supportArtifact(), item);
        }
    }

    private boolean checkEquipArtifact(Item item, int initialArtifact, int count) {
        for (int i = initialArtifact; i < initialArtifact + count; i++) {
            if (isNull(getPaperdollItem(i))) {
                setPaperdollItem(i, item);
                return true;
            }
        }
        return false;
    }

    private void equipAgathion(Item item) {
        if (getAgathionSlots() == 0) {
            return;
        }
        equipOnSameOrEmpty(item, AGATHION.paperdool(), getAgathionSlots());
    }

    private void equipBroochJewel(Item item) {
        if (getBroochJewelSlots() == 0) {
            return;
        }
        equipOnSameOrEmpty(item, BROOCH_JEWEL.paperdool(), getBroochJewelSlots());
    }

    private void equipTalisman(Item item) {
        if (getTalismanSlots() == 0) {
            return;
        }
        equipOnSameOrEmpty(item, TALISMAN.paperdool(), getTalismanSlots());
    }

    private void equipOnSameOrEmpty(Item item, int initialPaperdoll, int availableSlots) {
        int emptyPaperdoll = -1;

        for (int i = initialPaperdoll; i < initialPaperdoll + availableSlots; i++) {
            var paperdollItem = getPaperdollItem(i);
            if (nonNull(paperdollItem)) {
                if(paperdollItem.getId() == item.getId()) {
                    setPaperdollItem(i, item);
                    return;
                }
            }
            else if(emptyPaperdoll == -1) {
                emptyPaperdoll = i;
            }
        }

        if(emptyPaperdoll > 0) {
            setPaperdollItem(emptyPaperdoll, item);
        } else {
            setPaperdollItem(initialPaperdoll, item);
        }
    }

    /**
     * Refresh the weight of equipment loaded
     */
    @Override
    protected void refreshWeight() {
        long weight = 0;

        for (Item item : items.values()) {
            if ((item != null) && (item.getTemplate() != null)) {
                weight += item.getTemplate().getWeight() * item.getCount();
            }
        }
        _totalWeight = (int) min(weight, Integer.MAX_VALUE);
    }

    /**
     * @return the totalWeight.
     */
    public int getTotalWeight() {
        return _totalWeight;
    }

    /**
     * Return the Item of the arrows needed for this bow.
     *
     * @param bow : ItemTemplate designating the bow
     * @return Item pointing out arrows for bow
     */
    public Item findArrowForBow(ItemTemplate bow) {
        if (bow == null) {
            return null;
        }

        Item arrow = null;

        for (Item item : getItems()) {
            if (item.isEtcItem() && (item.getTemplate().getCrystalType() == bow.getCrystalType()) && (item.getEtcItem().getItemType() == EtcItemType.ARROW)) {
                arrow = item;
                break;
            }
        }

        // Get the Item corresponding to the item identifier and return it
        return arrow;
    }

    /**
     * Return the Item of the bolts needed for this crossbow.
     *
     * @param crossbow : ItemTemplate designating the crossbow
     * @return Item pointing out bolts for crossbow
     */
    public Item findBoltForCrossBow(ItemTemplate crossbow) {
        Item bolt = null;

        for (Item item : getItems()) {
            if (item.isEtcItem() && (item.getTemplate().getCrystalType() == crossbow.getCrystalType()) && (item.getEtcItem().getItemType() == EtcItemType.BOLT)) {
                bolt = item;
                break;
            }
        }

        // Get the Item corresponding to the item identifier and return it
        return bolt;
    }

    /**
     * Get back items in inventory from database
     */
    @Override
    public void restore() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM items WHERE owner_id=? AND (loc=? OR loc=?) ORDER BY loc_data")) {
            ps.setInt(1, getOwnerId());
            ps.setString(2, getBaseLocation().name());
            ps.setString(3, getEquipLocation().name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    final Item item = new Item(rs);
                    if (isPlayer(getOwner())) {
                        final Player player = (Player) getOwner();

                        if (!player.canOverrideCond(PcCondOverride.ITEM_CONDITIONS) && !player.isHero() && item.isHeroItem()) {
                            item.setItemLocation(ItemLocation.INVENTORY);
                        }
                    }

                    World.getInstance().addObject(item);

                    // If stackable item is found in inventory just add to current quantity
                    if (item.isStackable() && (getItemByItemId(item.getId()) != null)) {
                        addItem("Restore", item, getOwner().getActingPlayer(), null);
                    } else {
                        addItem(item);
                    }
                }
            }
            refreshWeight();
        } catch (Exception e) {
            LOGGER.warn("Could not restore inventory: " + e.getMessage(), e);
        }
    }

    public int getTalismanSlots() {
        return getOwner().getActingPlayer().getStat().getTalismanSlots();
    }

    public int getArtifactSlots() {
        return min(getOwner().getActingPlayer().getStat().getArtifactSlots(), 3);
    }

    public int getBroochJewelSlots() {
        return getOwner().getActingPlayer().getStat().getBroochJewelSlots();
    }

    public int getAgathionSlots() {
        return getOwner().getActingPlayer().getStat().getAgathionSlots();
    }

    public boolean canEquipCloak() {
        return getOwner().getActingPlayer().getStat().canEquipCloak();
    }

    /**
     * Re-notify to paperdoll listeners every equipped item
     */
    public void reloadEquippedItems() {
        int slot;

        for (Item item : paperdoll) {
            if (item == null) {
                continue;
            }

            slot = item.getLocationSlot();

            for (InventoryListener listener : listeners) {
                if (listener == null) {
                    continue;
                }

                listener.notifyUnequiped(slot, item, this);
                listener.notifyEquiped(slot, item, this);
            }
        }
        if (isPlayer(getOwner())) {
            getOwner().sendPacket(new ExUserInfoEquipSlot(getOwner().getActingPlayer()));
        }
    }

    public int getArmorMinEnchant() {
        if (!isPlayer(getOwner())) {
            return 0;
        }

        final Player player = getOwner().getActingPlayer();
        int maxSetEnchant = 0;
        for (Item item : getPaperdollItems()) {
            for (ArmorSet set : ArmorSetsData.getInstance().getSets(item.getId())) {
                final int enchantEffect = set.getLowestSetEnchant(player);
                if (enchantEffect > maxSetEnchant) {
                    maxSetEnchant = enchantEffect;
                }
            }
        }
        return maxSetEnchant;
    }

    public int getWeaponEnchant() {
        final Item item = getPaperdollItem(PAPERDOLL_RHAND);
        return item != null ? item.getEnchantLevel() : 0;
    }

    /**
     * Blocks the given item slot from being equipped.
     *
     * @param itemSlot mask from ItemTemplate
     */
    public void blockItemSlot(long itemSlot) {
        blockedItemSlotsMask |= itemSlot;
    }

    /**
     * Unblocks the given item slot so it can be equipped.
     *
     * @param itemSlot mask from ItemTemplate
     */
    public void unblockItemSlot(long itemSlot) {
        blockedItemSlotsMask &= ~itemSlot;
    }

    /**
     * @param bodyPart item bodyPart
     * @return if the given item slot is blocked or not.
     */
    public boolean isItemSlotBlocked(BodyPart bodyPart) {
        return falseIfNullOrElse(bodyPart, part -> {
           var slot = part.getId();
           return (blockedItemSlotsMask & slot) == slot;
        });
    }

    /**
     * Reduce the arrow number of the Creature.<br>
     * <B><U> Overridden in </U> :</B>
     * <li>Player</li>
     *
     * @param type
     */
    public void reduceArrowCount(EtcItemType type) {
        // default is to do nothing
    }

    /**
     * Gets the items in paperdoll slots filtered by filter.
     *
     * @param filters multiple filters
     * @return the filtered items in inventory
     */
    @SafeVarargs
    public final Collection<Item> getPaperdollItems(Predicate<Item>... filters) {
        Predicate<Item> filter = Objects::nonNull;
        for (Predicate<Item> additionalFilter : filters) {
            filter = filter.and(additionalFilter);
        }
        return Arrays.stream(paperdoll).filter(filter).collect(Collectors.toCollection(LinkedList::new));
    }


    private static final class ChangeRecorder implements InventoryListener {
        private final Set<Item> changed = ConcurrentHashMap.newKeySet();

        private ChangeRecorder(Inventory inventory) {
            inventory.addPaperdollListener(this);
        }

        @Override
        public void notifyEquiped(int slot, Item item, Inventory inventory) {
            changed.add(item);
        }

        @Override
        public void notifyUnequiped(int slot, Item item, Inventory inventory) {
            changed.add(item);
        }

        private Set<Item> getChangedItems() {
            return changed;
        }
    }
}
