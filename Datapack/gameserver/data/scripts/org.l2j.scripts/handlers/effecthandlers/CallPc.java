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

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.holders.SummonRequestHolder;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.olympiad.OlympiadManager;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ConfirmDlg;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

/**
 * Call Pc effect implementation.
 * @author Adry_85
 */
public final class CallPc extends AbstractEffect
{
	private final int _itemId;
	private final int _itemCount;
	
	public CallPc(StatsSet params)
	{
		_itemId = params.getInt("itemId", 0);
		_itemCount = params.getInt("itemCount", 0);
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item)
	{
		if (effector == effected)
		{
			return;
		}
		
		final Player target = effected.getActingPlayer();
		final Player activeChar = effector.getActingPlayer();
		if (checkSummonTargetStatus(target, activeChar))
		{
			if ((_itemId != 0) && (_itemCount != 0))
			{
				if (target.getInventory().getInventoryItemCount(_itemId, 0) < _itemCount)
				{
					final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_IS_REQUIRED_FOR_SUMMONING);
					sm.addItemName(_itemId);
					target.sendPacket(sm);
					return;
				}
				target.getInventory().destroyItemByItemId("Consume", _itemId, _itemCount, activeChar, target);
				final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_DISAPPEARED);
				sm.addItemName(_itemId);
				target.sendPacket(sm);
			}
			
			target.addScript(new SummonRequestHolder(activeChar, skill));
			final ConfirmDlg confirm = new ConfirmDlg(SystemMessageId.C1_WISHES_TO_SUMMON_YOU_FROM_S2_DO_YOU_ACCEPT);
			confirm.addString(activeChar.getName());
			confirm.addZoneName(activeChar.getX(), activeChar.getY(), activeChar.getZ());
			confirm.addTime(30000);
			confirm.addRequesterId(activeChar.getObjectId());
			target.sendPacket(confirm);
		}
	}
	
	public static boolean checkSummonTargetStatus(Player target, Creature activeChar)
	{
		if (target == activeChar)
		{
			return false;
		}
		
		if (target.isAlikeDead())
		{
			final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_DEAD_AT_THE_MOMENT_AND_CANNOT_BE_SUMMONED_OR_TELEPORTED);
			sm.addPcName(target);
			activeChar.sendPacket(sm);
			return false;
		}
		
		if (target.isInStoreMode())
		{
			final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_CURRENTLY_TRADING_OR_OPERATING_A_PRIVATE_STORE_AND_CANNOT_BE_SUMMONED_OR_TELEPORTED);
			sm.addPcName(target);
			activeChar.sendPacket(sm);
			return false;
		}
		
		if (target.isRooted() || target.isInCombat())
		{
			final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_ENGAGED_IN_COMBAT_AND_CANNOT_BE_SUMMONED_OR_TELEPORTED);
			sm.addPcName(target);
			activeChar.sendPacket(sm);
			return false;
		}
		
		if (target.isInOlympiadMode())
		{
			activeChar.sendPacket(SystemMessageId.A_USER_PARTICIPATING_IN_THE_OLYMPIAD_CANNOT_USE_SUMMONING_OR_TELEPORTING);
			return false;
		}
		
		if (target.isFlyingMounted() || target.isCombatFlagEquipped() || target.isInTraingCamp())
		{
			activeChar.sendPacket(SystemMessageId.YOU_CANNOT_USE_SUMMONING_OR_TELEPORTING_IN_THIS_AREA);
			return false;
		}
		
		if (target.inObserverMode() || OlympiadManager.getInstance().isRegisteredInComp(target) || target.isInsideZone(ZoneType.NO_SUMMON_FRIEND) || target.isInsideZone(ZoneType.JAIL))
		{
			final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_IN_AN_AREA_WHICH_BLOCKS_SUMMONING_OR_TELEPORTING);
			sm.addString(target.getName());
			activeChar.sendPacket(sm);
			return false;
		}
		
		final Instance instance = activeChar.getInstanceWorld();
		if ((instance != null) && !instance.isPlayerSummonAllowed())
		{
			activeChar.sendPacket(SystemMessageId.YOU_MAY_NOT_SUMMON_FROM_YOUR_CURRENT_LOCATION);
			return false;
		}
		return true;
	}
}