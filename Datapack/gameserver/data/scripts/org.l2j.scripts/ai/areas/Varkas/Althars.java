package ai.areas.Varkas;

import ai.AbstractNpcAI;
import org.l2j.gameserver.data.xml.impl.SpawnsData;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Althars extends AbstractNpcAI {
    private static Logger LOGGER = LoggerFactory.getLogger(Althars.class);

    private final int _DELAY = 10000;
    private boolean _ACTIVATED = false;


    private Althars() {
        startQuestTimer("ALTHARS_TIMER", _DELAY, null,null);
    }

    @Override
    public String onAdvEvent(String event, Npc npc, Player player) {
        LOGGER.info("running task {}", event);
        if("ALTHARS_TIMER".equals(event)) {

            _ACTIVATED = !_ACTIVATED;
            LOGGER.info("running spawn {}", _ACTIVATED);
            if (_ACTIVATED) {
                spawnMonsters();
            }
            if (!_ACTIVATED) {
                unSpawnMonsters();
            }

            startQuestTimer("ALTHARS_TIMER", _DELAY, null,null);
        }

        return super.onAdvEvent(event, npc, player);
    }

    private void spawnMonsters() {
        LOGGER.info("spawning mobs");
        SpawnsData.getInstance().spawnByName("althar_1");
        //TODO: activate the glow of althars
        //TODO: spawn monsters
    }

    private void unSpawnMonsters() {
        LOGGER.info("despawning mobs");
        SpawnsData.getInstance().deSpawnByName("althar_1");
        //TODO: deactivate the glow of althars
        //TODO: unspawn monsters
    }

    public static AbstractNpcAI provider() {
        return new Althars();
    }
}
