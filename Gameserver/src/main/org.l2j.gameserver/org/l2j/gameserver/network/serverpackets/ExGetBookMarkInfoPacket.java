package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.TeleportBookmark;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author ShanSoft
 */
public class ExGetBookMarkInfoPacket extends ServerPacket {
    private final Player player;

    public ExGetBookMarkInfoPacket(Player cha) {
        player = cha;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_USER_BOOKMARK);

        writeInt(0x00); // Dummy
        writeInt(player.getBookmarkslot());
        writeInt(player.getTeleportBookmarks().size());

        for (TeleportBookmark tpbm : player.getTeleportBookmarks()) {
            writeInt(tpbm.getId());
            writeInt(tpbm.getX());
            writeInt(tpbm.getY());
            writeInt(tpbm.getZ());
            writeString(tpbm.getName());
            writeInt(tpbm.getIcon());
            writeString(tpbm.getTag());
        }
    }

}
