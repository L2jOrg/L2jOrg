package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//Edited by Evil_dnk
//Tested

public final class _408_PathToElvenwizard extends Quest
{
	//npc
	public final int GREENIS = 30157;
	public final int THALIA = 30371;
	public final int ROSELLA = 30414;
	public final int NORTHWIND = 30423;
	//mobs
	public final int DRYAD_ELDER = 20019;
	public final int PINCER_SPIDER = 20466;
	public final int SUKAR_WERERAT_LEADER = 20047;
	//items
	public final int ROGELLIAS_LETTER_ID = 1218;
	public final int RED_DOWN_ID = 1219;
	public final int MAGICAL_POWERS_RUBY_ID = 1220;
	public final int PURE_AQUAMARINE_ID = 1221;
	public final int APPETIZING_APPLE_ID = 1222;
	public final int GOLD_LEAVES_ID = 1223;
	public final int IMMORTAL_LOVE_ID = 1224;
	public final int AMETHYST_ID = 1225;
	public final int NOBILITY_AMETHYST_ID = 1226;
	public final int FERTILITY_PERIDOT_ID = 1229;
	public final int ETERNITY_DIAMOND_ID = 1230;
	public final int CHARM_OF_GRAIN_ID = 1272;
	public final int SAP_OF_WORLD_TREE_ID = 1273;
	public final int LUCKY_POTPOURI_ID = 1274;

	public _408_PathToElvenwizard()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(ROSELLA);

		addTalkId(GREENIS);
		addTalkId(THALIA);
		addTalkId(NORTHWIND);

		addKillId(DRYAD_ELDER);
		addKillId(PINCER_SPIDER);
		addKillId(SUKAR_WERERAT_LEADER);

		addQuestItem(new int[]{
				ROGELLIAS_LETTER_ID,
				FERTILITY_PERIDOT_ID,
				IMMORTAL_LOVE_ID,
				APPETIZING_APPLE_ID,
				CHARM_OF_GRAIN_ID,
				MAGICAL_POWERS_RUBY_ID,
				SAP_OF_WORLD_TREE_ID,
				PURE_AQUAMARINE_ID,
				LUCKY_POTPOURI_ID,
				NOBILITY_AMETHYST_ID,
				GOLD_LEAVES_ID,
				RED_DOWN_ID,
				AMETHYST_ID
		});

