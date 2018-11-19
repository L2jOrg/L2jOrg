package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.ClassType;

public class _11009_NewPotionDevelopment13 extends Quest
{
	public final int STARDEN = 30220;
	public final int GABRIEL = 30150;
	
	//mobs
	public final int KRASNPAUK = 20393;
	public final int PAUKPADALWIK = 20410;
	public final int ZVER = 20369;
		
	//items
	public final int SUKROVICA = 90229;
	public final int CHESHUA = 90230;
		
	//	# [MOB_ID, REQUIRED, ITEM, NEED_COUNT, CHANCE]
	public final int[][] DROPLIST =
	{
			{ KRASNPAUK, SUKROVICA, 20, 100 },
			{ PAUKPADALWIK, SUKROVICA, 20, 100 },
			{ ZVER, CHESHUA, 20, 100 },			
	};
	
	public _11009_NewPotionDevelopment13()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(STARDEN);
		addTalkId(GABRIEL);		
		addQuestItem(SUKROVICA);
		addQuestItem(CHESHUA);

		for(int[] element : DROPLIST)
			addKillId(element[0]);

		addQuestItem(new int[]
		{ SUKROVICA, CHESHUA });


		addLevelCheck("lvl.htm", 15, 20);
		addRaceCheck("lvl.htm", Race.ELF);
		addQuestCompletedCheck("questnotdone.htm", 11008);
		
	}
	
	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("starden2.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("gab2.htm"))
		{			
				st.setCond(2);
		}
		
		else if(event.equalsIgnoreCase("gab4.htm") && st.getPlayer().getClassId().isOfType(ClassType.MYSTIC))
		{
			if(st.getCond() == 4)
			{			 			
				st.addExpAndSp(70000, 3600);
				st.giveItems(1073, 40);
				st.giveItems(10650, 5);
				st.giveItems(90310, 40);
				st.giveItems(5790, 700);
				
				st.takeItems(SUKROVICA, -1);
				st.takeItems(CHESHUA, -1);
						
				st.finishQuest();			
			}
		}
		else if(event.equalsIgnoreCase("gab4.htm") && st.getPlayer().getClassId().isOfType(ClassType.FIGHTER))
		{
			if(st.getCond() == 4)
			{			 			
				st.addExpAndSp(70000, 3600);
				st.giveItems(1073, 40);
				st.giveItems(10650, 5);
				st.giveItems(90310, 40); 
				st.giveItems(5789, 1000);
				
				st.takeItems(SUKROVICA, -1);
				st.takeItems(CHESHUA, -1);
						
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

		if(npcId == STARDEN) {
			if(cond == 0)
				{	if(st.getPlayer().getLevel() >= 11)
												
						htmltext = "starden.htm";		
					else
						htmltext = "lvl.htm";						
				}
					if(cond ==1){
						htmltext = "starden2.htm";
				}					
				 
					 }
		if(npcId == GABRIEL) {
			if(cond ==1){
						htmltext = "gab.htm";
				}	
			if(cond ==2){
						htmltext = "gab2.htm";
				}				
			if(cond ==4){
						htmltext = "gab3.htm";
				}					
			}
			return htmltext;
	}
	
	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() == 2 )
		{
			if(npc.getNpcId() == PAUKPADALWIK || npc.getNpcId() == KRASNPAUK)
			if(qs.rollAndGive(SUKROVICA, 1, 1, 20, 100))
				qs.setCond(3);
		}
		if(qs.getCond() == 3 )
		{
			if(npc.getNpcId() == ZVER)
			if(qs.rollAndGive(CHESHUA, 1, 1, 20, 100))
				qs.setCond(4);
		}	
		
		return null;
	}
}