package ai.areas.AligatorIsland;


import ai.AbstractNpcAI;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Nos extends AbstractNpcAI {

    private final int MONSTER_CHANCE_SPAWN = 70;
    private final int NOS = 20793;
    private final int CROKIAN = 20804;
    private final int MONSTER_DESPAWN_DELAY = 300000;

    private Nos()
    {
    addAggroRangeEnterId(CROKIAN);
    }

    @Override
    public String onAggroRangeEnter(Npc npc, Player player, boolean isSummon) {
        if(Rnd.get(100) <= MONSTER_CHANCE_SPAWN) {
            addSpawn(NOS, npc, false, MONSTER_DESPAWN_DELAY);
            addSpawn(NOS, npc, false, MONSTER_DESPAWN_DELAY);
            addSpawn(NOS, npc, false, MONSTER_DESPAWN_DELAY);
        }
        return super.onAggroRangeEnter(npc, player, isSummon);
    }

    public static AbstractNpcAI provider()
    {
        return new ai.areas.AligatorIsland.Nos();
    }
}
