package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.pledge.Clan;
import org.l2j.gameserver.settings.ServerSettings;

import static org.l2j.commons.configuration.Configurator.getSettings;

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
		writeInt(getSettings(ServerSettings.class).serverId());
		writeInt(leader_id);
		writeInt(clan_id);
		writeInt(crestId);
		writeInt(allyId);
		writeInt(0);
		writeInt(0);
		writeInt(0);
	}
}