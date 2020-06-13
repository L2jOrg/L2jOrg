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
package org.l2j.gameserver.instancemanager;

import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import io.github.joealisson.primitive.IntSet;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.commons.util.Rnd;
import org.l2j.commons.util.StreamUtil;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.database.dao.ManorProcureDAO;
import org.l2j.gameserver.data.database.dao.ManorProductionDAO;
import org.l2j.gameserver.data.database.data.CropProcure;
import org.l2j.gameserver.data.database.data.SeedProduction;
import org.l2j.gameserver.enums.ManorMode;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.ClanMember;
import org.l2j.gameserver.model.Seed;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.interfaces.IStorable;
import org.l2j.gameserver.model.item.container.ItemContainer;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.commons.database.DatabaseAccess.getDAO;

/**
 * Castle manor system.
 *
 * @author malyelfik
 * @author JoeAlisson
 */
public final class CastleManorManager extends GameXmlReader implements IStorable {
    private static final Logger LOGGER = LoggerFactory.getLogger(CastleManorManager.class);

    private final IntMap<Seed> seeds = new HashIntMap<>();

    private final IntMap<List<CropProcure>> procures = new HashIntMap<>();
    private final IntMap<List<CropProcure>> procuresNext = new HashIntMap<>();
    private final IntMap<List<SeedProduction>> productions = new HashIntMap<>();
    private final IntMap<List<SeedProduction>> productionsNext = new HashIntMap<>();

    private ManorMode mode = ManorMode.APPROVED;
    private Calendar nextModeChange = null;

