package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * Квест на вторую профессию Trial Of Seeker
 *
 * @author Sergey Ibryaev aka Artful
 */
//Edited by Evil_dnk

public final class _213_TrialOfSeeker extends Quest
{
	//NPC
	private static final int Dufner = 30106;
	private static final int Terry = 30064;
	private static final int Viktor = 30684;
	private static final int Marina = 30715;
	private static final int Brunon = 30526;
	//Quest Item
	private static final int DufnersLetter = 2647;
	private static final int Terrys1stOrder = 2648;
	private static final int Terrys2ndOrder = 2649;
	private static final int TerrysLetter = 2650;
	private static final int ViktorsLetter = 2651;
	private static final int HawkeyesLetter = 2652;
	private static final int MysteriousRunestone = 2653;
	private static final int OlMahumRunestone = 2654;
	private static final int TurekRunestone = 2655;
	private static final int AntRunestone = 2656;
	private static final int TurakBugbearRunestone = 2657;
	private static final int TerrysBox = 2658;
	private static final int ViktorsRequest = 2659;
	private static final int MedusaScales = 2660;
	private static final int ShilensRunestone = 2661;
	private static final int AnalysisRequest = 2662;
	private static final int MarinasLetter = 2663;
	private static final int ExperimentTools = 2664;
	private static final int AnalysisResult = 2665;
	private static final int Terrys3rdOrder = 2666;
	private static final int ListOfHost = 2667;
	private static final int AbyssRunestone1 = 2668;
	private static final int AbyssRunestone2 = 2669;
	private static final int AbyssRunestone3 = 2670;
	private static final int AbyssRunestone4 = 2671;
	private static final int TerrysReport = 2672;
	private static final int MarkofSeeker = 2673;
	//MOBs
	private static final int NeerGhoulBerserker = 20198;
	private static final int OlMahumCaptain = 20211;
	private static final int TurekOrcWarlord = 20495;
	private static final int AntCaptain = 20080;
	private static final int TurakBugbearWarrior = 20249;
	private static final int Medusa = 20158;
	private static final int MarshStakatoDrone = 20234;
	private static final int BrekaOrcOverlord = 20270;
	private static final int AntWarriorCaptain = 20088;
	private static final int LetoLizardmanWarrior = 20580;
	//Drop Cond
	//# [COND, NEWCOND, ID, REQUIRED, ITEM, NEED_COUNT, CHANCE, DROP]
	private static final int[][] DROPLIST_COND = {
			{
					2,
					3,
					NeerGhoulBerserker,
					Terrys1stOrder,
					MysteriousRunestone,
					1,
					10,
					1
			},
			{
					4,
					0,
					OlMahumCaptain,
					Terrys2ndOrder,
					OlMahumRunestone,
					1,
					20,
					1
			},
			{
					4,
					0,
					TurekOrcWarlord,
					Terrys2ndOrder,
					TurekRunestone,
					1,
					20,
					1
			},
			{
					4,
					0,
					AntCaptain,
					Terrys2ndOrder,
					AntRunestone,
					1,
					20,
					1
			},
			{
					4,
					0,
					TurakBugbearWarrior,
					Terrys2ndOrder,
					TurakBugbearRunestone,
					1,
					20,
					1
			},
			{
					9,
					10,
					Medusa,
					ViktorsRequest,
					MedusaScales,
					10,
					30,
					1
			},
			{
					15,
					0,
					MarshStakatoDrone,
					ListOfHost,
					AbyssRunestone1,
					1,
					25,
					1
			},
			{
					15,
					0,
					BrekaOrcOverlord,
					ListOfHost,
					AbyssRunestone2,
					1,
					25,
					1
			},
			{
					15,
					0,
					AntWarriorCaptain,
					ListOfHost,
					AbyssRunestone3,
					1,
					25,
					1
			},
			{
					15,
					0,
					LetoLizardmanWarrior,
					ListOfHost,
					AbyssRunestone4,
					1,
					25,
					1
			}
	};

	public _213_TrialOfSeeker()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(Dufner);

		addTalkId(Dufner);
		addTalkId(Terry);
		addTalkId(Viktor);
		addTalkId(Marina);
		addTalkId(Brunon);
		//Mob Drop
		for(int i = 0; i < DROPLIST_COND.length; i++)
			addKillId(DROPLIST_COND[i][2]);

