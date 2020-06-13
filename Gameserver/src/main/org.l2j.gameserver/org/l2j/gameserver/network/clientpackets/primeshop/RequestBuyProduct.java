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
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.CommonItem;
import org.l2j.gameserver.model.primeshop.PrimeShopProduct;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.primeshop.ExBRBuyProduct;
import org.l2j.gameserver.network.serverpackets.primeshop.ExBRBuyProduct.ExBrProductReplyType;
import org.l2j.gameserver.util.GameUtils;

import java.util.Calendar;

import static org.l2j.commons.database.DatabaseAccess.getDAO;

public abstract class RequestBuyProduct extends ClientPacket {

    private static final int HERO_COINS = 23805;

    protected static boolean validatePlayer(PrimeShopProduct product, int count, Player player) {
        final long currentTime = System.currentTimeMillis() / 1000;

        if (product == null) {
            player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.INVALID_PRODUCT));
            GameUtils.handleIllegalPlayerAction(player, "Player " + player.getName() + " tried to buy invalid brId from Prime");
            return false;
        }

        if ((count < 1) || (count > 99)) {
            GameUtils.handleIllegalPlayerAction(player, "Player " + player.getName() + " tried to buy invalid itemcount [" + count + "] from Prime");
            player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.INCORRECT_COUNT));
            return false;
        }

        if ( (product.getMinLevel() > 0 && product.getMinLevel() > player.getLevel()) || (product.getMaxLevel() > 0 && product.getMaxLevel() < player.getLevel())) {
            player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.INVALID_LEVEL));
            return false;
        }

        if ((product.getMinBirthday() > 0 && product.getMinBirthday() > player.getBirthdays()) || (product.getMaxBirthday() > 0 && product.getMaxBirthday() < player.getBirthdays())) {
            player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.INVALID_DATE_CREATION));
            return false;
        }

        if ((Calendar.getInstance().get(Calendar.DAY_OF_WEEK) & product.getDaysOfWeek()) == 0) {
            player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.NOT_DAY_OF_WEEK));
            return false;
        }

        if(product.getVipTier() > player.getVipTier()) {
            // should never happens but is better check
            // TODO find the correct message
            player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.SOLD_OUT));
            return false;
        }

        if ((product.getStartSale() > 1) && (product.getStartSale() > currentTime)) {
            player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.BEFORE_SALE_DATE));
            return false;
        }

        if ((product.getEndSale() > 1) && (product.getEndSale() < currentTime)) {
            player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.AFTER_SALE_DATE));
            return false;
        }

        final int weight = product.getWeight() * count;
        final long slots = product.getCount() * count;

        if (player.getInventory().validateWeight(weight)) {
            if (!player.getInventory().validateCapacity(slots)) {
                player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.INVENTORY_FULL));
                return false;
            }
        } else {
            player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.INVENTROY_OVERFLOW));
            return false;
        }

        if(product.getRestrictionDay() > 0 && getDAO(PrimeShopDAO.class).countBougthItemToday(player.getObjectId(), product.getId()) >= product.getRestrictionDay()) {
            player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.ALREADY_BOUGHT));
            return false;
        }
        return true;
    }

    protected static int validatePaymentId(Player player, PrimeShopProduct item, long amount) {
        switch (item.getPaymentType()) {
            case 0: // Prime points
            {
                return 0;
            }
            case 1: // Adenas
            {
                return CommonItem.ADENA;
            }
            case 2: // Hero coins
            {
                return HERO_COINS;
            }
        }

        return -1;
    }

    protected boolean processPayment(Player activeChar, PrimeShopProduct item, int count) {
        final int price = (item.getPrice() * count);
        final int paymentId = validatePaymentId(activeChar, item, price);

        if (paymentId < 0) {
            activeChar.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.LACK_OF_POINT));
            return false;
        } else if (paymentId > 0) {
            if (!activeChar.destroyItemByItemId("PrimeShop-" + item.getId(), paymentId, price, activeChar, true)) {
                activeChar.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.LACK_OF_POINT));
                return false;
            }
        } else {
            if (activeChar.getNCoins() < price) {
                activeChar.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.LACK_OF_POINT));
                return false;
            }
            if(price > 0) {
                activeChar.updateNCoins(-price);
                activeChar.updateVipPoints(price);
            }
        }
        return true;
    }
}
