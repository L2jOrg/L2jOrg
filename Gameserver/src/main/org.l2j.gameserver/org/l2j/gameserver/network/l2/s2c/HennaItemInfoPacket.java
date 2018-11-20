package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.templates.HennaTemplate;

public class HennaItemInfoPacket extends L2GameServerPacket
{
	private final int _str, _con, _dex, _int, _wit, _men;
	private final long _adena;
	private final HennaTemplate _hennaTemplate;
	private final boolean _available;

	public HennaItemInfoPacket(HennaTemplate hennaTemplate, Player player)
	{
		_hennaTemplate = hennaTemplate;
		_adena = player.getAdena();
		_str = player.getSTR();
		_dex = player.getDEX();
		_con = player.getCON();
		_int = player.getINT();
		_wit = player.getWIT();
		_men = player.getMEN();
		_available = _hennaTemplate.isForThisClass(player);
	}

	@Override
	protected final void writeImpl()
	{
		writeInt(_hennaTemplate.getSymbolId()); //symbol Id
		writeInt(_hennaTemplate.getDyeId()); //item id of dye
		writeLong(_hennaTemplate.getDrawCount());
		writeLong(_hennaTemplate.getDrawPrice());
		writeInt(_available); //able to draw or not 0 is false and 1 is true
		writeLong(_adena);
		writeInt(_int); //current INT
		writeShort(_int + _hennaTemplate.getStatINT()); //equip INT
		writeInt(_str); //current STR
		writeShort(_str + _hennaTemplate.getStatSTR()); //equip STR
		writeInt(_con); //current CON
		writeShort(_con + _hennaTemplate.getStatCON()); //equip CON
		writeInt(_men); //current MEM
		writeShort(_men + _hennaTemplate.getStatMEN()); //equip MEM
		writeInt(_dex); //current DEX
		writeShort(_dex + _hennaTemplate.getStatDEX()); //equip DEX
		writeInt(_wit); //current WIT
		writeShort(_wit + _hennaTemplate.getStatWIT()); //equip WIT
		writeInt(0x00); //current LUC
		writeShort(0x00); //equip LUC
		writeInt(0x00); //current CHA
		writeShort(0x00); //equip CHA
		writeInt(_hennaTemplate.getPeriod());
	}
}