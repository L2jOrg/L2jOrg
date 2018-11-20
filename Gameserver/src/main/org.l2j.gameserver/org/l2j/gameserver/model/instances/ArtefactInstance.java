package org.l2j.gameserver.model.instances;

import org.l2j.commons.collections.MultiValueSet;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.templates.npc.NpcTemplate;

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