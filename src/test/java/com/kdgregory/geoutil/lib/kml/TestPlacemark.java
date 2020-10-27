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

import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.practicalxml.DomUtil;
import net.sf.practicalxml.builder.XmlBuilder;


public class TestPlacemark
{
    @Test
    public void testAccessors() throws Exception
    {
        // note: this is also the place where we test shared Feature capabilities

        Placemark m = new Placemark();

        assertNull("getName(), initial value",                                  m.getName());
        assertEquals("setName()",                           m,                  m.setName("test"));
        assertEquals("getName()",                           "test",             m.getName());

        assertNull("getVisibility(), initial value",                            m.getVisibility());
        assertEquals("setVisibility()",                     m,                  m.setVisibility(Boolean.TRUE));
        assertEquals("getVisibility()",                     Boolean.TRUE,       m.getVisibility());

        assertNull("getDescription(), initial value",                           m.getDescription());
        assertEquals("setDescription()",                    m,                  m.setDescription("example"));
        assertEquals("getDescription()",                    "example",          m.getDescription());

        assertNull("getTimestamp(), initial value",                             m.getTimestamp());
        assertEquals("setTimestamp()",                      m,                  m.setTimestamp(new Timestamp(123)));
        assertEquals("getTimestamp()",                      new Timestamp(123), m.getTimestamp());

        assertNull("getStyleRef(), initial value",                              m.getStyleRef());
        assertEquals("setStyleRef()",                       m,                  m.setStyleRef("uniqueRef"));
        assertEquals("getStyleRef()",                       "uniqueRef",        m.getStyleRef());

        assertEquals("setLocalStyleRef()",                  m,                  m.setLocalStyleRef("uniqueRef"));
        assertEquals("getStyleRef() (local)",               "#uniqueRef",       m.getStyleRef());

        KmlPoint p = new KmlPoint(12, 34);

        assertNull("getGeometry(), initial value",                              m.getGeometry());
        assertEquals("setGeometry()",                       m,                  m.setGeometry(p));
        assertSame("getGeometry()",                         p,                  m.getGeometry());

        Style s = new Style();

        assertNull("getStyleSelector(), initial value",                         m.getStyleSelector());
        assertEquals("setStyleSelector()",                  m,                  m.setStyleSelector(s));
        assertSame("getStyleSelector()",                    s,                  m.getStyleSelector());
    }


    @Test
    public void testAppendAsXmlMinimal() throws Exception
    {
        Placemark m = new Placemark();

        Element parent = DomUtil.newDocument("irrelevant");
        Element child = m.appendAsXml(parent);

        assertEquals("added single child to existing parent",   1,                                  DomUtil.getChildren(parent).size());
        assertSame("returned child",                            child,                              DomUtil.getChildren(parent).get(0));
        assertEquals("child namespace",                         "http://www.opengis.net/kml/2.2",   child.getNamespaceURI());
        assertEquals("child name",                              "Placemark",                        child.getNodeName());

        assertEquals("number of data elements",                 0,                                  DomUtil.getChildren(child).size());
    }


