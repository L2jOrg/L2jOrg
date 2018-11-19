package l2s.gameserver.model.entity.events.impl;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.listener.actor.OnDeathFromUndyingListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.events.Event;
import l2s.gameserver.network.l2.components.IBroadcastPacket;

/**
 * @author VISTALL
 * @date 18:03/22.08.2011
 *
 * Базовый класс для евентов, которые могу быть на игроке ток в одном виде, толи это Твт, или Оллимпиада, или Ктф, или Дуель
 */
public abstract class SingleMatchEvent extends Event
{
	public class OnDeathFromUndyingListenerImpl implements OnDeathFromUndyingListener
	{
		@Override
		public void onDeathFromUndying(Creature actor, Creature killer)
		{
			onDie((Player)actor);
		}
	}

	protected SingleMatchEvent(MultiValueSet<String> set)
	{
		super(set);
	}

	protected SingleMatchEvent(int id, String name)
	{
		super(id, name);
	}

	/**
	 * Проверяет может ли флагаться владелец эвента при атаке цели. Участники одной дуэли не флагаются.
	 */
    public boolean checkPvPFlag(Player attacker, Creature target)
    {
        return !target.containsEvent(this);
    }

    public boolean canIncreasePvPPKCounter(Player killer, Player target)
    {
        return checkPvPFlag(killer, target);
    }

	public void onStatusUpdate(Player player)
	{}

    public void onEffectIconsUpdate(Player player, Abnormal[] effects)
	{}

	public void onDie(Player player)
	{}

	public void sendPacket(IBroadcastPacket packet)
	{}

	public void sendPackets(IBroadcastPacket... packet)
	{}

    public boolean canUseCommunityFunctions(Player player)
    {
        return true;
    }
}