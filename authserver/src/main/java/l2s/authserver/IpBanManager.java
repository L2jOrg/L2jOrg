package l2s.authserver;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IpBanManager
{
	private static final Logger _log = LoggerFactory.getLogger(IpBanManager.class);

	private static final IpBanManager _instance = new IpBanManager();

	public static final IpBanManager getInstance()
	{
		return _instance;
	}

	private class IpSession
	{
		public int tryCount;
		public long lastTry;
		public long banExpire;
	}

	private final Map<String, IpSession> ips = new HashMap<String, IpSession>();
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	private final Lock readLock = lock.readLock();
	private final Lock writeLock = lock.writeLock();

	private IpBanManager()
	{
		ThreadPoolManager.getInstance().scheduleAtFixedRate(() ->
		{
			long currentMillis = System.currentTimeMillis();
			
			writeLock.lock();
			try
			{
				//Чистка просроченных сессий
				IpSession session;
				for(Iterator<IpSession> itr = ips.values().iterator(); itr.hasNext();)
				{
					session = itr.next();
					if(session.banExpire < currentMillis && session.lastTry < currentMillis - Config.LOGIN_TRY_TIMEOUT)
						itr.remove();
				}
			}
			finally
			{
				writeLock.unlock();
			}
		}, 1000L, 1000L);
	}

	public boolean isIpBanned(String ip)
	{
		readLock.lock();
		try
		{
			IpSession ipsession;
			if((ipsession = ips.get(ip)) == null)
				return false;

			return ipsession.banExpire > System.currentTimeMillis();
		}
		finally
		{
			readLock.unlock();
		}
	}

	public boolean tryLogin(String ip, boolean success)
	{
		writeLock.lock();
		try
		{
			IpSession ipsession;
			if((ipsession = ips.get(ip)) == null)
				ips.put(ip, ipsession = new IpSession());

			long currentMillis = System.currentTimeMillis();

			if(currentMillis - ipsession.lastTry < Config.LOGIN_TRY_TIMEOUT)
				success = false;

			//Если успешный вход, и мы уложились в лимит между входами, уменьшаем количество неудачных попыток
			if(success)
			{
				if(ipsession.tryCount > 0)
					ipsession.tryCount--;
			}
			else
			{
				if(ipsession.tryCount < Config.LOGIN_TRY_BEFORE_BAN)
					ipsession.tryCount++;
			}

			ipsession.lastTry = currentMillis; 
				
			//Превысили лимит неудачных попыток, баним IP
			if(ipsession.tryCount == Config.LOGIN_TRY_BEFORE_BAN)
			{
				_log.warn("IpBanManager: " + ip + " banned for " + Config.IP_BAN_TIME / 1000L + " seconds.");
				ipsession.banExpire = currentMillis + Config.IP_BAN_TIME;
				return false;
			}

			return true;
		}
		finally
		{
			writeLock.unlock();
		}
	}
}
