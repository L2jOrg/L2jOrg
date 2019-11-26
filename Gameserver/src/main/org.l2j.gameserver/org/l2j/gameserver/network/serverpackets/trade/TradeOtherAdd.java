package org.l2j.gameserver.network.serverpackets.trade;

import org.l2j.gameserver.model.TradeItem;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author Yme
 * @author JoeAlisson
 */
public final class TradeOtherAdd extends TradeAdd {

    public TradeOtherAdd(int type, TradeItem item) {
        super(type, item);
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.TRADE_OTHER_ADD);
        writeItemAdd();
    }

}
