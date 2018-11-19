package l2s.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import l2s.gameserver.Config;
import l2s.gameserver.model.base.TeamType;
import l2s.gameserver.model.entity.olympiad.Olympiad;
import l2s.gameserver.model.entity.olympiad.OlympiadGame;
import l2s.gameserver.model.entity.olympiad.OlympiadManager;
import l2s.gameserver.model.entity.olympiad.OlympiadMember;
import l2s.gameserver.network.l2.ServerPacketOpcodes;

/**
 * @author VISTALL
 * @date 0:50/09.04.2011
 */
public abstract class ExReceiveOlympiadPacket extends L2GameServerPacket
{
	public static class MatchList extends ExReceiveOlympiadPacket
	{
		private List<ArenaInfo> _arenaList = Collections.emptyList();

		public MatchList()
		{
			super(0);
			OlympiadManager manager = Olympiad._manager;
			if(manager != null)
			{
				_arenaList = new ArrayList<ArenaInfo>();
				for(int i = 0; i < Olympiad.STADIUMS.length; i++)
				{
					OlympiadGame game = manager.getOlympiadInstance(i);
					if(game != null && game.getState() > 0)
						_arenaList.add(new ArenaInfo(i, game.getState(), game.getType().ordinal(), game.getMemberName1(), game.getMemberName2()));
				}
			}
		}

		public MatchList(List<ArenaInfo> arenaList)
		{
			super(0);
			_arenaList = arenaList;
		}

		@Override
		protected void writeImpl()
		{
			super.writeImpl();
			writeD(_arenaList.size());
			writeD(0x00); //unknown
			for(ArenaInfo arena : _arenaList)
			{
				writeD(arena._id);
				writeD(arena._matchType);
				writeD(arena._status);
				writeS(arena._name1);
				writeS(arena._name2);
			}
		}

		public static class ArenaInfo
		{
			public int _status;
			private int _id, _matchType;
			public String _name1, _name2;

			public ArenaInfo(int id, int status, int match_type, String name1, String name2) {
				_id = id;
				_status = status;
				_matchType = match_type;
				_name1 = name1;
				_name2 = name2;
			}
		}
	}

	public static class MatchResult extends ExReceiveOlympiadPacket
	{
		private boolean _tie;
		private String _name;
		private List<PlayerInfo> _teamOne = new ArrayList<PlayerInfo>(3);
		private List<PlayerInfo> _teamTwo = new ArrayList<PlayerInfo>(3);

		public MatchResult(boolean tie, String winnerName)
		{
			super(1);
			_tie = tie;
			_name = winnerName;
		}

		public void addPlayer(TeamType team, OlympiadMember member, int gameResultPoints, int dealOutDamage)
		{
			int points = Config.OLYMPIAD_OLDSTYLE_STAT ? 0 : member.getStat().getPoints();

			addPlayer(team, member.getName(), member.getClanName(), member.getClassId(), points, gameResultPoints, dealOutDamage);
		}

		public void addPlayer(TeamType team, String name, String clanName, int classId, int points, int resultPoints, int damage)
		{
			switch(team)
			{
				case RED:
					_teamOne.add(new PlayerInfo(name, clanName, classId, points, resultPoints, damage));
					break;
				case BLUE:
					_teamTwo.add(new PlayerInfo(name, clanName, classId, points, resultPoints, damage));
					break;
			}
		}

		@Override
		protected void writeImpl()
		{
			super.writeImpl();
			writeD(_tie);
			writeS(_name);
			writeD(0x01);
			writeD(_teamOne.size());
			for (PlayerInfo playerInfo : _teamOne)
			{
				writeS(playerInfo._name);
				writeS(playerInfo._clanName);
				writeD(0x00);
				writeD(playerInfo._classId);
				writeD(playerInfo._damage);
				writeD(playerInfo._currentPoints);
				writeD(playerInfo._gamePoints);
				writeD(0x00);
			}
			writeD(0x02);
			writeD(_teamTwo.size());
			for(PlayerInfo playerInfo : _teamTwo)
			{
				writeS(playerInfo._name);
				writeS(playerInfo._clanName);
				writeD(0x00);
				writeD(playerInfo._classId);
				writeD(playerInfo._damage);
				writeD(playerInfo._currentPoints);
				writeD(playerInfo._gamePoints);
				writeD(0x00);
			}
		}

		private static class PlayerInfo
		{
			private String _name, _clanName;
			private int _classId, _currentPoints, _gamePoints, _damage;

			public PlayerInfo(String name, String clanName, int classId, int currentPoints, int gamePoints, int damage)
			{
				_name = name;
				_clanName = clanName;
				_classId = classId;
				_currentPoints = currentPoints;
				_gamePoints = gamePoints;
				_damage = damage;
			}
		}
	}

	private int _type;

	public ExReceiveOlympiadPacket(int type)
	{
		_type = type;
	}

	@Override
	protected ServerPacketOpcodes getOpcodes()
	{
		return ServerPacketOpcodes.ExReceiveOlympiadPacket;
	}

	@Override
	protected void writeImpl()
	{
		writeD(_type);
	}
}