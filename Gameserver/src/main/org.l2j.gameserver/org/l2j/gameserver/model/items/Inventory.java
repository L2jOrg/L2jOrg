package org.l2j.gameserver.model.items;

import org.l2j.commons.dao.JdbcEntityState;
import org.l2j.commons.listener.Listener;
import org.l2j.commons.listener.ListenerList;
import org.l2j.gameserver.data.xml.holder.ItemHolder;
import org.l2j.gameserver.handler.items.IItemHandler;
import org.l2j.gameserver.listener.inventory.OnEquipListener;
import org.l2j.gameserver.model.Playable;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.items.ItemInstance.ItemLocation;
import org.l2j.gameserver.model.items.listeners.StatsListener;
import org.l2j.gameserver.stats.Stats;
import org.l2j.gameserver.stats.funcs.FuncTemplate;
import org.l2j.gameserver.templates.item.EtcItemTemplate.EtcItemType;
import org.l2j.gameserver.templates.item.ItemTemplate;
import org.l2j.gameserver.templates.item.WeaponTemplate.WeaponType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;

public abstract class Inventory extends ItemContainer
{
    private static final Logger _log = LoggerFactory.getLogger(Inventory.class);

    public static final int PAPERDOLL_PENDANT = 0;
    public static final int PAPERDOLL_REAR = 1;
    public static final int PAPERDOLL_LEAR = 2;
    public static final int PAPERDOLL_NECK = 3;
    public static final int PAPERDOLL_RFINGER = 4;
    public static final int PAPERDOLL_LFINGER = 5;
    public static final int PAPERDOLL_HEAD = 6;
    public static final int PAPERDOLL_RHAND = 7;
    public static final int PAPERDOLL_LHAND = 8;
    public static final int PAPERDOLL_GLOVES = 9;
    public static final int PAPERDOLL_CHEST = 10;
    public static final int PAPERDOLL_LEGS = 11;
    public static final int PAPERDOLL_FEET = 12;
    public static final int PAPERDOLL_BACK = 13;
    public static final int PAPERDOLL_LRHAND = 14;
    public static final int PAPERDOLL_HAIR = 15;
    public static final int PAPERDOLL_DHAIR = 16;
    public static final int PAPERDOLL_RBRACELET = 17;
    public static final int PAPERDOLL_LBRACELET = 18;
    public static final int PAPERDOLL_DECO1 = 19;
    public static final int PAPERDOLL_DECO2 = 20;
    public static final int PAPERDOLL_DECO3 = 21;
    public static final int PAPERDOLL_DECO4 = 22;
    public static final int PAPERDOLL_DECO5 = 23;
    public static final int PAPERDOLL_DECO6 = 24;
    public static final int PAPERDOLL_BELT = 25;
    public static final int PAPERDOLL_BROOCH = 26;
    public static final int PAPERDOLL_JEWEL1 = 27;
    public static final int PAPERDOLL_JEWEL2 = 28;
    public static final int PAPERDOLL_JEWEL3 = 29;
    public static final int PAPERDOLL_JEWEL4 = 30;
    public static final int PAPERDOLL_JEWEL5 = 31;
    public static final int PAPERDOLL_JEWEL6 = 32;

    public static final int PAPERDOLL_MAX = 33;

    public static final int[] PAPERDOLL_ORDER =
            {
                    Inventory.PAPERDOLL_PENDANT,
                    Inventory.PAPERDOLL_REAR,
                    Inventory.PAPERDOLL_LEAR,
                    Inventory.PAPERDOLL_NECK,
                    Inventory.PAPERDOLL_RFINGER,
                    Inventory.PAPERDOLL_LFINGER,
                    Inventory.PAPERDOLL_HEAD,
                    Inventory.PAPERDOLL_RHAND,
                    Inventory.PAPERDOLL_LHAND,
                    Inventory.PAPERDOLL_GLOVES,
                    Inventory.PAPERDOLL_CHEST,
                    Inventory.PAPERDOLL_LEGS,
                    Inventory.PAPERDOLL_FEET,
                    Inventory.PAPERDOLL_BACK,
                    Inventory.PAPERDOLL_LRHAND,
                    Inventory.PAPERDOLL_HAIR,
                    Inventory.PAPERDOLL_DHAIR,
                    Inventory.PAPERDOLL_RBRACELET,
                    Inventory.PAPERDOLL_LBRACELET,
                    Inventory.PAPERDOLL_DECO1,
                    Inventory.PAPERDOLL_DECO2,
                    Inventory.PAPERDOLL_DECO3,
                    Inventory.PAPERDOLL_DECO4,
                    Inventory.PAPERDOLL_DECO5,
                    Inventory.PAPERDOLL_DECO6,
                    Inventory.PAPERDOLL_BELT,
                    Inventory.PAPERDOLL_BROOCH,
                    Inventory.PAPERDOLL_JEWEL1,
                    Inventory.PAPERDOLL_JEWEL2,
                    Inventory.PAPERDOLL_JEWEL3,
                    Inventory.PAPERDOLL_JEWEL4,
                    Inventory.PAPERDOLL_JEWEL5,
                    Inventory.PAPERDOLL_JEWEL6
            };

