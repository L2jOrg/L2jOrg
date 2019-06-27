package org.l2j.gameserver.data.elemental;

import org.l2j.gameserver.data.database.dao.ElementalSpiritDAO;
import org.l2j.gameserver.data.database.data.ElementalSpiritData;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.network.serverpackets.elementalspirits.ExElementalSpiritGetExp;

import java.util.List;

import static org.l2j.commons.database.DatabaseAccess.getDAO;

public class ElementalSpirit {

    private final L2PcInstance owner;
    private ElementalSpiritTemplate template;
    private ElementalSpiritData data;

    public ElementalSpirit(ElementalType type, L2PcInstance owner) {
        data = new ElementalSpiritData(type.getId(), owner.getObjectId());
        this.template = ElementalSpiritManager.getInstance().getSpirit(type.getId(), data.getStage());
        this.owner = owner;
    }

    public ElementalSpirit(ElementalSpiritData data, L2PcInstance owner) {
        this.owner = owner;
        this.data = data;
        this.template = ElementalSpiritManager.getInstance().getSpirit(data.getType(), data.getStage());
    }

    public void addExperience(long experience) {
        data.addExperience(experience);
        if(data.getExperience()> getExperienceToNextLevel()) {
            if(data.getLevel() < getMaxLevel()) {
                data.increaseLevel();
            } else {
                data.setExperience(getExperienceToNextLevel());
            }
        }
        owner.sendPacket(new ExElementalSpiritGetExp(getType(), data.getExperience()));
    }

    public int getAvailableCharacteristicsPoints() {
        var stage = data.getStage();
        var level = data.getLevel();
        var points = ((stage -1) * 10) +  stage > 2 ? (level -1) * 2 : level -1;
        return points - data.getAttackPoints() - data.getDefensePoints() - data.getDefensePoints() - data.getCritRatePoints();
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
        return data.getExperience();
    }

    public long getExperienceToNextLevel() {
        return template.getMaxExperienceAtLevel(data.getLevel());
    }

    public byte getLevel() {
        return data.getLevel();
    }

    public int getMaxLevel() {
        return template.getMaxLevel();
    }

    public int getAttack() {
        return template.getAttackAtLevel(data.getLevel()) + data.getAttackPoints() * 5;
    }

    public int getDefense() {
        return template.getDefenseAtLevel(data.getLevel()) + data.getDefensePoints() * 5;
    }

    public int getMaxCharacteristics() {
        return template.getMaxCharacteristics();
    }

    public int getAttackPoints() {
        return data.getAttackPoints();
    }

    public int getDefensePoints() {
        return data.getDefensePoints();
    }

    public int getCriticalRatePoints() {
        return data.getCritRatePoints();
    }

    public int getCriticalDamagePoints() {
        return data.getCritDamagePoints();
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

    public void save() {
        getDAO(ElementalSpiritDAO.class).save(data);
    }

}
