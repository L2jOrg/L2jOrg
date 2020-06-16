package ai.areas.TowerOfInsolence.HeavenlyRift;

import ai.AbstractNpcAI;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.util.exp4j.Functions;

public class DivineAngel extends AbstractNpcAI {
    public DivineAngel()
    {

    }

    @Override
    public String onKill(Npc npc, Player killer, boolean isSummon) {
        if(ServerVariables.getInt("heavenly_rift_level", 0) > 1)
        {
            if(HeavenlyRift.getAliveNpcCount(getActor().getNpcId()) == 0)//Last
            {
                ServerVariables.set("heavenly_rift_complete", ServerVariables.getInt("heavenly_rift_level", 0));
                ServerVariables.set("heavenly_rift_level", 0);
                ServerVariables.set("heavenly_rift_reward", 1);
                for(Npc npc : HeavenlyRift.getZone().getInsideNpcs())
                {
                    if(npc.getNpcId() == 18004)
                    {
                        Functions.npcSay(npc, NpcString.DIVINE_ANGELS_ARE_NOWHERE);
                        break;
                    }
                }
            }
        }
        return super.onKill(npc, killer, isSummon);
    }


    @Override
    public String onAggroRangeEnter(Npc npc, Player player, boolean isSummon) {
        if(target == null)
            return false;

        if(target.isPlayable())
        {
            for(Npc npc : HeavenlyRift.getZone().getInsideNpcs()) //for tower defense event only the tower is a target
            {
                if(npc.getNpcId() == 18004)
                    return false;
            }
            return true;
        }
        else if(target.getNpcId() == 18004)
            return true;

        return false;
    }
        return super.onAggroRangeEnter(npc, player, isSummon);
    }

    @Override
    public boolean checkAggression(Creature target)
    {
        if(target == null)
            return false;

        if(target.isPlayable())
        {
            for(NpcInstance npc : HeavenlyRift.getZone().getInsideNpcs()) //for tower defense event only the tower is a target
            {
                if(npc.getNpcId() == 18004)
                    return false;
            }
            return true;
        }
        else if(target.getNpcId() == 18004)
            return true;

        return false;
    }
}
