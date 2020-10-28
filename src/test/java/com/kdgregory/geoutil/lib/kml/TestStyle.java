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

import com.kdgregory.geoutil.lib.internal.XmlUtils;


public class TestStyle
{
    @Test
    public void testAccessors() throws Exception
    {
        Style s = new Style();

        assertNull("getId() initial value",                                 s.getId());
        assertEquals("setId()",                     s,                      s.setId("argle"));
        assertEquals("getId()",                     "argle",                s.getId());

        LineStyle ls = new LineStyle();

        assertNull("getLineStyle() initial value",                          s.getLineStyle());
        assertEquals("setLineStyle()",              s,                      s.setLineStyle(ls));
        assertSame("getLineStyle()",                ls,                     s.getLineStyle());
    }


    @Test
    public void testAppendAsXmlMinimal() throws Exception
    {
        Style s = new Style();

        Element parent = DomUtil.newDocument("irrelevant");
        Element child = s.appendAsXml(parent);

        assertEquals("added single child to existing parent",   1,                                  DomUtil.getChildren(parent).size());
        assertSame("returned child",                            child,                              DomUtil.getChildren(parent).get(0));
        assertEquals("child namespace",                         "http://www.opengis.net/kml/2.2",   child.getNamespaceURI());
        assertEquals("child name",                              "Style",                            child.getNodeName());

        assertEquals("number of child elements",                0,                                  DomUtil.getChildren(child).size());
    }


    @Test
    public void testAppendAsXmlComplete() throws Exception
    {
        Style s = new Style()
                  .setId("uniqueId")
                  .setLineStyle(
                      new LineStyle()
                      .setColor("12345678")
                      .setColorMode(ColorMode.random)
                      .setWidth(12));

        Element parent = DomUtil.newDocument("irrelevant");
        Element child = s.appendAsXml(parent);

        assertEquals("added single child to existing parent",   1,                                  DomUtil.getChildren(parent).size());
        assertSame("returned child",                            child,                              DomUtil.getChildren(parent).get(0));
        assertEquals("child namespace",                         "http://www.opengis.net/kml/2.2",   child.getNamespaceURI());
        assertEquals("child name",                              "Style",                            child.getNodeName());
        assertEquals("child ID",                                "uniqueId",                         child.getAttribute("id"));

        List<Element> children = DomUtil.getChildren(child);

        assertEquals("number of style elements",                1,                                  children.size());

        assertEquals("element 1 namespace",                     "http://www.opengis.net/kml/2.2",   children.get(0).getNamespaceURI());
        assertEquals("element 1 name",                          "LineStyle",                        children.get(0).getNodeName());
        assertEquals("element 1 color",                         "12345678",                         XmlUtils.getChildText(children.get(0), "http://www.opengis.net/kml/2.2", "color"));
        assertEquals("element 1 colorMode",                     "random",                           XmlUtils.getChildText(children.get(0), "http://www.opengis.net/kml/2.2", "colorMode"));
        assertEquals("element 1 width",                         "12.0",                             XmlUtils.getChildText(children.get(0), "http://www.opengis.net/kml/2.2", "width"));
    }


    @Test
    public void testFromXmlMinimal() throws Exception
    {
        Element root = XmlBuilder.element("http://earth.google.com/kml/2.1", "Style")
                       .toDOM().getDocumentElement();

        Style s = Style.fromXml(root);

        assertNull("id",            s.getId());
        assertNull("lineStyle",     s.getLineStyle());
    }


    @Test
    public void testFromXmlComplete() throws Exception
    {
        // note: this creates empty references for all style types because real-world documents
        //       include all; we ignore the ones that we don't handle

        Element root = XmlBuilder.element("http://earth.google.com/kml/2.1", "Style",
                            XmlBuilder.attribute("id", "somethingUnique"),
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "IconStyle"),
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "LabelStyle"),
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "LineStyle",
                                XmlBuilder.element("http://earth.google.com/kml/2.1", "color", XmlBuilder.text("12345678")),
                                XmlBuilder.element("http://earth.google.com/kml/2.1", "colorMode", XmlBuilder.text("random")),
                                XmlBuilder.element("http://earth.google.com/kml/2.1", "width", XmlBuilder.text("1.5")),
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "PolyStyle"),
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "BalloonStyle"),
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "ListStyle")
                       )).toDOM().getDocumentElement();

        Style s = Style.fromXml(root);

        assertEquals("id",                      "somethingUnique",          s.getId());
        assertEquals("lineStyle color",         "12345678",                 s.getLineStyle().getColor());
        assertEquals("lineStyle colorMode",     ColorMode.random,           s.getLineStyle().getColorMode());
        assertEquals("lineStyle width",         1.5,                        s.getLineStyle().getWidth(), 0.0);
    }


    @Test
    public void testFromXmlInvalidName() throws Exception
    {
        Document dom = XmlBuilder.element("http://earth.google.com/kml/2.1", "SomethingElse").toDOM();

        try
        {
            Style.fromXml(dom.getDocumentElement());
            fail("should not have parsed successfully");
        }
        catch (IllegalArgumentException ex)
        {
            assertTrue("exception message (was: " + ex.getMessage() + ")", ex.getMessage().contains("SomethingElse"));
        }
    }
}
