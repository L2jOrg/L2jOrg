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
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * @author JoeAlisson
 */
public class PlaySound extends ServerPacket {
    private final SoundType type;
    private final String file;
    private final WorldObject sourceObject;
    private int delay;

    private PlaySound(SoundType type, String soundFile) {
        this(type, soundFile, null);
    }

    private PlaySound(SoundType type, String soundFile, WorldObject object) {
        this.type = type;
        this.file = soundFile;
        this.sourceObject = object;
    }

    public PlaySound delayed(int delay) {
        this.delay = delay;
        return this;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.PLAY_SOUND, buffer );

        buffer.writeInt(type.ordinal());
        buffer.writeString(file);
        buffer.writeInt(nonNull(sourceObject));
        if(isNull(sourceObject)) {
            buffer.writeInt(0);
            buffer.writeInt(0);
            buffer.writeInt(0);
            buffer.writeInt(0);
        } else {
            buffer.writeInt(sourceObject.getObjectId());
            writeLocation(sourceObject, buffer);
        }
        buffer.writeInt(delay);
    }
    
    
    public static PlaySound music(String soundFile) {
        return new PlaySound(SoundType.MUSIC, soundFile);
    }
    
    public static PlaySound music(String soundFile, WorldObject object) {
        return new PlaySound(SoundType.MUSIC, soundFile, object);
    }

    public static PlaySound sound(String sound) {
        return new PlaySound(SoundType.SOUND, sound);
    }

    public static PlaySound voice(String voice) {
        return new PlaySound(SoundType.VOICE, voice);
    }

    public enum SoundType {
        SOUND,
        MUSIC,
        VOICE
    }

}
