package org.l2j.gameserver.network.clientpackets.elementalspirits;

import org.l2j.gameserver.data.elemental.ElementalType;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.UserInfo;
import org.l2j.gameserver.network.serverpackets.elementalspirits.ElementalSpiritAbsorb;

import static java.util.Objects.isNull;
import static org.l2j.gameserver.network.SystemMessageId.*;

public class ExElementalSpiritAbsorb extends ClientPacket {

    private byte type;
    private int itemId;
    private int amount;

    @Override
    protected void readImpl() throws Exception {
        type = readByte();
        readInt(); //items for now is always 1
        itemId = readInt();
        amount = readInt();

    }

    @Override
    protected void runImpl()  {
        var player = client.getActiveChar();

        if(player.getPrivateStoreType() != PrivateStoreType.NONE) {
            player.sendPacket(SystemMessage.getSystemMessage(CANNOT_EVOLVE_ABSORB_EXTRACT_WHILE_USING_THE_PRIVATE_STORE_WORKSHOP));
            return;
        }

        var spirit = player.getElementalSpirit(ElementalType.of(type));

        if(isNull(spirit)) {
            player.sendPacket(SystemMessage.getSystemMessage(NO_SPIRITS_ARE_AVAILABLE));
            return;
        }

        var absorbItem = spirit.getAbsorbItem(itemId);

        if(amount < 1 || amount > 999999 || isNull(absorbItem) || !player.destroyItemByItemId("Absorb", itemId, amount, player, true)) {
            player.sendPacket(new ElementalSpiritAbsorb(type, false));
            return;
        }

        var currentLevel = spirit.getLevel();
        spirit.addExperience(absorbItem.getExperience() * amount);

        player.sendPacket(new ElementalSpiritAbsorb(type, true));
        player.sendPacket(SystemMessage.getSystemMessage(SUCCESFUL_ABSORPTION));
        if(currentLevel != spirit.getLevel()) {
            var userInfo = new UserInfo(player);
            userInfo.addComponentType(UserInfoType.ATT_SPIRITS);
            client.sendPacket(userInfo);
        }

    }
}
