package l2s.gameserver.model.actor.listener;

import l2s.commons.listener.Listener;
import l2s.commons.listener.ListenerList;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.listener.actor.*;
import l2s.gameserver.listener.actor.ai.OnAiEventListener;
import l2s.gameserver.listener.actor.ai.OnAiIntentionListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.utils.Location;

/**
 * @author G1ta0
 */
public class CharListenerList extends ListenerList<Creature>
{
	final static ListenerList<Creature> global = new ListenerList<Creature>();

	protected final Creature actor;

	public CharListenerList(Creature actor)
	{
		this.actor = actor;
	}

	public Creature getActor()
	{
		return actor;
	}

	public final static boolean addGlobal(Listener<Creature> listener)
	{
		return global.add(listener);
	}

	public final static boolean removeGlobal(Listener<Creature> listener)
	{
		return global.remove(listener);
	}

	public void onAiIntention(CtrlIntention intention, Object arg0, Object arg1)
	{
		if(!getListeners().isEmpty())
			for(Listener<Creature> listener : getListeners())
				if(OnAiIntentionListener.class.isInstance(listener))
					((OnAiIntentionListener) listener).onAiIntention(getActor(), intention, arg0, arg1);
	}

	public void onAiEvent(CtrlEvent evt, Object[] args)
	{
		if(!getListeners().isEmpty())
			for(Listener<Creature> listener : getListeners())
				if(OnAiEventListener.class.isInstance(listener))
					((OnAiEventListener) listener).onAiEvent(getActor(), evt, args);
	}

	public void onAttack(Creature target)
	{
		if(!global.getListeners().isEmpty())
			for(Listener<Creature> listener : global.getListeners())
				if(OnAttackListener.class.isInstance(listener))
					((OnAttackListener) listener).onAttack(getActor(), target);

		if(!getListeners().isEmpty())
			for(Listener<Creature> listener : getListeners())
				if(OnAttackListener.class.isInstance(listener))
					((OnAttackListener) listener).onAttack(getActor(), target);
	}

	public void onAttackHit(Creature attacker)
	{
		if(!global.getListeners().isEmpty())
			for(Listener<Creature> listener : global.getListeners())
				if(OnAttackHitListener.class.isInstance(listener))
					((OnAttackHitListener) listener).onAttackHit(getActor(), attacker);

		if(!getListeners().isEmpty())
			for(Listener<Creature> listener : getListeners())
				if(OnAttackHitListener.class.isInstance(listener))
					((OnAttackHitListener) listener).onAttackHit(getActor(), attacker);
	}

	public void onMagicUse(Skill skill, Creature target, boolean alt)
	{
		if(!global.getListeners().isEmpty())
			for(Listener<Creature> listener : global.getListeners())
				if(OnMagicUseListener.class.isInstance(listener))
					((OnMagicUseListener) listener).onMagicUse(getActor(), skill, target, alt);

		if(!getListeners().isEmpty())
			for(Listener<Creature> listener : getListeners())
				if(OnMagicUseListener.class.isInstance(listener))
					((OnMagicUseListener) listener).onMagicUse(getActor(), skill, target, alt);
	}

	public void onMagicHit(Skill skill, Creature caster)
	{
		if(!global.getListeners().isEmpty())
			for(Listener<Creature> listener : global.getListeners())
				if(OnMagicHitListener.class.isInstance(listener))
					((OnMagicHitListener) listener).onMagicHit(getActor(), skill, caster);

		if(!getListeners().isEmpty())
			for(Listener<Creature> listener : getListeners())
				if(OnMagicHitListener.class.isInstance(listener))
					((OnMagicHitListener) listener).onMagicHit(getActor(), skill, caster);
	}

	public void onDeath(Creature killer)
	{
		if(!global.getListeners().isEmpty())
			for(Listener<Creature> listener : global.getListeners())
				if(OnDeathListener.class.isInstance(listener))
					((OnDeathListener) listener).onDeath(getActor(), killer);

		if(!getListeners().isEmpty())
			for(Listener<Creature> listener : getListeners())
				if(OnDeathListener.class.isInstance(listener))
					((OnDeathListener) listener).onDeath(getActor(), killer);
	}

	public void onKill(Creature victim)
	{
		if(!global.getListeners().isEmpty())
			for(Listener<Creature> listener : global.getListeners())
				if(OnKillListener.class.isInstance(listener) && !((OnKillListener) listener).ignorePetOrSummon())
					((OnKillListener) listener).onKill(getActor(), victim);

		if(!getListeners().isEmpty())
			for(Listener<Creature> listener : getListeners())
				if(OnKillListener.class.isInstance(listener) && !((OnKillListener) listener).ignorePetOrSummon())
					((OnKillListener) listener).onKill(getActor(), victim);
	}

