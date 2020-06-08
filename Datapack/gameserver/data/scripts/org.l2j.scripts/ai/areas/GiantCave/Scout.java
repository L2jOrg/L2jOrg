package ai.areas.GiantCave;


import ai.AbstractNpcAI;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Scout extends AbstractNpcAI {
    private static Logger LOGGER = LoggerFactory.getLogger(Scout.class);


    private static final int GAMLIN = 20651;
    private static final int LEOGUL = 20652;

    private Scout()
    {
        addAttackId(GAMLIN, LEOGUL);
    }


    @Override
    public String onAttack(Npc npc, Player player, int damage, boolean isSummon)
    {
        Npc spawnMonster = null;
        final Playable attacker = isSummon ? player.getServitors().values().stream().findFirst().orElse(player.getPet()) : player;
        //TODO fix the spawn 
        if(((Attackable) npc).getAggroList().size() == 1) {
            switch (npc.getId()) {
                case GAMLIN:
                        spawnMonster = addSpawn(GAMLIN, npc, false, 300000);
                        addAttackPlayerDesire(spawnMonster, attacker);
                        spawnMonster = addSpawn(GAMLIN, npc, false, 300000);
                        addAttackPlayerDesire(spawnMonster, attacker);
                        spawnMonster = addSpawn(GAMLIN, npc, false, 300000);
                        addAttackPlayerDesire(spawnMonster, attacker);
                    break;

                case LEOGUL:
                        spawnMonster = addSpawn(LEOGUL, npc, false, 300000);
                        addAttackPlayerDesire(spawnMonster, attacker);
                        spawnMonster = addSpawn(LEOGUL, npc, false, 300000);
                        addAttackPlayerDesire(spawnMonster, attacker);
                        spawnMonster = addSpawn(LEOGUL, npc, false, 300000);
                        addAttackPlayerDesire(spawnMonster, attacker);
                    break;
            };
        }

        return super.onAttack(npc, player, damage, isSummon);
    }

    public static AbstractNpcAI provider()
    {
        return new Scout();
    }
}
