package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.instancemanager.clansearch.ClanSearchManager;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.clansearch.ClanSearchPlayer;
import org.l2j.gameserver.model.clansearch.base.ClanSearchListType;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.ExPledgeRecruitApplyInfo;
import org.l2j.gameserver.network.l2.s2c.SystemMessagePacket;

import java.nio.ByteBuffer;

/**
 * @author GodWorld
 * @reworked by Bonux
**/
public class RequestPledgeWaitingApply extends L2GameClientPacket
{
	private ClanSearchListType _searchType;
	private int _clanId;
	private String _desc;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_searchType = ClanSearchListType.getType(buffer.getInt());
		_clanId = buffer.getInt();
		_desc = readString(buffer);
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
		if(activeChar == null)
			return;

		if(activeChar.getClan() != null)
		{
			activeChar.sendPacket(SystemMsg.ONLY_THE_CLAN_LEADER_OR_SOMEONE_WITH_RANK_MANAGEMENT_AUTHORITY_MAY_REGISTER_THE_CLAN);
			return;
		}

		ClanSearchPlayer csPlayer = new ClanSearchPlayer(activeChar.getObjectId(), activeChar.getName(), activeChar.getLevel(), activeChar.getBaseClassId(), _clanId, _searchType, _desc);
		if(ClanSearchManager.getInstance().addPlayer(csPlayer))
			activeChar.sendPacket(ExPledgeRecruitApplyInfo.WAITING);
		else
			activeChar.sendPacket(new SystemMessagePacket(SystemMsg.YOU_MAY_APPLY_FOR_ENTRY_AFTER_S1_MINUTES_DUE_TO_CANCELLING_YOUR_APPLICATION).addInteger(5)); // TODO[Bonux]: Fix me.
	}
}