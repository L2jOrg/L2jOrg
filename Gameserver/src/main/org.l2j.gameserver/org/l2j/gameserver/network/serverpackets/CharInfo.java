package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.instancemanager.CursedWeaponsManager;
import org.l2j.gameserver.model.VariationInstance;
import org.l2j.gameserver.model.actor.instance.L2DecoyInstance;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.ceremonyofchaos.CeremonyOfChaosEvent;
import org.l2j.gameserver.model.ceremonyofchaos.CeremonyOfChaosMember;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.itemcontainer.Inventory;
import org.l2j.gameserver.model.skills.AbnormalVisualEffect;
import org.l2j.gameserver.model.zone.ZoneId;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.Set;

public class CharInfo extends IClientOutgoingPacket {
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
    private final L2PcInstance _activeChar;
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

    public CharInfo(L2PcInstance cha, boolean gmSeeInvis) {
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

    public CharInfo(L2DecoyInstance decoy, boolean gmSeeInvis) {
        this(decoy.getActingPlayer(), gmSeeInvis); // init
        _objId = decoy.getObjectId();
        _x = decoy.getX();
        _y = decoy.getY();
        _z = decoy.getZ();
        _heading = decoy.getHeading();
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.CHAR_INFO.writeId(packet);
        final CeremonyOfChaosEvent event = _activeChar.getEvent(CeremonyOfChaosEvent.class);
        final CeremonyOfChaosMember cocPlayer = event != null ? event.getMember(_activeChar.getObjectId()) : null;
        packet.put((byte) 0x00); // Grand Crusade
        packet.putInt(_x); // Confirmed
        packet.putInt(_y); // Confirmed
        packet.putInt(_z); // Confirmed
        packet.putInt(_vehicleId); // Confirmed
        packet.putInt(_objId); // Confirmed
        writeString(_activeChar.getAppearance().getVisibleName(), packet); // Confirmed

        packet.putShort((short) _activeChar.getRace().ordinal()); // Confirmed
        packet.put((byte) (_activeChar.getAppearance().getSex() ? 0x01 : 0x00)); // Confirmed
        packet.putInt(_activeChar.getBaseClass()); // Confirmed

        for (int slot : getPaperdollOrder()) {
            packet.putInt(_activeChar.getInventory().getPaperdollItemDisplayId(slot)); // Confirmed
        }

        for (int slot : getPaperdollOrderAugument()) {
            final VariationInstance augment = _activeChar.getInventory().getPaperdollAugmentation(slot);
            packet.putInt(augment != null ? augment.getOption1Id() : 0); // Confirmed
            packet.putInt(augment != null ? augment.getOption2Id() : 0); // Confirmed
        }

        packet.put((byte) _armorEnchant);

        packet.putInt(0x00); // RHAND Visual ID is not used on Classic
        packet.putInt(0x00); // LHAND Visual ID is not used on Classic
        packet.putInt(0x00); // RHAND Visual ID is not used on Classic
        packet.putInt(0x00); // GLOVES Visual ID is not used on Classic
        packet.putInt(0x00); // CHEST Visual ID is not used on Classic
        packet.putInt(0x00); // LEGS Visual ID is not used on Classic
        packet.putInt(0x00); // FEET Visual ID is not used on Classic
        packet.putInt(0x00); // HAIR Visual ID is not used on Classic
        packet.putInt(0x00); // HAIR2 Visual ID is not used on Classic

        packet.put(_activeChar.getPvpFlag());
        packet.putInt(_activeChar.getReputation());

        packet.putInt(_mAtkSpd);
        packet.putInt(_pAtkSpd);

        packet.putShort((short) _runSpd);
        packet.putShort((short) _walkSpd);
        packet.putShort((short) _swimRunSpd);
        packet.putShort((short) _swimWalkSpd);
        packet.putShort((short) _flyRunSpd);
        packet.putShort((short) _flyWalkSpd);
        packet.putShort((short) _flyRunSpd);
        packet.putShort((short) _flyWalkSpd);
        packet.putDouble(_moveMultiplier);
        packet.putDouble(_attackSpeedMultiplier);

        packet.putDouble(_activeChar.getCollisionRadius());
        packet.putDouble(_activeChar.getCollisionHeight());

        packet.putInt(_activeChar.getVisualHair());
        packet.putInt(_activeChar.getVisualHairColor());
        packet.putInt(_activeChar.getVisualFace());

        writeString(_gmSeeInvis ? "Invisible" : _activeChar.getAppearance().getVisibleTitle(), packet);

        packet.putInt(_activeChar.getAppearance().getVisibleClanId());
        packet.putInt(_activeChar.getAppearance().getVisibleClanCrestId());
        packet.putInt(_activeChar.getAppearance().getVisibleAllyId());
        packet.putInt(_activeChar.getAppearance().getVisibleAllyCrestId());

        packet.put((byte) (_activeChar.isSitting() ? 0x00 : 0x01)); // Confirmed
        packet.put((byte) (_activeChar.isRunning() ? 0x01 : 0x00)); // Confirmed
        packet.put((byte) (_activeChar.isInCombat() ? 0x01 : 0x00)); // Confirmed

        packet.put((byte) (!_activeChar.isInOlympiadMode() && _activeChar.isAlikeDead() ? 0x01 : 0x00)); // Confirmed

        packet.put((byte) (_activeChar.isInvisible() ? 0x01 : 0x00));

        packet.put((byte) _activeChar.getMountType().ordinal()); // 1-on Strider, 2-on Wyvern, 3-on Great Wolf, 0-no mount
        packet.put((byte) _activeChar.getPrivateStoreType().getId()); // Confirmed

        packet.putShort((short) _activeChar.getCubics().size()); // Confirmed
        _activeChar.getCubics().keySet().forEach(key -> packet.putShort(key.shortValue()));

        packet.put((byte) (_activeChar.isInMatchingRoom() ? 0x01 : 0x00)); // Confirmed

        packet.put((byte) (_activeChar.isInsideZone(ZoneId.WATER) ? 1 : _activeChar.isFlyingMounted() ? 2 : 0));
        packet.putShort((short) _activeChar.getRecomHave()); // Confirmed
        packet.putInt(_activeChar.getMountNpcId() == 0 ? 0 : _activeChar.getMountNpcId() + 1000000);

        packet.putInt(_activeChar.getClassId().getId()); // Confirmed
        packet.putInt(0x00); // TODO: Find me!
        packet.put((byte) (_activeChar.isMounted() ? 0 : _enchantLevel)); // Confirmed

        packet.put((byte) _activeChar.getTeam().getId()); // Confirmed

        packet.putInt(_activeChar.getClanCrestLargeId());
        packet.put((byte) (_activeChar.isNoble() ? 1 : 0)); // Confirmed
        packet.put((byte) (_activeChar.isHero() || (_activeChar.isGM() && Config.GM_HERO_AURA) ? 1 : 0)); // Confirmed

        packet.put((byte) (_activeChar.isFishing() ? 1 : 0)); // Confirmed

        final ILocational baitLocation = _activeChar.getFishing().getBaitLocation();
        packet.putInt(baitLocation.getX()); // Confirmed
        packet.putInt(baitLocation.getY()); // Confirmed
        packet.putInt(baitLocation.getZ()); // Confirmed

        packet.putInt(_activeChar.getAppearance().getNameColor()); // Confirmed

        packet.putInt(_heading); // Confirmed

        packet.put((byte) _activeChar.getPledgeClass());
        packet.putShort((short) _activeChar.getPledgeType());

        packet.putInt(_activeChar.getAppearance().getTitleColor()); // Confirmed

        packet.put((byte) (_activeChar.isCursedWeaponEquipped() ? CursedWeaponsManager.getInstance().getLevel(_activeChar.getCursedWeaponEquippedId()) : 0));

        packet.putInt(_activeChar.getAppearance().getVisibleClanId() > 0 ? _activeChar.getClan().getReputationScore() : 0);
        packet.putInt(_activeChar.getTransformationDisplayId()); // Confirmed
        packet.putInt(_activeChar.getAgathionId()); // Confirmed

        packet.put((byte) 0x00); // TODO: Find me!

        packet.putInt((int) Math.round(_activeChar.getCurrentCp())); // Confirmed
        packet.putInt(_activeChar.getMaxHp()); // Confirmed
        packet.putInt((int) Math.round(_activeChar.getCurrentHp())); // Confirmed
        packet.putInt(_activeChar.getMaxMp()); // Confirmed
        packet.putInt((int) Math.round(_activeChar.getCurrentMp())); // Confirmed

        packet.put((byte) 0x00); // TODO: Find me!
        final Set<AbnormalVisualEffect> abnormalVisualEffects = _activeChar.getEffectList().getCurrentAbnormalVisualEffects();
        packet.putInt(abnormalVisualEffects.size() + (_gmSeeInvis ? 1 : 0)); // Confirmed
        for (AbnormalVisualEffect abnormalVisualEffect : abnormalVisualEffects) {
            packet.putShort((short) abnormalVisualEffect.getClientId()); // Confirmed
        }
        if (_gmSeeInvis) {
            packet.putShort((short) AbnormalVisualEffect.STEALTH.getClientId());
        }
        packet.put((byte)( cocPlayer != null ? cocPlayer.getPosition() : _activeChar.isTrueHero() ? 100 : 0));
        packet.put((byte) (_activeChar.isHairAccessoryEnabled() ? 0x01 : 0x00)); // Hair accessory
        packet.put((byte) _activeChar.getAbilityPointsUsed()); // Used Ability Points
    }

    @Override
    public int[] getPaperdollOrder() {
        return PAPERDOLL_ORDER;
    }
}
