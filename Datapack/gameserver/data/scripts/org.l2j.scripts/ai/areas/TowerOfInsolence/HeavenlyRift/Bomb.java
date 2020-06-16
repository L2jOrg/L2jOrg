package ai.areas.TowerOfInsolence.HeavenlyRift;

import ai.AbstractNpcAI;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.instancemanager.GlobalVariablesManager;
import org.l2j.gameserver.model.DamageInfo;
import org.l2j.gameserver.model.HeavenlyRift;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ExChangeNpcState;
import org.l2j.gameserver.world.World;

/**
 * @reworked by Thoss
 */
public class Bomb extends AbstractNpcAI {
    private static final int[] ITEM_DROP_1 = { 49756, 49762, 49763 };
    private static final int[] ITEM_DROP_2 = { 49760, 49761 };

    public Bomb() { }

    @Override
    public String onSpawn(Npc npc) {
        // TODO: create a private Map <Npc, int> -> <Npc, state> ?
        npc.broadcastPacket(new ExChangeNpcState(npc.getObjectId(), 1));
        return super.onSpawn(npc);
    }

    @Override
    public String onKill(Npc npc, Player killer, boolean isSummon) {
        if(Rnd.chance(50))
        {
            World.getInstance().forEachVisibleObjectInRange(npc, Playable.class, 200, creature -> {
                if(creature != null && !creature.isDead())
                    creature.reduceCurrentHp(Rnd.get(300, 400), npc, null, DamageInfo.DamageType.ZONE);
            });
        }
        if(Rnd.chance(33))
        {
            addSpawn(20139, npc, false, 1800000);
        }
        else
        {
            if(Rnd.chance(90))
                npc.dropItem(killer.getActingPlayer(),  ITEM_DROP_1[Rnd.get(ITEM_DROP_1.length)], 1);
            else
                npc.dropItem(killer.getActingPlayer(),  ITEM_DROP_2[Rnd.get(ITEM_DROP_2.length)], 1);
        }

        if(HeavenlyRift.getAliveNpcCount(npc.getId()) == 0)//Last
        {
            GlobalVariablesManager.getInstance().set("heavenly_rift_complete", GlobalVariablesManager.getInstance().getInt("heavenly_rift_level", 0));
            GlobalVariablesManager.getInstance().set("heavenly_rift_level", 0);
        }
        return super.onKill(npc, killer, isSummon);
    }
}
