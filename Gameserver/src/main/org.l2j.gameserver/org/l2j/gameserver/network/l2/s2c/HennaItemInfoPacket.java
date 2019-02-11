package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.templates.HennaTemplate;

import java.nio.ByteBuffer;

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
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_hennaTemplate.getSymbolId()); //symbol Id
		buffer.putInt(_hennaTemplate.getDyeId()); //item id of dye
		buffer.putLong(_hennaTemplate.getDrawCount());
		buffer.putLong(_hennaTemplate.getDrawPrice());
		buffer.putInt(_available ? 0x01 : 0x00); //able to draw or not 0 is false and 1 is true
		buffer.putLong(_adena);
		buffer.putInt(_int); //current INT
		buffer.putShort((short) (_int + _hennaTemplate.getStatINT())); //equip INT
		buffer.putInt(_str); //current STR
		buffer.putShort((short) (_str + _hennaTemplate.getStatSTR())); //equip STR
		buffer.putInt(_con); //current CON
		buffer.putShort((short) (_con + _hennaTemplate.getStatCON())); //equip CON
		buffer.putInt(_men); //current MEM
		buffer.putShort((short) (_men + _hennaTemplate.getStatMEN())); //equip MEM
		buffer.putInt(_dex); //current DEX
		buffer.putShort((short) (_dex + _hennaTemplate.getStatDEX())); //equip DEX
		buffer.putInt(_wit); //current WIT
		buffer.putShort((short) (_wit + _hennaTemplate.getStatWIT())); //equip WIT
		buffer.putInt(0x00); //current LUC
		buffer.putShort((short) 0x00); //equip LUC
		buffer.putInt(0x00); //current CHA
		buffer.putShort((short) 0x00); //equip CHA
		buffer.putInt(_hennaTemplate.getPeriod());
	}
}