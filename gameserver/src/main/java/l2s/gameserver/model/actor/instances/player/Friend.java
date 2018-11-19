package l2s.gameserver.model.actor.instances.player;

import java.util.Calendar;

import l2s.commons.lang.reference.HardReference;
import l2s.commons.lang.reference.HardReferences;
import l2s.gameserver.model.Player;

/**
 * @author VISTALL
 * @date 22:16/22.03.2011
 */
public class Friend
{
	private final int _objectId;
	private String _name;
	private int _classId;
	private int _level;
	private String _memo;

	private int _clanId;
	private String _clanName;

	private int _allyId;
	private String _allyName;

	private int _creationDay;
	private int _creationMonth;
	private long _lastAccess;

	private HardReference<Player> _playerRef = HardReferences.emptyRef();

	public Friend(int objectId, String name, int classId, int level, String memo, int clanId, String clanName, int allyId, String allyName, int createTime, int lastAccess)
	{
		_objectId = objectId;
		_name = name;
		_classId = classId;
		_level = level;
		_memo = memo;
		_clanId = clanId;
		_clanName = clanName;
		_allyId = allyId;
		_allyName = allyName;
		setCreateTime(createTime * 1000L);
		_lastAccess = lastAccess * 1000L;
	}

	public Friend(Player player)
	{
		_objectId = player.getObjectId();
		_memo = "";
		update(player, true);
	}

	public void update(Player player, boolean set)
	{
		_level = player.getLevel();
		_name = player.getName();
		_classId = player.getActiveClassId();
		_playerRef = set ? player.getRef() : HardReferences.<Player> emptyRef();
		_clanId = player.getClanId();
		_allyId = player.getAllyId();
		if(player.getClan() != null)
		{
			_clanName = player.getClan().getName();
			if(player.getClan().getAlliance() != null)
				_allyName = player.getClan().getAlliance().getAllyName();
			else
				_allyName = "";
		}
		else
		{
			_clanName = "";
			_allyName = "";
		}
		setCreateTime(player.getCreateTime());
		_lastAccess = System.currentTimeMillis();
	}

	public String getName()
	{
		Player player = getPlayer();
		return player == null ? _name : player.getName();
	}

	public int getObjectId()
	{
		return _objectId;
	}

	public int getClassId()
	{
		Player player = getPlayer();
		return player == null ? _classId : player.getActiveClassId();
	}

	public int getLevel()
	{
		Player player = getPlayer();
		return player == null ? _level : player.getLevel();
	}

	public boolean isOnline()
	{
		Player player = _playerRef.get();
		return player != null && !player.isInOfflineMode();
	}

	public Player getPlayer()
	{
		Player player = _playerRef.get();
		return player != null && !player.isInOfflineMode() ? player : null;
	}

	public int getClanId()
	{
		Player player = getPlayer();
		return player == null ? _clanId : player.getClanId();
	}

	public String getClanName()
	{
		Player player = getPlayer();
		if(player == null)
			return _clanName;

		if(player.getClan() == null)
			return "";

		return player.getClan().getName();
	}

	public int getAllyId()
	{
		Player player = getPlayer();
		return player == null ? _allyId : player.getAllyId();
	}

	public String getAllyName()
	{
		Player player = getPlayer();
		if(player == null)
			return _allyName;

		if(player.getClan() == null || player.getClan().getAlliance() == null)
			return "";

		return player.getClan().getAlliance().getAllyName();
	}

	public String getMemo()
	{
		return _memo;
	}

	public void setMemo(String val)
	{
		_memo = val;
	}

	public int getCreationDay()
	{
		return _creationDay;
	}

	public int getCreationMonth()
	{
		return _creationMonth;
	}

	public void setCreateTime(long time)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		_creationDay = calendar.get(Calendar.DAY_OF_MONTH);
		_creationMonth = calendar.get(Calendar.MONTH);
	}

	public int getLastAccessDelay()
	{
		return isOnline() ? -1 : (int) ((System.currentTimeMillis() - _lastAccess) / 1000);
	}
}