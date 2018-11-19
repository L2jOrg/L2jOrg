package l2s.gameserver.instancemanager;

import java.sql.Connection;
import java.sql.PreparedStatement;

import l2s.commons.dbutils.DbUtils;
import l2s.commons.util.Rnd;
import l2s.gameserver.Config;
import l2s.gameserver.database.DatabaseFactory;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.TeamType;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.Zone.ZoneType;
import l2s.gameserver.utils.ItemFunctions;

/**
 * @author iqman
 * @reworked by Bonux
**/
public class PvPRewardManager
{
	private static final String PVP_REWARD_VAR = "@pvp_manager";
	
	private static final boolean no_msg = Config.DISALLOW_MSG_TO_PL;
	
	private static boolean basicCheck(Player killed, Player killer)
	{
		if(killed == null || killer == null)
			return false;

		if(killed.getLevel() < Config.PVP_REWARD_MIN_PL_LEVEL)
		{
			if(!no_msg)
			{
				if(killer.isLangRus())
					killer.sendMessage("Система PvP: Вы убили игрока но уровень противника не подходит. Минимальный уровень: " + Config.PVP_REWARD_MIN_PL_LEVEL);
				else
					killer.sendMessage("PvP System: You killed a player, but his level is too low. Suggested level: " + Config.PVP_REWARD_MIN_PL_LEVEL);
			}		
			return false;
		}

		if(killed.getClassLevel().ordinal() < Config.PVP_REWARD_MIN_PL_PROFF)
		{
			if(!no_msg)
			{
				if(killer.isLangRus())
					killer.sendMessage("Система PvP: Вы убили игрока но его профессия слишком низкая. Минимальный уровень профессии: " + (Config.PVP_REWARD_MIN_PL_PROFF - 1));
				else
					killer.sendMessage("PvP System: You killed a player, but his job level is too low. Suggested job level: " + (Config.PVP_REWARD_MIN_PL_PROFF - 1));
			}		
			return false;
		}

		if((System.currentTimeMillis() - killer.getLastAccess()) < (Config.PVP_REWARD_MIN_PL_UPTIME_MINUTE * 60000))
		{
			if(!no_msg)
			{
				if(killer.isLangRus())
					killer.sendMessage("Система PvP: Вы убили игрока но вы не достаточно провели время в игре, минимальное время в игре: " + Config.PVP_REWARD_MIN_PL_UPTIME_MINUTE + " мин.");
				else
					killer.sendMessage("PvP System: You killed a player, but you spent a little time ingame. Suggested minimal ingame time: " + Config.PVP_REWARD_MIN_PL_UPTIME_MINUTE + " min.");
			}			
			return false;
		}

		if((System.currentTimeMillis() - killed.getLastAccess()) < (Config.PVP_REWARD_MIN_PL_UPTIME_MINUTE * 60000))
		{
			if(!no_msg)
			{
				if(killer.isLangRus())
					killer.sendMessage("Система PvP: Вы убили игрока но он не достаточно время в игре, минимальное время в игре: " + Config.PVP_REWARD_MIN_PL_UPTIME_MINUTE + " мин.");
				else
					killer.sendMessage("PvP System: You killed a player, but he has spent a little time ingame. Suggested minimal ingame time: " + Config.PVP_REWARD_MIN_PL_UPTIME_MINUTE + " min.");
			}		
			return false;
		}

		if(!Config.PVP_REWARD_PK_GIVE && killer.isPK())
		{
			if(!no_msg)
			{
				if(killer.isLangRus())
					killer.sendMessage("Система PvP: Вы убили игрока но ПК убийства запрещены.");
				else
					killer.sendMessage("PvP System: You killed a player, but PK kills are disallowed");
			}			
			return false;
		}

		if(!Config.PVP_REWARD_ON_EVENT_GIVE && (killer.isInOlympiadMode() || killer.getTeam() != TeamType.NONE))
		{
			if(!no_msg)
			{
				if(killer.isLangRus())
					killer.sendMessage("Система PvP: Вы убили игрока но убийства на турнирах не засчитываться.");	
				else
					killer.sendMessage("PvP System: You killed a player, but event kills won't count");
			}		
			return false;
		}

		if(Config.PVP_REWARD_ONLY_BATTLE_ZONE && (!killer.isInZone(ZoneType.battle_zone) || !killed.isInZone(ZoneType.battle_zone)))
		{
			if(!no_msg)
			{
				if(killer.isLangRus())
					killer.sendMessage("Система PvP: Вы убили игрока но разрешено убивать игроков только на боевых площадках.");
				else
					killer.sendMessage("PvP System: You killed a player, it's allowed to kill players only on battle fields.");
			}			
			return false;
		}

		if(!Config.PVP_REWARD_SAME_PARTY_GIVE)
		{
			if(killer.getParty() != null && killer.getParty() == killed.getParty() && (killer.getParty().getCommandChannel() == null || killer.getParty().getCommandChannel() == killed.getParty().getCommandChannel()))
			{
				if(!no_msg)
				{
					if(killer.isLangRus())
						killer.sendMessage("Система PvP: Вы убили игрока но вы находитесь в одной партии, что запрещено.");	
					else
						killer.sendMessage("PvP System: You killed a player, but you both are in the same party, it's not allowed");
				}				
				return false;
			}
		}

		if(!Config.PVP_REWARD_SAME_CLAN_GIVE && killer.getClan() != null && killer.getClan() == killed.getClan())
		{
			if(!no_msg)
			{
				if(killer.isLangRus())
					killer.sendMessage("Система PvP: Вы убили игрока но вы находитесь в одной клане , что запрещено.");	
				else
					killer.sendMessage("PvP System: You killed a player, but you both are in the same clan, it's not allowed");
			}
			return false;
		}

		if(!Config.PVP_REWARD_SAME_ALLY_GIVE && killer.getAllyId() > 0 && killer.getAllyId() == killed.getAllyId())
		{
			if(!no_msg)
			{
				if(killer.isLangRus())
					killer.sendMessage("Система PvP: Вы убили игрока но вы находитесь в одном альянсе, что запрещено.");
				else
					killer.sendMessage("PvP System: You killed a player, but you both are in the same alliance, it's not allowed");
			}		
			return false;
		}

		if(killer.getNetConnection() != null && killer.getNetConnection().getHWID() != null && killed.getNetConnection() != null && killed.getNetConnection().getHWID() != null)
		{
			if(!Config.PVP_REWARD_SAME_HWID_GIVE && killer.getNetConnection().getHWID().equals(killed.getNetConnection().getHWID()))
			{
				if(!no_msg)
				{
					if(killer.isLangRus())
						killer.sendMessage("Система PvP: Вы убили игрока но система определила что это ваше другое окно, это запрещено!");	
					else
						killer.sendMessage("PvP System: You killed a player, but it seems you both playing from the same PC, it's not allowed");
				}		
				return false;
			}
		}

		if(!Config.PVP_REWARD_SAME_IP_GIVE && killer.getIP().equals(killed.getIP()))
		{
			if(!no_msg)
			{
				if(killer.isLangRus())
					killer.sendMessage("Система PvP: Вы убили игрока но ваши ИП совпадают, что запрещено.");
				else
					killer.sendMessage("PvP System: You killed a player, but your IP are the same, it's not allowed!");
			}
			return false;
		}

		if(Config.PVP_REWARD_SPECIAL_ANTI_TWINK_TIMER && (System.currentTimeMillis() - killed.getCreateTime()) < (Config.PVP_REWARD_HR_NEW_CHAR_BEFORE_GET_ITEM * 60000 * 60))
		{
			if(!no_msg)
			{
				if(killer.isLangRus())
					killer.sendMessage("Система PvP: Вы убили игрока но чар был создан недавно, что не разрешено системой. Чар должен быть создан не менее: "+Config.PVP_REWARD_HR_NEW_CHAR_BEFORE_GET_ITEM+" часов назад.");	
				else
					killer.sendMessage("PvP System: You killed a player, but char has been created really short time ago, suggested char creation, not less than "+Config.PVP_REWARD_HR_NEW_CHAR_BEFORE_GET_ITEM+" hours!");
			}		
			return false;
		}

		if(Config.PVP_REWARD_CHECK_EQUIP && !checkEquip(killed))
		{
			if(!no_msg)
			{
				if(killer.isLangRus())
					killer.sendMessage("Система PvP: Вы убили игрока но его экипировка слишком плоха.");	
				else
					killer.sendMessage("PvP System: You killed a player, but his equip is very low.");
			}			
			return false;	
		}
		return true;	
	}

