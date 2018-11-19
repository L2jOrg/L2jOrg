package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.instances.MonsterInstance;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//Edited by Evil_dnk
//Tested

public final class _417_PathToScavenger extends Quest
{
	// ITEMS
	int RING_OF_RAVEN = 1642;
	int PIPIS_LETTER = 1643;
	int ROUTS_TP_SCROLL = 1644;
	int SUCCUBUS_UNDIES = 1645;
	int MIONS_LETTER = 1646;
	int BRONKS_INGOT = 1647;
	int CHARIS_AXE = 1648;
	int ZIMENFS_POTION = 1649;
	int BRONKS_PAY = 1650;
	int CHALIS_PAY = 1651;
	int ZIMENFS_PAY = 1652;
	int BEAR_PIC = 1653;
	int TARANTULA_PIC = 1654;
	int HONEY_JAR = 1655;
	int BEAD = 1656;
	int BEAD_PARCEL = 1657;

	// NPC
	final int Pippi = 30524;
	final int Raut = 30316;
	final int Shari = 30517;
	final int Mion = 30519;
	final int Bronk = 30525;
	final int Zimenf = 30538;
	final int Toma = 30556;
	final int Torai = 30557;

	// MOBS
	int HunterTarantula = 20403;
	int HoneyBear = 27058;
	int PlunderTarantula = 20508;
	int HunterBear = 20777;

	public _417_PathToScavenger()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(Pippi);

		addTalkId(Raut);
		addTalkId(Shari);
		addTalkId(Mion);
		addTalkId(Bronk);
		addTalkId(Zimenf);
		addTalkId(Toma);
		addTalkId(Torai);

		addKillId(HunterTarantula);
		addKillId(HoneyBear);
		addKillId(PlunderTarantula);
		addKillId(HunterBear);

		addQuestItem(CHALIS_PAY);
		addQuestItem(ZIMENFS_PAY);
		addQuestItem(BRONKS_PAY);
		addQuestItem(PIPIS_LETTER);
		addQuestItem(CHARIS_AXE);
		addQuestItem(ZIMENFS_POTION);
		addQuestItem(BRONKS_INGOT);
		addQuestItem(MIONS_LETTER);
		addQuestItem(HONEY_JAR);
		addQuestItem(BEAR_PIC);
		addQuestItem(BEAD_PARCEL);
		addQuestItem(BEAD);
		addQuestItem(TARANTULA_PIC);
		addQuestItem(SUCCUBUS_UNDIES);
		addQuestItem(ROUTS_TP_SCROLL);

