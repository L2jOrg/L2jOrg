package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.impl.ExperienceData;
import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.instancemanager.CursedWeaponsManager;
import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.model.L2Party;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.zone.ZoneId;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

/**
 * @author Sdw, UnAfraid
 */
public class UserInfo extends AbstractMaskPacket<UserInfoType> {
    private final L2PcInstance _activeChar;

    private final int _relation;
    private final int _runSpd;
    private final int _walkSpd;
    private final int _swimRunSpd;
    private final int _swimWalkSpd;
    private final int _mountRunSpd = 0;
    private final int _mountWalkSpd = 0;
    private final int _flyRunSpd;
    private final int _flyWalkSpd;
    private final double _moveMultiplier;
    private final int _enchantLevel;
    private final int _armorEnchant;
    private final byte[] _masks = new byte[]
            {
                    (byte) 0x00,
                    (byte) 0x00,
                    (byte) 0x00
            };
    private String _title;
    private int _initSize = 5;

    public UserInfo(L2PcInstance cha) {
        this(cha, true);
    }

    public UserInfo(L2PcInstance cha, boolean addAll) {
        _activeChar = cha;

        _relation = calculateRelation(cha);
        _moveMultiplier = cha.getMovementSpeedMultiplier();
        _runSpd = (int) Math.round(cha.getRunSpeed() / _moveMultiplier);
        _walkSpd = (int) Math.round(cha.getWalkSpeed() / _moveMultiplier);
        _swimRunSpd = (int) Math.round(cha.getSwimRunSpeed() / _moveMultiplier);
        _swimWalkSpd = (int) Math.round(cha.getSwimWalkSpeed() / _moveMultiplier);
        _flyRunSpd = cha.isFlying() ? _runSpd : 0;
        _flyWalkSpd = cha.isFlying() ? _walkSpd : 0;
        _enchantLevel = cha.getInventory().getWeaponEnchant();
        _armorEnchant = cha.getInventory().getArmorMinEnchant();

        _title = cha.getTitle();
        if (cha.isGM() && cha.isInvisible()) {
            _title = "[Invisible]";
        }

        if (addAll) {
            addComponentType(UserInfoType.values());
        }
    }

    @Override
    protected byte[] getMasks() {
        return _masks;
    }

    @Override
    protected void onNewMaskAdded(UserInfoType component) {
        calcBlockSize(component);
    }

    private void calcBlockSize(UserInfoType type) {
        switch (type) {
            case BASIC_INFO: {
                _initSize += type.getBlockLength() + (_activeChar.getAppearance().getVisibleName().length() * 2);
                break;
            }
            case CLAN: {
                _initSize += type.getBlockLength() + (_title.length() * 2);
                break;
            }
            default: {
                _initSize += type.getBlockLength();
                break;
            }
        }
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.USER_INFO);

        writeInt(_activeChar.getObjectId());
        writeInt(_initSize);
        writeShort((short) 24);
        writeBytes(_masks);

        if (containsMask(UserInfoType.RELATION)) {
            writeInt(_relation);
        }

        if (containsMask(UserInfoType.BASIC_INFO)) {
            writeShort((short) (16 + (_activeChar.getAppearance().getVisibleName().length() * 2)));
            writeSizedString(_activeChar.getName());
            writeByte((byte) (_activeChar.isGM() ? 0x01 : 0x00));
            writeByte((byte) _activeChar.getRace().ordinal());
            writeByte((byte) (_activeChar.getAppearance().getSex() ? 0x01 : 0x00));
            writeInt(ClassId.getClassId(_activeChar.getBaseTemplate().getClassId().getId()).getRootClassId().getId());
            writeInt(_activeChar.getClassId().getId());
            writeByte((byte) _activeChar.getLevel());
        }

        if (containsMask(UserInfoType.BASE_STATS)) {
            writeShort((short) 18);
            writeShort((short) _activeChar.getSTR());
            writeShort((short) _activeChar.getDEX());
            writeShort((short) _activeChar.getCON());
            writeShort((short) _activeChar.getINT());
            writeShort((short) _activeChar.getWIT());
            writeShort((short) _activeChar.getMEN());
            writeShort((short) 0x01); // LUC
            writeShort((short) 0x01); // CHA
        }

        if (containsMask(UserInfoType.MAX_HPCPMP)) {
            writeShort((short) 14);
            writeInt(_activeChar.getMaxHp());
            writeInt(_activeChar.getMaxMp());
            writeInt(_activeChar.getMaxCp());
        }

