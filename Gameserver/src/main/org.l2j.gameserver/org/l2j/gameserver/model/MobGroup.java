/*
 * Copyright © 2019 L2J Mobius
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
package org.l2j.gameserver.model;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.ai.ControllableMobAI;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.datatables.SpawnTable;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.ControllableMob;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author littlecrow
 */
public final class MobGroup {
    private final NpcTemplate _npcTemplate;
    private final int _groupId;
    private final int _maxMobCount;

    private Set<ControllableMob> _mobs;

    public MobGroup(int groupId, NpcTemplate npcTemplate, int maxMobCount) {
        _groupId = groupId;
        _npcTemplate = npcTemplate;
        _maxMobCount = maxMobCount;
    }

    public int getActiveMobCount() {
        return getMobs().size();
    }

    public int getGroupId() {
        return _groupId;
    }

    public int getMaxMobCount() {
        return _maxMobCount;
    }

    public Set<ControllableMob> getMobs() {
        if (_mobs == null) {
            _mobs = ConcurrentHashMap.newKeySet();
        }

        return _mobs;
    }

    public String getStatus() {
        try {
            final ControllableMobAI mobGroupAI = (ControllableMobAI) getMobs().stream().findFirst().get().getAI();

            switch (mobGroupAI.getAlternateAI()) {
                case ControllableMobAI.AI_NORMAL: {
                    return "Idle";
                }
                case ControllableMobAI.AI_FORCEATTACK: {
                    return "Force Attacking";
                }
                case ControllableMobAI.AI_FOLLOW: {
                    return "Following";
                }
                case ControllableMobAI.AI_CAST: {
                    return "Casting";
                }
                case ControllableMobAI.AI_ATTACK_GROUP: {
                    return "Attacking Group";
                }
                default: {
                    return "Idle";
                }
            }
        } catch (Exception e) {
            return "Unspawned";
        }
    }

    public NpcTemplate getTemplate() {
        return _npcTemplate;
    }

    public boolean isGroupMember(ControllableMob mobInst) {
        for (ControllableMob groupMember : getMobs()) {
            if (groupMember == null) {
                continue;
            }

            if (groupMember.getObjectId() == mobInst.getObjectId()) {
                return true;
            }
        }

        return false;
    }

    public void spawnGroup(int x, int y, int z) {
        if (getMobs().size() > 0) {
            return;
        }

        try {
            for (int i = 0; i < _maxMobCount; i++) {
                final GroupSpawn spawn = new GroupSpawn(_npcTemplate);

                final int signX = Rnd.nextBoolean() ? -1 : 1;
                final int signY = Rnd.nextBoolean() ? -1 : 1;
                final int randX = Rnd.get(MobGroupTable.RANDOM_RANGE);
                final int randY = Rnd.get(MobGroupTable.RANDOM_RANGE);

                spawn.setXYZ(x + (signX * randX), y + (signY * randY), z);
                spawn.stopRespawn();

                SpawnTable.getInstance().addNewSpawn(spawn, false);
                getMobs().add((ControllableMob) spawn.doGroupSpawn());
            }
        } catch (ClassNotFoundException e) {
        } catch (NoSuchMethodException e2) {
        }
    }

    public void spawnGroup(Player activeChar) {
        spawnGroup(activeChar.getX(), activeChar.getY(), activeChar.getZ());
    }

    public void teleportGroup(Player player) {
        removeDead();

        for (ControllableMob mobInst : getMobs()) {
            if (mobInst == null) {
                continue;
            }

            if (!mobInst.isDead()) {
                final int x = player.getX() + Rnd.get(50);
                final int y = player.getY() + Rnd.get(50);

                mobInst.teleToLocation(new Location(x, y, player.getZ()), true);
                ((ControllableMobAI) mobInst.getAI()).follow(player);
            }
        }
    }

    public ControllableMob getRandomMob() {
        removeDead();

        if (getMobs().size() == 0) {
            return null;
        }

        int choice = Rnd.get(getMobs().size());
        for (ControllableMob mob : getMobs()) {
            if (--choice == 0) {
                return mob;
            }
        }
        return null;
    }

