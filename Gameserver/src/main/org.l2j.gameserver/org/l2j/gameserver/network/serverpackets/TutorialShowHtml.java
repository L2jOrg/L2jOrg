package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.enums.HtmlActionScope;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * TutorialShowHtml server packet implementation.
 *
 * @author HorridoJoho
 */
public final class TutorialShowHtml extends AbstractHtmlPacket {
    // TODO: Enum
    public static final int NORMAL_WINDOW = 1;
    public static final int LARGE_WINDOW = 2;

    private final int _type;

    public TutorialShowHtml(String html) {
        super(html);
        _type = NORMAL_WINDOW;
    }

    public TutorialShowHtml(int npcObjId, String html, int type) {
        super(npcObjId, html);
        _type = type;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.TUTORIAL_SHOW_HTML);

        writeInt(_type);
        writeString(getHtml());
    }


    @Override
    public HtmlActionScope getScope() {
        return HtmlActionScope.TUTORIAL_HTML;
    }
}
