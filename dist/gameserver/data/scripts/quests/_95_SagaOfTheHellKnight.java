package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.network.l2.s2c.MagicSkillUse;

//SanyaDC


public final class _95_SagaOfTheHellKnight extends Quest
{
	//npc
	public final int MORDRED = 31582;
	public final int RURU = 34271;
	public final int LANCER = 30477;
	public final int LANCER1 = 34271;
	public final int VALDWTEIN = 31599;
	public final int KAMEN_POZNANIA1 = 31646;
	public final int KAMEN_POZNANIA2 = 31648;
	public final int KAMEN_POZNANIA3 = 31653;
	public final int KAMEN_POZNANIA4 = 31654;	
	//mobs
	public final int LEDANOI_MONSTR = 27316;
	public final int DUH_UTOPL = 27317;
	public final int DUWA_HOLODA = 27318;
	public final int PRIZ_ODINOCHESTVA = 27319;
	public final int CHUDIWE_HOLODA = 27320;
	public final int DUH_HOLODA = 27321;
	public final int SMOTRITEL_SVATOGO_ZAKONA = 27215;
	public final int ARHANGEL_BOGOBOREC = 27257;
	public final int SMOTRITEL_TOPI = 21650;
	public final int PULAYWIY_DREIK = 21651;
	public final int PLAMENNIY_IFRIT = 21652;
	public final int IKEDIT = 21653;
	public final int ARHONT_HALIWI = 27219;
	public final int LORD_SMERTI_HALLET = 25220;
	public static final String A_LIST = "a_list";
	public static final String B_LIST = "a_list";
	public static final String C_LIST = "a_list";
	public static final String D_LIST = "a_list";
	//items	
	public final int BOOKGOLDLION = 90038;
	public final int OSKOLOK_KRI_HOLODA = 49829;
	public final int ZNAK_HALIWI = 7510;
	public final int AMULET_REZONANSA_PERVIY = 7293;
	public final int AMULET_REZONANSA_VTOROI = 7324;
	public final int AMULET_REZONANSA_TRETIY = 7355;
	public final int AMULET_REZONANSA_CHETVERTIY = 7386;

	//	# [MOB_ID, REQUIRED, ITEM, NEED_COUNT, CHANCE]
	public final int[][] DROPLIST =
	{
			{ LEDANOI_MONSTR, OSKOLOK_KRI_HOLODA, 50, 100 },
			{ DUH_UTOPL, OSKOLOK_KRI_HOLODA, 50, 100 },
			{ DUWA_HOLODA, OSKOLOK_KRI_HOLODA, 50, 100 },
			{ PRIZ_ODINOCHESTVA, OSKOLOK_KRI_HOLODA, 50, 100 },
			{ CHUDIWE_HOLODA, OSKOLOK_KRI_HOLODA, 50, 100 },
			{ DUH_HOLODA, OSKOLOK_KRI_HOLODA, 50, 100 },
	{ SMOTRITEL_TOPI, ZNAK_HALIWI, 700, 100 },
	{ PULAYWIY_DREIK, ZNAK_HALIWI, 700, 100 },
	{ IKEDIT, ZNAK_HALIWI, 700, 100 },
	{ PLAMENNIY_IFRIT, ZNAK_HALIWI, 700, 100 }};

	public _95_SagaOfTheHellKnight()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(MORDRED);
		addTalkId(LANCER);
		addTalkId(RURU);
		addTalkId(VALDWTEIN);
		addTalkId(KAMEN_POZNANIA1);
		addTalkId(KAMEN_POZNANIA2);
		addTalkId(KAMEN_POZNANIA3);
		addTalkId(KAMEN_POZNANIA4);
		addQuestItem(OSKOLOK_KRI_HOLODA);
		addQuestItem(ZNAK_HALIWI);
		addKillNpcWithLog(7, A_LIST, 20, 27215);
		addKillNpcWithLog(9, B_LIST, 1, 27257);
		addKillNpcWithLog(13, C_LIST, 1, 27219);
		addKillNpcWithLog(15, D_LIST, 1, 25220);


