package l2s.gameserver.model.clansearch.base;

/**
 * @author GodWorld
 * @reworked by Bonux
**/
public enum ClanSearchTargetType
{
	/*0*/TARGET_TYPE_LEADER_NAME, 
	/*1*/TARGET_TYPE_CLAN_NAME;

	public static ClanSearchTargetType valueOf(int value)
	{
		switch(value)
		{
			case 0:
				return TARGET_TYPE_LEADER_NAME;
		}
		return TARGET_TYPE_CLAN_NAME;
	}
}