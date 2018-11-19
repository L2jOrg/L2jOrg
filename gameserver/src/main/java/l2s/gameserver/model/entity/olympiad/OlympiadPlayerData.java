package l2s.gameserver.model.entity.olympiad;

public abstract class OlympiadPlayerData
{
	private final int _objectId;

	private String _name;

	private int _classId;

	public OlympiadPlayerData(int objectId, String name, int classId)
	{
		_objectId = objectId;
		setName(name);
		setClassId(classId);
	}

	public int getObjectId()
	{
		return _objectId;
	}

	public String getName()
	{
		return _name;
	}

	public void setName(String value)
	{
		_name = value;
	}

	public int getClassId()
	{
		return _classId;
	}

	public void setClassId(int value)
	{
		_classId = Olympiad.convertParticipantClassId(value);
	}
}