package ai;

import l2s.commons.lang.reference.HardReference;
import l2s.commons.lang.reference.HardReferences;
import l2s.commons.util.Rnd;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.utils.Functions;

/**
 * AI для ищущих помощи при HP < 50%
 *
 * @author Diamond
 */
public class WatchmanMonster extends Fighter
{
	private long _lastSearch = 0;
	private boolean isSearching = false;
	private HardReference<? extends Creature> _attackerRef = HardReferences.emptyRef();

	public WatchmanMonster(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtAttacked(final Creature attacker, Skill skill, int damage)
	{
		final NpcInstance actor = getActor();
		if(attacker != null && !actor.getFaction().isNone() && actor.getCurrentHpPercents() < 50 && _lastSearch < System.currentTimeMillis() - 15000)
		{
			_lastSearch = System.currentTimeMillis();
			_attackerRef = attacker.getRef();

			int msg = Rnd.get(100);
			if(msg < 7)
				Functions.npcSay(actor, NpcString.WE_SHALL_SEE_ABOUT_THAT);
			else if(msg < 14)
				Functions.npcSay(actor, NpcString.I_WILL_DEFINITELY_REPAY_THIS_HUMILIATION);
			else if(msg < 21)
				Functions.npcSay(actor, NpcString.RETREAT);
			else if(msg < 28)
				Functions.npcSay(actor, NpcString.TACTICAL_RETREAT);
			else if(msg < 35)
				Functions.npcSay(actor, NpcString.MASS_FLEEING);
			else if(msg < 42)
				Functions.npcSay(actor, NpcString.ITS_STRONGER_THAN_EXPECTED);
			else if(msg < 49)
				Functions.npcSay(actor, NpcString.ILL_KILL_YOU_NEXT_TIME);
			else if(msg < 56)
				Functions.npcSay(actor, NpcString.ILL_DEFINITELY_KILL_YOU_NEXT_TIME);
			else if(msg < 63)
				Functions.npcSay(actor, NpcString.OH_HOW_STRONG);
			else if(msg < 70)
				Functions.npcSay(actor, NpcString.INVADER);
			else if(msg < 77)
				Functions.npcSay(actor, NpcString.THERE_IS_NO_REASON_FOR_YOU_TO_KILL_ME_I_HAVE_NOTHING_YOU_NEED);
			else if(msg < 79)
				Functions.npcSay(actor, NpcString.SOMEDAY_YOU_WILL_PAY);
			else if(msg < 81)
				Functions.npcSay(actor, NpcString.I_WONT_JUST_STAND_STILL_WHILE_YOU_HIT_ME);
			else if(msg < 83)
				Functions.npcSay(actor, NpcString.STOP_HITTING);
			else if(msg < 85)
				Functions.npcSay(actor, NpcString.IT_HURTS_TO_THE_BONE);
			else if(msg < 87)
				Functions.npcSay(actor, NpcString.AM_I_THE_NEIGHBORHOOD_DRUM_FOR_BEATING);
			else if(msg < 89)
				Functions.npcSay(actor, NpcString.FOLLOW_ME_IF_YOU_WANT);
			else if(msg < 91)
				Functions.npcSay(actor, NpcString.SURRENDER);
			else if(msg < 93)
				Functions.npcSay(actor, NpcString.OH_IM_DEAD);
			else if(msg < 95)
				Functions.npcSay(actor, NpcString.ILL_BE_BACK);
			else
				Functions.npcSay(actor, NpcString.ILL_GIVE_YOU_TEN_MILLION_ARENA_IF_YOU_LET_ME_LIVE);

			if(findHelp())
				return;
		}
		super.onEvtAttacked(attacker, skill, damage);
	}

	private boolean findHelp()
	{
		isSearching = false;
		final NpcInstance actor = getActor();
		Creature attacker = _attackerRef.get();
		if(attacker == null)
			return false;

		for(final NpcInstance npc : actor.getAroundNpc(1000, 150))
		{
			if(!actor.isDead() && npc.isInFaction(actor) && !npc.isInCombat())
			{
				clearTasks();
				isSearching = true;
				addTaskMove(npc.getLoc(), true);
				return true;
			}
		}
		return false;
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		_lastSearch = 0;
		_attackerRef = HardReferences.emptyRef();
		isSearching = false;
		super.onEvtDead(killer);
	}

	@Override
	protected void onEvtArrived()
	{
		NpcInstance actor = getActor();
		if(isSearching)
		{
			Creature attacker = _attackerRef.get();
			if(attacker != null)
				notifyFriends(attacker, null, 100);

			isSearching = false;
			notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 100);
		}
		else
			super.onEvtArrived();
	}

	@Override
	protected void onEvtAggression(Creature target, int aggro)
	{
		if(!isSearching)
			super.onEvtAggression(target, aggro);
	}
}