package org.l2j.gameserver.network.clientpackets.friend;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.friend.ExFriendDetailInfo;

/**
 * @author Sdw
 */
public class RequestFriendDetailInfo extends ClientPacket {
    private String _name;

    @Override
    public void readImpl() {
        _name = readString();
    }

    @Override
    public void runImpl() {
        final L2PcInstance player = client.getActiveChar();
        if (player != null) {
            client.sendPacket(new ExFriendDetailInfo(player, _name));
        }
    }
}