		for(int[] element : DROPLIST)
			addKillId(element[0]);

		addQuestItem(new int[]
		{ OSKOLOK_KRI_HOLODA, ZNAK_HALIWI });


		addLevelCheck("mordred_q95_02.htm", 76);
		addClassIdCheck("mordred_q95_03.htm", 6);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("mordred_q95_02a.htm"))
		{
			htmltext = "mordred_q95_5.htm";
		}
		else if(event.equalsIgnoreCase("mordred_q95_001.htm"))
		{
			if(st.getCond() == 0)
				st.setCond(1);
		}
		else if(event.equalsIgnoreCase("ruru2.htm"))
		{
			if(st.getCond() == 1)
				st.setCond(2);
		}
		else if(event.equalsIgnoreCase("ruru4.htm"))
		{
			if(st.getCond() == 2)
				st.setCond(3);
		}
		else if(event.equalsIgnoreCase("ruru6.htm"))
		{
			if(st.getCond() == 4)
			{
				st.setCond(5);
				st.takeItems(OSKOLOK_KRI_HOLODA, -1);
			}
		}
		else if(event.equalsIgnoreCase("lancer6.htm"))
		{
			if(st.getCond() == 5)
			{
				st.setCond(6);
				st.giveItems(AMULET_REZONANSA_PERVIY, 1);
			}
		}
		else if(event.equalsIgnoreCase("stone12.htm"))
		{
			if(st.getCond() == 6)
				st.setCond(7);
		}
		else if(event.equalsIgnoreCase("stone22.htm"))
		{
			if(st.getCond() == 8)
			{
				st.addSpawn(ARHANGEL_BOGOBOREC);
				st.setCond(9);
			}
		}
		else if(event.equalsIgnoreCase("stone25.htm"))
		{
			if(st.getCond() == 10)
				st.setCond(11);
		}
		else if(event.equalsIgnoreCase("lancer12.htm"))
		{
			if(st.getCond() == 11)
				st.setCond(12);
		}
		else if(event.equalsIgnoreCase("stone32.htm"))
		{
			if(st.getCond() == 14)
				st.setCond(15);
		}
		else if(event.equalsIgnoreCase("valdwtein2.htm"))
		{
			if(st.getCond() == 16)
			{
				st.setCond(17);
				st.giveItems(AMULET_REZONANSA_CHETVERTIY, 1);
			}
		}
		else if(event.equalsIgnoreCase("stone42.htm"))
		{
			if(st.getCond() == 17)
				st.setCond(18);
		}
		else if(event.equalsIgnoreCase("mordred_q95_22.htm"))
		{
			if(st.getCond() == 18)
			{			 			
				st.addExpAndSp(3100000, 103000);
				st.giveItems(BOOKGOLDLION, 1);
				st.takeItems(AMULET_REZONANSA_PERVIY, -1);
				st.takeItems(AMULET_REZONANSA_VTOROI, -1);
				st.takeItems(AMULET_REZONANSA_TRETIY, -1);
				st.takeItems(AMULET_REZONANSA_CHETVERTIY, -1);
				st.takeItems(ZNAK_HALIWI, -1);
				st.finishQuest();

				Player player = st.getPlayer();
				player.setClassId(ClassId.HELL_KNIGHT.getId(), false);
				if(player.getBaseClassId() == ClassId.DARK_AVENGER.getId())
					player.setClassId(ClassId.HELL_KNIGHT.getId(), false);
				player.broadcastCharInfo();
				npc.broadcastPacket(new MagicSkillUse(npc, player, 5103, 1, 1000, 0));
			}
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		long squire = st.getQuestItemsCount(OSKOLOK_KRI_HOLODA);


		if(npcId == MORDRED) {
			if(cond == 0)
				{				
					if(st.getQuestItemsCount(OSKOLOK_KRI_HOLODA) > 0)
						htmltext = "mordred_q95_04.htm";
					else
						htmltext = "mordred_q95_01.htm";
				}
				
				 else if(st.getPlayer().getLevel() >= 76) 
					if(st.getPlayer().getClassId().getId() == 6)
						htmltext = "mordred_q95_02a.htm";
					else
						htmltext = "mordred_q95_03.htm";				 

			
			if(cond ==1){
				htmltext = "mordred_q95_001.htm";
			}
			if(cond ==18){
				htmltext = "mordred_q95_011.htm";
			}
			if(cond ==19){
				htmltext = "mordred_q95_012.htm";
			}
		}
		if(npcId == LANCER){
			if(cond ==1){
				htmltext = "ruru.htm";
			}
			if(cond ==2){
				htmltext = "ruru2.htm";
			}
			if(cond ==5){
				htmltext = "lancer5.htm";
			}
			if(cond ==6){
				htmltext = "lancer6.htm";
			}
			if(cond ==11){
				htmltext = "lancer11.htm";
			}
			if(cond ==12){
				htmltext = "lancer12.htm";
			}
		}
		if(npcId == RURU){
			if(cond == 2){
				htmltext = "ruru3.htm";
			}

				if(cond == 3){
					htmltext = "ruru4.htm";
				}
			if(cond == 4){
				htmltext = "ruru5.htm";
			}
			if(cond == 5){
				htmltext = "ruru6.htm";
			}
		}
		if(npcId == KAMEN_POZNANIA1){
			if(cond ==6){
				htmltext = "stone11.htm";
			}
			if(cond ==7){
				htmltext = "stone12.htm";
			}

		}
		if(npcId == KAMEN_POZNANIA2){
			if(cond ==8){
				htmltext = "stone21.htm";
			}
			if(cond ==9){
				htmltext = "stone23.htm";
			}
			if(cond ==10){
				htmltext = "stone24.htm";
			}

		}
		if(npcId == KAMEN_POZNANIA3){
			if(cond ==14){
				htmltext = "stone31.htm";
			}
			if(cond ==15){
				htmltext = "stone33.htm";
			}

		}
		if(npcId == VALDWTEIN){
			if(cond ==16){
				htmltext = "valdwtein1.htm";
			}
			if(cond ==17){
				htmltext = "valdwtein2.htm";
			}

		}
		if(npcId == KAMEN_POZNANIA4){
			if(cond ==15){
				htmltext = "stone40.htm";
			}
			if(cond ==17){
				htmltext = "stone41.htm";
			}
			if(cond ==18){
				htmltext = "stone43.htm";
			}

		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() == 3)
		{
			if(qs.rollAndGive(OSKOLOK_KRI_HOLODA, 1, 1, 50, 100))
				qs.setCond(4);
		}
		if(qs.getCond() == 7)
		{
		boolean doneKill = updateKill(npc, qs);
		if(doneKill)
		{
			qs.unset(A_LIST);
			qs.giveItems(AMULET_REZONANSA_VTOROI, 1);
			qs.setCond(8);
		}}
		if(qs.getCond() == 9)
		{
			boolean doneKill = updateKill(npc, qs);
			if(doneKill)
			{
				qs.unset(B_LIST);
				qs.setCond(10);

			}}
		if(qs.getCond() == 12)
		{
			if(qs.rollAndGive(ZNAK_HALIWI, 1, 1, 700, 100))
			{
				qs.setCond(13);
				if(qs.rollAndGive(ZNAK_HALIWI, 1, 1, 701, 100))
				qs.addSpawn(ARHONT_HALIWI);
			}
		}
		if(qs.getCond() == 13)
		{
			boolean doneKill = updateKill(npc, qs);
			if(doneKill)
			{	qs.unset(C_LIST);
				qs.giveItems(AMULET_REZONANSA_TRETIY, 1);
				qs.setCond(14);

			}}
		if(qs.getCond() == 15)
		{
			boolean doneKill = updateKill(npc, qs);
			if(doneKill)
			{	qs.unset(D_LIST);
				qs.setCond(16);

			}}
		return null;
	}
}