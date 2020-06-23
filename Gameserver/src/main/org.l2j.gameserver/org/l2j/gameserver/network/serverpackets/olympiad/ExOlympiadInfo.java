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
package org.l2j.gameserver.network.serverpackets.olympiad;

import org.l2j.gameserver.engine.olympiad.OlympiadRuleType;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author JoeAlisson
 */
public class ExOlympiadInfo extends ServerPacket {

    private final boolean open;
    private final OlympiadRuleType type;
    private final int remainTime;

    private ExOlympiadInfo(boolean open, OlympiadRuleType type, int remainTime) {
        this.open = open;
        this.type = type;
        this.remainTime = remainTime;
    }

    @Override
    protected void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_OLYMPIAD_INFO);
        writeByte(open);
        writeInt(remainTime);
        writeByte(type.ordinal());
    }

    public static ExOlympiadInfo show(OlympiadRuleType type, int remainTime) {
        return new ExOlympiadInfo(true, type, remainTime);
    }

    public static ExOlympiadInfo hide(OlympiadRuleType type) {
        return new ExOlympiadInfo(false, type, 0);
    }

}
