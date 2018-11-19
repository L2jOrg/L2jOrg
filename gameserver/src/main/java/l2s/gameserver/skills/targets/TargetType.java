package l2s.gameserver.skills.targets;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import l2s.gameserver.geodata.GeoEngine;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.World;
import l2s.gameserver.model.Zone;
import l2s.gameserver.model.actor.instances.player.Mount;
import l2s.gameserver.model.base.MountType;
import l2s.gameserver.model.entity.events.Event;
import l2s.gameserver.model.entity.events.impl.SiegeEvent;
import l2s.gameserver.model.instances.ChestInstance;
import l2s.gameserver.model.instances.DoorInstance;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.FlyToLocationPacket;
import l2s.gameserver.skills.AbnormalType;
import l2s.gameserver.utils.Location;

/**
 * Target type enumerated.
 * @author Zoey76
 */
public enum TargetType
{
    /** Advance Head Quarters (Outposts). */
    ADVANCE_BASE
    {
        @Override
        public GameObject getTarget(Creature caster, GameObject selectedTarget, Skill skill, boolean forceUse, boolean dontMove, boolean sendMessage)
        {
            GameObject target = caster.getTarget();
            if(target != null && target.isCreature())
            {
                Creature creature = (Creature) target;
                if(creature.getNpcId() == 36590 && !creature.isDead())
                    return creature;
            }
            if(sendMessage)
                caster.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);

            return null;
        }
    },
    /** Enemies in high terrain or protected by castle walls and doors. */
    ARTILLERY
    {
        @Override
        public GameObject getTarget(Creature caster, GameObject selectedTarget, Skill skill, boolean forceUse, boolean dontMove, boolean sendMessage)
        {
            GameObject target = caster.getTarget();
            if(target != null && target.isDoor())
            {
                DoorInstance targetDoor = (DoorInstance) target;
                if(!targetDoor.isDead() && targetDoor.isAutoAttackable(caster))
                    return targetDoor;
            }
            if(sendMessage)
                caster.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);

            return null;
        }
    },
    /** Doors or treasure chests. */
    DOOR_TREASURE
    {
        @Override
        public GameObject getTarget(Creature caster, GameObject selectedTarget, Skill skill, boolean forceUse, boolean dontMove, boolean sendMessage)
        {
            GameObject target = caster.getTarget();
            if(target != null && (target.isDoor() || target instanceof ChestInstance))
                return target;

            if(sendMessage)
                caster.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);

            return null;
        }
    },
    /** Any enemies (included allies). */
    ENEMY
    {
        @Override
        public GameObject getTarget(Creature caster, GameObject selectedTarget, Skill skill, boolean forceUse, boolean dontMove, boolean sendMessage)
        {
            if(selectedTarget == null)
                return null;

            if(!selectedTarget.isCreature())
                return null;

            Creature target = (Creature) selectedTarget;
            if(caster == target)
            {
                if(sendMessage)
                    caster.sendPacket(SystemMsg.INVALID_TARGET);
                return null;
            }

            if(target.isDead())
            {
                if(sendMessage)
                    caster.sendPacket(SystemMsg.INVALID_TARGET);
                return null;
            }

            for(Event e : caster.getEvents())
            {
                SystemMsg msg = e.checkForAttack(target, caster, skill, forceUse);
                if(msg != null)
                {
                    if(sendMessage)
                        caster.sendPacket(msg);
                    return null;
                }
            }

            for(Event e : caster.getEvents())
            {
                if(e.canAttack(target, caster, skill, forceUse, false))
                    return target;
            }

            if(!target.isAutoAttackable(caster) && (target.isDoor() || !forceUse))
            {
                if(sendMessage)
                    caster.sendPacket(SystemMsg.INVALID_TARGET);
                return null;
            }

            if(dontMove && skill.getCastRange() > 0 && !caster.isInRange(target, skill.getCastRange()))
            {
                if(sendMessage)
                    caster.sendPacket(SystemMsg.THE_DISTANCE_IS_TOO_FAR_AND_SO_THE_CASTING_HAS_BEEN_STOPPED);
                return null;
            }

            if(!GeoEngine.canSeeTarget(caster, target, caster.isFlying()))
            {
                if(sendMessage)
                    caster.sendPacket(SystemMsg.CANNOT_SEE_TARGET);
                return null;
            }

            if(caster.isInPeaceZone() || target.isInPeaceZone())
            {
                Player player = caster.getPlayer();
                if(player == null || !player.getPlayerAccess().PeaceAttack)
                {
                    if(sendMessage)
                        caster.sendPacket(SystemMsg.A_MALICIOUS_SKILL_CANNOT_BE_USED_IN_A_PEACE_ZONE);
                    return null;
                }
            }

            return target;
        }
    },
    /** Friendly. */
    ENEMY_NOT
    {
        @Override
        public GameObject getTarget(Creature caster, GameObject selectedTarget, Skill skill, boolean forceUse, boolean dontMove, boolean sendMessage)
        {
            if(selectedTarget == null)
                return null;

            if(!selectedTarget.isCreature())
                return null;

            Creature target = (Creature) selectedTarget;
            if(caster == target)
                return target;

            if(!target.isAutoAttackable(caster))
            {

                if(dontMove && skill.getCastRange() > 0 && !caster.isInRange(target, skill.getCastRange()))
                {
                    if(sendMessage)
                        caster.sendPacket(SystemMsg.THE_DISTANCE_IS_TOO_FAR_AND_SO_THE_CASTING_HAS_BEEN_STOPPED);
                    return null;
                }

                if(skill.getFlyType() == FlyToLocationPacket.FlyType.CHARGE && !GeoEngine.canMoveToCoord(caster.getX(), caster.getY(), caster.getZ(), target.getX(), target.getY(), target.getZ(), caster.getGeoIndex()))
                {
                    if(sendMessage)
                        caster.sendPacket(SystemMsg.THE_TARGET_IS_LOCATED_WHERE_YOU_CANNOT_CHARGE);
                    return null;
                }

                if(!GeoEngine.canSeeTarget(caster, target, caster.isFlying()))
                {
                    if(sendMessage)
                        caster.sendPacket(SystemMsg.CANNOT_SEE_TARGET);
                    return null;
                }

                return target;
            }

            if(sendMessage)
                caster.sendPacket(SystemMsg.INVALID_TARGET);

            return null;
        }
    },
    /** Only enemies (not included allies). */
    ENEMY_ONLY
    {
        @Override
        public GameObject getTarget(Creature caster, GameObject selectedTarget, Skill skill, boolean forceUse, boolean dontMove, boolean sendMessage)
        {
            return ENEMY.getTarget(caster, selectedTarget, skill, false, dontMove, sendMessage);
        }
    },
    /** Fortress's Flagpole. */
    FORTRESS_FLAGPOLE
    {
        @Override
        public GameObject getTarget(Creature caster, GameObject selectedTarget, Skill skill, boolean forceUse, boolean dontMove, boolean sendMessage)
        {
            GameObject target = caster.getTarget();
            if(target != null && target.isCreature())
            {
                Creature creature = (Creature) target;
                switch(creature.getNpcId())
                {
                    case 35002:
                    case 35657:
                    case 35688:
                    case 35726:
                    case 35757:
                    case 35795:
                    case 35826:
                    case 35857:
                    case 35895:
                    case 35926:
                    case 35964:
                    case 36033:
                    case 36071:
                    case 36109:
                    case 36140:
                    case 36171:
                    case 36209:
                    case 36247:
                    case 36285:
                    case 36316:
                    case 36354:
                        return creature;
                }
            }

            if(sendMessage)
                caster.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);

            return null;
        }
    },
    /** Ground. */
    GROUND
    {
        @Override
        public GameObject getTarget(Creature caster, GameObject selectedTarget, Skill skill, boolean forceUse, boolean dontMove, boolean sendMessage)
        {
            if(caster.isPlayer())
            {
                Location skillLoc = caster.getPlayer().getGroundSkillLoc();
                if(skillLoc != null)
                {
                    if(dontMove && !caster.isInRange(skillLoc, (int) (skill.getCastRange() + caster.getCollisionRadius())))
                        return null;

                    if(!GeoEngine.canSeeCoord(caster, skillLoc.getX(), skillLoc.getY(), skillLoc.getZ(), caster.isFlying()))
                        return null;

                    if(skill.isOffensive())
                    {
                        Set<Zone> zones = new HashSet<Zone>();
                        World.getZones(zones, skillLoc, caster.getReflection());
                        Iterator<Zone> iterator = zones.iterator();
                        if(iterator.hasNext())
                        {
                            Zone zone = iterator.next();
                            if(sendMessage)
                                caster.sendPacket(SystemMsg.A_MALICIOUS_SKILL_CANNOT_BE_USED_IN_A_PEACE_ZONE);
                            return null;
                        }
                    }

                    return caster;
                }
            }

            return null;
        }
    },
    /** Holy Artifacts from sieges. */
    HOLYTHING
    {
        @Override
        public GameObject getTarget(Creature caster, GameObject selectedTarget, Skill skill, boolean forceUse, boolean dontMove, boolean sendMessage)
        {
            GameObject target = caster.getTarget();
            if(target != null && target.isArtefact())
                return target;

            if(sendMessage)
                caster.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);

            return null;
        }
    },
    /** Items. */
    ITEM
    {
        @Override
        public GameObject getTarget(Creature caster, GameObject selectedTarget, Skill skill, boolean forceUse, boolean dontMove, boolean sendMessage)
        {
            return null;
        }
    },
    /** Nothing. */
    NONE
    {
        @Override
        public GameObject getTarget(Creature caster, GameObject selectedTarget, Skill skill, boolean forceUse, boolean dontMove, boolean sendMessage)
        {
            return caster;
        }
    },
    /** NPC corpses. */
    NPC_BODY
    {
        @Override
        public GameObject getTarget(Creature caster, GameObject selectedTarget, Skill skill, boolean forceUse, boolean dontMove, boolean sendMessage)
        {
            if(selectedTarget == null)
                return null;

            if(!selectedTarget.isCreature())
                return null;

            if(!selectedTarget.isNpc())
            {
                if(sendMessage)
                    caster.sendPacket(SystemMsg.INVALID_TARGET);
                return null;
            }

            NpcInstance target = (NpcInstance) selectedTarget;
            if(target.isDead())
            {
                if(dontMove && skill.getCastRange() > 0 && !caster.isInRange(target, skill.getCastRange()))
                {
                    if(sendMessage)
                        caster.sendPacket(SystemMsg.THE_DISTANCE_IS_TOO_FAR_AND_SO_THE_CASTING_HAS_BEEN_STOPPED);
                    return null;
                }

                if(!GeoEngine.canSeeTarget(caster, target, caster.isFlying()))
                {
                    if(sendMessage)
                        caster.sendPacket(SystemMsg.CANNOT_SEE_TARGET);
                    return null;
                }

                return target;
            }
            if(sendMessage)
                caster.sendPacket(SystemMsg.INVALID_TARGET);

            return null;
        }
    },
    /** Others, except caster. */
    OTHERS
    {
        @Override
        public GameObject getTarget(Creature caster, GameObject selectedTarget, Skill skill, boolean forceUse, boolean dontMove, boolean sendMessage)
        {
            return null;
        }
    },
    /** Player corpses. */
    PC_BODY
    {
        @Override
        public GameObject getTarget(Creature caster, GameObject selectedTarget, Skill skill, boolean forceUse, boolean dontMove, boolean sendMessage)
        {
            if(selectedTarget == null)
                return null;

            if(!selectedTarget.isCreature())
                return null;

            if(!selectedTarget.isPlayer())
            {
                if(sendMessage)
                    caster.sendPacket(SystemMsg.INVALID_TARGET);
                return null;
            }

            Player target = (Player) selectedTarget;
            if(target.isDead())
            {
                if(skill.getSkillType() == Skill.SkillType.RESURRECT)
                {
                    if(caster.getAbnormalList().contains(AbnormalType.clan_resurrection_block) || target.getAbnormalList().contains(AbnormalType.clan_resurrection_block))
                    {
                        if(sendMessage)
                        {
                            caster.sendPacket(SystemMsg.REJECT_RESURRECTION);
                            target.sendPacket(SystemMsg.REJECT_RESURRECTION);
                        }
                        return null;
                    }

                    if(target.isPlayer() && target.isInSiegeZone())
                    {
                        SiegeEvent<?, ?> siegeEvent = target.getEvent(SiegeEvent.class);
                        if(siegeEvent == null)
                        {
                            if(sendMessage)
                                caster.sendPacket(SystemMsg.IT_IS_NOT_POSSIBLE_TO_RESURRECT_IN_BATTLEFIELDS_WHERE_A_SIEGE_WAR_IS_TAKING_PLACE);
                            return null;
                        }
                    }
                }

                if(dontMove && skill.getCastRange() > 0 && !caster.isInRange(target, skill.getCastRange()))
                {
                    if(sendMessage)
                        caster.sendPacket(SystemMsg.THE_DISTANCE_IS_TOO_FAR_AND_SO_THE_CASTING_HAS_BEEN_STOPPED);
                    return null;
                }

                if(!GeoEngine.canSeeTarget(caster, target, caster.isFlying()))
                {
                    if(sendMessage)
                        caster.sendPacket(SystemMsg.CANNOT_SEE_TARGET);
                    return null;
                }
                return target;
            }

            if(sendMessage)
                caster.sendPacket(SystemMsg.INVALID_TARGET);

            return null;
        }
    },
    /** Self. */
    SELF
    {
        @Override
        public GameObject getTarget(Creature caster, GameObject selectedTarget, Skill skill, boolean forceUse, boolean dontMove, boolean sendMessage)
        {
            if(caster.isInPeaceZone() && skill.isOffensive())
            {
                if(sendMessage)
                    caster.sendPacket(SystemMsg.A_MALICIOUS_SKILL_CANNOT_BE_USED_IN_A_PEACE_ZONE);
                return null;
            }
            return caster;
        }
    },
    /** Servitor or pet. */
    SUMMON
    {
        @Override
        public GameObject getTarget(Creature caster, GameObject selectedTarget, Skill skill, boolean forceUse, boolean dontMove, boolean sendMessage)
        {
            if(caster.isPlayer())
            {
                Player player = caster.getPlayer();
                if(player.hasSummon())
                    return player.getSummon();

                return player.getPet();
            }

            return null;
        }
    },
    /** Anything targetable. */
    TARGET
    {
        @Override
        public GameObject getTarget(Creature caster, GameObject selectedTarget, Skill skill, boolean forceUse, boolean dontMove, boolean sendMessage)
        {
            if(selectedTarget == null)
                return null;

            if(!selectedTarget.isCreature())
            {
                if(sendMessage)
                    caster.sendPacket(SystemMsg.INVALID_TARGET);
                return null;
            }

            Creature target = (Creature) selectedTarget;
            if(caster == target)
                return target;

            if(dontMove && skill.getCastRange() > 0 && !caster.isInRange(target, skill.getCastRange()))
            {
                if(sendMessage)
                    caster.sendPacket(SystemMsg.THE_DISTANCE_IS_TOO_FAR_AND_SO_THE_CASTING_HAS_BEEN_STOPPED);
                return null;
            }

            if(skill.getFlyType() == FlyToLocationPacket.FlyType.CHARGE && !GeoEngine.canMoveToCoord(caster.getX(), caster.getY(), caster.getZ(), target.getX(), target.getY(), target.getZ(), caster.getGeoIndex()))
            {
                if(sendMessage)
                    caster.sendPacket(SystemMsg.THE_TARGET_IS_LOCATED_WHERE_YOU_CANNOT_CHARGE);
                return null;
            }

            if(!GeoEngine.canSeeTarget(caster, target, caster.isFlying()))
            {
                if(sendMessage)
                    caster.sendPacket(SystemMsg.CANNOT_SEE_TARGET);
                return null;
            }

            return target;
        }
    },
    /** Wyverns. */
    WYVERN_TARGET
    {
        @Override
        public GameObject getTarget(Creature caster, GameObject selectedTarget, Skill skill, boolean forceUse, boolean dontMove, boolean sendMessage)
        {
            if(selectedTarget == null)
                return null;

            if(!selectedTarget.isCreature())
                return null;

            if(!selectedTarget.isPlayer())
            {
                if(sendMessage)
                    caster.sendPacket(SystemMsg.INVALID_TARGET);
                return null;
            }

            Player target = (Player) selectedTarget;
            Mount mount = target.getMount();
            if(mount == null || !mount.isOfType(MountType.WYVERN))
            {
                if(sendMessage)
                    caster.sendPacket(SystemMsg.INVALID_TARGET);
                return null;
            }
            return target;
        }
    },
    /** Me or my party (if any). Seen in aura skills. */
    MY_PARTY
    {
        @Override
        public GameObject getTarget(Creature caster, GameObject selectedTarget, Skill skill, boolean forceUse, boolean dontMove, boolean sendMessage)
        {
            if(selectedTarget == null)
                return null;

            if(selectedTarget == caster)
                return null;

            if(!caster.isPlayer() || !selectedTarget.isPlayer())
                return null;

            Player target = selectedTarget.getPlayer();
            if(caster.getPlayer().isInSameParty(target))
                return target;

            return null;
        }
    };

    public abstract GameObject getTarget(Creature caster, GameObject selectedTarget, Skill skill, boolean forceUse, boolean dontMove, boolean sendMessage);
}