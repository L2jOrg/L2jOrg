package org.l2j.scripts.quests;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.GameObjectsStorage;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.base.ClassLevel;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;

/**
 * Квест Path To Orc Shaman
 *
 * @author Sergey Ibryaev aka Artful
 */
//Edited by Evil_dnk
//Tested
public final class _416_PathToOrcShaman extends Quest {
    //NPC
    private static final int Hestui = 30585;
    private static final int HestuiTotemSpirit = 30592;
    private static final int SeerUmos = 30502;
    private static final int DudaMaraTotemSpirit = 30593;
    //Quest Items
    private static final int FireCharm = 1616;
    private static final int KashaBearPelt = 1617;
    private static final int KashaBladeSpiderHusk = 1618;
    private static final int FieryEgg1st = 1619;
    private static final int HestuiMask = 1620;
    private static final int FieryEgg2nd = 1621;
    private static final int TotemSpiritClaw = 1622;
    private static final int TatarusLetterOfRecommendation = 1623;
    private static final int FlameCharm = 1624;
    private static final int GrizzlyBlood = 1625;
    private static final int BloodCauldron = 1626;
    private static final int SpiritNet = 1627;
    private static final int BoundDurkaSpirit = 1628;
    private static final int DurkaParasite = 1629;
    private static final int TotemSpiritBlood = 1630;
    //Items
    private static final int MaskOfMedium = 1631;
    //MOB
    private static final int KashaBear = 20479;
    private static final int KashaBladeSpider = 20478;
    private static final int ScarletSalamander = 20415;
    private static final int GrizzlyBear = 20335;
    private static final int VenomousSpider = 20038;
    private static final int ArachnidTracker = 20043;
    private static final int QuestMonsterDurkaSpirit = 27056;

    public _416_PathToOrcShaman() {
        super(PARTY_NONE, ONETIME);

        addStartNpc(Hestui);

        addTalkId(HestuiTotemSpirit, SeerUmos, DudaMaraTotemSpirit);

        addKillId(VenomousSpider, ArachnidTracker, QuestMonsterDurkaSpirit, KashaBear, KashaBladeSpider, ScarletSalamander, GrizzlyBear);
        addQuestItem(FireCharm, KashaBearPelt, KashaBladeSpiderHusk, FieryEgg1st, HestuiMask, FieryEgg2nd, TotemSpiritClaw, TatarusLetterOfRecommendation, FlameCharm, GrizzlyBlood, BloodCauldron, SpiritNet, BoundDurkaSpirit, DurkaParasite, TotemSpiritBlood);

        addLevelCheck("tataru_zu_hestui_q0416_03.htm", 19);
        addClassIdCheck("tataru_zu_hestui_q0416_02.htm", 49);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equalsIgnoreCase("tataru_zu_hestui_q0416_06.htm")) {
            st.giveItems(FireCharm, 1);
            st.setCond(1);
        } else if (event.equalsIgnoreCase("hestui_totem_spirit_q0416_03.htm")) {
            st.takeItems(HestuiMask, -1);
            st.takeItems(FieryEgg2nd, -1);
            st.giveItems(TotemSpiritClaw, 1);
            st.setCond(4);
        } else if (event.equalsIgnoreCase("tataru_zu_hestui_q0416_11.htm")) {
            st.takeItems(TotemSpiritClaw, -1);
            st.giveItems(TatarusLetterOfRecommendation, 1);
            st.setCond(5);
        } else if (event.equalsIgnoreCase("dudamara_totem_spirit_q0416_03.htm")) {
            st.takeItems(BloodCauldron, -1);
            st.giveItems(SpiritNet, 1);
            st.setCond(9);
        } else if (event.equalsIgnoreCase("seer_umos_q0416_07.htm")) {
            st.takeItems(TotemSpiritBlood, -1);
            if (st.getPlayer().getClassId().isOfLevel(ClassLevel.NONE)) {
                st.giveItems(MaskOfMedium, 1);
                if (!st.getPlayer().getVarBoolean("prof1")) {
                    st.getPlayer().setVar("prof1", "1", -1);
                    st.addExpAndSp(80314, 5910);
                }
            }
            st.finishQuest();
        }
        if (event.equalsIgnoreCase("QuestMonsterDurkaSpirit_Fail"))
            for (NpcInstance n : GameObjectsStorage.getAllByNpcId(QuestMonsterDurkaSpirit, false))
                n.deleteMe();
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = NO_QUEST_DIALOG;
        int cond = st.getCond();

