package l2s.gameserver.network.authcomm.gs2as;

import l2s.gameserver.Config;
import l2s.gameserver.GameServer;
import l2s.gameserver.config.templates.HostInfo;
import l2s.gameserver.config.xml.holder.HostsConfigHolder;
import l2s.gameserver.network.authcomm.SendablePacket;

public class AuthRequest extends SendablePacket
{
	protected void writeImpl()
	{
		writeC(0x00);
		writeD(GameServer.AUTH_SERVER_PROTOCOL);
		writeC(Config.REQUEST_ID);
		writeC(0x00); // ACCEPT_ALTERNATE_ID
		writeD(Config.AUTH_SERVER_SERVER_TYPE);
		writeD(Config.AUTH_SERVER_AGE_LIMIT);
		writeC(Config.AUTH_SERVER_GM_ONLY ? 0x01 : 0x00);
		writeC(Config.AUTH_SERVER_BRACKETS ? 0x01 : 0x00);
		writeC(Config.AUTH_SERVER_IS_PVP ? 0x01 : 0x00);
		writeS(Config.EXTERNAL_HOSTNAME);
		writeS(Config.INTERNAL_HOSTNAME);
		writeH(1); // Ports counts
		writeH(Config.PORT_GAME);
		writeD(GameServer.getInstance().getOnlineLimit());

		HostInfo[] hosts = HostsConfigHolder.getInstance().getGameServerHosts();
		writeC(hosts.length);
		for(HostInfo host : hosts)
		{
			writeC(host.getId());
			writeS(host.getIP());
			writeS(host.getInnerIP());
			writeH(host.getPort());
			writeS(host.getKey());
		}
	}
}