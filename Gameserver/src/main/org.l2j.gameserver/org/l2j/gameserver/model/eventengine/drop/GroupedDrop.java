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
package org.l2j.gameserver.model.eventengine.drop;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.holders.ItemHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author UnAfraid
 */
public class GroupedDrop implements IEventDrop {
    private final List<EventDropGroup> _groups = new ArrayList<>();

    public List<EventDropGroup> getGroups() {
        return _groups;
    }

    public void addGroup(EventDropGroup group) {
        _groups.add(group);
    }

    @Override
    public Collection<ItemHolder> calculateDrops() {
        final List<ItemHolder> rewards = new ArrayList<>();
        for (EventDropGroup group : _groups) {
            if ((Rnd.nextDouble() * 100) < group.getChance()) {
                double totalChance = 0;
                final double random = (Rnd.nextDouble() * 100);
                for (EventDropItem item : group.getItems()) {
                    totalChance += item.getChance();
                    if (totalChance > random) {
                        final long count = Rnd.get(item.getMin(), item.getMax());
                        if (count > 0) {
                            rewards.add(new ItemHolder(item.getId(), count));
                            break;
                        }
                    }
                }
            }
        }
        return rewards;
    }
}
