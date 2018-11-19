package l2s.gameserver.model.clansearch;

import l2s.gameserver.model.clansearch.base.ClanSearchListType;

/**
 * @author GodWorld
 * @reworked by Bonux
**/
public class ClanSearchClan
{
	private int _clanId;
	private int _application;
	private int _subUnit;
	private ClanSearchListType _searchType;
	private String _desc;

	public ClanSearchClan(int clanId, ClanSearchListType searchType, String desc, int application, int subUnit)
	{
		_clanId = clanId;
		_searchType = searchType;
		_desc = desc;
		_application = application;
		_subUnit = subUnit;
	}

	public int getClanId()
	{
		return _clanId;
	}

	public ClanSearchListType getSearchType()
	{
		return _searchType;
	}

	public void setSearchType(ClanSearchListType searchType)
	{
		_searchType = searchType;
	}

	public String getDesc()
	{
		return _desc;
	}

	public void setDesc(String desc)
	{
		_desc = desc;
	}

	public int getApplication()
	{
		return _application;
	}

	public void setApplication(int value)
	{
		_application = value;
	}

	public int getSubUnit()
	{
		return _subUnit;
	}

	public void setSubUnit(int value)
	{
		_subUnit = value;
	}
}