        if (containsMask(UserInfoType.CURRENT_HPMPCP_EXP_SP)) {
            writeShort((short) 38);
            writeInt((int) Math.round(_activeChar.getCurrentHp()));
            writeInt((int) Math.round(_activeChar.getCurrentMp()));
            writeInt((int) Math.round(_activeChar.getCurrentCp()));
            writeLong(_activeChar.getSp());
            writeLong(_activeChar.getExp());
            writeDouble((float) (_activeChar.getExp() - ExperienceData.getInstance().getExpForLevel(_activeChar.getLevel())) / (ExperienceData.getInstance().getExpForLevel(_activeChar.getLevel() + 1) - ExperienceData.getInstance().getExpForLevel(_activeChar.getLevel())));
        }

        if (containsMask(UserInfoType.ENCHANTLEVEL)) {
            writeShort((short) 4);
            writeByte((byte) _enchantLevel);
            writeByte((byte) _armorEnchant);
        }

        if (containsMask(UserInfoType.APPAREANCE)) {
            writeShort((short) 15);
            writeInt(_activeChar.getVisualHair());
            writeInt(_activeChar.getVisualHairColor());
            writeInt(_activeChar.getVisualFace());
            writeByte((byte) (_activeChar.isHairAccessoryEnabled() ? 0x01 : 0x00));
        }

        if (containsMask(UserInfoType.STATUS)) {
            writeShort((short) 6);
            writeByte((byte) _activeChar.getMountType().ordinal());
            writeByte((byte) _activeChar.getPrivateStoreType().getId());
            writeByte((byte) (_activeChar.hasDwarvenCraft() || (_activeChar.getSkillLevel(248) > 0) ? 1 : 0));
            writeByte((byte) 0x00); // Ability Points
        }

        if (containsMask(UserInfoType.STATS)) {
            writeShort((short) 56);
            writeShort((short) (_activeChar.getActiveWeaponItem() != null ? 40 : 20));
            writeInt(_activeChar.getPAtk());
            writeInt(_activeChar.getPAtkSpd());
            writeInt(_activeChar.getPDef());
            writeInt(_activeChar.getEvasionRate());
            writeInt(_activeChar.getAccuracy());
            writeInt(_activeChar.getCriticalHit());
            writeInt(_activeChar.getMAtk());
            writeInt(_activeChar.getMAtkSpd());
            writeInt(_activeChar.getPAtkSpd()); // Seems like atk speed - 1
            writeInt(_activeChar.getMagicEvasionRate());
            writeInt(_activeChar.getMDef());
            writeInt(_activeChar.getMagicAccuracy());
            writeInt(_activeChar.getMCriticalHit());
        }

        if (containsMask(UserInfoType.ELEMENTALS)) {
            writeShort((short) 14);
            writeShort((short) 0x00); // Fire defense
            writeShort((short) 0x00); // Water defense
            writeShort((short) 0x00); // Wind defense
            writeShort((short) 0x00); // Earth defense
            writeShort((short) 0x00); // Holy defense
            writeShort((short) 0x00); // dark defense
        }

        if (containsMask(UserInfoType.POSITION)) {
            writeShort((short) 18);
            writeInt(_activeChar.getX());
            writeInt(_activeChar.getY());
            writeInt(_activeChar.getZ());
            writeInt(_activeChar.isInVehicle() ? _activeChar.getVehicle().getObjectId() : 0);
        }

        if (containsMask(UserInfoType.SPEED)) {
            writeShort((short) 18);
            writeShort((short) _runSpd);
            writeShort((short) _walkSpd);
            writeShort((short) _swimRunSpd);
            writeShort((short) _swimWalkSpd);
            writeShort((short) _mountRunSpd);
            writeShort((short) _mountWalkSpd);
            writeShort((short) _flyRunSpd);
            writeShort((short) _flyWalkSpd);
        }

        if (containsMask(UserInfoType.MULTIPLIER)) {
            writeShort((short) 18);
            writeDouble(_moveMultiplier);
            writeDouble(_activeChar.getAttackSpeedMultiplier());
        }

        if (containsMask(UserInfoType.COL_RADIUS_HEIGHT)) {
            writeShort((short) 18);
            writeDouble(_activeChar.getCollisionRadius());
            writeDouble(_activeChar.getCollisionHeight());
        }

        if (containsMask(UserInfoType.ATK_ELEMENTAL)) {
            writeShort((short) 5);
            writeByte((byte) -2); // Attack Element
            writeShort((short) 0x00); // Attack element power
        }

        if (containsMask(UserInfoType.CLAN)) {
            writeShort((short) (32 + (_title.length() * 2)));
            writeSizedString(_title);
            writeShort((short) _activeChar.getPledgeType());
            writeInt(_activeChar.getClanId());
            writeInt(_activeChar.getClanCrestLargeId());
            writeInt(_activeChar.getClanCrestId());
            writeInt(_activeChar.getClanPrivileges().getBitmask());
            writeByte((byte) (_activeChar.isClanLeader() ? 0x01 : 0x00));
            writeInt(_activeChar.getAllyId());
            writeInt(_activeChar.getAllyCrestId());
            writeByte((byte) (_activeChar.isInMatchingRoom() ? 0x01 : 0x00));
        }

