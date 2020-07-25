/*
 * Copyright © 2019-2020 L2JOrg
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
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.PET_INFO);

        writeByte(summon.getSummonType());
        writeInt(summon.getObjectId());
        writeInt(summon.getTemplate().getDisplayId() + 1000000);

        writeInt(summon.getX());
        writeInt(summon.getY());
        writeInt(summon.getZ());
        writeInt(summon.getHeading());

        writeInt(summon.getStats().getMAtkSpd());
        writeInt(summon.getStats().getPAtkSpd());

        writeShort(runSpd);
        writeShort(walkSpd);
        writeShort(swimRunSpd);
        writeShort(swimWalkSpd);
        writeShort(runSpd);
        writeShort(walkSpd);
        writeShort(flyRunSpd);
        writeShort(flyWalkSpd);

        writeDouble(moveMultiplier);
        writeDouble(summon.getAttackSpeedMultiplier());
        writeDouble(summon.getTemplate().getfCollisionRadius());
        writeDouble(summon.getTemplate().getfCollisionHeight());

        writeInt(summon.getWeapon());
        writeInt(summon.getArmor());
        writeInt(0x00); // left hand

        writeByte((summon.isShowSummonAnimation() ? 0x02 : animationType)); // 0=teleported 1=default 2=summoned
        writeInt(-1); // High Five NPCString ID
        if (isPet(summon)) {
            writeString(summon.getName());
        } else {
            writeString(summon.getTemplate().isUsingServerSideName() ? summon.getName() : "");
        }
        writeInt(-1); // High Five NPCStringID (title)
        writeString(summon.getTitle());

        writeByte(summon.getPvpFlag());
        writeInt(summon.getReputation());

        writeInt(currentFed);
        writeInt(maxFed);
        writeInt((int) summon.getCurrentHp());
        writeInt(summon.getMaxHp());
        writeInt((int) summon.getCurrentMp());
        writeInt(summon.getMaxMp());
        writeLong(summon.getStats().getSp());

        writeShort(summon.getLevel());
        writeLong(summon.getStats().getExp());
        writeLong(summon.getExpForThisLevel());
        writeLong(summon.getExpForNextLevel());

        writeInt(isPet(summon) ? summon.getInventory().getTotalWeight() : 0);
        writeInt(summon.getMaxLoad());
        writeInt(summon.getPAtk());
        writeInt(summon.getPDef());
        writeInt(summon.getAccuracy());
        writeInt(summon.getEvasionRate());
        writeInt(summon.getCriticalHit());
        writeInt(summon.getMAtk());
        writeInt(summon.getMDef());
        writeInt(summon.getMagicAccuracy());
        writeInt(summon.getMagicEvasionRate());
        writeInt(summon.getMCriticalHit());
        writeInt((int) summon.getStats().getMoveSpeed());
        writeInt(summon.getPAtkSpd());
        writeInt(summon.getMAtkSpd());

        writeByte(0); // TODO: Check me, might be ride status
        writeByte( summon.getTeam().getId());
        writeByte( summon.getSoulShotsPerHit());
        writeByte( summon.getSpiritShotsPerHit());

        writeInt(0x00); // TODO: Find me
        writeInt(summon.getFormId());

        writeByte( summon.getOwner().getSummonPoints());
        writeByte( summon.getOwner().getMaxSummonPoints());

        final Set<AbnormalVisualEffect> aves = summon.getEffectList().getCurrentAbnormalVisualEffects();
        writeShort(aves.size());
        for (AbnormalVisualEffect ave : aves) {
            writeShort(ave.getClientId());
        }

        writeByte(statusMask);
        writeInt(0);
        writeInt(0);
        writeInt(0);
    }

}
