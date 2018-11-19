package l2s.gameserver.model.instances;

import l2s.commons.lang.reference.HardReference;
import l2s.gameserver.idfactory.IdFactory;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.reference.L2Reference;

/**
 * @author VISTALL
 * @date 20:20/03.01.2011
 */
public class ControlKeyInstance extends GameObject
{
	private static final long serialVersionUID = 1L;

	protected HardReference<ControlKeyInstance> reference;

	public ControlKeyInstance()
	{
		super(IdFactory.getInstance().getNextId());
		reference = new L2Reference<ControlKeyInstance>(this);
	}

	@Override
	public HardReference<ControlKeyInstance> getRef()
	{
		return reference;
	}

	@Override
	public void onAction(Player player, boolean shift)
	{
		if(player.getTarget() != this)
		{
			player.setTarget(this);
			return;
		}

		player.sendActionFailed();
	}
}
