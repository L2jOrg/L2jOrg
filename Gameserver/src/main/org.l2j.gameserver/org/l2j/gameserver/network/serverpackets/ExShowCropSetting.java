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
import org.l2j.gameserver.data.database.data.CropProcure;
import org.l2j.gameserver.instancemanager.CastleManorManager;
import org.l2j.gameserver.model.Seed;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author l3x
 */
public class ExShowCropSetting extends ServerPacket {
    private final int _manorId;
    private final Set<Seed> _seeds;
    private final Map<Integer, CropProcure> _current = new HashMap<>();
    private final Map<Integer, CropProcure> _next = new HashMap<>();

    public ExShowCropSetting(int manorId) {
        final CastleManorManager manor = CastleManorManager.getInstance();
        _manorId = manorId;
        _seeds = manor.getSeedsForCastle(_manorId);
        for (Seed s : _seeds) {
            // Current period
            CropProcure cp = manor.getCropProcure(manorId, s.getCropId(), false);
            if (cp != null) {
                _current.put(s.getCropId(), cp);
            }
            // Next period
            cp = manor.getCropProcure(manorId, s.getCropId(), true);
            if (cp != null) {
                _next.put(s.getCropId(), cp);
            }
        }
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_SHOW_CROP_SETTING, buffer );

        buffer.writeInt(_manorId); // manor id
        buffer.writeInt(_seeds.size()); // size

        for (Seed s : _seeds) {
            buffer.writeInt(s.getCropId()); // crop id
            buffer.writeInt(s.getLevel()); // seed level
            buffer.writeByte(1);
            buffer.writeInt(s.getReward(1)); // reward 1 id
            buffer.writeByte(1);
            buffer.writeInt(s.getReward(2)); // reward 2 id
            buffer.writeInt(s.getCropLimit()); // next sale limit
            buffer.writeInt(0); // ???
            buffer.writeInt(s.getCropMinPrice()); // min crop price
            buffer.writeInt((int) s.getCropMaxPrice()); // max crop price
            // Current period
            if (_current.containsKey(s.getCropId())) {
                final CropProcure cp = _current.get(s.getCropId());
                buffer.writeLong(cp.getStartAmount()); // buy
                buffer.writeLong(cp.getPrice()); // price
                buffer.writeByte(cp.getReward()); // reward
            } else {
                buffer.writeLong(0);
                buffer.writeLong(0);
                buffer.writeByte(0);
            }
            // Next period
            if (_next.containsKey(s.getCropId())) {
                final CropProcure cp = _next.get(s.getCropId());
                buffer.writeLong(cp.getStartAmount()); // buy
                buffer.writeLong(cp.getPrice()); // price
                buffer.writeByte(cp.getReward()); // reward
            } else {
                buffer.writeLong(0);
                buffer.writeLong(0);
                buffer.writeByte(0);
            }
        }
        _next.clear();
        _current.clear();
    }

}