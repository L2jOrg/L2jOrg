package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.TeleportBookmark;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author ShanSoft
 */
public class ExGetBookMarkInfoPacket extends IClientOutgoingPacket {
    private final L2PcInstance player;

    public ExGetBookMarkInfoPacket(L2PcInstance cha) {
        player = cha;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_GET_BOOK_MARK_INFO.writeId(packet);

        packet.putInt(0x00); // Dummy
        packet.putInt(player.getBookmarkslot());
        packet.putInt(player.getTeleportBookmarks().size());

        for (TeleportBookmark tpbm : player.getTeleportBookmarks()) {
            packet.putInt(tpbm.getId());
            packet.putInt(tpbm.getX());
            packet.putInt(tpbm.getY());
            packet.putInt(tpbm.getZ());
            writeString(tpbm.getName(), packet);
            packet.putInt(tpbm.getIcon());
            writeString(tpbm.getTag(), packet);
        }
    }

    @Override
    protected int size(L2GameClient client) {
        return 17 + player.getTeleportBookmarks().size() * 24 + player.getTeleportBookmarks().stream().mapToInt(t -> (t.getName().length() + t.getTag().length() *2)).sum();
    }
}
