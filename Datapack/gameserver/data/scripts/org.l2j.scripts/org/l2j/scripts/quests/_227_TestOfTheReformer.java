package org.l2j.scripts.quests;

import org.l2j.gameserver.ai.CtrlEvent;
import org.l2j.gameserver.model.GameObjectsStorage;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;

/**
 * Квест на вторую профессию Test Of The Reformer
 *
 * @author Sergey Ibryaev aka Artful
 */
public final class _227_TestOfTheReformer extends Quest
{
	//NPC
	private static final int Pupina = 30118;
	private static final int Sla = 30666;
	private static final int Katari = 30668;
	private static final int OlMahumPilgrimNPC = 30732;
	private static final int Kakan = 30669;
	private static final int Nyakuri = 30670;
	private static final int Ramus = 30667;
	//Quest Items
	private static final int BookOfReform = 2822;
	private static final int LetterOfIntroduction = 2823;
	private static final int SlasLetter = 2824;
	private static final int Greetings = 2825;
	private static final int OlMahumMoney = 2826;
	private static final int KatarisLetter = 2827;
	private static final int NyakurisLetter = 2828;
	private static final int KakansLetter = 3037;
	private static final int UndeadList = 2829;
	private static final int RamussLetter = 2830;
	private static final int RippedDiary = 2831;
	private static final int HugeNail = 2832;
	private static final int LetterOfBetrayer = 2833;
	private static final int BoneFragment1 = 2834;
	private static final int BoneFragment2 = 2835;
	private static final int BoneFragment3 = 2836;
	private static final int BoneFragment4 = 2837;
	private static final int BoneFragment5 = 2838;
	//private static final int BoneFragment6 = 2839;
	//Items
	private static final int MarkOfReformer = 2821;
	//MOB
	private static final int NamelessRevenant = 27099;
	private static final int Aruraune = 27128;
	private static final int OlMahumInspector = 27129;
	private static final int OlMahumBetrayer = 27130;
	private static final int CrimsonWerewolf = 27131;
	private static final int KrudelLizardman = 27132;
	private static final int SilentHorror = 20404;
	private static final int SkeletonLord = 20104;
	private static final int SkeletonMarksman = 20102;
	private static final int MiserySkeleton = 20022;
	private static final int SkeletonArcher = 20100;

	// Reward's
	private static final int EXP_REWARD = 151200; // EXP reward count
	private static final int SP_REWARD = 0; // SP reward count

	// Condition's
	private static final int MIN_LEVEL = 39;

	//Drop Cond
	//# [COND, NEWCOND, ID, REQUIRED, ITEM, NEED_COUNT, CHANCE, DROP]
	public final int[][] DROPLIST_COND = {
			{
					18,
					0,
					SilentHorror,
					0,
					BoneFragment1,
					1,
					70,
					1
			},
			{
					18,
					0,
					SkeletonLord,
					0,
					BoneFragment2,
					1,
					70,
					1
			},
			{
					18,
					0,
					SkeletonMarksman,
					0,
					BoneFragment3,
					1,
					70,
					1
			},
			{
					18,
					0,
					MiserySkeleton,
					0,
					BoneFragment4,
					1,
					70,
					1
			},
			{
					18,
					0,
					SkeletonArcher,
					0,
					BoneFragment5,
					1,
					70,
					1
			}
	};

	public _227_TestOfTheReformer()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(Pupina);

		addTalkId(Sla);
		addTalkId(Katari);
		addTalkId(OlMahumPilgrimNPC);
		addTalkId(Kakan);
		addTalkId(Nyakuri);
		addTalkId(Ramus);

		addKillId(NamelessRevenant);
		addKillId(Aruraune);
		addKillId(OlMahumInspector);
		addKillId(OlMahumBetrayer);
		addKillId(CrimsonWerewolf);
		addKillId(KrudelLizardman);
		for(int i = 0; i < DROPLIST_COND.length; i++)
		{
			addKillId(DROPLIST_COND[i][2]);
			addQuestItem(DROPLIST_COND[i][4]);
		}
		addQuestItem(new int[]{
				BookOfReform,
				HugeNail,
				LetterOfIntroduction,
				SlasLetter,
				KatarisLetter,
				LetterOfBetrayer,
				OlMahumMoney,
				NyakurisLetter,
				UndeadList,
				Greetings,
				KakansLetter,
				RamussLetter,
				RippedDiary
		});

