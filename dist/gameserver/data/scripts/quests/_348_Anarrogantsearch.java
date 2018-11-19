package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.network.l2.s2c.MagicSkillUse;
import l2s.gameserver.network.l2.s2c.RadarControlPacket;

//SanyaDC

	public class _348_Anarrogantsearch extends Quest
{
	public final int HAN = 30864;
	public final int KLAUD = 31001;
	public final int KAM = 31646;
	
	//mobs
	public final int DREIK = 20670;
	public final int LICHINKA = 20671;
	public final int SHAM = 20828;
	public final int VLADUKA = 20829;
	public final int ANGEL_HR = 20830;
	public final int ANGEL_P = 20831;	
	public final int AIRON = 27296;
	public static final String A_LIST = "a_list";
	//items
	public final int PANC = 14857;
	public final int BOOK = 4397;
	public final int ZELIE = 1061;
	public final int BELT1 = 4294;
	public final int BELT2 = 4400;
	public final int TKAN = 4295;
	
	
	//	# [MOB_ID, REQUIRED, ITEM, NEED_COUNT, CHANCE]
	public final int[][] DROPLIST =
	{		
		{ DREIK, PANC, 1, 100 },
		{ LICHINKA, PANC, 1, 100 },
		{ SHAM, BELT1, 100, 100 },
		{ VLADUKA, BELT1, 100, 100 },
		{ ANGEL_P, BELT2, 1000, 100 },
		{ ANGEL_HR, BELT2, 1000, 100 }};
	
	public _348_Anarrogantsearch()
	{
		super(PARTY_NONE, REPEATABLE);

		addStartNpc(HAN);
		addTalkId(KLAUD);
		addTalkId(KAM);
		addQuestItem(PANC);
		addQuestItem(BOOK);
		addQuestItem(ZELIE);
		addQuestItem(BELT1);
		addQuestItem(BELT2);
		addKillNpcWithLog(5, A_LIST, 1, 27296);

		for(int[] element : DROPLIST)
			addKillId(element[0]);
		addQuestItem(new int[]
		{ PANC, BOOK, ZELIE, BELT1, BELT2 });


		addLevelCheck("lvl.htm", 60);		
	}
	
	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("aiken02a.htm"))
		{
			htmltext = "aiken5.htm";
		}
		else if(event.equalsIgnoreCase("han2.htm"))
		{
			if(st.getCond() == 0)
				st.setCond(2);
		}
		else if(event.equalsIgnoreCase("han5.htm"))
		{
			if(st.getCond() == 3)
				st.setCond(4);
			st.takeItems(PANC, -1);
		}
		else if(event.equalsIgnoreCase("han6.htm"))
		{
			if(st.getCond() == 4)
				st.setCond(5);
				
		}
		else if(event.equalsIgnoreCase("klaud2.htm"))
		{
			if(st.getCond() == 5)
				st.getPlayer().addRadarWithMap(121992, 29400, -3648);
		}
		else if(event.equalsIgnoreCase("kam2.htm"))
		{
			if(st.getCond() == 5)
				st.addSpawn(AIRON);
			st.getPlayer().sendPacket(new RadarControlPacket(2, 2, 0, 0, 0));
		}
		else if(event.equalsIgnoreCase("han8.htm"))
		{
			if(st.getCond() == 6)
				st.setCond(7);
		}
		else if(event.equalsIgnoreCase("han10.htm"))
		{
			if(st.getCond() == 7)
				st.takeItems(ZELIE, 1);
				st.setCond(8);
			
		}
			else if(event.equalsIgnoreCase("han11.htm"))
		{
			if(st.getCond() == 7)
				st.takeItems(ZELIE, 1);
				st.setCond(9);
		}
		else if(event.equalsIgnoreCase("end.htm"))
		{
			if(st.getCond() == 10 || st.getCond() == 11)
			{			 			
				st.giveItems(TKAN, 1);
				st.takeItems(BELT1, -1);
				st.takeItems(BELT2, -1);
				st.finishQuest();
				
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
		


		if(npcId == HAN) {
				if(cond == 0)
				{	if(st.getPlayer().getLevel() >= 60)
												
						htmltext = "han1.htm";		
					else
						htmltext = "lvl.htm";						
				}
				if(cond ==2){
						htmltext = "magister_hanellin_q0348_06.htm";
				}
				if(cond ==3){
						htmltext = "han4.htm";
				}
				if(cond ==4){
						htmltext = "han5.htm";
				}
				if(cond ==5){
						htmltext = "han6.htm";
						
				}	
				if(cond ==6){
						htmltext = "han7.htm";
						
				}
					if(cond == 7){
						if(cond ==7 && st.getQuestItemsCount(ZELIE) > 0)
						htmltext = "han9.htm";
					else
						htmltext = "noz.htm";
				}	
					if(cond ==9){
						htmltext = "han10.htm";
						
				}	
					if(cond ==10){
						htmltext = "hanend100.htm";
						
				}	
					if(cond ==11){
						htmltext = "hanend100.htm";
						
				}					
					
					 
					 }
		if(npcId == KLAUD) {	
			if(cond ==5){
						htmltext = "klaud1.htm";
				}
						
			}
		if(npcId == KAM) {	
			if(cond ==5){
						htmltext = "kam1.htm";
				}
		}

		
			return htmltext;
	}
	
	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
	if(qs.getCond() == 2)
		{
			if(qs.rollAndGive(PANC, 1, 1, 1, 100))
				qs.setCond(3);
		}
	if(qs.getCond() == 8)
		{
			if(qs.rollAndGive(BELT1, 1, 1, 100, 100))
				qs.setCond(10);
		}
	if(qs.getCond() == 9)
		{
			if(qs.rollAndGive(BELT2, 1, 1, 1000, 100))
				qs.setCond(11);
		}
		if(qs.getCond() == 5)
		{
			boolean doneKill = updateKill(npc, qs);
			if(doneKill)
			{	qs.unset(A_LIST);
				qs.giveItems(BOOK, 1);
				qs.setCond(6);

			}
			}
	
		return null;
	}
}