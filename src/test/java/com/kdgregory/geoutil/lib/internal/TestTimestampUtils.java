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

import org.junit.Test;
import static org.junit.Assert.*;


public class TestTimestampUtils
{
    @Test
    public void testParse() throws Exception
    {
        assertEquals("null",    null,                                   TimestampUtils.parse(null));
        assertEquals("empty",   null,                                   TimestampUtils.parse(""));
        assertEquals("zulu",    Instant.ofEpochMilli(1577547828000L),   TimestampUtils.parse("2019-12-28T15:43:48Z"));
        assertEquals("offset",  Instant.ofEpochMilli(1577547828000L),   TimestampUtils.parse("2019-12-28T11:43:48-04:00"));
    }


    @Test
    public void testParseFailure() throws Exception
    {
        String value = "garbage";

        try
        {
            TimestampUtils.parse(value);
            fail("did not throw for unparseable string");
        }
        catch (IllegalArgumentException ex)
        {
            assertTrue("message indicates unparseable value (was: " + ex.getMessage() + ")",
                       ex.getMessage().contains(value));
        }
    }


    @Test
    public void testCompare() throws Exception
    {
        Instant i1a = Instant.ofEpochMilli(1577547828000L);
        Instant i1b = Instant.ofEpochMilli(1577547828000L);
        Instant i2  = Instant.ofEpochMilli(1577547828001L);

        assertTrue("equal",             TimestampUtils.compare(i1a, i1b)  == 0);
        assertTrue("<",                 TimestampUtils.compare(i1a, i2)   < 0);
        assertTrue(">",                 TimestampUtils.compare(i2, i1a)   > 0);
        assertTrue("null, not-null",    TimestampUtils.compare(null,i2)   < 0);
        assertTrue("not-null, null",    TimestampUtils.compare(i2,null)   > 0);
        assertTrue("null, null",        TimestampUtils.compare(null,null) == 0);
    }


}
