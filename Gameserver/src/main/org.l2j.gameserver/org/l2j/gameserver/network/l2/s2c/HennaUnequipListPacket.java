package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.actor.instances.player.Henna;
import org.l2j.gameserver.model.actor.instances.player.HennaList;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.templates.HennaTemplate;

import java.nio.ByteBuffer;

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
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putLong(_adena);
		buffer.putInt(_hennaList.getFreeSize());
		buffer.putInt(_hennaList.size());
		for(Henna henna : _hennaList.values(true))
		{
			HennaTemplate template = henna.getTemplate();
			buffer.putInt(template.getSymbolId()); //symbolid
			buffer.putInt(template.getDyeId()); //itemid of dye
			buffer.putLong(template.getRemoveCount());
			buffer.putLong(template.getRemovePrice());
			buffer.putInt(template.isForThisClass(_player) ? 0x01 : 0x00);
		}
	}
}