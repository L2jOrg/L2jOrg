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
package org.l2j.gameserver.model.options;

import org.l2j.commons.util.Rnd;

import java.util.Map;

/**
 * @author Pere
 */
public final class OptionDataCategory {
    private final Map<Options, Double> _options;
    private final double _chance;

    public OptionDataCategory(Map<Options, Double> options, double chance) {
        _options = options;
        _chance = chance;
    }

    Options getRandomOptions() {
        Options result = null;
        do {
            double random = Rnd.nextDouble() * 100.0;
            for (Map.Entry<Options, Double> entry : _options.entrySet()) {
                if (entry.getValue() >= random) {
                    result = entry.getKey();
                    break;
                }

                random -= entry.getValue();
            }
        }
        while (result == null);

        return result;
    }

    public double getChance() {
        return _chance;
    }
}