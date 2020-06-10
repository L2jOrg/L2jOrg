package ai.areas.GiantCave;

import ai.AbstractNpcAI;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.data.xml.impl.SpawnsData;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Monster;
import org.l2j.gameserver.model.actor.instance.Player;

import java.util.concurrent.Future;

public class Batur extends AbstractNpcAI
{
    private static final int TIME_TO_LIVE = 60000;
    private final long TIME_TO_DIE = System.currentTimeMillis() + TIME_TO_LIVE;
    private static final int BATUR = 24020;
    private static final long RESPAWN_DELAY = 900000; // 15 min
    private static final String[] SPAWN_GROUPS = {
            "batur1",
            "batur2",
    };

    private int _currentSpawnedGroup = -1;
    private Future<?> _respawnTask;

    private Batur()
    {
        addAttackId(BATUR);
    }

    @Override
    public String onAdvEvent(String event, Npc npc, Player player) {
        SpawnsData.getInstance().spawnByName("batur");
        _respawnTask = ThreadPool.scheduleAtFixedRate(new RespawnTask(), RESPAWN_DELAY, RESPAWN_DELAY);
        return super.onAdvEvent(event, npc, player);
    }

    @Override
    public String onAttack(Npc npc, Player attacker, int damage, boolean isSummon)
    {

        final Monster monster = (Monster) npc;
        if(monster != null && System.currentTimeMillis() >= TIME_TO_DIE + 60000)
        {
            monster.deleteMe();
        }

       return super.onAttack(monster, attacker, damage, isSummon);
    }

    private void stopRespawnTask()
    {
        if(_respawnTask != null)
        {
            _respawnTask.cancel(false);
            _respawnTask = null;
        }
    }
    private class RespawnTask implements Runnable
    {
        @Override
        public void run()
        {
            int newSpawnGroup = 0;
            newSpawnGroup = Rnd.get(SPAWN_GROUPS.length);
            while(newSpawnGroup == _currentSpawnedGroup)
            {
                newSpawnGroup = Rnd.get(SPAWN_GROUPS.length);
            }

            String groupName = SPAWN_GROUPS[_currentSpawnedGroup];
            SpawnsData.getInstance().deSpawnByName(groupName);


            groupName = SPAWN_GROUPS[newSpawnGroup];
            SpawnsData.getInstance().spawnByName(groupName);
            _currentSpawnedGroup = newSpawnGroup;
        }
    }

    public static AbstractNpcAI provider()
    {
        return new Batur();
    }

}