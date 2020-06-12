package ai.areas.TowerOfInsolence;

import ai.AbstractNpcAI;
import org.l2j.commons.util.Rnd;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.model.Spawn;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Monster;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.quest.QuestTimer;
import org.l2j.gameserver.model.skills.AbnormalVisualEffect;
import org.l2j.gameserver.network.NpcStringId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TowerOfInsolence extends AbstractNpcAI {
    private static final Logger LOGGER = LoggerFactory.getLogger(TowerOfInsolence.class);

    private final int LEVEL_MAX_DIFF = 9;
    private final int TIME_UNTIL_MOVE = 60000;

    private final int ELMOREDEN_LADY = 20977;
    private final int POWER_ANGEL_AMON = 21081;
    private final int ENERGY_OF_INSOLENCE_DROP_RATE = 70;
    private final int ENERGY_OF_INSOLENCE_ITEM_ID = 49685;
    private final int ENERGY_OF_INSOLENCE_DROP_COUNT = 1;

    private final int UNIDENTIFIED_STONE_DROP_RATE = 4;
    private final int UNIDENTIFIED_STONE_ITEM_ID = 49766;

    private final int[] ENERGY_OF_INSOLENCE_NPC_IDS = {ELMOREDEN_LADY, POWER_ANGEL_AMON};

    private final int[] ENERGY_OF_INSOLENCE_MINIONS = {
            21073,
            21078,
            21079,
            21082,
            21083
    };

    private final int[] UNIDENTIFIED_STONE_NPC_IDS = {
            20980,
            20981,
            20982,
            20983,
            20984,
            20985,
            21074,
            21075,
            21076,
            21077,
            21080,
            21980,
            21981
    };


    private TowerOfInsolence()
    {
        addSpawnId(ENERGY_OF_INSOLENCE_NPC_IDS);
        addKillId(ENERGY_OF_INSOLENCE_NPC_IDS);
        addKillId(UNIDENTIFIED_STONE_NPC_IDS);
        addKillId(ENERGY_OF_INSOLENCE_MINIONS);
    }

    @Override
    public String onAdvEvent(String event, Npc npc, Player player) {
        if (event.equals("ENERGY_OF_INSOLENCE_AI_THREAD")) {
            npc.deleteMe();
            final Spawn spawn = npc.getSpawn();
            spawn.respawnNpc(npc);
        }
        return super.onAdvEvent(event, npc, player);
    }

    private void makeInvul(Npc npc) {
        npc.getEffectList().startAbnormalVisualEffect(AbnormalVisualEffect.ULTIMATE_DEFENCE);
        npc.setIsInvul(true);
    }

    private void makeMortal(Npc npc) {
        npc.getEffectList().stopAbnormalVisualEffect(AbnormalVisualEffect.ULTIMATE_DEFENCE);
        npc.setIsInvul(false);
    }

    @Override
    public String onSpawn(Npc npc) {
        if(Util.contains(ENERGY_OF_INSOLENCE_NPC_IDS, npc.getId())) {
            switch (npc.getId()) {
                case ELMOREDEN_LADY -> {
                    npc.broadcastSay(ChatType.NPC_SHOUT, NpcStringId.MY_SERVANTS_CAN_KEEP_ME_SAFE_I_HAVE_NOTHING_TO_FEAR);
                    makeInvul(npc);
                }
                case POWER_ANGEL_AMON -> npc.broadcastSay(ChatType.NPC_SHOUT, NpcStringId.WHO_DARED_TO_ENTER_HERE);
            }
            startQuestTimer("ENERGY_OF_INSOLENCE_AI_THREAD", TIME_UNTIL_MOVE, npc, null);
        }
        return super.onSpawn(npc);
    }

    @Override
    public String onKill(Npc npc, Player killer, boolean isSummon, Object payload) {
        if(Util.contains(UNIDENTIFIED_STONE_NPC_IDS, npc.getId())) {
            // We don't use droplist because we don't want several boosts to modify drop rate or count
            if((killer.getLevel() - npc.getLevel()) <= LEVEL_MAX_DIFF && Rnd.get(100) <= UNIDENTIFIED_STONE_DROP_RATE)
                npc.dropItem(killer, UNIDENTIFIED_STONE_ITEM_ID, 1);
        }

        if(Util.contains(ENERGY_OF_INSOLENCE_NPC_IDS, npc.getId())) {
            switch (npc.getId()) {
                case ELMOREDEN_LADY -> npc.broadcastSay(ChatType.NPC_SHOUT, NpcStringId.CAN_T_DIE_IN_A_PLACE_LIKE_THIS);

                case POWER_ANGEL_AMON -> npc.broadcastSay(ChatType.NPC_SHOUT, NpcStringId.HOW_DARE_YOU_INVADE_OUR_LAND_I_WONT_LEAVE_IT_THAT_EASY);
            }
            final QuestTimer questTimer = getQuestTimer("ENERGY_OF_INSOLENCE_AI_THREAD", npc, null);
            if(questTimer != null)
                removeQuestTimer(getQuestTimer("ENERGY_OF_INSOLENCE_AI_THREAD", npc, null));
            // We don't use droplist because we don't want several boosts to modify drop rate or count
            if((killer.getLevel() - npc.getLevel()) <= LEVEL_MAX_DIFF && Rnd.get(100) <= ENERGY_OF_INSOLENCE_DROP_RATE)
                npc.dropItem(killer, ENERGY_OF_INSOLENCE_ITEM_ID, ENERGY_OF_INSOLENCE_DROP_COUNT);
        }

        if(Util.contains(ENERGY_OF_INSOLENCE_MINIONS, npc.getId())) {
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
