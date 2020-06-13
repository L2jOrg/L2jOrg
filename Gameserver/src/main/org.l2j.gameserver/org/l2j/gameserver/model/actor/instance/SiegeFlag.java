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
package org.l2j.gameserver.model.actor.instance;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.instancemanager.SiegeManager;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.DamageInfo.DamageType;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.status.SiegeFlagStatus;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.entity.Siegable;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

import static java.util.Objects.nonNull;

public class SiegeFlag extends Npc {
    private final Clan _clan;
    private final boolean _isAdvanced;
    private Siegable _siege;
    private boolean _canTalk;

    public SiegeFlag(Player player, NpcTemplate template, boolean advanced) {
        super(template);
        setInstanceType(InstanceType.L2SiegeFlagInstance);

        _clan = player.getClan();
        _canTalk = true;
        _siege = SiegeManager.getInstance().getSiege(player);
        if ((_clan == null) || (_siege == null)) {
            throw new NullPointerException(getClass().getSimpleName() + ": Initialization failed.");
        }

        final var sc = _siege.getAttackerClan(_clan);
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
    public boolean isAutoAttackable(Creature attacker) {
        return !isInvul();
    }

    @Override
    public boolean doDie(Creature killer) {
        if (!super.doDie(killer)) {
            return false;
        }
        if ((_siege != null) && (_clan != null)) {
            final var sc = _siege.getAttackerClan(_clan);
            if (sc != null) {
                sc.removeFlag(this);
            }
        }
        return true;
    }

    @Override
    public void onForcedAttack(Player player) {
        onAction(player);
    }

    @Override
    public void onAction(Player player, boolean interact) {
        if ((player == null) || !canTarget(player)) {
            return;
        }

        // Check if the Player already target the Folk
        if (this != player.getTarget()) {
            // Set the target of the Player player
            player.setTarget(this);
        } else if (interact) {
            if (isAutoAttackable(player) && (Math.abs(player.getZ() - getZ()) < 100)) {
                player.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, this);
            } else {
                // Send a Server->Client ActionFailed to the Player in order to avoid that the client wait another packet
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
    public void reduceCurrentHp(double damage, Creature attacker, Skill skill, DamageType damageType) {
        super.reduceCurrentHp(damage, attacker, skill, damageType);
        if (canTalk()) {
            if (nonNull(getCastle()) && getCastle().getSiege().isInProgress()) {
                if (_clan != null) {
                    // send warning to owners of headquarters that theirs base is under attack
                    _clan.broadcastToOnlineMembers(SystemMessage.getSystemMessage(SystemMessageId.SIEGE_CAMP_IS_UNDER_ATTACK));
                    setCanTalk(false);
                    ThreadPool.schedule(new ScheduleTalkTask(), 20000);
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