		addClassIdCheck("30118-01.htm", ClassId.CLERIC, ClassId.SHILLEN_ORACLE);
		addLevelCheck("30118-02.htm", MIN_LEVEL);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("30118-04.htm"))
		{
			st.giveItems(BookOfReform, 1);
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("30118-06.htm"))
		{
			st.takeItems(HugeNail, -1);
			st.takeItems(BookOfReform, -1);
			st.giveItems(LetterOfIntroduction, 1);
			st.setCond(4);
		}
		else if(event.equalsIgnoreCase("preacher_sla_q0227_04.htm"))
		{
			st.takeItems(LetterOfIntroduction, -1);
			st.giveItems(SlasLetter, 1);
			st.setCond(5);
		}
		else if(event.equalsIgnoreCase("30669-03.htm"))
		{
			if(GameObjectsStorage.getByNpcId(CrimsonWerewolf) == null)
			{
				st.setCond(12, false);
				NpcInstance Mob = addSpawn(CrimsonWerewolf, npc.getLoc(), 60, 0);
				Mob.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, st.getPlayer(), 1000);
				st.startQuestTimer("Wait4", 300000);
			}
		}
		else if(event.equalsIgnoreCase("30670-03.htm"))
		{
			if(GameObjectsStorage.getByNpcId(KrudelLizardman) == null)
			{
				st.setCond(15, false);
				NpcInstance Mob = addSpawn(KrudelLizardman, npc.getLoc(), 60, 0);
				Mob.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, st.getPlayer(), 1000);
				st.startQuestTimer("Wait5", 300000);
			}
		}
		else if(event.equalsIgnoreCase("Wait1"))
		{
			NpcInstance isQuest = GameObjectsStorage.getByNpcId(Aruraune);
			if(isQuest != null)
				isQuest.deleteMe();
			if(st.getCond() == 2)
				st.setCond(1);
			return null;
		}
		else if(event.equalsIgnoreCase("Wait2"))
		{
			NpcInstance isQuest = GameObjectsStorage.getByNpcId(OlMahumInspector);
			if(isQuest != null)
				isQuest.deleteMe();
			isQuest = GameObjectsStorage.getByNpcId(OlMahumPilgrimNPC);
			if(isQuest != null)
				isQuest.deleteMe();
			if(st.getCond() == 6)
				st.setCond(5);
			return null;
		}
		else if(event.equalsIgnoreCase("Wait3"))
		{
			NpcInstance isQuest = GameObjectsStorage.getByNpcId(OlMahumBetrayer);
			if(isQuest != null)
				isQuest.deleteMe();
			if(st.getCond() == 8)
				st.setCond(7);
			return null;
		}
		else if(event.equalsIgnoreCase("Wait4"))
		{
			NpcInstance isQuest = GameObjectsStorage.getByNpcId(CrimsonWerewolf);
			if(isQuest != null)
				isQuest.deleteMe();
			if(st.getCond() == 12)
				st.setCond(11);
			return null;
		}
		else if(event.equalsIgnoreCase("Wait5"))
		{
			NpcInstance isQuest = GameObjectsStorage.getByNpcId(KrudelLizardman);
			if(isQuest != null)
				isQuest.deleteMe();
			if(st.getCond() == 15)
				st.setCond(14);
			return null;
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		if(st.getQuestItemsCount(MarkOfReformer) != 0)
			return COMPLETED_DIALOG;

		int npcId = npc.getNpcId();
		String htmltext = NO_QUEST_DIALOG;
		int cond = st.getCond();

		switch (npcId)
		{
			case Pupina:
			{
				if(cond == 0)
					htmltext = "30118-03.htm";
				else if(cond == 3)
					htmltext = "30118-05.htm";
				else if(cond >= 4)
					htmltext = "30118-07.htm";
				break;
			}
			case Sla:
			{
				if(cond == 4)
					htmltext = "preacher_sla_q0227_01.htm";
				else if(cond == 5)
					htmltext = "preacher_sla_q0227_05.htm";
				else if(cond > 5 && cond < 10)
					htmltext = "preacher_sla_q0227_06b.htm";
				else if(cond == 10)
				{
					if(st.haveQuestItem(OlMahumMoney))
					{
						htmltext = "preacher_sla_q0227_06.htm";
						st.takeItems(OlMahumMoney, -1);
					}
					else
						htmltext = "preacher_sla_q0227_06a.htm";
					st.giveItems(Greetings, 3);
					st.setCond(11);
				}
				else if(cond == 20)
				{
					st.takeItems(KatarisLetter, -1);
					st.takeItems(KakansLetter, -1);
					st.takeItems(NyakurisLetter, -1);
					st.takeItems(RamussLetter, -1);
					st.giveItems(MarkOfReformer, 1);
					if(!st.getPlayer().getVarBoolean("prof2.3"))
					{
						st.addExpAndSp(EXP_REWARD, SP_REWARD);
						st.getPlayer().setVar("prof2.3", "1", -1);
					}
					htmltext = "preacher_sla_q0227_07.htm";
					st.finishQuest();
				}
				break;
			}
			case Katari:
			{
				if(cond == 5 || cond == 6)
				{
					htmltext = "katari_q0227_01.htm";
					st.takeItems(SlasLetter, -1);
					st.setCond(6, false);
					NpcInstance piligrim = GameObjectsStorage.getByNpcId(OlMahumPilgrimNPC);
					NpcInstance inspector = GameObjectsStorage.getByNpcId(OlMahumInspector);
					if(piligrim == null && inspector == null)
					{
						piligrim = addSpawn(OlMahumPilgrimNPC, npc.getLoc(), 60, 0);
						inspector = addSpawn(OlMahumInspector, piligrim.getLoc(), 60, 0);
						inspector.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, piligrim, 10000);
						st.startQuestTimer("Wait2", 300000);
					}
				}
				else if(cond == 7 || cond == 8)
				{
					htmltext = "katari_q0227_02.htm";

					if(cond == 7)
					{
						st.setCond(8, false);

						NpcInstance pilgrim = GameObjectsStorage.getByNpcId(OlMahumPilgrimNPC);
						if(pilgrim != null)
							pilgrim.deleteMe();

						st.cancelQuestTimer("Wait2");
					}

					if(GameObjectsStorage.getByNpcId(OlMahumBetrayer) == null)
					{
						addSpawn(OlMahumBetrayer, npc.getLoc(), 60, 0);
						st.startQuestTimer("Wait3", 300000);
					}
				}
				else if(cond == 9)
				{
					st.takeItems(LetterOfBetrayer, -1);
					st.giveItems(KatarisLetter, 1);
					htmltext = "katari_q0227_03.htm";
					st.setCond(10);
				}
				else if(cond == 10)
					htmltext = "katari_q0227_04.htm";
				break;
			}
			case OlMahumPilgrimNPC:
			{
				if(cond == 7)
				{
					htmltext = "ol_mahum_pilgrim_q0227_01.htm";
					if(!st.haveQuestItem(OlMahumMoney))
						st.giveItems(OlMahumMoney, 1);

					NpcInstance pilgrim = GameObjectsStorage.getByNpcId(OlMahumPilgrimNPC);
					if(pilgrim != null)
						pilgrim.deleteMe();

					st.cancelQuestTimer("Wait2");
				}
				break;
			}
			case Kakan:
			{
				if(cond == 11 || cond == 12)
					htmltext = "30669-01.htm";
				else if(cond == 13)
				{
					st.takeItems(Greetings, 1);
					st.giveItems(KakansLetter, 1);
					htmltext = "30669-04.htm";
					st.setCond(14);
				}
				break;
			}
			case Nyakuri:
			{
				if(cond == 14 || cond == 15)
					htmltext = "30670-01.htm";
				else if(cond == 16)
				{
					st.takeItems(Greetings, 1);
					st.giveItems(NyakurisLetter, 1);
					htmltext = "30670-04.htm";
					st.setCond(17);
				}
				break;
			}
			case Ramus:
			{
				if(cond == 17)
				{
					st.takeItems(Greetings, -1);
					st.giveItems(UndeadList, 1);
					htmltext = "30667-01.htm";
					st.setCond(18);
				}
				else if(cond == 19)
				{
					st.takeItems(BoneFragment1, -1);
					st.takeItems(BoneFragment2, -1);
					st.takeItems(BoneFragment3, -1);
					st.takeItems(BoneFragment4, -1);
					st.takeItems(BoneFragment5, -1);
					st.takeItems(UndeadList, -1);
					st.giveItems(RamussLetter, 1);
					htmltext = "30667-03.htm";
					st.setCond(20);
				}
				break;
			}
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
		if(cond == 18 && st.getQuestItemsCount(BoneFragment1) != 0 && st.getQuestItemsCount(BoneFragment2) != 0 && st.getQuestItemsCount(BoneFragment3) != 0 && st.getQuestItemsCount(BoneFragment4) != 0 && st.getQuestItemsCount(BoneFragment5) != 0)
		{
			st.setCond(19);
		}
		else if(npcId == NamelessRevenant && (cond == 1 || cond == 2))
		{
			if(st.getQuestItemsCount(RippedDiary) < 6)
				st.giveItems(RippedDiary, 1);
			else if(GameObjectsStorage.getByNpcId(Aruraune) == null)
			{
				st.takeItems(RippedDiary, -1);
				st.setCond(2, false);
				NpcInstance Mob = addSpawn(Aruraune, npc.getLoc(), 60, 0);
				Mob.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, st.getPlayer(), 1000);
				st.startQuestTimer("Wait1", 300000);
			}
			else if(!st.isRunningQuestTimer("Wait1"))
				st.startQuestTimer("Wait1", 300000);
		}
		else if(npcId == Aruraune)
		{
			NpcInstance isQuest = GameObjectsStorage.getByNpcId(Aruraune);
			if(isQuest != null)
				isQuest.deleteMe();
			if(cond == 2)
			{
				if(st.getQuestItemsCount(HugeNail) == 0)
					st.giveItems(HugeNail, 1);
				st.setCond(3);
				st.cancelQuestTimer("Wait1");
			}
		}
		else if(npcId == OlMahumInspector)
		{
			NpcInstance isQuest = GameObjectsStorage.getByNpcId(OlMahumInspector);
			if(isQuest != null)
				isQuest.deleteMe();
			st.cancelQuestTimer("Wait2");
			if(cond == 6)
			{
				st.setCond(7);
			}
		}
		else if(npcId == OlMahumBetrayer)
		{
			NpcInstance isQuest = GameObjectsStorage.getByNpcId(OlMahumBetrayer);
			if(isQuest != null)
				isQuest.deleteMe();
			st.cancelQuestTimer("Wait3");
			if(cond == 8)
			{
				if(st.getQuestItemsCount(LetterOfBetrayer) == 0)
					st.giveItems(LetterOfBetrayer, 1);
				st.setCond(9);
			}
		}
		else if(npcId == CrimsonWerewolf)
		{
			NpcInstance isQuest = GameObjectsStorage.getByNpcId(CrimsonWerewolf);
			if(isQuest != null)
				isQuest.deleteMe();
			st.cancelQuestTimer("Wait4");
			if(cond == 12)
			{
				st.setCond(13);
			}
		}
		else if(npcId == KrudelLizardman)
		{
			NpcInstance isQuest = GameObjectsStorage.getByNpcId(KrudelLizardman);
			if(isQuest != null)
				isQuest.deleteMe();
			st.cancelQuestTimer("Wait5");
			if(cond == 15)
			{
				st.setCond(16);
			}
		}
		return null;
	}
}