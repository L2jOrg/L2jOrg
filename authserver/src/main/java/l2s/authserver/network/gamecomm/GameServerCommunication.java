package l2s.authserver.network.gamecomm;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.locks.Lock;

import l2s.authserver.ThreadPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameServerCommunication extends Thread
{
	private static final Logger _log = LoggerFactory.getLogger(GameServerCommunication.class);

	private static final GameServerCommunication instance = new GameServerCommunication();

	private final ByteBuffer writeBuffer = ByteBuffer.allocate(64 * 1024).order(ByteOrder.LITTLE_ENDIAN);

	private Selector selector;

	private boolean shutdown;

	public static GameServerCommunication getInstance()
	{
		return instance;
	}

	private GameServerCommunication()
	{

	}

	public void openServerSocket(InetAddress address, int tcpPort) throws IOException
	{
		selector = Selector.open();

		ServerSocketChannel selectable = ServerSocketChannel.open();
		selectable.configureBlocking(false);

		selectable.socket().bind(address == null ? new InetSocketAddress(tcpPort) : new InetSocketAddress(address, tcpPort));
		selectable.register(selector, selectable.validOps());
	}

	@Override
	public void run()
	{
		Set<SelectionKey> keys;
		Iterator<SelectionKey> iterator;
		SelectionKey key = null;
		int opts;

		while(!isShutdown())
			try
			{
				selector.select();
				keys = selector.selectedKeys();
				iterator = keys.iterator();

				while(iterator.hasNext())
				{
					key = iterator.next();
					iterator.remove();

					if(!key.isValid())
					{
						close(key);
						continue;
					}

					opts = key.readyOps();

					switch(opts)
					{
						case SelectionKey.OP_CONNECT:
							close(key);
							break;
						case SelectionKey.OP_ACCEPT:
							accept(key);
							break;
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
			catch(ClosedSelectorException e)
			{
				_log.error("Selector " + selector + " closed!");
				return;
			}
			catch(IOException e)
			{
				_log.error("Gameserver I/O error: " + e.getMessage());
				close(key);
			}
			catch(Exception e)
			{
				_log.error("", e);
			}
	}

	public void accept(SelectionKey key) throws IOException
	{
		ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
		SocketChannel sc;
		SelectionKey clientKey;

		sc = ssc.accept();
		sc.configureBlocking(false);
		clientKey = sc.register(selector, SelectionKey.OP_READ);

		GameServerConnection conn;
		clientKey.attach(conn = new GameServerConnection(clientKey));
		conn.setGameServer(new GameServer(conn));
	}

	public void read(SelectionKey key) throws IOException
	{
		SocketChannel channel = (SocketChannel) key.channel();
		GameServerConnection conn = (GameServerConnection) key.attachment();
		GameServer gs = conn.getGameServer();
		ByteBuffer buf = conn.getReadBuffer();

		int count;
		
		count = channel.read(buf);

		if(count == -1)
		{
			close(key);
			return;
		}
		else if(count == 0)
			return;

		buf.flip();

		while(tryReadPacket(key, gs, buf));
	}

	protected boolean tryReadPacket(SelectionKey key, GameServer gs, ByteBuffer buf) throws IOException
	{
		int pos = buf.position();
		// проверяем, хватает ли нам байт для чтения заголовка и не пустого тела пакета
		if(buf.remaining() > 2)
		{
			// получаем ожидаемый размер пакета
			int size = buf.getShort() & 0xffff;

			// проверяем корректность размера
			if(size <= 2)
			{
				throw new IOException("Incorrect packet size: <= 2");
			}

			//ожидаемый размер тела пакета
			size -= 2;

			// проверяем, хватает ли байт на чтение тела
			if(size <= buf.remaining())
			{
				//  apply limit
				int limit = buf.limit();
				buf.limit(pos + size + 2);

				ReceivablePacket rp = PacketHandler.handlePacket(gs, buf);

				if(rp != null)
				{
					rp.setByteBuffer(buf);
					rp.setClient(gs);

					if(rp.read())
						ThreadPoolManager.getInstance().execute(rp);

					rp.setByteBuffer(null);
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

	public void write(SelectionKey key) throws IOException
	{
		GameServerConnection conn = (GameServerConnection) key.attachment();
		GameServer gs = conn.getGameServer();
		SocketChannel channel = (SocketChannel) key.channel();
		ByteBuffer buf = getWriteBuffer();

		conn.disableWriteInterest();
		
		Queue<SendablePacket> sendQueue = conn.sendQueue;
		Lock sendLock = conn.sendLock;
		
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

				sp.setByteBuffer(buf);
				sp.setClient(gs);

				// write content to buffer
				sp.write();

				// size (incl header)
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
				conn.disableWriteInterest();
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
			buf.clear();
		
		if(!done)
		{
			if(conn.enableWriteInterest())
				selector.wakeup();
		}		
	}

	private ByteBuffer getWriteBuffer()
	{
		return writeBuffer;
	}

	public void close(SelectionKey key)
	{
		if(key == null)
			return;
		
		try
		{
			try
			{
				GameServerConnection conn = (GameServerConnection) key.attachment();
				if(conn != null)
					conn.onDisconnection();
			}
			finally
			{
				key.channel().close();
				key.cancel();
			}
		}
		catch(IOException e)
		{
			_log.error("", e);
		}
	}

	public boolean isShutdown()
	{
		return shutdown;
	}

	public void setShutdown(boolean shutdown)
	{
		this.shutdown = shutdown;
	}

}
