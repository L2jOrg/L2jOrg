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
import org.l2j.gameserver.network.ServerPacketId;

/**
 * NpcHtmlMessage server packet implementation.
 *
 * @author HorridoJoho
 */
public final class NpcHtmlMessage extends AbstractHtmlPacket {
    private final int _itemId;

    public NpcHtmlMessage() {
        _itemId = 0;
    }

    public NpcHtmlMessage(int npcObjId) {
        super(npcObjId);
        _itemId = 0;
    }

    public NpcHtmlMessage(String html) {
        super(html);
        _itemId = 0;
    }

    public NpcHtmlMessage(int npcObjId, String html) {
        super(npcObjId, html);
        _itemId = 0;
    }

    public NpcHtmlMessage(int npcObjId, int itemId) {
        super(npcObjId);

        if (itemId < 0) {
            throw new IllegalArgumentException();
        }

        _itemId = itemId;
    }

    public NpcHtmlMessage(int npcObjId, int itemId, String html) {
        super(npcObjId, html);

        if (itemId < 0) {
            throw new IllegalArgumentException();
        }

        _itemId = itemId;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.NPC_HTML_MESSAGE);

        writeInt(getNpcObjId());
        writeString(getHtml());
        writeInt(_itemId);
        writeInt(0x00); // TODO: Find me!
    }

    @Override
    public HtmlActionScope getScope() {
        return _itemId == 0 ? HtmlActionScope.NPC_HTML : HtmlActionScope.NPC_ITEM_HTML;
    }

}
