package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kerberos
 */
public final class NpcSay extends IClientOutgoingPacket {
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

    public NpcSay(L2Npc npc, ChatType messageType, String text) {
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

    public NpcSay(L2Npc npc, ChatType messageType, NpcStringId npcString) {
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
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.NPC_SAY.writeId(packet);

        packet.putInt(_objectId);
        packet.putInt(_textType.getClientId());
        packet.putInt(_npcId);
        packet.putInt(_npcString);
        if (_npcString == -1) {
            writeString(_text, packet);
        } else if (_parameters != null) {
            for (String s : _parameters) {
                writeString(s, packet);
            }
        }
    }
}