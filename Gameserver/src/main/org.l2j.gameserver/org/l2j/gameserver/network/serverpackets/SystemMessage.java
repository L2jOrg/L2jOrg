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


import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.SystemMessageId;

/**
 * @author Forsaiken, UnAfraid
 */
public final class SystemMessage extends AbstractMessagePacket<SystemMessage> {
    private SystemMessage(SystemMessageId smId) {
        super(smId);
    }

    public static SystemMessage sendString(String text) {
        if (text == null) {
            throw new NullPointerException();
        }

        final SystemMessage sm = getSystemMessage(SystemMessageId.S1);
        sm.addString(text);
        return sm;
    }

    public static SystemMessage getSystemMessage(SystemMessageId smId) {
        SystemMessage sm = smId.getStaticSystemMessage();
        if (sm != null) {
            return sm;
        }

        sm = new SystemMessage(smId);
        if (smId.getParamCount() == 0) {
            smId.setStaticSystemMessage(sm);
        }

        return sm;
    }

    /**
     * Use {@link #getSystemMessage(SystemMessageId)} where possible instead
     *
     * @param id
     * @return the system message associated to the given Id.
     */
    public static SystemMessage getSystemMessage(int id) {
        return getSystemMessage(SystemMessageId.getSystemMessageId(id));
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.SYSTEM_MSG);

        writeShort(getId());
        writeMe();
    }

}
