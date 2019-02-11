package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ShowTutorialMarkPacket extends L2GameServerPacket
{
	private boolean _quest;
	private int _tutorialId;

	public ShowTutorialMarkPacket(boolean quest, int tutorialId)
	{
		_quest = quest;
		_tutorialId = tutorialId;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.put((byte) (_quest? 0x01 : 0x00));
		buffer.putInt(_tutorialId);
	}
}