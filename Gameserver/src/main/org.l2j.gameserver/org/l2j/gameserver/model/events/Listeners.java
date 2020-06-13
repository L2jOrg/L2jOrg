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
package org.l2j.gameserver.model.events;

/**
 * @author UnAfraid
 */
public class Listeners {
    private static final ListenersContainer _globalContainer = new ListenersContainer();
    private static final ListenersContainer _globalNpcsContainer = new ListenersContainer();
    private static final ListenersContainer _globalMonstersContainer = new ListenersContainer();
    private static final ListenersContainer _globalPlayersContainer = new ListenersContainer();

    protected Listeners() {

    }

    /**
     * @return global listeners container
     */
    public static ListenersContainer Global() {
        return _globalContainer;
    }

    /**
     * @return global npc listeners container
     */
    public static ListenersContainer Npcs() {
        return _globalNpcsContainer;
    }

    /**
     * @return global monster listeners container
     */
    public static ListenersContainer Monsters() {
        return _globalMonstersContainer;
    }

    /**
     * @return global player listeners container
     */
    public static ListenersContainer players() {
        return _globalPlayersContainer;
    }
}
