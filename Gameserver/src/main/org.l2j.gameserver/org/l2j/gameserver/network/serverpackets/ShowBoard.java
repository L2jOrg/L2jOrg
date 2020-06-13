/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.List;

public class ShowBoard extends ServerPacket {

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
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.SHOW_BOARD);

        writeByte(_showBoard); // c4 1 to show community 00 to hide
        writeString("bypass _bbshome"); // top
        writeString("bypass _bbsgetfav"); // favorite
        writeString("bypass _bbsloc"); // region
        writeString("bypass _bbsclan"); // clan
        writeString("bypass _bbsmemo"); // memo
        writeString("bypass _bbsmail"); // mail
        writeString("bypass _bbsfriends"); // friends
        writeString("bypass _bbsgetfav add"); // add fav.
        writeString(_content);
    }

}
