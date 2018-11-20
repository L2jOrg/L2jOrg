package quests;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;

//Edited by Evil_dnk

public final class _212_TrialOfDuty extends Quest
{

	private static final int MARK_OF_DUTY_ID = 2633;
	private static final int LETTER_OF_DUSTIN_ID = 2634;
	private static final int KNIGHTS_TEAR_ID = 2635;
	private static final int MIRROR_OF_ORPIC_ID = 2636;
	private static final int TEAR_OF_CONFESSION_ID = 2637;
	private static final int REPORT_PIECE_ID = 2638;
	private static final int TALIANUSS_REPORT_ID = 2639;
	private static final int TEAR_OF_LOYALTY_ID = 2640;
	private static final int MILITAS_ARTICLE_ID = 2641;
	private static final int SAINTS_ASHES_URN_ID = 2642;
	private static final int ATEBALTS_SKULL_ID = 2643;
	private static final int ATEBALTS_RIBS_ID = 2644;
	private static final int ATEBALTS_SHIN_ID = 2645;
	private static final int LETTER_OF_WINDAWOOD_ID = 2646;
	private static final int OLD_KNIGHT_SWORD_ID = 3027;
	private static final int RewardExp = 92450;
	private static final int RewardSP = 0;

	public _212_TrialOfDuty()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(30109);

		addTalkId(30109);
		addTalkId(30109);

		addTalkId(30109);
		addTalkId(30116);
		addTalkId(30311);
		for(int i = 30653; i < 30657; i++)
			addTalkId(i);

		addKillId(20144);
		addKillId(20190);
		addKillId(20191);
		addKillId(20200);
		addKillId(20201);
		addKillId(20270);
		addKillId(27119);
		for(int j = 20577; j < 20583; j++)
			addKillId(j);

		addQuestItem(new int[]{
				LETTER_OF_DUSTIN_ID,
				KNIGHTS_TEAR_ID,
				OLD_KNIGHT_SWORD_ID,
				TEAR_OF_CONFESSION_ID,
				MIRROR_OF_ORPIC_ID,
				TALIANUSS_REPORT_ID,
				MILITAS_ARTICLE_ID,
				ATEBALTS_SKULL_ID,
				ATEBALTS_RIBS_ID,
				ATEBALTS_SHIN_ID,
				LETTER_OF_WINDAWOOD_ID,
				TEAR_OF_LOYALTY_ID,
				SAINTS_ASHES_URN_ID,
				REPORT_PIECE_ID
		});

