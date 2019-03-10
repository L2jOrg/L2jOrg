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
package org.l2j.gameserver.mobius.gameserver.instancemanager;

import com.l2jmobius.Config;
import org.l2j.commons.database.DatabaseFactory;
import org.l2j.commons.util.IGameXmlReader;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.mobius.gameserver.enums.ManorMode;
import org.l2j.gameserver.mobius.gameserver.model.*;
import org.l2j.gameserver.mobius.gameserver.model.entity.Castle;
import org.l2j.gameserver.mobius.gameserver.model.interfaces.IStorable;
import org.l2j.gameserver.mobius.gameserver.model.itemcontainer.ItemContainer;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Castle manor system.
 *
 * @author malyelfik
 */
public final class CastleManorManager implements IGameXmlReader, IStorable {
    private static final Logger LOGGER = Logger.getLogger(CastleManorManager.class.getName());

    // SQL queries
    private static final String INSERT_PRODUCT = "INSERT INTO castle_manor_production VALUES (?, ?, ?, ?, ?, ?)";
    private static final String INSERT_CROP = "INSERT INTO castle_manor_procure VALUES (?, ?, ?, ?, ?, ?, ?)";
    // Seeds holder
    private static final Map<Integer, L2Seed> _seeds = new HashMap<>();
    // Manor period settings
    private final Map<Integer, List<CropProcure>> _procure = new HashMap<>();
    private final Map<Integer, List<CropProcure>> _procureNext = new HashMap<>();
    private final Map<Integer, List<SeedProduction>> _production = new HashMap<>();
    private final Map<Integer, List<SeedProduction>> _productionNext = new HashMap<>();
    // Current manor status
    private ManorMode _mode = ManorMode.APPROVED;
    // Temporary date
    private Calendar _nextModeChange = null;

    public CastleManorManager() {
        if (Config.ALLOW_MANOR) {
            load(); // Load seed data (XML)
            loadDb(); // Load castle manor data (DB)

            // Set mode and start timer
            final Calendar currentTime = Calendar.getInstance();
            final int hour = currentTime.get(Calendar.HOUR_OF_DAY);
            final int min = currentTime.get(Calendar.MINUTE);
            final int maintenanceMin = Config.ALT_MANOR_REFRESH_MIN + Config.ALT_MANOR_MAINTENANCE_MIN;

            if (((hour >= Config.ALT_MANOR_REFRESH_TIME) && (min >= maintenanceMin)) || (hour < Config.ALT_MANOR_APPROVE_TIME) || ((hour == Config.ALT_MANOR_APPROVE_TIME) && (min <= Config.ALT_MANOR_APPROVE_MIN))) {
                _mode = ManorMode.MODIFIABLE;
            } else if ((hour == Config.ALT_MANOR_REFRESH_TIME) && ((min >= Config.ALT_MANOR_REFRESH_MIN) && (min < maintenanceMin))) {
                _mode = ManorMode.MAINTENANCE;
            }

            // Schedule mode change
            scheduleModeChange();

            // Schedule autosave
            if (!Config.ALT_MANOR_SAVE_ALL_ACTIONS) {
                ThreadPoolManager.getInstance().scheduleAtFixedRate(this::storeMe, Config.ALT_MANOR_SAVE_PERIOD_RATE * 60 * 60 * 1000, Config.ALT_MANOR_SAVE_PERIOD_RATE * 60 * 60 * 1000);
            }
        } else {
            _mode = ManorMode.DISABLED;
            LOGGER.info(getClass().getSimpleName() + ": Manor system is deactivated.");
        }
    }

    // -------------------------------------------------------
    // Static methods
    // -------------------------------------------------------
    public static CastleManorManager getInstance() {
        return SingletonHolder._instance;
    }

