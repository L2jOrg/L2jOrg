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
package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.actor.instance.Servitor;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import static org.l2j.gameserver.util.GameUtils.isPet;

public class PetStatusUpdate extends ServerPacket {
    private final Summon summon;
    private int maxFed;
    private int currentFed;

    public PetStatusUpdate(Summon summon) {
        this.summon = summon;
        if (isPet(this.summon)) {
            final Pet pet = (Pet) this.summon;
            currentFed = pet.getCurrentFed(); // how fed it is
            maxFed = pet.getMaxFed(); // max fed it can be
        } else if (this.summon.isServitor()) {
            final Servitor sum = (Servitor) this.summon;
            currentFed = sum.getLifeTimeRemaining();
            maxFed = sum.getLifeTime();
        }
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.PET_STATUS_UPDATE);

        writeInt(summon.getSummonType());
        writeInt(summon.getObjectId());
        writeInt(summon.getX());
        writeInt(summon.getY());
        writeInt(summon.getZ());
        writeString(summon.getTitle());
        writeInt(currentFed);
        writeInt(maxFed);
        writeInt((int) summon.getCurrentHp());
        writeInt(summon.getMaxHp());
        writeInt((int) summon.getCurrentMp());
        writeInt(summon.getMaxMp());
        writeInt(summon.getLevel());
        writeLong(summon.getStats().getExp());
        writeLong(summon.getExpForThisLevel());
        writeLong(summon.getExpForNextLevel());
        writeInt(0x00);
    }

}
