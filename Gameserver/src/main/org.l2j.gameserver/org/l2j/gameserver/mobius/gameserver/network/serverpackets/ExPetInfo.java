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

import org.l2j.commons.network.PacketWriter;
import org.l2j.gameserver.mobius.gameserver.enums.NpcInfoType;
import org.l2j.gameserver.mobius.gameserver.enums.Team;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Summon;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.skills.AbnormalVisualEffect;
import org.l2j.gameserver.mobius.gameserver.model.zone.ZoneId;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.util.Set;

/**
 * @author Sdw
 */
public class ExPetInfo extends AbstractMaskPacket<NpcInfoType>
{
	private final L2Summon _summon;
	private final L2PcInstance _attacker;
	private final int _val;
	private final byte[] _masks = new byte[]
	{
		(byte) 0x00,
		(byte) 0x0C,
		(byte) 0x0C,
		(byte) 0x00,
		(byte) 0x00
	};
	
	private int _initSize = 0;
	private int _blockSize = 0;
	
	private int _clanCrest = 0;
	private int _clanLargeCrest = 0;
	private int _allyCrest = 0;
	private int _allyId = 0;
	private int _clanId = 0;
	private int _statusMask = 0;
	private final String _title;
	private final Set<AbnormalVisualEffect> _abnormalVisualEffects;
	
	public ExPetInfo(L2Summon summon, L2PcInstance attacker, int val)
	{
		_summon = summon;
		_attacker = attacker;
		_title = (summon.getOwner() != null) && summon.getOwner().isOnline() ? summon.getOwner().getName() : "";
		_val = val;
		_abnormalVisualEffects = summon.getEffectList().getCurrentAbnormalVisualEffects();
		
		if (summon.getTemplate().getDisplayId() != summon.getTemplate().getId())
		{
			_masks[2] |= 0x10;
			addComponentType(NpcInfoType.NAME);
		}
		
		addComponentType(NpcInfoType.ATTACKABLE, NpcInfoType.UNKNOWN1, NpcInfoType.TITLE, NpcInfoType.ID, NpcInfoType.POSITION, NpcInfoType.ALIVE, NpcInfoType.RUNNING, NpcInfoType.PVP_FLAG);
		
		if (summon.getHeading() > 0)
		{
			addComponentType(NpcInfoType.HEADING);
		}
		
		if ((summon.getStat().getPAtkSpd() > 0) || (summon.getStat().getMAtkSpd() > 0))
		{
			addComponentType(NpcInfoType.ATK_CAST_SPEED);
		}
		
		if (summon.getRunSpeed() > 0)
		{
			addComponentType(NpcInfoType.SPEED_MULTIPLIER);
		}
		
		if ((summon.getWeapon() > 0) || (summon.getArmor() > 0))
		{
			addComponentType(NpcInfoType.EQUIPPED);
		}
		
		if (summon.getTeam() != Team.NONE)
		{
			addComponentType(NpcInfoType.TEAM);
		}
		
		if (summon.isInsideZone(ZoneId.WATER) || summon.isFlying())
		{
			addComponentType(NpcInfoType.SWIM_OR_FLY);
		}
		
		if (summon.isFlying())
		{
			addComponentType(NpcInfoType.FLYING);
		}
		
		if (summon.getMaxHp() > 0)
		{
			addComponentType(NpcInfoType.MAX_HP);
		}
		
		if (summon.getMaxMp() > 0)
		{
			addComponentType(NpcInfoType.MAX_MP);
		}
		
		if (summon.getCurrentHp() <= summon.getMaxHp())
		{
			addComponentType(NpcInfoType.CURRENT_HP);
		}
		
		if (summon.getCurrentMp() <= summon.getMaxMp())
		{
			addComponentType(NpcInfoType.CURRENT_MP);
		}
		
		if (!_abnormalVisualEffects.isEmpty())
		{
			addComponentType(NpcInfoType.ABNORMALS);
		}
		
		if (summon.getTemplate().getWeaponEnchant() > 0)
		{
			addComponentType(NpcInfoType.ENCHANT);
		}
		
		if (summon.getTransformationDisplayId() > 0)
		{
			addComponentType(NpcInfoType.TRANSFORMATION);
		}
		
		if (summon.isShowSummonAnimation())
		{
			addComponentType(NpcInfoType.SUMMONED);
		}
		
		if (summon.getReputation() != 0)
		{
			addComponentType(NpcInfoType.REPUTATION);
		}
		
		if (summon.getOwner().getClan() != null)
		{
			_clanId = summon.getOwner().getAppearance().getVisibleClanId();
			_clanCrest = summon.getOwner().getAppearance().getVisibleClanCrestId();
			_clanLargeCrest = summon.getOwner().getAppearance().getVisibleClanLargeCrestId();
			_allyCrest = summon.getOwner().getAppearance().getVisibleAllyId();
			_allyId = summon.getOwner().getAppearance().getVisibleAllyCrestId();
			
			addComponentType(NpcInfoType.CLAN);
		}
		
		addComponentType(NpcInfoType.COLOR_EFFECT);
		
		// TODO: Confirm me
		if (summon.isInCombat())
		{
			_statusMask |= 0x01;
		}
		if (summon.isDead())
		{
			_statusMask |= 0x02;
		}
		if (summon.isTargetable())
		{
			_statusMask |= 0x04;
		}
		
		_statusMask |= 0x08;
		
		if (_statusMask != 0)
		{
			addComponentType(NpcInfoType.VISUAL_STATE);
		}
	}
	
