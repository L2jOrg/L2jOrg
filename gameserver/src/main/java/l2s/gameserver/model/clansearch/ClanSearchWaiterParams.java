package l2s.gameserver.model.clansearch;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.World;
import l2s.gameserver.model.clansearch.base.ClanSearchPlayerRoleType;
import l2s.gameserver.model.clansearch.base.ClanSearchPlayerSortType;
import l2s.gameserver.model.clansearch.base.ClanSearchSortOrder;

/**
 * @author GodWorld
 * @reworked by Bonux
**/
public class ClanSearchWaiterParams
{
	private final int _minLevel;
	private final int _maxLevel;
	private final ClanSearchPlayerRoleType _role;
	private final String _charName;
	private final ClanSearchPlayerSortType _sortType;
	private final ClanSearchSortOrder _sortOrder;

	public ClanSearchWaiterParams(int minLevel, int maxLevel, ClanSearchPlayerRoleType role, String charName, ClanSearchPlayerSortType sortType, ClanSearchSortOrder sortOrder)
	{
		_minLevel = minLevel;
		_maxLevel = maxLevel;
		_role = role;
		_charName = charName;
		_sortType = sortType;
		_sortOrder = sortOrder;
	}

	public int getMinLevel()
	{
		return _minLevel;
	}

	public int getMaxLevel()
	{
		return _maxLevel;
	}

	public ClanSearchPlayerRoleType getRole()
	{
		return _role;
	}

	public String getCharName()
	{
		return _charName;
	}

	public ClanSearchPlayerSortType getSortType()
	{
		return _sortType;
	}

	public ClanSearchSortOrder getSortOrder()
	{
		return _sortOrder;
	}
}