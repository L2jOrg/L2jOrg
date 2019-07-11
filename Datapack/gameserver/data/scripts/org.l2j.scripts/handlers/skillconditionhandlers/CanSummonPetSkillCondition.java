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

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.sql.impl.CharSummonTable;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.skills.ISkillCondition;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.taskmanager.AttackStanceTaskManager;

/**
 * @author Sdw
 */
public class CanSummonPetSkillCondition implements ISkillCondition
{
	public CanSummonPetSkillCondition(StatsSet params)
	{
		
	}
	
	@Override
	public boolean canUse(L2Character caster, Skill skill, L2Object target)
	{
		final Player player = caster.getActingPlayer();
		if ((player == null) || player.isSpawnProtected() || player.isTeleportProtected())
		{
			return false;
		}
		
		boolean canSummon = true;
		
		if (Config.RESTORE_PET_ON_RECONNECT && CharSummonTable.getInstance().getPets().containsKey(player.getObjectId()))
		{
			player.sendPacket(SystemMessageId.YOU_MAY_NOT_SUMMON_MULTIPLE_PETS_AT_THE_SAME_TIME);
			canSummon = false;
		}
		else if (player.hasPet())
		{
			player.sendPacket(SystemMessageId.YOU_MAY_NOT_SUMMON_MULTIPLE_PETS_AT_THE_SAME_TIME);
			canSummon = false;
		}
		else if ((player.getActiveTradeList() != null) || player.hasItemRequest() || (player.getPrivateStoreType() != PrivateStoreType.NONE))
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_SUMMON_DURING_A_TRADE_OR_WHILE_USING_A_PRIVATE_STORE);
			canSummon = false;
		}
		else if (AttackStanceTaskManager.getInstance().hasAttackStanceTask(player))
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_SUMMON_DURING_COMBAT);
			canSummon = false;
		}
		else if (player.isInAirShip())
		{
			player.sendPacket(SystemMessageId.A_SERVITOR_CANNOT_BE_SUMMONED_WHILE_ON_AN_AIRSHIP);
			canSummon = false;
		}
		else if (player.isFlyingMounted() || player.isMounted() || player.inObserverMode() || player.isTeleporting())
		{
			canSummon = false;
		}
		
		return canSummon;
	}
}
