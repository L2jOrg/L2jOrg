package quests;

import org.l2j.gameserver.model.GameObjectsStorage;
import org.l2j.gameserver.model.base.Race;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;

/**
 * Квест на вторую профессию Testimony Of Glory
 *
 * @author Sergey Ibryaev aka Artful
 */

public final class _220_TestimonyOfGlory extends Quest
{
	//NPC
	private static final int Vokian = 30514;
	private static final int Chianta = 30642;
	private static final int Manakia = 30515;
	private static final int Kasman = 30501;
	private static final int Voltar = 30615;
	private static final int Kepra = 30616;
	private static final int Burai = 30617;
	private static final int Harak = 30618;
	private static final int Driko = 30619;
	private static final int Tanapi = 30571;
	private static final int Kakai = 30565;
	//Quest Items
	private static final int VokiansOrder = 3204;
	private static final int ManashenShard = 3205;
	private static final int TyrantTalon = 3206;
	private static final int GuardianBasiliskFang = 3207;
	private static final int VokiansOrder2 = 3208;
	private static final int NecklaceOfAuthority = 3209;
	private static final int ChiantaOrder1st = 3210;
	private static final int ScepterOfBreka = 3211;
	private static final int ScepterOfEnku = 3212;
	private static final int ScepterOfVuku = 3213;
	private static final int ScepterOfTurek = 3214;
	private static final int ScepterOfTunath = 3215;
	private static final int ChiantasOrder2rd = 3216;
	private static final int ChiantasOrder3rd = 3217;
	private static final int TamlinOrcSkull = 3218;
	private static final int TimakOrcHead = 3219;
	private static final int ScepterBox = 3220;
	private static final int PashikasHead = 3221;
	private static final int VultusHead = 3222;
	private static final int GloveOfVoltar = 3223;
	private static final int EnkuOverlordHead = 3224;
	private static final int GloveOfKepra = 3225;
	private static final int MakumBugbearHead = 3226;
	private static final int GloveOfBurai = 3227;
	private static final int ManakiaLetter1st = 3228;
	private static final int ManakiaLetter2st = 3229;
	private static final int KasmansLetter1rd = 3230;
	private static final int KasmansLetter2rd = 3231;
	private static final int KasmansLetter3rd = 3232;
	private static final int DrikosContract = 3233;
	private static final int StakatoDroneHusk = 3234;
	private static final int TanapisOrder = 3235;
	private static final int ScepterOfTantos = 3236;
	private static final int RitualBox = 3237;
	//Items
	private static final int MarkOfGlory = 3203;
	//MOB
	private static final int Tyrant = 20192;
	private static final int TyrantKingpin = 20193;
	private static final int GuardianBasilisk = 20550;
	private static final int ManashenGargoyle = 20563;
	private static final int MarshStakatoDrone = 20234;
	private static final int PashikasSonOfVoltarQuestMonster = 27080;
	private static final int VultusSonOfVoltarQuestMonster = 27081;
	private static final int EnkuOrcOverlordQuestMonster = 27082;
	private static final int MakumBugbearThugQuestMonster = 27083;
	private static final int TimakOrc = 20583;
	private static final int TimakOrcArcher = 20584;
	private static final int TimakOrcSoldier = 20585;
	private static final int TimakOrcWarrior = 20586;
	private static final int TimakOrcShaman = 20587;
	private static final int TimakOrcOverlord = 20588;
	private static final int TamlinOrc = 20601;
	private static final int TamlinOrcArcher = 20602;
	private static final int RagnaOrcOverlord = 20778;
	private static final int RagnaOrcSeer = 20779;
	private static final int RevenantOfTantosChief = 27086;

