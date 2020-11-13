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

package com.kdgregory.geoutil.lib.gpx.model;

import java.time.Instant;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.practicalxml.DomUtil;
import net.sf.practicalxml.builder.XmlBuilder;

import com.kdgregory.geoutil.lib.gpx.model.GpxPoint;
import com.kdgregory.geoutil.lib.shared.Point;


public class TestGpxPoint
{
    @Test
    public void testConstructor() throws Exception
    {
        GpxPoint p = new GpxPoint(12.34, 45.67);

        assertEquals("lat", 12.34, p.getLat(), 0.01);
        assertEquals("lon", 45.67, p.getLon(), 0.01);

        // all values set via setters should be null;

        assertNull("elevation",                     p.getElevation());
        assertNull("magnetic variance",             p.getMagneticVariance());
        assertNull("geoid height",                  p.getGeoidHeight());
        assertNull("name",                          p.getName());
        assertNull("comment",                       p.getComment());
        assertNull("description",                   p.getDescription());

        // checking variants of timestamp separately; note default vale for millis
        assertNull("timestamp",                     p.getTimestamp());
        assertNull("timestamp as string",           p.getTimestampString());
        assertEquals("timestamp as millis",     0,  p.getTimestampMillis());

    }


    @Test
    public void testGettersAndSetters() throws Exception
    {
        GpxPoint p = new GpxPoint(12.34, 45.67)
                  .setTimestamp(Instant.ofEpochMilli(1577547828000L))
                  .setElevation(12345.0)
                  .setMagneticVariance(15.2)
                  .setGeoidHeight(321.0)
                  .setName("testing")
                  .setComment("a comment")
                  .setDescription("a description");

        assertEquals("lat", 12.34, p.getLat(), 0.01);
        assertEquals("lon", 45.67, p.getLon(), 0.01);

        // check most of the fields in the order they appear in the GPX spec

        assertEquals("elevation",               Double.valueOf(12345.0),                p.getElevation());
        assertEquals("magnetic variance",       Double.valueOf(15.2),                   p.getMagneticVariance());
        assertEquals("geoid height",            Double.valueOf(321.0),                  p.getGeoidHeight());
        assertEquals("name",                    "testing",                              p.getName());
        assertEquals("comment",                 "a comment",                            p.getComment());
        assertEquals("description",             "a description",                        p.getDescription());

        // and the various variants of timestamp

        assertEquals("timestamp",               Instant.ofEpochMilli(1577547828000L),   p.getTimestamp());
        assertEquals("timestamp as string",     "2019-12-28T15:43:48Z",                 p.getTimestampString());
        assertEquals("timestamp as millies",    1577547828000L,                         p.getTimestampMillis());

        // ensure that lat/lon/elevation/timestamp are managed by the underlying Point object

        assertEquals("underlying point",        new Point(12.34, 45.67, 12345.0, 1577547828000L),   p.getPoint());
    }


    @Test
    public void testAlternativeTimestampFormats() throws Exception
    {
        GpxPoint p1 = new GpxPoint(0,0).setTimestampMillis(1577547828000L);

        assertEquals("timestamp",               Instant.ofEpochMilli(1577547828000L),   p1.getTimestamp());
        assertEquals("timestamp as string",     "2019-12-28T15:43:48Z",                 p1.getTimestampString());
        assertEquals("timestamp as millies",    1577547828000L,                         p1.getTimestampMillis());

        GpxPoint p2 = new GpxPoint(0,0).setTimestampString("2019-12-28T15:43:48Z");

        assertEquals("timestamp",               Instant.ofEpochMilli(1577547828000L),   p2.getTimestamp());
        assertEquals("timestamp as string",     "2019-12-28T15:43:48Z",                 p2.getTimestampString());
        assertEquals("timestamp as millies",    1577547828000L,                         p2.getTimestampMillis());

        GpxPoint p3 = new GpxPoint(0,0).setTimestampString("2019-12-28T10:43:48-05:00");

        assertEquals("timestamp",               Instant.ofEpochMilli(1577547828000L),   p3.getTimestamp());
        assertEquals("timestamp as string",     "2019-12-28T15:43:48Z",                 p3.getTimestampString());
        assertEquals("timestamp as millies",    1577547828000L,                         p3.getTimestampMillis());
    }


