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

import java.util.Arrays;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.practicalxml.DomUtil;
import net.sf.practicalxml.builder.XmlBuilder;


public class TestTrackSegment
{
    @Test
    public void testPointList() throws Exception
    {
        TrackSegment seg = new TrackSegment();

        assertTrue("after construction, list exists and is empty", seg.getPoints().isEmpty());

        Point p1 = new Point(12,34);
        Point p2 = new Point(23,45);
        Point p3 = new Point(34,56);

        seg.add(p1);
        assertEquals("after adding single point", Arrays.asList(p1), seg.getPoints());

        seg.addAll(Arrays.asList(p2, p3));
        assertEquals("after adding list", Arrays.asList(p1, p2, p3), seg.getPoints());

        try
        {
            seg.getPoints().add(new Point(0,0));
            fail("returned list of points should be unmodifiable");
        }
        catch (UnsupportedOperationException ex)
        {
            // success
        }

        seg.clear();
        assertTrue("after clear, list exists and is empty", seg.getPoints().isEmpty());
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

        Point p1 = new Point(12,34);
        Point p2 = new Point(23, 45);
        seg.addAll(Arrays.asList(p1, p2));

        seg.appendAsXml(root);
        assertEquals("appendeded single child to parent", 1, DomUtil.getChildren(root).size());

        Element eSeg = DomUtil.getChildren(root).get(0);
        assertEquals("track segment namespace",     "http://www.topografix.com/GPX/1/1",    eSeg.getNamespaceURI());
        assertEquals("track segment name",          "trkseg",                               eSeg.getNodeName());
        assertEquals("track segment child count",   2,                                      DomUtil.getChildren(eSeg).size());
        assertEquals("first child",                 p1,                                     new Point(DomUtil.getChildren(eSeg).get(0)));
        assertEquals("second child",                p2,                                     new Point(DomUtil.getChildren(eSeg).get(1)));
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

        TrackSegment seg = new TrackSegment(dom.getDocumentElement());
        assertEquals("number of points", 2, seg.getPoints().size());
        assertEquals("point 1", new Point(12, 34), seg.getPoints().get(0));
        assertEquals("point 2", new Point(23, 45), seg.getPoints().get(1));
    }

}
