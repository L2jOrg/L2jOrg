package org.l2j.gameserver.model.options;

/**
 * @author UnAfraid
 */
public class EnchantOptions {
    private final int _level;
    private final int[] _options;

    public EnchantOptions(int level) {
        _level = level;
        _options = new int[3];
    }

    public int getLevel() {
        return _level;
    }

    public int[] getOptions() {
        return _options;
    }

    public void setOption(byte index, int option) {
        if (_options.length > index) {
            _options[index] = option;
        }
    }
}