    @Test
    public void testIsBetween() throws Exception
    {
        GpxPoint p = new GpxPoint(12,34).setTimestampMillis(1577547827000L);

        assertTrue("in middle of range", p.isBetween(Instant.ofEpochMilli(1577547825000L), Instant.ofEpochMilli(1577547829000L)));
        assertTrue("at start range",     p.isBetween(Instant.ofEpochMilli(1577547827000L), Instant.ofEpochMilli(1577547829000L)));
        assertTrue("at end of range",    p.isBetween(Instant.ofEpochMilli(1577547825000L), Instant.ofEpochMilli(1577547827000L)));

        assertFalse("below range",       p.isBetween(Instant.ofEpochMilli(1577547828000L), Instant.ofEpochMilli(1577547829000L)));
        assertFalse("above range",       p.isBetween(Instant.ofEpochMilli(1577547828000L), Instant.ofEpochMilli(1577547827000L)));
    }


    @Test
    public void testConvertToXmlBasic() throws Exception
    {
        // this point contains no child elements
        GpxPoint p = new GpxPoint(12,34);

        Element parent = DomUtil.newDocument("bogus");  // not a real name, and no namespace!
        p.appendAsXml(parent, "p1");

        Element ep = DomUtil.getChild(parent, "http://www.topografix.com/GPX/1/1", "p1");

        assertNotNull("point added to DOM",             ep);
        assertEquals("lat",                 "12.0",     ep.getAttribute("lat"));
        assertEquals("lon",                 "34.0",     ep.getAttribute("lon"));
    }


    @Test
    public void testConvertToXmlComplete() throws Exception
    {
        // this point contains all allowed elements
        GpxPoint p = new GpxPoint(34,56)
                   .setElevation(123.0)
                   .setTimestampMillis(1577547828000L)
                   .setMagneticVariance(15.2)
                   .setGeoidHeight(101.0)
                   .setName("testing")
                   .setComment("test comment")
                   .setDescription("a description");

        Element parent = DomUtil.newDocument("bogus");  // not a real name, and no namespace!
        p.appendAsXml(parent, "p2");

        Element ep = DomUtil.getChild(parent, "http://www.topografix.com/GPX/1/1", "p2");

        assertNotNull("point added to DOM",                 ep);
        assertEquals("lat",                     "34.0",     ep.getAttribute("lat"));
        assertEquals("lon",                     "56.0",     ep.getAttribute("lon"));

        // elements have a required order per schema, so we'll examine them one-by-one
        // we use literal strings here to verify the constants used by the class

        List<Element> ep2Children = DomUtil.getChildren(ep);

        assertEquals("number of children",      7,                                      ep2Children.size());

        assertEquals("elevation namespace",     "http://www.topografix.com/GPX/1/1",    ep2Children.get(0).getNamespaceURI());
        assertEquals("elevation name",          "ele",                                  ep2Children.get(0).getNodeName());
        assertEquals("elevation",               "123.0",                                ep2Children.get(0).getTextContent());

        assertEquals("timestamp namespace",     "http://www.topografix.com/GPX/1/1",    ep2Children.get(1).getNamespaceURI());
        assertEquals("timestamp name",          "time",                                 ep2Children.get(1).getNodeName());
        assertEquals("timestamp",               "2019-12-28T15:43:48Z",                 ep2Children.get(1).getTextContent());

        assertEquals("variance namespace",      "http://www.topografix.com/GPX/1/1",    ep2Children.get(2).getNamespaceURI());
        assertEquals("variance name",           "magvar",                               ep2Children.get(2).getNodeName());
        assertEquals("variance",                "15.2",                                 ep2Children.get(2).getTextContent());

        assertEquals("geoid height namespace",  "http://www.topografix.com/GPX/1/1",    ep2Children.get(3).getNamespaceURI());
        assertEquals("geoid height name",       "geoidheight",                          ep2Children.get(3).getNodeName());
        assertEquals("geoid height",            "101.0",                                ep2Children.get(3).getTextContent());

        assertEquals("name namespace",          "http://www.topografix.com/GPX/1/1",    ep2Children.get(4).getNamespaceURI());
        assertEquals("name name",               "name",                                 ep2Children.get(4).getNodeName());
        assertEquals("name",                    "testing",                              ep2Children.get(4).getTextContent());

        assertEquals("comment namespace",       "http://www.topografix.com/GPX/1/1",    ep2Children.get(5).getNamespaceURI());
        assertEquals("comment name",            "cmt",                                  ep2Children.get(5).getNodeName());
        assertEquals("comment",                 "test comment",                         ep2Children.get(5).getTextContent());

        assertEquals("description namespace",   "http://www.topografix.com/GPX/1/1",    ep2Children.get(6).getNamespaceURI());
        assertEquals("description name",        "desc",                                 ep2Children.get(6).getNodeName());
        assertEquals("description",             "a description",                        ep2Children.get(6).getTextContent());
    }


