package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.instancemanager.CursedWeaponsManager;
import org.l2j.gameserver.model.VariationInstance;
import org.l2j.gameserver.model.actor.instance.Decoy;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.ceremonyofchaos.CeremonyOfChaosEvent;
import org.l2j.gameserver.model.ceremonyofchaos.CeremonyOfChaosMember;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.itemcontainer.Inventory;
import org.l2j.gameserver.model.skills.AbnormalVisualEffect;
import org.l2j.gameserver.world.zone.ZoneId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.Set;

public class CharInfo extends ServerPacket {

    private static final int[] PAPERDOLL_ORDER = new int[]
            {
                    Inventory.PAPERDOLL_UNDER,
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
                    Inventory.PAPERDOLL_HAIR2
            };
    private final Player _activeChar;
    private final int _mAtkSpd;
    private final int _pAtkSpd;
    private final int _runSpd;
    private final int _walkSpd;
    private final int _swimRunSpd;
    private final int _swimWalkSpd;
    private final int _flyRunSpd;
    private final int _flyWalkSpd;
    private final double _moveMultiplier;
    private final float _attackSpeedMultiplier;
    private final boolean _gmSeeInvis;
    private int _objId;
    private int _x;
    private int _y;
    private int _z;
    private int _heading;
    private int _enchantLevel = 0;
    private int _armorEnchant = 0;
    private int _vehicleId = 0;

    public CharInfo(Player cha, boolean gmSeeInvis) {
        _activeChar = cha;
        _objId = cha.getObjectId();
        if ((_activeChar.getVehicle() != null) && (_activeChar.getInVehiclePosition() != null)) {
            _x = _activeChar.getInVehiclePosition().getX();
            _y = _activeChar.getInVehiclePosition().getY();
            _z = _activeChar.getInVehiclePosition().getZ();
            _vehicleId = _activeChar.getVehicle().getObjectId();
        } else {
            _x = _activeChar.getX();
            _y = _activeChar.getY();
            _z = _activeChar.getZ();
        }
        _heading = _activeChar.getHeading();
        _mAtkSpd = _activeChar.getMAtkSpd();
        _pAtkSpd = _activeChar.getPAtkSpd();
        _attackSpeedMultiplier = (float) _activeChar.getAttackSpeedMultiplier();
        _moveMultiplier = cha.getMovementSpeedMultiplier();
        _runSpd = (int) Math.round(cha.getRunSpeed() / _moveMultiplier);
        _walkSpd = (int) Math.round(cha.getWalkSpeed() / _moveMultiplier);
        _swimRunSpd = (int) Math.round(cha.getSwimRunSpeed() / _moveMultiplier);
        _swimWalkSpd = (int) Math.round(cha.getSwimWalkSpeed() / _moveMultiplier);
        _flyRunSpd = cha.isFlying() ? _runSpd : 0;
        _flyWalkSpd = cha.isFlying() ? _walkSpd : 0;
        _enchantLevel = cha.getInventory().getWeaponEnchant();
        _armorEnchant = cha.getInventory().getArmorMinEnchant();
        _gmSeeInvis = gmSeeInvis;
    }

    public CharInfo(Decoy decoy, boolean gmSeeInvis) {
        this(decoy.getActingPlayer(), gmSeeInvis); // init
        _objId = decoy.getObjectId();
        _x = decoy.getX();
        _y = decoy.getY();
        _z = decoy.getZ();
        _heading = decoy.getHeading();
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.CHAR_INFO);
        final CeremonyOfChaosEvent event = _activeChar.getEvent(CeremonyOfChaosEvent.class);
        final CeremonyOfChaosMember cocPlayer = event != null ? event.getMember(_activeChar.getObjectId()) : null;
        writeByte((byte) 0x00); // Grand Crusade
        writeInt(_x); // Confirmed
        writeInt(_y); // Confirmed
        writeInt(_z); // Confirmed
        writeInt(_vehicleId); // Confirmed
        writeInt(_objId); // Confirmed
        writeString(_activeChar.getAppearance().getVisibleName()); // Confirmed

