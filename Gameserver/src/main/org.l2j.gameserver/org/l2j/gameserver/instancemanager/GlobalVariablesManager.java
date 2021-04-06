/*
 * Copyright © 2019-2021 L2JOrg
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
package org.l2j.gameserver.instancemanager;

import org.l2j.gameserver.data.database.dao.GlobalVariablesDAO;
import org.l2j.gameserver.model.variables.AbstractVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.l2j.commons.database.DatabaseAccess.getDAO;

/**
 * Global Variables Manager.
 *
 * @author xban1x
 */
public final class GlobalVariablesManager extends AbstractVariables {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalVariablesManager.class);

    private GlobalVariablesManager() {
    }

    @Override
    public boolean restoreMe() {
        merge(getDAO(GlobalVariablesDAO.class).findAll());
        LOGGER.info("Loaded {} variables", getSet().size());
        return true;
    }

    @Override
    public boolean storeMe() {
        // No changes, nothing to store.
        if (!hasChanges()) {
            return false;
        }

        deleteMe();
        getDAO(GlobalVariablesDAO.class).save(this);
        LOGGER.info("Stored {} variables", getSet().size());
        return true;
    }

    @Override
    public boolean deleteMe() {
        return getDAO(GlobalVariablesDAO.class).deleteAll();
    }

    public static void init() {
        getInstance().restoreMe();
    }

    public static GlobalVariablesManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final GlobalVariablesManager INSTANCE = new GlobalVariablesManager();
    }
}