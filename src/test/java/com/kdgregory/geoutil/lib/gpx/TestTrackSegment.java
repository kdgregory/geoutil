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
import java.util.Arrays;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.practicalxml.DomUtil;
import net.sf.practicalxml.builder.XmlBuilder;

import com.kdgregory.geoutil.lib.shared.Point;


public class TestTrackSegment
{
    @Test
    public void testPointList() throws Exception
    {
        TrackSegment seg = new TrackSegment();

        assertTrue("after construction, list exists and is empty", seg.getPoints().isEmpty());
        assertTrue("after construction, segment reports empty",    seg.isEmpty());

        GpxPoint p1 = new GpxPoint(12,34);
        GpxPoint p2 = new GpxPoint(23,45);
        GpxPoint p3 = new GpxPoint(34,56);

        seg.add(p1);
        assertEquals("after adding single point", Arrays.asList(p1), seg.getPoints());

        seg.addAll(Arrays.asList(p2, p3));
        assertEquals("after adding list", Arrays.asList(p1, p2, p3), seg.getPoints());

        try
        {
            seg.getPoints().add(new GpxPoint(0,0));
            fail("returned list of points should be unmodifiable");
        }
        catch (UnsupportedOperationException ex)
        {
            // success
        }

        seg.clear();
        assertTrue("after clear, list exists and is empty", seg.getPoints().isEmpty());
        assertTrue("after clear, segment reports empty",    seg.isEmpty());
    }


    @Test
    public void testFilter() throws Exception
    {
        GpxPoint p1 = new GpxPoint(12,34).setTimestampMillis(1577547825000L);
        GpxPoint p2 = new GpxPoint(12,34).setTimestampMillis(1577547826000L);
        GpxPoint p3 = new GpxPoint(12,34).setTimestampMillis(1577547827000L);
        GpxPoint p4 = new GpxPoint(12,34).setTimestampMillis(1577547828000L);

        TrackSegment seg = new TrackSegment().addAll(Arrays.asList(p1, p2, p3, p4));
        assertEquals("before filter", Arrays.asList(p1, p2, p3, p4), seg.getPoints());

        // using a date filter provides full coverage
        seg.filter(Instant.ofEpochMilli(1577547826000L), Instant.ofEpochMilli(1577547827000L));

        assertEquals("after filter", Arrays.asList(p2, p3), seg.getPoints());
    }


    @Test
    public void testSort() throws Exception
    {
        GpxPoint p1 = new GpxPoint(12,34).setTimestampMillis(1577547825000L);
        GpxPoint p2 = new GpxPoint(12,34).setTimestampMillis(1577547826000L);
        GpxPoint p3 = new GpxPoint(12,34).setTimestampMillis(1577547827000L);
        GpxPoint p4 = new GpxPoint(12,34).setTimestampMillis(1577547828000L);

        TrackSegment seg = new TrackSegment().addAll(Arrays.asList(p3, p2, p4, p1));
        seg.sortPoints();

        assertEquals("points are sorted", Arrays.asList(p1, p2, p3, p4), seg.getPoints());
    }



    @Test
    public void testConvertToXml() throws Exception
    {
        Element root = DomUtil.newDocument("irrelevant");
        TrackSegment seg = new TrackSegment();

        // first test is of empty segment; it should not be added

        seg.appendAsXml(root);
        assertEquals("appending empty TrackSegment does not change parent", 0, DomUtil.getChildren(root).size());

        // second test appends some actual points

        GpxPoint p1 = new GpxPoint(12,34);
        GpxPoint p2 = new GpxPoint(23, 45);
        seg.addAll(Arrays.asList(p1, p2));

        seg.appendAsXml(root);
        assertEquals("appendeded single child to parent", 1, DomUtil.getChildren(root).size());

        // note that we validate output by parsing the points

        Element eSeg = DomUtil.getChildren(root).get(0);
        assertEquals("track segment namespace",     "http://www.topografix.com/GPX/1/1",    eSeg.getNamespaceURI());
        assertEquals("track segment name",          "trkseg",                               eSeg.getNodeName());
        assertEquals("track segment child count",   2,                                      DomUtil.getChildren(eSeg).size());
        assertEquals("first child",                 new Point(12,34),                       new GpxPoint(DomUtil.getChildren(eSeg).get(0)).getPoint());
        assertEquals("second child",                new Point(23,45),                       new GpxPoint(DomUtil.getChildren(eSeg).get(1)).getPoint());
    }


    @Test
    public void testConvertFromXml() throws Exception
    {
        Document dom = XmlBuilder.element("http://www.topografix.com/GPX/1/1", "trkseg",
                            XmlBuilder.element("http://www.topografix.com/GPX/1/1", "trkpt",
                                XmlBuilder.attribute("lat", "12.0"),
                                XmlBuilder.attribute("lon", "34.0")),
                            XmlBuilder.element("http://www.topografix.com/GPX/1/1", "trkpt",
                                XmlBuilder.attribute("lat", "23.0"),
                                XmlBuilder.attribute("lon", "45.0")))
                       .toDOM();

        // note: we assume that the points in the segment have been properly converted if
        //       there aren't any errors and the underlying Point was properly constructed

        TrackSegment seg = new TrackSegment(dom.getDocumentElement());
        assertEquals("number of points", 2, seg.getPoints().size());
        assertEquals("point 1", new Point(12, 34), seg.getPoints().get(0).getPoint());
        assertEquals("point 2", new Point(23, 45), seg.getPoints().get(1).getPoint());
    }

}
