package org.l2j.scripts.quests;

import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;

public final class _306_CrystalOfFireice extends Quest {
    //NPCs
    private static final int Katerina = 30004;
    //Mobs
    private static int Salamander = 20109;
    private static int Undine = 20110;
    private static int Salamander_Elder = 20112;
    private static int Undine_Elder = 20113;
    private static int Salamander_Noble = 20114;
    private static int Undine_Noble = 20115;
    //Quest Items
    private static int Flame_Shard = 1020;
    private static int Ice_Shard = 1021;
    //Chances
    private static int Chance = 30;
    private static int Elder_Chance = 40;
    private static int Noble_Chance = 50;

    public _306_CrystalOfFireice() {
        super(PARTY_NONE, REPEATABLE);
        addStartNpc(Katerina);
        addKillId(Salamander);
        addKillId(Undine);
        addKillId(Salamander_Elder);
        addKillId(Undine_Elder);
        addKillId(Salamander_Noble);
        addKillId(Undine_Noble);
        addQuestItem(Flame_Shard);
        addQuestItem(Ice_Shard);

        addLevelCheck("katrine_q0306_02.htm", 17/*, 23*/);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equalsIgnoreCase("katrine_q0306_04.htm")) {
            st.setCond(1);
        } else if (event.equalsIgnoreCase("katrine_q0306_08.htm")) {
            st.finishQuest();
        }

        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int npcId = npc.getNpcId();
        int cond = st.getCond();

        switch (npcId) {
            case Katerina:
                if (cond == 0)
                    htmltext = "katrine_q0306_03.htm";
                else if (cond == 1) {
                    long Shrads_count = st.getQuestItemsCount(Flame_Shard) + st.getQuestItemsCount(Ice_Shard);
                    long Reward = Shrads_count * 15;

                    if (Reward > 0) {
                        htmltext = "katrine_q0306_07.htm";
                        st.takeItems(Flame_Shard, -1);
                        st.takeItems(Ice_Shard, -1);
                        st.giveItems(ADENA_ID, Reward, 1000);
                    } else
                        htmltext = "katrine_q0306_05.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState qs) {
        int npcId = npc.getNpcId();
        if (qs.getCond() == 1) {
            if (npcId == Salamander || npcId == Undine)
                qs.rollAndGive(npcId == Salamander ? Flame_Shard : Ice_Shard, 1, Chance);
            else if (npcId == Salamander_Elder || npcId == Undine_Elder)
                qs.rollAndGive(npcId == Salamander_Elder ? Flame_Shard : Ice_Shard, 1, Elder_Chance);
            else if (npcId == Salamander_Noble || npcId == Undine_Noble)
                qs.rollAndGive(npcId == Salamander_Noble ? Flame_Shard : Ice_Shard, 1, Noble_Chance);
        }
        return null;
    }
}