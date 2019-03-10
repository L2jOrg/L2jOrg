package org.l2j.gameserver.mobius.gameserver.model.actor.instance;

import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.mobius.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.mobius.gameserver.enums.ChatType;
import org.l2j.gameserver.mobius.gameserver.enums.InstanceType;
import org.l2j.gameserver.mobius.gameserver.instancemanager.FortSiegeManager;
import org.l2j.gameserver.mobius.gameserver.model.FortSiegeSpawn;
import org.l2j.gameserver.mobius.gameserver.model.L2Spawn;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Character;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Summon;
import org.l2j.gameserver.mobius.gameserver.model.actor.templates.L2NpcTemplate;
import org.l2j.gameserver.mobius.gameserver.model.skills.Skill;
import org.l2j.gameserver.mobius.gameserver.network.NpcStringId;

import java.util.List;

public class L2FortCommanderInstance extends L2DefenderInstance {

    private boolean _canTalk;

    public L2FortCommanderInstance(L2NpcTemplate template) {
        super(template);
        setInstanceType(InstanceType.L2FortCommanderInstance);
        _canTalk = true;
    }

    /**
     * Return True if a siege is in progress and the L2Character attacker isn't a Defender.
     *
     * @param attacker The L2Character that the L2CommanderInstance try to attack
     */
    @Override
    public boolean isAutoAttackable(L2Character attacker) {
        if ((attacker == null) || !attacker.isPlayer()) {
            return false;
        }

        // Attackable during siege by all except defenders
        return ((getFort() != null) && (getFort().getResidenceId() > 0) && getFort().getSiege().isInProgress() && !getFort().getSiege().checkIsDefender(attacker.getClan()));
    }

    @Override
    public void addDamageHate(L2Character attacker, int damage, int aggro) {
        if (attacker == null) {
            return;
        }

        if (!(attacker instanceof L2FortCommanderInstance)) {
            super.addDamageHate(attacker, damage, aggro);
        }
    }

    @Override
    public boolean doDie(L2Character killer) {
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
        if (!isInsideRadius2D(getSpawn(), 200)) {
            setisReturningToSpawnPoint(true);
            clearAggroList();

            if (hasAI()) {
                getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, getSpawn().getLocation());
            }
        }
    }

    @Override
    public final void addDamage(L2Character attacker, int damage, Skill skill) {
        final L2Spawn spawn = getSpawn();
        if ((spawn != null) && canTalk()) {
            final List<FortSiegeSpawn> commanders = FortSiegeManager.getInstance().getCommanderSpawnList(getFort().getResidenceId());
            for (FortSiegeSpawn spawn2 : commanders) {
                if (spawn2.getId() == spawn.getId()) {
                    NpcStringId npcString = null;
                    switch (spawn2.getMessageId()) {
                        case 1: {
                            npcString = NpcStringId.ATTACKING_THE_ENEMY_S_REINFORCEMENTS_IS_NECESSARY_TIME_TO_DIE;
                            break;
                        }
                        case 2: {
                            if (attacker.isSummon()) {
                                attacker = ((L2Summon) attacker).getOwner();
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
                        ThreadPoolManager.getInstance().schedule(new ScheduleTalkTask(), 10000);
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
