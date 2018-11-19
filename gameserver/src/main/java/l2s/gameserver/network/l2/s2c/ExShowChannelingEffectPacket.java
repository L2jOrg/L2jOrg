package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Creature;

/**
 * @author monithly
 */
public class ExShowChannelingEffectPacket extends L2GameServerPacket
{
	private final int _casterObjectId;
	private final int _targetObjectId;
	private final int _state;

	public ExShowChannelingEffectPacket(Creature caster, Creature target, int state)
	{
		_casterObjectId = caster.getObjectId();
		_targetObjectId = target.getObjectId();
		_state = state;
	}

	@Override
	protected void writeImpl()
	{
		writeD(_casterObjectId);
		writeD(_targetObjectId);
		writeD(_state);
	}
}