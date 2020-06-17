/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.bypasshandlers;

import org.l2j.commons.util.CommonUtil;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.enums.AttributeType;
import org.l2j.gameserver.enums.DropType;
import org.l2j.gameserver.handler.IBypassHandler;
import org.l2j.gameserver.model.Spawn;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.DropHolder;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.util.HtmlUtil;
import org.l2j.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Objects.isNull;
import static org.l2j.commons.util.Util.SPACE;
import static org.l2j.commons.util.Util.parseNextInt;
import static org.l2j.gameserver.util.GameUtils.doIfIsNpc;
import static org.l2j.gameserver.util.GameUtils.isAttackable;

/**
 * @author NosBit
 * @author JoeAlisson
 */
public class NpcViewMod implements IBypassHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(NpcViewMod.class);
    private static final String[] COMMANDS = { "NpcViewMod" };

    private static final int DROP_LIST_ITEMS_PER_PAGE = 8;

    @Override
    public boolean useBypass(String command, Player player, Creature bypassOrigin) {
        final StringTokenizer st = new StringTokenizer(command);
        st.nextToken();

        if (!st.hasMoreTokens()) {
            LOGGER.warn("Bypass[NpcViewMod] used without enough parameters.");
            return false;
        }

        final String actualCommand = st.nextToken();
        switch (actualCommand.toLowerCase()) {
            case "view": {
                int objectId = parseNextInt(st, -1);
                final WorldObject target = objectId > 0 ? World.getInstance().findObject(objectId) : player.getTarget();
                doIfIsNpc(target, npc -> sendNpcView(player, npc));
                break;
            }
            case "droplist": {
                if (st.countTokens() < 2) {
                    LOGGER.warn("Bypass[NpcViewMod] used without enough parameters.");
                    return false;
                }

                final String dropListTypeString = st.nextToken();
                try {
                    final DropType dropListType = Enum.valueOf(DropType.class, dropListTypeString);
                    final WorldObject target = World.getInstance().findObject(Integer.parseInt(st.nextToken()));

                    if(!(target instanceof Npc npc)) {
                        return false;
                    }
                    sendNpcDropList(player, npc, dropListType, Util.parseNextInt(st, 0));
                }
                catch (IllegalArgumentException e) {
                    LOGGER.warn("Bypass[NpcViewMod] unknown drop list scope: {}", dropListTypeString);
                    return false;
                }
                break;
            }
            case "skills": {
                final var objectId = parseNextInt(st, -1);
                final WorldObject target = objectId > 0 ? World.getInstance().findObject(objectId) : player.getTarget();
                doIfIsNpc(target, npc -> sendNpcSkillView(player, npc));
                break;
            }
            case "aggrolist": {
                final var objectId = parseNextInt(st, -1);
                final WorldObject target = objectId > 0 ? World.getInstance().findObject(objectId) : player.getTarget();
                doIfIsNpc(target, npc -> sendAggroListView(player, npc));
                break;
            }
        }
        return true;
    }

    @Override
    public String[] getBypassList()
    {
        return COMMANDS;
    }

    private void sendNpcView(Player activeChar, Npc npc) {
        final NpcHtmlMessage html = new NpcHtmlMessage();
        html.setFile(activeChar, "data/html/mods/NpcView/Info.htm");
        html.replace("%name%", npc.getName());
        html.replace("%hpGauge%", HtmlUtil.getHpGauge(250, (long) npc.getCurrentHp(), npc.getMaxHp(), false));
        html.replace("%mpGauge%", HtmlUtil.getMpGauge(250, (long) npc.getCurrentMp(), npc.getMaxMp(), false));

        final Spawn npcSpawn = npc.getSpawn();
        if (isNull(npcSpawn) || (npcSpawn.getRespawnMinDelay() == 0)) {
            html.replace("%respawn%", "None");
        }
        else
        {
            TimeUnit timeUnit = TimeUnit.MILLISECONDS;
            long min = Long.MAX_VALUE;
            for (TimeUnit tu : TimeUnit.values()) {
                final long minTimeFromMillis = tu.convert(npcSpawn.getRespawnMinDelay(), TimeUnit.MILLISECONDS);
                final long maxTimeFromMillis = tu.convert(npcSpawn.getRespawnMaxDelay(), TimeUnit.MILLISECONDS);
                if ((TimeUnit.MILLISECONDS.convert(minTimeFromMillis, tu) == npcSpawn.getRespawnMinDelay()) && (TimeUnit.MILLISECONDS.convert(maxTimeFromMillis, tu) == npcSpawn.getRespawnMaxDelay()))
                {
                    if (min > minTimeFromMillis)
                    {
                        min = minTimeFromMillis;
                        timeUnit = tu;
                    }
                }
            }
            final long minRespawnDelay = timeUnit.convert(npcSpawn.getRespawnMinDelay(), TimeUnit.MILLISECONDS);
            final long maxRespawnDelay = timeUnit.convert(npcSpawn.getRespawnMaxDelay(), TimeUnit.MILLISECONDS);
            final String timeUnitName = timeUnit.name().charAt(0) + timeUnit.name().toLowerCase().substring(1);
            if (npcSpawn.hasRespawnRandom())
            {
                html.replace("%respawn%", minRespawnDelay + "-" + maxRespawnDelay + " " + timeUnitName);
            }
            else
            {
                html.replace("%respawn%", minRespawnDelay + " " + timeUnitName);
            }
        }

        html.replace("%atktype%", CommonUtil.capitalizeFirst(npc.getAttackType().name().toLowerCase()));
        html.replace("%atkrange%", npc.getStats().getPhysicalAttackRange());

        html.replace("%patk%", npc.getPAtk());
        html.replace("%pdef%", npc.getPDef());

        html.replace("%matk%", npc.getMAtk());
        html.replace("%mdef%", npc.getMDef());

        html.replace("%atkspd%", npc.getPAtkSpd());
        html.replace("%castspd%", npc.getMAtkSpd());

        html.replace("%critrate%", npc.getStats().getCriticalHit());
        html.replace("%evasion%", npc.getEvasionRate());

        html.replace("%accuracy%", npc.getStats().getAccuracy());
        html.replace("%speed%", (int) npc.getStats().getMoveSpeed());

        html.replace("%attributeatktype%", npc.getStats().getAttackElement().name());
        html.replace("%attributeatkvalue%", npc.getStats().getAttackElementValue(npc.getStats().getAttackElement()));
        html.replace("%attributefire%", npc.getStats().getDefenseElementValue(AttributeType.FIRE));
        html.replace("%attributewater%", npc.getStats().getDefenseElementValue(AttributeType.WATER));
        html.replace("%attributewind%", npc.getStats().getDefenseElementValue(AttributeType.WIND));
        html.replace("%attributeearth%", npc.getStats().getDefenseElementValue(AttributeType.EARTH));
        html.replace("%attributedark%", npc.getStats().getDefenseElementValue(AttributeType.DARK));
        html.replace("%attributeholy%", npc.getStats().getDefenseElementValue(AttributeType.HOLY));

        html.replace("%dropListButtons%", getDropListButtons(npc));

        activeChar.sendPacket(html);
    }

    private void sendNpcSkillView(Player activeChar, Npc npc)
    {
        final NpcHtmlMessage html = new NpcHtmlMessage();
        html.setFile(activeChar, "data/html/mods/NpcView/Skills.htm");

        final StringBuilder sb = new StringBuilder();

        npc.getSkills().values().forEach(s ->
        {
            sb.append("<table width=277 height=32 cellspacing=0 background=\"L2UI_CT1.Windows.Windows_DF_TooltipBG\">");
            sb.append("<tr><td width=32>");
            sb.append("<img src=\"");
            sb.append(s.getIcon());
            sb.append("\" width=32 height=32>");
            sb.append("</td><td width=110>");
            sb.append(s.getName());
            sb.append("</td>");
            sb.append("<td width=45 align=center>");
            sb.append(s.getId());
            sb.append("</td>");
            sb.append("<td width=35 align=center>");
            sb.append(s.getLevel());
            sb.append("</td></tr></table>");
        });

        html.replace("%skills%", sb.toString());
        html.replace("%npc_name%", npc.getName());
        html.replace("%npcId%", npc.getId());

        activeChar.sendPacket(html);
    }

    private static void sendAggroListView(Player activeChar, Npc npc)
    {
        final NpcHtmlMessage html = new NpcHtmlMessage();
        html.setFile(activeChar, "data/html/mods/NpcView/AggroList.htm");

        final StringBuilder sb = new StringBuilder();

        if (isAttackable(npc))
        {
            ((Attackable) npc).getAggroList().values().forEach(a ->
            {
                sb.append("<table width=277 height=32 cellspacing=0 background=\"L2UI_CT1.Windows.Windows_DF_TooltipBG\">");
                sb.append("<tr><td width=110>");
                sb.append(a.getAttacker() != null ? a.getAttacker().getName() : "NULL");
                sb.append("</td>");
                sb.append("<td width=60 align=center>");
                sb.append(a.getHate());
                sb.append("</td>");
                sb.append("<td width=60 align=center>");
                sb.append(a.getDamage());
                sb.append("</td></tr></table>");
            });
        }

        html.replace("%aggrolist%", sb.toString());
        html.replace("%npc_name%", npc.getName());
        html.replace("%npcId%", npc.getId());
        html.replace("%objid%", npc.getObjectId());

        activeChar.sendPacket(html);
    }

    private static String getDropListButtons(Npc npc)
    {
        final StringBuilder sb = new StringBuilder();
        final List<DropHolder> dropListDeath = npc.getTemplate().getDropList(DropType.DROP);
        final List<DropHolder> dropListSpoil = npc.getTemplate().getDropList(DropType.SPOIL);
        if ((dropListDeath != null) || (dropListSpoil != null))
        {
            sb.append("<table width=275 cellpadding=0 cellspacing=0><tr>");
            if (dropListDeath != null)
            {
                sb.append("<td align=center><button value=\"Show Drop\" width=100 height=25 action=\"bypass NpcViewMod dropList DROP ").append(npc.getObjectId()).append("\" back=\"L2UI_CT1.Button_DF_Calculator_Down\" fore=\"L2UI_CT1.Button_DF_Calculator\"></td>");
            }

            if (dropListSpoil != null)
            {
                sb.append("<td align=center><button value=\"Show Spoil\" width=100 height=25 action=\"bypass NpcViewMod dropList SPOIL ").append(npc.getObjectId()).append("\" back=\"L2UI_CT1.Button_DF_Calculator_Down\" fore=\"L2UI_CT1.Button_DF_Calculator\"></td>");
            }

            sb.append("</tr></table>");
        }
        return sb.toString();
    }

    private void sendNpcDropList(Player player, Npc npc, DropType dropType, int page)
    {
        final var dropList = npc.getTemplate().getDropList(dropType);
        if (isNull(dropList)) {
            return;
        }

        int pages = (int) Math.ceil(dropList.size() / (double) DROP_LIST_ITEMS_PER_PAGE);
        page = max(0, min(page, pages - 1));

        final StringBuilder pagesSb = new StringBuilder();

        if (pages > 1) {
            pagesSb.append("<table><tr>");
            for (int i = max(0, page-3); i <= min(pages-1, page+3); i++) {
                if(i == page) {
                    pagesSb.append("<td width=20 height=20 align=CENTER>").append(i + 1).append("</td>");
                } else {
                    pagesSb.append("<td><button value=\"").append(i + 1).append("\" width=20 height=20 action=\"bypass NpcViewMod dropList ").append(dropType).append(SPACE).append(npc.getObjectId()).append(SPACE).append(i).append("\" fore=\"L2UI_CT1.Button_DF_Calculator\"></td>");
                }
            }
            pagesSb.append("</tr></table>");
        }

        final int start = page > 0 ? page * DROP_LIST_ITEMS_PER_PAGE : 0;
        int end = min(page * DROP_LIST_ITEMS_PER_PAGE + DROP_LIST_ITEMS_PER_PAGE, dropList.size());

        final DecimalFormat amountFormat = new DecimalFormat("#,###");
        final DecimalFormat chanceFormat = new DecimalFormat("0.00##");

        int leftHeight = 0;
        int rightHeight = 0;
        final StringBuilder leftSb = new StringBuilder();
        final StringBuilder rightSb = new StringBuilder();
        String limitReachedMsg = "";

        for (int i = start; i < end; i++) {
            final StringBuilder sb = new StringBuilder();

            int height = 64;
            final DropHolder dropItem = dropList.get(i);
            final ItemTemplate item = ItemEngine.getInstance().getTemplate(dropItem.getItemId());

            // real time server rate calculations
            double rateChance = 1;
            double rateAmount = 1;
            if (dropType == DropType.SPOIL)
            {
                rateChance = Config.RATE_SPOIL_DROP_CHANCE_MULTIPLIER;
                rateAmount = Config.RATE_SPOIL_DROP_AMOUNT_MULTIPLIER;
            }
            else
            {
                if (Config.RATE_DROP_CHANCE_BY_ID.get(dropItem.getItemId()) != null)
                {
                    rateChance *= Config.RATE_DROP_CHANCE_BY_ID.get(dropItem.getItemId());
                }
                else if (item.hasExImmediateEffect())
                {
                    rateChance *= Config.RATE_HERB_DROP_CHANCE_MULTIPLIER;
                }
                else if (npc.isRaid())
                {
                    rateChance *= Config.RATE_RAID_DROP_CHANCE_MULTIPLIER;
                }
                else
                {
                    rateChance *= Config.RATE_DEATH_DROP_CHANCE_MULTIPLIER;
                }

                if (Config.RATE_DROP_AMOUNT_BY_ID.get(dropItem.getItemId()) != null)
                {
                    rateAmount *= Config.RATE_DROP_AMOUNT_BY_ID.get(dropItem.getItemId());
                }
                else if (item.hasExImmediateEffect())
                {
                    rateAmount *= Config.RATE_HERB_DROP_AMOUNT_MULTIPLIER;
                }
                else if (npc.isRaid())
                {
                    rateAmount *= Config.RATE_RAID_DROP_AMOUNT_MULTIPLIER;
                }
                else
                {
                    rateAmount *= Config.RATE_DEATH_DROP_AMOUNT_MULTIPLIER;
                }
            }

            sb.append("<table width=332 cellpadding=2 cellspacing=0 background=\"L2UI_CT1.Windows.Windows_DF_TooltipBG\">");
            sb.append("<tr><td width=32 valign=top>");
            sb.append("<button width=\"32\" height=\"32\" itemtooltip=\"").append(dropItem.getItemId()).append("\"></td>");
            sb.append("<td><table width=295 cellpadding=0 cellspacing=4>");
            sb.append("<tr><td width=48 align=right valign=top><font color=\"LEVEL\">Amount: </font></td>");
            sb.append("<td>");

            final long min = (long) (dropItem.getMin() * rateAmount);
            final long max = (long) (dropItem.getMax() * rateAmount);
            if (min == max)
            {
                sb.append(amountFormat.format(min));
            }
            else
            {
                sb.append(amountFormat.format(min));
                sb.append(" - ");
                sb.append(amountFormat.format(max));
            }

            sb.append("</td></tr><tr><td width=48 align=right valign=top><font color=\"LEVEL\">Chance:</font></td>");
            sb.append("<td width=247>");
            sb.append(chanceFormat.format(min(dropItem.getChance() * rateChance, 100)));
            sb.append("%</td></tr></table></td></tr><tr><td width=32></td><td width=300>&nbsp;</td></tr></table>");

            if ((sb.length() + rightSb.length() + leftSb.length()) < 12000) // limit of 32766?
            {
                if (leftHeight >= (rightHeight + height))
                {
                    rightSb.append(sb);
                    rightHeight += height;
                }
                else
                {
                    leftSb.append(sb);
                    leftHeight += height;
                }
            }
            else
            {
                limitReachedMsg = "<br><center>Too many drops! Could not display them all!</center>";
                break;
            }
        }

        String html = HtmCache.getInstance().getHtm(player, "data/html/mods/NpcView/DropList.htm");
        if (html == null)
        {
            LOGGER.warn("The html file data/html/mods/NpcView/DropList.htm could not be found.");
            return;
        }
        html = html.replaceAll("%name%", npc.getName());
        html = html.replaceAll("%dropListButtons%", getDropListButtons(npc));
        html = html.replaceAll("%pages%", pagesSb.toString());
        String bodySb = "<table><tr><td>" + leftSb.toString() + "</td><td>" + rightSb.toString() + "</td></tr></table>";
        html = html.replaceAll("%items%", bodySb + limitReachedMsg);
        GameUtils.sendCBHtml(player, html);
    }
}
