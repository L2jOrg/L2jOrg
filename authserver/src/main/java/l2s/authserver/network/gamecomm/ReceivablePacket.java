package l2s.authserver.network.gamecomm;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class ReceivablePacket extends l2s.commons.net.nio.ReceivablePacket<GameServer>
{
	private static final Logger _log = LoggerFactory.getLogger(ReceivablePacket.class);

	protected GameServer _gs;
	protected ByteBuffer _buf;

	protected void setByteBuffer(ByteBuffer buf)
	{
		_buf = buf;
	}

	@Override
	protected ByteBuffer getByteBuffer()
	{
		return _buf;
	}

	protected void setClient(GameServer gs)
	{
		_gs = gs;
	}

	@Override
	public GameServer getClient()
	{
		return _gs;
	}

	public GameServer getGameServer()
	{
		return getClient();
	}

	@Override
	public final boolean read()
	{
		try
		{
			readImpl();
		}
		catch(Exception e)
		{
			_log.error("", e);
		}
		return true;
	}

	@Override
	public final void run()
	{
		try
		{
			runImpl();
		}
		catch(Exception e)
		{
			_log.error("", e);
		}
	}

	protected abstract void readImpl();

	protected abstract void runImpl();

	public void sendPacket(SendablePacket packet)
	{
		getGameServer().sendPacket(packet);
	}
}