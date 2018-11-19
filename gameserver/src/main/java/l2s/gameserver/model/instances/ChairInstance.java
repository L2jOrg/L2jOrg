package l2s.gameserver.model.instances;

import l2s.gameserver.model.Player;
import l2s.gameserver.templates.StaticObjectTemplate;

public class ChairInstance extends StaticObjectInstance
{
	private static final long serialVersionUID = 1L;
	private Player _seatedPlayer = null;

	public ChairInstance(int objectId, StaticObjectTemplate template)
	{
		super(objectId, template);
	}

	public Player getSeatedPlayer()
	{
		return _seatedPlayer;
	}

	public void setSeatedPlayer(Player player)
	{
		_seatedPlayer = player;
	}

	public boolean canSit(Player player)
	{
		if(_seatedPlayer != null && _seatedPlayer.getChairObject() == this)
			return false;
		if(player.getRealDistance3D(this) > 80)
			return false;
		return true;
	}
}