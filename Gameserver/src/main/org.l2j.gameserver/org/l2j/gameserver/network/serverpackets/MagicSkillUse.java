/*
 * Copyright © 2019-2021 L2JOrg
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
package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.interfaces.IPositionable;
import org.l2j.gameserver.model.skills.SkillCastingType;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.Collections;
import java.util.List;

import static java.lang.Math.max;
import static java.util.Objects.nonNull;

/**
 * MagicSkillUse server packet implementation.
 *
 * @author UnAfraid, NosBit
 * @author JoeAlisson
 */
public final class MagicSkillUse extends ServerPacket {

    private final int hitTime;
    private final int reuseDelay;
    private final int actionId; // If skill is called from RequestActionUse, use that ID.
    private final SkillCastingType castingType;
    private final Creature caster;
    private final WorldObject target;
    private final List<Integer> _unknown = Collections.emptyList();
    private final List<Location> groundLocations;
    private final Skill skill;

    public MagicSkillUse(Creature caster, WorldObject target, Skill skill, int hitTime, int reuseDelay, int actionId, SkillCastingType castingType) {
        this.caster = caster;
        this.target = target;
        this.skill = skill;
        this.hitTime = hitTime;
        this.reuseDelay = reuseDelay;
        this.actionId = actionId;
        this.castingType = castingType;
        Location skillWorldPos = null;
        if(caster instanceof Player player) {
            skillWorldPos = player.getCurrentSkillWorldPosition();
        }
        groundLocations =  nonNull(skillWorldPos) ? List.of(skillWorldPos) :  Collections.emptyList();
    }

    public MagicSkillUse(Creature caster, Skill skill, int reuseDelay) {
        this(caster, caster, skill, skill.getHitTime(), reuseDelay, -1, SkillCastingType.NORMAL);
    }

    public MagicSkillUse(Creature caster, WorldObject target, Skill skill, int reuseDelay) {
        this(caster, target, skill, skill.getHitTime(), reuseDelay, -1, SkillCastingType.NORMAL);
    }

    public MagicSkillUse(Creature cha, WorldObject target, int skillId, int skillLevel, int hitTime, int reuseDelay) {
        this(cha, target, SkillEngine.getInstance().getSkill(skillId, skillLevel), hitTime, reuseDelay, -1, SkillCastingType.NORMAL);
    }

    public MagicSkillUse(Creature cha, int skillId, int skillLevel, int hitTime, int reuseDelay) {
        this(cha, cha, SkillEngine.getInstance().getSkill(skillId, skillLevel), hitTime, reuseDelay, -1, SkillCastingType.NORMAL);
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.MAGIC_SKILL_USE, buffer );

        buffer.writeInt(castingType.getClientBarId()); // Casting bar type: 0 - default, 1 - default up, 2 - blue, 3 - green, 4 - red.
        buffer.writeInt(caster.getObjectId());
        buffer.writeInt(target.getObjectId());
        buffer.writeInt(skill.getId());
        buffer.writeInt(skill.getLevel());
        buffer.writeInt(hitTime);
        buffer.writeInt(skill.getReuseDelayGroup());
        buffer.writeInt(reuseDelay);
        buffer.writeInt(caster.getX());
        buffer.writeInt(caster.getY());
        buffer.writeInt(caster.getZ());
        buffer.writeShort(_unknown.size()); // TODO: Implement me!
        for (int unknown : _unknown) {
            buffer.writeShort(unknown);
        }
        buffer.writeShort(groundLocations.size());
        for (IPositionable target : groundLocations) {
            buffer.writeInt(target.getX());
            buffer.writeInt(target.getY());
            buffer.writeInt(target.getZ());
        }
        buffer.writeInt(target.getX());
        buffer.writeInt(target.getY());
        buffer.writeInt(target.getZ());
        buffer.writeInt(actionId >= 0 ? 0x01 : 0x00); // 1 when ID from RequestActionUse is used
        buffer.writeInt(max(actionId, 0)); // ID from RequestActionUse. Used to set cooldown on summon skills.
    }

}
