package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;

//By SanyaDC

public class _935_ExploringTheEastWingOfTheDungeonOfAbyss extends Quest
{
	// NPC's
	private static final int MAGRIT = 31776;
	private static final int INGRIT = 31777;
	//mobs
	public final int MERTT = 21644;
	public final int DUHT = 21645;
	public final int PRIZT = 21646;
	public final int KOVART = 21647;
	//items
	public final int OSKZLA = 90009;
	public final int POD = 90136;
	//	# [MOB_ID, REQUIRED, ITEM, NEED_COUNT, CHANCE]
	public final int[][] DROPLIST =
	{
			{ MERTT, OSKZLA, 50, 100 },
			{ DUHT, OSKZLA, 50, 100 },
			{ PRIZT, OSKZLA, 50, 100 },
			{ KOVART, OSKZLA, 50, 100 },			
	};
	
	
	
	public _935_ExploringTheEastWingOfTheDungeonOfAbyss()
	{
		super(PARTY_ALL, DAILY);
		
		addStartNpc(MAGRIT);
		addStartNpc(INGRIT);
		//addTalkId(INGRIT);
		addQuestItem(OSKZLA);
		
		for(int[] element : DROPLIST)
			addKillId(element[0]);

		addQuestItem(new int[]
		{ OSKZLA });
		addLevelCheck("nolvl.htm", 45, 51);
	}
	
	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;		
		if(event.equalsIgnoreCase("magrit04.htm"))
		{
			if(st.getCond() == 0)
				st.setCond(1);
		}
		if(event.equalsIgnoreCase("ingrit04.htm"))
		{
			if(st.getCond() == 0)
				st.setCond(1);
		}
		
		else if(event.equalsIgnoreCase("end.htm"))
		{
						 			
				st.addExpAndSp(300000, 9000);
				st.giveItems(POD, 1);				
				st.takeItems(OSKZLA, -1);
				st.finishQuest();
				}
		
		
			return htmltext;
	}
	
	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		long squire = st.getQuestItemsCount(OSKZLA);


		if(npcId == MAGRIT) {
			if(cond == 0)
				{	if(st.getPlayer().getLevel() >= 45 && st.getPlayer().getLevel() <= 51)
												
						htmltext = "magrit01.htm";		
					else
						htmltext = "magrit01a.htm";						
				}
					if(cond ==1){
						htmltext = "magrit04.htm";
				}	
				if(cond ==2){
						htmltext = "magrit05.htm";
				}	
				 
					 }
		if(npcId == INGRIT) {
			if(cond == 0)
				{	if(st.getPlayer().getLevel() >= 45 && st.getPlayer().getLevel() <= 51)
												
						htmltext = "ingrit01.htm";		
					else
						htmltext = "ingrit01a.htm";						
				}
					if(cond ==1){
						htmltext = "ingrit04.htm";
				}	
				if(cond ==2){
						htmltext = "ingrit05.htm";
				}	
				 
					 }
		
			return htmltext;
	}
	
	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() == 1)
		{
			if(qs.rollAndGive(OSKZLA, 1, 1, 50, 100))
				qs.setCond(2);
		}


	
		return null;
	}
}
