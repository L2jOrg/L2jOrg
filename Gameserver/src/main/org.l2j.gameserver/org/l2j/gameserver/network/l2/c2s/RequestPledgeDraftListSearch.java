package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.clansearch.ClanSearchWaiterParams;
import org.l2j.gameserver.model.clansearch.base.ClanSearchPlayerRoleType;
import org.l2j.gameserver.model.clansearch.base.ClanSearchPlayerSortType;
import org.l2j.gameserver.model.clansearch.base.ClanSearchSortOrder;
import org.l2j.gameserver.network.l2.s2c.ExPledgeDraftListSearch;

import java.nio.ByteBuffer;

/**
 * @author GodWorld
 * @reworked by Bonux
**/
public class RequestPledgeDraftListSearch extends L2GameClientPacket
{
	private int _minLevel;
	private int _maxLevel;
	private ClanSearchPlayerRoleType _role;
	private String _charName;
	private ClanSearchPlayerSortType _sortType;
	private ClanSearchSortOrder _sortOrder;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_minLevel = Math.max(0, Math.min(buffer.getInt(), 99));
		_maxLevel = Math.max(0, Math.min(buffer.getInt(), 99));
		_role = ClanSearchPlayerRoleType.valueOf(buffer.getInt());

		_charName = readString(buffer).trim().toLowerCase();

		if(_charName.length() > 255)
			_charName = _charName.substring(0, 255);

		_sortType = ClanSearchPlayerSortType.valueOf(buffer.getInt());
		_sortOrder = ClanSearchSortOrder.valueOf(buffer.getInt());
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
		if(activeChar == null)
			return;

		//if(!((L2GameClient)client).getFloodProtectors().getClanSearch().tryPerformAction(FloodAction.CLAN_BOARD_DRAFT_SEARCH))
			//return;

		activeChar.sendPacket(new ExPledgeDraftListSearch(new ClanSearchWaiterParams(_minLevel, _maxLevel, _role, _charName, _sortType, _sortOrder)));
	}
}