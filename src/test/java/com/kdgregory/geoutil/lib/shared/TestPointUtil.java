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

import org.junit.Test;
import static org.junit.Assert.*;


public class TestPointUtil
{
    @Test
    public void testCorrectedLongitude() throws Exception
    {
        // note: 100,000 chosen to be in same scale as base degree length on Earth

        assertEquals("0",   100000.0, PointUtil.correctedLongitude(100000.0,  0), 0.1);

        assertEquals("45S",  70710.7, PointUtil.correctedLongitude(100000.0, -45), 0.1);
        assertEquals("45N",  70710.7, PointUtil.correctedLongitude(100000.0,  45), 0.1);

        assertEquals("90S",      0.0, PointUtil.correctedLongitude(100000.0, -90), 0.1);
        assertEquals("90N",      0.0, PointUtil.correctedLongitude(100000.0,  90), 0.1);
    }


    @Test
    public void testPythagoreanDistance() throws Exception
    {
        // verify the base calculation: as long as this is right any values should work

        double d1 = PointUtil.pythagoreanDistance(45, 75, 46, 76, 100000);
        assertEquals("base calculation", 122474.5, d1, 0.1);

        // verify the chain of alls and base Earth degree length

        Point p2a = new Point(45, 75);
        Point p2b = new Point(46, 76);
        double d2 = PointUtil.pythagoreanDistance(p2a, p2b);
        assertEquals("point calculation, on Earth", 136108.4, d2, 0.1);
    }


    @Test
    public void testGreatCircleDistance() throws Exception
    {
        // rather than work through the calculation for arbitrary radius (and likely making
        // a mistake), I used an online source for Earth distance. However, the available
        // resolution for that source was kilometers, so I'm adjusted assertion to correspond

        Point p1 = new Point(-15, -15);
        Point p2 = new Point(15, 15);
        double d = PointUtil.greatCircleDistance(p1, p2) / 1000;
        assertEquals("calculation on Earth", 4690.0, d, 0.5);
    }


    @Test
    public void testVelocity() throws Exception
    {
        Point p1 = new Point(45, 75, 0);
        Point p2 = new Point(46, 76, 2000);
        assertEquals("meters/second",  68054.2, PointUtil.velocity(p1, p2), 0.1);
        assertEquals("meters/second",  152232.6, PointUtil.velocityMPH(p1, p2), 0.1);
    }


    @Test
    public void testMidpoint() throws Exception
    {
        Point p1a = new Point(-1,  1, 10, 1000);
        Point p1b = new Point( 1, -1, 11, 2000);
        Point x1  = new Point( 0,  0, 10.5, 1500);

        assertEquals("all values",            x1, PointUtil.midpoint(p1a, p1b));
        assertEquals("all values, reversed",  x1, PointUtil.midpoint(p1b, p1a));

        Point p2a = new Point(-1,  1, Double.valueOf(10), null);
        Point p2b = new Point( 1, -1, Double.valueOf(11), null);
        Point x2  = new Point( 0,  0, Double.valueOf(10.5), null);

        assertEquals("no timestamp",            x2, PointUtil.midpoint(p2a, p2b));
        assertEquals("no timestamp, reversed",  x2, PointUtil.midpoint(p2b, p2a));

        Point p3a = new Point(-1,  1, 1000);
        Point p3b = new Point( 1, -1, 2000);
        Point x3  = new Point( 0,  0, 1500);

        assertEquals("no elevation",            x3, PointUtil.midpoint(p3a, p3b));
        assertEquals("no elevation, reversed",  x3, PointUtil.midpoint(p3b, p3a));

        Point p4a = new Point(-1,  1);
        Point p4b = new Point( 1, -1);
        Point x4  = new Point( 0,  0);

        assertEquals("lat/lon only",            x4, PointUtil.midpoint(p4a, p4b));
        assertEquals("lat/lon only, reversed",  x4, PointUtil.midpoint(p4b, p4a));
    }
}
