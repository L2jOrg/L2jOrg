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
package org.l2j.gameserver.mobius.gameserver.model.actor.instance;

import org.l2j.gameserver.mobius.gameserver.enums.InstanceType;
import org.l2j.gameserver.mobius.gameserver.model.actor.templates.L2NpcTemplate;

/**
 * @author Gnacik
 */
public class L2EventMonsterInstance extends L2MonsterInstance
{
	// Block offensive skills usage on event mobs
	// mainly for AoE skills, disallow kill many event mobs
	// with one skill
	public boolean block_skill_attack = false;
	
	// Event mobs should drop items to ground
	// but item pickup must be protected to killer
	// Todo: Some mobs need protect drop for spawner
	public boolean drop_on_ground = false;
	
	public L2EventMonsterInstance(L2NpcTemplate template)
	{
		super(template);
		setInstanceType(InstanceType.L2EventMobInstance);
	}
	
	public void eventSetBlockOffensiveSkills(boolean value)
	{
		block_skill_attack = value;
	}
	
	public void eventSetDropOnGround(boolean value)
	{
		drop_on_ground = value;
	}
	
	public boolean eventDropOnGround()
	{
		return drop_on_ground;
	}
	
	public boolean eventSkillAttackBlocked()
	{
		return block_skill_attack;
	}
}