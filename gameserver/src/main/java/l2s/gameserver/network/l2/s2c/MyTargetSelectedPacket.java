package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.Player;

public class MyTargetSelectedPacket extends L2GameServerPacket
{
	private final boolean _success;
	private int _objectId;
	private int _color;
	private final boolean _actionMenu;

	/**
	 * @param objectId of the target
	 * @param color difference to the target. name color is calculated from that
	 */
	public MyTargetSelectedPacket(int objectId, int color, boolean actionMenu)
	{
		_success = true;
		_objectId = objectId;
		_color = color;
		_actionMenu = actionMenu;
	}

	public MyTargetSelectedPacket(int objectId, int color)
	{
		this(objectId, color, false);
	}

	public MyTargetSelectedPacket(Player player, GameObject target, boolean actionMenu)
	{
		_success = true;
		_objectId = target.getObjectId();
		if(target.isCreature())
			_color = player.getLevel() - ((Creature) target).getLevel();
		else
			_color = 0;
		_actionMenu = actionMenu;
	}

	public MyTargetSelectedPacket(Player player, GameObject target)
	{
		this(player, target, false);
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_success ? 0x01 : 0x00);
		if(_success)
		{
			writeD(_objectId);
			writeH(_color);
			writeD(_actionMenu ? 0x03 : 0x00);
		}
	}
}