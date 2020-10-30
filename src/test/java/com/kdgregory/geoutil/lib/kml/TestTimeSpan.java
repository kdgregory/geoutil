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

package com.kdgregory.geoutil.lib.kml;

import java.time.Instant;
import java.util.List;

import org.w3c.dom.Element;

import org.junit.Test;

import static org.junit.Assert.*;

import net.sf.kdgcommons.test.StringAsserts;
import net.sf.practicalxml.DomUtil;
import net.sf.practicalxml.builder.XmlBuilder;


public class TestTimeSpan
{
    private static Instant  TEST_BEGIN          = Instant.ofEpochMilli(1577547828000L);
    private static Long     TEST_BEGIN_MILLIS   = Long.valueOf(1577547828000L);
    private static String   TEST_BEGIN_STRING   = "2019-12-28T15:43:48Z";

    private static Instant  TEST_END            = Instant.ofEpochMilli(1577547829000L);
    private static Long     TEST_END_MILLIS     = Long.valueOf(1577547829000L);
    private static String   TEST_END_STRING     = "2019-12-28T15:43:49Z";


    @Test
    public void testConstructorsAndAccessorts() throws Exception
    {
        TimeSpan t1 = new TimeSpan();

        assertNull("default constructor, getBegin()",                                   t1.getBegin());
        assertNull("default constructor, getBeginMillis()",                             t1.getBeginMillis());
        assertNull("default constructor, getBeginString()",                             t1.getBeginString());
        assertNull("default constructor, getEnd()",                                     t1.getEnd());
        assertNull("default constructor, getEndMillis()",                               t1.getEndMillis());
        assertNull("default constructor, getEndString()",                               t1.getEndString());

        TimeSpan t2 = new TimeSpan(TEST_BEGIN, TEST_END);

        assertEquals("Instant constructor, getBegin()",         TEST_BEGIN,             t2.getBegin());
        assertEquals("Instant constructor, getBeginMillis()",   TEST_BEGIN_MILLIS,      t2.getBeginMillis());
        assertEquals("Instant constructor, getBeginString()",   TEST_BEGIN_STRING,      t2.getBeginString());
        assertEquals("Instant constructor, getEnd()",           TEST_END,               t2.getEnd());
        assertEquals("Instant constructor, getEndMillis()",     TEST_END_MILLIS,        t2.getEndMillis());
        assertEquals("Instant constructor, getEndString()",     TEST_END_STRING,        t2.getEndString());

        TimeSpan t3 = new TimeSpan(TEST_BEGIN_MILLIS.longValue(), TEST_END_MILLIS.longValue());

        assertEquals("Millis constructor, getBegin()",          TEST_BEGIN,             t3.getBegin());
        assertEquals("Millis constructor, getBeginMillis()",    TEST_BEGIN_MILLIS,      t3.getBeginMillis());
        assertEquals("Millis constructor, getBeginString()",    TEST_BEGIN_STRING,      t3.getBeginString());
        assertEquals("Millis constructor, getEnd()",            TEST_END,               t3.getEnd());
        assertEquals("Millis constructor, getEndMillis()",      TEST_END_MILLIS,        t3.getEndMillis());
        assertEquals("Millis constructor, getEndString()",      TEST_END_STRING,        t3.getEndString());

        TimeSpan t4 = new TimeSpan(TEST_BEGIN_STRING, TEST_END_STRING);

        assertEquals("String constructor, getBegin()",          TEST_BEGIN,             t4.getBegin());
        assertEquals("String constructor, getBeginMillis()",    TEST_BEGIN_MILLIS,      t4.getBeginMillis());
        assertEquals("String constructor, getBeginString()",    TEST_BEGIN_STRING,      t4.getBeginString());
        assertEquals("String constructor, getEnd()",            TEST_END,               t4.getEnd());
        assertEquals("String constructor, getEndMillis()",      TEST_END_MILLIS,        t4.getEndMillis());
        assertEquals("String constructor, getEndString()",      TEST_END_STRING,        t4.getEndString());
    }


    @Test
    public void testEqualityAndHashcode() throws Exception
    {
        TimeSpan t1 = new TimeSpan(TEST_BEGIN, TEST_END);
        TimeSpan t2 = new TimeSpan(TEST_BEGIN_MILLIS.longValue(),     TEST_END_MILLIS.longValue());
        TimeSpan t3 = new TimeSpan(TEST_BEGIN_MILLIS.longValue() + 1, TEST_END_MILLIS.longValue());
        TimeSpan t4 = new TimeSpan(TEST_BEGIN_MILLIS.longValue(),     TEST_END_MILLIS.longValue() + 1);
        TimeSpan t5a = new TimeSpan();
        TimeSpan t5b = new TimeSpan();

        assertTrue("identity",          t1.equals(t1));
        assertTrue("equal values",      t1.equals(t2));
        assertFalse("unequal start",    t1.equals(t3));
        assertFalse("unequal end",      t1.equals(t4));
        assertTrue("null instance",     t5a.equals(t5b));
        assertFalse("null vs not-null", t5a.equals(t3));

        assertTrue("equal instances have equal hashcodes",  t1.hashCode() == t2.hashCode());
        assertTrue("known different hashcodes",             t1.hashCode() != t3.hashCode());
        assertTrue("null instances have same hashcodes",    t5a.hashCode() == t5b.hashCode());
    }


