package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.database.model.Character;
import org.l2j.gameserver.data.sql.impl.CharNameTable;
import org.l2j.gameserver.data.xml.impl.*;
import org.l2j.gameserver.idfactory.IdFactory;
import org.l2j.gameserver.model.L2SkillLearn;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.actor.stat.PcStat;
import org.l2j.gameserver.model.actor.templates.L2PcTemplate;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.events.Containers;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerCreate;
import org.l2j.gameserver.model.items.PcItemTemplate;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.network.Disconnection;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.serverpackets.CharCreateFail;
import org.l2j.gameserver.network.serverpackets.CharCreateOk;
import org.l2j.gameserver.network.serverpackets.CharSelectionInfo;
import org.l2j.gameserver.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.List;

import static java.util.Objects.isNull;

public final class CharacterCreate extends IClientIncomingPacket {

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
    public void readImpl(ByteBuffer packet) {
        name = readString(packet);
        packet.getInt(); // Race
        female = packet.getInt() != 0;
        classId = packet.getInt();

        // Don't trust these values from client
        packet.getInt(); // INT
        packet.getInt(); // STR
        packet.getInt(); // CON
        packet.getInt(); // MEN
        packet.getInt(); // DEX
        packet.getInt(); // WIT

        hairStyle = (byte) packet.getInt();
        hairColor = (byte) packet.getInt();
        face = (byte) packet.getInt();
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
        if (!Util.isAlphaNumeric(name) || !isValidName(name)) {
            client.sendPacket(new CharCreateFail(CharCreateFail.REASON_INCORRECT_NAME));
            return;
        }

        L2PcInstance newChar;
        L2PcTemplate template;

        /*
         * DrHouse: Since checks for duplicate names are done using SQL, lock must be held until data is written to DB as well.
         */
        synchronized (CharNameTable.getInstance()) {
            if (Config.MAX_CHARACTERS_NUMBER_PER_ACCOUNT != 0 && (CharNameTable.getInstance().getAccountCharacterCount(client.getAccountName()) >= Config.MAX_CHARACTERS_NUMBER_PER_ACCOUNT)) {
                client.sendPacket(new CharCreateFail(CharCreateFail.REASON_TOO_MANY_CHARACTERS));
                return;
            }

            if (CharNameTable.getInstance().doesCharNameExist(name)) {
                client.sendPacket(new CharCreateFail(CharCreateFail.REASON_NAME_ALREADY_EXISTS));
                return;
            }

            template = PlayerTemplateData.getInstance().getTemplate(classId);
            if ((template == null) || (ClassId.getClassId(classId).level() > 0)) {
                client.sendPacket(new CharCreateFail(CharCreateFail.REASON_CREATION_FAILED));
                return;
            }

            var character = new Character();
            character.setId(IdFactory.getInstance().getNextId());
            character.setName(name);
            character.setClassId(classId);
            character.setFace(face);
            character.setHairColor(hairColor);
            character.setHairStyle(hairStyle);
            character.setFemale(female);
            character.setAccountName(client.getAccountName());

            newChar = L2PcInstance.create(character, template);


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

    private void initNewChar(L2GameClient client, L2PcInstance newChar) {
        L2World.getInstance().addObject(newChar);

        if (Config.STARTING_ADENA > 0) {
            newChar.addAdena("Init", Config.STARTING_ADENA, null, false);
        }

        final L2PcTemplate template = newChar.getTemplate();

        if (Config.CUSTOM_STARTING_LOC) {
            final Location createLoc = new Location(Config.CUSTOM_STARTING_LOC_X, Config.CUSTOM_STARTING_LOC_Y, Config.CUSTOM_STARTING_LOC_Z);
            newChar.setXYZInvisible(createLoc.getX(), createLoc.getY(), createLoc.getZ());
        } else {
            final Location createLoc = template.getCreationPoint();
            newChar.setXYZInvisible(createLoc.getX(), createLoc.getY(), createLoc.getZ());
        }
        newChar.setTitle("");

        if (Config.ENABLE_VITALITY) {
            newChar.setVitalityPoints(Math.min(Config.STARTING_VITALITY_POINTS, PcStat.MAX_VITALITY_POINTS), true);
        }
        if (Config.STARTING_LEVEL > 1) {
            newChar.getStat().addLevel((byte) (Config.STARTING_LEVEL - 1));
        }
        if (Config.STARTING_SP > 0) {
            newChar.getStat().addSp(Config.STARTING_SP);
        }

        final List<PcItemTemplate> initialItems = InitialEquipmentData.getInstance().getEquipmentList(newChar.getClassId());
        if (initialItems != null) {
            for (PcItemTemplate ie : initialItems) {
                final L2ItemInstance item = newChar.getInventory().addItem("Init", ie.getId(), ie.getCount(), newChar, null);
                if (item == null) {
                    LOGGER.warn("Could not create item during char creation: itemId " + ie.getId() + ", amount " + ie.getCount() + ".");
                    continue;
                }

                if (item.isEquipable() && ie.isEquipped()) {
                    newChar.getInventory().equipItem(item);
                }
            }
        }

        for (L2SkillLearn skill : SkillTreesData.getInstance().getAvailableSkills(newChar, newChar.getClassId(), false, true)) {
            newChar.addSkill(SkillData.getInstance().getSkill(skill.getSkillId(), skill.getSkillLevel()), true);
        }

        // Register all shortcuts for actions, skills and items for this new character.
        InitialShortcutData.getInstance().registerAllShortcuts(newChar);

        EventDispatcher.getInstance().notifyEvent(new OnPlayerCreate(newChar, newChar.getObjectId(), newChar.getName(), client), Containers.Players());

        newChar.setOnlineStatus(true, false);
        if (Config.SHOW_GOD_VIDEO_INTRO) {
            newChar.getVariables().set("intro_god_video", true);
        }
        Disconnection.of(client, newChar).storeMe().deleteMe();

        final CharSelectionInfo cl = new CharSelectionInfo(client.getAccountName(), client.getSessionId().getGameServerSessionId());
        client.setCharSelection(cl.getCharInfo());
    }
}
