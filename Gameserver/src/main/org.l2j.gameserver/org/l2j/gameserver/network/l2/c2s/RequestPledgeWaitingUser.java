package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.instancemanager.clansearch.ClanSearchManager;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.clansearch.ClanSearchPlayer;
import org.l2j.gameserver.network.l2.s2c.ExPledgeWaitingList;
import org.l2j.gameserver.network.l2.s2c.ExPledgeWaitingUser;

import java.nio.ByteBuffer;

/**
 * @author GodWorld
 * @reworked by Bonux
**/
public class RequestPledgeWaitingUser extends L2GameClientPacket
{
	private int _clanId;
	private int _charId;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_clanId = buffer.getInt();
		_charId = buffer.getInt();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
		if(activeChar == null)
			return;

		ClanSearchPlayer csPlayer = ClanSearchManager.getInstance().getApplicant(_clanId, _charId);
		if(csPlayer == null)
			activeChar.sendPacket(new ExPledgeWaitingList(_clanId));
		else
			activeChar.sendPacket(new ExPledgeWaitingUser(_charId, csPlayer.getDesc()));
	}
}