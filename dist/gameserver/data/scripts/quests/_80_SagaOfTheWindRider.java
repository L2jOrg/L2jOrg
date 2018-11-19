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

public class _80_SagaOfTheWindRider extends Quest
{
	public final int AIKEN = 31603;//черная кошка
	public final int JER = 30840;//лоренцо
	public final int RIFKEN = 34268;//рифкен
	public final int BELINDA = 31616;//отшельник
	public final int KAMEN_POZNANIA1 = 31646;
	public final int KAMEN_POZNANIA2 = 31647;
	public final int KAMEN_POZNANIA3 = 31653;
	public final int KAMEN_POZNANIA4 = 31655;
	//mobs
	public final int LEDANOI_MONSTR = 27316;
	public final int DUH_UTOPL = 27317;
	public final int DUWA_HOLODA = 27318;
	public final int PRIZ_ODINOCHESTVA = 27319;
	public final int CHUDIWE_HOLODA = 27320;
	public final int DUH_HOLODA = 27321;
	public final int SMOTRITEL_TOPI = 21650;
	public final int PULAYWIY_DREIK = 21651;
	public final int PLAMENNIY_IFRIT = 21652;
	public final int IKEDIT = 21653;
	public final int BLUSTITEL = 27214;   //1 хранитель
	public final int AIRON = 27296; //2 эзекиэль
	public final int ARHONT_HALIWI = 27219;
	public final int TENBELEFA = 27302;//3 нарцисс
	public final int KAIN = 31612;//4 старис
	public static final String A_LIST = "a_list";
	public static final String B_LIST = "a_list";
	public static final String C_LIST = "a_list";
	public static final String D_LIST = "a_list";
	//items
	public final int BOOKGOLDLION = 90039;
	public final int OSKOLOK_KRI_HOLODA = 49814;
	public final int ZNAK_HALIWI = 7495;
	public final int AMULET_REZONANSA_PERVIY = 7278;
	public final int AMULET_REZONANSA_VTOROI = 7309;
	public final int AMULET_REZONANSA_TRETIY = 7341;
	public final int AMULET_REZONANSA_CHETVERTIY = 7372;
	
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
	
	public _80_SagaOfTheWindRider()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(AIKEN);
		addTalkId(JER);
		addTalkId(RIFKEN);
		addTalkId(BELINDA);		
		addTalkId(KAIN);
		addTalkId(KAMEN_POZNANIA1);
		addTalkId(KAMEN_POZNANIA2);
		addTalkId(KAMEN_POZNANIA3);
		addTalkId(KAMEN_POZNANIA4);
		addQuestItem(OSKOLOK_KRI_HOLODA);
		addQuestItem(ZNAK_HALIWI);
		addKillNpcWithLog(7, A_LIST, 20, 27214);//1
		addKillNpcWithLog(9, B_LIST, 1, 27296);//2
		addKillNpcWithLog(13, C_LIST, 1, 27219);
		addKillNpcWithLog(16, D_LIST, 1, 27302);//3


		for(int[] element : DROPLIST)
			addKillId(element[0]);

		addQuestItem(new int[]
		{ OSKOLOK_KRI_HOLODA, ZNAK_HALIWI });


