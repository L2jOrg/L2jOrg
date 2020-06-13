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
package org.l2j.gameserver.model;

/**
 * This class ...
 *
 * @version $Revision: 1.2.4.1 $ $Date: 2005/03/27 15:29:32 $
 */
public class TeleportLocation {
    private int _teleId;
    private int _locX;
    private int _locY;
    private int _locZ;
    private int _price;
    private boolean _forNoble;
    private int _itemId;

    /**
     * @return
     */
    public int getTeleId() {
        return _teleId;
    }

    /**
     * @param id
     */
    public void setTeleId(int id) {
        _teleId = id;
    }

    /**
     * @return
     */
    public int getLocX() {
        return _locX;
    }

    /**
     * @param locX
     */
    public void setLocX(int locX) {
        _locX = locX;
    }

    /**
     * @return
     */
    public int getLocY() {
        return _locY;
    }

    /**
     * @param locY
     */
    public void setLocY(int locY) {
        _locY = locY;
    }

    /**
     * @return
     */
    public int getLocZ() {
        return _locZ;
    }

    /**
     * @param locZ
     */
    public void setLocZ(int locZ) {
        _locZ = locZ;
    }

    /**
     * @return
     */
    public int getPrice() {
        return _price;
    }

    /**
     * @param price
     */
    public void setPrice(int price) {
        _price = price;
    }

    /**
     * @return
     */
    public boolean getIsForNoble() {
        return _forNoble;
    }

    /**
     * @param val
     */
    public void setIsForNoble(boolean val) {
        _forNoble = val;
    }

    /**
     * @return
     */
    public int getItemId() {
        return _itemId;
    }

    /**
     * @param val
     */
    public void setItemId(int val) {
        _itemId = val;
    }
}
