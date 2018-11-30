package org.l2j.gameserver.network.l2.s2c;

import org.l2j.mmocore.StaticPacket;

/**
 * @author VISTALL
 * @date 12:11/05.03.2011
 */
@StaticPacket
public class ExDominionWarEnd extends L2GameServerPacket {

	public static final L2GameServerPacket STATIC = new ExDominionWarEnd();

	private ExDominionWarEnd() { }

	@Override
	public void writeImpl() {  }
}
