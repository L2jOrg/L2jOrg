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
package org.l2j.gameserver.mobius.gameserver.model;

import com.l2jmobius.commons.util.Rnd;
import com.l2jmobius.gameserver.ai.CtrlIntention;
import com.l2jmobius.gameserver.ai.L2ControllableMobAI;
import com.l2jmobius.gameserver.datatables.SpawnTable;
import com.l2jmobius.gameserver.model.actor.L2Character;
import com.l2jmobius.gameserver.model.actor.instance.L2ControllableMobInstance;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.actor.templates.L2NpcTemplate;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author littlecrow
 */
public final class MobGroup
{
	private final L2NpcTemplate _npcTemplate;
	private final int _groupId;
	private final int _maxMobCount;
	
	private Set<L2ControllableMobInstance> _mobs;
	
	public MobGroup(int groupId, L2NpcTemplate npcTemplate, int maxMobCount)
	{
		_groupId = groupId;
		_npcTemplate = npcTemplate;
		_maxMobCount = maxMobCount;
	}
	
	public int getActiveMobCount()
	{
		return getMobs().size();
	}
	
	public int getGroupId()
	{
		return _groupId;
	}
	
	public int getMaxMobCount()
	{
		return _maxMobCount;
	}
	
	public Set<L2ControllableMobInstance> getMobs()
	{
		if (_mobs == null)
		{
			_mobs = ConcurrentHashMap.newKeySet();
		}
		
		return _mobs;
	}
	
	public String getStatus()
	{
		try
		{
			final L2ControllableMobAI mobGroupAI = (L2ControllableMobAI) getMobs().stream().findFirst().get().getAI();
			
			switch (mobGroupAI.getAlternateAI())
			{
				case L2ControllableMobAI.AI_NORMAL:
				{
					return "Idle";
				}
				case L2ControllableMobAI.AI_FORCEATTACK:
				{
					return "Force Attacking";
				}
				case L2ControllableMobAI.AI_FOLLOW:
				{
					return "Following";
				}
				case L2ControllableMobAI.AI_CAST:
				{
					return "Casting";
				}
				case L2ControllableMobAI.AI_ATTACK_GROUP:
				{
					return "Attacking Group";
				}
				default:
				{
					return "Idle";
				}
			}
		}
		catch (Exception e)
		{
			return "Unspawned";
		}
	}
	
	public L2NpcTemplate getTemplate()
	{
		return _npcTemplate;
	}
	
	public boolean isGroupMember(L2ControllableMobInstance mobInst)
	{
		for (L2ControllableMobInstance groupMember : getMobs())
		{
			if (groupMember == null)
			{
				continue;
			}
			
			if (groupMember.getObjectId() == mobInst.getObjectId())
			{
				return true;
			}
		}
		
		return false;
	}
	
	public void spawnGroup(int x, int y, int z)
	{
		if (getMobs().size() > 0)
		{
			return;
		}
		
		try
		{
			for (int i = 0; i < _maxMobCount; i++)
			{
				final L2GroupSpawn spawn = new L2GroupSpawn(_npcTemplate);
				
				final int signX = Rnd.nextBoolean() ? -1 : 1;
				final int signY = Rnd.nextBoolean() ? -1 : 1;
				final int randX = Rnd.get(MobGroupTable.RANDOM_RANGE);
				final int randY = Rnd.get(MobGroupTable.RANDOM_RANGE);
				
				spawn.setXYZ(x + (signX * randX), y + (signY * randY), z);
				spawn.stopRespawn();
				
				SpawnTable.getInstance().addNewSpawn(spawn, false);
				getMobs().add((L2ControllableMobInstance) spawn.doGroupSpawn());
			}
		}
		catch (ClassNotFoundException e)
		{
		}
		catch (NoSuchMethodException e2)
		{
		}
	}
	
	public void spawnGroup(L2PcInstance activeChar)
	{
		spawnGroup(activeChar.getX(), activeChar.getY(), activeChar.getZ());
	}
	
	public void teleportGroup(L2PcInstance player)
	{
		removeDead();
		
		for (L2ControllableMobInstance mobInst : getMobs())
		{
			if (mobInst == null)
			{
				continue;
			}
			
			if (!mobInst.isDead())
			{
				final int x = player.getX() + Rnd.get(50);
				final int y = player.getY() + Rnd.get(50);
				
				mobInst.teleToLocation(new Location(x, y, player.getZ()), true);
				((L2ControllableMobAI) mobInst.getAI()).follow(player);
			}
		}
	}
	
