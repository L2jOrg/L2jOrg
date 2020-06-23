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
package org.l2j.gameserver.model.item;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.enums.AttributeType;
import org.l2j.gameserver.enums.ItemGrade;
import org.l2j.gameserver.enums.ItemSkillType;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.commission.CommissionItemType;
import org.l2j.gameserver.model.conditions.Condition;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.holders.ItemSkillHolder;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.interfaces.IIdentifiable;
import org.l2j.gameserver.model.item.enchant.attribute.AttributeHolder;
import org.l2j.gameserver.model.item.type.ActionType;
import org.l2j.gameserver.model.item.type.CrystalType;
import org.l2j.gameserver.model.item.type.EtcItemType;
import org.l2j.gameserver.model.item.type.ItemType;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.model.stats.functions.FuncAdd;
import org.l2j.gameserver.model.stats.functions.FuncSet;
import org.l2j.gameserver.model.stats.functions.FuncTemplate;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNullElse;
import static org.l2j.gameserver.util.GameUtils.*;

/**
 * This class contains all informations concerning the item (weapon, armor, etc).<BR>
 * Mother class of :
 * <ul>
 * <li>Armor</li>
 * <li>EtcItem</li>
 * <li>Weapon</li>
 * </ul>
 */
public abstract class ItemTemplate extends ListenersContainer implements IIdentifiable {
    public static final int TYPE1_WEAPON_RING_EARRING_NECKLACE = 0;
    public static final int TYPE1_SHIELD_ARMOR = 1;
    public static final int TYPE1_ITEM_QUESTITEM_ADENA = 4;
    public static final int TYPE2_WEAPON = 0;
    public static final int TYPE2_SHIELD_ARMOR = 1;
    public static final int TYPE2_ACCESSORY = 2;
    public static final int TYPE2_QUEST = 3;
    public static final int TYPE2_MONEY = 4;
    public static final int TYPE2_OTHER = 5;

    protected static final Logger LOGGER = LoggerFactory.getLogger(ItemTemplate.class);
    protected int type1; // needed for item list (inventory)
    protected int type2; // different lists for armor, weapon, etc
    protected List<FuncTemplate> _funcTemplates;
    protected List<Condition> _preConditions;
    private int id;
    private int displayId;
    private String name;
    private String icon;
    private int weight;
    private boolean stackable;
    protected CrystalType crystalType;
    protected int equipReuseDelay;
    private long time;
    private int _autoDestroyTime = -1;
    private long price;
    protected int crystalCount;
    private boolean sellable;
    private boolean dropable;
    private boolean destroyable;
    private boolean tradable;
    private boolean depositable;
    protected boolean enchantable;
    protected boolean questItem;
    private boolean freightable;
    private boolean olympiadRestricted;
    private boolean cocRestricted;
    private boolean forNpc;
    private boolean _common;
    private boolean _heroItem;
    private boolean _pvpItem;
    protected boolean immediateEffect;
    protected boolean exImmediateEffect;
    protected ActionType _defaultAction = ActionType.NONE;
    private Map<AttributeType, AttributeHolder> _elementals = null;
    private List<ItemSkillHolder> skills;

    private int reuseDelay;
    private int reuseGroup;

    private CommissionItemType commissionType;

    protected BodyPart bodyPart; // TODO should be on Weapon and Armor


    public ItemTemplate(int id, String name) {
        this.id = id;
        this.name = name;

        _common = ((id >= 11605) && (id <= 12361));
        _heroItem = ((id >= 6611) && (id <= 6621)) || ((id >= 9388) && (id <= 9390)) || (id == 6842);
        _pvpItem = ((id >= 10667) && (id <= 10835)) || ((id >= 12852) && (id <= 12977)) || ((id >= 14363) && (id <= 14525)) || (id == 14528) || (id == 14529) || (id == 14558) || ((id >= 15913) && (id <= 16024)) || ((id >= 16134) && (id <= 16147)) || (id == 16149) || (id == 16151) || (id == 16153) || (id == 16155) || (id == 16157) || (id == 16159) || ((id >= 16168) && (id <= 16176)) || ((id >= 16179) && (id <= 16220));
    }