		addLevelCheck("rogellia_q0408_04.htm", 19);
		addClassIdCheck("rogellia_q0408_03.htm", 25);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("1"))
		{
				st.setCond(1);
				st.giveItems(FERTILITY_PERIDOT_ID, 1);
				htmltext = "rogellia_q0408_06.htm";
		}
		else if(event.equalsIgnoreCase("408_1"))
		{
			if(st.getQuestItemsCount(MAGICAL_POWERS_RUBY_ID) > 0)
				htmltext = "rogellia_q0408_10.htm";
			else if(st.getQuestItemsCount(MAGICAL_POWERS_RUBY_ID) < 1 && st.getQuestItemsCount(FERTILITY_PERIDOT_ID) > 0)
			{
				st.giveItems(ROGELLIAS_LETTER_ID, 1);
				htmltext = "rogellia_q0408_07.htm";
			}
		}
		else if(event.equalsIgnoreCase("408_4"))
		{
			if(st.getQuestItemsCount(ROGELLIAS_LETTER_ID) > 0)
			{
				st.takeItems(ROGELLIAS_LETTER_ID, -1);
				st.giveItems(CHARM_OF_GRAIN_ID, 1);
				htmltext = "grain_q0408_02.htm";
			}
		}
		else if(event.equalsIgnoreCase("408_2"))
		{
			if(st.getQuestItemsCount(PURE_AQUAMARINE_ID) > 0)
				htmltext = "rogellia_q0408_13.htm";
			else if(st.getQuestItemsCount(PURE_AQUAMARINE_ID) < 1 && st.getQuestItemsCount(FERTILITY_PERIDOT_ID) > 0)
			{
				st.giveItems(APPETIZING_APPLE_ID, 1);
				htmltext = "rogellia_q0408_14.htm";
			}
		}
		else if(event.equalsIgnoreCase("408_5"))
		{
			if(st.getQuestItemsCount(APPETIZING_APPLE_ID) > 0)
			{
				st.takeItems(APPETIZING_APPLE_ID, -1);
				st.giveItems(SAP_OF_WORLD_TREE_ID, 1);
				htmltext = "thalya_q0408_02.htm";
			}
		}
		else if(event.equalsIgnoreCase("408_3"))
			if(st.getQuestItemsCount(NOBILITY_AMETHYST_ID) > 0)
				htmltext = "rogellia_q0408_17.htm";
			else if(st.getQuestItemsCount(NOBILITY_AMETHYST_ID) < 1 && st.getQuestItemsCount(FERTILITY_PERIDOT_ID) > 0)
			{
				st.giveItems(IMMORTAL_LOVE_ID, 1);
				htmltext = "rogellia_q0408_18.htm";
			}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = NO_QUEST_DIALOG;
		int npcId = npc.getNpcId();
		int cond = st.getCond();

		switch (npcId) {
			case ROSELLA:
				if (cond == 0)
				{
					if (st.getQuestItemsCount(ETERNITY_DIAMOND_ID) > 0)
						htmltext = "rogellia_q0408_05.htm";
					else
						htmltext = "rogellia_q0408_01.htm";
				}
				else if (st.getQuestItemsCount(CHARM_OF_GRAIN_ID) > 0)
				{
					if (st.getQuestItemsCount(RED_DOWN_ID) < 5)
						htmltext = "rogellia_q0408_09.htm";
					else if (st.getQuestItemsCount(RED_DOWN_ID) > 4)
						htmltext = "rogellia_q0408_25.htm";
					else if (st.getQuestItemsCount(GOLD_LEAVES_ID) > 4)
						htmltext = "rogellia_q0408_26.htm";
				}
				else if (st.getQuestItemsCount(APPETIZING_APPLE_ID) > 0)
					htmltext = "rogellia_q0408_15.htm";
				else if (st.getQuestItemsCount(IMMORTAL_LOVE_ID) > 0)
					htmltext = "rogellia_q0408_19.htm";
				else if (st.getQuestItemsCount(SAP_OF_WORLD_TREE_ID) > 0 && st.getQuestItemsCount(GOLD_LEAVES_ID) < 5)
					htmltext = "rogellia_q0408_16.htm";
				else if (st.getQuestItemsCount(LUCKY_POTPOURI_ID) > 0)
				{
					if (st.getQuestItemsCount(AMETHYST_ID) < 2)
						htmltext = "rogellia_q0408_20.htm";
					else
						htmltext = "rogellia_q0408_27.htm";
				}
				else if (st.getQuestItemsCount(ROGELLIAS_LETTER_ID) > 0)
					htmltext = "rogellia_q0408_08.htm";
				else if (st.getQuestItemsCount(ROGELLIAS_LETTER_ID) < 1 && st.getQuestItemsCount(APPETIZING_APPLE_ID) < 1 && st.getQuestItemsCount(IMMORTAL_LOVE_ID) < 1 && st.getQuestItemsCount(CHARM_OF_GRAIN_ID) < 1 && st.getQuestItemsCount(SAP_OF_WORLD_TREE_ID) < 1 && st.getQuestItemsCount(LUCKY_POTPOURI_ID) < 1 && st.getQuestItemsCount(FERTILITY_PERIDOT_ID) > 0)
					if (st.getQuestItemsCount(MAGICAL_POWERS_RUBY_ID) < 1 | st.getQuestItemsCount(NOBILITY_AMETHYST_ID) < 1 | st.getQuestItemsCount(PURE_AQUAMARINE_ID) < 1)
						htmltext = "rogellia_q0408_11.htm";
					else if (st.getQuestItemsCount(MAGICAL_POWERS_RUBY_ID) > 0 && st.getQuestItemsCount(NOBILITY_AMETHYST_ID) > 0 && st.getQuestItemsCount(PURE_AQUAMARINE_ID) > 0)
					{
						st.takeItems(MAGICAL_POWERS_RUBY_ID, -1);
						st.takeItems(PURE_AQUAMARINE_ID, st.getQuestItemsCount(PURE_AQUAMARINE_ID));
						st.takeItems(NOBILITY_AMETHYST_ID, st.getQuestItemsCount(NOBILITY_AMETHYST_ID));
						st.takeItems(FERTILITY_PERIDOT_ID, st.getQuestItemsCount(FERTILITY_PERIDOT_ID));
						htmltext = "rogellia_q0408_24.htm";
						if (st.getPlayer().getClassId().isOfLevel(ClassLevel.NONE))
						{
							st.giveItems(ETERNITY_DIAMOND_ID, 1);
							if (!st.getPlayer().getVarBoolean("prof1"))
							{
								st.getPlayer().setVar("prof1", "1", -1);
								st.addExpAndSp(80314, 5910);
							}
						}
						st.finishQuest();
					}
			break;

			case GREENIS:
				if (cond > 0)
				{
					if (st.getQuestItemsCount(ROGELLIAS_LETTER_ID) > 0)
						htmltext = "grain_q0408_01.htm";
					else if (st.getQuestItemsCount(CHARM_OF_GRAIN_ID) > 0)
						if (st.getQuestItemsCount(RED_DOWN_ID) < 5)
							htmltext = "grain_q0408_03.htm";
						else {
							st.takeItems(RED_DOWN_ID, -1);
							st.takeItems(CHARM_OF_GRAIN_ID, -1);
							st.giveItems(MAGICAL_POWERS_RUBY_ID, 1);
							htmltext = "grain_q0408_04.htm";
						}
				}
			break;

			case THALIA:
				if (cond > 0)
				{
					if (st.getQuestItemsCount(APPETIZING_APPLE_ID) > 0)
						htmltext = "thalya_q0408_01.htm";
					else if (st.getQuestItemsCount(SAP_OF_WORLD_TREE_ID) > 0)
						if (st.getQuestItemsCount(GOLD_LEAVES_ID) < 5)
							htmltext = "thalya_q0408_03.htm";
						else
						{
							st.takeItems(GOLD_LEAVES_ID, -1);
							st.takeItems(SAP_OF_WORLD_TREE_ID, -1);
							st.giveItems(PURE_AQUAMARINE_ID, 1);
							htmltext = "thalya_q0408_04.htm";
						}
				}
			break;

			case NORTHWIND:
				if (cond > 0)
				{
					if (st.getQuestItemsCount(IMMORTAL_LOVE_ID) > 0)
					{
						st.takeItems(IMMORTAL_LOVE_ID, -1);
						st.giveItems(LUCKY_POTPOURI_ID, 1);
						htmltext = "northwindel_q0408_01.htm";
					}
					else if (st.getQuestItemsCount(LUCKY_POTPOURI_ID) > 0)
					{
						if (st.getQuestItemsCount(AMETHYST_ID) < 2)
							htmltext = "northwindel_q0408_02.htm";
						else
						{
							st.takeItems(AMETHYST_ID, -1);
							st.takeItems(LUCKY_POTPOURI_ID, -1);
							st.giveItems(NOBILITY_AMETHYST_ID, 1);
							htmltext = "northwindel_q0408_03.htm";
						}
					}
				}
			break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == PINCER_SPIDER)
		{
			if(cond > 0 && st.getQuestItemsCount(CHARM_OF_GRAIN_ID) > 0 && st.getQuestItemsCount(RED_DOWN_ID) < 5)
			{
				st.rollAndGive(RED_DOWN_ID, 1, 1, 5, 70);
			}
		}
		else if(npcId == DRYAD_ELDER)
		{
			if(cond > 0 && st.getQuestItemsCount(SAP_OF_WORLD_TREE_ID) > 0 && st.getQuestItemsCount(GOLD_LEAVES_ID) < 5)
			{
				st.rollAndGive(GOLD_LEAVES_ID, 1, 1, 5, 40);
			}
		}
		else if(npcId == SUKAR_WERERAT_LEADER)
			if(cond > 0 && st.getQuestItemsCount(LUCKY_POTPOURI_ID) > 0 && st.getQuestItemsCount(AMETHYST_ID) < 2)
			{
				st.rollAndGive(AMETHYST_ID, 1, 1, 2, 40);
			}
		return null;
	}

	@Override
	public String checkStartCondition(NpcInstance npc, Player player)
	{
		if(player.getClassId().getId() == 0x1a)
			return "rogellia_q0408_02a.htm";
		return super.checkStartCondition(npc, player);
	}
}