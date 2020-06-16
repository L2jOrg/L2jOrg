package ai.areas.TowerOfInsolence.HeavenlyRift;

import ai.AbstractNpcAI;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.DamageInfo;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.world.World;

public class Bomb extends AbstractNpcAI {
    private static final int[] ITEM_DROP_1 = { 49756, 49762, 49763 };
    private static final int[] ITEM_DROP_2 = { 49760, 49761 };

    public Bomb() { }

    @Override
    public String onSpawn(Npc npc) {
        //getActor().setNpcState(1); // sending ExChangeNpcState ?
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
            addSpawn(20139, npc, false, 1800000L);
            NpcUtils.spawnSingle(20139, getActor().getSpawnedLoc(), 1800000L);
        }
        else
        {
            if(Rnd.chance(90))
                npc.dropItem(killer.getActingPlayer(),  ITEM_DROP_1[Rnd.get(ITEM_DROP_1.length)], 1);
                getActor().dropItem(killer.getPlayer(), ITEM_DROP_1[Rnd.get(ITEM_DROP_1.length)], 1);
            else
                npc.dropItem(killer.getActingPlayer(),  ITEM_DROP_2[Rnd.get(ITEM_DROP_2.length)], 1);
                getActor().dropItem(killer.getPlayer(), ITEM_DROP_2[Rnd.get(ITEM_DROP_2.length)], 1);
        }

        if(HeavenlyRift.getAliveNpcCount(npc.getId()) == 0)//Last
        {
            ServerVariables.set("heavenly_rift_complete", ServerVariables.getInt("heavenly_rift_level", 0));
            ServerVariables.set("heavenly_rift_level", 0);
        }
        return super.onKill(npc, killer, isSummon);
    }
}
