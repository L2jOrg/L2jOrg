package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class ExPutIntensiveResultForVariationMake extends IClientOutgoingPacket {
    private final int _refinerItemObjId;
    private final int _lifestoneItemId;
    private final int _gemstoneItemId;
    private final long _gemstoneCount;
    private final int _unk2;

    public ExPutIntensiveResultForVariationMake(int refinerItemObjId, int lifeStoneId, int gemstoneItemId, long gemstoneCount) {
        _refinerItemObjId = refinerItemObjId;
        _lifestoneItemId = lifeStoneId;
        _gemstoneItemId = gemstoneItemId;
        _gemstoneCount = gemstoneCount;
        _unk2 = 1;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_PUT_INTENSIVE_RESULT_FOR_VARIATION_MAKE.writeId(packet);

        packet.putInt(_refinerItemObjId);
        packet.putInt(_lifestoneItemId);
        packet.putInt(_gemstoneItemId);
        packet.putLong(_gemstoneCount);
        packet.putInt(_unk2);
    }
}
