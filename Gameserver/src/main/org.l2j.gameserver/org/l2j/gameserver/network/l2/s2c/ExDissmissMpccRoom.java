package org.l2j.gameserver.network.l2.s2c;

import org.l2j.mmocore.StaticPacket;

/**
 * @author VISTALL
 */
@StaticPacket
public class ExDissmissMpccRoom extends L2GameServerPacket {
	public static final L2GameServerPacket STATIC = new ExDissmissMpccRoom();

	private ExDissmissMpccRoom() { }

	@Override
	protected void writeImpl() {  }
}