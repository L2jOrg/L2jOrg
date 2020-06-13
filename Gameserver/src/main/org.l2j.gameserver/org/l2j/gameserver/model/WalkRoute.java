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
package org.l2j.gameserver.model;

import java.util.List;

/**
 * @author GKR
 */
public class WalkRoute {
    private final String _name;
    private final List<NpcWalkerNode> _nodeList; // List of nodes
    private final boolean _repeatWalk; // Does repeat walk, after arriving into last point in list, or not
    private final byte _repeatType; // Repeat style: 0 - go back, 1 - go to first point (circle style), 2 - teleport to first point (conveyor style), 3 - random walking between points
    private boolean _stopAfterCycle; // Make only one cycle or endlessly

    public WalkRoute(String name, List<NpcWalkerNode> route, boolean repeat, boolean once, byte repeatType) {
        _name = name;
        _nodeList = route;
        _repeatType = repeatType;
        _repeatWalk = (_repeatType >= 0) && (_repeatType <= 2) && repeat;
    }

    public String getName() {
        return _name;
    }

    public List<NpcWalkerNode> getNodeList() {
        return _nodeList;
    }

    public NpcWalkerNode getLastNode() {
        return _nodeList.get(_nodeList.size() - 1);
    }

    public boolean repeatWalk() {
        return _repeatWalk;
    }

    public boolean doOnce() {
        return _stopAfterCycle;
    }

    public byte getRepeatType() {
        return _repeatType;
    }

    public int getNodesCount() {
        return _nodeList.size();
    }
}
