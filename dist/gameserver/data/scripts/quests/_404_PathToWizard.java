package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//Edited by Evil_dnk
//Tested

public final class _404_PathToWizard extends Quest
{
	//npc
	public final int PARINA = 30391;
	public final int EARTH_SNAKE = 30409;
	public final int WASTELAND_LIZARDMAN = 30410;
	public final int FLAME_SALAMANDER = 30411;
	public final int WIND_SYLPH = 30412;
	public final int WATER_UNDINE = 30413;
	//mobs
	public final int RED_BEAR = 20021;
	public final int RATMAN_WARRIOR = 20359;
	public final int WATER_SEER = 27030;
	//items
	public final int MAP_OF_LUSTER_ID = 1280;
	public final int KEY_OF_FLAME_ID = 1281;
	public final int FLAME_EARING_ID = 1282;
	public final int BROKEN_BRONZE_MIRROR_ID = 1283;
	public final int WIND_FEATHER_ID = 1284;
	public final int WIND_BANGEL_ID = 1285;
	public final int RAMAS_DIARY_ID = 1286;
	public final int SPARKLE_PEBBLE_ID = 1287;
	public final int WATER_NECKLACE_ID = 1288;
	public final int RUST_GOLD_COIN_ID = 1289;
	public final int RED_SOIL_ID = 1290;
	public final int EARTH_RING_ID = 1291;
	public final int BEAD_OF_SEASON_ID = 1292;

	public _404_PathToWizard()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(PARINA);

		addTalkId(EARTH_SNAKE);
		addTalkId(WASTELAND_LIZARDMAN);
		addTalkId(FLAME_SALAMANDER);
		addTalkId(WIND_SYLPH);
		addTalkId(WATER_UNDINE);

		addKillId(RED_BEAR);
		addKillId(RATMAN_WARRIOR);
		addKillId(WATER_SEER);

		addQuestItem(new int[]{
				KEY_OF_FLAME_ID,
				MAP_OF_LUSTER_ID,
				WIND_FEATHER_ID,
				BROKEN_BRONZE_MIRROR_ID,
				SPARKLE_PEBBLE_ID,
				RAMAS_DIARY_ID,
				RED_SOIL_ID,
				RUST_GOLD_COIN_ID,
				FLAME_EARING_ID,
				WIND_BANGEL_ID,
				WATER_NECKLACE_ID,
				EARTH_RING_ID
		});

