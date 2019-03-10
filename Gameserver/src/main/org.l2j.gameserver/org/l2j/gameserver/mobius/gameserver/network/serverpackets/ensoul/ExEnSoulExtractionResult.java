package org.l2j.gameserver.mobius.gameserver.network.serverpackets.ensoul;

import org.l2j.gameserver.mobius.gameserver.model.ensoul.EnsoulOption;
import org.l2j.gameserver.mobius.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

/**
 * @author Mobius
 */
public class ExEnSoulExtractionResult extends IClientOutgoingPacket {
    private final boolean _success;
    private final L2ItemInstance _item;

    public ExEnSoulExtractionResult(boolean success, L2ItemInstance item) {
        _success = success;
        _item = item;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_ENSOUL_EXTRACTION_RESULT.writeId(packet);
        packet.put((byte) (_success ? 1 : 0));
        if (_success) {
            packet.put((byte) _item.getSpecialAbilities().size());
            for (EnsoulOption option : _item.getSpecialAbilities()) {
                packet.putInt(option.getId());
            }
            packet.put((byte) _item.getAdditionalSpecialAbilities().size());
            for (EnsoulOption option : _item.getAdditionalSpecialAbilities()) {
                packet.putInt(option.getId());
            }
        }
    }
}
