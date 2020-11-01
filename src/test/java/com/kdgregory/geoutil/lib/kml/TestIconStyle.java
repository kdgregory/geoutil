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


public class TestIconStyle
{
    @Test
    public void testAccessors() throws Exception
    {
        IconStyle s = new IconStyle();

        assertEquals("getColor(), initial value",       "00000000",                     s.getColor());
        assertEquals("setColor()",                      s,                              s.setColor("12345678"));
        assertEquals("getColor()",                      "12345678",                     s.getColor());

        assertNull("getColorMode(), initial value",                                     s.getColorMode());
        assertEquals("setColorMode()",                  s,                              s.setColorMode(ColorMode.random));
        assertEquals("getColorModeString()",            "random",                       s.getColorModeString());
        assertEquals("setColorModeString()",            s,                              s.setColorModeString("normal"));
        assertEquals("getColorMode()",                  ColorMode.normal,               s.getColorMode());

        assertNull("getScale(), initial value",                                         s.getScale());
        assertEquals("setScale()",                      s,                              s.setScale(Double.valueOf(1.5)));
        assertEquals("getScale()",                      Double.valueOf(1.5),            s.getScale());

        assertNull("getHeading(), initial value",                                       s.getHeading());
        assertEquals("setHeading()",                    s,                              s.setHeading(Double.valueOf(10)));
        assertEquals("getHeading()",                    Double.valueOf(10),             s.getHeading());

        assertNull("getHref(), initial value",                                          s.getHref());
        assertEquals("setHref()",                       s,                              s.setHref("https://www.example.com/icon"));
        assertEquals("getHref()",                       "https://www.example.com/icon", s.getHref());
    }


    @Test
    public void testFromXmlMinimal() throws Exception
    {
        Document dom = XmlBuilder.element("http://earth.google.com/kml/2.1", "IconStyle")
                       .toDOM();

        IconStyle s = IconStyle.fromXml(dom.getDocumentElement());

        assertNull("getId()",                           s.getId());
        assertEquals("getColor()",      "00000000",     s.getColor());
        assertNull("getColorMode()",                    s.getColorMode());
        assertNull("getScale()",                        s.getScale());
        assertNull("getHeading()",                      s.getHeading());
        assertNull("getHref()",                         s.getHref());
    }


    @Test
    public void testFromXmlComplete() throws Exception
    {
        Document dom = XmlBuilder.element("http://earth.google.com/kml/2.1", "IconStyle",
                            XmlBuilder.attribute("id", "someUniqueValue"),
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "color",      XmlBuilder.text("12345678")),
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "colorMode",  XmlBuilder.text("normal")),
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "scale",      XmlBuilder.text("1.5")),
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "heading",    XmlBuilder.text("122")),
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "Icon",
                                XmlBuilder.element("http://earth.google.com/kml/2.1", "href",   XmlBuilder.text("http://www.example.com/icon"))))
                       .toDOM();

        IconStyle s = IconStyle.fromXml(dom.getDocumentElement());

        assertEquals("getId()",             "someUniqueValue",              s.getId());
        assertEquals("getColor()",          "12345678",                     s.getColor());
        assertEquals("getColorMode()",      ColorMode.normal,               s.getColorMode());
        assertEquals("getScale()",          Double.valueOf(1.5),            s.getScale());
        assertEquals("getHeading()",        Double.valueOf(122),            s.getHeading());
        assertEquals("getHref()",           "http://www.example.com/icon",  s.getHref());
    }


    @Test
    public void testFromXmlInvalidName() throws Exception
    {
        Document dom = XmlBuilder.element("http://earth.google.com/kml/2.1", "SomethingElse")
                       .toDOM();

        try
        {
            IconStyle.fromXml(dom.getDocumentElement());
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
        IconStyle s = new IconStyle();

        Element parent = DomUtil.newDocument("irrelevant");
        Element child = s.appendAsXml(parent);

        assertEquals("added single child to existing parent",   1,                                  DomUtil.getChildren(parent).size());
        assertSame("returned child",                            child,                              DomUtil.getChildren(parent).get(0));
        assertEquals("child namespace",                         "http://www.opengis.net/kml/2.2",   child.getNamespaceURI());
        assertEquals("child name",                              "IconStyle",                        child.getNodeName());

        assertEquals("does not have ID",                        "",                                 child.getAttribute("id"));

        List<Element> children = DomUtil.getChildren(child);

        assertEquals("number of data elements",                 1,                                  children.size());

        assertEquals("color namespace",                         "http://www.opengis.net/kml/2.2",   children.get(0).getNamespaceURI());
        assertEquals("color name",                              "color",                            children.get(0).getNodeName());
        assertEquals("color value",                             "00000000",                         children.get(0).getTextContent());
    }


    @Test
    public void testAppendAsXmlComplete() throws Exception
    {
        IconStyle s = new IconStyle()
                      .setId("somethingUnique")
                      .setColor("12345678")
                      .setColorMode(ColorMode.random)
                      .setScale(1.5)
                      .setHeading(325.0)
                      .setHref("http://www.example.com/icon");

        Element parent = DomUtil.newDocument("irrelevant");
        Element child = s.appendAsXml(parent);

        assertEquals("added single child to existing parent",   1,                                  DomUtil.getChildren(parent).size());
        assertSame("returned child",                            child,                              DomUtil.getChildren(parent).get(0));
        assertEquals("child namespace",                         "http://www.opengis.net/kml/2.2",   child.getNamespaceURI());
        assertEquals("child name",                              "IconStyle",                        child.getNodeName());

        assertEquals("has ID",                                  "somethingUnique",                  child.getAttribute("id"));

        List<Element> children = DomUtil.getChildren(child);

        assertEquals("number of data elements",                 5,                                  children.size());

        assertEquals("child 1 namespace",                       "http://www.opengis.net/kml/2.2",   children.get(0).getNamespaceURI());
        assertEquals("child 1 name",                            "color",                            children.get(0).getNodeName());
        assertEquals("child 1 value",                           "12345678",                         children.get(0).getTextContent());

        assertEquals("child 2 namespace",                       "http://www.opengis.net/kml/2.2",   children.get(1).getNamespaceURI());
        assertEquals("child 2 name",                            "colorMode",                        children.get(1).getNodeName());
        assertEquals("child 2 value",                           "random",                           children.get(1).getTextContent());

        assertEquals("child 3 namespace",                       "http://www.opengis.net/kml/2.2",   children.get(2).getNamespaceURI());
        assertEquals("child 3 name",                            "scale",                            children.get(2).getNodeName());
        assertEquals("child 3 value",                           "1.5",                              children.get(2).getTextContent());

        assertEquals("child 4 namespace",                       "http://www.opengis.net/kml/2.2",   children.get(3).getNamespaceURI());
        assertEquals("child 4 name",                            "heading",                          children.get(3).getNodeName());
        assertEquals("child 4 value",                           "325.0",                            children.get(3).getTextContent());

        assertEquals("child 5 namespace",                       "http://www.opengis.net/kml/2.2",   children.get(4).getNamespaceURI());
        assertEquals("child 5 name",                            "Icon",                             children.get(4).getNodeName());

        List<Element> iconChildren = DomUtil.getChildren(children.get(4));

        assertEquals("Icon element has one child",              1,                                  iconChildren.size());

        assertEquals("Icon element child namespace",            "http://www.opengis.net/kml/2.2",   iconChildren.get(0).getNamespaceURI());
        assertEquals("Icon element child name",                 "href",                             iconChildren.get(0).getNodeName());
        assertEquals("Icon element child value",                "http://www.example.com/icon",      iconChildren.get(0).getTextContent());
    }
}
