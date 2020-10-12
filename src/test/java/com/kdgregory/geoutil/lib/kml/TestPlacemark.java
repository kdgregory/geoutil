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


public class TestPlacemark
{
    @Test
    public void testAccessors() throws Exception
    {
        Placemark m = new Placemark();

        assertNull("getName(), initial value",                          m.getName());
        assertEquals("setName()",                        m,             m.setName("test"));
        assertEquals("getName()",                       "test",         m.getName());

        assertNull("getVisibility(), initial value",                    m.getVisibility());
        assertEquals("setVisibility()",                 m,              m.setVisibility(Boolean.TRUE));
        assertEquals("getVisibility()",                 Boolean.TRUE,   m.getVisibility());

        assertNull("getDescription(), initial value",                   m.getDescription());
        assertEquals("setDescription()",                m,              m.setDescription("example"));
        assertEquals("getDescription()",                "example",      m.getDescription());

        KmlPoint p = new KmlPoint(12, 34);

        assertNull("getGeometry(), initial value",                      m.getGeometry());
        assertEquals("setGeometry()",                   m,              m.setGeometry(p));
        assertSame("getGeometry()",                     p,              m.getGeometry());
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
        KmlPoint p = new KmlPoint(12,34);
        Placemark m = new Placemark()
                      .setName("example")
                      .setDescription("a description")
                      .setVisibility(Boolean.TRUE)
                      .setGeometry(p);

        Element parent = DomUtil.newDocument("irrelevant");
        Element child = m.appendAsXml(parent);

        assertEquals("added single child to existing parent",   1,                                  DomUtil.getChildren(parent).size());
        assertSame("returned child",                            child,                              DomUtil.getChildren(parent).get(0));
        assertEquals("child namespace",                         "http://www.opengis.net/kml/2.2",   child.getNamespaceURI());
        assertEquals("child name",                              "Placemark",                        child.getNodeName());

        // we care about order, so will retrieve all children and access via index
        List<Element> dataElements = DomUtil.getChildren(child);

        assertEquals("number of data elements",     4,                                  dataElements.size());

        assertEquals("name namespace",              "http://www.opengis.net/kml/2.2",   dataElements.get(0).getNamespaceURI());
        assertEquals("name name",                   "name",                             dataElements.get(0).getNodeName());
        assertEquals("name value",                  "example",                          dataElements.get(0).getTextContent());

        assertEquals("visibility namespace",        "http://www.opengis.net/kml/2.2",   dataElements.get(1).getNamespaceURI());
        assertEquals("visibility name",             "visibility",                       dataElements.get(1).getNodeName());
        assertEquals("visibility value",            "1",                                dataElements.get(1).getTextContent());

        assertEquals("description namespace",       "http://www.opengis.net/kml/2.2",   dataElements.get(2).getNamespaceURI());
        assertEquals("description name",            "description",                      dataElements.get(2).getNodeName());
        assertEquals("description value",           "a description",                    dataElements.get(2).getTextContent());

        assertEquals("Point namespace",             "http://www.opengis.net/kml/2.2",   dataElements.get(3).getNamespaceURI());
        assertEquals("Point name",                  "Point",                            dataElements.get(3).getNodeName());

        // rather than verify the Point element's contents, we'll try to convert it

        assertEquals("nested point", p, KmlPoint.fromXml(dataElements.get(3)));
    }

    // TODO - test conversion to XML with a different geometry


    @Test
    public void testFromXmlMinimal() throws Exception
    {
        Document dom = XmlBuilder.element("http://earth.google.com/kml/2.1", "Placemark")
                       .toDOM();

        Placemark pm = Placemark.fromXml(dom.getDocumentElement());

        assertNull("name",          pm.getName());
        assertNull("visibility",    pm.getVisibility());
        assertNull("description",   pm.getDescription());
        assertNull("geometry",      pm.getGeometry());
    }


    @Test
    public void testFromXmlComplete() throws Exception
    {
        Document dom = XmlBuilder.element("http://earth.google.com/kml/2.1", "Placemark",
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "name",               XmlBuilder.text("example")),
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "visibility",         XmlBuilder.text("1")),
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "description",        XmlBuilder.text("a description")),
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "Point",
                                XmlBuilder.element("http://earth.google.com/kml/2.1", "coordinates",    XmlBuilder.text("12.0,34.0,56.0"))))
                       .toDOM();

        Placemark pm = Placemark.fromXml(dom.getDocumentElement());

        assertEquals("name",                "example",          pm.getName());
        assertEquals("visibility",          Boolean.TRUE,       pm.getVisibility());
        assertEquals("description",         "a description",    pm.getDescription());

        KmlPoint p = (KmlPoint)pm.getGeometry();

        assertEquals("geometry, lat",       12.0,               p.getLat(), 0.0);
        assertEquals("geometry, lon",       34.0,               p.getLon(), 0.0);
        assertEquals("geometry, altitude",  56.0,               p.getAltitude().doubleValue(), 0.0);
    }

    // TODO - test alternate geometries


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