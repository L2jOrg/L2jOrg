/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.mobius.gameserver.model.events.returns;

import org.l2j.gameserver.mobius.gameserver.enums.ChatType;

/**
 * @author UnAfraid
 */
public class ChatFilterReturn extends AbstractEventReturn {
    private final String _filteredText;
    private final ChatType _chatType;

    public ChatFilterReturn(String filteredText, ChatType newChatType, boolean override, boolean abort) {
        super(override, abort);
        _filteredText = filteredText;
        _chatType = newChatType;
    }

    public String getFilteredText() {
        return _filteredText;
    }

    public ChatType getChatType() {
        return _chatType;
    }
}
