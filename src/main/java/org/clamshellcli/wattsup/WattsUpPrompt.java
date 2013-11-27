/**
 *     WattsUp-J is a Java application to interact with the Watts up? power meter.
 *     Copyright (C) 2013  Contributors
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 *     Contributors:
 *         Alessandro Ferreira Leite - the initial implementation.
 */
package org.clamshellcli.wattsup;

import org.clamshellcli.api.Context;
import org.clamshellcli.api.Prompt;

public class WattsUpPrompt implements Prompt
{
    /**
     * The watts up prompt.
     */
    private static final String PROMPT = "watts-up > ";

    @Override
    public void plug(Context ctx)
    {
    }

    @Override
    public String getValue(Context ctx)
    {
        return PROMPT;
    }
}