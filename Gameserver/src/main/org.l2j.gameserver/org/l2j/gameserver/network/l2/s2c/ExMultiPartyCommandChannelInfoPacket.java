package org.l2j.gameserver.network.l2.s2c;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.l2j.gameserver.model.CommandChannel;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.GameClient;

public class ExMultiPartyCommandChannelInfoPacket extends L2GameServerPacket
{
	private String ChannelLeaderName;
	private int MemberCount;
	private List<ChannelPartyInfo> parties;

	public ExMultiPartyCommandChannelInfoPacket(CommandChannel channel)
	{
		ChannelLeaderName = channel.getChannelLeader().getName();
		MemberCount = channel.getMemberCount();

		parties = new ArrayList<ChannelPartyInfo>();
		for(Party party : channel.getParties())
		{
			Player leader = party.getPartyLeader();
			if(leader != null)
				parties.add(new ChannelPartyInfo(leader.getName(), leader.getObjectId(), party.getMemberCount()));
		}
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		writeString(ChannelLeaderName, buffer); // имя лидера CC
		buffer.putInt(0); // Looting type?
		buffer.putInt(MemberCount); // общее число человек в СС
		buffer.putInt(parties.size()); // общее число партий в СС

		for(ChannelPartyInfo party : parties)
		{
			writeString(party.Leader_name, buffer); // имя лидера партии
			buffer.putInt(party.Leader_obj_id); // ObjId пати лидера
			buffer.putInt(party.MemberCount); // количество мемберов в пати
		}
	}

	static class ChannelPartyInfo
	{
		public String Leader_name;
		public int Leader_obj_id, MemberCount;

		public ChannelPartyInfo(String _Leader_name, int _Leader_obj_id, int _MemberCount)
		{
			Leader_name = _Leader_name;
			Leader_obj_id = _Leader_obj_id;
			MemberCount = _MemberCount;
		}
	}
}