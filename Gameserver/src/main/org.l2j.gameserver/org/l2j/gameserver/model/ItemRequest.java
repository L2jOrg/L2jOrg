/*
 * Copyright © 2019 L2J Mobius
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
package org.l2j.gameserver.model;

/**
 *
 */
public class ItemRequest {
    int _objectId;
    int _itemId;
    long _count;
    long _price;

    public ItemRequest(int objectId, long count, long price) {
        _objectId = objectId;
        _count = count;
        _price = price;
    }

    public ItemRequest(int objectId, int itemId, long count, long price) {
        _objectId = objectId;
        _itemId = itemId;
        _count = count;
        _price = price;
    }

    public int getObjectId() {
        return _objectId;
    }

    public int getItemId() {
        return _itemId;
    }

    public long getCount() {
        return _count;
    }

    public void setCount(long count) {
        _count = count;
    }

    public long getPrice() {
        return _price;
    }

    @Override
    public int hashCode() {
        return _objectId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ItemRequest)) {
            return false;
        }
        return (_objectId != ((ItemRequest) obj)._objectId);
    }
}
