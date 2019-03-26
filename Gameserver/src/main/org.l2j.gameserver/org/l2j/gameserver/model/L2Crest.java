package org.l2j.gameserver.model;

import org.l2j.gameserver.model.interfaces.IIdentifiable;

/**
 * @author NosBit
 */
public final class L2Crest implements IIdentifiable {
    private final int _id;
    private final byte[] _data;
    private final CrestType _type;

    public L2Crest(int id, byte[] data, CrestType type) {
        _id = id;
        _data = data;
        _type = type;
    }

    @Override
    public int getId() {
        return _id;
    }

    public byte[] getData() {
        return _data;
    }

    public CrestType getType() {
        return _type;
    }

    public enum CrestType {
        PLEDGE(1),
        PLEDGE_LARGE(2),
        ALLY(3);

        private final int _id;

        CrestType(int id) {
            _id = id;
        }

        public static CrestType getById(int id) {
            for (CrestType crestType : values()) {
                if (crestType.getId() == id) {
                    return crestType;
                }
            }
            return null;
        }

        public int getId() {
            return _id;
        }
    }
}