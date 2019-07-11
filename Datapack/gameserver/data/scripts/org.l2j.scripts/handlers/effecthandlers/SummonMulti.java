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
package handlers.effecthandlers;

import org.l2j.gameserver.data.xml.impl.ExperienceData;
import org.l2j.gameserver.data.xml.impl.NpcData;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.instance.Servitor;
import org.l2j.gameserver.model.actor.templates.L2NpcTemplate;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.model.skills.Skill;

/**
 * SummonMulti effect implementation.
 * @author UnAfraid
 */
public final class SummonMulti extends AbstractEffect
{
	private final int _npcId;
	private final float _expMultiplier;
	private final ItemHolder _consumeItem;
	private final int _lifeTime;
	private final int _consumeItemInterval;
	private final int _summonPoints;
	
	public SummonMulti(StatsSet params)
	{
		_npcId = params.getInt("npcId");
		_expMultiplier = params.getFloat("expMultiplier", 1);
		_consumeItem = new ItemHolder(params.getInt("consumeItemId", 0), params.getInt("consumeItemCount", 1));
		_consumeItemInterval = params.getInt("consumeItemInterval", 0);
		_lifeTime = params.getInt("lifeTime", 3600) > 0 ? params.getInt("lifeTime", 3600) * 1000 : -1;
		_summonPoints = params.getInt("summonPoints", 0);
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(L2Character effector, L2Character effected, Skill skill, L2ItemInstance item)
	{
		if (!effector.isPlayer())
		{
			return;
		}
		
		final Player player = effected.getActingPlayer();
		if ((player.getSummonPoints() + _summonPoints) > player.getMaxSummonPoints())
		{
			return;
		}
		final L2NpcTemplate template = NpcData.getInstance().getTemplate(_npcId);
		final Servitor summon = new Servitor(template, player);
		final int consumeItemInterval = (_consumeItemInterval > 0 ? _consumeItemInterval : (template.getRace() != Race.SIEGE_WEAPON ? 240 : 60)) * 1000;
		
		summon.setName(template.getName());
		summon.setTitle(effected.getName());
		summon.setReferenceSkill(skill.getId());
		summon.setExpMultiplier(_expMultiplier);
		summon.setLifeTime(_lifeTime);
		summon.setItemConsume(_consumeItem);
		summon.setItemConsumeInterval(consumeItemInterval);
		
		if (summon.getLevel() >= ExperienceData.getInstance().getMaxLevel())
		{
			summon.getStat().setExp(ExperienceData.getInstance().getExpForLevel(ExperienceData.getInstance().getMaxLevel() - 1));
			LOGGER.warn(": (" + summon.getName() + ") NpcID: " + summon.getId() + " has a level above " + ExperienceData.getInstance().getMaxLevel() + ". Please rectify.");
		}
		else
		{
			summon.getStat().setExp(ExperienceData.getInstance().getExpForLevel(summon.getLevel() % ExperienceData.getInstance().getMaxLevel()));
		}
		
		// Summons must have their master buffs upon spawn.
		for (BuffInfo effect : player.getEffectList().getEffects())
		{
			final Skill sk = effect.getSkill();
			if (!sk.isBad())
			{
				sk.applyEffects(player, summon, false, effect.getTime());
			}
		}
		
		summon.setCurrentHp(summon.getMaxHp());
		summon.setCurrentMp(summon.getMaxMp());
		summon.setHeading(player.getHeading());
		summon.setSummonPoints(_summonPoints);
		
		player.addServitor(summon);
		
		summon.setShowSummonAnimation(true);
		summon.setRunning();
		summon.spawnMe();
	}
}
