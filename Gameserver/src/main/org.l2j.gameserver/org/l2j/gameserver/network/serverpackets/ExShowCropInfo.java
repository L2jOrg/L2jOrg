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

import org.l2j.gameserver.data.database.data.CropProcure;
import org.l2j.gameserver.instancemanager.CastleManorManager;
import org.l2j.gameserver.model.Seed;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

import java.util.List;

/**
 * @author l3x
 */
public class ExShowCropInfo extends ServerPacket {
    private final List<CropProcure> _crops;
    private final int _manorId;
    private final boolean _hideButtons;

    public ExShowCropInfo(int manorId, boolean nextPeriod, boolean hideButtons) {
        _manorId = manorId;
        _hideButtons = hideButtons;

        final CastleManorManager manor = CastleManorManager.getInstance();
        _crops = (nextPeriod && !manor.isManorApproved()) ? null : manor.getCropProcure(manorId, nextPeriod);
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_SHOW_CROP_INFO);

        writeByte((byte)(_hideButtons ? 0x01 : 0x00)); // Hide "Crop Sales" button
        writeInt(_manorId); // Manor ID
        writeInt(0x00);
        if (_crops != null) {
            writeInt(_crops.size());
            for (CropProcure crop : _crops) {
                writeInt(crop.getSeedId()); // Crop id
                writeLong(crop.getAmount()); // Buy residual
                writeLong(crop.getStartAmount()); // Buy
                writeLong(crop.getPrice()); // Buy price
                writeByte((byte) crop.getReward()); // Reward
                final Seed seed = CastleManorManager.getInstance().getSeedByCrop(crop.getSeedId());
                if (seed == null) {
                    writeInt(0); // Seed level
                    writeByte((byte) 0x01); // Reward 1
                    writeInt(0); // Reward 1 - item id
                    writeByte((byte) 0x01); // Reward 2
                    writeInt(0); // Reward 2 - item id
                } else {
                    writeInt(seed.getLevel()); // Seed level
                    writeByte((byte) 0x01); // Reward 1
                    writeInt(seed.getReward(1)); // Reward 1 - item id
                    writeByte((byte) 0x01); // Reward 2
                    writeInt(seed.getReward(2)); // Reward 2 - item id
                }
            }
        }
    }

}