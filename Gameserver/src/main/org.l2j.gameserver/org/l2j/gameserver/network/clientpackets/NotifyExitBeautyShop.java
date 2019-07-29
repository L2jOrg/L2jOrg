package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.model.actor.instance.Player;
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
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        final UserInfo userInfo = new UserInfo(activeChar, false);
        userInfo.addComponentType(UserInfoType.APPAREANCE);
        client.sendPacket(userInfo);
    }
}
