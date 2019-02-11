package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.pledge.Clan;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.settings.ServerSettings;

import java.nio.ByteBuffer;

import static org.l2j.commons.configuration.Configurator.getSettings;

public class PledgeInfoPacket extends L2GameServerPacket
{
	private int clan_id;
	private String clan_name, ally_name;

	public PledgeInfoPacket(Clan clan)
	{
		clan_id = clan.getClanId();
		clan_name = clan.getName();
		ally_name = clan.getAlliance() == null ? "" : clan.getAlliance().getAllyName();
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(getSettings(ServerSettings.class).serverId());
		buffer.putInt(clan_id);
		writeString(clan_name, buffer);
		writeString(ally_name, buffer);
	}
}