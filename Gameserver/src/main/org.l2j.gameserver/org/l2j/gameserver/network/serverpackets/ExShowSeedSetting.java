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

import org.l2j.gameserver.data.database.data.SeedProduction;
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
public class ExShowSeedSetting extends ServerPacket {
    private final int _manorId;
    private final Set<Seed> _seeds;
    private final Map<Integer, SeedProduction> _current = new HashMap<>();
    private final Map<Integer, SeedProduction> _next = new HashMap<>();

    public ExShowSeedSetting(int manorId) {
        final CastleManorManager manor = CastleManorManager.getInstance();
        _manorId = manorId;
        _seeds = manor.getSeedsForCastle(_manorId);
        for (Seed s : _seeds) {
            // Current period
            SeedProduction sp = manor.getSeedProduct(manorId, s.getSeedId(), false);
            if (sp != null) {
                _current.put(s.getSeedId(), sp);
            }
            // Next period
            sp = manor.getSeedProduct(manorId, s.getSeedId(), true);
            if (sp != null) {
                _next.put(s.getSeedId(), sp);
            }
        }
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_SHOW_SEED_SETTING);

        writeInt(_manorId); // manor id
        writeInt(_seeds.size()); // size

        for (Seed s : _seeds) {
            writeInt(s.getSeedId()); // seed id
            writeInt(s.getLevel()); // level
            writeByte((byte) 1);
            writeInt(s.getReward(1)); // reward 1 id
            writeByte(1);
            writeInt(s.getReward(2)); // reward 2 id
            writeInt(s.getSeedLimit()); // next sale limit
            writeInt((int) s.getSeedReferencePrice()); // price for castle to produce 1
            writeInt(s.getSeedMinPrice()); // min seed price
            writeInt((int) s.getSeedMaxPrice()); // max seed price
            // Current period
            if (_current.containsKey(s.getSeedId())) {
                final SeedProduction sp = _current.get(s.getSeedId());
                writeLong(sp.getStartAmount()); // sales
                writeLong(sp.getPrice()); // price
            } else {
                writeLong(0);
                writeLong(0);
            }
            // Next period
            if (_next.containsKey(s.getSeedId())) {
                final SeedProduction sp = _next.get(s.getSeedId());
                writeLong(sp.getStartAmount()); // sales
                writeLong(sp.getPrice()); // price
            } else {
                writeLong(0);
                writeLong(0);
            }
        }
        _current.clear();
        _next.clear();
    }

}