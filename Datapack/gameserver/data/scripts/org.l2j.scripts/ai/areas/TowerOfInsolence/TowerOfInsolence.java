package ai.areas.TowerOfInsolence;

import ai.AbstractNpcAI;
import org.l2j.commons.util.Rnd;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.data.xml.impl.SpawnsData;
import org.l2j.gameserver.model.ChanceLocation;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Monster;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.skills.AbnormalVisualEffect;
import org.l2j.gameserver.model.spawns.NpcSpawnTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class TowerOfInsolence extends AbstractNpcAI {
    private static final Logger LOGGER = LoggerFactory.getLogger(TowerOfInsolence.class);

    private final int LEVEL_MAX_DIFF = 9;

    private final int ENERGY_OF_INSOLENCE_DROP_RATE = 70;
    private final int ENERGY_OF_INSOLENCE_ITEM_ID = 49685;
    private final int ENERGY_OF_INSOLENCE_DROP_COUNT = 1;

    private final int UNIDENTIFIED_STONE_DROP_RATE = 4;
    private final int UNIDENTIFIED_STONE_ITEM_ID = 49766;


    private final int[] ENERGY_OF_INSOLENCE_MINIONS = {
            21073,
            21078,
            21079,
            21082,
            21083
    };

    private final int[] ENERGY_OF_INSOLENCE_NPC_IDS = {
            20977,
            21081
    };

    private final int[] UNIDENTIFIED_STONE_NPC_IDS = {
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


    private TowerOfInsolence()
    {
        addKillId(ENERGY_OF_INSOLENCE_NPC_IDS);
        addKillId(UNIDENTIFIED_STONE_NPC_IDS);
        addKillId(ENERGY_OF_INSOLENCE_MINIONS);

        startQuestTimer("ELMOREDEN_AI_THREAD", 30000, null, null);
    }

    @Override
    public String onAdvEvent(String event, Npc npc, Player player) {
        if (event.equals("ELMOREDEN_AI_THREAD")) {
            for(int npcId : ENERGY_OF_INSOLENCE_NPC_IDS)
                spawnInvulNpc(npcId);
        }
        return super.onAdvEvent(event, npc, player);
    }

    private void spawnInvulNpc(int npcId) {
        final List<NpcSpawnTemplate> spawn = SpawnsData.getInstance().getNpcSpawns(npcSpawnTemplate -> npcSpawnTemplate.getId() == npcId);
        final List<ChanceLocation> locations = spawn.get(0).getLocation();
        final Location location = locations.get(Rnd.get(0, locations.size() - 1));
        final Npc npc = addSpawn(npcId, location);
        makeInvul(npc);
    }

    private void makeInvul(Npc npc) {
        npc.getEffectList().startAbnormalVisualEffect(AbnormalVisualEffect.INVINCIBILITY);
        npc.setIsInvul(true);
    }

    private void makeMortal(Npc npc) {
        npc.getEffectList().stopAbnormalVisualEffect(AbnormalVisualEffect.INVINCIBILITY);
        npc.setIsInvul(false);
    }

    @Override
    public String onKill(Npc npc, Player killer, boolean isSummon, Object payload) {
        if(Util.contains(UNIDENTIFIED_STONE_NPC_IDS, npc.getId())) {
            // We don't use droplist because we don't want several boosts to modify drop rate or count
            if((killer.getLevel() - npc.getLevel()) <= LEVEL_MAX_DIFF && Rnd.get(100) <= UNIDENTIFIED_STONE_DROP_RATE)
                npc.dropItem(killer, UNIDENTIFIED_STONE_ITEM_ID, 1);
        } else if(Util.contains(ENERGY_OF_INSOLENCE_NPC_IDS, npc.getId())) {
            // We don't use droplist because we don't want several boosts to modify drop rate or count
            if((killer.getLevel() - npc.getLevel()) <= LEVEL_MAX_DIFF && Rnd.get(100) <= ENERGY_OF_INSOLENCE_DROP_RATE)
                npc.dropItem(killer, ENERGY_OF_INSOLENCE_ITEM_ID, ENERGY_OF_INSOLENCE_DROP_COUNT);
        } else if(Util.contains(ENERGY_OF_INSOLENCE_MINIONS, npc.getId())) {
            if(payload != null && payload instanceof Monster) {
                final Monster leader = (Monster) payload;

                // If all minions are dead, turn master to mortal mode
                if(leader.getMinionList().getSpawnedMinions().size() == 0) {
                    makeMortal(leader);
                }
            }
        }

        return super.onKill(npc, killer, isSummon, payload);
    }

    public static AbstractNpcAI provider()
    {
        return new TowerOfInsolence();
    }
}