    public class InventoryListenerList extends ListenerList<Playable>
    {
        public void onEquip(int slot, ItemInstance item)
        {
            for(Listener<Playable> listener : getListeners())
                ((OnEquipListener) listener).onEquip(slot, item, getActor());
        }

        public void onUnequip(int slot, ItemInstance item)
        {
            for(Listener<Playable> listener : getListeners())
                ((OnEquipListener) listener).onUnequip(slot, item, getActor());
        }
    }

    public static class ItemOrderComparator implements Comparator<ItemInstance>
    {
        private static final Comparator<ItemInstance> instance = new ItemOrderComparator();

        public static final Comparator<ItemInstance> getInstance()
        {
            return instance;
        }

        @Override
        public int compare(ItemInstance o1, ItemInstance o2)
        {
            if(o1 == null || o2 == null)
                return 0;
            return o1.getLocData() - o2.getLocData();
        }
    }

    protected final int _ownerId;

    protected final ItemInstance[] _paperdoll = new ItemInstance[PAPERDOLL_MAX];
    protected final InventoryListenerList _listeners = new InventoryListenerList();

    protected int _totalWeight;
    // used to quickly check for using of items of special type
    protected long _wearedMask;

    protected Inventory(int ownerId)
    {
        _ownerId = ownerId;

        addListener(StatsListener.getInstance());
    }

    public abstract Playable getActor();

    protected abstract ItemLocation getBaseLocation();

    protected abstract ItemLocation getEquipLocation();

    public int getOwnerId()
    {
        return _ownerId;
    }

    protected void onRestoreItem(ItemInstance item)
    {
        _totalWeight += item.getTemplate().getWeight() * item.getCount();

        IItemHandler handler = item.getTemplate().getHandler();
        if(handler != null)
            handler.onRestoreItem(getActor(), item);
    }

    @Override
    protected void onAddItem(ItemInstance item)
    {
        item.setOwnerId(getOwnerId());
        item.setLocation(getBaseLocation());
        item.setLocData(findSlot(item.getTemplate().isQuest()));
        if(item.getJdbcState().isSavable())
        {
            item.save();
        }
        else
        {
            item.setJdbcState(JdbcEntityState.UPDATED);
            item.update();
        }

        sendAddItem(item);
        refreshWeight();

        IItemHandler handler = item.getTemplate().getHandler();
        if(handler != null)
            handler.onAddItem(getActor(), item);
    }

    @Override
    protected void onModifyItem(ItemInstance item)
    {
        item.setJdbcState(JdbcEntityState.UPDATED);
        item.update();

        sendModifyItem(item);
        refreshWeight();
    }

    @Override
    protected void onRemoveItem(ItemInstance item)
    {
        if(item.isEquipped())
            unEquipItem(item);

        sendRemoveItem(item);

        item.setLocData(-1);

        refreshWeight();

        IItemHandler handler = item.getTemplate().getHandler();
        if(handler != null)
            handler.onRemoveItem(getActor(), item);
    }

    @Override
    protected void onDestroyItem(ItemInstance item)
    {
        item.setCount(0L);
        item.delete();
    }

    protected void onEquip(int slot, ItemInstance item)
    {
        item.setLocation(getEquipLocation());
        item.setLocData(slot);
        item.setEquipped(true);
        item.setJdbcState(JdbcEntityState.UPDATED);

        _listeners.onEquip(slot, item);

        _wearedMask |= item.getTemplate().getItemMask();

        sendEquipInfo(slot);
        sendModifyItem(item);
    }

    protected void onReequip(int slot, ItemInstance newItem, ItemInstance oldItem)
    {
        oldItem.setLocation(getBaseLocation());
        oldItem.setLocData(findSlot(oldItem.getTemplate().isQuest()));
        oldItem.setEquipped(false);
        oldItem.setJdbcState(JdbcEntityState.UPDATED);

        oldItem.setChargedSoulshotPower(0);
        oldItem.setChargedSpiritshotPower(0);
        oldItem.setChargedFishshotPower(0);

        _listeners.onUnequip(slot, oldItem);

        _wearedMask &= ~oldItem.getTemplate().getItemMask();

        newItem.setLocation(getEquipLocation());
        newItem.setLocData(slot);
        newItem.setEquipped(true);
        newItem.setJdbcState(JdbcEntityState.UPDATED);

        _listeners.onEquip(slot, newItem);

        _wearedMask |= newItem.getTemplate().getItemMask();

        sendEquipInfo(slot);
        sendModifyItem(newItem, oldItem);
    }

    protected void onUnequip(int slot, ItemInstance item)
    {
        item.setLocation(getBaseLocation());
        item.setLocData(findSlot(item.getTemplate().isQuest()));
        item.setEquipped(false);
        item.setJdbcState(JdbcEntityState.UPDATED);

        item.setChargedSoulshotPower(0);
        item.setChargedSpiritshotPower(0);
        item.setChargedFishshotPower(0);

        _listeners.onUnequip(slot, item);

        _wearedMask &= ~item.getTemplate().getItemMask();

        sendEquipInfo(slot);
        sendModifyItem(item);
    }

