package org.l2j.gameserver.model.entity.events.objects;

import java.util.ArrayList;
import java.util.List;

import org.l2j.gameserver.data.dao.SiegePlayerDAO;
import org.l2j.gameserver.model.pledge.Clan;
import org.l2j.gameserver.model.GameObjectsStorage;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.entity.events.impl.SiegeEvent;
import org.l2j.gameserver.model.entity.residence.Residence;

/**
 * @author VISTALL
 * @date 14:23/31.03.2011
 */
public class CTBSiegeClanObject extends SiegeClanObject
{
	private static final long serialVersionUID = 1L;

	private List<Integer> _players = new ArrayList<Integer>();
	private long _npcId;

	public CTBSiegeClanObject(String type, Clan clan, long param, long date)
	{
		super(type, clan, param, date);
		_npcId = param;
	}

	public CTBSiegeClanObject(String type, Clan clan, long param)
	{
		this(type, clan, param, System.currentTimeMillis());
	}

	public void select(Residence r)
	{
		_players.addAll(SiegePlayerDAO.getInstance().select(r, getObjectId()));
	}

	public List<Integer> getPlayers()
	{
		return _players;
	}

	@Override
	public void setEvent(boolean start, SiegeEvent<?, ?> event)
	{
		for(int i : getPlayers())
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

	@Override
	public boolean isParticle(Player player)
	{
		return _players.contains(player.getObjectId());
	}

	@Override
	public long getParam()
	{
		return _npcId;
	}

	public void setParam(int npcId)
	{
		_npcId = npcId;
	}
}