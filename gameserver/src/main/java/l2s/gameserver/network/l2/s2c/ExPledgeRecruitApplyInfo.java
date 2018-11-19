package l2s.gameserver.network.l2.s2c;

/**
 * @author GodWorld
 * @reworked by Bonux
**/
public class ExPledgeRecruitApplyInfo extends L2GameServerPacket
{
	public static final L2GameServerPacket DEFAULT = new ExPledgeRecruitApplyInfo(0);
	public static final L2GameServerPacket ORDER_LIST = new ExPledgeRecruitApplyInfo(1);
	public static final L2GameServerPacket CLAN_REG = new ExPledgeRecruitApplyInfo(2);
	public static final L2GameServerPacket UNKNOWN = new ExPledgeRecruitApplyInfo(3);
	public static final L2GameServerPacket WAITING = new ExPledgeRecruitApplyInfo(4);

	private final int _state;

	private ExPledgeRecruitApplyInfo(int state)
	{
		_state = state;
	}

	@Override
	protected void writeImpl()
	{
		writeD(_state);
	}
}