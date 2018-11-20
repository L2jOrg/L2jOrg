package org.l2j.gameserver.ai;

import java.util.ArrayList;
import java.util.List;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.World;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.model.instances.RaceManagerInstance;
import org.l2j.gameserver.network.l2.s2c.MonRaceInfoPacket;

public class RaceManager extends DefaultAI
{
	private boolean thinking = false; // to prevent recursive thinking
	private List<Player> _knownPlayers = new ArrayList<Player>();

	public RaceManager(NpcInstance actor)
	{
		super(actor);
		_attackAITaskDelay = 5000;
	}

	@Override
	public void runImpl()
	{
		onEvtThink();
	}

	@Override
	protected void onEvtThink()
	{
		RaceManagerInstance actor = getActor();
		if(actor == null)
			return;

		MonRaceInfoPacket packet = actor.getPacket();
		if(packet == null)
			return;

		synchronized (this)
		{
			if(thinking)
				return;
			thinking = true;
		}

		try
		{
			List<Player> newPlayers = new ArrayList<Player>();
			for(Player player : World.getAroundObservers(actor))
			{
				if(player == null)
					continue;
				newPlayers.add(player);
				if(!_knownPlayers.contains(player))
					player.sendPacket(packet);
				_knownPlayers.remove(player);
			}

			for(Player player : _knownPlayers)
				actor.removeKnownPlayer(player);

			_knownPlayers = newPlayers;
		}
		finally
		{
			// Stop thinking action
			thinking = false;
		}
	}

	@Override
	public RaceManagerInstance getActor()
	{
		return (RaceManagerInstance) super.getActor();
	}
}