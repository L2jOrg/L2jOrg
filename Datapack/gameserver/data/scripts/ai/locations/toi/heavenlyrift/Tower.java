package ai.locations.toi.heavenlyrift;

import org.l2j.gameserver.ai.NpcAI;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.instancemanager.ServerVariables;

import manager.HeavenlyRift;

/**
 * @reworked by Bonux
 * this mob supposed to be attackable
**/
public class Tower extends NpcAI
{	
	public Tower(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		for(NpcInstance npc : HeavenlyRift.getZone().getInsideNpcs())
		{
			if(npc.getNpcId() == 20139 && !npc.isDead())
				npc.decayMe();
		}

		ServerVariables.set("heavenly_rift_complete", ServerVariables.getInt("heavenly_rift_level", 0));
		ServerVariables.set("heavenly_rift_level", 0);
		ServerVariables.set("heavenly_rift_reward", 0);
		super.onEvtDead(killer);
	}
}
