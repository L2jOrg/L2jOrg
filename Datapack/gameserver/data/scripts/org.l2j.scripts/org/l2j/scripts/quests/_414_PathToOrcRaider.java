package org.l2j.scripts.quests;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.base.ClassLevel;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;

//Edited by Evil_dnk
//Tested

public final class _414_PathToOrcRaider extends Quest
{
	//org.l2j.scripts.npc
	public final int KARUKIA = 30570;
	public final int KASMAN = 30501;
	//mobs
	public final int GOBLIN_TOMB_RAIDER_LEADER = 20320;
	public final int KURUKA_RATMAN_LEADER = 27045;
	public final int UMBAR_ORC = 27054;
	//items
	public final int GREEN_BLOOD = 1578;
	public final int GOBLIN_DWELLING_MAP = 1579;
	public final int KURUKA_RATMAN_TOOTH = 1580;
	public final int BETRAYER_UMBAR_REPORT = 1589;
	public final int HEAD_OF_BETRAYER = 1591;
	public final int TIMORA_ORCS_HEAD = 8544;
	public final int MARK_OF_RAIDER = 1592;

	public _414_PathToOrcRaider()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(KARUKIA);

		addTalkId(KASMAN);

		addKillId(GOBLIN_TOMB_RAIDER_LEADER);
		addKillId(KURUKA_RATMAN_LEADER);
		addKillId(UMBAR_ORC);

		addQuestItem(KURUKA_RATMAN_TOOTH);
		addQuestItem(GOBLIN_DWELLING_MAP);
		addQuestItem(GREEN_BLOOD);
		addQuestItem(HEAD_OF_BETRAYER);
		addQuestItem(BETRAYER_UMBAR_REPORT);
		addQuestItem(TIMORA_ORCS_HEAD);

		addLevelCheck("prefect_karukia_q0414_02.htm", 19);
		addClassIdCheck("prefect_karukia_q0414_03.htm", 44);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("prefect_karukia_q0414_05.htm"))
		{
			st.setCond(1);
			st.giveItems(GOBLIN_DWELLING_MAP, 1);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = NO_QUEST_DIALOG;
		int npcId = npc.getNpcId();
		int cond = st.getCond();

		switch (npcId) {
			case KARUKIA:
				if (cond == 0)
				{
					if (st.getQuestItemsCount(MARK_OF_RAIDER) > 0)
						htmltext = "prefect_karukia_q0414_04.htm";
					else
						htmltext = "prefect_karukia_q0414_01.htm";
				}
				else if (cond == 1)
					htmltext = "prefect_karukia_q0414_06.htm";
				else if (cond == 2)
				{
					st.takeItems(KURUKA_RATMAN_TOOTH, -1);
					st.takeItems(GOBLIN_DWELLING_MAP, -1);
					st.giveItems(BETRAYER_UMBAR_REPORT, 1);
					st.addRadar(-74490, 83275, -3374);
					st.setCond(3);
					htmltext = "prefect_karukia_q0414_07.htm";
				}
				else if (cond == 3)
					htmltext = "prefect_karukia_q0414_08.htm";
				else if (cond == 4)
					htmltext = "prefect_karukia_q0414_09.htm";
			break;

			case KASMAN:
				if (cond == 3)
				{
					if (st.haveQuestItem(HEAD_OF_BETRAYER))
						htmltext = "prefect_kasman_q0414_02.htm";
					else
						htmltext = "prefect_kasman_q0414_01.htm";
				}
				else if (cond == 4)
				{
					htmltext = "prefect_kasman_q0414_03.htm";
					if (st.getPlayer().getClassId().isOfLevel(ClassLevel.NONE))
					{
						st.giveItems(MARK_OF_RAIDER, 1);
						if (!st.getPlayer().getVarBoolean("prof1"))
						{
							st.getPlayer().setVar("prof1", "1", -1);
							st.addExpAndSp(80314, 5087);
						}
					}
					st.finishQuest();
				}
			break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == GOBLIN_TOMB_RAIDER_LEADER && cond == 1)
		{
			if (st.getQuestItemsCount(GOBLIN_DWELLING_MAP) == 1 && st.getQuestItemsCount(KURUKA_RATMAN_TOOTH) < 10 && st.getQuestItemsCount(GREEN_BLOOD) < 40)
			{
				if (st.getQuestItemsCount(GREEN_BLOOD) > 20 && Rnd.chance((st.getQuestItemsCount(GREEN_BLOOD) - 20) * 5))
				{
					st.takeItems(GREEN_BLOOD, -1);
					st.addSpawn(KURUKA_RATMAN_LEADER);
				}
				else
				{
					st.giveItems(GREEN_BLOOD, 1, true);
					st.playSound(SOUND_ITEMGET);
				}
			}
		}
		else if(npcId == KURUKA_RATMAN_LEADER && cond == 1)
		{
			if(st.getQuestItemsCount(GOBLIN_DWELLING_MAP) > 0)
			{
				st.rollAndGive(KURUKA_RATMAN_TOOTH, 1, 1, 10, 100);
				if(st.getQuestItemsCount(KURUKA_RATMAN_TOOTH) > 9)
					st.setCond(2);
			}
		}
		else if(npcId == UMBAR_ORC && cond == 3)
		{
				st.rollAndGive(HEAD_OF_BETRAYER, 1, 1, 2, 100);
				if(st.getQuestItemsCount(HEAD_OF_BETRAYER) > 1)
				{
					st.setCond(4);
					st.addRadar(-80450, 153410, -3175);
				}
		}
		return null;
	}

	@Override
	public String checkStartCondition(NpcInstance npc, Player player)
	{
		if(player.getClassId().getId() == 0x2d)
			return "prefect_karukia_q0414_02a.htm";
		return super.checkStartCondition(npc, player);
	}
}