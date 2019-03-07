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
package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import com.l2jmobius.commons.network.PacketWriter;
import com.l2jmobius.gameserver.instancemanager.CastleManager;
import com.l2jmobius.gameserver.instancemanager.FortManager;
import com.l2jmobius.gameserver.model.L2Clan;
import com.l2jmobius.gameserver.model.L2SiegeClan;
import com.l2jmobius.gameserver.model.actor.L2Character;
import com.l2jmobius.gameserver.model.entity.Castle;
import com.l2jmobius.gameserver.model.entity.Fort;
import com.l2jmobius.gameserver.network.OutgoingPackets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author UnAfraid, Nos
 */
public class Die implements IClientOutgoingPacket
{
	private final int _objectId;
	private boolean _toVillage;
	private boolean _toClanHall;
	private boolean _toCastle;
	private boolean _toOutpost;
	private final boolean _isSweepable;
	private boolean _useFeather;
	private boolean _toFortress;
	private boolean _hideAnimation;
	private List<Integer> _items = null;
	private boolean _itemsEnabled;
	
	public Die(L2Character activeChar)
	{
		_objectId = activeChar.getObjectId();
		if (activeChar.isPlayer())
		{
			final L2Clan clan = activeChar.getActingPlayer().getClan();
			boolean isInCastleDefense = false;
			boolean isInFortDefense = false;
			
			L2SiegeClan siegeClan = null;
			final Castle castle = CastleManager.getInstance().getCastle(activeChar);
			final Fort fort = FortManager.getInstance().getFort(activeChar);
			if ((castle != null) && castle.getSiege().isInProgress())
			{
				siegeClan = castle.getSiege().getAttackerClan(clan);
				isInCastleDefense = (siegeClan == null) && castle.getSiege().checkIsDefender(clan);
			}
			else if ((fort != null) && fort.getSiege().isInProgress())
			{
				siegeClan = fort.getSiege().getAttackerClan(clan);
				isInFortDefense = (siegeClan == null) && fort.getSiege().checkIsDefender(clan);
			}
			
			_toVillage = activeChar.canRevive() && !activeChar.isPendingRevive();
			_toClanHall = (clan != null) && (clan.getHideoutId() > 0);
			_toCastle = ((clan != null) && (clan.getCastleId() > 0)) || isInCastleDefense;
			_toOutpost = ((siegeClan != null) && !isInCastleDefense && !isInFortDefense && !siegeClan.getFlag().isEmpty());
			_useFeather = activeChar.getAccessLevel().allowFixedRes() || activeChar.getInventory().haveItemForSelfResurrection();
			_toFortress = ((clan != null) && (clan.getFortId() > 0)) || isInFortDefense;
		}
		
		_isSweepable = activeChar.isAttackable() && activeChar.isSweepActive();
	}
	
	public void setHideAnimation(boolean val)
	{
		_hideAnimation = val;
	}
	
	public void addItem(int itemId)
	{
		if (_items == null)
		{
			_items = new ArrayList<>(8);
		}
		
		if (_items.size() < 8)
		{
			_items.add(itemId);
		}
		else
		{
			throw new IndexOutOfBoundsException("Die packet doesn't support more then 8 items!");
		}
	}
	
	public List<Integer> getItems()
	{
		return _items != null ? _items : Collections.emptyList();
	}
	
	public void setItemsEnabled(boolean val)
	{
		_itemsEnabled = val;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.DIE.writeId(packet);
		
		packet.writeD(_objectId);
		packet.writeD(_toVillage ? 0x01 : 0x00);
		packet.writeD(_toClanHall ? 0x01 : 0x00);
		packet.writeD(_toCastle ? 0x01 : 0x00);
		packet.writeD(_toOutpost ? 0x01 : 0x00);
		packet.writeD(_isSweepable ? 0x01 : 0x00);
		packet.writeD(_useFeather ? 0x01 : 0x00);
		packet.writeD(_toFortress ? 0x01 : 0x00);
		packet.writeD(0x00); // Disables use Feather button for X seconds
		packet.writeD(0x00); // Adventure's Song
		packet.writeC(_hideAnimation ? 0x01 : 0x00);
		
		packet.writeD(_itemsEnabled ? 0x01 : 0x00);
		packet.writeD(getItems().size());
		getItems().forEach(packet::writeD);
		return true;
	}
}
