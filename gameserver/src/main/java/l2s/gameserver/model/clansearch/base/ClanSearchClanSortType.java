package l2s.gameserver.model.clansearch.base;

/**
 * @author GodWorld
 * @reworked by Bonux
**/
public enum ClanSearchClanSortType
{
	/*0*/SORT_TYPE_NONE,
	/*1*/SORT_TYPE_CLAN_NAME,
	/*2*/SORT_TYPE_LEADER_NAME,
	/*3*/SORT_TYPE_MEMBER_COUNT,
	/*4*/SORT_TYPE_CLAN_LEVEL,
	/*5*/SORT_TYPE_SEARCH_LIST_TYPE;

	public static ClanSearchClanSortType[] VALUES = values();

	public static ClanSearchClanSortType valueOf(int value)
	{
		if(value < VALUES.length)
			return VALUES[value];
		return SORT_TYPE_NONE;
	}
}