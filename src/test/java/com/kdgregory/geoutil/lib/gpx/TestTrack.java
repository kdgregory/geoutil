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
import java.util.Collections;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.practicalxml.DomUtil;
import net.sf.practicalxml.builder.XmlBuilder;

import com.kdgregory.geoutil.lib.shared.Point;


public class TestTrack
{
    @Test
    public void testGettersAndSetters() throws Exception
    {
        Track track = new Track();

        track.setName("example");
        assertEquals("name", "example", track.getName());

        track.setDescription("this is something");
        assertEquals("description", "this is something", track.getDescription());

        assertEquals("after construction", Collections.emptyList(), track.getSegments());

        TrackSegment segment = new TrackSegment();
        track.addSegment(segment);
        assertEquals("after adding single segment", Arrays.asList(segment), track.getSegments());

        track.setSegments(null);
        assertEquals("after setting null segment list", Collections.emptyList(), track.getSegments());

        track.setSegments(Arrays.asList(segment));
        assertEquals("after adding list of segments", Arrays.asList(segment), track.getSegments());
    }


    @Test
    public void testFilter() throws Exception
    {
        GpxPoint p1 = new GpxPoint(12,35).setTimestampMillis(1577547825000L);
        GpxPoint p2 = new GpxPoint(12,36).setTimestampMillis(1577547826000L);
        GpxPoint p3 = new GpxPoint(12,37).setTimestampMillis(1577547827000L);
        GpxPoint p4 = new GpxPoint(12,38).setTimestampMillis(1577547828000L);
        GpxPoint p5 = new GpxPoint(12,39).setTimestampMillis(1577547829000L);

        TrackSegment s1 = new TrackSegment().addAll(Arrays.asList(p1, p2));
        TrackSegment s2 = new TrackSegment().addAll(Arrays.asList(p3, p4));
        TrackSegment s3 = new TrackSegment().addAll(Arrays.asList(p5));

        Track track = new Track().setSegments(Arrays.asList(s1, s2, s3));
        assertEquals("segments before filter", Arrays.asList(s1, s2, s3), track.getSegments());

        // using a date filter provides full coverage
        track.filter(Instant.ofEpochMilli(1577547826000L), Instant.ofEpochMilli(1577547827000L));

        assertEquals("segments after filter",         Arrays.asList(s1, s2), track.getSegments());
        assertEquals("segment 1 points after filter", Arrays.asList(p2),     track.getSegments().get(0).getPoints());
        assertEquals("segment 2 points after filter", Arrays.asList(p3),     track.getSegments().get(1).getPoints());
    }


    @Test
    public void testCombine() throws Exception
    {
        GpxPoint p1 = new GpxPoint(12,35).setTimestampMillis(1577547825000L);
        GpxPoint p2 = new GpxPoint(12,36).setTimestampMillis(1577547826000L);
        GpxPoint p3 = new GpxPoint(12,37).setTimestampMillis(1577547827000L);
        GpxPoint p4 = new GpxPoint(12,38).setTimestampMillis(1577547828000L);
        GpxPoint p5 = new GpxPoint(12,39).setTimestampMillis(1577547829000L);

        TrackSegment s1 = new TrackSegment().addAll(Arrays.asList(p1, p2));
        TrackSegment s2 = new TrackSegment().addAll(Arrays.asList(p3, p4));
        TrackSegment s3 = new TrackSegment().addAll(Arrays.asList(p5));

        Track track = new Track().setSegments(Arrays.asList(s1, s2, s3));
        assertEquals("segments before combine", Arrays.asList(s1, s2, s3), track.getSegments());

        track.combineSegments();
        assertEquals("number of segments after combine", 1, track.getSegments().size());
        assertEquals("all points accounted-for",
                     Arrays.asList(p1, p2, p3, p4, p5),
                     track.getSegments().get(0).getPoints());
    }