    /**
     * Находит и возвращает пустой слот в инвентаре.
     */
    private int findSlot(boolean quest)
    {
        ItemInstance item;
        int slot = 0;
        loop: for(slot = 0; slot < _items.size(); slot++)
        {
            for(int i = 0; i < _items.size(); i++)
            {
                item = _items.get(i);
                if(item.isEquipped()) // игнорируем надетое и квестовые вещи
                    continue;
                if(!quest && item.getTemplate().isQuest())
                    continue;
                if(quest && !item.getTemplate().isQuest())
                    continue;
                if(item.getEquipSlot() == slot) // слот занят?
                    continue loop;
            }
            break;
        }
        return slot; // слот не занят, возвращаем
    }

    public ItemInstance getPaperdollItem(int slot)
    {
        return _paperdoll[slot];
    }

    public ItemInstance[] getPaperdollItems()
    {
        return _paperdoll;
    }

    public int getPaperdollItemId(int slot)
    {
        ItemInstance item = getPaperdollItem(slot);
        if(item != null)
            return item.getItemId();
        else if(slot == PAPERDOLL_HAIR)
        {
            item = _paperdoll[PAPERDOLL_DHAIR];
            if(item != null)
                return item.getItemId();
        }

        return 0;
    }

    public int getPaperdollVisualId(int slot)
    {
        ItemInstance item = getPaperdollItem(slot);
        if(item != null)
        {
            if(item.getVisualId() > 0)
                return item.getVisualId();
        }
        else if(slot == PAPERDOLL_HAIR)
        {
            item = _paperdoll[PAPERDOLL_DHAIR];
            if(item != null)
            {
                if(item.getVisualId() > 0)
                    return item.getVisualId();
            }
        }
        return 0;
    }

    public int getPaperdollObjectId(int slot)
    {
        ItemInstance item = _paperdoll[slot];
        if(item != null)
            return item.getObjectId();
        else if(slot == PAPERDOLL_HAIR)
        {
            item = _paperdoll[PAPERDOLL_DHAIR];
            if(item != null)
                return item.getObjectId();
        }
        return 0;
    }

    public void addListener(OnEquipListener listener)
    {
        _listeners.add(listener);
    }

    public void removeListener(OnEquipListener listener)
    {
        _listeners.remove(listener);
    }

    public ItemInstance setPaperdollItem(int slot, ItemInstance item)
    {
        ItemInstance old;
        writeLock();
        try
        {
            old = _paperdoll[slot];
            if(old != item)
            {
                if(old != null && item != null)
                {
                    _paperdoll[slot] = item;
                    onReequip(slot, item, old);
                }
                else
                {
                    if(old != null)
                    {
                        _paperdoll[slot] = null;
                        onUnequip(slot, old);
                    }
                    if(item != null)
                    {
                        _paperdoll[slot] = item;
                        onEquip(slot, item);
                    }
                }
            }
        }
        finally
        {
            writeUnlock();
        }
        return old;
    }

    public long getWearedMask()
    {
        return _wearedMask;
    }

    public void unEquipItem(ItemInstance item)
    {
        if(item.isEquipped())
            unEquipItemInBodySlot(item.getBodyPart(), item);
    }

    public void unEquipItemInBodySlot(int bodySlot)
    {
        unEquipItemInBodySlot(bodySlot, null);
    }

