/*
 * Copyright © 2019-2021 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.actor.instance.Servitor;
import org.l2j.gameserver.model.skills.AbnormalVisualEffect;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.taskmanager.AttackStanceTaskManager;

import java.util.Set;

import static org.l2j.gameserver.util.GameUtils.isPet;

/**
 * @author JoeAlisson
 */
public class PetInfo extends ServerPacket {
    private final Summon summon;
    private final int animationType;
    private final int runSpd;
    private final int walkSpd;
    private final int swimRunSpd;
    private final int swimWalkSpd;
    private final int flyRunSpd;
    private final int flyWalkSpd;
    private final double moveMultiplier;
    private int maxFed;
    private int currentFed;
    private int statusMask = 0;

    public PetInfo(Summon summon, int val) {
        this.summon = summon;
        moveMultiplier = summon.getMovementSpeedMultiplier();
        runSpd = (int) Math.round(summon.getRunSpeed() / moveMultiplier);
        walkSpd = (int) Math.round(summon.getWalkSpeed() / moveMultiplier);
        swimRunSpd = (int) Math.round(summon.getSwimRunSpeed() / moveMultiplier);
        swimWalkSpd = (int) Math.round(summon.getSwimWalkSpeed() / moveMultiplier);
        flyRunSpd = summon.isFlying() ? runSpd : 0;
        flyWalkSpd = summon.isFlying() ? walkSpd : 0;
        animationType = val;
        if (isPet(summon)) {
            final Pet pet = (Pet) this.summon;
            currentFed = pet.getCurrentFed(); // how fed it is
            maxFed = pet.getMaxFed(); // max fed it can be
        } else if (summon.isServitor()) {
            final Servitor sum = (Servitor) this.summon;
            currentFed = sum.getLifeTimeRemaining();
            maxFed = sum.getLifeTime();
        }

        if (summon.isBetrayed()) {
            statusMask |= 0x01; // Auto attackable status
        }
        statusMask |= 0x02; // can be chatted with

        if (summon.isRunning()) {
            statusMask |= 0x04;
        }
        if (AttackStanceTaskManager.getInstance().hasAttackStanceTask(summon)) {
            statusMask |= 0x08;
        }
        if (summon.isDead()) {
            statusMask |= 0x10;
        }
        if (summon.isMountable()) {
            statusMask |= 0x20;
        }
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.PET_INFO, buffer );

        buffer.writeByte(summon.getSummonType());
        buffer.writeInt(summon.getObjectId());
        buffer.writeInt(summon.getTemplate().getDisplayId() + 1000000);

        buffer.writeInt(summon.getX());
        buffer.writeInt(summon.getY());
        buffer.writeInt(summon.getZ());
        buffer.writeInt(summon.getHeading());

        buffer.writeInt(summon.getStats().getMAtkSpd());
        buffer.writeInt(summon.getStats().getPAtkSpd());

        buffer.writeShort(runSpd);
        buffer.writeShort(walkSpd);
        buffer.writeShort(swimRunSpd);
        buffer.writeShort(swimWalkSpd);
        buffer.writeShort(runSpd);
        buffer.writeShort(walkSpd);
        buffer.writeShort(flyRunSpd);
        buffer.writeShort(flyWalkSpd);

        buffer.writeDouble(moveMultiplier);
        buffer.writeDouble(summon.getAttackSpeedMultiplier());
        buffer.writeDouble(summon.getTemplate().getfCollisionRadius());
        buffer.writeDouble(summon.getTemplate().getfCollisionHeight());

        buffer.writeInt(summon.getWeapon());
        buffer.writeInt(summon.getArmor());
        buffer.writeInt(0x00); // left hand

        buffer.writeByte((summon.isShowSummonAnimation() ? 0x02 : animationType)); // 0=teleported 1=default 2=summoned
        buffer.writeInt(-1); // High Five NPCString ID
        if (isPet(summon)) {
            buffer.writeString(summon.getName());
        } else {
            buffer.writeString(summon.getTemplate().isUsingServerSideName() ? summon.getName() : "");
        }
        buffer.writeInt(-1); // High Five NPCStringID (title)
        buffer.writeString(summon.getTitle());

        buffer.writeByte(summon.getPvpFlag());
        buffer.writeInt(summon.getReputation());

        buffer.writeInt(currentFed);
        buffer.writeInt(maxFed);
        buffer.writeInt((int) summon.getCurrentHp());
        buffer.writeInt(summon.getMaxHp());
        buffer.writeInt((int) summon.getCurrentMp());
        buffer.writeInt(summon.getMaxMp());
        buffer.writeLong(summon.getStats().getSp());

        buffer.writeShort(summon.getLevel());
        buffer.writeLong(summon.getStats().getExp());
        buffer.writeLong(summon.getExpForThisLevel());
        buffer.writeLong(summon.getExpForNextLevel());

        buffer.writeInt(isPet(summon) ? summon.getInventory().getTotalWeight() : 0);
        buffer.writeInt(summon.getMaxLoad());
        buffer.writeInt(summon.getPAtk());
        buffer.writeInt(summon.getPDef());
        buffer.writeInt(summon.getAccuracy());
        buffer.writeInt(summon.getEvasionRate());
        buffer.writeInt(summon.getCriticalHit());
        buffer.writeInt(summon.getMAtk());
        buffer.writeInt(summon.getMDef());
        buffer.writeInt(summon.getMagicAccuracy());
        buffer.writeInt(summon.getMagicEvasionRate());
        buffer.writeInt(summon.getMCriticalHit());
        buffer.writeInt((int) summon.getStats().getMoveSpeed());
        buffer.writeInt(summon.getPAtkSpd());
        buffer.writeInt(summon.getMAtkSpd());

        buffer.writeByte(0); // TODO: Check me, might be ride status
        buffer.writeByte( summon.getTeam().getId());
        buffer.writeByte( summon.getSoulShotsPerHit());
        buffer.writeByte( summon.getSpiritShotsPerHit());

        buffer.writeInt(0x00); // TODO: Find me
        buffer.writeInt(summon.getFormId());

        buffer.writeByte( 0 ); // summon points
        buffer.writeByte( summon.getOwner().getMaxSummonPoints());

        final Set<AbnormalVisualEffect> aves = summon.getEffectList().getCurrentAbnormalVisualEffects();
        buffer.writeShort(aves.size());
        for (AbnormalVisualEffect ave : aves) {
            buffer.writeShort(ave.getClientId());
        }

        buffer.writeByte(statusMask);
        buffer.writeInt(0);
        buffer.writeInt(0);
        buffer.writeInt(0);
    }

}
