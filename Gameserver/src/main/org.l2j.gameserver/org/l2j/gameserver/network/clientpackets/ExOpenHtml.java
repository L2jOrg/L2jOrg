package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.network.serverpackets.html.ExPremiumManagerShowHtml;

/**
 * @author JoeAlisson
 */
public class ExOpenHtml extends ClientPacket {

    private static final String COMMON_HTML_PATH = "data/html/common/%d.htm";
    private byte html;

    @Override
    protected void readImpl() throws Exception {
        html = readByte();
    }

    @Override
    protected void runImpl(){
        client.sendPacket( new ExPremiumManagerShowHtml( HtmCache.getInstance().getHtm( client.getPlayer(), String.format(COMMON_HTML_PATH, html ))));
    }
}
