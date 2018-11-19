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

public class _11011_NewPotionDevelopment33 extends Quest
{
	
	public final int GABRIEL = 30150;
	
	//mobs
	public final int KRUSOLUD = 20039;
	public final int TRIMDEN = 20043;	
		
	//items
	public final int PROTIVOYAD = 90235;
	public final int WIP = 90236;
		
	//	# [MOB_ID, REQUIRED, ITEM, NEED_COUNT, CHANCE]
	public final int[][] DROPLIST =
	{
			{ KRUSOLUD, PROTIVOYAD, 20, 100 },
			{ TRIMDEN, WIP, 20, 100 },				
	};
	
	public _11011_NewPotionDevelopment33()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(GABRIEL);		
		addQuestItem(PROTIVOYAD);
		addQuestItem(WIP);

		for(int[] element : DROPLIST)
			addKillId(element[0]);

		addQuestItem(new int[]
		{ PROTIVOYAD, WIP });


		addLevelCheck("lvl.htm", 15, 20);
		addRaceCheck("lvl.htm", Race.ELF);
		addQuestCompletedCheck("questnotdone.htm", 11010);
		
	}
	
	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("geb2.htm"))
		{
			st.setCond(2);
		}		
		else if(event.equalsIgnoreCase("geb4.htm") && st.getPlayer().getClassId().isOfType(ClassType.MYSTIC))
		{
			if(st.getCond() == 4)
			{			 			
				st.addExpAndSp(70000, 3600);
				st.giveItems(1073, 40);
				st.giveItems(10650, 5);
				st.giveItems(90310, 40);
				st.giveItems(5790, 700);
				
				st.takeItems(PROTIVOYAD, -1);
				st.takeItems(WIP, -1);
						
				st.finishQuest();			
			}
		}
		else if(event.equalsIgnoreCase("geb4.htm") && st.getPlayer().getClassId().isOfType(ClassType.FIGHTER))
		{
			if(st.getCond() == 4)
			{			 			
				st.addExpAndSp(70000, 3600);
				st.giveItems(1073, 40);
				st.giveItems(10650, 5);
				st.giveItems(90310, 40); 
				st.giveItems(5789, 1000);
				
				st.takeItems(PROTIVOYAD, -1);
				st.takeItems(WIP, -1);
						
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

		if(npcId == GABRIEL) {
			if(cond == 0)
				{	if(st.getPlayer().getLevel() >= 11)
												
						htmltext = "geb.htm";		
					else
						htmltext = "lvl.htm";						
				}
					if(cond ==1){
						htmltext = "geb2.htm";
				}
					if(cond ==4){
						htmltext = "geb3.htm";
				}				
				 
					 }		
			return htmltext;
	}
	
	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() == 2 )
		{
			if(npc.getNpcId() == KRUSOLUD)
			if(qs.rollAndGive(PROTIVOYAD, 1, 1, 20, 100))
				qs.setCond(3);
		}
		if(qs.getCond() == 3 )
		{
			if(npc.getNpcId() == TRIMDEN)
			if(qs.rollAndGive(WIP, 1, 1, 20, 100))
				qs.setCond(4);
		}	
		
		return null;
	}
}