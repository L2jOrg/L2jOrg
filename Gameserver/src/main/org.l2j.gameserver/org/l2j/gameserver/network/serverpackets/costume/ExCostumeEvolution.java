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
package org.l2j.gameserver.network.serverpackets.costume;

import org.l2j.gameserver.data.database.data.CostumeData;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.Collection;
import java.util.Set;

import static java.util.Objects.nonNull;

/**
 * @author JoeAlisson
 */
public class ExCostumeEvolution extends ServerPacket {

    private boolean success;
    private Collection<CostumeData> targetCostumes;
    private CostumeData resultCostume;

    private ExCostumeEvolution() {
    }

    public static ExCostumeEvolution failed() {
        return new ExCostumeEvolution();
    }

    public static ExCostumeEvolution success(Set<CostumeData> costume, CostumeData resultCostume) {
        var packet = new ExCostumeEvolution();
        packet.success = true;
        packet.targetCostumes = costume;
        packet.resultCostume = resultCostume;
        return packet;
    }

    @Override
    protected void writeImpl(GameClient client)  {
        writeId(ServerExPacketId.EX_COSTUME_EVOLUTION);
        writeByte(success);
        writeInt(targetCostumes.size());

        for (CostumeData targetCostume : targetCostumes) {
            writeInt(targetCostume.getId());
            writeLong(targetCostume.getAmount());
        }
        if(nonNull(resultCostume)) {
            writeInt(1);
            writeInt(resultCostume.getId());
            writeLong(resultCostume.getAmount());
        } else {
            writeInt(0);
        }
    }
}
