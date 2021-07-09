package org.l2j.scripts.quests.Q00630_PirateTreasureHunt;

import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;

import static java.util.Objects.isNull;

public class Q00630_PirateTreasureHunt extends Quest {
    public final int GAROM = 34021;
    public final int CHEST = 24058;
    public final int TELEPORT1 = 34038;

    //mobs
    public final int PLAKALWIK = 24041;
    public final int DOHLAK = 24042;
    public final int SYWESTVO = 24043;


    //items
    public final int KEY = 90815;
    public final int NAGRADASUNDUK = 90762;
    public final int MAP = 90756;
    public final int PIECEMAP = 90755;
    public static final String A_LIST = "a_list";
    private static final int DESPAWN_TIME = 600000;

    private static final ItemHolder TREASURE_MAP = new ItemHolder(90756, 1);
    private static final ItemHolder TREASURE_KEY = new ItemHolder(90815, 1);
    private static final ItemHolder TREASURE_CHEST = new ItemHolder(90762, 1);

    public Q00630_PirateTreasureHunt() {
        super(630);
        addStartNpc(GAROM);
        addTalkId(CHEST);
        addItemTalkId(KEY);
        addKillId(PLAKALWIK, DOHLAK, SYWESTVO);
        addCondLevel(70, 90, "no_lvl.html");
    }

    @Override
    public boolean checkPartyMember(Player member, Npc npc)
    {
        final QuestState qs = getQuestState(member, false);
        return ((qs != null) && qs.isStarted());
    }

    @Override
    public String onAdvEvent(String event, Npc npc, Player player){
        final QuestState qs = getQuestState(player, false);
        if(qs != null) {
            processEvent(event, player, qs);
        }
        return null;
    }

    private void processEvent(String event, Player player, QuestState qs) {
        if(event.equalsIgnoreCase("GAROM2.htm"))
        {
            qs.setCond(1);
        }
        else if(event.equalsIgnoreCase("GAROMCHANGE2.htm"))
        {
            giveItems(player, TREASURE_MAP);
            takeItems(player, 25, PIECEMAP);
        }
        else if(event.equalsIgnoreCase("GAROM4.htm"))
        {
            qs.setCond(2);
        }
        else if(event.equalsIgnoreCase("TP2.htm"))
        {
            takeItems(player, 1, MAP);
            giveItems(player, TREASURE_KEY);
            qs.setCond(6);
        }
        else if(event.equalsIgnoreCase("KAENA4.htm"))
        {
            qs.setCond(6);
        }
        else if(event.equalsIgnoreCase("SUND2.htm"))
        {
            addExpAndSp(player,300000, 9000);
            giveItems(player, TREASURE_CHEST);
            takeItems(player, 1, 90815);
            qs.exitQuest(false, true);
        }
    }

    @Override
    public String onTalk(Npc npc, Player talker) {
        final QuestState qs = getQuestState(talker, true);
        String htmlText = getNoQuestMsg(talker);

        if(qs != null) {
            htmlText = processChat(npc, talker, qs, htmlText);
        }
        return htmlText;
    }

    private String processChat(Npc npc, Player talker, QuestState qs, String htmltext) {
        int npcId = npc.getId();
        int cond = qs.getCond();

        if(npcId == GAROM) {
            if(cond == 0 && getQuestItemsCount(talker, MAP) >= 1)
            {
                htmltext = "GAROM.htm";
            }
            if(cond == 0 && getQuestItemsCount(talker,PIECEMAP) >= 25)
            {
                htmltext = "GAROMCHANGE.htm";
            }
            if(cond == 0 && getQuestItemsCount(talker,MAP) < 1 && getQuestItemsCount(talker, PIECEMAP) < 25)
            {
                htmltext = "NOITEMS.htm";
            }
            if(cond == 1)
            {
                htmltext = "GAROM3.htm";
            }
            if(cond == 2)
            {
                htmltext = "GAROM4.htm";
            }
        }
        if(npcId == CHEST) {
            if(cond ==12){
                htmltext = "SUND.htm";
            }

        }
        if(npcId == TELEPORT1) {
            if(cond ==2){
                htmltext = "TP.htm";
            }
            if(cond ==6){
                htmltext = "TP3.htm";
            }
            if(cond ==7){
                htmltext = "TP4.htm";
            }
            if(cond ==8){
                htmltext = "TP5.htm";
            }

        }
        return htmltext;
    }

    @Override
    public String onKill(Npc npc, Player killer, boolean isSummon) {
        final QuestState qs = getQuestState(killer, false);
        if (qs == null)
        {
            return null;
        }
        if(qs.getCond() == 9 )
        {
            if(npc.getId() == 24041)
                qs.unset(A_LIST);
            addSpawn(CHEST, npc, true, DESPAWN_TIME);
            qs.setCond(12);
        }
        if(qs.getCond() == 10 )
        {
            if(npc.getId() == 24042)
                qs.unset(A_LIST);
            addSpawn(CHEST, npc, true, DESPAWN_TIME);
            qs.setCond(12);
        }
        if(qs.getCond() == 11 )
        {
            if(npc.getId() == 24043)
                qs.unset(A_LIST);
            addSpawn(CHEST, npc, true, DESPAWN_TIME);
            qs.setCond(12);
        }
        return null;
    }
}
