package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.mobius.gameserver.data.xml.impl.FakePlayerData;
import org.l2j.gameserver.mobius.gameserver.enums.Sex;
import org.l2j.gameserver.mobius.gameserver.model.L2Clan;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.mobius.gameserver.model.holders.FakePlayerHolder;
import org.l2j.gameserver.mobius.gameserver.model.skills.AbnormalVisualEffect;
import org.l2j.gameserver.mobius.gameserver.model.zone.ZoneId;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.Set;

/**
 * @author Mobius
 */
public class FakePlayerInfo extends IClientOutgoingPacket {
    private final L2Npc _npc;
    private final int _objId;
    private final int _x;
    private final int _y;
    private final int _z;
    private final int _heading;
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
    private final FakePlayerHolder _fpcHolder;
    private final L2Clan _clan;

    public FakePlayerInfo(L2Npc npc) {
        _npc = npc;
        _objId = npc.getObjectId();
        _x = npc.getX();
        _y = npc.getY();
        _z = npc.getZ();
        _heading = npc.getHeading();
        _mAtkSpd = npc.getMAtkSpd();
        _pAtkSpd = npc.getPAtkSpd();
        _attackSpeedMultiplier = (float) npc.getAttackSpeedMultiplier();
        _moveMultiplier = npc.getMovementSpeedMultiplier();
        _runSpd = (int) Math.round(npc.getRunSpeed() / _moveMultiplier);
        _walkSpd = (int) Math.round(npc.getWalkSpeed() / _moveMultiplier);
        _swimRunSpd = (int) Math.round(npc.getSwimRunSpeed() / _moveMultiplier);
        _swimWalkSpd = (int) Math.round(npc.getSwimWalkSpeed() / _moveMultiplier);
        _flyRunSpd = npc.isFlying() ? _runSpd : 0;
        _flyWalkSpd = npc.isFlying() ? _walkSpd : 0;
        _fpcHolder = FakePlayerData.getInstance().getInfo(npc.getId());
        _clan = ClanTable.getInstance().getClan(_fpcHolder.getClanId());
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.CHAR_INFO.writeId(packet);
        packet.put((byte) 0x00); // Grand Crusade
        packet.putInt(_x);
        packet.putInt(_y);
        packet.putInt(_z);
        packet.putInt(0x00); // vehicleId
        packet.putInt(_objId);
        writeString(_npc.getName(), packet);

        packet.putShort((short) _npc.getRace().ordinal());
        packet.put((byte) (_npc.getTemplate().getSex() == Sex.FEMALE ? 0x01 : 0x00));
        packet.putInt(_fpcHolder.getClassId());

        packet.putInt(0x00); // Inventory.PAPERDOLL_UNDER
        packet.putInt(_fpcHolder.getEquipHead());
        packet.putInt(_fpcHolder.getEquipRHand());
        packet.putInt(_fpcHolder.getEquipLHand());
        packet.putInt(_fpcHolder.getEquipGloves());
        packet.putInt(_fpcHolder.getEquipChest());
        packet.putInt(_fpcHolder.getEquipLegs());
        packet.putInt(_fpcHolder.getEquipFeet());
        packet.putInt(_fpcHolder.getEquipCloak());
        packet.putInt(_fpcHolder.getEquipRHand()); // dual hand
        packet.putInt(_fpcHolder.getEquipHair());
        packet.putInt(_fpcHolder.getEquipHair2());

        for (@SuppressWarnings("unused")
                int slot : getPaperdollOrderAugument()) {
            packet.putInt(0x00);
            packet.putInt(0x00);
        }

        packet.put((byte) _fpcHolder.getArmorEnchantLevel());

        for (@SuppressWarnings("unused")
                int slot : getPaperdollOrderVisualId()) {
            packet.putInt(0x00);
        }

        packet.put((byte) _npc.getScriptValue()); // getPvpFlag()
        packet.putInt(_npc.getReputation());

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

        packet.putDouble(_npc.getCollisionRadius());
        packet.putDouble(_npc.getCollisionHeight());

        packet.putInt(_fpcHolder.getHair());
        packet.putInt(_fpcHolder.getHairColor());
        packet.putInt(_fpcHolder.getFace());

        writeString(_npc.getTemplate().getTitle(), packet);

        if (_clan != null) {
            packet.putInt(_clan.getId());
            packet.putInt(_clan.getCrestId());
            packet.putInt(_clan.getAllyId());
            packet.putInt(_clan.getAllyCrestId());
        } else {
            packet.putInt(0x00);
            packet.putInt(0x00);
            packet.putInt(0x00);
            packet.putInt(0x00);
        }

        packet.put((byte) 0x01); // isSitting() ? 0x00 : 0x01 (at some initial tests it worked)
        packet.put((byte)(_npc.isRunning() ? 0x01 : 0x00));
        packet.put((byte) (_npc.isInCombat() ? 0x01 : 0x00));

        packet.put((byte) (_npc.isAlikeDead() ? 0x01 : 0x00));

        packet.put((byte) (_npc.isInvisible() ? 0x01 : 0x00));

        packet.put((byte) 0x00); // 1-on Strider, 2-on Wyvern, 3-on Great Wolf, 0-no mount
        packet.put((byte) 0x00); // getPrivateStoreType().getId()

        packet.putShort((short) 0x00); // getCubics().size()
        // getCubics().keySet().forEach(packet::writeH);

        packet.put((byte) 0x00);

        packet.put((byte) (_npc.isInsideZone(ZoneId.WATER) ? 1 : 0));
        packet.putShort((short) _fpcHolder.getRecommends());
        packet.putInt(0x00); // getMountNpcId() == 0 ? 0 : getMountNpcId() + 1000000

        packet.putInt(_fpcHolder.getClassId());
        packet.putInt(0x00);
        packet.put((byte) _fpcHolder.getWeaponEnchantLevel()); // isMounted() ? 0 : _enchantLevel

        packet.put((byte) _npc.getTeam().getId());

        packet.putInt(_clan != null ? _clan.getCrestLargeId() : 0x00);
        packet.put((byte) _fpcHolder.getNobleLevel());
        packet.put((byte) (_fpcHolder.isHero() ? 0x01 : 0x00));

        packet.put((byte) (_fpcHolder.isFishing() ? 0x01 : 0x00));

        packet.putInt(_fpcHolder.getBaitLocationX());
        packet.putInt(_fpcHolder.getBaitLocationY());
        packet.putInt(_fpcHolder.getBaitLocationZ());

        packet.putInt(_fpcHolder.getNameColor());

        packet.putInt(_heading);

        packet.put((byte) _fpcHolder.getPledgeStatus());
        packet.putShort((short) 0x00); // getPledgeType()

        packet.putInt(_fpcHolder.getTitleColor());

        packet.put((byte) 0x00); // isCursedWeaponEquipped

        packet.putInt(0x00); // getAppearance().getVisibleClanId() > 0 ? getClan().getReputationScore() : 0
        packet.putInt(0x00); // getTransformationDisplayId()
        packet.putInt(_fpcHolder.getAgathionId());

        packet.put((byte) 0x00);

        packet.putInt(0x00); // getCurrentCp()
        packet.putInt(_npc.getMaxHp());
        packet.putInt((int) Math.round(_npc.getCurrentHp()));
        packet.putInt(_npc.getMaxMp());
        packet.putInt((int) Math.round(_npc.getCurrentMp()));

        packet.put((byte) 0x00);
        final Set<AbnormalVisualEffect> abnormalVisualEffects = _npc.getEffectList().getCurrentAbnormalVisualEffects();
        packet.putInt(abnormalVisualEffects.size() + (_npc.isInvisible() ? 1 : 0));
        for (AbnormalVisualEffect abnormalVisualEffect : abnormalVisualEffects) {
            packet.putShort((short) abnormalVisualEffect.getClientId());
        }
        if (_npc.isInvisible()) {
            packet.putShort((short) AbnormalVisualEffect.STEALTH.getClientId());
        }
        packet.put((byte) 0x00); // cocPlayer.getPosition()
        packet.put((byte) ((_fpcHolder.getHair() > 0) || (_fpcHolder.getEquipHair2() > 0) ? 0x01 : 0x00));
        packet.put((byte) 0x00); // Used Ability Points
    }
}
