package org.l2j.gameserver.ai;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.network.SystemMessageId;

import static org.l2j.gameserver.util.GameUtils.isPlayable;

/**
 * This class manages AI of Playable.<br>
 * PlayableAI :
 * <li>SummonAI</li>
 * <li>PlayerAI</li>
 *
 * @author JIV
 */
public abstract class PlayableAI extends CreatureAI {
    public PlayableAI(Playable playable) {
        super(playable);
    }

    @Override
    protected void onIntentionAttack(Creature target) {
        if (isPlayable(target)) {
            if (target.getActingPlayer().isProtectionBlessingAffected() && ((actor.getActingPlayer().getLevel() - target.getActingPlayer().getLevel()) >= 10) && (actor.getActingPlayer().getReputation() < 0) && !(target.isInsideZone(ZoneType.PVP))) {
                // If attacker have karma and have level >= 10 than his target and target have
                // Newbie Protection Buff,
                actor.getActingPlayer().sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
                clientActionFailed();
                return;
            }

            if (actor.getActingPlayer().isProtectionBlessingAffected() && ((target.getActingPlayer().getLevel() - actor.getActingPlayer().getLevel()) >= 10) && (target.getActingPlayer().getReputation() < 0) && !(target.isInsideZone(ZoneType.PVP))) {
                // If target have karma and have level >= 10 than his target and actor have
                // Newbie Protection Buff,
                actor.getActingPlayer().sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
                clientActionFailed();
                return;
            }

            if (target.getActingPlayer().isCursedWeaponEquipped() && (actor.getActingPlayer().getLevel() <= 20)) {
                actor.getActingPlayer().sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
                clientActionFailed();
                return;
            }

            if (actor.getActingPlayer().isCursedWeaponEquipped() && (target.getActingPlayer().getLevel() <= 20)) {
                actor.getActingPlayer().sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
                clientActionFailed();
                return;
            }
        }
        super.onIntentionAttack(target);
    }

    @Override
    protected void onIntentionCast(Skill skill, WorldObject target, Item item, boolean forceUse, boolean dontMove) {
        if ((isPlayable(target)) && skill.isBad()) {
            if (target.getActingPlayer().isProtectionBlessingAffected() && ((actor.getActingPlayer().getLevel() - target.getActingPlayer().getLevel()) >= 10) && (actor.getActingPlayer().getReputation() < 0) && !target.isInsideZone(ZoneType.PVP)) {
                // If attacker have karma and have level >= 10 than his target and target have
                // Newbie Protection Buff,
                actor.getActingPlayer().sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
                clientActionFailed();
                return;
            }

            if (actor.getActingPlayer().isProtectionBlessingAffected() && ((target.getActingPlayer().getLevel() - actor.getActingPlayer().getLevel()) >= 10) && (target.getActingPlayer().getReputation() < 0) && !target.isInsideZone(ZoneType.PVP)) {
                // If target have karma and have level >= 10 than his target and actor have
                // Newbie Protection Buff,
                actor.getActingPlayer().sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
                clientActionFailed();
                return;
            }

            if (target.getActingPlayer().isCursedWeaponEquipped() && ((actor.getActingPlayer().getLevel() <= 20) || (target.getActingPlayer().getLevel() <= 20))) {
                actor.getActingPlayer().sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
                clientActionFailed();
                return;
            }
        }
        super.onIntentionCast(skill, target, item, forceUse, dontMove);
    }
}
