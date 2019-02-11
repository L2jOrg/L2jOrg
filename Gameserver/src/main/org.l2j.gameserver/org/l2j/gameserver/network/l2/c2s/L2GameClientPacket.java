package org.l2j.gameserver.network.l2.c2s;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import org.l2j.gameserver.GameServer;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.network.l2.s2c.L2GameServerPacket;

import io.github.joealisson.mmocore.ReadablePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Packets received by the game server from clients
 */
public abstract class L2GameClientPacket extends ReadablePacket<GameClient>
{
	private static final Logger _log = LoggerFactory.getLogger(L2GameClientPacket.class);

	@Override
	public final boolean read(ByteBuffer buffer) {
		try {
			readImpl(buffer);
			return true;
		} catch(BufferUnderflowException e) {
			client.onPacketReadFail();
			_log.error("Client: " + client + " - Failed reading: " + getType() + " - Server Version: " + GameServer.getInstance().getVersion(), e);
		} catch(Exception e) {
			_log.error("Client: " + client + " - Failed reading: " + getType() + " - Server Version: " + GameServer.getInstance().getVersion(), e);
		}
		return false;
	}

	protected abstract void readImpl(ByteBuffer buffer) throws Exception;

	@Override
	public final void run()
	{
		try
		{
			runImpl();
		}
		catch(Exception e)
		{
			_log.error("Client: " + client + " - Failed running: " + getType() + " - Server Version: " + GameServer.getInstance().getVersion(), e);
		}
	}

	protected abstract void runImpl() throws Exception;

	protected String readString(ByteBuffer buffer, int len)
	{
		String ret = readString(buffer);
		return ret.length() > len ? ret.substring(0, len) : ret;
	}

	protected void sendPacket(L2GameServerPacket packet)
	{
		client.sendPacket(packet);
	}

	protected void sendPacket(L2GameServerPacket... packets)
	{
		client.sendPacket(packets);
	}

	public String getType()
	{
		return "[C] " + getClass().getSimpleName();
	}
}