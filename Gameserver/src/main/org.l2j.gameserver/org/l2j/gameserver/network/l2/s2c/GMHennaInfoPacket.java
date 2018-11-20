package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.actor.instances.player.Henna;
import org.l2j.gameserver.model.actor.instances.player.HennaList;

public class GMHennaInfoPacket extends L2GameServerPacket
{
	private final Player _player;
	private final HennaList _hennaList;

	public GMHennaInfoPacket(Player player)
	{
		_player = player;
		_hennaList = player.getHennaList();
	}

	@Override
	protected final void writeImpl()
	{
		writeByte(_hennaList.getINT()); //equip INT
		writeByte(_hennaList.getSTR()); //equip STR
		writeByte(_hennaList.getCON()); //equip CON
		writeByte(_hennaList.getMEN()); //equip MEN
		writeByte(_hennaList.getDEX()); //equip DEX
		writeByte(_hennaList.getWIT()); //equip WIT
		writeByte(0); //equip LUC
		writeByte(0); //equip CHA
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
			writeInt(_hennaList.isActive(henna));	// Premium symbol active
			writeInt(henna.getLeftTime());	// Premium symbol left time
		}
		else
		{
			writeInt(0x00);	// Premium symbol ID
			writeInt(0x00);	// Premium symbol active
			writeInt(0x00);	// Premium symbol left time
		}
	}
}