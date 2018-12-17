package org.l2j.gameserver.network.l2.s2c;

import io.github.joealisson.mmocore.StaticPacket;

//пир отправке этого пакета на экране появляется иконка получения письма
@StaticPacket
public class ExCuriousHouseEnter extends L2GameServerPacket {
	public static final L2GameServerPacket STATIC = new ExCuriousHouseEnter();

	private ExCuriousHouseEnter() { }

	@Override
	protected void writeImpl() {  }

	@Override
	protected int packetSize() {
		return 5;
	}
}
