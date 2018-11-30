package handler.bbs.custom;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import handler.bbs.ScriptsCommunityHandler;
import org.l2j.commons.dbutils.DbUtils;
import org.l2j.commons.lang.ArrayUtils;
import org.l2j.gameserver.data.htm.HtmCache;
import org.l2j.gameserver.data.xml.holder.SkillHolder;
import org.l2j.gameserver.database.DatabaseFactory;
import org.l2j.gameserver.model.Playable;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Servitor;
import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.ShowBoardPacket;
import org.l2j.gameserver.skills.EffectType;
import org.l2j.gameserver.skills.EffectUseType;
import org.l2j.gameserver.utils.HtmlUtils;
import org.l2j.gameserver.utils.ItemFunctions;
import org.l2j.gameserver.utils.Strings;
import org.l2j.gameserver.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class CommunityBuffer extends ScriptsCommunityHandler {
    private static final Logger _log = LoggerFactory.getLogger(CommunityBuffer.class);

    private static final TIntObjectMap<Map<String, List<Skill>>> PLAYER_BUFF_SETS = new TIntObjectHashMap<Map<String, List<Skill>>>();
    private static final TIntObjectMap<Skill> AVAILABLE_BUFFS = new TIntObjectHashMap<Skill>();
    private static final List<Skill> ALL_BUFFS_SET = new ArrayList<Skill>();

    @Override
    public void onInit() {
        PLAYER_BUFF_SETS.clear();
        AVAILABLE_BUFFS.clear();
        ALL_BUFFS_SET.clear();

        cleanUP();

        for (int[] buff : BBSConfig.BUFF_SERVICE_AVAILABLE_SKILLS_FOR_BUFF) {
            int id = buff[0];
            Skill skill = SkillHolder.getInstance().getSkill(id, 1);
            if (skill == null) {
                _log.warn(getClass().getSimpleName() + ": Error while init buffs list. Cannot find skill ID[" + id + "], LEVEL[1]!");
                continue;
            }

            int lvl = buff.length >= 2 ? buff[1] : 0;
            if (lvl <= 0)
                lvl = skill.getMaxLevel();

            skill = SkillHolder.getInstance().getSkill(id, lvl);
            if (skill == null) {
                _log.warn(getClass().getSimpleName() + ": Error while init buffs list. Cannot find skill ID[" + id + "], LEVEL[" + lvl + "]!");
                continue;
            }

            AVAILABLE_BUFFS.put(id, skill);
            ALL_BUFFS_SET.add(skill);
        }

        //_log.info("CommunityBuffer: Loaded " + AVAILABLE_BUFFS.size() + " AVAILABLE_BUFFS count.]");
        super.onInit();
    }

    @Override
    public String[] getBypassCommands() {
        return new String[]{"_cbbsbuffer", "_bbsrestore", "_bbscancel"};
    }

    @Override
    protected void doBypassCommand(Player player, String bypass) {
        if (BBSConfig.BUFF_SERVICE_COST_ITEM_ID == 0 && !BBSConfig.BUFF_SERVICE_ALLOW_RESTORE && !BBSConfig.BUFF_SERVICE_ALLOW_CANCEL_BUFFS) {
            player.sendMessage(player.isLangRus() ? "Данный сервис отключен." : "This service disallowed.");
            player.sendPacket(ShowBoardPacket.CLOSE);
            return;
        }

        String html = HtmCache.getInstance().getHtml("scripts/handler/bbs/pages/buff.htm", player);
        String content = "";

        if (bypass.startsWith("_bbsrestore")) {
            if (!BBSConfig.BUFF_SERVICE_ALLOW_RESTORE) {
                player.sendMessage(player.isLangRus() ? "Данный сервис отключен." : "This service disallowed.");
                player.sendPacket(ShowBoardPacket.CLOSE);
                return;
            }

            if (!BBSConfig.GLOBAL_USE_FUNCTIONS_CONFIGS && (!checkUseCondition(player) || player.isInCombat())) {
                onWrongCondition(player);
                return;
            }

            player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
            player.setCurrentCp(player.getMaxCp());
            onBypassCommand(player, "_cbbsbuffer 0");
            return;
        }
        if (bypass.startsWith("_bbscancel")) {
            if (!BBSConfig.BUFF_SERVICE_ALLOW_CANCEL_BUFFS) {
                player.sendMessage(player.isLangRus() ? "Данный сервис отключен." : "This service disallowed.");
                player.sendPacket(ShowBoardPacket.CLOSE);
                return;
            }

            if (!BBSConfig.GLOBAL_USE_FUNCTIONS_CONFIGS && !checkUseCondition(player)) {
                onWrongCondition(player);
                return;
            }

            for (Abnormal eff : player.getAbnormalList()) {
                if (!eff.isOffensive() && (eff.getSkill().isMusic() || eff.getSkill().isSelfDispellable()) && !eff.getSkill().hasEffect(EffectUseType.NORMAL, EffectType.Transformation) && !player.isSpecialAbnormal(eff.getSkill()))
                    eff.exit();
            }

            onBypassCommand(player, "_cbbsbuffer 0");
            return;
        }

        if (bypass.startsWith("_cbbsbuffer")) {
            if (BBSConfig.BUFF_SERVICE_COST_ITEM_ID == 0) {
                player.sendMessage(player.isLangRus() ? "Данный сервис отключен." : "This service disallowed.");
                player.sendPacket(ShowBoardPacket.CLOSE);
                return;
            }

            if (player.isInOlympiadMode()) {
                player.sendMessage(player.isLangRus() ? "Эта функция недоступна на олимпиаде." : "This feature is not available at the Olympiad Game.");
                player.sendPacket(ShowBoardPacket.CLOSE);
                return;
            }

            if (player.isInCombat()) {
                player.sendMessage(player.isLangRus() ? "Эта функция недоступна во время боя." : "This feature is not available during the battle.");
                player.sendPacket(ShowBoardPacket.CLOSE);
                return;
            }

            StringTokenizer bf = new StringTokenizer(bypass, " ");
            bf.nextToken();
            String[] arg = new String[0];
            while (bf.hasMoreTokens())
                arg = ArrayUtils.add(arg, bf.nextToken());

            content = BuffList(arg, player);
        }

        if (content == null)
            return;

        html = html.replace("%content%", content);

        long price = 0L;
        if (player.getLevel() > BBSConfig.BUFF_SERVICE_MAX_LEVEL_FOR_FREE_BUFF) {
            if (BBSConfig.BUFF_SERVICE_COST_ITEM_COUNT > 0)
                price = BBSConfig.BUFF_SERVICE_COST_ITEM_COUNT;
        }
        html = html.replace("<?price?>", price > 0 ? Util.formatAdena(price) : "");
        html = html.replace("<?fee_item?>", price > 0 ? HtmlUtils.htmlItemName(BBSConfig.BUFF_SERVICE_COST_ITEM_ID) : (player.isLangRus() ? "Бесплатно" : "Free"));

        ShowBoardPacket.separateAndSend(html, player);
    }

    @Override
    protected void doWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5) {
        //
    }

    public static String htmlButton(String value, int width, int height, Object... args) {
        String action = "bypass _cbbsbuffer";
        for (Object arg : args)
            action += " " + arg.toString();
        return HtmlUtils.htmlButton(value, action, width, height);
    }

    //public static String htmlButton(String value, int width, Object... args)
    //{
    //	return htmlButton(value, width, 25, args);
    //}

    private static boolean takeItemsAndBuff(Playable player, List<Skill> buffs, boolean toPet) {
        long needCount = BBSConfig.BUFF_SERVICE_COST_ITEM_COUNT > 0 ? BBSConfig.BUFF_SERVICE_COST_ITEM_COUNT * buffs.size() : 0L;

        if (player.getLevel() > BBSConfig.BUFF_SERVICE_MAX_LEVEL_FOR_FREE_BUFF) {
            if (ItemFunctions.getItemCount(player, BBSConfig.BUFF_SERVICE_COST_ITEM_ID) < needCount)
                return false;
        }

        if (!toPet) {
            Playable target = player;
            if (target != null) {
                try {
                    if (player.getLevel() > BBSConfig.BUFF_SERVICE_MAX_LEVEL_FOR_FREE_BUFF)
                        ItemFunctions.deleteItem(player, BBSConfig.BUFF_SERVICE_COST_ITEM_ID, needCount);
                } catch (Exception e) {
                    return false;
                }
                for (Skill nextbuff : buffs) {
                    if (nextbuff == null)
                        continue;

                    if (nextbuff.isMusic())
                        //songs and dances
                        nextbuff.getEffects(target, target, BBSConfig.BUFF_SERVICE_ASSIGN_BUFF_TIME_MUSIC * 60 * 1000, BBSConfig.BUFF_SERVICE_BUFF_TIME_MODIFIER_MUSIC);
                        //for special skill that last less than 20min
                    else if (nextbuff.getId() == 1355 || nextbuff.getId() == 1356 || nextbuff.getId() == 1357 || nextbuff.getId() == 1363 || nextbuff.getId() == 1413 || nextbuff.getId() == 1414)
                        nextbuff.getEffects(target, target, BBSConfig.BUFF_SERVICE_ASSIGN_BUFF_TIME_SPECIAL * 60 * 1000, BBSConfig.BUFF_SERVICE_BUFF_TIME_MODIFIER_SPECIAL);
                        //normal buff
                    else
                        nextbuff.getEffects(target, target, BBSConfig.BUFF_SERVICE_ASSIGN_BUFF_TIME * 60 * 1000, BBSConfig.BUFF_SERVICE_BUFF_TIME_MODIFIER);
                    try {
                        Thread.sleep(10L);
                    } catch (Exception e) {
                    }
                }
            }
        } else {
            for (Servitor target2 : player.getServitors()) {
                try {
                    if (player.getLevel() > BBSConfig.BUFF_SERVICE_MAX_LEVEL_FOR_FREE_BUFF)
                        ItemFunctions.deleteItem(player, BBSConfig.BUFF_SERVICE_COST_ITEM_ID, needCount);
                } catch (Exception e) {
                    return false;
                }
                for (Skill nextbuff : buffs) {
                    if (nextbuff == null)
                        continue;

                    if (nextbuff.isMusic())
                        //songs and dances
                        nextbuff.getEffects(target2, target2, BBSConfig.BUFF_SERVICE_ASSIGN_BUFF_TIME_MUSIC * 60 * 1000, BBSConfig.BUFF_SERVICE_BUFF_TIME_MODIFIER_MUSIC);
                        //for special skill that last less than 20min
                    else if (nextbuff.getId() == 1355 || nextbuff.getId() == 1356 || nextbuff.getId() == 1357 || nextbuff.getId() == 1363 || nextbuff.getId() == 1413 || nextbuff.getId() == 1414)
                        nextbuff.getEffects(target2, target2, BBSConfig.BUFF_SERVICE_ASSIGN_BUFF_TIME_SPECIAL * 60 * 1000, BBSConfig.BUFF_SERVICE_BUFF_TIME_MODIFIER_SPECIAL);
                        //normal buff
                    else
                        nextbuff.getEffects(target2, target2, BBSConfig.BUFF_SERVICE_ASSIGN_BUFF_TIME * 60 * 1000, BBSConfig.BUFF_SERVICE_BUFF_TIME_MODIFIER);
                }
            }
        }
        return true;
    }

    private static int getSkillIdx(List<Skill> set, int skill_id) {
        for (int i = 0; i < set.size(); i++) {
            if (set.get(i).getId() == skill_id)
                return i;
        }
        return -1;
    }

    private String pageGet(Player player, String[] var) {
        if (!BBSConfig.GLOBAL_USE_FUNCTIONS_CONFIGS && !checkUseCondition(player)) {
            onWrongCondition(player);
            return null;
        }

        boolean buffallset = var[1].equalsIgnoreCase("0") || var[1].equalsIgnoreCase("2");
        String[] var2 = new String[var.length - (buffallset ? 1 : 2)];
        System.arraycopy(var, var.length - var2.length, var2, 0, var2.length);
        List<Skill> buffs_to_buff = new ArrayList<Skill>();

        if (buffallset) {
            String[] a = var[2].split("_");
            int listid = a[0].equalsIgnoreCase("2") ? player.getObjectId() : 0;
            String name = Strings.joinStrings(" ", var, 3);
            String localized_name = name;
            Map<String, List<Skill>> sets = getBuffSets(listid);
            if (listid == 0) {
                String[] langs = name.split(";");
                if (langs.length == 2)
                    localized_name = langs[player.isLangRus() ? 1 : 0];
            }
            if (!sets.containsKey(name)) {
                if (player.isLangRus())
                    return "<table><tr><td align=center><font color=FF3355>Набор '" + localized_name + "' не найден</font></td></tr></table>";
                else
                    return "<table><tr><td align=center><font color=FF3355>'" + localized_name + "' set not found</font></td></tr></table>";
            }
            buffs_to_buff.addAll(sets.get(name));
        } else
            buffs_to_buff.add(AVAILABLE_BUFFS.get(Integer.parseInt(var[2])));

        if (!takeItemsAndBuff(player, buffs_to_buff, var[1].equalsIgnoreCase("2")))
            player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);

        return pageList(player, var2);
    }

    private static final int pageRows = 9;
    private static final int pageCols = 3;
    private static final int pageMax = pageRows * pageCols;

    private static String pageList(Player player, String[] var) {
        String[] a = var[1].split("_");
        int pageIdx = Integer.parseInt(a[1]);
        boolean _all = a[0].equalsIgnoreCase("0");
        int listid = a[0].equalsIgnoreCase("2") ? player.getObjectId() : 0;
        String name = "Все баффы";
        if (!player.isLangRus())
            name = "All buffs";
        String param1 = Strings.joinStrings(" ", var, 1);
        List<Skill> set = ALL_BUFFS_SET;

        String localized_name = name;
        if (!_all) {
            Map<String, List<Skill>> sets = getBuffSets(listid);
            name = Strings.joinStrings(" ", var, 2);
            localized_name = name;
            if (listid == 0) {
                String[] langs = name.split(";");
                if (langs.length == 2)
                    localized_name = langs[player.isLangRus() ? 1 : 0];
            }

            if (!sets.containsKey(name)) {
                if (player.isLangRus())
                    return "<tr><td align=center><font color=FF3355>Набор '" + localized_name + "' не найден</font></td></tr>";
                else
                    return "<tr><td align=center><font color=FF3355>'" + localized_name + "' set not found</font></td></tr>";
            }

            set = sets.get(name);
        }

        String pagePrev = pageIdx == 0 ? "" : htmlButton("&$543;", 80, 25, "list", param1.replaceFirst(var[1], a[0] + "_" + (pageIdx - 1)));
        String pageNext = "";
        List<String> tds = new ArrayList<String>();

        for (int i = pageIdx * pageMax; i < set.size(); i++) {
            if (tds.size() == pageMax) {
                pageNext = htmlButton("&$544;", 80, 25, "list", param1.replaceFirst(var[1], a[0] + "_" + (pageIdx + 1)));
                break;
            }
            Skill _buff = set.get(i);
            if (_buff == null)
                continue;
            String buff_str = "<td FIXWIDTH=5>&nbsp;</td>";
            buff_str = "<td FIXWIDTH=35 valign=top><img src=\"" + _buff.getIcon() + "\" width=32 height=32><br></td>";
            buff_str += "<td FIXWIDTH=30>" + htmlButton("$", 25, 32, "get", 1, _buff.getId(), param1) + "</td>";
            if (player.isLangRus())
                buff_str += "<td FIXWIDTH=125><font color=3399FF>" + _buff.getName(player) + "</font><br1><font color=LEVEL> Уровень " + _buff.getLevel() + "</font></td>";
            else
                buff_str += "<td FIXWIDTH=125><font color=3399FF>" + _buff.getName(player) + "</font><br1><font color=LEVEL> Level " + _buff.getLevel() + "</font></td>";
            tds.add(buff_str);
        }

        StringBuilder result = new StringBuilder();

        long cost = 0L;
        if (player.getLevel() > BBSConfig.BUFF_SERVICE_MAX_LEVEL_FOR_FREE_BUFF && BBSConfig.BUFF_SERVICE_COST_ITEM_COUNT > 0)
            cost = set.size() * BBSConfig.BUFF_SERVICE_COST_ITEM_COUNT;

        result.append("<table width=650><tr>");
        String all = "All";
        if (player.isLangRus())
            all = "Все";

        result.append("<td width=600 align=center><font color=DDD3B6>");
        result.append(localized_name);
        result.append("</font></td>");
        result.append("</tr>");
        if (!_all/* && pageNext.isEmpty() && pagePrev.isEmpty()*/) {
            result.append("<tr><td width=600 align=center>");
            if (cost > 0) {
                if (player.isLangRus())
                    result.append("Цена за набор: ");
                else
                    result.append("Set price: ");
                result.append("<font color=LEVEL>");
                result.append(Util.formatAdena(cost) + " " + HtmlUtils.htmlItemName(BBSConfig.BUFF_SERVICE_COST_ITEM_ID));
                result.append("</font>");
            } else {
                result.append("<font color=LEVEL>");
                if (player.isLangRus())
                    result.append("Бесплатно");
                else
                    result.append("Free");
                result.append("</font>");
            }
            result.append("</td></tr>");
        }
        result.append("</table>");
        if (!_all && /*pageNext.isEmpty() && pagePrev.isEmpty() && */tds.size() > 0) {
            result.append("<table width=300><tr><td align=center>");
            if (player.isLangRus()) {
                result.append(htmlButton("Себе", 100, 25, "get", 0, param1) + "</td>");
                if (player.hasServitor())
                    result.append("<td align=center>" + htmlButton("Питомцам", 100, 25, "get", 2, param1) + "</td>");
            } else {
                result.append(htmlButton("For Me", 100, 25, "get", 0, param1) + "</td>");
                if (player.hasServitor())
                    result.append("<td align=center>" + htmlButton("For Servitors", 100, 25, "get", 2, param1) + "</td>");
            }
            result.append("</tr></table>");
        }

        if (listid != 0) {
            if (player.isLangRus())
                result.append("<table><tr><td align=center>" + htmlButton("Редактировать", 125, 25, "editset", "edit", name) + "</td></tr></table>");
            else
                result.append("<table><tr><td align=center>" + htmlButton("Edit", 125, 25, "editset", "edit", name) + "</td></tr></table>");
        }

        if (!pagePrev.isEmpty() || !pageNext.isEmpty()) {
            result.append("<table><tr>");
            result.append("<td width=90 align=center>" + pagePrev + "</td>");
            result.append("<td width=80 align=center>");
            if (player.isLangRus())
                result.append("Страница: ");
            else
                result.append("Page: ");
            result.append(pageIdx + 1);
            result.append("</td>");
            result.append("<td width=90 align=center>" + pageNext + "</td>");
            result.append("</tr></table>");
        }

        if (tds.size() > 0) {
            result.append("<table width=605 background=\"L2UI_CH3.refinewnd_back_Pattern\">" + formatTable(tds, pageCols, false) + "</table>");
        }

        result.append("<br><table><tr><td align=center>" + htmlButton((player.isLangRus() ? "Назад" : "Back"), 125, 25, "0") + "</td></tr></table>");

        return result.toString();
    }

    private static String pageEdit(Player player, String[] var) {
        int charId = player.getObjectId();
        Map<String, List<Skill>> sets = getBuffSets(charId);
        String name = "";

        if (var[1].equalsIgnoreCase("del")) {
            //Log.add("BUFF\tУдален набор: " + name, "service_buff", player);
            name = Strings.joinStrings(" ", var, 2);
            deleteBuffSet(charId, name);
            sets.remove(name);
            return pageMain(player);
        }

        String result = "";
        List<String> tds = new ArrayList<String>();

        if (var[1].equalsIgnoreCase("delconf")) {
            result += "<table><tr>";
            name = Strings.joinStrings(" ", var, 2);
            if (player.isLangRus())
                result += "<td width=400 align=center><font color=FF3355>Вы действительно желаете удалить набор: " + name + "?</font></td>";
            else
                result += "<td width=400 align=center><font color=FF3355>Are you sure you want to delete a set: " + name + "?</font></td>";
            result += "</tr></table><table><tr>";
            if (player.isLangRus()) {
                result += "<td>" + htmlButton("ДА", 80, 25, "editset", "del", name) + "</td>";
                result += "<td>" + htmlButton("НЕТ", 80, 25, "editset", "edit", name) + "</td>";
            } else {
                result += "<td>" + htmlButton("YES", 80, 25, "editset", "del", name) + "</td>";
                result += "<td>" + htmlButton("NO", 80, 25, "editset", "edit", name) + "</td>";
            }
            result += "</tr></table>";

            return result;
        }

        List<Skill> set = null;

        if (var[1].equalsIgnoreCase("new")) {
            if (sets.size() >= BBSConfig.BUFF_SERVICE_MAX_BUFF_SETS_PER_CHAR)
                return player.isLangRus() ? "<table><tr><td><font color=FF3355>Вы достигли лимита наборов!</font></td></tr></table>" : "<table><tr><td><center><font color=FF3355>You have reached the limit set!</font></td></tr></table>";

            name = trimHtml(Strings.joinStrings(" ", var, 2));
            if (name.length() > 16)
                name = name.substring(0, 15);
            if (name.isEmpty() || name.equalsIgnoreCase(" ")) {
                if (player.isLangRus())
                    return "<table><tr><td><font color=FF3355>Необходимо указать имя набора!</font></td></tr></table>";
                else
                    return "<table><tr><td><font color=FF3355>You must specify the name of the set!</font></td></tr></table>";
            }

            set = new ArrayList<Skill>();
            for (Skill skill : ALL_BUFFS_SET) {
                if (player.getAbnormalList().contains(skill))
                    set.add(skill);
            }
            sets.put(name, set);
            updateBuffSet(charId, name, set);
            //Log.add("BUFF\tСоздан набор: " + name, "service_buff", player);
        } else if (var[1].equalsIgnoreCase("edit")) {
            name = Strings.joinStrings(" ", var, 2);
            if (!sets.containsKey(name)) {
                if (player.isLangRus())
                    return "<table><tr><td><font color=FF3355>Набор '" + name + "' не найден.</font></td></tr></table>";
                else
                    return "<table><tr><td><font color=FF3355>'" + name + "' set not found.</font></td></tr></table>";
            }
            set = sets.get(name);
        } else if (var[1].equalsIgnoreCase("rem")) {
            name = Strings.joinStrings(" ", var, 3);
            if (!sets.containsKey(name)) {
                if (player.isLangRus())
                    return "<table><tr><td><font color=FF3355>Набор '" + name + "' не найден</font></td></tr></table>";
                else
                    return "<table><tr><td><font color=FF3355>'" + name + "' set not found</font></td></tr></table>";
            }
            set = sets.get(name);
            int skill_to_remove = Integer.valueOf(var[2]);
            int idx = getSkillIdx(set, skill_to_remove);
            if (idx != -1)
                set.remove(idx);
            updateBuffSet(charId, name, set);
        } else if (var[1].equalsIgnoreCase("add")) {
            name = Strings.joinStrings(" ", var, 4);
            if (!sets.containsKey(name)) {
                if (player.isLangRus())
                    return "<table><tr><td><font color=FF3355>Набор '" + name + "' не найден</font></td></tr></table>";
                else
                    return "<table><tr><td><font color=FF3355>'" + name + "' set not found</font></td></tr></table>";
            }

            set = sets.get(name);
            if (set.size() >= BBSConfig.BUFF_SERVICE_MAX_BUFFS_IN_SET)
                return pageMain(player);

            final int addSkillId = Integer.valueOf(var[3]);
            if (addSkillId > 0) {
                set.add(AVAILABLE_BUFFS.get(addSkillId));
                updateBuffSet(charId, name, set);
            }

            final List<Skill> availableBuffs = new ArrayList<Skill>();
            for (int i = 0; i < ALL_BUFFS_SET.size(); i++) {
                Skill _buff = ALL_BUFFS_SET.get(i);
                if (_buff == null)
                    continue;

                int idx = getSkillIdx(set, _buff.getId());
                if (idx != -1)
                    continue;

                availableBuffs.add(_buff);
            }

            if (!availableBuffs.isEmpty()) {
                int pageIdx = Integer.valueOf(var[2]);
                int minSkillIndex = pageIdx * pageMax;
                while (pageIdx > 0 && minSkillIndex >= availableBuffs.size()) {
                    pageIdx--;
                    minSkillIndex = pageIdx * pageMax;
                }

                int maxSkillIndex = Math.min(minSkillIndex + pageMax, availableBuffs.size());

                String pagePrev = pageIdx == 0 ? "" : htmlButton("&$543;", 80, 25, "editset", "add", pageIdx - 1, 0, name);
                String pageNext = "";
                for (int i = minSkillIndex; i < maxSkillIndex; i++) {
                    Skill _buff = availableBuffs.get(i);
                    if (_buff == null)
                        continue;

                    String buff_str = "<td FIXWIDTH=5>&nbsp;</td>";
                    buff_str = "<td FIXWIDTH=35 valign=top><img src=\"" + _buff.getIcon() + "\" width=32 height=32><br></td>";
                    buff_str += "<td FIXWIDTH=40>" + htmlButton("+", 25, 32, "editset", "add", pageIdx, _buff.getId(), name) + "</td>";
                    if (player.isLangRus())
                        buff_str += "<td fixwidth=125><font color=3399FF>" + _buff.getName(player) + "</font><br1><font color=LEVEL> Уровень " + _buff.getLevel() + "</font></td>";
                    else
                        buff_str += "<td fixwidth=125><font color=3399FF>" + _buff.getName(player) + "</font><br1><font color=LEVEL> Level " + _buff.getLevel() + "</font></td>";
                    tds.add(buff_str);
                }

                if (availableBuffs.size() > maxSkillIndex)
                    pageNext = htmlButton("&$544;", 80, 25, "editset", "add", pageIdx + 1, 0, name);

                result += "<table><tr>";
                if (player.isLangRus())
                    result += "<td width=300 align=center><font color=DDD3B6>Редактирование набора: " + name + "</font></td>";
                else
                    result += "<td width=300 align=center><font color=DDD3B6>Set editing: " + name + "</font></td>";
                result += "</tr></table>";
                if (!pagePrev.isEmpty() || !pageNext.isEmpty()) {
                    result += "<table><tr>";
                    result += "<td width=90 align=center>" + pagePrev + "</td>";
                    result += "<td width=80 align=center>";
                    if (player.isLangRus())
                        result += "Страница: ";
                    else
                        result += "Page: ";
                    result += String.valueOf(pageIdx + 1);
                    result += "</td>";
                    result += "<td width=90 align=center>" + pageNext + "</td>";
                    result += "</tr></table>";
                }

                result += "<table width=605 background=\"L2UI_CH3.refinewnd_back_Pattern\">" + formatTable(tds, pageCols, false) + "</table>";
                result += "<br><table><tr><td>" + htmlButton((player.isLangRus() ? "Назад" : "Back"), 125, 25, "editset", "edit", name) + "</td></tr></table>";
                return result;
            }
        } else
            return pageMain(player);

        for (int i = 0; i < set.size(); i++) {
            Skill _buff = set.get(i);
            if (_buff == null)
                continue;

            String buff_str = "<td FIXWIDTH=5>&nbsp;</td>";
            buff_str = "<td FIXWIDTH=35 valign=top><img src=\"" + _buff.getIcon() + "\" width=32 height=32><br></td>";
            buff_str += "<td>" + htmlButton("-", 25, 32, "editset", "rem", _buff.getId(), name) + "</td>";
            if (player.isLangRus())
                buff_str += "<td FIXWIDTH=125><font color=3399FF>" + _buff.getName(player) + "</font><br1><font color=LEVEL> Уровень " + _buff.getLevel() + "</font></td>";
            else
                buff_str += "<td FIXWIDTH=125><font color=3399FF>" + _buff.getName(player) + "</font><br1><font color=LEVEL> Level " + _buff.getLevel() + "</font></td>";
            tds.add(buff_str);
        }

        result += "<table><tr>";
        if (player.isLangRus()) {
            result += "<td width=300 align=center><font color=DDD3B6>Редактирование: " + name + "</font></td></tr></table>";
            result += "<table><tr>";
            if (set.size() < BBSConfig.BUFF_SERVICE_MAX_BUFFS_IN_SET)
                result += "<td width=120 align=center>" + htmlButton("Добавить бафф", 120, 25, "editset", "add", 0, 0, name) + "</td>";
            result += "<td width=120 align=center>" + htmlButton("Удалить набор", 120, 25, "editset", "delconf", name) + "</td>";
            result += "</tr></table>";
        } else {
            result += "<td width=300 align=center><font color=DDD3B6>Editing: " + name + "</font></td></tr></table>";
            result += "<table><tr>";
            if (set.size() < BBSConfig.BUFF_SERVICE_MAX_BUFFS_IN_SET)
                result += "<td width=120 align=center>" + htmlButton("Add buff", 120, 25, "editset", "add", 0, 0, name) + "</td>";
            result += "<td width=120 align=center>" + htmlButton("Delete set", 120, 25, "editset", "delconf", name) + "</td>";
            result += "</tr></table>";
        }

        if (tds.size() > 0) {
            result += "<table width=605 background=\"L2UI_CH3.refinewnd_back_Pattern\">" + formatTable(tds, pageCols, false) + "</table>";
        }

        result += "<br><table><tr><td>" + htmlButton((player.isLangRus() ? "Назад" : "Back"), 125, 25, "list", "2_0", name) + "</td></tr></table>";

        return result;
    }

    private static String pageMain(Player player) {
        StringBuilder result = new StringBuilder();
        result.append("<table><tr>");

        result.append("<td width=350 align=center>");

        result.append("<table width=320 background=\"L2UI_CT1.editbox_df_bg\">");
        result.append("<tr>");
        result.append("<td align=center>");
        result.append("<button value=\"");

        if (player.isLangRus())
            result.append("Исцелиться");
        else
            result.append("Heal");

        result.append("\" action=\"bypass _bbsrestore\" width=150 height=25 back=\"L2UI_ct1.button_df_down\" fore=\"L2UI_ct1.button_df\">");
        result.append("</td>");
        result.append("<td align=center>");
        result.append("<button value=\"");

        if (player.isLangRus())
            result.append("Отменить эффекты");
        else
            result.append("Cancel effects");

        result.append("\" action=\"bypass _bbscancel\" width=150 height=25 back=\"L2UI_ct1.button_df_down\" fore=\"L2UI_ct1.button_df\">");
        result.append("</td>");
        result.append("</tr>");
        result.append("</table>");
        result.append("<br>");

        result.append("<table width=300 border=0 background=\"L2UI_CT1.editbox_df_bg\">");
        result.append("<tr>");
        result.append("<td align=center width=40 height=38>&nbsp;</td>");
        result.append("<td align=center width=220 height=38>");

        if (player.isLangRus())
            result.append(htmlButton("Все Баффы", 200, 30, "list", "0_0"));
        else
            result.append(htmlButton("All Buffs", 200, 30, "list", "0_0"));

        result.append("</td>");
        result.append("<td align=center width=40 height=38>&nbsp;</td>");
        result.append("</tr>");
        result.append("</table>");

        Map<String, List<Skill>> sets = getBuffSets(0);
        if (!sets.isEmpty()) {
            result.append("<br>");
            result.append("<table width=300 border=0>");
            result.append("<tr>");
            result.append("<td align=center width=40>&nbsp;</td>");
            result.append("<td align=center width=220>");

            if (player.isLangRus())
                result.append("Стандартные наборы");
            else
                result.append("Standart sets");

            result.append("</td>");
            result.append("<td align=center width=40>&nbsp;</td>");
            result.append("</tr>");
            result.append("</table>");

            for (Map.Entry<String, List<Skill>> set : sets.entrySet()) {
                String setname = set.getKey();
                String icon = set.getValue().get(0).getIcon();
                String name = setname;
                String[] langs = setname.split(";");
                if (langs.length == 2)
                    name = langs[player.isLangRus() ? 1 : 0];

                result.append("<br1>");
                result.append("<table width=300 border=0 background=\"L2UI_CT1.editbox_df_bg\">");
                result.append("<tr>");
                result.append("<td align=center width=40 height=38><img src=\"");
                result.append(icon);
                result.append("\" width=32 height=32></td>");
                result.append("<td align=center width=220 height=38>");
                result.append(htmlButton(name, 200, 30, "list", "1_0", setname));
                result.append("</td>");
                result.append("<td align=center width=40 height=38><img src=\"");
                result.append(icon);
                result.append("\" width=32 height=32></td>");
                result.append("</tr>");
                result.append("</table>");
            }
        }

        result.append("</td>");

        result.append("<td width=350 align=center>");
        result.append("<table width=300 background=\"L2UI_CH3.refinewnd_back_Pattern\" border=0 cellpadding=0 cellspacing=0>");
        result.append("<tr>");
        result.append("<td align=center>");
        if (player.isLangRus())
            result.append("<br><br><font color=DDD3B6>Собственные наборы</font><br>");
        else
            result.append("<br><br><font color=DDD3B6>Own sets</font><br>");
        result.append("<table>");

        result.append("<tr><td align=center><edit var=\"newfname\" width=200><br></td></tr>");
        result.append("<tr><td align=center>");

        if (player.isLangRus())
            result.append(htmlButton("Сохранить", 100, 25, "editset", "new", "$newfname"));
        else
            result.append(htmlButton("Save", 100, 25, "editset", "new", "$newfname"));

        result.append("</td></tr>");
        result.append("</table><br>");

        sets = getBuffSets(player.getObjectId());
        if (!sets.isEmpty()) {

            result.append("<table>");
            result.append("<tr><td><table width=288 bgcolor=3D3D3D><tr><td width=260 align=center></td></tr></table></td></tr>");
            result.append("<tr><td>");

            int i = 0;
            for (String setname : sets.keySet()) {
                if (i % 2 == 0)
                    result.append("<table width=288 bgcolor=000000>");
                else
                    result.append("<table width=288>");
                result.append("<tr>");
                result.append("<td width=185 align=center>");
                result.append(htmlButton(setname, 200, 25, "list", "2_0", setname));
                result.append("</td>");
                result.append("</tr></table>");
                i++;
            }

            result.append("</td></tr>");
            result.append("<tr><td align=center><table width=288 bgcolor=3D3D3D><tr><td width=260 align=center></td></tr></table></td></tr>");
            result.append("</table><br><br><br>");
        }
        result.append("<br><br>");
        result.append("</td>");
        result.append("</tr>");
        result.append("</table>");
        result.append("</td>");
        result.append("</tr>");
        result.append("</table>");

        return result.toString();
    }

    public String BuffList(String[] var, Player player) {
        if (var[0].equalsIgnoreCase("get"))
            return pageGet(player, var);

        if (var[0].equalsIgnoreCase("list"))
            return pageList(player, var);

        if (var[0].equalsIgnoreCase("editset") && var.length > 1)
            return pageEdit(player, var);

        return pageMain(player);
    }

    private static synchronized Map<String, List<Skill>> getBuffSets(int charId) {
        if (PLAYER_BUFF_SETS.containsKey(charId))
            return PLAYER_BUFF_SETS.get(charId);

        Map<String, List<Skill>> _new = loadBuffSets(charId);
        PLAYER_BUFF_SETS.put(charId, _new);
        return _new;
    }

    private static void updateBuffSet(int charId, String setname, List<Skill> _set) {
        String skills = _set.size() == 0 ? "" : String.valueOf(_set.get(0).getId());
        for (Skill skill : _set)
            skills += "," + skill.getId();

        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("REPLACE INTO bbs_buffs (char_id,name,skills) VALUES (?,?,?)");
            statement.setInt(1, charId);
            statement.setString(2, setname);
            statement.setString(3, skills);
            statement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    private static void deleteBuffSet(int charId, String setname) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("DELETE FROM bbs_buffs WHERE char_id=? AND name=?");
            statement.setInt(1, charId);
            statement.setString(2, setname);
            statement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    private static Map<String, List<Skill>> loadBuffSets(int charId) {
        Map<String, List<Skill>> result = new LinkedHashMap<String, List<Skill>>();

        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT name,skills FROM bbs_buffs WHERE char_id=? ORDER BY id");
            statement.setInt(1, charId);
            rset = statement.executeQuery();
            while (rset.next()) {
                List<Skill> next_set = new ArrayList<Skill>();
                String skills = rset.getString("skills");
                if (skills != null && !skills.isEmpty()) {
                    if (!skills.contains(","))
                        next_set.add(AVAILABLE_BUFFS.get(Integer.parseInt(skills)));
                    else {
                        String[] skill_ids = skills.split(",");
                        for (String skill_id : skill_ids)
                            if (!skill_id.isEmpty())
                                next_set.add(AVAILABLE_BUFFS.get(Integer.parseInt(skill_id)));
                    }
                }

                result.put(rset.getString("name"), next_set);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
        return result;
    }

    private static void cleanUP() {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("DELETE FROM `bbs_buffs` WHERE char_id != 0 AND char_id NOT IN(SELECT obj_id FROM characters);");
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    private static String formatTable(Collection<String> tds, int rows, boolean appendTD) {
        StringBuilder result = new StringBuilder();
        int i = 0;
        int j = 0;
        result.append("<tr><td align=center><br><br><br><br>");
        result.append("<table width=595 bgcolor=3D3D3D><tr><td width=590 align=center></td></tr></table>");
        for (String td : tds) {
            if (i == 0) {
                if (j % 2 == 0)
                    result.append("<table width=595 border=0 bgcolor=000000><tr>");
                else
                    result.append("<table width=595 border=0><tr>");
            }

            if (appendTD)
                result.append("<td align=center width=195>");

            result.append(td);

            if (appendTD)
                result.append("</td>");

            i++;
            if (i == rows) {
                result.append("</tr></table>");
                i = 0;
                j++;
            }
        }
        if (i > 0 && i < rows) {
            while (i < rows) {
                result.append("<td align=center width=195></td>");
                i++;
            }
            result.append("</tr></table>");
        }
        result.append("<table width=595 bgcolor=3D3D3D><tr><td width=590 align=center></td></tr></table>");
        result.append("<br><br><br><br><br></td></tr>");
        return result.toString();
    }

    /**
     * кроме обычного trim, заменяет кавычки на нестандартные UTF-8, удяляет ВСЕ двойные пробелы, убирает символы <>
     */
    private static String trimHtml(String s) {
        int i;
        s = s.trim().replaceAll("\"", "״").replaceAll("'", "´").replaceAll("<", "").replaceAll(">", "");
        do {
            i = s.length();
            s = s.replaceAll("  ", " ");
        }
        while (i > s.length());

        return s;
    }
}