    @Test
    public void testAppendAsXmlComplete() throws Exception
    {
        KmlPoint p = new KmlPoint(12, 34).setExtrude(Boolean.TRUE);
        Style s = new Style().setLineStyle(new LineStyle().setWidth(2));
        Placemark m = new Placemark()
                      .setName("example")
                      .setDescription("a description")
                      .setVisibility(Boolean.TRUE)
                      .setGeometry(p)
                      .setTimestamp(new Timestamp(1577547828000L))
                      .setStyleRef("styleId")
                      .setStyleSelector(s);

        Element parent = DomUtil.newDocument("irrelevant");
        Element child = m.appendAsXml(parent);

        assertEquals("added single child to existing parent",   1,                                  DomUtil.getChildren(parent).size());
        assertSame("returned child",                            child,                              DomUtil.getChildren(parent).get(0));
        assertEquals("child namespace",                         "http://www.opengis.net/kml/2.2",   child.getNamespaceURI());
        assertEquals("child name",                              "Placemark",                        child.getNodeName());

        // we care about order, so will retrieve all children and access via index
        List<Element> dataElements = DomUtil.getChildren(child);

        assertEquals("number of data elements",                 7,                                  dataElements.size());

        assertEquals("first child namespace",                   "http://www.opengis.net/kml/2.2",   dataElements.get(0).getNamespaceURI());
        assertEquals("first child name",                        "name",                             dataElements.get(0).getNodeName());
        assertEquals("first child value",                       "example",                          dataElements.get(0).getTextContent());

        assertEquals("second child namespace",                  "http://www.opengis.net/kml/2.2",   dataElements.get(1).getNamespaceURI());
        assertEquals("second child name",                       "visibility",                       dataElements.get(1).getNodeName());
        assertEquals("second child value",                      "1",                                dataElements.get(1).getTextContent());

        assertEquals("third child namespace",                   "http://www.opengis.net/kml/2.2",   dataElements.get(2).getNamespaceURI());
        assertEquals("third child name",                        "description",                      dataElements.get(2).getNodeName());
        assertEquals("third child value",                       "a description",                    dataElements.get(2).getTextContent());

        assertEquals("fourth child namespace",                  "http://www.opengis.net/kml/2.2",   dataElements.get(3).getNamespaceURI());
        assertEquals("fourth child name",                       "TimeStamp",                        dataElements.get(3).getNodeName());
        assertEquals("fourth child value",                      "2019-12-28T15:43:48Z",             DomUtil.getChild(dataElements.get(3), "http://www.opengis.net/kml/2.2", "when").getTextContent());

        assertEquals("fifth child namespace",                   "http://www.opengis.net/kml/2.2",   dataElements.get(4).getNamespaceURI());
        assertEquals("fifth child name",                        "styleUrl",                         dataElements.get(4).getNodeName());
        assertEquals("fifth child value",                       "styleId",                          dataElements.get(4).getTextContent());

        assertEquals("sixth child namespace",                   "http://www.opengis.net/kml/2.2",   dataElements.get(5).getNamespaceURI());
        assertEquals("sixth child name",                        "Style",                            dataElements.get(5).getNodeName());

        assertEquals("seventh child namespace",                 "http://www.opengis.net/kml/2.2",   dataElements.get(6).getNamespaceURI());
        assertEquals("seventh child name",                      "Point",                            dataElements.get(6).getNodeName());

        // rather than verify the Style element's contents, we'll try to convert it and verify its contents

        Style ss = Style.fromXml(dataElements.get(5));

        assertEquals("nested style element",                    2.0,                                ss.getLineStyle().getWidth(), 0.0);

        // ditto with the nested Point

        KmlPoint pp =  KmlPoint.fromXml(dataElements.get(6));

        assertEquals("nested point coordinates",                new Coordinates(12,34),             pp.getCoordinates());
        assertEquals("nested point extrude",                    Boolean.TRUE,                       pp.getExtrude());

    }


    @Test
    public void testAppendAsXmlLineString() throws Exception
    {
        Coordinates c1 = new Coordinates(12,34);
        Coordinates c2 = new Coordinates(23, 45, 67);

        Placemark m = new Placemark()
                      .setGeometry(new LineString(c1, c2));

        Element parent = DomUtil.newDocument("irrelevant");
        Element child = m.appendAsXml(parent);

        assertEquals("added single child to existing parent",   1,                                  DomUtil.getChildren(parent).size());
        assertSame("returned child",                            child,                              DomUtil.getChildren(parent).get(0));
        assertEquals("child namespace",                         "http://www.opengis.net/kml/2.2",   child.getNamespaceURI());
        assertEquals("child name",                              "Placemark",                        child.getNodeName());

        // we care about order, so will retrieve all children and access via index
        List<Element> dataElements = DomUtil.getChildren(child);

        assertEquals("number of data elements",                 1,                                  dataElements.size());

        assertEquals("data element 1 namespace",                "http://www.opengis.net/kml/2.2",   dataElements.get(0).getNamespaceURI());
        assertEquals("geometry name",                           "LineString",                       dataElements.get(0).getNodeName());

        // let the LineString element parse itself and verify results

        assertEquals("nested linestring coordinates",            Arrays.asList(c1, c2),              LineString.fromXml(dataElements.get(0)).getCoordinates());
    }


