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
package org.l2j.gameserver.model.base;

import java.util.regex.Matcher;

/**
 * This class will hold the information of the player classes.
 *
 * @author Zoey76
 */
public final class ClassInfo {
    private final ClassId _classId;
    private final String _className;
    private final ClassId _parentClassId;

    /**
     * Constructor for ClassInfo.
     *
     * @param classId       the class Id.
     * @param className     the in game class name.
     * @param parentClassId the parent class for the given {@code classId}.
     */
    public ClassInfo(ClassId classId, String className, ClassId parentClassId) {
        _classId = classId;
        _className = className;
        _parentClassId = parentClassId;
    }

    /**
     * @return the class Id.
     */
    public ClassId getClassId() {
        return _classId;
    }

    /**
     * @return the hardcoded in-game class name.
     */
    public String getClassName() {
        return _className;
    }

    /**
     * @return the class client Id.
     */
    private int getClassClientId() {
        int classClientId = _classId.getId();
        if ((classClientId >= 0) && (classClientId <= 57)) {
            classClientId += 247;
        } else if ((classClientId >= 88) && (classClientId <= 118)) {
            classClientId += 1071;
        } else if ((classClientId >= 123) && (classClientId <= 136)) {
            classClientId += 1438;
        } else if ((classClientId >= 139) && (classClientId <= 146)) {
            classClientId += 2338;
        } else if ((classClientId >= 148) && (classClientId <= 181)) {
            classClientId += 2884;
        } else if ((classClientId >= 182) && (classClientId <= 189)) {
            classClientId += 3121;
        }

        return classClientId;
    }

    /**
     * @return the class client Id formatted to be displayed on a HTML.
     */
    public String getClientCode() {
        return "&$" + getClassClientId() + ";";
    }

    /**
     * @return the escaped class client Id formatted to be displayed on a HTML.
     */
    public String getEscapedClientCode() {
        return Matcher.quoteReplacement(getClientCode());
    }

    /**
     * @return the parent class Id.
     */
    public ClassId getParentClassId() {
        return _parentClassId;
    }
}
