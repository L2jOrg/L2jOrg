/*
 * Copyright © 2019-2021 L2JOrg
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
package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.data.database.data.PlayerData;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.data.xml.impl.PlayerTemplateData;
import org.l2j.gameserver.model.actor.instance.PlayerFactory;
import org.l2j.gameserver.model.actor.templates.PlayerTemplate;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.network.serverpackets.CharCreateFail;
import org.l2j.gameserver.network.serverpackets.CharCreateOk;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.settings.ServerSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.isNull;
import static org.l2j.gameserver.network.serverpackets.CharCreateFail.CharacterCreateFailReason.*;

public final class CharacterCreate extends ClientPacket {

    private static final Logger LOGGER_ACCOUNTING = LoggerFactory.getLogger("accounting");
    private static final Logger LOGGER = LoggerFactory.getLogger(CharacterCreate.class);

    private static final Object NAME_VERIFY_LOCKER = new Object();

    private String name;
    private boolean female;
    private int classId;
    private byte hairStyle;
    private byte hairColor;
    private byte face;

    @Override
    public void readImpl() {
        name = readString();
        readInt(); // Race
        female = readInt() != 0;
        classId = readInt();

        // Don't trust these values from client
        readInt(); // INT
        readInt(); // STR
        readInt(); // CON
        readInt(); // MEN
        readInt(); // DEX
        readInt(); // WIT

        hairStyle = (byte) readInt();
        hairColor = (byte) readInt();
        face = (byte) readInt();
    }

    @Override
    public void runImpl() {
        if (name.length() < 1 || name.length() > 16) {
            client.sendPacket(new CharCreateFail(REASON_16_ENG_CHARS));
            return;
        }

        if (face > 2 || face < 0) {
            LOGGER.warn("Character Creation Failure: Character face {} is invalid. Possible client hack {}", face , client);
            client.sendPacket(new CharCreateFail(REASON_CREATION_FAILED));
            return;
        }

        if (hairStyle < 0 || ( !female && hairStyle > 4 ) || (female && hairStyle > 6)) {
            LOGGER.warn("Character Creation Failure: Character hair style {} is invalid. Possible client hack {}", hairStyle, client);
            client.sendPacket(new CharCreateFail(REASON_CREATION_FAILED));
            return;
        }

        if (hairColor > 3 || hairColor < 0) {
            LOGGER.warn("Character Creation Failure: Character hair color {} is invalid. Possible client hack {}", hairColor , client);
            client.sendPacket(new CharCreateFail(REASON_CREATION_FAILED));
            return;
        }

        if (!ServerSettings.acceptPlayerName(name)) {
            client.sendPacket(new CharCreateFail(REASON_INCORRECT_NAME));
            return;
        }


        if (!ServerSettings.allowPlayersCount(client.getPlayerCount())) {
            client.sendPacket(new CharCreateFail(REASON_TOO_MANY_CHARACTERS));
            return;
        }

        PlayerTemplate template = PlayerTemplateData.getInstance().getTemplate(classId);
        if (isNull(template) || (ClassId.getClassId(classId).level() > 0)) {
            LOGGER.warn("Character Creation Failure: Character class {} is invalid. Possible client hack {}", classId , client);
            client.sendPacket(new CharCreateFail(REASON_CREATION_FAILED));
            return;
        }

        /*
         * DrHouse: Since checks for duplicate names are done using SQL, lock must be held until data is written to DB as well.
         */
        PlayerData data;
        synchronized (NAME_VERIFY_LOCKER) {
            if (PlayerNameTable.getInstance().doesCharNameExist(name)) {
                client.sendPacket(new CharCreateFail(REASON_NAME_ALREADY_EXISTS));
                return;
            }

            data = PlayerData.of(client.getAccountName(), name, classId, face, hairColor, hairStyle, female);
            PlayerFactory.savePlayerData(template, data);

            if (GeneralSettings.cachePlayersName()) {
                PlayerNameTable.getInstance().addName(data.getCharId(), data.getName());
            }
        }

        PlayerFactory.init(client, data);
        client.sendPacket(CharCreateOk.STATIC_PACKET);
        // TODO set active player info
        LOGGER_ACCOUNTING.info("Created new character {}, {}", name, client);
    }
}