    /**
     * Returns the itemType.
     *
     * @return Enum
     */
    public abstract ItemType getItemType();

    /**
     * Verifies if the item is a magic weapon.
     *
     * @return {@code true} if the weapon is magic, {@code false} otherwise
     */
    public boolean isMagicWeapon() {
        return false;
    }

    /**
     * @return the _equipReuseDelay
     */
    public int getEquipReuseDelay() {
        return equipReuseDelay;
    }


    /**
     * Returns the time of the item
     *
     * @return long
     */
    public final long getTime() {
        return time;
    }

    /**
     * @return the auto destroy time of the item in seconds: 0 or less - default
     */
    public final int getAutoDestroyTime() {
        return _autoDestroyTime;
    }

    /**
     * Returns the ID of the item
     *
     * @return int
     */
    @Override
    public final int getId() {
        return id;
    }

    /**
     * Returns the ID of the item
     *
     * @return int
     */
    public final int getDisplayId() {
        return displayId;
    }

    public abstract int getItemMask();


    /**
     * Returns the type 2 of the item
     *
     * @return int
     */
    public final int getType2() {
        return type2;
    }

    /**
     * Returns the weight of the item
     *
     * @return int
     */
    public final int getWeight() {
        return weight;
    }

    /**
     * Returns if the item is crystallizable
     *
     * @return boolean
     */
    public final boolean isCrystallizable() {
        return (crystalType != CrystalType.NONE) && (crystalCount > 0);
    }

    /**
     * @return return General item grade (No S80, S84, R95, R99)
     */
    public ItemGrade getItemGrade() {
        return ItemGrade.valueOf(crystalType);
    }

    /**
     * Return the type of crystal if item is crystallizable
     *
     * @return CrystalType
     */
    public final CrystalType getCrystalType() {
        return crystalType;
    }

    /**
     * Return the ID of crystal if item is crystallizable
     *
     * @return int
     */
    public final int getCrystalItemId() {
        return crystalType.getCrystalId();
    }

    /**
     * @return the quantity of crystals for crystallization.
     */
    public final int getCrystalCount() {
        return crystalCount;
    }

    /**
     * @param enchantLevel
     * @return the quantity of crystals for crystallization on specific enchant level
     */
    public final int getCrystalCount(int enchantLevel) {
        if (enchantLevel > 3) {
            switch (type2) {
                case TYPE2_SHIELD_ARMOR:
                case TYPE2_ACCESSORY: {
                    return crystalCount + (crystalType.getCrystalEnchantBonusArmor() * ((3 * enchantLevel) - 6));
                }
                case TYPE2_WEAPON: {
                    return crystalCount + (crystalType.getCrystalEnchantBonusWeapon() * ((2 * enchantLevel) - 3));
                }
                default: {
                    return crystalCount;
                }
            }
        } else if (enchantLevel > 0) {
            switch (type2) {
                case TYPE2_SHIELD_ARMOR:
                case TYPE2_ACCESSORY: {
                    return crystalCount + (crystalType.getCrystalEnchantBonusArmor() * enchantLevel);
                }
                case TYPE2_WEAPON: {
                    return crystalCount + (crystalType.getCrystalEnchantBonusWeapon() * enchantLevel);
                }
                default: {
                    return crystalCount;
                }
            }
        } else {
            return crystalCount;
        }
    }

    /**
     * @return the name of the item.
     */
    public final String getName() {
        return name;
    }


    public Collection<AttributeHolder> getAttributes() {
        return _elementals != null ? _elementals.values() : null;
    }

    /**
     * Sets the base elemental of the item.
     *
     * @param holder the element to set.
     */
    public void setAttributes(AttributeHolder holder) {
        if (_elementals == null) {
            _elementals = new LinkedHashMap<>(3);
            _elementals.put(holder.getType(), holder);
        } else {
            final AttributeHolder attribute = getAttribute(holder.getType());
            if (attribute != null) {
                attribute.setValue(holder.getValue());
            } else {
                _elementals.put(holder.getType(), holder);
            }
        }
    }

