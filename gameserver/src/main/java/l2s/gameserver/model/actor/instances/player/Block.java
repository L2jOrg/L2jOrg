package l2s.gameserver.model.actor.instances.player;

/**
 * @author Bonux
 */
public class Block
{
	private final int _objectId;
	private String _name;
	private String _memo;

	public Block(int objectId, String name)
	{
		this(objectId, name, "");
	}

	public Block(int objectId, String name, String memo)
	{
		_objectId = objectId;
		_name = name;
		_memo = memo;
	}

	public int getObjectId()
	{
		return _objectId;
	}

	public String getName()
	{
		return _name;
	}

	public String getMemo()
	{
		return _memo;
	}

	public void setMemo(String val)
	{
		_memo = val;
	}
}