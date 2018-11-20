package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.actor.instances.player.Cubic;

public class ExUserInfoCubic extends L2GameServerPacket
{
	private final int _objectId, _agationId;
	private final Cubic[] _cubics;

	public ExUserInfoCubic(Player character)
	{
		_objectId = character.getObjectId();
		_cubics = character.getCubics().toArray(new Cubic[character.getCubics().size()]);
		_agationId = character.getAgathionId();
	}

	@Override
	protected void writeImpl()
	{
		writeInt(_objectId);
		writeShort(_cubics.length);
		for(Cubic cubic : _cubics)
			writeShort(cubic == null ? 0 : cubic.getId());
		writeInt(_agationId);
	}
}