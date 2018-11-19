package quests;

import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

/**
 * @reworked by Bonux
**/
public final class _231_TestOfTheMaestro extends Quest
{
	// NPC's
	private static final int IRON_GATES_LOCKIRIN = 30531;
	private static final int GOLDEN_WHEELS_SPIRON = 30532;
	private static final int SILVER_SCALES_BALANKI = 30533;
	private static final int BRONZE_KEYS_KEEF = 30534;
	private static final int GRAY_PILLAR_MEMBER_FILAUR = 30535;
	private static final int BLACK_ANVILS_ARIN = 30536;
	private static final int MASTER_TOMA = 30556;
	private static final int CHIEF_CROTO = 30671;
	private static final int JAILER_DUBABAH = 30672;
	private static final int RESEARCHER_LORAIN = 30673;

	// Monster's
	private static final int KING_BUGBEAR = 20150;
	private static final int GIANT_MIST_LEECH = 20225;
	private static final int STINGER_WASP = 20229;
	private static final int MARSH_SPIDER = 20233;

	// Quest Monster
	private static final int EVIL_EYE_LORD = 27133;

	// Item's
	private static final int RECOMMENDATION_OF_BALANKI = 2864;
	private static final int RECOMMENDATION_OF_FILAUR = 2865;
	private static final int RECOMMENDATION_OF_ARIN = 2866;
	private static final int LETTER_OF_SOLDER_DERACHMENT = 2868;
	private static final int PAINT_OF_KAMURU = 2869;
	private static final int NECKLACE_OF_KAMUTU = 2870;
	private static final int PAINT_OF_TELEPORT_DEVICE = 2871;
	private static final int TELEPORT_DEVICE = 2872;
	private static final int ARCHITECTURE_OF_CRUMA = 2873;
	private static final int REPORT_OF_CRUMA = 2874;
	private static final int INGREDIENTS_OF_ANTIDOTE = 2875;
	private static final int STINGER_WASP_NEEDLE = 2876;
	private static final int MARSH_SPIDERS_WEB = 2877;
	private static final int BLOOD_OF_LEECH = 2878;
	private static final int BROKEN_TELEPORT_DEVICE = 2916;
	private static final int MARK_OF_MAESTRO = 2867;
	private static final int DIMENSIONAL_DIAMOND = 7562;

	// Reward's
	private static final int EXP_REWARD = 248400; // EXP reward count
	private static final int SP_REWARD = 0; // SP reward count

	// Condition's
	private static final int MIN_LEVEL = 39;

	public _231_TestOfTheMaestro()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(IRON_GATES_LOCKIRIN);
		addTalkId(IRON_GATES_LOCKIRIN, GOLDEN_WHEELS_SPIRON, SILVER_SCALES_BALANKI, BRONZE_KEYS_KEEF, GRAY_PILLAR_MEMBER_FILAUR, BLACK_ANVILS_ARIN, MASTER_TOMA, CHIEF_CROTO, JAILER_DUBABAH, RESEARCHER_LORAIN);
		addKillId(KING_BUGBEAR, GIANT_MIST_LEECH, STINGER_WASP, MARSH_SPIDER, EVIL_EYE_LORD);
		addQuestItem(RECOMMENDATION_OF_BALANKI, RECOMMENDATION_OF_FILAUR, RECOMMENDATION_OF_ARIN, LETTER_OF_SOLDER_DERACHMENT, PAINT_OF_KAMURU, NECKLACE_OF_KAMUTU, PAINT_OF_TELEPORT_DEVICE, TELEPORT_DEVICE, ARCHITECTURE_OF_CRUMA, REPORT_OF_CRUMA, INGREDIENTS_OF_ANTIDOTE, STINGER_WASP_NEEDLE, MARSH_SPIDERS_WEB, BLOOD_OF_LEECH, BROKEN_TELEPORT_DEVICE);

