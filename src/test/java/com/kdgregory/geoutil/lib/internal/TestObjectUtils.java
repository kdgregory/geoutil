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

import org.junit.Test;
import static org.junit.Assert.*;


public class TestObjectUtils
{
//----------------------------------------------------------------------------
//  Helpers
//----------------------------------------------------------------------------

    // used to test getters and setters; it must be public so that Java can
    // access its members
    public static class TestBean
    {
        private String stringValue;
        private Double doubleValue;

        public String getStringValue()
        {
            return stringValue;
        }

        public void setStringValue(String stringValue)
        {
            this.stringValue = stringValue;
        }

        public Double getDoubleValue()
        {
            return doubleValue;
        }

        public void setDoubleValue(Double doubleValue)
        {
            this.doubleValue = doubleValue;
        }
    }

//----------------------------------------------------------------------------
//  Testcases
//----------------------------------------------------------------------------

    @Test
    public void testOptApply() throws Exception
    {
        assertEquals("not-null value",  "12.0",         ObjectUtils.optInvoke(new Double(12), String::valueOf));
        assertEquals("null value",      (String)null,   ObjectUtils.optInvoke((String)null,   String::valueOf));
    }


    @Test
    public void testOptAccept() throws Exception
    {
        TestBean b = new TestBean();

        ObjectUtils.optSet("", b::setStringValue);
        assertEquals("after setting empty", "", b.getStringValue());

        ObjectUtils.optSet("foo", b::setStringValue);
        assertEquals("after setting non-empty", "foo", b.getStringValue());

        ObjectUtils.optSet(null, b::setStringValue);
        assertEquals("after setting null", "foo", b.getStringValue());
    }


    @Test
    public void testOptSetString() throws Exception
    {
        TestBean b = new TestBean();

        ObjectUtils.optSetString("foo", b::setStringValue);
        assertEquals("after setting non-empty", "foo", b.getStringValue());

        ObjectUtils.optSetString("", b::setStringValue);
        assertEquals("after setting empty", "foo", b.getStringValue());

        ObjectUtils.optSetString(null, b::setStringValue);
        assertEquals("after setting null", "foo", b.getStringValue());
    }


    @Test
    public void testOptSetDouble() throws Exception
    {
        TestBean b = new TestBean();

        ObjectUtils.optSetDouble("12.0", b::setDoubleValue);
        assertEquals("after setting non-empty", Double.valueOf(12.0), b.getDoubleValue());

        ObjectUtils.optSetDouble("", b::setDoubleValue);
        assertEquals("after setting empty", Double.valueOf(12.0), b.getDoubleValue());

        ObjectUtils.optSetDouble(null, b::setDoubleValue);
        assertEquals("after setting null", Double.valueOf(12.0), b.getDoubleValue());

        try
        {
            ObjectUtils.optSetDouble("foo", b::setDoubleValue);
            fail("didn't throw for unparseable value");
        }
        catch (NumberFormatException ex)
        {
            assertTrue("exception message identifies unparseable value (was: " + ex.getMessage() + ")",
                       ex.getMessage().contains("foo"));
        }
    }
}
