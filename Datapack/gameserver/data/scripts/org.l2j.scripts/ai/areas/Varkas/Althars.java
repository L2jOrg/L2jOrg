package ai.areas.Varkas;

import ai.AbstractNpcAI;
import org.l2j.commons.threading.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Althars extends AbstractNpcAI{
    private static Logger LOGGER = LoggerFactory.getLogger(Althars.class);

    private final int DELAY = 30000;
    private final int SHAMAN = 21874;
    private final int ESCORT = 21869;
    private final int ALTHARS = 18926;

    private boolean ACTIVATED = false;


    private Althars() {
        ThreadPool.scheduleAtFixedDelay(new AltharsThread(), DELAY, DELAY);
    }

    private class AltharsThread implements Runnable{
        @Override
        public void run() {
            manageTask();
        }

    }

    private void manageTask() {
        ACTIVATED = !ACTIVATED;

        if (ACTIVATED) {
            spawnMonsters();
        }
        if (!ACTIVATED) {
            unSpawnMonsters();
        }
    }

    private void spawnMonsters(){
        //TODO find the glow of althars
        //TODO spawns monsters
    }

    private void unSpawnMonsters(){
    }

    public static AbstractNpcAI provider()
    {
        return new Althars();
    }

}
