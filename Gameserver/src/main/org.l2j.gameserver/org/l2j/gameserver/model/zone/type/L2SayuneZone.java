package org.l2j.gameserver.model.zone.type;

import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.actor.tasks.player.FlyMoveStartTask;
import org.l2j.gameserver.model.zone.L2ZoneType;
import org.l2j.gameserver.model.zone.ZoneId;

/**
 * @author UnAfraid
 */
public class L2SayuneZone extends L2ZoneType {
    private int _mapId = -1;

    public L2SayuneZone(int id) {
        super(id);
    }

    @Override
    public void setParameter(String name, String value) {
        switch (name) {
            case "mapId": {
                _mapId = Integer.parseInt(value);
                break;
            }
            default: {
                super.setParameter(name, value);
            }
        }
    }

    @Override
    protected void onEnter(L2Character character) {
        if (character.isPlayer() && (character.isInCategory(CategoryType.SIXTH_CLASS_GROUP) || Config.FREE_JUMPS_FOR_ALL) && !character.getActingPlayer().isMounted() && !character.isTransformed()) {
            character.setInsideZone(ZoneId.SAYUNE, true);
            ThreadPoolManager.getInstance().execute(new FlyMoveStartTask(this, character.getActingPlayer()));
        }
    }

    @Override
    protected void onExit(L2Character character) {
        if (character.isPlayer()) {
            character.setInsideZone(ZoneId.SAYUNE, false);
        }
    }

    public int getMapId() {
        return _mapId;
    }
}
