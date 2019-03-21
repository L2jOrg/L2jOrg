package org.l2j.gameserver.model.items.type;

/**
 * Crystal Type enumerated.
 *
 * @author Adry_85
 */
public enum CrystalType {
    NONE(0, 0, 0, 0),
    D(1, 1458, 11, 90),
    C(2, 1459, 6, 45),
    B(3, 1460, 11, 67),
    A(4, 1461, 20, 145),
    S(5, 1462, 25, 250),
    S80(6, 1462, 25, 250),
    S84(7, 1462, 25, 250),
    EVENT(11, 0, 0, 0);

    private final int _id;
    private final int _crystalId;
    private final int _crystalEnchantBonusArmor;
    private final int _crystalEnchantBonusWeapon;

    CrystalType(int id, int crystalId, int crystalEnchantBonusArmor, int crystalEnchantBonusWeapon) {
        _id = id;
        _crystalId = crystalId;
        _crystalEnchantBonusArmor = crystalEnchantBonusArmor;
        _crystalEnchantBonusWeapon = crystalEnchantBonusWeapon;
    }

    /**
     * Gets the crystal type ID.
     *
     * @return the crystal type ID
     */
    public int getId() {
        return _id;
    }

    /**
     * Gets the item ID of the crystal.
     *
     * @return the item ID of the crystal
     */
    public int getCrystalId() {
        return _crystalId;
    }

    public int getCrystalEnchantBonusArmor() {
        return _crystalEnchantBonusArmor;
    }

    public int getCrystalEnchantBonusWeapon() {
        return _crystalEnchantBonusWeapon;
    }

    public boolean isGreater(CrystalType crystalType) {
        return getId() > crystalType.getId();
    }

    public boolean isLesser(CrystalType crystalType) {
        return getId() < crystalType.getId();
    }
}
