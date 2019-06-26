package org.l2j.gameserver.data.elemental;

import org.l2j.gameserver.data.elemental.data.ElementalSpiritData;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.network.serverpackets.elementalspirits.ExElementalSpiritGetExp;

import java.util.List;

public class ElementalSpirit {

    private final L2PcInstance owner;
    private ElementalSpiritTemplate template;
    private ElementalSpiritData data;
    private long experience = 0;
    private byte level = 1;
    private byte stage = 0;
    private byte attackPoints = 0;
    private byte defensePoints = 0;
    private byte critRatePoints = 0;
    private byte critDamagePoints = 0;

    public ElementalSpirit(ElementalType type, L2PcInstance owner) {
        this.template = ElementalSpiritManager.getInstance().getSpirit(type.getId(), stage);
        data = new ElementalSpiritData(type.getId(), owner.getObjectId());
        this.owner = owner;
    }

    public void addExperience(long experience) {
        this.experience += experience;
        if(this.experience > getExperienceToNextLevel()) {
            if(level < getMaxLevel()) {
                level++;
            } else {
                this.experience = getExperienceToNextLevel();
            }
        }
        owner.sendPacket(new ExElementalSpiritGetExp(getType(), this.experience));
    }

    public int getAvailableCharacteristicsPoints() {
        var points = ((stage -1) * 10) +  stage > 2 ? (level -1) * 2 : level -1;
        return points - attackPoints - defensePoints - critDamagePoints - critRatePoints;
    }

    public AbsorbItem getAbsorbItem(int itemId) {
        for (AbsorbItem absorbItem : getAbsorbItems()) {
            if(absorbItem.getId() == itemId) {
                return absorbItem;
            }
        }
        return null;
    }

    public byte getType() {
        return template.getType();
    }

    public byte getStage() {
        return template.getStage();
    }

    public int getNpcId() {
        return template.getNpcId();
    }

    public long getExperience() {
        return experience;
    }

    public long getExperienceToNextLevel() {
        return template.getMaxExperienceAtLevel(level);
    }

    public byte getLevel() {
        return level;
    }

    public int getMaxLevel() {
        return template.getMaxLevel();
    }

    public int getAttack() {
        return template.getAttackAtLevel(level) + attackPoints * 5;
    }

    public int getDefense() {
        return template.getDefenseAtLevel(level) + defensePoints * 5;
    }

    public int getMaxCharacteristics() {
        return template.getMaxCharacteristics();
    }

    public int getAttackPoints() {
        return attackPoints;
    }

    public int getDefensePoints() {
        return defensePoints;
    }

    public int getCriticalRatePoints() {
        return critRatePoints;
    }

    public int getCriticalDamagePoints() {
        return critDamagePoints;
    }

    public List<ItemHolder> getItemsToEvolve() {
        return template.getItemsToEvolve();
    }


    public List<AbsorbItem> getAbsorbItems() {
        return template.getAbsorbItems();
    }

    public int getExtractItem() {
        return template.getExtractItem();
    }

}
