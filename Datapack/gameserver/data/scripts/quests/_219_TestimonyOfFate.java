package quests;

import org.l2j.gameserver.model.GameObjectsStorage;
import org.l2j.gameserver.model.base.Race;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;

/**
 * @reworked by Bonux & SanyaDC
**/
public final class _219_TestimonyOfFate extends Quest
{
	//NPC's
	private static final int Kaira = 30476;
	private static final int Metheus = 30614;
	private static final int Ixia = 30463;
	private static final int AldersSpirit = 30613;
	private static final int Roa = 30114;
	private static final int Norman = 30210;
	private static final int Thifiell = 30358;
	private static final int Arkenia = 30419;
	private static final int BloodyPixy = 31845;
	private static final int BlightTreant = 31850;

	// Item's
	private static final int KairasLetter = 3173;
	private static final int MetheussFuneralJar = 3174;
	private static final int KasandrasRemains = 3175;
	private static final int HerbalismTextbook = 3176;
	private static final int IxiasList = 3177;
	private static final int MedusasIchor = 3178;
	private static final int MarshSpiderFluids = 3179;
	private static final int DeadSeekerDung = 3180;
	private static final int TyrantsBlood = 3181;
	private static final int NightshadeRoot = 3182;
	private static final int Belladonna = 3183;
	private static final int AldersSkull1 = 3184;
	private static final int AldersSkull2 = 3185;
	private static final int AldersReceipt = 3186;
	private static final int RevelationsManuscript = 3187;
	private static final int KairasRecommendation = 3189;
	private static final int KairasInstructions = 3188;
	private static final int PalusCharm = 3190;
	private static final int ThifiellsLetter = 3191;
	private static final int ArkeniasNote = 3192;
	private static final int PixyGarnet = 3193;
	private static final int BlightTreantSeed = 3199;
	private static final int GrandissSkull = 3194;
	private static final int KarulBugbearSkull = 3195;
	private static final int BrekaOverlordSkull = 3196;
	private static final int LetoOverlordSkull = 3197;
	private static final int BlackWillowLeaf = 3200;
	private static final int RedFairyDust = 3198;
	private static final int BlightTreantSap = 3201;
	private static final int ArkeniasLetter = 1246;
	private static final int MarkofFate = 3172;

	// Monster's
	private static final int HangmanTree = 20144;
	private static final int Medusa = 20158;
	private static final int MarshSpider = 20233;
	private static final int DeadSeeker = 20202;
	private static final int Tyrant = 20192;
	private static final int TyrantKingpin = 20193;
	private static final int MarshStakatoWorker = 20230;
	private static final int MarshStakato = 20157;
	private static final int MarshStakatoSoldier = 20232;
	private static final int MarshStakatoDrone = 20234;
	private static final int Grandis = 20554;
	private static final int KarulBugbear = 20600;
	private static final int BrekaOrcOverlord = 20270;
	private static final int LetoLizardmanOverlord = 20582;
	private static final int BlackWillowLurker = 27079;

	// Reward's
	private static final int EXP_REWARD = 164500; // EXP reward count
	private static final int SP_REWARD = 0; // SP reward count

	// Condition's
	private static final int MIN_LEVEL = 37;

