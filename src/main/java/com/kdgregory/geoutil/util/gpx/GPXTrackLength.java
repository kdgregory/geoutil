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

package com.kdgregory.geoutil.util.gpx;

import java.io.File;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import net.sf.practicalxml.ParseUtil;
import net.sf.practicalxml.xpath.XPathWrapper;


/**
 *  Calculates the length of all tracks in a set of GPX files.
 *  <p>
 *  Invocation:
 *
 *      GPXTrackLength FILENAME [...]
 */
public class GPXTrackLength
{
    public static void main(String[] argv)
    throws Exception
    {
        for (String filename : argv)
        {
            describeTracks(new File(filename));
        }
    }


    private static void describeTracks(File file)
    {
        Document dom = ParseUtil.parse(file);

        List<Node> tracks = xpath("//ns:trk").evaluate(dom);
        for (Node track : tracks)
        {
            String date = xpath("ns:trkseg/ns:trkpt/ns:time").evaluateAsString(track).replaceAll("T.*", "");
            String trackName = xpath("ns:name").evaluateAsString(track);
            double trackLength = calculateTrackLength(track);
            System.out.println(String.format("%s: %-60s %8.2f", date, trackName, trackLength));
        }
    }


    private static double calculateTrackLength(Node track)
    {
        double trackLength = 0.0;
        double lastLat = -1;
        double lastLon = -1;

        List<Node> trackpoints = xpath("ns:trkseg/ns:trkpt").evaluate(track);
        for (Node trackpoint : trackpoints)
        {
            double lat = Double.parseDouble(((Element)trackpoint).getAttribute("lat"));
            double lon = Double.parseDouble(((Element)trackpoint).getAttribute("lon"));
            if (lastLat < 0)
            {
                lastLat = lat;
                lastLon = lon;
            }
            else
            {
                double milesPerDegreeLat = 69;
                double milesPerDegreeLon = milesPerDegreeLat * Math.cos(Math.toRadians(lat));
                double deltaLonLenth = milesPerDegreeLon * (lon - lastLon);
                double deltaLatLenth = milesPerDegreeLat * (lat - lastLat);
                double segmentLength = Math.sqrt(deltaLonLenth * deltaLonLenth + deltaLatLenth * deltaLatLenth);
                trackLength += segmentLength;
                lastLon = lon;
                lastLat = lat;
            }
        }

        return trackLength;
    }


    private static XPathWrapper xpath(String path)
    {
         return new XPathWrapper(path)
                .bindNamespace("ns", "http://www.topografix.com/GPX/1/1");
    }
}
