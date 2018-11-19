package l2s.gameserver.instancemanager;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;

import l2s.commons.threading.SteppingRunnableQueueManager;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Request;
import l2s.gameserver.network.l2.s2c.ExRegistWaitingSubstituteOk;

/**
 * @author monithly
**/
public class PartySubstituteManager extends SteppingRunnableQueueManager
{
	private static final PartySubstituteManager _instance = new PartySubstituteManager();

	private final List<Player> waitingPlayers = new CopyOnWriteArrayList<Player>();
	private final List<Player> waitingMembers = new CopyOnWriteArrayList<Player>();

	public void addWaitingPlayer(final Player player)
	{
		waitingPlayers.add(player);
	}

	public void removeWaitingPlayer(final Player player)
	{
		waitingPlayers.remove(player);
	}

	public void addPartyMember(final Player player)
	{
		waitingMembers.add(player);
	}

	public void removePartyMember(final Player player)
	{
		waitingMembers.remove(player);
	}

	public static  PartySubstituteManager getInstance()
	{
		return _instance;
	}

	private PartySubstituteManager()
	{
		super(10000L);
		ThreadPoolManager.getInstance().scheduleAtFixedRate(this, 10000L, 10000L);
		ThreadPoolManager.getInstance().scheduleAtFixedRate(() -> PartySubstituteManager.this.purge(), 60000L, 60000L);

		scheduleAtFixedRate(() ->
		{
			if(!waitingMembers.isEmpty() && !waitingPlayers.isEmpty())
			{
				for(Player player : waitingMembers)
				{
					if(player == null || !player.isOnline() || player.getRequest() != null)
						continue;

					if(!player.isPartySubstituteStarted() || player.getParty() == null)
						continue;

					for(Player wait : waitingPlayers)
					{
						if(wait == null || wait.getParty() != null || wait.getRequest() != null)
							continue;

						if(wait.getClassId() == player.getClassId() && wait.getLevel() == player.getLevel())
						{
							//player.getParty().getPartyLeader().sendMessage("finded new member "+wait.getPlayer().getName());
							//player.getParty().getPartyLeader().sendPacket(new ExExchangeSubstitute(wait,player));
							new Request(Request.L2RequestType.PARTY_MEMBER_SUBSTITUTE,player, wait).setTimeout(10000L);
							wait.sendPacket(new ExRegistWaitingSubstituteOk(null));
							player.stopSubstituteTask();
						}
					}
				}
			}
		}, 30000L, 30000L);
	}

	public Future<?> SubstituteSearchTask(final Player player)
	{
		if(player == null)
			return null;

		waitingMembers.add(player);
		return schedule(() ->
		{
			waitingMembers.remove(player);
			if(player.getParty() != null)
			{
				//player.getParty().getPartyLeader().sendPacket(new ExTimeOverPartySubstitute(player.getObjectId()));
			}
			player.sendUserInfo();
			player.sendMessage("test");
		}, 300000L);
	}
}