		addLevelCheck("collector_pipi_q0417_02.htm", 19);
		addClassIdCheck("collector_pipi_q0417_08.htm", 53);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		int cond = st.getCond();
		if(event.equals("1"))
		{
			st.set("id", "0");
			st.setCond(1);
			st.giveItems(PIPIS_LETTER, 1);
			htmltext = "collector_pipi_q0417_05.htm";
		}
		else if(event.equals("30519_1"))
		{
			if(st.getQuestItemsCount(PIPIS_LETTER) > 0)
			{
				st.takeItems(PIPIS_LETTER, 1);
				st.setCond(2);
				int n = Rnd.get(3);
				if(n == 0)
				{
					htmltext = "trader_mion_q0417_02.htm";
					st.giveItems(ZIMENFS_POTION, 1);
				}
				else if(n == 1)
				{
					htmltext = "trader_mion_q0417_03.htm";
					st.giveItems(CHARIS_AXE, 1);
				}
				else if(n == 2)
				{
					htmltext = "trader_mion_q0417_04.htm";
					st.giveItems(BRONKS_INGOT, 1);
				}
			}
			else
				htmltext = NO_QUEST_DIALOG;
		}
		else if(event.equals("30519_2"))
			htmltext = "trader_mion_q0417_06.htm";
		else if(event.equals("30519_3"))
		{
			htmltext = "trader_mion_q0417_07.htm";
			st.set("id", String.valueOf(st.getInt("id") + 1));
		}
		else if(event.equals("30519_4"))
		{
			int n = Rnd.get(2);
			if(n == 0)
				htmltext = "trader_mion_q0417_06.htm";
			else if(n == 1)
				htmltext = "trader_mion_q0417_11.htm";
		}
		else if(event.equals("30519_5"))
		{
			if(st.getQuestItemsCount(ZIMENFS_POTION, CHARIS_AXE, BRONKS_INGOT) > 0)
			{
				if(st.getInt("id") / 10 < 2)
				{
					htmltext = "trader_mion_q0417_07.htm";
					st.set("id", String.valueOf(st.getInt("id") + 1));
				}
				else if(st.getInt("id") / 10 >= 2 && cond == 0)
				{
					htmltext = "trader_mion_q0417_09.htm";
					if(st.getInt("id") / 10 < 3)
						st.set("id", String.valueOf(st.getInt("id") + 1));
				}
				else if(st.getInt("id") / 10 >= 3 && cond > 0)
				{
					htmltext = "trader_mion_q0417_10.htm";
					st.giveItems(MIONS_LETTER, 1);
					st.takeItems(CHARIS_AXE, 1);
					st.takeItems(ZIMENFS_POTION, 1);
					st.takeItems(BRONKS_INGOT, 1);
				}
			}
			else
				htmltext = NO_QUEST_DIALOG;
		}
		else if(event.equals("30519_6"))
		{
			if(st.getQuestItemsCount(ZIMENFS_PAY) > 0 || st.getQuestItemsCount(CHALIS_PAY) > 0 || st.getQuestItemsCount(BRONKS_PAY) > 0)
			{
				int n = Rnd.get(3);
				st.takeItems(ZIMENFS_PAY, 1);
				st.takeItems(CHALIS_PAY, 1);
				st.takeItems(BRONKS_PAY, 1);
				if(n == 0)
				{
					htmltext = "trader_mion_q0417_02.htm";
					st.giveItems(ZIMENFS_POTION, 1);
				}
				else if(n == 1)
				{
					htmltext = "trader_mion_q0417_03.htm";
					st.giveItems(CHARIS_AXE, 1);
				}
				else if(n == 2)
				{
					htmltext = "trader_mion_q0417_04.htm";
					st.giveItems(BRONKS_INGOT, 1);
				}
			}
			else
				htmltext = NO_QUEST_DIALOG;
		}
		else if(event.equals("30316_1"))
		{
			if(st.getQuestItemsCount(BEAD_PARCEL) > 0)
			{
				htmltext = "raut_q0417_02.htm";
				st.takeItems(BEAD_PARCEL, 1);
				st.giveItems(ROUTS_TP_SCROLL, 1);
				st.setCond(10);
			}
			else
				htmltext = NO_QUEST_DIALOG;
		}
		else if(event.equals("30316_2"))
		{
			if(st.getQuestItemsCount(BEAD_PARCEL) > 0)
			{
				htmltext = "raut_q0417_03.htm";
				st.takeItems(BEAD_PARCEL, 1);
				st.giveItems(ROUTS_TP_SCROLL, 1);
				st.setCond(10);
			}
			else
				htmltext = NO_QUEST_DIALOG;
		}
		else if(event.equals("30557_1"))
			htmltext = "torai_q0417_02.htm";
		else if(event.equals("30557_2"))
			if(st.getQuestItemsCount(ROUTS_TP_SCROLL) > 0)
			{
				htmltext = "torai_q0417_03.htm";
				st.takeItems(ROUTS_TP_SCROLL, 1);
				st.giveItems(SUCCUBUS_UNDIES, 1);
				st.setCond(11);
			}
			else
				htmltext = NO_QUEST_DIALOG;
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
			case Pippi:
				if (cond == 0)
				{
					if(st.getQuestItemsCount(RING_OF_RAVEN) > 0)
						htmltext = "collector_pipi_q0417_04.htm";
					else
						htmltext = "collector_pipi_q0417_01.htm";
				}
				else if (st.getQuestItemsCount(PIPIS_LETTER) > 0)
					htmltext = "collector_pipi_q0417_06.htm";
				else if (st.getQuestItemsCount(PIPIS_LETTER) == 0)
					htmltext = "collector_pipi_q0417_07.htm";
			break;

