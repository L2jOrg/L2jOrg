package org.l2j.gameserver.network.serverpackets;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.data.xml.impl.ExperienceData;
import org.l2j.gameserver.idfactory.IdFactory;
import org.l2j.gameserver.model.CharSelectInfoPackage;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.VariationInstance;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Hero;
import org.l2j.gameserver.model.itemcontainer.Inventory;
import org.l2j.gameserver.network.Disconnection;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;


public class CharSelectionInfo extends ServerPacket {
    private static final int[] PAPERDOLL_ORDER = new int[]
            {
                    Inventory.PAPERDOLL_UNDER,
                    Inventory.PAPERDOLL_REAR,
                    Inventory.PAPERDOLL_LEAR,
                    Inventory.PAPERDOLL_NECK,
                    Inventory.PAPERDOLL_RFINGER,
                    Inventory.PAPERDOLL_LFINGER,
                    Inventory.PAPERDOLL_HEAD,
                    Inventory.PAPERDOLL_RHAND,
                    Inventory.PAPERDOLL_LHAND,
                    Inventory.PAPERDOLL_GLOVES,
                    Inventory.PAPERDOLL_CHEST,
                    Inventory.PAPERDOLL_LEGS,
                    Inventory.PAPERDOLL_FEET,
                    Inventory.PAPERDOLL_CLOAK,
                    Inventory.PAPERDOLL_RHAND,
                    Inventory.PAPERDOLL_HAIR,
                    Inventory.PAPERDOLL_HAIR2,
                    Inventory.PAPERDOLL_RBRACELET,
                    Inventory.PAPERDOLL_LBRACELET,
                    Inventory.PAPERDOLL_AGATHION1, // 152
                    Inventory.PAPERDOLL_AGATHION2, // 152
                    Inventory.PAPERDOLL_AGATHION3, // 152
                    Inventory.PAPERDOLL_AGATHION4, // 152
                    Inventory.PAPERDOLL_AGATHION5, // 152
                    Inventory.TALISMAN1,
                    Inventory.TALISMAN2,
                    Inventory.TALISMAN3,
                    Inventory.TALISMAN4,
                    Inventory.TALISMAN5,
                    Inventory.TALISMAN6,
                    Inventory.PAPERDOLL_BELT,
                    Inventory.PAPERDOLL_BROOCH,
                    Inventory.PAPERDOLL_BROOCH_JEWEL1,
                    Inventory.PAPERDOLL_BROOCH_JEWEL2,
                    Inventory.PAPERDOLL_BROOCH_JEWEL3,
                    Inventory.PAPERDOLL_BROOCH_JEWEL4,
                    Inventory.PAPERDOLL_BROOCH_JEWEL5,
                    Inventory.PAPERDOLL_BROOCH_JEWEL6,
                    Inventory.PAPERDOLL_ARTIFACT_BOOK, // 152
                    Inventory.PAPERDOLL_ARTIFACT1, // 152
                    Inventory.PAPERDOLL_ARTIFACT2, // 152
                    Inventory.PAPERDOLL_ARTIFACT3, // 152
                    Inventory.PAPERDOLL_ARTIFACT4, // 152
                    Inventory.PAPERDOLL_ARTIFACT5, // 152
                    Inventory.PAPERDOLL_ARTIFACT6, // 152
                    Inventory.PAPERDOLL_ARTIFACT7, // 152
                    Inventory.PAPERDOLL_ARTIFACT8, // 152
                    Inventory.PAPERDOLL_ARTIFACT9, // 152
                    Inventory.PAPERDOLL_ARTIFACT10, // 152
                    Inventory.PAPERDOLL_ARTIFACT11, // 152
                    Inventory.PAPERDOLL_ARTIFACT12, // 152
                    Inventory.PAPERDOLL_ARTIFACT13, // 152
                    Inventory.PAPERDOLL_ARTIFACT14, // 152
                    Inventory.PAPERDOLL_ARTIFACT15, // 152
                    Inventory.PAPERDOLL_ARTIFACT16, // 152
                    Inventory.PAPERDOLL_ARTIFACT17, // 152
                    Inventory.PAPERDOLL_ARTIFACT18, // 152
                    Inventory.PAPERDOLL_ARTIFACT19, // 152
                    Inventory.PAPERDOLL_ARTIFACT20, // 152
                    Inventory.PAPERDOLL_ARTIFACT21 // 152
            };

    private static final Logger LOGGER = LoggerFactory.getLogger(CharSelectionInfo.class);
    private final String _loginName;
    private final int _sessionId;
    private final CharSelectInfoPackage[] _characterPackages;
    private int _activeId;

