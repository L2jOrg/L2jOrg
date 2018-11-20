package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.templates.HennaTemplate;

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
	protected final void writeImpl()
	{
		writeInt(_hennaTemplate.getSymbolId()); //symbol Id
		writeInt(_hennaTemplate.getDyeId()); //item id of dye
		writeLong(_hennaTemplate.getRemoveCount());
		writeLong(_hennaTemplate.getRemovePrice());
		writeInt(_hennaTemplate.isForThisClass(_player)); //able to draw or not 0 is false and 1 is true
		writeLong(_player.getAdena());
		writeInt(_player.getINT()); //current INT
		writeByte(_player.getINT() - _hennaTemplate.getStatINT()); //equip INT
		writeInt(_player.getSTR()); //current STR
		writeByte(_player.getSTR() - _hennaTemplate.getStatSTR()); //equip STR
		writeInt(_player.getCON()); //current CON
		writeByte(_player.getCON() - _hennaTemplate.getStatCON()); //equip CON
		writeInt(_player.getMEN()); //current MEM
		writeByte(_player.getMEN() - _hennaTemplate.getStatMEN()); //equip MEM
		writeInt(_player.getDEX()); //current DEX
		writeByte(_player.getDEX() - _hennaTemplate.getStatDEX()); //equip DEX
		writeInt(_player.getWIT()); //current WIT
		writeByte(_player.getWIT() - _hennaTemplate.getStatWIT()); //equip WIT
		writeInt(0x00); //current LUC
		writeByte(0x00); //equip LUC
		writeInt(0x00); //current CHA
		writeByte(0x00); //equip CHA
		writeInt(_hennaTemplate.getPeriod());
	}
}