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

public class _11008_PreparationForDungeon extends Quest
{
	public final int STARDEN = 30220;
	public final int KENDELL = 30218;
	
	//mobs
	public final int DRIADA = 20013;
	public final int STARDRIAD = 20019;
	public final int ORCKABUVOIN = 20471;
	public final int ORCAD = 20473;
	public final int GLAVARORC = 20472;
	public final int CPAUK = 20308;
	public final int BPAUK = 20460;
	public final int HPAUK = 20466;
	
	
	//items
	public final int BINT = 90223;
	public final int TRAVA = 90224;
	public final int YAD = 90225;
	
	
	
	
	//	# [MOB_ID, REQUIRED, ITEM, NEED_COUNT, CHANCE]
	public final int[][] DROPLIST =
	{
			{ ORCKABUVOIN, BINT, 20, 100 },
			{ ORCAD, BINT, 20, 100 },
			{ GLAVARORC, BINT, 20, 100 },
			{ DRIADA, TRAVA, 20, 100 },
			{ STARDRIAD, TRAVA, 20, 100 },
			{ CPAUK, YAD, 20, 100 },
			{ BPAUK, YAD, 20, 100 },
			{ HPAUK, YAD, 20, 100 },
	};
	
	public _11008_PreparationForDungeon()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(KENDELL);
		addTalkId(STARDEN);		
		addQuestItem(BINT);
		addQuestItem(TRAVA);
		addQuestItem(YAD);		


		for(int[] element : DROPLIST)
			addKillId(element[0]);

		addQuestItem(new int[]
		{ BINT, TRAVA, YAD });


		addLevelCheck("lvl.htm", 11, 20);
		addRaceCheck("lvl.htm", Race.ELF);
		addQuestCompletedCheck("questnotdone.htm", 11007);
		
	}
	
	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("kend2.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("starden2.htm"))
		{			
				st.setCond(2);
		}
		
		else if(event.equalsIgnoreCase("starden4.htm") && st.getPlayer().getClassId().isOfType(ClassType.MYSTIC))
		{
			if(st.getCond() == 5)
			{			 			
				st.addExpAndSp(80000, 0);
				st.giveItems(29486, 2);
				st.giveItems(90308, 1);
				st.giveItems(90309, 1);
				st.giveItems(5790, 700);
				
				st.takeItems(BINT, -1);
				st.takeItems(TRAVA, -1);
				st.takeItems(YAD, -1);				
				st.finishQuest();			
			}
		}
		else if(event.equalsIgnoreCase("starden4.htm") && st.getPlayer().getClassId().isOfType(ClassType.FIGHTER))
		{
			if(st.getCond() == 5)
			{			 			
				st.addExpAndSp(80000, 0);
				st.giveItems(29486, 2);
				st.giveItems(90306, 1);
				st.giveItems(90307, 1);
				st.giveItems(5789, 1000);
				
				st.takeItems(BINT, -1);
				st.takeItems(TRAVA, -1);
				st.takeItems(YAD, -1);
						
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

		if(npcId == KENDELL) {
			if(cond == 0)
				{	if(st.getPlayer().getLevel() >= 11)
												
						htmltext = "kend.htm";		
					else
						htmltext = "lvl.htm";						
				}
					if(cond ==1){
						htmltext = "kend2.htm";
				}					
				 
					 }
		if(npcId == STARDEN) {
			if(cond ==1){
						htmltext = "starden.htm";
				}	
			if(cond ==2){
						htmltext = "starden2.htm";
				}				
			if(cond ==5){
						htmltext = "starden3.htm";
				}					
			}
			return htmltext;
	}
	
	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() == 2 )
		{
			if(npc.getNpcId() == ORCKABUVOIN || npc.getNpcId() == GLAVARORC || npc.getNpcId() == ORCAD)
			if(qs.rollAndGive(BINT, 1, 1, 20, 100))
				qs.setCond(3);
		}
		if(qs.getCond() == 3 )
		{
			if(npc.getNpcId() == DRIADA || npc.getNpcId() == STARDRIAD)
			if(qs.rollAndGive(TRAVA, 1, 1, 20, 100))
				qs.setCond(4);
		}
		if(qs.getCond() == 4 )
		{
			if(npc.getNpcId() == CPAUK || npc.getNpcId() == BPAUK || npc.getNpcId() == HPAUK)
			if(qs.rollAndGive(YAD, 1, 1, 20, 100))			
					qs.setCond(5);				
		}
		
		return null;
	}
}