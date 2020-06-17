/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ai.areas.TowerOfInsolence;

import ai.AbstractNpcAI;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.commons.util.Rnd;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.data.xml.impl.SpawnsData;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.model.ChanceLocation;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.Spawn;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Monster;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.skills.AbnormalVisualEffect;
import org.l2j.gameserver.model.spawns.NpcSpawnTemplate;
import org.l2j.gameserver.network.NpcStringId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ScheduledFuture;

/**
 * @author Thoss
 */
// TODO: Make spawn in template, 10 locations min / floor for each monster 20977 & 21081 (@pearlbear)
public class TowerOfInsolence extends AbstractNpcAI {
    private static final Logger LOGGER = LoggerFactory.getLogger(TowerOfInsolence.class);

    private final int LEVEL_MAX_DIFF = 9;
    private final int TIME_UNTIL_MOVE = 1800000;

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

    private ScheduledFuture<?> _scheduleTaskElmoreden;
    private ScheduledFuture<?> _scheduleTaskAmon;


    private TowerOfInsolence()
    {
        addSpawnId(ENERGY_OF_INSOLENCE_NPC_IDS);
        addKillId(ENERGY_OF_INSOLENCE_NPC_IDS);
        addKillId(UNIDENTIFIED_STONE_NPC_IDS);
        addKillId(ENERGY_OF_INSOLENCE_MINIONS);
    }

    private void makeInvul(Npc npc) {
        npc.getEffectList().startAbnormalVisualEffect(AbnormalVisualEffect.INVINCIBILITY);
        npc.setIsInvul(true);
    }

    private void makeMortal(Npc npc) {
        npc.getEffectList().stopAbnormalVisualEffect(AbnormalVisualEffect.INVINCIBILITY);
        npc.setIsInvul(false);
    }

    private void makeTalk(Npc npc, boolean spawning) {
        NpcStringId npcStringId = null;
        switch (npc.getId()) {
            case ELMOREDEN_LADY -> npcStringId = spawning ? NpcStringId.MY_SERVANTS_CAN_KEEP_ME_SAFE_I_HAVE_NOTHING_TO_FEAR : NpcStringId.CAN_T_DIE_IN_A_PLACE_LIKE_THIS;
            case POWER_ANGEL_AMON -> npcStringId = spawning ? NpcStringId.WHO_DARED_TO_ENTER_HERE : NpcStringId.HOW_DARE_YOU_INVADE_OUR_LAND_I_WONT_LEAVE_IT_THAT_EASY;
        }
        npc.broadcastSay(ChatType.NPC_SHOUT, npcStringId);
    }

    @Override
    public String onSpawn(Npc npc) {
        if(Util.contains(ENERGY_OF_INSOLENCE_NPC_IDS, npc.getId())) {
            makeTalk(npc, true);
            switch (npc.getId()) {
                case ELMOREDEN_LADY -> {
                    makeInvul(npc);
                    _scheduleTaskElmoreden = ThreadPool.schedule(new ScheduleAITask(npc, ELMOREDEN_LADY), TIME_UNTIL_MOVE);
                }
                case POWER_ANGEL_AMON -> _scheduleTaskAmon = ThreadPool.schedule(new ScheduleAITask(npc, POWER_ANGEL_AMON), TIME_UNTIL_MOVE);
            }
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
            makeTalk(npc, false);

            switch (npc.getId()) {
                case ELMOREDEN_LADY -> {
                    _scheduleTaskElmoreden.cancel(true);
                    _scheduleTaskElmoreden = null;
                    _scheduleTaskElmoreden = ThreadPool.schedule(new ScheduleAITask(null, ELMOREDEN_LADY), TIME_UNTIL_MOVE);
                }
                case POWER_ANGEL_AMON -> {
                    _scheduleTaskAmon.cancel(true);
                    _scheduleTaskAmon = null;
                    _scheduleTaskAmon = ThreadPool.schedule(new ScheduleAITask(null, POWER_ANGEL_AMON), TIME_UNTIL_MOVE);
                }
            }

            // We don't use droplist because we don't want several boosts to modify drop rate or count
            if((killer.getLevel() - npc.getLevel()) <= LEVEL_MAX_DIFF && Rnd.get(100) <= ENERGY_OF_INSOLENCE_DROP_RATE)
                npc.dropItem(killer, ENERGY_OF_INSOLENCE_ITEM_ID, ENERGY_OF_INSOLENCE_DROP_COUNT);
        }

        if(Util.contains(ENERGY_OF_INSOLENCE_MINIONS, npc.getId())) {
            if(payload != null && payload instanceof Monster) {
                final Monster leader = (Monster) payload;

                // If all minions are dead, turn master to mortal mode
                if(leader.getMinionList().getSpawnedMinions().size() == 0 && !leader.isDead())
                    makeMortal(leader);
            }
        }

        return super.onKill(npc, killer, isSummon, payload);
    }

    public class ScheduleAITask implements Runnable {

        private final Npc _npc;
        private final int _npcId;

        public ScheduleAITask(Npc npc, int npcId) {
            _npc = npc;
            _npcId = npcId;
        }

        @Override
        public void run() {

            if (_npc != null)
                _npc.deleteMe();

            try {
                final Spawn spawn = new Spawn(_npcId);
                final List<NpcSpawnTemplate> spawns = SpawnsData.getInstance().getNpcSpawns(npcSpawnTemplate -> npcSpawnTemplate.getId() == _npcId);
                final List<ChanceLocation> locations = spawns.get(0).getLocation();
                final Location location = locations.get(Rnd.get(0, locations.size() - 1));
                spawn.setLocation(location);
                spawn.doSpawn();
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    public static AbstractNpcAI provider()
    {
        return new TowerOfInsolence();
    }
}
