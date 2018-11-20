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
		writeD(_hennaTemplate.getSymbolId()); //symbol Id
		writeD(_hennaTemplate.getDyeId()); //item id of dye
		writeQ(_hennaTemplate.getRemoveCount());
		writeQ(_hennaTemplate.getRemovePrice());
		writeD(_hennaTemplate.isForThisClass(_player)); //able to draw or not 0 is false and 1 is true
		writeQ(_player.getAdena());
		writeD(_player.getINT()); //current INT
		writeC(_player.getINT() - _hennaTemplate.getStatINT()); //equip INT
		writeD(_player.getSTR()); //current STR
		writeC(_player.getSTR() - _hennaTemplate.getStatSTR()); //equip STR
		writeD(_player.getCON()); //current CON
		writeC(_player.getCON() - _hennaTemplate.getStatCON()); //equip CON
		writeD(_player.getMEN()); //current MEM
		writeC(_player.getMEN() - _hennaTemplate.getStatMEN()); //equip MEM
		writeD(_player.getDEX()); //current DEX
		writeC(_player.getDEX() - _hennaTemplate.getStatDEX()); //equip DEX
		writeD(_player.getWIT()); //current WIT
		writeC(_player.getWIT() - _hennaTemplate.getStatWIT()); //equip WIT
		writeD(0x00); //current LUC
		writeC(0x00); //equip LUC
		writeD(0x00); //current CHA
		writeC(0x00); //equip CHA
		writeD(_hennaTemplate.getPeriod());
	}
}