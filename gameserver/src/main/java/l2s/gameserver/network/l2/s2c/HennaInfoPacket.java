package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.player.Henna;
import l2s.gameserver.model.actor.instances.player.HennaList;
import l2s.gameserver.templates.HennaTemplate;

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
		writeH(_hennaList.getINT()); //equip INT
		writeH(_hennaList.getSTR()); //equip STR
		writeH(_hennaList.getCON()); //equip CON
		writeH(_hennaList.getMEN()); //equip MEN
		writeH(_hennaList.getDEX()); //equip DEX
		writeH(_hennaList.getWIT()); //equip WIT
		writeH(0x00); //equip LUC
		writeH(0x00); //equip CHA
		writeD(HennaList.MAX_SIZE); //interlude, slots?
		writeD(_hennaList.size());
		for(Henna henna : _hennaList.values(false))
		{
			writeD(henna.getTemplate().getSymbolId());
			writeD(_hennaList.isActive(henna));
		}

		Henna henna = _hennaList.getPremiumHenna();
		if(henna != null)
		{
			writeD(henna.getTemplate().getSymbolId());	// Premium symbol ID
			writeD(henna.getLeftTime());	// Premium symbol left time
			writeD(_hennaList.isActive(henna));	// Premium symbol active
		}
		else
		{
			writeD(0x00);	// Premium symbol ID
			writeD(0x00);	// Premium symbol left time
			writeD(0x00);	// Premium symbol active
		}
	}
}