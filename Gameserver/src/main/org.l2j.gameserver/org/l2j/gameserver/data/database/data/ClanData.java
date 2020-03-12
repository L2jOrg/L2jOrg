package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;

/**
 * @author JoeAlisson
 */
@Table("clan_data")
public class ClanData {

    @Column("clan_id")
    private int id;

    @Column("clan_name")
    private String name;

    @Column("ally_id")
    private int allyId;

    @Column("ally_name")
    private String allyName;


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAllyId() {
        return allyId;
    }

    public String getAllyName() {
        return allyName;
    }
}
