package l2s.gameserver.model.entity.olympiad;

public class OlympiadParticipiantData extends OlympiadPlayerData
{
	private int _points = 0;
	private int _pointsPast = 0;
	private int _pointsPastStatic = 0;
	private int _compDone = 0;
	private int _compWin = 0;
	private int _compLoose = 0;
	private int _classedGamesCount = 0;
	private int _nonClassedGamesCount = 0;

	public OlympiadParticipiantData(int objectId, String name, int classId)
	{
		super(objectId, name, classId);
	}

	public int getPoints()
	{
		return _points;
	}

	public void setPoints(int value)
	{
		_points = value;
	}

	public int getPointsPast()
	{
		return _pointsPast;
	}

	public void setPointsPast(int value)
	{
		_pointsPast = value;
	}

	public int getPointsPastStatic()
	{
		return _pointsPastStatic;
	}

	public void setPointsPastStatic(int value)
	{
		_pointsPastStatic = value;
	}

	public int getCompDone()
	{
		return _compDone;
	}

	public void setCompDone(int value)
	{
		_compDone = value;
	}

	public int getCompWin()
	{
		return _compWin;
	}

	public void setCompWin(int value)
	{
		_compWin = value;
	}

	public int getCompLoose()
	{
		return _compLoose;
	}

	public void setCompLoose(int value)
	{
		_compLoose = value;
	}

	public int getClassedGamesCount()
	{
		return _classedGamesCount;
	}

	public void setClassedGamesCount(int value)
	{
		_classedGamesCount = value;
	}

	public int getNonClassedGamesCount()
	{
		return _nonClassedGamesCount;
	}

	public void setNonClassedGamesCount(int value)
	{
		_nonClassedGamesCount = value;
	}
}