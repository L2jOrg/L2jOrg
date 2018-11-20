package quests;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.model.items.Inventory;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;

public final class _224_TestOfSagittarius extends Quest
{
	private static final int BERNARDS_INTRODUCTION_ID = 3294;
	private static final int LETTER_OF_HAMIL3_ID = 3297;
	private static final int HUNTERS_RUNE2_ID = 3299;
	private static final int MARK_OF_SAGITTARIUS_ID = 3293;
	private static final int CRESCENT_MOON_BOW_ID = 3028;
	private static final int TALISMAN_OF_KADESH_ID = 3300;
	private static final int BLOOD_OF_LIZARDMAN_ID = 3306;
	private static final int LETTER_OF_HAMIL1_ID = 3295;
	private static final int LETTER_OF_HAMIL2_ID = 3296;
	private static final int HUNTERS_RUNE1_ID = 3298;
	private static final int TALISMAN_OF_SNAKE_ID = 3301;
	private static final int MITHRIL_CLIP_ID = 3302;
	private static final int STAKATO_CHITIN_ID = 3303;
	private static final int ST_BOWSTRING_ID = 3304;
	private static final int MANASHENS_HORN_ID = 3305;
	private static final int WOODEN_ARROW_ID = 17;
	private static final int RewardExp = 151200;
	private static final int RewardSP = 0;

	public _224_TestOfSagittarius()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(30702);
		addTalkId(30514);
		addTalkId(30626);
		addTalkId(30653);
		addTalkId(30702);
		addTalkId(30717);

		addKillId(20230);
		addKillId(20232);
		addKillId(20233);
		addKillId(20234);
		addKillId(20269);
		addKillId(20270);
		addKillId(27090);
		addKillId(20551);
		addKillId(20563);
		addKillId(20577);
		addKillId(20578);
		addKillId(20579);
		addKillId(20580);
		addKillId(20581);
		addKillId(20582);
		addKillId(20079);
		addKillId(20080);
		addKillId(20081);
		addKillId(20082);
		addKillId(20084);
		addKillId(20086);
		addKillId(20089);
		addKillId(20090);

		addQuestItem(new int[]{
				HUNTERS_RUNE2_ID,
				CRESCENT_MOON_BOW_ID,
				TALISMAN_OF_KADESH_ID,
				BLOOD_OF_LIZARDMAN_ID,
				BERNARDS_INTRODUCTION_ID,
				HUNTERS_RUNE1_ID,
				LETTER_OF_HAMIL1_ID,
				TALISMAN_OF_SNAKE_ID,
				LETTER_OF_HAMIL2_ID,
				LETTER_OF_HAMIL3_ID,
				MITHRIL_CLIP_ID,
				STAKATO_CHITIN_ID,
				ST_BOWSTRING_ID,
				MANASHENS_HORN_ID
		});

