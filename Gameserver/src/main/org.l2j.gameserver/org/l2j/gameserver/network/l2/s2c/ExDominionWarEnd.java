package org.l2j.gameserver.network.l2.s2c;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

/**
 * @author VISTALL
 * @date 12:11/05.03.2011
 */
@StaticPacket
public class ExDominionWarEnd extends L2GameServerPacket {

	public static final L2GameServerPacket STATIC = new ExDominionWarEnd();

	private ExDominionWarEnd() { }

	@Override
	public void writeImpl(GameClient client, ByteBuffer buffer) {  }

	@Override
	protected int size(GameClient client) {
		return 5;
	}
}
