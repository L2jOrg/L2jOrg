package org.l2j.gameserver.model.effects;

import org.l2j.gameserver.model.skills.BuffInfo;

/**
 * Effect tick task.
 *
 * @author Zoey76
 */
public class EffectTickTask implements Runnable {
    private final BuffInfo _info;
    private final AbstractEffect _effect;

    /**
     * EffectTickTask constructor.
     *
     * @param info   the buff info
     * @param effect the effect
     */
    public EffectTickTask(BuffInfo info, AbstractEffect effect) {
        _info = info;
        _effect = effect;
    }

    /**
     * Gets the buff info.
     *
     * @return the buff info
     */
    public BuffInfo getBuffInfo() {
        return _info;
    }

    /**
     * Gets the effect.
     *
     * @return the effect
     */
    public AbstractEffect getEffect() {
        return _effect;
    }


    @Override
    public void run() {
        _info.onTick(_effect);
    }
}
