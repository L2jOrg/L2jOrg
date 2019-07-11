package org.l2j.gameserver.network.clientpackets.elementalspirits;

import org.l2j.gameserver.data.elemental.ElementalSpirit;
import org.l2j.gameserver.data.elemental.ElementalType;
import org.l2j.gameserver.enums.InventoryBlockType;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.UserInfo;
import org.l2j.gameserver.network.serverpackets.elementalspirits.ElementalSpiritEvolution;

import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static org.l2j.gameserver.network.SystemMessageId.*;

public class ExElementalSpiritEvolution extends ClientPacket {

    private byte type;

    @Override
    protected void readImpl() throws Exception {
        type = readByte();
    }

    @Override
    protected void runImpl()  {
        var player = client.getActiveChar();
        var spirit = player.getElementalSpirit(ElementalType.of(type));

        if(isNull(spirit)) {
            client.sendPacket(NO_SPIRITS_ARE_AVAILABLE);
            return;
        }

        var canEvolve = checkConditions(player, spirit);

        if(canEvolve) {
            spirit.upgrade();
            client.sendPacket(SystemMessage.getSystemMessage(S1_EVOLVED_TO_S2_STAR).addElementalSpirit(type).addInt(spirit.getStage()));
            var userInfo = new UserInfo(player);
            userInfo.addComponentType(UserInfoType.ATT_SPIRITS);
            client.sendPacket(userInfo);
        }
         client.sendPacket(new ElementalSpiritEvolution(type, canEvolve));
    }

    private boolean checkConditions(Player player, ElementalSpirit spirit) {
        var noMeetConditions = false;
        if(noMeetConditions = player.getPrivateStoreType() != PrivateStoreType.NONE) {
            client.sendPacket(CANNOT_EVOLVE_ABSORB_EXTRACT_WHILE_USING_THE_PRIVATE_STORE_WORKSHOP);
        } else if(noMeetConditions = player.isInBattle()) {
            client.sendPacket(UNABLE_TO_EVOLVE_DURING_BATTLE);
        } else if(noMeetConditions = !spirit.canEvolve()) {
            client.sendPacket(THIS_SPIRIT_CANNOT_EVOLVE);
        } else if(noMeetConditions = !consumeEvolveItems(player, spirit)) {
            client.sendPacket(NOT_ENOUGH_INGREDIENTS_FOR_EVOLUTION);
        }
        return !noMeetConditions;
    }

    private boolean consumeEvolveItems(Player player, ElementalSpirit spirit) {
        var inventory = player.getInventory();
        try {
            inventory.setInventoryBlock(spirit.getItemsToEvolve().stream().map(ItemHolder::getId).collect(Collectors.toList()), InventoryBlockType.BLACKLIST);
            for (ItemHolder itemHolder : spirit.getItemsToEvolve()) {
                if(inventory.getInventoryItemCount(itemHolder.getId(), -1) < itemHolder.getCount()) {
                    return false;
                }
            }

            for (ItemHolder itemHolder : spirit.getItemsToEvolve()) {
                player.destroyItemByItemId("Evolve", itemHolder.getId(), itemHolder.getCount(), player, true);
            }
            return true;
        } finally {
            inventory.unblock();
        }
    }

}
