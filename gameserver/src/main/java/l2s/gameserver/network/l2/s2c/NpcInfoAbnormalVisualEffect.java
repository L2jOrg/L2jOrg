package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Creature;
import l2s.gameserver.skills.AbnormalEffect;

/**
 * @reworked by Bonux
**/
public class NpcInfoAbnormalVisualEffect extends L2GameServerPacket
{
	private final int _objectId;
	private final int _transformId;
	private final AbnormalEffect[] _abnormalEffects;

	public NpcInfoAbnormalVisualEffect(Creature npc)
	{
		_objectId = npc.getObjectId();
		_transformId = npc.getVisualTransformId();
		_abnormalEffects = npc.getAbnormalEffectsArray();
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_objectId);
		writeD(_transformId);
		writeH(_abnormalEffects.length);
		for(AbnormalEffect abnormal : _abnormalEffects)
			writeH(abnormal.getId());
	}
}