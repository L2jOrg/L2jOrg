package org.l2j.gameserver.network.clientpackets.elementalspirits;

import org.l2j.gameserver.data.elemental.ElementalSpiritManager;
import org.l2j.gameserver.data.elemental.ElementalType;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.UserInfo;
import org.l2j.gameserver.network.serverpackets.elementalspirits.ElementalSpiritExtract;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.network.SystemMessageId.CANNOT_EVOLVE_ABSORB_EXTRACT_WHILE_USING_THE_PRIVATE_STORE_WORKSHOP;
import static org.l2j.gameserver.network.SystemMessageId.EXTRACTED_S1_S2_SUCCESSFULLY;

public class ExElementalSpiritExtract extends ClientPacket {

    private byte type;

    @Override
    protected void readImpl() throws Exception {
        type = readByte();
    }

    @Override
    protected void runImpl() {
        var player = client.getActiveChar();

        if(player.getPrivateStoreType() != PrivateStoreType.NONE) {
            player.sendPacket(SystemMessage.getSystemMessage(CANNOT_EVOLVE_ABSORB_EXTRACT_WHILE_USING_THE_PRIVATE_STORE_WORKSHOP));
            return;
        }

        var spirit = player.getElementalSpirit(ElementalType.of(type));

        if(nonNull(spirit)) {
            var amount = spirit.getExtractAmount();
            var extracted = false;
            if(extracted = amount > 0) {
                player.addItem("Extract", spirit.getExtractItem(), amount, player, true);
                spirit.resetStage();

                var userInfo = new UserInfo(player);
                userInfo.addComponentType(UserInfoType.ATT_SPIRITS);
                client.sendPacket(userInfo);
                client.sendPacket(SystemMessage.getSystemMessage(EXTRACTED_S1_S2_SUCCESSFULLY).addItemName(spirit.getExtractItem()).addInt(amount));

            }
            client.sendPacket(new ElementalSpiritExtract(type, extracted));
        }
    }
}
