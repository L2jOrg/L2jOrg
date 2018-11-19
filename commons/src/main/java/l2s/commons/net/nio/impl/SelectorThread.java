package l2s.commons.net.nio.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("rawtypes")
public class SelectorThread<T extends MMOClient> extends Thread
{
	private static final Logger _log = LoggerFactory.getLogger(SelectorThread.class);

	private final Selector _selector;

	// Implementations
	private final IPacketHandler<T> _packetHandler;
	private final IMMOExecutor<T> _executor;
	private final IClientFactory<T> _clientFactory;
	private final IAcceptFilter _acceptFilter;

	// Configs
	private final SelectorConfig _sc;
	private final int HELPER_BUFFER_SIZE;

	// MAIN BUFFERS
	private ByteBuffer DIRECT_WRITE_BUFFER;
	private final ByteBuffer WRITE_BUFFER, READ_BUFFER;
	private T WRITE_CLIENT;

	// ByteBuffers General Purpose Pool
	private final Queue<ByteBuffer> _bufferPool;
	private final List<MMOConnection<T>> _connections;

	private final SelectorStats _stats;

	private boolean _shutdown;

	public SelectorThread(SelectorConfig sc, SelectorStats stats, IPacketHandler<T> packetHandler, IMMOExecutor<T> executor, IClientFactory<T> clientFactory, IAcceptFilter acceptFilter) throws IOException
	{
		_selector = Selector.open();

		_sc = sc;
		_stats = stats;
		_acceptFilter = acceptFilter;
		_packetHandler = packetHandler;
		_clientFactory = clientFactory;
		_executor = executor;

		_bufferPool = new ArrayDeque<ByteBuffer>(_sc.HELPER_BUFFER_COUNT);
		_connections = new CopyOnWriteArrayList<MMOConnection<T>>();

		DIRECT_WRITE_BUFFER = ByteBuffer.wrap(new byte[_sc.WRITE_BUFFER_SIZE]).order(_sc.BYTE_ORDER);
		WRITE_BUFFER = ByteBuffer.wrap(new byte[_sc.WRITE_BUFFER_SIZE]).order(_sc.BYTE_ORDER);
		READ_BUFFER = ByteBuffer.wrap(new byte[_sc.READ_BUFFER_SIZE]).order(_sc.BYTE_ORDER);
		HELPER_BUFFER_SIZE = Math.max(_sc.READ_BUFFER_SIZE, _sc.WRITE_BUFFER_SIZE);

		for(int i = 0; i < _sc.HELPER_BUFFER_COUNT; i++)
			_bufferPool.add(ByteBuffer.wrap(new byte[HELPER_BUFFER_SIZE]).order(_sc.BYTE_ORDER));
	}

	public void openServerSocket(InetAddress address, int tcpPort) throws IOException
	{
		ServerSocketChannel selectable = ServerSocketChannel.open();
		selectable.configureBlocking(false);

		selectable.socket().bind(address == null ? new InetSocketAddress(tcpPort) : new InetSocketAddress(address, tcpPort), _sc.BACKLOG);
		selectable.register(getSelector(), selectable.validOps());
		setName("SelectorThread:" + selectable.socket().getLocalPort());
	}

	protected ByteBuffer getPooledBuffer()
	{
		if(_bufferPool.isEmpty())
			return ByteBuffer.wrap(new byte[HELPER_BUFFER_SIZE]).order(_sc.BYTE_ORDER);
		return _bufferPool.poll();
	}

	protected void recycleBuffer(ByteBuffer buf)
	{
		if(_bufferPool.size() < _sc.HELPER_BUFFER_COUNT)
		{
			buf.clear();
			_bufferPool.add(buf);
		}
	}

	protected void freeBuffer(ByteBuffer buf, MMOConnection<T> con)
	{
		if(buf == READ_BUFFER)
			READ_BUFFER.clear();
		else
		{
			con.setReadBuffer(null);
			recycleBuffer(buf);
		}
	}

