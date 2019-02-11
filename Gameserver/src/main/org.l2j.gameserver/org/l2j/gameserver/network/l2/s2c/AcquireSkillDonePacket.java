package org.l2j.gameserver.network.l2.s2c;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

/**
 * Reworked: VISTALL
 */
@StaticPacket
public class AcquireSkillDonePacket extends L2GameServerPacket {
	public static final L2GameServerPacket STATIC = new AcquireSkillDonePacket();

	private AcquireSkillDonePacket() { }

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer) {  }

	@Override
	protected int size(GameClient client) {
		return 5;
	}
}