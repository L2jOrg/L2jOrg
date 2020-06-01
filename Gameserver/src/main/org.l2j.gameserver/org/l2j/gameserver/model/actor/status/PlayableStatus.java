package org.l2j.gameserver.model.actor.status;

import org.l2j.gameserver.model.actor.Playable;

public class PlayableStatus extends CreatureStatus {
    public PlayableStatus(Playable playable) {
        super(playable);
    }

    @Override
    public Playable getOwner() {
        return (Playable) super.getOwner();
    }
}
