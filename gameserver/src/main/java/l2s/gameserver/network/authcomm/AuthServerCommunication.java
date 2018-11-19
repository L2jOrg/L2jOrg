package l2s.gameserver.network.authcomm;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.config.templates.HostInfo;
import l2s.gameserver.config.xml.holder.HostsConfigHolder;
import l2s.gameserver.network.authcomm.gs2as.AuthRequest;
import l2s.gameserver.network.l2.GameClient;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthServerCommunication extends Thread
{
	private static final Logger _log = LoggerFactory.getLogger(AuthServerCommunication.class);

	private static final AuthServerCommunication instance = new AuthServerCommunication();

	public static final AuthServerCommunication getInstance()
	{
		return instance;
	}

	private final Map<String, GameClient> waitingClients = new HashMap<String, GameClient>();
	private final Map<String, GameClient> authedClients = new HashMap<String, GameClient>();

	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	private final Lock readLock = lock.readLock();
	private final Lock writeLock = lock.writeLock();

	private final ByteBuffer readBuffer = ByteBuffer.allocate(64 * 1024).order(ByteOrder.LITTLE_ENDIAN);
	private final ByteBuffer writeBuffer = ByteBuffer.allocate(64 * 1024).order(ByteOrder.LITTLE_ENDIAN);

	private final Queue<SendablePacket> sendQueue = new ArrayDeque<SendablePacket>();
	private final Lock sendLock = new ReentrantLock();

	private final AtomicBoolean isPengingWrite = new AtomicBoolean();

	private SelectionKey key;
	private Selector selector;

	private boolean shutdown;
	private boolean restart;

	private AuthServerCommunication()
	{
		try
		{
			selector = Selector.open();
		}
		catch(IOException e)
		{
			_log.error("", e);
		}
	}

	private void connect() throws IOException
	{
		HostInfo hostInfo = HostsConfigHolder.getInstance().getAuthServerHost();

		_log.info("Connecting to authserver on " + hostInfo.getIP() + ":" + hostInfo.getPort());

		SocketChannel channel = SocketChannel.open();
		channel.configureBlocking(false);

		key = channel.register(selector, SelectionKey.OP_CONNECT);
		channel.connect(new InetSocketAddress(hostInfo.getIP(), hostInfo.getPort()));
	}

	public void sendPacket(SendablePacket packet)
	{
		if(isShutdown())
			return;

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

	private boolean disableWriteInterest() throws CancelledKeyException
	{
		if(isPengingWrite.compareAndSet(true, false))
		{
			key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
			return true;
		}
		return false;
	}

	private boolean enableWriteInterest() throws CancelledKeyException
	{
		if(isPengingWrite.getAndSet(true) == false)
		{
			key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
			return true;
		}
		return false;
	}

	protected ByteBuffer getReadBuffer()
	{
		return readBuffer;
	}

	protected ByteBuffer getWriteBuffer()
	{
		return writeBuffer;
	}

	@Override
	public void run()
	{
		Set<SelectionKey> keys;
		Iterator<SelectionKey> iterator;
		SelectionKey key;
		int opts;

		while(!shutdown)
		{
			restart = false;

			try
			{
				loop: while(!isShutdown())
				{
					connect();

					selector.select(5000L);
					keys = selector.selectedKeys();
					if(keys.isEmpty())
						throw new IOException("Connection timeout.");

					iterator = keys.iterator();

					try
					{
						while(iterator.hasNext())
						{
							key = iterator.next();
							iterator.remove();

							opts = key.readyOps();

							switch(opts)
							{
								case SelectionKey.OP_CONNECT:
									connect(key);
									break loop;
							}
						}
					}
					catch(CancelledKeyException e)
					{
						// Exit selector loop
						break;
					}
				}

				loop: while(!isShutdown())
				{
					selector.select();
					keys = selector.selectedKeys();
					iterator = keys.iterator();

					try
					{
						while(iterator.hasNext())
						{
							key = iterator.next();
							iterator.remove();

							opts = key.readyOps();

							switch(opts)
							{
								case SelectionKey.OP_WRITE:
									write(key);
									break;
								case SelectionKey.OP_READ:
									read(key);
									break;
								case SelectionKey.OP_READ | SelectionKey.OP_WRITE:
									write(key);
									read(key);
									break;
							}
						}
					}
					catch(CancelledKeyException e)
					{
						// Exit selector loop
						break loop;
					}
				}
			}
			catch(IOException e)
			{
				_log.error("AuthServer I/O error: " + e.getMessage());
			}

			close();

			try
			{
				Thread.sleep(5000L);
			}
			catch(InterruptedException e)
			{

			}
		}
	}

	private void read(SelectionKey key) throws IOException
	{
		SocketChannel channel = (SocketChannel) key.channel();
		ByteBuffer buf = getReadBuffer();

		int count;

		count = channel.read(buf);

		if(count == -1)
			throw new IOException("End of stream.");

		if(count == 0)
			return;

		buf.flip();

		while(tryReadPacket(key, buf));
	}

	private boolean tryReadPacket(SelectionKey key, ByteBuffer buf) throws IOException
	{
		int pos = buf.position();
		// проверяем, хватает ли нам байт для чтения заголовка и не пустого тела пакета
		if(buf.remaining() > 2)
		{
			// получаем ожидаемый размер пакета
			int size = buf.getShort() & 0xffff;

			// проверяем корректность размера
			if(size <= 2){ throw new IOException("Incorrect packet size: <= 2"); }

			//ожидаемый размер тела пакета
			size -= 2;

			// проверяем, хватает ли байт на чтение тела
			if(size <= buf.remaining())
			{
				//  apply limit
				int limit = buf.limit();
				buf.limit(pos + size + 2);

				ReceivablePacket rp = PacketHandler.handlePacket(buf);

				if(rp != null)
				{
					if(rp.read())
						ThreadPoolManager.getInstance().execute(rp);
				}

				buf.limit(limit);
				buf.position(pos + size + 2);

				// закончили чтение из буфера, почистим
				if(!buf.hasRemaining())
				{
					buf.clear();
					return false;
				}

				return true;
			}

			// не хватает данных на чтение тела пакета, сбрасываем позицию
			buf.position(pos);
		}

		buf.compact();

		return false;
	}

	private void write(SelectionKey key) throws IOException
	{
		SocketChannel channel = (SocketChannel) key.channel();
		ByteBuffer buf = getWriteBuffer();

		boolean done;

		sendLock.lock();
		try
		{
			int i = 0;
			SendablePacket sp;
			while(i++ < 64 && (sp = sendQueue.poll()) != null)
			{
				int headerPos = buf.position();
				buf.position(headerPos + 2);

				sp.write();

				int dataSize = buf.position() - headerPos - 2;
				if(dataSize == 0)
				{
					buf.position(headerPos);
					continue;
				}

				// prepend header
				buf.position(headerPos);
				buf.putShort((short) (dataSize + 2));
				buf.position(headerPos + dataSize + 2);
			}

			done = sendQueue.isEmpty();
			if(done)
				disableWriteInterest();
		}
		finally
		{
			sendLock.unlock();
		}
		buf.flip();

		channel.write(buf);

		if(buf.remaining() > 0)
		{
			buf.compact();
			done = false;
		}
		else
		{
			buf.clear();
		}

		if(!done)
		{
			if(enableWriteInterest())
				selector.wakeup();
		}
	}

	private void connect(SelectionKey key) throws IOException
	{
		SocketChannel channel = (SocketChannel) key.channel();
		channel.finishConnect();

		key.interestOps(key.interestOps() & ~SelectionKey.OP_CONNECT);
		key.interestOps(key.interestOps() | SelectionKey.OP_READ);

		sendPacket(new AuthRequest());
	}

	private void close()
	{
		restart = !shutdown;

		sendLock.lock();
		try
		{
			sendQueue.clear();
		}
		finally
		{
			sendLock.unlock();
		}

		readBuffer.clear();
		writeBuffer.clear();

		isPengingWrite.set(false);

		try
		{
			if(key != null)
			{
				key.channel().close();
				key.cancel();
			}
		}
		catch(IOException e)
		{}

		writeLock.lock();
		try
		{
			waitingClients.clear();
		}
		finally
		{
			writeLock.unlock();
		}
	}

	public void shutdown()
	{
		shutdown = true;
		selector.wakeup();
	}

	public boolean isShutdown()
	{
		return shutdown || restart;
	}

	public void restart()
	{
		restart = true;
		selector.wakeup();
	}

	public GameClient addWaitingClient(GameClient client)
	{
		writeLock.lock();
		try
		{
			return waitingClients.put(client.getLogin(), client);
		}
		finally
		{
			writeLock.unlock();
		}
	}

	public GameClient removeWaitingClient(String account)
	{
		writeLock.lock();
		try
		{
			return waitingClients.remove(account);
		}
		finally
		{
			writeLock.unlock();
		}
	}

	public GameClient addAuthedClient(GameClient client)
	{
		writeLock.lock();
		try
		{
			return authedClients.put(client.getLogin(), client);
		}
		finally
		{
			writeLock.unlock();
		}
	}

	public GameClient removeAuthedClient(String login)
	{
		writeLock.lock();
		try
		{
			return authedClients.remove(login);
		}
		finally
		{
			writeLock.unlock();
		}
	}

	public GameClient getAuthedClient(String login)
	{
		readLock.lock();
		try
		{
			return authedClients.get(login);
		}
		finally
		{
			readLock.unlock();
		}
	}

	public List<GameClient> getAuthedClientsByIP(String ip)
	{
		List<GameClient> clients = new ArrayList<GameClient>();

		readLock.lock();
		try
		{
			for(GameClient client : authedClients.values())
			{
				if(client.getIpAddr().equalsIgnoreCase(ip))
					clients.add(client);
			}
		}
		finally
		{
			readLock.unlock();
		}
		return clients;
	}

	public List<GameClient> getAuthedClientsByHWID(String hwid)
	{
		List<GameClient> clients = new ArrayList<GameClient>();
		if(StringUtils.isEmpty(hwid))
			return clients;

		readLock.lock();
		try
		{
			for(GameClient client : authedClients.values())
			{
				String h = client.getHWID();
				if(!StringUtils.isEmpty(h) && h.equalsIgnoreCase(hwid))
					clients.add(client);
			}
		}
		finally
		{
			readLock.unlock();
		}
		return clients;
	}

	public GameClient removeClient(GameClient client)
	{
		writeLock.lock();
		try
		{
			if(client.isAuthed())
				return authedClients.remove(client.getLogin());
			else
				return waitingClients.remove(client.getLogin());
		}
		finally
		{
			writeLock.unlock();
		}
	}

	public String[] getAccounts()
	{
		readLock.lock();
		try
		{
			return authedClients.keySet().toArray(new String[authedClients.size()]);
		}
		finally
		{
			readLock.unlock();
		}
	}
}