			case Mion:
				if (st.getQuestItemsCount(PIPIS_LETTER) > 0)
					htmltext = "trader_mion_q0417_01.htm";
				else if (st.getQuestItemsCount(CHARIS_AXE, BRONKS_INGOT, ZIMENFS_POTION) > 0 && st.getInt("id") / 10 == 0)
					htmltext = "trader_mion_q0417_05.htm";
				else if (st.getQuestItemsCount(CHARIS_AXE, BRONKS_INGOT, ZIMENFS_POTION) > 0 && st.getInt("id") / 10 > 0)
					htmltext = "trader_mion_q0417_08.htm";
				else if (st.getQuestItemsCount(CHALIS_PAY, BRONKS_PAY, ZIMENFS_PAY) > 0 && st.getInt("id") < 50)
					htmltext = "trader_mion_q0417_12.htm";
				else if (st.getQuestItemsCount(CHALIS_PAY, BRONKS_PAY, ZIMENFS_PAY) > 0 && st.getInt("id") >= 50)
				{
					htmltext = "trader_mion_q0417_15.htm";
					st.giveItems(MIONS_LETTER, 1);
					st.takeItems(CHALIS_PAY, -1);
					st.takeItems(ZIMENFS_PAY, -1);
					st.takeItems(BRONKS_PAY, -1);
					st.setCond(4);
				}
				else if (st.getQuestItemsCount(MIONS_LETTER) > 0)
					htmltext = "trader_mion_q0417_13.htm";
				else if (st.getQuestItemsCount(BEAR_PIC) > 0 || st.getQuestItemsCount(TARANTULA_PIC) > 0 || st.getQuestItemsCount(BEAD_PARCEL) > 0 || st.getQuestItemsCount(ROUTS_TP_SCROLL) > 0 || st.getQuestItemsCount(SUCCUBUS_UNDIES) > 0)
					htmltext = "trader_mion_q0417_14.htm";
			break;

			case Shari:
				if (st.getQuestItemsCount(CHARIS_AXE) > 0)
				{
					if (st.getInt("id") < 20)
						htmltext = "trader_chali_q0417_01.htm";
					else
						htmltext = "trader_chali_q0417_02.htm";
					st.takeItems(CHARIS_AXE, 1);
					st.giveItems(CHALIS_PAY, 1);
					if (st.getInt("id") >= 50)
						st.setCond(3);
					st.set("id", st.getInt("id") + 10);
				} else if (st.getQuestItemsCount(CHALIS_PAY) >= 1)
					htmltext = "trader_chali_q0417_03.htm";
			break;

			case Bronk:
				if (st.getQuestItemsCount(BRONKS_INGOT) >= 1)
				{
					if (st.getInt("id") < 20)
						htmltext = "head_blacksmith_bronk_q0417_01.htm";
					else
						htmltext = "head_blacksmith_bronk_q0417_02.htm";
					st.takeItems(BRONKS_INGOT, 1);
					st.giveItems(BRONKS_PAY, 1);
					if (st.getInt("id") >= 50)
						st.setCond(3);
					st.set("id", st.getInt("id") + 10);
				}
				else if (st.getQuestItemsCount(BRONKS_PAY) >= 1)
					htmltext = "head_blacksmith_bronk_q0417_03.htm";
			break;

			case Zimenf:
				if (st.getQuestItemsCount(ZIMENFS_POTION) >= 1)
				{
					if (st.getInt("id") < 20)
						htmltext = "zimenf_priest_of_earth_q0417_01.htm";
					else
						htmltext = "zimenf_priest_of_earth_q0417_02.htm";
					st.takeItems(ZIMENFS_POTION, 1);
					st.giveItems(ZIMENFS_PAY, 1);
					if (st.getInt("id") >= 50)
						st.setCond(3);
					st.set("id", st.getInt("id") + 10);
				}
				else if (st.getQuestItemsCount(ZIMENFS_PAY) >= 1)
					htmltext = "zimenf_priest_of_earth_q0417_03.htm";
			break;

