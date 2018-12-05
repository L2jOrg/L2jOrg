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
		writeByte(0x00);
		writeInt(GameServer.AUTH_SERVER_PROTOCOL);
		writeByte(Config.REQUEST_ID);
		writeByte(0x00); // ACCEPT_ALTERNATE_ID
		writeInt(Config.AUTH_SERVER_SERVER_TYPE);
		writeByte(Config.AUTH_SERVER_AGE_LIMIT);
		writeByte(Config.AUTH_SERVER_GM_ONLY ? 0x01 : 0x00);
		writeByte(Config.AUTH_SERVER_BRACKETS ? 0x01 : 0x00);
		writeByte(Config.AUTH_SERVER_IS_PVP ? 0x01 : 0x00);
		writeString(Config.EXTERNAL_HOSTNAME);
		writeString(Config.INTERNAL_HOSTNAME);
		writeShort(Config.PORT_GAME);
		writeInt(GameServer.getInstance().getOnlineLimit());

		HostInfo[] hosts = HostsConfigHolder.getInstance().getGameServerHosts();
		writeByte(hosts.length);
		for(HostInfo host : hosts)
		{
			writeByte(host.getId());
			writeString(host.getIP());
			writeString(host.getInnerIP());
			writeShort(host.getPort());
			writeString(host.getKey());
		}
	}
}