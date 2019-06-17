package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.actor.instance.L2PetInstance;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.network.serverpackets.PetItemList;
import org.l2j.gameserver.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class ...
 *
 * @version $Revision: 1.3.4.4 $ $Date: 2005/03/29 23:15:33 $
 */
public final class RequestGetItemFromPet extends ClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestGetItemFromPet.class);
    private int _objectId;
    private long _amount;
    @SuppressWarnings("unused")
    private int _unknown;

    @Override
    public void readImpl() {
        _objectId = readInt();
        _amount = readLong();
        _unknown = readInt(); // = 0 for most trades
    }

    @Override
    public void runImpl() {
        final L2PcInstance player = client.getActiveChar();
        if ((_amount <= 0) || (player == null) || !player.hasPet()) {
            return;
        }

        if (!client.getFloodProtectors().getTransaction().tryPerformAction("getfrompet")) {
            player.sendMessage("You get items from pet too fast.");
            return;
        }

        if (player.hasItemRequest()) {
            return;
        }

        final L2PetInstance pet = player.getPet();
        final L2ItemInstance item = pet.getInventory().getItemByObjectId(_objectId);
        if (item == null) {
            return;
        }

        if (_amount > item.getCount()) {
            Util.handleIllegalPlayerAction(player, getClass().getSimpleName() + ": Character " + player.getName() + " of account " + player.getAccountName() + " tried to get item with oid " + _objectId + " from pet but has invalid count " + _amount + " item count: " + item.getCount(), Config.DEFAULT_PUNISH);
            return;
        }

        final L2ItemInstance transferedItem = pet.transferItem("Transfer", _objectId, _amount, player.getInventory(), player, pet);
        if (transferedItem != null) {
            player.sendPacket(new PetItemList(pet.getInventory().getItems()));
        } else {
            LOGGER.warn("Invalid item transfer request: " + pet.getName() + "(pet) --> " + player.getName());
        }
    }
}
