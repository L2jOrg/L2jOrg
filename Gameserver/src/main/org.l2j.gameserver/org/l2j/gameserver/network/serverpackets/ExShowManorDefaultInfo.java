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

import org.l2j.gameserver.instancemanager.CastleManorManager;
import org.l2j.gameserver.model.Seed;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

import java.util.List;

/**
 * @author l3x
 */
public final class ExShowManorDefaultInfo extends ServerPacket {
    private final List<Seed> _crops;
    private final boolean _hideButtons;

    public ExShowManorDefaultInfo(boolean hideButtons) {
        _crops = CastleManorManager.getInstance().getCrops();
        _hideButtons = hideButtons;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_SHOW_MANOR_DEFAULT_INFO);

        writeByte((byte) (_hideButtons ? 0x01 : 0x00)); // Hide "Seed Purchase" and "Crop Sales" buttons
        writeInt(_crops.size());
        for (Seed crop : _crops) {
            writeInt(crop.getCropId()); // crop Id
            writeInt(crop.getLevel()); // level
            writeInt((int) crop.getSeedReferencePrice()); // seed price
            writeInt((int) crop.getCropReferencePrice()); // crop price
            writeByte((byte) 1); // Reward 1 type
            writeInt(crop.getReward(1)); // Reward 1 itemId
            writeByte((byte) 1); // Reward 2 type
            writeInt(crop.getReward(2)); // Reward 2 itemId
        }
    }

}