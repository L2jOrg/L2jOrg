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

import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kerberos
 */
public final class NpcSay extends ServerPacket {
    private final int _objectId;
    private final ChatType _textType;
    private final int _npcId;
    private final int _npcString;
    private String _text;
    private List<String> _parameters;

    /**
     * @param objectId
     * @param messageType
     * @param npcId
     * @param text
     */
    public NpcSay(int objectId, ChatType messageType, int npcId, String text) {
        _objectId = objectId;
        _textType = messageType;
        _npcId = 1000000 + npcId;
        _npcString = -1;
        _text = text;
    }

    public NpcSay(Npc npc, ChatType messageType, String text) {
        _objectId = npc.getObjectId();
        _textType = messageType;
        _npcId = 1000000 + npc.getTemplate().getDisplayId();
        _npcString = -1;
        _text = text;
    }

    public NpcSay(int objectId, ChatType messageType, int npcId, NpcStringId npcString) {
        _objectId = objectId;
        _textType = messageType;
        _npcId = 1000000 + npcId;
        _npcString = npcString.getId();
    }

    public NpcSay(Npc npc, ChatType messageType, NpcStringId npcString) {
        _objectId = npc.getObjectId();
        _textType = messageType;
        _npcId = 1000000 + npc.getTemplate().getDisplayId();
        _npcString = npcString.getId();
    }

    /**
     * @param text the text to add as a parameter for this packet's message (replaces S1, S2 etc.)
     * @return this NpcSay packet object
     */
    public NpcSay addStringParameter(String text) {
        if (_parameters == null) {
            _parameters = new ArrayList<>();
        }
        _parameters.add(text);
        return this;
    }

    /**
     * @param params a list of strings to add as parameters for this packet's message (replaces S1, S2 etc.)
     * @return this NpcSay packet object
     */
    public NpcSay addStringParameters(String... params) {
        if ((params != null) && (params.length > 0)) {
            if (_parameters == null) {
                _parameters = new ArrayList<>();
            }

            for (String item : params) {
                if ((item != null) && (item.length() > 0)) {
                    _parameters.add(item);
                }
            }
        }
        return this;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.NPC_SAY);

        writeInt(_objectId);
        writeInt(_textType.getClientId());
        writeInt(_npcId);
        writeInt(_npcString);
        if (_npcString == -1) {
            writeString(_text);
        } else if (_parameters != null) {
            for (String s : _parameters) {
                writeString(s);
            }
        }
    }

}