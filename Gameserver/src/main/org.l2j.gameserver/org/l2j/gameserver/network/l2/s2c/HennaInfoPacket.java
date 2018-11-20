package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.actor.instances.player.Henna;
import org.l2j.gameserver.model.actor.instances.player.HennaList;
import org.l2j.gameserver.templates.HennaTemplate;

public class HennaInfoPacket extends L2GameServerPacket
{
	private final Player _player;
	private final HennaList _hennaList;

	public HennaInfoPacket(Player player)
	{
		_player = player;
		_hennaList = player.getHennaList();
	}

	@Override
	protected final void writeImpl()
	{
		writeShort(_hennaList.getINT()); //equip INT
		writeShort(_hennaList.getSTR()); //equip STR
		writeShort(_hennaList.getCON()); //equip CON
		writeShort(_hennaList.getMEN()); //equip MEN
		writeShort(_hennaList.getDEX()); //equip DEX
		writeShort(_hennaList.getWIT()); //equip WIT
		writeShort(0x00); //equip LUC
		writeShort(0x00); //equip CHA
		writeInt(HennaList.MAX_SIZE); //interlude, slots?
		writeInt(_hennaList.size());
		for(Henna henna : _hennaList.values(false))
		{
			writeInt(henna.getTemplate().getSymbolId());
			writeInt(_hennaList.isActive(henna));
		}

		Henna henna = _hennaList.getPremiumHenna();
		if(henna != null)
		{
			writeInt(henna.getTemplate().getSymbolId());	// Premium symbol ID
			writeInt(henna.getLeftTime());	// Premium symbol left time
			writeInt(_hennaList.isActive(henna));	// Premium symbol active
		}
		else
		{
			writeInt(0x00);	// Premium symbol ID
			writeInt(0x00);	// Premium symbol left time
			writeInt(0x00);	// Premium symbol active
		}
	}
}