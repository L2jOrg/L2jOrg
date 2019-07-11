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

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.ai.CtrlEvent;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Monster;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.network.SystemMessageId;

import java.util.Collection;

/**
 * @author Sdw
 */
public final class Plunder extends AbstractEffect
{
	public Plunder(StatsSet params)
	{
	}
	
	@Override
	public boolean calcSuccess(Creature effector, Creature effected, Skill skill)
	{
		final int lvlDifference = (effected.getLevel() - (skill.getMagicLevel() > 0 ? skill.getMagicLevel() : effector.getLevel()));
		final double lvlModifier = Math.pow(1.3, lvlDifference);
		float targetModifier = 1;
		if (effected.isAttackable() && !effected.isRaid() && !effected.isRaidMinion() && (effected.getLevel() >= Config.MIN_NPC_LVL_MAGIC_PENALTY) && (effector.getActingPlayer() != null) && ((effected.getLevel() - effector.getActingPlayer().getLevel()) >= 3))
		{
			final int lvlDiff = effected.getLevel() - effector.getActingPlayer().getLevel() - 2;
			if (lvlDiff >= Config.NPC_SKILL_CHANCE_PENALTY.size())
			{
				targetModifier = Config.NPC_SKILL_CHANCE_PENALTY.get(Config.NPC_SKILL_CHANCE_PENALTY.size() - 1);
			}
			else
			{
				targetModifier = Config.NPC_SKILL_CHANCE_PENALTY.get(lvlDiff);
			}
		}
		return Rnd.get(100) < (100 - Math.round((float) (lvlModifier * targetModifier)));
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item)
	{
		if (!effector.isPlayer())
		{
			return;
		}
		else if (!effected.isMonster() || effected.isDead())
		{
			effector.sendPacket(SystemMessageId.INVALID_TARGET);
			return;
		}
		
		final Monster monster = (Monster) effected;
		final Player player = effector.getActingPlayer();
		
		if (monster.isSpoiled())
		{
			effector.sendPacket(SystemMessageId.PLUNDER_SKILL_HAS_BEEN_ALREADY_USED_ON_THIS_TARGET);
			return;
		}

		monster.setPlundered(player);

		if (!player.getInventory().checkInventorySlotsAndWeight(monster.getSpoilLootItems(), false, false))
		{
			return;
		}

		final Collection<ItemHolder> items = monster.takeSweep();
		if (items != null)
		{
			for (ItemHolder sweepedItem : items)
			{
				final ItemHolder rewardedItem = new ItemHolder(sweepedItem.getId(), sweepedItem.getCount());
				final Party party = effector.getParty();
				if (party != null)
				{
					party.distributeItem(player, rewardedItem, true, monster);
				}
				else
				{
					player.addItem("Plunder", rewardedItem, effected, true);
				}
			}
		}
		monster.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, effector);
	}
}
