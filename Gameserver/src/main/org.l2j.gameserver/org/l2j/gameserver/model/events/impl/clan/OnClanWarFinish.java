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
package org.l2j.gameserver.model.events.impl.clan;

import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

/**
 * @author UnAfraid
 */
public class OnClanWarFinish implements IBaseEvent {
    private final Clan _clan1;
    private final Clan _clan2;

    public OnClanWarFinish(Clan clan1, Clan clan2) {
        _clan1 = clan1;
        _clan2 = clan2;
    }

    public Clan getClan1() {
        return _clan1;
    }

    public Clan getClan2() {
        return _clan2;
    }

    @Override
    public EventType getType() {
        return EventType.ON_CLAN_WAR_FINISH;
    }
}