    public AttributeHolder getAttribute(AttributeType type) {
        return _elementals != null ? _elementals.get(type) : null;
    }

    /**
     * @return the part of the body used with the item.
     */
    public final BodyPart getBodyPart() {
        return requireNonNullElse(bodyPart, BodyPart.NONE);
    }

    /**
     * @return the type 1 of the item.
     */
    public final int getType1() {
        return type1;
    }

    /**
     * @return {@code true} if the item is stackable, {@code false} otherwise.
     */
    public final boolean isStackable() {
        return stackable;
    }

    /**
     * @return {@code true} if the item can be equipped, {@code false} otherwise.
     */
    public boolean isEquipable() {
        return (bodyPart != BodyPart.NONE) && !(getItemType() instanceof EtcItemType);
    }

    /**
     * @return the price of reference of the item.
     */
    public final long getReferencePrice() {
        return price;
    }

    /**
     * @return {@code true} if the item can be sold, {@code false} otherwise.
     */
    public final boolean isSellable() {
        return sellable;
    }

    /**
     * @return {@code true} if the item can be dropped, {@code false} otherwise.
     */
    public final boolean isDropable() {
        return dropable;
    }

    /**
     * @return {@code true} if the item can be destroyed, {@code false} otherwise.
     */
    public final boolean isDestroyable() {
        return destroyable;
    }

    /**
     * @return {@code true} if the item can be traded, {@code false} otherwise.
     */
    public final boolean isTradeable() {
        return tradable;
    }

    /**
     * @return {@code true} if the item can be put into warehouse, {@code false} otherwise.
     */
    public final boolean isDepositable() {
        return depositable;
    }

    public void setDepositable(boolean depositable) {
        this.depositable = depositable;
    }

    /**
     * This method also check the enchant blacklist.
     *
     * @return {@code true} if the item can be enchanted, {@code false} otherwise.
     */
    public final boolean isEnchantable() {
        return Arrays.binarySearch(Config.ENCHANT_BLACKLIST, id) < 0 && enchantable;
    }

    /**
     * Returns if item is common
     *
     * @return boolean
     */
    public final boolean isCommon() {
        return _common;
    }

    /**
     * Returns if item is hero-only
     *
     * @return
     */
    public final boolean isHeroItem() {
        return _heroItem;
    }

    /**
     * Returns if item is pvp
     *
     * @return
     */
    public final boolean isPvpItem() {
        return _pvpItem;
    }

    public boolean isPotion() {
        return getItemType() == EtcItemType.POTION;
    }

    public boolean isElixir() {
        return getItemType() == EtcItemType.ELIXIR;
    }

    public boolean isScroll() {
        return getItemType() == EtcItemType.SCROLL;
    }

    public List<FuncTemplate> getFunctionTemplates() {
        return _funcTemplates != null ? _funcTemplates : Collections.emptyList();
    }

    /**
     * Add the FuncTemplate f to the list of functions used with the item
     *
     * @param template : FuncTemplate to add
     */
    public void addFunctionTemplate(FuncTemplate template) {
        switch (template.getStat()) {
            case FIRE_RES:
            case FIRE_POWER: {
                setAttributes(new AttributeHolder(AttributeType.FIRE, (int) template.getValue()));
                break;
            }
            case WATER_RES:
            case WATER_POWER: {
                setAttributes(new AttributeHolder(AttributeType.WATER, (int) template.getValue()));
                break;
            }
            case WIND_RES:
            case WIND_POWER: {
                setAttributes(new AttributeHolder(AttributeType.WIND, (int) template.getValue()));
                break;
            }
            case EARTH_RES:
            case EARTH_POWER: {
                setAttributes(new AttributeHolder(AttributeType.EARTH, (int) template.getValue()));
                break;
            }
            case HOLY_RES:
            case HOLY_POWER: {
                setAttributes(new AttributeHolder(AttributeType.HOLY, (int) template.getValue()));
                break;
            }
            case DARK_RES:
            case DARK_POWER: {
                setAttributes(new AttributeHolder(AttributeType.DARK, (int) template.getValue()));
                break;
            }
        }

        if (_funcTemplates == null) {
            _funcTemplates = new ArrayList<>();
        }
        _funcTemplates.add(template);
    }

