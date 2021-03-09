/*
 * Copyright © 2019-2021 L2JOrg
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
package org.l2j.commons.database;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * @author JoeAlisson
 */
public class HandlersSupport {

    @SuppressWarnings("rawTypes")
    private static final Map<String, TypeHandler> MAP = new HashMap<>();

    private HandlersSupport() {
    }

    static void initialize() {
        for (TypeHandler<?> typeHandler : ServiceLoader.load(TypeHandler.class)) {
            MAP.put(typeHandler.type(), typeHandler);
        }
    }

    public static TypeHandler<?> handlerFromMethod(Method method) {
        return MAP.getOrDefault(method.getReturnType().isEnum() ? "enum" : method.getReturnType().getName(), HandlersSupport.MAP.get(Object.class.getName()));
    }

    @SuppressWarnings("unchecked")
    public static <T> TypeHandler<T> handlerFromClass(Class<T> genericType) {
        return MAP.getOrDefault(genericType.isEnum() ? "enum" : genericType.getName(), HandlersSupport.MAP.get(Object.class.getName()));
    }

    public static TypeHandler<?> handlerFromField(Field field) {
        return MAP.getOrDefault(field.getType().isEnum() ? "enum" : field.getType().getName(), HandlersSupport.MAP.get(Object.class.getName()));
    }
}
