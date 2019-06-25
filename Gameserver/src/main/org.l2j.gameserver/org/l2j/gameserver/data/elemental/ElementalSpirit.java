package org.l2j.gameserver.data.elemental;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.network.serverpackets.elementalspirits.ExElementalSpiritGetExp;

import java.util.List;

public class ElementalSpirit {

    private final L2PcInstance owner;
    private ElementalSpiritTemplate template;
    private long experience = 0;
    private byte level = 1;
    private byte stage = 1;
    private byte attackPoints = 0;
    private byte defensePoints = 0;
    private byte critRatePoints = 0;
    private byte critDamagePoints = 0;

    public ElementalSpirit(ElementalType type, L2PcInstance owner) {
        this.template = ElementalSpiritManager.getInstance().getSpirit((byte) (type.ordinal() + 1), stage);
        this.owner = owner;
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

    public int getAvailableCharacteristicsPoints() {
        var points = ((stage -1) * 10) +  stage > 2 ? (level -1) * 2 : level -1;
        return points - attackPoints - defensePoints - critDamagePoints - critRatePoints;
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

    public void addExperience(long experience) {
        this.experience += experience;
        long capExperience =  0;
        if(this.experience > getExperienceToNextLevel()) {
            if(level < getMaxLevel()) {
                level++;
            } else {
                capExperience = this.experience - getExperienceToNextLevel();
                this.experience = getExperienceToNextLevel();
            }
        }
        owner.sendPacket(new ExElementalSpiritGetExp(getType(), experience - capExperience));
    }
}
