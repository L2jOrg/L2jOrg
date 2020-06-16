package ai.areas.GiantCave;

import ai.AbstractNpcAI;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;

public class EntranceRoom extends AbstractNpcAI{
    private final int MONSTER_SPAWN_CHANCE_SOLO = 10;	// 10%
    private final int MONSTER_SPAWN_CHANCE_PARTY = 30;	// 30%

    private final int MONSTER_DESPAWN_DELAY_SOLO = 300000;
    private final int MONSTER_DESPAWN_DELAY_PARTY = 300000;

    private final int[] MONSTER_NPC_IDS = {
            20646,
            20647,
            20648,
            20649,
            20650
    };

    private static final int SUMMON_MONSTER_NPC_ID_SOLO = 24017;
    private static final int SUMMON_MONSTER_NPC_ID_PARTY = 24023;
    private EntranceRoom()
    {
        addKillId(MONSTER_NPC_IDS);
    }


    @Override
    public String onKill(Npc npc, Player killer, boolean isSummon) {
        if(killer.isInParty() && killer.getParty().getMemberCount() >= 5)
        {
            if(Rnd.get(100) <= MONSTER_SPAWN_CHANCE_PARTY)
                addSpawn(SUMMON_MONSTER_NPC_ID_PARTY, npc, false, MONSTER_DESPAWN_DELAY_PARTY);
        }
        else
        {
            if(Rnd.get(100) <= MONSTER_SPAWN_CHANCE_SOLO)
                addSpawn(SUMMON_MONSTER_NPC_ID_SOLO, npc, false, MONSTER_DESPAWN_DELAY_SOLO);
        }
        return super.onKill(npc, killer, isSummon);
    }


    public static AbstractNpcAI provider()
    {
        return new EntranceRoom();
    }
}
