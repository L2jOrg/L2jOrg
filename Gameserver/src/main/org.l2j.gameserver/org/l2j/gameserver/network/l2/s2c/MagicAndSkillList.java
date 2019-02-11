package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

/**
 * Format:   ddd
 * Пример пакета:
 * 40
 * c8 22 00 49
 * be 50 00 00
 * 86 25 0b 00
 * @author SYS
 */
public class MagicAndSkillList extends L2GameServerPacket
{
	private int _chaId;
	private int _unk1;
	private int _unk2;

	public MagicAndSkillList(Creature cha, int unk1, int unk2)
	{
		_chaId = cha.getObjectId();
		_unk1 = unk1;
		_unk2 = unk2;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_chaId);
		buffer.putInt(_unk1); // в снифе было 20670
		buffer.putInt(_unk2); // в снифе было 730502
	}
}