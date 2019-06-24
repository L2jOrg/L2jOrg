package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.sql.impl.CharNameTable;
import org.l2j.gameserver.network.serverpackets.ExIsCharNameCreatable;
import org.l2j.gameserver.util.GameUtils;

/**
 * @author UnAfraid
 */
public class RequestCharacterNameCreatable extends ClientPacket {
    public static int CHARACTER_CREATE_FAILED = 1;
    public static int NAME_ALREADY_EXISTS = 2;
    public static int INVALID_LENGTH = 3;
    public static int INVALID_NAME = 4;
    public static int CANNOT_CREATE_SERVER = 5;
    private String _name;
    private int result;

    @Override
    public void readImpl() {
        _name = readString();
    }

    @Override
    public void runImpl() {
        final int charId = CharNameTable.getInstance().getIdByName(_name);

        if (!GameUtils.isAlphaNumeric(_name) || !isValidName(_name)) {
            result = INVALID_NAME;
        } else if (charId > 0) {
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