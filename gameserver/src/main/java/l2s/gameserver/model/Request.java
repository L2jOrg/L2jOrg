package l2s.gameserver.model;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import l2s.commons.collections.MultiValueSet;
import l2s.commons.lang.reference.HardReference;
import l2s.commons.lang.reference.HardReferences;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.network.l2.components.IBroadcastPacket;
import l2s.gameserver.network.l2.components.SystemMsg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Класс обмена между игроками запросами и ответами.
 */
public class Request extends MultiValueSet<String>
{
	private static final long serialVersionUID = 1L;

	private static final Logger _log = LoggerFactory.getLogger(Request.class);

	public static enum L2RequestType
	{
		CUSTOM,
		PARTY,
		PARTY_ROOM,
		CLAN,
		CLAN_WAR_START,
		CLAN_WAR_STOP,
		CLAN_WAR_SURRENDER,
		ALLY,
		TRADE,
		TRADE_REQUEST,
		FRIEND,
		CHANNEL,
		DUEL,
		COUPLE_ACTION,
		MENTEE,
		PARTY_MEMBER_SUBSTITUTE
	}

	private final static AtomicInteger _nextId = new AtomicInteger();

	private final int _id;
	private L2RequestType _type;
	private HardReference<Player> _requestor;
	private HardReference<Player> _reciever;
	private boolean _isRequestorConfirmed;
	private boolean _isRecieverConfirmed;
	private boolean _isCancelled;
	private boolean _isDone;

	private long _timeout;
	private Future<?> _timeoutTask;

	/**
	 * Создает запрос
	 */
	public Request(L2RequestType type, Player requestor, Player reciever)
	{
		_id = _nextId.incrementAndGet();
		_requestor = requestor.getRef();
		_reciever = reciever != null ? reciever.getRef() : HardReferences.<Player> emptyRef();
		_type = type;
		requestor.setRequest(this);
		if(reciever != null)
			reciever.setRequest(this);
	}

	public Request setTimeout(long timeout)
	{
		_timeout = timeout > 0 ? System.currentTimeMillis() + timeout : 0;
		_timeoutTask = ThreadPoolManager.getInstance().schedule(() ->
		{
			timeout();
		}, timeout);
		return this;
	}

	public int getId()
	{
		return _id;
	}

	private void cancel0(IBroadcastPacket... packets)
	{
		if(_timeoutTask != null)
			_timeoutTask.cancel(false);
		_timeoutTask = null;
		Player player = getRequestor();
		if(player != null && player.getRequest() == this)
		{
			player.setRequest(null);
			player.sendPacket(packets);
		}
		player = getReciever();
		if(player != null && player.getRequest() == this)
		{
			player.setRequest(null);
			player.sendPacket(packets);
		}
	}

	/**
	 * Отменяет запрос и очищает соответствующее поле у участников.
	 */
	public void cancel(IBroadcastPacket... packets)
	{
		_isCancelled = true;
		cancel0(packets);
	}

	/**
	 * Заканчивает запрос и очищает соответствующее поле у участников.
	 */
	public void done(IBroadcastPacket... packets)
	{
		_isDone = true;
		cancel0(packets);
	}

	/**
	 * Действие при таймауте.
	 */
	public void timeout(IBroadcastPacket... packets)
	{
		Player player = getReciever();
		if(player != null)
			if(player.getRequest() == this)
				player.sendPacket(SystemMsg.TIME_EXPIRED);
		cancel(packets);
	}

	public Player getOtherPlayer(Player player)
	{
		if(player == getRequestor())
			return getReciever();
		if(player == getReciever())
			return getRequestor();
		return null;
	}

	public Player getRequestor()
	{
		return _requestor.get();
	}

	public Player getReciever()
	{
		return _reciever.get();
	}

	/**
	 * Проверяет не просрочен ли запрос.
	 */
	public boolean isInProgress()
	{
		if(_isCancelled)
			return false;
		if(_isDone)
			return false;
		if(_timeout == 0)
			return true;
		if(_timeout > System.currentTimeMillis())
			return true;
		return false;
	}

	/**
	 * Проверяет тип запроса.
	 */
	public boolean isTypeOf(L2RequestType type)
	{
		return _type == type;
	}

	/**
	 * Помечает участника как согласившегося.
	 */
	public void confirm(Player player)
	{
		if(player == getRequestor())
			_isRequestorConfirmed = true;
		else if(player == getReciever())
			_isRecieverConfirmed = true;
	}

	/**
	 * Проверяет согласился ли игрок с запросом.
	 */
	public boolean isConfirmed(Player player)
	{
		if(player == getRequestor())
			return _isRequestorConfirmed;
		else if(player == getReciever())
			return _isRecieverConfirmed;
		return false; // WTF???
	}
}