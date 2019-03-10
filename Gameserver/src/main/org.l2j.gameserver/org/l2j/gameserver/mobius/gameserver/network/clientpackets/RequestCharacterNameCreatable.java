package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.Config;
import org.l2j.gameserver.mobius.gameserver.data.sql.impl.CharNameTable;
import org.l2j.gameserver.mobius.gameserver.data.xml.impl.FakePlayerData;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ExIsCharNameCreatable;
import org.l2j.gameserver.mobius.gameserver.util.Util;

import java.nio.ByteBuffer;

/**
 * @author UnAfraid
 */
public class RequestCharacterNameCreatable extends IClientIncomingPacket {
    public static int CHARACTER_CREATE_FAILED = 1;
    public static int NAME_ALREADY_EXISTS = 2;
    public static int INVALID_LENGTH = 3;
    public static int INVALID_NAME = 4;
    public static int CANNOT_CREATE_SERVER = 5;
    private String _name;
    private int result;

    @Override
    public void readImpl(ByteBuffer packet) {
        _name = readString(packet);
    }

    @Override
    public void runImpl() {
        final int charId = CharNameTable.getInstance().getIdByName(_name);

        if (!Util.isAlphaNumeric(_name) || !isValidName(_name)) {
            result = INVALID_NAME;
        } else if (charId > 0) {
            result = NAME_ALREADY_EXISTS;
        } else if (FakePlayerData.getInstance().getProperName(_name) != null) {
            result = NAME_ALREADY_EXISTS;
        } else if (_name.length() > 16) {
            result = INVALID_LENGTH;
        } else {
            result = -1;
        }

        client.sendPacket(new ExIsCharNameCreatable(result));
    }

    private boolean isValidName(String text) {
        return Config.CHARNAME_TEMPLATE_PATTERN.matcher(text).matches();
    }
}