package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.actor.instances.player.Henna;

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
	protected void writeImpl()
	{
		if(_henna != null)
		{
			writeInt(_henna.getTemplate().getSymbolId());	// Premium symbol ID
			writeInt(_henna.getLeftTime());	// Premium symbol left time
			writeInt(_active);	// Premium symbol active
		}
		else
		{
			writeInt(0x00);	// Premium symbol ID
			writeInt(0x00);	// Premium symbol left time
			writeInt(0x00);	// Premium symbol active
		}
	}
}
