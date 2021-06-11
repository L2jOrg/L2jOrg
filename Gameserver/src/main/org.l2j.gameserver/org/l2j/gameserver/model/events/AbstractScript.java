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
package org.l2j.gameserver.model.events;

import io.github.joealisson.primitive.*;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.data.xml.DoorDataManager;
import org.l2j.gameserver.data.xml.MagicLampData;
import org.l2j.gameserver.data.xml.impl.NpcData;
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.engine.scripting.ManagedScript;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.instancemanager.InstanceManager;
import org.l2j.gameserver.instancemanager.PcCafePointsManager;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.Spawn;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.instance.Door;
import org.l2j.gameserver.model.actor.instance.Monster;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.instance.Trap;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.events.annotations.*;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.impl.character.OnCreatureDeath;
import org.l2j.gameserver.model.events.impl.character.OnCreatureZoneEnter;
import org.l2j.gameserver.model.events.impl.character.OnCreatureZoneExit;
import org.l2j.gameserver.model.events.impl.character.npc.*;
import org.l2j.gameserver.model.events.impl.character.player.*;
import org.l2j.gameserver.model.events.impl.instance.OnInstanceCreated;
import org.l2j.gameserver.model.events.impl.instance.OnInstanceDestroy;
import org.l2j.gameserver.model.events.impl.instance.OnInstanceEnter;
import org.l2j.gameserver.model.events.impl.instance.OnInstanceLeave;
import org.l2j.gameserver.model.events.impl.item.OnItemBypassEvent;
import org.l2j.gameserver.model.events.impl.item.OnItemTalk;
import org.l2j.gameserver.model.events.listeners.*;
import org.l2j.gameserver.model.events.returns.AbstractEventReturn;
import org.l2j.gameserver.model.events.returns.TerminateReturn;
import org.l2j.gameserver.model.events.timers.IEventTimerCancel;
import org.l2j.gameserver.model.events.timers.IEventTimerEvent;
import org.l2j.gameserver.model.events.timers.TimerHolder;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.instancezone.InstanceTemplate;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.interfaces.IPositionable;
import org.l2j.gameserver.model.item.CommonItem;
import org.l2j.gameserver.engine.item.EtcItem;
import org.l2j.gameserver.engine.item.ItemTemplate;
import org.l2j.gameserver.model.item.container.PlayerInventory;
import org.l2j.gameserver.model.spawns.SpawnGroup;
import org.l2j.gameserver.model.spawns.SpawnTemplate;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.*;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.util.MinionList;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.Objects.isNull;
import static org.l2j.gameserver.util.GameUtils.isAttackable;
import static org.l2j.gameserver.util.GameUtils.isCreature;

/**
 * @author UnAfraid
 */
