package org.l2j.gameserver.network.l2.s2c;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

//пир отправке этого пакета на экране появляется иконка получения письма
@StaticPacket
public class ExCuriousHouseEnter extends L2GameServerPacket {
	public static final L2GameServerPacket STATIC = new ExCuriousHouseEnter();

	private ExCuriousHouseEnter() { }

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer) {  }

	@Override
	protected int size(GameClient client) {
		return 5;
	}
}
