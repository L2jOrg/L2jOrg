package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//Edited by Evil_dnk
//Tested

public final class _405_PathToCleric extends Quest
{
	//npc
	public final int GALLINT = 30017;
	public final int ZIGAUNT = 30022;
	public final int VIVYAN = 30030;
	public final int SIMPLON = 30253;
	public final int PRAGA = 30333;
	public final int LIONEL = 30408;
	//mobs
	public final int RUIN_ZOMBIE = 20026;
	public final int RUIN_ZOMBIE_LEADER = 20029;
	//items
	public final int LETTER_OF_ORDER1 = 1191;
	public final int LETTER_OF_ORDER2 = 1192;
	public final int BOOK_OF_LEMONIELL = 1193;
	public final int BOOK_OF_VIVI = 1194;
	public final int BOOK_OF_SIMLON = 1195;
	public final int BOOK_OF_PRAGA = 1196;
	public final int CERTIFICATE_OF_GALLINT = 1197;
	public final int PENDANT_OF_MOTHER = 1198;
	public final int NECKLACE_OF_MOTHER = 1199;
	public final int LEMONIELLS_COVENANT = 1200;
	public final int MARK_OF_FAITH = 1201;

	public _405_PathToCleric()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(ZIGAUNT);

		addTalkId(GALLINT);
		addTalkId(VIVYAN);
		addTalkId(SIMPLON);
		addTalkId(PRAGA);
		addTalkId(LIONEL);

		addKillId(RUIN_ZOMBIE);
		addKillId(RUIN_ZOMBIE_LEADER);

		addQuestItem(new int[]{
				LEMONIELLS_COVENANT,
				LETTER_OF_ORDER2,
				BOOK_OF_PRAGA,
				BOOK_OF_VIVI,
				BOOK_OF_SIMLON,
				LETTER_OF_ORDER1,
				NECKLACE_OF_MOTHER,
				PENDANT_OF_MOTHER,
				CERTIFICATE_OF_GALLINT,
				BOOK_OF_LEMONIELL
		});

