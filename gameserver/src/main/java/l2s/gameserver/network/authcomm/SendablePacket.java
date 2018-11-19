package l2s.gameserver.network.authcomm;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SendablePacket extends l2s.commons.net.nio.SendablePacket<AuthServerCommunication>
{
	private static final Logger _log = LoggerFactory.getLogger(SendablePacket.class);

	@Override
	public AuthServerCommunication getClient()
	{
		return AuthServerCommunication.getInstance();
	}

	@Override
	protected ByteBuffer getByteBuffer()
	{
		return getClient().getWriteBuffer();
	}

	public boolean write()
	{
		try
		{
			writeImpl();
		}
		catch(Exception e)
		{
			_log.error("", e);
		}
		return true;
	}

	protected abstract void writeImpl();
}