    public final void attachCondition(Condition c) {
        if (_preConditions == null) {
            _preConditions = new ArrayList<>();
        }
        _preConditions.add(c);
    }

    public List<Condition> getConditions() {
        return _preConditions;
    }

    /**
     * Method to retrieve skills linked to this item armor and weapon: passive skills etcitem: skills used on item use <-- ???
     *
     * @return Skills linked to this item as SkillHolder[]
     */
    public final List<ItemSkillHolder> getAllSkills() {
        return skills;
    }

    /**
     * @param condition
     * @return {@code List} of {@link ItemSkillHolder} if item has skills and matches the condition, {@code null} otherwise
     */
    public final List<ItemSkillHolder> getSkills(Predicate<ItemSkillHolder> condition) {
        return skills != null ? skills.stream().filter(condition).collect(Collectors.toList()) : null;
    }

    /**
     * @param type
     * @return {@code List} of {@link ItemSkillHolder} if item has skills, {@code null} otherwise
     */
    public final List<ItemSkillHolder> getSkills(ItemSkillType type) {
        return nonNull(skills) ? skills.stream().filter(sk -> sk.getType() == type).collect(Collectors.toList()) : Collections.emptyList();
    }

    public final void forEachSkill(ItemSkillType type, Consumer<ItemSkillHolder> action) {
        if (nonNull(skills)) {
            skills.stream().filter(sk -> sk.getType() == type).forEach(action);
        }
    }

    public final void forEachSkill(ItemSkillType type, Predicate<Skill> filter, Consumer<Skill> action) {
        if(nonNull(skills)) {
            skills.stream().filter(sk -> sk.getType() == type).map(SkillHolder::getSkill).filter(filter).forEach(action);
        }
    }

    public boolean checkAnySkill(ItemSkillType type, Predicate<ItemSkillHolder> predicate) {
        if(nonNull(skills)) {
            return skills.stream().filter(sk -> sk.getType() == type).anyMatch(predicate);
        }
        return false;
    }


    public void addSkill(ItemSkillHolder holder) {
        if (skills == null) {
            skills = new ArrayList<>();
        }
        skills.add(holder);
    }

    public boolean checkCondition(Creature activeChar, WorldObject object, boolean sendMessage) {
        if (activeChar.canOverrideCond(PcCondOverride.ITEM_CONDITIONS) && !Config.GM_ITEM_RESTRICTION) {
            return true;
        }

        // Don't allow hero equipment and restricted items during Olympiad
        if ((isOlyRestrictedItem() || _heroItem) && (isPlayer(activeChar) && activeChar.getActingPlayer().isInOlympiadMode())) {
            if (isEquipable()) {
                activeChar.sendPacket(SystemMessageId.YOU_CANNOT_EQUIP_THAT_ITEM_IN_A_OLYMPIAD_MATCH);
            } else {
                activeChar.sendPacket(SystemMessageId.YOU_CANNOT_USE_THAT_ITEM_IN_A_OLYMPIAD_MATCH);
            }
            return false;
        }

        if (!isConditionAttached()) {
            return true;
        }

        final Creature target = isCreature(object) ? (Creature) object : null;
        for (Condition preCondition : _preConditions) {
            if (preCondition == null) {
                continue;
            }

            if (!preCondition.test(activeChar, target, null, null)) {
                if (isSummon(activeChar)) {
                    activeChar.sendPacket(SystemMessageId.THIS_PET_CANNOT_USE_THIS_ITEM);
                    return false;
                }

                if (sendMessage) {
                    final String msg = preCondition.getMessage();
                    final int msgId = preCondition.getMessageId();
                    if (msg != null) {
                        activeChar.sendMessage(msg);
                    } else if (msgId != 0) {
                        final SystemMessage sm = SystemMessage.getSystemMessage(msgId);
                        if (preCondition.isAddName()) {
                            sm.addItemName(id);
                        }
                        activeChar.sendPacket(sm);
                    }
                }
                return false;
            }
        }
        return true;
    }

