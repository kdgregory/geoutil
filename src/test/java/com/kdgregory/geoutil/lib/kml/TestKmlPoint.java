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


public class TestKmlPoint
{
    @Test
    public void testConstruction() throws Exception
    {
        KmlPoint p1 = new KmlPoint(12, 34);
        assertEquals("p1 lat",  12.0,   p1.getLat(), 0.0);
        assertEquals("p1 lon",  34.0,   p1.getLon(), 0.0);
        assertEquals("p1 alt",  null,   p1.getAltitude());

        KmlPoint p2 = new KmlPoint(12, 34, 56);
        assertEquals("p1 lat",  12.0,   p2.getLat(), 0.0);
        assertEquals("p1 lon",  34.0,   p2.getLon(), 0.0);
        assertEquals("p1 alt",  56.0,   p2.getAltitude().doubleValue(), 0.0);
    }


    @Test
    public void testFromCoordinates() throws Exception
    {
        KmlPoint p1 = KmlPoint.fromCoordinates("12,34");
        assertEquals("p1 lat",  12.0,   p1.getLat(), 0.0);
        assertEquals("p1 lon",  34.0,   p1.getLon(), 0.0);
        assertEquals("p1 alt",  null,   p1.getAltitude());

        KmlPoint p2 = KmlPoint.fromCoordinates("12,34,56");
        assertEquals("p1 lat",  12.0,   p2.getLat(), 0.0);
        assertEquals("p1 lon",  34.0,   p2.getLon(), 0.0);
        assertEquals("p1 alt",  56.0,   p2.getAltitude().doubleValue(), 0.0);
    }


    @Test
    public void testAccessors() throws Exception
    {
        KmlPoint p = new KmlPoint(12, 34);

        assertEquals("getCoordinates() 1",      "12.0,34.0",                    p.getCoordinates());

        assertEquals("setAltitude()",           p,                              p.setAltitude(new Double(56)));
        assertEquals("getAltitude()",           new Double(56),                 p.getAltitude());
        assertEquals("getCoordinates() 2",      "12.0,34.0,56.0",               p.getCoordinates());

        assertNull("default altitudeMode",                                      p.getAltitudeMode());
        assertEquals("setAltitudeMode()",       p,                              p.setAltitudeMode(AltitudeMode.clampToGround));
        assertEquals("getAltitudeMode()",       AltitudeMode.clampToGround,     p.getAltitudeMode());

        assertNull("default extrude",                                           p.getExtrude());
        assertEquals("setExtrude()",            p,                              p.setExtrude(Boolean.FALSE));
        assertEquals("getExtrude()",            Boolean.FALSE,                  p.getExtrude());
    }


    @Test
    public void testFromXmlMinimal() throws Exception
    {
        Document dom = XmlBuilder.element("irrelevant",
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "coordinates",    XmlBuilder.text("12.0,34.0")))
                       .toDOM();

        KmlPoint p = KmlPoint.fromXml(dom.getDocumentElement());

        assertEquals("lat",  12.0,      p.getLat(), 0.0);
        assertEquals("lon",  34.0,      p.getLon(), 0.0);

        assertNull("altitude",          p.getAltitude());
        assertNull("altitudeMode",      p.getAltitudeMode());
        assertNull("extrude",           p.getExtrude());
    }


    @Test
    public void testFromXmlComplete() throws Exception
    {
        Document dom = XmlBuilder.element("irrelevant",
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "extrude",        XmlBuilder.text("1")),
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "altitudeMode",   XmlBuilder.text("relativeToGround")),
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "coordinates",    XmlBuilder.text("12.0,34.0,56.0")))
                       .toDOM();

        KmlPoint p = KmlPoint.fromXml(dom.getDocumentElement());

        assertEquals("lat",             12.0,                           p.getLat(), 0.0);
        assertEquals("lon",             34.0,                           p.getLon(), 0.0);
        assertEquals("altitude",        56.0,                           p.getAltitude().doubleValue(), 0.0);

        assertEquals("extrude",         Boolean.TRUE,                   p.getExtrude());
        assertEquals("altitudeMode",    AltitudeMode.relativeToGround,  p.getAltitudeMode());
    }


    @Test
    public void testAppendAsXmlMinimal() throws Exception
    {
        KmlPoint p = new KmlPoint(12, 34);

        Element parent = DomUtil.newDocument("irrelevant");
        p.appendAsXml(parent);

        assertEquals("added single child to existing", 1, DomUtil.getChildren(parent).size());

        Element ep = DomUtil.getChild(parent, "http://www.opengis.net/kml/2.2", "Point");

        // we care about order, so will retrieve all children and access via index
        List<Element> children = DomUtil.getChildren(ep);

        assertEquals("number of child elements",    1,                                  children.size());

        assertEquals("coordinates namespace",       "http://www.opengis.net/kml/2.2",   children.get(0).getNamespaceURI());
        assertEquals("coordinates name",            "coordinates",                      children.get(0).getNodeName());
        assertEquals("coordinates value",           "12.0,34.0",                        children.get(0).getTextContent());
    }


    @Test
    public void testAppendAsXmlComplete() throws Exception
    {
        KmlPoint p = new KmlPoint(12, 34)
                     .setAltitude(56.0)
                     .setAltitudeMode(AltitudeMode.clampToGround)
                     .setExtrude(Boolean.TRUE);

        Element parent = DomUtil.newDocument("irrelevant");
        p.appendAsXml(parent);

        assertEquals("added single child to existing", 1, DomUtil.getChildren(parent).size());

        Element ep = DomUtil.getChild(parent, "http://www.opengis.net/kml/2.2", "Point");

        // we care about order, so will retrieve all children and access via index
        List<Element> children = DomUtil.getChildren(ep);

        assertEquals("number of child elements",    3,                                  children.size());

        assertEquals("altitudeMode namespace",      "http://www.opengis.net/kml/2.2",   children.get(0).getNamespaceURI());
        assertEquals("altitudeMode name",           "extrude",                          children.get(0).getNodeName());
        assertEquals("altitudeMode value",          "1",                                children.get(0).getTextContent());

        assertEquals("altitudeMode namespace",      "http://www.opengis.net/kml/2.2",   children.get(1).getNamespaceURI());
        assertEquals("altitudeMode name",           "altitudeMode",                     children.get(1).getNodeName());
        assertEquals("altitudeMode value",          "clampToGround",                    children.get(1).getTextContent());

        assertEquals("coordinates namespace",       "http://www.opengis.net/kml/2.2",   children.get(2).getNamespaceURI());
        assertEquals("coordinates name",            "coordinates",                      children.get(2).getNodeName());
        assertEquals("coordinates value",           "12.0,34.0,56.0",                   children.get(2).getTextContent());
    }
}
