package quests;

import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * Квест на вторую профессию Test Of Magus
 *
 * @author Sergey Ibryaev aka Artful
 */

public final class _228_TestOfMagus extends Quest
{
	//NPC
	private static final int Rukal = 30629;
	private static final int Parina = 30391;
	private static final int Casian = 30612;
	private static final int Salamander = 30411;
	private static final int Sylph = 30412;
	private static final int Undine = 30413;
	private static final int Snake = 30409;
	//Quest Items
	private static final int RukalsLetter = 2841;
	private static final int ParinasLetter = 2842;
	private static final int LilacCharm = 2843;
	private static final int GoldenSeed1st = 2844;
	private static final int GoldenSeed2st = 2845;
	private static final int GoldenSeed3st = 2846;
	private static final int ScoreOfElements = 2847;
	private static final int ToneOfWater = 2856;
	private static final int ToneOfFire = 2857;
	private static final int ToneOfWind = 2858;
	private static final int ToneOfEarth = 2859;
	private static final int UndineCharm = 2862;
	private static final int DazzlingDrop = 2848;
	private static final int SalamanderCharm = 2860;
	private static final int FlameCrystal = 2849;
	private static final int SylphCharm = 2861;
	private static final int HarpysFeather = 2850;
	private static final int WyrmsWingbone = 2851;
	private static final int WindsusMane = 2852;
	private static final int SerpentCharm = 2863;
	private static final int EnchantedMonsterEyeShell = 2853;
	private static final int EnchantedStoneGolemPowder = 2854;
	private static final int EnchantedIronGolemScrap = 2855;
	//Items
	private static final int MarkOfMagus = 2840;
	//MOB
	private static final int SingingFlowerPhantasm = 27095;
	private static final int SingingFlowerNightmare = 27096;
	private static final int SingingFlowerDarkling = 27097;
	private static final int Harpy = 20145;
	private static final int Wyrm = 20176;
	private static final int Windsus = 20553;
	private static final int EnchantedMonstereye = 20564;
	private static final int EnchantedStoneGolem = 20565;
	private static final int EnchantedIronGolem = 20566;
	private static final int QuestMonsterGhostFire = 27098;
	private static final int MarshStakatoWorker = 20230;
	private static final int ToadLord = 20231;
	private static final int MarshStakato = 20157;
	private static final int MarshStakatoSoldier = 20232;
	private static final int MarshStakatoDrone = 20234;

	//Drop Cond
	//# [COND, NEWCOND, ID, REQUIRED, ITEM, NEED_COUNT, CHANCE, DROP]
	private static final int[][] DROPLIST_COND = {
			{
					3,
					0,
					SingingFlowerPhantasm,
					LilacCharm,
					GoldenSeed1st,
					10,
					100,
					1
			},
			{
					3,
					0,
					SingingFlowerNightmare,
					LilacCharm,
					GoldenSeed2st,
					10,
					100,
					1
			},
			{
					3,
					0,
					SingingFlowerDarkling,
					LilacCharm,
					GoldenSeed3st,
					10,
					100,
					1
			},
			{
					5,
					0,
					Harpy,
					SylphCharm,
					HarpysFeather,
					20,
					50,
					2
			},
			{
					5,
					0,
					Wyrm,
					SylphCharm,
					WyrmsWingbone,
					10,
					50,
					2
			},
			{
					5,
					0,
					Windsus,
					SylphCharm,
					WindsusMane,
					10,
					50,
					2
			},
			{
					5,
					0,
					EnchantedMonstereye,
					SerpentCharm,
					EnchantedMonsterEyeShell,
					10,
					100,
					2
			},
			{
					5,
					0,
					EnchantedStoneGolem,
					SerpentCharm,
					EnchantedStoneGolemPowder,
					10,
					100,
					2
			},
			{
					5,
					0,
					EnchantedIronGolem,
					SerpentCharm,
					EnchantedIronGolemScrap,
					10,
					100,
					2
			},
			{
					5,
					0,
					QuestMonsterGhostFire,
					SalamanderCharm,
					FlameCrystal,
					5,
					50,
					1
			},
			{
					5,
					0,
					MarshStakatoWorker,
					UndineCharm,
					DazzlingDrop,
					20,
					30,
					2
			},
			{
					5,
					0,
					ToadLord,
					UndineCharm,
					DazzlingDrop,
					20,
					30,
					2
			},
			{
					5,
					0,
					MarshStakato,
					UndineCharm,
					DazzlingDrop,
					20,
					30,
					2
			},
			{
					5,
					0,
					MarshStakatoSoldier,
					UndineCharm,
					DazzlingDrop,
					20,
					40,
					2
			},
			{
					5,
					0,
					MarshStakatoDrone,
					UndineCharm,
					DazzlingDrop,
					20,
					50,
					2
			}
	};

