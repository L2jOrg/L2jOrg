package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.PremiumItem;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExGetPremiumItemList;
import org.l2j.gameserver.util.GameUtils;

/**
 * @author Gnacik
 */
public final class RequestWithDrawPremiumItem extends ClientPacket {
    private int _itemNum;
    private int _charId;
    private long _itemCount;

    @Override
    public void readImpl() {
        _itemNum = readInt();
        _charId = readInt();
        _itemCount = readLong();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();

        if (activeChar == null) {
            return;
        } else if (_itemCount <= 0) {
            return;
        } else if (activeChar.getObjectId() != _charId) {
            GameUtils.handleIllegalPlayerAction(activeChar, "[RequestWithDrawPremiumItem] Incorrect owner, Player: " + activeChar.getName());
            return;
        } else if (activeChar.getPremiumItemList().isEmpty()) {
            GameUtils.handleIllegalPlayerAction(activeChar, "[RequestWithDrawPremiumItem] Player: " + activeChar.getName() + " try to get item with empty list!");
            return;
        } else if ((activeChar.getWeightPenalty() >= 3) || !activeChar.isInventoryUnder90(false)) {
            client.sendPacket(SystemMessageId.YOU_CANNOT_RECEIVE_THE_DIMENSIONAL_ITEM_BECAUSE_YOU_HAVE_EXCEED_YOUR_INVENTORY_WEIGHT_QUANTITY_LIMIT);
            return;
        } else if (activeChar.isProcessingTransaction()) {
            client.sendPacket(SystemMessageId.YOU_CANNOT_RECEIVE_A_DIMENSIONAL_ITEM_DURING_AN_EXCHANGE);
            return;
        }

        final PremiumItem _item = activeChar.getPremiumItemList().get(_itemNum);
        if (_item == null) {
            return;
        } else if (_item.getCount() < _itemCount) {
            return;
        }

        final long itemsLeft = (_item.getCount() - _itemCount);

        activeChar.addItem("PremiumItem", _item.getItemId(), _itemCount, activeChar.getTarget(), true);

        if (itemsLeft > 0) {
            _item.updateCount(itemsLeft);
            activeChar.updatePremiumItem(_itemNum, itemsLeft);
        } else {
            activeChar.getPremiumItemList().remove(_itemNum);
            activeChar.deletePremiumItem(_itemNum);
        }

        if (activeChar.getPremiumItemList().isEmpty()) {
            client.sendPacket(SystemMessageId.THERE_ARE_NO_MORE_DIMENSIONAL_ITEMS_TO_BE_FOUND);
        } else {
            client.sendPacket(new ExGetPremiumItemList(activeChar));
        }
    }
}
