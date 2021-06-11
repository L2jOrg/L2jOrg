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

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.enums.ItemGrade;
import org.l2j.gameserver.enums.ItemSkillType;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.commission.CommissionItemType;
import org.l2j.gameserver.model.conditions.Condition;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.holders.ItemSkillHolder;
import org.l2j.gameserver.model.interfaces.IIdentifiable;
import org.l2j.gameserver.model.item.BodyPart;
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

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNullElse;
import static org.l2j.gameserver.util.GameUtils.isCreature;
import static org.l2j.gameserver.util.GameUtils.isSummon;

/**
 * @author JoeAlisson
 */
public abstract sealed class ItemTemplate extends ListenersContainer implements IIdentifiable permits Weapon, Armor, EtcItem {
    public static final int TYPE1_WEAPON_RING_EARRING_NECKLACE = 0;
    public static final int TYPE1_SHIELD_ARMOR = 1;
    public static final int TYPE1_ITEM_QUESTITEM_ADENA = 4;
    public static final int TYPE2_WEAPON = 0;
    public static final int TYPE2_SHIELD_ARMOR = 1;
    public static final int TYPE2_ACCESSORY = 2;
    public static final int TYPE2_QUEST = 3;
    public static final int TYPE2_MONEY = 4;
    public static final int TYPE2_OTHER = 5;

    private final int id;
    private final String name;

    protected int type1; // needed for item list (inventory)
    protected int type2; // different lists for armor, weapon, etc
    protected List<FuncTemplate> _funcTemplates;
    protected List<Condition> _preConditions;
    protected CrystalType crystalType;
    protected int equipReuseDelay;
    protected int crystalCount;
    protected boolean enchantable;
    protected boolean questItem;
    protected boolean immediateEffect;
    protected boolean exImmediateEffect;
    protected ActionType _defaultAction = ActionType.NONE;
    protected BodyPart bodyPart; // TODO should be on Weapon and Armor

    private Map<ItemSkillType, List<ItemSkillHolder>> skillsMap = Collections.emptyMap();
    private CommissionItemType commissionType;
    private int displayId;
    private int weight;
    private boolean stackable;
    private long duration;
    private long price;
    private boolean sellable;
    private boolean dropable;
    private boolean destroyable;
    private boolean tradable;
    private boolean depositable;
    private boolean freightable;
    private boolean olympiadRestricted;
    private boolean forNpc;
    private boolean heroItem;
    private int reuseDelay;
    private int reuseGroup;

