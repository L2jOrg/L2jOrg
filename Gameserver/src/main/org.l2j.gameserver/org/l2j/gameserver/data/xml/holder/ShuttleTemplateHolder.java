package org.l2j.gameserver.data.xml.holder;

import org.l2j.commons.data.xml.AbstractHolder;
import org.l2j.gameserver.templates.ShuttleTemplate;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;

/**
 * @author Bonux
 */
public final class ShuttleTemplateHolder extends AbstractHolder {
    private static final ShuttleTemplateHolder _instance = new ShuttleTemplateHolder();

    private HashIntObjectMap<ShuttleTemplate> _templates = new HashIntObjectMap<ShuttleTemplate>();

    public static ShuttleTemplateHolder getInstance() {
        return _instance;
    }

    public void addTemplate(ShuttleTemplate template) {
        _templates.put(template.getId(), template);
    }

    public ShuttleTemplate getTemplate(int id) {
        return _templates.get(id);
    }

    @Override
    public int size() {
        return _templates.size();
    }

    @Override
    public void clear() {
        _templates.clear();
    }
}