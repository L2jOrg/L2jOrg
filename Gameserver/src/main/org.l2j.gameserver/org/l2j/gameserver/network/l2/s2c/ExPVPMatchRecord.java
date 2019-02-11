package org.l2j.gameserver.network.l2.s2c;

import java.nio.ByteBuffer;
import java.util.List;

import org.l2j.gameserver.model.base.TeamType;
import org.l2j.gameserver.network.l2.GameClient;

public class ExPVPMatchRecord extends L2GameServerPacket
{
	public static final int START = 0;
	public static final int UPDATE = 1;
	public static final int FINISH = 2;

	private int _type;
	private TeamType _winnerTeam;
	private int _blueKills;
	private int _redKills;
	private List<Member> _blueList;
	private List<Member> _redList;

	public static class Member
	{
		public String name;
		public int kills;
		public int deaths;

		public Member(String name, int kills, int deaths)
		{
			this.name = name;
			this.kills = kills;
			this.deaths = deaths;
		}
	}

	public ExPVPMatchRecord(int type, TeamType winnerTeam, int blueKills, int redKills, List<Member> blueTeam, List<Member> redTeam)
	{
		_type = type;
		_winnerTeam = winnerTeam;
		_blueKills = blueKills;
		_redKills = redKills;
		_blueList = blueTeam;
		_redList = redTeam;
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_type);
		buffer.putInt(_winnerTeam.ordinal());
		buffer.putInt(_winnerTeam.revert().ordinal());
		buffer.putInt(_blueKills);
		buffer.putInt(_redKills);
		buffer.putInt(_blueList.size());
		for(Member member : _blueList)
		{
			writeString(member.name, buffer);
			buffer.putInt(member.kills);
			buffer.putInt(member.deaths);
		}
		buffer.putInt(_redList.size());
		for(Member member : _redList)
		{
			writeString(member.name, buffer);
			buffer.putInt(member.kills);
			buffer.putInt(member.deaths);
		}
	}
}