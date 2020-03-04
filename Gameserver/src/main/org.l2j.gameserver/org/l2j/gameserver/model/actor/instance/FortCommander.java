package org.l2j.gameserver.model.actor.instance;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.instancemanager.FortSiegeManager;
import org.l2j.gameserver.model.FortSiegeSpawn;
import org.l2j.gameserver.model.Spawn;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.util.MathUtil;

import java.util.Collection;

public class FortCommander extends Defender {

    private boolean _canTalk;

    public FortCommander(NpcTemplate template) {
        super(template);
        setInstanceType(InstanceType.L2FortCommanderInstance);
        _canTalk = true;
    }

    /**
     * Return True if a siege is in progress and the Creature attacker isn't a Defender.
     *
     * @param attacker The Creature that the L2CommanderInstance try to attack
     */
    @Override
    public boolean isAutoAttackable(Creature attacker) {
        if (!GameUtils.isPlayer(attacker)) {
            return false;
        }

        // Attackable during siege by all except defenders
        return ((getFort() != null) && (getFort().getId() > 0) && getFort().getSiege().isInProgress() && !getFort().getSiege().checkIsDefender(attacker.getClan()));
    }

    @Override
    public void addDamageHate(Creature attacker, int damage, int aggro) {
        if (attacker == null) {
            return;
        }

        if (!(attacker instanceof FortCommander)) {
            super.addDamageHate(attacker, damage, aggro);
        }
    }

    @Override
    public boolean doDie(Creature killer) {
        if (!super.doDie(killer)) {
            return false;
        }

        if (getFort().getSiege().isInProgress()) {
            getFort().getSiege().killedCommander(this);

        }

        return true;
    }

    /**
     * This method forces guard to return to home location previously set
     */
    @Override
    public void returnHome() {
        if (!MathUtil.isInsideRadius2D(this, getSpawn(), 200)) {
            setisReturningToSpawnPoint(true);
            clearAggroList();

            if (hasAI()) {
                getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, getSpawn().getLocation());
            }
        }
    }

    @Override
    public final void addDamage(Creature attacker, int damage, Skill skill) {
        final Spawn spawn = getSpawn();
        if ((spawn != null) && canTalk()) {
            final Collection<FortSiegeSpawn> commanders = FortSiegeManager.getInstance().getCommanderSpawnList(getFort().getId());
            for (FortSiegeSpawn spawn2 : commanders) {
                if (spawn2.getId() == spawn.getId()) {
                    NpcStringId npcString = null;
                    switch (spawn2.getMessageId()) {
                        case 1: {
                            npcString = NpcStringId.ATTACKING_THE_ENEMY_S_REINFORCEMENTS_IS_NECESSARY_TIME_TO_DIE;
                            break;
                        }
                        case 2: {
                            if (GameUtils.isSummon(attacker)) {
                                attacker = ((Summon) attacker).getOwner();
                            }
                            npcString = NpcStringId.EVERYONE_CONCENTRATE_YOUR_ATTACKS_ON_S1_SHOW_THE_ENEMY_YOUR_RESOLVE;
                            break;
                        }
                        case 3: {
                            npcString = NpcStringId.FIRE_SPIRIT_UNLEASH_YOUR_POWER_BURN_THE_ENEMY;
                            break;
                        }
                    }
                    if (npcString != null) {
                        broadcastSay(ChatType.NPC_SHOUT, npcString, npcString.getParamCount() == 1 ? attacker.getName() : null);
                        setCanTalk(false);
                        ThreadPool.schedule(new ScheduleTalkTask(), 10000);
                    }
                }
            }
        }
        super.addDamage(attacker, damage, skill);
    }

    void setCanTalk(boolean val) {
        _canTalk = val;
    }

    private boolean canTalk() {
        return _canTalk;
    }

    @Override
    public boolean hasRandomAnimation() {
        return false;
    }

    private class ScheduleTalkTask implements Runnable {

        public ScheduleTalkTask() {
        }

        @Override
        public void run() {
            setCanTalk(true);
        }
    }
}