	//Drop Cond
	//# [COND, NEWCOND, ID, REQUIRED, ITEM, NEED_COUNT, CHANCE, DROP]
	private static final int[][] DROPLIST_COND = {
			{
					1,
					0,
					ManashenGargoyle,
					VokiansOrder,
					ManashenShard,
					10,
					70,
					1
			},
			{
					1,
					0,
					Tyrant,
					VokiansOrder,
					TyrantTalon,
					10,
					70,
					1
			},
			{
					1,
					0,
					TyrantKingpin,
					VokiansOrder,
					TyrantTalon,
					10,
					70,
					1
			},
			{
					1,
					0,
					GuardianBasilisk,
					VokiansOrder,
					GuardianBasiliskFang,
					10,
					70,
					1
			},
			{
					4,
					0,
					MarshStakatoDrone,
					DrikosContract,
					StakatoDroneHusk,
					30,
					70,
					1
			},
			{
					4,
					0,
					EnkuOrcOverlordQuestMonster,
					GloveOfKepra,
					EnkuOverlordHead,
					4,
					100,
					1
			},
			{
					4,
					0,
					MakumBugbearThugQuestMonster,
					GloveOfBurai,
					MakumBugbearHead,
					2,
					100,
					1
			},
			{
					6,
					0,
					TimakOrc,
					ChiantasOrder3rd,
					TimakOrcHead,
					20,
					50,
					1
			},
			{
					6,
					0,
					TimakOrcArcher,
					ChiantasOrder3rd,
					TimakOrcHead,
					20,
					60,
					1
			},
			{
					6,
					0,
					TimakOrcSoldier,
					ChiantasOrder3rd,
					TimakOrcHead,
					20,
					70,
					1
			},
			{
					6,
					0,
					TimakOrcWarrior,
					ChiantasOrder3rd,
					TimakOrcHead,
					20,
					80,
					1
			},
			{
					6,
					0,
					TimakOrcShaman,
					ChiantasOrder3rd,
					TimakOrcHead,
					20,
					90,
					1
			},
			{
					6,
					0,
					TimakOrcOverlord,
					ChiantasOrder3rd,
					TimakOrcHead,
					20,
					100,
					1
			},
			{
					6,
					0,
					TamlinOrc,
					ChiantasOrder3rd,
					TamlinOrcSkull,
					20,
					50,
					1
			},
			{
					6,
					0,
					TamlinOrcArcher,
					ChiantasOrder3rd,
					TamlinOrcSkull,
					20,
					60,
					1
			}
	};

