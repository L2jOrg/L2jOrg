package l2s.gameserver.model.instances;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Creature;
import l2s.gameserver.templates.npc.NpcTemplate;

/**
 * @author Bonux
**/
public class DefenderInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public DefenderInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public boolean isAutoAttackable(Creature attacker)
	{
		return attacker.isMonster();
	}

	@Override
	public boolean isDefender()
	{
		return true;
	}
}