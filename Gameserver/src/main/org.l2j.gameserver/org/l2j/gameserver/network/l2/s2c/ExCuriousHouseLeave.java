package org.l2j.gameserver.network.l2.s2c;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

//пропадает почти весь интерфейс и пооявляется кнопка отказ
//связан с пакетом RequestLeaveCuriousHouse
@StaticPacket
public class ExCuriousHouseLeave extends L2GameServerPacket {
	public static final L2GameServerPacket STATIC = new ExCuriousHouseLeave();

	private ExCuriousHouseLeave() { }

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer) {  }

	@Override
	protected int size(GameClient client) {
		return 5;
	}
}