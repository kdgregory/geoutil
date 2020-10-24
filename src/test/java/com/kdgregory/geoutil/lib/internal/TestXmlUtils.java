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

import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.kdgcommons.test.StringAsserts;
import net.sf.practicalxml.DomUtil;


public class TestXmlUtils
{
    @Test
    public void testOptAppendDataElement() throws Exception
    {
        Element parent = DomUtil.newDocument("parent");

        // various things that have a simple string representation
        XmlUtils.optAppendDataElement(parent, "nsuri:something", "foo", "fribble");
        XmlUtils.optAppendDataElement(parent, "nsuri:something", "bar", Integer.valueOf(12));
        XmlUtils.optAppendDataElement(parent, "nsuri:something", "baz", Double.valueOf(12));

        // null won't be appended
        XmlUtils.optAppendDataElement(parent, "nsuri:something", "biff", null);

        // empty string will be
        XmlUtils.optAppendDataElement(parent, "nsuri:something", "boffo", "");

        // and we have two variants of booleans
        XmlUtils.optAppendDataElement(parent, "nsuri:something", "boo", Boolean.TRUE);
        XmlUtils.optAppendDataElement(parent, "nsuri:something", "boo", Boolean.FALSE);

        List<Element> children = DomUtil.getChildren(parent);
        assertEquals("number of children", 6, children.size());

        assertEquals("child namespace", "nsuri:something",  children.get(0).getNamespaceURI());
        assertEquals("child name",      "foo",              children.get(0).getNodeName());
        assertEquals("child content",   "fribble",          children.get(0).getTextContent());

        assertEquals("child namespace", "nsuri:something",  children.get(1).getNamespaceURI());
        assertEquals("child name",      "bar",              children.get(1).getNodeName());
        assertEquals("child content",   "12",               children.get(1).getTextContent());

        assertEquals("child namespace", "nsuri:something",  children.get(2).getNamespaceURI());
        assertEquals("child name",      "baz",              children.get(2).getNodeName());
        assertEquals("child content",   "12.0",             children.get(2).getTextContent());

        assertEquals("child namespace", "nsuri:something",  children.get(3).getNamespaceURI());
        assertEquals("child name",      "boffo",            children.get(3).getNodeName());
        assertEquals("child content",   "",                 children.get(3).getTextContent());

        assertEquals("child namespace", "nsuri:something",  children.get(4).getNamespaceURI());
        assertEquals("child name",      "boo",              children.get(4).getNodeName());
        assertEquals("child content",   "1",                children.get(4).getTextContent());

        assertEquals("child namespace", "nsuri:something",  children.get(5).getNamespaceURI());
        assertEquals("child name",      "boo",              children.get(5).getNodeName());
        assertEquals("child content",   "0",                children.get(5).getTextContent());
    }


    @Test
    public void testGetChildText() throws Exception
    {
        Element parent = DomUtil.newDocument("nsuri:argle", "foo");
        Element child  = DomUtil.appendChild(parent, "nsuri:argle", "bar");
        child.setTextContent("baz");

        // first variant ignores namespace if it exists on elements
        assertEquals("sans namespace",              "baz",  XmlUtils.getChildText(parent, "bar"));
        assertEquals("sans namespace, no child",    null,   XmlUtils.getChildText(parent, "biff"));

        // second variant requires namespace match
        assertEquals("with namespace",              "baz",  XmlUtils.getChildText(parent, "nsuri:argle", "bar"));
        assertEquals("with namespace, no child",    null,   XmlUtils.getChildText(parent, "nsuri:argle", "biff"));
        assertEquals("with incorrect namespace",    null,   XmlUtils.getChildText(parent, "nsuri:nargle", "bar"));
    }


    @Test
    public void testGetChildTextAsDouble() throws Exception
    {
        Element parent = DomUtil.newDocument("parent");
        XmlUtils.optAppendDataElement(parent, "nsuri:something", "foo", Double.valueOf(12));
        XmlUtils.optAppendDataElement(parent, "nsuri:something", "bar", "");
        XmlUtils.optAppendDataElement(parent, "nsuri:something", "biff", "NAN");

        assertEquals("parseable value", Double.valueOf(12), XmlUtils.getChildTextAsDouble(parent, "nsuri:something", "foo"));
        assertNull("child has empty string",                XmlUtils.getChildTextAsDouble(parent, "nsuri:something", "bar"));
        assertNull("no such child",                         XmlUtils.getChildTextAsDouble(parent, "nsuri:something", "boffo"));

        try
        {
            XmlUtils.getChildTextAsDouble(parent, "nsuri:something", "biff");
            fail("didn't throw on unparseable value");
        }
        catch (IllegalArgumentException ex)
        {
            StringAsserts.assertRegex("exception message identifies element, value (was: " + ex.getMessage() + ")",
                                      ".*nsuri:something.*biff.*: NAN",
                                      ex.getMessage());
        }
    }


    @Test
    public void testGetChildTextAsBoolean() throws Exception
    {
        Element parent = DomUtil.newDocument("parent");
        XmlUtils.optAppendDataElement(parent, "nsuri:something", "foo", "1");
        XmlUtils.optAppendDataElement(parent, "nsuri:something", "bar", "0");
        XmlUtils.optAppendDataElement(parent, "nsuri:something", "baz", "");
        XmlUtils.optAppendDataElement(parent, "nsuri:something", "biff", "NAN");

        assertEquals("parseable: true",         Boolean.TRUE,   XmlUtils.getChildTextAsBoolean(parent, "nsuri:something", "foo"));
        assertEquals("parseable: false",        Boolean.FALSE,  XmlUtils.getChildTextAsBoolean(parent, "nsuri:something", "bar"));
        assertNull("child has empty string",                    XmlUtils.getChildTextAsBoolean(parent, "nsuri:something", "baz"));
        assertNull("no such child",                             XmlUtils.getChildTextAsBoolean(parent, "nsuri:something", "boffo"));

        try
        {
            XmlUtils.getChildTextAsBoolean(parent, "nsuri:something", "biff");
            fail("didn't throw on unparseable value");
        }
        catch (IllegalArgumentException ex)
        {
            StringAsserts.assertRegex("exception message identifies element, value (was: " + ex.getMessage() + ")",
                                      ".*biff.*: NAN",
                                      ex.getMessage());
        }
    }


    @Test
    @SuppressWarnings("unused")
    public void testListToMap() throws Exception
    {
        Element parent = DomUtil.newDocument("foo");
        Element child1 = DomUtil.appendChild(parent, "argle");
        Element child2 = DomUtil.appendChild(parent, "bargle");                         // note: this gets replaced
        Element child3 = DomUtil.appendChild(parent, "nsuri:something", "x:bargle");    // by this

        Map<String,Element> map = XmlUtils.listToMap(DomUtil.getChildren(parent));
        assertEquals("number of elements", 2, map.size());

        assertSame("argle is child 1",  child1, map.get("argle"));
        assertSame("bargle is child 3", child3, map.get("bargle"));
    }
}
