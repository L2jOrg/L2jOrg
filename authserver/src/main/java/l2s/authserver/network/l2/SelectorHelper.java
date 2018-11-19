package l2s.authserver.network.l2;

import java.nio.channels.SocketChannel;

import l2s.authserver.Config;
import l2s.authserver.IpBanManager;
import l2s.authserver.ThreadPoolManager;
import l2s.authserver.network.l2.s2c.Init;
import l2s.commons.net.nio.impl.IAcceptFilter;
import l2s.commons.net.nio.impl.IClientFactory;
import l2s.commons.net.nio.impl.IMMOExecutor;
import l2s.commons.net.nio.impl.MMOConnection;
import l2s.commons.threading.RunnableImpl;


public class SelectorHelper implements IMMOExecutor<L2LoginClient>, IClientFactory<L2LoginClient>, IAcceptFilter
{
	@Override
	public void execute(Runnable r)
	{
		ThreadPoolManager.getInstance().execute(r);
	}

	@Override
	public L2LoginClient create(MMOConnection<L2LoginClient> con)
	{
		final L2LoginClient client = new L2LoginClient(con);
		client.sendPacket(new Init(client));
		ThreadPoolManager.getInstance().schedule(() ->
		{
			client.closeNow(false);
		}, Config.LOGIN_TIMEOUT);
		return client;
	}

	@Override
	public boolean accept(SocketChannel sc)
	{
		return !IpBanManager.getInstance().isIpBanned(sc.socket().getInetAddress().getHostAddress());
	}
}