	public _220_TestimonyOfGlory()
	{
		super(PARTY_NONE, ONETIME);
		addStartNpc(Vokian);
		addTalkId(Chianta);
		addTalkId(Manakia);
		addTalkId(Kasman);
		addTalkId(Voltar);
		addTalkId(Kepra);
		addTalkId(Burai);
		addTalkId(Harak);
		addTalkId(Driko);
		addTalkId(Tanapi);
		addTalkId(Kakai);

		for(int i = 0; i < DROPLIST_COND.length; i++)
			addKillId(DROPLIST_COND[i][2]);

		addKillId(PashikasSonOfVoltarQuestMonster);
		addKillId(VultusSonOfVoltarQuestMonster);
		addKillId(RagnaOrcOverlord);
		addKillId(RagnaOrcSeer);
		addKillId(RevenantOfTantosChief);

		addQuestItem(new int[]{
				VokiansOrder,
				VokiansOrder2,
				NecklaceOfAuthority,
				ChiantaOrder1st,
				ManakiaLetter1st,
				ManakiaLetter2st,
				KasmansLetter1rd,
				KasmansLetter2rd,
				KasmansLetter3rd,
				ScepterOfBreka,
				PashikasHead,
				VultusHead,
				GloveOfVoltar,
				GloveOfKepra,
				ScepterOfEnku,
				ScepterOfTurek,
				GloveOfBurai,
				ScepterOfTunath,
				DrikosContract,
				ChiantasOrder2rd,
				ChiantasOrder3rd,
				ScepterBox,
				TanapisOrder,
				ScepterOfTantos,
				RitualBox,
				ManashenShard,
				TyrantTalon,
				GuardianBasiliskFang,
				StakatoDroneHusk,
				EnkuOverlordHead,
				MakumBugbearHead,
				TimakOrcHead,
				TamlinOrcSkull
		});

		addClassIdCheck("30514-01.htm", 45, 47, 50);
		addLevelCheck("30514-02.htm", 37);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("RETURN"))
			return null;
		else if(event.equalsIgnoreCase("30514-05.htm"))
		{
			st.setCond(1);
			st.giveItems(VokiansOrder, 1);
		}
		else if(event.equalsIgnoreCase("30642-03.htm"))
		{
			st.takeItems(VokiansOrder2, -1);
			st.giveItems(ChiantaOrder1st, 1);
			st.setCond(4);
		}
		else if(event.equalsIgnoreCase("30571-03.htm"))
		{
			st.takeItems(ScepterBox, -1);
			st.giveItems(TanapisOrder, 1);
			st.setCond(9);
		}
		else if(event.equalsIgnoreCase("30642-07.htm"))
		{
			st.takeItems(ScepterOfBreka, -1);
			st.takeItems(ScepterOfEnku, -1);
			st.takeItems(ScepterOfVuku, -1);
			st.takeItems(ScepterOfTurek, -1);
			st.takeItems(ScepterOfTunath, -1);
			st.takeItems(ChiantaOrder1st, -1);
			if(st.getPlayer().getLevel() >= 38)
			{
				st.giveItems(ChiantasOrder3rd, 1);
				st.setCond(6);
			}
			else
			{
				htmltext = "30642-06.htm";
				st.giveItems(ChiantasOrder2rd, 1);
			}
		}
		else if(event.equalsIgnoreCase("BREKA"))
		{
			if(st.getQuestItemsCount(ScepterOfBreka) > 0)
				htmltext = "30515-02.htm";
			else if(st.getQuestItemsCount(ManakiaLetter1st) > 0)
				htmltext = "30515-04.htm";
			else
			{
				htmltext = "30515-03.htm";
				st.giveItems(ManakiaLetter1st, 1);
			}
		}
		else if(event.equalsIgnoreCase("ENKU"))
		{
			if(st.getQuestItemsCount(ScepterOfEnku) > 0)
				htmltext = "30515-05.htm";
			else if(st.getQuestItemsCount(ManakiaLetter2st) > 0)
				htmltext = "30515-07.htm";
			else
			{
				htmltext = "30515-06.htm";
				st.giveItems(ManakiaLetter2st, 1);
			}
		}
		else if(event.equalsIgnoreCase("VUKU"))
		{
			if(st.getQuestItemsCount(ScepterOfVuku) > 0)
				htmltext = "30501-02.htm";
			else if(st.getQuestItemsCount(KasmansLetter1rd) > 0)
				htmltext = "30501-04.htm";
			else
			{
				htmltext = "30501-03.htm";
				st.giveItems(KasmansLetter1rd, 1);
			}
		}
		else if(event.equalsIgnoreCase("TUREK"))
		{
			if(st.getQuestItemsCount(ScepterOfTurek) > 0)
				htmltext = "30501-05.htm";
			else if(st.getQuestItemsCount(KasmansLetter2rd) > 0)
				htmltext = "30501-07.htm";
			else
			{
				htmltext = "30501-06.htm";
				st.giveItems(KasmansLetter2rd, 1);
			}
		}
		else if(event.equalsIgnoreCase("TUNATH"))
		{
			if(st.getQuestItemsCount(ScepterOfTunath) > 0)
				htmltext = "30501-08.htm";
			else if(st.getQuestItemsCount(KasmansLetter3rd) > 0)
				htmltext = "30501-10.htm";
			else
			{
				htmltext = "30501-09.htm";
				st.giveItems(KasmansLetter3rd, 1);
			}
		}
		else if(event.equalsIgnoreCase("30615-04.htm"))
		{
			//Проверяем есть ли в мире уже квест монстры
			int spawn = 0;
			NpcInstance isQuest = GameObjectsStorage.getByNpcId(PashikasSonOfVoltarQuestMonster);
			if(isQuest != null)
				spawn = 1;
			isQuest = GameObjectsStorage.getByNpcId(VultusSonOfVoltarQuestMonster);
			if(isQuest != null)
				spawn = 1;
			if(spawn == 1) //если хоть один моб есть в мире, ставим таймер на удаление их(ня всякий) + говорим игроку подождать.
			{
				if(!st.isRunningQuestTimer("Wait1"))
					st.startQuestTimer("Wait1", 300000);
				htmltext = "<html><head><body>Please wait 5 minutes</body></html>";
			}
			else
			{
				st.takeItems(ManakiaLetter1st, -1);
				st.giveItems(GloveOfVoltar, 1);
				st.cancelQuestTimer("Wait1");
				st.startQuestTimer("PashikasSonOfVoltarQuestMonster", 200000);
				st.startQuestTimer("VultusSonOfVoltarQuestMonster", 200000);
				st.addSpawn(PashikasSonOfVoltarQuestMonster);
				st.addSpawn(VultusSonOfVoltarQuestMonster);
				st.playSound(SOUND_BEFORE_BATTLE);
			}
		}
		else if(event.equalsIgnoreCase("30616-04.htm"))
		{
			//Проверяем есть ли в мире уже квест монстры
			NpcInstance isQuest = GameObjectsStorage.getByNpcId(EnkuOrcOverlordQuestMonster);
			if(isQuest != null)
			{
				if(!st.isRunningQuestTimer("Wait2"))
					st.startQuestTimer("Wait2", 300000);
				htmltext = "<html><head><body>Please wait 5 minutes</body></html>";
			}
			else
			{
				st.takeItems(ManakiaLetter2st, -1);
				st.giveItems(GloveOfKepra, 1);
				st.cancelQuestTimer("Wait2");
				st.startQuestTimer("EnkuOrcOverlordQuestMonster", 200000);
				st.addSpawn(EnkuOrcOverlordQuestMonster);
				st.addSpawn(EnkuOrcOverlordQuestMonster);
				st.addSpawn(EnkuOrcOverlordQuestMonster);
				st.addSpawn(EnkuOrcOverlordQuestMonster);
				st.playSound(SOUND_BEFORE_BATTLE);
			}
		}
		else if(event.equalsIgnoreCase("30617-04.htm"))
		{
			//Проверяем есть ли в мире уже квест монстры
			NpcInstance isQuest = GameObjectsStorage.getByNpcId(MakumBugbearThugQuestMonster);
			if(isQuest != null)
			{
				if(!st.isRunningQuestTimer("Wait3"))
					st.startQuestTimer("Wait3", 300000);
				htmltext = "<html><head><body>Please wait 5 minutes</body></html>";
			}
			else
			{
				st.takeItems(KasmansLetter2rd, -1);
				st.giveItems(GloveOfBurai, 1);
				st.cancelQuestTimer("Wait3");
				st.startQuestTimer("MakumBugbearThugQuestMonster", 200000);
				st.addSpawn(MakumBugbearThugQuestMonster);
				st.addSpawn(MakumBugbearThugQuestMonster);
				st.playSound(SOUND_BEFORE_BATTLE);
			}

		}
		else if(event.equalsIgnoreCase("30618-03.htm"))
		{
			st.takeItems(KasmansLetter3rd, -1);
			st.giveItems(ScepterOfTunath, 1);
			if(st.getQuestItemsCount(ScepterOfBreka) != 0 && st.getQuestItemsCount(ScepterOfEnku) != 0 && st.getQuestItemsCount(ScepterOfVuku) != 0 && st.getQuestItemsCount(ScepterOfTurek) != 0 && st.getQuestItemsCount(ScepterOfTunath) != 0)
			{
				st.setCond(5);
			}
		}
		else if(event.equalsIgnoreCase("30619-03.htm"))
		{
			st.takeItems(KasmansLetter1rd, -1);
			st.giveItems(DrikosContract, 1);
		}
		else if(event.equalsIgnoreCase("Wait1") || event.equalsIgnoreCase("PashikasSonOfVoltarQuestMonster") || event.equalsIgnoreCase("VultusSonOfVoltarQuestMonster"))
		{
			mobDelete(PashikasSonOfVoltarQuestMonster, 1);
			mobDelete(VultusSonOfVoltarQuestMonster, 1);
			st.cancelQuestTimer("Wait1");
			st.cancelQuestTimer("PashikasSonOfVoltarQuestMonster");
		}
		else if(event.equalsIgnoreCase("Wait2") || event.equalsIgnoreCase("EnkuOrcOverlordQuestMonster"))
		{
			mobDelete(EnkuOrcOverlordQuestMonster, 4);
			st.cancelQuestTimer("Wait2");
			st.cancelQuestTimer("EnkuOrcOverlordQuestMonster");
		}
		else if(event.equalsIgnoreCase("Wait3") || event.equalsIgnoreCase("MakumBugbearThugQuestMonster"))
		{

			mobDelete(MakumBugbearThugQuestMonster, 2);
			st.cancelQuestTimer("Wait3");
			st.cancelQuestTimer("MakumBugbearThugQuestMonster");
		}
		else if(event.equalsIgnoreCase("Wait4") || event.equalsIgnoreCase("RevenantOfTantosChief"))
		{
			//Тележка...
			NpcInstance isQuest = GameObjectsStorage.getByNpcId(RevenantOfTantosChief);
			if(isQuest != null)
				isQuest.deleteMe();
			st.cancelQuestTimer("Wait4");
			st.cancelQuestTimer("RevenantOfTantosChief");
		}
		return htmltext;
	}

	private void mobDelete(int mobId, int count)
	{
		NpcInstance isQuest = GameObjectsStorage.getByNpcId(mobId);
		for(int i = 0; i < count; i++)
		{
			if(isQuest != null)
				isQuest.deleteMe();
		}
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		if(st.getQuestItemsCount(MarkOfGlory) != 0)
		{
			return COMPLETED_DIALOG;
		}
		int npcId = npc.getNpcId();
		String htmltext = NO_QUEST_DIALOG;
		int cond = st.getCond();

			switch (npcId)
			{
				case Vokian:
					if(st.getCond() == 0)
						htmltext = "30514-03.htm";
					else if(cond == 1)
						htmltext = "30514-06.htm";
					else if(cond == 2)
					{
						st.takeItems(VokiansOrder, -1);
						st.takeItems(ManashenShard, -10);
						st.takeItems(TyrantTalon, -10);
						st.takeItems(GuardianBasiliskFang, -10);
						st.giveItems(VokiansOrder2, 1);
						st.giveItems(NecklaceOfAuthority, 1);
						htmltext = "30514-08.htm";
						st.setCond(3);
					}
					else if(cond == 3)
						htmltext = "30514-09.htm";
					else if(cond == 4)
						htmltext = "30514-10.htm";
					break;

				case Chianta:
					if(cond == 3)
						htmltext = "30642-01.htm";
					else if(cond == 4)
						htmltext = "30642-04.htm";
					else if(cond == 5)
					{
						if(st.getQuestItemsCount(ChiantaOrder1st) > 0)
							htmltext = "30642-05.htm";
						else if(st.getQuestItemsCount(ChiantasOrder2rd) > 0)
							if(st.getPlayer().getLevel() >= 38)
							{
								st.takeItems(ChiantasOrder2rd, -1);
								st.giveItems(ChiantasOrder3rd, 1);
								htmltext = "30642-09.htm";
								st.setCond(6);
							}
							else
								htmltext = "30642-08.htm";
					}
					else if(cond == 6)
						htmltext = "30642-10.htm";
					else if(cond == 7)
					{
						st.takeItems(NecklaceOfAuthority, -1);
						st.takeItems(ChiantasOrder3rd, -1);
						st.takeItems(TamlinOrcSkull, -1);
						st.takeItems(TimakOrcHead, -1);
						st.giveItems(ScepterBox, 1);
						htmltext = "30642-11.htm";
						st.setCond(8);
					}
					else if(cond == 8)
						htmltext = "30642-12.htm";
					break;

				case Manakia:
					if(cond == 4)
						htmltext = "30515-01.htm";
					break;

				case Kasman:
					if(cond == 4)
						htmltext = "30501-01.htm";
					break;

				case Voltar:
					if(cond == 4)
						if(st.getQuestItemsCount(ManakiaLetter1st) > 0)
							htmltext = "30615-02.htm";
						else if(st.getQuestItemsCount(GloveOfVoltar) > 0 && (st.getQuestItemsCount(PashikasHead) == 0 || st.getQuestItemsCount(VultusHead) == 0))
						{
							htmltext = "30615-05.htm";
							int sound = 0;
							NpcInstance isQuest = GameObjectsStorage.getByNpcId(PashikasSonOfVoltarQuestMonster);
							if(isQuest == null)
							{
								sound = 1;
								st.addSpawn(PashikasSonOfVoltarQuestMonster);
								st.startQuestTimer("PashikasSonOfVoltarQuestMonster", 200000);
							}
							isQuest = GameObjectsStorage.getByNpcId(VultusSonOfVoltarQuestMonster);
							if(isQuest == null)
							{
								sound = 1;
								st.addSpawn(VultusSonOfVoltarQuestMonster);
								st.startQuestTimer("VultusSonOfVoltarQuestMonster", 200000);
							}
							if(sound == 1)
							{
								st.playSound(SOUND_BEFORE_BATTLE);
								st.cancelQuestTimer("Wait1");
							}
							else
							{
								st.startQuestTimer("Wait1", 300000);
								htmltext = "<html><head><body>Please wait 5 minutes</body></html>";
							}
						}
						else if(st.getQuestItemsCount(PashikasHead) > 0 && st.getQuestItemsCount(VultusHead) > 0)
						{
							st.takeItems(PashikasHead, -1);
							st.takeItems(VultusHead, -1);
							st.takeItems(GloveOfVoltar, -1);
							st.giveItems(ScepterOfBreka, 1);
							htmltext = "30615-06.htm";
							if(st.getQuestItemsCount(ScepterOfBreka) > 0 && st.getQuestItemsCount(ScepterOfEnku) > 0 && st.getQuestItemsCount(ScepterOfVuku) > 0 && st.getQuestItemsCount(ScepterOfTurek) > 0 && st.getQuestItemsCount(ScepterOfTunath) > 0)
								st.setCond(5);
							else
								st.playSound(SOUND_ITEMGET);
						}
						else if(st.getQuestItemsCount(ScepterOfBreka) > 0)
							htmltext = "30615-07.htm";
						else
							htmltext = "30615-01.htm";
					break;

				case Kepra:
					if(cond == 4)
						if(st.getQuestItemsCount(ManakiaLetter2st) > 0)
							htmltext = "30616-02.htm";
						else if(st.getQuestItemsCount(GloveOfKepra) > 0 && st.getQuestItemsCount(EnkuOverlordHead) < 4)
						{
							htmltext = "30616-05.htm";
							NpcInstance isQuest = GameObjectsStorage.getByNpcId(EnkuOrcOverlordQuestMonster);
							if(isQuest != null)
							{
								st.startQuestTimer("Wait2", 300000);
								htmltext = "<html><head><body>Please wait 5 minutes</body></html>";
							}
							else
							{
								st.cancelQuestTimer("Wait2");
								st.startQuestTimer("EnkuOrcOverlordQuestMonster", 200000);
								st.addSpawn(EnkuOrcOverlordQuestMonster);
								st.addSpawn(EnkuOrcOverlordQuestMonster);
								st.addSpawn(EnkuOrcOverlordQuestMonster);
								st.addSpawn(EnkuOrcOverlordQuestMonster);
								st.playSound(SOUND_BEFORE_BATTLE);
							}
						}
						else if(st.getQuestItemsCount(EnkuOverlordHead) >= 4)
						{
							htmltext = "30616-06.htm";
							st.takeItems(EnkuOverlordHead, -1);
							st.takeItems(GloveOfKepra, -1);
							st.giveItems(ScepterOfEnku, 1);
							if(st.getQuestItemsCount(ScepterOfBreka) > 0 && st.getQuestItemsCount(ScepterOfEnku) > 0 && st.getQuestItemsCount(ScepterOfVuku) > 0 && st.getQuestItemsCount(ScepterOfTurek) > 0 && st.getQuestItemsCount(ScepterOfTunath) > 0)
								st.setCond(5);
							else
								st.playSound(SOUND_ITEMGET);
						}
						else if(st.getQuestItemsCount(ScepterOfEnku) > 0)
							htmltext = "30616-07.htm";
						else
							htmltext = "30616-01.htm";
					break;

				case Burai:
					if(cond == 4)
						if(st.getQuestItemsCount(KasmansLetter2rd) > 0)
							htmltext = "30617-02.htm";
						else if(st.getQuestItemsCount(GloveOfBurai) > 0 && st.getQuestItemsCount(MakumBugbearHead) < 2)
						{
							htmltext = "30617-05.htm";
							NpcInstance isQuest = GameObjectsStorage.getByNpcId(MakumBugbearThugQuestMonster);
							if(isQuest != null)
							{
								st.startQuestTimer("Wait3", 300000);
								htmltext = "<html><head><body>Please wait 5 minutes</body></html>";
							}
							else
							{
								st.cancelQuestTimer("Wait3");
								st.startQuestTimer("MakumBugbearThugQuestMonster", 200000);
								st.addSpawn(MakumBugbearThugQuestMonster);
								st.addSpawn(MakumBugbearThugQuestMonster);
								st.playSound(SOUND_BEFORE_BATTLE);
							}
						}
						else if(st.getQuestItemsCount(MakumBugbearHead) >= 2)
						{
							htmltext = "30617-06.htm";
							st.takeItems(MakumBugbearHead, -1);
							st.takeItems(GloveOfBurai, -1);
							st.giveItems(ScepterOfTurek, 1);
							if(st.getQuestItemsCount(ScepterOfBreka) > 0 && st.getQuestItemsCount(ScepterOfEnku) > 0 && st.getQuestItemsCount(ScepterOfVuku) > 0 && st.getQuestItemsCount(ScepterOfTurek) > 0 && st.getQuestItemsCount(ScepterOfTunath) > 0)
								st.setCond(5);
							else
								st.playSound(SOUND_ITEMGET);
						}
						else if(st.getQuestItemsCount(ScepterOfTurek) > 0)
							htmltext = "30617-07.htm";
						else
							htmltext = "30617-01.htm";
					break;

				case Harak:
					if(cond == 4)
						if(st.getQuestItemsCount(KasmansLetter3rd) > 0)
							htmltext = "30618-02.htm";
						else if(st.getQuestItemsCount(ScepterOfTunath) > 0)
							htmltext = "30618-04.htm";
						else
							htmltext = "30618-01.htm";
					break;

				case Driko:
					if(cond == 4)
						if(st.getQuestItemsCount(KasmansLetter1rd) > 0)
							htmltext = "30619-02.htm";
						else if(st.getQuestItemsCount(DrikosContract) > 0)
						{
							if(st.getQuestItemsCount(StakatoDroneHusk) >= 30)
							{
								htmltext = "30619-05.htm";
								st.takeItems(StakatoDroneHusk, -1);
								st.takeItems(DrikosContract, -1);
								st.giveItems(ScepterOfVuku, 1);
								if(st.getQuestItemsCount(ScepterOfBreka) > 0 && st.getQuestItemsCount(ScepterOfEnku) > 0 && st.getQuestItemsCount(ScepterOfVuku) > 0 && st.getQuestItemsCount(ScepterOfTurek) > 0 && st.getQuestItemsCount(ScepterOfTunath) > 0)
									st.setCond(5);
								else
									st.playSound(SOUND_ITEMGET);
							}
							else
								htmltext = "30619-04.htm";
						}
						else if(st.getQuestItemsCount(ScepterOfVuku) > 0)
							htmltext = "30619-06.htm";
						else
							htmltext = "30619-01.htm";
					break;

				case Tanapi:
					if(cond == 8)
						htmltext = "30571-01.htm";
					else if(cond == 9)
						htmltext = "30571-04.htm";
					else if(cond == 10)
					{
						st.takeItems(ScepterOfTantos, -1);
						st.takeItems(TanapisOrder, -1);
						st.giveItems(RitualBox, 1);
						htmltext = "30571-05.htm";
						st.setCond(11);
					}
					else if(cond == 11)
						htmltext = "30571-06.htm";
					break;

				case Kakai:
					if(cond == 11)
					{
						st.takeItems(RitualBox, -1);
						st.giveItems(MarkOfGlory, 1);
						if(!st.getPlayer().getVarBoolean("prof2.2"))
						{
							st.addExpAndSp(188000, 0);
							st.getPlayer().setVar("prof2.2", "1", -1);
						}
						htmltext = "30565-02.htm";
						st.finishQuest();
					}
					break;
			}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		for(int i = 0; i < DROPLIST_COND.length; i++)
			if(cond == DROPLIST_COND[i][0] && npcId == DROPLIST_COND[i][2])
				if(DROPLIST_COND[i][3] == 0 || st.getQuestItemsCount(DROPLIST_COND[i][3]) > 0)
					if(DROPLIST_COND[i][5] == 0)
						st.rollAndGive(DROPLIST_COND[i][4], DROPLIST_COND[i][7], DROPLIST_COND[i][6]);
					else if(st.rollAndGive(DROPLIST_COND[i][4], DROPLIST_COND[i][7], DROPLIST_COND[i][7], DROPLIST_COND[i][5], DROPLIST_COND[i][6]))
						if(DROPLIST_COND[i][1] != cond && DROPLIST_COND[i][1] != 0)
						{
							st.setCond(Integer.valueOf(DROPLIST_COND[i][1]));
						}
		if(cond == 1 && st.getQuestItemsCount(TyrantTalon) >= 10 && st.getQuestItemsCount(GuardianBasiliskFang) >= 10 && st.getQuestItemsCount(ManashenShard) >= 10)
		{
			st.setCond(2);
		}
		else if(cond == 4)
		{
			if(npcId == PashikasSonOfVoltarQuestMonster)
			{
				st.cancelQuestTimer("PashikasSonOfVoltarQuestMonster");
				NpcInstance isQuest = GameObjectsStorage.getByNpcId(PashikasSonOfVoltarQuestMonster);
				if(isQuest != null)
					isQuest.deleteMe();
				if(st.getQuestItemsCount(GloveOfVoltar) > 0 && st.getQuestItemsCount(PashikasHead) == 0)
					st.giveItems(PashikasHead, 1);
			}
			else if(npcId == VultusSonOfVoltarQuestMonster)
			{
				st.cancelQuestTimer("VultusSonOfVoltarQuestMonster");
				NpcInstance isQuest = GameObjectsStorage.getByNpcId(VultusSonOfVoltarQuestMonster);
				if(isQuest != null)
					isQuest.deleteMe();
				if(st.getQuestItemsCount(GloveOfVoltar) > 0 && st.getQuestItemsCount(VultusHead) == 0)
					st.giveItems(VultusHead, 1);
			}
		}
		else if(cond == 6 && st.getQuestItemsCount(TimakOrcHead) >= 20 && st.getQuestItemsCount(TamlinOrcSkull) >= 20)
		{
			st.setCond(7);
		}
		else if(cond == 9)
			if(npcId == RagnaOrcOverlord || npcId == RagnaOrcSeer)
			{
				NpcInstance isQuest = GameObjectsStorage.getByNpcId(RevenantOfTantosChief);
				if(isQuest == null)
				{
					st.startQuestTimer("RevenantOfTantosChief", 300000);
					st.addSpawn(RevenantOfTantosChief);
					st.playSound(SOUND_BEFORE_BATTLE);
				}
				else
				{
					if(!st.isRunningQuestTimer("Wait4"))
						st.startQuestTimer("Wait4", 300000);
				}
			}
			else if(npcId == RevenantOfTantosChief)
			{
				st.cancelQuestTimer("RevenantOfTantosChief");
				st.cancelQuestTimer("Wait4");
				NpcInstance isQuest = GameObjectsStorage.getByNpcId(RevenantOfTantosChief);
				if(isQuest != null)
					isQuest.deleteMe();
				st.giveItems(ScepterOfTantos, 1);
				st.setCond(10);
			}
		return null;
	}
}