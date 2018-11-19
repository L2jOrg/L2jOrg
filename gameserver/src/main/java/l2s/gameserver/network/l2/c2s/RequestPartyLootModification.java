package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Party;
import l2s.gameserver.model.Player;

public class RequestPartyLootModification extends L2GameClientPacket
{
	private byte _mode;

	@Override
	protected void readImpl()
	{
		_mode = (byte) readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(_mode < 0 || _mode > Party.ITEM_ORDER_SPOIL)
			return;

		Party party = activeChar.getParty();
		if(party == null || _mode == party.getLootDistribution() || party.getPartyLeader() != activeChar)
			return;

		party.requestLootChange(_mode);
	}
}
