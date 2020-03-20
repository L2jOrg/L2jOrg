package org.l2j.gameserver.network.serverpackets;

import org.l2j.commons.util.Util;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.instancemanager.CursedWeaponsManager;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.VariationInstance;
import org.l2j.gameserver.model.actor.instance.Decoy;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.ceremonyofchaos.CeremonyOfChaosEvent;
import org.l2j.gameserver.model.ceremonyofchaos.CeremonyOfChaosMember;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.skills.AbnormalVisualEffect;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.Set;

import static org.l2j.commons.util.Util.zeroIfNullOrElse;
import static org.l2j.gameserver.enums.InventorySlot.*;

public class CharInfo extends ServerPacket {

    private static final InventorySlot[] PAPERDOLL_ORDER = new InventorySlot[] {PENDANT, HEAD, RIGHT_HAND, LEFT_HAND, GLOVES, CHEST, LEGS, FEET, CLOAK, TWO_HAND, HAIR, HAIR2};

    private final Player player;
    private final Clan _clan;
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
    private int _enchantLevel;
    private int _armorEnchant;
    private int _vehicleId = 0;

    public CharInfo(Player player, boolean gmSeeInvis) {
        this.player = player;
        _objId = player.getObjectId();
        _clan = player.getClan();
        if ((this.player.getVehicle() != null) && (this.player.getInVehiclePosition() != null)) {
            _x = this.player.getInVehiclePosition().getX();
            _y = this.player.getInVehiclePosition().getY();
            _z = this.player.getInVehiclePosition().getZ();
            _vehicleId = this.player.getVehicle().getObjectId();
        } else {
            _x = this.player.getX();
            _y = this.player.getY();
            _z = this.player.getZ();
        }
        _heading = this.player.getHeading();
        _mAtkSpd = this.player.getMAtkSpd();
        _pAtkSpd = this.player.getPAtkSpd();
        _attackSpeedMultiplier = (float) this.player.getAttackSpeedMultiplier();
        _moveMultiplier = player.getMovementSpeedMultiplier();
        _runSpd = (int) Math.round(player.getRunSpeed() / _moveMultiplier);
        _walkSpd = (int) Math.round(player.getWalkSpeed() / _moveMultiplier);
        _swimRunSpd = (int) Math.round(player.getSwimRunSpeed() / _moveMultiplier);
        _swimWalkSpd = (int) Math.round(player.getSwimWalkSpeed() / _moveMultiplier);
        _flyRunSpd = player.isFlying() ? _runSpd : 0;
        _flyWalkSpd = player.isFlying() ? _walkSpd : 0;
        _enchantLevel = player.getInventory().getWeaponEnchant();
        _armorEnchant = player.getInventory().getArmorMinEnchant();
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
        final CeremonyOfChaosEvent event = player.getEvent(CeremonyOfChaosEvent.class);
        final CeremonyOfChaosMember cocPlayer = event != null ? event.getMember(player.getObjectId()) : null;
        writeByte(0x00); // Grand Crusade
        writeInt(_x); // Confirmed
        writeInt(_y); // Confirmed
        writeInt(_z); // Confirmed
        writeInt(_vehicleId); // Confirmed
        writeInt(_objId); // Confirmed
        writeString(player.getAppearance().getVisibleName()); // Confirmed

        writeShort((short) player.getRace().ordinal()); // Confirmed
        writeByte((byte) (player.getAppearance().isFemale() ? 0x01 : 0x00)); // Confirmed
        writeInt(player.getBaseClass()); // Confirmed

        for (var slot : getPaperdollOrder()) {
            writeInt(player.getInventory().getPaperdollItemDisplayId(slot)); // Confirmed
        }

        for (var slot : getPaperdollOrderAugument()) {
            final VariationInstance augment = player.getInventory().getPaperdollAugmentation(slot);
            writeInt(augment != null ? augment.getOption1Id() : 0); // Confirmed
            writeInt(augment != null ? augment.getOption2Id() : 0); // Confirmed
        }

        writeByte(_armorEnchant);

        writeInt(0x00); // RHAND Visual ID is not used on Classic
        writeInt(0x00); // LHAND Visual ID is not used on Classic
        writeInt(0x00); // RHAND Visual ID is not used on Classic
        writeInt(0x00); // GLOVES Visual ID is not used on Classic
        writeInt(0x00); // CHEST Visual ID is not used on Classic
        writeInt(0x00); // LEGS Visual ID is not used on Classic
        writeInt(0x00); // FEET Visual ID is not used on Classic
        writeInt(0x00); // HAIR Visual ID is not used on Classic
        writeInt(0x00); // HAIR2 Visual ID is not used on Classic

        writeByte(player.getPvpFlag());
        writeInt(player.getReputation());

        writeInt(_mAtkSpd);
        writeInt(_pAtkSpd);

        writeShort(_runSpd);
        writeShort( _walkSpd);
        writeShort( _swimRunSpd);
        writeShort( _swimWalkSpd);
        writeShort( _flyRunSpd);
        writeShort( _flyWalkSpd);
        writeShort( _flyRunSpd);
        writeShort( _flyWalkSpd);
        writeDouble(_moveMultiplier);
        writeDouble(_attackSpeedMultiplier);

        writeDouble(player.getCollisionRadius());
        writeDouble(player.getCollisionHeight());

        writeInt(player.getVisualHair());
        writeInt(player.getVisualHairColor());
        writeInt(player.getVisualFace());

        writeString(_gmSeeInvis ? "Invisible" : player.getAppearance().getVisibleTitle());

        writeInt(player.getAppearance().getVisibleClanId());
        writeInt(player.getAppearance().getVisibleClanCrestId());
        writeInt(player.getAppearance().getVisibleAllyId());
        writeInt(player.getAppearance().getVisibleAllyCrestId());

        writeByte(!player.isSitting()); // Confirmed
        writeByte(player.isRunning()); // Confirmed
        writeByte(player.isInCombat()); // Confirmed

        writeByte(!player.isInOlympiadMode() && player.isAlikeDead()); // Confirmed

        writeByte(player.isInvisible());

        writeByte(player.getMountType().ordinal()); // 1-on Strider, 2-on Wyvern, 3-on Great Wolf, 0-no mount
        writeByte(player.getPrivateStoreType().getId()); // Confirmed

        writeShort(player.getCubics().size()); // Confirmed
        player.getCubics().keySet().forEach(key -> writeShort(key.shortValue()));

        writeByte(player.isInMatchingRoom()); // Confirmed

        writeByte(player.isInsideZone(ZoneType.WATER) ? 1 : player.isFlyingMounted() ? 2 : 0);
        writeShort(player.getRecomHave()); // Confirmed
        writeInt(player.getMountNpcId() == 0 ? 0 : player.getMountNpcId() + 1000000);

        writeInt(player.getClassId().getId()); // Confirmed
        writeInt(0x00); // TODO: Find me!
        writeByte(player.isMounted() ? 0 : _enchantLevel); // Confirmed

        writeByte(player.getTeam().getId()); // Confirmed

        writeInt(player.getClanCrestLargeId());
        writeByte(player.isNoble()); // Confirmed
        writeByte((player.isHero() || (player.isGM() && Config.GM_HERO_AURA) ? 2 : 0)); // 152 - Value for enabled changed to 2?

        writeByte(player.isFishing()); // Confirmed

        final ILocational baitLocation = player.getFishing().getBaitLocation();
        writeInt(baitLocation.getX()); // Confirmed
        writeInt(baitLocation.getY()); // Confirmed
        writeInt(baitLocation.getZ()); // Confirmed

        writeInt(player.getAppearance().getNameColor()); // Confirmed

        writeInt(_heading); // Confirmed

        writeByte(player.getPledgeClass());
        writeShort(player.getPledgeType());

        writeInt(player.getAppearance().getTitleColor()); // Confirmed

        writeByte((player.isCursedWeaponEquipped() ? CursedWeaponsManager.getInstance().getLevel(player.getCursedWeaponEquippedId()) : 0));

        writeInt(zeroIfNullOrElse(_clan, Clan::getReputationScore));
        writeInt(player.getTransformationDisplayId()); // Confirmed
        writeInt(player.getAgathionId()); // Confirmed

        writeByte(0x01); // TODO: Find me!

        writeInt((int) Math.round(player.getCurrentCp())); // Confirmed
        writeInt(player.getMaxHp()); // Confirmed
        writeInt((int) Math.round(player.getCurrentHp())); // Confirmed
        writeInt(player.getMaxMp()); // Confirmed
        writeInt((int) Math.round(player.getCurrentMp())); // Confirmed

        writeByte(0x00); // special effect ? TODO: Find me!
        final Set<AbnormalVisualEffect> abnormalVisualEffects = player.getEffectList().getCurrentAbnormalVisualEffects();
        writeInt(abnormalVisualEffects.size() + (_gmSeeInvis ? 1 : 0)); // Confirmed
        for (AbnormalVisualEffect abnormalVisualEffect : abnormalVisualEffects) {
            writeShort(abnormalVisualEffect.getClientId()); // Confirmed
        }
        if (_gmSeeInvis) {
            writeShort(AbnormalVisualEffect.STEALTH.getClientId());
        }
        writeByte(cocPlayer != null ? cocPlayer.getPosition() : player.isTrueHero() ? 100 : 0);
        writeByte(player.isHairAccessoryEnabled()); // Hair accessory
        writeByte(player.getAbilityPointsUsed()); // Used Ability Points
        writeLong(0x00); // 196 TODO find me
    }


    @Override
    public InventorySlot[] getPaperdollOrder() {
        return PAPERDOLL_ORDER;
    }
}