	public _219_TestimonyOfFate()
	{
		super(PARTY_NONE, ONETIME);

		addStartNpc(Kaira);

		addTalkId(Metheus);
		addTalkId(Ixia);
		addTalkId(AldersSpirit);
		addTalkId(Roa);
		addTalkId(Norman);
		addTalkId(Thifiell);
		addTalkId(Arkenia);
		addTalkId(BloodyPixy);
		addTalkId(BlightTreant);

		addKillId(HangmanTree);
		addKillId(Medusa);
		addKillId(MarshSpider);
		addKillId(DeadSeeker);
		addKillId(Tyrant);
		addKillId(TyrantKingpin);
		addKillId(MarshStakatoWorker);
		addKillId(MarshStakato);
		addKillId(MarshStakatoSoldier);
		addKillId(MarshStakatoDrone);
		addKillId(Grandis);
		addKillId(KarulBugbear);
		addKillId(BrekaOrcOverlord);
		addKillId(LetoLizardmanOverlord);
		addKillId(BlackWillowLurker);

		addQuestItem(KairasLetter);
		addQuestItem(MetheussFuneralJar);
		addQuestItem(KasandrasRemains);
		addQuestItem(IxiasList);
		addQuestItem(Belladonna);
		addQuestItem(AldersSkull1);
		addQuestItem(AldersSkull2);
		addQuestItem(AldersReceipt);
		addQuestItem(RevelationsManuscript);
		addQuestItem(KairasRecommendation);
		addQuestItem(KairasInstructions);
		addQuestItem(ThifiellsLetter);
		addQuestItem(PalusCharm);
		addQuestItem(ArkeniasNote);
		addQuestItem(PixyGarnet);
		addQuestItem(BlightTreantSeed);
		addQuestItem(RedFairyDust);
		addQuestItem(BlightTreantSap);
		addQuestItem(ArkeniasLetter);
		addQuestItem(MedusasIchor);
		addQuestItem(MarshSpiderFluids);
		addQuestItem(DeadSeekerDung);
		addQuestItem(TyrantsBlood);
		addQuestItem(NightshadeRoot);
		addQuestItem(GrandissSkull);
		addQuestItem(KarulBugbearSkull);
		addQuestItem(BrekaOverlordSkull);
		addQuestItem(LetoOverlordSkull);
		addQuestItem(BlackWillowLeaf);

		addRaceCheck("magister_kaira_q0219_01.htm", Race.DARKELF);
		addLevelCheck("magister_kaira_q0219_02.htm", MIN_LEVEL);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("magister_kaira_q0219_05.htm"))
		{
			st.setCond(1);
			st.giveItems(KairasLetter, 1);
		}
		else if(event.equalsIgnoreCase("roa_q0219_04.htm"))
		{
			st.takeItems(AldersSkull2, 1);
			st.giveItems(AldersReceipt, 1);
			st.setCond(12);
		}
		else if(event.equalsIgnoreCase("magister_kaira_q0219_12.htm"))
		{
			if(st.getPlayer().getLevel() >= 38)
			{
				st.takeItems(RevelationsManuscript, -1);
				st.giveItems(KairasRecommendation, 1);
				st.setCond(15);
			}
			else
			{
				htmltext = "magister_kaira_q0219_13.htm";
				st.takeItems(RevelationsManuscript, -1);
				st.giveItems(KairasInstructions, 1);
				st.setCond(14);
			}
		}
		else if(event.equalsIgnoreCase("arkenia_q0219_02.htm"))
		{
			st.takeItems(ThifiellsLetter, -1);
			st.giveItems(ArkeniasNote, 1);
			st.setCond(17);
		}
		else if(event.equalsIgnoreCase("bloody_pixy_q0219_02.htm"))
			st.giveItems(PixyGarnet, 1);
		else if(event.equalsIgnoreCase("ti_mi_riran_q0219_02.htm"))
			st.giveItems(BlightTreantSeed, 1);
		else if(event.equalsIgnoreCase("arkenia_q0219_05.htm"))
		{
			st.takeItems(ArkeniasNote, -1);
			st.takeItems(RedFairyDust, -1);
			st.takeItems(BlightTreantSap, -1);
			st.giveItems(ArkeniasLetter, 1);
			st.setCond(18);
		}
		if(event.equalsIgnoreCase("AldersSpirit_Fail"))
		{
			NpcInstance isQuest = GameObjectsStorage.getByNpcId(AldersSpirit);
			if(isQuest != null)
				isQuest.deleteMe();
			st.setCond(9);
			return null;
		}

		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		if(st.getQuestItemsCount(MarkofFate) != 0)
			return COMPLETED_DIALOG;

		int npcId = npc.getNpcId();
		String htmltext = NO_QUEST_DIALOG;
		int cond = st.getCond();