	private static boolean checkEquip(Player killed)
	{
		if(killed.getWeaponsExpertisePenalty() > 0 || killed.getArmorsExpertisePenalty() > 0)
			return false;

		ItemInstance weapon = killed.getActiveWeaponInstance();
		if(weapon == null)
			return false;

		if(weapon.getGrade().extOrdinal() < Config.PVP_REWARD_WEAPON_GRADE_TO_CHECK)
			return false;

		return true;	
	}
	
	public static void tryGiveReward(Player victim, Player player)
	{
		if(!Config.ALLOW_PVP_REWARD)
			return;

		if(!isNoDelayActive(victim, player))
		{
            if(player.isLangRus())
			    player.sendMessage("Система PvP: Вы убили игрока этого игрока совсем недавно! Еще не прошло время до возможности повторного убийства.");
			else
			    player.sendMessage("PvP System: You killed the player that player recently! Yet as time passed up the possibility of re-killing.");
			return;
		}

		if(!basicCheck(victim,player))
			return;

		victim.setVar(PVP_REWARD_VAR + "_" + player.getObjectId(), true, (System.currentTimeMillis() + (Config.PVP_REWARD_DELAY_ONE_KILL * 1000)));

		giveItem(player);

		if(Config.PVP_REWARD_LOG_KILLS)
			logCombat(player, victim);

		if(Config.PVP_REWARD_SEND_SUCC_NOTIF)
		{
            if(victim.isLangRus())
                victim.sendMessage("Система PvP: Вас убили!");
            else
                victim.sendMessage("PvP System: You killed!");

            if(player.isLangRus())
                player.sendMessage("Система PvP: Вы убили игрока!");
            else
                player.sendMessage("PvP System: You killed player!");
        }
	}