	@Override
	public void run()
	{
		int totalKeys = 0;
		Set<SelectionKey> keys = null;
		Iterator<SelectionKey> itr = null;
		Iterator<MMOConnection<T>> conItr = null;
		SelectionKey key = null;
		MMOConnection<T> con = null;
		long currentMillis = 0L;

		// main loop
		for(;;)
			try
		{

				if(isShuttingDown())
				{
					closeSelectorThread();
					break;
				}

				currentMillis = System.currentTimeMillis();

				conItr = _connections.iterator();
				while(conItr.hasNext())
				{
					con = conItr.next();
					if(!(con.getClient()).isAuthed() && currentMillis - con.getConnectionOpenTime() >= _sc.AUTH_TIMEOUT)
					{
						closeConnectionImpl(con);
						continue;
					}
					if (con.isPengingClose())
						if (!con.isPendingWrite() || currentMillis - con.getPendingCloseTime() >= _sc.CLOSEWAIT_TIMEOUT)
						{
							closeConnectionImpl(con);
							continue;
						}
					if (con.isPendingWrite())
						if (currentMillis - con.getPendingWriteTime() >= _sc.INTEREST_DELAY)
							con.enableWriteInterest();
				}

				totalKeys = getSelector().selectNow();

				if(totalKeys > 0)
				{
					keys = getSelector().selectedKeys();
					itr = keys.iterator();

					while(itr.hasNext())
					{
						key = itr.next();
						itr.remove();

						if(key.isValid())
							try
						{
								if(key.isAcceptable())
								{
									acceptConnection(key);
									continue;
								}
								else if(key.isConnectable())
								{
									finishConnection(key);
									continue;
								}


								if(key.isReadable())
									readPacket(key);
								if(key.isValid())
									if(key.isWritable())
										writePacket(key);
						}
						catch(CancelledKeyException cke)
						{

						}
					}

					//не делаем паузы, в случае если были какие-либо операции I/O
					continue;
				}

				try
				{
					Thread.sleep(_sc.SLEEP_TIME);
				}
				catch(InterruptedException ie)
				{

				}
		}
		catch(IOException e)
		{
			_log.error("Error in " + getName(), e);

			try
			{
				Thread.sleep(1000L);
			}
			catch(InterruptedException ie)
			{

			}
		}
	}

	@SuppressWarnings("unchecked")
	protected void finishConnection(SelectionKey key)
	{
		try
		{
			((SocketChannel) key.channel()).finishConnect();
		}
		catch(IOException e)
		{
			MMOConnection<T> con = (MMOConnection<T>) key.attachment();
			T client = con.getClient();
			client.getConnection().onForcedDisconnection();
			closeConnectionImpl(client.getConnection());
		}
	}

	@SuppressWarnings("unchecked")
	protected void acceptConnection(SelectionKey key)
	{
		ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
		SocketChannel sc;
		SelectionKey clientKey;
		try
		{
			while((sc = ssc.accept()) != null)
				if(getAcceptFilter() == null || getAcceptFilter().accept(sc))
				{
					sc.configureBlocking(false);
					clientKey = sc.register(getSelector(), SelectionKey.OP_READ);

					MMOConnection<T> con = new MMOConnection<T>(this, sc.socket(), clientKey);
					T client = getClientFactory().create(con);
					client.setConnection(con);
					con.setClient(client);
					clientKey.attach(con);

					_connections.add(con);
					_stats.increaseOpenedConnections();
				}
				else
				{
					sc.close();
				}
		}
		catch(IOException e)
		{
			_log.error("Error in " + getName(), e);
		}
	}

	@SuppressWarnings("unchecked")
	protected void readPacket(SelectionKey key)
	{
		MMOConnection<T> con = (MMOConnection<T>) key.attachment();

		if(con.isClosed())
			return;

		ByteBuffer buf;
		int result = -2;

		if((buf = con.getReadBuffer()) == null)
			buf = READ_BUFFER;

		// if we try to to do a read with no space in the buffer it will read 0 bytes
		// going into infinite loop
		if(buf.position() == buf.limit())
		{
			_log.error("Read buffer exhausted for client : " + con.getClient() + ", try to adjust buffer size, current : " + buf.capacity() + ", primary : " + (buf == READ_BUFFER) + ". Closing connection.");
			closeConnectionImpl(con);
		}
		else
		{

			try
			{
				result = con.getReadableByteChannel().read(buf);
			}
			catch(IOException e)
			{
				//error handling goes bellow
			}

			if(result > 0)
			{
				buf.flip();

				_stats.increaseIncomingBytes(result);

				int i;
				// читаем из буфера максимум пакетов
				for(i = 0; this.tryReadPacket2(key, con, buf); i++)
				{}
			}
			else if(result == 0)
				closeConnectionImpl(con);
			else if(result == -1)
				closeConnectionImpl(con);
			else
			{
				con.onForcedDisconnection();
				closeConnectionImpl(con);
			}
		}

		if(buf == READ_BUFFER)
			buf.clear();
	}

