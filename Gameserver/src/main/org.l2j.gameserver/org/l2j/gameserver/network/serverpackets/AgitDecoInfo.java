package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.residences.AbstractResidence;
import org.l2j.gameserver.model.residences.ResidenceFunctionType;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Steuf, UnAfraid
 */
public class AgitDecoInfo extends IClientOutgoingPacket {
    private final AbstractResidence _residense;

    public AgitDecoInfo(AbstractResidence residense) {
        _residense = residense;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.AGIT_DECO_INFO.writeId(packet);
        packet.putInt(_residense.getResidenceId());
        for (ResidenceFunctionType type : ResidenceFunctionType.values()) {
            if (type == ResidenceFunctionType.NONE) {
                continue;
            }
            packet.put((byte) (_residense.hasFunction(type) ? 0x01 : 0x00));
        }

        // Unknown
        packet.putInt(0); // TODO: Find me!
        packet.putInt(0); // TODO: Find me!
        packet.putInt(0); // TODO: Find me!
        packet.putInt(0); // TODO: Find me!
        packet.putInt(0); // TODO: Find me!
    }
}
