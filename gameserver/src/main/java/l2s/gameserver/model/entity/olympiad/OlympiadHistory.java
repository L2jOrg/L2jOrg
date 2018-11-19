package l2s.gameserver.model.entity.olympiad;

import l2s.gameserver.data.string.StringsHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.utils.TimeUtils;

/**
 * @author VISTALL
 * @date 20:33/02.05.2011
 */
public class OlympiadHistory
{
	private final int _objectId1;
	private final int _objectId2;

	private final int _classId1;
	private final int _classId2;

	private final String _name1;
	private final String _name2;

	private final long _gameStartTime;
	private final int _gameTime;
	private final int _gameStatus; // 1 - выиграл 1, 2 выиграл 2, 0 - "устали"
	private final int _gameType;

	public OlympiadHistory(int objectId1, int objectId2, int classId1, int classId2, String name1, String name2, long gameStartTime, int gameTime, int gameStatus, int gameType)
	{
		_objectId1 = objectId1;
		_objectId2 = objectId2;

		_classId1 = classId1;
		_classId2 = classId2;

		_name1 = name1;
		_name2 = name2;

		_gameStartTime = gameStartTime;
		_gameTime = gameTime;
		_gameStatus = gameStatus;
		_gameType = gameType;
	}

	public int getGameTime()
	{
		return _gameTime;
	}

	public int getGameStatus()
	{
		return _gameStatus;
	}

	public int getGameType()
	{
		return _gameType;
	}

	public long getGameStartTime()
	{
		return _gameStartTime;
	}

	public String toString(Player player, int target, int wins, int loss, int tie)
	{
		int team = _objectId1 == target ? 1 : 2;
		String main = null;
		if(_gameStatus == 0)
			main = StringsHolder.getInstance().getString(player, "hero.history.tie");
		else if(team == _gameStatus)
			main = StringsHolder.getInstance().getString(player, "hero.history.win");
		else
			main = StringsHolder.getInstance().getString(player, "hero.history.loss");

		main = main.replace("%classId%", String.valueOf(team == 1 ? _classId2 : _classId1));
		main = main.replace("%name%", team == 1 ? _name2 : _name1);
		main = main.replace("%date%", TimeUtils.toSimpleFormat(_gameStartTime));
		int m = _gameTime / 60;
		int s = _gameTime % 60;
		main = main.replace("%time%", (m <= 9 ? "0" : "") + m + ":" + (s <= 9 ? "0" : "") + s);
		main = main.replace("%victory_count%", String.valueOf(wins));
		main = main.replace("%tie_count%", String.valueOf(tie));
		main = main.replace("%loss_count%", String.valueOf(loss));
		return main;
	}

	public int getObjectId1()
	{
		return _objectId1;
	}

	public int getObjectId2()
	{
		return _objectId2;
	}

	public int getClassId1()
	{
		return _classId1;
	}

	public int getClassId2()
	{
		return _classId2;
	}

	public String getName1()
	{
		return _name1;
	}

	public String getName2()
	{
		return _name2;
	}
}