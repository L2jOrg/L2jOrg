/*
 * Copyright © 2019-2021 L2JOrg
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

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.enums.HtmlActionScope;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * TutorialShowHtml server packet implementation.
 *
 * @author HorridoJoho
 */
public final class TutorialShowHtml extends AbstractHtmlPacket {

    private final TutorialWindowType type;
    public TutorialShowHtml(String html) {
        super(html);
        type = TutorialWindowType.STANDARD;
    }

    public TutorialShowHtml(String html, TutorialWindowType type) {
        super(html);
        this.type = type;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.TUTORIAL_SHOW_HTML, buffer );
        buffer.writeInt(type.getId());
        buffer.writeString(getHtml());
    }

    @Override
    public HtmlActionScope getScope() {
        return HtmlActionScope.TUTORIAL_HTML;
    }
}
