package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.skills.AbnormalEffect;

import java.nio.ByteBuffer;

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
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_objectId);
		buffer.putInt(_transformId);
		buffer.putShort((short) _abnormalEffects.length);
		for(AbnormalEffect abnormal : _abnormalEffects)
			buffer.putShort((short) abnormal.getId());
	}
}