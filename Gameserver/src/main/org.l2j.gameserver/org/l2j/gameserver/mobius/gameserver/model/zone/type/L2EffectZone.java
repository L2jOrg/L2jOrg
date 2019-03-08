/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.mobius.gameserver.model.zone.type;

import org.l2j.commons.concurrent.ThreadPool;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.mobius.gameserver.data.xml.impl.SkillData;
import org.l2j.gameserver.mobius.gameserver.enums.InstanceType;
import org.l2j.gameserver.mobius.gameserver.instancemanager.ZoneManager;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Character;
import org.l2j.gameserver.mobius.gameserver.model.skills.Skill;
import org.l2j.gameserver.mobius.gameserver.model.zone.AbstractZoneSettings;
import org.l2j.gameserver.mobius.gameserver.model.zone.L2ZoneType;
import org.l2j.gameserver.mobius.gameserver.model.zone.TaskZoneSettings;
import org.l2j.gameserver.mobius.gameserver.model.zone.ZoneId;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.EtcStatusUpdate;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * another type of damage zone with skills
 * @author kerberos
 */
public final class L2EffectZone extends L2ZoneType
{
	int _chance;
	private int _initialDelay;
	private int _reuse;
	protected boolean _bypassConditions;
	private boolean _isShowDangerIcon;
	protected volatile Map<Integer, Integer> _skills;
	
	public L2EffectZone(int id)
	{
		super(id);
		_chance = 100;
		_initialDelay = 0;
		_reuse = 30000;
		setTargetType(InstanceType.L2Playable); // default only playable
		_bypassConditions = false;
		_isShowDangerIcon = true;
		AbstractZoneSettings settings = ZoneManager.getSettings(getName());
		if (settings == null)
		{
			settings = new TaskZoneSettings();
		}
		setSettings(settings);
	}
	
	@Override
	public TaskZoneSettings getSettings()
	{
		return (TaskZoneSettings) super.getSettings();
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		switch (name)
		{
			case "chance":
			{
				_chance = Integer.parseInt(value);
				break;
			}
			case "initialDelay":
			{
				_initialDelay = Integer.parseInt(value);
				break;
			}
			case "reuse":
			{
				_reuse = Integer.parseInt(value);
				break;
			}
			case "bypassSkillConditions":
			{
				_bypassConditions = Boolean.parseBoolean(value);
				break;
			}
			case "maxDynamicSkillCount":
			{
				_skills = new ConcurrentHashMap<>(Integer.parseInt(value));
				break;
			}
			case "showDangerIcon":
			{
				_isShowDangerIcon = Boolean.parseBoolean(value);
				break;
			}
			case "skillIdLvl":
			{
				final String[] propertySplit = value.split(";");
				_skills = new ConcurrentHashMap<>(propertySplit.length);
				for (String skill : propertySplit)
				{
					final String[] skillSplit = skill.split("-");
					if (skillSplit.length != 2)
					{
						LOGGER.warning(getClass().getSimpleName() + ": invalid config property -> skillsIdLvl \"" + skill + "\"");
					}
					else
					{
						try
						{
							_skills.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
						}
						catch (NumberFormatException nfe)
						{
							if (!skill.isEmpty())
							{
								LOGGER.warning(getClass().getSimpleName() + ": invalid config property -> skillsIdLvl \"" + skillSplit[0] + "\"" + skillSplit[1]);
							}
						}
					}
				}
				break;
			}
			default:
			{
				super.setParameter(name, value);
			}
		}
	}
	
	@Override
	protected void onEnter(L2Character character)
	{
		if (_skills != null)
		{
			if (getSettings().getTask() == null)
			{
				synchronized (this)
				{
					if (getSettings().getTask() == null)
					{
						getSettings().setTask(ThreadPoolManager.getInstance().scheduleAtFixedRate(new ApplySkill(), _initialDelay, _reuse));
					}
				}
			}
		}
		
		if (character.isPlayer())
		{
			character.setInsideZone(ZoneId.ALTERED, true);
			if (_isShowDangerIcon)
			{
				character.setInsideZone(ZoneId.DANGER_AREA, true);
				character.sendPacket(new EtcStatusUpdate(character.getActingPlayer()));
			}
		}
	}
	
	@Override
	protected void onExit(L2Character character)
	{
		if (character.isPlayer())
		{
			character.setInsideZone(ZoneId.ALTERED, false);
			if (_isShowDangerIcon)
			{
				character.setInsideZone(ZoneId.DANGER_AREA, false);
				if (!character.isInsideZone(ZoneId.DANGER_AREA))
				{
					character.sendPacket(new EtcStatusUpdate(character.getActingPlayer()));
				}
			}
		}
		
		if (_characterList.isEmpty() && (getSettings().getTask() != null))
		{
			getSettings().clear();
		}
	}
	
	public int getChance()
	{
		return _chance;
	}
	
	public void addSkill(int skillId, int skillLvL)
	{
		if (skillLvL < 1) // remove skill
		{
			removeSkill(skillId);
			return;
		}
		
		if (_skills == null)
		{
			synchronized (this)
			{
				if (_skills == null)
				{
					_skills = new ConcurrentHashMap<>(3);
				}
			}
		}
		_skills.put(skillId, skillLvL);
	}
	
	public void removeSkill(int skillId)
	{
		if (_skills != null)
		{
			_skills.remove(skillId);
		}
	}
	
	public void clearSkills()
	{
		if (_skills != null)
		{
			_skills.clear();
		}
	}
	
	public int getSkillLevel(int skillId)
	{
		if ((_skills == null) || !_skills.containsKey(skillId))
		{
			return 0;
		}
		return _skills.get(skillId);
	}
	
	private final class ApplySkill implements Runnable
	{
		protected ApplySkill()
		{
			if (_skills == null)
			{
				throw new IllegalStateException("No skills defined.");
			}
		}
		
		@Override
		public void run()
		{
			if (isEnabled())
			{
				getCharactersInside().forEach(character ->
				{
					if ((character != null) && !character.isDead() && (Rnd.get(100) < _chance))
					{
						for (Entry<Integer, Integer> e : _skills.entrySet())
						{
							final Skill skill = SkillData.getInstance().getSkill(e.getKey(), e.getValue());
							if ((skill != null) && (_bypassConditions || skill.checkCondition(character, character)))
							{
								if (character.getAffectedSkillLevel(skill.getId()) < skill.getLevel())
								{
									skill.activateSkill(character, character);
								}
							}
						}
					}
				});
			}
		}
	}
}