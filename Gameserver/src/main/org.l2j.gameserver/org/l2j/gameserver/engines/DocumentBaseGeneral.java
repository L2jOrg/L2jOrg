package org.l2j.gameserver.engines;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.conditions.Condition;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;

/**
 * A dummy class designed only to parse conditions
 *
 * @author UnAfraid
 */
public class DocumentBaseGeneral extends DocumentBase {

    private DocumentBaseGeneral(File file) {
        super(file);
    }

    @Override
    protected void parseDocument(Document doc) {
    }

    @Override
    protected StatsSet getStatsSet() {
        return null;
    }

    @Override
    protected String getTableValue(String name) {
        return null;
    }

    @Override
    protected String getTableValue(String name, int idx) {
        return null;
    }

    public Condition parseCondition(Node n) {
        return super.parseCondition(n, null);
    }

    public static DocumentBaseGeneral getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final DocumentBaseGeneral INSTANCE = new DocumentBaseGeneral(null);
    }
}
