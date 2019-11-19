package org.l2j.gameserver.network.clientpackets.elementalspirits;

import org.l2j.gameserver.api.elemental.ElementalSpirit;
import org.l2j.gameserver.engine.elemental.ElementalSpiritEngine;
import org.l2j.gameserver.api.elemental.ElementalType;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.UserInfo;
import org.l2j.gameserver.network.serverpackets.elementalspirits.ElementalSpiritExtract;

import static java.util.Objects.isNull;
import static org.l2j.gameserver.network.SystemMessageId.*;

public class ExElementalSpiritExtract extends ClientPacket {

    private byte type;

    @Override
    protected void readImpl() throws Exception {
        type = readByte();
    }

    @Override
    protected void runImpl() {
        var player = client.getPlayer();
        var spirit = player.getElementalSpirit(ElementalType.of(type));

        if(isNull(spirit)) {
            client.sendPacket(NO_SPIRITS_ARE_AVAILABLE);
            return;
        }

        var canExtract = checkConditions(player, spirit);

        if(canExtract) {
            var amount = spirit.getExtractAmount();
            client.sendPacket(SystemMessage.getSystemMessage(EXTRACTED_S1_S2_SUCCESSFULLY).addItemName(spirit.getExtractItem()).addInt(amount));
            spirit.resetLevel();
            player.addItem("Extract", spirit.getExtractItem(), amount, player, true);

            var userInfo = new UserInfo(player);
            userInfo.addComponentType(UserInfoType.SPIRITS);
            client.sendPacket(userInfo);
        }

        client.sendPacket(new ElementalSpiritExtract(type, canExtract));
    }

    private boolean checkConditions(Player player, ElementalSpirit spirit) {
        var noMeetConditions = false;

        if(noMeetConditions = spirit.getExtractAmount() < 1) {
            client.sendPacket(YOU_DO_NOT_HAVE_ENOUGH_SKILL_XP_TO_EXTRACT);
        } else if(noMeetConditions = !player.getInventory().validateCapacity(1)) {
            client.sendPacket(INVENTORY_IS_FULL_CANNOT_EXTRACT);
        } else if(noMeetConditions = player.getPrivateStoreType() != PrivateStoreType.NONE) {
            client.sendPacket(CANNOT_EVOLVE_ABSORB_EXTRACT_WHILE_USING_THE_PRIVATE_STORE_WORKSHOP);
        } else if(noMeetConditions = player.isInBattle()) {
            client.sendPacket(CANNOT_EVOLVE_DURING_BATTLE);
        } else if(noMeetConditions = !player.reduceAdena("Extract", ElementalSpiritEngine.EXTRACT_FEE,  player, true)) {
            client.sendPacket(YOU_DO_NOT_HAVE_THE_MATERIALS_REQUIRED_TO_EXTRACT);
        }
        return !noMeetConditions;
    }
}