    private void unEquipItemInBodySlot(int bodySlot, ItemInstance item)
    {
        int pdollSlot = -1;

        switch(bodySlot)
        {
            case ItemTemplate.SLOT_NECK:
                pdollSlot = PAPERDOLL_NECK;
                break;
            case ItemTemplate.SLOT_L_EAR:
                pdollSlot = PAPERDOLL_LEAR;
                break;
            case ItemTemplate.SLOT_R_EAR:
                pdollSlot = PAPERDOLL_REAR;
                break;
            case ItemTemplate.SLOT_L_EAR | ItemTemplate.SLOT_R_EAR:
                if(item == null)
                    return;
                if(getPaperdollItem(PAPERDOLL_LEAR) == item)
                    pdollSlot = PAPERDOLL_LEAR;
                if(getPaperdollItem(PAPERDOLL_REAR) == item)
                    pdollSlot = PAPERDOLL_REAR;
                break;
            case ItemTemplate.SLOT_L_FINGER:
                pdollSlot = PAPERDOLL_LFINGER;
                break;
            case ItemTemplate.SLOT_R_FINGER:
                pdollSlot = PAPERDOLL_RFINGER;
                break;
            case ItemTemplate.SLOT_L_FINGER | ItemTemplate.SLOT_R_FINGER:
                if(item == null)
                    return;
                if(getPaperdollItem(PAPERDOLL_LFINGER) == item)
                    pdollSlot = PAPERDOLL_LFINGER;
                if(getPaperdollItem(PAPERDOLL_RFINGER) == item)
                    pdollSlot = PAPERDOLL_RFINGER;
                break;
            case ItemTemplate.SLOT_HAIR:
                pdollSlot = PAPERDOLL_HAIR;
                break;
            case ItemTemplate.SLOT_DHAIR:
                pdollSlot = PAPERDOLL_DHAIR;
                break;
            case ItemTemplate.SLOT_HAIRALL:
                setPaperdollItem(PAPERDOLL_DHAIR, null); // This should be the same as in DHAIR
                pdollSlot = PAPERDOLL_HAIR;
                break;
            case ItemTemplate.SLOT_HEAD:
                pdollSlot = PAPERDOLL_HEAD;
                break;
            case ItemTemplate.SLOT_R_HAND:
                pdollSlot = PAPERDOLL_RHAND;
                break;
            case ItemTemplate.SLOT_L_HAND:
                pdollSlot = PAPERDOLL_LHAND;
                break;
            case ItemTemplate.SLOT_GLOVES:
                pdollSlot = PAPERDOLL_GLOVES;
                break;
            case ItemTemplate.SLOT_LEGS:
                pdollSlot = PAPERDOLL_LEGS;
                break;
            case ItemTemplate.SLOT_CHEST:
            case ItemTemplate.SLOT_FULL_ARMOR:
            case ItemTemplate.SLOT_FORMAL_WEAR:
                pdollSlot = PAPERDOLL_CHEST;
                break;
            case ItemTemplate.SLOT_BACK:
                pdollSlot = PAPERDOLL_BACK;
                break;
            case ItemTemplate.SLOT_FEET:
                pdollSlot = PAPERDOLL_FEET;
                break;
            case ItemTemplate.SLOT_BELT:
                pdollSlot = PAPERDOLL_BELT;
                break;
            case ItemTemplate.SLOT_LR_HAND:
                setPaperdollItem(PAPERDOLL_LHAND, null);
                pdollSlot = PAPERDOLL_RHAND;
                break;
            case ItemTemplate.SLOT_PENDANT:
                pdollSlot = PAPERDOLL_PENDANT;
                break;
            case ItemTemplate.SLOT_L_BRACELET:
                pdollSlot = PAPERDOLL_LBRACELET;
                break;
            case ItemTemplate.SLOT_R_BRACELET:
                pdollSlot = PAPERDOLL_RBRACELET;
                // При снятии правого браслета, снимаем и талисманы тоже
                setPaperdollItem(Inventory.PAPERDOLL_DECO1, null);
                setPaperdollItem(Inventory.PAPERDOLL_DECO2, null);
                setPaperdollItem(Inventory.PAPERDOLL_DECO3, null);
                setPaperdollItem(Inventory.PAPERDOLL_DECO4, null);
                setPaperdollItem(Inventory.PAPERDOLL_DECO5, null);
                setPaperdollItem(Inventory.PAPERDOLL_DECO6, null);
                break;
            case ItemTemplate.SLOT_DECO:
                if(item == null)
                    return;
                else if(getPaperdollItem(PAPERDOLL_DECO1) == item)
                    pdollSlot = PAPERDOLL_DECO1;
                else if(getPaperdollItem(PAPERDOLL_DECO2) == item)
                    pdollSlot = PAPERDOLL_DECO2;
                else if(getPaperdollItem(PAPERDOLL_DECO3) == item)
                    pdollSlot = PAPERDOLL_DECO3;
                else if(getPaperdollItem(PAPERDOLL_DECO4) == item)
                    pdollSlot = PAPERDOLL_DECO4;
                else if(getPaperdollItem(PAPERDOLL_DECO5) == item)
                    pdollSlot = PAPERDOLL_DECO5;
                else if(getPaperdollItem(PAPERDOLL_DECO6) == item)
                    pdollSlot = PAPERDOLL_DECO6;
                break;
            case ItemTemplate.SLOT_BROOCH:
                pdollSlot = PAPERDOLL_BROOCH;
                // При снятии брошки, снимаем и камни тоже
                setPaperdollItem(Inventory.PAPERDOLL_JEWEL1, null);
                setPaperdollItem(Inventory.PAPERDOLL_JEWEL2, null);
                setPaperdollItem(Inventory.PAPERDOLL_JEWEL3, null);
                setPaperdollItem(Inventory.PAPERDOLL_JEWEL4, null);
                setPaperdollItem(Inventory.PAPERDOLL_JEWEL5, null);
                setPaperdollItem(Inventory.PAPERDOLL_JEWEL6, null);
                break;
            case ItemTemplate.SLOT_JEWEL:
                if(item == null)
                    return;
                else if(getPaperdollItem(PAPERDOLL_JEWEL1) == item)
                    pdollSlot = PAPERDOLL_JEWEL1;
                else if(getPaperdollItem(PAPERDOLL_JEWEL2) == item)
                    pdollSlot = PAPERDOLL_JEWEL2;
                else if(getPaperdollItem(PAPERDOLL_JEWEL3) == item)
                    pdollSlot = PAPERDOLL_JEWEL3;
                else if(getPaperdollItem(PAPERDOLL_JEWEL4) == item)
                    pdollSlot = PAPERDOLL_JEWEL4;
                else if(getPaperdollItem(PAPERDOLL_JEWEL5) == item)
                    pdollSlot = PAPERDOLL_JEWEL5;
                else if(getPaperdollItem(PAPERDOLL_JEWEL6) == item)
                    pdollSlot = PAPERDOLL_JEWEL6;
                break;
            default:
                _log.warn("Requested invalid body slot: " + bodySlot + ", Item: " + item + ", ownerId: '" + getOwnerId() + "'");
                return;
        }

        if(pdollSlot >= 0)
            setPaperdollItem(pdollSlot, null);
    }

