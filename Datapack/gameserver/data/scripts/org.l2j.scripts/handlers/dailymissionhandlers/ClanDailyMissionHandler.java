package handlers.dailymissionhandlers;

import org.l2j.gameserver.handler.AbstractDailyMissionHandler;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.dailymission.DailyMissionDataHolder;
import org.l2j.gameserver.data.database.data.DailyMissionPlayerData;
import org.l2j.gameserver.model.dailymission.DailyMissionStatus;
import org.l2j.gameserver.model.events.Containers;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerClanJoin;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;

import java.util.function.Consumer;

import static java.util.Objects.nonNull;

public class ClanDailyMissionHandler extends AbstractDailyMissionHandler {

    private final MissionKind kind;

    public ClanDailyMissionHandler(DailyMissionDataHolder holder) {
        super(holder);
        kind = holder.getParams().getEnum("kind", MissionKind.class, MissionKind.JOIN);
    }

    @Override
    public boolean isAvailable(L2PcInstance player) {
        final DailyMissionPlayerData entry = getPlayerEntry(player, false);
        return (nonNull(entry)) && (DailyMissionStatus.AVAILABLE == entry.getStatus());
    }

    @Override
    public void init() {
        if(MissionKind.JOIN == kind) {
            Containers.Global().addListener(new ConsumerEventListener(this, EventType.ON_PLAYER_CLAN_JOIN, (Consumer<OnPlayerClanJoin>) this::onPlayerJoinClan, this));
        }
    }

    private void onPlayerJoinClan(OnPlayerClanJoin event) {
        final DailyMissionPlayerData entry = getPlayerEntry(event.getActiveChar().getPlayerInstance(), true);
        if(DailyMissionStatus.COMPLETED.equals(entry.getStatus())) {
            return;
        }

        entry.setProgress(1);
        entry.setStatus(DailyMissionStatus.AVAILABLE);
        storePlayerEntry(entry);
        notifyAvailablesReward(event.getActiveChar().getPlayerInstance());
    }


    enum MissionKind {
        JOIN,
        ARENA
    }
}
