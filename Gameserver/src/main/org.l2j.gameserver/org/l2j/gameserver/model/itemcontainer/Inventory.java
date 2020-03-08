package org.l2j.gameserver.model.itemcontainer;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.api.item.InventoryListener;
import org.l2j.gameserver.data.xml.impl.ArmorSetsData;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.enums.InventorySlot;
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
import java.util.function.*;

import static java.lang.Math.min;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.util.Util.*;
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

    public static final int BEAUTY_TICKET_ID = 36308;
    public static final long MAX_ADENA = Config.MAX_ADENA;

    protected static final Logger LOGGER = LoggerFactory.getLogger(Inventory.class);

    private final EnumMap<InventorySlot, Item> paperdoll;
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
        paperdoll = new EnumMap<>(InventorySlot.class);
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
        if (item.isEquipped() && item.getBodyPart() != NONE) {
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
        if(item.isEquipped()) {
            for (var entry : paperdoll.entrySet()) {
                if(entry.getValue().equals(item)) {
                    unEquipItemInSlot(entry.getKey());
                    break;
                }
            }
        }
        return super.removeItem(item);
    }

    /**
     * @param slot the slot.
     * @return the item in the paperdoll slot
     */
    public Item getPaperdollItem(InventorySlot slot) {
        return paperdoll.get(slot);
    }

    /**
     * @param slot the slot.
     * @return {@code true} if specified paperdoll slot is empty, {@code false} otherwise
     */
    public boolean isPaperdollSlotEmpty(InventorySlot slot) {
        return !paperdoll.containsKey(slot);
    }

    /**
     * Returns the item in the paperdoll ItemTemplate slot
     *
     * @param bodyPart identifier
     * @return Item
     */
    public Item getItemByBodyPart(BodyPart bodyPart) {
        if(isNull(bodyPart.slot())) {
            return null;
        }
        return paperdoll.get(bodyPart.slot());
    }

    /**
     * Returns the ID of the item in the paperdoll slot
     *
     * @param slot : int designating the slot
     * @return int designating the ID of the item
     */
    public int getPaperdollItemId(InventorySlot slot) {
        return zeroIfNullOrElse(paperdoll.get(slot), Item::getId);
    }

    /**
     * Returns the ID of the item in the paperdoll slot
     *
     * @param slot : int designating the slot
     * @return int designating the ID of the item
     */
    public int getPaperdollItemDisplayId(InventorySlot slot) {
        return zeroIfNullOrElse(paperdoll.get(slot), Item::getDisplayId);
    }

    public VariationInstance getPaperdollAugmentation(InventorySlot slot) {
        return computeIfNonNull(paperdoll.get(slot), Item::getAugmentation);
    }

    /**
     * Returns the objectID associated to the item in the paperdoll slot
     *
     * @param slot : int pointing out the slot
     * @return int designating the objectID
     */
    public int getPaperdollObjectId(InventorySlot slot) {
        return zeroIfNullOrElse(paperdoll.get(slot), Item::getObjectId);
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
    public synchronized Item setPaperdollItem(InventorySlot slot, Item item) {
        var old = paperdoll.get(slot);
        if (old != item) {
            if (nonNull(old)) {
                paperdoll.remove(slot);

                old.setItemLocation(getBaseLocation());
                old.setLastChange(Item.MODIFIED);

                _wearedMask = paperdoll.values().stream().mapToInt(Item::getItemMask).reduce(0, (l, r) -> l | r);
                listeners.forEach(l -> l.notifyUnequiped(slot, old, this));
                old.updateDatabase();
            }

            if (nonNull(item)) {
                paperdoll.put(slot, item);
                item.setItemLocation(getEquipLocation(), slot.getId());
                item.setLastChange(Item.MODIFIED);
                _wearedMask |= item.getTemplate().getItemMask();
                listeners.forEach(l -> l.notifyEquiped(slot, item, this));
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
    public Item unEquipItemInSlot(InventorySlot pdollSlot) {
        return setPaperdollItem(pdollSlot, null);
    }

    /**
     * Unequips item in slot and returns alterations<BR>
     * <B>If you dont need return value use {@link Inventory#unEquipItemInSlot(InventorySlot)} instead</B>
     *
     * @param slot : int designating the slot
     * @return Item[] : list of items altered
     */
    public Set<Item> unEquipItemInSlotAndRecord(InventorySlot slot) {
        final ChangeRecorder recorder = newRecorder();

        try {
            unEquipItemInSlot(slot);
        } finally {
            removePaperdollListener(recorder);
        }
        return recorder.getChangedItems();
    }

    /**
     * Unequips item in slot (i.e. equips with default value)
     *
     * @param bodyPart : int designating the slot
     * @return {@link Item} designating the item placed in the slot
     */
    public Item unEquipItemInBodySlot(BodyPart bodyPart) {
        InventorySlot pdollSlot = switch (bodyPart) {
            case HAIR_ALL -> InventorySlot.HAIR;
            case ALL_DRESS, FULL_ARMOR -> InventorySlot.CHEST;
            default -> bodyPart.slot();
        };

        if (nonNull(pdollSlot)) {
            if(pdollSlot == InventorySlot.TWO_HAND) {
                paperdoll.remove(InventorySlot.TWO_HAND);
                pdollSlot = InventorySlot.RIGHT_HAND;
            }
            return setPaperdollItem(pdollSlot, null);
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

        if(bodyPart.isAnyOf(TWO_HAND, LEFT_HAND, RIGHT_HAND, LEGS, FEET, GLOVES, HEAD)) {
            var formal = getPaperdollItem(InventorySlot.CHEST);
            if(nonNull(formal) && item.getId() != WEDDING_BOUQUET && formal.getBodyPart() == BodyPart.ALL_DRESS) {
                return;
            }
        }

        switch (bodyPart) {
            case TWO_HAND -> equipTwoHand(item);
            case LEFT_HAND -> equipLeftHand(item);
            case RIGHT_HAND -> equipRightHand(item);
            case FEET, HEAD, GLOVES -> equipCheckingDress(bodyPart, item);
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
            case NECK, CHEST, UNDERWEAR, BACK, LEFT_BRACELET, RIGHT_BRACELET, BELT, BROOCH, ARTIFACT_BOOK -> setPaperdollItem(bodyPart.slot(), item);
            default -> LOGGER.warn("Unknown body slot {} for Item ID: {}", bodyPart, item.getId());
        }
    }

    private void equipCheckingDress(BodyPart bodyPart, Item item) {
        checkEquippedDress();
        setPaperdollItem(bodyPart.slot(), item);
    }

    private void equipRightHand(Item item) {
        checkEquippedDress();

        if(!isPaperdollSlotEmpty(InventorySlot.TWO_HAND)) {
            setPaperdollItem(InventorySlot.TWO_HAND, null);
        }
        setPaperdollItem(InventorySlot.RIGHT_HAND, item);
    }

    private void equipAllDress(Item item) {
        setPaperdollItem(InventorySlot.LEGS, null);
        setPaperdollItem(InventorySlot.LEFT_HAND, null);
        setPaperdollItem(InventorySlot.RIGHT_HAND, null);
        setPaperdollItem(InventorySlot.HEAD, null);
        setPaperdollItem(InventorySlot.FEET, null);
        setPaperdollItem(InventorySlot.GLOVES, null);
        setPaperdollItem(InventorySlot.TWO_HAND, null);
        setPaperdollItem(InventorySlot.CHEST, item);
    }

    private void equipHairAll(Item item) {
        setPaperdollItem(InventorySlot.HAIR2, null);
        setPaperdollItem(InventorySlot.HAIR, item);
    }

    private void equipHair2(Item item) {
        var hair = getPaperdollItem(InventorySlot.HAIR);
        if (nonNull(hair) && (hair.getBodyPart() == HAIR_ALL)) {
            setPaperdollItem(InventorySlot.HAIR, null);
        }
        setPaperdollItem(InventorySlot.HAIR2, item);
    }

    private void equipHair(Item item) {
        var hair = getPaperdollItem(InventorySlot.HAIR);
        if (nonNull(hair) && hair.getBodyPart() == HAIR_ALL) {
            setPaperdollItem(InventorySlot.HAIR2, null);
        }
        setPaperdollItem(InventorySlot.HAIR, item);
    }

    private void equipLeg(Item item) {
        var chest = getPaperdollItem(InventorySlot.CHEST);
        if (nonNull(chest) && chest.getBodyPart().isAnyOf(FULL_ARMOR, ALL_DRESS)) {
            setPaperdollItem(InventorySlot.CHEST, null);
        }
        setPaperdollItem(InventorySlot.LEGS, item);
    }

    private void equipFullArmor(Item item) {
        setPaperdollItem(InventorySlot.LEGS, null);
        setPaperdollItem(InventorySlot.CHEST, item);
    }

    private void equipFinger(Item item) {
        if(  isPaperdollSlotEmpty(InventorySlot.LEFT_FINGER) || !isPaperdollSlotEmpty(InventorySlot.RIGHT_FINGER)) {
            setPaperdollItem(InventorySlot.LEFT_FINGER, item);
        } else {
            setPaperdollItem(InventorySlot.RIGHT_FINGER, item);
        }
    }

    private void equipEar(Item item) {
        if( isPaperdollSlotEmpty(InventorySlot.LEFT_EAR) || !isPaperdollSlotEmpty(InventorySlot.RIGHT_EAR)) {
            setPaperdollItem(InventorySlot.LEFT_EAR, item);
        } else {
            setPaperdollItem(InventorySlot.RIGHT_EAR, item);
        }
    }

    private void equipLeftHand(Item item) {
        var weapon = getPaperdollItem(InventorySlot.TWO_HAND);
        if(nonNull(weapon) && !(isEquipArrow(weapon, item) || isEquipBolt(weapon, item) || isEquipLure(weapon, item))) {
            setPaperdollItem(InventorySlot.TWO_HAND, null);
            setPaperdollItem(InventorySlot.RIGHT_HAND, null);
        }

        checkEquippedDress();
        setPaperdollItem(InventorySlot.LEFT_HAND, item);
    }

    private void checkEquippedDress() {
        var dress = getPaperdollItem(InventorySlot.CHEST);
        if(nonNull(dress) && dress.getBodyPart() == ALL_DRESS) {
            setPaperdollItem(InventorySlot.CHEST, null);
        }
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
        checkEquippedDress();
        setPaperdollItem(InventorySlot.LEFT_HAND, null);
        setPaperdollItem(InventorySlot.RIGHT_HAND, item);
        paperdoll.put(InventorySlot.TWO_HAND, item);
    }

    private void equipArtifact(Item item) {
        if (getArtifactSlots() == 0) {
            return;
        }

        // FIXME non existent ids
        // Balance Artifact Equip
        if(isBetween(item.getId(), 48969, 48985)) {
            if(checkEquipArtifact(item, InventorySlot.balanceArtifacts())) {
                return;
            }
            setPaperdollItem(InventorySlot.ARTIFACT1, item);
        }
        // Spirit Artifact Equip
        else if(isBetween(item.getId(), 48957, 48960)) {
            if(checkEquipArtifact(item, InventorySlot.spiritArtifacts())) {
                return;
            }
            setPaperdollItem(InventorySlot.ARTIFACT13, item);
        }
        // Protection Artifact Equip
        else if(isBetween(item.getId(), 48961, 48964)) {
            if(checkEquipArtifact(item, InventorySlot.protectionArtifacts())) {
                return;
            }
            setPaperdollItem(InventorySlot.ARTIFACT16, item);
        }
        // Support Artifact Equip
        else if(isBetween(item.getId(), 48965, 48968)) {
            if(checkEquipArtifact(item, InventorySlot.supportArtifact())) {
                return;
            }
            setPaperdollItem(InventorySlot.ARTIFACT19, item);
        }
    }

    private boolean checkEquipArtifact(Item item, EnumSet<InventorySlot> slots) {
        for (InventorySlot slot : slots) {
            if(isPaperdollSlotEmpty(slot)) {
                setPaperdollItem(slot, item);
                return true;
            }
        }
        return false;
    }

    private void equipAgathion(Item item) {
        equipOnSameOrEmpty(item, InventorySlot.agathions(), getAgathionSlots());
    }

    private void equipBroochJewel(Item item) {
        equipOnSameOrEmpty(item, InventorySlot.brochesJewel(), getBroochJewelSlots());
    }

    private void equipTalisman(Item item) {
        equipOnSameOrEmpty(item, InventorySlot.talismans(), getTalismanSlots());
    }

    private void equipOnSameOrEmpty(Item item, EnumSet<InventorySlot> slots, int availableSlots) {
        if(availableSlots < 1 || slots.isEmpty()) {
            return;
        }

        InventorySlot emptySlot = null;
        int i = 0;
        for (InventorySlot slot : slots) {
            if(i++ >= availableSlots) {
                break;
            }
            if(!isPaperdollSlotEmpty(slot) && item.getId() == getPaperdollItemId(slot)) {
                setPaperdollItem(slot, item);
                return;
            } else if(isNull(emptySlot) && isPaperdollSlotEmpty(slot)) {
                emptySlot = slot;
            }
        }

        if(nonNull(emptySlot)) {
            setPaperdollItem(emptySlot, item);
        } else {
            setPaperdollItem(slots.iterator().next(), item);
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
        if (isNull(bow)) {
            return null;
        }

        Item arrow = null;

        for (Item item : getItems()) {
            if (item.isEtcItem() && (item.getTemplate().getCrystalType() == bow.getCrystalType()) && (item.getItemType() == EtcItemType.ARROW)) {
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

                    try {
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
                    }catch (Exception e) {
                        LOGGER.warn("Could not restore item {}  for {}", rs.getInt("item_id"), getOwner());
                    }
                }
            }
            refreshWeight();
        } catch (Exception e) {
            LOGGER.warn("Could not restore inventory: " + e.getMessage(), e);
        }
    }

    public int getTalismanSlots() {
        return getOwner().getActingPlayer().getStats().getTalismanSlots();
    }

    public int getArtifactSlots() {
        return min(getOwner().getActingPlayer().getStats().getArtifactSlots(), 3);
    }

    public int getBroochJewelSlots() {
        return getOwner().getActingPlayer().getStats().getBroochJewelSlots();
    }

    public int getAgathionSlots() {
        return getOwner().getActingPlayer().getStats().getAgathionSlots();
    }

    public boolean canEquipCloak() {
        return getOwner().getActingPlayer().getStats().canEquipCloak();
    }

    /**
     * Re-notify to paperdoll listeners every equipped item
     */
    public void reloadEquippedItems() {
        paperdoll.forEach((slot, item) -> listeners.forEach(l -> {
            l.notifyUnequiped(slot, item, this);
            l.notifyEquiped(slot, item, this);
        }));
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
        for (Item item : paperdoll.values()) {
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
        final Item item = getPaperdollItem(InventorySlot.RIGHT_HAND);
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
        return new LinkedList<>(paperdoll.values());
    }

    public double calcForEachEquippedItem(ToDoubleFunction<Item> function, double identity, DoubleBinaryOperator accumulator) {
        return paperdoll.values().stream().mapToDouble(function).reduce(identity, accumulator);
    }

    public void forEachEquippedItem(Consumer<Item> action) {
        paperdoll.values().forEach(action);
    }

    public void forEachEquippedItem(Consumer<Item> action, Predicate<Item> predicate) {
        paperdoll.values().stream().filter(predicate).forEach(action);
    }

    public int countEquippedItems(Predicate<Item> predicate) {
        return (int) paperdoll.values().stream().filter(predicate).count();
    }

    public boolean existsEquippedItem(Predicate<Item> predicate) {
        return paperdoll.values().stream().anyMatch(predicate);
    }

    private static final class ChangeRecorder implements InventoryListener {
        private final Set<Item> changed = ConcurrentHashMap.newKeySet();

        private ChangeRecorder(Inventory inventory) {
            inventory.addPaperdollListener(this);
        }

        @Override
        public void notifyEquiped(InventorySlot slot, Item item, Inventory inventory) {
            changed.add(item);
        }

        @Override
        public void notifyUnequiped(InventorySlot slot, Item item, Inventory inventory) {
            changed.add(item);
        }

        private Set<Item> getChangedItems() {
            return changed;
        }
    }
}
