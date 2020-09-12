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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.practicalxml.DomUtil;
import net.sf.practicalxml.OutputUtil;
import net.sf.practicalxml.ParseUtil;
import net.sf.practicalxml.xpath.XPathWrapperFactory;
import net.sf.practicalxml.xpath.XPathWrapperFactory.CacheType;

import com.kdgregory.geoutil.lib.shared.Point;
import com.kdgregory.geoutil.lib.shared.SegmentUtil;
import com.kdgregory.geoutil.lib.shared.TimestampedPoint;


/**
 *  Merges two tracks from a Garmin export file. The output is a single file containing
 *  annotated points.
 *  <p>
 *  Invocation:
 *
 *      GarminTrackMerge SRC_1 SRC_2 DEST
 */
public class GarminTrackMerge
{
    private static Logger logger = LoggerFactory.getLogger(GarminTrackMerge.class);

    private static XPathWrapperFactory xpFact = new XPathWrapperFactory(CacheType.SIMPLE)
                                                .bindNamespace("ns", "http://earth.google.com/kml/2.1");

    public static void main(String[] argv)
    throws Exception
    {
        logger.info("starting");

        Document[] sources = new Document[2];
        List<TimestampedPoint>[] srcTracks = new List[2];

        for (int argidx = 0 ; argidx < 2 ; argidx++)
        {
            String filename = argv[argidx];
            sources[argidx] = ParseUtil.parse(new File(filename));
            srcTracks[argidx] = extractTrack(sources[argidx]);
            logger.info("extracted {} points from {}", srcTracks[argidx].size(), filename);
        }

        List<Point[]> aligned = SegmentUtil.align(srcTracks[0], srcTracks[1], 25, 100);
        logger.info("after alignment: {} points", aligned.size());

        Document result = buildOutput(aligned);

        String filename = argv[2];
        OutputUtil.indented(new DOMSource(result), new StreamResult(filename), 4);
        logger.info("output written to {}", filename);
    }


    private static List<TimestampedPoint> extractTrack(Document dom)
    throws Exception
    {
        List<TimestampedPoint> result = new ArrayList<>();

        List<Element> points = xpFact.newXPath("//ns:Folder/ns:name[text()='Track Points']/../ns:Placemark").evaluate(dom, Element.class);
        for (Element point : points)
        {
            String srcTimestamp = xpFact.newXPath("ns:TimeSpan/ns:begin").evaluateAsString(point);
            Instant timestamp = Instant.from(DateTimeFormatter.ISO_DATE_TIME.parse(srcTimestamp));

            String srcCoordinates = xpFact.newXPath("ns:Point/ns:coordinates").evaluateAsString(point);
            String[] parsedCoordinates = srcCoordinates.split(",");
            double lon = Double.valueOf(parsedCoordinates[0].trim());
            double lat = Double.valueOf(parsedCoordinates[1].trim());

            result.add(new TimestampedPoint(timestamp.toEpochMilli(), lat, lon));
        }

        return result;
    }


    private static Document buildOutput(List<Point[]> pairs)
    {
        Element root = DomUtil.newDocument("http://www.opengis.net/kml/2.2", "kml");

        Element kdoc = DomUtil.appendChildInheritNamespace(root, "Document");
        appendStyleDef(kdoc, 1, "FF00FF00");
        appendStyleDef(kdoc, 2, "FFFF0000");

        for (Point[] pair : pairs)
        {
            appendPoint(kdoc, (TimestampedPoint)pair[0], 1);
            appendPoint(kdoc, (TimestampedPoint)pair[1], 2);
        }

//        OutputUtil.indented(new DOMSource(root.getOwnerDocument()), new StreamResult("/tmp/test.xml"), 4);

        return root.getOwnerDocument();
    }


    private static void appendStyleDef(Element kdoc, int pathNumber, String color)
    {
        Element style = DomUtil.appendChildInheritNamespace(kdoc, "Style");
        style.setAttribute("id", "path_" + pathNumber);

        Element iconStyle = DomUtil.appendChildInheritNamespace(style, "IconStyle");
        Element iconColor = DomUtil.appendChildInheritNamespace(iconStyle, "color");
        iconColor.setTextContent(color);
    }


    private static void appendPoint(Element path, TimestampedPoint p, int pathNumber)
    {
        Element pm = DomUtil.appendChildInheritNamespace(path, "Placemark");
        
        Element desc = DomUtil.appendChildInheritNamespace(pm, "description");
        desc.setTextContent("path " + pathNumber + ": " + formatTimestamp(p));

        Element style = DomUtil.appendChildInheritNamespace(pm, "styleUrl");
        style.setTextContent("#" +  "path_" + pathNumber);

        Element pt = DomUtil.appendChildInheritNamespace(pm, "Point");
        Element coord = DomUtil.appendChildInheritNamespace(pt, "coordinates");
        coord.setTextContent(p.getLon() + "," + p.getLat());
    }
    
    
    private static String formatTimestamp(TimestampedPoint p)
    {
        Instant timestamp = Instant.ofEpochMilli(p.getTimestamp());
        ZonedDateTime localTime = timestamp.atZone(ZoneId.systemDefault());
        return DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(localTime);
    }
}