		addClassIdCheck("30531-02.htm", ClassId.ARTISAN);
		addLevelCheck("30531-01.htm", MIN_LEVEL);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("30531-04.htm"))
		{
			st.setCond(1);
			st.set("sub_cond", 1);
		}
		else if(event.equalsIgnoreCase("30533-02.htm"))
		{
			st.set("sub_cond", 2);
		}
		else if(event.equalsIgnoreCase("30556-05.htm"))
		{
			if(st.haveQuestItem(PAINT_OF_TELEPORT_DEVICE))
			{
				st.giveItems(BROKEN_TELEPORT_DEVICE, 1);
				st.takeItems(PAINT_OF_TELEPORT_DEVICE, 1);
				st.getPlayer().teleToLocation(140352, -194133, -3146);
				st.startQuestTimer("SPAWN_KING_BUGBEAR", 5000, npc);
			}
		}
		else if(event.equalsIgnoreCase("30671-02.htm"))
		{
			st.giveItems(PAINT_OF_KAMURU, 1);
		}
		else if(event.equalsIgnoreCase("30673-04.htm"))
		{
			if(st.haveQuestItem(INGREDIENTS_OF_ANTIDOTE) && st.haveQuestItem(STINGER_WASP_NEEDLE, 10) && st.haveQuestItem(MARSH_SPIDERS_WEB, 10) && st.haveQuestItem(BLOOD_OF_LEECH, 10))
			{
				st.giveItems(REPORT_OF_CRUMA, 1);
				st.takeItems(STINGER_WASP_NEEDLE, -1);
				st.takeItems(MARSH_SPIDERS_WEB, -1);
				st.takeItems(BLOOD_OF_LEECH, -1);
				st.takeItems(INGREDIENTS_OF_ANTIDOTE, 1);
			}
		}
		else if(event.equalsIgnoreCase("SPAWN_KING_BUGBEAR"))
		{
			st.getPlayer().setNonAggroTime(0); // Иначе после ТП не будут атаковать.
			for(int i = 0; i < 3; i++)
			{
				NpcInstance kingBugbear = addSpawn(KING_BUGBEAR, st.getPlayer().getLoc(), 100, 200000);
				kingBugbear.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, st.getPlayer(), 1000);
			}
			return null;
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		if(st.haveQuestItem(MARK_OF_MAESTRO))
			return  COMPLETED_DIALOG;

		String htmltext = NO_QUEST_DIALOG;

		final int npcId = npc.getNpcId();
		final int cond = st.getCond();
		final int subCond = st.getInt("sub_cond");
		switch(npcId)
		{
			case IRON_GATES_LOCKIRIN:
			{
				if(cond == 0)
					htmltext = "30531-03.htm";
				else if(cond == 2)
				{
					if(st.haveQuestItem(RECOMMENDATION_OF_BALANKI) && st.haveQuestItem(RECOMMENDATION_OF_FILAUR) && st.haveQuestItem(RECOMMENDATION_OF_ARIN))
					{
						htmltext = "30531-06.htm";

						st.giveItems(MARK_OF_MAESTRO, 1);
						st.addExpAndSp(EXP_REWARD, SP_REWARD);
						st.finishQuest();
					}
					else if(subCond >= 1)
						htmltext = "30531-05.htm";
				}
				break;
			}
			case GOLDEN_WHEELS_SPIRON:
			{
				if(cond == 1)
					htmltext = "30532-01.htm";
				break;
			}
			case SILVER_SCALES_BALANKI:
			{
				if(cond == 1)
				{
					if(subCond == 1 && !st.haveQuestItem(RECOMMENDATION_OF_BALANKI))
					{
						htmltext = "30533-01.htm";
					}
					else if(subCond == 2)
					{
						if(!st.haveQuestItem(LETTER_OF_SOLDER_DERACHMENT))
							htmltext = "30533-03.htm";
						else
						{
							htmltext = "30533-04.htm";

							st.giveItems(RECOMMENDATION_OF_BALANKI, 1);
							st.takeItems(LETTER_OF_SOLDER_DERACHMENT, 1);
							st.set("sub_cond", 1);
		
							if(st.haveQuestItem(RECOMMENDATION_OF_ARIN) && st.haveQuestItem(RECOMMENDATION_OF_FILAUR))
							{
								st.setCond(2);
								st.unset("sub_cond");
							}
						}
					}
					else if(st.haveQuestItem(RECOMMENDATION_OF_BALANKI))
						htmltext = "30533-05.htm";
				}
				break;
			}
			case BRONZE_KEYS_KEEF:
			{
				if(cond == 1)
					htmltext = "30534-01.htm";
				break;
			}
			case GRAY_PILLAR_MEMBER_FILAUR:
			{
				if(cond == 1)
				{
					if(subCond == 1 && !st.haveQuestItem(RECOMMENDATION_OF_FILAUR))
					{
						htmltext = "30535-01.htm";

						st.giveItems(ARCHITECTURE_OF_CRUMA, 1);
						st.set("sub_cond", 4);
					}
					else if(subCond == 4)
					{
						if(st.haveQuestItem(ARCHITECTURE_OF_CRUMA) && !st.haveQuestItem(REPORT_OF_CRUMA))
							htmltext = "30535-02.htm";
						else if(st.haveQuestItem(REPORT_OF_CRUMA) && !st.haveQuestItem(ARCHITECTURE_OF_CRUMA))
						{
							htmltext = "30535-03.htm";

							st.giveItems(RECOMMENDATION_OF_FILAUR, 1);
							st.takeItems(REPORT_OF_CRUMA, 1);
							st.set("sub_cond", 1);

							if(st.haveQuestItem(RECOMMENDATION_OF_BALANKI) && st.haveQuestItem(RECOMMENDATION_OF_ARIN))
							{
								st.setCond(2);
								st.unset("sub_cond");
							}
						}
					}
					else if(st.haveQuestItem(RECOMMENDATION_OF_FILAUR))
						htmltext = "30535-04.htm";
				}
				break;
			}
			case BLACK_ANVILS_ARIN:
			{
				if(cond == 1)
				{
					if(subCond == 1 && !st.haveQuestItem(RECOMMENDATION_OF_ARIN))
					{
						htmltext = "30536-01.htm";

						st.giveItems(PAINT_OF_TELEPORT_DEVICE, 1);
						st.set("sub_cond", 3);
					}
					else if(subCond == 3)
					{
						if(st.haveQuestItem(PAINT_OF_TELEPORT_DEVICE) && !st.haveQuestItem(TELEPORT_DEVICE))
							htmltext = "30536-02.htm";
						else if(st.haveQuestItem(TELEPORT_DEVICE, 5))
						{
							htmltext = "30536-03.htm";

							st.giveItems(RECOMMENDATION_OF_ARIN, 1);
							st.takeItems(TELEPORT_DEVICE, -1);
							st.set("sub_cond", 1);

							if(st.haveQuestItem(RECOMMENDATION_OF_BALANKI) && st.haveQuestItem(RECOMMENDATION_OF_FILAUR))
							{
								st.setCond(2);
								st.unset("sub_cond");
							}
						}
					}
					else if(st.haveQuestItem(RECOMMENDATION_OF_ARIN))
						htmltext = "30536-04.htm";
				}
				break;
			}
			case MASTER_TOMA:
			{
				if(cond == 1)
				{
					if(subCond == 3)
					{
						if(st.haveQuestItem(PAINT_OF_TELEPORT_DEVICE))
							htmltext = "30556-01.htm";
						else if(st.haveQuestItem(BROKEN_TELEPORT_DEVICE))
						{
							st.giveItems(TELEPORT_DEVICE, 5);
							st.takeItems(BROKEN_TELEPORT_DEVICE, 1);
							htmltext = "30556-06.htm";
						}
						else if(st.haveQuestItem(TELEPORT_DEVICE, 5))
							htmltext = "30556-07.htm";
					}
				}
				break;
			}
			case CHIEF_CROTO:
			{
				if(cond == 1)
				{
					if(subCond == 2 && !st.haveQuestItem(PAINT_OF_KAMURU) && !st.haveQuestItem(NECKLACE_OF_KAMUTU) && !st.haveQuestItem(LETTER_OF_SOLDER_DERACHMENT))
						htmltext = "30671-01.htm";
					else if(st.haveQuestItem(PAINT_OF_KAMURU) && !st.haveQuestItem(NECKLACE_OF_KAMUTU))
						htmltext = "30671-03.htm";
					else if(st.haveQuestItem(NECKLACE_OF_KAMUTU))
					{
						htmltext = "30671-04.htm";

						st.giveItems(LETTER_OF_SOLDER_DERACHMENT, 1);
						st.takeItems(NECKLACE_OF_KAMUTU, 1);
						st.takeItems(PAINT_OF_KAMURU, 1);
					}
					else if(st.haveQuestItem(LETTER_OF_SOLDER_DERACHMENT))
						htmltext = "30671-05.htm";
				}
				break;
			}
			case JAILER_DUBABAH:
			{
				if(cond == 1)
				{
					if(st.haveQuestItem(PAINT_OF_KAMURU))
						htmltext = "30672-01.htm";
				}
				break;
			}
			case RESEARCHER_LORAIN:
			{
				if(cond == 1)
				{
					if(subCond == 4)
					{
						if(st.haveQuestItem(ARCHITECTURE_OF_CRUMA) && !st.haveQuestItem(INGREDIENTS_OF_ANTIDOTE) && !st.haveQuestItem(REPORT_OF_CRUMA))
						{
							st.giveItems(INGREDIENTS_OF_ANTIDOTE, 1);
							st.takeItems(ARCHITECTURE_OF_CRUMA, 1);
							htmltext = "30673-01.htm";
						}
						else if(st.haveQuestItem(INGREDIENTS_OF_ANTIDOTE) && !st.haveQuestItem(REPORT_OF_CRUMA))
						{
							if(st.haveQuestItem(STINGER_WASP_NEEDLE, 10) && st.haveQuestItem(MARSH_SPIDERS_WEB, 10) && st.haveQuestItem(BLOOD_OF_LEECH, 10))
								htmltext = "30673-03.htm";
							else
								htmltext = "30673-02.htm";
						}
						else if(st.haveQuestItem(REPORT_OF_CRUMA))
							htmltext = "30673-05.htm";
					}
				}
				break;
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		final int npcId = npc.getNpcId();
		final int cond = st.getCond();
		final int subCond = st.getInt("sub_cond");
		if(cond == 1)
		{
			switch(npcId)
			{
				case GIANT_MIST_LEECH:
				{
					if(subCond == 4 && st.haveQuestItem(INGREDIENTS_OF_ANTIDOTE))
						st.rollAndGive(BLOOD_OF_LEECH, 1, 1, 10, 100);
					break;
				}
				case STINGER_WASP:
				{
					if(subCond == 4 && st.haveQuestItem(INGREDIENTS_OF_ANTIDOTE))
						st.rollAndGive(STINGER_WASP_NEEDLE, 1, 1, 10, 100);
					break;
				}
				case MARSH_SPIDER:
				{
					if(subCond == 4 && st.haveQuestItem(INGREDIENTS_OF_ANTIDOTE))
						st.rollAndGive(MARSH_SPIDERS_WEB, 1, 1, 10, 100);
					break;
				}
				case EVIL_EYE_LORD:
				{
					if(subCond == 2 && st.haveQuestItem(PAINT_OF_KAMURU))
						st.rollAndGive(NECKLACE_OF_KAMUTU, 1, 1, 1, 100);
					break;
				}
			}
		}
		return null;
	}
}