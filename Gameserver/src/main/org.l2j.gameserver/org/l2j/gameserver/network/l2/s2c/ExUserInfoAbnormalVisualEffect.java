package org.l2j.gameserver.network.l2.s2c;

import java.nio.ByteBuffer;
import java.util.Set;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.skills.AbnormalEffect;

/**
 * @reworked by Bonux
**/
public class ExUserInfoAbnormalVisualEffect extends L2GameServerPacket
{
	private final int _objectId;
	private final int _transformId;
	private final Set<AbnormalEffect> _abnormalEffects;

	public ExUserInfoAbnormalVisualEffect(Player player)
	{
		_objectId = player.getObjectId();
		_transformId = player.getVisualTransformId();
		_abnormalEffects = player.getAbnormalEffects();
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_objectId);
		buffer.putInt(_transformId);
		buffer.putInt(_abnormalEffects.size());
		for(AbnormalEffect abnormal : _abnormalEffects)
			buffer.putShort((short) abnormal.getId());
	}
}