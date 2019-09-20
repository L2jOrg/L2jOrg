package org.l2j.gameserver.network.serverpackets.html;

import org.l2j.gameserver.enums.HtmlActionScope;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author JoeAlisson
 */
public class ExPremiumManagerShowHtml extends AbstractHtmlPacket {

    public ExPremiumManagerShowHtml(String html) {
        super(html);
    }

    @Override
    protected void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_PREMIUM_MANAGER_SHOW_HTML);
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
