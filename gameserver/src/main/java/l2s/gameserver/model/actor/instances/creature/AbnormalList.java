package l2s.gameserver.model.actor.instances.creature;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import l2s.gameserver.Config;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.skills.AbnormalType;
import l2s.gameserver.skills.EffectType;
import l2s.gameserver.skills.EffectUseType;
import l2s.gameserver.skills.effects.Effect;
import l2s.gameserver.skills.skillclasses.Transformation;
import l2s.gameserver.stats.Stats;
import l2s.gameserver.stats.funcs.FuncTemplate;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class AbnormalList implements Iterable<Abnormal>
{
	public static final int NONE_SLOT_TYPE = -1;
	public static final int BUFF_SLOT_TYPE = 0;
	public static final int MUSIC_SLOT_TYPE = 1;
	public static final int TRIGGER_SLOT_TYPE = 2;
	public static final int DEBUFF_SLOT_TYPE = 3;

	private final Collection<Abnormal> _abnormals = new ConcurrentLinkedQueue<Abnormal>();

	private final Creature _owner;

	public AbnormalList(Creature owner)
	{
		_owner = owner;
	}

	@Override
	public Iterator<Abnormal> iterator()
	{
		return _abnormals.iterator();
	}

	public boolean contains(int skillId)
	{
		if(_abnormals.isEmpty())
			return false;

		for(Abnormal abnormal : _abnormals)
		{
			if(abnormal.getSkill().getId() == skillId)
				return true;
		}
		return false;
	}

	public boolean contains(Skill skill)
	{
		if(skill == null)
			return false;
		return contains(skill.getId());
	}

	public boolean contains(AbnormalType type)
	{
		if(type == null)
			return false;

		for(Abnormal abnormal : _abnormals)
		{
			if(abnormal.getAbnormalType() == type)
				return true;
		}
		return false;
	}

	public Collection<Abnormal> values()
	{
		return _abnormals;
	}

	public Abnormal[] toArray()
	{
		return _abnormals.toArray(new Abnormal[_abnormals.size()]);
	}

	public int getCount(int skillId)
	{
		int result = 0;

		if(_abnormals.isEmpty())
			return 0;

		for(Abnormal abnormal : _abnormals)
		{
			if(abnormal.getSkill().getId() == skillId)
				result++;
		}
		return result;
	}

	public int getCount(Skill skill)
	{
		if(skill == null)
			return 0;
		return getCount(skill.getId());
	}

	public int getCount(AbnormalType type)
	{
		int result = 0;

		if(_abnormals.isEmpty())
			return 0;

		for(Abnormal abnormal : _abnormals)
		{
			if(type == abnormal.getAbnormalType())
				result++;
		}
		return result;
	}

	public int size()
	{
		return _abnormals.size();
	}

	public boolean isEmpty()
	{
		return _abnormals.isEmpty();
	}

	private void checkSlotLimit(Abnormal newAbnormal)
	{
		if(_abnormals.isEmpty())
			return;

		int slotType = getSlotType(newAbnormal);
		if(slotType == NONE_SLOT_TYPE)
			return;

		int size = 0;
		TIntSet skillIds = new TIntHashSet();
		for(Abnormal e : _abnormals)
		{
			if(e.getSkill().equals(newAbnormal.getSkill())) // мы уже имеем эффект от этого скилла
				return;

			if(!skillIds.contains(e.getSkill().getId()))
			{
				int subType = getSlotType(e);
				if(subType == slotType)
				{
					size++;
					skillIds.add(e.getSkill().getId());
				}
			}
		}

		int limit = 0;
		switch(slotType)
		{
			case BUFF_SLOT_TYPE:
				limit = _owner.getBuffLimit();
				break;
			case MUSIC_SLOT_TYPE:
				limit = Config.ALT_MUSIC_LIMIT;
				break;
			case DEBUFF_SLOT_TYPE:
				limit = Config.ALT_DEBUFF_LIMIT;
				break;
			case TRIGGER_SLOT_TYPE:
				limit = Config.ALT_TRIGGER_LIMIT;
				break;
		}

		if(size < limit)
			return;

		for(Abnormal e : _abnormals)
		{
			if(getSlotType(e) == slotType)
			{
				stop(e.getSkill().getId());
				break;
			}
		}
	}

	public static int getSlotType(Abnormal e)
	{
		if(e.getSkill().getBuffSlotType() == -2)
		{
			if(e.isHidden() || e.getSkill().isPassive() || e.getSkill().isToggle() || e.getSkill() instanceof Transformation || e.checkAbnormalType(AbnormalType.hp_recover))
				return NONE_SLOT_TYPE;
			else if(e.isOffensive())
				return DEBUFF_SLOT_TYPE;
			else if(e.getSkill().isMusic())
				return MUSIC_SLOT_TYPE;
			else if(e.getSkill().isTrigger())
				return TRIGGER_SLOT_TYPE;
			else
				return BUFF_SLOT_TYPE;
		}

		return e.getSkill().getBuffSlotType();
	}

	public static boolean checkAbnormalType(Skill skill1, Skill skill2)
	{
		AbnormalType abnormalType1 = skill1.getAbnormalType();
		if(abnormalType1 == AbnormalType.none)
			return false;

		AbnormalType abnormalType2 = skill2.getAbnormalType();
		if(abnormalType2 == AbnormalType.none)
			return false;

		return abnormalType1 == abnormalType2;
	}

	public synchronized boolean add(Abnormal abnormal)
	{
		if(!abnormal.isTimeLeft())
			return false;

		Skill skill = abnormal.getSkill();
		if(skill == null)
			return false;

		//TODO [G1ta0] затычка на статы повышающие HP/MP/CP
		double hp = _owner.getCurrentHp();
		double mp = _owner.getCurrentMp();
		double cp = _owner.getCurrentCp();

		boolean success = false;
		boolean suspended = false;

		_owner.getStatsRecorder().block(); // Для того, чтобы не флудить пакетами.

		if(!_abnormals.isEmpty() && (abnormal.isOfUseType(EffectUseType.NORMAL) || abnormal.isOfUseType(EffectUseType.SELF)))
		{
			if(skill.isToggle())
			{
				if(contains(skill))
				{
					_owner.getStatsRecorder().unblock();
					return false;
				}
				if(skill.isToggleGrouped() && skill.getToggleGroupId() > 0)
				{
					for(Abnormal a : _abnormals)
					{
						if(!a.getSkill().isToggleGrouped())
							continue;

						if(skill.getToggleGroupId() != a.getSkill().getToggleGroupId())
							continue;

						a.exit();
					}
				}
			}
			else
			{
				AbnormalType abnormalType = abnormal.getAbnormalType();
				if(abnormalType == AbnormalType.none)
				{
					// Удаляем такие же эффекты
					for(Abnormal a : _abnormals)
					{
						if(a.getSkill().getId() == skill.getId())
						{
							// Если оставшаяся длительность старого эффекта больше чем длительность нового, то оставляем старый.
							if(skill.getLevel() >= a.getSkill().getLevel())
								a.exit();
							else
							{
								_owner.getStatsRecorder().unblock();
								return false;
							}
						}
					}
				}
				else
				{
					// Проверяем, нужно ли накладывать эффект, при совпадении StackType.
					// Новый эффект накладывается только в том случае, если у него больше StackOrder и больше длительность.
					// Если условия подходят - удаляем старый.
					for(Abnormal a : _abnormals)
					{
						if(a.checkBlockedAbnormalType(abnormalType))
						{
							_owner.getStatsRecorder().unblock();
							return false;
						}

						if(abnormal.checkBlockedAbnormalType(a.getAbnormalType()))
						{
							a.exit();
							continue;
						}

						if(a.getEffector() != abnormal.getEffector() && abnormal.getAbnormalType().isStackable())
							continue;

						if(!checkAbnormalType(a.getSkill(), skill))
							continue;

						if(a.getSkill().isIrreplaceableBuff())
						{
							_owner.getStatsRecorder().unblock();
							return false;
						}

						if(abnormal.getAbnormalLvl() < a.getAbnormalLvl())
						{
							if(a.getSkill().isAbnormalInstant() && !skill.isAbnormalInstant())
							{
								suspended = true;
								break;
							}
							else
							{
								_owner.getStatsRecorder().unblock();
								return false;
							}
						}
						else
						{
							if(!a.getSkill().isAbnormalInstant() && skill.isAbnormalInstant())
								a.suspend();
							else
								a.exit();
							break;
						}
					}
				}

				// Проверяем на лимиты бафов/дебафов
				checkSlotLimit(abnormal);
			}
		}

		if(_abnormals.add(abnormal))
		{
			if(!suspended)
				abnormal.start(); // Запускаем эффект
			else
				abnormal.suspend();
			success = true;
		}

		for(Effect effect : abnormal.getEffects())
		{
			for(FuncTemplate ft : effect.getTemplate().getAttachedFuncs())
			{
				if(ft._stat == Stats.MAX_HP)
					_owner.setCurrentHp(hp, false);
				else if(ft._stat == Stats.MAX_MP)
					_owner.setCurrentMp(mp);
				else if(ft._stat == Stats.MAX_CP)
					_owner.setCurrentCp(cp);
			}
		}

		_owner.getStatsRecorder().unblock();
		_owner.updateStats();
		_owner.updateAbnormalIcons();

		return success;
	}

	/**
	 * Удаление эффекта из списка
	 * @param abnormal эффект для удаления
	 */
	public void remove(Abnormal abnormal)
	{
		if(abnormal == null)
			return;

		if(_abnormals.remove(abnormal))
		{
			if(abnormal.getSkill().isAbnormalInstant())
			{
				for(Abnormal a : _abnormals)
				{
					if(a.getAbnormalType() == abnormal.getAbnormalType())
					{
						if(a.isSuspended())
						{
							a.start();
							break;
						}
					}
				}
			}
			_owner.updateStats();
			_owner.updateAbnormalIcons();
		}
	}

	public int stopAll()
	{
		if(_abnormals.isEmpty())
			return 0;

		int removed = 0;
		for(Abnormal a : _abnormals)
		{
			if(_owner.isSpecialAbnormal(a.getSkill()))
				continue;

			a.exit();
			removed++;
		}

		return removed;
	}

	public int stop(int skillId)
	{
		if(_abnormals.isEmpty())
			return 0;

		int removed = 0;
		for(Abnormal a : _abnormals)
		{
			if(a.getSkill().getId() == skillId)
			{
				a.exit();
				removed++;
			}
		}

		return removed;
	}

	public int stop(TIntSet skillIds)
	{
		if(_abnormals.isEmpty())
			return 0;

		int removed = 0;
		for(Abnormal a : _abnormals)
		{
			if(skillIds.contains(a.getSkill().getId()))
			{
				a.exit();
				removed++;
			}
		}

		return removed;
	}

	public int stop(AbnormalType type)
	{
		if(_abnormals.isEmpty())
			return 0;

		int removed = 0;
		for(Abnormal a : _abnormals)
		{
			if(a.getAbnormalType() == type)
			{
				a.exit();
				removed++;
			}
		}

		return removed;
	}

	public int stop(Skill skill)
	{
		if(skill == null)
			return 0;

		return stop(skill.getId());
	}

	/**
	 * Находит скиллы с указанным эффектом, и останавливает у этих скиллов все эффекты (не только указанный).
	 */
	@Deprecated
	public int stop(EffectType type)
	{
		if(_abnormals.isEmpty())
			return 0;

		TIntSet skillIds = new TIntHashSet();
		for(Abnormal abnormal : _abnormals)
		{
			for(Effect effect : abnormal.getEffects())
			{
				if(effect.getEffectType() == type)
				{
					skillIds.add(effect.getSkill().getId());
					break;
				}
			}
		}

		int removed = 0;
		for(Abnormal abnormal : _abnormals)
		{
			if(skillIds.contains(abnormal.getSkill().getId()))
			{
				abnormal.exit();
				removed++;
			}
		}

		return removed;
	}
}