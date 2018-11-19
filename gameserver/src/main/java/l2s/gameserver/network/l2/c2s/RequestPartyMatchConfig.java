package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.instancemanager.MatchingRoomManager;
import l2s.gameserver.model.CommandChannel;
import l2s.gameserver.model.Party;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.matching.CCMatchingRoom;
import l2s.gameserver.model.matching.MatchingRoom;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ListPartyWaitingPacket;

public class RequestPartyMatchConfig extends L2GameClientPacket
{
	private int _page;
	private int _region;
	private int _allLevels;

	@Override
	protected void readImpl()
	{
		_page = readD();
		_region = readD();
		_allLevels = readD();
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if(player == null)
			return;

		Party party = player.getParty();
		CommandChannel channel = party != null ? party.getCommandChannel() : null;

		if(channel != null && channel.getChannelLeader() == player)
		{
			if(channel.getMatchingRoom() == null)
			{
				CCMatchingRoom room = new CCMatchingRoom(player, 1, player.getLevel(), 50, party.getLootDistribution(), player.getName());
				channel.setMatchingRoom(room);
			}
		}
		else if(channel != null && !channel.getParties().contains(party))
			player.sendPacket(SystemMsg.THE_COMMAND_CHANNEL_AFFILIATED_PARTYS_PARTY_MEMBER_CANNOT_USE_THE_MATCHING_SCREEN);
		else if(party != null && !party.isLeader(player))
		{
			final MatchingRoom room = player.getMatchingRoom();
			if(room != null && room.getType() == MatchingRoom.PARTY_MATCHING)
			{
				player.setMatchingRoomWindowOpened(true);
				player.sendPacket(room.infoRoomPacket(), room.membersPacket(player));
			}
			else
				player.sendPacket(SystemMsg.THE_LIST_OF_PARTY_ROOMS_CAN_ONLY_BE_VIEWED_BY_A_PERSON_WHO_IS_NOT_PART_OF_A_PARTY);
		}
		else
		{
			if(party == null)
				MatchingRoomManager.getInstance().addToWaitingList(player);
			player.sendPacket(new ListPartyWaitingPacket(_region, _allLevels == 1, _page, player));
		}
	}
}