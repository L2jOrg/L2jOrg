package l2s.gameserver.model.instances;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.World;
import l2s.gameserver.model.Zone;
import l2s.gameserver.model.entity.events.impl.SingleMatchEvent;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.Location;

public final class ObservationInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public ObservationInstance(int objectId,  NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(player.isInOlympiadMode())
			return;

		if(player.containsEvent(SingleMatchEvent.class))
			return;

		if(command.startsWith("observeSiege"))
		{
			String val = command.substring(13);
			StringTokenizer st = new StringTokenizer(val);
			st.nextToken(); // Bypass cost

			List<Zone> zones = new ArrayList<Zone>();
			World.getZones(zones, new Location(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken())), ReflectionManager.MAIN);
			for(Zone z : zones)
			{
				if(z.getType() == Zone.ZoneType.SIEGE && z.isActive())
				{
					doObserve(player, val);
					return;
				}
			}

			player.sendPacket(SystemMsg.OBSERVATION_IS_ONLY_POSSIBLE_DURING_A_SIEGE);
		}
		else if(command.startsWith("observe"))
			doObserve(player, command.substring(8));
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public String getHtmlDir(String filename, Player player)
	{
		return "observation/";
	}

	private void doObserve(Player player, String val)
	{
		StringTokenizer st = new StringTokenizer(val);
		int cost = Integer.parseInt(st.nextToken());
		int x = Integer.parseInt(st.nextToken());
		int y = Integer.parseInt(st.nextToken());
		int z = Integer.parseInt(st.nextToken());

		if(!player.reduceAdena(cost, true))
		{
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			player.sendActionFailed();
			return;
		}

		player.enterObserverMode(new Location(x, y, z));
	}
}