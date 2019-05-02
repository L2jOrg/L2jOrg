package org.l2j.gameserver.enums;

import org.l2j.gameserver.model.interfaces.IUpdateTypeComponent;
import org.l2j.gameserver.model.itemcontainer.Inventory;

/**
 * @author UnAfraid
 */
public enum InventorySlot implements IUpdateTypeComponent {
    UNDER(Inventory.PAPERDOLL_UNDER),
    REAR(Inventory.PAPERDOLL_REAR),
    LEAR(Inventory.PAPERDOLL_LEAR),
    NECK(Inventory.PAPERDOLL_NECK),
    RFINGER(Inventory.PAPERDOLL_RFINGER),
    LFINGER(Inventory.PAPERDOLL_LFINGER),
    HEAD(Inventory.PAPERDOLL_HEAD),
    RHAND(Inventory.PAPERDOLL_RHAND),
    LHAND(Inventory.PAPERDOLL_LHAND),
    GLOVES(Inventory.PAPERDOLL_GLOVES),
    CHEST(Inventory.PAPERDOLL_CHEST),
    LEGS(Inventory.PAPERDOLL_LEGS),
    FEET(Inventory.PAPERDOLL_FEET),
    CLOAK(Inventory.PAPERDOLL_CLOAK),
    LRHAND(Inventory.PAPERDOLL_RHAND),
    HAIR(Inventory.PAPERDOLL_HAIR),
    HAIR2(Inventory.PAPERDOLL_HAIR2),
    RBRACELET(Inventory.PAPERDOLL_RBRACELET),
    LBRACELET(Inventory.PAPERDOLL_LBRACELET),
    AGATHION1(Inventory.PAPERDOLL_AGATHION1),
    AGATHION2(Inventory.PAPERDOLL_AGATHION2),
    AGATHION3(Inventory.PAPERDOLL_AGATHION3),
    AGATHION4(Inventory.PAPERDOLL_AGATHION4),
    AGATHION5(Inventory.PAPERDOLL_AGATHION5),
    TALISMAN1(Inventory.TALISMAN1),
    TALISMAN2(Inventory.TALISMAN2),
    TALISMAN3(Inventory.TALISMAN3),
    TALISMAN4(Inventory.TALISMAN4),
    TALISMAN5(Inventory.TALISMAN5),
    TALISMAN6(Inventory.TALISMAN6),
    BELT(Inventory.PAPERDOLL_BELT),
    BROOCH(Inventory.PAPERDOLL_BROOCH),
    BROOCH_JEWEL1(Inventory.PAPERDOLL_BROOCH_JEWEL1),
    BROOCH_JEWEL2(Inventory.PAPERDOLL_BROOCH_JEWEL2),
    BROOCH_JEWEL3(Inventory.PAPERDOLL_BROOCH_JEWEL3),
    BROOCH_JEWEL4(Inventory.PAPERDOLL_BROOCH_JEWEL4),
    BROOCH_JEWEL5(Inventory.PAPERDOLL_BROOCH_JEWEL5),
    BROOCH_JEWEL6(Inventory.PAPERDOLL_BROOCH_JEWEL6),
    ARTIFACT_BOOK(Inventory.PAPERDOLL_ARTIFACT_BOOK),
    ARTIFACT1(Inventory.PAPERDOLL_ARTIFACT1),
    ARTIFACT2(Inventory.PAPERDOLL_ARTIFACT2),
    ARTIFACT3(Inventory.PAPERDOLL_ARTIFACT3),
    ARTIFACT4(Inventory.PAPERDOLL_ARTIFACT4),
    ARTIFACT5(Inventory.PAPERDOLL_ARTIFACT5),
    ARTIFACT6(Inventory.PAPERDOLL_ARTIFACT6),
    ARTIFACT7(Inventory.PAPERDOLL_ARTIFACT7),
    ARTIFACT8(Inventory.PAPERDOLL_ARTIFACT8),
    ARTIFACT9(Inventory.PAPERDOLL_ARTIFACT9),
    ARTIFACT10(Inventory.PAPERDOLL_ARTIFACT10),
    ARTIFACT11(Inventory.PAPERDOLL_ARTIFACT11),
    ARTIFACT12(Inventory.PAPERDOLL_ARTIFACT12),
    ARTIFACT13(Inventory.PAPERDOLL_ARTIFACT13),
    ARTIFACT14(Inventory.PAPERDOLL_ARTIFACT14),
    ARTIFACT15(Inventory.PAPERDOLL_ARTIFACT15),
    ARTIFACT16(Inventory.PAPERDOLL_ARTIFACT16),
    ARTIFACT17(Inventory.PAPERDOLL_ARTIFACT17),
    ARTIFACT18(Inventory.PAPERDOLL_ARTIFACT18),
    ARTIFACT19(Inventory.PAPERDOLL_ARTIFACT19),
    ARTIFACT20(Inventory.PAPERDOLL_ARTIFACT20),
    ARTIFACT21(Inventory.PAPERDOLL_ARTIFACT21);

    private final int _paperdollSlot;

    InventorySlot(int paperdollSlot) {
        _paperdollSlot = paperdollSlot;
    }

    public int getSlot() {
        return _paperdollSlot;
    }

    @Override
    public int getMask() {
        return ordinal();
    }
}
