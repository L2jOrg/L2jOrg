package l2s.gameserver.model.actor.instances.player;

import java.util.ArrayList;
import java.util.concurrent.ScheduledFuture;

import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;
import l2s.gameserver.Config;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.geodata.GeoEngine;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.network.l2.s2c.MagicSkillLaunchedPacket;
import l2s.gameserver.network.l2.s2c.MagicSkillUse;
import l2s.gameserver.stats.Formulas;
import l2s.gameserver.templates.CubicTemplate;
import l2s.gameserver.templates.CubicTemplate.SkillInfo;

public class Cubic extends RunnableImpl
{
	private final Player _owner;
	private final CubicTemplate _template;
	private final Skill _skill;
	private ScheduledFuture<?> _task;
	private int _delay;
	private int _timeLeft;
	private int _count;

	public Cubic(Player owner, CubicTemplate template, Skill skill)
	{
		_owner = owner;
		_template = template;
		_skill = skill;
		_delay = _template.getDelay();
		_timeLeft = _template.getDuration();
		_count = _template.getMaxCount();
	}

	public int getId()
	{
		return _template.getId();
	}

	public int getSlot()
	{
		return _template.getSlot();
	}

	public Player getOwner()
	{
		return _owner;
	}

	public Skill getSkill()
	{
		return _skill;
	}

	public void init()
	{
		_owner.addCubic(this);
		if(_task == null)
			_task = ThreadPoolManager.getInstance().scheduleAtFixedRate(this, _delay * 1000, _delay * 1000);
	}

	public void delete()
	{
		if(_task != null)
		{
			_task.cancel(true);
			_task = null;
		}
		_owner.removeCubic(getSlot());
	}

	@Override
	public void runImpl() throws Exception
	{
		Player player = getOwner();
		if(player == null)
			return;

		_timeLeft -= _delay;
		SkillInfo skill = _template.getRandomSkill();
		if(skill == null)
			return;

		boolean success = false;
		switch(skill.getActionType())
		{
			case ATTACK:
				success = Cubic.doAttack(player, skill);
				break;
			case BUFF:
				success = Cubic.doBuff(player, skill);
				break;
			case DEBUFF:
				success = Cubic.doDebuff(player, skill);
				break;
			case HEAL:
				success = Cubic.doHeal(player, skill);
				break;
			case MANA:
				success = Cubic.doMana(player, skill);
				break;
			case CANCEL:
				success = Cubic.doCancel(player, skill);
				break;
		}

		if(_timeLeft <= 0)
		{
			delete();
			return;
		}

		if(success)
			_count--;

		if(_count <= 0)
		{
			switch(_template.getUseUp())
			{
				case INCREASE_DELAY:
					_count = _template.getMaxCount();
					_delay *= 5;
					if(_task != null)
						_task.cancel(true);

					_task = ThreadPoolManager.getInstance().scheduleAtFixedRate(this, _delay * 1000, _delay * 1000);
					break;
				case DISPELL:
					delete();
					break;
			}
		}
	}

	private static boolean doHeal(Player player, SkillInfo info)
	{
		Skill skill = info.getSkill();
		Creature target = null;
		if(player.getParty() == null)
		{
			if(!player.isCurrentHpFull() && !player.isDead())
				target = player;
		}
		else
		{
			double currentHp = Integer.MAX_VALUE;
			for(Player member : player.getParty().getPartyMembers())
			{
				if(member == null)
					continue;

				if(canCastSkill(player, member, info.getSkill()) && !member.isCurrentHpFull() && !member.isDead() && member.getCurrentHp() < currentHp)
				{
					currentHp = member.getCurrentHp();
					target = member;
				}
			}
		}

		if(target == null)
			return false;

		int chance = info.getChance((int) target.getCurrentHpPercents());
		if(!Rnd.chance(chance))
			return false;

		Creature aimTarget = target;
		if(!skill.isNotBroadcastable())
			player.broadcastPacket(new MagicSkillUse(player, aimTarget, skill.getDisplayId(), skill.getDisplayLevel(), skill.getHitTime(), 0));

		ThreadPoolManager.getInstance().schedule(() ->
		{
			ArrayList<Creature> targets = new ArrayList<Creature>(1);
			targets.add(aimTarget);
			if(!skill.isNotBroadcastable())
				player.broadcastPacket(new MagicSkillLaunchedPacket(player.getObjectId(), skill.getDisplayId(), skill.getDisplayLevel(), targets));

			player.callSkill(skill, targets, false, false);
		}, skill.getHitTime());
		return true;
	}