		addQuestItem(new int[]{
				DufnersLetter,
				Terrys1stOrder,
				Terrys2ndOrder,
				TerrysLetter,
				TerrysBox,
				ViktorsLetter,
				ViktorsRequest,
				HawkeyesLetter,
				ShilensRunestone,
				AnalysisRequest,
				MarinasLetter,
				ExperimentTools,
				AnalysisResult,
				ListOfHost,
				Terrys3rdOrder,
				TerrysReport,
				MysteriousRunestone,
				OlMahumRunestone,
				TurekRunestone,
				AntRunestone,
				TurakBugbearRunestone,
				MedusaScales,
				AbyssRunestone1,
				AbyssRunestone2,
				AbyssRunestone3,
				AbyssRunestone4
		});

		addClassIdCheck("dufner_q0213_02.htm", 7, 22, 35);
		addLevelCheck("dufner_q0213_00.htm", 35);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("dufner_q0213_05a.htm"))
		{
			st.setCond(1);
			st.giveItems(DufnersLetter, 1);
		}
		else if(event.equalsIgnoreCase("terry_q0213_03.htm"))
		{
			st.giveItems(Terrys1stOrder, 1);
			st.takeItems(DufnersLetter, -1);
			st.setCond(2);
		}
		else if(event.equalsIgnoreCase("terry_q0213_07.htm"))
		{
			st.takeItems(Terrys1stOrder, -1);
			st.takeItems(MysteriousRunestone, -1);
			st.giveItems(Terrys2ndOrder, 1);
			st.setCond(4);
		}
		else if(event.equalsIgnoreCase("terry_q0213_10.htm"))
		{
			st.takeItems(Terrys2ndOrder, -1);
			st.takeItems(OlMahumRunestone, -1);
			st.takeItems(TurekRunestone, -1);
			st.takeItems(AntRunestone, -1);
			st.takeItems(TurakBugbearRunestone, -1);
			st.giveItems(TerrysLetter, 1);
			st.giveItems(TerrysBox, 1);
			st.setCond(6);
		}
		else if(event.equalsIgnoreCase("trader_viktor_q0213_05.htm"))
		{
			st.takeItems(TerrysLetter, -1);
			st.giveItems(ViktorsLetter, 1);
			st.setCond(7);
		}
		else if(event.equalsIgnoreCase("trader_viktor_q0213_11.htm"))
		{
			st.takeItems(TerrysLetter, -1);
			st.takeItems(TerrysBox, -1);
			st.takeItems(HawkeyesLetter, -1);
			st.giveItems(ViktorsRequest, 1);
			st.setCond(9);
		}
		else if(event.equalsIgnoreCase("trader_viktor_q0213_15.htm"))
		{
			st.takeItems(ViktorsRequest, -1);
			st.takeItems(MedusaScales, -1);
			st.giveItems(ShilensRunestone, 1);
			st.giveItems(AnalysisRequest, 1);
			st.setCond(11);
		}
		else if(event.equalsIgnoreCase("magister_marina_q0213_02.htm"))
		{
			st.takeItems(ShilensRunestone, -1);
			st.takeItems(AnalysisRequest, -1);
			st.giveItems(MarinasLetter, 1);
			st.setCond(12);
		}
		else if(event.equalsIgnoreCase("magister_marina_q0213_05.htm"))
		{
			st.takeItems(ExperimentTools, 1);
			st.giveItems(AnalysisResult, 1);
			st.setCond(14);
		}
		else if(event.equalsIgnoreCase("terry_q0213_18.htm"))
			{
				htmltext = "terry_q0213_18.htm";
				st.giveItems(ListOfHost, 1);
				st.takeItems(AnalysisResult, -1);
				st.setCond(15);
			}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		if(st.getQuestItemsCount(MarkofSeeker) > 0)
		{
			return COMPLETED_DIALOG;
		}

		int npcId = npc.getNpcId();
		String htmltext = NO_QUEST_DIALOG;
		int cond = st.getCond();
		switch (npcId){
			case Dufner:
				if(cond == 0)
					htmltext = "dufner_q0213_03.htm";
				else if(cond == 1)
					htmltext = "dufner_q0213_06.htm";
				else if (cond > 1 && cond < 17)
					htmltext = "dufner_q0213_07.htm";
				else if(cond == 17)
					{
						if(!st.getPlayer().getVarBoolean("prof2.1"))
						{
							st.addExpAndSp(116100, 0);
							st.getPlayer().setVar("prof2.1", "1", -1);
						}
						htmltext = "dufner_q0213_08.htm";
						st.takeItems(TerrysReport, -1);
						st.giveItems(MarkofSeeker, 1);
						st.finishQuest();
					}
			break;

			case Terry:
				if(cond == 1)
					htmltext = "terry_q0213_01.htm";
				else if(cond == 2)
					htmltext = "terry_q0213_04.htm";
				else if(cond == 3)
					htmltext = "terry_q0213_05.htm";
				else if(cond == 4)
					htmltext = "terry_q0213_07.htm";
				else if(cond == 5)
					htmltext = "terry_q0213_09.htm";
				else if(cond == 6)
					htmltext = "terry_q0213_11.htm";
				else if(cond == 7)
				{
					st.takeItems(ViktorsLetter, -1);
					st.giveItems(HawkeyesLetter, 1);
					htmltext = "terry_q0213_12.htm";
					st.setCond(8);
				}
				else if(cond == 8)
					htmltext = "terry_q0213_13.htm";
				else if(cond > 8 && cond < 14)
					htmltext = "terry_q0213_14.htm";
				else if(cond == 14)
				{
					if(st.getPlayer().getLevel() < 36)
						htmltext = "terry_q0213_20.htm";
					else
						htmltext = "terry_q0213_15.htm";
				}

				else if(cond == 15)
					htmltext = "terry_q0213_21.htm";
				else if(cond == 16)
					{
						htmltext = "terry_q0213_23.htm";
						st.takeItems(ListOfHost, -1);
						st.takeItems(AbyssRunestone1, -1);
						st.takeItems(AbyssRunestone2, -1);
						st.takeItems(AbyssRunestone3, -1);
						st.takeItems(AbyssRunestone4, -1);
						st.giveItems(TerrysReport, 1);
						st.setCond(17);
					}
			break;

			case Viktor:
				if(cond == 6)
					htmltext = "trader_viktor_q0213_01.htm";
				else if(cond == 8)
					htmltext = "trader_viktor_q0213_12.htm";
				else if(cond == 9)
					htmltext = "trader_viktor_q0213_13.htm";
				else if(cond == 10)
					htmltext = "trader_viktor_q0213_14.htm";
				break;

			case Marina:
				if(cond == 11)
					htmltext = "magister_marina_q0213_01.htm";
				else if(cond == 12)
					htmltext = "magister_marina_q0213_03.htm";
				else if(cond == 13)
					htmltext = "magister_marina_q0213_04.htm";
				else if(cond > 13)
					htmltext = "magister_marina_q0213_06.htm";
				break;

			case Brunon:
				if(cond == 12)
				{
					htmltext = "blacksmith_bronp_q0213_01.htm";
					st.takeItems(MarinasLetter, 1);
					st.giveItems(ExperimentTools, 1);
					st.setCond(13);
				}
				else if(cond == 13)
					htmltext = "blacksmith_bronp_q0213_02.htm";
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		for(int i = 0; i < DROPLIST_COND.length; i++)
			if(cond == DROPLIST_COND[i][0] && npcId == DROPLIST_COND[i][2])
				if(DROPLIST_COND[i][3] == 0 || st.getQuestItemsCount(DROPLIST_COND[i][3]) > 0)
					if(DROPLIST_COND[i][5] == 0)
						st.rollAndGive(DROPLIST_COND[i][4], DROPLIST_COND[i][7], DROPLIST_COND[i][6]);
					else if(st.rollAndGive(DROPLIST_COND[i][4], DROPLIST_COND[i][7], DROPLIST_COND[i][7], DROPLIST_COND[i][5], DROPLIST_COND[i][6]))
						if(DROPLIST_COND[i][1] != cond && DROPLIST_COND[i][1] != 0)
						{
							st.setCond(Integer.valueOf(DROPLIST_COND[i][1]));
						}
		if(cond == 4 && st.getQuestItemsCount(OlMahumRunestone) != 0 && st.getQuestItemsCount(TurekRunestone) != 0 && st.getQuestItemsCount(AntRunestone) != 0 && st.getQuestItemsCount(TurakBugbearRunestone) != 0)
		{
			st.setCond(5);
		}
		else if(cond == 15 && st.getQuestItemsCount(AbyssRunestone1) != 0 && st.getQuestItemsCount(AbyssRunestone2) != 0 && st.getQuestItemsCount(AbyssRunestone3) != 0 && st.getQuestItemsCount(AbyssRunestone4) != 0)
		{
			st.setCond(16);
		}
		return null;
	}
}