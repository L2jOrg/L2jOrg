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
package org.l2j.gameserver.network.clientpackets.l2store;

import org.l2j.gameserver.data.database.dao.L2StoreDAO;
import org.l2j.gameserver.engine.item.shop.l2store.L2StoreProduct;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.CommonItem;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.store.ExBRBuyProduct;
import org.l2j.gameserver.network.serverpackets.store.ExBRBuyProduct.ExBrProductReplyType;
import org.l2j.gameserver.util.GameUtils;

import java.util.Calendar;

import static java.util.Objects.isNull;
import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.gameserver.network.SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_NCOIN;

/**
 * @author JoeAlisson
 */
public abstract class RequestBuyProduct extends ClientPacket {

    private static final int HERO_COINS = 23805;

    protected boolean validatePlayer(L2StoreProduct product, int count, Player player) {
        final long currentTime = System.currentTimeMillis() / 1000;

        if (isNull(product)) {
            player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.INVALID_PRODUCT));
            GameUtils.handleIllegalPlayerAction(player, "Player " + player.getName() + " tried to buy invalid brId from Prime");
            return false;
        }

        if (count < 1 || count > 99) {
            GameUtils.handleIllegalPlayerAction(player, "Player " + player.getName() + " tried to buy invalid item count [" + count + "] from l2 store");
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

        return !hasBoughtMaxAmount(product, count, player);
    }

    private boolean hasBoughtMaxAmount(L2StoreProduct product, int count, Player player) {
        if(product.getRestrictionAmount() > 0) {
            if (count > product.getRestrictionAmount()) {
                player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.INCORRECT_COUNT));
                return true;
            }

            int boughtAmount = getBoughtAmount(player, product);
            if (boughtAmount >= product.getRestrictionAmount()) {
                player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.ALREADY_BOUGHT));
                return true;
            }

            if(boughtAmount + count > product.getRestrictionAmount()) {
                player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.INCORRECT_COUNT));
                return true;
            }

        }
        return false;
    }

    private int getBoughtAmount(Player player, L2StoreProduct product) {
        return switch (product.getRestrictionPeriod()) {
            case DAY ->  getDAO(L2StoreDAO.class).countBoughtProductToday(player.getAccountName(), product.getId());
            case MONTH -> getDAO(L2StoreDAO.class).countBoughtProductInDays(player.getAccountName(), product.getId(), 30);
            case EVER -> getDAO(L2StoreDAO.class).countBoughtProduct(player.getAccountName(), product.getId());
        };
    }

    protected int validatePaymentId(L2StoreProduct item) {
        return switch (item.getPaymentType()) {
            case 0 -> 0; // Nc Coin
            case 1 -> CommonItem.ADENA; // Adenas
            case 2 -> HERO_COINS;
            default ->  -1;
        };
    }

    protected boolean processPayment(Player player, L2StoreProduct item, int count) {
        final int price = Math.multiplyExact(item.getPrice(), count);
        final int paymentId = validatePaymentId(item);

        if (paymentId < 0) {
            player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.LACK_OF_POINT));
            return false;
        } else if (paymentId > 0) {
            if (!player.destroyItemByItemId("PrimeShop", paymentId, price, player, true)) {
                player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.LACK_OF_POINT));
                return false;
            }
        } else {
            if (player.getNCoins() < price) {
                player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.LACK_OF_POINT));
                player.sendPacket(YOU_DO_NOT_HAVE_ENOUGH_NCOIN);
                return false;
            }
            if(price > 0) {
                player.updateNCoins(-price);
                player.updateVipPoints(price);
            }
        }
        return true;
    }
}