	private static boolean doMana(Player player, SkillInfo info)
	{
		Skill skill = info.getSkill();
		Creature target = null;
		if(player.getParty() == null)
		{
			if(!player.isCurrentMpFull() && !player.isDead())
				target = player;
		}
		else
		{
			double currentMp = Integer.MAX_VALUE;
			for(Player member : player.getParty().getPartyMembers())
			{
				if(member == null)
					continue;

				if(canCastSkill(player, member, info.getSkill()) && !member.isCurrentMpFull() && !member.isDead() && member.getCurrentMp() < currentMp)
				{
					currentMp = member.getCurrentMp();
					target = member;
				}
			}
		}

		if(target == null)
			return false;

		int chance = info.getChance((int) target.getCurrentMpPercents());
		if(!Rnd.chance(chance))
			return false;

		Creature aimTarget = target;
		if(!skill.isNotBroadcastable())
			player.broadcastPacket(new MagicSkillUse(player, aimTarget, skill.getDisplayId(), skill.getDisplayLevel(), skill.getHitTime(), 0));

		ThreadPoolManager.getInstance().schedule(() ->
		{
			ArrayList<Creature> targets = new ArrayList<Creature>(1);
			targets.add(aimTarget);
			if(!skill.isNotBroadcastable())
				player.broadcastPacket(new MagicSkillLaunchedPacket(player.getObjectId(), skill.getDisplayId(), skill.getDisplayLevel(), targets));

			player.callSkill(skill, targets, false, false);
		}, skill.getHitTime());
		return true;
	}

