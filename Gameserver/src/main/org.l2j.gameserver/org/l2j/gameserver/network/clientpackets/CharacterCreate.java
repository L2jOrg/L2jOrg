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
package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.database.data.PlayerData;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.data.xml.impl.InitialEquipmentData;
import org.l2j.gameserver.data.xml.impl.InitialShortcutData;
import org.l2j.gameserver.data.xml.impl.PlayerTemplateData;
import org.l2j.gameserver.idfactory.IdFactory;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.stat.PlayerStats;
import org.l2j.gameserver.model.actor.templates.PlayerTemplate;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.Listeners;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerCreate;
import org.l2j.gameserver.model.item.PcItemTemplate;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.Disconnection;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.CharCreateFail;
import org.l2j.gameserver.network.serverpackets.CharCreateOk;
import org.l2j.gameserver.network.serverpackets.CharSelectionInfo;
import org.l2j.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

import static java.util.Objects.isNull;
import static org.l2j.commons.util.Util.isAlphaNumeric;

public final class CharacterCreate extends ClientPacket {

    private static final Logger LOGGER_ACCOUNTING = LoggerFactory.getLogger("accounting");
    private static final Logger LOGGER = LoggerFactory.getLogger(CharacterCreate.class);

    // cSdddddddddddd
    private String name;
    private boolean female;
    private int classId;
    private byte hairStyle;
    private byte hairColor;
    private byte face;

    private static boolean isValidName(String text) {
        return Config.CHARNAME_TEMPLATE_PATTERN.matcher(text).matches();
    }

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
        // Last Verified: May 30, 2009 - Gracia Final - Players are able to create characters with names consisting of as little as 1,2,3 letter/number combinations.
        if ((name.length() < 1) || (name.length() > 16)) {
            client.sendPacket(new CharCreateFail(CharCreateFail.REASON_16_ENG_CHARS));
            return;
        }

        if ((face > 2) || (face < 0)) {
            LOGGER.warn("Character Creation Failure: Character face {} is invalid. Possible client hack {}", face , client);
            client.sendPacket(new CharCreateFail(CharCreateFail.REASON_CREATION_FAILED));
            return;
        }

        if ((hairStyle < 0) || ( (!female) && (hairStyle > 4) ) || ((female) && (hairStyle > 6))) {
            LOGGER.warn("Character Creation Failure: Character hair style {} is invalid. Possible client hack {}", hairStyle, client);
            client.sendPacket(new CharCreateFail(CharCreateFail.REASON_CREATION_FAILED));
            return;
        }

        if ((hairColor > 3) || (hairColor < 0)) {
            LOGGER.warn("Character Creation Failure: Character hair color {} is invalid. Possible client hack {}", hairColor , client);
            client.sendPacket(new CharCreateFail(CharCreateFail.REASON_CREATION_FAILED));
            return;
        }

        // Last Verified: May 30, 2009 - Gracia Final
        if (!isAlphaNumeric(name) || !isValidName(name)) {
            client.sendPacket(new CharCreateFail(CharCreateFail.REASON_INCORRECT_NAME));
            return;
        }

        Player newChar;
        PlayerTemplate template;

        /*
         * DrHouse: Since checks for duplicate names are done using SQL, lock must be held until data is written to DB as well.
         */
        synchronized (PlayerNameTable.getInstance()) {
            if (Config.MAX_CHARACTERS_NUMBER_PER_ACCOUNT != 0 && (PlayerNameTable.getInstance().getAccountCharacterCount(client.getAccountName()) >= Config.MAX_CHARACTERS_NUMBER_PER_ACCOUNT)) {
                client.sendPacket(new CharCreateFail(CharCreateFail.REASON_TOO_MANY_CHARACTERS));
                return;
            }

            if (PlayerNameTable.getInstance().doesCharNameExist(name)) {
                client.sendPacket(new CharCreateFail(CharCreateFail.REASON_NAME_ALREADY_EXISTS));
                return;
            }

            template = PlayerTemplateData.getInstance().getTemplate(classId);
            if ((template == null) || (ClassId.getClassId(classId).level() > 0)) {
                client.sendPacket(new CharCreateFail(CharCreateFail.REASON_CREATION_FAILED));
                return;
            }

            var character = new PlayerData();
            character.setId(IdFactory.getInstance().getNextId());
            character.setName(name);
            character.setBaseClass(classId);
            character.setClassId(classId);
            character.setFace(face);
            character.setHairColor(hairColor);
            character.setHairStyle(hairStyle);
            character.setFemale(female);
            character.setAccountName(client.getAccountName());
            character.setCreateDate(LocalDate.now());

            newChar = Player.create(character, template);


        }

