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
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.kdgcommons.collections.CollectionUtil;

import com.kdgregory.geoutil.lib.gpx.GpxFile;
import com.kdgregory.geoutil.lib.gpx.model.*;


/**
 *  Removes all points from a GPX file that aren't within a specified date range
 *  (expressed as a local date in the format YYYY-MM-DD), combines all track
 *  segments into a single track, and sets the name of that track.
 *  <pre>
 *  GPXCleanup FILENAME TRACK_NAME DESIRED_START_DATE [DESIRED_END_DATE]
 *  </pre>
 *  If <code>DESIRED_END_DATE</code> is omitted, then it is assumed to be the same
 *  as <code>DESIRED_START_DATE</code>.
 */
public class GPXCleanup
{
    private static Logger logger = LoggerFactory.getLogger(GPXCleanup.class);


    public static void main(String[] argv)
    throws Exception
    {
        File file = new File(argv[0]);
        String trackName = argv[1];
        Instant startTimestamp = parseProvidedDate(argv[2]);
        Instant finishTimestamp = (argv.length == 3)
                                ? ChronoUnit.DAYS.addTo(startTimestamp, 1).minusMillis(1)
                                : ChronoUnit.DAYS.addTo(parseProvidedDate(argv[3]), 1).minusMillis(1);

        logger.info("processing file: {}", file);
        GpxFile gpx = new GpxFile(file);

        Track track = combineSegments(gpx, trackName);
        TrackSegment segment = track.getSegments().get(0);
        filterAndTrim(segment, startTimestamp, finishTimestamp);

        if (segment.size() == 0)
        {
            logger.warn("all points removed; not overwriting file");
            System.exit(2);
        }

        int distanceInMiles = (int)(segment.distance() * 0.0006213712 + 0.5);
        logger.debug("track length: {} miles", distanceInMiles);

        track.splitSegments(Duration.ofMinutes(30));
        logger.debug("split track has {} segments", track.getSegments().size());

        gpx.setTracks(Arrays.asList(track));
        gpx.write(file);
        logger.info("file overwritten");
    }


    private static Track combineSegments(GpxFile gpx, String name)
    {
        Track track = new Track().setName(name);
        for (Track oldTrack : gpx.getTracks())
        {
            for (TrackSegment seg : oldTrack.getSegments())
            {
                logger.debug("loaded segment with {} points from track {}", seg.getPoints().size(), oldTrack.getName());
                track.addSegment(seg);
            }
        }

        TrackSegment segment = track.combineSegments();

        logger.info("combined track has {} points, from {} to {}",
                    segment.size(),
                    CollectionUtil.first(segment.getPoints()).getTimestamp(),
                    CollectionUtil.last(segment.getPoints()).getTimestamp());

        return track;
    }


    private static void filterAndTrim(TrackSegment segment, Instant startTimestamp, Instant finishTimestamp)
    {
        segment.sortPoints();

        segment.filter(startTimestamp, finishTimestamp);
        logger.info("filtered track has {} points, from {} to {}",
                    segment.size(),
                    CollectionUtil.first(segment.getPoints()).getTimestamp(),
                    CollectionUtil.last(segment.getPoints()).getTimestamp());

        segment.trim(25);
        logger.info("trimmed track has {} points, from {} to {}",
                    segment.size(),
                    CollectionUtil.first(segment.getPoints()).getTimestamp(),
                    CollectionUtil.last(segment.getPoints()).getTimestamp());
    }


    private static Instant parseProvidedDate(String date)
    {
        try
        {
            return Instant.from(
                    LocalDate.parse(date)
                        .atStartOfDay(ZoneId.systemDefault()));
        }
        catch (DateTimeParseException ex)
        {
            System.out.println("unable to parse date: " + date);
            System.exit(1);
            return null; // because compiler doesn't know what exit() does
        }
    }
}
