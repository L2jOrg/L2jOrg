package org.l2j.gameserver.enums;

/**
 * @author Sdw
 */
public enum SkillConditionPercentType {
    MORE {
        @Override
        public boolean test(int x1, int x2) {
            return x1 >= x2;
        }
    },
    LESS {
        @Override
        public boolean test(int x1, int x2) {
            return x1 <= x2;
        }
    };

    public abstract boolean test(int x1, int x2);
}
