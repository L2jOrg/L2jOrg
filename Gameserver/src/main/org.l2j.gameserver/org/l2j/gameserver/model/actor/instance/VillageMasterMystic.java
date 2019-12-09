package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.data.xml.CategoryManager;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.base.ClassId;

public final class VillageMasterMystic extends VillageMaster {
    /**
     * Creates a village master.
     *
     * @param template the village master NPC template
     */
    public VillageMasterMystic(NpcTemplate template) {
        super(template);
    }

    @Override
    protected final boolean checkVillageMasterRace(ClassId pclass) {
        if (pclass == null) {
            return false;
        }

        return pclass.getRace() == Race.HUMAN || pclass.getRace() == Race.ELF;
    }

    @Override
    protected final boolean checkVillageMasterTeachType(ClassId pclass) {
        if (pclass == null) {
            return false;
        }

        return CategoryManager.getInstance().isInCategory(CategoryType.MAGE_GROUP, pclass.getId());
    }
}