package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//Edited by Evil_dnk
//Tested

public final class _402_PathToKnight extends Quest
{
	//npc
	public final int SIR_KLAUS_VASPER = 30417;
	public final int BIOTIN = 30031;
	public final int LEVIAN = 30037;
	public final int GILBERT = 30039;
	public final int RAYMOND = 30289;
	public final int SIR_COLLIN_WINDAWOOD = 30311;
	public final int BATHIS = 30332;
	public final int BEZIQUE = 30379;
	public final int SIR_ARON_TANFORD = 30653;
	//mobs
	public final int BUGBEAR_RAIDER = 20775;
	public final int UNDEAD_PRIEST = 27024;
	public final int POISON_SPIDER = 20038;
	public final int ARACHNID_TRACKER = 20043;
	public final int ARACHNID_PREDATOR = 20050;
	public final int LANGK_LIZARDMAN = 20030;
	public final int LANGK_LIZARDMAN_SCOUT = 20027;
	public final int LANGK_LIZARDMAN_WARRIOR = 20024;
	public final int GIANT_SPIDER = 20103;
	public final int TALON_SPIDER = 20106;
	public final int BLADE_SPIDER = 20108;
	public final int SILENT_HORROR = 20404;
	//items
	public final int SWORD_OF_RITUAL = 1161;
	public final int COIN_OF_LORDS1 = 1162;
	public final int COIN_OF_LORDS2 = 1163;
	public final int COIN_OF_LORDS3 = 1164;
	public final int COIN_OF_LORDS4 = 1165;
	public final int COIN_OF_LORDS5 = 1166;
	public final int COIN_OF_LORDS6 = 1167;
	public final int GLUDIO_GUARDS_MARK1 = 1168;
	public final int BUGBEAR_NECKLACE = 1169;
	public final int EINHASAD_CHURCH_MARK1 = 1170;
	public final int EINHASAD_CRUCIFIX = 1171;
	public final int GLUDIO_GUARDS_MARK2 = 1172;
	public final int POISON_SPIDER_LEG1 = 1173;
	public final int EINHASAD_CHURCH_MARK2 = 1174;
	public final int LIZARDMAN_TOTEM = 1175;
	public final int GLUDIO_GUARDS_MARK3 = 1176;
	public final int GIANT_SPIDER_HUSK = 1177;
	public final int EINHASAD_CHURCH_MARK3 = 1178;
	public final int HORRIBLE_SKULL = 1179;
	public final int MARK_OF_ESQUIRE = 1271;
	//	# [MOB_ID, REQUIRED, ITEM, NEED_COUNT, CHANCE]
	public final int[][] DROPLIST = {
			{
					BUGBEAR_RAIDER,
					GLUDIO_GUARDS_MARK1,
					BUGBEAR_NECKLACE,
					10,
					100
			},
			{
					UNDEAD_PRIEST,
					EINHASAD_CHURCH_MARK1,
					EINHASAD_CRUCIFIX,
					12,
					100
			},
			{
					POISON_SPIDER,
					GLUDIO_GUARDS_MARK2,
					POISON_SPIDER_LEG1,
					20,
					100
			},
			{
					ARACHNID_TRACKER,
					GLUDIO_GUARDS_MARK2,
					POISON_SPIDER_LEG1,
					20,
					100
			},
			{
					ARACHNID_PREDATOR,
					GLUDIO_GUARDS_MARK2,
					POISON_SPIDER_LEG1,
					20,
					100
			},
			{
					LANGK_LIZARDMAN,
					EINHASAD_CHURCH_MARK2,
					LIZARDMAN_TOTEM,
					20,
					50
			},
			{
					LANGK_LIZARDMAN_SCOUT,
					EINHASAD_CHURCH_MARK2,
					LIZARDMAN_TOTEM,
					20,
					100
			},
			{
					LANGK_LIZARDMAN_WARRIOR,
					EINHASAD_CHURCH_MARK2,
					LIZARDMAN_TOTEM,
					20,
					100
			},
			{
					GIANT_SPIDER,
					GLUDIO_GUARDS_MARK3,
					GIANT_SPIDER_HUSK,
					20,
					40
			},
			{
					TALON_SPIDER,
					GLUDIO_GUARDS_MARK3,
					GIANT_SPIDER_HUSK,
					20,
					40
			},
			{
					BLADE_SPIDER,
					GLUDIO_GUARDS_MARK3,
					GIANT_SPIDER_HUSK,
					20,
					40
			},
			{
					SILENT_HORROR,
					EINHASAD_CHURCH_MARK3,
					HORRIBLE_SKULL,
					10,
					100
			}
	};

