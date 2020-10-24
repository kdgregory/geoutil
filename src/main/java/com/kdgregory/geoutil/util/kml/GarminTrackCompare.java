// Copyright (c) Keith D Gregory
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.kdgregory.geoutil.util.kml;

import java.io.File;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.practicalxml.DomUtil;
import net.sf.practicalxml.ParseUtil;
import net.sf.practicalxml.xpath.XPathWrapper;

import com.kdgregory.geoutil.lib.kml.KmlBuilder;
import com.kdgregory.geoutil.lib.shared.Point;
import com.kdgregory.geoutil.lib.shared.PointUtil;
import com.kdgregory.geoutil.lib.shared.SegmentUtil;


/**
 *  Extracts the tracks from two Garmin export files, aligns them, reduces them to
 *  25 meter segments, and then writes an output file consisting of line segments
 *  that are colored red or green depending on whether the first or second track is
 *  faster in that segment.
 *  <p>
 *  Invocation:
 *
 *      GarminTrackCompare SRC_1 SRC_2 DEST
 */
public class GarminTrackCompare
{
    private static Logger logger = LoggerFactory.getLogger(GarminTrackCompare.class);


    public static void main(String[] argv)
    throws Exception
    {
        logger.info("starting");

        Document[] sources = new Document[2];
        List<Point>[] srcTracks = new List[2];

        for (int argidx = 0 ; argidx < 2 ; argidx++)
        {
            String filename = argv[argidx];
            sources[argidx] = ParseUtil.parse(new File(filename));
            srcTracks[argidx] = extractTrack(sources[argidx]);
            logger.info("extracted {} points from {}", srcTracks[argidx].size(), filename);
        }

        List<Point[]> aligned = SegmentUtil.align(srcTracks[0], srcTracks[1], 50, 100);
        logger.info("after alignment: {} points", aligned.size());

        KmlBuilder result = buildOutput(aligned);

        String filename = argv[2];
        result.save(new File(filename));
        logger.info("output written to {}", filename);
    }


    private static List<Point> extractTrack(Document dom)
    throws Exception
    {
        List<Point> result = new ArrayList<>();

        XPathWrapper placemarkSelect = new XPathWrapper("//ns:Folder/ns:name[text()='Track Points']/../ns:Placemark")
                                       .bindNamespace("ns", "http://earth.google.com/kml/2.1");
        List<Element> placemarks = placemarkSelect.evaluate(dom, Element.class);
        for (Element ePlacemark : placemarks)
        {
            Element eTimespan = DomUtil.getChild(ePlacemark, "TimeSpan");
            Element eBegin = DomUtil.getChild(eTimespan, "begin");
            String srcTimestamp = eBegin.getTextContent();
            Instant timestamp = Instant.from(DateTimeFormatter.ISO_DATE_TIME.parse(srcTimestamp));

            Element ePoint = DomUtil.getChild(ePlacemark, "Point");
            Element eCoord = DomUtil.getChild(ePoint, "coordinates");
            String srcCoordinates = eCoord.getTextContent();
            String[] parsedCoordinates = srcCoordinates.split(",");
            double lon = Double.valueOf(parsedCoordinates[0].trim());
            double lat = Double.valueOf(parsedCoordinates[1].trim());

            result.add(new Point(lat, lon, timestamp));
        }

        return result;
    }


    private static KmlBuilder buildOutput(List<Point[]> pairs)
    {
        KmlBuilder builder = new KmlBuilder()
                             .add(KmlBuilder.style("faster", KmlBuilder.lineStyle(6, "FF00FF00")))
                             .add(KmlBuilder.style("slower", KmlBuilder.lineStyle(6, "FF0000FF")));

        List<Point> segment1 = new ArrayList<>(pairs.size());
        Point p1Prev = null;
        Point p2Prev = null;
        for (Point[] pair : pairs)
        {
            Point p1 = pair[0];
            Point p2 = pair[1];

            segment1.add(p1);
            if (p1Prev != null)
            {
                appendLineSegment(builder, p1Prev, p1, p2Prev, p2);
            }

            p1Prev = p1;
            p2Prev = p2;
        }

        builder.add(KmlBuilder.description(String.format("%tF vs %tF: %.1f miles",
                                                         p1Prev.getTimestamp(), p2Prev.getTimestamp(),
                                                         SegmentUtil.distance(segment1) / 1609)));

        return builder;
    }


    private static void appendLineSegment(KmlBuilder builder, Point p1Prev, Point p1, Point p2Prev, Point p2)
    {
        Point start = PointUtil.midpoint(p1Prev, p2Prev);
        Point finish = PointUtil.midpoint(p1, p2);
        double s1 = PointUtil.velocityMPH(p1Prev, p1);
        double s2 = PointUtil.velocityMPH(p2Prev, p2);
        String styleName = (s2 > s1) ? "faster" : "slower";

        builder.add(KmlBuilder.placemark(
                        KmlBuilder.description(String.format("%.1f vs %.1f mph", s1, s2)),
                        KmlBuilder.styleRef(styleName),
                        KmlBuilder.lineSegment(start, finish)));
    }
}
