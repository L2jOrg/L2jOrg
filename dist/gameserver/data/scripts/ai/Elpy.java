package ai;

import l2s.commons.util.Rnd;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.geodata.GeoEngine;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.utils.Location;

public class Elpy extends Fighter
{
	public Elpy(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, Skill skill, int damage)
	{
		NpcInstance actor = getActor();
		if(attacker != null && Rnd.chance(50))
		{
			Location pos = Location.findPointToStay(actor, 150, 200); 
			if(GeoEngine.canMoveToCoord(actor.getX(), actor.getY(), actor.getZ(), pos.x, pos.y, pos.z, actor.getGeoIndex()))
			{
				actor.setRunning();
				addTaskMove(pos, false);
			}
		}
	}

	@Override
	public boolean checkAggression(Creature target)
	{
		return false;
	}

	@Override
	protected void onEvtAggression(Creature target, int aggro)
	{
		//
	}
}