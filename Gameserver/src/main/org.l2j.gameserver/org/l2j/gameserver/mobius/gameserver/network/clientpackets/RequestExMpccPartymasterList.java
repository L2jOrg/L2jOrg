package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.enums.MatchingRoomType;
import org.l2j.gameserver.mobius.gameserver.model.L2Party;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.matching.MatchingRoom;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ExMPCCPartymasterList;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Sdw
 */
public class RequestExMpccPartymasterList extends IClientIncomingPacket {
    @Override
    public void readImpl(ByteBuffer packet) {

    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        final MatchingRoom room = activeChar.getMatchingRoom();
        if ((room != null) && (room.getRoomType() == MatchingRoomType.COMMAND_CHANNEL)) {
            final Set<String> leadersName = room.getMembers().stream().map(L2PcInstance::getParty).filter(Objects::nonNull).map(L2Party::getLeader).map(L2PcInstance::getName).collect(Collectors.toSet());
            activeChar.sendPacket(new ExMPCCPartymasterList(leadersName));
        }
    }
}
