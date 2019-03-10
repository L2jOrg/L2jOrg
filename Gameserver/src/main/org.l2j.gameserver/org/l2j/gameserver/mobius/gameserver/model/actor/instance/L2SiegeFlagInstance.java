package org.l2j.gameserver.mobius.gameserver.model.actor.instance;

import org.l2j.gameserver.mobius.gameserver.enums.InstanceType;
import org.l2j.gameserver.mobius.gameserver.instancemanager.FortSiegeManager;
import org.l2j.gameserver.mobius.gameserver.instancemanager.SiegeManager;
import org.l2j.gameserver.mobius.gameserver.model.L2Clan;
import org.l2j.gameserver.mobius.gameserver.model.L2SiegeClan;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Character;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.mobius.gameserver.model.actor.status.SiegeFlagStatus;
import org.l2j.gameserver.mobius.gameserver.model.actor.templates.L2NpcTemplate;
import org.l2j.gameserver.mobius.gameserver.model.entity.Siegable;
import org.l2j.gameserver.mobius.gameserver.model.skills.Skill;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.SystemMessage;

public class L2SiegeFlagInstance extends L2Npc {
    private final L2Clan _clan;
    private final boolean _isAdvanced;
    private Siegable _siege;
    private boolean _canTalk;

    public L2SiegeFlagInstance(L2PcInstance player, L2NpcTemplate template, boolean advanced) {
        super(template);
        setInstanceType(InstanceType.L2SiegeFlagInstance);

        _clan = player.getClan();
        _canTalk = true;
        _siege = SiegeManager.getInstance().getSiege(player.getX(), player.getY(), player.getZ());
        if (_siege == null) {
            _siege = FortSiegeManager.getInstance().getSiege(player.getX(), player.getY(), player.getZ());
        }
        if ((_clan == null) || (_siege == null)) {
            throw new NullPointerException(getClass().getSimpleName() + ": Initialization failed.");
        }

        final L2SiegeClan sc = _siege.getAttackerClan(_clan);
        if (sc == null) {
            throw new NullPointerException(getClass().getSimpleName() + ": Cannot find siege clan.");
        }

        sc.addFlag(this);
        _isAdvanced = advanced;
        getStatus();
        setIsInvul(false);
    }

    @Override
    public boolean canBeAttacked() {
        return !isInvul();
    }

    @Override
    public boolean isAutoAttackable(L2Character attacker) {
        return !isInvul();
    }

    @Override
    public boolean doDie(L2Character killer) {
        if (!super.doDie(killer)) {
            return false;
        }
        if ((_siege != null) && (_clan != null)) {
            final L2SiegeClan sc = _siege.getAttackerClan(_clan);
            if (sc != null) {
                sc.removeFlag(this);
            }
        }
        return true;
    }

    @Override
    public void onForcedAttack(L2PcInstance player) {
        onAction(player);
    }

    @Override
    public void onAction(L2PcInstance player, boolean interact) {
        if ((player == null) || !canTarget(player)) {
            return;
        }

        // Check if the L2PcInstance already target the L2NpcInstance
        if (this != player.getTarget()) {
            // Set the target of the L2PcInstance player
            player.setTarget(this);
        } else if (interact) {
            if (isAutoAttackable(player) && (Math.abs(player.getZ() - getZ()) < 100)) {
                player.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, this);
            } else {
                // Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet
                player.sendPacket(ActionFailed.STATIC_PACKET);
            }
        }
    }

    public boolean isAdvancedHeadquarter() {
        return _isAdvanced;
    }

    @Override
    public SiegeFlagStatus getStatus() {
        return (SiegeFlagStatus) super.getStatus();
    }

    @Override
    public void initCharStatus() {
        setStatus(new SiegeFlagStatus(this));
    }

    @Override
    public void reduceCurrentHp(double damage, L2Character attacker, Skill skill) {
        super.reduceCurrentHp(damage, attacker, skill);
        if (canTalk()) {
            if (((getCastle() != null) && getCastle().getSiege().isInProgress()) || ((getFort() != null) && getFort().getSiege().isInProgress())) {
                if (_clan != null) {
                    // send warning to owners of headquarters that theirs base is under attack
                    _clan.broadcastToOnlineMembers(SystemMessage.getSystemMessage(SystemMessageId.SIEGE_CAMP_IS_UNDER_ATTACK));
                    setCanTalk(false);
                    ThreadPoolManager.getInstance().schedule(new ScheduleTalkTask(), 20000);
                }
            }
        }
    }

    void setCanTalk(boolean val) {
        _canTalk = val;
    }

    private boolean canTalk() {
        return _canTalk;
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
