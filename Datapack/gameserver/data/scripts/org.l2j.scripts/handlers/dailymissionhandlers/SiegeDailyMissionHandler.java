package handlers.dailymissionhandlers;

import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.handler.AbstractDailyMissionHandler;
import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.model.L2SiegeClan;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.dailymission.DailyMissionDataHolder;
import org.l2j.gameserver.data.database.data.DailyMissionPlayerData;
import org.l2j.gameserver.model.dailymission.DailyMissionStatus;
import org.l2j.gameserver.model.events.Containers;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.sieges.OnCastleSiegeStart;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;

import java.util.function.Consumer;

import static java.util.Objects.nonNull;

/**
 * @author UnAfraid
 */
public class SiegeDailyMissionHandler extends AbstractDailyMissionHandler {
	public SiegeDailyMissionHandler(DailyMissionDataHolder holder)
	{
		super(holder);
	}
	
	@Override
	public void init() {
		Containers.Global().addListener(new ConsumerEventListener(this, EventType.ON_CASTLE_SIEGE_START, (Consumer<OnCastleSiegeStart>) this::onSiegeStart, this));
	}
	
	@Override
	public boolean isAvailable(L2PcInstance player) {
		final DailyMissionPlayerData entry = getPlayerEntry(player, false);
		return nonNull(entry) && DailyMissionStatus.AVAILABLE == entry.getStatus();
	}
	
	private void onSiegeStart(OnCastleSiegeStart event) {
		event.getSiege().getAttackerClans().forEach(this::processSiegeClan);
		event.getSiege().getDefenderClans().forEach(this::processSiegeClan);
	}
	
	private void processSiegeClan(L2SiegeClan siegeClan)
	{
		final L2Clan clan = ClanTable.getInstance().getClan(siegeClan.getClanId());
		if (clan != null)
		{
			clan.getOnlineMembers(0).forEach(player ->
			{
				final DailyMissionPlayerData entry = getPlayerEntry(player, true);
				entry.setStatus(DailyMissionStatus.AVAILABLE);
				notifyAvailablesReward(player);
				storePlayerEntry(entry);
			});
		}
	}
}
