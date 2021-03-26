/*
 * Copyright © 2019 L2J Mobius
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
package org.l2j.gameserver.model.holders;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.interfaces.IIdentifiable;

/**
 * This class hold info needed for minions spawns<br>
 *
 * @author Zealar
 */
public class MinionHolder implements IIdentifiable {
    private final int _id;
    private final int _count;

    public MinionHolder(StatsSet set) {
        _id = set.getInt("id");
        _count = set.getInt("count", 1);
    }

    /**
     * Constructs a minion holder.
     *
     * @param id          the id
     * @param count       the count
     */
    public MinionHolder(int id, int count) {
        _id = id;
        _count = count;
    }

    /**
     * @return the Identifier of the Minion to spawn.
     */
    @Override
    public int getId() {
        return _id;
    }

    /**
     * @return the count of the Minions to spawn.
     */
    public int getCount() {
        return _count;
    }

}
