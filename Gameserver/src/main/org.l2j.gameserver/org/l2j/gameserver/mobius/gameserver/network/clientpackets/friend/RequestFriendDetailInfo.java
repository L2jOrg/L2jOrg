package org.l2j.gameserver.mobius.gameserver.network.clientpackets.friend;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.friend.ExFriendDetailInfo;

import java.nio.ByteBuffer;

/**
 * @author Sdw
 */
public class RequestFriendDetailInfo extends IClientIncomingPacket {
    private String _name;

    @Override
    public void readImpl(ByteBuffer packet) {
        _name = readString(packet);
    }

    @Override
    public void runImpl() {
        final L2PcInstance player = client.getActiveChar();
        if (player != null) {
            client.sendPacket(new ExFriendDetailInfo(player, _name));
        }
    }
}
