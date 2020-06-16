package ai.areas.TowerOfInsolence.HeavenlyRift;

import ai.AbstractNpcAI;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;

public class Tower extends AbstractNpcAI {
    public Tower()
    {

    }

    @Override
    public String onKill(Npc npc, Player killer, boolean isSummon) {
        for(Npc npc : HeavenlyRift.getZone().getInsideNpcs())
        {
            if(npc.getNpcId() == 20139 && !npc.isDead())
                npc.decayMe();
        }

        ServerVariables.set("heavenly_rift_complete", ServerVariables.getInt("heavenly_rift_level", 0));
        ServerVariables.set("heavenly_rift_level", 0);
        ServerVariables.set("heavenly_rift_reward", 0);
        return super.onKill(npc, killer, isSummon);
    }

}
