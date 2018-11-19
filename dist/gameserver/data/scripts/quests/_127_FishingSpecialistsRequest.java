package quests;

import l2s.gameserver.data.QuestHolder;
import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;


public class _127_FishingSpecialistsRequest extends Quest
{
	public final int PierRishar = 30013;
	public final int Ferma = 30015;
	public final int Baikal = 30016;
	public final int Otchet = 49504;
	
	public _127_FishingSpecialistsRequest()
	{
		super(PARTY_NONE, REPEATABLE);
		
		addStartNpc(PierRishar);
		addTalkId(Ferma);		
		addTalkId(Baikal);		
		addQuestItem(Otchet);
		
		addLevelCheck("lvl.htm", 20, 75);
	}
	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		String htmltext = event;
		if(event.equalsIgnoreCase("p2.htm"))
		{
			st.setCond(1);			
			player.teleToLocation(105192, 162472, -3616, ReflectionManager.MAIN);
		}
		else if(event.equalsIgnoreCase("ferma2.htm"))
		{			
				st.giveItems(49504, 1);
				st.setCond(2);
		}
		else if(event.equalsIgnoreCase("baikal2.htm"))
		{		st.takeItems(Otchet, -1);
				st.setCond(3);
		}
		
		else if(event.equalsIgnoreCase("p4.htm"))
		{
			if(st.getCond() == 3)
			{			 							
				st.giveItems(49507, 1);				
													
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

		if(npcId == PierRishar) {
			if(cond == 0)
				{	if(st.getPlayer().getLevel() >= 20)
												
						htmltext = "p.htm";		
					else
						htmltext = "lvl.htm";						
				}
					if(cond ==1){
						htmltext = "p2.htm";
				}					
				 if(cond ==3){
						htmltext = "p3.htm";
				}
					 }
			if(npcId == Ferma) {
			if(cond == 1)
				{												
						htmltext = "ferma.htm";											
				}
					if(cond ==2){
						htmltext = "ferma2.htm";
				}					
				 
					 }
			if(npcId == Baikal) {
			if(cond == 2)
				{												
						htmltext = "baikal.htm";											
				}
					if(cond ==3){
						htmltext = "baikal2.htm";
				}					
				 
					 }
			return htmltext;
	}
	
}
