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
package org.l2j.gameserver.network.serverpackets.html;

import org.l2j.gameserver.enums.HtmlActionScope;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author JoeAlisson
 */
public class ExPremiumManagerShowHtml extends AbstractHtmlPacket {

    public ExPremiumManagerShowHtml(String html) {
        super(html);
    }

    @Override
    protected void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_PREMIUM_MANAGER_SHOW_HTML);
        writeInt(getNpcObjId());
        writeString(getHtml());
        writeInt(-1);
        writeInt(0);
    }

    @Override
    public HtmlActionScope getScope() {
        return HtmlActionScope.PREMIUM_HTML;
    }
}
