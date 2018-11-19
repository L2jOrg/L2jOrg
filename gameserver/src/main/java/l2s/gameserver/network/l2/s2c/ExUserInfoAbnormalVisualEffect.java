package l2s.gameserver.network.l2.s2c;

import java.util.Set;

import l2s.gameserver.model.Player;
import l2s.gameserver.skills.AbnormalEffect;

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
		writeD(_objectId);
		writeD(_transformId);
		writeD(_abnormalEffects.size());
		for(AbnormalEffect abnormal : _abnormalEffects)
			writeH(abnormal.getId());
	}
}