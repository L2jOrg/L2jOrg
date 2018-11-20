package npc.model;

import org.l2j.commons.collections.MultiValueSet;
import org.l2j.gameserver.model.GameObjectsStorage;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.templates.npc.NpcTemplate;
import org.l2j.gameserver.utils.Location;

import bosses.BaiumManager;

/**
 * @author Bonux
 */
public final class BaiumGatekeeperInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	// NPC's
	private static final int BAIUM_RAID_NPC_ID = 29020;
	private static final int BAIUM_STONED_NPC_ID = 29025;

	// Locations
	private static final Location TELEPORT_POSITION = new Location(113100, 14500, 10077);

	public BaiumGatekeeperInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onTeleportRequest(Player talker)
	{
		NpcInstance baiumNpc = GameObjectsStorage.getByNpcId(BAIUM_STONED_NPC_ID);
		NpcInstance baiumBoss = GameObjectsStorage.getByNpcId(BAIUM_RAID_NPC_ID);
		if(baiumNpc != null || baiumBoss != null)
		{
			if(baiumBoss == null)
			{
				if(BaiumManager.consumeRequiredItems(talker))
				{
					talker.setVar("baiumPermission", "granted", -1);
					talker.teleToLocation(TELEPORT_POSITION);
				}
				else
					showChatWindow(talker, "default/dimension_vertex_4002.htm", false);
			}
			else
				showChatWindow(talker, "default/dimension_vertex_4003.htm", false);
		}
		else
			showChatWindow(talker, "default/dimension_vertex_4004.htm", false);
	}

	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... arg)
	{
		if(val == 0)
			showChatWindow(player, "default/dimension_vertex_4001.htm", firstTalk);
		else
			super.showChatWindow(player, val, firstTalk, arg);
	}
}