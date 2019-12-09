package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.base.ClassId;

public final class VillageMasterDElf extends VillageMaster {
    /**
     * Creates a village master.
     *
     * @param template the village master NPC template
     */
    public VillageMasterDElf(NpcTemplate template) {
        super(template);
    }

    @Override
    protected final boolean checkVillageMasterRace(ClassId pclass) {
        if (pclass == null) {
            return false;
        }

        return pclass.getRace() == Race.DARK_ELF;
    }
}