package l2s.gameserver.model.clansearch.base;

/**
 * @author GodWorld
 * @reworked by Bonux
**/
public enum ClanSearchSortOrder
{
	/*0*/NONE, 
	/*1*/ASC, 
	/*2*/DESC;

	public static ClanSearchSortOrder valueOf(int value)
	{
		switch(value)
		{
			case 1:
				return ASC;
			case 2:
				return DESC;
		}
		return NONE;
	}
}