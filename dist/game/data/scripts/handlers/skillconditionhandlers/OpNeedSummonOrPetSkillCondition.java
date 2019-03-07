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
package handlers.skillconditionhandlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.l2jmobius.gameserver.model.L2Object;
import com.l2jmobius.gameserver.model.StatsSet;
import com.l2jmobius.gameserver.model.actor.L2Character;
import com.l2jmobius.gameserver.model.actor.L2Summon;
import com.l2jmobius.gameserver.model.skills.ISkillCondition;
import com.l2jmobius.gameserver.model.skills.Skill;

/**
 * @author UnAfraid
 */
public class OpNeedSummonOrPetSkillCondition implements ISkillCondition
{
	private final List<Integer> _npcIds = new ArrayList<>();
	
	public OpNeedSummonOrPetSkillCondition(StatsSet params)
	{
		final List<String> npcIds = params.getList("npcIds", String.class);
		if (npcIds != null)
		{
			npcIds.stream().map(Integer::valueOf).forEach(_npcIds::add);
		}
	}
	
	@Override
	public boolean canUse(L2Character caster, Skill skill, L2Object target)
	{
		final L2Summon pet = caster.getPet();
		final Collection<L2Summon> summons = caster.getServitors().values();
		return ((pet != null) && _npcIds.stream().anyMatch(npcId -> npcId == pet.getId())) || summons.stream().anyMatch(summon -> _npcIds.contains(summon.getId()));
	}
}
