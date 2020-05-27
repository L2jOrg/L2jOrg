package ai.areas.Varkas;

import ai.AbstractNpcAI;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.impl.SpawnsData;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.skills.AbnormalVisualEffect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class Althars extends AbstractNpcAI {
    private static Logger LOGGER = LoggerFactory.getLogger(Althars.class);

    private final int _DELAY = 30000; // Loop time for checking Althars state

    private final int invulSkillId = 0;
    private boolean[] althars = new boolean[12];


    private Althars() {
        for(int i = 0; i < althars.length; i++) {
            althars[i] = false;
        }
        startQuestTimer("ALTHARS_TIMER", _DELAY, null,null);
    }

    @Override
    public String onAdvEvent(String event, Npc npc, Player player) {
        LOGGER.info("running task {}", event);
        if("ALTHARS_TIMER".equals(event)) {
            for(int i = 0; i < Config.ALTHARS_MAX_ACTIVE - getAltharsActiveCount() ; i++) {
                if (Rnd.get(100) < Config.ALTHARS_ACTIVATE_CHANCE_RATE) {
                    int altharsIndex = getAltharsIndex(Rnd.get(12 - getAltharsActiveCount()));
                    spawnMonsters(altharsIndex);
                    int AltharsDurationCycle = Rnd.get(Config.ALTHARS_MIN_DURATION_CYCLE, Config.ALTHARS_MAX_DURATION_CYCLE);
                    LOGGER.info("starting althars_" + altharsIndex + " for {} sec", AltharsDurationCycle);
                    startQuestTimer("STOP_ALTHARS_" + altharsIndex, AltharsDurationCycle, null,null);
                }
            }
            startQuestTimer("ALTHARS_TIMER", _DELAY, null,null);
        } else if(event.startsWith("STOP_ALTHARS_")) {
            unSpawnMonsters(Integer.parseInt(event.substring(event.length() - 1)));
        }

        return super.onAdvEvent(event, npc, player);
    }

    private int getAltharsActiveCount() {
        int count = 0;
        for(int i = 0; i < althars.length; i++) {
            if (althars[i] == true) {
                count++;
            }
        }
        return count;
    }

    private int getAltharsIndex(int rndIndex) {
        int virtualIndex = 0;
        for(int realIndex = 0; realIndex < althars.length; realIndex++) {
            if (althars[realIndex] == true) {
                continue;
            } else {
                virtualIndex++;
            }
            if (rndIndex == virtualIndex) {
                return realIndex;
            }
        }
        LOGGER.error("No Althars index found. " + Arrays.toString(althars));
        return -1;
    }

    private void spawnMonsters(int altharIndex) {
        //TODO: activate the glow of althars
        // Creature npc = null;
        // npc.getEffectList().startAbnormalVisualEffect(AbnormalVisualEffect.INVINCIBILITY);
        LOGGER.info("spawning mobs for althars_{}", altharIndex);
        SpawnsData.getInstance().spawnByName("althar_" + altharIndex);
        althars[altharIndex] = true;
    }

    private void unSpawnMonsters(int altharIndex) {
        //TODO: deactivate the glow of althars
        // Creature npc = null;
        // npc.getEffectList().stopAbnormalVisualEffect(AbnormalVisualEffect.INVINCIBILITY);
        LOGGER.info("unspawning mobs for althars_{}", altharIndex);
        SpawnsData.getInstance().deSpawnByName("althar_" + altharIndex);
        althars[altharIndex] = false;
    }

    public static AbstractNpcAI provider() {
        return new Althars();
    }
}
