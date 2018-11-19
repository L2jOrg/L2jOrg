package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;
import l2s.gameserver.templates.HennaTemplate;

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
		writeD(_hennaTemplate.getSymbolId()); //symbol Id
		writeD(_hennaTemplate.getDyeId()); //item id of dye
		writeQ(_hennaTemplate.getDrawCount());
		writeQ(_hennaTemplate.getDrawPrice());
		writeD(_available); //able to draw or not 0 is false and 1 is true
		writeQ(_adena);
		writeD(_int); //current INT
		writeH(_int + _hennaTemplate.getStatINT()); //equip INT
		writeD(_str); //current STR
		writeH(_str + _hennaTemplate.getStatSTR()); //equip STR
		writeD(_con); //current CON
		writeH(_con + _hennaTemplate.getStatCON()); //equip CON
		writeD(_men); //current MEM
		writeH(_men + _hennaTemplate.getStatMEN()); //equip MEM
		writeD(_dex); //current DEX
		writeH(_dex + _hennaTemplate.getStatDEX()); //equip DEX
		writeD(_wit); //current WIT
		writeH(_wit + _hennaTemplate.getStatWIT()); //equip WIT
		writeD(0x00); //current LUC
		writeH(0x00); //equip LUC
		writeD(0x00); //current CHA
		writeH(0x00); //equip CHA
		writeD(_hennaTemplate.getPeriod());
	}
}