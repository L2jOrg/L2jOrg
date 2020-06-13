/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

/**
 * @author KenM
 */
public final class ExBasicActionList extends ServerPacket {
    //@formatter:off
    public static final int[] ACTIONS_ON_TRANSFORM =
            {
                    1, 2, 3, 4, 5,
                    6, 7, 8, 9,
                    11, 15, 16, 17,
                    18, 19, 21, 22,
                    23, 32, 36, 39,
                    40, 41, 42, 43,
                    44, 45, 46, 47,
                    48, 50, 52, 53,
                    54, 55, 56, 57,
                    63, 64, 65, 70,
                    86, 92, 1000, 1001, 1003,
                    1004, 1005, 1006, 1007,
                    1008, 1009, 1010, 1011,
                    1012, 1013, 1014, 1015,
                    1016, 1017, 1018, 1019,
                    1020, 1021, 1022, 1023,
                    1024, 1025, 1026, 1027,
                    1028, 1029, 1030, 1031,
                    1032, 1033, 1034, 1035,
                    1036, 1037, 1038, 1039,
                    1040, 1041, 1042, 1043,
                    1044, 1045, 1046, 1047,
                    1048, 1049, 1050, 1051,
                    1052, 1053, 1054, 1055,
                    1056, 1057, 1058, 1059,
                    1060, 1061, 1062, 1063,
                    1064, 1065, 1066, 1067,
                    1068, 1069, 1070, 1071,
                    1072, 1073, 1074, 1075,
                    1076, 1077, 1078, 1079,
                    1080, 1081, 1082, 1083,
                    1084, 1089, 1090, 1091,
                    1092, 1093, 1094, 1095,
                    1096, 1097, 1098, 1099,
                    1100, 1101, 1102, 1103,
                    1104, 1106, 1107, 1108,
                    1109, 1110, 1111, 1113,
                    1114, 1115, 1116, 1117,
                    1118, 1120, 1121, 1124,
                    1125, 1126, 1127, 1128,
                    1129, 1130, 1131, 1132,
                    1133, 1134, 1135, 1136,
                    1137, 1138, 1139, 1140,
                    1141, 1142, 1143, 1144,
                    1145, 1146, 1147, 1148,
                    1149, 1150, 1151, 1152,
                    1153, 1154, 1155
            };

    public static final int[] DEFAULT_ACTION_LIST =
            {
                    0, 1, 2, 3,
                    4, 5, 6, 7,
                    8, 9, 10, 11,
                    12, 13, 14, 15,
                    16, 17, 18, 19,
                    20, 21, 22, 23,
                    24, 25, 26, 27,
                    28, 29, 30, 31,
                    32, 33, 34, 35,
                    36, 37, 38, 39,
                    40, 41, 42, 43,
                    44, 45, 46, 47,
                    48, 49, 50, 51,
                    52, 53, 54, 55,
                    56, 57, 58, 59,
                    60, 61, 62, 63,
                    64, 65, 66, 67,
                    68, 69, 70, 71,
                    72, 73, 74, 76,
                    77, 78, 79, 80,
                    81, 82, 83, 84,
                    85, 86, 87, 88,
                    89, 90, 92, 93,
                    94, 96, 97, 98, 1000, 1001,
                    1002, 1003, 1004, 1005,
                    1006, 1007, 1008, 1009,
                    1010, 1011, 1012, 1013,
                    1014, 1015, 1016, 1017,
                    1018, 1019, 1020, 1021,
                    1022, 1023, 1024, 1025,
                    1026, 1027, 1028, 1029,
                    1030, 1031, 1032, 1033,
                    1034, 1035, 1036, 1037,
                    1038, 1039, 1040, 1041,
                    1042, 1043, 1044, 1045,
                    1046, 1047, 1048, 1049,
                    1050, 1051, 1052, 1053,
                    1054, 1055, 1056, 1057,
                    1058, 1059, 1060, 1061,
                    1062, 1063, 1064, 1065,
                    1066, 1067, 1068, 1069,
                    1070, 1071, 1072, 1073,
                    1074, 1075, 1076, 1077,
                    1078, 1079, 1080, 1081,
                    1082, 1083, 1084, 1085,
                    1086, 1087, 1088, 1089, 1090,
                    1091, 1092, 1093, 1094,
                    1095, 1096, 1097, 1098,
                    1099, 1100, 1101, 1102,
                    1103, 1104, 1106, 1107,
                    1108, 1109, 1110, 1111,
                    1112, 1113, 1114, 1115,
                    1116, 1117, 1118, 1119,
                    1120, 1121, 1122, 1123,
                    1124, 1125, 1126, 1127,
                    1128, 1129, 1130, 1131,
                    1132, 1133, 1134, 1135,
                    1136, 1137, 1138, 1139,
                    1140, 1141, 1142, 1143,
                    1144, 1145, 1146, 1147,
                    1148, 1149, 1150, 1151,
                    1152, 1153, 1154, 1155,
                    5000, 5001, 5002, 5003,
                    5004, 5005, 5006, 5007,
                    5008, 5009, 5010, 5011,
                    5012, 5013, 5014, 5015
            };
    //@formatter:on

    public static final ExBasicActionList STATIC_PACKET = new ExBasicActionList(DEFAULT_ACTION_LIST);

    private final int[] _actionIds;

    public ExBasicActionList(int[] actionIds) {
        _actionIds = actionIds;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_BASIC_ACTION_LIST);

        writeInt(_actionIds.length);
        for (int _actionId : _actionIds) {
            writeInt(_actionId);
        }
    }

}
