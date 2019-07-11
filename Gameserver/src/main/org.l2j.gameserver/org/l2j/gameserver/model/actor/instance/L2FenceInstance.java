package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.data.xml.impl.FenceData;
import org.l2j.gameserver.enums.FenceState;
import org.l2j.gameserver.idfactory.IdFactory;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.network.serverpackets.DeleteObject;
import org.l2j.gameserver.network.serverpackets.ExColosseumFenceInfo;

/**
 * @author HoridoJoho / FBIagent
 */
public final class L2FenceInstance extends WorldObject {
    private final int _xMin;
    private final int _xMax;
    private final int _yMin;
    private final int _yMax;

    private final String _name;
    private final int _width;
    private final int _length;

    private FenceState _state;
    private int[] _heightFences;

    public L2FenceInstance(int x, int y, String name, int width, int length, int height, FenceState state) {
        super(IdFactory.getInstance().getNextId());

        _xMin = x - (width / 2);
        _xMax = x + (width / 2);
        _yMin = y - (length / 2);
        _yMax = y + (length / 2);

        _name = name;
        _width = width;
        _length = length;

        _state = state;

        if (height > 1) {
            _heightFences = new int[height - 1];
            for (int i = 0; i < _heightFences.length; i++) {
                _heightFences[i] = IdFactory.getInstance().getNextId();
            }
        }
    }

    @Override
    public int getId() {
        return getObjectId();
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public boolean isAutoAttackable(Creature attacker) {
        return false;
    }

    @Override
    public void sendInfo(Player activeChar) {
        activeChar.sendPacket(new ExColosseumFenceInfo(this));

        if (_heightFences != null) {
            for (int objId : _heightFences) {
                activeChar.sendPacket(new ExColosseumFenceInfo(objId, getX(), getY(), getZ(), _width, _length, _state));
            }
        }
    }

    @Override
    public boolean decayMe() {
        if (_heightFences != null) {
            final DeleteObject[] deleteObjects = new DeleteObject[_heightFences.length];
            for (int i = 0; i < _heightFences.length; i++) {
                deleteObjects[i] = new DeleteObject(_heightFences[i]);
            }

            L2World.getInstance().forEachVisibleObject(this, Player.class, player -> player.sendPacket(deleteObjects));
        }

        return super.decayMe();
    }

    public boolean deleteMe() {
        decayMe();

        FenceData.getInstance().removeFence(this);
        return false;
    }

    public FenceState getState() {
        return _state;
    }

    public void setState(FenceState type) {
        _state = type;

        broadcastInfo();
    }

    public int getWidth() {
        return _width;
    }

    public int getLength() {
        return _length;
    }

    public int getXMin() {
        return _xMin;
    }

    public int getYMin() {
        return _yMin;
    }

    public int getXMax() {
        return _xMax;
    }

    public int getYMax() {
        return _yMax;
    }
}