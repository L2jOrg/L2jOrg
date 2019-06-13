package org.l2j.gameserver.network.clientpackets.elementalspirits;

import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2j.gameserver.network.serverpackets.UserInfo;

public class ExElementalSpiritChangeType extends IClientIncomingPacket {

    private byte element;

    @Override
    protected void readImpl() throws Exception {
        readByte(); // unk
        element = readByte(); /* 1 - Fire, 2 - Water, 3 - Wind, 4 Earth */
    }

    @Override
    protected void runImpl() {
        // TODO Change spirit element
        var userInfo = new UserInfo(client.getActiveChar());
        userInfo.addComponentType(UserInfoType.ATT_SPIRITS);
        client.sendPacket(userInfo);
    }
}
