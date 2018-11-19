package l2s.gameserver.network.l2.s2c;

import java.util.List;

import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.ServerPacketOpcodes;

/**
 * Format: (chd) ddd[dS]d[dS]
 * d: unknown
 * d: always -1
 * d: blue players number
 * [
 * 		d: player object id
 * 		S: player name
 * ]
 * d: blue players number
 * [
 * 		d: player object id
 * 		S: player name
 * ]
 */
public abstract class ExBlockUpSetList extends L2GameServerPacket
{
	@Override
	protected ServerPacketOpcodes getOpcodes()
	{
		return ServerPacketOpcodes.ExBlockUpSetList;
	}

	public static class TeamList extends ExBlockUpSetList
	{
		private final List<Player> _bluePlayers;
		private final List<Player> _redPlayers;
		private final int _roomNumber;

		public TeamList(List<Player> redPlayers, List<Player> bluePlayers, int roomNumber)
		{
			_redPlayers = redPlayers;
			_bluePlayers = bluePlayers;
			_roomNumber = roomNumber - 1;
		}

		@Override
		protected void writeImpl()
		{
			writeD(0x00);	// type

			writeD(_roomNumber);
			writeD(0xffffffff);

			writeD(_bluePlayers.size());
			for(Player player : _bluePlayers)
			{
				writeD(player.getObjectId());
				writeS(player.getName());
			}
			writeD(_redPlayers.size());
			for(Player player : _redPlayers)
			{
				writeD(player.getObjectId());
				writeS(player.getName());
			}
		}
	}

	public static class AddPlayer extends ExBlockUpSetList
	{
		private final int _objectId;
		private final String _name;
		private final boolean _isRedTeam;

		public AddPlayer(Player player, boolean isRedTeam)
		{
			_objectId = player.getObjectId();
			_name = player.getName();
			_isRedTeam = isRedTeam;
		}

		@Override
		protected void writeImpl()
		{
			writeD(0x01);	// type

			writeD(0xffffffff);

			writeD(_isRedTeam ? 0x01 : 0x00);
			writeD(_objectId);
			writeS(_name);
		}
	}

	public static class RemovePlayer extends ExBlockUpSetList
	{
		private final int _objectId;
		private final boolean _isRedTeam;

		public RemovePlayer(Player player, boolean isRedTeam)
		{
			_objectId = player.getObjectId();
			_isRedTeam = isRedTeam;
		}

		@Override
		protected void writeImpl()
		{
			writeD(0x02);	// type

			writeD(0xffffffff);

			writeD(_isRedTeam ? 0x01 : 0x00);
			writeD(_objectId);
		}
	}

	public static class ChangeTimeToStart extends ExBlockUpSetList
	{
		private final int _seconds;

		public ChangeTimeToStart(int seconds)
		{
			_seconds = seconds;
		}

		@Override
		protected void writeImpl()
		{
			writeD(0x03);
			writeD(_seconds);
		}
	}

	public static class RequestReady extends ExBlockUpSetList
	{
		public static final RequestReady STATIC = new RequestReady();

		public RequestReady()
		{
			//
		}

		@Override
		protected void writeImpl()
		{
			writeD(0x04);
		}
	}

	public static class ChangeTeam extends ExBlockUpSetList
	{
		private int _objectId;
		private boolean _fromRedTeam;

		public ChangeTeam(Player player, boolean fromRedTeam)
		{
			_objectId = player.getObjectId();
			_fromRedTeam = fromRedTeam;
		}

		@Override
		protected void writeImpl()
		{
			writeD(0x05);

			writeD(_objectId);
			writeD(_fromRedTeam ? 1 : 0);
			writeD(_fromRedTeam ? 0 : 1);
		}
	}

	public static class CloseUI extends ExBlockUpSetList
	{
		public static final CloseUI STATIC = new CloseUI();

		public CloseUI()
		{
			//
		}

		@Override
		protected void writeImpl()
		{
			writeD(0xffffffff);
		}
	}
}