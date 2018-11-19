package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//Edited by Evil_dnk
//Tested

public final class _413_PathToShillienOracle extends Quest
{
	//npc
	public final int SIDRA = 30330;
	public final int ADONIUS = 30375;
	public final int TALBOT = 30377;
	//mobs
	public final int ZOMBIE_SOLDIER = 20457;
	public final int ZOMBIE_WARRIOR = 20458;
	public final int SHIELD_SKELETON = 20514;
	public final int SKELETON_INFANTRYMAN = 20515;
	public final int DARK_SUCCUBUS = 20776;
	//items
	public final int SIDRAS_LETTER1_ID = 1262;
	public final int BLANK_SHEET1_ID = 1263;
	public final int BLOODY_RUNE1_ID = 1264;
	public final int GARMIEL_BOOK_ID = 1265;
	public final int PRAYER_OF_ADON_ID = 1266;
	public final int PENITENTS_MARK_ID = 1267;
	public final int ASHEN_BONES_ID = 1268;
	public final int ANDARIEL_BOOK_ID = 1269;
	public final int ORB_OF_ABYSS_ID = 1270;
	//ASHEN_BONES_DROP [moblist]
	public final int[] ASHEN_BONES_DROP = {
			ZOMBIE_SOLDIER,
			ZOMBIE_WARRIOR,
			SHIELD_SKELETON,
			SKELETON_INFANTRYMAN
	};

	public _413_PathToShillienOracle()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(SIDRA);

		addTalkId(ADONIUS);
		addTalkId(TALBOT);

		addKillId(DARK_SUCCUBUS);

		for(int i : ASHEN_BONES_DROP)
			addKillId(i);

		addQuestItem(ASHEN_BONES_ID);

		addQuestItem(SIDRAS_LETTER1_ID);
		addQuestItem(ANDARIEL_BOOK_ID);
		addQuestItem(PENITENTS_MARK_ID);
		addQuestItem(GARMIEL_BOOK_ID);
		addQuestItem(PRAYER_OF_ADON_ID);
		addQuestItem(BLANK_SHEET1_ID);
		addQuestItem(BLOODY_RUNE1_ID);

		addLevelCheck("master_sidra_q0413_02.htm", 19);
		addClassIdCheck("master_sidra_q0413_03.htm", 38);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("1"))
		{
			htmltext = "master_sidra_q0413_06.htm";
			st.setCond(1);
			st.giveItems(SIDRAS_LETTER1_ID, 1);
		}
		else if(event.equalsIgnoreCase("413_1"))
		{
			htmltext = "master_sidra_q0413_05.htm";
		}
		else if(event.equalsIgnoreCase("30377_1"))
		{
			htmltext = "magister_talbot_q0413_02.htm";
			st.takeItems(SIDRAS_LETTER1_ID, -1);
			st.giveItems(BLANK_SHEET1_ID, 5);
			st.setCond(2);
		}
		else if(event.equalsIgnoreCase("30375_1"))
			htmltext = "priest_adonius_q0413_02.htm";
		else if(event.equalsIgnoreCase("30375_2"))
			htmltext = "priest_adonius_q0413_03.htm";
		else if(event.equalsIgnoreCase("30375_3"))
		{
			htmltext = "priest_adonius_q0413_04.htm";
			st.takeItems(PRAYER_OF_ADON_ID, -1);
			st.giveItems(PENITENTS_MARK_ID, 1);
			st.setCond(5);
		}
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
			case SIDRA:
				if (cond == 0)
				{
					if (st.getQuestItemsCount(ORB_OF_ABYSS_ID) > 0)
						htmltext = "master_sidra_q0413_04.htm";
					else
						htmltext = "master_sidra_q0413_01.htm";
				}
				else if (cond == 1)
					htmltext = "master_sidra_q0413_07.htm";
				else if (cond == 2 | cond == 3)
					htmltext = "master_sidra_q0413_08.htm";
				else if (cond > 3 && cond < 7)
					htmltext = "master_sidra_q0413_09.htm";
				else if (cond == 7)
				{
					htmltext = "master_sidra_q0413_10.htm";
					st.finishQuest();
					if (st.getPlayer().getClassId().isOfLevel(ClassLevel.NONE))
					{
						st.giveItems(ORB_OF_ABYSS_ID, 1);
						if (!st.getPlayer().getVarBoolean("prof1"))
						{
							st.getPlayer().setVar("prof1", "1", -1);
							st.addExpAndSp(80314, 5910);
						}
					}
				}
			break;

			case TALBOT:
				if (cond == 1)
					htmltext = "magister_talbot_q0413_01.htm";
				else if (cond == 2)
				{
					if (!st.haveQuestItem(BLOODY_RUNE1_ID))
						htmltext = "magister_talbot_q0413_03.htm";
					else
						htmltext = "magister_talbot_q0413_04.htm";
				}
				else if (cond == 3)
				{
					htmltext = "magister_talbot_q0413_05.htm";
					st.takeItems(BLOODY_RUNE1_ID, -1);
					st.giveItems(GARMIEL_BOOK_ID, 1);
					st.giveItems(PRAYER_OF_ADON_ID, 1);
					st.setCond(4);
				}
				else if (cond > 3 && cond < 7)
					htmltext = "magister_talbot_q0413_06.htm";
				else if (cond == 7)
					htmltext = "magister_talbot_q0413_07.htm";
			break;

			case ADONIUS:
				if (cond == 4)
					htmltext = "priest_adonius_q0413_01.htm";
				else if (cond == 5)
				{
					if (st.haveQuestItem(ASHEN_BONES_ID))
						htmltext = "priest_adonius_q0413_05.htm";
					else
						htmltext = "priest_adonius_q0413_06.htm";
				}
				else if (cond == 6)
				{
					htmltext = "priest_adonius_q0413_07.htm";
					st.takeItems(ASHEN_BONES_ID, -1);
					st.takeItems(PENITENTS_MARK_ID, -1);
					st.giveItems(ANDARIEL_BOOK_ID, 1);
					st.setCond(7);
				}
				else if (cond == 7)
					htmltext = "priest_adonius_q0413_08.htm";
			break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();

		if(cond == 2 && npcId == DARK_SUCCUBUS)
			{
				st.takeItems(BLANK_SHEET1_ID, 1);
				st.giveItems(BLOODY_RUNE1_ID, 1, false);
				if(st.getQuestItemsCount(BLOODY_RUNE1_ID) > 4)
				{
					st.setCond(3);
				}
			}
		if(cond == 5 && npcId != DARK_SUCCUBUS)
			{
				st.rollAndGive(ASHEN_BONES_ID, 1, 1, 10, 100);
				if(st.getQuestItemsCount(ASHEN_BONES_ID) > 9)
					st.setCond(6);
			}
		return null;
	}

	@Override
	public String checkStartCondition(NpcInstance npc, Player player)
	{
		if(player.getClassId().getId() == 0x2a)
			return "master_sidra_q0413_02a.htm";
		return super.checkStartCondition(npc, player);
	}
}