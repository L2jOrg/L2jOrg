/*
 * Copyright © 2019-2020 L2JOrg
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
package org.l2j.gameserver.network.clientpackets.primeshop;

import org.l2j.gameserver.data.database.dao.PrimeShopDAO;
import org.l2j.gameserver.data.database.data.MailData;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.data.xml.impl.PrimeShopData;
import org.l2j.gameserver.engine.mail.MailEngine;
import org.l2j.gameserver.enums.MailType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.request.PrimeShopRequest;
import org.l2j.gameserver.model.item.container.Attachment;
import org.l2j.gameserver.model.primeshop.PrimeShopItem;
import org.l2j.gameserver.model.primeshop.PrimeShopProduct;
import org.l2j.gameserver.network.serverpackets.ExBRNewIconCashBtnWnd;
import org.l2j.gameserver.network.serverpackets.primeshop.ExBRBuyProduct;
import org.l2j.gameserver.network.serverpackets.primeshop.ExBRGamePoint;

import static org.l2j.commons.database.DatabaseAccess.getDAO;

/**
 * @author Gnacik, UnAfraid
 */
public final class RequestBRPresentBuyProduct extends RequestBuyProduct {

    private int productId;
    private int count;
    private String _charName;
    private String _mailTitle;
    private String _mailBody;

    @Override
    public void readImpl() {
        productId = readInt();
        count = readInt();
        _charName = readString();
        _mailTitle = readString();
        _mailBody = readString();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();

        if (activeChar == null) {
            return;
        }

        final int receiverId = PlayerNameTable.getInstance().getIdByName(_charName);
        if (receiverId <= 0) {
            activeChar.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.INVENTORY_FULL0));
            return;
        }

        if (activeChar.hasItemRequest() || activeChar.hasRequest(PrimeShopRequest.class)) {
            activeChar.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.INVENTORY_FULL));
            return;
        }

        try {
            activeChar.addRequest(new PrimeShopRequest(activeChar));

            final PrimeShopProduct item = PrimeShopData.getInstance().getItem(productId);
            if (validatePlayer(item, count, activeChar) && processPayment(activeChar, item, count)) {

                client.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.SUCCESS));
                client.sendPacket(new ExBRGamePoint());

                final var mail = MailData.of(receiverId, _mailTitle, _mailBody, MailType.PRIME_SHOP_GIFT);

                final Attachment attachement = new Attachment(mail.getSender(), mail.getId());
                for (PrimeShopItem subItem : item.getItems()) {
                    attachement.addItem("Prime Shop Gift", subItem.getId(), subItem.getCount() * count, activeChar, this);
                }
                mail.attach(attachement);
                MailEngine.getInstance().sendMail(mail);
                getDAO(PrimeShopDAO.class).addHistory(productId, count, activeChar.getObjectId());
                if(item.isVipGift()) {
                    client.sendPacket(ExBRNewIconCashBtnWnd.NOT_SHOW);
                }
            }
        } finally {
            activeChar.removeRequest(PrimeShopRequest.class);
        }

    }
}