    /**
     * Constructor for CharSelectionInfo.
     *
     * @param loginName
     * @param sessionId
     */
    public CharSelectionInfo(String loginName, int sessionId) {
        _sessionId = sessionId;
        _loginName = loginName;
        _characterPackages = loadCharacterSelectInfo(_loginName);
        _activeId = -1;
    }

    public CharSelectionInfo(String loginName, int sessionId, int activeId) {
        _sessionId = sessionId;
        _loginName = loginName;
        _characterPackages = loadCharacterSelectInfo(_loginName);
        _activeId = activeId;
    }

    private static CharSelectInfoPackage[] loadCharacterSelectInfo(String loginName) {
        CharSelectInfoPackage charInfopackage;
        final List<CharSelectInfoPackage> characterList = new LinkedList<>();

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT * FROM characters WHERE account_name=? ORDER BY createDate")) {
            statement.setString(1, loginName);
            try (ResultSet charList = statement.executeQuery()) {
                while (charList.next()) // fills the package
                {
                    charInfopackage = restoreChar(charList);
                    if (charInfopackage != null) {
                        characterList.add(charInfopackage);

                        final Player player = World.getInstance().findPlayer(charInfopackage.getObjectId());
                        if (player != null) {
                            IdFactory.getInstance().releaseId(player.getObjectId());
                            Disconnection.of(player).storeMe().deleteMe();
                        }
                    }
                }
            }
            return characterList.toArray(new CharSelectInfoPackage[characterList.size()]);
        } catch (Exception e) {
            LOGGER.warn("Could not restore char info: " + e.getMessage(), e);
        }
        return new CharSelectInfoPackage[0];
    }

    private static void loadCharacterSubclassInfo(CharSelectInfoPackage charInfopackage, int ObjectId, int activeClassId) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT exp, sp, level, vitality_points FROM character_subclasses WHERE charId=? AND class_id=? ORDER BY charId")) {
            statement.setInt(1, ObjectId);
            statement.setInt(2, activeClassId);
            try (ResultSet charList = statement.executeQuery()) {
                if (charList.next()) {
                    charInfopackage.setExp(charList.getLong("exp"));
                    charInfopackage.setSp(charList.getLong("sp"));
                    charInfopackage.setLevel(charList.getInt("level"));
                    charInfopackage.setVitalityPoints(charList.getInt("vitality_points"));
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Could not restore char subclass info: " + e.getMessage(), e);
        }
    }

    private static CharSelectInfoPackage restoreChar(ResultSet chardata) throws Exception {
        final int objectId = chardata.getInt("charId");
        final String name = chardata.getString("char_name");

        // See if the char must be deleted
        final long deletetime = chardata.getLong("deletetime");
        if (deletetime > 0) {
            if (System.currentTimeMillis() > deletetime) {
                final Clan clan = ClanTable.getInstance().getClan(chardata.getInt("clanid"));
                if (clan != null) {
                    clan.removeClanMember(objectId, 0);
                }

                GameClient.deleteCharByObjId(objectId);
                return null;
            }
        }

        final CharSelectInfoPackage charInfopackage = new CharSelectInfoPackage(objectId, name);
        charInfopackage.setAccessLevel(chardata.getInt("accesslevel"));
        charInfopackage.setLevel(chardata.getInt("level"));
        charInfopackage.setMaxHp(chardata.getInt("maxhp"));
        charInfopackage.setCurrentHp(chardata.getDouble("curhp"));
        charInfopackage.setMaxMp(chardata.getInt("maxmp"));
        charInfopackage.setCurrentMp(chardata.getDouble("curmp"));
        charInfopackage.setReputation(chardata.getInt("reputation"));
        charInfopackage.setPkKills(chardata.getInt("pkkills"));
        charInfopackage.setPvPKills(chardata.getInt("pvpkills"));
        charInfopackage.setFace(chardata.getInt("face"));
        charInfopackage.setHairStyle(chardata.getInt("hairstyle"));
        charInfopackage.setHairColor(chardata.getInt("haircolor"));
        charInfopackage.setSex(chardata.getInt("sex"));

        charInfopackage.setExp(chardata.getLong("exp"));
        charInfopackage.setSp(chardata.getLong("sp"));
        charInfopackage.setVitalityPoints(chardata.getInt("vitality_points"));
        charInfopackage.setClanId(chardata.getInt("clanid"));

        charInfopackage.setRace(chardata.getInt("race"));

        final int baseClassId = chardata.getInt("base_class");
        final int activeClassId = chardata.getInt("classid");

        charInfopackage.setX(chardata.getInt("x"));
        charInfopackage.setY(chardata.getInt("y"));
        charInfopackage.setZ(chardata.getInt("z"));


        if (Config.MULTILANG_ENABLE) {
            String lang = chardata.getString("language");
            if (!Config.MULTILANG_ALLOWED.contains(lang)) {
                lang = Config.MULTILANG_DEFAULT;
            }
            charInfopackage.setHtmlPrefix("data/lang/" + lang + "/");
        }

        // if is in subclass, load subclass exp, sp, lvl info
        if (baseClassId != activeClassId) {
            loadCharacterSubclassInfo(charInfopackage, objectId, activeClassId);
        }

        charInfopackage.setClassId(activeClassId);

        // Get the augmentation id for equipped weapon
        int weaponObjId = charInfopackage.getPaperdollObjectId(Inventory.PAPERDOLL_RHAND);
        if (weaponObjId < 1) {
            weaponObjId = charInfopackage.getPaperdollObjectId(Inventory.PAPERDOLL_RHAND);
        }

        if (weaponObjId > 0) {
            try (Connection con = DatabaseFactory.getInstance().getConnection();
                 PreparedStatement statement = con.prepareStatement("SELECT mineralId,option1,option2 FROM item_variations WHERE itemId=?")) {
                statement.setInt(1, weaponObjId);
                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        int mineralId = result.getInt("mineralId");
                        int option1 = result.getInt("option1");
                        int option2 = result.getInt("option2");
                        if ((option1 != -1) && (option2 != -1)) {
                            charInfopackage.setAugmentation(new VariationInstance(mineralId, option1, option2));
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.warn("Could not restore augmentation info: " + e.getMessage(), e);
            }
        }

        // Check if the base class is set to zero and also doesn't match with the current active class, otherwise send the base class ID. This prevents chars created before base class was introduced from being displayed incorrectly.
        if ((baseClassId == 0) && (activeClassId > 0)) {
            charInfopackage.setBaseClassId(activeClassId);
        } else {
            charInfopackage.setBaseClassId(baseClassId);
        }

        charInfopackage.setDeleteTimer(deletetime);
        charInfopackage.setLastAccess(chardata.getLong("lastAccess"));
        charInfopackage.setNoble(chardata.getInt("nobless") == 1);
        return charInfopackage;
    }

    public CharSelectInfoPackage[] getCharInfo() {
        return _characterPackages;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.CHARACTER_SELECTION_INFO);

        final int size = _characterPackages.length;
        writeInt(size); // Created character count

        writeInt(Config.MAX_CHARACTERS_NUMBER_PER_ACCOUNT); // Can prevent players from creating new characters (if 0); (if 1, the client will ask if chars may be created (0x13) Response: (0x0D) )
        writeByte((byte)(size == Config.MAX_CHARACTERS_NUMBER_PER_ACCOUNT ? 0x01 : 0x00)); // if 1 can't create new char
        writeByte((byte) 0x01); // 0=can't play, 1=can play free until level 85, 2=100% free play
        writeInt(0x02); // if 1, Korean client
        writeByte((byte) 0x00); // Gift message for inactive accounts // 152
        writeByte((byte) 0x00); // Balthus Knights, if 1 suggests premium account

        long lastAccess = 0;
        if (_activeId == -1) {
            for (int i = 0; i < size; i++) {
                if (lastAccess < _characterPackages[i].getLastAccess()) {
                    lastAccess = _characterPackages[i].getLastAccess();
                    _activeId = i;
                }
            }
        }
        for (int i = 0; i < size; i++) {
            final CharSelectInfoPackage charInfoPackage = _characterPackages[i];

            writeString(charInfoPackage.getName()); // Character name
            writeInt(charInfoPackage.getObjectId()); // Character ID
            writeString(_loginName); // Account name
            writeInt(_sessionId); // Account ID
            writeInt(0x00); // Pledge ID
            writeInt(0x00); // Builder level

            writeInt(charInfoPackage.getSex()); // Sex
            writeInt(charInfoPackage.getRace()); // Race

            if (charInfoPackage.getClassId() == charInfoPackage.getBaseClassId()) {
                writeInt(charInfoPackage.getClassId());
            } else {
                writeInt(charInfoPackage.getBaseClassId());
            }

            writeInt(0x01); // GameServerName

            writeInt(charInfoPackage.getX());
            writeInt(charInfoPackage.getY());
            writeInt(charInfoPackage.getZ());
            writeDouble(charInfoPackage.getCurrentHp());
            writeDouble(charInfoPackage.getCurrentMp());

            writeLong(charInfoPackage.getSp());
            writeLong(charInfoPackage.getExp());
            writeDouble((float) (charInfoPackage.getExp() - ExperienceData.getInstance().getExpForLevel(charInfoPackage.getLevel())) / (ExperienceData.getInstance().getExpForLevel(charInfoPackage.getLevel() + 1) - ExperienceData.getInstance().getExpForLevel(charInfoPackage.getLevel()))); // High
            // Five
            writeInt(charInfoPackage.getLevel());

            writeInt(charInfoPackage.getReputation());
            writeInt(charInfoPackage.getPkKills());
            writeInt(charInfoPackage.getPvPKills());

            writeInt(0x00);
            writeInt(0x00);
            writeInt(0x00);
            writeInt(0x00);
            writeInt(0x00);
            writeInt(0x00);
            writeInt(0x00);

            writeInt(0x00); // Ertheia
            writeInt(0x00); // Ertheia

            for (int slot : getPaperdollOrder()) {
                writeInt(charInfoPackage.getPaperdollItemId(slot));
            }

            writeInt(0x00); // RHAND Visual ID not Used on Classic
            writeInt(0x00); // LHAND Visual ID not Used on Classic
            writeInt(0x00); // GLOVES Visual ID not Used on Classic
            writeInt(0x00); // CHEST Visual ID not Used on Classic
            writeInt(0x00); // LEGS Visual ID not Used on Classic
            writeInt(0x00); // FEET Visual ID not Used on Classic
            writeInt(0x00); // RHAND Visual ID not Used on Classic
            writeInt(0x00); // HAIR Visual ID not Used on Classic
            writeInt(0x00); // HAIR2 Visual ID not Used on Classic


            writeShort((short) 0x00); // Upper Body enchant level
            writeShort((short) 0x00); // Lower Body enchant level
            writeShort((short) 0x00); // Headgear enchant level
            writeShort((short) 0x00); // Gloves enchant level
            writeShort((short) 0x00); // Boots enchant level

            writeInt(charInfoPackage.getHairStyle());
            writeInt(charInfoPackage.getHairColor());
            writeInt(charInfoPackage.getFace());

            writeDouble(charInfoPackage.getMaxHp()); // Maximum HP
            writeDouble(charInfoPackage.getMaxMp()); // Maximum MP

            writeInt(charInfoPackage.getDeleteTimer() > 0 ? (int) ((charInfoPackage.getDeleteTimer() - System.currentTimeMillis()) / 1000) : 0);
            writeInt(charInfoPackage.getClassId());
            writeInt(i == _activeId ? 1 : 0);

            writeByte((byte)(charInfoPackage.getEnchantEffect() > 127 ? 127 : charInfoPackage.getEnchantEffect()));
            writeInt(charInfoPackage.getAugmentation() != null ? charInfoPackage.getAugmentation().getOption1Id() : 0);
            writeInt(charInfoPackage.getAugmentation() != null ? charInfoPackage.getAugmentation().getOption2Id() : 0);

            // writeInt(charInfoPackage.getTransformId()); // Used to display Transformations
            writeInt(0x00); // Currently on retail when you are on character select you don't see your transformation.

            writeInt(0x00); // Pet NpcId
            writeInt(0x00); // Pet level
            writeInt(0x00); // Pet Food
            writeInt(0x00); // Pet Food Level
            writeDouble(0x00); // Current pet HP
            writeDouble(0x00); // Current pet MP

            writeInt(charInfoPackage.getVitalityPoints()); // Vitality
            writeInt((int) Config.RATE_VITALITY_EXP_MULTIPLIER * 100); // Vitality Percent
            writeInt(charInfoPackage.getVitalityItemsUsed()); // Remaining vitality item uses
            writeInt(charInfoPackage.getAccessLevel() == -100 ? 0x00 : 0x01); // Char is active or not
            writeByte((byte) (charInfoPackage.isNoble() ? 0x01 : 0x00));
            writeByte((byte)(Hero.getInstance().isHero(charInfoPackage.getObjectId()) ? 0x01 : 0x00)); // Hero glow
            writeByte((byte)( charInfoPackage.isHairAccessoryEnabled() ? 0x01 : 0x00)); // Show hair accessory if enabled
        }
    }



    @Override
    public int[] getPaperdollOrder() {
        return PAPERDOLL_ORDER;
    }
}
