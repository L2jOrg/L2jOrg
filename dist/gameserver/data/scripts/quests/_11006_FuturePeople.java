package quests;

import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.base.Race;

public class _11006_FuturePeople extends Quest
{
	
	public final int LEK = 30001;
	public final int AUR = 30010;
	public final int VASPER = 30417;
	public final int BEZIK = 30379;
	public final int PARINA = 30391;
	public final int ZIGONT = 30022;




	public _11006_FuturePeople()
	{
		super(PARTY_NONE, ONETIME);
		
		addStartNpc(LEK);
		addTalkId(AUR);
		addTalkId(VASPER);
		addTalkId(BEZIK);
		addTalkId(PARINA);
		addTalkId(ZIGONT);
		
		addLevelCheck("lvl.htm", 19);
		addRaceCheck("lvl.htm", Race.HUMAN);
		addQuestCompletedCheck("questnotdone.htm", 11005);
	}
	
	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("lek2.htm"))
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

		if(npcId == LEK) {
			if(cond == 0)
				{	if(st.getPlayer().getLevel() >= 19)
												
						htmltext = "lek.htm";		
					else
						htmltext = "lvl.htm";						
				}
			if(cond ==1){
						htmltext = "lek2.htm";
				}
					 }
		if(npcId == AUR) {
			if(cond == 1)
				{													
						htmltext = "auron.htm";											
					 }}
		if(npcId == VASPER) {
			if(cond == 1)
				{													
						htmltext = "claus.htm";											
					 }}
		if(npcId == BEZIK) {
			if(cond == 1)
				{													
						htmltext = "bezik.htm";											
					 }}
		if(npcId == PARINA) {
			if(cond == 1)
				{													
						htmltext = "parin.htm";											
					 }}
		if(npcId == ZIGONT) {
			if(cond == 1)
				{													
						htmltext = "zigont.htm";											
					 }}	
			return htmltext;
	}
}
