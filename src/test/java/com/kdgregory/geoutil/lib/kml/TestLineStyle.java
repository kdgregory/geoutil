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

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.practicalxml.DomUtil;
import net.sf.practicalxml.builder.XmlBuilder;


public class TestLineStyle
{
    @Test
    public void testAccessors() throws Exception
    {
        LineStyle s = new LineStyle();

        assertEquals("getColor(), initial value",       "00000000",         s.getColor());
        assertEquals("setColor()",                      s,                  s.setColor("12345678"));
        assertEquals("getColor()",                      "12345678",         s.getColor());

        assertNull("getColorMode(), initial value",                         s.getColorMode());
        assertEquals("setColorMode()",                  s,                  s.setColorMode(ColorMode.random));
        assertEquals("getColorMode()",                  ColorMode.random,   s.getColorMode());

        assertEquals("getWidth(), initial value",       0.0,                s.getWidth(),   0.0);
        assertEquals("setWidth()",                      s,                  s.setWidth(1.5));
        assertEquals("getWidth()",                      1.5,                s.getWidth(),   0.0);
    }


    @Test
    public void testFromXmlMinimal() throws Exception
    {
        Document dom = XmlBuilder.element("http://earth.google.com/kml/2.1", "LineStyle")
                       .toDOM();

        LineStyle s = LineStyle.fromXml(dom.getDocumentElement());

        assertNull("getId()",                           s.getId());
        assertEquals("getColor()",      "00000000",     s.getColor());
        assertNull("getColorMode()",                    s.getColorMode());
        assertEquals("getWidth()",      0.0,            s.getWidth(),   0.0);
    }


    @Test
    public void testFromXmlComplete() throws Exception
    {
        Document dom = XmlBuilder.element("http://earth.google.com/kml/2.1", "LineStyle",
                            XmlBuilder.attribute("id", "someUniqueValue"),
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "color",      XmlBuilder.text("12345678")),
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "colorMode",  XmlBuilder.text("normal")),
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "width",      XmlBuilder.text("1.5")))
                       .toDOM();


        LineStyle s = LineStyle.fromXml(dom.getDocumentElement());

        assertEquals("getId()",         "someUniqueValue",      s.getId());
        assertEquals("getColor()",      "12345678",             s.getColor());
        assertEquals("getColorMode()",  ColorMode.normal,       s.getColorMode());
        assertEquals("getWidth()",      1.5,                    s.getWidth(),   0.0);
    }


    @Test
    public void testFromXmlInvalidName() throws Exception
    {
        Document dom = XmlBuilder.element("http://earth.google.com/kml/2.1", "SomethingElse")
                       .toDOM();

        try
        {
            LineStyle.fromXml(dom.getDocumentElement());
            fail("should not have parsed successfully");
        }
        catch (IllegalArgumentException ex)
        {
            assertTrue("exception message (was: " + ex.getMessage() + ")", ex.getMessage().contains("SomethingElse"));
        }
    }


    @Test
    public void testAppendAsXmlMinimal() throws Exception
    {
        // note that "minimal" includes default values

        LineStyle s = new LineStyle();

        Element parent = DomUtil.newDocument("irrelevant");
        Element child = s.appendAsXml(parent);

        assertEquals("added single child to existing parent",   1,                                  DomUtil.getChildren(parent).size());
        assertSame("returned child",                            child,                              DomUtil.getChildren(parent).get(0));
        assertEquals("child namespace",                         "http://www.opengis.net/kml/2.2",   child.getNamespaceURI());
        assertEquals("child name",                              "LineStyle",                        child.getNodeName());

        assertEquals("does not have ID",                        "",                                 child.getAttribute("id"));

        List<Element> children = DomUtil.getChildren(child);

        assertEquals("number of data elements",                 2,                                  children.size());

        assertEquals("color namespace",                         "http://www.opengis.net/kml/2.2",   children.get(0).getNamespaceURI());
        assertEquals("color name",                              "color",                            children.get(0).getNodeName());
        assertEquals("color value",                             "00000000",                         children.get(0).getTextContent());

        assertEquals("width namespace",                         "http://www.opengis.net/kml/2.2",   children.get(1).getNamespaceURI());
        assertEquals("width name",                              "width",                            children.get(1).getNodeName());
        assertEquals("width value",                             "0.0",                              children.get(1).getTextContent());
    }


    @Test
    public void testAppendAsXmlComplete() throws Exception
    {
        LineStyle s = new LineStyle()
                      .setId("somethingUnique")
                      .setColor("12345678")
                      .setColorMode(ColorMode.random)
                      .setWidth(1.5);

        Element parent = DomUtil.newDocument("irrelevant");
        Element child = s.appendAsXml(parent);

        assertEquals("added single child to existing parent",   1,                                  DomUtil.getChildren(parent).size());
        assertSame("returned child",                            child,                              DomUtil.getChildren(parent).get(0));
        assertEquals("child namespace",                         "http://www.opengis.net/kml/2.2",   child.getNamespaceURI());
        assertEquals("child name",                              "LineStyle",                        child.getNodeName());

        assertEquals("has ID",                                  "somethingUnique",                  child.getAttribute("id"));

        List<Element> children = DomUtil.getChildren(child);

        assertEquals("number of data elements",                 3,                                  children.size());

        assertEquals("color namespace",                         "http://www.opengis.net/kml/2.2",   children.get(0).getNamespaceURI());
        assertEquals("color name",                              "color",                            children.get(0).getNodeName());
        assertEquals("color value",                             "12345678",                         children.get(0).getTextContent());

        assertEquals("width namespace",                         "http://www.opengis.net/kml/2.2",   children.get(1).getNamespaceURI());
        assertEquals("width name",                              "colorMode",                        children.get(1).getNodeName());
        assertEquals("width value",                             "random",                           children.get(1).getTextContent());

        assertEquals("width namespace",                         "http://www.opengis.net/kml/2.2",   children.get(2).getNamespaceURI());
        assertEquals("width name",                              "width",                            children.get(2).getNodeName());
        assertEquals("width value",                             "1.5",                              children.get(2).getTextContent());
    }
}
