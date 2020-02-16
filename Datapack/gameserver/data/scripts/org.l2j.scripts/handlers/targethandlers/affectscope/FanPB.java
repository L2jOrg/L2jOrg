package handlers.targethandlers.affectscope;

import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.handler.AffectObjectHandler;
import org.l2j.gameserver.handler.IAffectObjectHandler;
import org.l2j.gameserver.handler.IAffectScopeHandler;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.targets.AffectScope;
import org.l2j.gameserver.world.World;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.l2j.gameserver.util.MathUtil.calculateAngleFrom;
import static org.l2j.gameserver.util.MathUtil.convertHeadingToDegree;

/**
 * Fan point blank affect scope implementation. Gathers objects in a certain angle of circular area around yourself without taking target into account.
 * @author Nik
 * @author JoeAlisson
 */
public class FanPB implements IAffectScopeHandler {

    @Override
    public void forEachAffected(Creature activeChar, WorldObject target, Skill skill, Consumer<? super WorldObject> action) {
        World.getInstance().forEachVisibleObjectInRange(activeChar, Creature.class, skill.getFanRadius(), action::accept, fanFilterOf(activeChar, skill));
    }

    protected Predicate<Creature> fanFilterOf(Creature activeChar, Skill skill) {
        final IAffectObjectHandler affectObject = AffectObjectHandler.getInstance().getHandler(skill.getAffectObject());
        final double headingAngle = convertHeadingToDegree(activeChar.getHeading());
        final int fanStartAngle = skill.getFanStartAngle();
        final int fanAngle = skill.getFanAngle();
        final double fanHalfAngle = fanAngle / 2.; // Half left and half right.
        final int affectLimit = skill.getAffectLimit();
        // Target checks.
        final AtomicInteger affected = new AtomicInteger(0);

        return creature -> {
            if (creature.isDead() || ((affectLimit > 0) && (affected.get() >= affectLimit))) {
                return false;
            }

            if (Math.abs(calculateAngleFrom(activeChar, creature) - (headingAngle + fanStartAngle)) > fanHalfAngle) {
                return false;
            }

            if ((affectObject != null) && !affectObject.checkAffectedObject(activeChar, creature)) {
                return false;
            }
            if (!GeoEngine.getInstance().canSeeTarget(activeChar, creature)) {
                return false;
            }

            affected.incrementAndGet();
            return true;
        };
    }

    @Override
    public Enum<AffectScope> getAffectScopeType() {
        return AffectScope.FAN_PB;
    }
}
