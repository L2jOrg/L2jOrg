package org.l2j.gameserver.mobius.gameserver.network.serverpackets.ensoul;

import org.l2j.gameserver.mobius.gameserver.model.ensoul.EnsoulOption;
import org.l2j.gameserver.mobius.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

/**
 * @author UnAfraid
 */
public class ExEnsoulResult extends IClientOutgoingPacket {
    private final int _success;
    private final L2ItemInstance _item;

    public ExEnsoulResult(int success, L2ItemInstance item) {
        _success = success;
        _item = item;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_ENSOUL_RESULT.writeId(packet);
        packet.put((byte) _success); // success / failure
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
