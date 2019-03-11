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
package org.l2j.gameserver.model.holders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Mobius
 */
public class FakePlayerChatHolder {
    private final String _fpcName;
    private final String _searchMethod;
    private final List<String> _searchText;
    private final List<String> _answers;

    public FakePlayerChatHolder(String fpcName, String searchMethod, String searchText, String answers) {
        _fpcName = fpcName;
        _searchMethod = searchMethod;
        _searchText = new ArrayList<>(Arrays.asList(searchText.split(";")));
        _answers = new ArrayList<>(Arrays.asList(answers.split(";")));
    }

    public String getFpcName() {
        return _fpcName;
    }

    public String getSearchMethod() {
        return _searchMethod;
    }

    public List<String> getSearchText() {
        return _searchText;
    }

    public List<String> getAnswers() {
        return _answers;
    }
}
