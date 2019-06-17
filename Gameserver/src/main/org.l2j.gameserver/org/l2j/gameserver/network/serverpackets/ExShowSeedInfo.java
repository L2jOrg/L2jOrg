package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.instancemanager.CastleManorManager;
import org.l2j.gameserver.model.L2Seed;
import org.l2j.gameserver.model.SeedProduction;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

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
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_SHOW_SEED_INFO);

        writeByte((byte) (_hideButtons ? 0x01 : 0x00)); // Hide "Seed Purchase" button
        writeInt(_manorId); // Manor ID
        writeInt(0x00); // Unknown
        if (_seeds == null) {
            writeInt(0);
            return;
        }

        writeInt(_seeds.size());
        for (SeedProduction seed : _seeds) {
            writeInt(seed.getId()); // Seed id
            writeLong(seed.getAmount()); // Left to buy
            writeLong(seed.getStartAmount()); // Started amount
            writeLong(seed.getPrice()); // Sell Price
            final L2Seed s = CastleManorManager.getInstance().getSeed(seed.getId());
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