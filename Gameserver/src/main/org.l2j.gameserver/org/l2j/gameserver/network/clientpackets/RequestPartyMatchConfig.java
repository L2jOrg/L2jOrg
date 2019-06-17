package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.enums.PartyMatchingRoomLevelType;
import org.l2j.gameserver.instancemanager.MatchingRoomManager;
import org.l2j.gameserver.model.L2CommandChannel;
import org.l2j.gameserver.model.L2Party;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.matching.CommandChannelMatchingRoom;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ListPartyWaiting;

public final class RequestPartyMatchConfig extends ClientPacket {
    private int _page;
    private int _location;
    private PartyMatchingRoomLevelType _type;

    @Override
    public void readImpl() {
        _page = readInt();
        _location = readInt();
        _type = readInt() == 0 ? PartyMatchingRoomLevelType.MY_LEVEL_RANGE : PartyMatchingRoomLevelType.ALL;
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();

        if (activeChar == null) {
            return;
        }

        final L2Party party = activeChar.getParty();
        final L2CommandChannel cc = party == null ? null : party.getCommandChannel();

        if ((party != null) && (cc != null) && (cc.getLeader() == activeChar)) {
            if (activeChar.getMatchingRoom() == null) {
                activeChar.setMatchingRoom(new CommandChannelMatchingRoom(activeChar.getName(), party.getDistributionType().ordinal(), 1, activeChar.getLevel(), 50, activeChar));
            }
        } else if ((cc != null) && (cc.getLeader() != activeChar)) {
            activeChar.sendPacket(SystemMessageId.THE_COMMAND_CHANNEL_AFFILIATED_PARTY_S_PARTY_MEMBER_CANNOT_USE_THE_MATCHING_SCREEN);
        } else if ((party != null) && (party.getLeader() != activeChar)) {
            activeChar.sendPacket(SystemMessageId.THE_LIST_OF_PARTY_ROOMS_CAN_ONLY_BE_VIEWED_BY_A_PERSON_WHO_IS_NOT_PART_OF_A_PARTY);
        } else {
            MatchingRoomManager.getInstance().addToWaitingList(activeChar);
            activeChar.sendPacket(new ListPartyWaiting(_type, _location, _page, activeChar.getLevel()));
        }
    }
}
