package ai.areas.ForestOfTheMirrors;

import ai.AbstractNpcAI;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Thoss
 */
// FIXME: Dev in process
public class Mirrors extends AbstractNpcAI {
    private static final Logger LOGGER = LoggerFactory.getLogger(Mirrors.class);

    private final int MIRROR_NPC_ID = 20639;
    private final int DESPAWN_TIME = 600000;
    private final int MIRROR_COUNT = 4;

    private Map<Integer, Integer> _Leaders_Stages = new ConcurrentHashMap<>(); // <Leader ObjectID, Leader Stage>
    private Map<Integer, List<Integer>> _Leader_Minions = new ConcurrentHashMap<>(); // <Leader ObjectID, Leader minions ObjectID>

    private Mirrors()
    {
        addKillId(MIRROR_NPC_ID);
    }

    @Override
    public String onKill(Npc npc, Player killer, boolean isSummon) {
        if(npc.getId() == MIRROR_NPC_ID) {
            int leaderObjectId = getLeader(npc.getObjectId());

            if(leaderObjectId == -1) {
                leaderObjectId = npc.getObjectId();
                _Leaders_Stages.putIfAbsent(leaderObjectId, 0);
                _Leader_Minions.putIfAbsent(leaderObjectId, new ArrayList<>());
            } else {
                _Leader_Minions.get(leaderObjectId).add(npc.getObjectId());
            }

            switch (_Leaders_Stages.get(leaderObjectId))
            {
                case 0, 1, 2, 3: {
                    if(_Leader_Minions.get(leaderObjectId).size() % 4 == 0) {
                        for (int i = 0; i < MIRROR_COUNT; i++) {
                            Npc mirror = addSpawn(MIRROR_NPC_ID, npc, true, DESPAWN_TIME);
                            addAttackPlayerDesire(mirror, killer);
                        }
                        _Leaders_Stages.replace(leaderObjectId, _Leaders_Stages.get(leaderObjectId) + 1);
                    }
                }
                case 4: {
                    _Leaders_Stages.remove(leaderObjectId);
                    _Leader_Minions.remove(leaderObjectId);
                }
            }
        }
        return super.onKill(npc, killer, isSummon);
    }

    private int getLeader(int npcObjectId) {
        for(Map.Entry<Integer, List<Integer>> leader : _Leader_Minions.entrySet())
            if(leader.getValue().contains(npcObjectId))
                return leader.getKey();

        return -1;
    }

    public static AbstractNpcAI provider()
    {
        return new Mirrors();
    }
}
