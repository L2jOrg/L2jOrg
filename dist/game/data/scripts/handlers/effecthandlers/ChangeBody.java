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

import java.util.HashSet;
import java.util.Set;

import com.l2jmobius.commons.util.Rnd;
import com.l2jmobius.gameserver.model.StatsSet;
import com.l2jmobius.gameserver.model.actor.L2Character;
import com.l2jmobius.gameserver.model.effects.AbstractEffect;
import com.l2jmobius.gameserver.model.holders.TemplateChanceHolder;
import com.l2jmobius.gameserver.model.skills.Skill;

/**
 * Transformation type effect, which disables attack or use of skills.
 * @author Nik
 */
public final class ChangeBody extends AbstractEffect
{
	private final Set<TemplateChanceHolder> _transformations = new HashSet<>();
	
	public ChangeBody(StatsSet params)
	{
		for (StatsSet item : params.getList("templates", StatsSet.class))
		{
			_transformations.add(new TemplateChanceHolder(item.getInt(".templateId"), item.getInt(".minChance"), item.getInt(".maxChance")));
		}
	}
	
	@Override
	public boolean canStart(L2Character effector, L2Character effected, Skill skill)
	{
		return !effected.isDoor();
	}
	
	@Override
	public void onStart(L2Character effector, L2Character effected, Skill skill)
	{
		final int chance = Rnd.get(100);
		//@formatter:off
		_transformations.stream()
			.filter(t -> t.calcChance(chance)) // Calculate chance for each transformation.
			.mapToInt(TemplateChanceHolder::getTemplateId)
			.findAny()
			.ifPresent(id -> effected.transform(id, false)); // Transform effected to whatever successful random template without adding skills.
		//@formatter:on
	}
	
	@Override
	public void onExit(L2Character effector, L2Character effected, Skill skill)
	{
		effected.stopTransformation(false);
	}
}
