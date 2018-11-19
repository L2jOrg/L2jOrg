package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.ClassType;


//SanyaDC

	public class _10866_PunitiveOperationOnTheDevilsIsle extends Quest
{
	
	public final int RODEMAI = 30756;
	public final int AIN = 34017;
	public final int FETHIN = 34019;
	public final int NIKIA = 34020;
	
	//items
	public final int PAUTINA = 90212;
	public final int ESSENC = 90213;
	
	public _10866_PunitiveOperationOnTheDevilsIsle()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(RODEMAI);
		addTalkId(AIN);
		addTalkId(FETHIN);
		addTalkId(NIKIA);		
		
		addLevelCheck("lvl.htm", 70);			
		
	}
	
	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("rodemai2.htm"))
		{
			st.setCond(1);
		}	
		else if(event.equalsIgnoreCase("ain2.htm"))
		{
			st.setCond(2);
		}
		else if(event.equalsIgnoreCase("fethin2.htm"))
		{
			st.setCond(3);
		}
		else if(event.equalsIgnoreCase("nikia2.htm"))
		{
					 			
				st.addExpAndSp(150000, 4500);
				st.giveItems(57, 13616);								
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

		if(npcId == RODEMAI) {
			if(cond == 0)
				{	if(st.getPlayer().getLevel() >= 70)
												
						htmltext = "rodemai.htm";		
					else
						htmltext = "lvl.htm";						
				}
			if(cond ==1){
						htmltext = "rodemai2.htm";
				}				 
					 }
		if(npcId == AIN) {
			if(cond == 1)
				{	htmltext = "ain.htm";}
			if(cond ==2){
						htmltext = "ain2.htm";}	}
		if(npcId == FETHIN) {
			if(cond == 2)
				{	htmltext = "fethin.htm";}
		if(cond ==3){
						htmltext = "fethin2.htm";}	}
		if(npcId == NIKIA) {
			if(cond == 3)
				{	htmltext = "nikia.htm";					
		}}
			return htmltext;
	}
	
}