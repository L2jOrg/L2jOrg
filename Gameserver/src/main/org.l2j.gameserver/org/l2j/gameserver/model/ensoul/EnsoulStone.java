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
package org.l2j.gameserver.model.ensoul;

import java.util.ArrayList;
import java.util.List;

/**
 * @author UnAfraid
 */
public class EnsoulStone {
    private final int _id;
    private final int _slotType;
    private final List<Integer> _options = new ArrayList<>();

    public EnsoulStone(int id, int slotType) {
        _id = id;
        _slotType = slotType;
    }

    public int getId() {
        return _id;
    }

    public int getSlotType() {
        return _slotType;
    }

    public List<Integer> getOptions() {
        return _options;
    }

    public void addOption(int option) {
        _options.add(option);
    }
}
