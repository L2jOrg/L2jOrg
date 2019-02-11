package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.network.l2.s2c.VersionCheckPacket;
import org.l2j.gameserver.network.l2.s2c.SendStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * packet type id 0x0E
 * format:	cdbd
 */
public class ProtocolVersion extends L2GameClientPacket
{
	private static final Logger _log = LoggerFactory.getLogger(ProtocolVersion.class);

	private int version;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		version = buffer.getInt();
	}

	@Override
	protected void runImpl()
	{
		if(version == -2)
		{
			client.closeNow();
			return;
		}
		else if(version == -3)
		{
			_log.info("Status request from IP : " + client.getIpAddr());
			client.close(new SendStatus());
			return;
		}
		else if(!Config.AVAILABLE_PROTOCOL_REVISIONS.contains(version))
		{
			_log.warn("Unknown protocol revision : {}, client : {}", version, client);
			client.close(new VersionCheckPacket(null));
			return;
		}

		client.setRevision(version);
		sendPacket(new VersionCheckPacket(client.enableCrypt()));
	}
}