		addClassIdCheck("union_president_bernard_q0224_01.htm", 7, 35, 22);
		addLevelCheck("union_president_bernard_q0224_02.htm", 39);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equals("1"))
		{
			htmltext = "union_president_bernard_q0224_04.htm";
			st.setCond(1);
			st.giveItems(BERNARDS_INTRODUCTION_ID, 1);
		}
		else if(event.equals("30626_1"))
			htmltext = "sagittarius_hamil_q0224_02.htm";
		else if(event.equals("30626_2"))
		{
			htmltext = "sagittarius_hamil_q0224_03.htm";
			st.takeItems(BERNARDS_INTRODUCTION_ID, st.getQuestItemsCount(BERNARDS_INTRODUCTION_ID));
			st.giveItems(LETTER_OF_HAMIL1_ID, 1);
			st.setCond(2);
		}
		else if(event.equals("30626_3"))
			htmltext = "sagittarius_hamil_q0224_06.htm";
		else if(event.equals("30626_4"))
		{
			htmltext = "sagittarius_hamil_q0224_07.htm";
			st.takeItems(HUNTERS_RUNE1_ID, st.getQuestItemsCount(HUNTERS_RUNE1_ID));
			st.giveItems(LETTER_OF_HAMIL2_ID, 1);
			st.setCond(5);
		}
		else if(event.equals("30653_1"))
		{
			htmltext = "sir_aron_tanford_q0224_02.htm";
			st.takeItems(LETTER_OF_HAMIL1_ID, st.getQuestItemsCount(LETTER_OF_HAMIL1_ID));
			st.setCond(3);
		}
		else if(event.equals("30514_1"))
		{
			htmltext = "prefect_vokiyan_q0224_02.htm";
			st.takeItems(LETTER_OF_HAMIL2_ID, st.getQuestItemsCount(LETTER_OF_HAMIL2_ID));
			st.setCond(6);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		if(st.getQuestItemsCount(MARK_OF_SAGITTARIUS_ID) > 0)
		{
			return COMPLETED_DIALOG;
		}
		int npcId = npc.getNpcId();
		String htmltext = NO_QUEST_DIALOG;
		int cond = st.getCond();

		switch (npcId)
		{
			case 30702:
				if(cond == 0)
					htmltext = "union_president_bernard_q0224_03.htm";
				else if(cond == 1)
					htmltext = "union_president_bernard_q0224_05.htm";
				break;

			case 30626:
				if(cond == 1)
					htmltext = "sagittarius_hamil_q0224_01.htm";
				else if(cond == 2)
					htmltext = "sagittarius_hamil_q0224_04.htm";
				else if(cond == 4)
					htmltext = "sagittarius_hamil_q0224_05.htm";
				else if(cond == 5)
					htmltext = "sagittarius_hamil_q0224_08.htm";
				else if(cond == 8)
				{
					htmltext = "sagittarius_hamil_q0224_09.htm";
					st.giveItems(LETTER_OF_HAMIL3_ID, 1);
					st.setCond(9);
				}
				else if(cond == 9)
					htmltext = "sagittarius_hamil_q0224_10.htm";
				else if(cond == 12)
				{
					htmltext = "sagittarius_hamil_q0224_11.htm";
					st.setCond(13);
				}
				else if(cond == 13)
					htmltext = "м12.htm";
				else if(cond == 14)
				{
					htmltext = "sagittarius_hamil_q0224_13.htm";
					st.takeItems(CRESCENT_MOON_BOW_ID, -1);
					st.takeItems(TALISMAN_OF_KADESH_ID, -1);
					st.takeItems(BLOOD_OF_LIZARDMAN_ID, -1);
					st.giveItems(MARK_OF_SAGITTARIUS_ID, 1);
					if(!st.getPlayer().getVarBoolean("prof2.3"))
					{
						st.addExpAndSp(RewardExp, RewardSP);
						st.getPlayer().setVar("prof2.3", "1", -1);
					}
					st.finishQuest();
				}
				break;

			case 30653:
				if(cond == 2)
					htmltext = "sir_aron_tanford_q0224_01.htm";
				else if(cond == 3)
					htmltext = "sir_aron_tanford_q0224_03.htm";
				break;

			case 30514:
				if(cond == 5)
					htmltext = "prefect_vokiyan_q0224_01.htm";
				else if(cond == 6)
					htmltext = "prefect_vokiyan_q0224_03.htm";
				else if(cond == 7)
				{
					htmltext = "prefect_vokiyan_q0224_04.htm";
					st.takeItems(TALISMAN_OF_SNAKE_ID, st.getQuestItemsCount(TALISMAN_OF_SNAKE_ID));
					st.setCond(8);
				}
				else if(cond == 8)
					htmltext = "prefect_vokiyan_q0224_05.htm";
				break;

			case 30717:
				if(cond == 9)
				{
					htmltext = "magister_gauen_q0224_01.htm";
					st.takeItems(LETTER_OF_HAMIL3_ID, st.getQuestItemsCount(LETTER_OF_HAMIL3_ID));
					st.setCond(10);
				}
				else if(cond == 10)
					htmltext = "magister_gauen_q0224_03.htm";
				else if(cond == 12)
					htmltext = "magister_gauen_q0224_04.htm";
				else if(cond == 11)
				{
					htmltext = "magister_gauen_q0224_02.htm";
					st.takeItems(MITHRIL_CLIP_ID, st.getQuestItemsCount(MITHRIL_CLIP_ID));
					st.takeItems(STAKATO_CHITIN_ID, st.getQuestItemsCount(STAKATO_CHITIN_ID));
					st.takeItems(ST_BOWSTRING_ID, st.getQuestItemsCount(ST_BOWSTRING_ID));
					st.takeItems(MANASHENS_HORN_ID, st.getQuestItemsCount(MANASHENS_HORN_ID));
					st.giveItems(CRESCENT_MOON_BOW_ID, 1);
					st.giveItems(WOODEN_ARROW_ID, 10);
					st.setCond(12);
				}
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		if(npcId == 20079 || npcId == 20080 || npcId == 20081 || npcId == 20084 || npcId == 20086 || npcId == 20089 || npcId == 20090)
		{
			if(st.getCond() == 3)
			{
				st.rollAndGive(HUNTERS_RUNE1_ID, 1, 1, 10, 50);
				if(st.getQuestItemsCount(HUNTERS_RUNE1_ID) >= 10)
					st.setCond(4);
			}
		}
		else if(npcId == 20269 || npcId == 20270)
		{
			if(st.getCond() == 6)
			{
				st.rollAndGive(HUNTERS_RUNE2_ID, 1, 1, 10, 50);
				if(st.getQuestItemsCount(HUNTERS_RUNE2_ID) >= 10)
				{
					st.takeItems(HUNTERS_RUNE2_ID, 10);
					st.giveItems(TALISMAN_OF_SNAKE_ID, 1);
					st.setCond(7);
				}
			}
		}
		if(st.getCond() == 10)
		{
			if(npcId == 20230 || npcId == 20232 || npcId == 20234)
				st.rollAndGive(STAKATO_CHITIN_ID, 1, 1, 1, 10);
			else if(npcId == 20563)
				st.rollAndGive(MANASHENS_HORN_ID, 1, 1, 1, 10);
			else if(npcId == 20233)
				st.rollAndGive(ST_BOWSTRING_ID, 1, 1, 1, 10);
			else if(npcId == 20551)
				st.rollAndGive(MITHRIL_CLIP_ID, 1, 1, 1, 30);
			if(st.haveQuestItem(STAKATO_CHITIN_ID) && st.haveQuestItem(MANASHENS_HORN_ID) && st.haveQuestItem(ST_BOWSTRING_ID) && st.haveQuestItem(MITHRIL_CLIP_ID))
				st.setCond(11);
		}
		if(st.getCond() == 13)
		{
			if(npcId == 20577 || npcId == 20578 || npcId == 20579 || npcId == 20580 || npcId == 20581 || npcId == 20582)
			{
				if(Rnd.chance((st.getQuestItemsCount(BLOOD_OF_LIZARDMAN_ID) - 120) * 5))
				{
					st.addSpawn(27090);
					st.takeItems(BLOOD_OF_LIZARDMAN_ID, st.getQuestItemsCount(BLOOD_OF_LIZARDMAN_ID));
					st.playSound(SOUND_BEFORE_BATTLE);
				}
				else
				{
					st.giveItems(BLOOD_OF_LIZARDMAN_ID, 1);
					st.playSound(SOUND_ITEMGET);
				}
			}
			else if(npcId == 27090)
			{
				if(st.getQuestItemsCount(TALISMAN_OF_KADESH_ID) == 0)
					if(st.getItemEquipped(Inventory.PAPERDOLL_RHAND) == CRESCENT_MOON_BOW_ID)
					{
						st.giveItems(TALISMAN_OF_KADESH_ID, 1);
						st.setCond(14);
					}
					else
						st.addSpawn(27090);
			}
		}
		return null;
	}
}