package org.l2j.gameserver.data.xml.model;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public class ActionData {
    private final int id;
    private final String handler;
    private final int optionId;

    public ActionData(Integer id, String handler, Integer optionId) {
        this.id = id;
        this.handler = handler;
        this.optionId = optionId;
    }

    public int getId() {
        return id;
    }

    public String getHandler() {
        return handler;
    }

    public int getOptionId() {
        return optionId;
    }
}
