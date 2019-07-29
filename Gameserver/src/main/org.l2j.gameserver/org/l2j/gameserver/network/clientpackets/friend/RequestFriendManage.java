package org.l2j.gameserver.network.clientpackets.friend;

import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class RequestFriendManage extends ClientPacket {

    @Override
    protected void runImpl() throws Exception {
        /*final Player player = client.getPlayer();
        if (player != null) {
            client.sendPacket(new ExFriendDetailInfo(player, _name));
        }*/
    }

    @Override
    protected void readImpl() throws Exception {

    }
}
