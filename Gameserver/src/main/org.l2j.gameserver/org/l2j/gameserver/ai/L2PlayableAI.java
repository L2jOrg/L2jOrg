package org.l2j.gameserver.ai;

import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.zone.ZoneId;
import org.l2j.gameserver.network.SystemMessageId;

/**
 * This class manages AI of Playable.<br>
 * L2PlayableAI :
 * <li>L2SummonAI</li>
 * <li>L2PlayerAI</li>
 *
 * @author JIV
 */
public abstract class L2PlayableAI extends L2CharacterAI {
    public L2PlayableAI(Playable playable) {
        super(playable);
    }

    @Override
    protected void onIntentionAttack(L2Character target) {
        if ((target != null) && target.isPlayable()) {
            if (target.getActingPlayer().isProtectionBlessingAffected() && ((_actor.getActingPlayer().getLevel() - target.getActingPlayer().getLevel()) >= 10) && (_actor.getActingPlayer().getReputation() < 0) && !(target.isInsideZone(ZoneId.PVP))) {
                // If attacker have karma and have level >= 10 than his target and target have
                // Newbie Protection Buff,
                _actor.getActingPlayer().sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
                clientActionFailed();
                return;
            }

            if (_actor.getActingPlayer().isProtectionBlessingAffected() && ((target.getActingPlayer().getLevel() - _actor.getActingPlayer().getLevel()) >= 10) && (target.getActingPlayer().getReputation() < 0) && !(target.isInsideZone(ZoneId.PVP))) {
                // If target have karma and have level >= 10 than his target and actor have
                // Newbie Protection Buff,
                _actor.getActingPlayer().sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
                clientActionFailed();
                return;
            }

            if (target.getActingPlayer().isCursedWeaponEquipped() && (_actor.getActingPlayer().getLevel() <= 20)) {
                _actor.getActingPlayer().sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
                clientActionFailed();
                return;
            }

            if (_actor.getActingPlayer().isCursedWeaponEquipped() && (target.getActingPlayer().getLevel() <= 20)) {
                _actor.getActingPlayer().sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
                clientActionFailed();
                return;
            }
        }
        super.onIntentionAttack(target);
    }

    @Override
    protected void onIntentionCast(Skill skill, L2Object target, L2ItemInstance item, boolean forceUse, boolean dontMove) {
        if ((target != null) && (target.isPlayable()) && skill.isBad()) {
            if (target.getActingPlayer().isProtectionBlessingAffected() && ((_actor.getActingPlayer().getLevel() - target.getActingPlayer().getLevel()) >= 10) && (_actor.getActingPlayer().getReputation() < 0) && !target.isInsideZone(ZoneId.PVP)) {
                // If attacker have karma and have level >= 10 than his target and target have
                // Newbie Protection Buff,
                _actor.getActingPlayer().sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
                clientActionFailed();
                return;
            }

            if (_actor.getActingPlayer().isProtectionBlessingAffected() && ((target.getActingPlayer().getLevel() - _actor.getActingPlayer().getLevel()) >= 10) && (target.getActingPlayer().getReputation() < 0) && !target.isInsideZone(ZoneId.PVP)) {
                // If target have karma and have level >= 10 than his target and actor have
                // Newbie Protection Buff,
                _actor.getActingPlayer().sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
                clientActionFailed();
                return;
            }

            if (target.getActingPlayer().isCursedWeaponEquipped() && ((_actor.getActingPlayer().getLevel() <= 20) || (target.getActingPlayer().getLevel() <= 20))) {
                _actor.getActingPlayer().sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
                clientActionFailed();
                return;
            }
        }
        super.onIntentionCast(skill, target, item, forceUse, dontMove);
    }
}
