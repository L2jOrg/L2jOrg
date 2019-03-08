package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.items.L2Henna;
import org.l2j.gameserver.mobius.gameserver.model.stats.BaseStats;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.util.ArrayList;
import java.util.List;

/**
 * This server packet sends the player's henna information.
 * @author Zoey76
 */
public final class HennaInfo implements IClientOutgoingPacket
{
	private final L2PcInstance _activeChar;
	private final List<L2Henna> _hennas = new ArrayList<>();
	
	public HennaInfo(L2PcInstance player)
	{
		_activeChar = player;
		for (L2Henna henna : _activeChar.getHennaList())
		{
			if (henna != null)
			{
				_hennas.add(henna);
			}
		}
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.HENNA_INFO.writeId(packet);
		
		packet.writeH(_activeChar.getHennaValue(BaseStats.INT)); // equip INT
		packet.writeH(_activeChar.getHennaValue(BaseStats.STR)); // equip STR
		packet.writeH(_activeChar.getHennaValue(BaseStats.CON)); // equip CON
		packet.writeH(_activeChar.getHennaValue(BaseStats.MEN)); // equip MEN
		packet.writeH(_activeChar.getHennaValue(BaseStats.DEX)); // equip DEX
		packet.writeH(_activeChar.getHennaValue(BaseStats.WIT)); // equip WIT
		packet.writeH(0x00); // equip LUC
		packet.writeH(0x00); // equip CHA
		packet.writeD(3 - _activeChar.getHennaEmptySlots()); // Slots
		packet.writeD(_hennas.size()); // Size
		for (L2Henna henna : _hennas)
		{
			packet.writeD(henna.getDyeId());
			packet.writeD(henna.isAllowedClass(_activeChar.getClassId()) ? 0x01 : 0x00);
		}
		packet.writeD(0x00); // Premium Slot Dye ID
		packet.writeD(0x00); // Premium Slot Dye Time Left
		packet.writeD(0x00); // Premium Slot Dye ID isValid
		return true;
	}
}
