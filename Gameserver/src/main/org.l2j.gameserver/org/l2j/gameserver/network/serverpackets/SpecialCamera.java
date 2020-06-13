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

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * Special Camera server packet implementation.
 *
 * @author Zoey76
 */
public class SpecialCamera extends ServerPacket {
    private final int _id;
    private final int _force;
    private final int _angle1;
    private final int _angle2;
    private final int _time;
    private final int _duration;
    private final int _relYaw;
    private final int _relPitch;
    private final int _isWide;
    private final int _relAngle;
    private final int _unk;

    /**
     * Special Camera packet constructor.
     *
     * @param creature the creature
     * @param force
     * @param angle1
     * @param angle2
     * @param time
     * @param range
     * @param duration
     * @param relYaw
     * @param relPitch
     * @param isWide
     * @param relAngle
     */
    public SpecialCamera(Creature creature, int force, int angle1, int angle2, int time, int range, int duration, int relYaw, int relPitch, int isWide, int relAngle) {
        this(creature, force, angle1, angle2, time, duration, range, relYaw, relPitch, isWide, relAngle, 0);
    }

    /**
     * Special Camera Ex packet constructor.
     *
     * @param creature the creature
     * @param talker
     * @param force
     * @param angle1
     * @param angle2
     * @param time
     * @param duration
     * @param relYaw
     * @param relPitch
     * @param isWide
     * @param relAngle
     */
    public SpecialCamera(Creature creature, Creature talker, int force, int angle1, int angle2, int time, int duration, int relYaw, int relPitch, int isWide, int relAngle) {
        this(creature, force, angle1, angle2, time, duration, 0, relYaw, relPitch, isWide, relAngle, 0);
    }

    /**
     * Special Camera 3 packet constructor.
     *
     * @param creature the creature
     * @param force
     * @param angle1
     * @param angle2
     * @param time
     * @param range
     * @param duration
     * @param relYaw
     * @param relPitch
     * @param isWide
     * @param relAngle
     * @param unk      unknown post-C4 parameter
     */
    public SpecialCamera(Creature creature, int force, int angle1, int angle2, int time, int range, int duration, int relYaw, int relPitch, int isWide, int relAngle, int unk) {
        _id = creature.getObjectId();
        _force = force;
        _angle1 = angle1;
        _angle2 = angle2;
        _time = time;
        _duration = duration;
        _relYaw = relYaw;
        _relPitch = relPitch;
        _isWide = isWide;
        _relAngle = relAngle;
        _unk = unk;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.SPECIAL_CAMERA);

        writeInt(_id);
        writeInt(_force);
        writeInt(_angle1);
        writeInt(_angle2);
        writeInt(_time);
        writeInt(_duration);
        writeInt(_relYaw);
        writeInt(_relPitch);
        writeInt(_isWide);
        writeInt(_relAngle);
        writeInt(_unk);
    }

}
