package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.player.Cubic;

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
		writeD(_objectId);
		writeH(_cubics.length);
		for(Cubic cubic : _cubics)
			writeH(cubic == null ? 0 : cubic.getId());
		writeD(_agationId);
	}
}