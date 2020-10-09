// Copyright Keith D Gregory
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.kdgregory.geoutil.lib.internal;

import java.util.function.Consumer;

import net.sf.kdgcommons.lang.StringUtil;

/**
 *  Utility methods for interacting with objects. These methods should end
 *  up in KDGCommons (but many require Java8, so a later version).
 */
public class ObjectUtils
{
    /**
     *  Invokes the specified setter IFF the passed string is not null or empty.
     */
    public static void optSetString(String value, Consumer<String> setter)
    {
        if (StringUtil.isEmpty(value))
            return;

        setter.accept(value);
    }


    /**
     *  IFF the passed string is not null or empty, attempts to parse it as
     *  a Double and then invokes the specified setter.
     */
    public static void optSetDouble(String value, Consumer<Double> setter)
    {
        if (StringUtil.isEmpty(value))
            return;

        try
        {
            Double parsed = Double.valueOf(value);
            setter.accept(parsed);
        }
        catch (NumberFormatException ex)
        {
            // the original exception's stack trace can be misleading, so we'll throw away
            throw new NumberFormatException("could not parse: " + value);
        }
    }
}
