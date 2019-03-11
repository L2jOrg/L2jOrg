package org.l2j.gameserver.data.sql.impl;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.impl.PetDataTable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class PetNameTable {
    private static Logger LOGGER = Logger.getLogger(PetNameTable.class.getName());

    public static PetNameTable getInstance() {
        return SingletonHolder._instance;
    }

    public boolean doesPetNameExist(String name, int petNpcId) {
        boolean result = true;
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT name FROM pets p, items i WHERE p.item_obj_id = i.object_id AND name=? AND i.item_id IN (?)")) {
            ps.setString(1, name);
            final StringBuilder cond = new StringBuilder();
            if (!cond.toString().isEmpty()) {
                cond.append(", ");
            }

            cond.append(PetDataTable.getInstance().getPetItemsByNpc(petNpcId));
            ps.setString(2, cond.toString());
            try (ResultSet rs = ps.executeQuery()) {
                result = rs.next();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Could not check existing petname:" + e.getMessage(), e);
        }
        return result;
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
            LOGGER.warning(getClass().getSimpleName() + ": Pet name pattern of config is wrong!");
            pattern = Pattern.compile(".*");
        }
        final Matcher regexp = pattern.matcher(name);
        if (!regexp.matches()) {
            result = false;
        }
        return result;
    }

    private boolean isAlphaNumeric(String text) {
        boolean result = true;
        final char[] chars = text.toCharArray();
        for (char aChar : chars) {
            if (!Character.isLetterOrDigit(aChar)) {
                result = false;
                break;
            }
        }
        return result;
    }

    private static class SingletonHolder {
        protected static final PetNameTable _instance = new PetNameTable();
    }
}
