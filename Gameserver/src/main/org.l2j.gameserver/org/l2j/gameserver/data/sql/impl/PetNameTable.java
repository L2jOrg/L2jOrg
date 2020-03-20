package org.l2j.gameserver.data.sql.impl;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.database.dao.PetDAO;
import org.l2j.gameserver.data.xml.impl.PetDataTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.commons.util.Util.isAlphaNumeric;

/**
 * TODO merge with PetDataTable
 */
public class PetNameTable {
    private static final Logger LOGGER = LoggerFactory.getLogger(PetNameTable.class);

    private PetNameTable() {
    }

    public boolean doesPetNameExist(String name, int petNpcId) {
        return getDAO(PetDAO.class).existsPetName(name, PetDataTable.getInstance().getPetItemByNpc(petNpcId));
    }

    public boolean isValidPetName(String name) {
        boolean result = true;

        if (!isAlphaNumeric(name)) {
            return result;
        }

        Pattern pattern;
        try {
            pattern = Pattern.compile(Config.PET_NAME_TEMPLATE);
        } catch (PatternSyntaxException e) // case of illegal pattern
        {
            LOGGER.warn(": Pet name pattern of config is wrong!");
            pattern = Pattern.compile(".*");
        }
        final Matcher regexp = pattern.matcher(name);
        if (!regexp.matches()) {
            result = false;
        }
        return result;
    }

    public static PetNameTable getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final PetNameTable INSTANCE = new PetNameTable();
    }
}
