package l2s.gameserver.instancemanager.clansearch;

/**
 * @author GodWorld
 * @reworked by Bonux
**/
public class ClanSearchQueries
{
	public static final String LOAD_CLANS = "SELECT `clan_id`, `search_type`, `desc`, `application`, `sub_unit` FROM `clan_search_registered_clans`";
	public static final String LOAD_WAITERS = "SELECT `char_id`, `char_name`, `char_level`, `char_class_id`, `search_type` FROM `clan_search_waiting_players`";
	public static final String LOAD_APPLICANTS = "SELECT `char_id`, `char_name`, `char_level`, `char_class_id`, `preffered_clan_id`, `search_type`, `desc` FROM `clan_search_clan_applicants`";
	public static final String ADD_CLAN = "INSERT INTO `clan_search_registered_clans`(`clan_id`, `search_type`, `desc`, `timestamp`, `application`, `sub_unit`) VALUES (?, ?, ?, UNIX_TIMESTAMP(), ?, ?) ON DUPLICATE KEY UPDATE `search_type` = ?, `desc` = ?, `application` = ?, `sub_unit` = ?";
	public static final String CLEAN_CLANS = "DELETE FROM `clan_search_registered_clans` WHERE (UNIX_TIMESTAMP() - `timestamp`) >= 60 * 60 * 24 * 30";
	public static final String CLEAN_APPLICANTS = "DELETE FROM `clan_search_clan_applicants` WHERE (UNIX_TIMESTAMP() - `timestamp`) >= 60 * 60 * 24 * 30";
	public static final String CLEAN_WAITERS = "DELETE FROM `clan_search_waiting_players` WHERE (UNIX_TIMESTAMP() - `timestamp`) >= 60 * 60 * 24 * 30";

	public static String getAddWaitingPlayerQuery(int waiterCount)
	{
		StringBuilder query = new StringBuilder("INSERT IGNORE INTO `clan_search_waiting_players` VALUES (?, ?, ?, ?, ?, UNIX_TIMESTAMP())");
		for(int i = 0; i < waiterCount - 1; i++)
		{
			query.append(", (?, ?, ?, ?, ?, UNIX_TIMESTAMP())");
		}
		return query.toString();
	}

	public static String getAddApplicantPlayerQuery(int applicantCount)
	{
		StringBuilder query = new StringBuilder("INSERT IGNORE INTO `clan_search_clan_applicants` VALUES (?, ?, ?, ?, ?, ?, ?, UNIX_TIMESTAMP())");
		for(int i = 0; i < applicantCount - 1; i++)
		{
			query.append(", (?, ?, ?, ?, ?, ?, ?, UNIX_TIMESTAMP())");
		}
		return query.toString();
	}

	public static String getRemoveClanQuery(int clanCount)
	{
		StringBuilder query = new StringBuilder("DELETE FROM `clan_search_registered_clans` WHERE `clan_id` IN (?");
		for(int i = 0; i < clanCount - 1; i++)
		{
			query.append(", ?");
		}
		query.append(")");
		return query.toString();
	}

	public static String getRemoveClanApplicants(int clanCount)
	{
		StringBuilder query = new StringBuilder("DELETE FROM `clan_search_clan_applicants` WHERE `preffered_clan_id` IN (?");
		for(int i = 0; i < clanCount - 1; i++)
		{
			query.append(", ?");
		}
		query.append(")");
		return query.toString();
	}

	public static String getRemoveWaiterQuery(int waiterCount)
	{
		StringBuilder query = new StringBuilder("DELETE FROM `clan_search_waiting_players` WHERE `char_id` IN (?");
		for(int i = 0; i < waiterCount - 1; i++)
		{
			query.append(", ?");
		}
		query.append(")");
		return query.toString();
	}

	public static String getRemoveApplicantQuery(int applicantCount)
	{
		StringBuilder query = new StringBuilder("DELETE FROM `clan_search_clan_applicants` WHERE `char_id` IN (?");
		for(int i = 0; i < applicantCount - 1; i++)
		{
			query.append(", ?");
		}
		query.append(")");
		return query.toString();
	}
}