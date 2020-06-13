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
package org.l2j.gameserver.api.elemental;

import org.l2j.gameserver.data.database.dao.ElementalSpiritDAO;
import org.l2j.gameserver.data.database.data.ElementalSpiritData;
import org.l2j.gameserver.engine.elemental.AbsorbItem;
import org.l2j.gameserver.engine.elemental.ElementalSpiritEngine;
import org.l2j.gameserver.engine.elemental.ElementalSpiritTemplate;
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
import static org.l2j.gameserver.network.SystemMessageId.S1_ATTACK_SPIRITS_HAVE_REACHED_LEVEL_S2;
import static org.l2j.gameserver.network.SystemMessageId.YOU_HAVE_ACQUIRED_S1S_S2_SKILL_XP;

/**
 * @author JoeAlisson
 */
public class ElementalSpirit {


    private static final int[] DEFENSE_MOD_POINTS = {15, 10, 5, 0};
    private static final int[] DEFENSE_MOD_VALUES = {9, 8, 7, 6};

    private static final int[] ATTACK_MOD_POINTS = {15, 2, 0};
    private static final int[] ATTACK_MOD_VALUES = {5, 4, 3};

    private static final int[] CRITIC_MOD_POINTS = {15, 0};
    private static final int[] CRITIC_MOD_VALUES = {2, 1};

    private final Player owner;
    private ElementalSpiritTemplate template;
    private ElementalSpiritData data;

    public ElementalSpirit(ElementalType type, Player owner) {
        data = new ElementalSpiritData(type.getId(), owner.getObjectId());
        this.template = ElementalSpiritEngine.getInstance().getSpirit(type.getId(), data.getStage());
        this.owner = owner;
    }

    public ElementalSpirit(ElementalSpiritData data, Player owner) {
        this.owner = owner;
        this.data = data;
        this.template = ElementalSpiritEngine.getInstance().getSpirit(data.getType(), data.getStage());
    }

    public void addExperience(long experience) {
        data.addExperience(experience);
        owner.sendPacket(SystemMessage.getSystemMessage(YOU_HAVE_ACQUIRED_S1S_S2_SKILL_XP).addInt((int) experience).addElementalSpirit(getType()));
        if(data.getExperience() > getExperienceToNextLevel()) {
            levelUp();
            owner.sendPacket(SystemMessage.getSystemMessage(S1_ATTACK_SPIRITS_HAVE_REACHED_LEVEL_S2).addElementalSpirit(getType()).addByte(data.getLevel()));
            owner.sendPacket(new ElementalSpiritInfo(owner.getActiveElementalSpiritType(), (byte) 0));
            var userInfo = new UserInfo(owner);
            userInfo.addComponentType(UserInfoType.SPIRITS);
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
        return Math.round( (data.getExperience() - getExperienceToPreviousLevel()) / ElementalSpiritEngine.FRAGMENT_XP_CONSUME);
    }

    public void resetLevel() {
        data.setLevel((byte) Math.max(1, data.getLevel() -1));
        data.setExperience(0);
        resetCharacteristics();
    }

    public boolean canEvolve() {
        return getStage() < ElementalSpiritEngine.MAX_STAGE && getLevel() == 10 && getExperience() == getExperienceToNextLevel();
    }

    public void upgrade() {
        data.increaseStage();
        data.setLevel((byte) 1);
        data.setExperience(0);
        template = ElementalSpiritEngine.getInstance().getSpirit(data.getType(), data.getStage());
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

    private long getExperienceToPreviousLevel() {
        return data.getLevel() < 2 ? 0 : template.getMaxExperienceAtLevel((byte) (data.getLevel() -1));
    }

    public byte getLevel() {
        return data.getLevel();
    }

    public int getMaxLevel() {
        return template.getMaxLevel();
    }


    public int getAttack() {
        return template.getAttackAtLevel(data.getLevel()) + calcCharacteristicPoints(data.getAttackPoints(), ATTACK_MOD_POINTS, ATTACK_MOD_VALUES);
    }

    public int getDefense() {
        return template.getDefenseAtLevel(data.getLevel()) + calcCharacteristicPoints(data.getDefensePoints(), DEFENSE_MOD_POINTS, DEFENSE_MOD_VALUES);
    }

    private int calcCharacteristicPoints(int base, int[] points, int[] values) {
        var amount = 0;
        for (int i = 0; i < points.length; i++) {
            if(base > points[i]) {
                amount += (base -  points[i]) * values[i];
                base -= (base - points[i]);
            }
        }
        return amount;
    }

    public int getCriticalRate() {
        return  template.getCriticalRateAtLevel(data.getLevel()) + calcCharacteristicPoints(data.getCritRatePoints(), CRITIC_MOD_POINTS, CRITIC_MOD_VALUES);
    }

    public int getCriticalDamage() {
        return template.getCriticalDamageAtLevel(data.getLevel()) + calcCharacteristicPoints(data.getCritDamagePoints(), CRITIC_MOD_POINTS, CRITIC_MOD_VALUES);
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
}