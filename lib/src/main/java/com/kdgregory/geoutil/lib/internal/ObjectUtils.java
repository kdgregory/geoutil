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
import java.util.function.Function;

import net.sf.kdgcommons.lang.StringUtil;


/**
 *  Utility methods for interacting with objects. These methods should end
 *  up in KDGCommons (but many require Java8, so a later version).
 */
public class ObjectUtils
{
    /**
     *  Invokes a function IFF provided a non-null value. Returns the function
     *  result or null.
     */
    public static <T,R> R optInvoke(T value, Function<T,R> fn)
    {
        return (value == null)
             ? null
             : fn.apply(value);
    }


    /**
     *  Invokes a setter IFF provided a non-null value.
     */
    public static <T> void optSet(T value, Consumer<T> setter)
    {
        if (value != null)
        {
            setter.accept(value);
        }
    }


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
     *  Attempts to parse a string as a Boolean: "0" and "false" are false, "1"
     *  and "true" are true. All comparisons are case-insensitive, and passing
     *  null or empty string returns null.
     *
     *  @throws IllegalArgumentException if unable to parse.
     */
    public static Boolean parseAsBoolean(String value)
    {
        if (StringUtil.isEmpty(value))
            return null;

        switch (value.toLowerCase())
        {
            case "true":
            case "1":
                return Boolean.TRUE;

            case "false":
            case "0":
                return Boolean.FALSE;

            default:
                throw new IllegalArgumentException("unable to parse: " + value);
        }
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
