package l2s.gameserver.model.clansearch;

import l2s.gameserver.model.clansearch.base.ClanSearchClanSortType;
import l2s.gameserver.model.clansearch.base.ClanSearchListType;
import l2s.gameserver.model.clansearch.base.ClanSearchSortOrder;
import l2s.gameserver.model.clansearch.base.ClanSearchTargetType;

/**
 * @author GodWorld
 * @reworked by Bonux
**/
public class ClanSearchParams
{
	private final int _clanLevel;
	private final ClanSearchListType _listType;
	private final ClanSearchTargetType _targetType;
	private final String _targetName;
	private final ClanSearchClanSortType _sortType;
	private final ClanSearchSortOrder _sortOrder;
	private final int _currentPage;
	private final int _application;

	public ClanSearchParams(int clanLevel, ClanSearchListType searchListType, ClanSearchTargetType targetType, String targetName, ClanSearchClanSortType sortType, ClanSearchSortOrder sortOrder, int currentPage, int application)
	{
		_clanLevel = clanLevel;
		_listType = searchListType;
		_targetType = targetType;
		_targetName = targetName;
		_sortType = sortType;
		_sortOrder = sortOrder;
		_currentPage = Math.max(0, currentPage - 1);
		_application = application;
	}

	public int getClanLevel()
	{
		return _clanLevel;
	}

	public ClanSearchListType getSearchType()
	{
		return _listType;
	}

	public ClanSearchTargetType getTargetType()
	{
		return _targetType;
	}

	public String getName()
	{
		return _targetName;
	}

	public ClanSearchClanSortType getSortType()
	{
		return _sortType;
	}

	public ClanSearchSortOrder getSortOrder()
	{
		return _sortOrder;
	}

	public int getCurrentPage()
	{
		return _currentPage;
	}

	public int getApplication()
	{
		return _application;
	}
}