package org.l2j.gameserver.network.l2.s2c;

import org.l2j.mmocore.StaticPacket;

//пропадает почти весь интерфейс и пооявляется кнопка отказ
//связан с пакетом RequestLeaveCuriousHouse
@StaticPacket
public class ExCuriousHouseLeave extends L2GameServerPacket {
	public static final L2GameServerPacket STATIC = new ExCuriousHouseLeave();

	private ExCuriousHouseLeave() { }

	@Override
	protected void writeImpl() {  }

	@Override
	protected int packetSize() {
		return 5;
	}
}