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

import java.time.Instant;
import java.time.format.DateTimeFormatter;

import net.sf.kdgcommons.lang.StringUtil;

/**
 *  Utility methods for working with points in time.
 */
public class TimestampUtils
{
    /**
     *  Parses the ISO-8601 representation of a timestamp. Accepts both "Zulu"
     *  and offset forms (unlike <code>Instant.parse()</code>). If passed null
     *  or an empty string, returns null.
     *
     *  @throws IllegalArgumentException if unable to parse.
     */
    public static Instant parse(String value)
    {
        if (StringUtil.isEmpty(value))
            return null;

        try
        {
            return (value.endsWith("Z"))
                   ? Instant.parse(value)
                   : Instant.from(DateTimeFormatter.ISO_DATE_TIME.parse(value));
        }
        catch (Exception ex)
        {
            throw new IllegalArgumentException("unable to parse \"" + value + "\" as timestamp");
        }
    }


    /**
     *  Compares two Instants, where null < not-null.
     */
    public static int compare(Instant t1, Instant t2)
    {
        return (t1 == null) & (t2 == null)  ? 0
             : (t1 == null) & (t2 != null)  ? -1
             : (t1 != null) & (t2 == null)  ? 1
             : t1.compareTo(t2);
    }
}