    private static void logCombat(Player killer, Player victim)
	{
		String kill_name = killer.getName();
		String victim_name = victim.getName();
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("INSERT INTO pvp_system_log (killer,victim) values (?,?)");
			statement.setString(1, kill_name);
			statement.setString(2, victim_name);
			statement.execute();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}		
	}

	private static void giveItem(Player player)
	{
		if(player == null)
			return;

		if(Config.PVP_REWARD_REWARD_IDS.length != Config.PVP_REWARD_COUNTS.length)
			return;

		if(Config.PVP_REWARD_RANDOM_ONE)
		{
			int index = Rnd.get(Config.PVP_REWARD_REWARD_IDS.length);
			int rewardId = Config.PVP_REWARD_REWARD_IDS[index];
			long rewardCount = Config.PVP_REWARD_COUNTS[index];
			if(rewardId > 0 && rewardCount > 0)
				ItemFunctions.addItem(player, rewardId, rewardCount, true);
		}
		else
		{
			for(int i = 0 ; i < Config.PVP_REWARD_REWARD_IDS.length - 1 ; i++)
			{
				int rewardId = Config.PVP_REWARD_REWARD_IDS[i];
				long rewardCount = Config.PVP_REWARD_COUNTS[i];
				if(rewardId > 0 && rewardCount > 0)
					ItemFunctions.addItem(player, Config.PVP_REWARD_REWARD_IDS[i], Config.PVP_REWARD_COUNTS[i], true);
			}
		}
	}

	private static boolean isNoDelayActive(Player victim, Player killer)
	{
		String delay = victim.getVar(PVP_REWARD_VAR + "_" + killer.getObjectId());
		if(delay == null)
			return true;
		return false;	
	}	
}