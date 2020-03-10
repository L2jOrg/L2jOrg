package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;

/**
 * @author JoeAlisson
 */
public class QuestData {

    @Column("charId")
    private int playerId;
    private String name;
    private String var;
    private String value;
    @Column("class_index")
    private int classIndex;

    public int getPlayerId() {
        return playerId;
    }

    public String getName() {
        return name;
    }

    public String getVar() {
        return var;
    }

    public String getValue() {
        return value;
    }

    public int getClassIndex() {
        return classIndex;
    }
}
