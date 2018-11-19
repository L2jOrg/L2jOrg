package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//Edited by Evil_dnk
//Tested

public final class _409_PathToOracle extends Quest
{
	//npc
	public final int MANUEL = 30293;
	public final int ALLANA = 30424;
	public final int PERRIN = 30428;
	//mobs
	public final int LIZARDMAN_WARRIOR = 27032;
	public final int LIZARDMAN_SCOUT = 27033;
	public final int LIZARDMAN = 27034;
	public final int TAMIL = 27035;
	//items
	public final int CRYSTAL_MEDALLION_ID = 1231;
	public final int MONEY_OF_SWINDLER_ID = 1232;
	public final int DAIRY_OF_ALLANA_ID = 1233;
	public final int LIZARD_CAPTAIN_ORDER_ID = 1234;
	public final int LEAF_OF_ORACLE_ID = 1235;
	public final int HALF_OF_DAIRY_ID = 1236;
	public final int TAMATOS_NECKLACE_ID = 1275;

	public _409_PathToOracle()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(MANUEL);

		addTalkId(ALLANA);
		addTalkId(PERRIN);

		addKillId(LIZARDMAN_WARRIOR);
		addKillId(LIZARDMAN_SCOUT);
		addKillId(LIZARDMAN);
		addKillId(TAMIL);

		addQuestItem(new int[]{
				MONEY_OF_SWINDLER_ID,
				DAIRY_OF_ALLANA_ID,
				LIZARD_CAPTAIN_ORDER_ID,
				CRYSTAL_MEDALLION_ID,
				HALF_OF_DAIRY_ID,
				TAMATOS_NECKLACE_ID
		});

		addLevelCheck("father_manuell_q0409_03.htm", 19);
		addClassIdCheck("father_manuell_q0409_02.htm", 25);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) {
		String htmltext = event;
		if (event.equalsIgnoreCase("1"))
		{
			st.setCond(1);
			st.giveItems(CRYSTAL_MEDALLION_ID, 1);
			htmltext = "father_manuell_q0409_05.htm";
		}
		else if(event.equalsIgnoreCase("allana_q0409_08.htm"))
		{
			st.addSpawn(LIZARDMAN_WARRIOR);
			st.addSpawn(LIZARDMAN_SCOUT);
			st.addSpawn(LIZARDMAN);
			st.setCond(2);
		}
		else if(event.equalsIgnoreCase("30424_1"))
			htmltext = "";
		else if(event.equalsIgnoreCase("30428_1"))
			htmltext = "perrin_q0409_02.htm";
		else if(event.equalsIgnoreCase("30428_2"))
			htmltext = "perrin_q0409_03.htm";
		else if(event.equalsIgnoreCase("30428_3"))
			st.addSpawn(TAMIL);
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = NO_QUEST_DIALOG;
		int npcId = npc.getNpcId();
		int cond = st.getCond();

		switch (npcId)
		{
			case MANUEL:
				if (cond == 0)
				{
					if (st.getQuestItemsCount(LEAF_OF_ORACLE_ID) > 0)
						htmltext = "father_manuell_q0409_04.htm";
					else
						htmltext = "father_manuell_q0409_01.htm";
				}
				else if (cond > 0 && cond < 7)
					htmltext = "father_manuell_q0409_07.htm";
				else if (cond == 7)
				{
					htmltext = "father_manuell_q0409_09.htm";
					st.setCond(8);
				}
				else if (cond == 9)
				{
						htmltext = "father_manuell_q0409_08.htm";
						st.takeItems(MONEY_OF_SWINDLER_ID, 1);
						st.takeItems(DAIRY_OF_ALLANA_ID, -1);
						st.takeItems(LIZARD_CAPTAIN_ORDER_ID, -1);
						st.takeItems(CRYSTAL_MEDALLION_ID, -1);
						if (st.getPlayer().getClassId().isOfLevel(ClassLevel.NONE))
						{
							st.giveItems(LEAF_OF_ORACLE_ID, 1);
							if (!st.getPlayer().getVarBoolean("prof1"))
							{
								st.getPlayer().setVar("prof1", "1", -1);
								st.addExpAndSp(80314, 5910);
							}
						}
						st.finishQuest();
					}
			break;

			case ALLANA:
				if (cond == 1)
					htmltext = "allana_q0409_01.htm";
				else if (cond == 2)
					htmltext = "allana_q0409_05.htm";
				else if (cond == 3)
				{
					htmltext = "allana_q0409_02.htm";
					st.giveItems(HALF_OF_DAIRY_ID, 1);
					st.setCond(4);
				}
				else if (cond == 4)
					htmltext = "allana_q0409_03.htm";
				else if (cond == 6)
				{
					htmltext = "allana_q0409_06.htm";
					st.takeItems(HALF_OF_DAIRY_ID, -1);
					st.giveItems(DAIRY_OF_ALLANA_ID, 1);
					st.setCond(7);
				}
				else if (cond == 8)
				{
					st.setCond(9);
					htmltext = "allana_q0409_04.htm";
				}
			break;

			case PERRIN:
				if (cond > 1 && cond < 4)
					htmltext = "perrin_q0409_06.htm";
				else if (cond == 4)
					htmltext = "perrin_q0409_01.htm";
				else if (cond == 5)
					{
						htmltext = "perrin_q0409_04.htm";
						st.takeItems(TAMATOS_NECKLACE_ID, -1);
						st.giveItems(MONEY_OF_SWINDLER_ID, 1);
						st.setCond(6);
					}
					else if (cond == 6)
						htmltext = "perrin_q0409_05.htm";
            break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId != TAMIL)
		{
			if(cond == 2)
			{
				st.giveItems(LIZARD_CAPTAIN_ORDER_ID, 1);
				st.setCond(3);
			}
		}
		else if(npcId == TAMIL)
			if(cond == 4)
			{
				st.giveItems(TAMATOS_NECKLACE_ID, 1);
				st.setCond(5);
			}
		return null;
	}

	@Override
	public String checkStartCondition(NpcInstance npc, Player player)
	{
		if(player.getClassId().getId() == 0x1d)
			return "father_manuell_q0409_02a.htm";
		return super.checkStartCondition(npc, player);
	}
}