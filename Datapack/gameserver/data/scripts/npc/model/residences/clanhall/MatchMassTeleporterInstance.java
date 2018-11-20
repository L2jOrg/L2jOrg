package npc.model.residences.clanhall;

import java.util.List;

import org.l2j.commons.collections.MultiValueSet;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.World;
import org.l2j.gameserver.model.entity.events.impl.ClanHallTeamBattleEvent;
import org.l2j.gameserver.model.entity.events.objects.CTBTeamObject;
import org.l2j.gameserver.model.entity.residence.ClanHall;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.templates.npc.NpcTemplate;
import org.l2j.gameserver.utils.Location;

/**
 * @author VISTALL
 * @date 15:13/27.04.2011
 */
public class MatchMassTeleporterInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	private int _flagId;
	private long _timeout;

	public MatchMassTeleporterInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
		_flagId = template.getAIParams().getInteger("flag");
	}

	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... arg)
	{
		ClanHall clanHall = getClanHall();
		ClanHallTeamBattleEvent siegeEvent = clanHall.getSiegeEvent();

		if(_timeout > System.currentTimeMillis())
		{
			showChatWindow(player, "residence2/clanhall/agit_mass_teleporter001.htm", firstTalk);
			return;
		}

		if(checkInteractionDistance(player))
		{
			_timeout = System.currentTimeMillis() + 60000L;

			List<CTBTeamObject> locs = siegeEvent.getObjects(ClanHallTeamBattleEvent.TRYOUT_PART);

			CTBTeamObject object = locs.get(_flagId);
			if(object.getFlag() != null)
			{
				for(Player $player : World.getAroundPlayers(this, 400, 100))
					$player.teleToLocation(Location.findPointToStay(object.getFlag(), 100, 125));
			}
		}
	}
}
