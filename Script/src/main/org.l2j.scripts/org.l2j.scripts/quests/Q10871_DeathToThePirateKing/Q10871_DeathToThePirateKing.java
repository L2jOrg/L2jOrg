package org.l2j.scripts.quests.Q10871_DeathToThePirateKing;

import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;

import static java.util.Objects.isNull;

public class Q10871_DeathToThePirateKing extends Quest
{
    private static final int EIGIS = 34017;
    private static final int ZAKEN = 29022;
    private static final int ZAKEN_BLOOD = 90754;

    public Q10871_DeathToThePirateKing()
    {
        super(10871);

        addStartNpc(EIGIS);
        addTalkId(EIGIS);

        addKillId(ZAKEN);
        addItemTalkId(ZAKEN_BLOOD);
        addCondLevel(70, 90, "no_lvl.html");
    }

    @Override
    public boolean checkPartyMember(Player member, Npc npc)
    {
        final QuestState qs = getQuestState(member, false);
        return ((qs != null) && qs.isStarted());
    }

    @Override
    public String onAdvEvent(String event, Npc npc, Player player) {
        final QuestState qs = getQuestState(player, false);
        if (isNull(qs)) {
            return null;
        }

        String htmltext = null;
        if(event.equalsIgnoreCase("starting_now.htm"))
        {
            qs.startQuest();
            qs.setCond(1);
            htmltext = event;
        }
        else if(event.equalsIgnoreCase("finish_now.htm"))
        {
            addExpAndSp(player, 900000, 27000);
            giveItems(player, 21713, 1);	//cloak
            takeItems(player, ZAKEN_BLOOD, 1);
            qs.exitQuest(false, true);
        }
        return htmltext;
    }

    @Override
    public String onTalk(Npc npc, Player talker) {
        final QuestState qs = getQuestState(talker, true);
        String htmltext = getNoQuestMsg(talker);

        if(isNull(qs)) {
            return htmltext;
        }

        int npcId = npc.getId();
        int cond = qs.getCond();

        if(npcId == EIGIS)
        {
            if(cond == 0)
            {
                htmltext = "start.htm";
            }
            else if(cond == 1)
            {
                htmltext = "not_finish.htm";
            }
            else if(cond == 2)
            {
                htmltext = "finished.htm";
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
        if(qs.getCond() == 1)
        {
            qs.setCond(2);
            giveItems(killer, ZAKEN_BLOOD, 1);
        }
        return null;
    }

}
