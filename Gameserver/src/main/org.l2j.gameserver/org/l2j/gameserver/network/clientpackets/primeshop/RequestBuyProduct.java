package org.l2j.gameserver.network.clientpackets.primeshop;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.database.dao.PrimeShopDAO;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.items.CommonItem;
import org.l2j.gameserver.model.primeshop.PrimeShopProduct;
import org.l2j.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2j.gameserver.network.serverpackets.primeshop.ExBRBuyProduct;
import org.l2j.gameserver.network.serverpackets.primeshop.ExBRBuyProduct.ExBrProductReplyType;
import org.l2j.gameserver.util.Util;

import java.util.Calendar;

import static org.l2j.commons.database.DatabaseAccess.getDAO;

public abstract class RequestBuyProduct extends IClientIncomingPacket {

    private static final int HERO_COINS = 23805;

    protected static boolean validatePlayer(PrimeShopProduct item, int count, L2PcInstance player) {
        final long currentTime = System.currentTimeMillis() / 1000;

        if (item == null) {
            player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.INVALID_PRODUCT));
            Util.handleIllegalPlayerAction(player, "Player " + player.getName() + " tried to buy invalid brId from Prime", Config.DEFAULT_PUNISH);
            return false;
        }

        if ((count < 1) || (count > 99)) {
            Util.handleIllegalPlayerAction(player, "Player " + player.getName() + " tried to buy invalid itemcount [" + count + "] from Prime", Config.DEFAULT_PUNISH);
            player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.INCORRECT_COUNT));
            return false;
        }

        if ( (item.getMinLevel() > 0 && item.getMinLevel() > player.getLevel()) || (item.getMaxLevel() > 0 && item.getMaxLevel() < player.getLevel())) {
            player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.INVALID_LEVEL));
            return false;
        }

        if ((item.getMinBirthday() > 0 && item.getMinBirthday() > player.getBirthdays()) || (item.getMaxBirthday() > 0 && item.getMaxBirthday() < player.getBirthdays())) {
            player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.INVALID_DATE_CREATION));
            return false;
        }

        if ((Calendar.getInstance().get(Calendar.DAY_OF_WEEK) & item.getDaysOfWeek()) == 0) {
            player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.NOT_DAY_OF_WEEK));
            return false;
        }

        if ((item.getStartSale() > 1) && (item.getStartSale() > currentTime)) {
            player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.BEFORE_SALE_DATE));
            return false;
        }

        if ((item.getEndSale() > 1) && (item.getEndSale() < currentTime)) {
            player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.AFTER_SALE_DATE));
            return false;
        }

        final int weight = item.getWeight() * count;
        final long slots = item.getCount() * count;

        if (player.getInventory().validateWeight(weight)) {
            if (!player.getInventory().validateCapacity(slots)) {
                player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.INVENTORY_FULL));
                return false;
            }
        } else {
            player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.INVENTROY_OVERFLOW));
            return false;
        }

        if(item.getRestrictionDay() > 0 && getDAO(PrimeShopDAO.class).countBougthItemToday(player.getObjectId(), item.getId()) >= item.getRestrictionDay()) {
            player.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.ALREADY_BOUGHT));
            return false;
        }
        return true;
    }

    protected static int validatePaymentId(L2PcInstance player, PrimeShopProduct item, long amount) {
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

    protected boolean processPayment(L2PcInstance activeChar, PrimeShopProduct item, int count) {
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
            if (activeChar.getL2Coins() < price) {
                activeChar.sendPacket(new ExBRBuyProduct(ExBrProductReplyType.LACK_OF_POINT));
                return false;
            }
            if(price > 0) {
                activeChar.updateL2Coins(-price);
                activeChar.updateVipPoints((int) (price * 0.07));
            }
        }
        return true;
    }
}