        writeShort((short) _activeChar.getRace().ordinal()); // Confirmed
        writeByte((byte) (_activeChar.getAppearance().getSex() ? 0x01 : 0x00)); // Confirmed
        writeInt(_activeChar.getBaseClass()); // Confirmed

        for (int slot : getPaperdollOrder()) {
            writeInt(_activeChar.getInventory().getPaperdollItemDisplayId(slot)); // Confirmed
        }

        for (int slot : getPaperdollOrderAugument()) {
            final VariationInstance augment = _activeChar.getInventory().getPaperdollAugmentation(slot);
            writeInt(augment != null ? augment.getOption1Id() : 0); // Confirmed
            writeInt(augment != null ? augment.getOption2Id() : 0); // Confirmed
        }

        writeByte((byte) _armorEnchant);

        writeInt(0x00); // RHAND Visual ID is not used on Classic
        writeInt(0x00); // LHAND Visual ID is not used on Classic
        writeInt(0x00); // RHAND Visual ID is not used on Classic
        writeInt(0x00); // GLOVES Visual ID is not used on Classic
        writeInt(0x00); // CHEST Visual ID is not used on Classic
        writeInt(0x00); // LEGS Visual ID is not used on Classic
        writeInt(0x00); // FEET Visual ID is not used on Classic
        writeInt(0x00); // HAIR Visual ID is not used on Classic
        writeInt(0x00); // HAIR2 Visual ID is not used on Classic

        writeByte(_activeChar.getPvpFlag());
        writeInt(_activeChar.getReputation());

        writeInt(_mAtkSpd);
        writeInt(_pAtkSpd);

        writeShort((short) _runSpd);
        writeShort((short) _walkSpd);
        writeShort((short) _swimRunSpd);
        writeShort((short) _swimWalkSpd);
        writeShort((short) _flyRunSpd);
        writeShort((short) _flyWalkSpd);
        writeShort((short) _flyRunSpd);
        writeShort((short) _flyWalkSpd);
        writeDouble(_moveMultiplier);
        writeDouble(_attackSpeedMultiplier);

        writeDouble(_activeChar.getCollisionRadius());
        writeDouble(_activeChar.getCollisionHeight());

        writeInt(_activeChar.getVisualHair());
        writeInt(_activeChar.getVisualHairColor());
        writeInt(_activeChar.getVisualFace());

        writeString(_gmSeeInvis ? "Invisible" : _activeChar.getAppearance().getVisibleTitle());

        writeInt(_activeChar.getAppearance().getVisibleClanId());
        writeInt(_activeChar.getAppearance().getVisibleClanCrestId());
        writeInt(_activeChar.getAppearance().getVisibleAllyId());
        writeInt(_activeChar.getAppearance().getVisibleAllyCrestId());

        writeByte((byte) (_activeChar.isSitting() ? 0x00 : 0x01)); // Confirmed
        writeByte((byte) (_activeChar.isRunning() ? 0x01 : 0x00)); // Confirmed
        writeByte((byte) (_activeChar.isInCombat() ? 0x01 : 0x00)); // Confirmed

        writeByte((byte) (!_activeChar.isInOlympiadMode() && _activeChar.isAlikeDead() ? 0x01 : 0x00)); // Confirmed

        writeByte((byte) (_activeChar.isInvisible() ? 0x01 : 0x00));

        writeByte((byte) _activeChar.getMountType().ordinal()); // 1-on Strider, 2-on Wyvern, 3-on Great Wolf, 0-no mount
        writeByte((byte) _activeChar.getPrivateStoreType().getId()); // Confirmed

        writeShort((short) _activeChar.getCubics().size()); // Confirmed
        _activeChar.getCubics().keySet().forEach(key -> writeShort(key.shortValue()));

