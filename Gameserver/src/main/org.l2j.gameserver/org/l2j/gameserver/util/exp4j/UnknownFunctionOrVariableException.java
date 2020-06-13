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
package org.l2j.gameserver.util.exp4j;

/**
 * This exception is being thrown whenever {@link Tokenizer} finds unknown function or variable.
 *
 * @author Bartosz Firyn (sarxos)
 */
public class UnknownFunctionOrVariableException extends IllegalArgumentException {
    private final String message;
    private final String expression;
    private final String token;
    private final int position;

    public UnknownFunctionOrVariableException(String expression, int position, int length) {
        this.expression = expression;
        this.token = token(expression, position, length);
        this.position = position;
        this.message = "Unknown function or variable '" + token + "' at pos " + position + " in expression '" + expression + "'";
    }

    private static String token(String expression, int position, int length) {

        int len = expression.length();
        int end = (position + length) - 1;

        if (len < end) {
            end = len;
        }

        return expression.substring(position, end);
    }

    @Override
    public String getMessage() {
        return message;
    }

    /**
     * @return Expression which contains unknown function or variable
     */
    public String getExpression() {
        return expression;
    }

    /**
     * @return The name of unknown function or variable
     */
    public String getToken() {
        return token;
    }

    /**
     * @return The position of unknown function or variable
     */
    public int getPosition() {
        return position;
    }
}
