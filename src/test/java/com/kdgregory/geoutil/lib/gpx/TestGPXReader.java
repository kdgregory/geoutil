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

package com.kdgregory.geoutil.lib.gpx;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.kdgcommons.collections.CollectionUtil;
import net.sf.practicalxml.DomUtil;

import com.kdgregory.geoutil.lib.shared.TimestampedPoint;


public class TestGPXReader
{
    private GPXReader testdata;

    @Before
    public void setup()
    throws Exception
    {
        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("testdata.gpx"))
        {
            testdata = new GPXReader(in);
        }
    }

//----------------------------------------------------------------------------
//  Testcases
//----------------------------------------------------------------------------

    @Test
    public void testGetTracks() throws Exception
    {
        List<Element> tracks = testdata.getTracks();
        assertEquals("number of tracks", 3, tracks.size());

        assertEquals("first track name",  "Track 1", DomUtil.getChild(tracks.get(0), "name").getTextContent());
        assertEquals("second track name", "Track 2", DomUtil.getChild(tracks.get(1), "name").getTextContent());
        assertNull("third track has no name",        DomUtil.getChild(tracks.get(2), "name"));
    }


    @Test
    public void testGetTracksByName() throws Exception
    {
        Map<String,Element> tracks = testdata.getTracksByName();
        assertEquals("number of tracks", 2, tracks.size());
        assertEquals("track names",     CollectionUtil.asSet("Track 1", "Track 2"), tracks.keySet());
    }


    @Test
    public void testGetPoints() throws Exception
    {
        List<TimestampedPoint> points = testdata.getPoints();

        // I'll assert first and last, and assume the rest were translated correctly

        assertEquals("number of points", 15, points.size());
        assertEquals("first point", new TimestampedPoint("2019-12-28T15:43:48Z", 40.036694, -75.928012),
                                    points.get(0));
        assertEquals("last point",  new TimestampedPoint("2018-04-13T14:45:55Z", 40.266643, -75.804925),
                                    points.get(14));
    }


    @Test
    public void testGetPointsByTrack() throws Exception
    {
        Element track = testdata.getTracksByName().get("Track 1");
        List<TimestampedPoint> points = testdata.getPoints(track);

        // I'll assert first and last, and assume the rest were translated correctly

        assertEquals("number of points", 8, points.size());
        assertEquals("first point", new TimestampedPoint("2019-12-28T15:43:48Z", 40.036694, -75.928012),
                                    points.get(0));
        assertEquals("last point",  new TimestampedPoint("2019-12-28T15:44:35Z", 40.030976, -75.934887),
                                    points.get(7));
    }
}
