package ai.areas.Varkas;

import ai.AbstractNpcAI;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;

public class Althars extends AbstractNpcAI {
    private static final int SHAMAN = 21874;
    private static final int ESCORT = 21869;
    private static final int altar = 18926;
    private Object Althars;


    private Althars(){
        startQuestTimer("ACTIVATE_TIMER", 3600000, null, null);
        addSpawnId(SHAMAN, ESCORT);
    }


    @Override
    public void startQuestTimer(String name, long time, Npc npc, Player player) {
        ThreadPool.scheduleAtFixedDelay(this::spawnMonsters,3600000,3600000);
        super.startQuestTimer(name, time, npc, player);
    }

    private void spawnMonsters() {

    }



    public static AbstractNpcAI provider()
    {
        return new Althars();
    }

}
