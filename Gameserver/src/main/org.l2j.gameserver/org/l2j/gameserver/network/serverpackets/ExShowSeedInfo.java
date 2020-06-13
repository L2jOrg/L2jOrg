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

import java.util.List;

/**
 * @author l3x
 */
public class ExShowSeedInfo extends ServerPacket {
    private final List<SeedProduction> _seeds;
    private final int _manorId;
    private final boolean _hideButtons;

    public ExShowSeedInfo(int manorId, boolean nextPeriod, boolean hideButtons) {
        _manorId = manorId;
        _hideButtons = hideButtons;

        final CastleManorManager manor = CastleManorManager.getInstance();
        _seeds = (nextPeriod && !manor.isManorApproved()) ? null : manor.getSeedProduction(manorId, nextPeriod);
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_SHOW_SEED_INFO);

        writeByte((byte) (_hideButtons ? 0x01 : 0x00)); // Hide "Seed Purchase" button
        writeInt(_manorId); // Manor ID
        writeInt(0x00); // Unknown
        if (_seeds == null) {
            writeInt(0);
            return;
        }

        writeInt(_seeds.size());
        for (SeedProduction seed : _seeds) {
            writeInt(seed.getSeedId()); // Seed id
            writeLong(seed.getAmount()); // Left to buy
            writeLong(seed.getStartAmount()); // Started amount
            writeLong(seed.getPrice()); // Sell Price
            final Seed s = CastleManorManager.getInstance().getSeed(seed.getSeedId());
            if (s == null) {
                writeInt(0); // Seed level
                writeByte((byte) 0x01); // Reward 1
                writeInt(0); // Reward 1 - item id
                writeByte((byte) 0x01); // Reward 2
                writeInt(0); // Reward 2 - item id
            } else {
                writeInt(s.getLevel()); // Seed level
                writeByte((byte) 0x01); // Reward 1
                writeInt(s.getReward(1)); // Reward 1 - item id
                writeByte((byte) 0x01); // Reward 2
                writeInt(s.getReward(2)); // Reward 2 - item id
            }
        }
    }

}