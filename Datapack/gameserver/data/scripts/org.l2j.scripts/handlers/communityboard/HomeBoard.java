package handlers.communityboard;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.data.database.dao.ReportDAO;
import org.l2j.gameserver.data.database.data.ReportData;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.data.xml.impl.AdminData;
import org.l2j.gameserver.data.xml.impl.BuyListData;
import org.l2j.gameserver.data.xml.impl.MultisellData;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.handler.CommunityBoardHandler;
import org.l2j.gameserver.handler.IParseBoardHandler;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.network.serverpackets.BuyList;
import org.l2j.gameserver.network.serverpackets.ExBuySellList;
import org.l2j.gameserver.network.serverpackets.MagicSkillUse;
import org.l2j.gameserver.network.serverpackets.ShowBoard;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.gameserver.util.GameUtils.isSummon;

/**
 * Home board.
 * @author Zoey76, Mobius
 */
public final class HomeBoard implements IParseBoardHandler {
	// SQL Queries
	private static final String COUNT_FAVORITES = "SELECT COUNT(*) AS favorites FROM `bbs_favorites` WHERE `playerId`=?";
	private static final String NAVIGATION_PATH = "data/html/CommunityBoard/Custom/navigation.html";

	private static final String[] COMMANDS = {
			"_bbshome",
			"_bbstop",
			"_bbsreport"
	};

	private static final String[] CUSTOM_COMMANDS = {
			Config.COMMUNITYBOARD_ENABLE_MULTISELLS ? "_bbsexcmultisell" : null,
			Config.COMMUNITYBOARD_ENABLE_MULTISELLS ? "_bbsmultisell" : null,
			Config.COMMUNITYBOARD_ENABLE_MULTISELLS ? "_bbssell" : null,
			Config.COMMUNITYBOARD_ENABLE_TELEPORTS ? "_bbsteleport" : null,
			Config.COMMUNITYBOARD_ENABLE_BUFFS ? "_bbsbuff" : null,
			Config.COMMUNITYBOARD_ENABLE_HEAL ? "_bbsheal" : null
	};

	private static final BiPredicate<String, Player> COMBAT_CHECK = (command, activeChar) -> {
		boolean commandCheck = false;
		for (String c : CUSTOM_COMMANDS)
		{
			if ((c != null) && command.startsWith(c))
			{
				commandCheck = true;
				break;
			}
		}

		return commandCheck && (activeChar.isCastingNow() || activeChar.isInCombat() || activeChar.isInDuel() || activeChar.isInOlympiadMode() || activeChar.isInsideZone(ZoneType.SIEGE) || activeChar.isInsideZone(ZoneType.PVP));
	};

	private static final Predicate<Player> KARMA_CHECK = player -> Config.COMMUNITYBOARD_KARMA_DISABLED && (player.getReputation() < 0);

	@Override
	public String[] getCommunityBoardCommands()
	{
		List<String> commands = new ArrayList<>();
		commands.addAll(Arrays.asList(COMMANDS));
		commands.addAll(Arrays.asList(CUSTOM_COMMANDS));
		return commands.stream().filter(Objects::nonNull).toArray(String[]::new);
	}