        if (containsMask(UserInfoType.SOCIAL)) {
            writeShort((short) 26); // 196
            writeByte(_activeChar.getPvpFlag());
            writeInt(_activeChar.getReputation()); // Reputation
            writeByte((byte) (_activeChar.isNoble() ? 1 : 0));
            writeByte((byte) (_activeChar.isHero() || (_activeChar.isGM() && Config.GM_HERO_AURA) ? 2 : 0)); // 152 - Value for enabled changed to 2?
            writeByte((byte) _activeChar.getPledgeClass());
            writeInt(_activeChar.getPkKills());
            writeInt(_activeChar.getPvpKills());
            writeShort((short) _activeChar.getRecomLeft());
            writeShort((short) _activeChar.getRecomHave());
            writeInt(0x010); // unk 196
        }

        if (containsMask(UserInfoType.VITA_FAME)) {
            writeShort((short) 19); // 196
            writeInt(_activeChar.getVitalityPoints());
            writeByte((byte) 0x00); // Vita Bonus
            writeInt(_activeChar.getFame());
            writeInt(_activeChar.getRaidbossPoints());
            writeInt(0x00); // unk 196
        }

        if (containsMask(UserInfoType.SLOTS)) {
            writeShort((short) 12); // 152
            writeByte((byte) _activeChar.getInventory().getTalismanSlots());
            writeByte((byte) _activeChar.getInventory().getBroochJewelSlots());
            writeByte((byte) _activeChar.getTeam().getId());
            writeInt(0x00);

            if (_activeChar.getInventory().getAgathionSlots() > 0) {
                writeByte((byte) 0x01); // Charm slots
                writeByte((byte) (_activeChar.getInventory().getAgathionSlots() - 1));
                writeByte((byte) _activeChar.getInventory().getArtifactSlots()); // Artifact set slots // 152
            } else {
                writeByte((byte) 0x00); // Charm slots
                writeByte((byte) 0x00);
                writeByte((byte) _activeChar.getInventory().getArtifactSlots()); // Artifact set slots // 152
            }
        }

        if (containsMask(UserInfoType.MOVEMENTS)) {
            writeShort((short) 4);
            writeByte((byte) (_activeChar.isInsideZone(ZoneId.WATER) ? 1 : _activeChar.isFlyingMounted() ? 2 : 0));
            writeByte((byte) (_activeChar.isRunning() ? 0x01 : 0x00));
        }

        if (containsMask(UserInfoType.COLOR)) {
            writeShort((short) 10);
            writeInt(_activeChar.getAppearance().getNameColor());
            writeInt(_activeChar.getAppearance().getTitleColor());
        }

        if (containsMask(UserInfoType.INVENTORY_LIMIT)) {
            writeShort((short) 13);
            writeInt((short) 0x00); // mount ??
            writeShort((short) _activeChar.getInventoryLimit());
            writeByte((byte) (_activeChar.isCursedWeaponEquipped() ? CursedWeaponsManager.getInstance().getLevel(_activeChar.getCursedWeaponEquippedId()) : 0));
            writeInt(0x00); // unk 196
        }

        if (containsMask(UserInfoType.TRUE_HERO)) {
            writeShort((short) 9);
            writeByte((byte) 0x01);
            writeInt(0x00);
            writeByte((byte) 0x00);
            writeByte((byte) (_activeChar.isTrueHero() ? 100 : 0x00));
        }

        if (containsMask(UserInfoType.ATT_SPIRITS)) // 152
        {
            writeShort((short) 26);
            writeInt(0X00); // Active spirit power
            writeInt(0x00); // Fire defense
            writeInt(0x00); // Water defense
            writeInt(0x00); // Wind defense
            writeInt(0x00); // Earth defense
            writeInt(0x00); // Active Spirit 1 - Fire, 2 Water, 3 Wind, 4 Earth
        }

    }


    private int calculateRelation(L2PcInstance activeChar) {
        int relation = 0;
        final L2Party party = activeChar.getParty();
        final L2Clan clan = activeChar.getClan();

        if (party != null) {
            relation |= 0x08; // Party member
            if (party.getLeader() == _activeChar) {
                relation |= 0x10; // Party leader
            }
        }

        if (clan != null) {
            relation |= 0x20; // Clan member
            if (clan.getLeaderId() == activeChar.getObjectId()) {
                relation |= 0x40; // Clan leader
            }
        }

        if (activeChar.isInSiege()) {
            relation |= 0x80; // In siege
        }

        return relation;
    }
}
