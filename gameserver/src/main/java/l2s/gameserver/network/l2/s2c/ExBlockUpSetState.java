package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.ServerPacketOpcodes;

public class ExBlockUpSetState extends L2GameServerPacket
{
	@Override
	protected ServerPacketOpcodes getOpcodes()
	{
		return ServerPacketOpcodes.ExBlockUpSetState;
	}

	public static class ChangePoints extends ExBlockUpSetState
	{
		private final int _timeLeft;
		private final int _bluePoints;
		private final int _redPoints;
		private final boolean _isRedTeam;
		private final int _objectId;
		private final int _playerPoints;

		public ChangePoints(int timeLeft, int bluePoints, int redPoints, boolean isRedTeam, Player player, int playerPoints)
		{
			_timeLeft = timeLeft;
			_bluePoints = bluePoints;
			_redPoints = redPoints;
			_isRedTeam = isRedTeam;
			_objectId = player.getObjectId();
			_playerPoints = playerPoints;
		}

		@Override
		protected void writeImpl()
		{
			writeD(0x00);

			writeD(_timeLeft);
			writeD(_bluePoints);
			writeD(_redPoints);

			writeD(_isRedTeam ? 0x01 : 0x00);
			writeD(_objectId);
			writeD(_playerPoints);
		}
	}

	public static class GameEnd extends ExBlockUpSetState
	{
		private final boolean _isRedTeamWin;

		public GameEnd(boolean isRedTeamWin)
		{
			_isRedTeamWin = isRedTeamWin;
		}

		@Override
		protected void writeImpl()
		{
			writeD(0x01);

			writeD(_isRedTeamWin ? 0x01 : 0x00);
		}
	}

	public static class PointsInfo extends ExBlockUpSetState
	{
		private final int _timeLeft;
		private final int _bluePoints;
		private final int _redPoints;

		public PointsInfo(int timeLeft, int bluePoints, int redPoints)
		{
			_timeLeft = timeLeft;
			_bluePoints = bluePoints;
			_redPoints = redPoints;
		}

		@Override
		protected void writeImpl()
		{
			writeD(0x02);

			writeD(_timeLeft);
			writeD(_bluePoints);
			writeD(_redPoints);
		}
	}

	@Override
	protected void writeImpl()
	{
		/*writeD(BlockUpStateType);
		switch(BlockUpStateType)
		{
			case 0:
				//dddddd
				break;
			case 1:
				//dd
				break;
			case 2:
				//ddd
				break;
		}*/
	}
}