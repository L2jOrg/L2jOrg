package org.l2j.gameserver.network.clientpackets.elementalspirits;

import org.l2j.gameserver.data.elemental.ElementalSpirit;
import org.l2j.gameserver.data.elemental.ElementalType;
import org.l2j.gameserver.enums.InventoryBlockType;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.UserInfo;
import org.l2j.gameserver.network.serverpackets.elementalspirits.ElementalSpiritEvolution;

import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.network.SystemMessageId.CANNOT_EVOLVE_ABSORB_EXTRACT_WHILE_USING_THE_PRIVATE_STORE_WORKSHOP;
import static org.l2j.gameserver.network.SystemMessageId.S1_EVOLVED_TO_S2_STAR;

public class ExElementalSpiritEvolution extends ClientPacket {

    private byte type;

    @Override
    protected void readImpl() throws Exception {
        type = readByte();
    }

    @Override
    protected void runImpl()  {
        var player = client.getActiveChar();

        if(player.getPrivateStoreType() != PrivateStoreType.NONE) {
            player.sendPacket(SystemMessage.getSystemMessage(CANNOT_EVOLVE_ABSORB_EXTRACT_WHILE_USING_THE_PRIVATE_STORE_WORKSHOP));
            return;
        }

        var spirit = player.getElementalSpirit(ElementalType.of(type));

        var canEvolve = nonNull(spirit) && spirit.canEvolve() && consumeEvolveItems(player, spirit);

        if(canEvolve) {
            spirit.upgrade();
            var userInfo = new UserInfo(player);
            userInfo.addComponentType(UserInfoType.ATT_SPIRITS);
            client.sendPacket(userInfo);
            client.sendPacket(SystemMessage.getSystemMessage(S1_EVOLVED_TO_S2_STAR).addElementalSpirit(type).addInt(spirit.getStage()));
        }

        player.sendPacket(new ElementalSpiritEvolution(type, canEvolve));

    }

    private boolean consumeEvolveItems(L2PcInstance player, ElementalSpirit spirit) {
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
