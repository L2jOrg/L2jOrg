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
package org.l2j.gameserver.engine.clan.clanhall;

import io.github.joealisson.primitive.IntList;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.data.database.dao.ClanHallDAO;
import org.l2j.gameserver.engine.clan.ClanEngine;
import org.l2j.gameserver.enums.ClanHallGrade;
import org.l2j.gameserver.enums.ClanHallType;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Door;
import org.l2j.gameserver.model.item.CommonItem;
import org.l2j.gameserver.model.residences.AbstractResidence;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.pledge.PledgeShowInfoUpdate;
import org.l2j.gameserver.world.zone.ZoneEngine;
import org.l2j.gameserver.world.zone.type.ClanHallZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.nonNull;
import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.commons.util.Util.zeroIfNullOrElse;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;

/**
 * @author St3eT
 * @author JoeAlisson
 */
public final class ClanHall extends AbstractResidence {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClanHall.class);

    private long lease;
    private final ClanHallGrade grade;
    private final ClanHallType type;
    private long minBid;
    private IntList npcs;
    private final List<Door> doors = new ArrayList<>();
    private Location restartLocation;
    private Location banishPoint;
    protected ScheduledFuture<?> checkPaymentTask = null;

    Clan owner = null;
    long paidUntil = 0;

    public ClanHall(int id, String name, ClanHallGrade grade, ClanHallType type) {
        super(id);
        setName(name);
        this.grade = grade;
        this.type = type;
    }

    void init() {
        load();
        initResidenceZone();
        initFunctions();
    }

    private void load() {
        var clanHallDao = getDAO(ClanHallDAO.class);
        clanHallDao.findById(getId(), result -> {
            try {
                if(result.next()) {
                    paidUntil = result.getLong("paid_until");
                    setOwner(result.getInt("owner_id"));
                } else {
                    clanHallDao.save(getId(), 0, 0);
                }
            } catch (SQLException e) {
                LOGGER.error(e.getMessage(), e);
            }
        });
    }

    private void initResidenceZone() {
        ZoneEngine.getInstance().getAllZones(ClanHallZone.class).stream().filter(z -> z.getResidenceId() == getId()).findFirst().ifPresent(this::setResidenceZone);
    }

    public void updateDB() {
        getDAO(ClanHallDAO.class).save(getId(), getOwnerId(), paidUntil);
    }


    public int getCostFailDay() {
        var failDay = Duration.between(Instant.ofEpochMilli(paidUntil), Instant.now());
        return failDay.isNegative() ? 0 : (int) failDay.toDays();
    }

    /**
     * Teleport all non-owner players from {@link ClanHallZone} to {@link ClanHall#getBanishPoint()}.
     */
    public void banishOthers() {
        getResidenceZone().banishForeigners(getOwnerId());
    }

    /**
     * Open or close all {@link Door} related to this {@link ClanHall}.
     *
     * @param open {@code true} means open door, {@code false} means close door
     */
    public void openCloseDoors(boolean open) {
        doors.forEach(door -> door.openCloseMe(open));
    }

    /**
     * Gets the grade of clan hall.
     *
     * @return grade of this {@link ClanHall} in {@link ClanHallGrade} enum.
     */
    public ClanHallGrade getGrade() {
        return grade;
    }

    /**
     * Gets all {@link Npc} related to this {@link ClanHall}.
     *
     * @return all {@link Npc} related to this {@link ClanHall}
     */
    IntList getNpcs() {
        return npcs;
    }

    /**
     * Gets the {@link ClanHallType} of this {@link ClanHall}.
     *
     * @return {@link ClanHallType} of this {@link ClanHall} in {@link ClanHallGrade} enum.
     */
    public ClanHallType getType() {
        return type;
    }

    /**
     * Gets the {@link Clan} which own this {@link ClanHall}.
     *
     * @return {@link Clan} which own this {@link ClanHall}
     */
    public Clan getOwner() {
        return owner;
    }

    /**
     * Set the owner of clan hall
     *
     * @param clanId the Id of the clan
     */
    public void setOwner(int clanId) {
        setOwner(ClanEngine.getInstance().getClan(clanId));
    }

    /**
     * Set the clan as owner of clan hall
     *
     * @param clan the Clan object
     */
    public void setOwner(Clan clan) {
        if (nonNull(clan)) {
            owner = clan;
            clan.setHideoutId(getId());
            clan.broadcastToOnlineMembers(new PledgeShowInfoUpdate(clan));
            if (paidUntil == 0) {
                paidUntil = Instant.now().plus(Duration.ofDays(7)).toEpochMilli();
            }

            final int failDays = getCostFailDay();
            long time = paidUntil;

            if(failDays > 8) {
                time = Instant.now().toEpochMilli();
            } else if(failDays > 0) {
                time = Instant.ofEpochMilli(paidUntil).plus(1, ChronoUnit.DAYS).toEpochMilli();
            }

            checkPaymentTask = ThreadPool.schedule(new CheckPaymentTask(), time - System.currentTimeMillis());
        } else {
            if (nonNull(owner)) {
                owner.setHideoutId(0);
                owner.broadcastToOnlineMembers(new PledgeShowInfoUpdate(owner));
                removeFunctions();
            }
            owner = null;
            paidUntil = 0;
            if (checkPaymentTask != null) {
                checkPaymentTask.cancel(true);
                checkPaymentTask = null;
            }
        }
        updateDB();
    }

    /**
     * Gets the {@link Clan} ID which own this {@link ClanHall}.
     *
     * @return the {@link Clan} ID which own this {@link ClanHall}
     */
    @Override
    public int getOwnerId() {
        return zeroIfNullOrElse(owner, Clan::getId);
    }

    /**
     * Gets the next date of clan hall payment
     *
     * @return the next date of clan hall payment
     */
    public long getNextPayment() {
        return nonNull(checkPaymentTask) ? System.currentTimeMillis() + checkPaymentTask.getDelay(TimeUnit.MILLISECONDS) : 0;
    }

    public Location getRestartPoint() {
        return restartLocation;
    }

    public Location getBanishPoint() {
        return banishPoint;
    }

    public long getMinBid() {
        return minBid;
    }

    public long getLease() {
        return lease;
    }

    void setNpcs(IntList npcs) {
        this.npcs = npcs;
    }

    void setRestartPoint(Location restartPoint) {
        this.restartLocation = restartPoint;
    }

    void setBanishPoint(Location banishPoint) {
        this.banishPoint = banishPoint;
    }

    void setMinBid(long minBid) {
        this.minBid = minBid;
    }

    void setLease(long lease) {
        this.lease = lease;
    }

    void addDoor(Door door) {
        doors.add(door);
    }

    boolean hasDoor(Door door) {
        return doors.contains(door);
    }

    @Override
    public String toString() {
        return (getClass().getSimpleName() + ":" + getName() + "[" + getId() + "]");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        var clanHall = (ClanHall) o;
        return getId() == clanHall.getId();
    }

    @Override
    public int hashCode() {
        return 0;
    }

    class CheckPaymentTask implements Runnable {
        @Override
        public void run() {
            if (nonNull(owner)) {
                if (owner.getWarehouse().getAdena() < lease) {
                    if (getCostFailDay() > 8) {
                        owner.broadcastToOnlineMembers(getSystemMessage(SystemMessageId.THE_CLAN_HALL_FEE_IS_ONE_WEEK_OVERDUE_THEREFORE_THE_CLAN_HALL_OWNERSHIP_HAS_BEEN_REVOKED));
                        setOwner(null);
                    } else {
                        checkPaymentTask = ThreadPool.schedule(new CheckPaymentTask(), 1, TimeUnit.DAYS); // 1 day
                        owner.broadcastToOnlineMembers(getSystemMessage(SystemMessageId.PAYMENT_FOR_YOUR_CLAN_HALL_HAS_NOT_BEEN_MADE_PLEASE_MAKE_PAYMENT_TO_YOUR_CLAN_WAREHOUSE_BY_S1_TOMORROW).addLong(lease));
                    }
                } else {
                    owner.getWarehouse().destroyItem("Clan Hall Lease", CommonItem.ADENA, lease, null, null);
                    paidUntil = Instant.ofEpochMilli(paidUntil).plus(Duration.ofDays(7)).toEpochMilli();
                    checkPaymentTask = ThreadPool.schedule(new CheckPaymentTask(), paidUntil - System.currentTimeMillis());
                    updateDB();
                }
            }
        }
    }
}