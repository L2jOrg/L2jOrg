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
import org.l2j.gameserver.mobius.gameserver.data.xml.impl.VariationData;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.mobius.gameserver.model.options.VariationFee;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ExPutCommissionResultForVariationMake;

/**
 * Format:(ch) dddd
 * @author -Wooden-
 */
public final class RequestConfirmGemStone extends AbstractRefinePacket
{
	private int _targetItemObjId;
	private int _mineralItemObjId;
	private int _feeItemObjId;
	private long _feeCount;
	
	@Override
	public void readImpl(ByteBuffer packet)
	{
		_targetItemObjId = packet.getInt();
		_mineralItemObjId = packet.getInt();
		_feeItemObjId = packet.getInt();
		_feeCount = packet.getLong();
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
		
		final L2ItemInstance targetItem = activeChar.getInventory().getItemByObjectId(_targetItemObjId);
		if (targetItem == null)
		{
			return;
		}
		
		final L2ItemInstance refinerItem = activeChar.getInventory().getItemByObjectId(_mineralItemObjId);
		if (refinerItem == null)
		{
			return;
		}
		
		final L2ItemInstance gemStoneItem = activeChar.getInventory().getItemByObjectId(_feeItemObjId);
		if (gemStoneItem == null)
		{
			return;
		}
		
		final VariationFee fee = VariationData.getInstance().getFee(targetItem.getId(), refinerItem.getId());
		if (!isValid(activeChar, targetItem, refinerItem, gemStoneItem, fee))
		{
			client.sendPacket(SystemMessageId.THIS_IS_NOT_A_SUITABLE_ITEM);
			return;
		}
		
		// Check for fee count
		if (_feeCount != fee.getItemCount())
		{
			client.sendPacket(SystemMessageId.GEMSTONE_QUANTITY_IS_INCORRECT);
			return;
		}
		
		client.sendPacket(new ExPutCommissionResultForVariationMake(_feeItemObjId, _feeCount, gemStoneItem.getId()));
	}
}
