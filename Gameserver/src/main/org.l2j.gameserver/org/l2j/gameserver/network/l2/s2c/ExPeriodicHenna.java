package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.actor.instances.player.Henna;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ExPeriodicHenna extends L2GameServerPacket
{
	private final Henna _henna;
	private final boolean _active;

	public ExPeriodicHenna(Player player)
	{
		_henna = player.getHennaList().getPremiumHenna();
		_active = _henna != null && player.getHennaList().isActive(_henna);
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		if(_henna != null)
		{
			buffer.putInt(_henna.getTemplate().getSymbolId());	// Premium symbol ID
			buffer.putInt(_henna.getLeftTime());	// Premium symbol left time
			buffer.putInt(_active ? 1 : 0);	// Premium symbol active
		}
		else
		{
			buffer.putInt(0x00);	// Premium symbol ID
			buffer.putInt(0x00);	// Premium symbol left time
			buffer.putInt(0x00);	// Premium symbol active
		}
	}
}