		addLevelCheck("gigon_q0405_03.htm", 19);
		addClassIdCheck("gigon_q0405_02.htm", 10);
	}

	public void checkBooks(QuestState st)
	{
		if(st.getQuestItemsCount(BOOK_OF_PRAGA) + st.getQuestItemsCount(BOOK_OF_VIVI) + st.getQuestItemsCount(BOOK_OF_SIMLON) >= 5)
			st.setCond(2);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("1"))
		{
			st.setCond(1);
			st.giveItems(LETTER_OF_ORDER1, 1);
			htmltext = "gigon_q0405_05.htm";
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
			case ZIGAUNT:
				if (cond == 0)
				{
					if (st.getQuestItemsCount(MARK_OF_FAITH) > 0)
						htmltext = "gigon_q0405_04.htm";
					else
						htmltext = "gigon_q0405_01.htm";
				}
				else if (cond == 1)
					htmltext = "gigon_q0405_06.htm";
				else if (cond == 2)
				{
					htmltext = "gigon_q0405_08.htm";
					st.takeItems(BOOK_OF_PRAGA, -1);
					st.takeItems(BOOK_OF_VIVI, -1);
					st.takeItems(BOOK_OF_SIMLON, -1);
					st.takeItems(LETTER_OF_ORDER1, -1);
					st.giveItems(LETTER_OF_ORDER2, 1);
					st.setCond(3);
				}
				else if (cond < 6 && st.getQuestItemsCount(LETTER_OF_ORDER2) > 0)
					htmltext = "gigon_q0405_07.htm";
				else if (cond == 6)
				{
					htmltext = "gigon_q0405_09.htm";
					st.takeItems(LEMONIELLS_COVENANT, -1);
					st.takeItems(LETTER_OF_ORDER2, -1);
					if (!st.getPlayer().getVarBoolean("q405"))
						st.getPlayer().setVar("q405", "1", -1);
					st.finishQuest();
					if (st.getPlayer().getClassId().isOfLevel(ClassLevel.NONE))
					{
						st.giveItems(MARK_OF_FAITH, 1);
						if (!st.getPlayer().getVarBoolean("prof1"))
						{
							st.getPlayer().setVar("prof1", "1", -1);
							st.addExpAndSp(80314, 5910);
						}
					}
				}
			break;

			case SIMPLON:
				if (cond == 1)
				{
					if (!st.haveQuestItem(BOOK_OF_SIMLON))
					{
						htmltext = "trader_simplon_q0405_01.htm";
						st.giveItems(BOOK_OF_SIMLON, 3);
						checkBooks(st);
					}
					else
						htmltext = "trader_simplon_q0405_02.htm";
				}
				break;

			case VIVYAN:
				if (cond == 1)
				{
					if (!st.haveQuestItem(BOOK_OF_VIVI))
					{
						htmltext = "vivi_q0405_01.htm";
						st.giveItems(BOOK_OF_VIVI, 1);
						checkBooks(st);
					}
					else
						htmltext = "vivi_q0405_02.htm";
				}
			break;

			case PRAGA:
				if (cond == 1)
				{
					if (st.getQuestItemsCount(BOOK_OF_PRAGA) < 1 && st.getQuestItemsCount(NECKLACE_OF_MOTHER) < 1)
					{
						htmltext = "guard_praga_q0405_01.htm";
						st.giveItems(NECKLACE_OF_MOTHER, 1);
					}
					else if (st.getQuestItemsCount(BOOK_OF_PRAGA) < 1 && st.getQuestItemsCount(NECKLACE_OF_MOTHER) > 0 && st.getQuestItemsCount(PENDANT_OF_MOTHER) < 1)
						htmltext = "guard_praga_q0405_02.htm";
					else if (st.getQuestItemsCount(BOOK_OF_PRAGA) < 1 && st.getQuestItemsCount(NECKLACE_OF_MOTHER) > 0 && st.getQuestItemsCount(PENDANT_OF_MOTHER) > 0) {
						htmltext = "guard_praga_q0405_03.htm";
						st.takeItems(NECKLACE_OF_MOTHER, -1);
						st.takeItems(PENDANT_OF_MOTHER, -1);
						st.giveItems(BOOK_OF_PRAGA, 1);
						checkBooks(st);
					}
					else if (st.getQuestItemsCount(BOOK_OF_PRAGA) > 0)
						htmltext = "guard_praga_q0405_04.htm";
				}
			break;

			case LIONEL:
				if (cond == 2)
					htmltext = "lemoniell_q0405_02.htm";
				else if (cond == 3)
					{
						htmltext = "lemoniell_q0405_01.htm";
						st.giveItems(BOOK_OF_LEMONIELL, 1);
						st.setCond(4);
					}
					else if (cond == 4)
						htmltext = "lemoniell_q0405_03.htm";
					else if (cond == 5)
				{
						htmltext = "lemoniell_q0405_04.htm";
						st.takeItems(CERTIFICATE_OF_GALLINT, -1);
						st.giveItems(LEMONIELLS_COVENANT, 1);
						st.setCond(6);
					}
				else if (cond == 6)
						htmltext = "lemoniell_q0405_05.htm";
			break;

			case GALLINT:
				if (cond == 4)
				{
					htmltext = "gallin_q0405_01.htm";
					st.takeItems(BOOK_OF_LEMONIELL, -1);
					st.giveItems(CERTIFICATE_OF_GALLINT, 1);
					st.setCond(5);
				}
				else if (cond == 5)
					htmltext = "gallin_q0405_02.htm";
			break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		if(npcId == RUIN_ZOMBIE | npcId == RUIN_ZOMBIE_LEADER)
			if(st.getCond() == 1 && st.getQuestItemsCount(PENDANT_OF_MOTHER) < 1)
			{
				st.giveItems(PENDANT_OF_MOTHER, 1, true);
				st.playSound(SOUND_MIDDLE);
			}
		return null;
	}

	@Override
	public String checkStartCondition(NpcInstance npc, Player player)
	{
		if(player.getClassId().getId() == 0x0f)
			return "gigon_q0405_02a.htm";
		return super.checkStartCondition(npc, player);
	}
}