package handlers.effecthandlers;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Door;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.network.SystemMessageId;

import static org.l2j.gameserver.util.GameUtils.isDoor;

/**
 * Open Door effect implementation.
 * @author Adry_85
 * @author JoeAlisson
 */
public final class OpenDoor extends AbstractEffect {
    private final int power;

    private OpenDoor(StatsSet params) {
        power = params.getInt("power", 0);
    }

    @Override
    public boolean isInstant()
    {
        return true;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        if (!isDoor(effected) || (effector.getInstanceWorld() != effected.getInstanceWorld())) {
            return;
        }

        final Door door = (Door) effected;
        if (!door.isOpenableBySkill() || (door.getFort() != null)) {
            effector.sendPacket(SystemMessageId.THIS_DOOR_CANNOT_BE_UNLOCKED);
            return;
        }

        if (!door.isOpen() && Rnd.chance(power)) {
            door.openMe();
        } else {
            effector.sendPacket(SystemMessageId.YOU_HAVE_FAILED_TO_UNLOCK_THE_DOOR);
        }
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new OpenDoor(data);
        }

        @Override
        public String effectName() {
            return "OpenDoor";
        }
    }
}
