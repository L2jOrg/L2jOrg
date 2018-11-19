package l2s.gameserver.model.instances;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import l2s.commons.collections.MultiValueSet;
import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.Config;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.instancemanager.ServerVariables;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.MonsterRace;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.HtmlMessage;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.DeleteObjectPacket;
import l2s.gameserver.network.l2.s2c.L2GameServerPacket;
import l2s.gameserver.network.l2.s2c.MonRaceInfoPacket;
import l2s.gameserver.network.l2.s2c.PlaySoundPacket;
import l2s.gameserver.network.l2.s2c.SystemMessage;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.Location;

public class RaceManagerInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public static final int LANES = 8;
	public static final int WINDOW_START = 0;

	@SuppressWarnings("unused")
	private static List<Race> history;
	private static Set<RaceManagerInstance> managers;
	private static int _raceNumber = 1;

	//Time Constants
	private final static long SECOND = 1000;
	private final static long MINUTE = 60 * SECOND;

	private static int minutes = 5;

	//States
	private static final int ACCEPTING_BETS = 0;
	private static final int WAITING = 1;
	private static final int STARTING_RACE = 2;
	private static final int RACE_END = 3;
	private static int state = RACE_END;

	protected static final int[][] codes = { { -1, 0 }, { 0, 15322 }, { 13765, -1 } };
	private static boolean notInitialized = true;
	protected static MonRaceInfoPacket packet;
	protected static int cost[] = { 100, 500, 1000, 5000, 10000, 20000, 50000, 100000 };

	public RaceManagerInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
		if(!Config.ALLOW_MONSTER_RACE)
			return;

		if(notInitialized)
		{
			notInitialized = false;

			_raceNumber = ServerVariables.getInt("monster_race", 1);
			history = new ArrayList<Race>();
			managers = new CopyOnWriteArraySet<RaceManagerInstance>();

			ThreadPoolManager s = ThreadPoolManager.getInstance();
			s.scheduleAtFixedRate(new Announcement(SystemMessage.TICKETS_ARE_NOW_AVAILABLE_FOR_THE_S1TH_MONSTER_RACE), 0, 10 * MINUTE);
			s.scheduleAtFixedRate(new Announcement(SystemMessage.WE_ARE_NOW_SELLING_TICKETS_FOR_THE_S1TH_MONSTER_RACE), 30 * SECOND, 10 * MINUTE);
			s.scheduleAtFixedRate(new Announcement(SystemMessage.TICKETS_ARE_NOW_AVAILABLE_FOR_THE_S1TH_MONSTER_RACE), MINUTE, 10 * MINUTE);
			s.scheduleAtFixedRate(new Announcement(SystemMessage.WE_ARE_NOW_SELLING_TICKETS_FOR_THE_S1TH_MONSTER_RACE), MINUTE + 30 * SECOND, 10 * MINUTE);
			s.scheduleAtFixedRate(new Announcement(SystemMessage.TICKET_SALES_FOR_THE_MONSTER_RACE_WILL_CEASE_IN_S1_MINUTE_S), 2 * MINUTE, 10 * MINUTE);
			s.scheduleAtFixedRate(new Announcement(SystemMessage.TICKET_SALES_FOR_THE_MONSTER_RACE_WILL_CEASE_IN_S1_MINUTE_S), 3 * MINUTE, 10 * MINUTE);
			s.scheduleAtFixedRate(new Announcement(SystemMessage.TICKET_SALES_FOR_THE_MONSTER_RACE_WILL_CEASE_IN_S1_MINUTE_S), 4 * MINUTE, 10 * MINUTE);
			s.scheduleAtFixedRate(new Announcement(SystemMessage.TICKET_SALES_FOR_THE_MONSTER_RACE_WILL_CEASE_IN_S1_MINUTE_S), 5 * MINUTE, 10 * MINUTE);
			s.scheduleAtFixedRate(new Announcement(SystemMessage.TICKETS_SALES_ARE_CLOSED_FOR_THE_S1TH_MONSTER_RACE_ODDS_ARE_POSTED), 6 * MINUTE, 10 * MINUTE);
			s.scheduleAtFixedRate(new Announcement(SystemMessage.TICKETS_SALES_ARE_CLOSED_FOR_THE_S1TH_MONSTER_RACE_ODDS_ARE_POSTED), 7 * MINUTE, 10 * MINUTE);
			s.scheduleAtFixedRate(new Announcement(SystemMessage.THE_S2TH_MONSTER_RACE_WILL_BEGIN_IN_S1_MINUTES), 7 * MINUTE, 10 * MINUTE);
			s.scheduleAtFixedRate(new Announcement(SystemMessage.THE_S2TH_MONSTER_RACE_WILL_BEGIN_IN_S1_MINUTES), 8 * MINUTE, 10 * MINUTE);
			s.scheduleAtFixedRate(new Announcement(SystemMessage.THE_S1TH_MONSTER_RACE_WILL_BEGIN_IN_30_SECONDS), 8 * MINUTE + 30 * SECOND, 10 * MINUTE);
			s.scheduleAtFixedRate(new Announcement(SystemMessage.THE_S1TH_MONSTER_RACE_IS_ABOUT_TO_BEGIN_COUNTDOWN_IN_FIVE_SECONDS), 8 * MINUTE + 50 * SECOND, 10 * MINUTE);
			s.scheduleAtFixedRate(new Announcement(SystemMessage.THE_RACE_WILL_BEGIN_IN_S1_SECONDS), 8 * MINUTE + 55 * SECOND, 10 * MINUTE);
			s.scheduleAtFixedRate(new Announcement(SystemMessage.THE_RACE_WILL_BEGIN_IN_S1_SECONDS), 8 * MINUTE + 56 * SECOND, 10 * MINUTE);
			s.scheduleAtFixedRate(new Announcement(SystemMessage.THE_RACE_WILL_BEGIN_IN_S1_SECONDS), 8 * MINUTE + 57 * SECOND, 10 * MINUTE);
			s.scheduleAtFixedRate(new Announcement(SystemMessage.THE_RACE_WILL_BEGIN_IN_S1_SECONDS), 8 * MINUTE + 58 * SECOND, 10 * MINUTE);
			s.scheduleAtFixedRate(new Announcement(SystemMessage.THE_RACE_WILL_BEGIN_IN_S1_SECONDS), 8 * MINUTE + 59 * SECOND, 10 * MINUTE);
			s.scheduleAtFixedRate(new Announcement(SystemMessage.THEYRE_OFF), 9 * MINUTE, 10 * MINUTE);
		}
		managers.add(this);
	}

	public void removeKnownPlayer(Player player)
	{
		for(int i = 0; i < 8; i++)
			player.sendPacket(new DeleteObjectPacket(MonsterRace.getInstance().getMonsters()[i]));
	}

	class Announcement extends RunnableImpl
	{
		private int type;

		public Announcement(int type)
		{
			this.type = type;
		}

		@Override
		public void runImpl() throws Exception
		{
			makeAnnouncement(type);
		}
	}

	public void makeAnnouncement(int type)
	{
		SystemMessage sm = new SystemMessage(type);
		switch(type)
		{
			case SystemMessage.TICKETS_ARE_NOW_AVAILABLE_FOR_THE_S1TH_MONSTER_RACE:
			case SystemMessage.WE_ARE_NOW_SELLING_TICKETS_FOR_THE_S1TH_MONSTER_RACE:
				if(state != ACCEPTING_BETS)
				{
					state = ACCEPTING_BETS;
					startRace();
				}
				sm.addNumber(_raceNumber);
				break;
			case SystemMessage.TICKET_SALES_FOR_THE_MONSTER_RACE_WILL_CEASE_IN_S1_MINUTE_S:
			case SystemMessage.THE_S2TH_MONSTER_RACE_WILL_BEGIN_IN_S1_MINUTES:
			case SystemMessage.THE_RACE_WILL_BEGIN_IN_S1_SECONDS:
				sm.addNumber(minutes);
				sm.addNumber(_raceNumber);
				minutes--;
				break;
			case SystemMessage.TICKETS_SALES_ARE_CLOSED_FOR_THE_S1TH_MONSTER_RACE_ODDS_ARE_POSTED:
				//_log.info.println("Sales closed");
				sm.addNumber(_raceNumber);
				state = WAITING;
				minutes = 2;
				break;
			case SystemMessage.THE_S1TH_MONSTER_RACE_IS_ABOUT_TO_BEGIN_COUNTDOWN_IN_FIVE_SECONDS:
			case SystemMessage.MONSTER_RACE_S1_IS_FINISHED:
				sm.addNumber(_raceNumber);
				minutes = 5;
				break;
			case SystemMessage.FIRST_PRIZE_GOES_TO_THE_PLAYER_IN_LANE_S1_SECOND_PRIZE_GOES_TO_THE_PLAYER_IN_LANE_S2:
				//_log.info.println("Placing");
				state = RACE_END;
				sm.addNumber(MonsterRace.getInstance().getFirstPlace());
				sm.addNumber(MonsterRace.getInstance().getSecondPlace());
				break;
		}

		broadcast(sm);

		if(type == SystemMessage.THEYRE_OFF)
		{
			state = STARTING_RACE;
			startRace();
			minutes = 5;
		}
	}

	protected void broadcast(L2GameServerPacket pkt)
	{
		for(RaceManagerInstance manager : managers)
			if(!manager.isDead())
				manager.broadcastPacketToOthers(pkt);
	}

	public void sendMonsterInfo()
	{
		broadcast(packet);
	}

	private void startRace()
	{
		MonsterRace race = MonsterRace.getInstance();
		if(state == STARTING_RACE)
		{
			//state++;
			PlaySoundPacket SRace = new PlaySoundPacket("S_Race");
			broadcast(SRace);
			//TODO исправить 121209259 - обжект айди, ток неизвестно какого обьекта (VISTALL)
			PlaySoundPacket SRace2 = new PlaySoundPacket(PlaySoundPacket.Type.SOUND, "ItemSound2.race_start", 1, 121209259, new Location(12125, 182487, -3559));
			broadcast(SRace2);
			packet = new MonRaceInfoPacket(codes[1][0], codes[1][1], race.getMonsters(), race.getSpeeds());
			sendMonsterInfo();

			ThreadPoolManager.getInstance().schedule(new RunRace(), 5000);
		}
		else
		{
			//state++;
			race.newRace();
			race.newSpeeds();
			packet = new MonRaceInfoPacket(codes[0][0], codes[0][1], race.getMonsters(), race.getSpeeds());
			sendMonsterInfo();
		}

	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(command.startsWith("BuyTicket") && state != ACCEPTING_BETS)
		{
			player.sendPacket(SystemMsg.MONSTER_RACE_TICKETS_ARE_NO_LONGER_AVAILABLE);
			command = "Chat 0";
		}
		if(command.startsWith("ShowOdds") && state == ACCEPTING_BETS)
		{
			player.sendPacket(SystemMsg.MONSTER_RACE_PAYOUT_INFORMATION_IS_NOT_AVAILABLE_WHILE_TICKETS_ARE_BEING_SOLD);
			command = "Chat 0";
		}

		if(command.startsWith("BuyTicket"))
		{
			int val = Integer.parseInt(command.substring(10));
			if(val == 0)
			{
				player.setRace(0, 0);
				player.setRace(1, 0);
			}
			if(val == 10 && player.getRace(0) == 0 || val == 20 && player.getRace(0) == 0 && player.getRace(1) == 0)
				val = 0;
			showBuyTicket(player, val);
		}
		else if(command.equals("ShowOdds"))
			showOdds(player);
		else if(command.equals("ShowInfo"))
			showMonsterInfo(player);
		else if(command.equals("calculateWin"))
		{
			//displayCalculateWinnings(player);
		}
		else if(command.equals("viewHistory"))
		{
			//displayHistory(player);
		}
		else
			super.onBypassFeedback(player, command);
	}

	public void showOdds(Player player)
	{
		if(state == ACCEPTING_BETS)
			return;

		String filename, search;
		HtmlMessage html = new HtmlMessage(this);
		filename = getHtmlFilename(5, player);
		html.setFile(filename);
		for(int i = 0; i < 8; i++)
		{
			try
			{
				int n = i + 1;
				search = "<?Mob" + n + "?>";
				NpcInstance npc = MonsterRace.getInstance().getMonsters()[i];
				html.replace(search, npc == null ? "" : npc.getTemplate().name);
			}
			catch(Exception e)
			{
			}	
		}
		html.replace("<?1race?>", String.valueOf(_raceNumber));
		player.sendPacket(html);
		player.sendActionFailed();
	}

	public void showMonsterInfo(Player player)
	{
		String filename, search;
		HtmlMessage html = new HtmlMessage(this);
		filename = getHtmlFilename(6, player);
		html.setFile(filename);
		for(int i = 0; i < 8; i++)
		{
			int n = i + 1;
			search = "<?Mob" + n + "?>";
			NpcInstance npc = MonsterRace.getInstance().getMonsters()[i];
			html.replace(search, npc == null ? "" : npc.getTemplate().name);
		}
		player.sendPacket(html);
		player.sendActionFailed();
	}

	public void showBuyTicket(Player player, int val)
	{
		if(state != ACCEPTING_BETS)
			return;
		String filename, search, replace;
		HtmlMessage html = new HtmlMessage(this);
		if(val < 10)
		{
			filename = getHtmlFilename(2, player);
			html.setFile(filename);
			for(int i = 0; i < 8; i++)
			{
				int n = i + 1;
				search = "<?Mob" + n + "?>";
				NpcInstance npc = MonsterRace.getInstance().getMonsters()[i];
				html.replace(search, npc == null ? "" : npc.getTemplate().name);
			}
			search = "<?No1?>";
			if(val == 0)
				html.replace(search, "");
			else
			{
				html.replace(search, "" + val);
				player.setRace(0, val);
			}
		}
		else if(val < 20)
		{
			if(player.getRace(0) == 0)
				return;
			filename = getHtmlFilename(3, player);
			html.setFile(filename);
			html.replace("<?0place?>", "" + player.getRace(0));
			search = "<?Mob1?>";
			NpcInstance npc = MonsterRace.getInstance().getMonsters()[(player.getRace(0) - 1)];
			replace = npc == null ? "" : npc.getTemplate().name;
			html.replace(search, replace);
			search = "<?0adena?>";
			if(val == 10)
				html.replace(search, "");
			else
			{
				html.replace(search, "" + cost[val - 11]);
				player.setRace(1, val - 10);
			}
		}
		else if(val == 20)
		{
			if(player.getRace(0) == 0 || player.getRace(1) == 0)
				return;
			filename = getHtmlFilename(4, player);
			html.setFile(filename);
			html.replace("<?0place?>", "" + player.getRace(0));
			search = "<?Mob1?>";
			NpcInstance npc = MonsterRace.getInstance().getMonsters()[(player.getRace(0) - 1)];
			replace = npc == null ? "" : npc.getTemplate().name;
			html.replace(search, replace);
			search = "<?0adena?>";
			int price = cost[player.getRace(1) - 1];
			html.replace(search, "" + price);
			search = "<?0tax?>";
			int tax = 0;
			html.replace(search, "" + tax);
			search = "<?0total?>";
			int total = price + tax;
			html.replace(search, "" + total);
		}
		else
		{
			if(player.getRace(0) == 0 || player.getRace(1) == 0)
				return;
			if(player.getAdena() < cost[player.getRace(1) - 1])
			{
				player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
				return;
			}
			int ticket = player.getRace(0);
			int priceId = player.getRace(1);
			player.setRace(0, 0);
			player.setRace(1, 0);
			player.reduceAdena(cost[priceId - 1], true);
			SystemMessage sm = new SystemMessage(SystemMessage.ACQUIRED__S1_S2);
			sm.addNumber(_raceNumber);
			sm.addItemName(4443);
			player.sendPacket(sm);
			ItemInstance item = ItemFunctions.createItem(4443);
			item.setEnchantLevel(_raceNumber);
			item.setCustomType1(ticket);
			item.setCustomType2(cost[priceId - 1] / 100);
			player.getInventory().addItem(item);
			return;
		}
		html.replace("<?1race?>", String.valueOf(_raceNumber));
		player.sendPacket(html);
		player.sendActionFailed();
	}

	public class Race
	{
		private Info[] info;

		public Race(Info[] info)
		{
			this.info = info;
		}

		public Info getLaneInfo(int lane)
		{
			return info[lane];
		}

		public class Info
		{
			private int id;
			private int place;
			private int odds;
			private int payout;

			public Info(int id, int place, int odds, int payout)
			{
				this.id = id;
				this.place = place;
				this.odds = odds;
				this.payout = payout;
			}

			public int getId()
			{
				return id;
			}

			public int getOdds()
			{
				return odds;
			}

			public int getPayout()
			{
				return payout;
			}

			public int getPlace()
			{
				return place;
			}
		}
	}

	class RunRace extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			packet = new MonRaceInfoPacket(codes[2][0], codes[2][1], MonsterRace.getInstance().getMonsters(), MonsterRace.getInstance().getSpeeds());
			sendMonsterInfo();
			ThreadPoolManager.getInstance().schedule(new RunEnd(), 30000);
		}
	}

	class RunEnd extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			makeAnnouncement(SystemMessage.FIRST_PRIZE_GOES_TO_THE_PLAYER_IN_LANE_S1_SECOND_PRIZE_GOES_TO_THE_PLAYER_IN_LANE_S2);
			makeAnnouncement(SystemMessage.MONSTER_RACE_S1_IS_FINISHED);
			_raceNumber++;
			ServerVariables.set("monster_race", _raceNumber);

			for(int i = 0; i < 8; i++)
				broadcast(new DeleteObjectPacket(MonsterRace.getInstance().getMonsters()[i]));
		}
	}

	public MonRaceInfoPacket getPacket()
	{
		return packet;
	}
}