    @Test
    public void testFromXmlMinimal() throws Exception
    {
        Document dom = XmlBuilder.element("http://earth.google.com/kml/2.1", "Placemark")
                       .toDOM();

        Placemark pm = Placemark.fromXml(dom.getDocumentElement());

        assertNull("name",              pm.getName());
        assertNull("visibility",        pm.getVisibility());
        assertNull("description",       pm.getDescription());
        assertNull("geometry",          pm.getGeometry());
        assertNull("style ref",         pm.getStyleRef());
        assertNull("style selector",    pm.getStyleSelector());
    }


    @Test
    public void testFromXmlComplete() throws Exception
    {
        Document dom = XmlBuilder.element("http://earth.google.com/kml/2.1", "Placemark",
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "name",               XmlBuilder.text("example")),
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "visibility",         XmlBuilder.text("1")),
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "description",        XmlBuilder.text("a description")),
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "TimeStamp",
                                XmlBuilder.element("http://earth.google.com/kml/2.1", "when",           XmlBuilder.text("2019-12-28T15:43:48Z"))),
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "styleUrl",           XmlBuilder.text("style-reference")),
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "Style",
                                XmlBuilder.element("http://earth.google.com/kml/2.1", "LineStyle",
                                    XmlBuilder.element("http://earth.google.com/kml/2.1", "width",      XmlBuilder.text("2.0")))),
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "Point",
                                XmlBuilder.element("http://earth.google.com/kml/2.1", "coordinates",    XmlBuilder.text("34.0,12.0,56.0"))))
                       .toDOM();

        Placemark pm = Placemark.fromXml(dom.getDocumentElement());

        assertEquals("name",                    "example",                  pm.getName());
        assertEquals("visibility",              Boolean.TRUE,               pm.getVisibility());
        assertEquals("description",             "a description",            pm.getDescription());
        assertEquals("timestamp",               1577547828000L,             pm.getTimestamp().asMillis().longValue());
        assertEquals("style ref",               "style-reference",          pm.getStyleRef());

        Style s = pm.getStyleSelector();

        assertEquals("style (width)",           2.0,                        s.getLineStyle().getWidth(), 0.0);

        KmlPoint p = (KmlPoint)pm.getGeometry();

        assertEquals("geometry, coordinates",   new Coordinates(12,34,56),  p.getCoordinates());
    }


    @Test
    public void testFromXmlLineString() throws Exception
    {
        Document dom = XmlBuilder.element("http://earth.google.com/kml/2.1", "Placemark",
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "LineString",
                                XmlBuilder.element("http://earth.google.com/kml/2.1", "coordinates",    XmlBuilder.text("34.0,12.0,56.0"))))
                       .toDOM();

        Placemark pm = Placemark.fromXml(dom.getDocumentElement());
        LineString g = (LineString)pm.getGeometry();

        assertEquals("geometry, coordinates", Arrays.asList(new Coordinates(12,34,56)),  g.getCoordinates());
    }



    @Test
    public void testFromXmlInvalidName() throws Exception
    {
        Document dom = XmlBuilder.element("http://earth.google.com/kml/2.1", "SomethingElse")
                       .toDOM();

        try
        {
            Placemark.fromXml(dom.getDocumentElement());
            fail("should not have parsed successfully");
        }
        catch (IllegalArgumentException ex)
        {
            assertTrue("exception message (was: " + ex.getMessage() + ")", ex.getMessage().contains("SomethingElse"));
        }
    }
}
