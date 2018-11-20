package org.l2j.gameserver.network.l2.s2c;

import java.util.Set;

import org.l2j.gameserver.model.Player;
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
	protected final void writeImpl()
	{
		writeInt(_objectId);
		writeInt(_transformId);
		writeInt(_abnormalEffects.size());
		for(AbnormalEffect abnormal : _abnormalEffects)
			writeShort(abnormal.getId());
	}
}