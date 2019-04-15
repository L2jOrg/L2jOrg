package org.l2j.gameserver.network.clientpackets.primeshop;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.sql.impl.CharNameTable;
import org.l2j.gameserver.data.xml.impl.PrimeShopData;
import org.l2j.gameserver.enums.MailType;
import org.l2j.gameserver.instancemanager.MailManager;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.actor.request.PrimeShopRequest;
import org.l2j.gameserver.model.entity.Message;
import org.l2j.gameserver.model.itemcontainer.Inventory;
import org.l2j.gameserver.model.itemcontainer.Mail;
import org.l2j.gameserver.model.primeshop.PrimeShopProduct;
import org.l2j.gameserver.model.primeshop.PrimeShopItem;
import org.l2j.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2j.gameserver.network.serverpackets.primeshop.ExBRBuyProduct;
import org.l2j.gameserver.network.serverpackets.primeshop.ExBRGamePoint;
import org.l2j.gameserver.util.Util;

import java.nio.ByteBuffer;
import java.util.Calendar;

/**
 * @author Gnacik, UnAfraid
 */
public final class RequestBRPresentBuyProduct extends IClientIncomingPacket {
    private static final int HERO_COINS = 23805;

    private int _brId;
    private int _count;
    private String _charName;
    private String _mailTitle;
    private String _mailBody;

    /**
     * @param item
     * @param count
     * @param player
     * @return
     */
    private static boolean validatePlayer(PrimeShopProduct item, int count, L2PcInstance player) {
        final long currentTime = System.currentTimeMillis() / 1000;
        if (item == null) {
            player.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.INVALID_PRODUCT));
            Util.handleIllegalPlayerAction(player, "Player " + player.getName() + " tried to buy invalid brId from Prime", Config.DEFAULT_PUNISH);
            return false;
        } else if ((count < 1) || (count > 99)) {
            Util.handleIllegalPlayerAction(player, "Player " + player.getName() + " tried to buy invalid itemcount [" + count + "] from Prime", Config.DEFAULT_PUNISH);
            player.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.INVALID_USER_STATE));
            return false;
        } else if ((item.getMinLevel() > 0) && (item.getMinLevel() > player.getLevel())) {
            player.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.INVALID_USER));
            return false;
        } else if ((item.getMaxLevel() > 0) && (item.getMaxLevel() < player.getLevel())) {
            player.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.INVALID_USER));
            return false;
        } else if ((item.getMinBirthday() > 0) && (item.getMinBirthday() > player.getBirthdays())) {
            player.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.INVALID_USER_STATE));
            return false;
        } else if ((item.getMaxBirthday() > 0) && (item.getMaxBirthday() < player.getBirthdays())) {
            player.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.INVALID_USER_STATE));
            return false;
        } else if ((Calendar.getInstance().get(Calendar.DAY_OF_WEEK) & item.getDaysOfWeek()) == 0) {
            player.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.NOT_DAY_OF_WEEK));
            return false;
        } else if ((item.getStartSale() > 1) && (item.getStartSale() > currentTime)) {
            player.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.BEFORE_SALE_DATE));
            return false;
        } else if ((item.getEndSale() > 1) && (item.getEndSale() < currentTime)) {
            player.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.AFTER_SALE_DATE));
            return false;
        }

        final int weight = item.getWeight() * count;
        final long slots = item.getCount() * count;

        if (player.getInventory().validateWeight(weight)) {
            if (!player.getInventory().validateCapacity(slots)) {
                player.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.INVENTROY_OVERFLOW));
                return false;
            }
        } else {
            player.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.INVENTROY_OVERFLOW));
            return false;
        }

        return true;
    }

    private static int validatePaymentId(L2PcInstance player, PrimeShopProduct item, long amount) {
        switch (item.getPaymentType()) {
            case 0: // Prime points
            {
                return 0;
            }
            case 1: // Adenas
            {
                return Inventory.ADENA_ID;
            }
            case 2: // Hero coins
            {
                return HERO_COINS;
            }
        }

        return -1;
    }

    @Override
    public void readImpl(ByteBuffer packet) {
        _brId = packet.getInt();
        _count = packet.getInt();
        _charName = readString(packet);
        _mailTitle = readString(packet);
        _mailBody = readString(packet);
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        final int receiverId = CharNameTable.getInstance().getIdByName(_charName);
        if (receiverId <= 0) {
            activeChar.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.INVALID_USER));
            return;
        }

        if (activeChar.hasItemRequest() || activeChar.hasRequest(PrimeShopRequest.class)) {
            activeChar.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.INVALID_USER_STATE));
            return;
        }

        activeChar.addRequest(new PrimeShopRequest(activeChar));

        final PrimeShopProduct item = PrimeShopData.getInstance().getItem(_brId);
        if (validatePlayer(item, _count, activeChar)) {
            final int price = (item.getPrice() * _count);
            final int paymentId = validatePaymentId(activeChar, item, price);

            if (paymentId < 0) {
                activeChar.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.LACK_OF_POINT));
                activeChar.removeRequest(PrimeShopRequest.class);
                return;
            } else if (paymentId > 0) {
                if (!activeChar.destroyItemByItemId("PrimeShop-" + item.getId(), paymentId, price, activeChar, true)) {
                    activeChar.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.LACK_OF_POINT));
                    activeChar.removeRequest(PrimeShopRequest.class);
                    return;
                }
            } else if (paymentId == 0) {
                if (activeChar.getPrimePoints() < price) {
                    activeChar.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.LACK_OF_POINT));
                    activeChar.removeRequest(PrimeShopRequest.class);
                    return;
                }
                activeChar.setPrimePoints(activeChar.getPrimePoints() - price);
            }

            activeChar.sendPacket(new ExBRBuyProduct(ExBRBuyProduct.ExBrProductReplyType.SUCCESS));
            activeChar.sendPacket(new ExBRGamePoint(activeChar));

            final Message mail = new Message(receiverId, _mailTitle, _mailBody, MailType.PRIME_SHOP_GIFT);

            final Mail attachement = mail.createAttachments();
            for (PrimeShopItem subItem : item.getItems()) {
                attachement.addItem("Prime Shop Gift", subItem.getId(), subItem.getCount(), activeChar, this);
            }
            MailManager.getInstance().sendMessage(mail);
        }

        activeChar.removeRequest(PrimeShopRequest.class);
    }
}