    public void equipItem(ItemInstance item)
    {
        int bodySlot = item.getBodyPart();

        //TODO [G1ta0] затычка на статы повышающие HP/MP/CP
        double hp = getActor().getCurrentHp();
        double mp = getActor().getCurrentMp();
        double cp = getActor().getCurrentCp();

        switch(bodySlot)
        {
            case ItemTemplate.SLOT_LR_HAND:
            {
                setPaperdollItem(PAPERDOLL_LHAND, null);
                setPaperdollItem(PAPERDOLL_RHAND, item);
                break;
            }
            case ItemTemplate.SLOT_L_HAND:
            {
                final ItemInstance rHandItem = getPaperdollItem(PAPERDOLL_RHAND);

                final ItemTemplate rHandItemTemplate = rHandItem == null ? null : rHandItem.getTemplate();
                final ItemTemplate newItem = item.getTemplate();

                if(newItem.getItemType() == EtcItemType.ARROW || newItem.getItemType() == EtcItemType.ARROW_QUIVER)
                {
                    // arrows can be equipped only with bow
                    if(rHandItemTemplate == null)
                        return;
                    if(rHandItemTemplate.getItemType() != WeaponType.BOW)
                        return;
                    if(rHandItemTemplate.getGrade().extOrdinal() != newItem.getGrade().extOrdinal())
                        return;
                }
                else if(newItem.getItemType() == EtcItemType.BOLT || newItem.getItemType() == EtcItemType.BOLT_QUIVER)
                {
                    // bolts can be equipped only with crossbow
                    if(rHandItemTemplate == null)
                        return;
                    if(rHandItemTemplate.getItemType() != WeaponType.CROSSBOW && rHandItemTemplate.getItemType() != WeaponType.TWOHANDCROSSBOW)
                        return;
                    if(rHandItemTemplate.getGrade().extOrdinal() != newItem.getGrade().extOrdinal())
                        return;
                }
                else if(newItem.getItemType() == EtcItemType.LURE)
                {
                    // baits can be equipped only with rods
                    if(rHandItemTemplate == null)
                        return;
                    if(rHandItemTemplate.getItemType() != WeaponType.ROD)
                        return;
                }
                else
                {
                    // unequip two-hand weapon
                    if(rHandItemTemplate != null && rHandItemTemplate.getBodyPart() == ItemTemplate.SLOT_LR_HAND)
                        setPaperdollItem(PAPERDOLL_RHAND, null);
                }

                setPaperdollItem(PAPERDOLL_LHAND, item);
                break;
            }
            case ItemTemplate.SLOT_R_HAND:
            {
                ItemInstance lHandItem = getPaperdollItem(PAPERDOLL_LHAND);
                if(lHandItem != null)
                {
                    ItemTemplate lHandItemTemplate = lHandItem.getTemplate();
                    ItemTemplate newItem = item.getTemplate();

                    if(lHandItemTemplate.getItemType() == EtcItemType.ARROW || lHandItemTemplate.getItemType() == EtcItemType.ARROW_QUIVER)
                    {
                        if(newItem.getItemType() != WeaponType.BOW || newItem.getGrade().extOrdinal() != newItem.getGrade().extOrdinal())
                            setPaperdollItem(PAPERDOLL_LHAND, null);
                    }
                    else if(lHandItemTemplate.getItemType() == EtcItemType.BOLT || lHandItemTemplate.getItemType() == EtcItemType.BOLT_QUIVER)
                    {
                        if((newItem.getItemType() != WeaponType.CROSSBOW && newItem.getItemType() != WeaponType.TWOHANDCROSSBOW) || newItem.getGrade().extOrdinal() != newItem.getGrade().extOrdinal())
                            setPaperdollItem(PAPERDOLL_LHAND, null);
                    }
                    else if(lHandItemTemplate.getItemType() == EtcItemType.LURE)
                    {
                        if(newItem.getItemType() != WeaponType.ROD)
                            setPaperdollItem(PAPERDOLL_LHAND, null);
                    }
                }
                setPaperdollItem(PAPERDOLL_RHAND, item);
                break;
            }
            case ItemTemplate.SLOT_L_EAR:
            case ItemTemplate.SLOT_R_EAR:
            case ItemTemplate.SLOT_L_EAR | ItemTemplate.SLOT_R_EAR:
            {
                if(_paperdoll[PAPERDOLL_LEAR] == null)
                    setPaperdollItem(PAPERDOLL_LEAR, item);
                else if(_paperdoll[PAPERDOLL_REAR] == null)
                    setPaperdollItem(PAPERDOLL_REAR, item);
                else
                {
                    double lEarMDef = 0.;
                    final FuncTemplate[] lEarFuncTemplates = _paperdoll[PAPERDOLL_LEAR].getTemplate().getAttachedFuncs();
                    for(FuncTemplate func : lEarFuncTemplates)
                    {
                        if(func._stat == Stats.MAGIC_DEFENCE)
                        {
                            lEarMDef = func._value;
                            break;
                        }
                    }

                    double rEarMDef = 0.;
                    final FuncTemplate[] rEarFuncTemplates = _paperdoll[PAPERDOLL_REAR].getTemplate().getAttachedFuncs();
                    for(FuncTemplate func : rEarFuncTemplates)
                    {
                        if(func._stat == Stats.MAGIC_DEFENCE)
                        {
                            rEarMDef = func._value;
                            break;
                        }
                    }

                    if(lEarMDef > rEarMDef)
                        setPaperdollItem(PAPERDOLL_REAR, item);
                    else
                        setPaperdollItem(PAPERDOLL_LEAR, item);
                }
                break;
            }
            case ItemTemplate.SLOT_L_FINGER:
            case ItemTemplate.SLOT_R_FINGER:
            case ItemTemplate.SLOT_L_FINGER | ItemTemplate.SLOT_R_FINGER:
            {
                if(_paperdoll[PAPERDOLL_LFINGER] == null)
                    setPaperdollItem(PAPERDOLL_LFINGER, item);
                else if(_paperdoll[PAPERDOLL_RFINGER] == null)
                    setPaperdollItem(PAPERDOLL_RFINGER, item);
                else
                {
                    double lFingerMDef = 0.;
                    final FuncTemplate[] lFingerFuncTemplates = _paperdoll[PAPERDOLL_LFINGER].getTemplate().getAttachedFuncs();
                    for(FuncTemplate func : lFingerFuncTemplates)
                    {
                        if(func._stat == Stats.MAGIC_DEFENCE)
                        {
                            lFingerMDef = func._value;
                            break;
                        }
                    }

                    double rFingerMDef = 0.;
                    final FuncTemplate[] rFingerFuncTemplates = _paperdoll[PAPERDOLL_RFINGER].getTemplate().getAttachedFuncs();
                    for(FuncTemplate func : rFingerFuncTemplates)
                    {
                        if(func._stat == Stats.MAGIC_DEFENCE)
                        {
                            rFingerMDef = func._value;
                            break;
                        }
                    }

                    if(lFingerMDef > rFingerMDef)
                        setPaperdollItem(PAPERDOLL_RFINGER, item);
                    else
                        setPaperdollItem(PAPERDOLL_LFINGER, item);
                }
                break;
            }
            case ItemTemplate.SLOT_NECK:
                setPaperdollItem(PAPERDOLL_NECK, item);
                break;
            case ItemTemplate.SLOT_FULL_ARMOR:
                setPaperdollItem(PAPERDOLL_LEGS, null);
                setPaperdollItem(PAPERDOLL_CHEST, item);
                break;
            case ItemTemplate.SLOT_CHEST:
                setPaperdollItem(PAPERDOLL_CHEST, item);
                break;
            case ItemTemplate.SLOT_LEGS:
            {
                // handle full armor
                ItemInstance chest = getPaperdollItem(PAPERDOLL_CHEST);
                if(chest != null && chest.getBodyPart() == ItemTemplate.SLOT_FULL_ARMOR)
                    setPaperdollItem(PAPERDOLL_CHEST, null);
                else if(getPaperdollItemId(PAPERDOLL_CHEST) == ItemTemplate.ITEM_ID_FORMAL_WEAR)
                    setPaperdollItem(PAPERDOLL_CHEST, null);

                setPaperdollItem(PAPERDOLL_LEGS, item);
                break;
            }
            case ItemTemplate.SLOT_FEET:
                if(getPaperdollItemId(PAPERDOLL_CHEST) == ItemTemplate.ITEM_ID_FORMAL_WEAR)
                    setPaperdollItem(PAPERDOLL_CHEST, null);
                setPaperdollItem(PAPERDOLL_FEET, item);
                break;
            case ItemTemplate.SLOT_GLOVES:
                if(getPaperdollItemId(PAPERDOLL_CHEST) == ItemTemplate.ITEM_ID_FORMAL_WEAR)
                    setPaperdollItem(PAPERDOLL_CHEST, null);
                setPaperdollItem(PAPERDOLL_GLOVES, item);
                break;
            case ItemTemplate.SLOT_HEAD:
                if(getPaperdollItemId(PAPERDOLL_CHEST) == ItemTemplate.ITEM_ID_FORMAL_WEAR)
                    setPaperdollItem(PAPERDOLL_CHEST, null);
                setPaperdollItem(PAPERDOLL_HEAD, item);
                break;
            case ItemTemplate.SLOT_HAIR:
                ItemInstance old = getPaperdollItem(PAPERDOLL_DHAIR);
                if(old != null && old.getBodyPart() == ItemTemplate.SLOT_HAIRALL)
                    setPaperdollItem(PAPERDOLL_DHAIR, null);
                setPaperdollItem(PAPERDOLL_HAIR, item);
                break;
            case ItemTemplate.SLOT_DHAIR:
                ItemInstance slot2 = getPaperdollItem(PAPERDOLL_DHAIR);
                if(slot2 != null && slot2.getBodyPart() == ItemTemplate.SLOT_HAIRALL)
                    setPaperdollItem(PAPERDOLL_HAIR, null);
                setPaperdollItem(PAPERDOLL_DHAIR, item);
                break;
            case ItemTemplate.SLOT_HAIRALL:
                setPaperdollItem(PAPERDOLL_HAIR, null);
                setPaperdollItem(PAPERDOLL_DHAIR, item);
                break;
            case ItemTemplate.SLOT_R_BRACELET:
                setPaperdollItem(PAPERDOLL_RBRACELET, item);
                break;
            case ItemTemplate.SLOT_L_BRACELET:
                setPaperdollItem(PAPERDOLL_LBRACELET, item);
                break;
            case ItemTemplate.SLOT_PENDANT:
                setPaperdollItem(PAPERDOLL_PENDANT, item);
                break;
            case ItemTemplate.SLOT_BACK:
                setPaperdollItem(PAPERDOLL_BACK, item);
                break;
            case ItemTemplate.SLOT_BELT:
                setPaperdollItem(PAPERDOLL_BELT, item);
                break;
            case ItemTemplate.SLOT_DECO:
                if(_paperdoll[PAPERDOLL_DECO1] == null)
                    setPaperdollItem(PAPERDOLL_DECO1, item);
                else if(_paperdoll[PAPERDOLL_DECO2] == null)
                    setPaperdollItem(PAPERDOLL_DECO2, item);
                else if(_paperdoll[PAPERDOLL_DECO3] == null)
                    setPaperdollItem(PAPERDOLL_DECO3, item);
                else if(_paperdoll[PAPERDOLL_DECO4] == null)
                    setPaperdollItem(PAPERDOLL_DECO4, item);
                else if(_paperdoll[PAPERDOLL_DECO5] == null)
                    setPaperdollItem(PAPERDOLL_DECO5, item);
                else if(_paperdoll[PAPERDOLL_DECO6] == null)
                    setPaperdollItem(PAPERDOLL_DECO6, item);
                else
                    setPaperdollItem(PAPERDOLL_DECO1, item);
                break;
            case ItemTemplate.SLOT_FORMAL_WEAR:
                // При одевании свадебного платья руки не трогаем
                setPaperdollItem(PAPERDOLL_LEGS, null);
                setPaperdollItem(PAPERDOLL_HEAD, null);
                setPaperdollItem(PAPERDOLL_FEET, null);
                setPaperdollItem(PAPERDOLL_GLOVES, null);
                setPaperdollItem(PAPERDOLL_CHEST, item);
                break;
            case ItemTemplate.SLOT_BROOCH:
                setPaperdollItem(PAPERDOLL_BROOCH, item);
                break;
            case ItemTemplate.SLOT_JEWEL:
                if(_paperdoll[PAPERDOLL_JEWEL1] == null)
                    setPaperdollItem(PAPERDOLL_JEWEL1, item);
                else if(_paperdoll[PAPERDOLL_JEWEL2] == null)
                    setPaperdollItem(PAPERDOLL_JEWEL2, item);
                else if(_paperdoll[PAPERDOLL_JEWEL3] == null)
                    setPaperdollItem(PAPERDOLL_JEWEL3, item);
                else if(_paperdoll[PAPERDOLL_JEWEL4] == null)
                    setPaperdollItem(PAPERDOLL_JEWEL4, item);
                else if(_paperdoll[PAPERDOLL_JEWEL5] == null)
                    setPaperdollItem(PAPERDOLL_JEWEL5, item);
                else if(_paperdoll[PAPERDOLL_JEWEL6] == null)
                    setPaperdollItem(PAPERDOLL_JEWEL6, item);
                else
                    setPaperdollItem(PAPERDOLL_JEWEL1, item);
                break;
            default:
                _log.warn("unknown body slot:" + bodySlot + " for item id: " + item.getItemId());
                return;
        }

        //TODO [G1ta0] затычка на статы повышающие HP/MP/CP
        getActor().setCurrentHp(hp, false);
        getActor().setCurrentMp(mp);
        getActor().setCurrentCp(cp);

        if(getActor().isPlayer())
            ((Player) getActor()).autoShot();
    }

