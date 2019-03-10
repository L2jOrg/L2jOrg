package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.enums.UserInfoType;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.UserInfo;

import java.nio.ByteBuffer;

/**
 * @author Sdw
 */
public class NotifyExitBeautyShop extends IClientIncomingPacket {
    @Override
    public void readImpl(ByteBuffer packet) {

    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        final UserInfo userInfo = new UserInfo(activeChar, false);
        userInfo.addComponentType(UserInfoType.APPAREANCE);
        client.sendPacket(userInfo);
    }
}
