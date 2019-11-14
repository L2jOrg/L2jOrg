package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.base.PlayerState;

/**
 * @author JoeAlisson
 */
public class ConditionPlayerChaotic extends ConditionPlayerState {

    private static ConditionPlayerChaotic CHAOTIC = new ConditionPlayerChaotic(true);
    private static ConditionPlayerChaotic NO_CHAOTIC = new ConditionPlayerChaotic(false);

    /**
     * Instantiates a new condition player state.
     *
     * @param isChaotic the required value.
     */
    private ConditionPlayerChaotic(boolean isChaotic) {
        super(PlayerState.CHAOTIC, isChaotic);
    }

    public static ConditionPlayerChaotic of(boolean chaotic) {
        return chaotic ? CHAOTIC : NO_CHAOTIC;
    }
}
