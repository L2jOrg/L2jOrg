package npc.model.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.Config;
import l2s.gameserver.model.ObservePoint;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Servitor;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.entity.events.impl.SingleMatchEvent;
import l2s.gameserver.model.entity.olympiad.Olympiad;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.c2s.L2GameClientPacket;
import l2s.gameserver.network.l2.c2s.RequestBypassToServer;
import l2s.gameserver.network.l2.c2s.RequestOlympiadMatchList;
import l2s.gameserver.network.l2.components.CustomMessage;
import l2s.gameserver.network.l2.components.HtmlMessage;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExReceiveOlympiadPacket;
import l2s.gameserver.network.l2.s2c.ExReceiveOlympiadPacket.MatchList.ArenaInfo;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ChatUtils;

import events.AbstractCustomObservableEvent;

public class CustomObservationManagerInstance extends NpcInstance
{
	private static Lock _lock = new ReentrantLock();
	private static volatile int _numOfBattles = 0;
	private static final AbstractCustomObservableEvent[] BATTLES = new AbstractCustomObservableEvent[99];
	private static final List<NpcInstance> BROADCASTERS = new CopyOnWriteArrayList<NpcInstance>();

	public CustomObservationManagerInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
		setTitle("Event");
	}

	public static int registerBattle(AbstractCustomObservableEvent battle)
	{
		_lock.lock();
		try
		{
			for(int i = 0; i < BATTLES.length; i++)
				if(BATTLES[i] == null)
				{
					BATTLES[i] = battle;
					_numOfBattles++;
					return i;
				}
		}
		finally
		{
			_lock.unlock();
		}

		return -1;
	}

	public static void unRegisterBattle(AbstractCustomObservableEvent battle)
	{
		_lock.lock();
		try
		{
			for(int i = 0; i < BATTLES.length; i++)
				if(BATTLES[i] == battle)
				{
					BATTLES[i] = null;
					_numOfBattles--;
					return;
				}
		}
		finally
		{
			_lock.unlock();
		}
	}

	public static void broadcast(CustomMessage cm)
	{
		for(NpcInstance n : BROADCASTERS)
			ChatUtils.shout(n, cm);
	}

	private static void showMatchList(Player player)
	{
		List<ArenaInfo> arenaList;
		if(_numOfBattles > 0)
		{
			arenaList = new ArrayList<ArenaInfo>(_numOfBattles);
			for(AbstractCustomObservableEvent e : BATTLES)
				if(e != null)
					arenaList.add(e.getArena());
		}
		else
			arenaList = Collections.emptyList();

		player.sendPacket(new ExReceiveOlympiadPacket.MatchList(arenaList));
	}

	private static void addSpectator(int arenaId, Player player)
	{
		if(player.isInOlympiadMode() || Olympiad.isRegisteredInComp(player))
		{
			player.sendPacket(SystemMsg.YOU_MAY_NOT_OBSERVE_A_GRAND_OLYMPIAD_GAMES_MATCH_WHILE_YOU_ARE_ON_THE_WAITING_LIST);
			return;
		}

		if(player.isInCombat() || player.getPvpFlag() > 0 || player.getEvent(SingleMatchEvent.class) != null)
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_OBSERVE_WHILE_YOU_ARE_IN_COMBAT);
			return;			
		}

		if(_numOfBattles <= 0)
			return;

		for(Servitor servitor : player.getServitors())
			servitor.unSummon(false);

		final ObservePoint op = player.getObservePoint();
		if(op != null)
			for(AbstractCustomObservableEvent e : BATTLES)
				if(e != null && e.getReflection() == op.getReflection())
				{
					e.removeObserver(player);
					break;
				}

		for(AbstractCustomObservableEvent e : BATTLES)
			if(e != null && e.getArenaId() == arenaId)
			{
				final Reflection r = e.getReflection();
				if(r != null && !r.isCollapseStarted() && e.getObserverCoords() != null)
				{
					//player.enterArenaObserverMode(e.getObserverCoords(), null, r);
					e.addObserver(player);
				}
				return;
			}
	}

	@Override
	protected void onSpawn()
	{
		super.onSpawn();
		BROADCASTERS.add(this);
	}

	@Override
	protected void onDecay()
	{
		super.onDecay();
		BROADCASTERS.remove(this);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		// до всех проверок
		if(command.startsWith("_olympiad?")) // _olympiad?command=move_op_field&field=1
		{
			String[] ar = command.split("&");
			if(ar.length < 2)
				return;

			if(ar[0].equalsIgnoreCase("_olympiad?command=move_op_field"))
			{
				if(!Config.ENABLE_OLYMPIAD_SPECTATING)
					return;

				String[] command2 = ar[1].split("=");
				if(command2.length < 2)
					return;

				addSpectator(Integer.parseInt(command2[1]) - 1, player);
			}
			return;
		}

		if(command.startsWith("MatchList"))
			showMatchList(player);
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... replace)
	{
		String fileName;
		if(val > 0)
			fileName = getTemplate().getHtmRoot() + "manager-" + val + ".htm";
		else
			fileName = getTemplate().getHtmRoot() + "manager.htm";

		player.sendPacket(new HtmlMessage(this, fileName).setPlayVoice(firstTalk));
	}

	@Override
	public boolean canPassPacket(Player player, Class<? extends L2GameClientPacket> packet, Object... arg)
	{
		if(packet == RequestOlympiadMatchList.class)
		{
			showMatchList(player);
			return true;
		}

		return packet == RequestBypassToServer.class && arg.length == 1 && arg[0].equals("_olympiad?command=move_op_field");
	}
}