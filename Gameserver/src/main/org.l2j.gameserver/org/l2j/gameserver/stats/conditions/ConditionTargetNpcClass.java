package org.l2j.gameserver.stats.conditions;

import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.scripts.Scripts;
import org.l2j.gameserver.stats.Env;

/**
 * @author VISTALL
 * @date 3:38/17.05.2011
 */
@SuppressWarnings("unchecked")
public class ConditionTargetNpcClass extends Condition
{
	private final Class<NpcInstance> _npcClass;

	public ConditionTargetNpcClass(String name)
	{
		Class<NpcInstance> classType = null;
		try
		{
			classType = (Class<NpcInstance>) Class.forName("org.l2j.gameserver.model.instances." + name + "Instance");
		}
		catch(ClassNotFoundException e)
		{
			classType = (Class<NpcInstance>) Scripts.getInstance().getClasses().get("npc.model." + name + "Instance");
		}

		if(classType == null)
			throw new IllegalArgumentException("Not found type class for type: " + name + ".");
		else
			_npcClass = classType;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		return env.target != null && env.target.getClass() == _npcClass;
	}
}
