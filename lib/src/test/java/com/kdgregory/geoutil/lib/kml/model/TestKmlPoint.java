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

package com.kdgregory.geoutil.lib.kml.model;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.practicalxml.DomUtil;
import net.sf.practicalxml.builder.XmlBuilder;

import com.kdgregory.geoutil.lib.kml.fieldtypes.AltitudeMode;
import com.kdgregory.geoutil.lib.kml.model.KmlPoint;


public class TestKmlPoint
{
    @Test
    public void testConstruction() throws Exception
    {
        // simplest way to assert constrcution is to get the coordinates

        assertEquals("lat/lon",     "34.0,12.0",        new KmlPoint(12, 34).getCoordinates().toString());
        assertEquals("lat/lon/alt", "34.0,12.0,56.0",   new KmlPoint(12, 34, 56).getCoordinates().toString());
        assertEquals("lat/lon/alt", "34.0,12.0,56.0",   new KmlPoint("34.0,12.0,56.0").getCoordinates().toString());
    }


    @Test
    public void testAccessors() throws Exception
    {
        KmlPoint p = new KmlPoint(12, 34);

        assertEquals("getCoordinates()",            "34.0,12.0",                        p.getCoordinates().toString());

        assertNull("default altitudeMode",                                              p.getAltitudeMode());
        assertEquals("setAltitudeMode()",           p,                                  p.setAltitudeMode(AltitudeMode.clampToGround));
        assertEquals("getAltitudeModeString()",     "clampToGround",                    p.getAltitudeModeString());
        assertEquals("setAltitudeModeString()",     p,                                  p.setAltitudeModeString("absolute"));
        assertEquals("getAltitudeMode()",           AltitudeMode.absolute,              p.getAltitudeMode());

        assertNull("default extrude",                                                   p.getExtrude());
        assertEquals("setExtrude()",                p,                                  p.setExtrude(Boolean.FALSE));
        assertEquals("getExtrude()",                Boolean.FALSE,                      p.getExtrude());
    }


    @Test
    public void testAppendAsXmlMinimal() throws Exception
    {
        KmlPoint p = new KmlPoint(12, 34);

        Element parent = DomUtil.newDocument("irrelevant");
        Element child = p.appendAsXml(parent);

        assertEquals("added single child to existing parent",   1,                                  DomUtil.getChildren(parent).size());
        assertSame("returned child",                            child,                              DomUtil.getChildren(parent).get(0));
        assertEquals("child namespace",                         "http://www.opengis.net/kml/2.2",   child.getNamespaceURI());
        assertEquals("child name",                              "Point",                            child.getNodeName());

        List<Element> dataElements = DomUtil.getChildren(child);

        assertEquals("number of data elements",                 1,                                  dataElements.size());

        assertEquals("data element 1 namespace",                "http://www.opengis.net/kml/2.2",   dataElements.get(0).getNamespaceURI());
        assertEquals("data element 1 name",                     "coordinates",                      dataElements.get(0).getNodeName());
        assertEquals("data element 1 value",                    "34.0,12.0",                        dataElements.get(0).getTextContent());
    }


    @Test
    public void testAppendAsXmlComplete() throws Exception
    {
        KmlPoint p = new KmlPoint(12, 34, 56)
                     .setAltitudeMode(AltitudeMode.clampToGround)
                     .setExtrude(Boolean.TRUE);

        Element parent = DomUtil.newDocument("irrelevant");
        Element child = p.appendAsXml(parent);

        assertEquals("added single child to existing parent",   1,                                  DomUtil.getChildren(parent).size());
        assertSame("returned child",                            child,                              DomUtil.getChildren(parent).get(0));
        assertEquals("child namespace",                         "http://www.opengis.net/kml/2.2",   child.getNamespaceURI());
        assertEquals("child name",                              "Point",                            child.getNodeName());

        List<Element> dataElements = DomUtil.getChildren(child);

        assertEquals("number of data elements",                 3,                                  dataElements.size());

        assertEquals("data element 1 namespace",                "http://www.opengis.net/kml/2.2",   dataElements.get(0).getNamespaceURI());
        assertEquals("data element 1 name",                     "extrude",                          dataElements.get(0).getNodeName());
        assertEquals("data element 1 value",                    "1",                                dataElements.get(0).getTextContent());

        assertEquals("data element 1 namespace",                "http://www.opengis.net/kml/2.2",   dataElements.get(1).getNamespaceURI());
        assertEquals("data element 1 name",                     "altitudeMode",                     dataElements.get(1).getNodeName());
        assertEquals("data element 1 value",                    "clampToGround",                    dataElements.get(1).getTextContent());

        assertEquals("data element 1 namespace",                "http://www.opengis.net/kml/2.2",   dataElements.get(2).getNamespaceURI());
        assertEquals("data element 1 name",                     "coordinates",                      dataElements.get(2).getNodeName());
        assertEquals("data element 1 value",                    "34.0,12.0,56.0",                   dataElements.get(2).getTextContent());
    }


    @Test
    public void testFromXmlMinimal() throws Exception
    {
        Document dom = XmlBuilder.element("http://earth.google.com/kml/2.1", "Point",
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "coordinates",    XmlBuilder.text("12.0,34.0")))
                       .toDOM();

        KmlPoint p = KmlPoint.fromXml(dom.getDocumentElement());

        assertEquals("coordinates",     "12.0,34.0",    p.getCoordinates().toString());
        assertNull("altitudeMode",      p.getAltitudeMode());
        assertNull("extrude",           p.getExtrude());
    }


    @Test
    public void testFromXmlComplete() throws Exception
    {
        Document dom = XmlBuilder.element("http://earth.google.com/kml/2.1", "Point",
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "extrude",        XmlBuilder.text("1")),
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "altitudeMode",   XmlBuilder.text("relativeToGround")),
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "coordinates",    XmlBuilder.text("12.0,34.0,56.0")))
                       .toDOM();

        KmlPoint p = KmlPoint.fromXml(dom.getDocumentElement());

        assertEquals("coordinates",     "12.0,34.0,56.0",                   p.getCoordinates().toString());
        assertEquals("extrude",         Boolean.TRUE,                       p.getExtrude());
        assertEquals("altitudeMode",    AltitudeMode.relativeToGround,      p.getAltitudeMode());
    }


    @Test
    public void testFromXmlInvalidName() throws Exception
    {
        Document dom = XmlBuilder.element("http://earth.google.com/kml/2.1", "SomethingElse",
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "coordinates",    XmlBuilder.text("12.0,34.0")))
                       .toDOM();

        try
        {
            KmlPoint.fromXml(dom.getDocumentElement());
            fail("should not have parsed successfully");
        }
        catch (IllegalArgumentException ex)
        {
            assertTrue("exception message (was: " + ex.getMessage() + ")", ex.getMessage().contains("SomethingElse"));
        }
    }
}
