package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.items.L2Henna;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author Zoey76
 */
public class HennaRemoveList extends ServerPacket {
    private final Player _player;

    public HennaRemoveList(Player player) {
        _player = player;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.HENNA_UNEQUIP_LIST);

        writeLong(_player.getAdena());
        writeInt(0x03); // seems to be max size
        writeInt(3 - _player.getHennaEmptySlots());

        for (L2Henna henna : _player.getHennaList()) {
            if (henna != null) {
                writeInt(henna.getDyeId());
                writeInt(henna.getDyeItemId());
                writeLong(henna.getCancelCount());
                writeLong(henna.getCancelFee());
                writeInt(0x00);
            }
        }
    }

}
