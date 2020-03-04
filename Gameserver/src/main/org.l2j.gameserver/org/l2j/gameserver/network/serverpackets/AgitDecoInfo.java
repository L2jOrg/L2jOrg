package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.residences.AbstractResidence;
import org.l2j.gameserver.model.residences.ResidenceFunctionType;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author Steuf, UnAfraid
 */
public class AgitDecoInfo extends ServerPacket {
    private final AbstractResidence _residense;

    public AgitDecoInfo(AbstractResidence residense) {
        _residense = residense;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.AGIT_DECO_INFO);
        writeInt(_residense.getId());
        for (ResidenceFunctionType type : ResidenceFunctionType.values()) {
            if (type == ResidenceFunctionType.NONE) {
                continue;
            }
            writeByte((byte) (_residense.hasFunction(type) ? 0x01 : 0x00));
        }

        // Unknown
        writeInt(0); // TODO: Find me!
        writeInt(0); // TODO: Find me!
        writeInt(0); // TODO: Find me!
        writeInt(0); // TODO: Find me!
        writeInt(0); // TODO: Find me!
    }

}
