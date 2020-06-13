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
package org.l2j.gameserver.util;

import org.l2j.commons.util.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Audits Game Master's actions.
 */
public class GMAudit {
    private static final Logger LOGGER = LoggerFactory.getLogger(GMAudit.class);

    static {
        new File("log/GMAudit").mkdirs();
    }

    /**
     * Logs a Game Master's action into a file.
     *
     * @param gmName the Game Master's name
     * @param action the performed action
     * @param target the target's name
     * @param params the parameters
     */
    public static void auditGMAction(String gmName, String action, String target, String params) {
        final SimpleDateFormat _formatter = new SimpleDateFormat("dd/MM/yyyy H:mm:ss");
        final String date = _formatter.format(new Date());
        String name = CommonUtil.replaceIllegalCharacters(gmName);
        if (!CommonUtil.isValidFileName(name)) {
            name = "INVALID_GM_NAME_" + date;
        }

        final File file = new File("log/GMAudit/" + name + ".txt");
        try (FileWriter save = new FileWriter(file, true)) {
            save.write(date + ">" + gmName + ">" + action + ">" + target + ">" + params + System.lineSeparator());
        } catch (IOException e) {
            LOGGER.error("GMAudit for GM " + gmName + " could not be saved: ", e);
        }
    }

    /**
     * Wrapper method.
     *
     * @param gmName the Game Master's name
     * @param action the performed action
     * @param target the target's name
     */
    public static void auditGMAction(String gmName, String action, String target) {
        auditGMAction(gmName, action, target, "");
    }
}