public abstract class AbstractScript extends ManagedScript implements IEventTimerEvent<String>, IEventTimerCancel<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractScript.class);

    private final Map<ListenerRegisterType, IntSet> _registeredIds = new EnumMap<>(ListenerRegisterType.class);
    private final Queue<AbstractEventListener> _listeners = new PriorityBlockingQueue<>();
    private volatile TimerExecutor<String> _timerExecutor;

    protected AbstractScript() {
        initializeAnnotationListeners();
    }

    @Override
    public final void onTimerEvent(TimerHolder<String> holder) {
        onTimerEvent(holder.getEvent(), holder.getParams(), holder.getNpc(), holder.getPlayer());
    }

    @Override
    public final void onTimerCancel(TimerHolder<String> holder) {
        onTimerCancel(holder.getEvent(), holder.getParams(), holder.getNpc(), holder.getPlayer());
    }

    public void onTimerEvent(String event, StatsSet params, Npc npc, Player player) {
        LOGGER.warn("[" + getClass().getSimpleName() + "]: Timer event arrived at non overriden onTimerEvent method event: " + event + " npc: " + npc + " player: " + player);
    }

    public void onTimerCancel(String event, StatsSet params, Npc npc, Player player) {
    }

    /**
     * @return the {@link TimerExecutor} object that manages timers
     */
    public TimerExecutor<String> getTimers() {
        if (_timerExecutor == null) {
            synchronized (this) {
                if (_timerExecutor == null) {
                    _timerExecutor = new TimerExecutor<>(this, this);
                }
            }
        }
        return _timerExecutor;
    }

    private void initializeAnnotationListeners() {
        final IntSet ids = new HashIntSet();
        for (Method method : getClass().getMethods()) {
            if (method.isAnnotationPresent(RegisterEvent.class) && method.isAnnotationPresent(RegisterType.class)) {
                final RegisterEvent listener = method.getAnnotation(RegisterEvent.class);
                final RegisterType regType = method.getAnnotation(RegisterType.class);

                final ListenerRegisterType type = regType.value();
                final EventType eventType = listener.value();
                if (method.getParameterCount() != 1) {
                    LOGGER.warn("Non properly defined annotation listener on method: {} expected parameter count is 1 but found: {}", method.getName(), method.getParameterCount());
                    continue;
                } else if (!eventType.isEventClass(method.getParameterTypes()[0])) {
                    LOGGER.warn("Non properly defined annotation listener on method: {} expected parameter to be type of: {}  but found: {}", method.getName(), eventType.getEventClass().getSimpleName(), method.getParameterTypes()[0].getSimpleName());
                    continue;
                } else if (!eventType.isReturnClass(method.getReturnType())) {
                    LOGGER.warn("Non properly defined annotation listener on method: {} expected return type to be one of: {} but found: {}", method.getName(), Arrays.toString(eventType.getReturnClasses()), method.getReturnType().getSimpleName());
                    continue;
                }

                int priority = 0;
                ids.clear();

                for (Annotation annotation : method.getAnnotations()) {
                    if (annotation instanceof Id npc) {
                        for (int id : npc.value()) {
                            ids.add(id);
                        }
                    } else if (annotation instanceof Ids npcs) {
                        for (Id npc : npcs.value()) {
                            for (int id : npc.value()) {
                                ids.add(id);
                            }
                        }
                    } else if (annotation instanceof Range range) {
                        if (range.from() > range.to()) {
                            LOGGER.warn("Wrong Range: from is higher then to!");
                            continue;
                        }

                        for (int id = range.from(); id <= range.to(); id++) {
                            ids.add(id);
                        }
                    } else if (annotation instanceof Ranges ranges) {
                        for (Range range : ranges.value()) {
                            if (range.from() > range.to()) {
                                LOGGER.warn("Wrong Ranges: from is higher then to!");
                                continue;
                            }

                            for (int id = range.from(); id <= range.to(); id++) {
                                ids.add(id);
                            }
                        }
                    } else if (annotation instanceof NpcLevelRange range) {
                        if (range.from() > range.to()) {
                            LOGGER.warn("Wrong NpcLevelRange: from is higher then to!");
                            continue;
                        } else if (type != ListenerRegisterType.NPC) {
                            LOGGER.warn("ListenerRegisterType {} for NpcLevelRange, NPC is expected!", type);
                            continue;
                        }

                        for (int level = range.from(); level <= range.to(); level++) {
                            final List<NpcTemplate> templates = NpcData.getInstance().getAllOfLevel(level);
                            templates.forEach(template -> ids.add(template.getId()));
                        }

                    } else if (annotation instanceof NpcLevelRanges ranges) {
                        for (NpcLevelRange range : ranges.value()) {
                            if (range.from() > range.to()) {
                                LOGGER.warn(": Wrong " + annotation.getClass().getSimpleName() + " from is higher then to!");
                                continue;
                            } else if (type != ListenerRegisterType.NPC) {
                                LOGGER.warn(": ListenerRegisterType " + type + " for " + annotation.getClass().getSimpleName() + " NPC is expected!");
                                continue;
                            }

                            for (int level = range.from(); level <= range.to(); level++) {
                                final List<NpcTemplate> templates = NpcData.getInstance().getAllOfLevel(level);
                                templates.forEach(template -> ids.add(template.getId()));
                            }
                        }
                    } else if (annotation instanceof Priority p) {
                        priority = p.value();
                    }
                }

                if (!ids.isEmpty()) {
                    _registeredIds.computeIfAbsent(type, k -> CHashIntMap.newKeySet()).addAll(ids);
                }

                registerAnnotation(method, eventType, type, priority, ids);
            }
        }
    }

    /**
     * Unloads all listeners registered by this class.
     */
    @Override
    public boolean unload() {
        _listeners.forEach(AbstractEventListener::unregisterMe);
        _listeners.clear();
        if (_timerExecutor != null) {
            _timerExecutor.cancelAllTimers();
        }
        return true;
    }

    /**
     * Provides callback operation when Attackable dies from a player.
     */
    protected final List<AbstractEventListener> setAttackableKillId(Consumer<OnAttackableKill> callback, int... npcIds) {
        for (int id : npcIds) {
            if (NpcData.getInstance().getTemplate(id) == null) {
                LOGGER.error(super.getClass().getSimpleName() + ": Found addKillId for non existing NPC: " + id + "!");
            }
        }
        return registerConsumer(callback, EventType.ON_ATTACKABLE_KILL, ListenerRegisterType.NPC, npcIds);
    }

    /**
     * Provides callback operation when Attackable dies from a player.
     */
    protected final List<AbstractEventListener> setAttackableKillId(Consumer<OnAttackableKill> callback, IntCollection npcIds) {
        checkNpcIds(npcIds, EventType.ON_ATTACKABLE_KILL);
        return registerConsumer(callback, EventType.ON_ATTACKABLE_KILL, ListenerRegisterType.NPC, npcIds);
    }

    private void checkNpcIds(IntCollection npcIds, EventType type) {
        npcIds.forEach(id -> {
            if (isNull(NpcData.getInstance().getTemplate(id))) {
                LOGGER.error("Found registering event type {} for non existing NPC: {}!", type, id);
            }
        });
    }

    /**
     * Provides instant callback operation when Attackable dies from a player.
     */
    protected final List<AbstractEventListener> setCreatureKillId(Consumer<OnCreatureDeath> callback, int... npcIds) {
        return registerConsumer(callback, EventType.ON_CREATURE_DEATH, ListenerRegisterType.NPC, npcIds);
    }

    /**
     * Provides instant callback operation when {@link Player} talk to {@link Npc} for first time.
     */
    protected final List<AbstractEventListener> setNpcFirstTalkId(Consumer<OnNpcFirstTalk> callback, int... npcIds) {
        for (int id : npcIds) {
            if (NpcData.getInstance().getTemplate(id) == null) {
                LOGGER.error(super.getClass().getSimpleName() + ": Found addFirstTalkId for non existing NPC: " + id + "!");
            }
        }
        return registerConsumer(callback, EventType.ON_NPC_FIRST_TALK, ListenerRegisterType.NPC, npcIds);
    }

    /**
     * Provides instant callback operation when {@link Player} talk to {@link Npc} for first time.
     */
    protected final List<AbstractEventListener> setNpcFirstTalkId(Consumer<OnNpcFirstTalk> callback, IntCollection npcIds) {
        checkNpcIds(npcIds, EventType.ON_NPC_FIRST_TALK);
        return registerConsumer(callback, EventType.ON_NPC_FIRST_TALK, ListenerRegisterType.NPC, npcIds);
    }

    /**
     * Provides instant callback operation when {@link Player} talk to {@link Npc}.
     *
     */
    protected final List<AbstractEventListener> setNpcTalkId(IntCollection npcIds) {
        checkNpcIds(npcIds, EventType.ON_NPC_TALK);
        return registerDummy(EventType.ON_NPC_TALK, ListenerRegisterType.NPC, npcIds);
    }

    /**
     * Provides instant callback operation when {@link Player} talk to {@link Npc}.
     */
    protected final List<AbstractEventListener> setNpcTalkId(int... npcIds) {
        for (int id : npcIds) {
            if (NpcData.getInstance().getTemplate(id) == null) {
                LOGGER.error("Found addTalkId for non existing NPC: {}!", id);
            }
        }
        return registerDummy(EventType.ON_NPC_TALK, ListenerRegisterType.NPC, npcIds);
    }

    /**
     * Provides instant callback operation when teleport {@link Npc}.
     */
    protected final List<AbstractEventListener> setNpcTeleportId(Consumer<OnNpcTeleport> callback, IntCollection npcIds) {
        return registerConsumer(callback, EventType.ON_NPC_TELEPORT, ListenerRegisterType.NPC, npcIds);
    }

    /**
     * Provides instant callback operation when teleport {@link Npc}.
     */
    protected final List<AbstractEventListener> setNpcTeleportId(Consumer<OnNpcTeleport> callback, int... npcIds) {
        return registerConsumer(callback, EventType.ON_NPC_TELEPORT, ListenerRegisterType.NPC, npcIds);
    }

    /**
     * Provides instant callback operation when {@link Player} talk to {@link Npc} and must receive quest state.
     */
    protected final List<AbstractEventListener> setNpcQuestStartId(int... npcIds) {
        for (int id : npcIds) {
            if (NpcData.getInstance().getTemplate(id) == null) {
                LOGGER.error(super.getClass().getSimpleName() + ": Found addStartNpc for non existing NPC: " + id + "!");
            }
        }
        return registerDummy(EventType.ON_NPC_QUEST_START, ListenerRegisterType.NPC, npcIds);
    }

    /**
     * Provides instant callback operation when {@link Player} talk to {@link Npc} and must receive quest state.
     */
    protected final List<AbstractEventListener> setNpcQuestStartId(IntCollection npcIds) {
        checkNpcIds(npcIds, EventType.ON_NPC_QUEST_START);
        return registerDummy(EventType.ON_NPC_QUEST_START, ListenerRegisterType.NPC, npcIds);
    }

    /**
     * Provides instant callback operation when Npc sees skill from a player.
     */
    protected final List<AbstractEventListener> setNpcSkillSeeId(Consumer<OnNpcSkillSee> callback, int... npcIds) {
        for (int id : npcIds) {
            if (NpcData.getInstance().getTemplate(id) == null) {
                LOGGER.error(super.getClass().getSimpleName() + ": Found addSkillSeeId for non existing NPC: " + id + "!");
            }
        }
        return registerConsumer(callback, EventType.ON_NPC_SKILL_SEE, ListenerRegisterType.NPC, npcIds);
    }

    /**
     * Provides instant callback operation when Npc sees skill from a player.
     */
    protected final List<AbstractEventListener> setNpcSkillSeeId(Consumer<OnNpcSkillSee> callback, IntCollection npcIds) {
        checkNpcIds(npcIds, EventType.ON_NPC_SKILL_SEE);
        return registerConsumer(callback, EventType.ON_NPC_SKILL_SEE, ListenerRegisterType.NPC, npcIds);
    }

    /**
     * Provides instant callback operation when Npc casts skill on a player.
     */
    protected final List<AbstractEventListener> setNpcSkillFinishedId(Consumer<OnNpcSkillFinished> callback, int... npcIds) {
        for (int id : npcIds) {
            if (NpcData.getInstance().getTemplate(id) == null) {
                LOGGER.error(super.getClass().getSimpleName() + ": Found addSpellFinishedId for non existing NPC: " + id + "!");
            }
        }
        return registerConsumer(callback, EventType.ON_NPC_SKILL_FINISHED, ListenerRegisterType.NPC, npcIds);
    }

    /**
     * Provides instant callback operation when Npc casts skill on a player.
     */
    protected final List<AbstractEventListener> setNpcSkillFinishedId(Consumer<OnNpcSkillFinished> callback, IntCollection npcIds) {
        checkNpcIds(npcIds, EventType.ON_NPC_SKILL_FINISHED);
        return registerConsumer(callback, EventType.ON_NPC_SKILL_FINISHED, ListenerRegisterType.NPC, npcIds);
    }

    /**
     * Provides instant callback operation when Npc is spawned.
     */
    protected final List<AbstractEventListener> setNpcSpawnId(Consumer<OnNpcSpawn> callback, int... npcIds) {
        for (int id : npcIds) {
            if (NpcData.getInstance().getTemplate(id) == null) {
                LOGGER.error("Found addSpawnId for non existing NPC: {}!", id);
            }
        }
        return registerConsumer(callback, EventType.ON_NPC_SPAWN, ListenerRegisterType.NPC, npcIds);
    }

    /**
     * Provides instant callback operation when Npc is spawned.
     */
    protected final List<AbstractEventListener> setNpcSpawnId(Consumer<OnNpcSpawn> callback, IntCollection npcIds) {
        checkNpcIds(npcIds, EventType.ON_NPC_SPAWN);
        return registerConsumer(callback, EventType.ON_NPC_SPAWN, ListenerRegisterType.NPC, npcIds);
    }

    /**
     * Provides instant callback operation when Npc is despawned.
     */
    protected final List<AbstractEventListener> setNpcDespawnId(Consumer<OnNpcDespawn> callback, int... npcIds) {
        return registerConsumer(callback, EventType.ON_NPC_DESPAWN, ListenerRegisterType.NPC, npcIds);
    }

    /**
     * Provides instant callback operation when Npc is despawned.
     */
    protected final List<AbstractEventListener> setNpcDespawnId(Consumer<OnNpcDespawn> callback, IntCollection npcIds) {
        return registerConsumer(callback, EventType.ON_NPC_DESPAWN, ListenerRegisterType.NPC, npcIds);
    }

    /**
     * Provides instant callback operation when {@link Npc} receives event from another {@link Npc}
     */
    protected final List<AbstractEventListener> setNpcEventReceivedId(Consumer<OnNpcEventReceived> callback, int... npcIds) {
        return registerConsumer(callback, EventType.ON_NPC_EVENT_RECEIVED, ListenerRegisterType.NPC, npcIds);
    }

    /**
     * Provides instant callback operation when {@link Npc} receives event from another {@link Npc}
     */
    protected final List<AbstractEventListener> setNpcEventReceivedId(Consumer<OnNpcEventReceived> callback, IntCollection npcIds) {
        return registerConsumer(callback, EventType.ON_NPC_EVENT_RECEIVED, ListenerRegisterType.NPC, npcIds);
    }

    /**
     * Provides instant callback operation when {@link Npc} finishes to move.
     */
    protected final List<AbstractEventListener> setNpcMoveFinishedId(Consumer<OnNpcMoveFinished> callback, int... npcIds) {
        return registerConsumer(callback, EventType.ON_NPC_MOVE_FINISHED, ListenerRegisterType.NPC, npcIds);
    }

    /**
     * Provides instant callback operation when {@link Npc} finishes to move.
     */
    protected final List<AbstractEventListener> setNpcMoveFinishedId(Consumer<OnNpcMoveFinished> callback, IntCollection npcIds) {
        return registerConsumer(callback, EventType.ON_NPC_MOVE_FINISHED, ListenerRegisterType.NPC, npcIds);
    }

    /**
     * Provides instant callback operation when {@link Npc} finishes to move on its route.
     */
    protected final List<AbstractEventListener> setNpcMoveRouteFinishedId(Consumer<OnNpcMoveRouteFinished> callback, int... npcIds) {
        return registerConsumer(callback, EventType.ON_NPC_MOVE_ROUTE_FINISHED, ListenerRegisterType.NPC, npcIds);
    }

    /**
     * Provides instant callback operation when {@link Npc} finishes to move on its route.
     */
    protected final List<AbstractEventListener> setNpcMoveRouteFinishedId(Consumer<OnNpcMoveRouteFinished> callback, IntCollection npcIds) {
        return registerConsumer(callback, EventType.ON_NPC_MOVE_ROUTE_FINISHED, ListenerRegisterType.NPC, npcIds);
    }

    /**
     * Provides instant callback operation when {@link Npc} is about to hate and start attacking a creature.
     */
    protected final List<AbstractEventListener> addNpcHateId(Function<OnAttackableHate, TerminateReturn> callback, int... npcIds) {
        return registerFunction(callback, EventType.ON_NPC_HATE, ListenerRegisterType.NPC, npcIds);
    }

    /**
     * Provides instant callback operation when {@link Npc} is about to hate and start attacking a creature.
     */
    protected final List<AbstractEventListener> addNpcHateId(Function<OnAttackableHate, TerminateReturn> callback, IntCollection npcIds) {
        return registerFunction(callback, EventType.ON_NPC_HATE, ListenerRegisterType.NPC, npcIds);
    }

    /**
     * Provides instant callback operation when {@link Npc} sees another creature.
     */
    protected final List<AbstractEventListener> setNpcCreatureSeeId(Consumer<OnNpcCreatureSee> callback, int... npcIds) {
        return registerConsumer(callback, EventType.ON_NPC_CREATURE_SEE, ListenerRegisterType.NPC, npcIds);
    }

    /**
     * Provides instant callback operation when {@link Npc} sees another creature.
     */
    protected final List<AbstractEventListener> setNpcCreatureSeeId(Consumer<OnNpcCreatureSee> callback, IntCollection npcIds) {
        return registerConsumer(callback, EventType.ON_NPC_CREATURE_SEE, ListenerRegisterType.NPC, npcIds);
    }

    /**
     * Provides instant callback operation when Attackable is under attack to other clan mates.
     */
    protected final List<AbstractEventListener> setAttackableFactionIdId(Consumer<OnAttackableFactionCall> callback, int... npcIds) {
        return registerConsumer(callback, EventType.ON_ATTACKABLE_FACTION_CALL, ListenerRegisterType.NPC, npcIds);
    }


    /**
     * Provides instant callback operation when Attackable is under attack to other clan mates.
     *
     */
    protected final List<AbstractEventListener> setAttackableFactionIdId(Consumer<OnAttackableFactionCall> callback, IntCollection npcIds) {
        return registerConsumer(callback, EventType.ON_ATTACKABLE_FACTION_CALL, ListenerRegisterType.NPC, npcIds);
    }

    /**
     * Provides instant callback operation when Attackable is attacked from a player.
     *
     */
    protected final List<AbstractEventListener> setAttackableAttackId(Consumer<OnAttackableAttack> callback, int... npcIds) {
        for (int id : npcIds) {
            if (NpcData.getInstance().getTemplate(id) == null) {
                LOGGER.error(super.getClass().getSimpleName() + ": Found addAttackId for non existing NPC: " + id + "!");
            }
        }
        return registerConsumer(callback, EventType.ON_ATTACKABLE_ATTACK, ListenerRegisterType.NPC, npcIds);
    }

    /**
     * Provides instant callback operation when Attackable is attacked from a player.
     *
     */
    protected final List<AbstractEventListener> setAttackableAttackId(Consumer<OnAttackableAttack> callback, IntCollection npcIds) {
        checkNpcIds(npcIds, EventType.ON_ATTACKABLE_ATTACK);
        return registerConsumer(callback, EventType.ON_ATTACKABLE_ATTACK, ListenerRegisterType.NPC, npcIds);
    }

    /**
     * Provides instant callback operation when {@link Player} enters in {@link Attackable}'s aggressive range.
     *
     */
    protected final List<AbstractEventListener> setAttackableAggroRangeEnterId(Consumer<OnAttackableAggroRangeEnter> callback, int... npcIds) {
        return registerConsumer(callback, EventType.ON_ATTACKABLE_AGGRO_RANGE_ENTER, ListenerRegisterType.NPC, npcIds);
    }

    /**
     * Provides instant callback operation when {@link Player} enters in {@link Attackable}'s aggressive range.
     *
     */
    protected final List<AbstractEventListener> setAttackableAggroRangeEnterId(Consumer<OnAttackableAggroRangeEnter> callback, IntCollection npcIds) {
        return registerConsumer(callback, EventType.ON_ATTACKABLE_AGGRO_RANGE_ENTER, ListenerRegisterType.NPC, npcIds);
    }

    /**
     * Provides instant callback operation when {@link Player} learn's a {@link Skill}.
     *
     */
    protected final List<AbstractEventListener> setPlayerSkillLearnId(Consumer<OnPlayerSkillLearn> callback, int... npcIds) {
        return registerConsumer(callback, EventType.ON_PLAYER_SKILL_LEARN, ListenerRegisterType.NPC, npcIds);
    }

    /**
     * Provides instant callback operation when {@link Player} learn's a {@link Skill}.
     *
     */
    protected final List<AbstractEventListener> setPlayerSkillLearnId(Consumer<OnPlayerSkillLearn> callback, IntCollection npcIds) {
        return registerConsumer(callback, EventType.ON_PLAYER_SKILL_LEARN, ListenerRegisterType.NPC, npcIds);
    }

    /**
     * Provides instant callback operation when {@link Player} summons a servitor or a pet
     *
     */
    protected final List<AbstractEventListener> setPlayerSummonSpawnId(Consumer<OnPlayerSummonSpawn> callback, int... npcIds) {
        return registerConsumer(callback, EventType.ON_PLAYER_SUMMON_SPAWN, ListenerRegisterType.NPC, npcIds);
    }

    /**
     * Provides instant callback operation when {@link Player} summons a servitor or a pet
     *
     */
    protected final List<AbstractEventListener> setPlayerSummonSpawnId(Consumer<OnPlayerSummonSpawn> callback, IntCollection npcIds) {
        return registerConsumer(callback, EventType.ON_PLAYER_SUMMON_SPAWN, ListenerRegisterType.NPC, npcIds);
    }

    /**
     * Provides instant callback operation when {@link Player} talk with a servitor or a pet
     */
    protected final List<AbstractEventListener> setPlayerSummonTalkId(Consumer<OnPlayerSummonTalk> callback, int... npcIds) {
        return registerConsumer(callback, EventType.ON_PLAYER_SUMMON_TALK, ListenerRegisterType.NPC, npcIds);
    }

    /**
     * Provides instant callback operation when {@link Player} talk with a servitor or a pet
     */
    protected final List<AbstractEventListener> setPlayerSummonTalkId(Consumer<OnPlayerSummonSpawn> callback, IntCollection npcIds) {
        return registerConsumer(callback, EventType.ON_PLAYER_SUMMON_TALK, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> setPlayerLoginId(Consumer<OnPlayerLogin> callback) {
        return registerConsumer(callback, EventType.ON_PLAYER_LOGIN, ListenerRegisterType.GLOBAL);
    }

    /**
     * Provides instant callback operation when {@link Creature} Enters on a {@link Zone}.
     */
    protected final List<AbstractEventListener> setCreatureZoneEnterId(Consumer<OnCreatureZoneEnter> callback, int... npcIds) {
        return registerConsumer(callback, EventType.ON_CREATURE_ZONE_ENTER, ListenerRegisterType.ZONE, npcIds);
    }

    /**
     * Provides instant callback operation when {@link Creature} Enters on a {@link Zone}.
     */
    protected final List<AbstractEventListener> setCreatureZoneEnterId(Consumer<OnCreatureZoneEnter> callback, IntCollection npcIds) {
        return registerConsumer(callback, EventType.ON_CREATURE_ZONE_ENTER, ListenerRegisterType.ZONE, npcIds);
    }

    /**
     * Provides instant callback operation when {@link Creature} Exits on a {@link Zone}.
     */
    protected final List<AbstractEventListener> setCreatureZoneExitId(Consumer<OnCreatureZoneExit> callback, int... npcIds) {
        return registerConsumer(callback, EventType.ON_CREATURE_ZONE_EXIT, ListenerRegisterType.ZONE, npcIds);
    }

    /**
     * Provides instant callback operation when {@link Creature} Exits on a {@link Zone}.
     */
    protected final List<AbstractEventListener> setCreatureZoneExitId(Consumer<OnCreatureZoneExit> callback, IntCollection npcIds) {
        return registerConsumer(callback, EventType.ON_CREATURE_ZONE_EXIT, ListenerRegisterType.ZONE, npcIds);
    }

    /**
     * Provides instant callback operation when {@link Trap} acts.
     */
    protected final List<AbstractEventListener> setTrapActionId(Consumer<OnTrapAction> callback, int... npcIds) {
        return registerConsumer(callback, EventType.ON_TRAP_ACTION, ListenerRegisterType.NPC, npcIds);
    }

    /**
     * Provides instant callback operation when {@link Trap} acts.
     */
    protected final List<AbstractEventListener> setTrapActionId(Consumer<OnTrapAction> callback, IntCollection npcIds) {
        return registerConsumer(callback, EventType.ON_TRAP_ACTION, ListenerRegisterType.NPC, npcIds);
    }

    /**
     * Provides instant callback operation when {@link ItemTemplate} receives an event from {@link Player}.
     */
    protected final List<AbstractEventListener> setItemBypassEvenId(Consumer<OnItemBypassEvent> callback, int... npcIds) {
        return registerConsumer(callback, EventType.ON_ITEM_BYPASS_EVENT, ListenerRegisterType.ITEM, npcIds);
    }

    /**
     * Provides instant callback operation when {@link ItemTemplate} receives an event from {@link Player}.
     */
    protected final List<AbstractEventListener> setItemBypassEvenId(Consumer<OnItemBypassEvent> callback, IntCollection npcIds) {
        return registerConsumer(callback, EventType.ON_ITEM_BYPASS_EVENT, ListenerRegisterType.ITEM, npcIds);
    }

    /**
     * Provides instant callback operation when {@link Player} talk to {@link ItemTemplate}.
     */
    protected final List<AbstractEventListener> setItemTalkId(Consumer<OnItemTalk> callback, int... npcIds) {
        return registerConsumer(callback, EventType.ON_ITEM_TALK, ListenerRegisterType.ITEM, npcIds);
    }

    /**
     * Provides instant callback operation when {@link Player} talk to {@link ItemTemplate}.
     */
    protected final List<AbstractEventListener> setItemTalkId(Consumer<OnItemTalk> callback, IntCollection npcIds) {
        return registerConsumer(callback, EventType.ON_ITEM_TALK, ListenerRegisterType.ITEM, npcIds);
    }

    /**
     * Provides instant callback operation when instance world created
     */
    protected final List<AbstractEventListener> setInstanceCreatedId(Consumer<OnInstanceCreated> callback, int... templateIds) {
        return registerConsumer(callback, EventType.ON_INSTANCE_CREATED, ListenerRegisterType.INSTANCE, templateIds);
    }

    /**
     * Provides instant callback operation when instance world created
     */
    protected final List<AbstractEventListener> setInstanceCreatedId(Consumer<OnInstanceCreated> callback, IntCollection templateIds) {
        return registerConsumer(callback, EventType.ON_INSTANCE_CREATED, ListenerRegisterType.INSTANCE, templateIds);
    }

    /**
     * Provides instant callback operation when instance world destroyed
     */
    protected final List<AbstractEventListener> setInstanceDestroyId(Consumer<OnInstanceDestroy> callback, int... templateIds) {
        return registerConsumer(callback, EventType.ON_INSTANCE_DESTROY, ListenerRegisterType.INSTANCE, templateIds);
    }

    /**
     * Provides instant callback operation when instance world destroyed
     */
    protected final List<AbstractEventListener> setInstanceDestroyId(Consumer<OnInstanceDestroy> callback, IntCollection templateIds) {
        return registerConsumer(callback, EventType.ON_INSTANCE_DESTROY, ListenerRegisterType.INSTANCE, templateIds);
    }

    /**
     * Provides instant callback operation when player enters into instance world
     */
    protected final List<AbstractEventListener> setInstanceEnterId(Consumer<OnInstanceEnter> callback, int... templateIds) {
        return registerConsumer(callback, EventType.ON_INSTANCE_ENTER, ListenerRegisterType.INSTANCE, templateIds);
    }

    /**
     * Provides instant callback operation when player enters into instance world
     */
    protected final List<AbstractEventListener> setInstanceEnterId(Consumer<OnInstanceEnter> callback, IntCollection templateIds) {
        return registerConsumer(callback, EventType.ON_INSTANCE_ENTER, ListenerRegisterType.INSTANCE, templateIds);
    }

    /**
     * Provides instant callback operation when player leave from instance world
     */
    protected final List<AbstractEventListener> setInstanceLeaveId(Consumer<OnInstanceLeave> callback, int... templateIds) {
        return registerConsumer(callback, EventType.ON_INSTANCE_LEAVE, ListenerRegisterType.INSTANCE, templateIds);
    }

    /**
     * Provides instant callback operation when player leave from instance world
     */
    protected final List<AbstractEventListener> setInstanceLeaveId(Consumer<OnInstanceLeave> callback, IntCollection templateIds) {
        return registerConsumer(callback, EventType.ON_INSTANCE_LEAVE, ListenerRegisterType.INSTANCE, templateIds);
    }

    /**
     * Method that registers Function type of listeners (Listeners that need parameters but doesn't return objects)
     */
    protected final List<AbstractEventListener> registerConsumer(Consumer<? extends IBaseEvent> callback, EventType type, ListenerRegisterType registerType, int... npcIds) {
        return registerListener((container) -> new ConsumerEventListener(container, type, callback, this), registerType, npcIds);
    }

    /**
     * Method that registers Function type of listeners (Listeners that need parameters but doesn't return objects)
     */
    protected final List<AbstractEventListener> registerConsumer(Consumer<? extends IBaseEvent> callback, EventType type, ListenerRegisterType registerType, IntCollection npcIds) {
        return registerListener((container) -> new ConsumerEventListener(container, type, callback, this), registerType, npcIds);
    }

    /**
     * Method that registers Function type of listeners (Listeners that need parameters and return objects)
     */
    protected final List<AbstractEventListener> registerFunction(Function<? extends IBaseEvent, ? extends AbstractEventReturn> callback, EventType type, ListenerRegisterType registerType, int... npcIds) {
        return registerListener((container) -> new FunctionEventListener(container, type, callback, this), registerType, npcIds);
    }

    /**
     * Method that registers Function type of listeners (Listeners that need parameters and return objects)
     */
    protected final List<AbstractEventListener> registerFunction(Function<? extends IBaseEvent, ? extends AbstractEventReturn> callback, EventType type, ListenerRegisterType registerType, IntCollection npcIds) {
        return registerListener((container) -> new FunctionEventListener(container, type, callback, this), registerType, npcIds);
    }

    /**
     * Method that registers runnable type of listeners (Listeners that doesn't needs parameters or return objects)
     */
    protected final List<AbstractEventListener> registerAnnotation(Method callback, EventType type, ListenerRegisterType registerType, int priority, IntCollection npcIds) {
        return registerListener((container) -> new AnnotationEventListener(container, type, callback, this, priority), registerType, npcIds);
    }

    /**
     * Method that registers dummy type of listeners (Listeners doesn't gets notification but just used to check if their type present or not)
     */
    protected final List<AbstractEventListener> registerDummy(EventType type, ListenerRegisterType registerType, int... npcIds) {
        return registerListener((container) -> new DummyEventListener(container, type, this), registerType, npcIds);
    }

    /**
     * Method that registers dummy type of listeners (Listeners doesn't gets notification but just used to check if their type present or not)
     */
    protected final List<AbstractEventListener> registerDummy(EventType type, ListenerRegisterType registerType, IntCollection npcIds) {
        return registerListener((container) -> new DummyEventListener(container, type, this), registerType, npcIds);
    }

    /**
     * Generic listener register method
     */
    protected final List<AbstractEventListener> registerListener(Function<ListenersContainer, AbstractEventListener> action, ListenerRegisterType registerType, int... ids) {
        final List<AbstractEventListener> listeners = new ArrayList<>(ids.length > 0 ? ids.length : 1);
        if (ids.length > 0) {
            for (int id : ids) {
                registerListenrWithId(action, registerType, listeners, id);

                _registeredIds.computeIfAbsent(registerType, k -> CHashIntMap.newKeySet()).add(id);
            }
        } else {
            registerListenerWithoutId(action, registerType, listeners);
        }

        _listeners.addAll(listeners);
        return listeners;
    }

    private void registerListenerWithoutId(Function<ListenersContainer, AbstractEventListener> action, ListenerRegisterType registerType, List<AbstractEventListener> listeners) {
        switch (registerType) {
            case OLYMPIAD: {
                /*final OlympiadEngine template = OlympiadEngine.getInstance();
                listeners.add(template.addListener(action.apply(template)));*/
                break;
            }
            case GLOBAL: // Global Listener
            {
                final ListenersContainer template = Listeners.Global();
                listeners.add(template.addListener(action.apply(template)));
                break;
            }
            case GLOBAL_NPCS: // Global Npcs Listener
            {
                final ListenersContainer template = Listeners.Npcs();
                listeners.add(template.addListener(action.apply(template)));
                break;
            }
            case GLOBAL_MONSTERS: // Global Monsters Listener
            {
                final ListenersContainer template = Listeners.Monsters();
                listeners.add(template.addListener(action.apply(template)));
                break;
            }
            case GLOBAL_PLAYERS: // Global Players Listener
            {
                final ListenersContainer template = Listeners.players();
                listeners.add(template.addListener(action.apply(template)));
                break;
            }
        }
    }

    /**
     * Generic listener register method
     */
    protected final List<AbstractEventListener> registerListener(Function<ListenersContainer, AbstractEventListener> action, ListenerRegisterType registerType, IntCollection ids) {
        final List<AbstractEventListener> listeners = new ArrayList<>(!ids.isEmpty() ? ids.size() : 1);
        if (!ids.isEmpty()) {
            ids.forEach(id -> registerListenrWithId(action, registerType, listeners, id));

            _registeredIds.computeIfAbsent(registerType, k -> CHashIntMap.newKeySet()).addAll(ids);
        } else {
            registerListenerWithoutId(action, registerType, listeners);
        }
        _listeners.addAll(listeners);
        return listeners;
    }

    private void registerListenrWithId(Function<ListenersContainer, AbstractEventListener> action, ListenerRegisterType registerType, List<AbstractEventListener> listeners, int id) {
        switch (registerType) {
            case NPC: {
                final NpcTemplate template = NpcData.getInstance().getTemplate(id);
                if (template != null) {
                    listeners.add(template.addListener(action.apply(template)));
                }
                break;
            }
            case ZONE: {
                final Zone template = ZoneEngine.getInstance().getZoneById(id);
                if (template != null) {
                    listeners.add(template.addListener(action.apply(template)));
                }
                break;
            }
            case ITEM: {
                final ItemTemplate template = ItemEngine.getInstance().getTemplate(id);
                if (template != null) {
                    listeners.add(template.addListener(action.apply(template)));
                }
                break;
            }
            case CASTLE: {
                final Castle template = CastleManager.getInstance().getCastleById(id);
                if (template != null) {
                    listeners.add(template.addListener(action.apply(template)));
                }
                break;
            }
            case INSTANCE: {
                final InstanceTemplate template = InstanceManager.getInstance().getInstanceTemplate(id);
                if (template != null) {
                    listeners.add(template.addListener(action.apply(template)));
                }
                break;
            }
            default: {
                LOGGER.warn(": Unhandled register type: " + registerType);
            }
        }
    }

    public IntSet getRegisteredIds(ListenerRegisterType type) {
        return _registeredIds.getOrDefault(type, Containers.emptyIntSet());
    }

    public Queue<AbstractEventListener> getListeners() {
        return _listeners;
    }

    public void onSpawnActivate(SpawnTemplate template) {

    }

    public void onSpawnDeactivate(SpawnTemplate template) {

    }

    public void onSpawnNpc(SpawnTemplate template, SpawnGroup group, Npc npc) {

    }

    public void onSpawnDespawnNpc(SpawnTemplate template, SpawnGroup group, Npc npc) {

    }

    public void onSpawnNpcDeath(SpawnTemplate template, SpawnGroup group, Npc npc, Creature killer) {

    }

    public Trap addTrap(int trapId, int x, int y, int z, int heading, Skill skill, int instanceId) {
        final NpcTemplate npcTemplate = NpcData.getInstance().getTemplate(trapId);
        final Trap trap = new Trap(npcTemplate, instanceId, -1);
        trap.setCurrentHp(trap.getMaxHp());
        trap.setCurrentMp(trap.getMaxMp());
        trap.setIsInvul(true);
        trap.setHeading(heading);
        trap.spawnMe(x, y, z);
        return trap;
    }

    public Npc addMinion(Monster master, int minionId) {
        return MinionList.spawnMinion(master, minionId);
    }

    /**
     * Get the total amount of all specified items in player's inventory.
     *
     * @param player  the player whose inventory to check
     * @param itemIds a list of IDs of items whose amount to get
     * @return the summary amount of all listed items in player's inventory
     */
    public long getQuestItemsCount(Player player, int... itemIds) {
        long count = 0;
        for (Item item : player.getInventory().getQuestItems()) {
            for (int itemId : itemIds) {
                if (item.getId() == itemId) {
                    if (MathUtil.checkAddOverFlow(count, item.getCount())) {
                        return Long.MAX_VALUE;
                    }
                    count += item.getCount();
                }
            }
        }
        return count;
    }

    /**
     * Check for multiple items in player's inventory.
     *
     * @param player  the player whose inventory to check for quest items
     * @param itemIds a list of item IDs to check for
     * @return {@code true} if at least one items exist in player's inventory, {@code false} otherwise
     */
    public boolean hasAtLeastOneQuestItem(Player player, int... itemIds) {
        final PlayerInventory inv = player.getInventory();
        for (int itemId : itemIds) {
            if (inv.getItemByItemId(itemId) != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Give Adena to the player.
     *
     * @param player     the player to whom to give the Adena
     * @param count      the amount of Adena to give
     * @param applyRates if {@code true} quest rates will be applied to the amount
     */
    public void giveAdena(Player player, long count, boolean applyRates) {
        if (applyRates) {
            rewardItems(player, CommonItem.ADENA, count);
        } else {
            giveItems(player, CommonItem.ADENA, count);
        }
    }

    /**
     * Execute a procedure for each player depending on the parameters.
     * @param includeCommandChannel if {@code true}, {@link #actionForEachPlayer(Player, Npc, boolean)} will be called with the player's command channel members
     * @see #actionForEachPlayer(Player, Npc, boolean)
     */
    public final void executeForEachPlayer(Player player, Npc npc, boolean isSummon, boolean includeParty, boolean includeCommandChannel) {
        if ((includeParty || includeCommandChannel) && player.isInParty()) {
            if (includeCommandChannel && player.getParty().isInCommandChannel()) {
                player.getParty().getCommandChannel().checkEachMember(member ->
                {
                    actionForEachPlayer(member, npc, isSummon);
                    return true;
                });
            } else if (includeParty) {
                player.getParty().checkEachMember(member ->
                {
                    actionForEachPlayer(member, npc, isSummon);
                    return true;
                });
            }
        } else {
            actionForEachPlayer(player, npc, isSummon);
        }
    }

    /**
     * Overridable method called from {@link #executeForEachPlayer(Player, Npc, boolean, boolean, boolean)}
     *
     * @param player   the player on which the action will be run
     * @param npc      the NPC related to this action
     * @param isSummon {@code true} if the event that called this method was originated by the player's summon
     */
    public void actionForEachPlayer(Player player, Npc npc, boolean isSummon) {
        // To be overridden in quest scripts.
    }

    /**
     * Open a door if it is present on the instance and its not open.
     *
     * @param doorId     the ID of the door to open
     * @param instanceId the ID of the instance the door is in (0 if the door is not not inside an instance)
     */
    public void openDoor(int doorId, int instanceId) {
        final Door door = getDoor(doorId, instanceId);
        if (door == null) {
            LOGGER.warn(getClass().getSimpleName() + ": called openDoor(" + doorId + ", " + instanceId + "); but door wasnt found!", new NullPointerException());
        } else if (!door.isOpen()) {
            door.openMe();
        }
    }

    /**
     * Close a door if it is present in a specified the instance and its open.
     *
     * @param doorId     the ID of the door to close
     * @param instanceId the ID of the instance the door is in (0 if the door is not not inside an instance)
     */
    public void closeDoor(int doorId, int instanceId) {
        final Door door = getDoor(doorId, instanceId);
        if (door == null) {
            LOGGER.warn(getClass().getSimpleName() + ": called closeDoor(" + doorId + ", " + instanceId + "); but door wasnt found!", new NullPointerException());
        } else if (door.isOpen()) {
            door.closeMe();
        }
    }

    /**
     * Retrieve a door from an instance or the real world.
     *
     * @param doorId     the ID of the door to get
     * @param instanceId the ID of the instance the door is in (0 if the door is not not inside an instance)
     * @return the found door or {@code null} if no door with that ID and instance ID was found
     */
    public Door getDoor(int doorId, int instanceId) {
        Door door = null;
        if (instanceId <= 0) {
            door = DoorDataManager.getInstance().getDoor(doorId);
        } else {
            final Instance inst = InstanceManager.getInstance().getInstance(instanceId);
            if (inst != null) {
                door = inst.getDoor(doorId);
            }
        }
        return door;
    }

    /**
     * Monster is running and attacking the playable.
     *
     * @param npc      the NPC that performs the attack
     * @param playable the player
     */
    protected void addAttackPlayerDesire(Npc npc, Playable playable) {
        addAttackPlayerDesire(npc, playable, 999);
    }

    /**
     * Monster is running and attacking the target.
     *
     * @param npc    the NPC that performs the attack
     * @param target the target of the attack
     * @param desire the desire to perform the attack
     */
    protected void addAttackPlayerDesire(Npc npc, Playable target, int desire) {
        if (isAttackable(npc)) {
            ((Attackable) npc).addDamageHate(target, 0, desire);
        }
        npc.setRunning();
        npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
    }

    /**
     * Monster is running and attacking the target.
     *
     * @param npc    the NPC that performs the attack
     * @param target the target of the attack
     */
    protected void addAttackDesire(Npc npc, Creature target) {
        npc.setRunning();
        npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
    }

    /**
     * Adds desire to move to the given NPC.
     *
     * @param npc    the NPC
     * @param loc    the location
     * @param desire the desire
     */
    protected void addMoveToDesire(Npc npc, Location loc, int desire) {
        npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, loc);
    }

    /**
     * Instantly cast a skill upon the given target.
     *
     * @param npc    the caster NPC
     * @param target the target of the cast
     * @param skill  the skill to cast
     */
    protected void castSkill(Npc npc, Playable target, SkillHolder skill) {
        npc.setTarget(target);
        npc.doCast(skill.getSkill());
    }

    /**
     * Adds the desire to cast a skill to the given NPC.
     */
    protected void addSkillCastDesire(Npc npc, WorldObject target, Skill skill, int desire) {
        if (isAttackable(npc) && isCreature(target)) {
            ((Attackable) npc).addDamageHate((Creature) target, 0, desire);
        }
        npc.setTarget(target != null ? target : npc);
        npc.doCast(skill);
    }

    /**
     * Show an on screen message to the player.
     *
     * @param player the player to display the message to
     * @param text   the message to display
     * @param time   the duration of the message in milliseconds
     */
    public static void showOnScreenMsg(Player player, String text, int time) {
        player.sendPacket(new ExShowScreenMessage(text, time));
    }

    /**
     * Show an on screen message to the player.
     * @param params    values of parameters to replace in the NPC String (like S1, C1 etc.)
     */
    public static void showOnScreenMsg(Player player, NpcStringId npcString, int position, int time, String... params) {
        player.sendPacket(new ExShowScreenMessage(npcString, position, time, params));
    }

    /**
     * Add a temporary spawn of the specified NPC.
     *
     * @param npcId the ID of the NPC to spawn
     * @param pos   the object containing the spawn location coordinates
     * @return the {@link Npc} object of the newly spawned NPC or {@code null} if the NPC doesn't exist
     * @see #addSpawn(int, IPositionable, boolean, long, boolean, int)
     * @see #addSpawn(int, int, int, int, int, boolean, long, boolean, int)
     */
    public static Npc addSpawn(int npcId, IPositionable pos) {
        return addSpawn(npcId, pos.getX(), pos.getY(), pos.getZ(), pos.getHeading(), false, 0, false, 0);
    }

    /**
     * Add a temporary spawn of the specified NPC. the {@link Npc} object of the newly spawned NPC or {@code null} if the NPC doesn't exist
     * @see #addSpawn(int, IPositionable, boolean, long, boolean, int)
     * @see #addSpawn(int, int, int, int, int, boolean, long, boolean, int)
     */
    public static Npc addSpawn(int npcId, IPositionable pos, boolean isSummonSpawn) {
        return addSpawn(npcId, pos.getX(), pos.getY(), pos.getZ(), pos.getHeading(), false, 0, isSummonSpawn, 0);
    }

    /**
     * Add a temporary spawn of the specified NPC.
     * @return the {@link Npc} object of the newly spawned NPC or {@code null} if the NPC doesn't exist
     * @see #addSpawn(int, IPositionable, boolean, long, boolean, int)
     * @see #addSpawn(int, int, int, int, int, boolean, long, boolean, int)
     */
    public static Npc addSpawn(int npcId, IPositionable pos, boolean randomOffset, long despawnDelay) {
        return addSpawn(npcId, pos.getX(), pos.getY(), pos.getZ(), pos.getHeading(), randomOffset, despawnDelay, false, 0);
    }

    /**
     * Add a temporary spawn of the specified NPC.
     * @param isSummonSpawn if {@code true}, displays a summon animation on NPC spawn
     * @return the {@link Npc} object of the newly spawned NPC or {@code null} if the NPC doesn't exist
     * @see #addSpawn(int, IPositionable, boolean, long, boolean, int)
     * @see #addSpawn(int, int, int, int, int, boolean, long, boolean, int)
     */
    public static Npc addSpawn(int npcId, IPositionable pos, boolean randomOffset, long despawnDelay, boolean isSummonSpawn) {
        return addSpawn(npcId, pos.getX(), pos.getY(), pos.getZ(), pos.getHeading(), randomOffset, despawnDelay, isSummonSpawn, 0);
    }

    /**
     * Add a temporary spawn of the specified NPC.
     * @param isSummonSpawn if {@code true}, displays a summon animation on NPC spawn
     * @param instanceId    the ID of the instance to spawn the NPC in (0 - the open world)
     * @return the {@link Npc} object of the newly spawned NPC or {@code null} if the NPC doesn't exist
     * @see #addSpawn(int, IPositionable)
     * @see #addSpawn(int, IPositionable, boolean)
     * @see #addSpawn(int, IPositionable, boolean, long)
     * @see #addSpawn(int, IPositionable, boolean, long, boolean)
     * @see #addSpawn(int, int, int, int, int, boolean, long, boolean, int)
     */
    public static Npc addSpawn(int npcId, IPositionable pos, boolean randomOffset, long despawnDelay, boolean isSummonSpawn, int instanceId) {
        return addSpawn(npcId, pos.getX(), pos.getY(), pos.getZ(), pos.getHeading(), randomOffset, despawnDelay, isSummonSpawn, instanceId);
    }

    /**
     * Add a temporary spawn of the specified NPC.
     * @param heading      the heading of the NPC
     * @param randomOffset if {@code true}, adds +/- 50~100 to X/Y coordinates of the spawn location
     * @param despawnDelay time in milliseconds till the NPC is despawned (0 - only despawned on server shutdown)
     * @return the {@link Npc} object of the newly spawned NPC or {@code null} if the NPC doesn't exist
     * @see #addSpawn(int, IPositionable, boolean, long, boolean, int)
     * @see #addSpawn(int, int, int, int, int, boolean, long, boolean, int)
     */
    public static Npc addSpawn(int npcId, int x, int y, int z, int heading, boolean randomOffset, long despawnDelay) {
        return addSpawn(npcId, x, y, z, heading, randomOffset, despawnDelay, false, 0);
    }

    /**
     * Add a temporary spawn of the specified NPC.
     * @param heading       the heading of the NPC
     * @param randomOffset  if {@code true}, adds +/- 50~100 to X/Y coordinates of the spawn location
     * @param despawnDelay  time in milliseconds till the NPC is despawned (0 - only despawned on server shutdown)
     * @param isSummonSpawn if {@code true}, displays a summon animation on NPC spawn
     * @return the {@link Npc} object of the newly spawned NPC or {@code null} if the NPC doesn't exist
     * @see #addSpawn(int, IPositionable, boolean, long, boolean, int)
     * @see #addSpawn(int, int, int, int, int, boolean, long, boolean, int)
     */
    public static Npc addSpawn(int npcId, int x, int y, int z, int heading, boolean randomOffset, long despawnDelay, boolean isSummonSpawn) {
        return addSpawn(npcId, x, y, z, heading, randomOffset, despawnDelay, isSummonSpawn, 0);
    }

    /**
     * Add a temporary spawn of the specified NPC.
     * @param heading       the heading of the NPC
     * @param randomOffset  if {@code true}, adds +/- 50~100 to X/Y coordinates of the spawn location
     * @param despawnDelay  time in milliseconds till the NPC is despawned (0 - only despawned on server shutdown)
     * @param isSummonSpawn if {@code true}, displays a summon animation on NPC spawn
     * @param instanceId    the ID of the instance to spawn the NPC in (0 - the open world)
     * @return the {@link Npc} object of the newly spawned NPC or {@code null} if the NPC doesn't exist
     * @see #addSpawn(int, IPositionable, boolean, long, boolean, int)
     * @see #addSpawn(int, int, int, int, int, boolean, long)
     * @see #addSpawn(int, int, int, int, int, boolean, long, boolean)
     */
    public static Npc addSpawn(int npcId, int x, int y, int z, int heading, boolean randomOffset, long despawnDelay, boolean isSummonSpawn, int instanceId) {
        return addSpawn(null, npcId, x, y, z, heading, randomOffset, despawnDelay, isSummonSpawn, instanceId);
    }

    /**
     * Add a temporary spawn of the specified NPC.
     * @param z             the Z coordinate (height) of the spawn location
     * @param heading       the heading of the NPC
     * @param randomOffset  if {@code true}, adds +/- 50~100 to X/Y coordinates of the spawn location
     * @param despawnDelay  time in milliseconds till the NPC is despawned (0 - only despawned on server shutdown)
     * @param isSummonSpawn if {@code true}, displays a summon animation on NPC spawn
     * @param instance      instance where NPC should be spawned ({@code null} - normal world)
     * @return the {@link Npc} object of the newly spawned NPC or {@code null} if the NPC doesn't exist
     * @see #addSpawn(int, IPositionable, boolean, long, boolean, int)
     * @see #addSpawn(int, int, int, int, int, boolean, long)
     * @see #addSpawn(int, int, int, int, int, boolean, long, boolean)
     */
    public static Npc addSpawn(Npc summoner, int npcId, int x, int y, int z, int heading, boolean randomOffset, long despawnDelay, boolean isSummonSpawn, int instance) {
        try {
            final Spawn spawn = new Spawn(npcId);
            if ((x == 0) && (y == 0)) {
                LOGGER.error("addSpawn(): invalid spawn coordinates for NPC #" + npcId + "!");
                return null;
            }
            if (randomOffset) {
                int offset = Rnd.get(50, 100);
                if (Rnd.nextBoolean()) {
                    offset *= -1;
                }
                x += offset;
                offset = Rnd.get(50, 100);
                if (Rnd.nextBoolean()) {
                    offset *= -1;
                }
                y += offset;
            }
            spawn.setInstanceId(instance);
            spawn.setHeading(heading);
            spawn.setXYZ(x, y, z);
            spawn.stopRespawn();

            final Npc npc = spawn.doSpawn(isSummonSpawn);
            if (despawnDelay > 0) {
                npc.scheduleDespawn(despawnDelay);
            }
            if (summoner != null) {
                summoner.addSummonedNpc(npc);
                npc.setSummoner(summoner);
            }
            return npc;
        } catch (Exception e) {
            LOGGER.warn("Could not spawn NPC #" + npcId + "; error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Get the amount of an item in player's inventory.
     *
     * @param player the player whose inventory to check
     * @param itemId the ID of the item whose amount to get
     * @return the amount of the specified item in player's inventory
     */
    public static long getQuestItemsCount(Player player, int itemId) {
        return player.getInventory().getInventoryItemCount(itemId, -1);
    }

    /**
     * Check if the player has the specified item in his inventory.
     *
     * @param player the player whose inventory to check for the specified item
     * @param item   the {@link ItemHolder} object containing the ID and count of the item to check
     * @return {@code true} if the player has the required count of the item
     */
    protected static boolean hasItem(Player player, ItemHolder item) {
        return hasItem(player, item, true);
    }

    /**
     * Check if the player has the required count of the specified item in his inventory.
     *
     * @param player     the player whose inventory to check for the specified item
     * @param item       the {@link ItemHolder} object containing the ID and count of the item to check
     * @param checkCount if {@code true}, check if each item is at least of the count specified in the ItemHolder,<br>
     *                   otherwise check only if the player has the item at all
     * @return {@code true} if the player has the item
     */
    protected static boolean hasItem(Player player, ItemHolder item, boolean checkCount) {
        if (item == null) {
            return false;
        }
        if (checkCount) {
            return getQuestItemsCount(player, item.getId()) >= item.getCount();
        }
        return hasQuestItems(player, item.getId());
    }

    /**
     * Check if the player has all the specified items in his inventory and, if necessary, if their count is also as required.
     *
     * @param player     the player whose inventory to check for the specified item
     * @param checkCount if {@code true}, check if each item is at least of the count specified in the ItemHolder,<br>
     *                   otherwise check only if the player has the item at all
     * @param itemList   a list of {@link ItemHolder} objects containing the IDs of the items to check
     * @return {@code true} if the player has all the items from the list
     */
    protected static boolean hasAllItems(Player player, boolean checkCount, ItemHolder... itemList) {
        if ((itemList == null) || (itemList.length == 0)) {
            return false;
        }
        for (ItemHolder item : itemList) {
            if (!hasItem(player, item, checkCount)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check for an item in player's inventory.
     *
     * @param player the player whose inventory to check for quest items
     * @param itemId the ID of the item to check for
     * @return {@code true} if the item exists in player's inventory, {@code false} otherwise
     */
    public static boolean hasQuestItems(Player player, int itemId) {
        return player.getInventory().getItemByItemId(itemId) != null;
    }

    /**
     * Check for multiple items in player's inventory.
     *
     * @param player  the player whose inventory to check for quest items
     * @param itemIds a list of item IDs to check for
     * @return {@code true} if all items exist in player's inventory, {@code false} otherwise
     */
    public static boolean hasQuestItems(Player player, int... itemIds) {
        if ((itemIds == null) || (itemIds.length == 0)) {
            return false;
        }
        final PlayerInventory inv = player.getInventory();
        for (int itemId : itemIds) {
            if (inv.getItemByItemId(itemId) == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Give a reward to player using multipliers.
     */
    public static void rewardItems(Player player, ItemHolder holder) {
        rewardItems(player, holder.getId(), holder.getCount());
    }

    /**
     * Give a reward to player using multipliers.
     *
     * @param player the player to whom to give the item
     * @param itemId the ID of the item to give
     * @param count  the amount of items to give
     */
    public static void rewardItems(Player player, int itemId, long count) {
        if (count <= 0) {
            return;
        }

        final ItemTemplate item = ItemEngine.getInstance().getTemplate(itemId);
        if (item == null) {
            return;
        }

        try {
            if (itemId == CommonItem.ADENA) {
                count *= Config.RATE_QUEST_REWARD_ADENA;
            } else if (Config.RATE_QUEST_REWARD_USE_MULTIPLIERS) {
                if (item instanceof EtcItem) {
                    switch (((EtcItem) item).getItemType()) {
                        case POTION: {
                            count *= Config.RATE_QUEST_REWARD_POTION;
                            break;
                        }
                        case ENCHANT_WEAPON:
                        case ENCHANT_ARMOR:
                        case SCROLL: {
                            count *= Config.RATE_QUEST_REWARD_SCROLL;
                            break;
                        }
                        case RECIPE: {
                            count *= Config.RATE_QUEST_REWARD_RECIPE;
                            break;
                        }
                        case MATERIAL: {
                            count *= Config.RATE_QUEST_REWARD_MATERIAL;
                            break;
                        }
                        default: {
                            count *= Config.RATE_QUEST_REWARD;
                        }
                    }
                }
            } else {
                count *= Config.RATE_QUEST_REWARD;
            }
        } catch (Exception e) {
            count = Long.MAX_VALUE;
        }

        // Add items to player's inventory
        final Item itemInstance = player.getInventory().addItem("Quest", itemId, count, player, player.getTarget());
        if (itemInstance == null) {
            return;
        }

        sendItemGetMessage(player, itemInstance, count);
    }

    /**
     * Send the system message and the status update packets to the player.
     *
     * @param player the player that has got the item
     * @param item   the item obtain by the player
     * @param count  the item count
     */
    private static void sendItemGetMessage(Player player, Item item, long count) {
        // If item for reward is gold, send message of gold reward to client
        if (item.getId() == CommonItem.ADENA) {
            final SystemMessage smsg = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_EARNED_S1_ADENA);
            smsg.addLong(count);
            player.sendPacket(smsg);
        }
        // Otherwise, send message of object reward to client
        else if (count > 1) {
            final SystemMessage smsg = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_EARNED_S2_S1_S);
            smsg.addItemName(item);
            smsg.addLong(count);
            player.sendPacket(smsg);
        } else {
            final SystemMessage smsg = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_EARNED_S1);
            smsg.addItemName(item);
            player.sendPacket(smsg);
        }
    }

    /**
     * Give item/reward to the player
     */
    public static void giveItems(Player player, int itemId, long count) {
        giveItems(player, itemId, count, 0, false);
    }

    /**
     * Give item/reward to the player
     */
    protected static void giveItems(Player player, ItemHolder holder) {
        giveItems(player, holder.getId(), holder.getCount());
    }

    public static void giveItems(Player player, int itemId, long count, int enchantlevel, boolean playSound) {
        if (count <= 0) {
            return;
        }

        // Add items to player's inventory
        final Item item = player.getInventory().addItem("Quest", itemId, count, player, player.getTarget());
        if (item == null) {
            return;
        }

        // set enchant level for item if that item is not adena
        if ((enchantlevel > 0) && (itemId != CommonItem.ADENA)) {
            item.changeEnchantLevel(enchantlevel);
        }

        if (playSound) {
            playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
        }
        sendItemGetMessage(player, item, count);
    }

    /**
     * Give the specified player a set amount of items if he is lucky enough.<br>
     * Not recommended to use this for non-stacking items.
     * @param limit        the maximum amount of items the player can have. Won't give more if this limit is reached. 0 - no limit.
     * @param dropChance   the drop chance as a decimal digit from 0 to 1
     * @param playSound    if true, plays ItemSound.quest_itemget when items are given and ItemSound.quest_middle when the limit is reached
     * @return {@code true} if limit > 0 and the limit was reached or if limit <= 0 and items were given; {@code false} in all other cases
     */
    public static boolean giveItemRandomly(Player player, Npc npc, int itemId, long amountToGive, long limit, double dropChance, boolean playSound) {
        return giveItemRandomly(player, npc, itemId, amountToGive, amountToGive, limit, dropChance, playSound);
    }

    /**
     * Give the specified player a random amount of items if he is lucky enough.<br>
     * Not recommended to use this for non-stacking items.
     * @param maxAmount  the maximum amount of items to give (will give a random amount between min/maxAmount multiplied by quest rates)
     * @param limit      the maximum amount of items the player can have. Won't give more if this limit is reached. 0 - no limit.
     * @param dropChance the drop chance as a decimal digit from 0 to 1
     * @param playSound  if true, plays ItemSound.quest_itemget when items are given and ItemSound.quest_middle when the limit is reached
     * @return {@code true} if limit > 0 and the limit was reached or if limit <= 0 and items were given; {@code false} in all other cases
     */
    public static boolean giveItemRandomly(Player player, Npc npc, int itemId, long minAmount, long maxAmount, long limit, double dropChance, boolean playSound) {
        final long currentCount = getQuestItemsCount(player, itemId);

        if ((limit > 0) && (currentCount >= limit)) {
            return true;
        }

        minAmount *= Config.RATE_QUEST_DROP;
        maxAmount *= Config.RATE_QUEST_DROP;
        dropChance *= Config.RATE_QUEST_DROP; // TODO separate configs for rate and amount
        if ((npc != null) && Config.CHAMPION_ENABLE && npc.isChampion()) {
            if (itemId == CommonItem.ADENA) {
                dropChance *= Config.CHAMPION_ADENAS_REWARDS_CHANCE;
                minAmount *= Config.CHAMPION_ADENAS_REWARDS_AMOUNT;
                maxAmount *= Config.CHAMPION_ADENAS_REWARDS_AMOUNT;
            } else {
                dropChance *= Config.CHAMPION_REWARDS_CHANCE;
                minAmount *= Config.CHAMPION_REWARDS_AMOUNT;
                maxAmount *= Config.CHAMPION_REWARDS_AMOUNT;
            }
        }

        long amountToGive = (minAmount == maxAmount) ? minAmount : Rnd.get(minAmount, maxAmount);
        final double random = Rnd.nextDouble();
        // Inventory slot check (almost useless for non-stacking items)
        if ((dropChance >= random) && (amountToGive > 0) && player.getInventory().validateCapacityByItemId(itemId)) {
            if ((limit > 0) && ((currentCount + amountToGive) > limit)) {
                amountToGive = limit - currentCount;
            }

            // Give the item to player
            if (player.addItem("Quest", itemId, amountToGive, npc, true) != null) {
                // limit reached (if there is no limit, this block doesn't execute)
                if ((currentCount + amountToGive) == limit) {
                    if (playSound) {
                        playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
                    }
                    return true;
                }

                if (playSound) {
                    playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
                }
                return limit <= 0;
            }
        }
        return false;
    }

    /**
     * Take an amount of a specified item from player's inventory. {@code true} if any items were taken, {@code false} otherwise
     */
    public static boolean takeItems(Player player, int itemId, long amount) {
        // Get object item from player's inventory list
        final Item item = player.getInventory().getItemByItemId(itemId);
        if (item == null) {
            return false;
        }

        // Tests on count value in order not to have negative value
        if ((amount < 0) || (amount > item.getCount())) {
            amount = item.getCount();
        }

        // Destroy the quantity of items wanted
        if (item.isEquipped()) {
            var unequiped = player.getInventory().unEquipItemInBodySlotAndRecord(item.getBodyPart());
            final InventoryUpdate iu = new InventoryUpdate();
            for (Item itm : unequiped) {
                iu.addModifiedItem(itm);
            }
            player.sendInventoryUpdate(iu);
            player.broadcastUserInfo();
        }
        return player.destroyItemByItemId("Quest", itemId, amount, player, true);
    }

    /**
     * Take a set amount of a specified item from player's inventory.
     *
     * @param player the player whose item to take
     * @param holder the {@link ItemHolder} object containing the ID and count of the item to take
     * @return {@code true} if the item was taken, {@code false} otherwise
     */
    protected static boolean takeItem(Player player, ItemHolder holder) {
        if (holder == null) {
            return false;
        }
        return takeItems(player, holder.getId(), holder.getCount());
    }

    /**
     * Take an amount of all specified items from player's inventory. {@code true} if all items were taken, {@code false} otherwise
     */
    public static boolean takeItems(Player player, int amount, int... itemIds) {
        boolean check = true;
        if (itemIds != null) {
            for (int item : itemIds) {
                check &= takeItems(player, item, amount);
            }
        }
        return check;
    }

    /**
     * Send a packet in order to play a sound to the player.
     *
     * @param player the player whom to send the packet
     * @param sound  the name of the sound to play
     */
    public static void playSound(Player player, String sound) {
        player.sendPacket(PlaySound.sound(sound));
    }

    /**
     * Send a packet in order to play a sound to the player.
     *
     * @param player the player whom to send the packet
     * @param sound  the {@link QuestSound} object of the sound to play
     */
    public static void playSound(Player player, QuestSound sound) {
        player.sendPacket(sound.getPacket());
    }

    /**
     * Add EXP and SP as quest reward.
     *
     * @param player the player whom to reward with the EXP/SP
     * @param exp    the amount of EXP to give to the player
     * @param sp     the amount of SP to give to the player
     */
    public static void addExpAndSp(Player player, long exp, int sp) {
        player.addExpAndSp((long) player.getStats().getValue(Stat.EXPSP_RATE, (exp * Config.RATE_QUEST_REWARD_XP)), (int) player.getStats().getValue(Stat.EXPSP_RATE, (sp * Config.RATE_QUEST_REWARD_SP)));
        PcCafePointsManager.getInstance().givePcCafePoint(player, (long) (exp * Config.RATE_QUEST_REWARD_XP));
        MagicLampData.getInstance().addLampExp(player, exp, true);
    }

    /**
     * Sends the special camera packet to the player.
     */
    public static void specialCamera(Player player, Creature creature, int force, int angle1, int angle2, int time, int range, int duration, int relYaw, int relPitch, int isWide, int relAngle) {
        player.sendPacket(new SpecialCamera(creature, force, angle1, angle2, time, range, duration, relYaw, relPitch, isWide, relAngle));
    }

    /**
     * Sends the special camera packet to the player.
     */
    public static void specialCameraEx(Player player, Creature creature, int force, int angle1, int angle2, int time, int duration, int relYaw, int relPitch, int isWide, int relAngle) {
        player.sendPacket(new SpecialCamera(creature, player, force, angle1, angle2, time, duration, relYaw, relPitch, isWide, relAngle));
    }

    /**
     * Sends the special camera packet to the player.
     */
    public static void specialCamera3(Player player, Creature creature, int force, int angle1, int angle2, int time, int range, int duration, int relYaw, int relPitch, int isWide, int relAngle, int unk) {
        player.sendPacket(new SpecialCamera(creature, force, angle1, angle2, time, range, duration, relYaw, relPitch, isWide, relAngle, unk));
    }

    public static void addRadar(Player player, ILocational loc) {
        addRadar(player, loc.getX(), loc.getY(), loc.getZ());
    }

    public static void addRadar(Player player, int x, int y, int z) {
        player.getRadar().addMarker(x, y, z);
    }
}
