package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.network.serverpackets.ExPurchaseLimitShopItemList;
import org.l2j.gameserver.network.serverpackets.html.ExPremiumManagerShowHtml;

/**
 * @author JoeAlisson
 */
public class ExOpenHtml extends ClientPacket {

    private static final String COMMON_HTML_PATH = "data/html/common/%d.htm";
    private byte dialogId;

    @Override
    protected void readImpl() throws Exception {
        dialogId = readByte();
    }

    @Override
    protected void runImpl(){
        switch (dialogId) {
            // case 1 -> pc points
            case 4 -> client.sendPacket(new ExPurchaseLimitShopItemList());
            case 5 -> client.sendPacket(new ExPremiumManagerShowHtml( HtmCache.getInstance().getHtm( client.getPlayer(), String.format(COMMON_HTML_PATH, dialogId))));
        }
    }
}
