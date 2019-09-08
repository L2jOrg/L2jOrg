package org.l2j.gameserver.network.serverpackets.html;

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
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.TUTORIAL_SHOW_HTML);
        writeInt(type.getId());
        writeString(getHtml());
    }

    @Override
    public HtmlActionScope getScope() {
        return HtmlActionScope.TUTORIAL_HTML;
    }
}
