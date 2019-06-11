package org.l2j.gameserver.network.clientpackets.primeshop;

import org.l2j.gameserver.data.xml.impl.PrimeShopData;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2j.gameserver.network.serverpackets.primeshop.ExBRProductList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * @author Gnacik, UnAfraid
 */
public final class RequestBRProductList extends IClientIncomingPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestBRProductList.class);
    private int _type;

    @Override
    public void readImpl() {
        _type = readInt();
    }

    @Override
    public void runImpl() {
        final L2PcInstance player = client.getActiveChar();
        if (player != null) {

            switch (_type) {
                case 0: // Home page
                {
                    player.sendPacket(new ExBRProductList(player, 0, PrimeShopData.getInstance().getPrimeItems().values()));
                    break;
                }
                case 1: // History
                {
                    break;
                }
                case 2: // Favorites
                {
                    break;
                }
                default: {
                    LOGGER.warn(player + " send unhandled product list type: " + _type);
                    break;
                }
            }
        }
    }
}