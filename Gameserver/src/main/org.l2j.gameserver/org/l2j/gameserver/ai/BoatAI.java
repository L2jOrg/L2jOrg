package org.l2j.gameserver.ai;

import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.entity.boat.Boat;

/**
 * Author: VISTALL
 * Date:  16:56/28.12.2010
 */
public class BoatAI extends CharacterAI
{
	public BoatAI(Creature actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtArrived()
	{
		Boat actor = (Boat) getActor();
		if(actor == null)
			return;

		actor.onEvtArrived();
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}
}