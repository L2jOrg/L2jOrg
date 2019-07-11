package org.l2j.gameserver.network.clientpackets.primeshop;

import org.l2j.gameserver.data.xml.impl.PrimeShopData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

/**
 * @author Gnacik, UnAfraid
 */
public final class RequestBRProductInfo extends ClientPacket {
    private int _brId;

    @Override
    public void readImpl() {
        _brId = readInt();
    }

    @Override
    public void runImpl() {
        final Player player = client.getActiveChar();
        if (player != null) {
            PrimeShopData.getInstance().showProductInfo(player, _brId);
        }
    }
}