    public ItemTemplate(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public void addSkill(ItemSkillHolder holder) {
        if(skillsMap.equals(Collections.emptyMap())) {
            skillsMap = new EnumMap<>(ItemSkillType.class);
        }
        skillsMap.computeIfAbsent(holder.getType(), t -> new ArrayList<>()).add(holder);
    }

    public final List<ItemSkillHolder> getSkills(ItemSkillType type) {
        return skillsMap.getOrDefault(type, Collections.emptyList());
    }

    public Skill getFirstSkill(ItemSkillType type) {
        var skills = skillsMap.getOrDefault(type, Collections.emptyList());
        if(!skills.isEmpty()) {
            return skills.get(0).getSkill();
        }
        return null;
    }


    /**
     * Returns the min skills according to comparator
     *
     * @param type the type of item skills
     * @param comparator the comparator of Item Skill Holder
     * @return the min skills according to comparator
     */
    public Skill getFirstSkill(ItemSkillType type, Comparator<ItemSkillHolder> comparator) {
        var skills = skillsMap.getOrDefault(type, Collections.emptyList());
        ItemSkillHolder holder = null;
        for (var skill : skills) {
            if (holder == null || comparator.compare(holder, skill) > 0) {
                holder = skill;
            }
        }
        return holder != null ?  holder.getSkill() : null;
    }

    public boolean hasSkill(ItemSkillType type) {
        return skillsMap.containsKey(type);
    }

    public boolean hasSkill(ItemSkillType type, int skillId) {
        var skills = skillsMap.getOrDefault(type, Collections.emptyList());
        for (var skill : skills) {
            if(skill.getSkillId() == skillId || skillId < 0) {
                return true;
            }
        }
        return false;
    }

    public boolean hasSkill(ItemSkillType type, Predicate<Skill> predicate) {
        var skills = skillsMap.getOrDefault(type, Collections.emptyList());
        for (var skill : skills) {
            if(predicate.test(skill.getSkill())) {
                return true;
            }
        }
        return false;
    }

    public final void forEachSkill(ItemSkillType type, Consumer<ItemSkillHolder> action) {
        skillsMap.getOrDefault(type, Collections.emptyList()).forEach(action);
    }

    public final void forEachSkill(ItemSkillType type, Predicate<Skill> filter, Consumer<Skill> action) {
        for (var holder : skillsMap.getOrDefault(type, Collections.emptyList())) {
            var skill = holder.getSkill();
            if(filter.test(skill)) {
                action.accept(skill);
            }
        }
    }

    public boolean checkAnySkill(ItemSkillType type, Predicate<ItemSkillHolder> predicate) {
        for (var holder : skillsMap.getOrDefault(type, Collections.emptyList())) {
            if(predicate.test(holder)) {
                return true;
            }
        }
        return false;
    }

    public final int getCrystalCount() {
        return getCrystalCount(0);
    }

    public final int getCrystalCount(int enchantLevel) {
        if (enchantLevel > 3) {
            return switch (type2) {
                case TYPE2_SHIELD_ARMOR, TYPE2_ACCESSORY -> calcArmorCrystalBonus(enchantLevel, 3, 6);
                case TYPE2_WEAPON -> calcWeaponCrystalBonus(enchantLevel, 2, 3);
                default -> crystalCount;
            };
        } else if (enchantLevel > 0) {
            return switch (type2) {
                case TYPE2_SHIELD_ARMOR, TYPE2_ACCESSORY -> calcArmorCrystalBonus(enchantLevel, 1, 0);
                case TYPE2_WEAPON -> calcWeaponCrystalBonus(enchantLevel, 1, 0);
                default -> crystalCount;
            };
        }
        return crystalCount;
    }

    private int calcWeaponCrystalBonus(int enchantLevel, int factor, int diff) {
        return crystalCount + (crystalType.getCrystalEnchantBonusWeapon() * ((factor * enchantLevel) - diff));
    }

    private int calcArmorCrystalBonus(int enchantLevel, int factor, int diff) {
        return crystalCount + (crystalType.getCrystalEnchantBonusArmor() * ((factor * enchantLevel) -diff));
    }

    public boolean checkCondition(Creature creature, WorldObject object, boolean sendMessage) {
        if (!checkItemRestriction(creature)) {
            return false;
        }

        if (!isConditionAttached()) {
            return true;
        }

        final Creature target = isCreature(object) ? (Creature) object : null;
        for (Condition preCondition : _preConditions) {
            if (failPrecondition(creature, sendMessage, target, preCondition)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkItemRestriction(Creature creature) {
        if ((isOlyRestrictedItem() || heroItem) &&  (creature instanceof Player player && player.isInOlympiadMode())) {
            if (isEquipable()) {
                creature.sendPacket(SystemMessageId.YOU_CANNOT_EQUIP_THAT_ITEM_IN_A_OLYMPIAD_MATCH);
            } else {
                creature.sendPacket(SystemMessageId.YOU_CANNOT_USE_THAT_ITEM_IN_A_OLYMPIAD_MATCH);
            }
            return false;
        }
        return true;
    }

    private boolean failPrecondition(Creature creature, boolean sendMessage, Creature target, Condition preCondition) {
        if (!preCondition.test(creature, target, null, null)) {
            if (isSummon(creature)) {
                creature.sendPacket(SystemMessageId.THIS_PET_CANNOT_USE_THIS_ITEM);
            } else if (sendMessage) {
                sendPreconditionMessage(creature, preCondition);
            }
            return true;
        }
        return false;
    }

    private void sendPreconditionMessage(Creature creature, Condition preCondition) {
        final String msg = preCondition.getMessage();
        final int msgId = preCondition.getMessageId();
        if (msg != null) {
            creature.sendMessage(msg);
        } else if (msgId != 0) {
            final SystemMessage sm = SystemMessage.getSystemMessage(msgId);
            if (preCondition.isAddName()) {
                sm.addItemName(id);
            }
            creature.sendPacket(sm);
        }
    }

    public double getStats(Stat stat, double defaultValue) {
        if (_funcTemplates != null) {
            for (var func : _funcTemplates) {
                if(func.getStat() == stat && ((func.getFunctionClass() == FuncAdd.class) || (func.getFunctionClass() == FuncSet.class))) {
                    return func.getValue();
                }
            }
        }
        return defaultValue;
    }

    @Override
    public final int getId() {
        return id;
    }

    public int getEquipReuseDelay() {
        return equipReuseDelay;
    }

    public final int getType2() {
        return type2;
    }

    public final boolean isCrystallizable() {
        return crystalType != CrystalType.NONE && crystalCount > 0;
    }

    public ItemGrade getItemGrade() {
        return ItemGrade.valueOf(crystalType);
    }

    public final int getCrystalItemId() {
        return crystalType.getCrystalId();
    }

    public final String getName() {
        return name;
    }

    public final BodyPart getBodyPart() {
        return requireNonNullElse(bodyPart, BodyPart.NONE);
    }

    public final int getType1() {
        return type1;
    }

    public boolean isEquipable() {
        return bodyPart != BodyPart.NONE && !(getItemType() instanceof EtcItemType);
    }

    public final boolean isEnchantable() {
        return enchantable;
    }

    public List<FuncTemplate> getFunctionTemplates() {
        return _funcTemplates != null ? _funcTemplates : Collections.emptyList();
    }

    void addFunctionTemplate(FuncTemplate template) {
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

    public boolean isConditionAttached() {
        return (_preConditions != null) && !_preConditions.isEmpty();
    }

    public boolean isQuestItem() {
        return questItem;
    }

    public boolean hasExImmediateEffect() {
        return exImmediateEffect;
    }

    public boolean hasImmediateEffect() {
        return immediateEffect;
    }

    public ActionType getDefaultAction() {
        return _defaultAction;
    }

    public boolean isPetItem() {
        return getItemType() == EtcItemType.PET_COLLAR;
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

    void setDepositable(boolean depositable) {
        this.depositable = depositable;
    }

    public final boolean isDepositable() {
        return depositable;
    }

    void setHeroItem(boolean hero) {
        this.heroItem = hero;
    }

    public final boolean isHeroItem() {
        return heroItem;
    }

    void setDisplayId(int displayId) {
        this.displayId = displayId;
    }

    public final int getDisplayId() {
        return displayId;
    }

    void setFreightable(boolean freightable) {
        this.freightable = freightable;
    }

    public boolean isFreightable() {
        return freightable;
    }

    void setOlympiadRestricted(boolean olympiadRestricted) {
        this.olympiadRestricted = olympiadRestricted;
    }

    public boolean isOlyRestrictedItem() {
        return olympiadRestricted;
    }

    void setStackable(boolean stackable) {
        this.stackable = stackable;
    }

    public final boolean isStackable() {
        return stackable;
    }

    void setDestroyable(boolean destroyable) {
        this.destroyable = destroyable;
    }

    public final boolean isDestroyable() {
        return destroyable;
    }

    void setTradable(boolean tradable) {
        this.tradable = tradable;
    }

    public final boolean isTradeable() {
        return tradable;
    }

    void setDropable(boolean dropable) {
        this.dropable = dropable;
    }

    public final boolean isDropable() {
        return dropable;
    }

    void setSellable(boolean sellable) {
        this.sellable = sellable;
    }

    public final boolean isSellable() {
        return sellable;
    }

    void setWeight(int weight) {
        this.weight = weight;
    }

    public final int getWeight() {
        return weight;
    }

    void setPrice(long price) {
        this.price = price;
    }

    public final long getReferencePrice() {
        return price;
    }

    void setCommissionType(CommissionItemType commissionType) {
        this.commissionType = commissionType;
    }

    public CommissionItemType getCommissionItemType() {
        return commissionType;
    }

    void setReuseDelay(int reuseDelay) {
        this.reuseDelay = reuseDelay;
    }

    public int getReuseDelay() {
        return reuseDelay;
    }

    void setReuseGroup(int reuseGroup) {
        this.reuseGroup = reuseGroup;
    }

    public int getReuseGroup() {
        return reuseGroup;
    }

    void setDuration(long duration) {
        this.duration = duration;
    }

    public final long getDuration() {
        return duration;
    }

    void setForNpc(boolean forNpc) {
        this.forNpc = forNpc;
    }

    public boolean isForNpc() {
        return forNpc;
    }

    void setCrystalType(CrystalType type) {
        crystalType = type;
    }

    public final CrystalType getCrystalType() {
        return crystalType;
    }

    void setCrystalCount(int count) {
        crystalCount = count;
    }

    @Override
    public String toString() {
        return name + "(" + id + ")";
    }

    public abstract ItemType getItemType();

    public abstract int getItemMask();
}
