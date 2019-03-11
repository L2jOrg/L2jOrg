package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.L2Summon;
import org.l2j.gameserver.model.actor.instance.L2PetInstance;
import org.l2j.gameserver.model.actor.instance.L2ServitorInstance;
import org.l2j.gameserver.model.skills.AbnormalVisualEffect;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.taskmanager.AttackStanceTaskManager;

import java.nio.ByteBuffer;
import java.util.Set;

public class PetInfo extends IClientOutgoingPacket {
    private final L2Summon _summon;
    private final int _val;
    private final int _runSpd;
    private final int _walkSpd;
    private final int _swimRunSpd;
    private final int _swimWalkSpd;
    private final int _flRunSpd = 0;
    private final int _flWalkSpd = 0;
    private final int _flyRunSpd;
    private final int _flyWalkSpd;
    private final double _moveMultiplier;
    private int _maxFed;
    private int _curFed;
    private int _statusMask = 0;

    public PetInfo(L2Summon summon, int val) {
        _summon = summon;
        _moveMultiplier = summon.getMovementSpeedMultiplier();
        _runSpd = (int) Math.round(summon.getRunSpeed() / _moveMultiplier);
        _walkSpd = (int) Math.round(summon.getWalkSpeed() / _moveMultiplier);
        _swimRunSpd = (int) Math.round(summon.getSwimRunSpeed() / _moveMultiplier);
        _swimWalkSpd = (int) Math.round(summon.getSwimWalkSpeed() / _moveMultiplier);
        _flyRunSpd = summon.isFlying() ? _runSpd : 0;
        _flyWalkSpd = summon.isFlying() ? _walkSpd : 0;
        _val = val;
        if (summon.isPet()) {
            final L2PetInstance pet = (L2PetInstance) _summon;
            _curFed = pet.getCurrentFed(); // how fed it is
            _maxFed = pet.getMaxFed(); // max fed it can be
        } else if (summon.isServitor()) {
            final L2ServitorInstance sum = (L2ServitorInstance) _summon;
            _curFed = sum.getLifeTimeRemaining();
            _maxFed = sum.getLifeTime();
        }

        if (summon.isBetrayed()) {
            _statusMask |= 0x01; // Auto attackable status
        }
        _statusMask |= 0x02; // can be chatted with

        if (summon.isRunning()) {
            _statusMask |= 0x04;
        }
        if (AttackStanceTaskManager.getInstance().hasAttackStanceTask(summon)) {
            _statusMask |= 0x08;
        }
        if (summon.isDead()) {
            _statusMask |= 0x10;
        }
        if (summon.isMountable()) {
            _statusMask |= 0x20;
        }
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.PET_INFO.writeId(packet);

        packet.put((byte) _summon.getSummonType());
        packet.putInt(_summon.getObjectId());
        packet.putInt(_summon.getTemplate().getDisplayId() + 1000000);

        packet.putInt(_summon.getX());
        packet.putInt(_summon.getY());
        packet.putInt(_summon.getZ());
        packet.putInt(_summon.getHeading());

        packet.putInt(_summon.getStat().getMAtkSpd());
        packet.putInt(_summon.getStat().getPAtkSpd());

        packet.putShort((short) _runSpd);
        packet.putShort((short) _walkSpd);
        packet.putShort((short) _swimRunSpd);
        packet.putShort((short) _swimWalkSpd);
        packet.putShort((short) _flRunSpd);
        packet.putShort((short) _flWalkSpd);
        packet.putShort((short) _flyRunSpd);
        packet.putShort((short) _flyWalkSpd);

        packet.putDouble(_moveMultiplier);
        packet.putDouble(_summon.getAttackSpeedMultiplier()); // attack speed multiplier
        packet.putDouble(_summon.getTemplate().getfCollisionRadius());
        packet.putDouble(_summon.getTemplate().getfCollisionHeight());

        packet.putInt(_summon.getWeapon()); // right hand weapon
        packet.putInt(_summon.getArmor()); // body armor
        packet.putInt(0x00); // left hand weapon

        packet.put((byte) (_summon.isShowSummonAnimation() ? 0x02 : _val)); // 0=teleported 1=default 2=summoned
        packet.putInt(-1); // High Five NPCString ID
        if (_summon.isPet()) {
            writeString(_summon.getName(), packet); // Pet name.
        } else {
            writeString(_summon.getTemplate().isUsingServerSideName() ? _summon.getName() : "", packet); // Summon name.
        }
        packet.putInt(-1); // High Five NPCString ID
        writeString(_summon.getTitle(), packet); // owner name

        packet.put((byte) _summon.getPvpFlag()); // confirmed
        packet.putInt(_summon.getReputation()); // confirmed

        packet.putInt(_curFed); // how fed it is
        packet.putInt(_maxFed); // max fed it can be
        packet.putInt((int) _summon.getCurrentHp()); // current hp
        packet.putInt(_summon.getMaxHp()); // max hp
        packet.putInt((int) _summon.getCurrentMp()); // current mp
        packet.putInt(_summon.getMaxMp()); // max mp

        packet.putLong(_summon.getStat().getSp()); // sp
        packet.put((byte) _summon.getLevel()); // lvl
        packet.putLong(_summon.getStat().getExp());

        if (_summon.getExpForThisLevel() > _summon.getStat().getExp()) {
            packet.putLong(_summon.getStat().getExp()); // 0% absolute value
        } else {
            packet.putLong(_summon.getExpForThisLevel()); // 0% absolute value
        }

        packet.putLong(_summon.getExpForNextLevel()); // 100% absoulte value

        packet.putInt(_summon.isPet() ? _summon.getInventory().getTotalWeight() : 0); // weight
        packet.putInt(_summon.getMaxLoad()); // max weight it can carry
        packet.putInt(_summon.getPAtk()); // patk
        packet.putInt(_summon.getPDef()); // pdef
        packet.putInt(_summon.getAccuracy()); // accuracy
        packet.putInt(_summon.getEvasionRate()); // evasion
        packet.putInt(_summon.getCriticalHit()); // critical
        packet.putInt(_summon.getMAtk()); // matk
        packet.putInt(_summon.getMDef()); // mdef
        packet.putInt(_summon.getMagicAccuracy()); // magic accuracy
        packet.putInt(_summon.getMagicEvasionRate()); // magic evasion
        packet.putInt(_summon.getMCriticalHit()); // mcritical
        packet.putInt((int) _summon.getStat().getMoveSpeed()); // speed
        packet.putInt(_summon.getPAtkSpd()); // atkspeed
        packet.putInt(_summon.getMAtkSpd()); // casting speed

        packet.put((byte) 0); // TODO: Check me, might be ride status
        packet.put((byte) _summon.getTeam().getId()); // Confirmed
        packet.put((byte) _summon.getSoulShotsPerHit()); // How many soulshots this servitor uses per hit - Confirmed
        packet.put((byte) _summon.getSpiritShotsPerHit()); // How many spiritshots this servitor uses per hit - - Confirmed

        packet.putInt(0x00); // TODO: Find me
        packet.putInt(_summon.getFormId()); // Transformation ID - Confirmed

        packet.put((byte) _summon.getOwner().getSummonPoints()); // Used Summon Points
        packet.put((byte) _summon.getOwner().getMaxSummonPoints()); // Maximum Summon Points

        final Set<AbnormalVisualEffect> aves = _summon.getEffectList().getCurrentAbnormalVisualEffects();
        packet.putShort((short) aves.size()); // Confirmed
        for (AbnormalVisualEffect ave : aves) {
            packet.putShort((short) ave.getClientId()); // Confirmed
        }

        packet.put((byte) _statusMask);
    }
}