		addClassIdCheck("kash_q0211_02.htm", 4, 19, 32);
		addLevelCheck("kash_q0211_01.htm", 35);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("1"))
		{
			htmltext = "hannavalt_q0212_04.htm";
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("30116_1"))
			htmltext = "dustin_q0212_02.htm";
		else if(event.equalsIgnoreCase("30116_2"))
			htmltext = "dustin_q0212_03.htm";
		else if(event.equalsIgnoreCase("30116_3"))
			htmltext = "dustin_q0212_04.htm";
		else if(event.equalsIgnoreCase("30116_4"))
		{
			htmltext = "dustin_q0212_05.htm";
			st.takeItems(TEAR_OF_LOYALTY_ID, 1);
			st.setCond(14);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		if(st.getQuestItemsCount(MARK_OF_DUTY_ID) > 0)
		{
			return COMPLETED_DIALOG;
		}
		String htmltext = NO_QUEST_DIALOG;
		int npcId = npc.getNpcId();
		int cond = st.getCond();

		switch (npcId){
			case 30109:
				if(cond == 0)
					htmltext = "hannavalt_q0212_03.htm";
				else if(cond == 1)
					htmltext = "hannavalt_q0212_04.htm";
				else if(cond == 18)
				{
					htmltext = "hannavalt_q0212_05.htm";
					st.takeItems(LETTER_OF_DUSTIN_ID, -1);
					st.giveItems(MARK_OF_DUTY_ID, 1);
					if(!st.getPlayer().getVarBoolean("prof2.1"))
					{
						st.addExpAndSp(RewardExp, RewardSP);
						st.getPlayer().setVar("prof2.1", "1", -1);
					}
					st.finishQuest();
				}
			break;

			case 30653:
				if(cond == 1)
				{
					htmltext = "sir_aron_tanford_q0212_01.htm";
					if(st.getQuestItemsCount(OLD_KNIGHT_SWORD_ID) == 0)
						st.giveItems(OLD_KNIGHT_SWORD_ID, 1);
					st.setCond(2);
				}
				else if(cond == 2)
					htmltext = "sir_aron_tanford_q0212_02.htm";
				else if(cond == 3)
				{
					htmltext = "sir_aron_tanford_q0212_03.htm";
					st.takeItems(KNIGHTS_TEAR_ID, 1);
					st.takeItems(OLD_KNIGHT_SWORD_ID, 1);
					st.setCond(4);
				}
				else if(cond == 4)
					htmltext = "sir_aron_tanford_q0212_04.htm";
			break;

			case 30654:
				if(cond == 4)
				{
					htmltext = "sir_kiel_nighthawk_q0212_01.htm";
					st.setCond(5);
				}
				else if(cond == 5)
					htmltext = "sir_kiel_nighthawk_q0212_02.htm";
				else if(cond == 6)
				{
					htmltext = "sir_kiel_nighthawk_q0212_03.htm";
					st.setCond(7);
					st.giveItems(MIRROR_OF_ORPIC_ID, 1);
				}
				else if(cond == 6)
					htmltext = "sir_kiel_nighthawk_q0212_04.htm";
				else if(st.getQuestItemsCount(TEAR_OF_CONFESSION_ID) > 0)
				{
					htmltext = "sir_kiel_nighthawk_q0212_05.htm";
					st.takeItems(TEAR_OF_CONFESSION_ID, 1);
					st.setCond(10);
				}
				else if(cond == 10)
					htmltext = "sir_kiel_nighthawk_q0212_06.htm";
			break;

			case 30656:
				if(cond == 8)
				{
					htmltext = "spirit_of_sir_talianus_q0212_01.htm";
					st.takeItems(MIRROR_OF_ORPIC_ID, 1);
					st.takeItems(TALIANUSS_REPORT_ID, 1);
					st.giveItems(TEAR_OF_CONFESSION_ID, 1);
					st.setCond(9);
				}
			break;

			case 30655:
				if(cond == 10)
				{
					htmltext = "isael_silvershadow_q0212_02.htm";
					st.setCond(11);
//					if(st.getPlayer().getLevel() >= 36)
//					{
//						htmltext = "isael_silvershadow_q0212_02.htm";
//						st.setCond(11);
//					}
//					else
					//htmltext = "isael_silvershadow_q0212_01.htm";
				}
				else if(cond == 11)
					htmltext = "isael_silvershadow_q0212_03.htm";
				else if(cond == 12)
				{
					htmltext = "isael_silvershadow_q0212_04.htm";
					st.takeItems(MILITAS_ARTICLE_ID, st.getQuestItemsCount(MILITAS_ARTICLE_ID));
					st.giveItems(TEAR_OF_LOYALTY_ID, 1);
					st.setCond(13);
				}
				else if(cond == 13)
					htmltext = "isael_silvershadow_q0212_05.htm";
			break;

			case 30116:
				if(cond == 13)
					htmltext = "dustin_q0212_01.htm";
				else if(cond == 14)
					htmltext = "dustin_q0212_06.htm";
				else if(cond == 15)
				{
					htmltext = "dustin_q0212_07.htm";
					st.takeItems(ATEBALTS_SKULL_ID, 1);
					st.takeItems(ATEBALTS_RIBS_ID, 1);
					st.takeItems(ATEBALTS_SHIN_ID, 1);
					st.giveItems(SAINTS_ASHES_URN_ID, 1);
					st.setCond(16);
				}
				else if(cond == 16)
					htmltext = "dustin_q0212_09.htm";
				else if(cond == 17)
				{
					htmltext = "dustin_q0212_08.htm";
					st.takeItems(LETTER_OF_WINDAWOOD_ID, 1);
					st.giveItems(LETTER_OF_DUSTIN_ID, 1);
					st.setCond(18);
				}
				else if(cond == 18)
					htmltext = "dustin_q0212_10.htm";
			break;

			case 30311:
				if(cond == 16)
				{
					htmltext = "sir_collin_windawood_q0212_01.htm";
					st.takeItems(SAINTS_ASHES_URN_ID, 1);
					st.giveItems(LETTER_OF_WINDAWOOD_ID, 1);
					st.setCond(17);
				}
				else if(cond == 17)
					htmltext = "sir_collin_windawood_q0212_02.htm";
			break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == 20190 || npcId == 20191)
		{
			if(cond == 2)
				if(Rnd.chance(10))
				{
					st.addSpawn(27119);
					st.playSound(SOUND_BEFORE_BATTLE);
				}
		}
		else if(npcId == 27119 && cond == 2)
		{
			st.giveItems(KNIGHTS_TEAR_ID, 1);
			st.setCond(3);
		}
		else if((npcId == 20200 || npcId == 20201) && cond == 5)
		{
			if(Rnd.chance(50))
			{
				st.giveItems(REPORT_PIECE_ID, 1, true);
				st.playSound(SOUND_ITEMGET);
			}
			if(st.getQuestItemsCount(REPORT_PIECE_ID) >= 10)
			{
				st.takeItems(REPORT_PIECE_ID, st.getQuestItemsCount(REPORT_PIECE_ID));
				st.giveItems(TALIANUSS_REPORT_ID, 1);
				st.setCond(6);
			}
		}
		else if(npcId == 20144 && cond == 7 && Rnd.chance(20))
		{
			st.addSpawn(30656, npc.getX(), npc.getY(), npc.getZ(), 300000);
			st.setCond(8);
		}
		else if(npcId >= 20577 && npcId <= 20582 && cond == 11)
		{
			st.giveItems(MILITAS_ARTICLE_ID, 1, true);
			if(st.getQuestItemsCount(MILITAS_ARTICLE_ID) > 19)
				st.setCond(12);
			else
				st.playSound(SOUND_ITEMGET);
		}
		else if(npcId == 20270 && cond == 14 && Rnd.chance(50))
		{
			if(st.getQuestItemsCount(ATEBALTS_SKULL_ID) == 0)
			{
				st.giveItems(ATEBALTS_SKULL_ID, 1);
				st.playSound(SOUND_ITEMGET);
			}
			else if(st.getQuestItemsCount(ATEBALTS_RIBS_ID) == 0)
			{
				st.giveItems(ATEBALTS_RIBS_ID, 1);
				st.playSound(SOUND_ITEMGET);
			}
			else if(st.getQuestItemsCount(ATEBALTS_SHIN_ID) == 0)
			{
				st.giveItems(ATEBALTS_SHIN_ID, 1);
				st.setCond(15);
			}
		}
		return null;
	}
}