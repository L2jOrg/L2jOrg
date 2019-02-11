package org.l2j.gameserver.network.l2.s2c;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

@StaticPacket
public class ExPVPMatchCCRetire extends L2GameServerPacket  {

	public static final L2GameServerPacket STATIC = new ExPVPMatchCCRetire();

	private ExPVPMatchCCRetire() { }

	@Override
	public void writeImpl(GameClient client, ByteBuffer buffer)  {  }
}