    public boolean isConditionAttached() {
        return (_preConditions != null) && !_preConditions.isEmpty();
    }

    public boolean isQuestItem() {
        return questItem;
    }

    public boolean isFreightable() {
        return freightable;
    }

    public boolean isOlyRestrictedItem() {
        return olympiadRestricted || Config.LIST_OLY_RESTRICTED_ITEMS.contains(id);
    }

    public boolean isForNpc() {
        return forNpc;
    }

    /**
     * Returns the name of the item followed by the item ID.
     *
     * @return the name and the ID of the item
     */
    @Override
    public String toString() {
        return name + "(" + id + ")";
    }

    /**
     * Verifies if the item has effects immediately.<br>
     * <i>Used for herbs mostly.</i>
     *
     * @return {@code true} if the item applies effects immediately, {@code false} otherwise
     */
    public boolean hasExImmediateEffect() {
        return exImmediateEffect;
    }

    /**
     * Verifies if the item has effects immediately.
     *
     * @return {@code true} if the item applies effects immediately, {@code false} otherwise
     */
    public boolean hasImmediateEffect() {
        return immediateEffect;
    }

    /**
     * @return the _default_action
     */
    public ActionType getDefaultAction() {
        return _defaultAction;
    }

    /**
     * Gets the item reuse delay time in seconds.
     *
     * @return the reuse delay time
     */
    public int getReuseDelay() {
        return reuseDelay;
    }

    /**
     * Gets the shared reuse group.<br>
     * Items with the same reuse group will render reuse delay upon those items when used.
     *
     * @return the shared reuse group
     */
    public int getSharedReuseGroup() {
        return reuseGroup;
    }

    public CommissionItemType getCommissionItemType() {
        return commissionType;
    }

    /**
     * Usable in HTML windows.
     *
     * @return the icon link in client files
     */
    public String getIcon() {
        return icon;
    }

    public boolean isPetItem() {
        return getItemType() == EtcItemType.PET_COLLAR;
    }

    public double getStats(Stat stat, double defaultValue) {
        if (_funcTemplates != null) {
            final FuncTemplate template = _funcTemplates.stream().filter(func -> (func.getStat() == stat) && ((func.getFunctionClass() == FuncAdd.class) || (func.getFunctionClass() == FuncSet.class))).findFirst().orElse(null);
            if (template != null) {
                return template.getValue();
            }
        }
        return defaultValue;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setDisplayId(int displayId) {
        this.displayId = displayId;
    }

    public void setFreightable(boolean freightable) {
        this.freightable = freightable;
    }

    public void setOlympiadRestricted(boolean olympiadRestricted) {
        this.olympiadRestricted = olympiadRestricted;
    }

    public void setStackable(boolean stackable) {
        this.stackable = stackable;
    }

    public void setDestroyable(boolean destroyable) {
        this.destroyable = destroyable;
    }

    public void setTradable(boolean tradable) {
        this.tradable = tradable;
    }

    public void setDropable(boolean dropable) {
        this.dropable = dropable;
    }

    public void setSellable(boolean sellable) {
        this.sellable = sellable;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public void setCommissionType(CommissionItemType commissionType) {
        this.commissionType = commissionType;
    }

    public void setReuseDelay(int reuseDelay) {
        this.reuseDelay = reuseDelay;
    }

    public void setReuseGroup(int reuseGroup) {
        this.reuseGroup = reuseGroup;
    }

    public void setDuration(long duration) {
        this.time = duration;
    }

    public void setForNpc(Boolean forNpc) {
        this.forNpc = forNpc;
    }

    public void setCrystalType(CrystalType type) {
        crystalType = type;
    }

    public void setCrystalCount(int count) {
        crystalCount = count;
    }
}
