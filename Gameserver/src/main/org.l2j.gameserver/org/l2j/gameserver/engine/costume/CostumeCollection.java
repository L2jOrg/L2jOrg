package org.l2j.gameserver.engine.costume;

import io.github.joealisson.primitive.IntSet;

/**
 * @author JoeAlisson
 */
public class CostumeCollection {

    private final int id;
    private final int skill;
    private IntSet costumes;

    CostumeCollection(int id, int skill) {
        this.id = id;
        this.skill = skill;
    }

    void setCostumes(IntSet costumes) {
        this.costumes = costumes;
    }
}
