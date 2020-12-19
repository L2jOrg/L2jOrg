package org.l2j.gameserver.data.xml.impl;

import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.enums.ItemGrade;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.item.type.WeaponType;
import org.l2j.gameserver.model.options.BlessedOptions;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;

import static org.l2j.commons.configuration.Configurator.getSettings;

public class BlessedItemOptionsData extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(BlessedItemOptionsData.class);
    private final IntMap<IntMap<ArrayList<BlessedOptions>>> data = new HashIntMap<>();

    private BlessedItemOptionsData() {

    }
    @Override
    protected Path getSchemaFilePath() {
        return getSettings(ServerSettings.class).dataPackDirectory().resolve("data/xsd/BlessedItemOptions.xsd");
    }

    @Override
    public void load() {
        data.clear();
        parseDatapackFile("data/BlessedItemOptions.xml");
        releaseResources();
    }

    @Override
    protected void parseDocument(Document doc, File f) {
        forEach(doc, "list", list -> forEach(list, "option", optionNode -> forEach(optionNode, "effect", effectNode -> {
                    var attr = effectNode.getAttributes();
                    var attrOption = optionNode.getAttributes();

                    int enchant = parseInt(attr, "enchant");
                    int skillId = parseInt(attr, "skill_id");
                    int skillLvl = parseInt(attr, "skill_level");

                    var grade = parseEnum(attrOption, ItemGrade.class, "grade");
                    var itemType = parseEnum(attrOption, WeaponType.class, "type");
                    final var skill = new SkillHolder(skillId, skillLvl);
                    var option = new BlessedOptions(enchant, skill);

                    data.computeIfAbsent(grade.ordinal(), id -> new HashIntMap<>()).computeIfAbsent(itemType.ordinal(), arr -> new ArrayList<BlessedOptions>()).add(option);
                })
        ));
        LOGGER.info("Loaded {} Option Items.", data.size());
    }

    public static void init() {
        getInstance().load();
    }

    public ArrayList<BlessedOptions> getBlessedOptions(ItemGrade itemGrade, WeaponType weaponType, int enchant) {

        ArrayList<BlessedOptions> options = new ArrayList<BlessedOptions>();

        if (!this.data.containsKey(itemGrade.ordinal()) || !this.data.get(itemGrade.ordinal()).containsKey(weaponType.ordinal())) {
            return options;
        }

        var parsedOptions = this.data.get(itemGrade.ordinal()).get(weaponType.ordinal());

        for(BlessedOptions option : parsedOptions) {
            if (enchant >= option.getEnchant()) {
                options.add(option);
            }
        }

        return options;
    }

    public static BlessedItemOptionsData getInstance() {
        return BlessedItemOptionsData.Singleton.INSTANCE;
    }

    private static class Singleton {
        protected static final BlessedItemOptionsData INSTANCE = new BlessedItemOptionsData();
    }
}
