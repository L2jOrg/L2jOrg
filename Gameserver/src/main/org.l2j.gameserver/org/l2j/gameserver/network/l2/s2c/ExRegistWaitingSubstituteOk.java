package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

/**
 * Created by IntelliJ IDEA. User: Cain Date: 25.05.12 Time: 21:05 запрос
 * выбранному чару на вступление в пати
 */
public class ExRegistWaitingSubstituteOk extends L2GameServerPacket
{
	private final Player _partyLeader;

	public ExRegistWaitingSubstituteOk(final Player player)
	{
		_partyLeader = player;
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		/*
		 * TODO
		 */
	}
}
