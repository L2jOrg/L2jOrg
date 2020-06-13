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
package org.l2j.gameserver.data.sql.impl;

import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.data.database.dao.ClanDAO;
import org.l2j.gameserver.data.database.data.CrestData;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.util.PrimeFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.BitSet;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Objects.nonNull;
import static org.l2j.commons.database.DatabaseAccess.getDAO;

/**
 * Loads and saves crests from database.
 *
 * @author NosBit
 * @author JoeAlisson
 */
public final class CrestTable {
    private static final Logger LOGGER = LoggerFactory.getLogger(CrestTable.class);
    private static final int INITIAL_CREST_SIZE = 100;

    private final AtomicInteger nextId = new AtomicInteger(1);

    private IntMap<CrestData> crests;
    private  BitSet crestIds = new BitSet(PrimeFinder.nextPrime(INITIAL_CREST_SIZE));

    private CrestTable() {
        load();
    }

    public synchronized void load() {
        var clanDAO = getDAO(ClanDAO.class);
        clanDAO.removeUnusedCrests();
        crests = clanDAO.findAllCrests();
        crests.keySet().forEach(crestIds::set);
        nextId.set(crestIds.nextClearBit(1));
        LOGGER.info("Loaded {} Crests.", crests.size());
    }

    public CrestData getCrest(int crestId) {
        return crests.get(crestId);
    }

    public CrestData createCrest(byte[] data, CrestData.CrestType crestType) {
        var crest = new CrestData(getNextId(), data, crestType);
        getDAO(ClanDAO.class).save(crest);
        crests.put(crest.getId(), crest);
        return crest;
    }

    private synchronized int getNextId() {
        var id = nextId.get();
        var next = crestIds.nextClearBit(id+1);
        if(next < 0) {
            next = crestIds.nextClearBit(1);
        }
        if(next < 0) {
            increaseCrests();
            next = crestIds.nextClearBit(id);
        }
        nextId.set(next);
        return id;
    }

    private void increaseCrests() {
        var expanded = new BitSet((int) (crestIds.size() * 1.5));
        expanded.or(crestIds);
        crestIds = expanded;
    }

    public void removeCrest(int crestId) {
        if(nonNull(crests.remove(crestId))) {
            getDAO(ClanDAO.class).deleteCrest(crestId);
        }
    }

    public void removeCrests(Clan clan) {
        removeCrest(clan.getCrestId());
        removeCrest(clan.getCrestLargeId());
    }

    public static CrestTable getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final CrestTable INSTANCE = new CrestTable();
    }
}
