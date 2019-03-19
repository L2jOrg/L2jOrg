package org.l2j.gameserver.engines;

import org.l2j.gameserver.datatables.ItemTable;
import org.l2j.gameserver.enums.CastleSide;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.base.PlayerState;
import org.l2j.gameserver.model.conditions.*;
import org.l2j.gameserver.model.items.L2Item;
import org.l2j.gameserver.model.items.type.ArmorType;
import org.l2j.gameserver.model.items.type.WeaponType;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.model.skills.EffectScope;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.stats.Stats;
import org.l2j.gameserver.model.stats.functions.FuncTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.*;

/**
 * @author mkizub
 */
public abstract class DocumentBase {
    protected final Logger LOGGER = LoggerFactory.getLogger(getClass().getName());
    protected final Map<String, String[]> _tables = new HashMap<>();
    private final File _file;

    protected DocumentBase(File pFile) {
        _file = pFile;
    }

    public Document parse() {
        Document doc = null;
        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringComments(true);
            doc = factory.newDocumentBuilder().parse(_file);
            parseDocument(doc);
        } catch (Exception e) {
            LOGGER.error("Error loading file " + _file, e);
        }
        return doc;
    }

    protected abstract void parseDocument(Document doc);

    protected abstract StatsSet getStatsSet();

    protected abstract String getTableValue(String name);

    protected abstract String getTableValue(String name, int idx);

    protected void resetTable() {
        _tables.clear();
    }

    protected void setTable(String name, String[] table) {
        _tables.put(name, table);
    }

    protected void parseTemplate(Node n, Object template) {
        parseTemplate(n, template, null);
    }

    protected void parseTemplate(Node n, Object template, EffectScope effectScope) {
        Condition condition = null;
        n = n.getFirstChild();
        if (n == null) {
            return;
        }
        if ("cond".equalsIgnoreCase(n.getNodeName())) {
            condition = parseCondition(n.getFirstChild(), template);
            final Node msg = n.getAttributes().getNamedItem("msg");
            final Node msgId = n.getAttributes().getNamedItem("msgId");
            if ((condition != null) && (msg != null)) {
                condition.setMessage(msg.getNodeValue());
            } else if ((condition != null) && (msgId != null)) {
                condition.setMessageId(Integer.decode(getValue(msgId.getNodeValue(), null)));
                final Node addName = n.getAttributes().getNamedItem("addName");
                if ((addName != null) && (Integer.decode(getValue(msgId.getNodeValue(), null)) > 0)) {
                    condition.addName();
                }
            }
            n = n.getNextSibling();
        }
        for (; n != null; n = n.getNextSibling()) {
            final String name = n.getNodeName().toLowerCase();

            switch (name) {
                case "add":
                case "sub":
                case "mul":
                case "div":
                case "set":
                case "enchant":
                case "enchanthp": {
                    attachFunc(n, template, name, condition);
                }
            }
        }
    }

    protected void attachFunc(Node n, Object template, String functionName, Condition attachCond) {
        final Stats stat = Stats.valueOfXml(n.getAttributes().getNamedItem("stat").getNodeValue());
        int order = -1;
        final Node orderNode = n.getAttributes().getNamedItem("order");
        if (orderNode != null) {
            order = Integer.parseInt(orderNode.getNodeValue());
        }

        final String valueString = n.getAttributes().getNamedItem("val").getNodeValue();
        double value;
        if (valueString.charAt(0) == '#') {
            value = Double.parseDouble(getTableValue(valueString));
        } else {
            value = Double.parseDouble(valueString);
        }

        final Condition applayCond = parseCondition(n.getFirstChild(), template);
        final FuncTemplate ft = new FuncTemplate(attachCond, applayCond, functionName, order, stat, value);
        if (template instanceof L2Item) {
            ((L2Item) template).addFunctionTemplate(ft);
        } else {
            throw new RuntimeException("Attaching stat to a non-effect template [" + template + "]!!!");
        }
    }

    protected Condition parseCondition(Node n, Object template) {
        while ((n != null) && (n.getNodeType() != Node.ELEMENT_NODE)) {
            n = n.getNextSibling();
        }

        Condition condition = null;
        if (n != null) {
            switch (n.getNodeName().toLowerCase()) {
                case "and": {
                    condition = parseLogicAnd(n, template);
                    break;
                }
                case "or": {
                    condition = parseLogicOr(n, template);
                    break;
                }
                case "not": {
                    condition = parseLogicNot(n, template);
                    break;
                }
                case "player": {
                    condition = parsePlayerCondition(n, template);
                    break;
                }
                case "target": {
                    condition = parseTargetCondition(n, template);
                    break;
                }
                case "using": {
                    condition = parseUsingCondition(n);
                    break;
                }
                case "game": {
                    condition = parseGameCondition(n);
                    break;
                }
            }
        }
        return condition;
    }

    protected Condition parseLogicAnd(Node n, Object template) {
        final ConditionLogicAnd cond = new ConditionLogicAnd();
        for (n = n.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                cond.add(parseCondition(n, template));
            }
        }
        if ((cond.conditions == null) || (cond.conditions.length == 0)) {
            LOGGER.error("Empty <and> condition in " + _file);
        }
        return cond;
    }

    protected Condition parseLogicOr(Node n, Object template) {
        final ConditionLogicOr cond = new ConditionLogicOr();
        for (n = n.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                cond.add(parseCondition(n, template));
            }
        }
        if ((cond.conditions == null) || (cond.conditions.length == 0)) {
            LOGGER.error("Empty <or> condition in " + _file);
        }
        return cond;
    }

    protected Condition parseLogicNot(Node n, Object template) {
        for (n = n.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                return new ConditionLogicNot(parseCondition(n, template));
            }
        }
        LOGGER.error("Empty <not> condition in " + _file);
        return null;
    }

    protected Condition parsePlayerCondition(Node n, Object template) {
        Condition cond = null;
        final NamedNodeMap attrs = n.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
            final Node a = attrs.item(i);
            switch (a.getNodeName().toLowerCase()) {
                case "races": {
                    final String[] racesVal = a.getNodeValue().split(",");
                    final Race[] races = new Race[racesVal.length];
                    for (int r = 0; r < racesVal.length; r++) {
                        if (racesVal[r] != null) {
                            races[r] = Race.valueOf(racesVal[r]);
                        }
                    }
                    cond = joinAnd(cond, new ConditionPlayerRace(races));
                    break;
                }
                case "level": {
                    final int lvl = Integer.decode(getValue(a.getNodeValue(), template));
                    cond = joinAnd(cond, new ConditionPlayerLevel(lvl));
                    break;
                }
                case "levelrange": {
                    final String[] range = getValue(a.getNodeValue(), template).split(";");
                    if (range.length == 2) {
                        final int[] lvlRange = new int[2];
                        lvlRange[0] = Integer.decode(getValue(a.getNodeValue(), template).split(";")[0]);
                        lvlRange[1] = Integer.decode(getValue(a.getNodeValue(), template).split(";")[1]);
                        cond = joinAnd(cond, new ConditionPlayerLevelRange(lvlRange));
                    }
                    break;
                }
                case "resting": {
                    final boolean val = Boolean.parseBoolean(a.getNodeValue());
                    cond = joinAnd(cond, new ConditionPlayerState(PlayerState.RESTING, val));
                    break;
                }
                case "flying": {
                    final boolean val = Boolean.parseBoolean(a.getNodeValue());
                    cond = joinAnd(cond, new ConditionPlayerState(PlayerState.FLYING, val));
                    break;
                }
                case "moving": {
                    final boolean val = Boolean.parseBoolean(a.getNodeValue());
                    cond = joinAnd(cond, new ConditionPlayerState(PlayerState.MOVING, val));
                    break;
                }
                case "running": {
                    final boolean val = Boolean.parseBoolean(a.getNodeValue());
                    cond = joinAnd(cond, new ConditionPlayerState(PlayerState.RUNNING, val));
                    break;
                }
                case "standing": {
                    final boolean val = Boolean.parseBoolean(a.getNodeValue());
                    cond = joinAnd(cond, new ConditionPlayerState(PlayerState.STANDING, val));
                    break;
                }
                case "behind": {
                    final boolean val = Boolean.parseBoolean(a.getNodeValue());
                    cond = joinAnd(cond, new ConditionPlayerState(PlayerState.BEHIND, val));
                    break;
                }
                case "front": {
                    final boolean val = Boolean.parseBoolean(a.getNodeValue());
                    cond = joinAnd(cond, new ConditionPlayerState(PlayerState.FRONT, val));
                    break;
                }
                case "chaotic": {
                    final boolean val = Boolean.parseBoolean(a.getNodeValue());
                    cond = joinAnd(cond, new ConditionPlayerState(PlayerState.CHAOTIC, val));
                    break;
                }
                case "olympiad": {
                    final boolean val = Boolean.parseBoolean(a.getNodeValue());
                    cond = joinAnd(cond, new ConditionPlayerState(PlayerState.OLYMPIAD, val));
                    break;
                }
                case "ishero": {
                    final boolean val = Boolean.parseBoolean(a.getNodeValue());
                    cond = joinAnd(cond, new ConditionPlayerIsHero(val));
                    break;
                }
                case "ispvpflagged": {
                    final boolean val = Boolean.parseBoolean(a.getNodeValue());
                    cond = joinAnd(cond, new ConditionPlayerIsPvpFlagged(val));
                    break;
                }
                case "transformationid": {
                    final int id = Integer.parseInt(a.getNodeValue());
                    cond = joinAnd(cond, new ConditionPlayerTransformationId(id));
                    break;
                }
                case "hp": {
                    final int hp = Integer.decode(getValue(a.getNodeValue(), template));
                    cond = joinAnd(cond, new ConditionPlayerHp(hp));
                    break;
                }
                case "mp": {
                    final int mp = Integer.decode(getValue(a.getNodeValue(), template));
                    cond = joinAnd(cond, new ConditionPlayerMp(mp));
                    break;
                }
                case "cp": {
                    final int cp = Integer.decode(getValue(a.getNodeValue(), template));
                    cond = joinAnd(cond, new ConditionPlayerCp(cp));
                    break;
                }
                case "grade": {
                    final int expIndex = Integer.decode(getValue(a.getNodeValue(), template));
                    cond = joinAnd(cond, new ConditionPlayerGrade(expIndex));
                    break;
                }
                case "pkcount": {
                    final int expIndex = Integer.decode(getValue(a.getNodeValue(), template));
                    cond = joinAnd(cond, new ConditionPlayerPkCount(expIndex));
                    break;
                }
                case "siegezone": {
                    final int value = Integer.decode(getValue(a.getNodeValue(), null));
                    cond = joinAnd(cond, new ConditionSiegeZone(value, true));
                    break;
                }
                case "siegeside": {
                    final int value = Integer.decode(getValue(a.getNodeValue(), null));
                    cond = joinAnd(cond, new ConditionPlayerSiegeSide(value));
                    break;
                }
                case "charges": {
                    final int value = Integer.decode(getValue(a.getNodeValue(), template));
                    cond = joinAnd(cond, new ConditionPlayerCharges(value));
                    break;
                }
                case "souls": {
                    final int value = Integer.decode(getValue(a.getNodeValue(), template));
                    cond = joinAnd(cond, new ConditionPlayerSouls(value));
                    break;
                }
                case "weight": {
                    final int weight = Integer.decode(getValue(a.getNodeValue(), null));
                    cond = joinAnd(cond, new ConditionPlayerWeight(weight));
                    break;
                }
                case "invsize": {
                    final int size = Integer.decode(getValue(a.getNodeValue(), null));
                    cond = joinAnd(cond, new ConditionPlayerInvSize(size));
                    break;
                }
                case "isclanleader": {
                    final boolean val = Boolean.parseBoolean(a.getNodeValue());
                    cond = joinAnd(cond, new ConditionPlayerIsClanLeader(val));
                    break;
                }
                case "pledgeclass": {
                    final int pledgeClass = Integer.decode(getValue(a.getNodeValue(), null));
                    cond = joinAnd(cond, new ConditionPlayerPledgeClass(pledgeClass));
                    break;
                }
                case "clanhall": {
                    final StringTokenizer st = new StringTokenizer(a.getNodeValue(), ",");
                    final ArrayList<Integer> array = new ArrayList<>(st.countTokens());
                    while (st.hasMoreTokens()) {
                        final String item = st.nextToken().trim();
                        array.add(Integer.decode(getValue(item, template)));
                    }
                    cond = joinAnd(cond, new ConditionPlayerHasClanHall(array));
                    break;
                }
                case "fort": {
                    final int fort = Integer.decode(getValue(a.getNodeValue(), null));
                    cond = joinAnd(cond, new ConditionPlayerHasFort(fort));
                    break;
                }
                case "castle": {
                    final int castle = Integer.decode(getValue(a.getNodeValue(), null));
                    cond = joinAnd(cond, new ConditionPlayerHasCastle(castle));
                    break;
                }
                case "sex": {
                    final int sex = Integer.decode(getValue(a.getNodeValue(), null));
                    cond = joinAnd(cond, new ConditionPlayerSex(sex));
                    break;
                }
                case "flymounted": {
                    final boolean val = Boolean.parseBoolean(a.getNodeValue());
                    cond = joinAnd(cond, new ConditionPlayerFlyMounted(val));
                    break;
                }
                case "vehiclemounted": {
                    final boolean val = Boolean.parseBoolean(a.getNodeValue());
                    cond = joinAnd(cond, new ConditionPlayerVehicleMounted(val));
                    break;
                }
                case "landingzone": {
                    final boolean val = Boolean.parseBoolean(a.getNodeValue());
                    cond = joinAnd(cond, new ConditionPlayerLandingZone(val));
                    break;
                }
                case "active_effect_id": {
                    final int effect_id = Integer.decode(getValue(a.getNodeValue(), template));
                    cond = joinAnd(cond, new ConditionPlayerActiveEffectId(effect_id));
                    break;
                }
                case "active_effect_id_lvl": {
                    final String val = getValue(a.getNodeValue(), template);
                    final int effect_id = Integer.decode(getValue(val.split(",")[0], template));
                    final int effect_lvl = Integer.decode(getValue(val.split(",")[1], template));
                    cond = joinAnd(cond, new ConditionPlayerActiveEffectId(effect_id, effect_lvl));
                    break;
                }
                case "active_skill_id": {
                    final int skill_id = Integer.decode(getValue(a.getNodeValue(), template));
                    cond = joinAnd(cond, new ConditionPlayerActiveSkillId(skill_id));
                    break;
                }
                case "active_skill_id_lvl": {
                    final String val = getValue(a.getNodeValue(), template);
                    final int skill_id = Integer.decode(getValue(val.split(",")[0], template));
                    final int skill_lvl = Integer.decode(getValue(val.split(",")[1], template));
                    cond = joinAnd(cond, new ConditionPlayerActiveSkillId(skill_id, skill_lvl));
                    break;
                }
                case "class_id_restriction": {
                    final StringTokenizer st = new StringTokenizer(a.getNodeValue(), ",");
                    final ArrayList<Integer> array = new ArrayList<>(st.countTokens());
                    while (st.hasMoreTokens()) {
                        final String item = st.nextToken().trim();
                        array.add(Integer.decode(getValue(item, template)));
                    }
                    cond = joinAnd(cond, new ConditionPlayerClassIdRestriction(array));
                    break;
                }
                case "subclass": {
                    final boolean val = Boolean.parseBoolean(a.getNodeValue());
                    cond = joinAnd(cond, new ConditionPlayerSubclass(val));
                    break;
                }
                case "dualclass": {
                    final boolean val = Boolean.parseBoolean(a.getNodeValue());
                    cond = joinAnd(cond, new ConditionPlayerDualclass(val));
                    break;
                }
                case "canswitchsubclass": {
                    cond = joinAnd(cond, new ConditionPlayerCanSwitchSubclass(Integer.decode(a.getNodeValue())));
                    break;
                }
                case "instanceid": {
                    final StringTokenizer st = new StringTokenizer(a.getNodeValue(), ",");
                    final ArrayList<Integer> array = new ArrayList<>(st.countTokens());
                    while (st.hasMoreTokens()) {
                        final String item = st.nextToken().trim();
                        array.add(Integer.decode(getValue(item, template)));
                    }
                    cond = joinAnd(cond, new ConditionPlayerInstanceId(array));
                    break;
                }
                case "agathionid": {
                    final int agathionId = Integer.decode(a.getNodeValue());
                    cond = joinAnd(cond, new ConditionPlayerAgathionId(agathionId));
                    break;
                }
                case "cloakstatus": {
                    final boolean val = Boolean.parseBoolean(a.getNodeValue());
                    cond = joinAnd(cond, new ConditionPlayerCloakStatus(val));
                    break;
                }
                case "hassummon": {
                    final boolean val = Boolean.parseBoolean(a.getNodeValue());
                    cond = joinAnd(cond, new ConditionPlayerHasSummon(val));
                    break;
                }
                case "haspet": {
                    final StringTokenizer st = new StringTokenizer(a.getNodeValue(), ",");
                    final ArrayList<Integer> array = new ArrayList<>(st.countTokens());
                    while (st.hasMoreTokens()) {
                        final String item = st.nextToken().trim();
                        array.add(Integer.decode(getValue(item, template)));
                    }
                    cond = joinAnd(cond, new ConditionPlayerHasPet(array));
                    break;
                }
                case "servitornpcid": {
                    final StringTokenizer st = new StringTokenizer(a.getNodeValue(), ",");
                    final ArrayList<Integer> array = new ArrayList<>(st.countTokens());
                    while (st.hasMoreTokens()) {
                        final String item = st.nextToken().trim();
                        array.add(Integer.decode(getValue(item, null)));
                    }
                    cond = joinAnd(cond, new ConditionPlayerServitorNpcId(array));
                    break;
                }
                case "npcidradius": {
                    final StringTokenizer st = new StringTokenizer(a.getNodeValue(), ",");
                    if (st.countTokens() == 3) {
                        final String[] ids = st.nextToken().split(";");
                        final int[] npcIds = new int[ids.length];
                        for (int index = 0; index < ids.length; index++) {
                            npcIds[index] = Integer.parseInt(getValue(ids[index], template));
                        }
                        final int radius = Integer.parseInt(st.nextToken());
                        final boolean val = Boolean.parseBoolean(st.nextToken());
                        cond = joinAnd(cond, new ConditionPlayerRangeFromNpc(npcIds, radius, val));
                    }
                    break;
                }
                case "summonednpcidradius": {
                    final StringTokenizer st = new StringTokenizer(a.getNodeValue(), ",");
                    if (st.countTokens() == 3) {
                        final String[] ids = st.nextToken().split(";");
                        final int[] npcIds = new int[ids.length];
                        for (int index = 0; index < ids.length; index++) {
                            npcIds[index] = Integer.parseInt(getValue(ids[index], template));
                        }
                        final int radius = Integer.parseInt(st.nextToken());
                        final boolean val = Boolean.parseBoolean(st.nextToken());
                        cond = joinAnd(cond, new ConditionPlayerRangeFromSummonedNpc(npcIds, radius, val));
                    }
                    break;
                }
                case "callpc": {
                    cond = joinAnd(cond, new ConditionPlayerCallPc(Boolean.parseBoolean(a.getNodeValue())));
                    break;
                }
                case "cancreatebase": {
                    cond = joinAnd(cond, new ConditionPlayerCanCreateBase(Boolean.parseBoolean(a.getNodeValue())));
                    break;
                }
                case "canescape": {
                    cond = joinAnd(cond, new ConditionPlayerCanEscape(Boolean.parseBoolean(a.getNodeValue())));
                    break;
                }
                case "canrefuelairship": {
                    cond = joinAnd(cond, new ConditionPlayerCanRefuelAirship(Integer.parseInt(a.getNodeValue())));
                    break;
                }
                case "canresurrect": {
                    cond = joinAnd(cond, new ConditionPlayerCanResurrect(Boolean.parseBoolean(a.getNodeValue())));
                    break;
                }
                case "cansummonpet": {
                    cond = joinAnd(cond, new ConditionPlayerCanSummonPet(Boolean.parseBoolean(a.getNodeValue())));
                    break;
                }
                case "cansummonservitor": {
                    cond = joinAnd(cond, new ConditionPlayerCanSummonServitor(Boolean.parseBoolean(a.getNodeValue())));
                    break;
                }
                case "hasfreesummonpoints": {
                    cond = joinAnd(cond, new ConditionPlayerHasFreeSummonPoints(Integer.parseInt(a.getNodeValue())));
                    break;
                }
                case "hasfreeteleportbookmarkslots": {
                    cond = joinAnd(cond, new ConditionPlayerHasFreeTeleportBookmarkSlots(Integer.parseInt(a.getNodeValue())));
                    break;
                }
                case "cansummonsiegegolem": {
                    cond = joinAnd(cond, new ConditionPlayerCanSummonSiegeGolem(Boolean.parseBoolean(a.getNodeValue())));
                    break;
                }
                case "cansweep": {
                    cond = joinAnd(cond, new ConditionPlayerCanSweep(Boolean.parseBoolean(a.getNodeValue())));
                    break;
                }
                case "cantakecastle": {
                    cond = joinAnd(cond, new ConditionPlayerCanTakeCastle(Boolean.parseBoolean(a.getNodeValue())));
                    break;
                }
                case "cantakefort": {
                    cond = joinAnd(cond, new ConditionPlayerCanTakeFort(Boolean.parseBoolean(a.getNodeValue())));
                    break;
                }
                case "cantransform": {
                    cond = joinAnd(cond, new ConditionPlayerCanTransform(Boolean.parseBoolean(a.getNodeValue())));
                    break;
                }
                case "canuntransform": {
                    cond = joinAnd(cond, new ConditionPlayerCanUntransform(Boolean.parseBoolean(a.getNodeValue())));
                    break;
                }
                case "insidezoneid": {
                    final StringTokenizer st = new StringTokenizer(a.getNodeValue(), ",");
                    final List<Integer> array = new ArrayList<>(st.countTokens());
                    while (st.hasMoreTokens()) {
                        final String item = st.nextToken().trim();
                        array.add(Integer.decode(getValue(item, template)));
                    }
                    cond = joinAnd(cond, new ConditionPlayerInsideZoneId(array));
                    break;
                }
                case "checkabnormal": {
                    final String value = a.getNodeValue();
                    if (value.contains(",")) {
                        final String[] values = value.split(",");
                        cond = joinAnd(cond, new ConditionPlayerCheckAbnormal(AbnormalType.valueOf(values[0]), Integer.decode(getValue(values[1], template))));
                    } else {
                        cond = joinAnd(cond, new ConditionPlayerCheckAbnormal(AbnormalType.valueOf(value)));
                    }
                    break;
                }
                case "categorytype": {
                    final String[] values = a.getNodeValue().split(",");
                    final Set<CategoryType> array = new HashSet<>(values.length);
                    for (String value : values) {
                        array.add(CategoryType.valueOf(getValue(value, template)));
                    }
                    cond = joinAnd(cond, new ConditionCategoryType(array));
                    break;
                }
                case "immobile": {
                    cond = joinAnd(cond, new ConditionPlayerImmobile(Boolean.parseBoolean(a.getNodeValue())));
                    break;
                }
                case "incombat": {
                    cond = joinAnd(cond, new ConditionPlayerIsInCombat(Boolean.parseBoolean(a.getNodeValue())));
                    break;
                }
                case "isonside": {
                    cond = joinAnd(cond, new ConditionPlayerIsOnSide(Enum.valueOf(CastleSide.class, a.getNodeValue())));
                    break;
                }
                case "ininstance": {
                    cond = joinAnd(cond, new ConditionPlayerInInstance(Boolean.parseBoolean(a.getNodeValue())));
                    break;
                }
                case "minimumvitalitypoints": {
                    final int count = Integer.decode(getValue(a.getNodeValue(), null));
                    cond = joinAnd(cond, new ConditionMinimumVitalityPoints(count));
                    break;
                }
            }
        }

        if (cond == null) {
            LOGGER.error("Unrecognized <player> condition in " + _file);
        }
        return cond;
    }

    protected Condition parseTargetCondition(Node n, Object template) {
        Condition cond = null;
        final NamedNodeMap attrs = n.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
            final Node a = attrs.item(i);
            switch (a.getNodeName().toLowerCase()) {
                case "aggro": {
                    final boolean val = Boolean.parseBoolean(a.getNodeValue());
                    cond = joinAnd(cond, new ConditionTargetAggro(val));
                    break;
                }
                case "siegezone": {
                    final int value = Integer.decode(getValue(a.getNodeValue(), null));
                    cond = joinAnd(cond, new ConditionSiegeZone(value, false));
                    break;
                }
                case "level": {
                    final int lvl = Integer.decode(getValue(a.getNodeValue(), template));
                    cond = joinAnd(cond, new ConditionTargetLevel(lvl));
                    break;
                }
                case "levelrange": {
                    final String[] range = getValue(a.getNodeValue(), template).split(";");
                    if (range.length == 2) {
                        final int[] lvlRange = new int[2];
                        lvlRange[0] = Integer.decode(getValue(a.getNodeValue(), template).split(";")[0]);
                        lvlRange[1] = Integer.decode(getValue(a.getNodeValue(), template).split(";")[1]);
                        cond = joinAnd(cond, new ConditionTargetLevelRange(lvlRange));
                    }
                    break;
                }
                case "mypartyexceptme": {
                    cond = joinAnd(cond, new ConditionTargetMyPartyExceptMe(Boolean.parseBoolean(a.getNodeValue())));
                    break;
                }
                case "playable": {
                    cond = joinAnd(cond, new ConditionTargetPlayable());
                    break;
                }
                case "player": {
                    cond = joinAnd(cond, new ConditionTargetPlayer());
                    break;
                }
                case "class_id_restriction": {
                    final StringTokenizer st = new StringTokenizer(a.getNodeValue(), ",");
                    final List<Integer> array = new ArrayList<>(st.countTokens());
                    while (st.hasMoreTokens()) {
                        final String item = st.nextToken().trim();
                        array.add(Integer.decode(getValue(item, null)));
                    }
                    cond = joinAnd(cond, new ConditionTargetClassIdRestriction(array));
                    break;
                }
                case "active_effect_id": {
                    final int effect_id = Integer.decode(getValue(a.getNodeValue(), template));
                    cond = joinAnd(cond, new ConditionTargetActiveEffectId(effect_id));
                    break;
                }
                case "active_effect_id_lvl": {
                    final String val = getValue(a.getNodeValue(), template);
                    final int effect_id = Integer.decode(getValue(val.split(",")[0], template));
                    final int effect_lvl = Integer.decode(getValue(val.split(",")[1], template));
                    cond = joinAnd(cond, new ConditionTargetActiveEffectId(effect_id, effect_lvl));
                    break;
                }
                case "active_skill_id": {
                    final int skill_id = Integer.decode(getValue(a.getNodeValue(), template));
                    cond = joinAnd(cond, new ConditionTargetActiveSkillId(skill_id));
                    break;
                }
                case "active_skill_id_lvl": {
                    final String val = getValue(a.getNodeValue(), template);
                    final int skill_id = Integer.decode(getValue(val.split(",")[0], template));
                    final int skill_lvl = Integer.decode(getValue(val.split(",")[1], template));
                    cond = joinAnd(cond, new ConditionTargetActiveSkillId(skill_id, skill_lvl));
                    break;
                }
                case "abnormaltype": {
                    final AbnormalType abnormalType = AbnormalType.getAbnormalType(getValue(a.getNodeValue(), template));
                    cond = joinAnd(cond, new ConditionTargetAbnormalType(abnormalType));
                    break;
                }
                case "mindistance": {
                    final int distance = Integer.decode(getValue(a.getNodeValue(), null));
                    cond = joinAnd(cond, new ConditionMinDistance(distance * distance));
                    break;
                }
                case "race": {
                    cond = joinAnd(cond, new ConditionTargetRace(Race.valueOf(a.getNodeValue())));
                    break;
                }
                case "using": {
                    int mask = 0;
                    final StringTokenizer st = new StringTokenizer(a.getNodeValue(), ",");
                    while (st.hasMoreTokens()) {
                        final String item = st.nextToken().trim();
                        for (WeaponType wt : WeaponType.values()) {
                            if (wt.name().equals(item)) {
                                mask |= wt.mask();
                                break;
                            }
                        }
                        for (ArmorType at : ArmorType.values()) {
                            if (at.name().equals(item)) {
                                mask |= at.mask();
                                break;
                            }
                        }
                    }
                    cond = joinAnd(cond, new ConditionTargetUsesWeaponKind(mask));
                    break;
                }
                case "npcid": {
                    final StringTokenizer st = new StringTokenizer(a.getNodeValue(), ",");
                    final List<Integer> array = new ArrayList<>(st.countTokens());
                    while (st.hasMoreTokens()) {
                        final String item = st.nextToken().trim();
                        array.add(Integer.decode(getValue(item, null)));
                    }
                    cond = joinAnd(cond, new ConditionTargetNpcId(array));
                    break;
                }
                case "npctype": {
                    final String values = getValue(a.getNodeValue(), template).trim();
                    final String[] valuesSplit = values.split(",");
                    final InstanceType[] types = new InstanceType[valuesSplit.length];
                    InstanceType type;
                    for (int j = 0; j < valuesSplit.length; j++) {
                        type = Enum.valueOf(InstanceType.class, valuesSplit[j]);
                        if (type == null) {
                            throw new IllegalArgumentException("Instance type not recognized: " + valuesSplit[j]);
                        }
                        types[j] = type;
                    }
                    cond = joinAnd(cond, new ConditionTargetNpcType(types));
                    break;
                }
                case "weight": {
                    final int weight = Integer.decode(getValue(a.getNodeValue(), null));
                    cond = joinAnd(cond, new ConditionTargetWeight(weight));
                    break;
                }
                case "invsize": {
                    final int size = Integer.decode(getValue(a.getNodeValue(), null));
                    cond = joinAnd(cond, new ConditionTargetInvSize(size));
                    break;
                }
                case "checkcrteffect": {
                    cond = joinAnd(cond, new ConditionTargetCheckCrtEffect(Boolean.parseBoolean(a.getNodeValue())));
                    break;
                }
            }
        }

        if (cond == null) {
            LOGGER.error("Unrecognized <target> condition in " + _file);
        }
        return cond;
    }

    protected Condition parseUsingCondition(Node n) {
        Condition cond = null;
        final NamedNodeMap attrs = n.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
            final Node a = attrs.item(i);
            switch (a.getNodeName().toLowerCase()) {
                case "kind": {
                    int mask = 0;
                    final StringTokenizer st = new StringTokenizer(a.getNodeValue(), ",");
                    while (st.hasMoreTokens()) {
                        final int old = mask;
                        final String item = st.nextToken().trim();
                        for (WeaponType wt : WeaponType.values()) {
                            if (wt.name().equals(item)) {
                                mask |= wt.mask();
                            }
                        }

                        for (ArmorType at : ArmorType.values()) {
                            if (at.name().equals(item)) {
                                mask |= at.mask();
                            }
                        }

                        if (old == mask) {
                            LOGGER.info("[parseUsingCondition=\"kind\"] Unknown item type name: " + item);
                        }
                    }
                    cond = joinAnd(cond, new ConditionUsingItemType(mask));
                    break;
                }
                case "slot": {
                    int mask = 0;
                    final StringTokenizer st = new StringTokenizer(a.getNodeValue(), ",");
                    while (st.hasMoreTokens()) {
                        final int old = mask;
                        final String item = st.nextToken().trim();
                        if (ItemTable.SLOTS.containsKey(item)) {
                            mask |= ItemTable.SLOTS.get(item);
                        }

                        if (old == mask) {
                            LOGGER.info("[parseUsingCondition=\"slot\"] Unknown item slot name: " + item);
                        }
                    }
                    cond = joinAnd(cond, new ConditionUsingSlotType(mask));
                    break;
                }
                case "skill": {
                    final int id = Integer.parseInt(a.getNodeValue());
                    cond = joinAnd(cond, new ConditionUsingSkill(id));
                    break;
                }
                case "slotitem": {
                    final StringTokenizer st = new StringTokenizer(a.getNodeValue(), ";");
                    final int id = Integer.parseInt(st.nextToken().trim());
                    final int slot = Integer.parseInt(st.nextToken().trim());
                    int enchant = 0;
                    if (st.hasMoreTokens()) {
                        enchant = Integer.parseInt(st.nextToken().trim());
                    }
                    cond = joinAnd(cond, new ConditionSlotItemId(slot, id, enchant));
                    break;
                }
                case "weaponchange": {
                    final boolean val = Boolean.parseBoolean(a.getNodeValue());
                    cond = joinAnd(cond, new ConditionChangeWeapon(val));
                    break;
                }
            }
        }

        if (cond == null) {
            LOGGER.error("Unrecognized <using> condition in " + _file);
        }
        return cond;
    }

    protected Condition parseGameCondition(Node n) {
        Condition cond = null;
        final NamedNodeMap attrs = n.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
            final Node a = attrs.item(i);
            if ("skill".equalsIgnoreCase(a.getNodeName())) {
                final boolean val = Boolean.parseBoolean(a.getNodeValue());
                cond = joinAnd(cond, new ConditionWithSkill(val));
            }
            if ("night".equalsIgnoreCase(a.getNodeName())) {
                final boolean val = Boolean.parseBoolean(a.getNodeValue());
                cond = joinAnd(cond, new ConditionGameTime(ConditionGameTime.CheckGameTime.NIGHT, val));
            }
            if ("chance".equalsIgnoreCase(a.getNodeName())) {
                final int val = Integer.decode(getValue(a.getNodeValue(), null));
                cond = joinAnd(cond, new ConditionGameChance(val));
            }
        }
        if (cond == null) {
            LOGGER.error("Unrecognized <game> condition in " + _file);
        }
        return cond;
    }

    protected void parseTable(Node n) {
        final NamedNodeMap attrs = n.getAttributes();
        final String name = attrs.getNamedItem("name").getNodeValue();
        if (name.charAt(0) != '#') {
            throw new IllegalArgumentException("Table name must start with #");
        }
        final StringTokenizer data = new StringTokenizer(n.getFirstChild().getNodeValue());
        final List<String> array = new ArrayList<>(data.countTokens());
        while (data.hasMoreTokens()) {
            array.add(data.nextToken());
        }
        setTable(name, array.toArray(new String[array.size()]));
    }

    protected void parseBeanSet(Node n, StatsSet set, Integer level) {
        final String name = n.getAttributes().getNamedItem("name").getNodeValue().trim();
        final String value = n.getAttributes().getNamedItem("val").getNodeValue().trim();
        final char ch = value.isEmpty() ? ' ' : value.charAt(0);
        if ((ch == '#') || (ch == '-') || Character.isDigit(ch)) {
            set.set(name, getValue(value, level));
        } else {
            set.set(name, value);
        }
    }

    protected void setExtractableSkillData(StatsSet set, String value) {
        set.set("capsuled_items_skill", value);
    }

    protected String getValue(String value, Object template) {
        // is it a table?
        if (value.charAt(0) == '#') {
            if (template instanceof Skill) {
                return getTableValue(value);
            } else if (template instanceof Integer) {
                return getTableValue(value, (Integer) template);
            } else {
                throw new IllegalStateException();
            }
        }
        return value;
    }

    protected Condition joinAnd(Condition cond, Condition c) {
        if (cond == null) {
            return c;
        }
        if (cond instanceof ConditionLogicAnd) {
            ((ConditionLogicAnd) cond).add(c);
            return cond;
        }
        final ConditionLogicAnd and = new ConditionLogicAnd();
        and.add(cond);
        and.add(c);
        return and;
    }
}
