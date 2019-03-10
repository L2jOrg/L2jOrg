package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.instancemanager.CastleManorManager;
import org.l2j.gameserver.mobius.gameserver.model.L2Seed;
import org.l2j.gameserver.mobius.gameserver.model.SeedProduction;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author l3x
 */
public class ExShowSeedInfo extends IClientOutgoingPacket {
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
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_SHOW_SEED_INFO.writeId(packet);

        packet.put((byte) (_hideButtons ? 0x01 : 0x00)); // Hide "Seed Purchase" button
        packet.putInt(_manorId); // Manor ID
        packet.putInt(0x00); // Unknown
        if (_seeds == null) {
            packet.putInt(0);
            return;
        }

        packet.putInt(_seeds.size());
        for (SeedProduction seed : _seeds) {
            packet.putInt(seed.getId()); // Seed id
            packet.putLong(seed.getAmount()); // Left to buy
            packet.putLong(seed.getStartAmount()); // Started amount
            packet.putLong(seed.getPrice()); // Sell Price
            final L2Seed s = CastleManorManager.getInstance().getSeed(seed.getId());
            if (s == null) {
                packet.putInt(0); // Seed level
                packet.put((byte) 0x01); // Reward 1
                packet.putInt(0); // Reward 1 - item id
                packet.put((byte) 0x01); // Reward 2
                packet.putInt(0); // Reward 2 - item id
            } else {
                packet.putInt(s.getLevel()); // Seed level
                packet.put((byte) 0x01); // Reward 1
                packet.putInt(s.getReward(1)); // Reward 1 - item id
                packet.put((byte) 0x01); // Reward 2
                packet.putInt(s.getReward(2)); // Reward 2 - item id
            }
        }
    }
}