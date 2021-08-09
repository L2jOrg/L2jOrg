package org.l2j.gameserver.network.clientpackets.bless;

import io.github.joealisson.primitive.IntSet;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.enums.InventoryBlockType;
import org.l2j.gameserver.enums.ItemGrade;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.request.BlessItemRequest;
import org.l2j.gameserver.model.actor.request.CompoundRequest;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.network.serverpackets.bless.ExBlessOptionEnchant;
import org.l2j.gameserver.network.serverpackets.compound.ExEnchantFail;
import org.l2j.gameserver.network.serverpackets.compound.ExEnchantOneFail;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.util.GameUtils;

import java.util.Set;

import static java.util.Objects.isNull;

public class RequestBlessOptionEnchant extends ClientPacket {

//    private static final Set<ItemGrade> ALLOWED_GRADES = Set.of(
//            ItemGrade.C, ItemGrade.B, ItemGrade.A, ItemGrade.S
//    );

    private int _objectId;

    @Override
    protected void readImpl() throws Exception {
        _objectId = readInt();
    }

    @Override
    protected void runImpl() throws Exception {
        final Player activeChar = client.getPlayer();

        if (activeChar == null) {
            client.sendPacket(new ExBlessOptionEnchant(false));
            return;
        } else if (activeChar.isInStoreMode()) {
            client.sendPacket(SystemMessageId.YOU_CANNOT_DO_THAT_WHILE_IN_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
            client.sendPacket(new ExBlessOptionEnchant(false));
            return;
        } else if (activeChar.isProcessingTransaction() || activeChar.isProcessingRequest()) {
            client.sendPacket(SystemMessageId.YOU_CANNOT_USE_THIS_SYSTEM_DURING_TRADING_PRIVATE_STORE_AND_WORKSHOP_SETUP);
            client.sendPacket(new ExBlessOptionEnchant(false));
            return;
        }

        final BlessItemRequest request = activeChar.getRequest(BlessItemRequest.class);
        if ((request == null) || request.isProcessing()) {
            client.sendPacket(new ExBlessOptionEnchant(false));
            return;
        }

        if (isNull(request.getItem())) {
            client.sendPacket(new ExBlessOptionEnchant(false));
            return;
        }

        if (isNull(request.getScroll())) {
            client.sendPacket(new ExBlessOptionEnchant(false));
            return;
        }

        if (request.getItem().getOwnerId() != activeChar.getObjectId()) {
            client.sendPacket(new ExBlessOptionEnchant(false));
            return;
        }

//        if (!ALLOWED_GRADES.contains(request.getItem().getTemplate().getItemGrade())) {
//            client.sendPacket(new ExBlessOptionEnchant(false));
//            return;
//        }

        if (request.getItem().getTemplate().isEnchantBless()) {
            client.sendPacket(new ExBlessOptionEnchant(false));
            return;
        }

        request.setProcessing(true);

        final var inventory = activeChar.getInventory();
        inventory.setInventoryBlock(IntSet.of(request.getItem().getId()), InventoryBlockType.BLACKLIST);
        InventoryUpdate inventoryUpdate = new InventoryUpdate();

        if (isNull(activeChar.getInventory().destroyItem("Bless", request.getScroll().getObjectId(), 1, activeChar, request.getScroll()))) {
            activeChar.sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT);
            GameUtils.handleIllegalPlayerAction(activeChar, activeChar + " tried to bless with a scroll he doesn't have");
            client.sendPacket(new ExBlessOptionEnchant(false));
            request.setProcessing(false);
            activeChar.sendInventoryUpdate(inventoryUpdate);
            inventory.unblock();
            return;
        }

        boolean isSuccess = Rnd.chance( GeneralSettings.getBlessItemChance());

        var scroll = activeChar.getInventory().getItemByObjectId(request.getScroll().getObjectId());

        if (!isNull(scroll)) {
            inventoryUpdate.addModifiedItem(scroll);
        }else{
            inventoryUpdate.addRemovedItem(request.getScroll());
        }

        if (!isSuccess) {
            client.sendPacket(new ExBlessOptionEnchant(false));
            request.setProcessing(false);
            activeChar.sendInventoryUpdate(inventoryUpdate);
            inventory.unblock();
            return;
        }

        request.getItem().setIsBlessed(1);

        if (request.getItem().isEquipped()) {
            request.getItem().applyBlessedOptions(request.getItem().getEnchantLevel());
        }

        request.setProcessing(false);
        client.sendPacket(new ExBlessOptionEnchant(true));

        inventoryUpdate.addModifiedItem(request.getItem());
        activeChar.sendInventoryUpdate(inventoryUpdate);
        inventory.unblock();
    }
}
