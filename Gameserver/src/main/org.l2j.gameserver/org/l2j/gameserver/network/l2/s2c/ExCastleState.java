package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.entity.residence.Castle;
import org.l2j.gameserver.model.entity.residence.ResidenceSide;

/**
 * @author Bonux
 */
public class ExCastleState extends L2GameServerPacket
{
	private final int _id;
	private final ResidenceSide _side;

	public ExCastleState(Castle castle)
	{
		_id = castle.getId();
		_side = castle.getResidenceSide();
	}

	protected void writeImpl()
	{
		writeInt(_id);
		writeInt(_side.ordinal());
	}
}