		addLevelCheck("aiken02.htm", 76);
		addClassIdCheck("aiken03.htm", 23);
	}
	
	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("aiken02a.htm"))
		{
			htmltext = "aiken5.htm";
		}
		else if(event.equalsIgnoreCase("aiken01s.htm"))
		{
			if(st.getCond() == 0)
				st.setCond(1);
		}
		else if(event.equalsIgnoreCase("jer2.htm"))
		{
			if(st.getCond() == 1)
				st.setCond(2);
		}
		else if(event.equalsIgnoreCase("rifken2.htm"))
		{
			if(st.getCond() == 2)
				st.setCond(3);
		}
		else if(event.equalsIgnoreCase("rifken4.htm"))
		{
			if(st.getCond() == 4)
				st.setCond(5);
				st.takeItems(OSKOLOK_KRI_HOLODA, -1);
		}
		else if(event.equalsIgnoreCase("jer4.htm"))
		{
			if(st.getCond() == 5)
				st.setCond(6);
			st.giveItems(AMULET_REZONANSA_PERVIY, 1);
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
				st.addSpawn(AIRON);
				st.setCond(9);
			}
		}
		else if(event.equalsIgnoreCase("stone25.htm"))
		{
			if(st.getCond() == 10)
				st.setCond(11);
		}
		else if(event.equalsIgnoreCase("jer6.htm"))
		{
			if(st.getCond() == 11)
				st.setCond(12);
		}
		else if(event.equalsIgnoreCase("stone32.htm"))
		{
			if(st.getCond() == 14)
				st.setCond(15);
		}
		else if(event.equalsIgnoreCase("stone41.htm"))
		{
			if(st.getCond() == 15)
				st.setCond(16);
			st.addSpawn(TENBELEFA);
		}
		else if(event.equalsIgnoreCase("erikrams2.htm"))
		{
			if(st.getCond() == 16)
				st.setCond(17);
			st.giveItems(AMULET_REZONANSA_CHETVERTIY, 1);
			
		}
		else if(event.equalsIgnoreCase("stone43.htm"))
		{
			if(st.getCond() == 17)
				st.setCond(18);
			
			
		}
		else if(event.equalsIgnoreCase("aiken7.htm"))
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
				player.setClassId(ClassId.WIND_RIDER.getId(), false);
				if(player.getBaseClassId() == ClassId.PLAIN_WALKER.getId())
					player.setClassId(ClassId.WIND_RIDER.getId(), false);
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


		if(npcId == AIKEN) {
			if(cond == 0)
				{	if(st.getPlayer().getLevel() >= 76 && st.getPlayer().getClassId().getId() == 23)
												
						htmltext = "aiken01.htm";		
					else
						htmltext = "aiken03.htm";						
				}
					if(cond ==1){
						htmltext = "aiken01s.htm";
				}	
				if(cond ==18){
						htmltext = "aiken6.htm";
				}
				 
					 }
		if(npcId == JER) {
			if(cond ==1){
						htmltext = "jer1.htm";
				}	
			if(cond ==2){
						htmltext = "jer2.htm";
				}	
			if(cond ==5){
						htmltext = "jer3.htm";
				}
			if(cond ==6){
						htmltext = "jer4.htm";
				}

				
			}
			if(npcId == BELINDA) {			
				if(cond ==11){
						htmltext = "jer5.htm";
				}	
				if(cond ==12){
						htmltext = "jer6.htm";
				}	
			}

		if(npcId == RIFKEN) {
			if(cond ==2){
						htmltext = "rifken1.htm";
				}	
			if(cond ==3){
						htmltext = "rifken2.htm";
				}
			if(cond ==4){
						htmltext = "rifken3.htm";
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
			if(cond ==11){
				htmltext = "stone25.htm";
		}}		
		if(npcId == KAMEN_POZNANIA3){
			if(cond ==14){
				htmltext = "stone31.htm";
			}
			if(cond ==15){
				htmltext = "stone32.htm";
		}}	
		if(npcId == KAMEN_POZNANIA4){
			if(cond ==15){
				htmltext = "stone40.htm";
			}
			if(cond ==17){
				htmltext = "stone42.htm";
			}
			if(cond ==18){
				htmltext = "stone43.htm";
			}

		}
		if(npcId == KAIN){
			if(cond ==16){
				htmltext = "erikrams1.htm";
			}
			if(cond ==17){
				htmltext = "erikrams2.htm";
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

			}
		}
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

			}
			}
			if(qs.getCond() == 16)
		{
			boolean doneKill = updateKill(npc, qs);
			if(doneKill)
			{	qs.unset(D_LIST);				
				qs.addSpawn(KAIN);

			}}

	
		return null;
	}
}