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
