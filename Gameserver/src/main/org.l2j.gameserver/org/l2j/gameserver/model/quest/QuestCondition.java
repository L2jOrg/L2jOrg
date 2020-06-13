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
package org.l2j.gameserver.model.quest;

import org.l2j.gameserver.model.KeyValuePair;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * @author UnAfraid
 */
public class QuestCondition {
    private final Predicate<Player> _condition;
    private final String _html;
    private Map<Integer, String> _perNpcDialog;

    public QuestCondition(Predicate<Player> cond, String html) {
        _condition = cond;
        _html = html;
    }

    @SafeVarargs
    public QuestCondition(Predicate<Player> cond, KeyValuePair<Integer, String>... pairs) {
        _condition = cond;
        _html = null;
        _perNpcDialog = new HashMap<>();
        Stream.of(pairs).forEach(pair -> _perNpcDialog.put(pair.getKey(), pair.getValue()));
    }

    public boolean test(Player player) {
        return _condition.test(player);
    }

    public String getHtml(Npc npc) {
        return _perNpcDialog != null ? _perNpcDialog.get(npc.getId()) : _html;
    }
}
