package quests;

import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.base.Race;

public class _11018_FutureDarkElves extends Quest
{
	
	public final int VOLODOS = 30137;
	public final int VIRD = 30329;
	public final int TRISK = 30416;
	public final int VARIKA = 30421;
	public final int SIDRA = 30330;
	




	public _11018_FutureDarkElves()
	{
		super(PARTY_NONE, ONETIME);
		
		addStartNpc(VOLODOS);
		addTalkId(VIRD);
		addTalkId(TRISK);
		addTalkId(VARIKA);
		addTalkId(SIDRA);
		
		
		addLevelCheck("lvl.htm", 19);
		addRaceCheck("lvl.htm", Race.DARKELF);
		addQuestCompletedCheck("questnotdone.htm", 11017);
	}
	
	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("volodos2.htm"))
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

		if(npcId == VOLODOS) {
			if(cond == 0)
				{	if(st.getPlayer().getLevel() >= 19)
												
						htmltext = "volodos.htm";		
					else
						htmltext = "lvl.htm";						
				}
			if(cond ==1){
						htmltext = "volodos2.htm";
				}
					 }
		if(npcId == VIRD) {
			if(cond == 1)
				{													
						htmltext = "VIRD.htm";											
					 }}
		if(npcId == TRISK) {
			if(cond == 1)
				{													
						htmltext = "TRISK.htm";											
					 }}
		if(npcId == VARIKA) {
			if(cond == 1)
				{													
						htmltext = "VARIKA.htm";											
					 }}
		if(npcId == SIDRA) {
			if(cond == 1)
				{													
						htmltext = "SIDRA.htm";											
					 }}		
			return htmltext;
	}
}
