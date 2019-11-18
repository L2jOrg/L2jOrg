package org.l2j.gameserver.model.items;

import org.l2j.gameserver.model.items.type.CrystalType;

/**
 * @author  JoeAlisson
 */
public interface EquipableItem {

    void setBodyPart(BodyPart bodyPart);

    BodyPart getBodyPart();

    void setCrystalType(CrystalType type);

    CrystalType getCrystalType();

    void setCrystalCount(int count);

    int getCrystalCount();
}
