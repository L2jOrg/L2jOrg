package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.enums.MatchingRoomType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.matching.MatchingRoom;
import org.l2j.gameserver.world.World;

/**
 * @author jeremy
 */
public class RequestExOustFromMpccRoom extends ClientPacket {
    private int _objectId;

    @Override
    public void readImpl() {
        _objectId = readInt();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();

        if (activeChar == null) {
            return;
        }

        final MatchingRoom room = activeChar.getMatchingRoom();

        if ((room != null) && (room.getLeader() == activeChar) && (room.getRoomType() == MatchingRoomType.COMMAND_CHANNEL)) {
            final Player player = World.getInstance().findPlayer(_objectId);

            if (player != null) {
                room.deleteMember(player, true);
            }
        }
    }
}
