package org.l2j.gameserver.network.authcomm.gs2as;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.GameServer;
import org.l2j.gameserver.config.templates.HostInfo;
import org.l2j.gameserver.config.xml.holder.HostsConfigHolder;
import org.l2j.gameserver.network.authcomm.SendablePacket;

public class AuthRequest extends SendablePacket
{
	protected void writeImpl()
	{
		writeC(0x00);
		writeD(GameServer.AUTH_SERVER_PROTOCOL);
		writeC(Config.REQUEST_ID);
		writeC(0x00); // ACCEPT_ALTERNATE_ID
		writeD(Config.AUTH_SERVER_SERVER_TYPE);
		writeC(Config.AUTH_SERVER_AGE_LIMIT);
		writeC(Config.AUTH_SERVER_GM_ONLY ? 0x01 : 0x00);
		writeC(Config.AUTH_SERVER_BRACKETS ? 0x01 : 0x00);
		writeC(Config.AUTH_SERVER_IS_PVP ? 0x01 : 0x00);
		writeString(Config.EXTERNAL_HOSTNAME);
		writeString(Config.INTERNAL_HOSTNAME);
		writeH(Config.PORT_GAME);
		writeD(GameServer.getInstance().getOnlineLimit());

		HostInfo[] hosts = HostsConfigHolder.getInstance().getGameServerHosts();
		writeC(hosts.length);
		for(HostInfo host : hosts)
		{
			writeC(host.getId());
			writeString(host.getIP());
			writeString(host.getInnerIP());
			writeH(host.getPort());
			writeString(host.getKey());
		}
	}
}