        if(isNull(newChar)) {
            client.sendPacket(new CharCreateFail(CharCreateFail.REASON_CREATION_FAILED));
            return;
        }

        // HP and MP are at maximum and CP is zero by default.
        newChar.setCurrentHp(newChar.getMaxHp());
        newChar.setCurrentMp(newChar.getMaxMp());

        initNewChar(client, newChar);
        client.sendPacket(CharCreateOk.STATIC_PACKET);

        LOGGER_ACCOUNTING.info("Created new character {}, {}", newChar, client);
    }

    private void initNewChar(GameClient client, Player newChar) {
        World.getInstance().addObject(newChar);

        if (Config.STARTING_ADENA > 0) {
            newChar.addAdena("Init", Config.STARTING_ADENA, null, false);
        }

        final PlayerTemplate template = newChar.getTemplate();

        if (Config.CUSTOM_STARTING_LOC) {
            final Location createLoc = new Location(Config.CUSTOM_STARTING_LOC_X, Config.CUSTOM_STARTING_LOC_Y, Config.CUSTOM_STARTING_LOC_Z);
            newChar.setXYZInvisible(createLoc.getX(), createLoc.getY(), createLoc.getZ());
        } else {
            final Location createLoc = template.getCreationPoint();
            newChar.setXYZInvisible(createLoc.getX(), createLoc.getY(), createLoc.getZ());
        }
        newChar.setTitle("");

        if (Config.ENABLE_VITALITY) {
            newChar.setVitalityPoints(Math.min(Config.STARTING_VITALITY_POINTS, PlayerStats.MAX_VITALITY_POINTS), true);
        }
        if (Config.STARTING_LEVEL > 1) {
            newChar.getStats().addLevel((byte) (Config.STARTING_LEVEL - 1));
        }
        if (Config.STARTING_SP > 0) {
            newChar.getStats().addSp(Config.STARTING_SP);
        }

        final List<PcItemTemplate> initialItems = InitialEquipmentData.getInstance().getEquipmentList(newChar.getClassId());
        if (initialItems != null) {
            for (PcItemTemplate ie : initialItems) {
                final Item item = newChar.getInventory().addItem("Init", ie.getId(), ie.getCount(), newChar, null);
                if (item == null) {
                    LOGGER.warn("Could not create item during char creation: itemId " + ie.getId() + ", amount " + ie.getCount() + ".");
                    continue;
                }

                if (item.isEquipable() && ie.isEquipped()) {
                    newChar.getInventory().equipItem(item);
                }
            }
        }

        newChar.giveAvailableAutoGetSkills();

        // Register all shortcuts for actions, skills and items for this new character.
        InitialShortcutData.getInstance().registerAllShortcuts(newChar);

        EventDispatcher.getInstance().notifyEvent(new OnPlayerCreate(newChar, newChar.getObjectId(), newChar.getName(), client), Listeners.players());

        newChar.setOnlineStatus(true, false);

        Disconnection.of(client, newChar).storeMe().deleteMe();

        final CharSelectionInfo cl = new CharSelectionInfo(client.getAccountName(), client.getSessionId().getGameServerSessionId());
        client.setCharSelection(cl.getCharInfo());
    }
}
