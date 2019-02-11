package org.l2j.gameserver.network.l2.s2c;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

@StaticPacket
public class TutorialCloseHtmlPacket extends L2GameServerPacket {
	public static final L2GameServerPacket STATIC = new TutorialCloseHtmlPacket();

	private TutorialCloseHtmlPacket() { }

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer) {  }

	@Override
	protected int size(GameClient client) {
		return 3;
	}
}