	public _402_PathToKnight()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(SIR_KLAUS_VASPER);

		addTalkId(BIOTIN);
		addTalkId(LEVIAN);
		addTalkId(GILBERT);
		addTalkId(RAYMOND);
		addTalkId(SIR_COLLIN_WINDAWOOD);
		addTalkId(BATHIS);
		addTalkId(BEZIQUE);
		addTalkId(SIR_ARON_TANFORD);

		for(int[] element : DROPLIST)
			addKillId(element[0]);

		addQuestItem(new int[]{
				BUGBEAR_NECKLACE,
				EINHASAD_CRUCIFIX,
				POISON_SPIDER_LEG1,
				LIZARDMAN_TOTEM,
				GIANT_SPIDER_HUSK,
				HORRIBLE_SKULL
		});

		addLevelCheck("sir_karrel_vasper_q0402_02.htm", 19);
		addClassIdCheck("sir_karrel_vasper_q0402_03.htm", 0);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		Integer classid = st.getPlayer().getClassId().getId();
		int level = st.getPlayer().getLevel();
		long squire = st.getQuestItemsCount(MARK_OF_ESQUIRE);
		long coin1 = st.getQuestItemsCount(COIN_OF_LORDS1);
		long coin2 = st.getQuestItemsCount(COIN_OF_LORDS2);
		long coin3 = st.getQuestItemsCount(COIN_OF_LORDS3);
		long coin4 = st.getQuestItemsCount(COIN_OF_LORDS4);
		long coin5 = st.getQuestItemsCount(COIN_OF_LORDS5);
		long coin6 = st.getQuestItemsCount(COIN_OF_LORDS6);
		long guards_mark1 = st.getQuestItemsCount(GLUDIO_GUARDS_MARK1);
		long guards_mark2 = st.getQuestItemsCount(GLUDIO_GUARDS_MARK2);
		long guards_mark3 = st.getQuestItemsCount(GLUDIO_GUARDS_MARK3);
		long church_mark1 = st.getQuestItemsCount(EINHASAD_CHURCH_MARK1);
		long church_mark2 = st.getQuestItemsCount(EINHASAD_CHURCH_MARK2);
		long church_mark3 = st.getQuestItemsCount(EINHASAD_CHURCH_MARK3);
		if(event.equalsIgnoreCase("sir_karrel_vasper_q0402_02a.htm"))
		{
				htmltext = "sir_karrel_vasper_q0402_05.htm";
		}
		else if(event.equalsIgnoreCase("sir_karrel_vasper_q0402_08.htm"))
		{
			if(st.getCond() == 0 && classid == 0x00 && level >= 18)
			{
				st.setCond(1);
				st.giveItems(MARK_OF_ESQUIRE, 1);
			}
		}
		else if(event.equalsIgnoreCase("captain_bathia_q0402_02.htm"))
		{
			if(squire > 0 && guards_mark1 < 1 && coin1 < 1)
				st.giveItems(GLUDIO_GUARDS_MARK1, 1);
		}
		else if(event.equalsIgnoreCase("bishop_raimund_q0402_03.htm"))
		{
			if(squire > 0 && church_mark1 < 1 && coin2 < 1)
				st.giveItems(EINHASAD_CHURCH_MARK1, 1);
		}
		else if(event.equalsIgnoreCase("captain_bezique_q0402_02.htm"))
		{
			if(squire > 0 && guards_mark2 < 1 && coin3 < 1)
				st.giveItems(GLUDIO_GUARDS_MARK2, 1);
		}
		else if(event.equalsIgnoreCase("levian_q0402_02.htm"))
		{
			if(squire > 0 && church_mark2 < 1 && coin4 < 1)
				st.giveItems(EINHASAD_CHURCH_MARK2, 1);
		}
		else if(event.equalsIgnoreCase("gilbert_q0402_02.htm"))
		{
			if(squire > 0 && guards_mark3 < 1 && coin5 < 1)
				st.giveItems(GLUDIO_GUARDS_MARK3, 1);
		}
		else if(event.equalsIgnoreCase("quilt_q0402_02.htm"))
		{
			if(squire > 0 && church_mark3 < 1 && coin6 < 1)
				st.giveItems(EINHASAD_CHURCH_MARK3, 1);
		}
		else if(event.equalsIgnoreCase("sir_karrel_vasper_q0402_13.htm") | event.equalsIgnoreCase("sir_karrel_vasper_q0402_14.htm"))
			if(squire > 0 && coin1 + coin2 + coin3 + coin4 + coin5 + coin6 >= 3)
			{
				for(int i = 1162; i < 1179; i++)
					st.takeItems(i, -1);
				st.takeItems(MARK_OF_ESQUIRE, -1);
				if(st.getPlayer().getClassId().isOfLevel(ClassLevel.NONE))
				{
					st.giveItems(SWORD_OF_RITUAL, 1);
					if(!st.getPlayer().getVarBoolean("prof1"))
					{
						st.getPlayer().setVar("prof1", "1", -1);
						st.addExpAndSp(80314, 5087);
					}
				}
				st.finishQuest();
			}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = NO_QUEST_DIALOG;
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		long squire = st.getQuestItemsCount(MARK_OF_ESQUIRE);
		long coin1 = st.getQuestItemsCount(COIN_OF_LORDS1);
		long coin2 = st.getQuestItemsCount(COIN_OF_LORDS2);
		long coin3 = st.getQuestItemsCount(COIN_OF_LORDS3);
		long coin4 = st.getQuestItemsCount(COIN_OF_LORDS4);
		long coin5 = st.getQuestItemsCount(COIN_OF_LORDS5);
		long coin6 = st.getQuestItemsCount(COIN_OF_LORDS6);
		long guards_mark1 = st.getQuestItemsCount(GLUDIO_GUARDS_MARK1);
		long guards_mark2 = st.getQuestItemsCount(GLUDIO_GUARDS_MARK2);
		long guards_mark3 = st.getQuestItemsCount(GLUDIO_GUARDS_MARK3);
		long church_mark1 = st.getQuestItemsCount(EINHASAD_CHURCH_MARK1);
		long church_mark2 = st.getQuestItemsCount(EINHASAD_CHURCH_MARK2);
		long church_mark3 = st.getQuestItemsCount(EINHASAD_CHURCH_MARK3);

