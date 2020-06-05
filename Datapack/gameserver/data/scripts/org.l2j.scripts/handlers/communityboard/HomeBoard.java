package handlers.communityboard;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.data.database.dao.CommunityDAO;
import org.l2j.gameserver.data.database.dao.ReportDAO;
import org.l2j.gameserver.data.database.data.ReportData;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.data.xml.impl.AdminData;
import org.l2j.gameserver.data.xml.impl.BuyListData;
import org.l2j.gameserver.data.xml.impl.MultisellData;
import org.l2j.gameserver.datatables.SchemeBufferTable;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.handler.CommunityBoardHandler;
import org.l2j.gameserver.handler.IParseBoardHandler;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.BuyList;
import org.l2j.gameserver.network.serverpackets.ExBuySellList;
import org.l2j.gameserver.network.serverpackets.MagicSkillUse;
import org.l2j.gameserver.network.serverpackets.ShowBoard;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.world.zone.ZoneType;

import java.text.NumberFormat;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import static java.util.Objects.nonNull;
import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.gameserver.util.GameUtils.isSummon;

/**
 * Home board.
 *
 * @author Zoey76, Mobius
 * @author JoeAlisson
 */
public final class HomeBoard implements IParseBoardHandler {

    private static final String NAVIGATION_PATH = "data/html/CommunityBoard/Custom/new/navigation.html";
    private static final int PAGE_LIMIT = 6;

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
            Config.COMMUNITYBOARD_ENABLE_BUFFS ? "_bbscreatescheme" : null,
            Config.COMMUNITYBOARD_ENABLE_BUFFS ? "_bbseditscheme" : null,
            Config.COMMUNITYBOARD_ENABLE_BUFFS ? "_bbsdeletescheme" : null,
            Config.COMMUNITYBOARD_ENABLE_BUFFS ? "_bbsskillselect" : null,
            Config.COMMUNITYBOARD_ENABLE_BUFFS ? "_bbsskillunselect" : null,
            Config.COMMUNITYBOARD_ENABLE_BUFFS ? "_bbsgivebuffs" : null,
            Config.COMMUNITYBOARD_ENABLE_HEAL ? "_bbsheal" : null,
            Config.COMMUNITYBOARD_ENABLE_PREMIUM ? "_bbspremium" : null
    };

    private static final BiPredicate<String, Player> COMBAT_CHECK = (command, activeChar) -> {
        boolean commandCheck = false;
        for (String c : CUSTOM_COMMANDS) {
            if ((c != null) && command.startsWith(c)) {
                commandCheck = true;
                break;
            }
        }

        return commandCheck && (activeChar.isCastingNow() || activeChar.isInCombat() || activeChar.isInDuel() || activeChar.isInOlympiadMode() || activeChar.isInsideZone(ZoneType.SIEGE) || activeChar.isInsideZone(ZoneType.PVP));
    };

    private static final Predicate<Player> KARMA_CHECK = player -> Config.COMMUNITYBOARD_KARMA_DISABLED && (player.getReputation() < 0);

    /**
     * Gets the count Favorite links for the given player.
     *
     * @param player the player
     * @return the favorite links count
     */
    private static int getFavoriteCount(Player player) {
        return getDAO(CommunityDAO.class).getFavoritesCount(player.getObjectId());
    }

    /**
     * Gets the registered regions count for the given player.
     *
     * @param player the player
     * @return the registered regions count
     */
    private static int getRegionCount(Player player) {
        return 0; // TODO: Implement.
    }

    @Override
    public String[] getCommunityBoardCommands() {
        List<String> commands = new ArrayList<>();
        commands.addAll(Arrays.asList(COMMANDS));
        commands.addAll(Arrays.asList(CUSTOM_COMMANDS));
        return commands.stream().filter(Objects::nonNull).toArray(String[]::new);
    }

    private void showBuffsWindow(Player player)
    {
        final StringBuilder sb = new StringBuilder(200);

        final Map<String, ArrayList<Integer>> schemes = SchemeBufferTable.getInstance().getPlayerSchemes(player.getObjectId());
        if ((schemes == null) || schemes.isEmpty())
        {
            sb.append("<font color=\"LEVEL\">You haven't defined any scheme.</font>");
        }
        else
        {
            for (Map.Entry<String, ArrayList<Integer>> scheme : schemes.entrySet())
            {
                final int cost = Config.BUFFER_STATIC_BUFF_COST;
                sb.append("<font color=\"LEVEL\">" + scheme.getKey() + " [" + scheme.getValue().size() + " skill(s)]" + ((cost > 0) ? " - cost: " + NumberFormat.getInstance(Locale.ENGLISH).format(cost) : "") + "</font><br1>");
                sb.append("<a action=\"bypass -h npc_%objectId%_givebuffs;" + scheme.getKey() + ";" + cost + "\">Use on Me</a>&nbsp;|&nbsp;");
                sb.append("<a action=\"bypass -h npc_%objectId%_givebuffs;" + scheme.getKey() + ";" + cost + ";pet\">Use on Pet</a>&nbsp;|&nbsp;");
                sb.append("<a action=\"bypass -h npc_%objectId%_editschemes;Buffs;" + scheme.getKey() + ";1\">Edit</a>&nbsp;|&nbsp;");
                sb.append("<a action=\"bypass -h npc_%objectId%_deletescheme;" + scheme.getKey() + "\">Delete</a><br>");
            }
        }
        String returnHtml = null;
        returnHtml = HtmCache.getInstance().getHtm(player, "data/html/CommunityBoard/Custom/new/services-buffer.html");
        returnHtml = returnHtml.replace("%schemes%", sb.toString());
        returnHtml.replace("%max_schemes%", Integer.toString(Config.BUFFER_MAX_SCHEMES));
        CommunityBoardHandler.separateAndSend(returnHtml, player);

    }

    @Override
    public boolean parseCommunityBoardCommand(String command, StringTokenizer tokens, Player activeChar) {
        // Old custom conditions check move to here
        if (COMBAT_CHECK.test(command, activeChar)) {
            activeChar.sendMessage("You can't use the Community Board right now.");
            return false;
        }

        if (KARMA_CHECK.test(activeChar)) {
            activeChar.sendMessage("Players with Karma cannot use the Community Board.");
            return false;
        }

        String returnHtml = null;
        final String navigation = HtmCache.getInstance().getHtm(activeChar, NAVIGATION_PATH);
        if (command.equals("_bbshome") || command.equals("_bbstop")) {
            final String customPath = Config.CUSTOM_CB_ENABLED ? "Custom/" : "";
            CommunityBoardHandler.getInstance().addBypass(activeChar, "Home", command);

            returnHtml = HtmCache.getInstance().getHtm(activeChar, "data/html/CommunityBoard/" + customPath + "home.html");
            if (!Config.CUSTOM_CB_ENABLED) {
                returnHtml = returnHtml.replaceAll("%fav_count%", Integer.toString(getFavoriteCount(activeChar)));
                returnHtml = returnHtml.replaceAll("%region_count%", Integer.toString(getRegionCount(activeChar)));
                returnHtml = returnHtml.replaceAll("%clan_count%", Integer.toString(ClanTable.getInstance().getClanCount()));
            }
            if (Config.CUSTOM_CB_ENABLED) {
                returnHtml = returnHtml.replaceAll("%name%", activeChar.getName());
                returnHtml = returnHtml.replaceAll("%premium%", "Could not find acount setup");
                returnHtml = returnHtml.replaceAll("%clan%", (activeChar.getClan() != null) ? activeChar.getClan().getName() : "No clan");
                returnHtml = returnHtml.replaceAll("%alliance%", "Could not find it");
                returnHtml = returnHtml.replaceAll("%country%", "Could not found it");
                returnHtml = returnHtml.replaceAll("%class%", activeChar.getBaseTemplate().getClassId().name().replace("_", " "));
                returnHtml = returnHtml.replaceAll("%exp%", String.valueOf(activeChar.getExp()));
                returnHtml = returnHtml.replaceAll("%adena%", String.valueOf(activeChar.getAdena()));
                returnHtml = returnHtml.replaceAll("%online%", String.valueOf(activeChar.getUptime()));
                returnHtml = returnHtml.replaceAll("%onlinePlayers%", String.valueOf(World.getInstance().getPlayers().size()));
            }
        } else if (command.startsWith("_bbstop")) {
            final String customPath = Config.CUSTOM_CB_ENABLED ? "Custom/" : "";
            final String path = command.replace("_bbstop ", "");
            if ((path.length() > 0) && path.endsWith(".html")) {
                returnHtml = HtmCache.getInstance().getHtm(activeChar, "data/html/CommunityBoard/" + customPath + path);
            }
            if (Config.CUSTOM_CB_ENABLED && (returnHtml != null)) {
                returnHtml = returnHtml.replaceAll("%name%", activeChar.getName());
                returnHtml = returnHtml.replaceAll("%premium%", "Could not find acount setup");
                returnHtml = returnHtml.replaceAll("%clan%", (activeChar.getClan() != null) ? activeChar.getClan().getName() : "No clan");
                returnHtml = returnHtml.replaceAll("%alliance%", "Could not find it");
                returnHtml = returnHtml.replaceAll("%country%", "Could not found it");
                returnHtml = returnHtml.replaceAll("%class%", activeChar.getBaseTemplate().getClassId().name().replace("_", " "));
                returnHtml = returnHtml.replaceAll("%exp%", String.valueOf(activeChar.getExp()));
                returnHtml = returnHtml.replaceAll("%adena%", String.valueOf(activeChar.getAdena()));
                returnHtml = returnHtml.replaceAll("%online%", String.valueOf(activeChar.getUptime()));
                returnHtml = returnHtml.replaceAll("%onlinePlayers%", String.valueOf(World.getInstance().getPlayers().size()));
            }
        } else if (command.startsWith("_bbsmultisell")) {
            final String fullBypass = command.replace("_bbsmultisell ", "");
            final String[] buypassOptions = fullBypass.split(",");
            final int multisellId = Integer.parseInt(buypassOptions[0]);
            final String page = buypassOptions[1];
            returnHtml = HtmCache.getInstance().getHtm(activeChar, "data/html/CommunityBoard/Custom/" + page + ".html");
            MultisellData.getInstance().separateAndSend(multisellId, activeChar, null, false);
        } else if (command.startsWith("_bbsexcmultisell")) {
            final String fullBypass = command.replace("_bbsexcmultisell ", "");
            final String[] buypassOptions = fullBypass.split(",");
            final int multisellId = Integer.parseInt(buypassOptions[0]);
            final String page = buypassOptions[1];
            returnHtml = HtmCache.getInstance().getHtm(activeChar, "data/html/CommunityBoard/Custom/" + page + ".html");
            MultisellData.getInstance().separateAndSend(multisellId, activeChar, null, true);
        } else if (command.startsWith("_bbssell")) {
            final String page = command.replace("_bbssell ", "");
            returnHtml = HtmCache.getInstance().getHtm(activeChar, "data/html/CommunityBoard/Custom/" + page + ".html");
            activeChar.sendPacket(new BuyList(BuyListData.getInstance().getBuyList(423), activeChar, 0));
            activeChar.sendPacket(new ExBuySellList(activeChar, false));
        } else if (command.startsWith("_bbsteleport")) {
            final String teleBuypass = command.replace("_bbsteleport ", "");
            if (activeChar.getInventory().getInventoryItemCount(Config.COMMUNITYBOARD_CURRENCY, -1) < Config.COMMUNITYBOARD_TELEPORT_PRICE) {
                activeChar.sendMessage("Not enough currency!");
            } else if (Config.COMMUNITY_AVAILABLE_TELEPORTS.get(teleBuypass) != null) {
                activeChar.disableAllSkills();
                activeChar.sendPacket(new ShowBoard());
                activeChar.destroyItemByItemId("CB_Teleport", Config.COMMUNITYBOARD_CURRENCY, Config.COMMUNITYBOARD_TELEPORT_PRICE, activeChar, true);
                activeChar.setInstanceById(0);
                activeChar.teleToLocation(Config.COMMUNITY_AVAILABLE_TELEPORTS.get(teleBuypass), 0);
                ThreadPool.schedule(activeChar::enableAllSkills, 3000);
            }
        } else if (command.startsWith("_bbsbuff")) {
            final String fullBypass = command.replace("_bbsbuff ", "");
            final String[] buypassOptions = fullBypass.split(";");
            final int buffCount = buypassOptions.length - 1;
            final String page = buypassOptions[buffCount];
            if (activeChar.getInventory().getInventoryItemCount(Config.COMMUNITYBOARD_CURRENCY, -1) < (Config.COMMUNITYBOARD_BUFF_PRICE * buffCount)) {
                activeChar.sendMessage("Not enough currency!");
            } else {
                activeChar.destroyItemByItemId("CB_Buff", Config.COMMUNITYBOARD_CURRENCY, Config.COMMUNITYBOARD_BUFF_PRICE * buffCount, activeChar, true);
                final Pet pet = activeChar.getPet();
                List<Creature> targets = new ArrayList<>(4);
                targets.add(activeChar);
                if (pet != null) {
                    targets.add(pet);
                }

                targets.addAll(activeChar.getServitors().values());

                for (int i = 0; i < buffCount; i++) {
                    final Skill skill = SkillEngine.getInstance().getSkill(Integer.parseInt(buypassOptions[i].split(",")[0]), Integer.parseInt(buypassOptions[i].split(",")[1]));
                    if (!Config.COMMUNITY_AVAILABLE_BUFFS.contains(skill.getId())) {
                        continue;
                    }
                    targets.stream().filter(target -> !isSummon(target) || !skill.isSharedWithSummon()).forEach(target ->
                    {
                        skill.applyEffects(activeChar, target);
                        if (Config.COMMUNITYBOARD_CAST_ANIMATIONS) {
                            activeChar.sendPacket(new MagicSkillUse(activeChar, target, skill.getId(), skill.getLevel(), skill.getHitTime(), skill.getReuseDelay()));
                            // not recommend broadcast
                            // activeChar.broadcastPacket(new MagicSkillUse(activeChar, target, skill.getId(), skill.getLevel(), skill.getHitTime(), skill.getReuseDelay()));
                        }
                    });
                }
            }
            returnHtml = HtmCache.getInstance().getHtm(activeChar, "data/html/CommunityBoard/Custom/" + page + ".html");
        } else if (command.startsWith("_bbsheal")) {
            final String page = command.replace("_bbsheal ", "");
            if (activeChar.getInventory().getInventoryItemCount(Config.COMMUNITYBOARD_CURRENCY, -1) < (Config.COMMUNITYBOARD_HEAL_PRICE)) {
                activeChar.sendMessage("Not enough currency!");
            } else {
                activeChar.destroyItemByItemId("CB_Heal", Config.COMMUNITYBOARD_CURRENCY, Config.COMMUNITYBOARD_HEAL_PRICE, activeChar, true);
                activeChar.setCurrentHp(activeChar.getMaxHp());
                activeChar.setCurrentMp(activeChar.getMaxMp());
                activeChar.setCurrentCp(activeChar.getMaxCp());
                if (activeChar.hasPet()) {
                    activeChar.getPet().setCurrentHp(activeChar.getPet().getMaxHp());
                    activeChar.getPet().setCurrentMp(activeChar.getPet().getMaxMp());
                    activeChar.getPet().setCurrentCp(activeChar.getPet().getMaxCp());
                }
                for (Summon summon : activeChar.getServitors().values()) {
                    summon.setCurrentHp(summon.getMaxHp());
                    summon.setCurrentMp(summon.getMaxMp());
                    summon.setCurrentCp(summon.getMaxCp());
                }
                activeChar.sendMessage("You used heal!");
            }

            returnHtml = HtmCache.getInstance().getHtm(activeChar, "data/html/CommunityBoard/Custom/" + page + ".html");
        } else if (command.startsWith("_bbsreport")) {
            var reportText = command.replace("_bbsreport ", "");
            if (Util.isNotEmpty(reportText)) {

                var report = new ReportData();
                report.setPlayerId(activeChar.getObjectId());
                report.setReport(reportText);
                report.setPending(true);
                getDAO(ReportDAO.class).save(report);
                activeChar.sendMessage("Thank you For your Report!! the GM will be informed!");
                AdminData.getInstance().broadcastMessageToGMs(String.format("Player: %s (%s) has just submitted a report!", activeChar.getName(), activeChar.getObjectId()));
            }
        } else if (command.startsWith("_bbspremium")) {
                //_bbspremium;L2 amount;VIP amout ex: _bbspremium;100;200
                final String fullBypass = command.replace("_bbspremium ", "");
                final String[] buypassOptions = fullBypass.split(";");
                final long buypassL2Coins = Long.parseLong(buypassOptions[0]);
                final long buypassVIPPoints = Long.parseLong(buypassOptions[1]);

                if (activeChar.getVipTier() >= 5) {
                    activeChar.sendMessage("Max VIP already reached!");
                } else if (activeChar.getLCoins() <= buypassL2Coins) {
                    activeChar.sendMessage("Not enough currency!");
                } else {
                    activeChar.addLCoins(-buypassL2Coins);
                    activeChar.updateVipPoints(buypassVIPPoints);
                }
        } else if (command.startsWith("_bbscreatescheme"))
        {
            // Simple hack to use _bbscreatescheme bypass with a space.
            command = command.replace("_bbscreatescheme ", "");

            boolean canCreateScheme = true;

            try
            {
                // Check if more then 14 chars
                final String schemeName = command.trim();
                if (schemeName.length() > 14)
                {
                    activeChar.sendMessage("Scheme's name must contain up to 14 chars.");
                    canCreateScheme = false;
                }

                // Simple hack to use spaces, dots, commas, minus, plus, exclamations or question marks.
                if (!Util.isAlphaNumeric(schemeName.replace(" ", "").replace(".", "").replace(",", "").replace("-", "").replace("+", "").replace("!", "").replace("?", "")))
                {
                    activeChar.sendMessage("Please use plain alphanumeric characters.");
                    canCreateScheme = false;
                }

                final Map<String, ArrayList<Integer>> schemes = SchemeBufferTable.getInstance().getPlayerSchemes(activeChar.getObjectId());
                if (schemes != null)
                {
                    if (schemes.size() == Config.BUFFER_MAX_SCHEMES)
                    {
                        activeChar.sendMessage("Maximum schemes amount is already reached.");
                        canCreateScheme = false;
                    }
                    if (schemes.containsKey(schemeName))
                    {
                        activeChar.sendMessage("The scheme name already exists.");
                        canCreateScheme = false;
                    }
                }

                if (canCreateScheme)
                {
                    SchemeBufferTable.getInstance().setScheme(activeChar.getObjectId(), schemeName.trim(), new ArrayList<>());
                   // showBuffsWindow(activeChar);
                }

                returnHtml = showEditSchemeWindow(activeChar,"Buffs", schemeName, 1, returnHtml);
            }
            catch (Exception e)
            {
                activeChar.sendMessage(e.getMessage());
            }
        }
        else if (command.startsWith("_bbseditscheme"))
        {
            // Simple hack to use createscheme bypass with a space.
            // command = command.replace("_bbseditscheme ", "_bbseditscheme;");

            final StringTokenizer st = new StringTokenizer(command, " ");
            final String currentCommand = st.nextToken();

            final String groupType = st.nextToken();
            final String schemeName = st.nextToken();
            final int page = Integer.parseInt(st.nextToken());

            returnHtml = HtmCache.getInstance().getHtm(activeChar, "data/html/CommunityBoard/Custom/new/services-buffer-editscheme.html");

            final List<Integer> schemeSkills = SchemeBufferTable.getInstance().getScheme(activeChar.getObjectId(), schemeName);
            returnHtml = returnHtml.replace("%schemename%", schemeName);
            returnHtml = returnHtml.replace("%count%", getCountOf(schemeSkills, false) + " / " + activeChar.getBuffCount() + " buffs, " + getCountOf(schemeSkills, true) + " / " + Config.DANCES_MAX_AMOUNT + " dances/songs");
            returnHtml = returnHtml.replace("%typesframe%", getTypesFrame(groupType, schemeName));
            returnHtml = returnHtml.replace("%skilllistframe%", getGroupSkillList(activeChar, groupType, schemeName, page));
        }
        else if (command.startsWith("_bbsskill"))
        {
            final StringTokenizer st = new StringTokenizer(command, " ");
            final String currentCommand = st.nextToken();

            final String groupType = st.nextToken();
            final String schemeName = st.nextToken();
            final int skillId = Integer.parseInt(st.nextToken());
            final int page = Integer.parseInt(st.nextToken());
            final List<Integer> skills = SchemeBufferTable.getInstance().getScheme(activeChar.getObjectId(), schemeName);

            if (currentCommand.startsWith("_bbsskillselect") && !schemeName.equalsIgnoreCase("none"))
            {
                final Skill skill = SkillEngine.getInstance().getSkill(skillId, SkillEngine.getInstance().getMaxLevel(skillId));
                if (skill.isDance())
                {
                    if (getCountOf(skills, true) < Config.DANCES_MAX_AMOUNT)
                    {
                        skills.add(skillId);
                    }
                    else
                    {
                        activeChar.sendMessage("This scheme has reached the maximum amount of dances/songs.");
                    }
                }
                else
                {
                    if (getCountOf(skills, false) < activeChar.getBuffCount())
                    {
                        skills.add(skillId);
                    }
                    else
                    {
                        activeChar.sendMessage("This scheme has reached the maximum amount of buffs.");
                    }
                }
            }
            else if (currentCommand.startsWith("_bbsskillunselect"))
            {
                skills.remove(Integer.valueOf(skillId));
            }

            returnHtml = showEditSchemeWindow(activeChar, groupType, schemeName, page, returnHtml);
        }
        else if (command.startsWith("_bbsgivebuffs"))
        {
            final StringTokenizer st = new StringTokenizer(command, ";");
            final String currentCommand = st.nextToken();

            final String schemeName = st.nextToken();
            final long cost = Integer.parseInt(st.nextToken());
            Creature target = null;
            if (st.hasMoreTokens())
            {
                final String targetType = st.nextToken();
                if ((targetType != null) && targetType.equalsIgnoreCase("pet"))
                {
                    target = activeChar.getPet();
                }
                else if ((targetType != null) && targetType.equalsIgnoreCase("summon"))
                {
                    for (Summon summon : activeChar.getServitorsAndPets())
                    {
                        if (summon.isServitor())
                        {
                            target = summon;
                        }
                    }
                }
            }
            else
            {
                target = activeChar;
            }

            if (target == null)
            {
                activeChar.sendMessage("You don't have a pet.");
            }
            else if ((cost == 0) || activeChar.reduceAdena("Community Board Buffer", cost, target, true))
            {
                for (int skillId : SchemeBufferTable.getInstance().getScheme(activeChar.getObjectId(), schemeName))
                {
                    SkillEngine.getInstance().getSkill(skillId, SkillEngine.getInstance().getMaxLevel(skillId)).applyEffects(target, target);
                }
            }

            returnHtml = HtmCache.getInstance().getHtm(activeChar, "data/html/CommunityBoard/Custom/buffer/main.html");
        }
        else if (command.startsWith("_bbsdeletescheme"))
        {

            final StringTokenizer st = new StringTokenizer(command, ";");
            final String currentCommand = st.nextToken();

            final String schemeName = st.nextToken();
            final Map<String, ArrayList<Integer>> schemes = SchemeBufferTable.getInstance().getPlayerSchemes(activeChar.getObjectId());
            if ((schemes != null) && schemes.containsKey(schemeName))
            {
                schemes.remove(schemeName);
            }

            returnHtml = HtmCache.getInstance().getHtm(activeChar, "data/html/CommunityBoard/Custom/buffer/main.html");
        }

        if (nonNull(returnHtml)) {
            if (Config.CUSTOM_CB_ENABLED) {
                final StringBuilder sb = new StringBuilder(200);

                final Map<String, ArrayList<Integer>> schemes = SchemeBufferTable.getInstance().getPlayerSchemes(activeChar.getObjectId());
                if ((schemes == null) || schemes.isEmpty())
                {
                    // sb.append("<tr><td align=center><font color=\"LEVEL\">0/4 schemes defined.</font></td></tr>");
                }
                else
                {
                    for (Map.Entry<String, ArrayList<Integer>> scheme : schemes.entrySet())
                    {
                        final int cost = getFee(scheme.getValue());
                        sb.append("<tr><td align=center><button value=\"" + scheme.getKey() + "\" action=\"bypass _bbsgivebuffs " + scheme.getKey() + ";" + cost + "\" width=128 height=28 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
                        sb.append("<td align=center><button value=\"Pet\" action=\"bypass _bbsgivebuffs " + scheme.getKey() + ";" + cost + ";pet\" width=92 height=28 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
                        sb.append("<td align=center><button value=\"Summon\" action=\"bypass _bbsgivebuffs " + scheme.getKey() + ";" + cost + ";summon\" width=92 height=28 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
                        sb.append("<td align=center><button value=\"Edit\" action=\"bypass _bbseditscheme Buffs;" + scheme.getKey() + ";1\" width=64 height=28 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
                        sb.append("<td align=center><button value=\"X\" action=\"bypass _bbsdeletescheme " + scheme.getKey() + ";1\" width=32 height=28 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");
                    }

                }
                returnHtml = returnHtml.replace("%schemes%", sb.toString());
                returnHtml = returnHtml.replace("%max_schemes%", Integer.toString(Config.BUFFER_MAX_SCHEMES));
                returnHtml = returnHtml.replace("%navigation%", navigation);
                returnHtml = returnHtml.replaceAll("%name%", activeChar.getName());
                returnHtml = returnHtml.replaceAll("%premium%", "Could not find account setup");
                returnHtml = returnHtml.replaceAll("%clan%", (activeChar.getClan() != null) ? activeChar.getClan().getName() : "No clan");
                returnHtml = returnHtml.replaceAll("%alliance%", "Could not find it");
                returnHtml = returnHtml.replaceAll("%country%", "Could not found it");
                returnHtml = returnHtml.replaceAll("%class%", activeChar.getBaseTemplate().getClassId().name().replace("_", " "));
                returnHtml = returnHtml.replaceAll("%exp%", String.valueOf(activeChar.getExp()));
                returnHtml = returnHtml.replaceAll("%adena%", String.valueOf(activeChar.getAdena()));
                returnHtml = returnHtml.replaceAll("%online%", String.valueOf(activeChar.getUptime()));
                returnHtml = returnHtml.replaceAll("%onlinePlayers%", String.valueOf(World.getInstance().getPlayers().size()));
            }
            CommunityBoardHandler.separateAndSend(returnHtml, activeChar);
        }

        return false;
    }

    private String showEditSchemeWindow(Player player, String groupType, String schemeName, int page, String returnHtml)
    {
        returnHtml = HtmCache.getInstance().getHtm(player, "data/html/CommunityBoard/Custom/new/services-buffer-editscheme.html");

        final List<Integer> schemeSkills = SchemeBufferTable.getInstance().getScheme(player.getObjectId(), schemeName);
        returnHtml = returnHtml.replace("%schemename%", schemeName);
        returnHtml = returnHtml.replace("%count%", getCountOf(schemeSkills, false) + " / " + player.getBuffCount() + " buffs, " + getCountOf(schemeSkills, true) + " / " + Config.DANCES_MAX_AMOUNT + " dances/songs");
        returnHtml = returnHtml.replace("%typesframe%", getTypesFrame(groupType, schemeName));
        returnHtml = returnHtml.replace("%skilllistframe%", getGroupSkillList(player, groupType, schemeName, page));
        return returnHtml;
    }

    /**
     * @param player : The player to make checks on.
     * @param groupType : The group of skills to select.
     * @param schemeName : The scheme to make check.
     * @param page The page.
     * @return a String representing skills available to selection for a given groupType.
     */
    private String getGroupSkillList(Player player, String groupType, String schemeName, int page)
    {
        // Retrieve the entire skills list based on group type.
        List<Integer> skills = SchemeBufferTable.getInstance().getSkillsIdsByType(groupType);
        if (skills.isEmpty())
        {
            return "That group doesn't contain any skills.";
        }

        // Calculate page number.
        final int max = countPagesNumber(skills.size(), PAGE_LIMIT);
        if (page > max)
        {
            page = max;
        }

        // Cut skills list up to page number.
        // skills = skills.subList((page - 1) * PAGE_LIMIT, Math.min(page * PAGE_LIMIT, skills.size()));

        final List<Integer> schemeSkills = SchemeBufferTable.getInstance().getScheme(player.getObjectId(), schemeName);
        final StringBuilder sb = new StringBuilder(skills.size() * 150);
        int column = 0;
        int maxColumn = skills.size() <= 16 ? 4 : 5;
        sb.append("<table>");

        for (int skillId : skills)
        {
            // sb.append(((row % 2) == 0 ? "<table style=\"float: left\" bgcolor=\"000000\"><tr>" : "<table><tr>"));
            if (column == 0)
            {
                sb.append("<tr>");
            }

            final Skill skill = SkillEngine.getInstance().getSkill(skillId, 1);
            if (schemeSkills.contains(skillId))
            {
                sb.append("<td><img src=\"" + skill.getIcon() + "\" width=32 height=32></td><td><button value=\" \" action=\"bypass _bbsskillunselect;" + groupType + ";" + schemeName + ";" + skillId + ";" + page + "\" width=32 height=32 back=\"L2UI_CH3.mapbutton_zoomout2\" fore=\"L2UI_CH3.mapbutton_zoomout1\"></td>");
            }
            else
            {
                sb.append("<td><img src=\"" + skill.getIcon() + "\" width=32 height=32></td><td><button value=\" \" action=\"bypass _bbsskillselect " + groupType + " " + schemeName + " " + skillId + " " + page + "\" width=32 height=32 back=\"L2UI_CH3.mapbutton_zoomin2\" fore=\"L2UI_CH3.mapbutton_zoomin1\"></td>");
            }

            column++;

            if (column == maxColumn)
            {
                sb.append("</tr>");
                column = 0;
            }

        }

        if (!sb.toString().endsWith("</tr>"))
        {
            sb.append("</tr>");
        }

        sb.append("</table>");
        // Build page footer.
        /*
         * sb.append("<br><table><tr>"); if (page > 1) { sb.append("<td align=left width=70><a action=\"bypass _bbseditschemes;" + groupType + ";" + schemeName + ";" + (page - 1) + "\">Previous</a></td>"); } else { sb.append("<td align=left width=70>Previous</td>"); }
         * sb.append("<td align=center width=100>Page " + page + "</td>"); if (page < max) { sb.append("<td align=right width=70><a action=\"bypass _bbseditschemes;" + groupType + ";" + schemeName + ";" + (page + 1) + "\">Next</a></td>"); } else { sb.append("<td align=right width=70>Next</td>"); }
         * sb.append("</tr></table>");
         */

        return sb.toString();
    }

    /**
     * @param groupType : The group of skills to select.
     * @param schemeName : The scheme to make check.
     * @return a string representing all groupTypes available. The group currently on selection isn't linkable.
     */
    private static String getTypesFrame(String groupType, String schemeName)
    {
        final StringBuilder sb = new StringBuilder(500);
        sb.append("<table>");

        int count = 0;
        for (String type : SchemeBufferTable.getInstance().getSkillTypes())
        {
            if (count == 0)
            {
                sb.append("<tr>");
            }

            if (groupType.equalsIgnoreCase(type))
            {
                sb.append("<td width=65>" + type + "</td>");
            }
            else
            {
                sb.append("<td width=65><a action=\"bypass _bbseditscheme " + type + " " + schemeName + " 1\">" + type + "</a></td>");
            }

            count++;
            if (count == 4)
            {
                sb.append("</tr>");
                count = 0;
            }
        }

        if (!sb.toString().endsWith("</tr>"))
        {
            sb.append("</tr>");
        }

        sb.append("</table>");

        return sb.toString();
    }

    /**
     * @param list : A list of skill ids.
     * @return a global fee for all skills contained in list.
     */
    private static int getFee(List<Integer> list)
    {
        if (Config.BUFFER_STATIC_BUFF_COST > 0)
        {
            return list.size() * Config.BUFFER_STATIC_BUFF_COST;
        }

        int fee = 0;
        for (int sk : list)
        {
            fee += SchemeBufferTable.getInstance().getAvailableBuff(sk).getPrice();
        }

        return fee;
    }

    private static int countPagesNumber(int objectsSize, int pageSize)
    {
        return (objectsSize / pageSize) + ((objectsSize % pageSize) == 0 ? 0 : 1);
    }

    private static long getCountOf(List<Integer> skills, boolean dances)
    {
        return skills.stream().filter(sId -> SkillEngine.getInstance().getSkill(sId, 1).isDance() == dances).count();
    }
}
