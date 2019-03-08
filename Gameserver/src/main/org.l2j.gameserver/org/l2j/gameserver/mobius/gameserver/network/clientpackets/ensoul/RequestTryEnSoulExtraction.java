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
package org.l2j.gameserver.mobius.gameserver.network.clientpackets.ensoul;

import org.l2j.gameserver.mobius.gameserver.data.xml.impl.EnsoulData;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.ensoul.EnsoulOption;
import org.l2j.gameserver.mobius.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.mobius.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ensoul.ExEnSoulExtractionResult;

import java.nio.ByteBuffer;
import java.util.Collection;

/**
 * @author Mobius
 */
public class RequestTryEnSoulExtraction extends IClientIncomingPacket
{
	private int _itemObjectId;
	private int _type;
	private int _position;
	
	@Override
	public void readImpl(ByteBuffer packet)
	{
		_itemObjectId = packet.getInt();
		_type = packet.get();
		_position = packet.get() - 1;
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance player = client.getActiveChar();
		if (player == null)
		{
			return;
		}
		
		final L2ItemInstance item = player.getInventory().getItemByObjectId(_itemObjectId);
		if (item == null)
		{
			return;
		}
		
		EnsoulOption option = null;
		if (_type == 1)
		{
			option = item.getSpecialAbility(_position);
		}
		if (_type == 2)
		{
			option = item.getAdditionalSpecialAbility(_position);
		}
		if (option == null)
		{
			return;
		}
		
		final Collection<ItemHolder> removalFee = EnsoulData.getInstance().getRemovalFee(item.getItem().getCrystalType());
		if (removalFee.isEmpty())
		{
			return;
		}
		
		// Check if player has required items.
		for (ItemHolder itemHolder : removalFee)
		{
			if (player.getInventory().getInventoryItemCount(itemHolder.getId(), -1) < itemHolder.getCount())
			{
				player.sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT);
				player.sendPacket(new ExEnSoulExtractionResult(false, item));
				return;
			}
		}
		
		// Take required items.
		for (ItemHolder itemHolder : removalFee)
		{
			player.destroyItemByItemId("Rune Extract", itemHolder.getId(), itemHolder.getCount(), player, true);
		}
		
		// Remove equipped rune.
		item.removeSpecialAbility(_position, _type);
		final InventoryUpdate iu = new InventoryUpdate();
		iu.addModifiedItem(item);
		
		// Add rune in player inventory.
		final int runeId = EnsoulData.getInstance().getStone(_type, option.getId());
		if (runeId > 0)
		{
			iu.addItem(player.addItem("Rune Extract", runeId, 1, player, true));
		}
		
		player.sendInventoryUpdate(iu);
		player.sendPacket(new ExEnSoulExtractionResult(true, item));
	}
}