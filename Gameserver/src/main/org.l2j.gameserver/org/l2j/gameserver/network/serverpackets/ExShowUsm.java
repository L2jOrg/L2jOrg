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
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author Sdw
 */
public class ExShowUsm extends ServerPacket {
    public static final ExShowUsm GOD_INTRO = new ExShowUsm(2);
    public static final ExShowUsm SECOND_TRANSFER_QUEST = new ExShowUsm(4);
    public static final ExShowUsm OCTAVIS_INSTANCE_END = new ExShowUsm(6);
    public static final ExShowUsm AWAKENING_END = new ExShowUsm(10);
    public static final ExShowUsm ERTHEIA_FIRST_QUEST = new ExShowUsm(14);
    public static final ExShowUsm USM_Q015_E = new ExShowUsm(15); // Chamber of Prophecies instance
    public static final ExShowUsm ERTHEIA_INTRO_FOR_ERTHEIA = new ExShowUsm(147);
    public static final ExShowUsm ERTHEIA_INTRO_FOR_OTHERS = new ExShowUsm(148);

    private final int _videoId;

    private ExShowUsm(int videoId) {
        _videoId = videoId;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_SEND_USM_EVENT);

        writeInt(_videoId);
    }

}
