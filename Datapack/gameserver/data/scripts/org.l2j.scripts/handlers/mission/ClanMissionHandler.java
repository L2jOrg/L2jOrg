package handlers.mission;

import org.l2j.gameserver.handler.AbstractMissionHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.engine.mission.MissionDataHolder;
import org.l2j.gameserver.data.database.data.MissionPlayerData;
import org.l2j.gameserver.engine.mission.MissionStatus;
import org.l2j.gameserver.model.events.Listeners;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerClanJoin;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;

import java.util.function.Consumer;

import static java.util.Objects.nonNull;

public class ClanMissionHandler extends AbstractMissionHandler {

    private final MissionKind kind;

    public ClanMissionHandler(MissionDataHolder holder) {
        super(holder);
        kind = holder.getParams().getEnum("kind", MissionKind.class, MissionKind.JOIN);
    }

    @Override
    public boolean isAvailable(Player player) {
        final MissionPlayerData entry = getPlayerEntry(player, false);
        return (nonNull(entry)) && (MissionStatus.AVAILABLE == entry.getStatus());
    }

    @Override
    public void init() {
        if(MissionKind.JOIN == kind) {
            Listeners.Global().addListener(new ConsumerEventListener(this, EventType.ON_PLAYER_CLAN_JOIN, (Consumer<OnPlayerClanJoin>) this::onPlayerJoinClan, this));
        }
    }

    private void onPlayerJoinClan(OnPlayerClanJoin event) {
        final MissionPlayerData entry = getPlayerEntry(event.getActiveChar().getPlayerInstance(), true);
        if(MissionStatus.COMPLETED.equals(entry.getStatus())) {
            return;
        }

        entry.setProgress(1);
        entry.setStatus(MissionStatus.AVAILABLE);
        storePlayerEntry(entry);
        notifyAvailablesReward(event.getActiveChar().getPlayerInstance());
    }


    enum MissionKind {
        JOIN,
        ARENA
    }
}
