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

import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.SystemMessageId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;
import java.util.Arrays;

/**
 * @param <T>
 * @author UnAfraid
 */
@SuppressWarnings("unchecked")
public abstract class AbstractMessagePacket<T extends AbstractMessagePacket<?>> extends ServerPacket {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractMessagePacket.class);

    private static final SMParam[] EMPTY_PARAM_ARRAY = new SMParam[0];
    private static final byte TYPE_ELEMENTAL_SPIRIT = 26;
    private static final byte TYPE_FACTION_NAME = 24; // c(short), faction id.
    // id 22 d (shared with 1-3,17,22
    // id 21 h
    // id 20 c
    // id 19 c
    // id 18 Q (read same as 6)
    // id 17 shared with 1-3,17,22
    private static final byte TYPE_BYTE = 20;
    private static final byte TYPE_POPUP_ID = 16;
    private static final byte TYPE_CLASS_ID = 15;
    // id 14 dSSSSS
    private static final byte TYPE_SYSTEM_STRING = 13;
    private static final byte TYPE_PLAYER_NAME = 12;
    private static final byte TYPE_DOOR_NAME = 11;
    private static final byte TYPE_INSTANCE_NAME = 10;
    private static final byte TYPE_ELEMENT_NAME = 9;
    // id 8 - ddd
    private static final byte TYPE_ZONE_NAME = 7;
    private static final byte TYPE_LONG_NUMBER = 6;
    private static final byte TYPE_CASTLE_NAME = 5;
    private static final byte TYPE_SKILL_NAME = 4;
    private static final byte TYPE_ITEM_NAME = 3;
    private static final byte TYPE_NPC_NAME = 2;
    private static final byte TYPE_INT_NUMBER = 1;
    private static final byte TYPE_TEXT = 0;

    private final SystemMessageId _smId;
    private SMParam[] _params;
    private int _paramIndex;

    public AbstractMessagePacket(SystemMessageId smId) {
        if (smId == null) {
            throw new NullPointerException("SystemMessageId cannot be null!");
        }
        _smId = smId;
        _params = smId.getParamCount() > 0 ? new SMParam[smId.getParamCount()] : EMPTY_PARAM_ARRAY;
    }

    public final int getId() {
        return _smId.getId();
    }

    public final SystemMessageId getSystemMessageId() {
        return _smId;
    }

    private void append(SMParam param) {
        if (_paramIndex >= _params.length) {
            _params = Arrays.copyOf(_params, _paramIndex + 1);
            _smId.setParamCount(_paramIndex + 1);
            // Mobius: With additional on-screen damage param (popup), length is increased.
            if (param.getType() != TYPE_POPUP_ID) {
                LOGGER.info("Wrong parameter count '" + (_paramIndex + 1) + "' for SystemMessageId: " + _smId);
            }
        }

        _params[_paramIndex++] = param;
    }

    public final T addString(String text) {
        append(new SMParam(TYPE_TEXT, text));
        return (T) this;
    }

    /**
     * Appends a Castle name parameter type, the name will be read from CastleName-e.dat.<br>
     * <ul>
     * <li>1-9 Castle names</li>
     * <li>21 Fortress of Resistance</li>
     * <li>22-33 Clan Hall names</li>
     * <li>34 Devastated Castle</li>
     * <li>35 Bandit Stronghold</li>
     * <li>36-61 Clan Hall names</li>
     * <li>62 Rainbow Springs</li>
     * <li>63 Wild Beast Reserve</li>
     * <li>64 Fortress of the Dead</li>
     * <li>81-89 Territory names</li>
     * <li>90-100 null</li>
     * <li>101-121 Fortress names</li>
     * </ul>
     *
     * @param number the conquerable entity
     * @return the system message with the proper parameter
     */
    public final T addCastleId(int number) {
        append(new SMParam(TYPE_CASTLE_NAME, number));
        return (T) this;
    }

    public final T addInt(int number) {
        append(new SMParam(TYPE_INT_NUMBER, number));
        return (T) this;
    }

    public final T addLong(long number) {
        append(new SMParam(TYPE_LONG_NUMBER, number));
        return (T) this;
    }

    public final T addPcName(Player pc) {
        append(new SMParam(TYPE_PLAYER_NAME, pc.getAppearance().getVisibleName()));
        return (T) this;
    }

    /**
     * ID from doorData.xml
     *
     * @param doorId
     * @return
     */
    public final T addDoorName(int doorId) {
        append(new SMParam(TYPE_DOOR_NAME, doorId));
        return (T) this;
    }

    public final T addNpcName(Npc npc) {
        return addNpcName(npc.getTemplate());
    }

    public final T addNpcName(Summon npc) {
        return addNpcName(npc.getId());
    }

    public final T addNpcName(NpcTemplate template) {
        if (template.isUsingServerSideName()) {
            return addString(template.getName());
        }
        return addNpcName(template.getId());
    }

    public final T addNpcName(int id) {
        append(new SMParam(TYPE_NPC_NAME, 1000000 + id));
        return (T) this;
    }

    public T addItemName(Item item) {
        return addItemName(item.getTemplate());
    }

    public T addItemName(ItemTemplate item) {
        if (item.getDisplayId() != item.getId()) {
            return addString(item.getName());
        }

        append(new SMParam(TYPE_ITEM_NAME, item.getId()));
        return (T) this;
    }

    public final T addItemName(int id) {
        return addItemName(ItemEngine.getInstance().getTemplate(id));
    }

    public final T addZoneName(int x, int y, int z) {
        append(new SMParam(TYPE_ZONE_NAME, new int[]
                {
                        x,
                        y,
                        z
                }));
        return (T) this;
    }

    public final T addSkillName(Skill skill) {
        if (skill.getId() != skill.getDisplayId()) {
            return addString(skill.getName());
        }
        return addSkillName(skill.getId(), skill.getLevel(), skill.getSubLevel());
    }

    public final T addSkillName(int id) {
        return addSkillName(id, 1, 0);
    }

    public final T addSkillName(int id, int lvl, int subLvl) {
        append(new SMParam(TYPE_SKILL_NAME, new int[] { id, lvl, subLvl }));
        return (T) this;
    }

    /**
     * Elemental name - 0(Fire) ...
     *
     * @param type
     * @return
     */
    public final T addAttribute(int type) {
        append(new SMParam(TYPE_ELEMENT_NAME, type));
        return (T) this;
    }

    /**
     * ID from sysstring-e.dat
     *
     * @param type
     * @return
     */
    public final T addSystemString(int type) {
        append(new SMParam(TYPE_SYSTEM_STRING, type));
        return (T) this;
    }

    /**
     * ID from ClassInfo-e.dat
     *
     * @param type
     * @return
     */
    public final T addClassId(int type) {
        append(new SMParam(TYPE_CLASS_ID, type));
        return (T) this;
    }

    public final T addFactionName(int factionId) {
        append(new SMParam(TYPE_FACTION_NAME, factionId));
        return (T) this;
    }

    public final T addPopup(int target, int attacker, int damage) {
        append(new SMParam(TYPE_POPUP_ID, new int[]
                {
                        target,
                        attacker,
                        damage
                }));
        return (T) this;
    }

    public final T addByte(int time) {
        append(new SMParam(TYPE_BYTE, time));
        return (T) this;
    }

    /**
     * Instance name from instantzonedata-e.dat
     *
     * @param type id of instance
     * @return
     */
    public final T addInstanceName(int type) {
        append(new SMParam(TYPE_INSTANCE_NAME, type));
        return (T) this;
    }

    public final T addElementalSpirit(int elementType) {
        append(new SMParam(TYPE_ELEMENTAL_SPIRIT, elementType));
        return (T) this;
    }

    protected void writeParamsSize(int size) {
        writeByte(size);
    }

    protected void writeParamType(int type) {
        writeByte(type);
    }

    protected final void writeMe() {
        writeParamsSize(_params.length);
        for (int i = 0; i < _paramIndex; i++) {
            var param = _params[i];

            writeParamType(param.getType());
            switch (param.getType()) {
                case TYPE_ELEMENT_NAME, TYPE_BYTE, TYPE_FACTION_NAME, TYPE_ELEMENTAL_SPIRIT -> writeByte((byte) param.getIntValue());
                case TYPE_CASTLE_NAME, TYPE_SYSTEM_STRING, TYPE_INSTANCE_NAME, TYPE_CLASS_ID -> writeShort((short) param.getIntValue());
                case TYPE_ITEM_NAME, TYPE_INT_NUMBER, TYPE_NPC_NAME, TYPE_DOOR_NAME -> writeInt(param.getIntValue());
                case TYPE_LONG_NUMBER -> writeLong(param.getLongValue());
                case TYPE_TEXT, TYPE_PLAYER_NAME -> writeString(param.getStringValue());
                case TYPE_SKILL_NAME -> {
                    final int[] array = param.getIntArrayValue();
                    writeInt(array[0]); // skill id
                    writeShort( array[1]); // skill level
                    writeShort(array[2]); // skill sub level
                }
                case TYPE_POPUP_ID, TYPE_ZONE_NAME -> {
                    final int[] array = param.getIntArrayValue();
                    writeInt(array[0]); // x
                    writeInt(array[1]); // y
                    writeInt(array[2]); // z
                }
            }
        }
    }


    public final void printMe(PrintStream out) {
        out.println(0x62);

        out.println(_smId.getId());
        out.println(_params.length);

        for (SMParam param : _params) {
            switch (param.getType()) {
                case TYPE_TEXT:
                case TYPE_PLAYER_NAME: {
                    out.println(param.getStringValue());
                    break;
                }

                case TYPE_LONG_NUMBER: {
                    out.println(param.getLongValue());
                    break;
                }

                case TYPE_ITEM_NAME:
                case TYPE_CASTLE_NAME:
                case TYPE_INT_NUMBER:
                case TYPE_NPC_NAME:
                case TYPE_ELEMENT_NAME:
                case TYPE_SYSTEM_STRING:
                case TYPE_INSTANCE_NAME:
                case TYPE_DOOR_NAME:
                case TYPE_CLASS_ID: {
                    out.println(param.getIntValue());
                    break;
                }

                case TYPE_POPUP_ID: {
                    final int[] array = param.getIntArrayValue();
                    out.println(array[0]); // Target
                    out.println(array[1]); // Attacker
                    out.println(array[2]); // Value
                    break;
                }

                case TYPE_SKILL_NAME: {
                    final int[] array = param.getIntArrayValue();
                    out.println(array[0]); // SkillId
                    out.println(array[1]); // SkillLevel
                    out.println(array[2]); // SkillSubLevel
                    break;
                }

                case TYPE_ZONE_NAME: {
                    final int[] array = param.getIntArrayValue();
                    out.println(array[0]); // x
                    out.println(array[1]); // y
                    out.println(array[2]); // z
                    break;
                }
            }
        }
    }

    private static final class SMParam {
        private final byte _type;
        private final Object _value;

        public SMParam(byte type, Object value) {
            _type = type;
            _value = value;
        }

        public final byte getType() {
            return _type;
        }

        public final String getStringValue() {
            return (String) _value;
        }

        public final int getIntValue() {
            return ((Integer) _value).intValue();
        }

        public final long getLongValue() {
            return ((Long) _value).longValue();
        }

        public final int[] getIntArrayValue() {
            return (int[]) _value;
        }
    }
}