			case Toma:
				if (st.getQuestItemsCount(MIONS_LETTER) >= 1)
				{
					htmltext = "master_toma_q0417_01.htm";
					st.takeItems(MIONS_LETTER, -1);
					st.giveItems(BEAR_PIC, 1);
					st.setCond(5);
					st.set("id", String.valueOf(0));
				}
				else if (st.getQuestItemsCount(BEAR_PIC) >= 1 && st.getQuestItemsCount(HONEY_JAR) < 5)
					htmltext = "master_toma_q0417_02.htm";
				else if (st.getQuestItemsCount(BEAR_PIC) >= 1 && st.getQuestItemsCount(HONEY_JAR) >= 5)
				{
					htmltext = "master_toma_q0417_03.htm";
					st.takeItems(HONEY_JAR, st.getQuestItemsCount(HONEY_JAR));
					st.takeItems(BEAR_PIC, 1);
					st.giveItems(TARANTULA_PIC, 1);
					st.setCond(7);
				}
				else if (st.getQuestItemsCount(TARANTULA_PIC) >= 1 && st.getQuestItemsCount(BEAD) < 20)
					htmltext = "master_toma_q0417_04.htm";
				else if (st.getQuestItemsCount(TARANTULA_PIC) >= 1 && st.getQuestItemsCount(BEAD) >= 20)
				{
					htmltext = "master_toma_q0417_05.htm";
					st.takeItems(BEAD, st.getQuestItemsCount(BEAD));
					st.takeItems(TARANTULA_PIC, 1);
					st.giveItems(BEAD_PARCEL, 1);
					st.setCond(9);
				}
				else if (st.getQuestItemsCount(BEAD_PARCEL) > 0)
					htmltext = "master_toma_q0417_06.htm";
				else if (st.getQuestItemsCount(ROUTS_TP_SCROLL) > 0 || st.getQuestItemsCount(SUCCUBUS_UNDIES) > 0)
					htmltext = "master_toma_q0417_07.htm";
			break;

			case Raut:
				if (st.getQuestItemsCount(BEAD_PARCEL) >= 1)
					htmltext = "raut_q0417_01.htm";
				else if (st.getQuestItemsCount(ROUTS_TP_SCROLL) >= 1)
					htmltext = "raut_q0417_04.htm";
				else if (st.getQuestItemsCount(SUCCUBUS_UNDIES) >= 1)
				{
					htmltext = "raut_q0417_05.htm";
					st.takeItems(SUCCUBUS_UNDIES, 1);
					if (st.getPlayer().getClassId().isOfLevel(ClassLevel.NONE))
					{
						st.giveItems(RING_OF_RAVEN, 1);
						if (!st.getPlayer().getVarBoolean("prof1"))
						{
							st.getPlayer().setVar("prof1", "1", -1);
							st.addExpAndSp(80314, 5087);
						}
					}
					st.finishQuest();
				}
			break;
			case Torai:
				if (st.getQuestItemsCount(ROUTS_TP_SCROLL) >= 1)
					htmltext = "torai_q0417_01.htm";
			break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		MonsterInstance mob = (MonsterInstance) npc;
		boolean cond = st.getCond() > 0;
		if(npcId == HunterBear)
		{
			if(cond && st.getQuestItemsCount(BEAR_PIC) >= 1 && st.getQuestItemsCount(HONEY_JAR) < 5 && Rnd.chance(20))
				st.addSpawn(HoneyBear);
		}
		else if(npcId == HoneyBear)
		{
			if(cond && st.getQuestItemsCount(BEAR_PIC) >= 1 && st.getQuestItemsCount(HONEY_JAR) < 5)
				if(mob.isSpoiled())
				{
					st.giveItems(HONEY_JAR, 1, true);
					if(st.getQuestItemsCount(HONEY_JAR) >= 5)
						st.setCond(6);
					else
						st.playSound(SOUND_ITEMGET);
				}
		}
		else if(npcId == HunterTarantula || npcId == PlunderTarantula)
			if(cond && st.getQuestItemsCount(TARANTULA_PIC) >= 1 && st.getQuestItemsCount(BEAD) < 20)
				if(mob.isSpoiled())
					if(Rnd.chance(50))
					{
						st.giveItems(BEAD, 1, true);
						if(st.getQuestItemsCount(BEAD) >= 20)
							st.setCond(8);
						else
							st.playSound(SOUND_ITEMGET);
					}
		return null;
	}

	@Override
	public String checkStartCondition(NpcInstance npc, Player player)
	{
		if(player.getClassId().getId() == 0x36)
			return "collector_pipi_q0417_02a.htm";
		return super.checkStartCondition(npc, player);
	}
}