    private CastleManorManager() {
        if (Config.ALLOW_MANOR) {
            load(); // Load seed data (XML)
            loadDb(); // Load castle manor data (DB)

            // Set mode and start timer
            final Calendar currentTime = Calendar.getInstance();
            final int hour = currentTime.get(Calendar.HOUR_OF_DAY);
            final int min = currentTime.get(Calendar.MINUTE);
            final int maintenanceMin = Config.ALT_MANOR_REFRESH_MIN + Config.ALT_MANOR_MAINTENANCE_MIN;

            if (((hour >= Config.ALT_MANOR_REFRESH_TIME) && (min >= maintenanceMin)) || (hour < Config.ALT_MANOR_APPROVE_TIME) || ((hour == Config.ALT_MANOR_APPROVE_TIME) && (min <= Config.ALT_MANOR_APPROVE_MIN))) {
                mode = ManorMode.MODIFIABLE;
            } else if (hour == Config.ALT_MANOR_REFRESH_TIME && min >= Config.ALT_MANOR_REFRESH_MIN) {
                mode = ManorMode.MAINTENANCE;
            }

            // Schedule mode change
            scheduleModeChange();

            // Schedule autosave
            ThreadPool.scheduleAtFixedRate(this::storeMe, Config.ALT_MANOR_SAVE_PERIOD_RATE * 60 * 60 * 1000, Config.ALT_MANOR_SAVE_PERIOD_RATE * 60 * 60 * 1000);
        } else {
            mode = ManorMode.DISABLED;
            LOGGER.info("Manor system is deactivated.");
        }
    }

    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/Seeds.xsd");
    }

    @Override
    public final void load() {
        parseDatapackFile("data/Seeds.xml");
        LOGGER.info("Loaded {} seeds.", seeds.size());
        releaseResources();
    }

    @Override
    public final void parseDocument(Document doc, File f) {
        StatsSet set;
        NamedNodeMap attrs;
        Node att;
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("list".equalsIgnoreCase(n.getNodeName())) {
                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                    if ("castle".equalsIgnoreCase(d.getNodeName())) {
                        final int castleId = parseInteger(d.getAttributes(), "id");
                        for (Node c = d.getFirstChild(); c != null; c = c.getNextSibling()) {
                            if ("crop".equalsIgnoreCase(c.getNodeName())) {
                                set = new StatsSet();
                                set.set("castleId", castleId);

                                attrs = c.getAttributes();
                                for (int i = 0; i < attrs.getLength(); i++) {
                                    att = attrs.item(i);
                                    set.set(att.getNodeName(), att.getNodeValue());
                                }
                                seeds.put(set.getInt("seedId"), new Seed(set));
                            }
                        }
                    }
                }
            }
        }
    }

    private void loadDb() {
        for (Castle castle : CastleManager.getInstance().getCastles()) {
            loadProductions(castle);
            loadProcures(castle);
        }
    }

    private void loadProcures(Castle castle) {
        var cropIds = getCropIds();
        var partition = getDAO(ManorProcureDAO.class).findManorProcureByCastle(castle.getId()).stream()
                .filter(procure -> cropIds.contains(procure.getSeedId()))
                .collect(Collectors.partitioningBy(SeedProduction::isNextPeriod));

        procures.put(castle.getId(), partition.get(false));
        procuresNext.put(castle.getId(), partition.get(true));
    }

    private void loadProductions(Castle castle) {
        var partition = getDAO(ManorProductionDAO.class).findManorProductionByCastle(castle.getId()).stream()
            .filter(production -> seeds.containsKey(production.getSeedId()))
            .collect(Collectors.partitioningBy(SeedProduction::isNextPeriod));

        productions.put(castle.getId(), partition.get(false));
        productionsNext.put(castle.getId(), partition.get(true));
    }

    private void scheduleModeChange() {
        nextModeChange = Calendar.getInstance();
        nextModeChange.set(Calendar.SECOND, 0);
        switch (mode) {
            case MODIFIABLE -> {
                nextModeChange.set(Calendar.HOUR_OF_DAY, Config.ALT_MANOR_APPROVE_TIME);
                nextModeChange.set(Calendar.MINUTE, Config.ALT_MANOR_APPROVE_MIN);
                if (nextModeChange.before(Calendar.getInstance())) {
                    nextModeChange.add(Calendar.DATE, 1);
                }
            }
            case MAINTENANCE -> {
                nextModeChange.set(Calendar.HOUR_OF_DAY, Config.ALT_MANOR_REFRESH_TIME);
                nextModeChange.set(Calendar.MINUTE, Config.ALT_MANOR_REFRESH_MIN + Config.ALT_MANOR_MAINTENANCE_MIN);
            }
            case APPROVED -> {
                nextModeChange.set(Calendar.HOUR_OF_DAY, Config.ALT_MANOR_REFRESH_TIME);
                nextModeChange.set(Calendar.MINUTE, Config.ALT_MANOR_REFRESH_MIN);
            }
        }
        // Schedule mode change
        ThreadPool.schedule(this::changeMode, (nextModeChange.getTimeInMillis() - System.currentTimeMillis()));
    }
    public final void changeMode() {
        switch (mode) {
            case APPROVED: {
                // Change mode
                mode = ManorMode.MAINTENANCE;

                // Update manor period
                for (Castle castle : CastleManager.getInstance().getCastles()) {
                    final Clan owner = castle.getOwner();
                    if (owner == null) {
                        continue;
                    }

                    final int castleId = castle.getId();
                    final ItemContainer cwh = owner.getWarehouse();
                    for (CropProcure crop : procures.get(castleId)) {
                        if (crop.getStartAmount() > 0) {
                            // Adding bought crops to clan warehouse
                            if (crop.getStartAmount() != crop.getAmount()) {
                                long count = (long) ((crop.getStartAmount() - crop.getAmount()) * 0.9);
                                if ((count < 1) && (Rnd.get(99) < 90)) {
                                    count = 1;
                                }

                                if (count > 0) {
                                    cwh.addItem("Manor", getSeedByCrop(crop.getSeedId()).getMatureId(), count, null, null);
                                }
                            }
                            // Reserved and not used money giving back to treasury
                            if (crop.getAmount() > 0) {
                                castle.addToTreasuryNoTax(crop.getAmount() * crop.getPrice());
                            }
                        }
                    }

                    // Change next period to current and prepare next period data
                    final List<SeedProduction> _nextProduction = productionsNext.get(castleId);
                    final List<CropProcure> _nextProcure = procuresNext.get(castleId);

                    productions.put(castleId, _nextProduction);
                    procures.put(castleId, _nextProcure);

                    if (castle.getTreasury() < getManorCost(castleId, false)) {
                        productionsNext.put(castleId, Collections.emptyList());
                        procuresNext.put(castleId, Collections.emptyList());
                    } else {
                        final List<SeedProduction> production = new ArrayList<>(_nextProduction);
                        for (SeedProduction s : production) {
                            s.setAmount(s.getStartAmount());
                        }
                        productionsNext.put(castleId, production);

                        final List<CropProcure> procure = new ArrayList<>(_nextProcure);
                        for (CropProcure cr : procure) {
                            cr.setAmount(cr.getStartAmount());
                        }
                        procuresNext.put(castleId, procure);
                    }
                }

                // Save changes
                storeMe();
                break;
            }
            case MAINTENANCE: {
                // Notify clan leader about manor mode change
                for (Castle castle : CastleManager.getInstance().getCastles()) {
                    final Clan owner = castle.getOwner();
                    if (owner != null) {
                        final ClanMember clanLeader = owner.getLeader();
                        if ((clanLeader != null) && clanLeader.isOnline()) {
                            clanLeader.getPlayerInstance().sendPacket(SystemMessageId.THE_MANOR_INFORMATION_HAS_BEEN_UPDATED);
                        }
                    }
                }
                mode = ManorMode.MODIFIABLE;
                break;
            }
            case MODIFIABLE: {
                mode = ManorMode.APPROVED;

                for (Castle castle : CastleManager.getInstance().getCastles()) {
                    final Clan owner = castle.getOwner();
                    if (owner == null) {
                        continue;
                    }

                    int slots = 0;
                    final int castleId = castle.getId();
                    final ItemContainer cwh = owner.getWarehouse();
                    for (CropProcure crop : procuresNext.get(castleId)) {
                        if ((crop.getStartAmount() > 0) && (cwh.getItemsByItemId(getSeedByCrop(crop.getSeedId()).getMatureId()) == null)) {
                            slots++;
                        }
                    }

                    final long manorCost = getManorCost(castleId, true);
                    if (!cwh.validateCapacity(slots) && (castle.getTreasury() < manorCost)) {
                        productionsNext.get(castleId).clear();
                        procuresNext.get(castleId).clear();

                        // Notify clan leader
                        final ClanMember clanLeader = owner.getLeader();
                        if ((clanLeader != null) && clanLeader.isOnline()) {
                            clanLeader.getPlayerInstance().sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_FUNDS_IN_THE_CLAN_WAREHOUSE_FOR_THE_MANOR_TO_OPERATE);
                        }
                    } else {
                        castle.addToTreasuryNoTax(-manorCost);
                    }
                }
                break;
            }
        }
        scheduleModeChange();
    }

    public final void setNextSeedProduction(List<SeedProduction> list, int castleId) {
        productionsNext.put(castleId, list);
    }

    public final void setNextCropProcure(List<CropProcure> list, int castleId) {
        procuresNext.put(castleId, list);
    }

    public final List<SeedProduction> getSeedProduction(int castleId, boolean nextPeriod) {
        return (nextPeriod) ? productionsNext.get(castleId) : productions.get(castleId);
    }

    public final SeedProduction getSeedProduct(int castleId, int seedId, boolean nextPeriod) {
        for (SeedProduction sp : getSeedProduction(castleId, nextPeriod)) {
            if (sp.getSeedId() == seedId) {
                return sp;
            }
        }
        return null;
    }

    public final List<CropProcure> getCropProcure(int castleId, boolean nextPeriod) {
        return (nextPeriod) ? procuresNext.get(castleId) : procures.get(castleId);
    }

    public final CropProcure getCropProcure(int castleId, int cropId, boolean nextPeriod) {
        for (CropProcure cp : getCropProcure(castleId, nextPeriod)) {
            if (cp.getSeedId() == cropId) {
                return cp;
            }
        }
        return null;
    }

    public final long getManorCost(int castleId, boolean nextPeriod) {
        final List<CropProcure> procure = getCropProcure(castleId, nextPeriod);
        final List<SeedProduction> production = getSeedProduction(castleId, nextPeriod);

        long total = 0;
        for (SeedProduction seed : production) {
            final Seed s = getSeed(seed.getSeedId());
            total += (s == null) ? 1 : (s.getSeedReferencePrice() * seed.getStartAmount());
        }
        for (CropProcure crop : procure) {
            total += (crop.getPrice() * crop.getStartAmount());
        }
        return total;
    }

    @Override
    public final boolean storeMe() {
        var manorProduceDAO = getDAO(ManorProductionDAO.class);
        manorProduceDAO.deleteManorProduction();
        manorProduceDAO.save(Stream.concat(productions.values().stream(), productionsNext.values().stream()).flatMap(Collection::stream).collect(Collectors.toList()));

        var manorProcureDAO = getDAO(ManorProcureDAO.class);
        manorProcureDAO.deleteManorProcure();
        manorProcureDAO.save(Stream.concat(procures.values().stream(), procuresNext.values().stream()).flatMap(Collection::stream).collect(Collectors.toList()));
        return true;
    }

    public final void resetManorData(int castleId) {
        if (!Config.ALLOW_MANOR) {
            return;
        }

        procures.get(castleId).clear();
        procuresNext.get(castleId).clear();
        productions.get(castleId).clear();
        productionsNext.get(castleId).clear();
    }

    public final boolean isUnderMaintenance() {
        return mode == ManorMode.MAINTENANCE;
    }

    public final boolean isManorApproved() {
        return mode == ManorMode.APPROVED;
    }

    public final boolean isModifiablePeriod() {
        return mode == ManorMode.MODIFIABLE;
    }

    public final String getCurrentModeName() {
        return mode.toString();
    }

    public final String getNextModeChange() {
        return new SimpleDateFormat("dd/MM HH:mm:ss").format(nextModeChange.getTime());
    }

    public final List<Seed> getCrops() {
        final List<Seed> seeds = new ArrayList<>();
        final List<Integer> cropIds = new ArrayList<>();
        for (Seed seed : this.seeds.values()) {
            if (!cropIds.contains(seed.getCropId())) {
                seeds.add(seed);
                cropIds.add(seed.getCropId());
            }
        }
        cropIds.clear();
        return seeds;
    }
    public final Set<Seed> getSeedsForCastle(int castleId) {
        return seeds.values().stream().filter(s -> s.getCastleId() == castleId).collect(Collectors.toSet());
    }

    public final IntSet getCropIds() {
        return StreamUtil.collectToSet(seeds.values().stream().mapToInt(Seed::getCropId));
    }

    public final Seed getSeed(int seedId) {
        return seeds.get(seedId);
    }

    public final Seed getSeedByCrop(int cropId, int castleId) {
        for (Seed s : getSeedsForCastle(castleId)) {
            if (s.getCropId() == cropId) {
                return s;
            }
        }
        return null;
    }

    public final Seed getSeedByCrop(int cropId) {
        for (Seed s : seeds.values()) {
            if (s.getCropId() == cropId) {
                return s;
            }
        }
        return null;
    }

    public static CastleManorManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final CastleManorManager INSTANCE = new CastleManorManager();
    }
}
