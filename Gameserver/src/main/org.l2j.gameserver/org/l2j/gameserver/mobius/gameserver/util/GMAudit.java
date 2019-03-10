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
package org.l2j.gameserver.mobius.gameserver.util;

import com.l2jmobius.Config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Audits Game Master's actions.
 */
public class GMAudit {
    private static final Logger LOGGER = Logger.getLogger(GMAudit.class.getName());

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
        String name = com.l2jmobius.commons.util.CommonUtil.replaceIllegalCharacters(gmName);
        if (!com.l2jmobius.commons.util.CommonUtil.isValidFileName(name)) {
            name = "INVALID_GM_NAME_" + date;
        }

        final File file = new File("log/GMAudit/" + name + ".txt");
        try (FileWriter save = new FileWriter(file, true)) {
            save.write(date + ">" + gmName + ">" + action + ">" + target + ">" + params + Config.EOL);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "GMAudit for GM " + gmName + " could not be saved: ", e);
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