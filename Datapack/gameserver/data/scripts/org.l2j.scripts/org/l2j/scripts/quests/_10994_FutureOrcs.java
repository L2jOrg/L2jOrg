package org.l2j.scripts.quests;

import org.l2j.gameserver.model.base.Race;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;

public class _10994_FutureOrcs extends Quest
{
	
	public final int ASKA = 30560;
	public final int KARUKIA = 30570;
	public final int GENTAKI = 30587;
	public final int TATARU = 30421;

	public _10994_FutureOrcs()
	{
		super(PARTY_NONE, ONETIME);
		
		addStartNpc(ASKA);
		addTalkId(KARUKIA);
		addTalkId(GENTAKI);
		addTalkId(TATARU);
		
		
		
		addLevelCheck("lvl.htm", 19);
		addRaceCheck("lvl.htm", Race.ORC);
		addQuestCompletedCheck("questnotdone.htm", 11023);
	}
	
	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("ASKA2.htm"))
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

		if(npcId == ASKA) {
			if(cond == 0)
				{	if(st.getPlayer().getLevel() >= 19)
												
						htmltext = "ASKA.htm";		
					else
						htmltext = "lvl.htm";						
				}
			if(cond ==1){
						htmltext = "ASKA2.htm";
				}
					 }
		if(npcId == KARUKIA) {
			if(cond == 1)
				{													
						htmltext = "KARUKIA.htm";											
					 }}
		if(npcId == GENTAKI) {
			if(cond == 1)
				{													
						htmltext = "GENTAKI.htm";											
					 }}
		if(npcId == TATARU) {
			if(cond == 1)
				{													
						htmltext = "TATARU.htm";											
					 }}
				
			return htmltext;
	}

	@Override
	public int getDescriprionId(int state)
	{
		return 102400 + state;
	}
}
