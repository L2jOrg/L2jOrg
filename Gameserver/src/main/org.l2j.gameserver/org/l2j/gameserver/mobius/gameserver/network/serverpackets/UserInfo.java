package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.data.xml.impl.ExperienceData;
import org.l2j.gameserver.mobius.gameserver.enums.UserInfoType;
import org.l2j.gameserver.mobius.gameserver.instancemanager.CursedWeaponsManager;
import org.l2j.gameserver.mobius.gameserver.model.L2Clan;
import org.l2j.gameserver.mobius.gameserver.model.L2Party;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.base.ClassId;
import org.l2j.gameserver.mobius.gameserver.model.zone.ZoneId;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

/**
 * @author Sdw, UnAfraid
 */
public class UserInfo extends AbstractMaskPacket<UserInfoType>
{
    private final L2PcInstance _activeChar;

    private final int _relation;
    private final int _runSpd;
    private final int _walkSpd;
    private final int _swimRunSpd;
    private final int _swimWalkSpd;
    private final int _flRunSpd = 0;
    private final int _flWalkSpd = 0;
    private final int _flyRunSpd;
    private final int _flyWalkSpd;
    private final double _moveMultiplier;
    private final int _enchantLevel;
    private final int _armorEnchant;
    private String _title;

    private final byte[] _masks = new byte[]
            {
                    (byte) 0x00,
                    (byte) 0x00,
                    (byte) 0x00
            };

    private int _initSize = 5;

    public UserInfo(L2PcInstance cha)
    {
        this(cha, true);
    }

    public UserInfo(L2PcInstance cha, boolean addAll)
    {
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
        if (cha.isGM() && cha.isInvisible())
        {
            _title = "[Invisible]";
        }

        if (addAll)
        {
            addComponentType(UserInfoType.values());
        }
    }

    @Override
    protected byte[] getMasks()
    {
        return _masks;
    }

    @Override
    protected void onNewMaskAdded(UserInfoType component)
    {
        calcBlockSize(component);
    }

    private void calcBlockSize(UserInfoType type)
    {
        switch (type)
        {
            case BASIC_INFO:
            {
                _initSize += type.getBlockLength() + (_activeChar.getAppearance().getVisibleName().length() * 2);
                break;
            }
            case CLAN:
            {
                _initSize += type.getBlockLength() + (_title.length() * 2);
                break;
            }
            default:
            {
                _initSize += type.getBlockLength();
                break;
            }
        }
    }

