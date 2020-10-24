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

package com.kdgregory.geoutil.lib.shared;

import java.time.Instant;

import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.kdgcommons.test.NumericAsserts;
import net.sf.kdgcommons.test.StringAsserts;


public class TestPoint
{
    @Test
    public void testConstructorsAndAccessors() throws Exception
    {
        Point p1 = new Point(39.95229, -75.1657517, Double.valueOf(10.5), Instant.ofEpochMilli(1577547828000L));

        assertEquals("lat",                     39.95229,                               p1.getLat(), 0.00001);
        assertEquals("lon",                     -75.1657517,                            p1.getLon(), 0.00001);
        assertEquals("elevation",               Double.valueOf(10.5),                   p1.getElevation());
        assertEquals("elevation (primitive)",   10.5,                                   p1.getElevationOrZero(), 0.0);
        assertEquals("timestamp",               Instant.ofEpochMilli(1577547828000L),   p1.getTimestamp());
        assertEquals("timestamp (string)",      "2019-12-28T15:43:48Z",                 p1.getTimestampAsString());
        assertEquals("timestamp (millis)",      1577547828000L,                         p1.getTimestampMillis());

        Point p2 = new Point(39.95229, -75.1657517, null, null);

        assertEquals("lat",                     39.95229,                               p2.getLat(), 0.00001);
        assertEquals("lon",                     -75.1657517,                            p2.getLon(), 0.00001);
        assertEquals("elevation",               null,                                   p2.getElevation());
        assertEquals("elevation (primitive)",   0,                                      p2.getElevationOrZero(), 0.0);
        assertEquals("timestamp",               null,                                   p2.getTimestamp());
        assertEquals("timestamp (string)",      null,                                   p2.getTimestampAsString());
        assertEquals("timestamp (millis)",      0L,                                     p2.getTimestampMillis());

        Point p3 = new Point(39.95229, -75.1657517, 10.5, 1577547828000L);

        assertEquals("lat",                     39.95229,                               p3.getLat(), 0.00001);
        assertEquals("lon",                     -75.1657517,                            p3.getLon(), 0.00001);
        assertEquals("elevation",               Double.valueOf(10.5),                   p3.getElevation());
        assertEquals("elevation (primitive)",   10.5,                                   p3.getElevationOrZero(), 0.0);
        assertEquals("timestamp",               Instant.ofEpochMilli(1577547828000L),   p3.getTimestamp());
        assertEquals("timestamp (string)",      "2019-12-28T15:43:48Z",                 p3.getTimestampAsString());
        assertEquals("timestamp (millis)",      1577547828000L,                         p3.getTimestampMillis());

        Point p4 = new Point(39.95229, -75.1657517, Instant.ofEpochMilli(1577547828000L));

        assertEquals("lat",                     39.95229,                               p4.getLat(), 0.00001);
        assertEquals("lon",                     -75.1657517,                            p4.getLon(), 0.00001);
        assertEquals("elevation",               null,                                   p4.getElevation());
        assertEquals("elevation (primitive)",   0,                                      p4.getElevationOrZero(), 0.0);
        assertEquals("timestamp",               Instant.ofEpochMilli(1577547828000L),   p4.getTimestamp());
        assertEquals("timestamp (string)",      "2019-12-28T15:43:48Z",                 p4.getTimestampAsString());
        assertEquals("timestamp (millis)",      1577547828000L,                         p4.getTimestampMillis());

        Point p5 = new Point(39.95229, -75.1657517, 1577547828000L);

        assertEquals("lat",                     39.95229,                               p5.getLat(), 0.00001);
        assertEquals("lon",                     -75.1657517,                            p5.getLon(), 0.00001);
        assertEquals("elevation",               null,                                   p5.getElevation());
        assertEquals("elevation (primitive)",   0,                                      p5.getElevationOrZero(), 0.0);
        assertEquals("timestamp",               Instant.ofEpochMilli(1577547828000L),   p5.getTimestamp());
        assertEquals("timestamp (string)",      "2019-12-28T15:43:48Z",                 p5.getTimestampAsString());
        assertEquals("timestamp (millis)",      1577547828000L,                         p5.getTimestampMillis());

        Point p6 = new Point(39.95229, -75.1657517);

        assertEquals("lat",                     39.95229,                               p6.getLat(), 0.00001);
        assertEquals("lon",                     -75.1657517,                            p6.getLon(), 0.00001);
        assertEquals("elevation",               null,                                   p6.getElevation());
        assertEquals("elevation (primitive)",   0,                                      p6.getElevationOrZero(), 0.0);
        assertEquals("timestamp",               null,                                   p6.getTimestamp());
        assertEquals("timestamp (string)",      null,                                   p6.getTimestampAsString());
        assertEquals("timestamp (millis)",      0L,                                     p6.getTimestampMillis());
    }


    @Test
    public void testConstructorBoundsCheck() throws Exception
    {
        // being able to construct these two indicates success
        new Point(-90.0, -180.0);
        new Point(90.0, 180.0);

        try
        {
            new Point(-90.01, 0);
            fail("accepted latitude < -90");
        }
        catch (IllegalArgumentException ex)
        {
            StringAsserts.assertRegex("invalid lat.*-90.01.*", ex.getMessage());
        }

        try
        {
            new Point(90.01, 0);
            fail("accepted latitude > 90");
        }
        catch (IllegalArgumentException ex)
        {
            StringAsserts.assertRegex("invalid lat.*90.01.*", ex.getMessage());
        }

        try
        {
            new Point(0, -180.01);
            fail("accepted longitude < -180");
        }
        catch (IllegalArgumentException ex)
        {
            StringAsserts.assertRegex("invalid lon.*-180.01.*", ex.getMessage());
        }

        try
        {
            new Point(0, 180.01);
            fail("accepted longitude > 180");
        }
        catch (IllegalArgumentException ex)
        {
            StringAsserts.assertRegex("invalid lon.*180.01.*", ex.getMessage());
        }
    }