		if(npcId == SIR_KLAUS_VASPER)
		{
			if(cond == 0)
			{
				if (st.getQuestItemsCount(SWORD_OF_RITUAL) > 0)
					htmltext = "sir_karrel_vasper_q0402_04.htm";
				else
					htmltext = "sir_karrel_vasper_q0402_01.htm";
			}
			else if(cond == 1 && squire > 0)
				if(coin1 + coin2 + coin3 + coin4 + coin5 + coin6 < 3)
					htmltext = "sir_karrel_vasper_q0402_09.htm";
				else if(coin1 + coin2 + coin3 + coin4 + coin5 + coin6 == 3)
					htmltext = "sir_karrel_vasper_q0402_10.htm";
				else if(coin1 + coin2 + coin3 + coin4 + coin5 + coin6 > 3 && coin1 + coin2 + coin3 + coin4 + coin5 + coin6 < 6)
					htmltext = "sir_karrel_vasper_q0402_11.htm";
				else if(coin1 + coin2 + coin3 + coin4 + coin5 + coin6 == 6)
				{
					htmltext = "sir_karrel_vasper_q0402_12.htm";
					for(int i = 1162; i < 1179; i++)
						st.takeItems(i, -1);
					st.takeItems(MARK_OF_ESQUIRE, -1);
					st.giveItems(SWORD_OF_RITUAL, 1);
					st.finishQuest();
				}
		}
		else if(npcId == BATHIS && cond == 1 && squire > 0)
		{
			if(guards_mark1 < 1 && coin1 < 1)
				htmltext = "captain_bathia_q0402_01.htm";
			else if(guards_mark1 > 0)
			{
				if(st.getQuestItemsCount(BUGBEAR_NECKLACE) < 10)
					htmltext = "captain_bathia_q0402_03.htm";
				else
				{
					htmltext = "captain_bathia_q0402_04.htm";
					st.takeItems(BUGBEAR_NECKLACE, -1);
					st.takeItems(GLUDIO_GUARDS_MARK1, 1);
					st.giveItems(COIN_OF_LORDS1, 1, false);
					st.playSound(SOUND_MIDDLE);
				}
			}
			else if(coin1 > 0)
				htmltext = "captain_bathia_q0402_05.htm";
		}
		else if(npcId == RAYMOND && cond == 1 && squire > 0)
		{
			if(church_mark1 < 1 && coin2 < 1)
				htmltext = "bishop_raimund_q0402_01.htm";
			else if(church_mark1 > 0)
			{
				if(st.getQuestItemsCount(EINHASAD_CRUCIFIX) < 12)
					htmltext = "bishop_raimund_q0402_04.htm";
				else
				{
					htmltext = "bishop_raimund_q0402_05.htm";
					st.takeItems(EINHASAD_CRUCIFIX, -1);
					st.takeItems(EINHASAD_CHURCH_MARK1, 1);
					st.giveItems(COIN_OF_LORDS2, 1, false);
					st.playSound(SOUND_MIDDLE);
				}
			}
			else if(coin2 > 0)
				htmltext = "bishop_raimund_q0402_06.htm";
		}
		else if(npcId == BEZIQUE && cond == 1 && squire > 0)
		{
			if(coin3 < 1 && guards_mark2 < 1)
				htmltext = "captain_bezique_q0402_01.htm";
			else if(guards_mark2 > 0)
			{
				if(st.getQuestItemsCount(POISON_SPIDER_LEG1) < 20)
					htmltext = "captain_bezique_q0402_03.htm";
				else
				{
					htmltext = "captain_bezique_q0402_04.htm";
					st.takeItems(POISON_SPIDER_LEG1, -1);
					st.takeItems(GLUDIO_GUARDS_MARK2, 1);
					st.giveItems(COIN_OF_LORDS3, 1, false);
					st.playSound(SOUND_MIDDLE);
				}
			}
			else if(coin3 > 0)
				htmltext = "captain_bezique_q0402_05.htm";
		}
		else if(npcId == LEVIAN && cond == 1 && squire > 0)
		{
			if(coin4 < 1 && church_mark2 < 1)
				htmltext = "levian_q0402_01.htm";
			else if(church_mark2 > 0)
			{
				if(st.getQuestItemsCount(LIZARDMAN_TOTEM) < 20)
					htmltext = "levian_q0402_03.htm";
				else
				{
					htmltext = "levian_q0402_04.htm";
					st.takeItems(LIZARDMAN_TOTEM, -1);
					st.takeItems(EINHASAD_CHURCH_MARK2, 1);
					st.giveItems(COIN_OF_LORDS4, 1, false);
					st.playSound(SOUND_MIDDLE);
				}
			}
			else if(coin4 > 0)
				htmltext = "levian_q0402_05.htm";
		}
		else if(npcId == GILBERT && cond == 1 && squire > 0)
		{
			if(guards_mark3 < 1 && coin5 < 1)
				htmltext = "gilbert_q0402_01.htm";
			else if(guards_mark3 > 0)
			{
				if(st.getQuestItemsCount(GIANT_SPIDER_HUSK) < 20)
					htmltext = "gilbert_q0402_03.htm";
				else
				{
					htmltext = "gilbert_q0402_04.htm";
					st.takeItems(GIANT_SPIDER_HUSK, -1);
					st.takeItems(GLUDIO_GUARDS_MARK3, 1);
					st.giveItems(COIN_OF_LORDS5, 1, false);
					st.playSound(SOUND_MIDDLE);
				}
			}
			else if(coin5 > 0)
				htmltext = "gilbert_q0402_05.htm";
		}
		else if(npcId == BIOTIN && cond == 1 && squire > 0)
		{
			if(church_mark3 < 1 && coin6 < 1)
				htmltext = "quilt_q0402_01.htm";
			else if(church_mark3 > 0)
			{
				if(st.getQuestItemsCount(HORRIBLE_SKULL) < 10)
					htmltext = "quilt_q0402_03.htm";
				else
				{
					htmltext = "quilt_q0402_04.htm";
					st.takeItems(HORRIBLE_SKULL, -1);
					st.takeItems(EINHASAD_CHURCH_MARK3, 1);
					st.giveItems(COIN_OF_LORDS6, 1, false);
					st.playSound(SOUND_MIDDLE);
				}
			}
			else if(coin6 > 0)
				htmltext = "quilt_q0402_05.htm";
		}
		else if(npcId == SIR_COLLIN_WINDAWOOD && cond == 1 && squire > 0)
			htmltext = "sir_collin_windawood_q0402_01.htm";
		else if(npcId == SIR_ARON_TANFORD && cond == 1 && squire > 0)
			htmltext = "sir_aron_tanford_q0402_01.htm";
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		if (st.getCond() == 1)
		{
			for (int[] element : DROPLIST)
				if (st.getCond() > 0 && npcId == element[0] && st.getQuestItemsCount(element[1]) > 0 && st.getQuestItemsCount(element[2]) < element[3] && Rnd.chance(element[4])) {
					st.giveItems(element[2], 1, true);
					if (st.getQuestItemsCount(element[2]) >= element[3])
						st.playSound(SOUND_MIDDLE);
					else
						st.playSound(SOUND_ITEMGET);
				}
		}
		return null;
	}

	@Override
	public String checkStartCondition(NpcInstance npc, Player player)
	{
		if(player.getClassId().getId() == 0x04)
			return "sir_karrel_vasper_q0402_02a.htm";
		return super.checkStartCondition(npc, player);
	}
}