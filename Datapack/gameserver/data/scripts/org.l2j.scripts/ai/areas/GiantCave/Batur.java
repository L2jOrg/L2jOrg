package ai.areas.GiantCave;

import ai.AbstractNpcAI;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.data.xml.impl.NpcData;
import org.l2j.gameserver.data.xml.impl.SpawnsData;
import org.l2j.gameserver.model.ChanceLocation;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Monster;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.spawns.NpcSpawnTemplate;

import java.util.List;
import java.util.concurrent.Future;

/**
 * Giant's Cave - Lower Part
 * Every 15 minutes in a random place of the zone Batur appears.
 * Batur doesn't attack first, but if you attack Batur, you need to defeat it within a minute, or it will disappear.
 * Batur has strong values of P. Atk., M. Atk. and M. Def, so it's pretty difficult to defeat it.
 *
 */
public class Batur extends AbstractNpcAI
{
    private final int TIME_TO_LIVE = 10000;
    private final int BATUR_ID = 24020;
    private final long RESPAWN_DELAY = 30000; // 15 min

    private static Npc BATUR;

    private Batur()
    {
        addAttackId(BATUR_ID);
        addKillId(BATUR_ID);
        startQuestTimer("BATUR_SPAWN_THREAD", 30000, null, null);
    }


    @Override
    public String onAdvEvent(String event, Npc npc, Player player) {
        if (event.equals("BATUR_SPAWN_THREAD")) {
            final List<NpcSpawnTemplate> spawns = SpawnsData.getInstance().getNpcSpawns(npcSpawnTemplate -> npcSpawnTemplate.getId() == BATUR_ID);
            final List<ChanceLocation> locations = spawns.get(0).getLocation();
            // FIXME: Getting NPE on picking location, so NPE keep going everywhere
            final Location location = locations.get(Rnd.get(0, locations.size() - 1));
            BATUR = addSpawn(BATUR_ID, location);
        } else if (event.equals("BATUR_DESPAWN_THREAD")) {
            BATUR.scheduleDespawn(0);
            startQuestTimer("BATUR_SPAWN_THREAD", RESPAWN_DELAY, null, null);
        }
        return super.onAdvEvent(event, npc, player);
    }


    @Override
    public String onAttack(Npc npc, Player attacker, int damage, boolean isSummon) {
        if(npc.getId() == BATUR_ID && getQuestTimer("BATUR_DESPAWN_THREAD", null, null) == null) {
            startQuestTimer("BATUR_DESPAWN_THREAD", TIME_TO_LIVE, null, null);
        }
        return super.onAttack(npc, attacker, damage, isSummon);
    }


    @Override
    public String onKill(Npc npc, Player killer, boolean isSummon) {
        if(npc.getId() == BATUR_ID) {
            startQuestTimer("BATUR_SPAWN_THREAD", RESPAWN_DELAY, null, null);
        }
        return super.onKill(npc, killer, isSummon);
    }


    public static AbstractNpcAI provider()
    {
        return new Batur();
    }

}