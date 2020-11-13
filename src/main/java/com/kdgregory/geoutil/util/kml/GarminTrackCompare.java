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
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kdgregory.geoutil.lib.kml.*;
import com.kdgregory.geoutil.lib.kml.fieldtypes.Coordinates;
import com.kdgregory.geoutil.lib.shared.*;


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
    private final static String STYLE_FASTER = "faster";
    private final static String STYLE_SLOWER = "slower";
    
    private static Logger logger = LoggerFactory.getLogger(GarminTrackCompare.class);


    public static void main(String[] argv)
    throws Exception
    {
        logger.info("starting");

        KmlFile[] sources = new KmlFile[2];
        List<Point>[] srcTracks = new List[2];

        for (int argidx = 0 ; argidx < 2 ; argidx++)
        {
            File file = new File(argv[argidx]);
            sources[argidx] = KmlFile.parse(file);
            srcTracks[argidx] = extractTrack(sources[argidx]);
            logger.info("extracted {} points from {}", srcTracks[argidx].size(), file);
        }

        List<Point[]> aligned = SegmentUtil.align(srcTracks[0], srcTracks[1], 50, 100);
        logger.info("after alignment: {} points", aligned.size());

        KmlFile result = buildOutput(aligned);
        
        result.write(new File(argv[2]));
        logger.info("output written to {}", argv[2]);
    }


    private static List<Point> extractTrack(KmlFile kmlfile)
    throws Exception
    {
        List<Point> result = new ArrayList<>();
        
        for (Folder folder : kmlfile.find(Folder.class, "Track Points"))
        {
            for (Placemark pm : folder.find(Placemark.class, null))
            {
                // these files represent the path as points, but with a timespan rather than a timestamp
                if ((pm.getGeometry() instanceof KmlPoint) && (pm.getTimespan() != null))
                {
                    Coordinates coord = ((KmlPoint)pm.getGeometry()).getCoordinates();
                    Point p = new Point(coord.getLat(), coord.getLon(), coord.getElevation(), pm.getTimespan().getBegin());
                    result.add(p);
                }
            }
        }

        return result;
    }


    private static KmlFile buildOutput(List<Point[]> pairs)
    {
        Document doc = new Document()
                       .addSharedStyle(
                           new Style().setId(STYLE_FASTER)
                               .setLineStyle(
                                   new LineStyle().setColor("FF00FF00").setWidth(6.0)))
                       .addSharedStyle(
                           new Style().setId(STYLE_SLOWER)
                               .setLineStyle(
                                   new LineStyle().setColor("FF0000FF").setWidth(6.0)));

        double distance = 0 ;
        Point p1Prev = null;
        Point p2Prev = null;
        for (Point[] pair : pairs)
        {
            Point p1 = pair[0];
            Point p2 = pair[1];

            if (p1Prev != null)
            {
                distance += appendLineSegment(doc, p1Prev, p1, p2Prev, p2);
            }

            p1Prev = p1;
            p2Prev = p2;
        }

        doc.setDescription(String.format("%tF vs %tF: %.1f miles",
                           p1Prev.getTimestampMillis(), p2Prev.getTimestampMillis(),
                           (distance / 1609)));

        return new KmlFile().addFeature(doc);
    }


    private static double appendLineSegment(Document doc, Point p1Prev, Point p1, Point p2Prev, Point p2)
    {
        Point start = PointUtil.midpoint(p1Prev, p2Prev);
        Point finish = PointUtil.midpoint(p1, p2);
        double v1 = PointUtil.velocityMPH(p1Prev, p1);
        double v2 = PointUtil.velocityMPH(p2Prev, p2);
        String styleName = (v2 > v1) ? STYLE_FASTER : STYLE_SLOWER;
        
        doc.addFeature(new Placemark()
                       .setDescription(String.format("%.1f vs %.1f mph", v1, v2))
                       .setStyleRef(styleName)
                       .setGeometry(new LineString(start, finish)));
        
        return PointUtil.pythagorean(start, finish);
    }
}
