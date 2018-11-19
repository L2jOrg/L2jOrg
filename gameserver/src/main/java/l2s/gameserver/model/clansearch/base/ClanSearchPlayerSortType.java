package l2s.gameserver.model.clansearch.base;

/**
 * @author GodWorld
 * @reworked by Bonux
**/
public enum ClanSearchPlayerSortType
{
	/*0*/SORT_TYPE_NONE, 
	/*1*/SORT_TYPE_NAME, 
	/*2*/SORT_TYPE_SEARCH_TYPE, 
	/*3*/SORT_TYPE_ROLE, 
	/*4*/SORT_TYPE_LEVEL;

	public static final ClanSearchPlayerSortType[] VALUES = values();

	public static ClanSearchPlayerSortType valueOf(int value)
	{
		if(value < VALUES.length)
			return VALUES[value];
		return SORT_TYPE_NONE;
	}
}