    public void unspawnGroup() {
        removeDead();

        if (getMobs().size() == 0) {
            return;
        }

        for (ControllableMob mobInst : getMobs()) {
            if (mobInst == null) {
                continue;
            }

            if (!mobInst.isDead()) {
                mobInst.deleteMe();
            }

            SpawnTable.getInstance().deleteSpawn(mobInst.getSpawn(), false);
        }

        getMobs().clear();
    }

    public void killGroup(Player activeChar) {
        removeDead();

        for (ControllableMob mobInst : getMobs()) {
            if (mobInst == null) {
                continue;
            }

            if (!mobInst.isDead()) {
                mobInst.reduceCurrentHp(mobInst.getMaxHp() + 1, activeChar, null, DamageInfo.DamageType.OTHER);
            }

            SpawnTable.getInstance().deleteSpawn(mobInst.getSpawn(), false);
        }

        getMobs().clear();
    }

    public void setAttackRandom() {
        removeDead();

        for (ControllableMob mobInst : getMobs()) {
            if (mobInst == null) {
                continue;
            }

            final ControllableMobAI ai = (ControllableMobAI) mobInst.getAI();
            ai.setAlternateAI(ControllableMobAI.AI_NORMAL);
            ai.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
        }
    }

    public void setAttackTarget(Creature target) {
        removeDead();

        for (ControllableMob mobInst : getMobs()) {
            if (mobInst == null) {
                continue;
            }

            ((ControllableMobAI) mobInst.getAI()).forceAttack(target);
        }
    }

    public void setIdleMode() {
        removeDead();

        for (ControllableMob mobInst : getMobs()) {
            if (mobInst == null) {
                continue;
            }

            ((ControllableMobAI) mobInst.getAI()).stop();
        }
    }

    public void returnGroup(Creature activeChar) {
        setIdleMode();

        for (ControllableMob mobInst : getMobs()) {
            if (mobInst == null) {
                continue;
            }

            final int signX = Rnd.nextBoolean() ? -1 : 1;
            final int signY = Rnd.nextBoolean() ? -1 : 1;
            final int randX = Rnd.get(MobGroupTable.RANDOM_RANGE);
            final int randY = Rnd.get(MobGroupTable.RANDOM_RANGE);

            final ControllableMobAI ai = (ControllableMobAI) mobInst.getAI();
            ai.move(activeChar.getX() + (signX * randX), activeChar.getY() + (signY * randY), activeChar.getZ());
        }
    }

    public void setFollowMode(Creature character) {
        removeDead();

        for (ControllableMob mobInst : getMobs()) {
            if (mobInst == null) {
                continue;
            }

            ((ControllableMobAI) mobInst.getAI()).follow(character);
        }
    }

    public void setCastMode() {
        removeDead();

        for (ControllableMob mobInst : getMobs()) {
            if (mobInst == null) {
                continue;
            }

            ((ControllableMobAI) mobInst.getAI()).setAlternateAI(ControllableMobAI.AI_CAST);
        }
    }

    public void setNoMoveMode(boolean enabled) {
        removeDead();

        for (ControllableMob mobInst : getMobs()) {
            if (mobInst == null) {
                continue;
            }

            ((ControllableMobAI) mobInst.getAI()).setNotMoving(enabled);
        }
    }

    protected void removeDead() {
        getMobs().removeIf(Creature::isDead);
    }

    public void setInvul(boolean invulState) {
        removeDead();

        for (ControllableMob mobInst : getMobs()) {
            if (mobInst != null) {
                mobInst.setInvul(invulState);
            }
        }
    }

    public void setAttackGroup(MobGroup otherGrp) {
        removeDead();

        for (ControllableMob mobInst : getMobs()) {
            if (mobInst == null) {
                continue;
            }

            final ControllableMobAI ai = (ControllableMobAI) mobInst.getAI();
            ai.forceAttackGroup(otherGrp);
            ai.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
        }
    }
}