package org.l2j.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;

import org.l2j.gameserver.data.xml.holder.HennaHolder;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.templates.HennaTemplate;

public class HennaEquipListPacket extends L2GameServerPacket
{
	private final Player _player;
	private final int _emptySlots;
	private final long _adena;
	private final List<HennaTemplate> _hennas = new ArrayList<HennaTemplate>();

	public HennaEquipListPacket(Player player)
	{
		_player = player;
		_adena = player.getAdena();
		_emptySlots = player.getHennaList().getFreeSize();

		List<HennaTemplate> list = HennaHolder.getInstance().generateList(player);
		for(HennaTemplate element : list)
		{
			if(player.getInventory().getItemByItemId(element.getDyeId()) != null)
				_hennas.add(element);
		}
	}

	@Override
	protected final void writeImpl()
	{
		writeLong(_adena);
		writeInt(_emptySlots);
		writeInt(_hennas.size());
		for(HennaTemplate henna : _hennas)
		{
			writeInt(henna.getSymbolId()); //symbolid
			writeInt(henna.getDyeId()); //itemid of dye
			writeLong(henna.getDrawCount());
			writeLong(henna.getDrawPrice());
			writeInt(henna.isForThisClass(_player) ? 0x01 : 0x00);
		}
	}
}