	public void onKillIgnorePetOrSummon(Creature victim)
	{
		if(!global.getListeners().isEmpty())
			for(Listener<Creature> listener : global.getListeners())
				if(OnKillListener.class.isInstance(listener) && ((OnKillListener) listener).ignorePetOrSummon())
					((OnKillListener) listener).onKill(getActor(), victim);

		if(!getListeners().isEmpty())
			for(Listener<Creature> listener : getListeners())
				if(OnKillListener.class.isInstance(listener) && ((OnKillListener) listener).ignorePetOrSummon())
					((OnKillListener) listener).onKill(getActor(), victim);
	}

	public void onCurrentHpDamage(double damage, Creature attacker, Skill skill)
	{
		if(!global.getListeners().isEmpty())
			for(Listener<Creature> listener : global.getListeners())
				if(OnCurrentHpDamageListener.class.isInstance(listener))
					((OnCurrentHpDamageListener) listener).onCurrentHpDamage(getActor(), damage, attacker, skill);

		if(!getListeners().isEmpty())
			for(Listener<Creature> listener : getListeners())
				if(OnCurrentHpDamageListener.class.isInstance(listener))
					((OnCurrentHpDamageListener) listener).onCurrentHpDamage(getActor(), damage, attacker, skill);
	}

	public void onChangeCurrentCp(double oldCp, double newCp)
	{
		if(!global.getListeners().isEmpty())
			for(Listener<Creature> listener : global.getListeners())
				if(OnChangeCurrentCpListener.class.isInstance(listener))
					((OnChangeCurrentCpListener) listener).onChangeCurrentCp(getActor(), oldCp, newCp);

		if(!getListeners().isEmpty())
			for(Listener<Creature> listener : getListeners())
				if(OnChangeCurrentCpListener.class.isInstance(listener))
					((OnChangeCurrentCpListener) listener).onChangeCurrentCp(getActor(), oldCp, newCp);
	}

	public void onChangeCurrentHp(double oldHp, double newHp)
	{
		if(!global.getListeners().isEmpty())
			for(Listener<Creature> listener : global.getListeners())
				if(OnChangeCurrentHpListener.class.isInstance(listener))
					((OnChangeCurrentHpListener) listener).onChangeCurrentHp(getActor(), oldHp, newHp);

		if(!getListeners().isEmpty())
			for(Listener<Creature> listener : getListeners())
				if(OnChangeCurrentHpListener.class.isInstance(listener))
					((OnChangeCurrentHpListener) listener).onChangeCurrentHp(getActor(), oldHp, newHp);
	}

	public void onChangeCurrentMp(double oldMp, double newMp)
	{
		if(!global.getListeners().isEmpty())
			for(Listener<Creature> listener : global.getListeners())
				if(OnChangeCurrentMpListener.class.isInstance(listener))
					((OnChangeCurrentMpListener) listener).onChangeCurrentMp(getActor(), oldMp, newMp);

		if(!getListeners().isEmpty())
			for(Listener<Creature> listener : getListeners())
				if(OnChangeCurrentMpListener.class.isInstance(listener))
					((OnChangeCurrentMpListener) listener).onChangeCurrentMp(getActor(), oldMp, newMp);
	}

	public void onDeathFromUndying(Creature killer)
	{
		if(!global.getListeners().isEmpty())
			for(Listener<Creature> listener : global.getListeners())
				if(OnDeathFromUndyingListener.class.isInstance(listener))
					((OnDeathFromUndyingListener) listener).onDeathFromUndying(getActor(), killer);

		if(!getListeners().isEmpty())
			for(Listener<Creature> listener : getListeners())
				if(OnDeathFromUndyingListener.class.isInstance(listener))
					((OnDeathFromUndyingListener) listener).onDeathFromUndying(getActor(), killer);
	}

	public void onRevive()
	{
		if(!global.getListeners().isEmpty())
			for(Listener<Creature> listener : global.getListeners())
				if(listener instanceof OnReviveListener)
					((OnReviveListener) listener).onRevive(getActor());

		if(!getListeners().isEmpty())
			for(Listener<Creature> listener : getListeners())
				if(listener instanceof OnReviveListener)
					((OnReviveListener) listener).onRevive(getActor());
	}

	public void onMove(Location loc)
	{
		if(!global.getListeners().isEmpty())
			for(Listener<Creature> listener : global.getListeners())
				if(OnMoveListener.class.isInstance(listener))
					((OnMoveListener) listener).onMove(getActor(), loc);

		if(!getListeners().isEmpty())
			for(Listener<Creature> listener : getListeners())
				if(OnMoveListener.class.isInstance(listener))
					((OnMoveListener) listener).onMove(getActor(), loc);
	}
}