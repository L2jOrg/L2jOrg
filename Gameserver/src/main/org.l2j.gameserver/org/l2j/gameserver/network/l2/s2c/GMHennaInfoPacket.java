package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.actor.instances.player.Henna;
import org.l2j.gameserver.model.actor.instances.player.HennaList;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

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
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.put((byte)_hennaList.getINT()); //equip INT
		buffer.put((byte)_hennaList.getSTR()); //equip STR
		buffer.put((byte)_hennaList.getCON()); //equip CON
		buffer.put((byte)_hennaList.getMEN()); //equip MEN
		buffer.put((byte)_hennaList.getDEX()); //equip DEX
		buffer.put((byte)_hennaList.getWIT()); //equip WIT
		buffer.put((byte)0); //equip LUC
		buffer.put((byte)0); //equip CHA
		buffer.putInt(HennaList.MAX_SIZE); //interlude, slots?
		buffer.putInt(_hennaList.size());
		for(Henna henna : _hennaList.values(false))
		{
			buffer.putInt(henna.getTemplate().getSymbolId());
			buffer.putInt(_hennaList.isActive(henna) ? 1 : 0);
		}

		Henna henna = _hennaList.getPremiumHenna();
		if(henna != null)
		{
			buffer.putInt(henna.getTemplate().getSymbolId());	// Premium symbol ID
			buffer.putInt(_hennaList.isActive(henna) ? 1 : 0);	// Premium symbol active
			buffer.putInt(henna.getLeftTime());	// Premium symbol left time
		}
		else
		{
			buffer.putInt(0x00);	// Premium symbol ID
			buffer.putInt(0x00);	// Premium symbol active
			buffer.putInt(0x00);	// Premium symbol left time
		}
	}
}