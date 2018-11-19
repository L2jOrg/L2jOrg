package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.Config;
import l2s.gameserver.model.pledge.Clan;

/**
 * sample
 * 0000: cd b0 98 a0 48 1e 01 00 00 00 00 00 00 00 00 00    ....H...........
 * 0010: 00 00 00 00 00                                     .....
 *
 * format   ddddd
 */
public class PledgeStatusChangedPacket extends L2GameServerPacket
{
	private final int leader_id;
	private final int clan_id;
	private final int level;
	private final int crestId;
	private final int allyId;

	public PledgeStatusChangedPacket(Clan clan)
	{
		leader_id = clan.getLeaderId();
		clan_id = clan.getClanId();
		level = clan.getLevel();
		crestId = clan.getCrestId();
		allyId = clan.getAllyId();
	}

	@Override
	protected final void writeImpl()
	{
		writeD(Config.REQUEST_ID);
		writeD(leader_id);
		writeD(clan_id);
		writeD(crestId);
		writeD(allyId);
		writeD(0);
		writeD(0);
		writeD(0);
	}
}