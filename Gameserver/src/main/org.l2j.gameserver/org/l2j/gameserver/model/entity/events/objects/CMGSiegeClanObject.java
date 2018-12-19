package org.l2j.gameserver.model.entity.events.objects;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import org.l2j.gameserver.model.GameObjectsStorage;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.entity.events.impl.SiegeEvent;
import org.l2j.gameserver.model.pledge.Clan;

/**
 * @author VISTALL
 * @date 17:22/14.05.2011
 */
public class CMGSiegeClanObject extends SiegeClanObject
{
	private static final long serialVersionUID = 1L;

	private TIntSet _players = new TIntHashSet();
	private long _param;

	public CMGSiegeClanObject(String type, Clan clan, long param, long date)
	{
		super(type, clan, param, date);
		_param = param;
	}

	public CMGSiegeClanObject(String type, Clan clan, long param)
	{
		super(type, clan, param);
		_param = param;
	}

	public void addPlayer(int objectId)
	{
		_players.add(objectId);
	}

	@Override
	public long getParam()
	{
		return _param;
	}

	@Override
	public boolean isParticle(Player player)
	{
		return _players.contains(player.getObjectId());
	}

	@Override
	public void setEvent(boolean start, SiegeEvent<?, ?> event)
	{
		for(int i : _players.toArray())
		{
			Player player = GameObjectsStorage.getPlayer(i);
			if(player != null)
			{
				if(start)
					player.addEvent(event);
				else
					player.removeEvent(event);
				player.broadcastCharInfo();
			}
		}
	}

	public void setParam(long param)
	{
		_param = param;
	}

	public TIntSet getPlayers()
	{
		return _players;
	}
}