        writeByte((byte) (_activeChar.isInMatchingRoom() ? 0x01 : 0x00)); // Confirmed

        writeByte((byte) (_activeChar.isInsideZone(ZoneId.WATER) ? 1 : _activeChar.isFlyingMounted() ? 2 : 0));
        writeShort((short) _activeChar.getRecomHave()); // Confirmed
        writeInt(_activeChar.getMountNpcId() == 0 ? 0 : _activeChar.getMountNpcId() + 1000000);

        writeInt(_activeChar.getClassId().getId()); // Confirmed
        writeInt(0x00); // TODO: Find me!
        writeByte((byte) (_activeChar.isMounted() ? 0 : _enchantLevel)); // Confirmed

        writeByte((byte) _activeChar.getTeam().getId()); // Confirmed

        writeInt(_activeChar.getClanCrestLargeId());
        writeByte((byte) (_activeChar.isNoble() ? 1 : 0)); // Confirmed
        writeByte((byte) (_activeChar.isHero() || (_activeChar.isGM() && Config.GM_HERO_AURA) ? 2 : 0)); // 152 - Value for enabled changed to 2?

        writeByte((byte) (_activeChar.isFishing() ? 1 : 0)); // Confirmed

        final ILocational baitLocation = _activeChar.getFishing().getBaitLocation();
        writeInt(baitLocation.getX()); // Confirmed
        writeInt(baitLocation.getY()); // Confirmed
        writeInt(baitLocation.getZ()); // Confirmed

        writeInt(_activeChar.getAppearance().getNameColor()); // Confirmed

        writeInt(_heading); // Confirmed

        writeByte((byte) _activeChar.getPledgeClass());
        writeShort((short) _activeChar.getPledgeType());

        writeInt(_activeChar.getAppearance().getTitleColor()); // Confirmed

        writeByte((byte) (_activeChar.isCursedWeaponEquipped() ? CursedWeaponsManager.getInstance().getLevel(_activeChar.getCursedWeaponEquippedId()) : 0));

        writeInt(_activeChar.getAppearance().getVisibleClanId() > 0 ? _activeChar.getClan().getReputationScore() : 0);
        writeInt(_activeChar.getTransformationDisplayId()); // Confirmed
        writeInt(_activeChar.getAgathionId()); // Confirmed

        writeByte((byte) 0x01); // TODO: Find me!

        writeInt((int) Math.round(_activeChar.getCurrentCp())); // Confirmed
        writeInt(_activeChar.getMaxHp()); // Confirmed
        writeInt((int) Math.round(_activeChar.getCurrentHp())); // Confirmed
        writeInt(_activeChar.getMaxMp()); // Confirmed
        writeInt((int) Math.round(_activeChar.getCurrentMp())); // Confirmed

        writeByte((byte) 0x00); // special effect ? TODO: Find me!
        final Set<AbnormalVisualEffect> abnormalVisualEffects = _activeChar.getEffectList().getCurrentAbnormalVisualEffects();
        writeInt(abnormalVisualEffects.size() + (_gmSeeInvis ? 1 : 0)); // Confirmed
        for (AbnormalVisualEffect abnormalVisualEffect : abnormalVisualEffects) {
            writeShort((short) abnormalVisualEffect.getClientId()); // Confirmed
        }
        if (_gmSeeInvis) {
            writeShort((short) AbnormalVisualEffect.STEALTH.getClientId());
        }
        writeByte((byte)( cocPlayer != null ? cocPlayer.getPosition() : _activeChar.isTrueHero() ? 100 : 0));
        writeByte((byte) (_activeChar.isHairAccessoryEnabled() ? 0x01 : 0x00)); // Hair accessory
        writeByte((byte) _activeChar.getAbilityPointsUsed()); // Used Ability Points
        writeLong(0x00); // 196 TODO find me
    }


    @Override
    public int[] getPaperdollOrder() {
        return PAPERDOLL_ORDER;
    }
}
