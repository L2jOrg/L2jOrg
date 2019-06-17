package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.serverpackets.UserInfo;

/**
 * @author Sdw
 */
public class NotifyExitBeautyShop extends ClientPacket {
    @Override
    public void readImpl() {

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
