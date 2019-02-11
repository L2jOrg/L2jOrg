package org.l2j.gameserver.network.l2.s2c;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

/**
 * @author Bonux
**/
@StaticPacket
public final class ExEnchantOneFail extends L2GameServerPacket  {
	public static final L2GameServerPacket STATIC = new ExEnchantOneFail();

	private ExEnchantOneFail() {  }

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer) {  }

	@Override
	protected int size(GameClient client) {
		return 5;
	}
}