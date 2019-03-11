package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.actor.instance.L2PetInstance;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * This class ...
 *
 * @version $Revision: 1.3.2.1.2.5 $ $Date: 2005/03/29 23:15:33 $
 */
public final class RequestGiveItemToPet extends IClientIncomingPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestGiveItemToPet.class);
    private int _objectId;
    private long _amount;

    @Override
    public void readImpl(ByteBuffer packet) {
        _objectId = packet.getInt();
        _amount = packet.getLong();
    }

    @Override
    public void runImpl() {
        final L2PcInstance player = client.getActiveChar();
        if ((_amount <= 0) || (player == null) || !player.hasPet()) {
            return;
        }

        if (!client.getFloodProtectors().getTransaction().tryPerformAction("giveitemtopet")) {
            player.sendMessage("You are giving items to pet too fast.");
            return;
        }

        if (player.hasItemRequest()) {
            return;
        }

        // Alt game - Karma punishment
        if (!Config.ALT_GAME_KARMA_PLAYER_CAN_TRADE && (player.getReputation() < 0)) {
            return;
        }

        if (player.getPrivateStoreType() != PrivateStoreType.NONE) {
            player.sendMessage("You cannot exchange items while trading.");
            return;
        }

        final L2ItemInstance item = player.getInventory().getItemByObjectId(_objectId);
        if (item == null) {
            return;
        }

        if (_amount > item.getCount()) {
            Util.handleIllegalPlayerAction(player, getClass().getSimpleName() + ": Character " + player.getName() + " of account " + player.getAccountName() + " tried to get item with oid " + _objectId + " from pet but has invalid count " + _amount + " item count: " + item.getCount(), Config.DEFAULT_PUNISH);
            return;
        }

        if (item.isAugmented()) {
            return;
        }

        if (item.isHeroItem() || !item.isDropable() || !item.isDestroyable() || !item.isTradeable()) {
            player.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_THIS_ITEM);
            return;
        }

        final L2PetInstance pet = player.getPet();
        if (pet.isDead()) {
            player.sendPacket(SystemMessageId.YOUR_PET_IS_DEAD_AND_ANY_ATTEMPT_YOU_MAKE_TO_GIVE_IT_SOMETHING_GOES_UNRECOGNIZED);
            return;
        }

        if (!pet.getInventory().validateCapacity(item)) {
            player.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_ANY_MORE_ITEMS);
            return;
        }

        if (!pet.getInventory().validateWeight(item, _amount)) {
            player.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_ANY_MORE_ITEMS_2);
            return;
        }

        if (player.transferItem("Transfer", _objectId, _amount, pet.getInventory(), pet) == null) {
            LOGGER.warn("Invalid item transfer request: " + pet.getName() + "(pet) --> " + player.getName());
        }
    }
}
