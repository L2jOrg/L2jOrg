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

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author UnAfraid
 */
@StaticPacket
public class ExChangeClientEffectInfo extends ServerPacket {
    public static final ExChangeClientEffectInfo STATIC_FREYA_DEFAULT = new ExChangeClientEffectInfo(0, 0, 1);
    public static final ExChangeClientEffectInfo STATIC_FREYA_DESTROYED = new ExChangeClientEffectInfo(0, 0, 2);

    private final int _type;
    private final int _key;
    private final int _value;

    /**
     * @param type  <ul>
     *              <li>0 - ChangeZoneState</li>
     *              <li>1 - SetL2Fog</li>
     *              <li>2 - postEffectData</li>
     *              </ul>
     * @param key
     * @param value
     */
    private ExChangeClientEffectInfo(int type, int key, int value) {
        _type = type;
        _key = key;
        _value = value;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_CLIENT_EFFECT_INFO);

        writeInt(_type);
        writeInt(_key);
        writeInt(_value);
    }

}
