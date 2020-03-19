package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;
import org.l2j.gameserver.model.interfaces.IIdentifiable;

/**
 * @author NosBit
 * @author JoeAlisson
 */
@Table("crests")
public final class CrestData implements IIdentifiable {

    @Column("crest_id")
    private int id;
    private byte[] data;
    private CrestType type;

    public CrestData() {
    }

    public CrestData(int id, byte[] data, CrestType type) {
        this.id = id;
        this.data = data;
        this.type = type;
    }

    @Override
    public int getId() {
        return id;
    }

    public byte[] getData() {
        return data;
    }

    public CrestType getType() {
        return type;
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