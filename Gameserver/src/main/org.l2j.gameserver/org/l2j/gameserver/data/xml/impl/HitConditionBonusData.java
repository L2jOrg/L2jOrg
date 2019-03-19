package org.l2j.gameserver.data.xml.impl;

import org.l2j.gameserver.GameTimeController;
import org.l2j.gameserver.enums.Position;
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.util.IGameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;


/**
 * This class load, holds and calculates the hit condition bonuses.
 *
 * @author Nik
 */
public final class HitConditionBonusData implements IGameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(HitConditionBonusData.class);

    private int frontBonus = 0;
    private int sideBonus = 0;
    private int backBonus = 0;
    private int highBonus = 0;
    private int lowBonus = 0;
    private int darkBonus = 0;
    @SuppressWarnings("unused")
    private int rainBonus = 0;

    /**
     * Instantiates a new hit condition bonus.
     */
    protected HitConditionBonusData() {
        load();
    }

    /**
     * Gets the single instance of HitConditionBonus.
     *
     * @return single instance of HitConditionBonus
     */
    public static HitConditionBonusData getInstance() {
        return SingletonHolder._instance;
    }

    @Override
    public void load() {
        parseDatapackFile("data/stats/hitConditionBonus.xml");
        LOGGER.info(getClass().getSimpleName() + ": Loaded Hit Condition bonuses.");
    }

    @Override
    public void parseDocument(Document doc, File f) {
        for (Node d = doc.getFirstChild().getFirstChild(); d != null; d = d.getNextSibling()) {
            final NamedNodeMap attrs = d.getAttributes();
            switch (d.getNodeName()) {
                case "front": {
                    frontBonus = parseInteger(attrs, "val");
                    break;
                }
                case "side": {
                    sideBonus = parseInteger(attrs, "val");
                    break;
                }
                case "back": {
                    backBonus = parseInteger(attrs, "val");
                    break;
                }
                case "high": {
                    highBonus = parseInteger(attrs, "val");
                    break;
                }
                case "low": {
                    lowBonus = parseInteger(attrs, "val");
                    break;
                }
                case "dark": {
                    darkBonus = parseInteger(attrs, "val");
                    break;
                }
                case "rain": {
                    rainBonus = parseInteger(attrs, "val");
                    break;
                }
            }
        }
    }

    /**
     * Gets the condition bonus.
     *
     * @param attacker the attacking character.
     * @param target   the attacked character.
     * @return the bonus of the attacker against the target.
     */
    public double getConditionBonus(L2Character attacker, L2Character target) {
        double mod = 100;
        // Get high or low bonus
        if ((attacker.getZ() - target.getZ()) > 50) {
            mod += highBonus;
        } else if ((attacker.getZ() - target.getZ()) < -50) {
            mod += lowBonus;
        }

        // Get weather bonus
        if (GameTimeController.getInstance().isNight()) {
            mod += darkBonus;
            // else if () No rain support yet.
            // chance += hitConditionBonus.rainBonus;
        }

        // Get side bonus
        switch (Position.getPosition(attacker, target)) {
            case SIDE: {
                mod += sideBonus;
                break;
            }
            case BACK: {
                mod += backBonus;
                break;
            }
            default: {
                mod += frontBonus;
                break;
            }
        }

        // If (mod / 100) is less than 0, return 0, because we can't lower more than 100%.
        return Math.max(mod / 100, 0);
    }

    private static class SingletonHolder {
        protected static final HitConditionBonusData _instance = new HitConditionBonusData();
    }
}