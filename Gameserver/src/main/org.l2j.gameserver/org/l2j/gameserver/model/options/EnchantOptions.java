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
package org.l2j.gameserver.model.options;

/**
 * @author UnAfraid
 */
public class EnchantOptions {
    private final int _level;
    private final int[] _options;

    public EnchantOptions(int level) {
        _level = level;
        _options = new int[3];
    }

    public int getLevel() {
        return _level;
    }

    public int[] getOptions() {
        return _options;
    }

    public void setOption(byte index, int option) {
        if (_options.length > index) {
            _options[index] = option;
        }
    }
}
