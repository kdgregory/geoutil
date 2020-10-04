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

import com.kdgregory.geoutil.lib.gpx.GpxFile;
import com.kdgregory.geoutil.lib.gpx.Track;
import com.kdgregory.geoutil.lib.gpx.TrackSegment;
import com.kdgregory.geoutil.lib.shared.SegmentUtil;


/**
 *  Calculates the length of all tracks in a set of GPX files, using latitude-adjusted
 *  Pythagorean distance between successive points.
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
        GpxFile gpx = new GpxFile(file);
        for (Track track : gpx.getTracks())
        {
            double trackLength = calculateTrackLength(track);
            System.out.println(String.format("%s: %-60s %8.2f", file, track.getName(), trackLength));
        }
    }


    private static double calculateTrackLength(Track track)
    {
        track.combineSegments();
        if (track.getSegments().size() == 0)
            return 0;

        TrackSegment seg = track.getSegments().get(0);
        seg.sortPoints();

        return SegmentUtil.distance(seg.getPoints()) / 1609.34;
    }
}
