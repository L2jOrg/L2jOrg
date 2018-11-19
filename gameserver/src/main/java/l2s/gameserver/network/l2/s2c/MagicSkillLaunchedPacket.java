package l2s.gameserver.network.l2.s2c;

import java.util.Collection;
import java.util.Collections;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;

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
	protected final void writeImpl()
	{
		writeD(0x00);//unk
		writeD(_casterId);
		writeD(_skillId);
		writeD(_skillLevel);
		writeD(_targets.size());
		for(Creature target : _targets)
		{
			if(target != null)
				writeD(target.getObjectId());
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