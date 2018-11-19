package quests;

import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.base.Race;

public class _11012_FutureElves extends Quest
{
	
	public final int GERBIEL = 30150;
	public final int SORIS = 30327;
	public final int REYS = 30328;
	public final int ROZELLA = 30414;
	public final int MANUEL = 30293;
	




	public _11012_FutureElves()
	{
		super(PARTY_NONE, ONETIME);
		
		addStartNpc(GERBIEL);
		addTalkId(SORIS);
		addTalkId(REYS);
		addTalkId(ROZELLA);
		addTalkId(MANUEL);
		
		
		addLevelCheck("lvl.htm", 19);
		addRaceCheck("lvl.htm", Race.ELF);
		addQuestCompletedCheck("questnotdone.htm", 11011);
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

		if(npcId == GERBIEL) {
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
		if(npcId == SORIS) {
			if(cond == 1)
				{													
						htmltext = "soris.htm";											
					 }}
		if(npcId == REYS) {
			if(cond == 1)
				{													
						htmltext = "reys.htm";											
					 }}
		if(npcId == ROZELLA) {
			if(cond == 1)
				{													
						htmltext = "rozella.htm";											
					 }}
		if(npcId == MANUEL) {
			if(cond == 1)
				{													
						htmltext = "manuel.htm";											
					 }}		
			return htmltext;
	}
}