    public abstract void sendAddItem(ItemInstance item);

    public abstract void sendModifyItem(ItemInstance... items);

    public abstract void sendRemoveItem(ItemInstance item);

    public void sendEquipInfo(int slot)
    {
        //
    }

    /**
     * Refresh the weight of equipment loaded
     */
    protected void refreshWeight()
    {
        int weight = 0;

        readLock();
        try
        {
            ItemInstance item;
            for(int i = 0; i < _items.size(); i++)
            {
                item = _items.get(i);
                weight += item.getTemplate().getWeight() * item.getCount();
            }
        }
        finally
        {
            readUnlock();
        }

        if(_totalWeight == weight)
            return;

        _totalWeight = weight;

        onRefreshWeight();
    }

    protected abstract void onRefreshWeight();

    public int getTotalWeight()
    {
        return _totalWeight;
    }

    public boolean validateCapacity(ItemInstance item)
    {
        long slots = 0;
        if(!item.isStackable() || getItemByItemId(item.getItemId()) == null)
            slots++;
        return validateCapacity(slots);
    }

    public boolean validateCapacity(int itemId, long count)
    {
        ItemTemplate item = ItemHolder.getInstance().getTemplate(itemId);
        return validateCapacity(item, count);
    }

    public boolean validateCapacity(ItemTemplate item, long count)
    {
        long slots = 0;
        if(!item.isStackable() || getItemByItemId(item.getItemId()) == null)
            slots = count;
        return validateCapacity(slots);
    }

