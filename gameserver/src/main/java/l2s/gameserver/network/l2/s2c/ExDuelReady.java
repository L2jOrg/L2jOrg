package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.entity.events.impl.DuelEvent;

public class ExDuelReady extends L2GameServerPacket
{
	private int _duelType;

	public ExDuelReady(DuelEvent event)
	{
		_duelType = event.getDuelType();
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_duelType);
	}
}