    @Override
    public final void load() {
        parseDatapackFile("data/Seeds.xml");
        LOGGER.info(getClass().getSimpleName() + ": Loaded " + _seeds.size() + " seeds.");
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
                                _seeds.put(set.getInt("seedId"), new L2Seed(set));
                            }
                        }
                    }
                }
            }
        }
    }

    private void loadDb() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement stProduction = con.prepareStatement("SELECT * FROM castle_manor_production WHERE castle_id=?");
             PreparedStatement stProcure = con.prepareStatement("SELECT * FROM castle_manor_procure WHERE castle_id=?")) {
            for (Castle castle : CastleManager.getInstance().getCastles()) {
                final int castleId = castle.getResidenceId();

                // Clear params
                stProduction.clearParameters();
                stProcure.clearParameters();

                // Seed production
                final List<SeedProduction> pCurrent = new ArrayList<>();
                final List<SeedProduction> pNext = new ArrayList<>();
                stProduction.setInt(1, castleId);
                try (ResultSet rs = stProduction.executeQuery()) {
                    while (rs.next()) {
                        final int seedId = rs.getInt("seed_id");
                        if (_seeds.containsKey(seedId)) // Don't load unknown seeds
                        {
                            final SeedProduction sp = new SeedProduction(seedId, rs.getLong("amount"), rs.getLong("price"), rs.getInt("start_amount"));
                            if (rs.getBoolean("next_period")) {
                                pNext.add(sp);
                            } else {
                                pCurrent.add(sp);
                            }
                        } else {
                            LOGGER.warning(getClass().getSimpleName() + ": Unknown seed id: " + seedId + "!");
                        }
                    }
                }
                _production.put(castleId, pCurrent);
                _productionNext.put(castleId, pNext);

                // Seed procure
                final List<CropProcure> current = new ArrayList<>();
                final List<CropProcure> next = new ArrayList<>();
                stProcure.setInt(1, castleId);
                try (ResultSet rs = stProcure.executeQuery()) {
                    final Set<Integer> cropIds = getCropIds();
                    while (rs.next()) {
                        final int cropId = rs.getInt("crop_id");
                        if (cropIds.contains(cropId)) // Don't load unknown crops
                        {
                            final CropProcure cp = new CropProcure(cropId, rs.getLong("amount"), rs.getInt("reward_type"), rs.getLong("start_amount"), rs.getLong("price"));
                            if (rs.getBoolean("next_period")) {
                                next.add(cp);
                            } else {
                                current.add(cp);
                            }
                        } else {
                            LOGGER.warning(getClass().getSimpleName() + ": Unknown crop id: " + cropId + "!");
                        }
                    }
                }
                _procure.put(castleId, current);
                _procureNext.put(castleId, next);
            }
            LOGGER.info(getClass().getSimpleName() + ": Manor data loaded.");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Unable to load manor data! ", e);
        }
    }

    // -------------------------------------------------------
    // Manor methods
    // -------------------------------------------------------
    private void scheduleModeChange() {
        // Calculate next mode change
        _nextModeChange = Calendar.getInstance();
        _nextModeChange.set(Calendar.SECOND, 0);
        switch (_mode) {
            case MODIFIABLE: {
                _nextModeChange.set(Calendar.HOUR_OF_DAY, Config.ALT_MANOR_APPROVE_TIME);
                _nextModeChange.set(Calendar.MINUTE, Config.ALT_MANOR_APPROVE_MIN);
                if (_nextModeChange.before(Calendar.getInstance())) {
                    _nextModeChange.add(Calendar.DATE, 1);
                }
                break;
            }
            case MAINTENANCE: {
                _nextModeChange.set(Calendar.HOUR_OF_DAY, Config.ALT_MANOR_REFRESH_TIME);
                _nextModeChange.set(Calendar.MINUTE, Config.ALT_MANOR_REFRESH_MIN + Config.ALT_MANOR_MAINTENANCE_MIN);
                break;
            }
            case APPROVED: {
                _nextModeChange.set(Calendar.HOUR_OF_DAY, Config.ALT_MANOR_REFRESH_TIME);
                _nextModeChange.set(Calendar.MINUTE, Config.ALT_MANOR_REFRESH_MIN);
                break;
            }
        }
        // Schedule mode change
        ThreadPoolManager.getInstance().schedule(this::changeMode, (_nextModeChange.getTimeInMillis() - System.currentTimeMillis()));
    }

    public final void changeMode() {
        switch (_mode) {
            case APPROVED: {
                // Change mode
                _mode = ManorMode.MAINTENANCE;

                // Update manor period
                for (Castle castle : CastleManager.getInstance().getCastles()) {
                    final L2Clan owner = castle.getOwner();
                    if (owner == null) {
                        continue;
                    }

                    final int castleId = castle.getResidenceId();
                    final ItemContainer cwh = owner.getWarehouse();
                    for (CropProcure crop : _procure.get(castleId)) {
                        if (crop.getStartAmount() > 0) {
                            // Adding bought crops to clan warehouse
                            if (crop.getStartAmount() != crop.getAmount()) {
                                long count = (long) ((crop.getStartAmount() - crop.getAmount()) * 0.9);
                                if ((count < 1) && (Rnd.get(99) < 90)) {
                                    count = 1;
                                }

                                if (count > 0) {
                                    cwh.addItem("Manor", getSeedByCrop(crop.getId()).getMatureId(), count, null, null);
                                }
                            }
                            // Reserved and not used money giving back to treasury
                            if (crop.getAmount() > 0) {
                                castle.addToTreasuryNoTax(crop.getAmount() * crop.getPrice());
                            }
                        }
                    }

                    // Change next period to current and prepare next period data
                    final List<SeedProduction> _nextProduction = _productionNext.get(castleId);
                    final List<CropProcure> _nextProcure = _procureNext.get(castleId);

                    _production.put(castleId, _nextProduction);
                    _procure.put(castleId, _nextProcure);

                    if (castle.getTreasury() < getManorCost(castleId, false)) {
                        _productionNext.put(castleId, Collections.emptyList());
                        _procureNext.put(castleId, Collections.emptyList());
                    } else {
                        final List<SeedProduction> production = new ArrayList<>(_nextProduction);
                        for (SeedProduction s : production) {
                            s.setAmount(s.getStartAmount());
                        }
                        _productionNext.put(castleId, production);

                        final List<CropProcure> procure = new ArrayList<>(_nextProcure);
                        for (CropProcure cr : procure) {
                            cr.setAmount(cr.getStartAmount());
                        }
                        _procureNext.put(castleId, procure);
                    }
                }

                // Save changes
                storeMe();
                break;
            }
            case MAINTENANCE: {
                // Notify clan leader about manor mode change
                for (Castle castle : CastleManager.getInstance().getCastles()) {
                    final L2Clan owner = castle.getOwner();
                    if (owner != null) {
                        final L2ClanMember clanLeader = owner.getLeader();
                        if ((clanLeader != null) && clanLeader.isOnline()) {
                            clanLeader.getPlayerInstance().sendPacket(SystemMessageId.THE_MANOR_INFORMATION_HAS_BEEN_UPDATED);
                        }
                    }
                }
                _mode = ManorMode.MODIFIABLE;
                break;
            }
            case MODIFIABLE: {
                _mode = ManorMode.APPROVED;

                for (Castle castle : CastleManager.getInstance().getCastles()) {
                    final L2Clan owner = castle.getOwner();
                    if (owner == null) {
                        continue;
                    }

                    int slots = 0;
                    final int castleId = castle.getResidenceId();
                    final ItemContainer cwh = owner.getWarehouse();
                    for (CropProcure crop : _procureNext.get(castleId)) {
                        if ((crop.getStartAmount() > 0) && (cwh.getItemsByItemId(getSeedByCrop(crop.getId()).getMatureId()) == null)) {
                            slots++;
                        }
                    }

                    final long manorCost = getManorCost(castleId, true);
                    if (!cwh.validateCapacity(slots) && (castle.getTreasury() < manorCost)) {
                        _productionNext.get(castleId).clear();
                        _procureNext.get(castleId).clear();

                        // Notify clan leader
                        final L2ClanMember clanLeader = owner.getLeader();
                        if ((clanLeader != null) && clanLeader.isOnline()) {
                            clanLeader.getPlayerInstance().sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_FUNDS_IN_THE_CLAN_WAREHOUSE_FOR_THE_MANOR_TO_OPERATE);
                        }
                    } else {
                        castle.addToTreasuryNoTax(-manorCost);
                    }
                }

                // Store changes
                if (Config.ALT_MANOR_SAVE_ALL_ACTIONS) {
                    storeMe();
                }
                break;
            }
        }
        scheduleModeChange();
    }

    public final void setNextSeedProduction(List<SeedProduction> list, int castleId) {
        _productionNext.put(castleId, list);
        if (Config.ALT_MANOR_SAVE_ALL_ACTIONS) {
            try (Connection con = DatabaseFactory.getInstance().getConnection();
                 PreparedStatement dps = con.prepareStatement("DELETE FROM castle_manor_production WHERE castle_id = ? AND next_period = 1");
                 PreparedStatement ips = con.prepareStatement(INSERT_PRODUCT)) {
                // Delete old data
                dps.setInt(1, castleId);
                dps.executeUpdate();

                // Insert new data
                if (!list.isEmpty()) {
                    for (SeedProduction sp : list) {
                        ips.setInt(1, castleId);
                        ips.setInt(2, sp.getId());
                        ips.setLong(3, sp.getAmount());
                        ips.setLong(4, sp.getStartAmount());
                        ips.setLong(5, sp.getPrice());
                        ips.setBoolean(6, true);
                        ips.addBatch();
                    }
                    ips.executeBatch();
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, getClass().getSimpleName() + ": Unable to store manor data!", e);
            }
        }
    }

    public final void setNextCropProcure(List<CropProcure> list, int castleId) {
        _procureNext.put(castleId, list);
        if (Config.ALT_MANOR_SAVE_ALL_ACTIONS) {
            try (Connection con = DatabaseFactory.getInstance().getConnection();
                 PreparedStatement dps = con.prepareStatement("DELETE FROM castle_manor_procure WHERE castle_id = ? AND next_period = 1");
                 PreparedStatement ips = con.prepareStatement(INSERT_CROP)) {
                // Delete old data
                dps.setInt(1, castleId);
                dps.executeUpdate();

                // Insert new data
                if (!list.isEmpty()) {
                    for (CropProcure cp : list) {
                        ips.setInt(1, castleId);
                        ips.setInt(2, cp.getId());
                        ips.setLong(3, cp.getAmount());
                        ips.setLong(4, cp.getStartAmount());
                        ips.setLong(5, cp.getPrice());
                        ips.setInt(6, cp.getReward());
                        ips.setBoolean(7, true);
                        ips.addBatch();
                    }
                    ips.executeBatch();
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, getClass().getSimpleName() + ": Unable to store manor data!", e);
            }
        }
    }

    public final void updateCurrentProduction(int castleId, Collection<SeedProduction> items) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE castle_manor_production SET amount = ? WHERE castle_id = ? AND seed_id = ? AND next_period = 0")) {
            for (SeedProduction sp : items) {
                ps.setLong(1, sp.getAmount());
                ps.setInt(2, castleId);
                ps.setInt(3, sp.getId());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (Exception e) {
            LOGGER.log(Level.INFO, getClass().getSimpleName() + ": Unable to store manor data!", e);
        }
    }

    public final void updateCurrentProcure(int castleId, Collection<CropProcure> items) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE castle_manor_procure SET amount = ? WHERE castle_id = ? AND crop_id = ? AND next_period = 0")) {
            for (CropProcure sp : items) {
                ps.setLong(1, sp.getAmount());
                ps.setInt(2, castleId);
                ps.setInt(3, sp.getId());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (Exception e) {
            LOGGER.log(Level.INFO, getClass().getSimpleName() + ": Unable to store manor data!", e);
        }
    }

    public final List<SeedProduction> getSeedProduction(int castleId, boolean nextPeriod) {
        return (nextPeriod) ? _productionNext.get(castleId) : _production.get(castleId);
    }

    public final SeedProduction getSeedProduct(int castleId, int seedId, boolean nextPeriod) {
        for (SeedProduction sp : getSeedProduction(castleId, nextPeriod)) {
            if (sp.getId() == seedId) {
                return sp;
            }
        }
        return null;
    }

    public final List<CropProcure> getCropProcure(int castleId, boolean nextPeriod) {
        return (nextPeriod) ? _procureNext.get(castleId) : _procure.get(castleId);
    }

    public final CropProcure getCropProcure(int castleId, int cropId, boolean nextPeriod) {
        for (CropProcure cp : getCropProcure(castleId, nextPeriod)) {
            if (cp.getId() == cropId) {
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
            final L2Seed s = getSeed(seed.getId());
            total += (s == null) ? 1 : (s.getSeedReferencePrice() * seed.getStartAmount());
        }
        for (CropProcure crop : procure) {
            total += (crop.getPrice() * crop.getStartAmount());
        }
        return total;
    }

    @Override
    public final boolean storeMe() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ds = con.prepareStatement("DELETE FROM castle_manor_production");
             PreparedStatement is = con.prepareStatement(INSERT_PRODUCT);
             PreparedStatement dp = con.prepareStatement("DELETE FROM castle_manor_procure");
             PreparedStatement ip = con.prepareStatement(INSERT_CROP)) {
            // Delete old seeds
            ds.executeUpdate();

            // Current production
            for (Map.Entry<Integer, List<SeedProduction>> entry : _production.entrySet()) {
                for (SeedProduction sp : entry.getValue()) {
                    is.setInt(1, entry.getKey());
                    is.setInt(2, sp.getId());
                    is.setLong(3, sp.getAmount());
                    is.setLong(4, sp.getStartAmount());
                    is.setLong(5, sp.getPrice());
                    is.setBoolean(6, false);
                    is.addBatch();
                }
            }

            // Next production
            for (Map.Entry<Integer, List<SeedProduction>> entry : _productionNext.entrySet()) {
                for (SeedProduction sp : entry.getValue()) {
                    is.setInt(1, entry.getKey());
                    is.setInt(2, sp.getId());
                    is.setLong(3, sp.getAmount());
                    is.setLong(4, sp.getStartAmount());
                    is.setLong(5, sp.getPrice());
                    is.setBoolean(6, true);
                    is.addBatch();
                }
            }

            // Execute production batch
            is.executeBatch();

            // Delete old procure
            dp.executeUpdate();

            // Current procure
            for (Map.Entry<Integer, List<CropProcure>> entry : _procure.entrySet()) {
                for (CropProcure cp : entry.getValue()) {
                    ip.setInt(1, entry.getKey());
                    ip.setInt(2, cp.getId());
                    ip.setLong(3, cp.getAmount());
                    ip.setLong(4, cp.getStartAmount());
                    ip.setLong(5, cp.getPrice());
                    ip.setInt(6, cp.getReward());
                    ip.setBoolean(7, false);
                    ip.addBatch();
                }
            }

            // Next procure
            for (Map.Entry<Integer, List<CropProcure>> entry : _procureNext.entrySet()) {
                for (CropProcure cp : entry.getValue()) {
                    ip.setInt(1, entry.getKey());
                    ip.setInt(2, cp.getId());
                    ip.setLong(3, cp.getAmount());
                    ip.setLong(4, cp.getStartAmount());
                    ip.setLong(5, cp.getPrice());
                    ip.setInt(6, cp.getReward());
                    ip.setBoolean(7, true);
                    ip.addBatch();
                }
            }

            // Execute procure batch
            ip.executeBatch();

            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, getClass().getSimpleName() + ": Unable to store manor data! ", e);
            return false;
        }
    }

    public final void resetManorData(int castleId) {
        _procure.get(castleId).clear();
        _procureNext.get(castleId).clear();
        _production.get(castleId).clear();
        _productionNext.get(castleId).clear();

        if (Config.ALT_MANOR_SAVE_ALL_ACTIONS) {
            try (Connection con = DatabaseFactory.getInstance().getConnection();
                 PreparedStatement ds = con.prepareStatement("DELETE FROM castle_manor_production WHERE castle_id = ?");
                 PreparedStatement dc = con.prepareStatement("DELETE FROM castle_manor_procure WHERE castle_id = ?")) {
                // Delete seeds
                ds.setInt(1, castleId);
                ds.executeUpdate();

                // Delete procure
                dc.setInt(1, castleId);
                dc.executeUpdate();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, getClass().getSimpleName() + ": Unable to store manor data!", e);
            }
        }
    }

    public final boolean isUnderMaintenance() {
        return _mode == ManorMode.MAINTENANCE;
    }

    public final boolean isManorApproved() {
        return _mode == ManorMode.APPROVED;
    }

    public final boolean isModifiablePeriod() {
        return _mode == ManorMode.MODIFIABLE;
    }

    public final String getCurrentModeName() {
        return _mode.toString();
    }

    public final String getNextModeChange() {
        return new SimpleDateFormat("dd/MM HH:mm:ss").format(_nextModeChange.getTime());
    }

    // -------------------------------------------------------
    // Seed methods
    // -------------------------------------------------------
    public final List<L2Seed> getCrops() {
        final List<L2Seed> seeds = new ArrayList<>();
        final List<Integer> cropIds = new ArrayList<>();
        for (L2Seed seed : _seeds.values()) {
            if (!cropIds.contains(seed.getCropId())) {
                seeds.add(seed);
                cropIds.add(seed.getCropId());
            }
        }
        cropIds.clear();
        return seeds;
    }

    public final Set<L2Seed> getSeedsForCastle(int castleId) {
        return _seeds.values().stream().filter(s -> s.getCastleId() == castleId).collect(Collectors.toSet());
    }

    public final Set<Integer> getSeedIds() {
        return _seeds.keySet();
    }

    public final Set<Integer> getCropIds() {
        return _seeds.values().stream().map(L2Seed::getCropId).collect(Collectors.toSet());
    }

    public final L2Seed getSeed(int seedId) {
        return _seeds.get(seedId);
    }

    public final L2Seed getSeedByCrop(int cropId, int castleId) {
        for (L2Seed s : getSeedsForCastle(castleId)) {
            if (s.getCropId() == cropId) {
                return s;
            }
        }
        return null;
    }

    public final L2Seed getSeedByCrop(int cropId) {
        for (L2Seed s : _seeds.values()) {
            if (s.getCropId() == cropId) {
                return s;
            }
        }
        return null;
    }

    private static class SingletonHolder {
        protected static final CastleManorManager _instance = new CastleManorManager();
    }
}
