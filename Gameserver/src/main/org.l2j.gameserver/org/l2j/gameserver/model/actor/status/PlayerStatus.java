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
package org.l2j.gameserver.model.actor.status;

import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.instancemanager.DuelManager;
import org.l2j.gameserver.model.DamageInfo.DamageType;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.stat.PlayerStats;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.entity.Duel;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

import static java.util.Objects.nonNull;
import static org.l2j.commons.util.Util.doIfNonNull;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;
import static org.l2j.gameserver.util.GameUtils.isPlayable;
import static org.l2j.gameserver.util.MathUtil.isInsideRadius3D;

public class PlayerStatus extends PlayableStatus {

    private double currentCp = 0;

    public PlayerStatus(Player player) {
        super(player);
    }

    @Override
    public final void reduceCp(int value) {
        setCurrentCp(Math.max(0, currentCp - value));
    }

    @Override
    public final void reduceHp(double value, Creature attacker) {
        reduceHp(value, attacker, true, false, false, false);
    }

    @Override
    public final void reduceHp(double value, Creature attacker, boolean awake, boolean isDOT, boolean isHPConsumption) {
        reduceHp(value, attacker, awake, isDOT, isHPConsumption, false);
    }

    public final void reduceHp(double value, Creature attacker, boolean awake, boolean isDOT, boolean isHPConsumption, boolean ignoreCP) {
        final var me = getOwner();

        if (me.isDead()) {
            return;
        }

        if (me.isHpBlocked() && !(isDOT || isHPConsumption)) {
            return;
        }

        if (me.isAffected(EffectFlag.DUELIST_FURY) && !attacker.isAffected(EffectFlag.FACEOFF)) {
            return;
        }

        if (!isHPConsumption) {
            if (awake) {
                me.stopEffectsOnDamage();
            }

            if (me.isCrafting() || me.isInStoreMode()) {
                me.setPrivateStoreType(PrivateStoreType.NONE);
                me.standUp();
                me.broadcastUserInfo();
            } else if (me.isSitting()) {
                me.standUp();
            }

            if (!isDOT) {
                if (Formulas.calcStunBreak(me)) {
                    me.stopStunning(true);
                }
                if (Formulas.calcRealTargetBreak()) {
                    me.getEffectList().stopEffects(AbnormalType.REAL_TARGET);
                }
            }
        }

        double fullValue = value;
        double tDmg = 0;
        double mpDam;

        if (nonNull(attacker) && (attacker != me)) {
            final Player attackerPlayer = attacker.getActingPlayer();

            if (attackerPlayer != null) {
                if (attackerPlayer.isGM() && !attackerPlayer.getAccessLevel().canGiveDamage()) {
                    return;
                }

                if (me.isInDuel()) {
                    if (me.getDuelState() == Duel.DUELSTATE_DEAD || me.getDuelState() == Duel.DUELSTATE_WINNER) {
                        return;
                    }

                    // cancel duel if player got hit by another player, that is not part of the duel
                    if (attackerPlayer.getDuelId() != me.getDuelId()) {
                        me.setDuelState(Duel.DUELSTATE_INTERRUPTED);
                    }
                }
            }

            // Check and calculate transfered damage
            final Summon summon = me.getFirstServitor();
            if (nonNull(summon) && isInsideRadius3D(me, summon, 1000)) {
                tDmg = value * me.getStats().getValue(Stat.TRANSFER_DAMAGE_SUMMON_PERCENT, 0) / 100;

                // Only transfer dmg up to current HP, it should not be killed
                tDmg = Math.min(summon.getCurrentHp() - 1, tDmg);
                if (tDmg > 0) {
                    summon.reduceCurrentHp(tDmg, attacker, null, DamageType.OTHER);
                    value -= tDmg;
                    fullValue = value; // reduce the announced value here as player will get a message about summon damage
                }
            }

            mpDam = value * me.getStats().getValue(Stat.MANA_SHIELD_PERCENT, 0) / 100;

            if (mpDam > 0) {
                mpDam = value - mpDam;
                if (mpDam > me.getCurrentMp()) {
                    me.sendPacket(SystemMessageId.MP_BECAME_0_AND_THE_ARCANE_SHIELD_IS_DISAPPEARING);
                    me.stopSkillEffects(true, 1556);
                    value = mpDam - me.getCurrentMp();
                    me.setCurrentMp(0);
                } else {
                    me.reduceCurrentMp(mpDam);
                    me.sendPacket(getSystemMessage(SystemMessageId.ARCANE_SHIELD_DECREASED_YOUR_MP_BY_S1_INSTEAD_OF_HP).addInt((int) mpDam));
                    return;
                }
            }

            final Player caster = me.getTransferingDamageTo();
            if (nonNull(caster) && !caster.isDead() && (me != caster) &&  nonNull(me.getParty()) && me.getParty().contains(caster) && isInsideRadius3D(me, caster, 1000)) {

                double transferDmg = value * me.getStats().getValue(Stat.TRANSFER_DAMAGE_TO_PLAYER, 0) / 100;
                transferDmg = Math.min(caster.getCurrentHp() - 1, transferDmg);
                if (transferDmg > 0) {

                    int membersInRange = 0;
                    for (Player member : caster.getParty().getMembers()) {
                        if ((member != caster) && isInsideRadius3D(member, caster, 1000)) {
                            membersInRange++;
                        }
                    }

                    if ((isPlayable(attacker)) && (caster.getCurrentCp() > 0)) {
                        if (caster.getCurrentCp() > transferDmg) {
                            caster.getStatus().reduceCp((int) transferDmg);
                        } else {
                            transferDmg = transferDmg - caster.getCurrentCp();
                            caster.getStatus().reduceCp((int) caster.getCurrentCp());
                        }
                    }

                    if (membersInRange > 0) {
                        caster.reduceCurrentHp(transferDmg / membersInRange, attacker, null, DamageType.TRANSFERED_DAMAGE);
                        value -= transferDmg;
                        fullValue = value;
                    }
                }
            }

            if (!ignoreCP && isPlayable(attacker)) {
                if (currentCp >= value) {
                    setCurrentCp(currentCp - value); // Set Cp to diff of Cp vs value
                    value = 0; // No need to subtract anything from Hp
                } else {
                    value -= currentCp; // Get diff from value vs Cp; will apply diff to Hp
                    setCurrentCp(0, false); // Set Cp to 0
                }
            }

            if (fullValue > 0 && !isDOT) {
                // Send a System Message to the Player
                SystemMessage smsg = getSystemMessage(SystemMessageId.C1_HAS_RECEIVED_S3_DAMAGE_FROM_C2)
                        .addString(me.getName())
                        .addString(attacker.getName())
                        .addInt((int) fullValue)
                        .addPopup(me.getObjectId(), attacker.getObjectId(), (int) -fullValue);

                me.sendPacket(smsg);

                if (tDmg > 0 && attackerPlayer != null) {
                    smsg = getSystemMessage(SystemMessageId.YOU_HAVE_DEALT_S1_DAMAGE_TO_YOUR_TARGET_AND_S2_DAMAGE_TO_THE_SERVITOR);
                    smsg.addInt((int) fullValue);
                    smsg.addInt((int) tDmg);
                    attackerPlayer.sendPacket(smsg);
                }
            }
        }

        if (value > 0) {
            double newHp = Math.max(getCurrentHp() - value, me.isUndying() ? 1 : 0);
            if (newHp <= 0) {
                if (me.isInDuel()) {
                    me.disableAllSkills();
                    stopHpMpRegeneration();
                    if (nonNull(attacker)) {
                        attacker.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                        attacker.sendPacket(ActionFailed.STATIC_PACKET);
                    }
                    // let the DuelManager know of his defeat
                    DuelManager.getInstance().onPlayerDefeat(me);
                    newHp = 1;
                } else {
                    newHp = 0;
                }
            }
            setCurrentHp(newHp);
        }

        if ((me.getCurrentHp() < 0.5) && !isHPConsumption && !me.isUndying()) {
            me.abortAttack();
            me.abortCast();

            if (me.isInOlympiadMode()) {
                stopHpMpRegeneration();
                me.setIsDead(true);
                me.setIsPendingRevive(true);
                doIfNonNull(me.getPet(), pet -> pet.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE));
                me.getServitors().values().forEach(s -> s.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE));
                return;
            }
            me.doDie(attacker);
        }
    }

    @Override
    public final double getCurrentCp() {
        return currentCp;
    }

    @Override
    public final void setCurrentCp(double newCp) {
        setCurrentCp(newCp, true);
    }

    @Override
    public final void setCurrentCp(double newCp, boolean broadcastPacket) {
        // Get the Max CP of the Creature
        final int currentCp = (int) this.currentCp;
        final int maxCp = getOwner().getStats().getMaxCp();

        synchronized (this) {
            if (getOwner().isDead()) {
                return;
            }

            if (newCp < 0) {
                newCp = 0;
            }

            if (newCp >= maxCp) {
                // Set the RegenActive flag to false
                this.currentCp = maxCp;
                _flagsRegenActive &= ~REGEN_FLAG_CP;

                // Stop the HP/MP/CP Regeneration task
                if (_flagsRegenActive == 0) {
                    stopHpMpRegeneration();
                }
            } else {
                // Set the RegenActive flag to true
                this.currentCp = newCp;
                _flagsRegenActive |= REGEN_FLAG_CP;

                // Start the HP/MP/CP Regeneration task with Medium priority
                startHpMpRegeneration();
            }
        }

        // Send the Server->Client packet StatusUpdate with current HP and MP to all other Player to inform
        if ((currentCp != this.currentCp) && broadcastPacket) {
            getOwner().broadcastStatusUpdate();
        }
    }

    @Override
    protected void doRegeneration() {
        final PlayerStats charstat = getOwner().getStats();

        // Modify the current CP of the Creature and broadcast Server->Client packet StatusUpdate
        if (currentCp < charstat.getMaxRecoverableCp()) {
            setCurrentCp(currentCp + getOwner().getStats().getValue(Stat.REGENERATE_CP_RATE), false);
        }

        // Modify the current HP of the Creature and broadcast Server->Client packet StatusUpdate
        if (getCurrentHp() < charstat.getMaxRecoverableHp()) {
            setCurrentHp(getCurrentHp() + getOwner().getStats().getValue(Stat.REGENERATE_HP_RATE), false);
        }

        // Modify the current MP of the Creature and broadcast Server->Client packet StatusUpdate
        if (getCurrentMp() < charstat.getMaxRecoverableMp()) {
            setCurrentMp(getCurrentMp() + getOwner().getStats().getValue(Stat.REGENERATE_MP_RATE), false);
        }

        getOwner().broadcastStatusUpdate(); // send the StatusUpdate packet
    }

    @Override
    public Player getOwner() {
        return (Player) super.getOwner();
    }
}