    @Override
    public boolean write(PacketWriter packet)
    {
        OutgoingPackets.USER_INFO.writeId(packet);

        packet.writeD(_activeChar.getObjectId());
        packet.writeD(_initSize);
        packet.writeH(24);
        packet.writeB(_masks);

        if (containsMask(UserInfoType.RELATION))
        {
            packet.writeD(_relation);
        }

        if (containsMask(UserInfoType.BASIC_INFO))
        {
            packet.writeH(16 + (_activeChar.getAppearance().getVisibleName().length() * 2));
            packet.writeString(_activeChar.getName());
            packet.writeC(_activeChar.isGM() ? 0x01 : 0x00);
            packet.writeC(_activeChar.getRace().ordinal());
            packet.writeC(_activeChar.getAppearance().getSex() ? 0x01 : 0x00);
            packet.writeD(ClassId.getClassId(_activeChar.getBaseTemplate().getClassId().getId()).getRootClassId().getId());
            packet.writeD(_activeChar.getClassId().getId());
            packet.writeC(_activeChar.getLevel());
        }

        if (containsMask(UserInfoType.BASE_STATS))
        {
            packet.writeH(18);
            packet.writeH(_activeChar.getSTR());
            packet.writeH(_activeChar.getDEX());
            packet.writeH(_activeChar.getCON());
            packet.writeH(_activeChar.getINT());
            packet.writeH(_activeChar.getWIT());
            packet.writeH(_activeChar.getMEN());
            packet.writeH(0x00);
            packet.writeH(0x00);
        }

        if (containsMask(UserInfoType.MAX_HPCPMP))
        {
            packet.writeH(14);
            packet.writeD(_activeChar.getMaxHp());
            packet.writeD(_activeChar.getMaxMp());
            packet.writeD(_activeChar.getMaxCp());
        }

        if (containsMask(UserInfoType.CURRENT_HPMPCP_EXP_SP))
        {
            packet.writeH(38);
            packet.writeD((int) Math.round(_activeChar.getCurrentHp()));
            packet.writeD((int) Math.round(_activeChar.getCurrentMp()));
            packet.writeD((int) Math.round(_activeChar.getCurrentCp()));
            packet.writeQ(_activeChar.getSp());
            packet.writeQ(_activeChar.getExp());
            packet.writeF((float) (_activeChar.getExp() - ExperienceData.getInstance().getExpForLevel(_activeChar.getLevel())) / (ExperienceData.getInstance().getExpForLevel(_activeChar.getLevel() + 1) - ExperienceData.getInstance().getExpForLevel(_activeChar.getLevel())));
        }

        if (containsMask(UserInfoType.ENCHANTLEVEL))
        {
            packet.writeH(4);
            packet.writeC(_enchantLevel);
            packet.writeC(_armorEnchant);
        }

        if (containsMask(UserInfoType.APPAREANCE))
        {
            packet.writeH(15);
            packet.writeD(_activeChar.getVisualHair());
            packet.writeD(_activeChar.getVisualHairColor());
            packet.writeD(_activeChar.getVisualFace());
            packet.writeC(_activeChar.isHairAccessoryEnabled() ? 0x01 : 0x00);
        }

        if (containsMask(UserInfoType.STATUS))
        {
            packet.writeH(6);
            packet.writeC(_activeChar.getMountType().ordinal());
            packet.writeC(_activeChar.getPrivateStoreType().getId());
            packet.writeC(_activeChar.hasDwarvenCraft() || (_activeChar.getSkillLevel(248) > 0) ? 1 : 0);
            packet.writeC(0x00);
        }

        if (containsMask(UserInfoType.STATS))
        {
            packet.writeH(56);
            packet.writeH(_activeChar.getActiveWeaponItem() != null ? 40 : 20);
            packet.writeD(_activeChar.getPAtk());
            packet.writeD(_activeChar.getPAtkSpd());
            packet.writeD(_activeChar.getPDef());
            packet.writeD(_activeChar.getEvasionRate());
            packet.writeD(_activeChar.getAccuracy());
            packet.writeD(_activeChar.getCriticalHit());
            packet.writeD(_activeChar.getMAtk());
            packet.writeD(_activeChar.getMAtkSpd());
            packet.writeD(_activeChar.getPAtkSpd()); // Seems like atk speed - 1
            packet.writeD(_activeChar.getMagicEvasionRate());
            packet.writeD(_activeChar.getMDef());
            packet.writeD(_activeChar.getMagicAccuracy());
            packet.writeD(_activeChar.getMCriticalHit());
        }

        if (containsMask(UserInfoType.ELEMENTALS))
        {
            packet.writeH(14);
            packet.writeH(0x00);
            packet.writeH(0x00);
            packet.writeH(0x00);
            packet.writeH(0x00);
            packet.writeH(0x00);
            packet.writeH(0x00);
        }

        if (containsMask(UserInfoType.POSITION))
        {
            packet.writeH(18);
            packet.writeD(_activeChar.getX());
            packet.writeD(_activeChar.getY());
            packet.writeD(_activeChar.getZ());
            packet.writeD(_activeChar.isInVehicle() ? _activeChar.getVehicle().getObjectId() : 0);
        }

        if (containsMask(UserInfoType.SPEED))
        {
            packet.writeH(18);
            packet.writeH(_runSpd);
            packet.writeH(_walkSpd);
            packet.writeH(_swimRunSpd);
            packet.writeH(_swimWalkSpd);
            packet.writeH(_flRunSpd);
            packet.writeH(_flWalkSpd);
            packet.writeH(_flyRunSpd);
            packet.writeH(_flyWalkSpd);
        }

        if (containsMask(UserInfoType.MULTIPLIER))
        {
            packet.writeH(18);
            packet.writeF(_moveMultiplier);
            packet.writeF(_activeChar.getAttackSpeedMultiplier());
        }

        if (containsMask(UserInfoType.COL_RADIUS_HEIGHT))
        {
            packet.writeH(18);
            packet.writeF(_activeChar.getCollisionRadius());
            packet.writeF(_activeChar.getCollisionHeight());
        }

        if (containsMask(UserInfoType.ATK_ELEMENTAL))
        {
            packet.writeH(5);
            packet.writeC(0x00);
            packet.writeH(0x00);
        }

        if (containsMask(UserInfoType.CLAN))
        {
            packet.writeH(32 + (_title.length() * 2));
            packet.writeString(_title);
            packet.writeH(_activeChar.getPledgeType());
            packet.writeD(_activeChar.getClanId());
            packet.writeD(_activeChar.getClanCrestLargeId());
            packet.writeD(_activeChar.getClanCrestId());
            packet.writeD(_activeChar.getClanPrivileges().getBitmask());
            packet.writeC(_activeChar.isClanLeader() ? 0x01 : 0x00);
            packet.writeD(_activeChar.getAllyId());
            packet.writeD(_activeChar.getAllyCrestId());
            packet.writeC(_activeChar.isInMatchingRoom() ? 0x01 : 0x00);
        }

        if (containsMask(UserInfoType.SOCIAL))
        {
            packet.writeH(22);
            packet.writeC(_activeChar.getPvpFlag());
            packet.writeD(_activeChar.getReputation()); // Reputation
            packet.writeC(_activeChar.isNoble() ? 1 : 0);
            packet.writeC(_activeChar.isHero() || (_activeChar.isGM() && Config.GM_HERO_AURA) ? 1 : 0);
            packet.writeC(_activeChar.getPledgeClass());
            packet.writeD(_activeChar.getPkKills());
            packet.writeD(_activeChar.getPvpKills());
            packet.writeH(_activeChar.getRecomLeft());
            packet.writeH(_activeChar.getRecomHave());
        }

        if (containsMask(UserInfoType.VITA_FAME))
        {
            packet.writeH(15);
            packet.writeD(_activeChar.getVitalityPoints());
            packet.writeC(0x00); // Vita Bonus
            packet.writeD(_activeChar.getFame());
            packet.writeD(_activeChar.getRaidbossPoints());
        }

        if (containsMask(UserInfoType.SLOTS))
        {
            packet.writeH(12); // 152
            packet.writeC(_activeChar.getInventory().getTalismanSlots());
            packet.writeC(_activeChar.getInventory().getBroochJewelSlots());
            packet.writeC(_activeChar.getTeam().getId());
            packet.writeD(0x00);

            if (_activeChar.getInventory().getAgathionSlots() > 0)
            {
                packet.writeC(0x01); // Charm slots
                packet.writeC(_activeChar.getInventory().getAgathionSlots() - 1);
                packet.writeC(_activeChar.getInventory().getArtifactSlots()); // Artifact set slots // 152
            }
            else
            {
                packet.writeC(0x00); // Charm slots
                packet.writeC(0x00);
                packet.writeC(_activeChar.getInventory().getArtifactSlots()); // Artifact set slots // 152
            }
        }

        if (containsMask(UserInfoType.MOVEMENTS))
        {
            packet.writeH(4);
            packet.writeC(_activeChar.isInsideZone(ZoneId.WATER) ? 1 : _activeChar.isFlyingMounted() ? 2 : 0);
            packet.writeC(_activeChar.isRunning() ? 0x01 : 0x00);
        }

        if (containsMask(UserInfoType.COLOR))
        {
            packet.writeH(10);
            packet.writeD(_activeChar.getAppearance().getNameColor());
            packet.writeD(_activeChar.getAppearance().getTitleColor());
        }

        if (containsMask(UserInfoType.INVENTORY_LIMIT))
        {
            packet.writeH(9);
            packet.writeH(0x00);
            packet.writeH(0x00);
            packet.writeH(_activeChar.getInventoryLimit());
            packet.writeC(_activeChar.isCursedWeaponEquipped() ? CursedWeaponsManager.getInstance().getLevel(_activeChar.getCursedWeaponEquippedId()) : 0);
        }

        if (containsMask(UserInfoType.TRUE_HERO))
        {
            packet.writeH(9);
            packet.writeD(0x00);
            packet.writeH(0x00);
            packet.writeC(_activeChar.isTrueHero() ? 100 : 0x00);
        }

        if (containsMask(UserInfoType.ATT_SPIRITS)) // 152
        {
            packet.writeH(26);
            packet.writeD(-1);
            packet.writeD(0x00);
            packet.writeD(0x00);
            packet.writeD(0x00);
            packet.writeD(0x00);
            packet.writeD(0x00);
        }

        return true;
    }

    private int calculateRelation(L2PcInstance activeChar)
    {
        int relation = 0;
        final L2Party party = activeChar.getParty();
        final L2Clan clan = activeChar.getClan();

        if (party != null)
        {
            relation |= 0x08; // Party member
            if (party.getLeader() == _activeChar)
            {
                relation |= 0x10; // Party leader
            }
        }

        if (clan != null)
        {
            relation |= 0x20; // Clan member
            if (clan.getLeaderId() == activeChar.getObjectId())
            {
                relation |= 0x40; // Clan leader
            }
        }

        if (activeChar.isInSiege())
        {
            relation |= 0x80; // In siege
        }

        return relation;
    }
}
