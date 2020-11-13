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
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.kdgcommons.collections.CollectionUtil;

import com.kdgregory.geoutil.lib.gpx.GpxFile;
import com.kdgregory.geoutil.lib.gpx.model.*;


/**
 *  Removes all points from a GPX file that don't match a specific date (expressed
 *  as a local date in the format YYYY-MM-DD), combines all track segments into a
 *  single track, and sets the name of that track.
 *  <p>
 *  Invocation:
 *
 *      GPXCleanup FILENAME DESIRED_DATE NAME
 */
public class GPXCleanup
{
    private static Logger logger = LoggerFactory.getLogger(GPXCleanup.class);


    public static void main(String[] argv)
    throws Exception
    {
        File file = new File(argv[0]);
        Instant startTimestamp = parseProvidedDate(argv[1]);
        Instant finishTimestamp = ChronoUnit.DAYS.addTo(startTimestamp, 1).minusMillis(1);
        String trackName = argv[2];

        logger.info("processing file: {}", file);
        GpxFile gpx = new GpxFile(file);

        Track newTrack = new Track().setName(trackName);
        for (Track oldTrack : gpx.getTracks())
        {
            for (TrackSegment seg : oldTrack.getSegments())
            {
                logger.debug("loaded segment with {} points from track {}", seg.getPoints().size(), oldTrack.getName());
                newTrack.addSegment(seg);
            }
        }

        newTrack.combineSegments();
        TrackSegment newSeg = newTrack.getSegments().get(0);

        List<GpxPoint> tmpPoints = newSeg.getPoints();
        logger.info("combined track has {} points, from {} to {}",
                    tmpPoints.size(),
                    CollectionUtil.first(tmpPoints).getTimestamp(),
                    CollectionUtil.last(tmpPoints).getTimestamp());

        newSeg.sortPoints();
        newSeg.filter(startTimestamp, finishTimestamp);

        tmpPoints = newSeg.getPoints();

        if (tmpPoints.size() == 0)
        {
            logger.warn("all points removed; not overwriting file");
            System.exit(2);
        }

        logger.info("filtered track has {} points, from {} to {}",
                    tmpPoints.size(),
                    CollectionUtil.first(tmpPoints).getTimestamp(),
                    CollectionUtil.last(tmpPoints).getTimestamp());

        gpx.setTracks(Arrays.asList(newTrack));
        gpx.write(file);
        logger.info("file overwritten");
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