    @Test
    public void testAppendAsXml() throws Exception
    {
        Element parent = DomUtil.newDocument("irrelevant");
        Element child = new TimeSpan(TEST_BEGIN, TEST_END).appendAsXml(parent);

        assertEquals("added single child to existing parent",   1,                                  DomUtil.getChildren(parent).size());
        assertSame("returned child",                            child,                              DomUtil.getChildren(parent).get(0));
        assertEquals("child namespace",                         "http://www.opengis.net/kml/2.2",   child.getNamespaceURI());
        assertEquals("child name",                              "TimeSpan",                         child.getNodeName());

        List<Element> dataElements = DomUtil.getChildren(child);

        assertEquals("number of data elements",                 2,                                  dataElements.size());
        assertEquals("first data element, namespace",           "http://www.opengis.net/kml/2.2",   dataElements.get(0).getNamespaceURI());
        assertEquals("first data element, name",                "begin",                            dataElements.get(0).getNodeName());
        assertEquals("first data element, value",               TEST_BEGIN_STRING,                  dataElements.get(0).getTextContent());
        assertEquals("second data element, namespace",          "http://www.opengis.net/kml/2.2",   dataElements.get(1).getNamespaceURI());
        assertEquals("second data element, name",               "end",                              dataElements.get(1).getNodeName());
        assertEquals("second data element, value",              TEST_END_STRING,                    dataElements.get(1).getTextContent());
    }


    @Test
    public void testAppendAsXmlNullContent() throws Exception
    {
        Element parent = DomUtil.newDocument("irrelevant");

        Element child1 = new TimeSpan().appendAsXml(parent);
        assertNull("no values, append returned null",                                               child1);
        assertEquals("no values, did not add child",            0,                                  DomUtil.getChildren(parent).size());

        Element child2 = new TimeSpan(null, TEST_END).appendAsXml(parent);
        assertNull("no begin, append returned null",                                                child2);
        assertEquals("no begin, did not add child",             0,                                  DomUtil.getChildren(parent).size());

        Element child3 = new TimeSpan(TEST_BEGIN, null).appendAsXml(parent);
        assertNull("no end, append returned null",                                                  child3);
        assertEquals("no end, did not add child",               0,                                  DomUtil.getChildren(parent).size());
    }


    @Test
    public void testFromXmlWithNamespace() throws Exception
    {
        Element elem = XmlBuilder.element("http://earth.google.com/kml/2.1", "irrelevant",
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "begin", XmlBuilder.text("2019-12-28T15:43:48Z")),
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "end",   XmlBuilder.text("2019-12-28T15:43:49Z")))
                       .toDOM().getDocumentElement();

        TimeSpan ts = TimeSpan.fromXml(elem);
        assertEquals("begin",   TEST_BEGIN,     ts.getBegin());
        assertEquals("end",     TEST_END,       ts.getEnd());
    }


    @Test
    public void testFromXmlWithoutNamespace() throws Exception
    {
        Element elem = XmlBuilder.element("irrelevant",
                            XmlBuilder.element("begin", XmlBuilder.text("2019-12-28T15:43:48Z")),
                            XmlBuilder.element("end",  XmlBuilder.text("2019-12-28T15:43:49Z")))
                       .toDOM().getDocumentElement();

        TimeSpan ts = TimeSpan.fromXml(elem);
        assertEquals("begin",   TEST_BEGIN,     ts.getBegin());
        assertEquals("end",     TEST_END,       ts.getEnd());
    }


    @Test
    public void testFromXmlMissingChildren() throws Exception
    {
        try
        {
            Element elem = XmlBuilder.element("irrelevant",
                                XmlBuilder.element("end",  XmlBuilder.text("2019-12-28T15:43:49Z")))
                           .toDOM().getDocumentElement();

            TimeSpan.fromXml(elem);
            fail("successful parse when missing \"begin\"");
        }
        catch (IllegalArgumentException ex)
        {
            StringAsserts.assertRegex("exception message (was: " + ex.getMessage() + ")",
                                      "TimeSpan.*missing.*begin.*",
                                      ex.getMessage());
        }

        try
        {
            Element elem = XmlBuilder.element("irrelevant",
                                XmlBuilder.element("begin", XmlBuilder.text("2019-12-28T15:43:48Z")))
                           .toDOM().getDocumentElement();

            TimeSpan.fromXml(elem);
            fail("successful parse when missing \"end\"");
        }
        catch (IllegalArgumentException ex)
        {
            StringAsserts.assertRegex("exception message (was: " + ex.getMessage() + ")",
                                      "TimeSpan.*missing.*end.*",
                                      ex.getMessage());
        }
    }


    @Test
    public void testFromXmlNull() throws Exception
    {
        assertNull(Timestamp.fromXml(null));
    }
}
