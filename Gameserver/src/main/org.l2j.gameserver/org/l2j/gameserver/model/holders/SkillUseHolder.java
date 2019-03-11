package org.l2j.gameserver.model.holders;

import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.model.skills.Skill;

/**
 * @author UnAfraid
 */
public class SkillUseHolder extends SkillHolder {
    private final L2ItemInstance _item;
    private final boolean _ctrlPressed;
    private final boolean _shiftPressed;

    public SkillUseHolder(Skill skill, L2ItemInstance item, boolean ctrlPressed, boolean shiftPressed) {
        super(skill);
        _item = item;
        _ctrlPressed = ctrlPressed;
        _shiftPressed = shiftPressed;
    }

    public L2ItemInstance getItem() {
        return _item;
    }

    public boolean isCtrlPressed() {
        return _ctrlPressed;
    }

    public boolean isShiftPressed() {
        return _shiftPressed;
    }
}