    @Test
    public void testConvertFromXmlBasic() throws Exception
    {
        Document dom = XmlBuilder.element("bogus",
                            XmlBuilder.attribute("lat", "12.0"),
                            XmlBuilder.attribute("lon", "34.0"))
                       .toDOM();

        GpxPoint p = new GpxPoint(dom.getDocumentElement());

        assertEquals("latitude",  12.0, p.getLat(), 0.0);
        assertEquals("longitude", 34.0, p.getLon(), 0.0);
    }


    @Test
    public void testConvertFromXmlComplete() throws Exception
    {
        Document dom = XmlBuilder.element("bogus",
                            XmlBuilder.attribute("lat", "12.0"),
                            XmlBuilder.attribute("lon", "34.0"),
                            XmlBuilder.element("http://www.topografix.com/GPX/1/1", "ele",          XmlBuilder.text("123.0")),
                            XmlBuilder.element("http://www.topografix.com/GPX/1/1", "time",         XmlBuilder.text("2019-12-28T15:43:48Z")),
                            XmlBuilder.element("http://www.topografix.com/GPX/1/1", "magvar",       XmlBuilder.text("15.2")),
                            XmlBuilder.element("http://www.topografix.com/GPX/1/1", "geoidheight",  XmlBuilder.text("101.0")),
                            XmlBuilder.element("http://www.topografix.com/GPX/1/1", "name",         XmlBuilder.text("testing")),
                            XmlBuilder.element("http://www.topografix.com/GPX/1/1", "cmt",          XmlBuilder.text("test comment")),
                            XmlBuilder.element("http://www.topografix.com/GPX/1/1", "desc",         XmlBuilder.text("a description")))
                       .toDOM();

        GpxPoint p = new GpxPoint(dom.getDocumentElement());

        assertEquals("latitude",            12.0,                                   p.getLat(), 0.0);
        assertEquals("longitude",           34.0,                                   p.getLon(), 0.0);
        assertEquals("elevation",           Double.valueOf(123.0),                  p.getElevation());
        assertEquals("timestamp",           Instant.ofEpochMilli(1577547828000L),   p.getTimestamp());
        assertEquals("magnetic variance",   Double.valueOf(15.2),                   p.getMagneticVariance());
        assertEquals("geoid height",        Double.valueOf(101.0),                  p.getGeoidHeight());
        assertEquals("name",                "testing",                              p.getName());
        assertEquals("comment",             "test comment",                         p.getComment());
        assertEquals("description",         "a description",                         p.getDescription());
    }
}
