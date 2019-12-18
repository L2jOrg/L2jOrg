package handlers.bypasshandlers;

import org.l2j.commons.util.CommonUtil;
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
import org.l2j.gameserver.model.items.ItemTemplate;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.util.HtmlUtil;
import org.l2j.gameserver.world.World;

import java.text.DecimalFormat;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import static org.l2j.gameserver.util.GameUtils.isAttackable;

/**
 * @author NosBit
 */
public class NpcViewMod implements IBypassHandler
{
    private static final String[] COMMANDS =
            {
                    "NpcViewMod"
            };

    private static final int DROP_LIST_ITEMS_PER_PAGE = 10;

    @Override
    public boolean useBypass(String command, Player activeChar, Creature bypassOrigin)
    {
        final StringTokenizer st = new StringTokenizer(command);
        st.nextToken();

        if (!st.hasMoreTokens())
        {
            LOGGER.warn("Bypass[NpcViewMod] used without enough parameters.");
            return false;
        }

        final String actualCommand = st.nextToken();
        switch (actualCommand.toLowerCase())
        {
            case "view":
            {
                final WorldObject target;
                if (st.hasMoreElements())
                {
                    try
                    {
                        target = World.getInstance().findObject(Integer.parseInt(st.nextToken()));
                    }
                    catch (NumberFormatException e)
                    {
                        return false;
                    }
                }
                else
                {
                    target = activeChar.getTarget();
                }

                final Npc npc = target instanceof Npc ? (Npc) target : null;
                if (npc == null)
                {
                    return false;
                }

                sendNpcView(activeChar, npc);
                break;
            }
            case "droplist":
            {
                if (st.countTokens() < 2)
                {
                    LOGGER.warn("Bypass[NpcViewMod] used without enough parameters.");
                    return false;
                }

                final String dropListTypeString = st.nextToken();
                try
                {
                    final DropType dropListType = Enum.valueOf(DropType.class, dropListTypeString);
                    final WorldObject target = World.getInstance().findObject(Integer.parseInt(st.nextToken()));
                    final Npc npc = target instanceof Npc ? (Npc) target : null;
                    if (npc == null)
                    {
                        return false;
                    }
                    final int page = st.hasMoreElements() ? Integer.parseInt(st.nextToken()) : 0;
                    sendNpcDropList(activeChar, npc, dropListType, page);
                }
                catch (NumberFormatException e)
                {
                    return false;
                }
                catch (IllegalArgumentException e)
                {
                    LOGGER.warn("Bypass[NpcViewMod] unknown drop list scope: " + dropListTypeString);
                    return false;
                }
                break;
            }
            case "skills":
            {
                final WorldObject target;
                if (st.hasMoreElements())
                {
                    try
                    {
                        target = World.getInstance().findObject(Integer.parseInt(st.nextToken()));
                    }
                    catch (NumberFormatException e)
                    {
                        return false;
                    }
                }
                else
                {
                    target = activeChar.getTarget();
                }

                final Npc npc = target instanceof Npc ? (Npc) target : null;
                if (npc == null)
                {
                    return false;
                }

                sendNpcSkillView(activeChar, npc);
                break;
            }
            case "aggrolist":
            {
                final WorldObject target;
                if (st.hasMoreElements())
                {
                    try
                    {
                        target = World.getInstance().findObject(Integer.parseInt(st.nextToken()));
                    }
                    catch (NumberFormatException e)
                    {
                        return false;
                    }
                }
                else
                {
                    target = activeChar.getTarget();
                }

                final Npc npc = target instanceof Npc ? (Npc) target : null;
                if (npc == null)
                {
                    return false;
                }

                sendAggroListView(activeChar, npc);
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

    public static void sendNpcView(Player activeChar, Npc npc)
    {
        final NpcHtmlMessage html = new NpcHtmlMessage();
        html.setFile(activeChar, "data/html/mods/NpcView/Info.htm");
        html.replace("%name%", npc.getName());
        html.replace("%hpGauge%", HtmlUtil.getHpGauge(250, (long) npc.getCurrentHp(), npc.getMaxHp(), false));
        html.replace("%mpGauge%", HtmlUtil.getMpGauge(250, (long) npc.getCurrentMp(), npc.getMaxMp(), false));

        final Spawn npcSpawn = npc.getSpawn();
        if ((npcSpawn == null) || (npcSpawn.getRespawnMinDelay() == 0))
        {
            html.replace("%respawn%", "None");
        }
        else
        {
            TimeUnit timeUnit = TimeUnit.MILLISECONDS;
            long min = Long.MAX_VALUE;
            for (TimeUnit tu : TimeUnit.values())
            {
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

    private static void sendNpcSkillView(Player activeChar, Npc npc)
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
                sb.append("<td align=center><button value=\"Show Drop\" width=100 height=25 action=\"bypass NpcViewMod dropList DROP " + npc.getObjectId() + "\" back=\"L2UI_CT1.Button_DF_Calculator_Down\" fore=\"L2UI_CT1.Button_DF_Calculator\"></td>");
            }

            if (dropListSpoil != null)
            {
                sb.append("<td align=center><button value=\"Show Spoil\" width=100 height=25 action=\"bypass NpcViewMod dropList SPOIL " + npc.getObjectId() + "\" back=\"L2UI_CT1.Button_DF_Calculator_Down\" fore=\"L2UI_CT1.Button_DF_Calculator\"></td>");
            }

            sb.append("</tr></table>");
        }
        return sb.toString();
    }

    private static void sendNpcDropList(Player activeChar, Npc npc, DropType dropType, int page)
    {
        final List<DropHolder> dropList = npc.getTemplate().getDropList(dropType);
        if (dropList == null)
        {
            return;
        }

        int pages = dropList.size() / DROP_LIST_ITEMS_PER_PAGE;
        if ((DROP_LIST_ITEMS_PER_PAGE * pages) < dropList.size())
        {
            pages++;
        }

        final StringBuilder pagesSb = new StringBuilder();
        if (pages > 1)
        {
            pagesSb.append("<table><tr>");
            for (int i = 0; i < pages; i++)
            {
                pagesSb.append("<td align=center><button value=\"" + (i + 1) + "\" width=20 height=20 action=\"bypass NpcViewMod dropList " + dropType + " " + npc.getObjectId() + " " + i + "\" back=\"L2UI_CT1.Button_DF_Calculator_Down\" fore=\"L2UI_CT1.Button_DF_Calculator\"></td>");
            }
            pagesSb.append("</tr></table>");
        }

        if (page >= pages)
        {
            page = pages - 1;
        }

        final int start = page > 0 ? page * DROP_LIST_ITEMS_PER_PAGE : 0;

        int end = (page * DROP_LIST_ITEMS_PER_PAGE) + DROP_LIST_ITEMS_PER_PAGE;
        if (end > dropList.size())
        {
            end = dropList.size();
        }

        final DecimalFormat amountFormat = new DecimalFormat("#,###");
        final DecimalFormat chanceFormat = new DecimalFormat("0.00##");

        int leftHeight = 0;
        int rightHeight = 0;
        final double dropAmountEffectBonus = activeChar.getStats().getValue(Stat.BONUS_DROP_AMOUNT, 1);
        final double dropRateEffectBonus = activeChar.getStats().getValue(Stat.BONUS_DROP_RATE, 1);
        final double spoilRateEffectBonus = activeChar.getStats().getValue(Stat.BONUS_SPOIL_RATE, 1);
        final StringBuilder leftSb = new StringBuilder();
        final StringBuilder rightSb = new StringBuilder();
        String limitReachedMsg = "";
        for (int i = start; i < end; i++)
        {
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

                // bonus spoil rate effect
                rateChance *= spoilRateEffectBonus;
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

                // bonus drop amount effect
                rateAmount *= dropAmountEffectBonus;
                // bonus drop rate effect
                rateChance *= dropRateEffectBonus;
            }

            sb.append("<table width=332 cellpadding=2 cellspacing=0 background=\"L2UI_CT1.Windows.Windows_DF_TooltipBG\">");
            sb.append("<tr><td width=32 valign=top>");
            sb.append("<button width=\"32\" height=\"32\" back=\"" + (item.getIcon() == null ? "icon.etc_question_mark_i00" : item.getIcon()) + "\" fore=\"" + (item.getIcon() == null ? "icon.etc_question_mark_i00" : item.getIcon()) + "\" itemtooltip=\"" + dropItem.getItemId() + "\">");
            sb.append("</td><td fixwidth=300 align=center><font name=\"hs9\" color=\"CD9000\">");
            sb.append(item.getName());
            sb.append("</font></td></tr><tr><td width=32></td><td width=300><table width=295 cellpadding=0 cellspacing=0>");
            sb.append("<tr><td width=48 align=right valign=top><font color=\"LEVEL\">Amount:</font></td>");
            sb.append("<td width=247 align=center>");

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
            sb.append("<td width=247 align=center>");
            sb.append(chanceFormat.format(Math.min(dropItem.getChance() * rateChance, 100)));
            sb.append("%</td></tr></table></td></tr><tr><td width=32></td><td width=300>&nbsp;</td></tr></table>");

            if ((sb.length() + rightSb.length() + leftSb.length()) < 16000) // limit of 32766?
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
            }
        }

        final StringBuilder bodySb = new StringBuilder();
        bodySb.append("<table><tr>");
        bodySb.append("<td>");
        bodySb.append(leftSb.toString());
        bodySb.append("</td><td>");
        bodySb.append(rightSb.toString());
        bodySb.append("</td>");
        bodySb.append("</tr></table>");

        String html = HtmCache.getInstance().getHtm(activeChar, "data/html/mods/NpcView/DropList.htm");
        if (html == null)
        {
            LOGGER.warn(NpcViewMod.class.getSimpleName() + ": The html file data/html/mods/NpcView/DropList.htm could not be found.");
            return;
        }
        html = html.replaceAll("%name%", npc.getName());
        html = html.replaceAll("%dropListButtons%", getDropListButtons(npc));
        html = html.replaceAll("%pages%", pagesSb.toString());
        html = html.replaceAll("%items%", bodySb.toString() + limitReachedMsg);
        GameUtils.sendCBHtml(activeChar, html);
    }
}