		addLevelCheck("parina_q0404_02.htm", 19);
		addClassIdCheck("parina_q0404_01.htm", 10);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("1"))
		{
			htmltext = "parina_q0404_08.htm";
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("30410_1"))
			if(st.getQuestItemsCount(WIND_FEATHER_ID) < 1)
			{
				htmltext = "lizardman_of_wasteland_q0404_03.htm";
				st.giveItems(WIND_FEATHER_ID, 1);
				st.setCond(6);
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
			case PARINA:
				if (cond == 0)
				{
					if (st.getQuestItemsCount(BEAD_OF_SEASON_ID) > 0)
						htmltext = "parina_q0404_03.htm";
					else
						htmltext = "parina_q0404_04.htm";
				}
				else if (cond > 0 && cond < 13)
					htmltext = "parina_q0404_05.htm";
				else if (cond == 13)
				{
					htmltext = "parina_q0404_06.htm";
					st.takeItems(FLAME_EARING_ID, st.getQuestItemsCount(FLAME_EARING_ID));
					st.takeItems(WIND_BANGEL_ID, st.getQuestItemsCount(WIND_BANGEL_ID));
					st.takeItems(WATER_NECKLACE_ID, st.getQuestItemsCount(WATER_NECKLACE_ID));
					st.takeItems(EARTH_RING_ID, st.getQuestItemsCount(EARTH_RING_ID));
					if (st.getPlayer().getClassId().isOfLevel(ClassLevel.NONE))
					{
						if (st.getQuestItemsCount(BEAD_OF_SEASON_ID) < 1)
							st.giveItems(BEAD_OF_SEASON_ID, 1);
						if (!st.getPlayer().getVarBoolean("prof1"))
						{
							st.getPlayer().setVar("prof1", "1", -1);
							st.addExpAndSp(80314, 5910);
						}
					}
					st.finishQuest();
				}
			break;

			case FLAME_SALAMANDER:
				if (cond == 1)
				{
					st.giveItems(MAP_OF_LUSTER_ID, 1);
					htmltext = "flame_salamander_q0404_01.htm";
					st.setCond(2);
				}
				else if (cond == 2)
					htmltext = "flame_salamander_q0404_02.htm";
				else if (cond == 3)
				{
					st.takeItems(KEY_OF_FLAME_ID, -1);
					st.takeItems(MAP_OF_LUSTER_ID, -1);
					if (st.getQuestItemsCount(FLAME_EARING_ID) < 1)
						st.giveItems(FLAME_EARING_ID, 1);
					htmltext = "flame_salamander_q0404_03.htm";
					st.setCond(4);
				}
				else if (cond > 4)
					htmltext = "flame_salamander_q0404_04.htm";
			break;

			case WIND_SYLPH:
				if (cond == 4)
				{
					st.giveItems(BROKEN_BRONZE_MIRROR_ID, 1);
					htmltext = "wind_sylph_q0404_01.htm";
					st.setCond(5);
				}
				else if (cond == 5)
					htmltext = "wind_sylph_q0404_02.htm";
				else if (cond == 6)
				{
					st.takeItems(WIND_FEATHER_ID, -1);
					st.takeItems(BROKEN_BRONZE_MIRROR_ID, -1);
					if (st.getQuestItemsCount(WIND_BANGEL_ID) < 1)
						st.giveItems(WIND_BANGEL_ID, 1);
					htmltext = "wind_sylph_q0404_03.htm";
					st.setCond(7);
				} else if (cond > 7)
					htmltext = "wind_sylph_q0404_04.htm";
			break;

			case WASTELAND_LIZARDMAN:
				if (cond == 5)
					htmltext = "lizardman_of_wasteland_q0404_01.htm";
				else if (cond == 6)
					htmltext = "lizardman_of_wasteland_q0404_04.htm";
				break;

			case WATER_UNDINE:
				if (cond == 7)
				{
					st.giveItems(RAMAS_DIARY_ID, 1);
					htmltext = "water_undine_q0404_01.htm";
					st.setCond(8);
				}
				else if (cond == 8)
					htmltext = "water_undine_q0404_02.htm";
				else if (cond == 9)
				{
					st.takeItems(SPARKLE_PEBBLE_ID, -1);
					st.takeItems(RAMAS_DIARY_ID, -1);
					if (st.getQuestItemsCount(WATER_NECKLACE_ID) < 1)
						st.giveItems(WATER_NECKLACE_ID, 1);
					htmltext = "water_undine_q0404_03.htm";
					st.setCond(10);
				} else if (cond > 10)
					htmltext = "water_undine_q0404_04.htm";
			break;

			case EARTH_SNAKE:

				if (cond == 10)
				{
					st.giveItems(RUST_GOLD_COIN_ID, 1);
					htmltext = "earth_snake_q0404_01.htm";
					st.setCond(11);
				}
				else if (cond == 11)
					htmltext = "earth_snake_q0404_02.htm";
				else if (cond == 12)
				{
					st.takeItems(RED_SOIL_ID, -1);
					st.takeItems(RUST_GOLD_COIN_ID, -1);
					if (st.getQuestItemsCount(EARTH_RING_ID) < 1)
						st.giveItems(EARTH_RING_ID, 1);
					htmltext = "earth_snake_q0404_04.htm";
					st.setCond(13);
				}
				else if (cond > 13)
					htmltext = "earth_snake_q0404_04.htm";
			break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == RATMAN_WARRIOR)
		{
			if(cond == 2)
			{
				st.giveItems(KEY_OF_FLAME_ID, 1);
				st.setCond(3);
			}
		}
		else if(npcId == WATER_SEER)
		{
			if(cond == 8 && st.getQuestItemsCount(SPARKLE_PEBBLE_ID) < 2)
			{
				st.giveItems(SPARKLE_PEBBLE_ID, 1, true);
				if(st.getQuestItemsCount(SPARKLE_PEBBLE_ID) >= 2)
					st.setCond(9);
				else
					st.playSound(SOUND_ITEMGET);
			}
		}
		else if(npcId == RED_BEAR)
			if(cond == 11)
			{
				st.giveItems(RED_SOIL_ID, 1);
				st.setCond(12);
			}
		return null;
	}

	@Override
	public String checkStartCondition(NpcInstance npc, Player player)
	{
		if(player.getClassId().getId() == 0x0b)
			return "parina_q0404_02a.htm";
		return super.checkStartCondition(npc, player);
	}
}
