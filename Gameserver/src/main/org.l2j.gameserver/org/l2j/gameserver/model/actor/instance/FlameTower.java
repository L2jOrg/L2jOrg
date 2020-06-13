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
package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Tower;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneManager;

import java.util.List;

/**
 * Class for Flame Control Tower instance.
 *
 * @author JIV
 */
public class FlameTower extends Tower {
    private int _upgradeLevel = 0;
    private List<Integer> _zoneList;

    public FlameTower(NpcTemplate template) {
        super(template);
        setInstanceType(InstanceType.L2FlameTowerInstance);
    }

    @Override
    public boolean doDie(Creature killer) {
        enableZones(false);
        return super.doDie(killer);
    }

    @Override
    public boolean deleteMe() {
        enableZones(false);
        return super.deleteMe();
    }

    public final void enableZones(boolean state) {
        if ((_zoneList != null) && (_upgradeLevel != 0)) {
            final int maxIndex = _upgradeLevel * 2;
            for (int i = 0; i < maxIndex; i++) {
                final Zone zone = ZoneManager.getInstance().getZoneById(_zoneList.get(i));
                if (zone != null) {
                    zone.setEnabled(state);
                }
            }
        }
    }

    public final void setUpgradeLevel(int level) {
        _upgradeLevel = level;
    }

    public final void setZoneList(List<Integer> list) {
        _zoneList = list;
        enableZones(true);
    }
}