	protected boolean tryReadPacket2(SelectionKey key, MMOConnection<T> con, ByteBuffer buf)
	{
		if(con.isClosed())
			return false;

		int pos = buf.position();
		// проверяем, хватает ли нам байт для чтения заголовка и не пустого тела пакета
		if(buf.remaining() > _sc.HEADER_SIZE)
		{
			// получаем ожидаемый размер пакета
			int size = buf.getShort() & 0xffff;

			// проверяем корректность размера
			if(size <= _sc.HEADER_SIZE || size > _sc.PACKET_SIZE)
			{
				_log.error("Incorrect packet size : " + size + "! Client : " + con.getClient() + ". Closing connection.");
				closeConnectionImpl(con);
				return false;
			}

			//ожидаемый размер тела пакета
			size -= _sc.HEADER_SIZE;

			// проверяем, хватает ли байт на чтение тела
			if(size <= buf.remaining())
			{
				_stats.increaseIncomingPacketsCount();
				parseClientPacket(getPacketHandler(), buf, size, con);
				buf.position(pos + size + _sc.HEADER_SIZE);

				// закончили чтение из буфера, почистим
				if(!buf.hasRemaining())
				{
					freeBuffer(buf, con);
					return false;
				}

				return true;
			}

			// не хватает данных на чтение тела пакета, сбрасываем позицию
			buf.position(pos);
		}

		if(pos == buf.capacity())
			_log.warn("Read buffer exhausted for client : " + con.getClient() + ", try to adjust buffer size, current : " + buf.capacity() + ", primary : " + (buf == READ_BUFFER) + ".");

		// не хватает данных, переносим содержимое первичного буфера во вторичный
		if(buf == READ_BUFFER)
			allocateReadBuffer(con);
		else
			buf.compact();

		return false;
	}

	protected void allocateReadBuffer(MMOConnection<T> con)
	{
		con.setReadBuffer(getPooledBuffer().put(READ_BUFFER));
		READ_BUFFER.clear();
	}

