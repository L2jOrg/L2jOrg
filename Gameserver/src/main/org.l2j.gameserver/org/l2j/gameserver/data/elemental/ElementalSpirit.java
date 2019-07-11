package org.l2j.gameserver.data.elemental;

import org.l2j.gameserver.data.database.dao.ElementalSpiritDAO;
import org.l2j.gameserver.data.database.data.ElementalSpiritData;
import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.OnElementalSpiritUpgrade;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.UserInfo;
import org.l2j.gameserver.network.serverpackets.elementalspirits.ElementalSpiritInfo;
import org.l2j.gameserver.network.serverpackets.elementalspirits.ExElementalSpiritGetExp;

import java.util.List;

import static java.lang.Math.max;
import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.gameserver.network.SystemMessageId.OBTAINED_S2_ATTRIBUTE_XP_OF_S1;
import static org.l2j.gameserver.network.SystemMessageId.S1_ATTRIBUTE_SPIRIT_BECAME_LEVEL_S2;

public class ElementalSpirit {

    private final Player owner;
    private ElementalSpiritTemplate template;
    private ElementalSpiritData data;

    public ElementalSpirit(ElementalType type, Player owner) {
        data = new ElementalSpiritData(type.getId(), owner.getObjectId());
        this.template = ElementalSpiritManager.getInstance().getSpirit(type.getId(), data.getStage());
        this.owner = owner;
    }

    public ElementalSpirit(ElementalSpiritData data, Player owner) {
        this.owner = owner;
        this.data = data;
        this.template = ElementalSpiritManager.getInstance().getSpirit(data.getType(), data.getStage());
    }

    public void addExperience(long experience) {
        data.addExperience(experience);
        owner.sendPacket(SystemMessage.getSystemMessage(OBTAINED_S2_ATTRIBUTE_XP_OF_S1).addInt((int) experience).addElementalSpirit(getType()));
        if(data.getExperience() > getExperienceToNextLevel()) {
            levelUp();
            owner.sendPacket(SystemMessage.getSystemMessage(S1_ATTRIBUTE_SPIRIT_BECAME_LEVEL_S2).addElementalSpirit(getType()).addByte(data.getLevel()));
            owner.sendPacket(new ElementalSpiritInfo(owner.getActiveElementalSpiritType(), (byte) 0));
            var userInfo = new UserInfo(owner);
            userInfo.addComponentType(UserInfoType.ATT_SPIRITS);
            owner.sendPacket(userInfo);

        }
        owner.sendPacket(new ExElementalSpiritGetExp(getType(), data.getExperience()));
    }

    private void levelUp() {
        do {
            if (data.getLevel() < getMaxLevel()) {
                data.increaseLevel();
            } else {
                data.setExperience(getExperienceToNextLevel());
            }
        } while (data.getExperience() > getExperienceToNextLevel());

    }

    public int getAvailableCharacteristicsPoints() {
        var stage = data.getStage();
        var level = data.getLevel();
        var points = ((stage -1) * 11) +  ( stage > 2 ? (level -1) * 2 : level -1);
        return max(points - data.getAttackPoints() - data.getDefensePoints() - data.getCritDamagePoints() - data.getCritRatePoints(), 0);
    }

    public AbsorbItem getAbsorbItem(int itemId) {
        for (AbsorbItem absorbItem : getAbsorbItems()) {
            if(absorbItem.getId() == itemId) {
                return absorbItem;
            }
        }
        return null;
    }

    public int getExtractAmount() {
        return Math.round(data.getExperience() / ElementalSpiritManager.FRAGMENT_XP_CONSUME);
    }

    public void resetStage() {
        data.setLevel((byte) 1);
        data.setExperience(0);
        resetCharacteristics();
    }

    public boolean canEvolve() {
        return getStage() < 3 && getLevel() == 10 && getExperience() == getExperienceToNextLevel();
    }

    public void upgrade() {
        data.increaseStage();
        data.setLevel((byte) 1);
        data.setExperience(0);
        template = ElementalSpiritManager.getInstance().getSpirit(data.getType(), data.getStage());
        EventDispatcher.getInstance().notifyEventAsync(new OnElementalSpiritUpgrade(owner, this), owner);

    }

    public void resetCharacteristics() {
        data.setAttackPoints((byte) 0);
        data.setDefensePoints((byte) 0);
        data.setCritRatePoints((byte) 0);
        data.setCritDamagePoints((byte) 0);
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

    public void addAttackPoints(byte attackPoints) {
        data.addAttackPoints(attackPoints);
    }

    public void addDefensePoints(byte defensePoints) {
        data.addDefensePoints(defensePoints);
    }

    public void addCritRatePoints(byte critRatePoints) {
        data.addCritRatePoints(critRatePoints);
    }

    public void addCritDamage(byte critDamagePoints) {
        data.addCritDamagePoints(critDamagePoints);
    }

    public int getCriticalRate() {
        return  template.getCriticalRateAtLevel(data.getLevel()) + getCriticalRatePoints();
    }

    public int getCriticalDamage() {
        return template.getCriticalDamageAtLevel(data.getLevel()) + getCriticalRatePoints();
    }
}