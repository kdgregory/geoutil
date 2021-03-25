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

import java.io.InputStream;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.kdgcommons.collections.CollectionUtil;
import net.sf.kdgcommons.tuple.Tuple2;
import net.sf.practicalxml.ParseUtil;

import com.kdgregory.geoutil.lib.core.Point;
import com.kdgregory.geoutil.lib.gpx.GpxFile;
import com.kdgregory.geoutil.lib.kml.KmlFile;
import com.kdgregory.geoutil.lib.kml.fieldtypes.Coordinates;
import com.kdgregory.geoutil.lib.kml.model.Document;
import com.kdgregory.geoutil.lib.kml.model.Feature;
import com.kdgregory.geoutil.lib.kml.model.LineString;
import com.kdgregory.geoutil.lib.kml.model.Placemark;
import com.kdgregory.geoutil.lib.kml.model.Style;


public class TestGPXToKML
{
    private GpxFile gpxFile;

    @Before
    public void setup() throws Exception
    {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("testdata.gpx"))
        {
            gpxFile = new GpxFile(ParseUtil.parse(in));
        }
    }


    @Test
    public void testExtractPoints() throws Exception
    {
        List<Point> points = GPXToKML.extractPoints(gpxFile);

        assertEquals("number of points", 24, points.size());
        assertEquals("first point", new Point(40.135216, -75.205757, Double.valueOf(80.41), Instant.parse("2021-03-10T13:09:03Z")), CollectionUtil.first(points));
        assertEquals("last point",  new Point(40.124013, -75.217921, Double.valueOf(39.07), Instant.parse("2021-03-10T18:36:43Z")), CollectionUtil.last(points));
    }


    @Test
    public void testSimplifyAndSplit() throws Exception
    {
        List<Point> points = GPXToKML.extractPoints(gpxFile);
        List<List<Point>> segments = GPXToKML.simplifyAndSplit(points);

        assertEquals("number of segments", 4, segments.size());

        List<Point> segment1 = segments.get(0);
        assertEquals("segment 1, size", 6, segment1.size());
        assertEquals("segment 1, first point", new Point(40.135216, -75.205757, Double.valueOf(80.41), Instant.parse("2021-03-10T13:09:03Z")), CollectionUtil.first(segment1));
        assertEquals("segment 1, last point",  new Point(40.137575, -75.2119,   Double.valueOf(90.02), Instant.parse("2021-03-10T13:11:10Z")), CollectionUtil.last(segment1));

        List<Point> segment2 = segments.get(1);
        assertEquals("segment 2, size", 2, segment2.size());
        assertEquals("segment 2, first point", new Point(40.152807, -75.900908, Double.valueOf(179.9), Instant.parse("2021-03-10T15:00:04Z")), CollectionUtil.first(segment2));
        assertEquals("segment 2, last point",  new Point(40.152902, -75.901418, Double.valueOf(179.9), Instant.parse("2021-03-10T15:00:21Z")), CollectionUtil.last(segment2));

        List<Point> segment3 = segments.get(2);
        assertEquals("segment 3, size", 3, segment3.size());
        assertEquals("segment 3, first point", new Point(39.796229, -76.989512, Double.valueOf(177.5), Instant.parse("2021-03-10T17:50:37Z")), CollectionUtil.first(segment3));
        assertEquals("segment 3, last point",  new Point(39.795646, -76.990316, Double.valueOf(176.06), Instant.parse("2021-03-10T17:50:55Z")), CollectionUtil.last(segment3));

        List<Point> segment4 = segments.get(3);
        assertEquals("segment 4, size", 5, segment4.size());
        assertEquals("segment 4, first point", new Point(39.795198, -76.990748, Double.valueOf(178.94), Instant.parse("2021-03-10T18:34:33Z")), CollectionUtil.first(segment4));
        assertEquals("segment 4, last point",  new Point(40.124013, -75.217921, Double.valueOf(39.07), Instant.parse("2021-03-10T18:36:43Z")), CollectionUtil.last(segment4));
    }


    @Test
    public void testAssignSegmentsToStyle() throws Exception
    {
        List<Point> points = GPXToKML.extractPoints(gpxFile);
        List<List<Point>> segments = GPXToKML.simplifyAndSplit(points);
        List<Tuple2<String,List<Point>>> namedSegments = GPXToKML.assignSegmentsToStyle(segments);

        assertEquals("number of segments", 4, namedSegments.size());

        Tuple2<String,List<Point>> segment1 = namedSegments.get(0);
        assertEquals("segment 1, size", 6, segment1.get1().size());
        assertEquals("segment 1, style", "outbound", segment1.get0());
        assertEquals("segment 1, first point", new Point(40.135216, -75.205757, Double.valueOf(80.41), Instant.parse("2021-03-10T13:09:03Z")), CollectionUtil.first(segment1.get1()));
        assertEquals("segment 1, last point",  new Point(40.137575, -75.2119,   Double.valueOf(90.02), Instant.parse("2021-03-10T13:11:10Z")), CollectionUtil.last(segment1.get1()));

        Tuple2<String,List<Point>> segment2 = namedSegments.get(1);
        assertEquals("segment 2, size", 2, segment2.get1().size());
        assertEquals("segment 2, style", "outbound", segment2.get0());
        assertEquals("segment 2, first point", new Point(40.152807, -75.900908, Double.valueOf(179.9), Instant.parse("2021-03-10T15:00:04Z")), CollectionUtil.first(segment2.get1()));
        assertEquals("segment 2, last point",  new Point(40.152902, -75.901418, Double.valueOf(179.9), Instant.parse("2021-03-10T15:00:21Z")), CollectionUtil.last(segment2.get1()));

        Tuple2<String,List<Point>> segment3 = namedSegments.get(2);
        assertEquals("segment 3, size", 3, segment3.get1().size());
        assertEquals("segment 3, style", "outbound", segment3.get0());
        assertEquals("segment 3, first point", new Point(39.796229, -76.989512, Double.valueOf(177.5), Instant.parse("2021-03-10T17:50:37Z")), CollectionUtil.first(segment3.get1()));
        assertEquals("segment 3, last point",  new Point(39.795646, -76.990316, Double.valueOf(176.06), Instant.parse("2021-03-10T17:50:55Z")), CollectionUtil.last(segment3.get1()));

        Tuple2<String,List<Point>> segment4 = namedSegments.get(3);
        assertEquals("segment 4, style", "return", segment4.get0());
        assertEquals("segment 4, size", 5, segment4.get1().size());
        assertEquals("segment 4, first point", new Point(39.795198, -76.990748, Double.valueOf(178.94), Instant.parse("2021-03-10T18:34:33Z")), CollectionUtil.first(segment4.get1()));
        assertEquals("segment 4, last point",  new Point(40.124013, -75.217921, Double.valueOf(39.07), Instant.parse("2021-03-10T18:36:43Z")), CollectionUtil.last(segment4.get1()));
    }



    @Test
    public void testEndToEnd() throws Exception
    {
        KmlFile kml = GPXToKML.process(gpxFile);

        List<Feature<?>> topLevelFeatures = kml.getFeatures();
        assertEquals("one top-level feature", 1, topLevelFeatures.size());

        // this will throw if it isn't constructed as expected
        Document doc = (Document)topLevelFeatures.get(0);

        List<Style> sharedStyles = doc.getSharedStyles();
        assertEquals("number of shared styles", 2, sharedStyles.size());

        Set<String> styleNames = new HashSet<>();
        for (Style style : sharedStyles)
        {
            styleNames.add(style.getId());
        }
        assertEquals("style names", CollectionUtil.asSet("outbound", "return"), styleNames);

        // this will also throw if the file structure gets changed (eg, adding Folders into the mix)
        List<Placemark> placemarks = CollectionUtil.cast(doc.getFeatures(), Placemark.class);
        assertEquals("number of route lines", 12, placemarks.size());

        // spot-check coordinates of first and last line segments --
        // note: since we haven't serialized/deserialized the file, the placemarks actually contain
        //       the original points read from the GPX file (including timestamp, which isn't in the
        //       serialized representation)

        LineString pm0 = (LineString)placemarks.get(0).getGeometry();
        List<Coordinates> c0 = pm0.getCoordinates();
        assertEquals("segment 0 number of points", 2, c0.size());
        assertEquals("segment 0 first point",  new Point(40.135216, -75.205757, Double.valueOf(80.41), Instant.parse("2021-03-10T13:09:03Z")), c0.get(0));
        assertEquals("segment 0 second point", new Point(40.135473, -75.205865, Double.valueOf(79.45), Instant.parse("2021-03-10T13:09:09Z")), c0.get(1));

        LineString pm11 = (LineString)placemarks.get(11).getGeometry();
        List<Coordinates> c11 = pm11.getCoordinates();
        assertEquals("segment 0 number of points", 2, c11.size());
        assertEquals("segment 0 first point",  new Point(40.123691, -75.219215, Double.valueOf(39.07), Instant.parse("2021-03-10T18:36:33Z")), c11.get(0));
        assertEquals("segment 0 second point", new Point(40.124013, -75.217921, Double.valueOf(39.07), Instant.parse("2021-03-10T18:36:43Z")), c11.get(1));

        // ensure that styles have been correctly attached

        for (int ii = 0 ; ii < placemarks.size() ; ii++)
        {
            if (ii <= 7)
                assertEquals("point " + ii + " style", "outbound", placemarks.get(ii).getStyleRef());
            else
                assertEquals("point " + ii + " style", "return", placemarks.get(ii).getStyleRef());
        }
    }
}