	protected boolean parseClientPacket(IPacketHandler<T> handler, ByteBuffer buf, int dataSize, MMOConnection<T> con)
	{
		T client = con.getClient();

		int pos = buf.position();

		client.decrypt(buf, dataSize);
		buf.position(pos);

		if(buf.hasRemaining())
		{
			//  apply limit
			int limit = buf.limit();
			buf.limit(pos + dataSize);
			ReceivablePacket<T> rp = handler.handlePacket(buf, client);

			if(rp != null)
			{
				rp.setByteBuffer(buf);
				rp.setClient(client);

				if(rp.read())
					con.recvPacket(rp);

				rp.setByteBuffer(null);
			}
			buf.limit(limit);
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	protected void writePacket(SelectionKey key)
	{
		MMOConnection<T> con = (MMOConnection<T>) key.attachment();

		prepareWriteBuffer(con);

		DIRECT_WRITE_BUFFER.flip();
		int size = DIRECT_WRITE_BUFFER.remaining();

		int result = -1;

		try
		{
			result = con.getWritableChannel().write(DIRECT_WRITE_BUFFER);
		}
		catch(IOException e)
		{
			// error handling goes on the if bellow
		}

		// check if no error happened
		if(result >= 0)
		{
			_stats.increaseOutgoingBytes(result);

			// check if we written everything
			if(result != size)
				con.createWriteBuffer(DIRECT_WRITE_BUFFER);

			if(!con.getSendQueue().isEmpty() || con.hasPendingWriteBuffer())
				// запись не завершена
				con.scheduleWriteInterest();
			else
				con.disableWriteInterest();
		}
		else
		{
			con.onForcedDisconnection();
			closeConnectionImpl(con);
		}
	}

	protected T getWriteClient()
	{
		return WRITE_CLIENT;
	}

	protected ByteBuffer getWriteBuffer()
	{
		return WRITE_BUFFER;
	}

	protected void prepareWriteBuffer(MMOConnection<T> con)
	{
		WRITE_CLIENT = con.getClient();
		DIRECT_WRITE_BUFFER.clear();

		if(con.hasPendingWriteBuffer()) // если осталось что-то с прошлого раза
			con.movePendingWriteBufferTo(DIRECT_WRITE_BUFFER);

		if(DIRECT_WRITE_BUFFER.hasRemaining() && !con.hasPendingWriteBuffer())
		{
			int i;
			Queue<SendablePacket<T>> sendQueue = con.getSendQueue();
			SendablePacket<T> sp;

			for(i = 0; i < _sc.MAX_SEND_PER_PASS; i++)
			{
				con.lock();
				try
				{
					if((sp = sendQueue.poll()) == null)
						break;
				}
				finally
				{
					con.unlock();
				}

				try
				{
					_stats.increaseOutgoingPacketsCount();
					putPacketIntoWriteBuffer(sp, true); // записываем пакет в WRITE_BUFFER
					WRITE_BUFFER.flip();
					if(DIRECT_WRITE_BUFFER.remaining() >= WRITE_BUFFER.limit())
						DIRECT_WRITE_BUFFER.put(WRITE_BUFFER);
					else
						// если не осталось места в DIRECT_WRITE_BUFFER для WRITE_BUFFER то мы его запишев в следующий раз
					{
						con.createWriteBuffer(WRITE_BUFFER);
						break;
					}
				}
				catch(Exception e)
				{
					_log.error("Error in " + getName(), e);
					break;
				}
			}
		}

		WRITE_BUFFER.clear();
		WRITE_CLIENT = null;
	}

	protected final void putPacketIntoWriteBuffer(SendablePacket<T> sp, boolean encrypt)
	{
		WRITE_BUFFER.clear();

		// reserve space for the size
		int headerPos = WRITE_BUFFER.position();
		WRITE_BUFFER.position(headerPos + _sc.HEADER_SIZE);

		// write content to buffer
		sp.write();

		// size (incl header)
		int dataSize = WRITE_BUFFER.position() - headerPos - _sc.HEADER_SIZE;
		if(dataSize == 0)
		{
			WRITE_BUFFER.position(headerPos);
			return;
		}
		WRITE_BUFFER.position(headerPos + _sc.HEADER_SIZE);
		if(encrypt)
		{
			WRITE_CLIENT.encrypt(WRITE_BUFFER, dataSize);
			// recalculate size after encryption
			dataSize = WRITE_BUFFER.position() - headerPos - _sc.HEADER_SIZE;
		}

		// prepend header
		WRITE_BUFFER.position(headerPos);
		WRITE_BUFFER.putShort((short) (_sc.HEADER_SIZE + dataSize));
		WRITE_BUFFER.position(headerPos + _sc.HEADER_SIZE + dataSize);
	}

	protected SelectorConfig getConfig()
	{
		return _sc;
	}

	protected Selector getSelector()
	{
		return _selector;
	}

	protected IMMOExecutor<T> getExecutor()
	{
		return _executor;
	}

	protected IPacketHandler<T> getPacketHandler()
	{
		return _packetHandler;
	}

	protected IClientFactory<T> getClientFactory()
	{
		return _clientFactory;
	}

	protected IAcceptFilter getAcceptFilter()
	{
		return _acceptFilter;
	}

	@SuppressWarnings("unchecked")
	protected void closeConnectionImpl(MMOConnection<T> con)
	{
		try
		{
			// notify connection
			con.onDisconnection();
		}
		finally
		{
			try
			{
				// close socket and the SocketChannel
				con.close();
			}
			catch(IOException e)
			{
				// ignore, we are closing anyway
			}
			finally
			{
				try
				{
					// очистить буферы
					con.releaseBuffers();
					// очистим очереди
					con.clearQueues();
					// обнуляем соединение у клиента
					con.getClient().setConnection(null);
					// обнуляем соединение у ключа
					con.getSelectionKey().attach(null);
					// отменяем ключ
					con.getSelectionKey().cancel();
				}
				finally
				{
					_connections.remove(con);
					_stats.decreaseOpenedConnections();
				}
			}
		}
	}

	public void shutdown()
	{
		_shutdown = true;
	}

	public boolean isShuttingDown()
	{
		return _shutdown;
	}

	protected void closeAllChannels()
	{
		Set<SelectionKey> keys = getSelector().keys();
		for(SelectionKey key : keys)
			try
		{
				key.channel().close();
		}
		catch(IOException e)
		{
			// ignore
		}
	}

	protected void closeSelectorThread()
	{
		closeAllChannels();

		try
		{
			getSelector().close();
		}
		catch(IOException e)
		{
			// Ignore
		}
	}
}