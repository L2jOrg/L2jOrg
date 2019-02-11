package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.templates.HennaTemplate;

import java.nio.ByteBuffer;

public class HennaUnequipInfoPacket extends L2GameServerPacket
{
	private final HennaTemplate _hennaTemplate;
	private final Player _player;

	public HennaUnequipInfoPacket(HennaTemplate hennaTemplate, Player player)
	{
		_hennaTemplate = hennaTemplate;
		_player = player;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_hennaTemplate.getSymbolId()); //symbol Id
		buffer.putInt(_hennaTemplate.getDyeId()); //item id of dye
		buffer.putLong(_hennaTemplate.getRemoveCount());
		buffer.putLong(_hennaTemplate.getRemovePrice());
		buffer.putInt(_hennaTemplate.isForThisClass(_player) ? 0x01 : 0x00); //able to draw or not 0 is false and 1 is true
		buffer.putLong(_player.getAdena());
		buffer.putInt(_player.getINT()); //current INT
		buffer.put((byte) (_player.getINT() - _hennaTemplate.getStatINT())); //equip INT
		buffer.putInt(_player.getSTR()); //current STR
		buffer.put((byte) (_player.getSTR() - _hennaTemplate.getStatSTR())); //equip STR
		buffer.putInt(_player.getCON()); //current CON
		buffer.put((byte) (_player.getCON() - _hennaTemplate.getStatCON())); //equip CON
		buffer.putInt(_player.getMEN()); //current MEM
		buffer.put((byte) (_player.getMEN() - _hennaTemplate.getStatMEN())); //equip MEM
		buffer.putInt(_player.getDEX()); //current DEX
		buffer.put((byte) (_player.getDEX() - _hennaTemplate.getStatDEX())); //equip DEX
		buffer.putInt(_player.getWIT()); //current WIT
		buffer.put((byte) (_player.getWIT() - _hennaTemplate.getStatWIT())); //equip WIT
		buffer.putInt(0x00); //current LUC
		buffer.put((byte)0x00); //equip LUC
		buffer.putInt(0x00); //current CHA
		buffer.put((byte)0x00); //equip CHA
		buffer.putInt(_hennaTemplate.getPeriod());
	}
}