    public boolean validateCapacity(long slots)
    {
        if(slots == 0)
            return true;
        if(slots < Integer.MIN_VALUE || slots > Integer.MAX_VALUE)
            return false;
        if(getSize() + (int) slots < 0)
            return false;
        return getSize() + slots <= getActor().getInventoryLimit();
    }

    public boolean validateWeight(ItemInstance item)
    {
        long weight = item.getTemplate().getWeight() * item.getCount();
        return validateWeight(weight);
    }

    public boolean validateWeight(int itemId, long count)
    {
        ItemTemplate item = ItemHolder.getInstance().getTemplate(itemId);
        return validateWeight(item, count);
    }

    public boolean validateWeight(ItemTemplate item, long count)
    {
        long weight = item.getWeight() * count;
        return validateWeight(weight);
    }

    public boolean validateWeight(long weight)
    {
        if(weight == 0L)
            return true;
        if(weight < Integer.MIN_VALUE || weight > Integer.MAX_VALUE)
            return false;
        if(getTotalWeight() + (int) weight < 0)
            return false;
        return getTotalWeight() + weight <= getActor().getMaxLoad();
    }

    public abstract void restore();

    public abstract void store();

    public static int getPaperdollIndex(int slot)
    {
        switch(slot)
        {
            case ItemTemplate.SLOT_PENDANT:
                return PAPERDOLL_PENDANT;
            case ItemTemplate.SLOT_R_EAR:
                return PAPERDOLL_REAR;
            case ItemTemplate.SLOT_L_EAR:
                return PAPERDOLL_LEAR;
            case ItemTemplate.SLOT_NECK:
                return PAPERDOLL_NECK;
            case ItemTemplate.SLOT_R_FINGER:
                return PAPERDOLL_RFINGER;
            case ItemTemplate.SLOT_L_FINGER:
                return PAPERDOLL_LFINGER;
            case ItemTemplate.SLOT_HEAD:
                return PAPERDOLL_HEAD;
            case ItemTemplate.SLOT_R_HAND:
                return PAPERDOLL_RHAND;
            case ItemTemplate.SLOT_L_HAND:
                return PAPERDOLL_LHAND;
            case ItemTemplate.SLOT_LR_HAND:
                return PAPERDOLL_LRHAND;
            case ItemTemplate.SLOT_GLOVES:
                return PAPERDOLL_GLOVES;
            case ItemTemplate.SLOT_CHEST:
            case ItemTemplate.SLOT_FULL_ARMOR:
            case ItemTemplate.SLOT_FORMAL_WEAR:
                return PAPERDOLL_CHEST;
            case ItemTemplate.SLOT_LEGS:
                return PAPERDOLL_LEGS;
            case ItemTemplate.SLOT_FEET:
                return PAPERDOLL_FEET;
            case ItemTemplate.SLOT_BACK:
                return PAPERDOLL_BACK;
            case ItemTemplate.SLOT_HAIR:
            case ItemTemplate.SLOT_HAIRALL:
                return PAPERDOLL_HAIR;
            case ItemTemplate.SLOT_DHAIR:
                return PAPERDOLL_DHAIR;
            case ItemTemplate.SLOT_R_BRACELET:
                return PAPERDOLL_RBRACELET;
            case ItemTemplate.SLOT_L_BRACELET:
                return PAPERDOLL_LBRACELET;
            case ItemTemplate.SLOT_DECO:
                return PAPERDOLL_DECO1; //return first we deal with it later
            case ItemTemplate.SLOT_BELT:
                return PAPERDOLL_BELT;
            case ItemTemplate.SLOT_BROOCH:
                return PAPERDOLL_BROOCH;
            case ItemTemplate.SLOT_JEWEL:
                return PAPERDOLL_JEWEL1; //return first we deal with it later
        }
        return -1;
    }

    @Override
    public int getSize()
    {
        return super.getSize() - getQuestSize();
    }

    public int getAllSize()
    {
        return super.getSize();
    }

    public int getQuestSize()
    {
        int size = 0;
        for(ItemInstance item : getItems())
            if(item.getTemplate().isQuest())
                size++;
        return size;
    }
}