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
package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.interfaces.IPositionable;
import org.l2j.gameserver.model.skills.SkillCastingType;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.lang.Math.max;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * MagicSkillUse server packet implementation.
 *
 * @author UnAfraid, NosBit
 */
public final class MagicSkillUse extends ServerPacket {
    private final int _skillId;
    private final int _skillLevel;
    private final int _hitTime;
    private final int _reuseGroup;
    private final int _reuseDelay;
    private final int _actionId; // If skill is called from RequestActionUse, use that ID.
    private final SkillCastingType _castingType; // Defines which client bar is going to use.
    private final Creature _activeChar;
    private final WorldObject _target;
    private final List<Integer> _unknown = Collections.emptyList();
    private final List<Location> _groundLocations;

    public MagicSkillUse(Creature cha, WorldObject target, int skillId, int skillLevel, int hitTime, int reuseDelay, int reuseGroup, int actionId, SkillCastingType castingType) {
        _activeChar = cha;
        _target = target;
        _skillId = skillId;
        _skillLevel = skillLevel;
        _hitTime = hitTime;
        _reuseGroup = reuseGroup;
        _reuseDelay = reuseDelay;
        _actionId = actionId;
        _castingType = castingType;
        Location skillWorldPos = null;
        if (isPlayer(cha)) {
            final Player player = cha.getActingPlayer();
            if (player.getCurrentSkillWorldPosition() != null) {
                skillWorldPos = player.getCurrentSkillWorldPosition();
            }
        }
        _groundLocations = skillWorldPos != null ? Arrays.asList(skillWorldPos) : Collections.emptyList();
    }

    public MagicSkillUse(Creature cha, WorldObject target, int skillId, int skillLevel, int hitTime, int reuseDelay) {
        this(cha, target, skillId, skillLevel, hitTime, reuseDelay, -1, -1, SkillCastingType.NORMAL);
    }

    public MagicSkillUse(Creature cha, int skillId, int skillLevel, int hitTime, int reuseDelay) {
        this(cha, cha, skillId, skillLevel, hitTime, reuseDelay, -1, -1, SkillCastingType.NORMAL);
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.MAGIC_SKILL_USE);

        writeInt(_castingType.getClientBarId()); // Casting bar type: 0 - default, 1 - default up, 2 - blue, 3 - green, 4 - red.
        writeInt(_activeChar.getObjectId());
        writeInt(_target.getObjectId());
        writeInt(_skillId);
        writeInt(_skillLevel);
        writeInt(_hitTime);
        writeInt(_reuseGroup);
        writeInt(_reuseDelay);
        writeInt(_activeChar.getX());
        writeInt(_activeChar.getY());
        writeInt(_activeChar.getZ());
        writeShort((short) _unknown.size()); // TODO: Implement me!
        for (int unknown : _unknown) {
            writeShort((short) unknown);
        }
        writeShort((short) _groundLocations.size());
        for (IPositionable target : _groundLocations) {
            writeInt(target.getX());
            writeInt(target.getY());
            writeInt(target.getZ());
        }
        writeInt(_target.getX());
        writeInt(_target.getY());
        writeInt(_target.getZ());
        writeInt(_actionId >= 0 ? 0x01 : 0x00); // 1 when ID from RequestActionUse is used
        writeInt(max(_actionId, 0)); // ID from RequestActionUse. Used to set cooldown on summon skills.
    }

}
