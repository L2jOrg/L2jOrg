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

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.engine.olympiad.OlympiadRuleType;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author JoeAlisson
 */
public class ExOlympiadMatchMakingResult extends ServerPacket {

    private final boolean registered;
    private final OlympiadRuleType ruleType;

    public ExOlympiadMatchMakingResult(boolean registered, OlympiadRuleType ruleType) {
        this.registered = registered;
        this.ruleType = ruleType;
    }

    @Override
    protected void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_OLYMPIAD_MATCH_MAKING_RESULT, buffer );
        buffer.writeByte(registered);
        buffer.writeByte(ruleType.ordinal());
    }
}
