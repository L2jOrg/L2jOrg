package l2s.gameserver.model.clansearch.base;

/**
 * @author GodWorld
 * @reworked by Bonux
**/
public enum ClanSearchListType
{
	/*0*/SLT_FRIEND_LIST,
	/*1*/SLT_PLEDGE_MEMBER_LIST,
	/*2*/SLT_ADDITIONAL_FRIEND_LIST,
	/*3*/SLT_ADDITIONAL_LIST,
	/*4*/SLT_ANY;

	public static final ClanSearchListType[] VALUES = values();

	public static ClanSearchListType getType(int value)
	{
		return (value == -1 || value >= VALUES.length) ? SLT_ANY : VALUES[value];
	}
}