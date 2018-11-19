package npc.model.residences.castle;

import l2s.commons.collections.MultiValueSet;
import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.Location;

/**
 * @author VISTALL
 * @date 21:53/23.05.2011
 * 29055
 */
public class VenomTeleportCubicInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public static final Location[] LOCS = new Location[]
	{
		new Location(11913, -48851, -1088),
		new Location(11918, -49447, -1088)
	};

	public VenomTeleportCubicInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		player.teleToLocation(LOCS[Rnd.get(LOCS.length)]);
	}

	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... arg)
	{
		showChatWindow(player, "residence2/castle/teleport_cube_benom001.htm", firstTalk);
	}
}
