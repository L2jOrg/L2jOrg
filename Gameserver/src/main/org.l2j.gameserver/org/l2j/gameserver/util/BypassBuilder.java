/*
 * Copyright © 2019 L2J Mobius
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
package org.l2j.gameserver.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author UnAfraid
 */
public class BypassBuilder {
    private final String _bypass;
    private final List<BypassParam> _params = new ArrayList<>();

    public BypassBuilder(String bypass) {
        _bypass = bypass;
    }

    public void addParam(BypassParam param) {
        Objects.requireNonNull(param, "param cannot be null!");
        _params.add(param);
    }

    public void addParam(String name, String separator, Object value) {
        Objects.requireNonNull(name, "name cannot be null!");
        addParam(new BypassParam(name, Optional.ofNullable(separator), Optional.ofNullable(value)));
    }

    public void addParam(String name, Object value) {
        addParam(name, "=", value);
    }

    public void addParam(String name) {
        addParam(name, null, null);
    }

    public StringBuilder toStringBuilder() {
        final StringBuilder sb = new StringBuilder(_bypass);
        for (BypassParam param : _params) {
            sb.append(" ").append(param.getName().trim());
            if (param.getSeparator().isPresent() && param.getValue().isPresent()) {
                sb.append(param.getSeparator().get().trim());
                final Object value = param.getValue().get();
                if (value instanceof String) {
                    sb.append('"');
                }
                sb.append(String.valueOf(value).trim());
                if (value instanceof String) {
                    sb.append('"');
                }
            }
        }
        return sb;
    }

    @Override
    public String toString() {
        return toStringBuilder().toString();
    }

    private static class BypassParam {
        private final String _name;
        private final Optional<String> _separator;
        private final Optional<Object> _value;

        public BypassParam(String name, Optional<String> separator, Optional<Object> value) {
            _name = name;
            _separator = separator;
            _value = value;
        }

        public String getName() {
            return _name;
        }

        public Optional<String> getSeparator() {
            return _separator;
        }

        public Optional<Object> getValue() {
            return _value;
        }
    }
}
