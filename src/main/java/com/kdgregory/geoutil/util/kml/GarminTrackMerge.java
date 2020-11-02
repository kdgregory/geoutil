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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kdgregory.geoutil.lib.kml.*;
import com.kdgregory.geoutil.lib.shared.Point;
import com.kdgregory.geoutil.lib.shared.SegmentUtil;


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

        List<Point[]> aligned = SegmentUtil.align(srcTracks[0], srcTracks[1], 25, 100);
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
                           new Style().setId("path_1_styles")
                               .setIconStyle(
                                   new IconStyle().setColor("FF00FF00")))
                       .addSharedStyle(
                           new Style().setId("path_2_styles")
                               .setIconStyle(
                                   new IconStyle().setColor("FFFF0000")));

        for (Point[] pair : pairs)
        {
            for (int path = 0 ; path <= 1 ; path++)
            {
                Point p = pair[path];
                doc.addFeature(
                    new Placemark()
                        .setDescription("path " + path + ": " + formatTimestamp(p))
                        .setStyleRef("path_" + (path + 1) + "_styles")
                        .setGeometry(new KmlPoint(p.getLat(), p.getLon())));
            }
        }

        return new KmlFile().addFeature(doc);
    }


    private static String formatTimestamp(Point p)
    {
        Instant timestamp = Instant.ofEpochMilli(p.getTimestampMillis());
        ZonedDateTime localTime = timestamp.atZone(ZoneId.systemDefault());
        return DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(localTime);
    }
}
