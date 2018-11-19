package l2s.authserver.accounts;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import l2s.authserver.Config;
import l2s.authserver.ThreadPoolManager;
import l2s.authserver.network.l2.SessionKey;
import l2s.commons.threading.RunnableImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionManager
{
	private static final Logger _log = LoggerFactory.getLogger(SessionManager.class);

	private static final SessionManager _instance = new SessionManager();

	public static final SessionManager getInstance()
	{
		return _instance;
	}

	public final class Session
	{
		private final Account account;
		private final SessionKey skey;
		private final long expireTime;

		private Session(Account account)
		{
			this.account = account;
			this.skey = SessionKey.create();
			this.expireTime = System.currentTimeMillis() + Config.LOGIN_TIMEOUT;
		}

		public SessionKey getSessionKey()
		{
			return skey;
		}

		public Account getAccount()
		{
			return account;
		}

		public long getExpireTime()
		{
			return expireTime;
		}
	}

	/** Карта текущих сессий **/
	private final Map<SessionKey, Session> sessions = new HashMap<SessionKey, Session>();
	private final Lock lock = new ReentrantLock();

	private SessionManager()
	{
		ThreadPoolManager.getInstance().scheduleAtFixedRate(() ->
		{
			lock.lock();
			try
			{
				//Чистка просроченных сессий
				long currentMillis = System.currentTimeMillis();
				Session session;
				for(Iterator<Session> itr = sessions.values().iterator(); itr.hasNext();)
				{
					session = itr.next();
					if(session.getExpireTime() < currentMillis)
						itr.remove();
				}
			}
			finally
			{
				lock.unlock();
			}
		}, 30000L, 30000L);
	}

	public Session openSession(Account account)
	{
		lock.lock();
		try
		{
			Session session = new Session(account);
			sessions.put(session.getSessionKey(), session);
			return session;
		}
		finally
		{
			lock.unlock();
		}
	}

	public Session closeSession(SessionKey skey)
	{
		lock.lock();
		try
		{
			return sessions.remove(skey);
		}
		finally
		{
			lock.unlock();
		}
	}

	public Session getSessionByName(String name)
	{
		for(Session session : sessions.values())
		{
			if(session.account.getLogin().equalsIgnoreCase(name))
				return session;
		}
		return null;
	}
}
