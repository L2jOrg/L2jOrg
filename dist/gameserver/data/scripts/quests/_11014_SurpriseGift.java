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

public class _11014_SurpriseGift extends Quest
{
	public final int PAIN = 30136;
	public final int TALLOT = 30141;
	
	//mobs
	public final int ELFZ = 20015;
	public final int ELFZISL = 20020;
	public final int GNMOUSE = 20433;
	public final int REDEYEMOUSE = 20392;
	public final int KAMVOI = 20379;
	public final int KAMHRAN = 20380;
	public final int TEMNYZ = 20105;
	
	
	
	//items
	public final int ZUB = 90244;
	public final int WKURA = 90245;
	public final int KAMEN = 90246;
	public final int KOSTI = 90247;
	
	
	
	
	//	# [MOB_ID, REQUIRED, ITEM, NEED_COUNT, CHANCE]
	public final int[][] DROPLIST =
	{
			{ ELFZ, ZUB, 10, 100 },
			{ ELFZISL, ZUB, 10, 100 },
			{ GNMOUSE, WKURA, 10, 100 },
			{ REDEYEMOUSE, WKURA, 10, 100 },
			{ KAMVOI, KAMEN, 10, 100 },
			{ KAMHRAN, KAMEN, 10, 100 },
			{ TEMNYZ, KOSTI, 20, 100 },			
	};
	
	public _11014_SurpriseGift()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(TALLOT);
		addTalkId(PAIN);		
		addQuestItem(ZUB);
		addQuestItem(WKURA);
		addQuestItem(KAMEN);		
		addQuestItem(KOSTI);		


		for(int[] element : DROPLIST)
			addKillId(element[0]);

		addQuestItem(new int[]
		{ KAMEN, ZUB, WKURA, KOSTI });


		addLevelCheck("lvl.htm", 11, 20);
		addRaceCheck("lvl.htm", Race.DARKELF);
		addQuestCompletedCheck("questnotdone.htm", 11013);
		
	}
	
	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("tallot2.htm"))
		{
			st.setCond(1);
		}
		else if(event.equalsIgnoreCase("pain2.htm"))
		{			
				st.setCond(2);
		}
		
		else if(event.equalsIgnoreCase("pain4.htm") && st.getPlayer().getClassId().isOfType(ClassType.MYSTIC))
		{
			if(st.getCond() == 6)
			{			 			
				st.addExpAndSp(80000, 0);
				st.giveItems(29486, 2);
				st.giveItems(90308, 1);
				st.giveItems(90309, 1);
				st.giveItems(5790, 700);
				
				st.takeItems(ZUB, -1);
				st.takeItems(WKURA, -1);
				st.takeItems(KAMEN, -1);
				st.takeItems(KOSTI, -1);				
				st.finishQuest();			
			}
		}
		else if(event.equalsIgnoreCase("pain4.htm") && st.getPlayer().getClassId().isOfType(ClassType.FIGHTER))
		{
			if(st.getCond() == 6)
			{			 			
				st.addExpAndSp(80000, 0);
				st.giveItems(29486, 2);
				st.giveItems(90306, 1);
				st.giveItems(90307, 1);
				st.giveItems(5789, 1000);
				
				st.takeItems(ZUB, -1);
				st.takeItems(WKURA, -1);
				st.takeItems(KAMEN, -1);
				st.takeItems(KOSTI, -1);
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

		if(npcId == TALLOT) {
			if(cond == 0)
				{	if(st.getPlayer().getLevel() >= 11)
												
						htmltext = "tallot.htm";		
					else
						htmltext = "lvl.htm";						
				}
					if(cond ==1){
						htmltext = "tallot2.htm";
				}					
				 
					 }
		if(npcId == PAIN) {
			if(cond ==1){
						htmltext = "pain.htm";
				}	
			if(cond ==2){
						htmltext = "pain2.htm";
				}				
			if(cond ==6){
						htmltext = "pain3.htm";
				}					
			}
			return htmltext;
	}
	
	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs.getCond() == 2 )
		{
			if(npc.getNpcId() == ELFZ || npc.getNpcId() == ELFZISL)
			if(qs.rollAndGive(ZUB, 1, 1, 10, 100))
				qs.setCond(3);
		}
		if(qs.getCond() == 3 )
		{
			if(npc.getNpcId() == GNMOUSE || npc.getNpcId() == REDEYEMOUSE)
			if(qs.rollAndGive(WKURA, 1, 1, 10, 100))
				qs.setCond(4);
		}
		if(qs.getCond() == 4 )
		{
			if(npc.getNpcId() == KAMVOI || npc.getNpcId() == KAMHRAN)
			if(qs.rollAndGive(KAMEN, 1, 1, 10, 100))			
					qs.setCond(5);				
		}
		if(qs.getCond() == 5 )
		{
			if(npc.getNpcId() == TEMNYZ)
			if(qs.rollAndGive(KOSTI, 1, 1, 20, 100))			
					qs.setCond(6);				
		}
		return null;
	}
}