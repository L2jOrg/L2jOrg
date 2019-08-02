package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.impl.ExperienceData;
import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.instancemanager.CursedWeaponsManager;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.world.zone.ZoneId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author Sdw, UnAfraid
 */
public class UserInfo extends AbstractMaskPacket<UserInfoType> {
    private final Player activeChar;

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

    public UserInfo(Player cha) {
        this(cha, true);
    }

    public UserInfo(Player cha, boolean addAll) {
        activeChar = cha;

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
            case BASIC_INFO:
                _initSize += type.getBlockLength() + (activeChar.getAppearance().getVisibleName().length() * 2);
            case CLAN:
                _initSize += type.getBlockLength() + (_title.length() * 2);
            default:
                _initSize += type.getBlockLength();
        }
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.USER_INFO);

        writeInt(activeChar.getObjectId());
        writeInt(_initSize);
        writeShort((short) 24);
        writeBytes(_masks);

        if (containsMask(UserInfoType.RELATION)) {
            writeInt(_relation);
        }

        if (containsMask(UserInfoType.BASIC_INFO)) {
            writeShort((short) (16 + (activeChar.getAppearance().getVisibleName().length() * 2)));
            writeSizedString(activeChar.getName());
            writeByte((byte) (activeChar.isGM() ? 0x01 : 0x00));
            writeByte((byte) activeChar.getRace().ordinal());
            writeByte((byte) (activeChar.getAppearance().getSex() ? 0x01 : 0x00));
            writeInt(ClassId.getClassId(activeChar.getBaseTemplate().getClassId().getId()).getRootClassId().getId());
            writeInt(activeChar.getClassId().getId());
            writeByte((byte) activeChar.getLevel());
        }

        if (containsMask(UserInfoType.BASE_STATS)) {
            writeShort((short) 18);
            writeShort((short) activeChar.getSTR());
            writeShort((short) activeChar.getDEX());
            writeShort((short) activeChar.getCON());
            writeShort((short) activeChar.getINT());
            writeShort((short) activeChar.getWIT());
            writeShort((short) activeChar.getMEN());
            writeShort((short) 0x01); // LUC
            writeShort((short) 0x01); // CHA
        }

        if (containsMask(UserInfoType.MAX_HPCPMP)) {
            writeShort((short) 14);
            writeInt(activeChar.getMaxHp());
            writeInt(activeChar.getMaxMp());
            writeInt(activeChar.getMaxCp());
        }

        if (containsMask(UserInfoType.CURRENT_HPMPCP_EXP_SP)) {
            writeShort((short) 38);
            writeInt((int) Math.round(activeChar.getCurrentHp()));
            writeInt((int) Math.round(activeChar.getCurrentMp()));
            writeInt((int) Math.round(activeChar.getCurrentCp()));
            writeLong(activeChar.getSp());
            writeLong(activeChar.getExp());
            writeDouble((float) (activeChar.getExp() - ExperienceData.getInstance().getExpForLevel(activeChar.getLevel())) / (ExperienceData.getInstance().getExpForLevel(activeChar.getLevel() + 1) - ExperienceData.getInstance().getExpForLevel(activeChar.getLevel())));
        }

        if (containsMask(UserInfoType.ENCHANTLEVEL)) {
            writeShort((short) 4);
            writeByte((byte) _enchantLevel);
            writeByte((byte) _armorEnchant);
        }

        if (containsMask(UserInfoType.APPAREANCE)) {
            writeShort((short) 15);
            writeInt(activeChar.getVisualHair());
            writeInt(activeChar.getVisualHairColor());
            writeInt(activeChar.getVisualFace());
            writeByte((byte) (activeChar.isHairAccessoryEnabled() ? 0x01 : 0x00));
        }

        if (containsMask(UserInfoType.STATUS)) {
            writeShort((short) 6);
            writeByte((byte) activeChar.getMountType().ordinal());
            writeByte((byte) activeChar.getPrivateStoreType().getId());
            writeByte((byte) (activeChar.hasDwarvenCraft() || (activeChar.getSkillLevel(248) > 0) ? 1 : 0));
            writeByte((byte) 0x00); // Ability Points
        }

        if (containsMask(UserInfoType.STATS)) {
            writeShort((short) 56);
            writeShort((short) (activeChar.getActiveWeaponItem() != null ? 40 : 20));
            writeInt(activeChar.getPAtk());
            writeInt(activeChar.getPAtkSpd());
            writeInt(activeChar.getPDef());
            writeInt(activeChar.getEvasionRate());
            writeInt(activeChar.getAccuracy());
            writeInt(activeChar.getCriticalHit());
            writeInt(activeChar.getMAtk());
            writeInt(activeChar.getMAtkSpd());
            writeInt(activeChar.getPAtkSpd()); // Seems like atk speed - 1
            writeInt(activeChar.getMagicEvasionRate());
            writeInt(activeChar.getMDef());
            writeInt(activeChar.getMagicAccuracy());
            writeInt(activeChar.getMCriticalHit());
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
            writeInt(activeChar.getX());
            writeInt(activeChar.getY());
            writeInt(activeChar.getZ());
            writeInt(activeChar.isInVehicle() ? activeChar.getVehicle().getObjectId() : 0);
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
            writeDouble(activeChar.getAttackSpeedMultiplier());
        }

        if (containsMask(UserInfoType.COL_RADIUS_HEIGHT)) {
            writeShort((short) 18);
            writeDouble(activeChar.getCollisionRadius());
            writeDouble(activeChar.getCollisionHeight());
        }

        if (containsMask(UserInfoType.ATK_ELEMENTAL)) {
            writeShort((short) 5);
            writeByte((byte) -2); // Attack Element
            writeShort((short) 0x00); // Attack element power
        }

        if (containsMask(UserInfoType.CLAN)) {
            writeShort((short) (32 + (_title.length() * 2)));
            writeSizedString(_title);
            writeShort((short) activeChar.getPledgeType());
            writeInt(activeChar.getClanId());
            writeInt(activeChar.getClanCrestLargeId());
            writeInt(activeChar.getClanCrestId());
            writeInt(activeChar.getClanPrivileges().getBitmask());
            writeByte((byte) (activeChar.isClanLeader() ? 0x01 : 0x00));
            writeInt(activeChar.getAllyId());
            writeInt(activeChar.getAllyCrestId());
            writeByte((byte) (activeChar.isInMatchingRoom() ? 0x01 : 0x00));
        }

        if (containsMask(UserInfoType.SOCIAL)) {
            writeShort((short) 26); // 196
            writeByte(activeChar.getPvpFlag());
            writeInt(activeChar.getReputation()); // Reputation
            writeByte((byte) (activeChar.isNoble() ? 1 : 0));
            writeByte((byte) (activeChar.isHero() || (activeChar.isGM() && Config.GM_HERO_AURA) ? 2 : 0)); // 152 - Value for enabled changed to 2?
            writeByte((byte) activeChar.getPledgeClass());
            writeInt(activeChar.getPkKills());
            writeInt(activeChar.getPvpKills());
            writeShort((short) activeChar.getRecomLeft());
            writeShort((short) activeChar.getRecomHave());
            writeInt(0x010); // unk 196
        }

        if (containsMask(UserInfoType.VITA_FAME)) {
            writeShort((short) 19); // 196
            writeInt(activeChar.getVitalityPoints());
            writeByte((byte) 0x00); // Vita Bonus
            writeInt(activeChar.getFame());
            writeInt(activeChar.getRaidbossPoints());
            writeInt(0x00); // unk 196
        }

        if (containsMask(UserInfoType.SLOTS)) {
            writeShort((short) 12); // 152
            writeByte((byte) activeChar.getInventory().getTalismanSlots());
            writeByte((byte) activeChar.getInventory().getBroochJewelSlots());
            writeByte((byte) activeChar.getTeam().getId());
            writeInt(0x00);

            if (activeChar.getInventory().getAgathionSlots() > 0) {
                writeByte((byte) 0x01); // Charm slots
                writeByte((byte) (activeChar.getInventory().getAgathionSlots() - 1));
                writeByte((byte) activeChar.getInventory().getArtifactSlots()); // Artifact set slots // 152
            } else {
                writeByte((byte) 0x00); // Charm slots
                writeByte((byte) 0x00);
                writeByte((byte) activeChar.getInventory().getArtifactSlots()); // Artifact set slots // 152
            }
        }

        if (containsMask(UserInfoType.MOVEMENTS)) {
            writeShort((short) 4);
            writeByte((byte) (activeChar.isInsideZone(ZoneId.WATER) ? 1 : activeChar.isFlyingMounted() ? 2 : 0));
            writeByte((byte) (activeChar.isRunning() ? 0x01 : 0x00));
        }

        if (containsMask(UserInfoType.COLOR)) {
            writeShort((short) 10);
            writeInt(activeChar.getAppearance().getNameColor());
            writeInt(activeChar.getAppearance().getTitleColor());
        }

        if (containsMask(UserInfoType.INVENTORY_LIMIT)) {
            writeShort((short) 13);
            writeInt((short) 0x00); // mount ??
            writeShort((short) activeChar.getInventoryLimit());
            writeByte((byte) (activeChar.isCursedWeaponEquipped() ? CursedWeaponsManager.getInstance().getLevel(activeChar.getCursedWeaponEquippedId()) : 0));
            writeInt(0x00); // unk 196
        }

        if (containsMask(UserInfoType.TRUE_HERO)) {
            writeShort(9);
            writeByte(0x01);
            writeInt(0x00);
            writeByte(0x00);
            writeByte(activeChar.isTrueHero() ? 100 : 0x00);
        }

        if (containsMask(UserInfoType.ATT_SPIRITS)) {
            writeShort(26);
            writeInt((int) activeChar.getActiveElementalSpiritAttack());
            writeInt((int) activeChar.getFireSpiritDefense());
            writeInt((int) activeChar.getWaterSpiritDefense());
            writeInt((int) activeChar.getWindSpiritDefense());
            writeInt((int) activeChar.getEarthSpiritDefense());
            writeInt(activeChar.getActiveElementalSpiritType());
        }
    }

    private int calculateRelation(Player activeChar) {
        int relation = 0;
        final Party party = activeChar.getParty();
        final Clan clan = activeChar.getClan();

        if (party != null) {
            relation |= 0x08; // Party member
            if (party.getLeader() == this.activeChar) {
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
