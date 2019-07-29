package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.enums.MatchingRoomType;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.matching.MatchingRoom;
import org.l2j.gameserver.network.serverpackets.ExMPCCPartymasterList;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Sdw
 */
public class RequestExMpccPartymasterList extends ClientPacket {
    @Override
    public void readImpl() {

    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        final MatchingRoom room = activeChar.getMatchingRoom();
        if ((room != null) && (room.getRoomType() == MatchingRoomType.COMMAND_CHANNEL)) {
            final Set<String> leadersName = room.getMembers().stream().map(Player::getParty).filter(Objects::nonNull).map(Party::getLeader).map(Player::getName).collect(Collectors.toSet());
            activeChar.sendPacket(new ExMPCCPartymasterList(leadersName));
        }
    }
}
