package l2s.gameserver;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.Future;

import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.components.ChatType;
import l2s.gameserver.network.l2.components.CustomMessage;
import l2s.gameserver.network.l2.components.IBroadcastPacket;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.SayPacket2;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.utils.ArabicConv;
import l2s.gameserver.utils.ChatUtils;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Announcements
{
	public class Announce extends RunnableImpl
	{
		private Future<?> _task;
		private final int _time;
		private final String _announce;

		public Announce(int t, String announce)
		{
			_time = t;
			_announce = announce;
		}

		@Override
		public void runImpl() throws Exception
		{
            Announcements.announceToAll(Config.HTM_SHAPE_ARABIC ? ArabicConv.shapeArabic(_announce) : _announce);
		}

		public void showAnnounce(Player player)
		{
            String text = Config.HTM_SHAPE_ARABIC ? ArabicConv.shapeArabic(_announce) : _announce;
			SayPacket2 cs = new SayPacket2(0, ChatType.ANNOUNCEMENT, player.getName(), text);
			player.sendPacket(cs);
		}

		public void start()
		{
			if(_time > 0)
				_task = ThreadPoolManager.getInstance().scheduleAtFixedRate(this, _time * 1000L, _time * 1000L);
		}

		public void stop()
		{
			if(_task != null)
			{
				_task.cancel(false);
				_task = null;
			}
		}

		public int getTime()
		{
			return _time;
		}

		public String getAnnounce()
		{
			return _announce;
		}
	}

	private static final Logger _log = LoggerFactory.getLogger(Announcements.class);

	private static final Announcements _instance = new Announcements();

	public static final Announcements getInstance()
	{
		return _instance;
	}

	private List<Announce> _announcements = new ArrayList<Announce>();

	private Announcements()
	{
		loadAnnouncements();
	}

	public List<Announce> getAnnouncements()
	{
		return _announcements;
	}

	public void loadAnnouncements()
	{
		_announcements.clear();

		try
		{
			List<String> lines = Arrays.asList(FileUtils.readFileToString(new File("config/announcements.txt"), "UTF-8").split("\n"));
			for(String line : lines)
			{
				if(line == null || line.isEmpty())
					continue;

				StringTokenizer token = new StringTokenizer(line, "\t");
				if(token.countTokens() > 1)
					addAnnouncement(Integer.parseInt(token.nextToken()), token.nextToken(), false);
				else
					addAnnouncement(0, line, false);
			}
		}
		catch(Exception e)
		{
			_log.error("Error while loading config/announcements.txt!");
		}
	}

	public void showAnnouncements(Player activeChar)
	{
		for(Announce announce : _announcements)
			announce.showAnnounce(activeChar);
	}

	public void addAnnouncement(int val, String text, boolean save)
	{
		Announce announce = new Announce(val, text);
		announce.start();

		_announcements.add(announce);
		if(save)
			saveToDisk();
	}

	public void delAnnouncement(int line)
	{
		Announce announce = _announcements.remove(line);
		if(announce != null)
			announce.stop();

		saveToDisk();
	}

	private void saveToDisk()
	{
		try
		{
			File f = new File("config/announcements.txt");
			FileWriter writer = new FileWriter(f, false);
			for(Announce announce : _announcements)
				writer.write(announce.getTime() + "\t" + announce.getAnnounce() + "\n");
			writer.close();
		}
		catch(Exception e)
		{
			_log.error("Error while saving config/announcements.txt!", e);
		}
	}

	public static void announceToAll(String text)
	{
		announceToAll(text, ChatType.ANNOUNCEMENT);
	}

    public static void announceToAll(NpcString npcString, String... params)
    {
        announceToAll(ChatType.ANNOUNCEMENT, npcString, params);
    }

    public static void shout(Player activeChar, String text, ChatType type)
    {
		SayPacket2 cs = new SayPacket2(activeChar.getObjectId(), type, activeChar.getName(), text);
        ChatUtils.shout(activeChar, cs);
		activeChar.sendPacket(cs);
	}

	public static void announceToAll(String text, ChatType type)
	{
		SayPacket2 cs = new SayPacket2(0, type, "", text);
		for(Player player : GameObjectsStorage.getPlayers())
			player.sendPacket(cs);
	}

    public static void announceToAll(ChatType type, NpcString npcString, String... params)
    {
        SayPacket2 cs = new SayPacket2(0, type, "", npcString, params);
        for(Player player : GameObjectsStorage.getPlayers())
            player.sendPacket(cs);
    }

    public static void announceToAllFromStringHolder(String add, Object... arg)
    {
        for(Player player : GameObjectsStorage.getPlayers())
            announceToPlayerFromStringHolder(player, add, arg);
    }

    public static void announceToPlayerFromStringHolder(Player player, String add, Object... arg)
    {
        CustomMessage message = new CustomMessage(add);
        for(Object a : arg)
        {
            if(a instanceof CustomMessage)
                message.addCustomMessage((CustomMessage) a);
            else
                message.addString(String.valueOf(a));
        }
        player.sendPacket(new SayPacket2(0, ChatType.ANNOUNCEMENT, "", message.toString(player)));
    }

    public static void criticalAnnounceToAllFromStringHolder(String add, Object... arg)
    {
        for(Player player : GameObjectsStorage.getPlayers())
            criticalAnnounceToPlayerFromStringHolder(player, add, arg);
    }

    public static void criticalAnnounceToPlayerFromStringHolder(Player player, String add, Object... arg)
    {
        CustomMessage message = new CustomMessage(add);

        for(Object a : arg)
            message.addString(String.valueOf(a));
        player.sendPacket(new SayPacket2(0, ChatType.CRITICAL_ANNOUNCE, "", message.toString(player)));
    }

    public static void announceToAll(SystemMessagePacket sm)
    {
        for(Player player : GameObjectsStorage.getPlayers())
            player.sendPacket(sm);
    }

    public static void announceToAll(IBroadcastPacket sm)
    {
        for(Player player : GameObjectsStorage.getPlayers())
            player.sendPacket(sm);
    }
}