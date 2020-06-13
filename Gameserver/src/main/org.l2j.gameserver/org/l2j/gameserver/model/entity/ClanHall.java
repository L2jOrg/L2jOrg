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
package org.l2j.gameserver.model.entity;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.data.database.dao.ClanHallDAO;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.data.xml.impl.ClanHallManager;
import org.l2j.gameserver.enums.ClanHallGrade;
import org.l2j.gameserver.enums.ClanHallType;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Door;
import org.l2j.gameserver.model.holders.ClanHallTeleportHolder;
import org.l2j.gameserver.model.item.CommonItem;
import org.l2j.gameserver.model.residences.AbstractResidence;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.pledge.PledgeShowInfoUpdate;
import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.gameserver.world.zone.type.ClanHallZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(ClanHallManager.class);

    private final int lease;
    private final ClanHallGrade grade;
    private final ClanHallType type;
    private final int minBid;
    private final int deposit;
    private final List<Integer> npcs;
    private final List<Door> doors;
    private final List<ClanHallTeleportHolder> teleports;
    private final Location ownerLocation;
    private final Location banishLocation;
    protected ScheduledFuture<?> checkPaymentTask = null;

    Clan owner = null;
    long paidUntil = 0;

    public ClanHall(StatsSet params) {
        super(params.getInt("id"));
        setName(params.getString("name"));
        grade = params.getEnum("grade", ClanHallGrade.class);
        type = params.getEnum("type", ClanHallType.class);
        minBid = params.getInt("minBid");
        lease = params.getInt("lease");
        deposit = params.getInt("deposit");
        npcs = params.getList("npcList", Integer.class);
        doors = params.getList("doorList", Door.class);
        teleports = params.getList("teleportList", ClanHallTeleportHolder.class);
        ownerLocation = params.getLocation("owner_loc");
        banishLocation = params.getLocation("banish_loc");

        load();
        initResidenceZone();
        initFunctions();
    }

    @Override
    protected void load() {
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

    @Override
    protected void initResidenceZone() {
        ZoneManager.getInstance().getAllZones(ClanHallZone.class).stream().filter(z -> z.getResidenceId() == getId()).findFirst().ifPresent(this::setResidenceZone);
    }

    public void updateDB() {
        getDAO(ClanHallDAO.class).save(getId(), getOwnerId(), paidUntil);
    }


    public int getCostFailDay() {
        final Duration failDay = Duration.between(Instant.ofEpochMilli(paidUntil), Instant.now());
        return failDay.isNegative() ? 0 : (int) failDay.toDays();
    }

    /**
     * Teleport all non-owner players from {@link ClanHallZone} to {@link ClanHall#getBanishLocation()}.
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
     * Gets all {@link Door} related to this {@link ClanHall}.
     *
     * @return all {@link Door} related to this {@link ClanHall}
     */
    public List<Door> getDoors() {
        return doors;
    }

    /**
     * Gets all {@link Npc} related to this {@link ClanHall}.
     *
     * @return all {@link Npc} related to this {@link ClanHall}
     */
    public List<Integer> getNpcs() {
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
        setOwner(ClanTable.getInstance().getClan(clanId));
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
            final long time = failDays > 0 ? (failDays > 8 ? Instant.now().toEpochMilli() : Instant.ofEpochMilli(paidUntil).plus(Duration.ofDays(failDays + 1)).toEpochMilli()) : paidUntil;
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

    public Location getOwnerLocation() {
        return ownerLocation;
    }

    public Location getBanishLocation() {
        return banishLocation;
    }

    public int getMinBid() {
        return minBid;
    }

    public int getLease() {
        return lease;
    }

    public int getDeposit() {
        return deposit;
    }

    @Override
    public String toString() {
        return (getClass().getSimpleName() + ":" + getName() + "[" + getId() + "]");
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
                        owner.broadcastToOnlineMembers(getSystemMessage(SystemMessageId.PAYMENT_FOR_YOUR_CLAN_HALL_HAS_NOT_BEEN_MADE_PLEASE_MAKE_PAYMENT_TO_YOUR_CLAN_WAREHOUSE_BY_S1_TOMORROW).addInt(lease));
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