/*
 * Copyright © 2019-2021 L2JOrg
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
package org.l2j.gameserver.network.clientpackets.l2store;

import org.l2j.gameserver.data.database.dao.L2StoreDAO;
import org.l2j.gameserver.data.database.data.MailData;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.engine.item.shop.L2Store;
import org.l2j.gameserver.engine.item.shop.l2store.L2StoreItem;
import org.l2j.gameserver.engine.item.shop.l2store.L2StoreProduct;
import org.l2j.gameserver.engine.mail.MailEngine;
import org.l2j.gameserver.enums.MailType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.request.L2StoreRequest;
import org.l2j.gameserver.model.item.container.Attachment;
import org.l2j.gameserver.network.serverpackets.ExBRNewIconCashBtnWnd;
import org.l2j.gameserver.network.serverpackets.store.ExBRBuyProduct;
import org.l2j.gameserver.network.serverpackets.store.ExBRGamePoint;

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
        final Player player = client.getPlayer();

        if (player == null) {
            return;
        }

        final int receiverId = PlayerNameTable.getInstance().getIdByName(_charName);
        if (receiverId <= 0) {
            player.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.INVENTORY_FULL0));
            return;
        }

        if (player.hasItemRequest() || player.hasRequest(L2StoreRequest.class)) {
            player.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.INVENTORY_FULL));
            return;
        }

        try {
            player.addRequest(new L2StoreRequest(player));

            final L2StoreProduct item = L2Store.getInstance().getItem(productId);
            if (validatePlayer(item, count, player) && processPayment(player, item, count)) {

                client.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.SUCCESS));
                client.sendPacket(new ExBRGamePoint());

                final var mail = MailData.of(receiverId, _mailTitle, _mailBody, MailType.PRIME_SHOP_GIFT);

                final Attachment attachement = new Attachment(mail.getSender(), mail.getId());
                for (L2StoreItem subItem : item.getItems()) {
                    attachement.addItem("Prime Shop Gift", subItem.getId(), subItem.getCount() * count, player, this);
                }
                mail.attach(attachement);
                MailEngine.getInstance().sendMail(mail);
                getDAO(L2StoreDAO.class).addHistory(productId, count, player.getAccountName());
                if(item.isVipGift()) {
                    client.sendPacket(ExBRNewIconCashBtnWnd.NOT_SHOW);
                }
            }
        } finally {
            player.removeRequest(L2StoreRequest.class);
        }

    }
}
