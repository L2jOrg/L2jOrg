package l2s.authserver.network.gamecomm;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import l2s.authserver.Config;
import l2s.authserver.ThreadPoolManager;
import l2s.authserver.network.gamecomm.as2gs.PingRequest;
import l2s.commons.threading.RunnableImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameServerConnection
{
	private static final Logger _log = LoggerFactory.getLogger(GameServerConnection.class);

	final ByteBuffer readBuffer = ByteBuffer.allocate(64 * 1024).order(ByteOrder.LITTLE_ENDIAN);
	final Queue<SendablePacket> sendQueue = new ArrayDeque<SendablePacket>();
	final Lock sendLock = new ReentrantLock();

	final AtomicBoolean isPengingWrite = new AtomicBoolean();

	private final Selector selector;
	private final SelectionKey key;

	private GameServer gameServer;

	/**
	 * Ping system:
	 *
	 * После авторизации игрвого сервера запускается PingTask с Config.GAME_SERVER_PING_DELAY - он посылает пакет и запускает такс PingCheckTask - который ждет ответ он ГС,
	 * если ответ непрыбыл, ГС умер, и про это будет написано в консоль
	 */
	private Future<?> _pingTask;
	private int _pingRetry;

	private class PingTask extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			if(Config.GAME_SERVER_PING_RETRY > 0)
			{
				if(_pingRetry > Config.GAME_SERVER_PING_RETRY)
				{
					_log.warn("Gameserver IP[" + getIpAddress() + "]: ping timeout!");
					closeNow();
					return;
				}
			}
			_pingRetry++;
			sendPacket(new PingRequest());
		}
	}

	public GameServerConnection(SelectionKey key)
	{
		this.key = key;
		selector = key.selector();
	}

	public void sendPacket(SendablePacket packet)
	{
		boolean wakeUp;

		sendLock.lock();
		try
		{
			sendQueue.add(packet);
			wakeUp = enableWriteInterest();
		}
		catch(CancelledKeyException e)
		{
			return;
		}
		finally
		{
			sendLock.unlock();
		}

		if(wakeUp)
			selector.wakeup();
	}

	protected boolean disableWriteInterest() throws CancelledKeyException
	{
		if(isPengingWrite.compareAndSet(true, false))
		{
			key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
			return true;
		}
		return false;
	}

	protected boolean enableWriteInterest() throws CancelledKeyException
	{
		if(isPengingWrite.getAndSet(true) == false)
		{
			key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
			return true;
		}
		return false;
	}

	public void closeNow()
	{
		key.interestOps(SelectionKey.OP_CONNECT);
		selector.wakeup();
	}

	public void onDisconnection()
	{
		try
		{
			stopPingTask();

			readBuffer.clear();

			sendLock.lock();
			try
			{
				sendQueue.clear();
			}
			finally
			{
				sendLock.unlock();
			}

			isPengingWrite.set(false);

			if(gameServer != null && gameServer.isAuthed())
			{
				_log.info("Connection with gameserver IP[" + getIpAddress() + "] lost.");
				_log.info("Setting gameserver down.");
				gameServer.setDown();
			}

			gameServer = null;
		}
		catch(Exception e)
		{
			_log.error("", e);
		}
	}

	ByteBuffer getReadBuffer()
	{
		return readBuffer;
	}

	GameServer getGameServer()
	{
		return gameServer;
	}

	void setGameServer(GameServer gameServer)
	{
		this.gameServer = gameServer;
	}

	public String getIpAddress()
	{
		return ((SocketChannel) key.channel()).socket().getInetAddress().getHostAddress();
	}

	public void onPingResponse()
	{
		_pingRetry = 0;
	}

	public void startPingTask()
	{
		if(Config.GAME_SERVER_PING_DELAY == 0)
			return;

		_pingTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new PingTask(), Config.GAME_SERVER_PING_DELAY, Config.GAME_SERVER_PING_DELAY);
	}

	public void stopPingTask()
	{
		if(_pingTask != null)
		{
			_pingTask.cancel(false);
			_pingTask = null;
		}
	}
}