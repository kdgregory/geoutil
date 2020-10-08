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

package com.kdgregory.geoutil.lib.shared;

import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.practicalxml.DomUtil;


public class TestXmlUtils
{
    @Test
    public void testOptAppendDataElement() throws Exception
    {
        Element parent = DomUtil.newDocument("foo");
        XmlUtils.optAppendDataElement(parent, "nsuri:something", "bar", Integer.valueOf(12));

        List<Element> children = DomUtil.getChildren(parent);
        assertEquals("number of children", 1, children.size());

        Element child = children.get(0);
        assertEquals("child namespace", "nsuri:something",  child.getNamespaceURI());
        assertEquals("child name",      "bar",              child.getNodeName());
        assertEquals("child content",   "12",               child.getTextContent());
    }


    @Test
    public void testOptAppendNullDataElement() throws Exception
    {
        Element parent = DomUtil.newDocument("foo");
        XmlUtils.optAppendDataElement(parent, "nsuri:something", "bar", null);

        List<Element> children = DomUtil.getChildren(parent);
        assertEquals("number of children", 0, children.size());
    }


    @Test
    public void testOptAppendBooleanDataElement() throws Exception
    {
        Element parent = DomUtil.newDocument("foo");

        XmlUtils.optAppendBooleanDataElement(parent, "nsuri:something", "foo", null);
        XmlUtils.optAppendBooleanDataElement(parent, "nsuri:something", "bar", Boolean.TRUE);
        XmlUtils.optAppendBooleanDataElement(parent, "nsuri:something", "baz", Boolean.FALSE);

        assertEquals("only added two children",     2,      DomUtil.getChildren(parent).size());
        assertEquals("encoded Boolean.TRUE",        "1",    DomUtil.getChild(parent, "nsuri:something", "bar").getTextContent());
        assertEquals("encoded Boolean.FALSE",        "0",   DomUtil.getChild(parent, "nsuri:something", "baz").getTextContent());
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
