package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.instancemanager.CastleManorManager;
import org.l2j.gameserver.model.L2Seed;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author l3x
 */
public final class ExShowManorDefaultInfo extends IClientOutgoingPacket {
    private final List<L2Seed> _crops;
    private final boolean _hideButtons;

    public ExShowManorDefaultInfo(boolean hideButtons) {
        _crops = CastleManorManager.getInstance().getCrops();
        _hideButtons = hideButtons;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_SHOW_MANOR_DEFAULT_INFO.writeId(packet);

        packet.put((byte) (_hideButtons ? 0x01 : 0x00)); // Hide "Seed Purchase" and "Crop Sales" buttons
        packet.putInt(_crops.size());
        for (L2Seed crop : _crops) {
            packet.putInt(crop.getCropId()); // crop Id
            packet.putInt(crop.getLevel()); // level
            packet.putInt(crop.getSeedReferencePrice()); // seed price
            packet.putInt(crop.getCropReferencePrice()); // crop price
            packet.put((byte) 1); // Reward 1 type
            packet.putInt(crop.getReward(1)); // Reward 1 itemId
            packet.put((byte) 1); // Reward 2 type
            packet.putInt(crop.getReward(2)); // Reward 2 itemId
        }
    }

    @Override
    protected int size(L2GameClient client) {
        return 10 + _crops.size() * 26;
    }
}