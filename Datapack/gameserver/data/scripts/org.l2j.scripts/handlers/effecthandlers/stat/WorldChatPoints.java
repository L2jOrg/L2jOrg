package handlers.effecthandlers.stat;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Nik
 */
public class WorldChatPoints extends AbstractStatEffect {

    public WorldChatPoints(StatsSet params) {
        super(params, Stat.WORLD_CHAT_POINTS);
    }
}