    @Test
    public void testConstructFromXml() throws Exception
    {
        Document dom = XmlBuilder.element("bogus",
                            XmlBuilder.element("http://www.topografix.com/GPX/1/1", "name",         XmlBuilder.text("example")),
                            XmlBuilder.element("http://www.topografix.com/GPX/1/1", "desc",         XmlBuilder.text("a description")),
                            XmlBuilder.element("http://www.topografix.com/GPX/1/1", "trkseg",
                                XmlBuilder.element("http://www.topografix.com/GPX/1/1", "trkpt",
                                    XmlBuilder.attribute("lat", "12.0"),
                                    XmlBuilder.attribute("lon", "34.0")),
                                XmlBuilder.element("http://www.topografix.com/GPX/1/1", "trkpt",
                                    XmlBuilder.attribute("lat", "23.0"),
                                    XmlBuilder.attribute("lon", "45.0"))),
                            XmlBuilder.element("http://www.topografix.com/GPX/1/1", "trkseg",
                                XmlBuilder.element("http://www.topografix.com/GPX/1/1", "trkpt",
                                    XmlBuilder.attribute("lat", "56.0"),
                                    XmlBuilder.attribute("lon", "78.0"))))
                       .toDOM();

        Track track = new Track(dom.getDocumentElement());

        // note: we verify that GpxPoints were properly parsed by looking at underlying Point equality

        assertEquals("name",                "example",          track.getName());
        assertEquals("description",         "a description",    track.getDescription());
        assertEquals("segment 1 point 1",   new Point(12,34),   track.getSegments().get(0).getPoints().get(0).getPoint());
        assertEquals("segment 1 point 2",   new Point(23,45),   track.getSegments().get(0).getPoints().get(1).getPoint());
        assertEquals("segment 2 point 1",   new Point(56,78),   track.getSegments().get(1).getPoints().get(0).getPoint());
    }



    @Test
    public void testConvertToXml() throws Exception
    {
        GpxPoint p1a = new GpxPoint(12,34);
        GpxPoint p1b = new GpxPoint(23,45);
        TrackSegment s1 = new TrackSegment().add(p1a).add(p1b);

        GpxPoint p2a = new GpxPoint(45,67);
        TrackSegment s2 = new TrackSegment().add(p2a);

        Track track = new Track()
                      .setName("example")
                      .setDescription("this is something")
                      .addSegment(s1)
                      .addSegment(s2);

        Element root = DomUtil.newDocument("irrelevant");
        track.appendAsXml(root);

        assertEquals("added single child to root", 1, DomUtil.getChildren(root).size());

        Element eTrack = DomUtil.getChildren(root).get(0);
        assertEquals("track namespace",     "http://www.topografix.com/GPX/1/1",    eTrack.getNamespaceURI());
        assertEquals("track name",          "trk",                                  eTrack.getNodeName());

        List<Element> children = DomUtil.getChildren(eTrack);
        assertEquals("nmber of children", 4, children.size());

        // note: we'll assume that if the track segments have the correct number of children, then they have
        //       the correct child content

        assertEquals("child 1 namespace",   "http://www.topografix.com/GPX/1/1",    children.get(0).getNamespaceURI());
        assertEquals("child 1 name",        "name",                                 children.get(0).getNodeName());
        assertEquals("child 1 value",       "example",                              children.get(0).getTextContent());

        assertEquals("child 2 namespace",   "http://www.topografix.com/GPX/1/1",    children.get(1).getNamespaceURI());
        assertEquals("child 2 name",        "desc",                                 children.get(1).getNodeName());
        assertEquals("child 2 value",       "this is something",                    children.get(1).getTextContent());

        assertEquals("child 3 namespace",   "http://www.topografix.com/GPX/1/1",    children.get(2).getNamespaceURI());
        assertEquals("child 3 name",        "trkseg",                               children.get(2).getNodeName());
        assertEquals("child 3 #/children",   2,                                     new TrackSegment(children.get(2)).getPoints().size());

        assertEquals("child 4 namespace",   "http://www.topografix.com/GPX/1/1",    children.get(3).getNamespaceURI());
        assertEquals("child 4 value",       "trkseg",                               children.get(3).getNodeName());
        assertEquals("child 4 #/children",  1,                                      new TrackSegment(children.get(3)).getPoints().size());
    }
}
