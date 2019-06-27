package org.l2j.gameserver.network.clientpackets.elementalspirits;

import org.l2j.gameserver.data.elemental.ElementalSpiritManager;
import org.l2j.gameserver.data.elemental.ElementalType;
import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.UserInfo;
import org.l2j.gameserver.network.serverpackets.elementalspirits.ElementalSpiritExtract;

import static java.util.Objects.nonNull;

public class ExElementalSpiritExtract extends ClientPacket {

    private byte type;

    @Override
    protected void readImpl() throws Exception {
        type = readByte();
    }

    @Override
    protected void runImpl() {
        var player = client.getActiveChar();
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
            }
            client.sendPacket(new ElementalSpiritExtract(type, extracted));
        }
    }
}
