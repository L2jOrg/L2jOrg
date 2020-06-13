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
package org.l2j.gameserver.model.buylist;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.database.dao.BuyListDAO;
import org.l2j.gameserver.data.database.data.BuyListInfo;
import org.l2j.gameserver.model.item.BodyPart;
import org.l2j.gameserver.model.item.EquipableItem;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.model.item.type.CrystalType;
import org.l2j.gameserver.model.item.type.EtcItemType;

import java.util.Objects;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.Objects.isNull;
import static org.l2j.commons.database.DatabaseAccess.getDAO;

/**
 * @author NosBit
 */
public final class Product {

    private final ItemTemplate template;
    private final long restockDelay;
    private AtomicLong count = null;

    private final long price;
    private final long maxCount;
    private final double baseTax;
    private ScheduledFuture<?> restockTask = null;
    private BuyListInfo info;

    public Product(int buyListId, ItemTemplate item, long price, long restockDelay, long maxCount, int baseTax) {
        Objects.requireNonNull(item);
        template = item;
        this.maxCount = maxCount;
        if (hasLimitedStock()) {
            count = new AtomicLong(maxCount);
        }

        this.restockDelay =  restockDelay * 60000;
        this.baseTax = baseTax / 100.0;
        this.price = (price < 0) ? item.getReferencePrice() : price;
        info = BuyListInfo.of(item.getId(), buyListId);
    }

    public ItemTemplate getTemplate() {
        return template;
    }

    public int getItemId() {
        return template.getId();
    }

    public long getPrice() {
        long price = this.price;
        if (template.getItemType().equals(EtcItemType.CASTLE_GUARD)) {
            price *= Config.RATE_SIEGE_GUARDS_PRICE;
        }
        return price;
    }

    public double getBaseTaxRate() {
        return baseTax;
    }

    public long getMaxCount() {
        return maxCount;
    }

    public long getCount() {
        if (count == null) {
            return 0;
        }
        final long count = this.count.get();
        return count > 0 ? count : 0;
    }

    public void setCount(long currentCount) {
        if (count == null) {
            count = new AtomicLong();
        }
        count.set(currentCount);
    }

    public boolean decreaseCount(long val) {
        if (count == null) {
            return false;
        }
        if ((restockTask == null) || restockTask.isDone()) {
            restockTask = ThreadPool.schedule(this::restock, restockDelay);
        }
        final boolean result = count.addAndGet(-val) >= 0;
        save();
        return result;
    }

    public boolean hasLimitedStock() {
        return maxCount > -1;
    }

    public void restartRestockTask(long nextRestockTime) {
        final long remainTime = nextRestockTime - System.currentTimeMillis();
        if (remainTime > 0) {
            restockTask = ThreadPool.schedule(this::restock, remainTime);
        } else {
            restock();
        }
    }

    public void restock() {
        setCount(maxCount);
        save();
    }

    private void save() {
        if(hasLimitedStock()) {
            info.setCount(getCount());
            info.setNextRestock(isNull(restockTask) ? 0 : restockTask.getDelay(TimeUnit.MILLISECONDS) + System.currentTimeMillis());
            getDAO(BuyListDAO.class).save(info);
        }
    }

    public boolean isStackable() {
        return template.isStackable();
    }

    public long getWeight() {
        return template.getWeight();
    }

    public CrystalType getCrystalType() {
        return template.getCrystalType();
    }

    public boolean isEquipable() {
        return template instanceof EquipableItem;
    }

    public int getType2() {
        return template.getType2();
    }

    public int getType1() {
        return template.getType1();
    }

    public BodyPart getBodyPart() {
        return template.getBodyPart();
    }

    public void updateInfo(BuyListInfo info) {
        this.info = info;
        if(info.getCount() < maxCount) {
            setCount(info.getCount());
            restartRestockTask(info.getNextRestock());
        }
    }
}
