package handlers.mission;

import org.l2j.gameserver.data.database.data.MissionPlayerData;
import org.l2j.gameserver.data.database.data.SiegeClanData;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.engine.mission.MissionDataHolder;
import org.l2j.gameserver.engine.mission.MissionStatus;
import org.l2j.gameserver.handler.AbstractMissionHandler;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.Listeners;
import org.l2j.gameserver.model.events.impl.sieges.OnCastleSiegeStart;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;

import java.util.function.Consumer;

import static java.util.Objects.nonNull;

/**
 * @author UnAfraid
 */
public class SiegeMissionHandler extends AbstractMissionHandler {
	public SiegeMissionHandler(MissionDataHolder holder)
	{
		super(holder);
	}
	
	@Override
	public void init() {
		Listeners.Global().addListener(new ConsumerEventListener(this, EventType.ON_CASTLE_SIEGE_START, (Consumer<OnCastleSiegeStart>) this::onSiegeStart, this));
	}
	
	@Override
	public boolean isAvailable(Player player) {
		final MissionPlayerData entry = getPlayerEntry(player, false);
		return nonNull(entry) && MissionStatus.AVAILABLE == entry.getStatus();
	}
	
	private void onSiegeStart(OnCastleSiegeStart event) {
		event.getSiege().getAttackerClans().values().forEach(this::processSiegeClan);
		event.getSiege().getDefenderClans().values().forEach(this::processSiegeClan);
	}
	
	private void processSiegeClan(SiegeClanData siegeClan)
	{
		final Clan clan = ClanTable.getInstance().getClan(siegeClan.getClanId());
		if (clan != null)
		{
			clan.getOnlineMembers(0).forEach(player ->
			{
				final MissionPlayerData entry = getPlayerEntry(player, true);
				entry.setStatus(MissionStatus.AVAILABLE);
				notifyAvailablesReward(player);
				storePlayerEntry(entry);
			});
		}
	}
}
