package ai.residences.castle;

import org.l2j.commons.lang.reference.HardReference;
import org.l2j.commons.threading.RunnableImpl;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.ai.CtrlEvent;
import org.l2j.gameserver.ai.NpcAI;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.model.entity.events.impl.SiegeEvent;
import org.l2j.gameserver.model.entity.events.objects.SiegeClanObject;
import org.l2j.gameserver.model.instances.NpcInstance;

/**
 * @author VISTALL
 * @date 8:32/06.04.2011
 */
public class ArtefactAI extends NpcAI
{
	public ArtefactAI(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtAggression(Creature attacker, int aggro)
	{
		NpcInstance actor;
		Player player;
		if(attacker == null || (player = attacker.getPlayer()) == null || (actor = (NpcInstance)getActor()) == null)
			return;

		SiegeEvent<?, ?> siegeEvent1 = actor.getEvent(SiegeEvent.class);
		SiegeEvent<?, ?> siegeEvent2 = player.getEvent(SiegeEvent.class);
		SiegeClanObject siegeClan = siegeEvent1.getSiegeClan(SiegeEvent.ATTACKERS, player.getClan());

		if(siegeEvent2 == null || siegeEvent1 == siegeEvent2 && siegeClan != null)
			ThreadPoolManager.getInstance().schedule(new notifyGuard(player), 1000);
	}

	class notifyGuard extends RunnableImpl
	{
		private HardReference<Player> _playerRef;

		public notifyGuard(Player attacker)
		{
			_playerRef = attacker.getRef();
		}

		@Override
		public void runImpl() throws Exception
		{
			NpcInstance actor;
			Player attacker = _playerRef.get();
			if(attacker == null || (actor = (NpcInstance)getActor()) == null)
				return;

			for(NpcInstance npc : actor.getAroundNpc(1500, 200))
				if(npc.isSiegeGuard() && Rnd.chance(20))
					npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 5000);

			if(attacker.getCastingSkill() != null && attacker.getCastingSkill().getTargetType() == Skill.SkillTargetType.TARGET_HOLY)
				ThreadPoolManager.getInstance().schedule(this, 10000);
		}
	}
}
