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

import net.sf.kdgcommons.collections.CollectionUtil;
import net.sf.kdgcommons.tuple.Tuple2;

import com.kdgregory.geoutil.lib.core.Point;
import com.kdgregory.geoutil.lib.core.PointUtil;
import com.kdgregory.geoutil.lib.core.SegmentUtil;
import com.kdgregory.geoutil.lib.gpx.GpxFile;
import com.kdgregory.geoutil.lib.gpx.model.*;
import com.kdgregory.geoutil.lib.kml.KmlFile;
import com.kdgregory.geoutil.lib.kml.model.*;


/**
 *  Extracts the tracks from a GPX file and writes it as a series of line
 *  segments to a KML file with the same name but different extension.
 *
 */
public class GPXToKML
{
    private static Logger logger = LoggerFactory.getLogger(GPXToKML.class);

    public final static String  STYLENAME_OUTBOUND = "outbound";
    public final static String  STYLENAME_RETURN   = "return";



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
        KmlFile kml = process(gpx);

        File outputFile = transformFilename(file);
        logger.info("writing to {}", outputFile);
        kml.write(outputFile);
    }


    protected static KmlFile process(GpxFile gpx)
    {
        List<Point> points = extractPoints(gpx);
        List<List<Point>> segments = simplifyAndSplit(points);
        List<Tuple2<String,List<Point>>> namedSegments = assignSegmentsToStyle(segments);
        return buildOutput(namedSegments);
    }


    protected static List<Point> extractPoints(GpxFile gpx)
    {
        List<Point> points = new ArrayList<>(8192);

        for (Track oldTrack : gpx.getTracks())
        {
            for (TrackSegment seg : oldTrack.getSegments())
            {
                logger.debug("loading segment with {} points from track {}", seg.getPoints().size(), oldTrack.getName());
                for (GpxPoint point : seg.getPoints())
                {
                    points.add(point.getPoint());
                }
            }
        }

        logger.debug("extracted {} points", points.size());
        return points;
    }


    protected static List<List<Point>> simplifyAndSplit(List<Point> points)
    {
        points = SegmentUtil.simplify(points, 25);
        logger.debug("after simplification, {} points remain", points.size());

        List<List<Point>> segments = SegmentUtil.split(points, Duration.ofMinutes(30));
        logger.debug("after split, {} segments", segments.size());

        return segments;
    }


    protected static List<Tuple2<String,List<Point>>> assignSegmentsToStyle(List<List<Point>> segments)
    {
        List<Tuple2<String,List<Point>>> out = new ArrayList<>();
        List<Tuple2<String,List<Point>>> back = new ArrayList<>();

        int startIdx = 0;
        int endIdx = segments.size();
        double outDist = 0.0;
        double backDist = 0.0;

        while (startIdx < endIdx)
        {
            if (outDist <= backDist)
            {
                List<Point> segment = segments.get(startIdx++);
                out.add(new Tuple2<>(STYLENAME_OUTBOUND, segment));
                outDist += SegmentUtil.pythagoreanDistance(segment);
            }
            else
            {
                List<Point> segment = segments.get(--endIdx);
                back.add(0, new Tuple2<>(STYLENAME_RETURN, segment));
                backDist += SegmentUtil.pythagoreanDistance(segment);
            }
        }

        return CollectionUtil.combine(out, back);
    }


    protected static KmlFile buildOutput(List<Tuple2<String,List<Point>>> namedSegments)
    {
        Document doc = new Document()
                       .addSharedStyle(
                           new Style().setId(STYLENAME_OUTBOUND)
                               .setLineStyle(new LineStyle()
                                   .setColor("FF00FF00")
                                   .setWidth(6.0)))
                       .addSharedStyle(
                           new Style().setId(STYLENAME_RETURN)
                               .setLineStyle(new LineStyle()
                                   .setColor("FF0000FF")
                                   .setWidth(6.0)));

        for (Tuple2<String,List<Point>> namedSegment : namedSegments)
        {
            Point prev = null;
            String curStyle = namedSegment.get0();
            for (Point p : namedSegment.get1())
            {
                if (prev != null)
                {
                    double velocity = PointUtil.velocityMPH(prev, p);
                    double elevation = p.getElevationOrZero() * 39.37 / 12;
                    String description = String.format("%s: lat %.4f, lon %.4f, ele %.0f feet, %.1f mph", 
                                                       String.valueOf(p.getTimestamp()), 
                                                       p.getLat(), p.getLon(), elevation, velocity);
                    doc.addFeature(new Placemark()
                                   .setDescription(description)
                                   .setStyleRef(curStyle)
                                   .setGeometry(new LineString(prev, p)));
                }
                prev = p;
            }
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
