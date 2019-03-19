package org.l2j.gameserver.instancemanager.events;

import org.l2j.gameserver.instancemanager.QuestManager;
import org.l2j.gameserver.model.eventengine.AbstractEvent;
import org.l2j.gameserver.model.eventengine.AbstractEventManager;
import org.l2j.gameserver.model.eventengine.ScheduleTarget;
import org.l2j.gameserver.model.quest.Event;

/**
 * @author Mobius
 */
public class RaceManager extends AbstractEventManager<AbstractEvent<?>> {
    private RaceManager() {
    }

    @Override
    public void onInitialized() {
    }

    @ScheduleTarget
    protected void startEvent() {
        final Event event = (Event) QuestManager.getInstance().getQuest("Race");
        if (event != null) {
            event.eventStart(null);
        }
    }

    public static RaceManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final RaceManager INSTANCE = new RaceManager();
    }
}