        switch (npcId) {
            case Hestui:
                if (cond == 0) {
                    if (st.getQuestItemsCount(MaskOfMedium) > 0)
                        htmltext = "seer_umos_q0416_04.htm";
                    else
                        htmltext = "tataru_zu_hestui_q0416_01.htm";
                } else if (cond == 1)
                    htmltext = "tataru_zu_hestui_q0416_07.htm";
                else if (cond == 2) {
                    htmltext = "tataru_zu_hestui_q0416_08.htm";
                    st.takeItems(KashaBearPelt, -1);
                    st.takeItems(KashaBladeSpiderHusk, -1);
                    st.takeItems(FieryEgg1st, -1);
                    st.takeItems(FireCharm, -1);
                    st.giveItems(HestuiMask, 1);
                    st.giveItems(FieryEgg2nd, 1);
                    st.setCond(3);
                } else if (cond == 3)
                    htmltext = "tataru_zu_hestui_q0416_09.htm";
                else if (cond == 4)
                    htmltext = "tataru_zu_hestui_q0416_10.htm";
                else if (cond == 5)
                    htmltext = "tataru_zu_hestui_q0416_12.htm";
                else if (cond > 5)
                    htmltext = "tataru_zu_hestui_q0416_13.htm";
                break;

            case HestuiTotemSpirit:
                if (cond == 3)
                    htmltext = "hestui_totem_spirit_q0416_01.htm";
                else if (cond == 4)
                    htmltext = "hestui_totem_spirit_q0416_04.htm";
                else if (st.getCond() > 4)
                    htmltext = "hestui_totem_spirit_q0416_05.htm";
                break;

            case SeerUmos:
                if (cond == 5) {
                    st.takeItems(TatarusLetterOfRecommendation, -1);
                    st.giveItems(FlameCharm, 1);
                    htmltext = "seer_umos_q0416_01.htm";
                    st.setCond(6);
                } else if (cond == 6)
                    htmltext = "seer_umos_q0416_02.htm";
                else if (cond == 7) {
                    st.takeItems(GrizzlyBlood, -1);
                    st.takeItems(FlameCharm, -1);
                    st.giveItems(BloodCauldron, 1);
                    htmltext = "seer_umos_q0416_03.htm";
                    st.setCond(8);
                } else if (cond == 8)
                    htmltext = "seer_umos_q0416_04.htm";
                else if (cond == 9 || cond == 10)
                    htmltext = "seer_umos_q0416_05.htm";
                else if (cond == 11)
                    htmltext = "seer_umos_q0416_06.htm";
                break;

            case DudaMaraTotemSpirit:
                if (cond == 8)
                    htmltext = "dudamara_totem_spirit_q0416_01.htm";
                else if (cond == 9)
                    htmltext = "dudamara_totem_spirit_q0416_04.htm";
                else if (cond == 10) {
                    st.takeItems(BoundDurkaSpirit, -1);
                    st.giveItems(TotemSpiritBlood, 1);
                    htmltext = "dudamara_totem_spirit_q0416_05.htm";
                    st.setCond(11);
                } else if (cond == 11)
                    htmltext = "dudamara_totem_spirit_q0416_06.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();

        if (cond == 1) {
            if (npcId == KashaBear) {
                st.rollAndGive(KashaBearPelt, 1, 1, 1, 70);
            }
            if (npcId == KashaBladeSpider) {
                st.rollAndGive(KashaBladeSpiderHusk, 1, 1, 1, 70);
            }

            if (npcId == ScarletSalamander) {
                st.rollAndGive(FieryEgg1st, 1, 1, 1, 70);
            }
            if (st.getQuestItemsCount(KashaBearPelt) != 0 && st.getQuestItemsCount(KashaBladeSpiderHusk) != 0 && st.getQuestItemsCount(FieryEgg1st) != 0)
                st.setCond(2);
        } else if (cond == 6) {
            if (npcId == GrizzlyBear) {
                st.rollAndGive(GrizzlyBlood, 1, 1, 3, 70);
                if (st.getQuestItemsCount(GrizzlyBlood) > 2)
                    st.setCond(7);
            }
        } else if (cond == 9) {
            if (npcId == VenomousSpider || npcId == ArachnidTracker) {
                if (st.getQuestItemsCount(DurkaParasite) < 8) {
                    st.giveItems(DurkaParasite, 1, true);
                    st.playSound(SOUND_ITEMGET);
                }
                if (st.getQuestItemsCount(DurkaParasite) >= 8 || st.getQuestItemsCount(DurkaParasite) >= 5 && Rnd.chance(st.getQuestItemsCount(DurkaParasite) * 10)) {
                    if (GameObjectsStorage.getByNpcId(QuestMonsterDurkaSpirit) == null) {
                        st.takeItems(DurkaParasite, -1);
                        st.addSpawn(QuestMonsterDurkaSpirit);
                        st.startQuestTimer("QuestMonsterDurkaSpirit_Fail", 300000);
                    }
                }
            } else if (npcId == QuestMonsterDurkaSpirit) {
                st.cancelQuestTimer("QuestMonsterDurkaSpirit_Fail");

                for (NpcInstance qnpc : GameObjectsStorage.getAllByNpcId(QuestMonsterDurkaSpirit, false))
                    qnpc.deleteMe();
                if (cond == 9) {
                    st.takeItems(SpiritNet, -1);
                    st.takeItems(DurkaParasite, -1);
                    st.giveItems(BoundDurkaSpirit, 1);
                    st.setCond(10);
                }
            }
        }
        return null;
    }

    @Override
    public String checkStartCondition(NpcInstance npc, Player player) {
        if (player.getClassId().getId() == 0x32)
            return "tataru_zu_hestui_q0416_02a.htm";
        return super.checkStartCondition(npc, player);
    }
}