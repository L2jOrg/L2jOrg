package ai.locations.toi.heavenlyrift;

import org.l2j.gameserver.ai.Fighter;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.instancemanager.ServerVariables;
import org.l2j.gameserver.network.l2.components.NpcString;
import org.l2j.gameserver.utils.Functions;

import manager.HeavenlyRift;

/**
 * @reworked by Bonux
 * this mob isn't supposed to do anything
**/
public class DivineAngel extends Fighter
{
	public DivineAngel(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		if(ServerVariables.getInt("heavenly_rift_level", 0) > 1)
		{
			if(HeavenlyRift.getAliveNpcCount(getActor().getNpcId()) == 0)//Last
			{
				ServerVariables.set("heavenly_rift_complete", ServerVariables.getInt("heavenly_rift_level", 0));
				ServerVariables.set("heavenly_rift_level", 0);
				ServerVariables.set("heavenly_rift_reward", 1);
				for(NpcInstance npc : HeavenlyRift.getZone().getInsideNpcs())
				{
					if(npc.getNpcId() == 18004)
					{
						Functions.npcSay(npc, NpcString.DIVINE_ANGELS_ARE_NOWHERE);
						break;
					}
				}
			}
		}
		super.onEvtDead(killer);
	}

	@Override
	public boolean checkAggression(Creature target)
	{
		if(target == null)
			return false;

		if(target.isPlayable())
		{
			for(NpcInstance npc : HeavenlyRift.getZone().getInsideNpcs()) //for tower defense event only the tower is a target
			{
				if(npc.getNpcId() == 18004)
					return false;
			}
			return true;
		}	
		else if(target.getNpcId() == 18004)
			return true;

		return false;
	}
}
