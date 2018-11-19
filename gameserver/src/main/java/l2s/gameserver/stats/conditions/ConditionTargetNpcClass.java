package l2s.gameserver.stats.conditions;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.scripts.Scripts;
import l2s.gameserver.stats.Env;

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
			classType = (Class<NpcInstance>) Class.forName("l2s.gameserver.model.instances." + name + "Instance");
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
