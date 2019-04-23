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

/**
 * @author Iris, Mobius
 */
public class LoginMonthDailyMissionHandler extends AbstractDailyMissionHandler
{
    public LoginMonthDailyMissionHandler(DailyMissionDataHolder holder)
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
        // Monthly rewards do not reset daily.
    }

    private void onPlayerLogin(OnPlayerLogin event)
    {
        final DailyMissionPlayerEntry entry = getPlayerEntry(event.getActiveChar().getObjectId(), true);
        final long lastCompleted = entry.getLastCompleted();
        if (lastCompleted == 0) // Initial entry.
        {
            entry.setLastCompleted(System.currentTimeMillis());
        }
        else if ((System.currentTimeMillis() - lastCompleted) > 2506000000L) // 2506000000L (29 day) delay.
        {
            entry.setProgress(1);
            entry.setStatus(DailyMissionStatus.AVAILABLE);
        }
        else
        {
            entry.setProgress(0);
            entry.setStatus(DailyMissionStatus.NOT_AVAILABLE);
        }
        storePlayerEntry(entry);
    }
}