	@Override
	public boolean parseCommunityBoardCommand(String command, Player activeChar)
	{
		// Old custom conditions check move to here
		if (COMBAT_CHECK.test(command, activeChar))
		{
			activeChar.sendMessage("You can't use the Community Board right now.");
			return false;
		}

		if (KARMA_CHECK.test(activeChar))
		{
			activeChar.sendMessage("Players with Karma cannot use the Community Board.");
			return false;
		}

		String returnHtml = null;
		final String navigation = HtmCache.getInstance().getHtm(activeChar, NAVIGATION_PATH);
		if (command.equals("_bbshome") || command.equals("_bbstop"))
		{
			final String customPath = Config.CUSTOM_CB_ENABLED ? "Custom/" : "";
			CommunityBoardHandler.getInstance().addBypass(activeChar, "Home", command);

			returnHtml = HtmCache.getInstance().getHtm(activeChar, "data/html/CommunityBoard/" + customPath + "home.html");
			if (!Config.CUSTOM_CB_ENABLED)
			{
				returnHtml = returnHtml.replaceAll("%fav_count%", Integer.toString(getFavoriteCount(activeChar)));
				returnHtml = returnHtml.replaceAll("%region_count%", Integer.toString(getRegionCount(activeChar)));
				returnHtml = returnHtml.replaceAll("%clan_count%", Integer.toString(ClanTable.getInstance().getClanCount()));
			}
			if (Config.CUSTOM_CB_ENABLED)
			{
				returnHtml = returnHtml.replaceAll("%name%", activeChar.getName());
				returnHtml = returnHtml.replaceAll("%premium%", "Could not find acount setup");
				returnHtml = returnHtml.replaceAll("%clan%", (activeChar.getClan() != null) ? activeChar.getClan().getName() : "No clan");
				returnHtml = returnHtml.replaceAll("%alliance%", "Could not find it");
				returnHtml = returnHtml.replaceAll("%country%", "Could not found it");
				returnHtml = returnHtml.replaceAll("%class%", activeChar.getBaseTemplate().getClassId().name().replace("_", " "));
				returnHtml = returnHtml.replaceAll("%exp%", String.valueOf(activeChar.getExp()).toString());
				returnHtml = returnHtml.replaceAll("%adena%", String.valueOf(activeChar.getAdena()).toString());
				returnHtml = returnHtml.replaceAll("%online%", String.valueOf(activeChar.getUptime()).toString());
				returnHtml = returnHtml.replaceAll("%onlinePlayers%", String.valueOf(World.getInstance().getPlayers().size()));
			}
		}
		else if (command.startsWith("_bbstop;"))
		{
			final String customPath = Config.CUSTOM_CB_ENABLED ? "Custom/" : "";
			final String path = command.replace("_bbstop;", "");
			if ((path.length() > 0) && path.endsWith(".html"))
			{
				returnHtml = HtmCache.getInstance().getHtm(activeChar, "data/html/CommunityBoard/" + customPath + path);
			}
			if (Config.CUSTOM_CB_ENABLED && (returnHtml != null))
			{
				returnHtml = returnHtml.replaceAll("%name%", activeChar.getName());
				returnHtml = returnHtml.replaceAll("%premium%", "Could not find acount setup");
				returnHtml = returnHtml.replaceAll("%clan%", (activeChar.getClan() != null) ? activeChar.getClan().getName() : "No clan");
				returnHtml = returnHtml.replaceAll("%alliance%", "Could not find it");
				returnHtml = returnHtml.replaceAll("%country%", "Could not found it");
				returnHtml = returnHtml.replaceAll("%class%", activeChar.getBaseTemplate().getClassId().name().replace("_", " "));
				returnHtml = returnHtml.replaceAll("%exp%", String.valueOf(activeChar.getExp()).toString());
				returnHtml = returnHtml.replaceAll("%adena%", String.valueOf(activeChar.getAdena()).toString());
				returnHtml = returnHtml.replaceAll("%online%", String.valueOf(activeChar.getUptime()).toString());
				returnHtml = returnHtml.replaceAll("%onlinePlayers%", String.valueOf(World.getInstance().getPlayers().size()));
			}
		}
		else if (command.startsWith("_bbsmultisell"))
		{
			final String fullBypass = command.replace("_bbsmultisell;", "");
			final String[] buypassOptions = fullBypass.split(",");
			final int multisellId = Integer.parseInt(buypassOptions[0]);
			final String page = buypassOptions[1];
			returnHtml = HtmCache.getInstance().getHtm(activeChar, "data/html/CommunityBoard/Custom/" + page + ".html");
			MultisellData.getInstance().separateAndSend(multisellId, activeChar, null, false);
		}
		else if (command.startsWith("_bbsexcmultisell"))
		{
			final String fullBypass = command.replace("_bbsexcmultisell;", "");
			final String[] buypassOptions = fullBypass.split(",");
			final int multisellId = Integer.parseInt(buypassOptions[0]);
			final String page = buypassOptions[1];
			returnHtml = HtmCache.getInstance().getHtm(activeChar, "data/html/CommunityBoard/Custom/" + page + ".html");
			MultisellData.getInstance().separateAndSend(multisellId, activeChar, null, true);
		}
		else if (command.startsWith("_bbssell"))
		{
			final String page = command.replace("_bbssell;", "");
			returnHtml = HtmCache.getInstance().getHtm(activeChar, "data/html/CommunityBoard/Custom/" + page + ".html");
			activeChar.sendPacket(new BuyList(BuyListData.getInstance().getBuyList(423), activeChar, 0));
			activeChar.sendPacket(new ExBuySellList(activeChar, false));
		}
		else if (command.startsWith("_bbsteleport"))
		{
			final String teleBuypass = command.replace("_bbsteleport;", "");
			if (activeChar.getInventory().getInventoryItemCount(Config.COMMUNITYBOARD_CURRENCY, -1) < Config.COMMUNITYBOARD_TELEPORT_PRICE)
			{
				activeChar.sendMessage("Not enough currency!");
			}
			else if (Config.COMMUNITY_AVAILABLE_TELEPORTS.get(teleBuypass) != null)
			{
				activeChar.disableAllSkills();
				activeChar.sendPacket(new ShowBoard());
				activeChar.destroyItemByItemId("CB_Teleport", Config.COMMUNITYBOARD_CURRENCY, Config.COMMUNITYBOARD_TELEPORT_PRICE, activeChar, true);
				activeChar.setInstanceById(0);
				activeChar.teleToLocation(Config.COMMUNITY_AVAILABLE_TELEPORTS.get(teleBuypass), 0);
				ThreadPool.schedule(activeChar::enableAllSkills, 3000);
			}
		}
		else if (command.startsWith("_bbsbuff"))
		{
			final String fullBypass = command.replace("_bbsbuff;", "");
			final String[] buypassOptions = fullBypass.split(";");
			final int buffCount = buypassOptions.length - 1;
			final String page = buypassOptions[buffCount];
			if (activeChar.getInventory().getInventoryItemCount(Config.COMMUNITYBOARD_CURRENCY, -1) < (Config.COMMUNITYBOARD_BUFF_PRICE * buffCount))
			{
				activeChar.sendMessage("Not enough currency!");
			}
			else
			{
				activeChar.destroyItemByItemId("CB_Buff", Config.COMMUNITYBOARD_CURRENCY, Config.COMMUNITYBOARD_BUFF_PRICE * buffCount, activeChar, true);
				final Pet pet = activeChar.getPet();
				List<Creature> targets = new ArrayList<>(4);
				targets.add(activeChar);
				if (pet != null)
				{
					targets.add(pet);
				}

				targets.addAll(activeChar.getServitors().values());

				for (int i = 0; i < buffCount; i++)
				{
					final Skill skill = SkillEngine.getInstance().getSkill(Integer.parseInt(buypassOptions[i].split(",")[0]), Integer.parseInt(buypassOptions[i].split(",")[1]));
					if (!Config.COMMUNITY_AVAILABLE_BUFFS.contains(skill.getId()))
					{
						continue;
					}
					targets.stream().filter(target -> !isSummon(target) || !skill.isSharedWithSummon()).forEach(target ->
					{
						skill.applyEffects(activeChar, target);
						if (Config.COMMUNITYBOARD_CAST_ANIMATIONS)
						{
							activeChar.sendPacket(new MagicSkillUse(activeChar, target, skill.getId(), skill.getLevel(), skill.getHitTime(), skill.getReuseDelay()));
							// not recommend broadcast
							// activeChar.broadcastPacket(new MagicSkillUse(activeChar, target, skill.getId(), skill.getLevel(), skill.getHitTime(), skill.getReuseDelay()));
						}
					});
				}
			}

			returnHtml = HtmCache.getInstance().getHtm(activeChar, "data/html/CommunityBoard/Custom/" + page + ".html");
		}
		else if (command.startsWith("_bbsheal"))
		{
			final String page = command.replace("_bbsheal;", "");
			if (activeChar.getInventory().getInventoryItemCount(Config.COMMUNITYBOARD_CURRENCY, -1) < (Config.COMMUNITYBOARD_HEAL_PRICE))
			{
				activeChar.sendMessage("Not enough currency!");
			}
			else
			{
				activeChar.destroyItemByItemId("CB_Heal", Config.COMMUNITYBOARD_CURRENCY, Config.COMMUNITYBOARD_HEAL_PRICE, activeChar, true);
				activeChar.setCurrentHp(activeChar.getMaxHp());
				activeChar.setCurrentMp(activeChar.getMaxMp());
				activeChar.setCurrentCp(activeChar.getMaxCp());
				if (activeChar.hasPet())
				{
					activeChar.getPet().setCurrentHp(activeChar.getPet().getMaxHp());
					activeChar.getPet().setCurrentMp(activeChar.getPet().getMaxMp());
					activeChar.getPet().setCurrentCp(activeChar.getPet().getMaxCp());
				}
				for (Summon summon : activeChar.getServitors().values())
				{
					summon.setCurrentHp(summon.getMaxHp());
					summon.setCurrentMp(summon.getMaxMp());
					summon.setCurrentCp(summon.getMaxCp());
				}
				activeChar.sendMessage("You used heal!");
			}

			returnHtml = HtmCache.getInstance().getHtm(activeChar, "data/html/CommunityBoard/Custom/" + page + ".html");
		} else if(command.startsWith("_bbsreport")) {
			var reportText =  command.replace("_bbsreport", "");

			var report = new ReportData();
			report.setPlayerId(activeChar.getObjectId());
			report.setReport(reportText);
			report.setPending(true);
			getDAO(ReportDAO.class).save(report);

			activeChar.sendMessage("Thank you For your Report!! the GM will be informed!");
			AdminData.getInstance().broadcastMessageToGMs(String.format("Player: %s (%s) has just submitted a report!", activeChar.getName(), activeChar.getObjectId()));
		}

		if (returnHtml != null)
		{
			if (Config.CUSTOM_CB_ENABLED)
			{
				returnHtml = returnHtml.replace("%navigation%", navigation);
				returnHtml = returnHtml.replaceAll("%name%", activeChar.getName());
				returnHtml = returnHtml.replaceAll("%premium%", "Could not find acount setup");
				returnHtml = returnHtml.replaceAll("%clan%", (activeChar.getClan() != null) ? activeChar.getClan().getName() : "No clan");
				returnHtml = returnHtml.replaceAll("%alliance%", "Could not find it");
				returnHtml = returnHtml.replaceAll("%country%", "Could not found it");
				returnHtml = returnHtml.replaceAll("%class%", activeChar.getBaseTemplate().getClassId().name().replace("_", " "));
				returnHtml = returnHtml.replaceAll("%exp%", String.valueOf(activeChar.getExp()).toString());
				returnHtml = returnHtml.replaceAll("%adena%", String.valueOf(activeChar.getAdena()).toString());
				returnHtml = returnHtml.replaceAll("%online%", String.valueOf(activeChar.getUptime()).toString());
				returnHtml = returnHtml.replaceAll("%onlinePlayers%", String.valueOf(World.getInstance().getPlayers().size()));
			}
			CommunityBoardHandler.separateAndSend(returnHtml, activeChar);
		}
		return false;
	}

	/**
	 * Gets the Favorite links for the given player.
	 * @param player the player
	 * @return the favorite links count
	 */
	private static int getFavoriteCount(Player player)
	{
		int count = 0;
		try (Connection con = DatabaseFactory.getInstance().getConnection();
			 PreparedStatement ps = con.prepareStatement(COUNT_FAVORITES))
		{
			ps.setInt(1, player.getObjectId());
			try (ResultSet rs = ps.executeQuery())
			{
				if (rs.next())
				{
					count = rs.getInt("favorites");
				}
			}
		}
		catch (Exception e)
		{
			LOG.warn(FavoriteBoard.class.getSimpleName() + ": Coudn't load favorites count for player " + player.getName());
		}
		return count;
	}

	/**
	 * Gets the registered regions count for the given player.
	 * @param player the player
	 * @return the registered regions count
	 */
	private static int getRegionCount(Player player)
	{
		return 0; // TODO: Implement.
	}
}
