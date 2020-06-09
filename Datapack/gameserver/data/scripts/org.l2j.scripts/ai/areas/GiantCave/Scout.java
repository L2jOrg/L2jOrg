package ai.areas.GiantCave;


import ai.AbstractNpcAI;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.instance.Monster;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.MinionHolder;
import org.l2j.gameserver.model.quest.QuestState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.l2j.gameserver.util.GameUtils.isMonster;

public class Scout extends AbstractNpcAI {
    private static Logger LOGGER = LoggerFactory.getLogger(Scout.class);


    private static final int SPAWN_DELAY = 10000; // milliseconds
    private static final int DESPAWN_MINION_DELAY = 300000; // milliseconds
    private static final int GAMLIN = 20651;
    private static final int LEOGUL = 20652;

    private Scout()
    {
        addAttackId(GAMLIN, LEOGUL);
    }

    @Override
    public String onAdvEvent(String event, Npc npc, Player player) {
        if (event.equals("GC_SCOUT_EVENT_AI")) {
            final Playable pAttacker = player.getServitors().size() > 0 ? player.getServitors().values().stream().findFirst().orElse(player.getPet()) : player;
            final Monster monster = (Monster) npc;

            if (monster != null && !monster.isDead() && !monster.isTeleporting() && !monster.hasMinions())
                for (MinionHolder is : npc.getParameters().getMinionList("Privates")) {
                    monster.getMinionList().spawnMinions(monster.getParameters().getMinionList("Privates"));
                    monster.getMinionList().getSpawnedMinions().forEach(minion -> {
                        minion.scheduleDespawn(DESPAWN_MINION_DELAY);
                        addAttackPlayerDesire(minion, pAttacker);
                    });
                }
        }

        return super.onAdvEvent(event, npc, player);
    }

    @Override
    public String onAttack(Npc npc, Player attacker, int damage, boolean isSummon)
    {
        if (isMonster(npc))
        {
            final Monster monster = (Monster) npc;

            if (!monster.isTeleporting() && !monster.hasMinions() && getQuestTimer("GC_SCOUT_EVENT_AI", npc, attacker) == null)
                startQuestTimer("GC_SCOUT_EVENT_AI", SPAWN_DELAY, npc, attacker);
        }

        return super.onAttack(npc, attacker, damage, isSummon);
    }

    public static AbstractNpcAI provider()
    {
        return new Scout();
    }
}
