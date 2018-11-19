package l2s.gameserver.model.instances;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Creature;
import l2s.gameserver.templates.npc.NpcTemplate;

public final class ArtefactInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public ArtefactInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
		setHasChatWindow(false);
	}

	@Override
	public boolean isArtefact()
	{
		return true;
	}

	@Override
	public boolean isAutoAttackable(Creature attacker)
	{
		return false;
	}

	@Override
	public boolean isAttackable(Creature attacker)
	{
		return false;
	}
}