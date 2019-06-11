package org.l2j.gameserver.network.clientpackets.compound;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.data.xml.impl.CombinationItemsData;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.actor.request.CompoundRequest;
import org.l2j.gameserver.model.items.combination.CombinationItem;
import org.l2j.gameserver.model.items.combination.CombinationItemReward;
import org.l2j.gameserver.model.items.combination.CombinationItemType;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.network.serverpackets.compound.ExEnchantFail;
import org.l2j.gameserver.network.serverpackets.compound.ExEnchantOneFail;
import org.l2j.gameserver.network.serverpackets.compound.ExEnchantSucess;

import java.nio.ByteBuffer;

/**
 * @author UnAfraid
 */
public class RequestNewEnchantTry extends IClientIncomingPacket {
    @Override
    public void readImpl() {
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        } else if (activeChar.isInStoreMode()) {
            client.sendPacket(SystemMessageId.YOU_CANNOT_DO_THAT_WHILE_IN_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
            client.sendPacket(ExEnchantOneFail.STATIC_PACKET);
            return;
        } else if (activeChar.isProcessingTransaction() || activeChar.isProcessingRequest()) {
            client.sendPacket(SystemMessageId.YOU_CANNOT_USE_THIS_SYSTEM_DURING_TRADING_PRIVATE_STORE_AND_WORKSHOP_SETUP);
            client.sendPacket(ExEnchantOneFail.STATIC_PACKET);
            return;
        }

        final CompoundRequest request = activeChar.getRequest(CompoundRequest.class);
        if ((request == null) || request.isProcessing()) {
            client.sendPacket(ExEnchantFail.STATIC_PACKET);
            return;
        }

        request.setProcessing(true);

        final L2ItemInstance itemOne = request.getItemOne();
        final L2ItemInstance itemTwo = request.getItemTwo();
        if ((itemOne == null) || (itemTwo == null)) {
            client.sendPacket(ExEnchantFail.STATIC_PACKET);
            activeChar.removeRequest(request.getClass());
            return;
        }

        // Lets prevent using same item twice
        if (itemOne.getObjectId() == itemTwo.getObjectId()) {
            client.sendPacket(new ExEnchantFail(itemOne.getId(), itemTwo.getId()));
            activeChar.removeRequest(request.getClass());
            return;
        }

        final CombinationItem combinationItem = CombinationItemsData.getInstance().getItemsBySlots(itemOne.getId(), itemTwo.getId());

        // Not implemented or not able to merge!
        if (combinationItem == null) {
            client.sendPacket(new ExEnchantFail(itemOne.getId(), itemTwo.getId()));
            activeChar.removeRequest(request.getClass());
            return;
        }

        final InventoryUpdate iu = new InventoryUpdate();
        iu.addRemovedItem(itemOne);
        iu.addRemovedItem(itemTwo);

        if (activeChar.destroyItem("Compound-Item-One", itemOne, 1, null, true) && activeChar.destroyItem("Compound-Item-Two", itemTwo, 1, null, true)) {
            final double random = (Rnd.nextDouble() * 100);
            final boolean success = random <= combinationItem.getChance();
            final CombinationItemReward rewardItem = combinationItem.getReward(success ? CombinationItemType.ON_SUCCESS : CombinationItemType.ON_FAILURE);
            final L2ItemInstance item = activeChar.addItem("Compound-Result", rewardItem.getId(), rewardItem.getCount(), null, true);

            if (success) {
                client.sendPacket(new ExEnchantSucess(item.getId()));
            } else {
                client.sendPacket(new ExEnchantFail(itemOne.getId(), itemTwo.getId()));
            }
        }

        activeChar.sendInventoryUpdate(iu);
        activeChar.removeRequest(request.getClass());
    }
}
