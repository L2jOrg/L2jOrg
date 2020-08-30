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
package org.l2j.gameserver.model.eventengine;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.AbstractScript;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.ListenerRegisterType;
import org.l2j.gameserver.model.events.annotations.RegisterEvent;
import org.l2j.gameserver.model.events.annotations.RegisterType;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogout;
import org.l2j.gameserver.util.GameXmlReader;
import org.w3c.dom.Node;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;

/**
 * @author UnAfraid
 */
public abstract class AbstractEventManager<T extends AbstractEvent> extends AbstractScript {

    private String name;
    private Set<EventScheduler> schedulers = Collections.emptySet();
    private Set<IConditionalEventScheduler> conditionalSchedulers = Collections.emptySet();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EventScheduler getScheduler(String name) {
        return schedulers.stream().filter(scheduler -> scheduler.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public void setSchedulers(Set<EventScheduler> schedulers) {
        this.schedulers = Collections.unmodifiableSet(schedulers);
    }

    public Set<IConditionalEventScheduler> getConditionalSchedulers() {
        return conditionalSchedulers;
    }

    public void setConditionalSchedulers(Set<IConditionalEventScheduler> schedulers) {
        conditionalSchedulers = Collections.unmodifiableSet(schedulers);
    }

    public void startScheduler() {
        schedulers.forEach(EventScheduler::startScheduler);
    }

    public void stopScheduler() {
        schedulers.forEach(EventScheduler::stopScheduler);
    }

    public void startConditionalSchedulers() {
        //@formatter:off
        conditionalSchedulers.stream()
                .filter(IConditionalEventScheduler::test)
                .forEach(IConditionalEventScheduler::run);
        //@formatter:on
    }

    @RegisterEvent(EventType.ON_PLAYER_LOGOUT)
    @RegisterType(ListenerRegisterType.GLOBAL)
    public void playerLogout(OnPlayerLogout event) {
        onPlayerLogout(event.getPlayer());
    }

    protected void onPlayerLogout(Player player) {
    }

    public void config(GameXmlReader reader, Node configNode) {
    }

    @Override
    public String getScriptName() {
        return getClass().getSimpleName();
    }

    @Override
    public Path getScriptPath() {
        return null;
    }

    public abstract void onInitialized();
}
