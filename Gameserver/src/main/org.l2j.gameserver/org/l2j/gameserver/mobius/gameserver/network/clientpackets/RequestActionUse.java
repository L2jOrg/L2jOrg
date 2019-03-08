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
package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.commons.network.PacketReader;
import org.l2j.gameserver.mobius.gameserver.data.xml.impl.ActionData;
import org.l2j.gameserver.mobius.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.mobius.gameserver.handler.IPlayerActionHandler;
import org.l2j.gameserver.mobius.gameserver.handler.PlayerActionHandler;
import org.l2j.gameserver.mobius.gameserver.model.ActionDataHolder;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.mobius.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.mobius.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ExBasicActionList;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.RecipeShopManageList;

import java.util.Arrays;
import java.util.logging.Logger;

/**
 * This class manages the action use request packet.
 * @author Zoey76
 */
public final class RequestActionUse extends IClientIncomingPacket
{
	private static final Logger LOGGER = Logger.getLogger(RequestActionUse.class.getName());
	
	private int _actionId;
	private boolean _ctrlPressed;
	private boolean _shiftPressed;
	
	@Override
	public void readImpl(ByteBuffer packet)
	{
		_actionId = packet.getInt();
		_ctrlPressed = (packet.getInt() == 1);
		_shiftPressed = (packet.get() == 1);
		return true;
	}
	
	@Override
	public void runImpl()
	{
		final L2PcInstance activeChar = client.getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		// Don't do anything if player is dead or confused
		if ((activeChar.isFakeDeath() && (_actionId != 0)) || activeChar.isDead() || activeChar.isControlBlocked())
		{
			client.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final BuffInfo info = activeChar.getEffectList().getFirstBuffInfoByAbnormalType(AbnormalType.BOT_PENALTY);
		if (info != null)
		{
			for (AbstractEffect effect : info.getEffects())
			{
				if (!effect.checkCondition(_actionId))
				{
					activeChar.sendPacket(SystemMessageId.YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_YOUR_ACTIONS_HAVE_BEEN_RESTRICTED);
					activeChar.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
			}
		}
		
		// Don't allow to do some action if player is transformed
		if (activeChar.isTransformed())
		{
			final int[] allowedActions = activeChar.isTransformed() ? ExBasicActionList.ACTIONS_ON_TRANSFORM : ExBasicActionList.DEFAULT_ACTION_LIST;
			if (!(Arrays.binarySearch(allowedActions, _actionId) >= 0))
			{
				client.sendPacket(ActionFailed.STATIC_PACKET);
				LOGGER.warning("Player " + activeChar + " used action which he does not have! Id = " + _actionId + " transform: " + activeChar.getTransformation().orElse(null));
				return;
			}
		}
		
		final ActionDataHolder actionHolder = ActionData.getInstance().getActionData(_actionId);
		if (actionHolder != null)
		{
			final IPlayerActionHandler actionHandler = PlayerActionHandler.getInstance().getHandler(actionHolder.getHandler());
			if (actionHandler != null)
			{
				actionHandler.useAction(activeChar, actionHolder, _ctrlPressed, _shiftPressed);
				return;
			}
			LOGGER.warning("Couldnt find handler with name: " + actionHolder.getHandler());
			return;
		}
		
		switch (_actionId)
		{
			case 51: // General Manufacture
			{
				// Player shouldn't be able to set stores if he/she is alike dead (dead or fake death)
				if (activeChar.isAlikeDead())
				{
					client.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
				
				if (activeChar.isSellingBuffs())
				{
					client.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
				
				if (activeChar.getPrivateStoreType() != PrivateStoreType.NONE)
				{
					activeChar.setPrivateStoreType(PrivateStoreType.NONE);
					activeChar.broadcastUserInfo();
				}
				if (activeChar.isSitting())
				{
					activeChar.standUp();
				}
				
				client.sendPacket(new RecipeShopManageList(activeChar, false));
				break;
			}
			default:
			{
				LOGGER.warning(activeChar.getName() + ": unhandled action type " + _actionId);
				break;
			}
		}
	}
}
