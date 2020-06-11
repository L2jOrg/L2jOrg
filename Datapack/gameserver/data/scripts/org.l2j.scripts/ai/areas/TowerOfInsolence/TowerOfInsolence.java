package ai.areas.TowerOfInsolence;

import ai.AbstractNpcAI;
import org.l2j.commons.util.Rnd;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;


public class TowerOfInsolence extends AbstractNpcAI {
    // Item's
    private static final int ENERGY_OF_INSOLENCE_ITEM_ID = 49685;	// Энергия Дерзости
    private static final int UNIDENTIFIED_STONE_ITEM_ID = 49766;	// Неопознанный Камень
    private static final int MONSTER_SPAWN_CHANCE = 10;
    private static final int MONSTER_DESPAWN_DELAY = 30 * 1000;	// 30 sec

    private static final int[] SPAWN_INSOLENCE_NPC_IDS = {
            20809
    };


    private static final int[] ENERGY_OF_INSOLENCE_NPC_IDS = {
            20977,
            21081
    };

    private static final int[] UNIDENTIFIED_STONE_NPC_IDS = {
            21075,
            21076,
            21077,
            21980,
            21981,
            20982,
            20980,
            20981,
            20982,
            21078,
            21079,
            21080,
            20983,
            20984,
            20985,
            21074
    };

    private static final int ENERGY_OF_INSOLENCE_MIN_DROP_COUNT = 1;
    private static final int ENERGY_OF_INSOLENCE_MAX_DROP_COUNT = 1;

    private TowerOfInsolence()
    {
        for(int npcId : SPAWN_INSOLENCE_NPC_IDS)
            addKillId(npcId);
        for(int npcId : ENERGY_OF_INSOLENCE_NPC_IDS)
            addKillId(npcId);
        for(int npcId : UNIDENTIFIED_STONE_NPC_IDS)
            addKillId(npcId);
    }

    @Override
    public String onKill(Npc npc, Player killer, boolean isSummon) {
        if(Util.contains(SPAWN_INSOLENCE_NPC_IDS, npc.getId())){
            if(Rnd.chance(MONSTER_SPAWN_CHANCE))
                addSpawn(20977, npc, false, MONSTER_DESPAWN_DELAY);
        }
        if(Util.contains(ENERGY_OF_INSOLENCE_NPC_IDS, npc.getId()))
        {
            if(Rnd.chance(70))
                npc.dropItem(killer, ENERGY_OF_INSOLENCE_ITEM_ID, Rnd.get(ENERGY_OF_INSOLENCE_MIN_DROP_COUNT, ENERGY_OF_INSOLENCE_MAX_DROP_COUNT));
        }

        if(Util.contains(UNIDENTIFIED_STONE_NPC_IDS, npc.getId()))
        {
            if((killer.getLevel() - npc.getLevel()) <= 9 && Rnd.chance(4))
                npc.dropItem(killer, UNIDENTIFIED_STONE_ITEM_ID, 1);
        }
        return super.onKill(npc, killer, isSummon);
    }

    public static AbstractNpcAI provider()
    {
        return new TowerOfInsolence();
    }
}
