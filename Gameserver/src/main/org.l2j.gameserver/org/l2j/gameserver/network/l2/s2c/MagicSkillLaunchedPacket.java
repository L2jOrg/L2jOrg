package org.l2j.gameserver.network.l2.s2c;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Collections;

import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.GameClient;

public class MagicSkillLaunchedPacket extends L2GameServerPacket
{
	private final int _casterId;
	private final int _skillId;
	private final int _skillLevel;
	private final Collection<Creature> _targets;

	public MagicSkillLaunchedPacket(int casterId, int skillId, int skillLevel, Creature target)
	{
		_casterId = casterId;
		_skillId = skillId;
		_skillLevel = skillLevel;
		_targets = Collections.singletonList(target);
	}

	public MagicSkillLaunchedPacket(int casterId, int skillId, int skillLevel, Collection<Creature> targets)
	{
		_casterId = casterId;
		_skillId = skillId;
		_skillLevel = skillLevel;
		_targets = targets;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(0x00);//unk
		buffer.putInt(_casterId);
		buffer.putInt(_skillId);
		buffer.putInt(_skillLevel);
		buffer.putInt(_targets.size());
		for(Creature target : _targets)
		{
			if(target != null)
				buffer.putInt(target.getObjectId());
		}
	}

	public L2GameServerPacket packet(Player player)
	{
		if(player != null)
		{
			if(player.isNotShowBuffAnim())
				return _casterId == player.getObjectId() ? super.packet(player) : null;
		}
		return super.packet(player);
	}
}