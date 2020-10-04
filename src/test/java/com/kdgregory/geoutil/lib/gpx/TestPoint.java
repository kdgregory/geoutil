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

package com.kdgregory.geoutil.lib.gpx;

import java.time.Instant;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.practicalxml.DomUtil;
import net.sf.practicalxml.builder.XmlBuilder;

import com.kdgregory.geoutil.lib.shared.TimestampedPoint;


public class TestPoint
{
    @Test
    public void testConstructor() throws Exception
    {
        Point p = new Point(12.34, 45.67);

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
        Point p = new Point(12.34, 45.67)
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

    }


    @Test
    public void testAlternativeTimestampFormats() throws Exception
    {
        Point p1 = new Point(0,0).setTimestampMillis(1577547828000L);

        assertEquals("timestamp",               Instant.ofEpochMilli(1577547828000L),   p1.getTimestamp());
        assertEquals("timestamp as string",     "2019-12-28T15:43:48Z",                 p1.getTimestampString());
        assertEquals("timestamp as millies",    1577547828000L,                         p1.getTimestampMillis());

        Point p2 = new Point(0,0).setTimestampString("2019-12-28T15:43:48Z");

        assertEquals("timestamp",               Instant.ofEpochMilli(1577547828000L),   p2.getTimestamp());
        assertEquals("timestamp as string",     "2019-12-28T15:43:48Z",                 p2.getTimestampString());
        assertEquals("timestamp as millies",    1577547828000L,                         p2.getTimestampMillis());

        Point p3 = new Point(0,0).setTimestampString("2019-12-28T10:43:48-05:00");

        assertEquals("timestamp",               Instant.ofEpochMilli(1577547828000L),   p3.getTimestamp());
        assertEquals("timestamp as string",     "2019-12-28T15:43:48Z",                 p3.getTimestampString());
        assertEquals("timestamp as millies",    1577547828000L,                         p3.getTimestampMillis());
    }


    @Test
    public void testEqualsAndHashcode() throws Exception
    {
        Point p1a = new Point(12,34);
        Point p1b = new Point(12,34);
        Point p2a = new Point(12,34).setTimestampMillis(1577547828000L);
        Point p2b = new Point(12,34).setTimestampMillis(1577547828000L);
        Point p2c = new Point(12,34).setTimestampMillis(1577547829000L);
        Point p3a = new Point(12,34).setTimestampMillis(1577547828000L)
                                    .setElevation(123.45)
                                    .setMagneticVariance(15.5)
                                    .setGeoidHeight(123.0);
        Point p3b = new Point(12,34).setTimestampMillis(1577547828000L)
                                    .setElevation(123.45)
                                    .setMagneticVariance(15.5)
                                    .setGeoidHeight(123.0);

        Point p4  = new Point(34,56);

        assertTrue("equal, identity",                       p1a.equals(p1a));
        assertTrue("equal, lat/lon",                        p1a.equals(p1b));
        assertTrue("equal, one field",                      p2a.equals(p2b));
        assertTrue("equal, all fields",                     p3a.equals(p3b));

        assertFalse("not equal, lat/lon",                   p1a.equals(p4));
        assertFalse("not equal, missing fields 1",          p1a.equals(p2a));
        assertFalse("not equal, missing fields 2",          p2a.equals(p1a));
        assertFalse("not equal, missing fields 3",          p2a.equals(p3a));
        assertFalse("not equal, missing fields 4",          p3a.equals(p2a));
        assertFalse("not equal, different field values",    p2a.equals(p2c));

        assertTrue("hashcode, same lat/lon",                p1a.hashCode() == p1b.hashCode());
        assertTrue("hashcode, ignores fields",              p1a.hashCode() == p3b.hashCode());
        assertTrue("hashcode, different lat/lon",           p1a.hashCode() != p4.hashCode());
    }


    @Test
    public void testCompare() throws Exception
    {
        Point p1 = new Point(12,34);
        Point p2 = new Point(12,34).setTimestampMillis(1577547827000L);
        Point p3 = new Point(12,34).setTimestampMillis(1577547828000L);
        Point p5 = new Point(56,78);
        Point p6 = new Point(56,78).setTimestampMillis(1577547828000L);

        com.kdgregory.geoutil.lib.shared.Point x1 = new com.kdgregory.geoutil.lib.shared.Point(12,34);
        com.kdgregory.geoutil.lib.shared.Point x2 = new com.kdgregory.geoutil.lib.shared.Point(56,78);

        assertEquals("lat/lon, ==",                             0,  p1.compareTo(p1));
        assertEquals("lat/lon, <",                             -1,  p1.compareTo(p5));
        assertEquals("lat/lon, >",                              1,  p5.compareTo(p1));
        assertEquals("timestamp, ==",                           0,  p3.compareTo(p3));
        assertEquals("timestamp, <",                           -1,  p2.compareTo(p3));
        assertEquals("timestamp, >",                            1,  p3.compareTo(p2));
        assertEquals("default timestamp, <",                   -1,  p1.compareTo(p2));
        assertEquals("default timestamp, >",                    1,  p2.compareTo(p1));
        assertEquals("same timestamp, different lat/lon, <",   -1,  p3.compareTo(p6));
        assertEquals("same timestamp, different lat/lon, >",    1,  p6.compareTo(p3));
        assertEquals("super, ==",                               0,  p1.compareTo(x1));
        assertEquals("super, <",                               -1,  p1.compareTo(x2));
        assertEquals("super, >",                                1,  x2.compareTo(p1));
    }


    @Test
    public void testConvertToTimestampedPoint() throws Exception
    {
        Point p1 = new Point(12,34).setTimestampMillis(1577547828000L);
        TimestampedPoint tp1 = p1.toTimestampedPoint();

        assertEquals("timestamp",   1577547828000L,     tp1.getTimestamp());
        assertEquals("lat",         12,                 tp1.getLat(), 0.0);
        assertEquals("lon",         34,                 tp1.getLon(), 0.0);

        Point p2 = new Point(12,34);
        TimestampedPoint tp2 = p2.toTimestampedPoint();

        assertEquals("timestamp",   0,                  tp2.getTimestamp());
        assertEquals("lat",         12,                 tp2.getLat(), 0.0);
        assertEquals("lon",         34,                 tp2.getLon(), 0.0);
    }


    @Test
    public void testConvertToXmlBasic() throws Exception
    {
        // this point contains no child elements
        Point p = new Point(12,34);

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
        Point p = new Point(34,56)
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

        Point p = new Point(dom.getDocumentElement());

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

        Point p = new Point(dom.getDocumentElement());

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
