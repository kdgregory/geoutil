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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.practicalxml.DomUtil;
import net.sf.practicalxml.builder.XmlBuilder;


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

        assertEquals("name",                "example",          track.getName());
        assertEquals("description",         "a description",    track.getDescription());
        assertEquals("segment 1 point 1",   new Point(12,34),   track.getSegments().get(0).getPoints().get(0));
        assertEquals("segment 1 point 2",   new Point(23,45),   track.getSegments().get(0).getPoints().get(1));
        assertEquals("segment 2 point 1",   new Point(56,78),   track.getSegments().get(1).getPoints().get(0));
    }



    @Test
    public void testConvertToXml() throws Exception
    {
        Point p1a = new Point(12,34);
        Point p1b = new Point(23,45);
        TrackSegment s1 = new TrackSegment().add(p1a).add(p1b);

        Point p2a = new Point(45,67);
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

        assertEquals("child 1 namespace",   "http://www.topografix.com/GPX/1/1",    children.get(0).getNamespaceURI());
        assertEquals("child 1 name",        "name",                                 children.get(0).getNodeName());
        assertEquals("child 1 value",       "example",                              children.get(0).getTextContent());

        assertEquals("child 2 namespace",   "http://www.topografix.com/GPX/1/1",    children.get(1).getNamespaceURI());
        assertEquals("child 2 name",        "desc",                                 children.get(1).getNodeName());
        assertEquals("child 2 value",       "this is something",                    children.get(1).getTextContent());

        assertEquals("child 3 namespace",   "http://www.topografix.com/GPX/1/1",    children.get(2).getNamespaceURI());
        assertEquals("child 3 name",        "trkseg",                               children.get(2).getNodeName());
        assertEquals("child 3 value",       Arrays.asList(p1a, p1b),                new TrackSegment(children.get(2)).getPoints());

        assertEquals("child 4 namespace",   "http://www.topografix.com/GPX/1/1",    children.get(3).getNamespaceURI());
        assertEquals("child 4 value",       "trkseg",                               children.get(3).getNodeName());
        assertEquals("child 4 value",       Arrays.asList(p2a),                     new TrackSegment(children.get(3)).getPoints());
    }
}
