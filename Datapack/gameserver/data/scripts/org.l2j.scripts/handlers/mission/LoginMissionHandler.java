package handlers.mission;


import org.l2j.gameserver.engine.mission.MissionStatus;
import org.l2j.gameserver.handler.AbstractMissionHandler;
import org.l2j.gameserver.engine.mission.MissionDataHolder;
import org.l2j.gameserver.data.database.data.MissionPlayerData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.Listeners;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogin;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;

import java.util.Calendar;
import java.util.function.Consumer;

import static org.l2j.commons.util.Util.isInteger;

public class LoginMissionHandler extends AbstractMissionHandler {

    private byte days = 0;

    public LoginMissionHandler(MissionDataHolder holder) {
        super(holder);
        var days = holder.getParams().getString("days", "");
        for (String day : days.split(" ")) {
            if(isInteger(day)) {
                this.days |= 1 << Integer.parseInt(day);
            }
        }
    }

    @Override
    public boolean isAvailable(Player player) {
        final MissionPlayerData entry = getPlayerEntry(player, false);
        return (entry != null) && (MissionStatus.AVAILABLE == entry.getStatus());
    }

    @Override
    public void init() {
        Listeners.Global().addListener(new ConsumerEventListener(this, EventType.ON_PLAYER_LOGIN, (Consumer<OnPlayerLogin>) this::onPlayerLogin, this));
    }

    private void onPlayerLogin(OnPlayerLogin event) {
        final MissionPlayerData entry = getPlayerEntry(event.getPlayer(), true);
        if(MissionStatus.COMPLETED.equals(entry.getStatus())) {
            return;
        }

        final int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        if(days != 0 && (days & 1 << currentDay) == 0) {
            entry.setProgress(0);
            entry.setStatus(MissionStatus.NOT_AVAILABLE);
        } else {
            entry.setProgress(1);
            entry.setStatus(MissionStatus.AVAILABLE);
            notifyAvailablesReward(event.getPlayer());
        }
    }
}
