package l2s.gameserver.network.l2.s2c;

public class PledgeSkillListAddPacket extends L2GameServerPacket
{
	private int _skillId;
	private int _skillLevel;

	public PledgeSkillListAddPacket(int skillId, int skillLevel)
	{
		_skillId = skillId;
		_skillLevel = skillLevel;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_skillId);
		writeD(_skillLevel);
	}
}