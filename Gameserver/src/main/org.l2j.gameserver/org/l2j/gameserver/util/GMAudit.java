package org.l2j.gameserver.util;

import org.l2j.commons.util.CommonUtil;
import org.l2j.gameserver.Config;
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