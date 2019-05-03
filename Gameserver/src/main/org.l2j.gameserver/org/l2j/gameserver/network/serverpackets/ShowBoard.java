package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.List;

public class ShowBoard extends IClientOutgoingPacket {

    private static final int BOARD_MENU_SIZE = 20 * 8;
    private final String _content;
    private int _showBoard = 1; // 1 show, 0 hide


    public ShowBoard(String htmlCode, String id) {
        _content = id + "\u0008" + htmlCode;
    }

    /**
     * Hides the community board
     */
    public ShowBoard() {
        _showBoard = 0;
        _content = "";
    }

    public ShowBoard(List<String> arg) {
        final StringBuilder builder = new StringBuilder(256).append("1002\u0008");
        for (String str : arg) {
            builder.append(str).append("\u0008");
        }
        _content = builder.toString();
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.SHOW_BOARD.writeId(packet);

        packet.put((byte) _showBoard); // c4 1 to show community 00 to hide
        writeString("bypass _bbshome", packet); // top
        writeString("bypass _bbsgetfav", packet); // favorite
        writeString("bypass _bbsloc", packet); // region
        writeString("bypass _bbsclan", packet); // clan
        writeString("bypass _bbsmemo", packet); // memo
        writeString("bypass _bbsmail", packet); // mail
        writeString("bypass _bbsfriends", packet); // friends
        writeString("bypass bbs_add_fav", packet); // add fav.
        writeString(_content, packet);
    }

    @Override
    protected int size(L2GameClient client) {
        return (_content.length() + BOARD_MENU_SIZE ) * 2;
    }
}
