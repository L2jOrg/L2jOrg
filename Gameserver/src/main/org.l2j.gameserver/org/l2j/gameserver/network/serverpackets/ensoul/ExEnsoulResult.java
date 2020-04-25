package org.l2j.gameserver.network.serverpackets.ensoul;

import org.l2j.gameserver.model.ensoul.EnsoulOption;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author UnAfraid
 */
public class ExEnsoulResult extends ServerPacket {
    private final int _success;
    private final Item _item;

    public ExEnsoulResult(int success, Item item) {
        _success = success;
        _item = item;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_ENSOUL_RESULT);
        writeByte((byte) _success); // success / failure
        writeByte((byte) _item.getSpecialAbilities().size());
        for (EnsoulOption option : _item.getSpecialAbilities()) {
            writeInt(option.getId());
        }
        writeByte((byte) _item.getAdditionalSpecialAbilities().size());
        for (EnsoulOption option : _item.getAdditionalSpecialAbilities()) {
            writeInt(option.getId());
        }
    }

}