	@Override
	protected byte[] getMasks()
	{
		return _masks;
	}
	
	@Override
	protected void onNewMaskAdded(NpcInfoType component)
	{
		calcBlockSize(_summon, component);
	}
	
	private void calcBlockSize(L2Summon summon, NpcInfoType type)
	{
		switch (type)
		{
			case ATTACKABLE:
			case UNKNOWN1:
			{
				_initSize += type.getBlockLength();
				break;
			}
			case TITLE:
			{
				_initSize += type.getBlockLength() + (_title.length() * 2);
				break;
			}
			case NAME:
			{
				_blockSize += type.getBlockLength() + (summon.getName().length() * 2);
				break;
			}
			default:
			{
				_blockSize += type.getBlockLength();
				break;
			}
		}
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_PET_INFO.writeId(packet);
		
		packet.writeD(_summon.getObjectId());
		packet.writeC(_val); // // 0=teleported 1=default 2=summoned
		packet.writeH(37); // mask_bits_37
		packet.writeB(_masks);
		
		// Block 1
		packet.writeC(_initSize);
		
		if (containsMask(NpcInfoType.ATTACKABLE))
		{
			packet.writeC(_summon.isAutoAttackable(_attacker) ? 0x01 : 0x00);
		}
		if (containsMask(NpcInfoType.UNKNOWN1))
		{
			packet.writeD(0x00); // unknown
		}
		if (containsMask(NpcInfoType.TITLE))
		{
			packet.writeS(_title);
		}
		
		// Block 2
		packet.writeH(_blockSize);
		if (containsMask(NpcInfoType.ID))
		{
			packet.writeD(_summon.getTemplate().getDisplayId() + 1000000);
		}
		if (containsMask(NpcInfoType.POSITION))
		{
			packet.writeD(_summon.getX());
			packet.writeD(_summon.getY());
			packet.writeD(_summon.getZ());
		}
		if (containsMask(NpcInfoType.HEADING))
		{
			packet.writeD(_summon.getHeading());
		}
		if (containsMask(NpcInfoType.UNKNOWN2))
		{
			packet.writeD(0x00); // Unknown
		}
		if (containsMask(NpcInfoType.ATK_CAST_SPEED))
		{
			packet.writeD(_summon.getPAtkSpd());
			packet.writeD(_summon.getMAtkSpd());
		}
		if (containsMask(NpcInfoType.SPEED_MULTIPLIER))
		{
			packet.writeE((float) _summon.getStat().getMovementSpeedMultiplier());
			packet.writeE((float) _summon.getStat().getAttackSpeedMultiplier());
		}
		if (containsMask(NpcInfoType.EQUIPPED))
		{
			packet.writeD(_summon.getWeapon());
			packet.writeD(_summon.getArmor()); // Armor id?
			packet.writeD(0x00);
		}
		if (containsMask(NpcInfoType.ALIVE))
		{
			packet.writeC(_summon.isDead() ? 0x00 : 0x01);
		}
		if (containsMask(NpcInfoType.RUNNING))
		{
			packet.writeC(_summon.isRunning() ? 0x01 : 0x00);
		}
		if (containsMask(NpcInfoType.SWIM_OR_FLY))
		{
			packet.writeC(_summon.isInsideZone(ZoneId.WATER) ? 0x01 : _summon.isFlying() ? 0x02 : 0x00);
		}
		if (containsMask(NpcInfoType.TEAM))
		{
			packet.writeC(_summon.getTeam().getId());
		}
		if (containsMask(NpcInfoType.ENCHANT))
		{
			packet.writeD(_summon.getTemplate().getWeaponEnchant());
		}
		if (containsMask(NpcInfoType.FLYING))
		{
			packet.writeD(_summon.isFlying() ? 0x01 : 00);
		}
		if (containsMask(NpcInfoType.CLONE))
		{
			packet.writeD(0x00); // Player ObjectId with Decoy
		}
		if (containsMask(NpcInfoType.COLOR_EFFECT))
		{
			// No visual effect
			packet.writeD(0x00); // Unknown
		}
		if (containsMask(NpcInfoType.DISPLAY_EFFECT))
		{
			packet.writeD(0x00);
		}
		if (containsMask(NpcInfoType.TRANSFORMATION))
		{
			packet.writeD(_summon.getTransformationDisplayId()); // Transformation ID
		}
		if (containsMask(NpcInfoType.CURRENT_HP))
		{
			packet.writeD((int) _summon.getCurrentHp());
		}
		if (containsMask(NpcInfoType.CURRENT_MP))
		{
			packet.writeD((int) _summon.getCurrentMp());
		}
		if (containsMask(NpcInfoType.MAX_HP))
		{
			packet.writeD(_summon.getMaxHp());
		}
		if (containsMask(NpcInfoType.MAX_MP))
		{
			packet.writeD(_summon.getMaxMp());
		}
		if (containsMask(NpcInfoType.SUMMONED))
		{
			packet.writeC(_summon.isShowSummonAnimation() ? 0x02 : 0x00); // 2 - do some animation on spawn
		}
		if (containsMask(NpcInfoType.UNKNOWN12))
		{
			packet.writeD(0x00);
			packet.writeD(0x00);
		}
		if (containsMask(NpcInfoType.NAME))
		{
			packet.writeS(_summon.getName());
		}
		if (containsMask(NpcInfoType.NAME_NPCSTRINGID))
		{
			packet.writeD(-1); // NPCStringId for name
		}
		if (containsMask(NpcInfoType.TITLE_NPCSTRINGID))
		{
			packet.writeD(-1); // NPCStringId for title
		}
		if (containsMask(NpcInfoType.PVP_FLAG))
		{
			packet.writeC(_summon.getPvpFlag()); // PVP flag
		}
		if (containsMask(NpcInfoType.REPUTATION))
		{
			packet.writeD(_summon.getReputation()); // Name color
		}
		if (containsMask(NpcInfoType.CLAN))
		{
			packet.writeD(_clanId);
			packet.writeD(_clanCrest);
			packet.writeD(_clanLargeCrest);
			packet.writeD(_allyId);
			packet.writeD(_allyCrest);
		}
		
		if (containsMask(NpcInfoType.VISUAL_STATE))
		{
			packet.writeC(_statusMask);
		}
		
		if (containsMask(NpcInfoType.ABNORMALS))
		{
			packet.writeH(_abnormalVisualEffects.size());
			for (AbnormalVisualEffect abnormalVisualEffect : _abnormalVisualEffects)
			{
				packet.writeH(abnormalVisualEffect.getClientId());
			}
		}
		return true;
	}
}