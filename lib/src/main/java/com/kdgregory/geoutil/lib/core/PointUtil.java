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

package com.kdgregory.geoutil.lib.core;

import java.time.Instant;

/**
 *  Utility functions to work with individual points.
 */
public class PointUtil
{
    /**
     *  The base length of a degree in meters, used for Pythagorean diestance
     *  calculations. This is the length of a degree of latitude at 45 N/S as
     *  given by https://en.wikipedia.org/wiki/Latitude
     */
    public final static double EARTH_DEGREE_LENGTH = 111132;


    /**
     *  The mean Earth radius, from https://en.wikipedia.org/wiki/Great-circle_distance.
     */
    public final static double EARTH_RADIUS = 6371000;


    /**
     *  Corrects the length of a degree of longitude based on the measured latitude
     *  (specified in degrees, not radians).
     */
    public static double correctedLongitude(double baseDegreeLength, double atLatitude)
    {
        return baseDegreeLength * Math.cos(Math.toRadians(atLatitude));
    }


    /**
     *  Calculates the Pythagorean distance (in meters) between two points,
     *  with arbitrary base degree length.
     */
    public static double pythagoreanDistance(double lat1, double lon1, double lat2, double lon2, double baseDegreeLength)
    {
        double dLat = baseDegreeLength * (lat2 - lat1);
        double dLon = correctedLongitude(baseDegreeLength * (lon2 - lon1), lat1);
        return Math.sqrt(dLat * dLat + dLon * dLon);
    }


    /**
     *  Calculates the Pythagorean distance (in meters) between two points on Earth.
     */
    public static double pythagoreanDistance(double lat1, double lon1, double lat2, double lon2)
    {
        return pythagoreanDistance(lat1, lon1, lat2, lon2, EARTH_DEGREE_LENGTH);
    }


    /**
     *  Calculates the Pythagorean distance between two points on Earth.
     */
    public static double pythagoreanDistance(Point p1, Point p2)
    {
        return pythagoreanDistance(p1.getLat(), p1.getLon(), p2.getLat(), p2.getLon());
    }


    /**
     *  Calculates the Great Circle distance between two points, with arbitrary
     *  sphere radius.
     */
    public static double greatCircleDistance(double lat1, double lon1, double lat2, double lon2, double radius)
    {
        double lat1R = Math.toRadians(lat1);
        double lat2R = Math.toRadians(lat2);
        double dLon = Math.toRadians(lon2) - Math.toRadians(lon1);
        double n1 = Math.cos(lat2R) * Math.sin(dLon);
        double n2 = Math.cos(lat1R) * Math.sin(lat2R) - Math.sin(lat1R) * Math.cos(lat2R) * Math.cos(dLon);
        double d  = Math.sin(lat1R) * Math.sin(lat2R) + Math.cos(lat1R) * Math.cos(lat2R) * Math.cos(dLon);
        double sigma = Math.atan(Math.sqrt(n1 * n1 + n2 * n2) / d);
        return sigma * radius;
    }


    /**
     *  Calculates the Great Circle distance between two points on the Earth.
     */
    public static double greatCircleDistance(double lat1, double lon1, double lat2, double lon2)
    {
        return greatCircleDistance(lat1, lon1, lat2, lon2, EARTH_RADIUS);
    }


    /**
     *  Calculates the Great Circle distance between two points on the Earth.
     */
    public static double greatCircleDistance(Point p1, Point p2)
    {
        return greatCircleDistance(p1.getLat(), p1.getLon(), p2.getLat(), p2.getLon());
    }


    /**
     *  Determines the velocity, in meters/second, to travel from one point to another
     *  (calculated using Pythagorean distance).
     */
    public static double velocity(Point p1, Point p2)
    {
        double distMeters = PointUtil.pythagoreanDistance(p1, p2);
        double elapsed = (p1.getTimestampMillis() - p2.getTimestampMillis()) / 1000.0;
        return Math.abs(distMeters / elapsed);
    }


    /**
     *  Determines the velocity, in miles/hour, to travel from one point to another
     *  (calculated using Pythagorean distance).
     */
    public static double velocityMPH(Point p1, Point p2)
    {
        return velocity(p1, p2) * 39.37 / 12 / 5280 * 3600;
    }


    /**
     *  Returns the midpoint of the two given points. If both points have elevation
     *  and/or timestamp, then these values are averaged as well. If only one (or
     *  neither) have elevation/timestamp, the result is null.
     */
    public static Point midpoint(Point p1, Point p2)
    {
        double lat = (p2.getLat() + p1.getLat()) / 2;
        double lon = (p2.getLon() + p1.getLon()) / 2;

        Double elevation = null;
        if ((p1.getElevation() != null) && (p2.getElevation() != null))
        {
            double avg = (p1.getElevation().doubleValue() + p2.getElevation().doubleValue()) / 2;
            elevation = Double.valueOf(avg);
        }

        Instant timestamp = null;
        if ((p1.getTimestamp() != null) && (p2.getTimestamp() != null))
        {
            // this calculation will overflow in the distant future
            long avg = (p1.getTimestampMillis() + p2.getTimestampMillis()) / 2;
            timestamp = Instant.ofEpochMilli(avg);
        }

        return new Point(lat, lon, elevation, timestamp);
    }
}
