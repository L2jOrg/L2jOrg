package quests;

import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.base.Race;

public class _10993_FutureDwarves extends Quest
{
	
	public final int GERALD = 30650;
	public final int PEPI = 30524;
	public final int SILVERE = 30527;
	
	




	public _10993_FutureDwarves()
	{
		super(PARTY_NONE, ONETIME);
		
		addStartNpc(GERALD);
		addTalkId(PEPI);
		addTalkId(SILVERE);
		
		
		
		addLevelCheck("lvl.htm", 19);
		addRaceCheck("lvl.htm", Race.DWARF);
		addQuestCompletedCheck("questnotdone.htm", 10999);
	}

	@Override
	public int getDescriprionId(int state)
	{
		return 103000 + state;
	}
	
	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("gerb2.htm"))
		{
			st.setCond(1);
		}		
		else if(event.equalsIgnoreCase("end.htm"))
		{
								
				st.giveItems(49087, 1);
				st.giveItems(49772, 2);									
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

		if(npcId == GERALD) {
			if(cond == 0)
				{	if(st.getPlayer().getLevel() >= 19)
												
						htmltext = "gerb.htm";		
					else
						htmltext = "lvl.htm";						
				}
			if(cond ==1){
						htmltext = "gerb2.htm";
				}
					 }
		if(npcId == PEPI) {
			if(cond == 1)
				{													
						htmltext = "PEPI.htm";											
					 }}
		if(npcId == SILVERE) {
			if(cond == 1)
				{													
						htmltext = "SILVERE.htm";											
					 }}
				
			return htmltext;
	}
}
