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
package org.l2j.gameserver.model.actor.stat;

import org.l2j.gameserver.data.xml.impl.LevelData;
import org.l2j.gameserver.data.xml.impl.PetDataTable;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SocialAction;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

public class PetStats extends SummonStats {
    public PetStats(Pet activeChar) {
        super(activeChar);
    }

    public boolean addExp(int value) {
        if (getCreature().isUncontrollable() || !super.addExp(value)) {
            return false;
        }

        getCreature().updateAndBroadcastStatus(1);
        return true;
    }

    public boolean addExpAndSp(double addToExp, double addToSp) {
        final long finalExp = Math.round(addToExp);
        if (getCreature().isUncontrollable() || !addExp(finalExp)) {
            return false;
        }

        SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOUR_PET_GAINED_S1_XP);
        sm.addLong(finalExp);
        getCreature().updateAndBroadcastStatus(1);
        getCreature().sendPacket(sm);
        return true;
    }

    @Override
    public final boolean addLevel(byte value) {
        if ((getLevel() + value) > (getMaxLevel() - 1)) {
            return false;
        }

        final boolean levelIncreased = super.addLevel(value);

        getCreature().broadcastStatusUpdate();
        if (levelIncreased) {
            getCreature().broadcastPacket(new SocialAction(getCreature().getObjectId(), SocialAction.LEVEL_UP));
        }
        // Send a Server->Client packet PetInfo to the Player
        getCreature().updateAndBroadcastStatus(1);

        if (getCreature().getControlItem() != null) {
            getCreature().getControlItem().setEnchantLevel(getLevel());
        }

        return levelIncreased;
    }

    @Override
    public final long getExpForLevel(int level) {
        try {
            return PetDataTable.getInstance().getPetLevelData(getCreature().getId(), level).getPetMaxExp();
        } catch (NullPointerException e) {
            if (getCreature() != null) {
                LOGGER.warn("Pet objectId:" + getCreature().getObjectId() + ", NpcId:" + getCreature().getId() + ", level:" + level + " is missing data from pets_stats table!");
            }
            throw e;
        }
    }

    @Override
    public Pet getCreature() {
        return (Pet) super.getCreature();
    }

    public final int getFeedBattle() {
        return getCreature().getPetLevelData().getPetFeedBattle();
    }

    public final int getFeedNormal() {
        return getCreature().getPetLevelData().getPetFeedNormal();
    }

    @Override
    public void setLevel(byte value) {
        getCreature().setPetData(PetDataTable.getInstance().getPetLevelData(getCreature().getTemplate().getId(), value));
        if (getCreature().getPetLevelData() == null) {
            throw new IllegalArgumentException("No pet data for npc: " + getCreature().getTemplate().getId() + " level: " + value);
        }
        getCreature().stopFeed();
        super.setLevel(value);

        getCreature().startFeed();

        if (getCreature().getControlItem() != null) {
            getCreature().getControlItem().setEnchantLevel(getLevel());
        }
    }

    public final int getMaxFeed() {
        return getCreature().getPetLevelData().getPetMaxFeed();
    }

    @Override
    public int getPAtkSpd() {
        int val = super.getPAtkSpd();
        if (getCreature().isHungry()) {
            val /= 2;
        }
        return val;
    }

    @Override
    public int getMAtkSpd() {
        int val = super.getMAtkSpd();
        if (getCreature().isHungry()) {
            val /= 2;
        }
        return val;
    }

    @Override
    public int getMaxLevel() {
        return LevelData.getInstance().getMaxLevel();
    }
}