		switch(npcId)
		{
			case Kaira:
				if(cond == 0)
					htmltext = "magister_kaira_q0219_03.htm";
				else if(cond == 2)
					htmltext = "magister_kaira_q0219_06.htm";
				else if(cond == 9 || cond == 10)
				{
					if(cond == 9)
					{
						st.setCond(10, false);
						st.takeItems(AldersSkull1, -1);
						if(st.getQuestItemsCount(AldersSkull2) == 0)
							st.giveItems(AldersSkull2, 1);
					}

					htmltext = "magister_kaira_q0219_09.htm";

					NpcInstance AldersSpiritObject = GameObjectsStorage.getByNpcId(AldersSpirit);
					if(AldersSpiritObject == null)
					{
						st.addSpawn(AldersSpirit);
						st.startQuestTimer("AldersSpirit_Fail", 300000);
					}
				}
				else if(cond == 13)
					htmltext = "magister_kaira_q0219_11.htm";
				else if(cond == 14)
				{
					if(st.getPlayer().getLevel() < 38)
						htmltext = "magister_kaira_q0219_14.htm";
					else if(st.getPlayer().getLevel() >= 38)
					{
						st.giveItems(KairasRecommendation, 1);
						st.takeItems(KairasInstructions, 1);
						htmltext = "magister_kaira_q0219_15.htm";
						st.setCond(15);
					}
				}
				else if(cond == 15)
					htmltext = "magister_kaira_q0219_16.htm";
				else if(cond == 16 || cond == 17)
					htmltext = "magister_kaira_q0219_17.htm";
				else if(st.getQuestItemsCount(MetheussFuneralJar) > 0 || st.getQuestItemsCount(KasandrasRemains) > 0)
					htmltext = "magister_kaira_q0219_07.htm";
				else if(st.getQuestItemsCount(HerbalismTextbook) > 0 || st.getQuestItemsCount(IxiasList) > 0)
					htmltext = "magister_kaira_q0219_08.htm";
				else if(st.getQuestItemsCount(AldersSkull2) > 0 || st.getQuestItemsCount(AldersReceipt) > 0)
					htmltext = "magister_kaira_q0219_10.htm";
			break;

			case Metheus:
				if(cond == 1)
				{
					htmltext = "brother_metheus_q0219_01.htm";
					st.takeItems(KairasLetter, -1);
					st.giveItems(MetheussFuneralJar, 1);
					st.setCond(2);
				}
				else if(cond == 2)
					htmltext = "brother_metheus_q0219_02.htm";
				else if(cond == 3)
				{
					st.takeItems(KasandrasRemains, -1);
					st.giveItems(HerbalismTextbook, 1);
					htmltext = "brother_metheus_q0219_03.htm";
					st.setCond(5);
				}
				else if(cond == 8)
				{
					st.takeItems(Belladonna, -1);
					st.giveItems(AldersSkull1, 1);
					htmltext = "brother_metheus_q0219_05.htm";
					st.setCond(9);
				}
				else if(st.getQuestItemsCount(HerbalismTextbook) > 0 || st.getQuestItemsCount(IxiasList) > 0)
					htmltext = "brother_metheus_q0219_04.htm";
				else if(st.getQuestItemsCount(AldersSkull1) > 0 || st.getQuestItemsCount(AldersSkull2) > 0 || st.getQuestItemsCount(AldersReceipt) > 0 || st.getQuestItemsCount(RevelationsManuscript) > 0 || st.getQuestItemsCount(KairasInstructions) > 0 || st.getQuestItemsCount(KairasRecommendation) > 0)
					htmltext = "brother_metheus_q0219_06.htm";

			break;

			case Ixia:
				if(cond == 5)
				{
					st.takeItems(HerbalismTextbook, -1);
					st.giveItems(IxiasList, 1);
					htmltext = "master_ixia_q0219_01.htm";
					st.setCond(6);
				}
				else if(cond == 6)
					htmltext = "master_ixia_q0219_02.htm";
				else if(cond == 7)
				{
					st.takeItems(MedusasIchor, -1);
					st.takeItems(MarshSpiderFluids, -1);
					st.takeItems(DeadSeekerDung, -1);
					st.takeItems(TyrantsBlood, -1);
					st.takeItems(NightshadeRoot, -1);
					st.takeItems(IxiasList, -1);
					st.giveItems(Belladonna, 1);
					htmltext = "master_ixia_q0219_03.htm";
					st.setCond(8);
				}
				else if(cond == 8)
					htmltext = "master_ixia_q0219_04.htm";
				else if(st.getQuestItemsCount(AldersSkull1) > 0 || st.getQuestItemsCount(AldersSkull2) > 0 || st.getQuestItemsCount(AldersReceipt) > 0 || st.getQuestItemsCount(RevelationsManuscript) > 0 || st.getQuestItemsCount(KairasInstructions) > 0 || st.getQuestItemsCount(KairasRecommendation) > 0)
					htmltext = "master_ixia_q0219_05.htm";
			break;

			case AldersSpirit:
				if(cond == 10)
				{
					htmltext = "alders_spirit_q0219_02.htm";
					st.setCond(11);
					st.cancelQuestTimer("AldersSpirit_Fail");
					NpcInstance isQuest = GameObjectsStorage.getByNpcId(AldersSpirit);
					if(isQuest != null)
						isQuest.deleteMe();
				}
			break;

			case Roa:
				if(cond == 11)
					htmltext = "roa_q0219_01.htm";
				else if(cond == 12)
					htmltext = "roa_q0219_05.htm";
				else if(st.getQuestItemsCount(RevelationsManuscript) > 0 || st.getQuestItemsCount(KairasInstructions) > 0 || st.getQuestItemsCount(KairasRecommendation) > 0)
					htmltext = "roa_q0219_06.htm";
				break;

			case Norman:
				if(cond == 12)
				{
					st.takeItems(AldersReceipt, -1);
					st.giveItems(RevelationsManuscript, 1);
					htmltext = "warehouse_keeper_norman_q0219_01.htm";
					st.setCond(13);
				}
				else if(cond == 13)
					htmltext = "warehouse_keeper_norman_q0219_02.htm";
			break;

			case Thifiell:
				if(cond == 15)
				{
					st.takeItems(KairasRecommendation, -1);
					st.giveItems(ThifiellsLetter, 1);
					st.giveItems(PalusCharm, 1);
					htmltext = "tetrarch_thifiell_q0219_01.htm";
					st.setCond(16);
				}
				else if(cond == 16)
					htmltext = "tetrarch_thifiell_q0219_02.htm";
				else if(cond == 17)
					htmltext = "tetrarch_thifiell_q0219_03.htm";
				else if(cond == 18)
				{
					if(!st.getPlayer().getVarBoolean("prof2.2"))
					{
						st.addExpAndSp(EXP_REWARD, SP_REWARD);
						st.getPlayer().setVar("prof2.2", "1", -1);
					}
					st.takeItems(ArkeniasLetter, -1);
					st.takeItems(PalusCharm, -1);
					st.giveItems(MarkofFate, 1);
					htmltext = "tetrarch_thifiell_q0219_04.htm";
					st.finishQuest();
				}
			break;

			case Arkenia:
				if(cond == 16)
					htmltext = "arkenia_q0219_01.htm";
				else if(cond == 17)
				{
					if(st.getQuestItemsCount(RedFairyDust) < 1 || st.getQuestItemsCount(BlightTreantSap) < 1)
						htmltext = "arkenia_q0219_03.htm";
					else if(st.getQuestItemsCount(RedFairyDust) >= 1 && st.getQuestItemsCount(BlightTreantSap) >= 1)
						htmltext = "arkenia_q0219_04.htm";
				}
				else if(cond == 18)
					htmltext = "arkenia_q0219_06.htm";
			break;

			case BloodyPixy:
				if(cond == 17)
				{
					if(st.getQuestItemsCount(RedFairyDust) == 0 && st.getQuestItemsCount(PixyGarnet) == 0)
						htmltext = "bloody_pixy_q0219_01.htm";
					else if(st.getQuestItemsCount(RedFairyDust) == 0 && st.getQuestItemsCount(PixyGarnet) > 0 && (st.getQuestItemsCount(GrandissSkull) < 1 || st.getQuestItemsCount(KarulBugbearSkull) < 1 || st.getQuestItemsCount(BrekaOverlordSkull) < 1 || st.getQuestItemsCount(LetoOverlordSkull) < 1))
						htmltext = "bloody_pixy_q0219_03.htm";
					else if(st.getQuestItemsCount(RedFairyDust) == 0 && st.getQuestItemsCount(PixyGarnet) > 0 && st.getQuestItemsCount(GrandissSkull) >= 1 && st.getQuestItemsCount(KarulBugbearSkull) >= 1 && st.getQuestItemsCount(BrekaOverlordSkull) >= 1 && st.getQuestItemsCount(LetoOverlordSkull) >= 1)
					{
						st.takeItems(GrandissSkull, -1);
						st.takeItems(KarulBugbearSkull, -1);
						st.takeItems(BrekaOverlordSkull, -1);
						st.takeItems(LetoOverlordSkull, -1);
						st.takeItems(PixyGarnet, -1);
						st.giveItems(RedFairyDust, 1);
						htmltext = "bloody_pixy_q0219_04.htm";
					}
					else if(st.getQuestItemsCount(RedFairyDust) != 0)
						htmltext = "bloody_pixy_q0219_05.htm";
				}
			break;

			case BlightTreant:
				if(cond == 17)
				{
					if(st.getQuestItemsCount(BlightTreantSap) == 0 && st.getQuestItemsCount(BlightTreantSeed) == 0)
						htmltext = "ti_mi_riran_q0219_01.htm";
					else if(st.getQuestItemsCount(BlightTreantSap) == 0 && st.getQuestItemsCount(BlightTreantSeed) > 0 && st.getQuestItemsCount(BlackWillowLeaf) == 0)
						htmltext = "ti_mi_riran_q0219_03.htm";
					else if(st.getQuestItemsCount(BlightTreantSap) == 0 && st.getQuestItemsCount(BlightTreantSeed) > 0 && st.getQuestItemsCount(BlackWillowLeaf) > 0)
					{
						st.takeItems(BlackWillowLeaf, -1);
						st.takeItems(BlightTreantSeed, -1);
						st.giveItems(BlightTreantSap, 1);
						htmltext = "ti_mi_riran_q0219_04.htm";
					}
					else if(st.getQuestItemsCount(BlightTreantSap) > 0)
						htmltext = "ti_mi_riran_q0219_05.htm";
				}
			break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		final int npcId = npc.getNpcId();
		final int cond = st.getCond();

		if(cond == 2)
		{
			if(npcId == HangmanTree)
			{
				st.setCond(3);
				st.takeItems(MetheussFuneralJar, -1);
				st.giveItems(KasandrasRemains, 1);
			}
		}
		if(cond == 6)
		{
			if(st.getQuestItemsCount(IxiasList) == 0)
				return null;

			int itemId;
			switch(npcId)
			{
				case Medusa:
					itemId = MedusasIchor;
					break;
				case MarshSpider:
					itemId = MarshSpiderFluids;
					break;
				case DeadSeeker:
					itemId = DeadSeekerDung;
					break;
				case Tyrant:
				case TyrantKingpin:
					itemId = TyrantsBlood;
					break;
				case MarshStakatoWorker:
				case MarshStakato:
				case MarshStakatoSoldier:
				case MarshStakatoDrone:
					itemId = NightshadeRoot;
					break;
				default:
					return null;
			}

			st.rollAndGive(itemId, 1, 1, 10, 100);

			if(st.getQuestItemsCount(MedusasIchor) < 10)
				return null;
			if(st.getQuestItemsCount(MarshSpiderFluids) < 10)
				return null;
			if(st.getQuestItemsCount(DeadSeekerDung) < 10)
				return null;
			if(st.getQuestItemsCount(TyrantsBlood) < 10)
				return null;
			if(st.getQuestItemsCount(NightshadeRoot) < 10)
				return null;

			st.setCond(7);
		}
		if(cond == 17)
		{
			if(st.getQuestItemsCount(PixyGarnet) > 0)
			{
				if(npc.getNpcId() == 20554)
				st.rollAndGive(GrandissSkull, 1, 1, 1, 100);
			if(npc.getNpcId() == 20600)
				st.rollAndGive(KarulBugbearSkull, 1, 1, 1, 100);
			if(npc.getNpcId() == 20270)
				st.rollAndGive(BrekaOverlordSkull, 1, 1, 1, 100);
			if(npc.getNpcId() == 20582)
				st.rollAndGive(LetoOverlordSkull, 1, 1, 1, 100);
			}
			if(st.getQuestItemsCount(BlightTreantSeed) > 0)
			{
				if(npc.getNpcId() == 27079)
				st.rollAndGive(BlackWillowLeaf, 1, 1, 1, 100);				
			}
		}
		return null;
	}
}