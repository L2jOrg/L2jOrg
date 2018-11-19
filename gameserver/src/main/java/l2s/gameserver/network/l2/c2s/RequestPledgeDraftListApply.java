package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.instancemanager.clansearch.ClanSearchManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.clansearch.ClanSearchPlayer;
import l2s.gameserver.model.clansearch.base.ClanSearchListType;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;

/**
 * @author GodWorld
 * @reworked by Bonux
**/
public class RequestPledgeDraftListApply extends L2GameClientPacket
{
	private int _applyType;
	private ClanSearchListType _searchType;

	@Override
	protected void readImpl()
	{
		_applyType = readD();
		_searchType = ClanSearchListType.getType(readD());
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(activeChar.getClan() != null)
		{
			activeChar.sendPacket(SystemMsg.ONLY_THE_CLAN_LEADER_OR_SOMEONE_WITH_RANK_MANAGEMENT_AUTHORITY_MAY_REGISTER_THE_CLAN);
			return;
		}

		switch(_applyType)
		{
			case 0:
				if(!ClanSearchManager.getInstance().removeWaiter(activeChar.getObjectId()))
					break;

				activeChar.sendPacket(SystemMsg.ENTRY_APPLICATION_CANCELLED_YOU_MAY_APPLY_TO_A_NEW_CLAN_AFTER_5_MINUTES);
				break;
			case 1:
				ClanSearchPlayer csPlayer = new ClanSearchPlayer(activeChar.getObjectId(), activeChar.getName(), activeChar.getLevel(), activeChar.getBaseClassId(), _searchType);
				if(ClanSearchManager.getInstance().addPlayer(csPlayer))
					activeChar.sendPacket(SystemMsg.ENTERED_INTO_WAITING_LIST_NAME_IS_AUTOMATICALLY_DELETED_AFTER_30_DAYS_IF_DELETE_FROM_WAITING_LIST_IS_USED_YOU_CANNOT_ENTER_NAMES_INTO_THE_WAITING_LIST_FOR_5_MINUTES);
				else
					activeChar.sendPacket(new SystemMessagePacket(SystemMsg.YOU_MAY_APPLY_FOR_ENTRY_AFTER_S1_MINUTES_DUE_TO_CANCELLING_YOUR_APPLICATION).addInteger(5)); // TODO[Bonux]: Fix me.
				break;
		}
	}
}