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

package com.kdgregory.geoutil.util.gpx;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kdgregory.geoutil.lib.gpx.GpxFile;
import com.kdgregory.geoutil.lib.gpx.model.*;
import com.kdgregory.geoutil.lib.kml.KmlFile;
import com.kdgregory.geoutil.lib.kml.model.*;
import com.kdgregory.geoutil.lib.shared.Point;
import com.kdgregory.geoutil.lib.shared.PointUtil;
import com.kdgregory.geoutil.lib.shared.SegmentUtil;


/**
 *  Extracts the tracks from a GPX file and writes it as a series of line
 *  segments to a KML file with the same name but different extension.
 *
 */
public class GPXToKML
{
    private static Logger logger = LoggerFactory.getLogger(GPXToKML.class);

    public static void main(String[] argv)
    throws Exception
    {
        if (argv.length != 1)
        {
            System.err.println("invocation: GpxToKml FILENAME");
            System.exit(1);
        }

        File file = new File(argv[0]);
        logger.info("processing file: {}", file);
        GpxFile gpx = new GpxFile(file);

        List<Point> points = extractPoints(gpx);
        logger.debug("extracted {} points", points.size());

        points = SegmentUtil.simplify(points, 25);
        logger.debug("after simplification, {} points remain", points.size());

        List<List<Point>> split = SegmentUtil.split(points, Duration.ofMinutes(30));
        logger.debug("after split, {} segments", split.size());

        KmlFile kml = buildOutput(split);

        File outputFile = transformFilename(file);
        logger.info("writing to {}", outputFile);
        kml.write(outputFile);
    }


    private static List<Point> extractPoints(GpxFile gpx)
    {
        List<Point> result = new ArrayList<>(8192);

        for (Track oldTrack : gpx.getTracks())
        {
            for (TrackSegment seg : oldTrack.getSegments())
            {
                logger.debug("loading segment with {} points from track {}", seg.getPoints().size(), oldTrack.getName());
                for (GpxPoint point : seg.getPoints())
                {
                    result.add(point.getPoint());
                }
            }
        }

        return result;
    }


    private static KmlFile buildOutput(List<List<Point>> segments)
    {
        Document doc = new Document()
                       .addSharedStyle(
                           new Style().setId("outbound")
                               .setLineStyle(new LineStyle()
                                   .setColor("FF00FF00")
                                   .setWidth(6.0)))
                       .addSharedStyle(
                           new Style().setId("return")
                               .setLineStyle(new LineStyle()
                                   .setColor("FF0000FF")
                                   .setWidth(6.0)));

        String curStyle = "outbound";
        for (List<Point> segment : segments)
        {
            Point prev = null;
            for (Point p : segment)
            {
                if (prev != null)
                {
                    double velocity = PointUtil.velocityMPH(prev, p);
                    doc.addFeature(new Placemark()
                                   .setDescription(String.format("%.1f mph", velocity))
                                   .setStyleRef(curStyle)
                                   .setGeometry(new LineString(prev, p)));
                }
                prev = p;
            }
            curStyle = "return";
        }

        return new KmlFile().addFeature(doc);
    }


    private static File transformFilename(File src)
    {
        String srcName = src.getAbsolutePath();
        srcName = srcName.replaceAll(".[Gg][Pp][Xx]$", "");
        return new File(srcName + ".kml");
    }
}
