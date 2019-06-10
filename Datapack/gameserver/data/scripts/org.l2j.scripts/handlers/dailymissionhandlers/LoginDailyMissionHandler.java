package handlers.dailymissionhandlers;


import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.dailymission.DailyMissionStatus;
import org.l2j.gameserver.handler.AbstractDailyMissionHandler;
import org.l2j.gameserver.model.dailymission.DailyMissionDataHolder;
import org.l2j.gameserver.model.dailymission.DailyMissionPlayerData;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.events.Containers;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogin;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;

import java.util.Calendar;
import java.util.function.Consumer;

public class LoginDailyMissionHandler extends AbstractDailyMissionHandler {

    private byte days = 0;

    public LoginDailyMissionHandler(DailyMissionDataHolder holder) {
        super(holder);
        var days = holder.getParams().getString("days", "");
        for (String day : days.split(" ")) {
            if(Util.isNumeric(day)) {
                this.days |= 1 << Integer.parseInt(day);
            }
        }
    }

    @Override
    public boolean isAvailable(L2PcInstance player) {
        final DailyMissionPlayerData entry = getPlayerEntry(player.getObjectId(), false);
        return (entry != null) && (DailyMissionStatus.AVAILABLE == entry.getStatus());
    }

    @Override
    public void init() {
        Containers.Global().addListener(new ConsumerEventListener(this, EventType.ON_PLAYER_LOGIN, (Consumer<OnPlayerLogin>) this::onPlayerLogin, this));
    }

    private void onPlayerLogin(OnPlayerLogin event) {
        final DailyMissionPlayerData entry = getPlayerEntry(event.getActiveChar().getObjectId(), true);
        if(DailyMissionStatus.COMPLETED.equals(entry.getStatus())) {
            return;
        }

        final int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        if(days > 0 && (days & 1 << currentDay) == 0) {
            entry.setProgress(0);
            entry.setStatus(DailyMissionStatus.NOT_AVAILABLE);
        } else {
            entry.setProgress(1);
            entry.setStatus(DailyMissionStatus.AVAILABLE);
        }
    }
}
