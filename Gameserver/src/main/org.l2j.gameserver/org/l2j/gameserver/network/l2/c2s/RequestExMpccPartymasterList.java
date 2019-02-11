package org.l2j.gameserver.network.l2.c2s;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.matching.MatchingRoom;
import org.l2j.gameserver.network.l2.s2c.ExMpccPartymasterList;

/**
 * @author VISTALL
 */
public class RequestExMpccPartymasterList extends L2GameClientPacket
{
	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		//
	}

	@Override
	protected void runImpl()
	{
		Player player = client.getActiveChar();
		if(player == null)
			return;

		MatchingRoom room = player.getMatchingRoom();
		if(room == null || room.getType() != MatchingRoom.CC_MATCHING)
			return;

		Set<String> set = new HashSet<String>();
		for(Player $member : room.getPlayers())
			if($member.getParty() != null)
				set.add($member.getParty().getPartyLeader().getName());

		player.sendPacket(new ExMpccPartymasterList(set));
	}
}