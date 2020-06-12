package ai.areas.ForestOfTheMirrors;

import ai.AbstractNpcAI;
import org.l2j.gameserver.ai.CtrlEvent;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;


public class Mirrors extends AbstractNpcAI {
    private final int MIRROR_NPC_ID = 20639;	// Зекрало
    private final int DESPAWN_TIME = 600000;
    private final long DELAY = 1000L;
    private int _spawnStage = 0;


    private Mirrors()
    {
        addKillId(MIRROR_NPC_ID);
        startQuestTimer("MIRRORS_SPAWN_THREAD", DELAY, null, null);
    }

    @Override
    public String onKill(Npc npc, Player killer, boolean isSummon) {
        if(npc.getId() == MIRROR_NPC_ID) {
            if(_spawnStage < 4)
            {
                startQuestTimer("MIRRORS_SPAWN_THREAD", DELAY, null, null);
                for(int i = 0; i < 2; i++)
                {
                    addSpawn(MIRROR_NPC_ID, npc, false, DESPAWN_TIME);
                    if (npc.getAI() != null){
                        setSpawnStage(_spawnStage + 1);
                    }
                   /* if(npc.getAI() instanceof Mirrors)
                        ((Mirrors) npc.getAI()).setSpawnStage(_spawnStage + 1);*/
                    npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer, 200);
                }
            }
        }
        return super.onKill(npc, killer, isSummon);
    }

    public void setSpawnStage(int value)
    {
        _spawnStage = value;
    }

    public static AbstractNpcAI provider()
    {
        return new Mirrors();
    }
}
