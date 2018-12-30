package org.l2j.gameserver.model.instances;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

import org.l2j.commons.string.StringArrayUtils;
import org.l2j.commons.threading.RunnableImpl;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.data.xml.holder.PetDataHolder;
import org.l2j.gameserver.data.xml.holder.SkillHolder;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.model.actor.instances.creature.AbnormalList;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.stats.Formulas;
import org.l2j.gameserver.templates.npc.NpcTemplate;
import io.github.joealisson.primitive.maps.IntObjectMap;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PetBabyInstance extends PetInstance
{
	private static final long serialVersionUID = 1L;

	private static final Logger _log = LoggerFactory.getLogger(PetBabyInstance.class);

	private Future<?> _actionTask;
	private boolean _buffEnabled = true;

	private final IntObjectMap<List<Skill>> _buffSkills = new HashIntObjectMap<List<Skill>>();

	public PetBabyInstance(int objectId, NpcTemplate template, Player owner, ItemInstance control, long exp)
	{
		super(objectId, template, owner, control, exp);
		parseSkills();
	}

	public PetBabyInstance(int objectId, NpcTemplate template, Player owner, ItemInstance control)
	{
		super(objectId, template, owner, control);
		parseSkills();
	}

	private void parseSkills()
	{
		for(int step = 0; step < 10; step++)
		{
			List<Skill> skills = _buffSkills.get(step);
			for(int buff = 1; buff < 10; buff++)
			{
				String data = getTemplate().getAIParams().getString("step" + step + "_buff0" + buff, null);
				if(data == null)
					break;

				if(skills == null)
				{
					skills = new ArrayList<Skill>();
					_buffSkills.put(step, skills);
				}

				int[][] skillsData = StringArrayUtils.stringToIntArray2X(data, ";", "-");
				for(int[] skillData : skillsData)
				{
					int skillLevel = skillData.length > 1 ? skillData[1] : 1;
					skills.add(SkillHolder.getInstance().getSkill(skillData[0], skillLevel));
				}
			}
		}
	}

	// heal
	private static final int HealTrick = 4717;
	private static final int GreaterHealTrick = 4718;
	private static final int GreaterHeal = 5195;
	private static final int BattleHeal = 5590;
	private static final int Recharge = 5200;

	class ActionTask extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			Skill skill = onActionTask();
			_actionTask = ThreadPoolManager.getInstance().schedule(new ActionTask(), skill == null ? 1000 : Formulas.calcSkillCastSpd(PetBabyInstance.this, skill, skill.getHitTime()));
		}
	}

	public List<Skill> getBuffs()
	{
		for(int step = getBuffLevel(); step >= 0; step--)
		{
			List<Skill> skills = _buffSkills.get(step);
			if(skills != null)
				return skills;
		}
		return Collections.emptyList();
	}

	private Skill getHealSkill(int hpPercent)
	{
		if(PetDataHolder.isImprovedBabyPet(getNpcId()))
		{
			if(hpPercent < 90)
			{
				if(hpPercent < 33)
					return SkillHolder.getInstance().getSkill(BattleHeal, getHealLevel());
				if(getNpcId() != PetDataHolder.IMPROVED_BABY_KOOKABURRA_ID)
					return SkillHolder.getInstance().getSkill(GreaterHeal, getHealLevel());
			}
		}
		else if(PetDataHolder.isBabyPet(getNpcId()))
		{
			if(hpPercent < 90)
			{
				if(hpPercent < 33)
					return SkillHolder.getInstance().getSkill(GreaterHealTrick, getHealLevel());
				else
					return SkillHolder.getInstance().getSkill(HealTrick, getHealLevel());
			}
		}
		else
		{
			switch(getNpcId())
			{
				case PetDataHolder.WHITE_WEASEL_ID:
				case PetDataHolder.TOY_KNIGHT_ID:
					if(hpPercent < 70)
					{
						if(hpPercent < 30)
							return SkillHolder.getInstance().getSkill(BattleHeal, getHealLevel());
						else
							return SkillHolder.getInstance().getSkill(GreaterHeal, getHealLevel());
					}
					break;
				case PetDataHolder.FAIRY_PRINCESS_ID:
				case PetDataHolder.SPIRIT_SHAMAN_ID:
					if(hpPercent < 30)
						return SkillHolder.getInstance().getSkill(BattleHeal, getHealLevel());
					break;
			}
		}
		return null;
	}

	private Skill getManaHealSkill(int mpPercent)
	{
		switch(getNpcId())
		{
			case PetDataHolder.IMPROVED_BABY_KOOKABURRA_ID:
				if(mpPercent < 66)
					return SkillHolder.getInstance().getSkill(Recharge, getRechargeLevel());
				break;
			case PetDataHolder.FAIRY_PRINCESS_ID:
			case PetDataHolder.SPIRIT_SHAMAN_ID:
				if(mpPercent < 50)
					return SkillHolder.getInstance().getSkill(Recharge, getRechargeLevel());
				break;
		}
		return null;
	}

	public Skill onActionTask()
	{
		try
		{
			Player owner = getPlayer();
			if(!owner.isDead() && !owner.isInvulnerable() && !isCastingNow())
			{
				if(getAbnormalList().contains(5753)) // Awakening
					return null;

				if(getAbnormalList().contains(5771)) // Buff Control
					return null;

				Skill skill = null;

				if(!Config.ALT_PET_HEAL_BATTLE_ONLY || owner.isInCombat())
				{
					// проверка лечения
					double curHp = owner.getCurrentHpPercents();
					if(Rnd.chance((100 - curHp) / 3))
						skill = getHealSkill((int) curHp);

					// проверка речарджа
					if(skill == null)
					{
						double curMp = owner.getCurrentMpPercents();
						if(Rnd.chance((100 - curMp) / 2))
							skill = getManaHealSkill((int) curMp);
					}

					if(skill != null && skill.checkCondition(PetBabyInstance.this, owner, false, !isFollowMode(), true))
					{
						setTarget(owner);
						getAI().Cast(skill, owner, false, !isFollowMode());
						return skill;
					}
				}

				if(owner.isInOfflineMode() || owner.getAbnormalList().contains(5771))
					return null;

				outer: for(Skill buff : getBuffs())
				{
					if(getCurrentMp() < buff.getMpConsume2())
						continue;

					for(Abnormal ef : owner.getAbnormalList())
						if(checkEffect(ef, buff))
							continue outer;

					if(buff.checkCondition(PetBabyInstance.this, owner, false, !isFollowMode(), true))
					{
						setTarget(owner);
						getAI().Cast(buff, owner, false, !isFollowMode());
						return buff;
					}
					return null;
				}
			}
		}
		catch(Throwable e)
		{
			_log.warn("Pet [#" + getNpcId() + "] a buff task error has occurred: " + e);
			_log.error("", e);
		}
		return null;
	}

	/**
	 * Возвращает true если эффект для скилла уже есть и заново накладывать не надо
	 */
	private boolean checkEffect(Abnormal abnormal, Skill skill)
	{
		if(abnormal == null)
			return false;

		if(abnormal.checkBlockedAbnormalType(skill.getAbnormalType()))
			return true;

		if(!AbnormalList.checkAbnormalType(abnormal.getSkill(), skill)) // такого скилла нет
			return false;
		if(abnormal.getAbnormalLvl() < skill.getAbnormalLvl()) // старый слабее
			return false;
		if(abnormal.getTimeLeft() > 10) // старый не слабее и еще не кончается - ждем
			return true;
		return false;
	}

	public synchronized void stopBuffTask()
	{
		if(_actionTask != null)
		{
			_actionTask.cancel(false);
			_actionTask = null;
		}
	}

	public synchronized void startBuffTask()
	{
		if(_actionTask != null)
			stopBuffTask();

		if(_actionTask == null && !isDead())
			_actionTask = ThreadPoolManager.getInstance().schedule(new ActionTask(), 5000);
	}

	public boolean isBuffEnabled()
	{
		return _buffEnabled;
	}

	public void triggerBuff()
	{
		_buffEnabled = !_buffEnabled;
	}

	@Override
	protected void onDeath(Creature killer)
	{
		stopBuffTask();
		super.onDeath(killer);
	}

	@Override
	public void doRevive()
	{
		super.doRevive();
		startBuffTask();
	}

	@Override
	public void unSummon(boolean logout)
	{
		stopBuffTask();
		super.unSummon(logout);
	}

	public int getHealLevel()
	{
		return Math.min(Math.max((getLevel() - getMinLevel()) / ((80 - getMinLevel()) / 12), 1), 12);
	}

	public int getRechargeLevel()
	{
		return Math.min(Math.max((getLevel() - getMinLevel()) / ((80 - getMinLevel()) / 8), 1), 8);
	}

	public int getBuffLevel()
	{
		if(PetDataHolder.isSpecialPet(getNpcId()))
		{
			if(getLevel() < 10)
				return 0;
			else if(getLevel() < 20)
				return 1;
			else if(getLevel() < 30)
				return 2;
			else if(getLevel() < 40)
				return 3;
			else if(getLevel() < 50)
				return 4;
			else if(getLevel() < 60)
				return 5;
			else if(getLevel() < 70)
				return 6;
			else if(getLevel() >= 70)
				return 7;
		}
		else
		{
			if(getLevel() < 60)
				return 0;
			else if(getLevel() < 65)
				return 1;
			else if(getLevel() < 70)
				return 2;
			else if(getLevel() < 75)
				return 3;
			else if(getLevel() < 80)
				return 4;
			else if(getLevel() >= 80)
				return 5;
		}
		return 0;
	}

	@Override
	public int getSoulshotConsumeCount()
	{
		return 1;
	}

	@Override
	public int getSpiritshotConsumeCount()
	{
		return 1;
	}
}