/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.model.actor.stat;

import org.l2j.gameserver.data.xml.impl.ExperienceData;
import org.l2j.gameserver.data.xml.impl.PetDataTable;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SocialAction;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

public class PetStat extends SummonStat {
    public PetStat(Pet activeChar) {
        super(activeChar);
    }

    public boolean addExp(int value) {
        if (getActiveChar().isUncontrollable() || !super.addExp(value)) {
            return false;
        }

        getActiveChar().updateAndBroadcastStatus(1);
        return true;
    }

    public boolean addExpAndSp(double addToExp, double addToSp) {
        final long finalExp = Math.round(addToExp);
        if (getActiveChar().isUncontrollable() || !addExp(finalExp)) {
            return false;
        }

        SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOUR_PET_GAINED_S1_XP);
        sm.addLong(finalExp);
        getActiveChar().updateAndBroadcastStatus(1);
        getActiveChar().sendPacket(sm);
        return true;
    }

    @Override
    public final boolean addLevel(byte value) {
        if ((getLevel() + value) > (getMaxLevel() - 1)) {
            return false;
        }

        final boolean levelIncreased = super.addLevel(value);

        getActiveChar().broadcastStatusUpdate();
        if (levelIncreased) {
            getActiveChar().broadcastPacket(new SocialAction(getActiveChar().getObjectId(), SocialAction.LEVEL_UP));
        }
        // Send a Server->Client packet PetInfo to the Player
        getActiveChar().updateAndBroadcastStatus(1);

        if (getActiveChar().getControlItem() != null) {
            getActiveChar().getControlItem().setEnchantLevel(getLevel());
        }

        return levelIncreased;
    }

    @Override
    public final long getExpForLevel(int level) {
        try {
            return PetDataTable.getInstance().getPetLevelData(getActiveChar().getId(), level).getPetMaxExp();
        } catch (NullPointerException e) {
            if (getActiveChar() != null) {
                LOGGER.warn("Pet objectId:" + getActiveChar().getObjectId() + ", NpcId:" + getActiveChar().getId() + ", level:" + level + " is missing data from pets_stats table!");
            }
            throw e;
        }
    }

    @Override
    public Pet getActiveChar() {
        return (Pet) super.getActiveChar();
    }

    public final int getFeedBattle() {
        return getActiveChar().getPetLevelData().getPetFeedBattle();
    }

    public final int getFeedNormal() {
        return getActiveChar().getPetLevelData().getPetFeedNormal();
    }

    @Override
    public void setLevel(byte value) {
        getActiveChar().setPetData(PetDataTable.getInstance().getPetLevelData(getActiveChar().getTemplate().getId(), value));
        if (getActiveChar().getPetLevelData() == null) {
            throw new IllegalArgumentException("No pet data for npc: " + getActiveChar().getTemplate().getId() + " level: " + value);
        }
        getActiveChar().stopFeed();
        super.setLevel(value);

        getActiveChar().startFeed();

        if (getActiveChar().getControlItem() != null) {
            getActiveChar().getControlItem().setEnchantLevel(getLevel());
        }
    }

    public final int getMaxFeed() {
        return getActiveChar().getPetLevelData().getPetMaxFeed();
    }

    @Override
    public int getPAtkSpd() {
        int val = super.getPAtkSpd();
        if (getActiveChar().isHungry()) {
            val /= 2;
        }
        return val;
    }

    @Override
    public int getMAtkSpd() {
        int val = super.getMAtkSpd();
        if (getActiveChar().isHungry()) {
            val /= 2;
        }
        return val;
    }

    @Override
    public int getMaxLevel() {
        return ExperienceData.getInstance().getMaxPetLevel();
    }
}
