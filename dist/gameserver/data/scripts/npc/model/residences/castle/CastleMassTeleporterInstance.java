package npc.model.residences.castle;

import java.util.List;
import java.util.concurrent.Future;

import l2s.commons.collections.MultiValueSet;
import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.World;
import l2s.gameserver.model.entity.events.impl.CastleSiegeEvent;
import l2s.gameserver.model.entity.events.impl.SiegeEvent;
import l2s.gameserver.model.entity.events.objects.SiegeToggleNpcObject;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.Functions;
import l2s.gameserver.utils.Location;

/**
 * @author VISTALL
 * @date 17:46/12.07.2011
 */
public class CastleMassTeleporterInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	private class TeleportTask extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			Functions.npcShout(CastleMassTeleporterInstance.this, NpcString.THE_DEFENDERS_OF_S1_CASTLE_WILL_BE_TELEPORTED_TO_THE_INNER_CASTLE, "#" + getCastle().getNpcStringName().getId());

			for(Player p : World.getAroundPlayers(CastleMassTeleporterInstance.this, 200, 50))
				p.teleToLocation(Location.findPointToStay(_teleportLoc, 10, 100, p.getGeoIndex()));

			_teleportTask = null;
		}
	}

	private Future<?> _teleportTask = null;
	private Location _teleportLoc;

	public CastleMassTeleporterInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
		_teleportLoc = Location.parseLoc(template.getAIParams().getString("teleport_loc"));
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(_teleportTask != null)
		{
			showChatWindow(player, "residence2/castle/CastleTeleportDelayed.htm", false);
			return;
		}

		_teleportTask = ThreadPoolManager.getInstance().schedule(new TeleportTask(), isAllTowersDead() ? 480000L : 30000L);

		showChatWindow(player, "residence2/castle/CastleTeleportDelayed.htm", false);
	}

	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... arg)
	{
		if(_teleportTask != null)
			showChatWindow(player, "residence2/castle/CastleTeleportDelayed.htm", firstTalk);
		else
		{
			if(isAllTowersDead())
				showChatWindow(player, "residence2/castle/gludio_mass_teleporter002.htm", firstTalk);
			else
				showChatWindow(player, "residence2/castle/gludio_mass_teleporter001.htm", firstTalk);
		}
	}

	private boolean isAllTowersDead()
	{
		SiegeEvent siegeEvent = getEvent(SiegeEvent.class);
		if(siegeEvent == null || !siegeEvent.isInProgress())
			return false;

		List<SiegeToggleNpcObject> towers = siegeEvent.getObjects(CastleSiegeEvent.CONTROL_TOWERS);
		for(SiegeToggleNpcObject t : towers)
			if(t.isAlive())
				return false;

		return true;
	}
}
