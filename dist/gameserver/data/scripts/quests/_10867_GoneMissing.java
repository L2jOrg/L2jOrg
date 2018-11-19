package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.network.l2.s2c.MagicSkillUse;

//SanyaDC

public class _10867_GoneMissing extends Quest
{
	public final int NIKIA = 34020;
	public final int DUH = 34022;	
	public final int KOSTI = 34024;
	public final int KOSTI2 = 34025;
	public final int KOSTI3 = 34026;
	public final int KOSTI4 = 34027;
		
	//items
	public final int CHEREP = 90734;
	public final int REBRO = 90735;	
	public final int PREDPLECHE = 90736;
	public final int BEDRO = 90737;	
		
	
	
	public _10867_GoneMissing()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(NIKIA);
		addTalkId(DUH);	
		addTalkId(KOSTI);	
		addTalkId(KOSTI2);	
		addTalkId(KOSTI3);	
		addTalkId(KOSTI4);			
		addQuestItem(CHEREP);
		addQuestItem(REBRO);
		addQuestItem(PREDPLECHE);
		addQuestItem(BEDRO);
		addQuestItem(new int[]{
				CHEREP,
				REBRO,
				PREDPLECHE,
				BEDRO});
		addLevelCheck("lvl.htm", 70);		
		addQuestCompletedCheck("questnotdone.htm", 10866);		
	}

	
	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("NIKIA2.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("DUH2.htm"))
		{			
				st.setCond(2);
		}
		else if(event.equalsIgnoreCase("NIKIA4.htm"))
		{		/*добавить выдаччу бафа инвиз как я думаю, ид неизвестно */
				st.setCond(3);
		}
		else if(event.equalsIgnoreCase("KOSTI1a.htm") && st.getQuestItemsCount(CHEREP) < 1){						
		st.giveItems(CHEREP, 1);
		if(st.getQuestItemsCount(REBRO) > 0 && st.getQuestItemsCount(PREDPLECHE) > 0 && st.getQuestItemsCount(BEDRO) > 0)
		{
			st.setCond(4);
		}}
		else if(event.equalsIgnoreCase("KOSTI2a.htm") && st.getQuestItemsCount(REBRO) < 1){						
		st.giveItems(REBRO, 1);
		if(st.getQuestItemsCount(CHEREP) > 0 && st.getQuestItemsCount(PREDPLECHE) > 0 && st.getQuestItemsCount(BEDRO) > 0)
		{
			st.setCond(4);
		}}
		else if(event.equalsIgnoreCase("KOSTI3a.htm") && st.getQuestItemsCount(PREDPLECHE) < 1){						
		st.giveItems(PREDPLECHE, 1);
		if(st.getQuestItemsCount(CHEREP) > 0 && st.getQuestItemsCount(REBRO) > 0 && st.getQuestItemsCount(BEDRO) > 0)
		{
			st.setCond(4);
		}}
		else if(event.equalsIgnoreCase("KOSTI4a.htm") && st.getQuestItemsCount(BEDRO) < 1){						
		st.giveItems(BEDRO, 1);{
		if(st.getQuestItemsCount(CHEREP) > 0 && st.getQuestItemsCount(PREDPLECHE) > 0 && st.getQuestItemsCount(REBRO) > 0)
		{
			st.setCond(4);
		}
		}}
		
		else if(event.equalsIgnoreCase("DUH5.htm"))
		{
			if(st.getCond() == 4)
			{			 			
				st.addExpAndSp(300000, 9000);
				st.giveItems(57, 30000);			
				
				st.takeItems(CHEREP, -1);
				st.takeItems(REBRO, -1);
				st.takeItems(PREDPLECHE, -1);
				st.takeItems(BEDRO, -1);				
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

		if(npcId == NIKIA) {
			if(cond == 0)
				{	if(st.getPlayer().getLevel() >= 70)
												
						htmltext = "NIKIA.htm";		
					else
						htmltext = "lvl.htm";						
				}
					if(cond ==1){
						htmltext = "NIKIA2.htm";
				}					
				 if(cond ==2){
						htmltext = "NIKIA3.htm";
				}
					 }
		if(npcId == DUH) {
			if(cond ==1){
						htmltext = "DUH.htm";
				}	
			if(cond ==2){
						htmltext = "DUH2.htm";
				}				
			if(cond ==3){
						htmltext = "DUH3.htm";
				}					
			
			if(cond ==4){
						htmltext = "DUH4.htm";
				}					
			}
		if(npcId == KOSTI) {
			if(cond ==3 && st.getQuestItemsCount(CHEREP) < 1){
						htmltext = "KOSTI1.htm";						
				}
			else
				{htmltext = "KOSTI.htm";}
			}
		if(npcId == KOSTI2) {
			if(cond ==3 && st.getQuestItemsCount(REBRO) < 1){
						htmltext = "KOSTI2.htm";						
				}
			else
				{htmltext = "KOSTI.htm";}
			}
			if(npcId == KOSTI3) {
			if(cond ==3 && st.getQuestItemsCount(PREDPLECHE) < 1){
						htmltext = "KOSTI3.htm";						
				}
			else
				{htmltext = "KOSTI.htm";}
			}
			if(npcId == KOSTI4) {
			if(cond ==3 && st.getQuestItemsCount(BEDRO) < 1){
						htmltext = "KOSTI4.htm";						
				}
			else
				{htmltext = "KOSTI.htm";}
			}
			return htmltext;
	}	
}