    @Test
    public void testHashcodeAndEquals() throws Exception
    {
        Point p1  = new Point(39.95229, -75.1657517, 10.5, 1577547828000L);
        Point p2  = new Point(39.95229, -75.1657517, 10.5, 1577547828000L);
        Point p3  = new Point(39.9523,  -75.1657517, 10.5, 1577547828000L);
        Point p4  = new Point(39.95229, -75.1658,    10.5, 1577547828000L);
        Point p5  = new Point(39.95229, -75.1657517, 10.6, 1577547828000L);
        Point p6  = new Point(39.95229, -75.1657517, 10.5, 1577547828001L);
        Point p7  = new Point(39.95229, -75.1657517, null, Instant.ofEpochMilli(1577547828000L));
        Point p8  = new Point(39.95229, -75.1657517, Double.valueOf(10.5), null);
        Point p9  = new Point(39.95229, -75.1657517);
        Point p10 = new Point(39.95229, -75.1657517);

        assertTrue("identity",                          p1.equals(p1));
        assertTrue("equality",                          p1.equals(p2));
        assertFalse("inequality of latitude",           p1.equals(p3));
        assertFalse("inequality of longitude",          p1.equals(p4));
        assertFalse("inequality of elevation",          p1.equals(p5));
        assertFalse("inequality of timestamp",          p1.equals(p6));
        assertFalse("null elevation",                   p1.equals(p7));
        assertFalse("null timestamp",                   p1.equals(p8));
        assertTrue("null elevation and timestamp",      p9.equals(p10));

        assertEquals("hashcode of equal points",        p1.hashCode(), p2.hashCode());
        assertEquals("hashcode only considers lat/lon", p1.hashCode(), p10.hashCode());

        // within precision, different fractional components will always be different
        assertTrue("hashcode of unequal lats",          p1.hashCode() != p3.hashCode());
        assertTrue("hashcode of unequal lons",          p1.hashCode() != p4.hashCode());
    }


    @Test
    public void testHashDistribution() throws Exception
    {
        int countGT = 0;
        int countLT = 0;

        for (double lat = -90.0 ; lat <= 90.0 ; lat += .1)
        {
            for (double lon = -180.0 ; lon <= 180.0 ; lon += .1)
            {
                int hc = new Point(lat,lon).hashCode();
                if (hc > 0) countGT++;
                if (hc < 0) countLT++;
            }
        }

        assertTrue("some values > 0", countGT > 0);
        assertTrue("some values < 0", countLT > 0);

        NumericAsserts.assertApproximate(">0 within 5% of <0", countGT, countLT, 5);
    }


    @Test
    public void testToString() throws Exception
    {
        Point p = new Point(39.95229, -75.1657517);

        // note that there can be additional digits due to floating-point conversion
        StringAsserts.assertRegex("\\(39.95229\\d*,-75.1657517\\d*\\)", p.toString());
    }


    @Test
    public void testComparable() throws Exception
    {
        Point p1 = new Point( 12.34,  56.78, Double.valueOf(100.0), Instant.ofEpochMilli(1577547828000L));
        Point p2 = new Point( 12.34,  56.78, Double.valueOf(100.0), Instant.ofEpochMilli(1577547829000L));
        Point p3 = new Point( 12.34,  56.78, Double.valueOf(100.0), null);
        Point p4 = new Point(-12.39,  56.78, Double.valueOf(100.0), Instant.ofEpochMilli(1577547828000L));
        Point p5 = new Point( 12.34, -56.80, Double.valueOf(100.0), Instant.ofEpochMilli(1577547828000L));
        Point p6 = new Point( 12.34,  56.78, Double.valueOf(200.0), Instant.ofEpochMilli(1577547828000L));
        Point p7 = new Point( 12.34,  56.78, null,                  Instant.ofEpochMilli(1577547828000L));
        Point p8 = new Point( 12.34,  56.78);

        assertEquals("timestamp <",                         -1,     p1.compareTo(p2));
        assertEquals("timestamp >",                          1,     p2.compareTo(p1));
        assertEquals("null timestamp <",                    -1,     p3.compareTo(p1));
        assertEquals("null timestamp >",                     1,     p1.compareTo(p3));

        assertEquals("latitude <",                          -1,     p1.compareTo(p4));
        assertEquals("latitude >",                           1,     p4.compareTo(p1));

        assertEquals("longitude <",                         -1,     p1.compareTo(p5));
        assertEquals("longitude >",                          1,     p5.compareTo(p1));

        assertEquals("elevation <",                         -1,     p1.compareTo(p6));
        assertEquals("elevation >",                          1,     p6.compareTo(p1));
        assertEquals("null elevation <",                     1,     p6.compareTo(p1));
        assertEquals("null elevation >",                    -1,     p1.compareTo(p6));

        assertEquals("equal",                                0,     p1.compareTo(p1));
        assertEquals("equal, null timestamp",                0,     p3.compareTo(p3));
        assertEquals("equal, null elevation",                0,     p7.compareTo(p7));
        assertEquals("equal, null elevation and timestamp",  0,     p8.compareTo(p8));
    }
}
