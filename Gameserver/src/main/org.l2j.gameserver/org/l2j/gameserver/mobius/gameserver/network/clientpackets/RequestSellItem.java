package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.Config;
import org.l2j.gameserver.mobius.gameserver.data.xml.impl.BuyListData;
import org.l2j.gameserver.mobius.gameserver.enums.TaxType;
import org.l2j.gameserver.mobius.gameserver.model.L2Object;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2MerchantInstance;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.buylist.ProductList;
import org.l2j.gameserver.mobius.gameserver.model.holders.UniqueItemHolder;
import org.l2j.gameserver.mobius.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.mobius.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ExBuySellList;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ExUserInfoInvenWeight;
import org.l2j.gameserver.mobius.gameserver.util.Util;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.l2j.gameserver.mobius.gameserver.model.actor.L2Npc.INTERACTION_DISTANCE;
import static org.l2j.gameserver.mobius.gameserver.model.itemcontainer.Inventory.MAX_ADENA;


/**
 * RequestSellItem client packet class.
 */
public final class RequestSellItem extends IClientIncomingPacket {
    private static final int BATCH_LENGTH = 16;
    private static final int CUSTOM_CB_SELL_LIST = 423;

    private int _listId;
    private List<UniqueItemHolder> _items = null;

    @Override
    public void readImpl(ByteBuffer packet) throws InvalidDataPacketException {
        _listId = packet.getInt();
        final int size = packet.getInt();
        if ((size <= 0) || (size > Config.MAX_ITEM_IN_PACKET) || ((size * BATCH_LENGTH) != packet.remaining())) {
            throw new InvalidDataPacketException();
        }

        _items = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            final int objectId = packet.getInt();
            final int itemId = packet.getInt();
            final long count = packet.getLong();
            if ((objectId < 1) || (itemId < 1) || (count < 1)) {
                _items = null;
                throw new InvalidDataPacketException();
            }
            _items.add(new UniqueItemHolder(itemId, objectId, count));
        }
    }

    @Override
    public void runImpl() {
        final L2PcInstance player = client.getActiveChar();
        if (player == null) {
            return;
        }

        if (!client.getFloodProtectors().getTransaction().tryPerformAction("buy")) {
            player.sendMessage("You are buying too fast.");
            return;
        }

        if (_items == null) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        // Alt game - Karma punishment
        if (!Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP && (player.getReputation() < 0)) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        final L2Object target = player.getTarget();
        L2MerchantInstance merchant = null;
        if (!player.isGM() && (_listId != CUSTOM_CB_SELL_LIST)) {
            if ((target == null) || !player.isInsideRadius3D(target, INTERACTION_DISTANCE) || (player.getInstanceId() != target.getInstanceId())) {
                client.sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }
            if (target instanceof L2MerchantInstance) {
                merchant = (L2MerchantInstance) target;
            } else {
                client.sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }
        }

        if ((merchant == null) && !player.isGM()) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        final ProductList buyList = BuyListData.getInstance().getBuyList(_listId);
        if (buyList == null) {
            Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " sent a false BuyList list_id " + _listId, Config.DEFAULT_PUNISH);
            return;
        }

        if ((merchant != null) && !buyList.isNpcAllowed(merchant.getId())) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        long totalPrice = 0;
        // Proceed the sell
        for (UniqueItemHolder i : _items) {
            L2ItemInstance item = player.checkItemManipulation(i.getObjectId(), i.getCount(), "sell");
            if ((item == null) || (!item.isSellable())) {
                continue;
            }

            long price = item.getReferencePrice() / 2;
            totalPrice += price * i.getCount();
            if (((MAX_ADENA / i.getCount()) < price) || (totalPrice > MAX_ADENA)) {
                Util.handleIllegalPlayerAction(player, "Warning!! Character " + player.getName() + " of account " + player.getAccountName() + " tried to purchase over " + MAX_ADENA + " adena worth of goods.", Config.DEFAULT_PUNISH);
                return;
            }

            if (Config.ALLOW_REFUND) {
                player.getInventory().transferItem("Sell", i.getObjectId(), i.getCount(), player.getRefund(), player, merchant);
            } else {
                player.getInventory().destroyItem("Sell", i.getObjectId(), i.getCount(), player, merchant);
            }
        }

        // add to castle treasury
        if (merchant != null) {
            // Keep here same formula as in {@link ExBuySellList} to produce same result.
            final long profit = (long) (totalPrice * (1.0 - merchant.getCastleTaxRate(TaxType.SELL)));
            merchant.handleTaxPayment(totalPrice - profit);
            totalPrice = profit;
        }

        player.addAdena("Sell", totalPrice, merchant, false);

        // Update current load as well
        client.sendPacket(new ExUserInfoInvenWeight(player));
        client.sendPacket(new ExBuySellList(player, true));
    }
}
