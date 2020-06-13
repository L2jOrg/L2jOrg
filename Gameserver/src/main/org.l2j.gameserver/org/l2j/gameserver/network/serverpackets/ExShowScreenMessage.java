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
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.SystemMessageId;

import java.util.ArrayList;
import java.util.List;

/**
 * ExShowScreenMessage server packet implementation.
 *
 * @author Kerberos
 */
public class ExShowScreenMessage extends ServerPacket {
    // Positions
    public static final byte TOP_LEFT = 0x01;
    public static final byte TOP_CENTER = 0x02;
    public static final byte TOP_RIGHT = 0x03;
    public static final byte MIDDLE_LEFT = 0x04;
    public static final byte MIDDLE_CENTER = 0x05;
    public static final byte MIDDLE_RIGHT = 0x06;
    public static final byte BOTTOM_CENTER = 0x07;
    public static final byte BOTTOM_RIGHT = 0x08;
    private final int _type;
    private final int _sysMessageId;
    private final int _unk1;
    private final int _unk2;
    private final int _unk3;
    private final boolean _fade;
    private final int _size;
    private final int _position;
    private final boolean _effect;
    private final String _text;
    private final int _time;
    private final int _npcString;
    private List<String> _parameters = null;

    /**
     * Display a String on the screen for a given time.
     *
     * @param text the text to display
     * @param time the display time
     */
    public ExShowScreenMessage(String text, int time) {
        _type = 2;
        _sysMessageId = -1;
        _unk1 = 0;
        _unk2 = 0;
        _unk3 = 0;
        _fade = false;
        _position = TOP_CENTER;
        _text = text;
        _time = time;
        _size = 0;
        _effect = false;
        _npcString = -1;
    }

    /**
     * Display a String on the screen for a given time.
     * @param text the text to display
     * @param position the position on the screen
     * @param time the display time
     */
    public ExShowScreenMessage(String text, int position, int time)
    {
        _type = 2;
        _sysMessageId = -1;
        _unk1 = 0;
        _unk2 = 0;
        _unk3 = 0;
        _fade = false;
        _position = position;
        _text = text;
        _time = time;
        _size = 0;
        _effect = false;
        _npcString = -1;
    }

    /**
     * Display a String on the screen for a given time.
     *
     * @param text       the text to display
     * @param position   the position on the screen
     * @param time       the display time
     * @param size       the font size 0 - normal, 1 - small
     * @param fade       the fade effect
     * @param showEffect upper effect
     */
    public ExShowScreenMessage(String text, int position, int time, int size, boolean fade, boolean showEffect) {
        _type = 1;
        _sysMessageId = -1;
        _unk1 = 0;
        _unk2 = 0;
        _unk3 = 0;
        _fade = fade;
        _position = position;
        _text = text;
        _time = time;
        _size = size;
        _effect = showEffect;
        _npcString = -1;
    }

    /**
     * Display a NPC String on the screen for a given position and time.
     *
     * @param npcString the NPC String Id
     * @param position  the position on the screen
     * @param time      the display time
     * @param params    the String parameters
     */
    public ExShowScreenMessage(NpcStringId npcString, int position, int time, String... params) {
        _type = 2;
        _sysMessageId = -1;
        _unk1 = 0x00;
        _unk2 = 0x00;
        _unk3 = 0x00;
        _fade = false;
        _position = position;
        _text = null;
        _time = time;
        _size = 0x00;
        _effect = false;
        _npcString = npcString.getId();
        if (params != null) {
            addStringParameter(params);
        }
    }

    /**
     * Display a System Message on the screen for a given position and time.
     *
     * @param systemMsg the System Message Id
     * @param position  the position on the screen
     * @param time      the display time
     * @param params    the String parameters
     */
    public ExShowScreenMessage(SystemMessageId systemMsg, int position, int time, String... params) {
        _type = 2;
        _sysMessageId = systemMsg.getId();
        _unk1 = 0x00;
        _unk2 = 0x00;
        _unk3 = 0x00;
        _fade = false;
        _position = position;
        _text = null;
        _time = time;
        _size = 0x00;
        _effect = false;
        _npcString = -1;
        if (params != null) {
            addStringParameter(params);
        }
    }

    /**
     * Display a NPC String on the screen for a given position and time.
     *
     * @param npcString  the NPC String Id
     * @param position   the position on the screen
     * @param time       the display time
     * @param showEffect upper effect
     * @param params     the String parameters
     */
    public ExShowScreenMessage(NpcStringId npcString, int position, int time, boolean showEffect, String... params) {
        _type = 2;
        _sysMessageId = -1;
        _unk1 = 0x00;
        _unk2 = 0x00;
        _unk3 = 0x00;
        _fade = false;
        _position = position;
        _text = null;
        _time = time;
        _size = 0x00;
        _effect = showEffect;
        _npcString = npcString.getId();
        if (params != null) {
            addStringParameter(params);
        }
    }

    /**
     * Display a Text, System Message or a NPC String on the screen for the given parameters.
     *
     * @param type       0 - System Message, 1 - Text, 2 - NPC String
     * @param messageId  the System Message Id
     * @param position   the position on the screen
     * @param unk1
     * @param size       the font size 0 - normal, 1 - small
     * @param unk2
     * @param unk3
     * @param showEffect upper effect (0 - disabled, 1 enabled) - _position must be 2 (center) otherwise no effect
     * @param time       the display time
     * @param fade       the fade effect (0 - disabled, 1 enabled)
     * @param text       the text to display
     * @param npcString
     * @param params     the String parameters
     */
    public ExShowScreenMessage(int type, int messageId, int position, int unk1, int size, int unk2, int unk3, boolean showEffect, int time, boolean fade, String text, NpcStringId npcString, String params) {
        _type = type;
        _sysMessageId = messageId;
        _unk1 = unk1;
        _unk2 = unk2;
        _unk3 = unk3;
        _fade = fade;
        _position = position;
        _text = text;
        _time = time;
        _size = size;
        _effect = showEffect;
        _npcString = npcString.getId();
    }

    /**
     * String parameter for argument S1,S2,.. in npcstring-e.dat
     *
     * @param params the parameter
     */
    public void addStringParameter(String... params) {
        if (_parameters == null) {
            _parameters = new ArrayList<>();
        }
        for (String param : params) {
            _parameters.add(param);
        }
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_SHOW_SCREEN_MESSAGE);

        writeInt(_type);
        writeInt(_sysMessageId);
        writeInt(_position);
        writeInt(_unk1);
        writeInt(_size);
        writeInt(_unk2);
        writeInt(_unk3);
        writeInt(_effect ? 0x01 : 0x00);
        writeInt(_time);
        writeInt(_fade ? 0x01 : 0x00);
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