	public _228_TestOfMagus()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(Rukal);

		addTalkId(Parina);
		addTalkId(Casian);
		addTalkId(Sylph);
		addTalkId(Snake);
		addTalkId(Undine);
		addTalkId(Salamander);

		for(int i = 0; i < DROPLIST_COND.length; i++)
			addKillId(DROPLIST_COND[i][2]);

		addQuestItem(new int[]{
				RukalsLetter,
				ParinasLetter,
				LilacCharm,
				ToneOfWind,
				SylphCharm,
				SerpentCharm,
				ToneOfEarth,
				UndineCharm,
				ToneOfFire,
				SalamanderCharm,
				ToneOfWater,
				ScoreOfElements,
				GoldenSeed1st,
				GoldenSeed2st,
				GoldenSeed3st,
				HarpysFeather,
				WyrmsWingbone,
				WindsusMane,
				EnchantedMonsterEyeShell,
				EnchantedStoneGolemPowder,
				EnchantedIronGolemScrap,
				FlameCrystal,
				DazzlingDrop
		});

		addClassIdCheck("30629-01.htm", 11, 26, 39);
		addLevelCheck("30629-02.htm", 39);
	}

	public void checkBooks(QuestState st)
	{
		if(st.getQuestItemsCount(ToneOfWater) != 0 && st.getQuestItemsCount(ToneOfFire) != 0 && st.getQuestItemsCount(ToneOfWind) != 0 && st.getQuestItemsCount(ToneOfEarth) != 0)
		{
			st.setCond(6);
		}
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("30629-04.htm"))
		{
			st.giveItems(RukalsLetter, 1);
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("30391-02.htm"))
		{
			st.takeItems(RukalsLetter, -1);
			st.giveItems(ParinasLetter, 1);
			st.setCond(2);
		}
		else if(event.equalsIgnoreCase("30612-02.htm"))
		{
			st.takeItems(ParinasLetter, -1);
			st.giveItems(LilacCharm, 1);
			st.setCond(3);
		}
		else if(event.equalsIgnoreCase("30629-10.htm"))
		{
			st.takeItems(LilacCharm, -1);
			st.takeItems(GoldenSeed1st, -1);
			st.takeItems(GoldenSeed2st, -1);
			st.takeItems(GoldenSeed3st, -1);
			st.giveItems(ScoreOfElements, 1);
			st.setCond(5);
		}
		else if(event.equalsIgnoreCase("30412-02.htm"))
		{
			st.giveItems(SylphCharm, 1);
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("30409-03.htm"))
		{
			st.giveItems(SerpentCharm, 1);
			st.playSound(SOUND_MIDDLE);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		if(st.getQuestItemsCount(MarkOfMagus) != 0)
		{
			return COMPLETED_DIALOG;
		}

		int npcId = npc.getNpcId();
		String htmltext = NO_QUEST_DIALOG;
		int cond = st.getCond();

		switch (npcId)
		{
			case Rukal:
				if(cond == 0)
					htmltext = "30629-03.htm";
				else if(cond == 1)
					htmltext = "30629-05.htm";
				else if(cond == 2)
					htmltext = "30629-06.htm";
				else if(cond == 3)
					htmltext = "30629-07.htm";
				else if(cond == 4)
					htmltext = "30629-08.htm";
				else if(cond == 5)
					htmltext = "30629-11.htm";
				else if(cond == 6)
				{
					st.takeItems(ScoreOfElements, -1);
					st.takeItems(ToneOfWater, -1);
					st.takeItems(ToneOfFire, -1);
					st.takeItems(ToneOfWind, -1);
					st.takeItems(ToneOfEarth, -1);
					st.giveItems(MarkOfMagus, 1);
					htmltext = "30629-12.htm";
					if(!st.getPlayer().getVarBoolean("prof2.3"))
					{
						st.addExpAndSp(270000, 0);
						st.getPlayer().setVar("prof2.3", "1", -1);
					}
					st.finishQuest();
				}
			break;

			case Parina:
				if(cond == 1)
					htmltext = "30391-01.htm";
				else if(cond == 2)
					htmltext = "30391-03.htm";
				else if(cond == 3 || cond == 4)
					htmltext = "30391-04.htm";
				else if(cond >= 5)
					htmltext = "30391-05.htm";
			break;

			case Casian:
				if(cond == 2)
					htmltext = "30612-01.htm";
				else if(cond == 3)
					htmltext = "30612-03.htm";
				else if(cond == 4)
					htmltext = "30612-04.htm";
				else if(cond >= 5)
					htmltext = "30612-05.htm";
			break;

			case Salamander:
				if(cond == 5)
				{
					if(st.getQuestItemsCount(ToneOfFire) == 0)
					{
						if(st.getQuestItemsCount(SalamanderCharm) == 0)
						{
							htmltext = "30411-01.htm";
							st.giveItems(SalamanderCharm, 1);
							st.playSound(SOUND_MIDDLE);
						}
						else if(st.getQuestItemsCount(FlameCrystal) < 5)
							htmltext = "30411-02.htm";
						else
						{
							st.takeItems(SalamanderCharm, -1);
							st.takeItems(FlameCrystal, -1);
							st.giveItems(ToneOfFire, 1);
							htmltext = "30411-03.htm";
							checkBooks(st);
							st.playSound(SOUND_MIDDLE);
						}
					}
					else
						htmltext = "30411-04.htm";
				}
			break;

			case Sylph:
				if(cond == 5)
				{
					if(st.getQuestItemsCount(ToneOfWind) == 0)
					{
						if(st.getQuestItemsCount(SylphCharm) == 0)
							htmltext = "30412-01.htm";
						else if(st.getQuestItemsCount(HarpysFeather) < 20 || st.getQuestItemsCount(WyrmsWingbone) < 10 || st.getQuestItemsCount(WindsusMane) < 10)
							htmltext = "30412-03.htm";
						else
						{
							st.takeItems(SylphCharm, -1);
							st.takeItems(HarpysFeather, -1);
							st.takeItems(WyrmsWingbone, -1);
							st.takeItems(WindsusMane, -1);
							st.giveItems(ToneOfWind, 1);
							htmltext = "30412-04.htm";
							checkBooks(st);
							st.playSound(SOUND_MIDDLE);
						}
					}
					else
						htmltext = "30412-05.htm";
				}
			break;

			case Snake:
				if(cond == 5)
				{
					if(st.getQuestItemsCount(ToneOfEarth) == 0)
					{
						if(st.getQuestItemsCount(SerpentCharm) == 0)
							htmltext = "30409-01.htm";
						else if(st.getQuestItemsCount(EnchantedMonsterEyeShell) < 10 || st.getQuestItemsCount(EnchantedStoneGolemPowder) < 10 || st.getQuestItemsCount(EnchantedIronGolemScrap) < 10)
							htmltext = "30409-04.htm";
						else
						{
							st.takeItems(SerpentCharm, -1);
							st.takeItems(EnchantedMonstereye, -1);
							st.takeItems(EnchantedStoneGolemPowder, -1);
							st.takeItems(EnchantedIronGolemScrap, -1);
							st.giveItems(ToneOfEarth, 1);
							htmltext = "30409-05.htm";
							checkBooks(st);
							st.playSound(SOUND_MIDDLE);
						}
					}
					else
						htmltext = "30409-06.htm";
				}
			break;

			case Undine:
				if(cond == 5)
				{
					if(st.getQuestItemsCount(ToneOfWater) == 0)
					{
						if(st.getQuestItemsCount(UndineCharm) == 0)
						{
							htmltext = "30413-01.htm";
							st.giveItems(UndineCharm, 1);
							st.playSound(SOUND_MIDDLE);
						}
						else if(st.getQuestItemsCount(DazzlingDrop) < 20)
							htmltext = "30413-02.htm";
						else
						{
							st.takeItems(UndineCharm, -1);
							st.takeItems(DazzlingDrop, -1);
							st.giveItems(ToneOfWater, 1);
							htmltext = "30413-03.htm";
							checkBooks(st);
							st.playSound(SOUND_MIDDLE);
						}
					}
				}
				else
					htmltext = "30413-04.htm";
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
		if(cond == 3 && st.getQuestItemsCount(GoldenSeed1st) != 0 && st.getQuestItemsCount(GoldenSeed2st) != 0 && st.getQuestItemsCount(GoldenSeed3st) != 0)
		{
			st.setCond(4);
		}
		return null;
	}
}