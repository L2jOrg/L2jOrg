package ai.areas.TowerOfInsolence.HeavenlyRift;

import ai.AbstractNpcAI;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.instancemanager.GlobalVariablesManager;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.util.exp4j.Functions;

public class DivineAngel extends AbstractNpcAI {
    public DivineAngel(){ }

    @Override
    public String onKill(Npc npc, Player killer, boolean isSummon) {
        if(GlobalVariablesManager.getInstance().getInt("heavenly_rift_level", 0) > 1)
        {
            if(HeavenlyRift.getAliveNpcCount(npc.getId()) == 0)//Last
            {
                GlobalVariablesManager.getInstance().set("heavenly_rift_complete", GlobalVariablesManager.getInstance().getInt("heavenly_rift_level", 0));
                GlobalVariablesManager.getInstance().set("heavenly_rift_level", 0);
                GlobalVariablesManager.getInstance().set("heavenly_rift_reward", 1);
                HeavenlyRift.getZone().forEachCreature(riftNpc -> {
                    npc.broadcastSay(ChatType.NPC_SHOUT, NpcStringId.DIVINE_ANGELS_ARE_NOWHERE_TO_BE_SEEN_I_WANT_TO_TALK_TO_THE_PARTY_LEADER);
                }, riftNpc -> GameUtils.isNpc(riftNpc) &&  riftNpc.getId() == 18004);

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
