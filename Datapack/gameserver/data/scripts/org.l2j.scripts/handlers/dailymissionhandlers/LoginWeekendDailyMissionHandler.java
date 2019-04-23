package handlers.dailymissionhandlers;


import org.l2j.gameserver.enums.DailyMissionStatus;
import org.l2j.gameserver.handler.AbstractDailyMissionHandler;
import org.l2j.gameserver.model.DailyMissionDataHolder;
import org.l2j.gameserver.model.DailyMissionPlayerEntry;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.events.Containers;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogin;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;

import java.util.Calendar;

/**
 * @author Iris, Mobius
 */
public class LoginWeekendDailyMissionHandler extends AbstractDailyMissionHandler
{
    public LoginWeekendDailyMissionHandler(DailyMissionDataHolder holder)
    {
        super(holder);
    }

    @Override
    public boolean isAvailable(L2PcInstance player)
    {
        final DailyMissionPlayerEntry entry = getPlayerEntry(player.getObjectId(), false);
        if ((entry != null) && (entry.getStatus() == DailyMissionStatus.AVAILABLE))
        {
            return true;
        }
        return false;
    }

    @Override
    public void init()
    {
        Containers.Global().addListener(new ConsumerEventListener(this, EventType.ON_PLAYER_LOGIN, (OnPlayerLogin event) -> onPlayerLogin(event), this));
    }

    @Override
    public void reset()
    {
        // Weekend rewards do not reset daily.
    }

    private void onPlayerLogin(OnPlayerLogin event)
    {
        final DailyMissionPlayerEntry entry = getPlayerEntry(event.getActiveChar().getObjectId(), true);
        final int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        final long lastCompleted = entry.getLastCompleted();
        if (((currentDay == Calendar.SATURDAY) || (currentDay == Calendar.SUNDAY)) // Reward only on weekend.
                && ((lastCompleted == 0) || ((System.currentTimeMillis() - lastCompleted) > 172800000))) // Initial entry or 172800000 (2 day) delay.
        {
            entry.setProgress(1);
            entry.setStatus(DailyMissionStatus.AVAILABLE);
        }
        else if (entry.getStatus() != DailyMissionStatus.AVAILABLE) // Not waiting to be rewarded.
        {
            entry.setProgress(0);
            entry.setStatus(DailyMissionStatus.NOT_AVAILABLE);
        }
        storePlayerEntry(entry);
    }
}
