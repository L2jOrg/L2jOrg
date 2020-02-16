package org.l2j.gameserver.model.conditions;

import io.github.joealisson.primitive.IntList;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.items.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneManager;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public class ConditionPlayerInsideZoneId extends Condition {
    public final IntList zones;

    public ConditionPlayerInsideZoneId(IntList zones) {
        this.zones = zones;
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, Skill skill, ItemTemplate item) {
        if (effector.getActingPlayer() == null) {
            return false;
        }

        for (Zone zone : ZoneManager.getInstance().getZones(effector)) {
            if (zones.contains(zone.getId())) {
                return true;
            }
        }
        return false;
    }
}
