/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import com.l2jmobius.commons.network.PacketWriter;
import com.l2jmobius.gameserver.model.actor.L2Summon;
import com.l2jmobius.gameserver.model.actor.instance.L2PetInstance;
import com.l2jmobius.gameserver.model.actor.instance.L2ServitorInstance;
import com.l2jmobius.gameserver.model.skills.AbnormalVisualEffect;
import com.l2jmobius.gameserver.network.OutgoingPackets;
import com.l2jmobius.gameserver.taskmanager.AttackStanceTaskManager;

import java.util.Set;

public class PetInfo implements IClientOutgoingPacket
{
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
	
	public PetInfo(L2Summon summon, int val)
	{
		_summon = summon;
		_moveMultiplier = summon.getMovementSpeedMultiplier();
		_runSpd = (int) Math.round(summon.getRunSpeed() / _moveMultiplier);
		_walkSpd = (int) Math.round(summon.getWalkSpeed() / _moveMultiplier);
		_swimRunSpd = (int) Math.round(summon.getSwimRunSpeed() / _moveMultiplier);
		_swimWalkSpd = (int) Math.round(summon.getSwimWalkSpeed() / _moveMultiplier);
		_flyRunSpd = summon.isFlying() ? _runSpd : 0;
		_flyWalkSpd = summon.isFlying() ? _walkSpd : 0;
		_val = val;
		if (summon.isPet())
		{
			final L2PetInstance pet = (L2PetInstance) _summon;
			_curFed = pet.getCurrentFed(); // how fed it is
			_maxFed = pet.getMaxFed(); // max fed it can be
		}
		else if (summon.isServitor())
		{
			final L2ServitorInstance sum = (L2ServitorInstance) _summon;
			_curFed = sum.getLifeTimeRemaining();
			_maxFed = sum.getLifeTime();
		}
		
		if (summon.isBetrayed())
		{
			_statusMask |= 0x01; // Auto attackable status
		}
		_statusMask |= 0x02; // can be chatted with
		
		if (summon.isRunning())
		{
			_statusMask |= 0x04;
		}
		if (AttackStanceTaskManager.getInstance().hasAttackStanceTask(summon))
		{
			_statusMask |= 0x08;
		}
		if (summon.isDead())
		{
			_statusMask |= 0x10;
		}
		if (summon.isMountable())
		{
			_statusMask |= 0x20;
		}
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.PET_INFO.writeId(packet);
		
		packet.writeC(_summon.getSummonType());
		packet.writeD(_summon.getObjectId());
		packet.writeD(_summon.getTemplate().getDisplayId() + 1000000);
		
		packet.writeD(_summon.getX());
		packet.writeD(_summon.getY());
		packet.writeD(_summon.getZ());
		packet.writeD(_summon.getHeading());
		
		packet.writeD(_summon.getStat().getMAtkSpd());
		packet.writeD(_summon.getStat().getPAtkSpd());
		
		packet.writeH(_runSpd);
		packet.writeH(_walkSpd);
		packet.writeH(_swimRunSpd);
		packet.writeH(_swimWalkSpd);
		packet.writeH(_flRunSpd);
		packet.writeH(_flWalkSpd);
		packet.writeH(_flyRunSpd);
		packet.writeH(_flyWalkSpd);
		
		packet.writeF(_moveMultiplier);
		packet.writeF(_summon.getAttackSpeedMultiplier()); // attack speed multiplier
		packet.writeF(_summon.getTemplate().getfCollisionRadius());
		packet.writeF(_summon.getTemplate().getfCollisionHeight());
		
		packet.writeD(_summon.getWeapon()); // right hand weapon
		packet.writeD(_summon.getArmor()); // body armor
		packet.writeD(0x00); // left hand weapon
		
		packet.writeC(_summon.isShowSummonAnimation() ? 0x02 : _val); // 0=teleported 1=default 2=summoned
		packet.writeD(-1); // High Five NPCString ID
		if (_summon.isPet())
		{
			packet.writeS(_summon.getName()); // Pet name.
		}
		else
		{
			packet.writeS(_summon.getTemplate().isUsingServerSideName() ? _summon.getName() : ""); // Summon name.
		}
		packet.writeD(-1); // High Five NPCString ID
		packet.writeS(_summon.getTitle()); // owner name
		
		packet.writeC(_summon.getPvpFlag()); // confirmed
		packet.writeD(_summon.getReputation()); // confirmed
		
		packet.writeD(_curFed); // how fed it is
		packet.writeD(_maxFed); // max fed it can be
		packet.writeD((int) _summon.getCurrentHp()); // current hp
		packet.writeD(_summon.getMaxHp()); // max hp
		packet.writeD((int) _summon.getCurrentMp()); // current mp
		packet.writeD(_summon.getMaxMp()); // max mp
		
		packet.writeQ(_summon.getStat().getSp()); // sp
		packet.writeC(_summon.getLevel()); // lvl
		packet.writeQ(_summon.getStat().getExp());
		
		if (_summon.getExpForThisLevel() > _summon.getStat().getExp())
		{
			packet.writeQ(_summon.getStat().getExp()); // 0% absolute value
		}
		else
		{
			packet.writeQ(_summon.getExpForThisLevel()); // 0% absolute value
		}
		
		packet.writeQ(_summon.getExpForNextLevel()); // 100% absoulte value
		
		packet.writeD(_summon.isPet() ? _summon.getInventory().getTotalWeight() : 0); // weight
		packet.writeD(_summon.getMaxLoad()); // max weight it can carry
		packet.writeD(_summon.getPAtk()); // patk
		packet.writeD(_summon.getPDef()); // pdef
		packet.writeD(_summon.getAccuracy()); // accuracy
		packet.writeD(_summon.getEvasionRate()); // evasion
		packet.writeD(_summon.getCriticalHit()); // critical
		packet.writeD(_summon.getMAtk()); // matk
		packet.writeD(_summon.getMDef()); // mdef
		packet.writeD(_summon.getMagicAccuracy()); // magic accuracy
		packet.writeD(_summon.getMagicEvasionRate()); // magic evasion
		packet.writeD(_summon.getMCriticalHit()); // mcritical
		packet.writeD((int) _summon.getStat().getMoveSpeed()); // speed
		packet.writeD(_summon.getPAtkSpd()); // atkspeed
		packet.writeD(_summon.getMAtkSpd()); // casting speed
		
		packet.writeC(0); // TODO: Check me, might be ride status
		packet.writeC(_summon.getTeam().getId()); // Confirmed
		packet.writeC(_summon.getSoulShotsPerHit()); // How many soulshots this servitor uses per hit - Confirmed
		packet.writeC(_summon.getSpiritShotsPerHit()); // How many spiritshots this servitor uses per hit - - Confirmed
		
		packet.writeD(0x00); // TODO: Find me
		packet.writeD(_summon.getFormId()); // Transformation ID - Confirmed
		
		packet.writeC(_summon.getOwner().getSummonPoints()); // Used Summon Points
		packet.writeC(_summon.getOwner().getMaxSummonPoints()); // Maximum Summon Points
		
		final Set<AbnormalVisualEffect> aves = _summon.getEffectList().getCurrentAbnormalVisualEffects();
		packet.writeH(aves.size()); // Confirmed
		for (AbnormalVisualEffect ave : aves)
		{
			packet.writeH(ave.getClientId()); // Confirmed
		}
		
		packet.writeC(_statusMask);
		return true;
	}
}
