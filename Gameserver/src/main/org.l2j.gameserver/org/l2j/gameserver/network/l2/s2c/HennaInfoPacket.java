package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.actor.instances.player.Henna;
import org.l2j.gameserver.model.actor.instances.player.HennaList;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

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
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putShort((short) _hennaList.getINT()); //equip INT
		buffer.putShort((short) _hennaList.getSTR()); //equip STR
		buffer.putShort((short) _hennaList.getCON()); //equip CON
		buffer.putShort((short) _hennaList.getMEN()); //equip MEN
		buffer.putShort((short) _hennaList.getDEX()); //equip DEX
		buffer.putShort((short) _hennaList.getWIT()); //equip WIT
		buffer.putShort((short) 0x00); //equip LUC
		buffer.putShort((short) 0x00); //equip CHA
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
			buffer.putInt(henna.getLeftTime());	// Premium symbol left time
			buffer.putInt(_hennaList.isActive(henna) ? 1 : 0);	// Premium symbol active
		}
		else
		{
			buffer.putInt(0x00);	// Premium symbol ID
			buffer.putInt(0x00);	// Premium symbol left time
			buffer.putInt(0x00);	// Premium symbol active
		}
	}
}