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

import org.w3c.dom.Element;

import org.junit.Test;

import static org.junit.Assert.*;

import net.sf.practicalxml.DomUtil;
import net.sf.practicalxml.builder.XmlBuilder;


public class TestTimestamp
{
    private static Instant TEST_INSTANT = Instant.ofEpochMilli(1577547828000L);
    private static Long TEST_MILLIS     = Long.valueOf(1577547828000L);
    private static String TEST_STRING   = "2019-12-28T15:43:48Z";


    @Test
    public void testConstructorsAndAccessorts() throws Exception
    {
        Timestamp t1 = new Timestamp();

        assertNull("default constructor, asInstant",                    t1.asInstant());
        assertNull("default constructor, asMillis",                     t1.asMillis());
        assertNull("default constructor, asString",                     t1.asString());

        Timestamp t2 = new Timestamp(TEST_INSTANT);

        assertEquals("set from Instant, asInstant",     TEST_INSTANT,    t2.asInstant());
        assertEquals("set from Instant, asMillis",      TEST_MILLIS,     t2.asMillis());
        assertEquals("set from Instant, asString",      TEST_STRING,     t2.asString());

        Timestamp t3 = new Timestamp(TEST_MILLIS.longValue());

        assertEquals("set from millis, asInstant",      TEST_INSTANT,    t3.asInstant());
        assertEquals("set from millis, asMillis",       TEST_MILLIS,     t3.asMillis());
        assertEquals("set from millis, asString",       TEST_STRING,     t3.asString());

        Timestamp t4 = new Timestamp(TEST_STRING);

        assertEquals("set from string, asInstant",      TEST_INSTANT,    t4.asInstant());
        assertEquals("set from string, asMillis",       TEST_MILLIS,     t4.asMillis());
        assertEquals("set from string, asString",       TEST_STRING,     t4.asString());
    }


    @Test
    public void testEqualityAndHashcode() throws Exception
    {
        Timestamp t1 = new Timestamp(TEST_INSTANT);
        Timestamp t2 = new Timestamp(TEST_MILLIS);
        Timestamp t3 = new Timestamp(TEST_MILLIS + 1);
        Timestamp t4a = new Timestamp();
        Timestamp t4b = new Timestamp();

        assertTrue("identity", t1.equals(t1));
        assertTrue("equal values", t1.equals(t2));
        assertFalse("unequal values", t1.equals(t3));
        assertTrue("null instance", t4a.equals(t4b));
        assertFalse("null vs not-null", t4a.equals(t3));

        assertTrue("equal instances have equal hashcodes",  t1.hashCode() == t2.hashCode());
        assertTrue("known different hashcodes",             t1.hashCode() != t3.hashCode());
        assertTrue("null instances have same hashcodes",    t4a.hashCode() == t4b.hashCode());
    }



    @Test
    public void testAppendAsXml() throws Exception
    {
        Element parent = DomUtil.newDocument("irrelevant");
        Element child = new Timestamp(TEST_INSTANT).appendAsXml(parent);

        assertEquals("added single child to existing parent",   1,                                  DomUtil.getChildren(parent).size());
        assertSame("returned child",                            child,                              DomUtil.getChildren(parent).get(0));
        assertEquals("child namespace",                         "http://www.opengis.net/kml/2.2",   child.getNamespaceURI());
        assertEquals("child name",                              "TimeStamp",                        child.getNodeName());

        assertEquals("child has single element",                1,                                  DomUtil.getChildren(child).size());
        assertEquals("when namespace",                          "http://www.opengis.net/kml/2.2",   DomUtil.getChildren(child).get(0).getNamespaceURI());
        assertEquals("when name",                               "when",                             DomUtil.getChildren(child).get(0).getNodeName());
        assertEquals("when value",                              TEST_STRING,                        DomUtil.getChildren(child).get(0).getTextContent());
    }


    @Test
    public void testAppendAsXmlNullTimestamp() throws Exception
    {
        Element parent = DomUtil.newDocument("irrelevant");
        Element child = new Timestamp().appendAsXml(parent);

        assertNull("append returned null",                                                          child);
        assertEquals("did not add child to existing parent",    0,                                  DomUtil.getChildren(parent).size());
    }


    @Test
    public void testFromXmlWithNamespace() throws Exception
    {
        Element elem = XmlBuilder.element("http://earth.google.com/kml/2.1", "irrelevant",
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "when", XmlBuilder.text("2019-12-28T15:43:48Z")))
                       .toDOM().getDocumentElement();

        Timestamp ts = Timestamp.fromXml(elem);
        assertEquals("value", TEST_INSTANT, ts.asInstant());
    }


    @Test
    public void testFromXmlWithoutNamespace() throws Exception
    {
        Element elem = XmlBuilder.element("irrelevant",
                            XmlBuilder.element("when", XmlBuilder.text("2019-12-28T15:43:48Z")))
                       .toDOM().getDocumentElement();

        Timestamp ts = Timestamp.fromXml(elem);
        assertEquals("value", TEST_INSTANT, ts.asInstant());
    }


    @Test
    public void testFromXmlNull() throws Exception
    {
        assertNull(Timestamp.fromXml(null));
    }
}
