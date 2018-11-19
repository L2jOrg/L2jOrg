package l2s.gameserver.model.instances;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.templates.npc.NpcTemplate;

public class GuardInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public GuardInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public boolean isAutoAttackable(Creature attacker)
	{
		return attacker.isMonster() && ((MonsterInstance) attacker).isAggressive() || attacker.isPlayable() && attacker.isPK();
	}

	@Override
	public String getHtmlDir(String filename, Player player)
	{
		return "guard/";
	}

	@Override
	public boolean isPeaceNpc()
	{
		return false;
	}

	@Override
	public boolean isFearImmune()
	{
		return true;
	}

	@Override
	public boolean isParalyzeImmune()
	{
		return true;
	}

	@Override
	public boolean noShiftClick()
	{
		return true;
	}
}