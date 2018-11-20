package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.actor.instances.player.Henna;
import org.l2j.gameserver.model.actor.instances.player.HennaList;
import org.l2j.gameserver.templates.HennaTemplate;

public class HennaUnequipListPacket extends L2GameServerPacket
{
	private final Player _player;
	private final long _adena;
	private final HennaList _hennaList;

	public HennaUnequipListPacket(Player player)
	{
		_player = player;
		_adena = player.getAdena();
		_hennaList = player.getHennaList();
	}

	@Override
	protected final void writeImpl()
	{
		writeLong(_adena);
		writeInt(_hennaList.getFreeSize());
		writeInt(_hennaList.size());
		for(Henna henna : _hennaList.values(true))
		{
			HennaTemplate template = henna.getTemplate();
			writeInt(template.getSymbolId()); //symbolid
			writeInt(template.getDyeId()); //itemid of dye
			writeLong(template.getRemoveCount());
			writeLong(template.getRemovePrice());
			writeInt(template.isForThisClass(_player) ? 0x01 : 0x00);
		}
	}
}