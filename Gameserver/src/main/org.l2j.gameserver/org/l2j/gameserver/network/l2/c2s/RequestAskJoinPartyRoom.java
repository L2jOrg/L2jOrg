package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Request;
import org.l2j.gameserver.model.Request.L2RequestType;
import org.l2j.gameserver.model.World;
import org.l2j.gameserver.model.matching.MatchingRoom;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.ExAskJoinPartyRoom;
import org.l2j.gameserver.network.l2.s2c.SystemMessagePacket;

import java.nio.ByteBuffer;

/**
 * format: (ch)S
 */
public class RequestAskJoinPartyRoom extends L2GameClientPacket
{
	private String _name; // not tested, just guessed

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_name = readString(buffer, 16);
	}

	@Override
	protected void runImpl()
	{
		Player player = client.getActiveChar();
		if(player == null)
			return;

		Player targetPlayer = World.getPlayer(_name);

		if(targetPlayer == null || targetPlayer == player)
		{
			player.sendActionFailed();
			return;
		}

		if(player.isProcessingRequest())
		{
			player.sendPacket(SystemMsg.WAITING_FOR_ANOTHER_REPLY);
			return;
		}

		if(targetPlayer.isProcessingRequest())
		{
			player.sendPacket(new SystemMessagePacket(SystemMsg.C1_IS_ON_ANOTHER_TASK).addName(targetPlayer));
			return;
		}

		if(targetPlayer.isInTrainingCamp())
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_REQUEST_TO_A_CHARACTER_WHO_IS_ENTERING_THE_TRAINING_CAMP);
			return;
		}

		if(targetPlayer.getMatchingRoom() != null)
			return;

		MatchingRoom room = player.getMatchingRoom();
		if(room == null || room.getType() != MatchingRoom.PARTY_MATCHING)
			return;
/*
		if(room.getLeader() != player)
		{
			player.sendPacket(SystemMsg.ONLY_A_ROOM_LEADER_MAY_INVITE_OTHERS_TO_A_PARTY_ROOM);
			return;
		}
*/
		if(room.getPlayers().size() >= room.getMaxMembersSize())
		{
			player.sendPacket(SystemMsg.THE_PARTY_ROOM_IS_FULL);
			return;
		}

		new Request(L2RequestType.PARTY_ROOM, player, targetPlayer).setTimeout(10000L);

		targetPlayer.sendPacket(new ExAskJoinPartyRoom(player.getName(), room.getTopic()));

		player.sendPacket(new SystemMessagePacket(SystemMsg.S1_HAS_SENT_AN_INVITATION_TO_ROOM_S2).addName(player).addString(room.getTopic()));
		targetPlayer.sendPacket(new SystemMessagePacket(SystemMsg.S1_HAS_SENT_AN_INVITATION_TO_ROOM_S2).addName(player).addString(room.getTopic()));
	}
}