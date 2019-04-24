package org.l2j.gameserver.network.clientpackets.primeshop;

import org.l2j.gameserver.data.xml.impl.PrimeShopData;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.actor.request.PrimeShopRequest;
import org.l2j.gameserver.model.primeshop.PrimeShopItem;
import org.l2j.gameserver.model.primeshop.PrimeShopProduct;
import org.l2j.gameserver.network.serverpackets.primeshop.ExBRBuyProduct;
import org.l2j.gameserver.network.serverpackets.primeshop.ExBRGamePoint;

import java.nio.ByteBuffer;

/**
 * @author Gnacik, UnAfraid
 */
public final class RequestBRBuyProduct extends RequestBuyProduct {

    private int productId;
    private int count;

    @Override
    public void readImpl(ByteBuffer packet) {
        productId = packet.getInt();
        count = packet.getInt();
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (activeChar.hasItemRequest() || activeChar.hasRequest(PrimeShopRequest.class)) {
            activeChar.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.INVALID_USER_STATE));
            return;
        }

        activeChar.addRequest(new PrimeShopRequest(activeChar));
        final PrimeShopProduct item = PrimeShopData.getInstance().getItem(productId);

        if (validatePlayer(item, count, activeChar) && processPayment(activeChar, item, count)) {

            for (PrimeShopItem subItem : item.getItems()) {
                activeChar.addItem("PrimeShop", subItem.getId(), subItem.getCount() * count, activeChar, true);
            }

            client.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.SUCCESS));
            client.sendPacket(new ExBRGamePoint());
        }

        activeChar.removeRequest(PrimeShopRequest.class);
    }

}
