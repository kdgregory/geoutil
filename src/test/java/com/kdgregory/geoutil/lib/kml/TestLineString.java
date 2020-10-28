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


public class TestLineString
{
    @Test
    public void testConstructors() throws Exception
    {
        List<Coordinates> cs = Arrays.asList(new Coordinates(12,34), new Coordinates(12,34,56));

        assertEquals("constructed from list of coordinates",    cs,     new LineString(cs).getCoordinates());
        assertEquals("constructed from explicit coordinates",   cs,     new LineString(new Coordinates(12,34), new Coordinates(12,34,56)).getCoordinates());
        assertEquals("constructed from serialized string",      cs,     new LineString("34.0,12.0 34.0,12.0,56.0").getCoordinates());
    }


    @Test
    public void testAccessors() throws Exception
    {
        LineString ls = new LineString(new Coordinates(12,34), new Coordinates(12,34,56));

        assertNull("default altitudeMode",                                      ls.getAltitudeMode());
        assertEquals("setAltitudeMode()",       ls,                             ls.setAltitudeMode(AltitudeMode.clampToGround));
        assertEquals("getAltitudeMode()",       AltitudeMode.clampToGround,     ls.getAltitudeMode());

        assertNull("default extrude",                                           ls.getExtrude());
        assertEquals("setExtrude()",            ls,                             ls.setExtrude(Boolean.FALSE));
        assertEquals("getExtrude()",            Boolean.FALSE,                  ls.getExtrude());

        assertNull("default tessellate",                                        ls.getTessellate());
        assertEquals("setTessellate()",         ls,                             ls.setTessellate(Boolean.TRUE));
        assertEquals("getTessellate()",         Boolean.TRUE,                   ls.getTessellate());
    }


    @Test
    public void testAppendAsXmlMinimal() throws Exception
    {
        LineString m = new LineString(new Coordinates(12, 34));

        Element parent = DomUtil.newDocument("irrelevant");
        Element child = m.appendAsXml(parent);

        assertEquals("added single child to existing parent",   1,                                  DomUtil.getChildren(parent).size());
        assertSame("returned child",                            child,                              DomUtil.getChildren(parent).get(0));
        assertEquals("child namespace",                         "http://www.opengis.net/kml/2.2",   child.getNamespaceURI());
        assertEquals("child name",                              "LineString",                       child.getNodeName());

        // we care about order, so will retrieve all nested elements and access via index
        List<Element> nested = DomUtil.getChildren(child);

        assertEquals("number of data elements",                 1,                                  nested.size());

        assertEquals("coordinates namespace",                   "http://www.opengis.net/kml/2.2",   nested.get(0).getNamespaceURI());
        assertEquals("coordinates name",                        "coordinates",                      nested.get(0).getNodeName());
        assertEquals("coordinates value",                       "34.0,12.0",                        nested.get(0).getTextContent());
    }


    @Test
    public void testAppendAsXmlComplete() throws Exception
    {
        LineString m = new LineString(new Coordinates(12, 34, 56), new Coordinates(34, 56, 78))
                       .setAltitudeMode(AltitudeMode.relativeToGround)
                       .setExtrude(Boolean.TRUE)
                       .setTessellate(Boolean.FALSE);

        Element parent = DomUtil.newDocument("irrelevant");
        Element child = m.appendAsXml(parent);

        assertEquals("added single child to existing parent",   1,                                  DomUtil.getChildren(parent).size());
        assertSame("returned child",                            child,                              DomUtil.getChildren(parent).get(0));
        assertEquals("child namespace",                         "http://www.opengis.net/kml/2.2",   child.getNamespaceURI());
        assertEquals("child name",                              "LineString",                       child.getNodeName());

        // we care about order, so will retrieve all nested elements and access via index
        List<Element> nested = DomUtil.getChildren(child);

        assertEquals("number of data elements",                 4,                                  nested.size());

        assertEquals("altitudeMode namespace",                  "http://www.opengis.net/kml/2.2",   nested.get(0).getNamespaceURI());
        assertEquals("altitudeMode name",                       "extrude",                          nested.get(0).getNodeName());
        assertEquals("altitudeMode value",                      "1",                                nested.get(0).getTextContent());

        assertEquals("altitudeMode namespace",                  "http://www.opengis.net/kml/2.2",   nested.get(1).getNamespaceURI());
        assertEquals("altitudeMode name",                       "tessellate",                       nested.get(1).getNodeName());
        assertEquals("altitudeMode value",                      "0",                                nested.get(1).getTextContent());

        assertEquals("altitudeMode namespace",                  "http://www.opengis.net/kml/2.2",   nested.get(2).getNamespaceURI());
        assertEquals("altitudeMode name",                       "altitudeMode",                     nested.get(2).getNodeName());
        assertEquals("altitudeMode value",                      "relativeToGround",                 nested.get(2).getTextContent());

        assertEquals("coordinates namespace",                   "http://www.opengis.net/kml/2.2",   nested.get(3).getNamespaceURI());
        assertEquals("coordinates name",                        "coordinates",                      nested.get(3).getNodeName());
        assertEquals("coordinates value",                       "34.0,12.0,56.0 56.0,34.0,78.0",    nested.get(3).getTextContent());
    }


    @Test
    public void testFromXmlMinimal() throws Exception
    {
        Document dom = XmlBuilder.element("http://earth.google.com/kml/2.1", "LineString",
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "coordinates",    XmlBuilder.text("34.0,12.0 34.0,12.0,56.0")))
                       .toDOM();

        LineString p = LineString.fromXml(dom.getDocumentElement());

        // coordinates should be parsed

        Coordinates c1 = new Coordinates(12, 34);
        Coordinates c2 = new Coordinates(12, 34, 56);
        assertEquals("coordinates",         Arrays.asList(c1, c2),              p.getCoordinates());

        // everything else should be left as default

        assertNull("altitudeMode",          p.getAltitudeMode());
        assertNull("extrude",               p.getExtrude());
        assertNull("tessellate",            p.getTessellate());
    }


    @Test
    public void testFromXmlComplete() throws Exception
    {
        Document dom = XmlBuilder.element("http://earth.google.com/kml/2.1", "LineString",
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "extrude",        XmlBuilder.text("1")),
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "tessellate",     XmlBuilder.text("0")),
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "altitudeMode",   XmlBuilder.text("absolute")),
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "coordinates",    XmlBuilder.text("34.0,12.0 34.0,12.0,56.0")))
                       .toDOM();

        LineString p = LineString.fromXml(dom.getDocumentElement());

        Coordinates c1 = new Coordinates(12, 34);
        Coordinates c2 = new Coordinates(12, 34, 56);
        assertEquals("coordinates",         Arrays.asList(c1, c2),              p.getCoordinates());

        // everything else should be unchanged

        assertEquals("altitudeMode",        AltitudeMode.absolute,              p.getAltitudeMode());
        assertEquals("extrude",             Boolean.TRUE,                       p.getExtrude());
        assertEquals("tessellate",          Boolean.FALSE,                      p.getTessellate());
    }


    @Test
    public void testFromXmlMissingGeometry() throws Exception
    {
        // according to the schema, this is legal; according to the docs, it isn't; we'll go with the docs
        Document dom = XmlBuilder.element("http://earth.google.com/kml/2.1", "LineString")
                       .toDOM();

        try
        {
            LineString.fromXml(dom.getDocumentElement());
            fail("should not have allowed LineString without coordinates");
        }
        catch (IllegalArgumentException ex)
        {
            assertEquals("exception message", "LineString must have coordinates", ex.getMessage());
        }
    }
}