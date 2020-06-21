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
package handlers.mission;

import org.l2j.commons.util.Util;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.engine.mission.AbstractMissionHandler;
import org.l2j.gameserver.engine.mission.MissionDataHolder;
import org.l2j.gameserver.engine.mission.MissionHandlerFactory;
import org.l2j.gameserver.engine.mission.MissionStatus;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.Listeners;
import org.l2j.gameserver.model.events.impl.character.npc.OnAttackableKill;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.util.MathUtil;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

/**
 * @author JoeAlisson
 */
public class HuntMissionHandler extends AbstractMissionHandler {

    private final int requiredLevel;
    private final int maxLevel;
    private final List<Integer> monsters;
    private final int classLevel;

    private HuntMissionHandler(MissionDataHolder holder) {
        super(holder);
        requiredLevel = holder.getParams().getInt("minLevel", 0);
        maxLevel = holder.getParams().getInt("maxLevel", Byte.MAX_VALUE);
        classLevel = holder.getParams().getInt("classLevel", 0);
        final String monsters = holder.getParams().getString("monsters", "");
        this.monsters = Arrays.stream(monsters.split(" ")).filter(Util::isInteger).map(Integer::parseInt).collect(Collectors.toList());
    }

    @Override
    public void init() {
        Listeners.Monsters().addListener(new ConsumerEventListener(this, EventType.ON_ATTACKABLE_KILL, (Consumer<OnAttackableKill>) this::onKill, this));
    }

    private void onKill(OnAttackableKill event) {
        var monster = event.getTarget();
        if (!monsters.isEmpty() && !monsters.contains(monster.getId())) {
            return;
        }

        final var player = event.getAttacker();

        if(player.getLevel() < requiredLevel || player.getLevel() > maxLevel || (player.getLevel() - monster.getLevel() > 5) || player.getClassId().level() < classLevel) {
            return;
        }

        final var party = player.getParty();
        if (isNull(party)) {
            onKillProgress(player);
        } else {
            var channel = party.getCommandChannel();
            final List<Player> members = isNull(channel) ? party.getMembers() : channel.getMembers();
            members.stream().filter(member -> MathUtil.isInsideRadius3D(member, monster,  Config.ALT_PARTY_RANGE)).forEach(this::onKillProgress);
        }
    }

    private void onKillProgress(Player player)
    {
        final var entry = getPlayerEntry(player, true);
        if (entry.getStatus() == MissionStatus.NOT_AVAILABLE)
        {
            if (entry.increaseProgress() >= getRequiredCompletion())
            {
                entry.setStatus(MissionStatus.AVAILABLE);
                notifyAvailablesReward(player);
            }
            storePlayerEntry(entry);
        }
    }

    public static class Factory implements MissionHandlerFactory {

        @Override
        public AbstractMissionHandler create(MissionDataHolder data) {
            return new HuntMissionHandler(data);
        }

        @Override
        public String handlerName() {
            return "hunt";
        }
    }
}
