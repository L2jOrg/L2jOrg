/*
 * Copyright © 2019-2021 L2JOrg
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

import io.github.joealisson.mmocore.WritableBuffer;
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
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_SHOW_MANOR_DEFAULT_INFO, buffer );

        buffer.writeByte(_hideButtons); // Hide "Seed Purchase" and "Crop Sales" buttons
        buffer.writeInt(_crops.size());
        for (Seed crop : _crops) {
            buffer.writeInt(crop.getCropId()); // crop Id
            buffer.writeInt(crop.getLevel()); // level
            buffer.writeInt((int) crop.getSeedReferencePrice()); // seed price
            buffer.writeInt((int) crop.getCropReferencePrice()); // crop price
            buffer.writeByte(1); // Reward 1 type
            buffer.writeInt(crop.getReward(1)); // Reward 1 itemId
            buffer.writeByte(1); // Reward 2 type
            buffer.writeInt(crop.getReward(2)); // Reward 2 itemId
        }
    }

}