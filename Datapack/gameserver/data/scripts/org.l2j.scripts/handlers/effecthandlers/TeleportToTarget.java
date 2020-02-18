package handlers.effecthandlers;

import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.network.serverpackets.FlyToLocation;
import org.l2j.gameserver.network.serverpackets.FlyToLocation.FlyType;
import org.l2j.gameserver.network.serverpackets.ValidateLocation;

import static org.l2j.gameserver.util.MathUtil.convertHeadingToDegree;

/**
 * Teleport To Target effect implementation.
 * @author Didldak, Adry_85
 * @author JoeAlisson
 */
public final class TeleportToTarget extends AbstractEffect {
    private TeleportToTarget() {
    }

    @Override
    public EffectType getEffectType()
    {
        return EffectType.TELEPORT_TO_TARGET;
    }

    @Override
    public boolean canStart(Creature effector, Creature effected, Skill skill)
    {
        return (effected != null) && GeoEngine.getInstance().canSeeTarget(effected, effector);
    }

    @Override
    public boolean isInstant()
    {
        return true;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        final int px = effected.getX();
        final int py = effected.getY();
        double ph = convertHeadingToDegree(effected.getHeading());

        ph += 180;
        if (ph > 360) {
            ph -= 360;
        }

        ph = (Math.PI * ph) / 180;
        final int x = (int) (px + (25 * Math.cos(ph)));
        final int y = (int) (py + (25 * Math.sin(ph)));
        final int z = effected.getZ();

        final Location loc = GeoEngine.getInstance().canMoveToTargetLoc(effector.getX(), effector.getY(), effector.getZ(), x, y, z, effector.getInstanceWorld());

        effector.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
        effector.broadcastPacket(new FlyToLocation(effector, loc.getX(), loc.getY(), loc.getZ(), FlyType.DUMMY));
        effector.abortAttack();
        effector.abortCast();
        effector.setXYZ(loc);
        effector.broadcastPacket(new ValidateLocation(effector));
        effected.revalidateZone(true);
    }

    public static class Factory implements SkillEffectFactory {

        private static final TeleportToTarget INSTANCE = new TeleportToTarget();

        @Override
        public AbstractEffect create(StatsSet data) {
            return INSTANCE;
        }

        @Override
        public String effectName() {
            return "TeleportToTarget";
        }
    }
}