	private static boolean doAttack(Player player, SkillInfo info)
	{
		if(!Rnd.chance(info.getChance()))
			return false;

		Skill skill = info.getSkill();
		Creature target = null;
		if(player.isInCombat())
		{
			GameObject object = player.getTarget();
			target = object != null && object.isCreature() ? ((Creature) object) : null;
		}
		if(target == null || target.isDead() || target.isDoor() && !info.isCanAttackDoor() || !Cubic.canCastSkill(player, target, skill) || !target.isAutoAttackable(player))
			return false;

		Creature aimTarget = target;
		if(!skill.isNotBroadcastable())
			player.broadcastPacket(new MagicSkillUse(player, target, skill.getDisplayId(), skill.getDisplayLevel(), skill.getHitTime(), 0));

		ThreadPoolManager.getInstance().schedule(() ->
		{
			ArrayList<Creature> targets = new ArrayList<Creature>(1);
			targets.add(aimTarget);
			if(!skill.isNotBroadcastable())
				player.broadcastPacket(new MagicSkillLaunchedPacket(player.getObjectId(), skill.getDisplayId(), skill.getDisplayLevel(), targets));

			player.callSkill(skill, targets, false, false);
			if(aimTarget.isNpc())
			{
				if(aimTarget.paralizeOnAttack(player))
				{
					if(Config.PARALIZE_ON_RAID_DIFF)
						player.paralizeMe(aimTarget);
				}
				else
				{
					int damage = skill.getEffectPoint() != 0 ? skill.getEffectPoint() : (int) skill.getPower();
					aimTarget.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, player, skill, damage);
				}
			}
		}, skill.getHitTime());
		return true;
	}

	private static boolean doBuff(Player player, SkillInfo info)
	{
		if(!Rnd.chance(info.getChance()))
			return false;

		Skill skill = info.getSkill();
		if(!skill.isNotBroadcastable())
			player.broadcastPacket(new MagicSkillUse(player, player, skill.getDisplayId(), skill.getDisplayLevel(), skill.getHitTime(), 0));

		ThreadPoolManager.getInstance().schedule(() ->
		{
			ArrayList<Creature> targets = new ArrayList<Creature>(1);
			targets.add(player);
			if(!skill.isNotBroadcastable())
				player.broadcastPacket(new MagicSkillLaunchedPacket(player.getObjectId(), skill.getDisplayId(), skill.getDisplayLevel(), targets));

			player.callSkill(skill, targets, false, false);
		}, skill.getHitTime());
		return true;
	}

	private static boolean doDebuff(Player player, SkillInfo info)
	{
		if(!Rnd.chance(info.getChance()))
			return false;

		Skill skill = info.getSkill();
		Creature target = null;
		if(player.isInCombat())
		{
			GameObject object = player.getTarget();
			target = object != null && object.isCreature() ? ((Creature) object) : null;
		}

		if(target == null || target.isDead() || (target.isDoor() && !info.isCanAttackDoor()) || !canCastSkill(player, target, skill) || !target.isAutoAttackable(player))
			return false;

		Creature aimTarget = target;
		if(!skill.isNotBroadcastable())
			player.broadcastPacket(new MagicSkillUse(player, target, skill.getDisplayId(), skill.getDisplayLevel(), skill.getHitTime(), 0));

		ThreadPoolManager.getInstance().schedule(() ->
		{
			ArrayList<Creature> targets = new ArrayList<Creature>(1);
			targets.add(aimTarget);
			if(!skill.isNotBroadcastable())
				player.broadcastPacket(new MagicSkillLaunchedPacket(player.getObjectId(), skill.getDisplayId(), skill.getDisplayLevel(), targets));

			boolean succ = Formulas.calcEffectsSuccess(player, aimTarget, skill, info.getChance());
			if(succ)
				player.callSkill(skill, targets, false, false);

			if(aimTarget.isNpc())
			{
				if(aimTarget.paralizeOnAttack(player))
				{
					if(Config.PARALIZE_ON_RAID_DIFF)
						player.paralizeMe(aimTarget);
				}
				else
				{
					int damage = skill.getEffectPoint() != 0 ? skill.getEffectPoint() : (int) skill.getPower();
					aimTarget.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, player, skill, damage);
				}
			}
		}, skill.getHitTime());
		return true;
	}

	private static boolean doCancel(Player player, SkillInfo info)
	{
		if(!Rnd.chance(info.getChance()))
			return false;

		Skill skill = info.getSkill();
		if(!skill.isNotBroadcastable())
			player.broadcastPacket(new MagicSkillUse(player, player, skill.getDisplayId(), skill.getDisplayLevel(), skill.getHitTime(), 0));

		ThreadPoolManager.getInstance().schedule(() ->
		{
			ArrayList<Creature> targets = new ArrayList<Creature>(1);
			targets.add(player);
			if(!skill.isNotBroadcastable())
				player.broadcastPacket(new MagicSkillLaunchedPacket(player.getObjectId(), skill.getDisplayId(), skill.getDisplayLevel(), targets));

			player.callSkill(skill, targets, false, false);
		}, skill.getHitTime());
		return true;
	}

	private static boolean canCastSkill(Player player, Creature target, Skill skill)
	{
		if(!GeoEngine.canSeeTarget(player, target, false))
			return false;

		if(skill.getCastRange() == -1)
			return true;

		int range = Math.max(10, skill.getCastRange()) + (int) player.getMinDistance(target);
		range += 40;
		if(!player.isInRangeZ(target, range))
			return false;

		return true;
	}
}