	public L2ControllableMobInstance getRandomMob()
	{
		removeDead();
		
		if (getMobs().size() == 0)
		{
			return null;
		}
		
		int choice = Rnd.get(getMobs().size());
		for (L2ControllableMobInstance mob : getMobs())
		{
			if (--choice == 0)
			{
				return mob;
			}
		}
		return null;
	}
	
	public void unspawnGroup()
	{
		removeDead();
		
		if (getMobs().size() == 0)
		{
			return;
		}
		
		for (L2ControllableMobInstance mobInst : getMobs())
		{
			if (mobInst == null)
			{
				continue;
			}
			
			if (!mobInst.isDead())
			{
				mobInst.deleteMe();
			}
			
			SpawnTable.getInstance().deleteSpawn(mobInst.getSpawn(), false);
		}
		
		getMobs().clear();
	}
	
	public void killGroup(L2PcInstance activeChar)
	{
		removeDead();
		
		for (L2ControllableMobInstance mobInst : getMobs())
		{
			if (mobInst == null)
			{
				continue;
			}
			
			if (!mobInst.isDead())
			{
				mobInst.reduceCurrentHp(mobInst.getMaxHp() + 1, activeChar, null);
			}
			
			SpawnTable.getInstance().deleteSpawn(mobInst.getSpawn(), false);
		}
		
		getMobs().clear();
	}
	
	public void setAttackRandom()
	{
		removeDead();
		
		for (L2ControllableMobInstance mobInst : getMobs())
		{
			if (mobInst == null)
			{
				continue;
			}
			
			final L2ControllableMobAI ai = (L2ControllableMobAI) mobInst.getAI();
			ai.setAlternateAI(L2ControllableMobAI.AI_NORMAL);
			ai.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
		}
	}
	
	public void setAttackTarget(L2Character target)
	{
		removeDead();
		
		for (L2ControllableMobInstance mobInst : getMobs())
		{
			if (mobInst == null)
			{
				continue;
			}
			
			((L2ControllableMobAI) mobInst.getAI()).forceAttack(target);
		}
	}
	
	public void setIdleMode()
	{
		removeDead();
		
		for (L2ControllableMobInstance mobInst : getMobs())
		{
			if (mobInst == null)
			{
				continue;
			}
			
			((L2ControllableMobAI) mobInst.getAI()).stop();
		}
	}
	
	public void returnGroup(L2Character activeChar)
	{
		setIdleMode();
		
		for (L2ControllableMobInstance mobInst : getMobs())
		{
			if (mobInst == null)
			{
				continue;
			}
			
			final int signX = Rnd.nextBoolean() ? -1 : 1;
			final int signY = Rnd.nextBoolean() ? -1 : 1;
			final int randX = Rnd.get(MobGroupTable.RANDOM_RANGE);
			final int randY = Rnd.get(MobGroupTable.RANDOM_RANGE);
			
			final L2ControllableMobAI ai = (L2ControllableMobAI) mobInst.getAI();
			ai.move(activeChar.getX() + (signX * randX), activeChar.getY() + (signY * randY), activeChar.getZ());
		}
	}
	
	public void setFollowMode(L2Character character)
	{
		removeDead();
		
		for (L2ControllableMobInstance mobInst : getMobs())
		{
			if (mobInst == null)
			{
				continue;
			}
			
			((L2ControllableMobAI) mobInst.getAI()).follow(character);
		}
	}
	
	public void setCastMode()
	{
		removeDead();
		
		for (L2ControllableMobInstance mobInst : getMobs())
		{
			if (mobInst == null)
			{
				continue;
			}
			
			((L2ControllableMobAI) mobInst.getAI()).setAlternateAI(L2ControllableMobAI.AI_CAST);
		}
	}
	
	public void setNoMoveMode(boolean enabled)
	{
		removeDead();
		
		for (L2ControllableMobInstance mobInst : getMobs())
		{
			if (mobInst == null)
			{
				continue;
			}
			
			((L2ControllableMobAI) mobInst.getAI()).setNotMoving(enabled);
		}
	}
	
	protected void removeDead()
	{
		getMobs().removeIf(L2Character::isDead);
	}
	
	public void setInvul(boolean invulState)
	{
		removeDead();
		
		for (L2ControllableMobInstance mobInst : getMobs())
		{
			if (mobInst != null)
			{
				mobInst.setInvul(invulState);
			}
		}
	}
	
	public void setAttackGroup(MobGroup otherGrp)
	{
		removeDead();
		
		for (L2ControllableMobInstance mobInst : getMobs())
		{
			if (mobInst == null)
			{
				continue;
			}
			
			final L2ControllableMobAI ai = (L2ControllableMobAI) mobInst.getAI();
			ai.forceAttackGroup(otherGrp);
			ai.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
		}
	}
}