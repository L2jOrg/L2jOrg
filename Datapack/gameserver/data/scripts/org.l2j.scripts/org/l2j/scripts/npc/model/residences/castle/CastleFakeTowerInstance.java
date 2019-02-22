package org.l2j.scripts.npc.model.residences.castle;

import org.l2j.commons.collections.MultiValueSet;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.templates.npc.NpcTemplate;

/**
 * @Author: Death
 * @Date: 17/9/2007
 * @Time: 19:11:50
 *
 * Этот инстанс просто для отрисовки умершей вышки на месте оригинальной на осаде
 * Фэйковый инстанс неуязвим.
 */
public class CastleFakeTowerInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public CastleFakeTowerInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	/**
	 * Фэйковые вышки нельзя атаковать
	 */
	@Override
	public boolean isAutoAttackable(Creature player)
	{
		return false;
	}

	/**
	 * Вышки не умеют говорить
	 */
	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... arg)
	{}

	/**
	 * Вышки не умеют говорить
	 */
	@Override
	public void showChatWindow(Player player, String filename, boolean firstTalk, Object... replace)
	{}

	@Override
	public boolean hasRandomAnimation()
	{
		return false;
	}
}