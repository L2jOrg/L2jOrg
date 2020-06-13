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
package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.handler.CommunityBoardHandler;

/**
 * RequestBBSwrite client packet implementation.
 *
 * @author -Wooden-, Zoey76
 */
public final class RequestBBSwrite extends ClientPacket {
    private String _url;
    private String _arg1;
    private String _arg2;
    private String _arg3;
    private String _arg4;
    private String _arg5;

    @Override
    public final void readImpl() {
        _url = readString();
        _arg1 = readString();
        _arg2 = readString();
        _arg3 = readString();
        _arg4 = readString();
        _arg5 = readString();
    }

    @Override
    public final void runImpl() {
        CommunityBoardHandler.getInstance().handleWriteCommand(client.getPlayer(), _url, _arg1, _arg2, _arg3, _arg4, _arg5);
    }
}