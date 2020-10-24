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

import net.sf.kdgcommons.lang.ObjectUtil;

/**
 *  Represents a point on a sphere, with latitude, longitude, optional
 *  elevation, and an optional timestamp.
 *  <p>
 *  Instances are immutable once constructed.
 */
public class Point
implements Comparable<Point>
{
    private final double lat;
    private final double lon;
    private final Double elevation;
    private final Instant timestamp;


    /**
     *  Base constructor, allowing all values to be set.
     *
     *  @param  lat         Latitude, ranging from -90 (south) to +90 (north).
     *  @param  lon         Longitude, ranging from -180 (west) to +180 (east).
     *  @param  elevation   Elevation of the point, in meters.
     *  @param  timestamp   Fixes the point in time as well as space.
     */
    public Point(double lat, double lon, Double elevation, Instant timestamp)
    {
        if ((lat < -90.0) || (lat > 90.0))
            throw new IllegalArgumentException("invalid latitude: " + lat);

        if ((lon < -180.0) || (lon > 180.0))
            throw new IllegalArgumentException("invalid longitude: " + lon);

        this.lat = lat;
        this.lon = lon;
        this.elevation = elevation;
        this.timestamp = timestamp;
    }


    /**
     *  Convenience constructor for primitive values, where timestamp is
     *  provided as milliseconds since epoch.
     */
    public Point(double lat, double lon, double elevation, long timestamp)
    {
        this(lat, lon, Double.valueOf(elevation), Instant.ofEpochMilli(timestamp));
    }


    /**
     *  Convenience constructor for timestamped points without elevation.
     */
    public Point(double lat, double lon, Instant timestamp)
    {
        this(lat, lon, null, timestamp);
    }


    /**
     *  Convenience constructor for timestamped points without elevation, where
     *  timestamp is provided as milliseconds sinch epoch.
     */
    public Point(double lat, double lon, long timestamp)
    {
        this(lat, lon, null, Instant.ofEpochMilli(timestamp));
    }


    /**
     *  Convenience constructor, for points that just represent 2D location.
     */
    public Point(double lat, double lon)
    {
        this(lat, lon, null, null);
    }

//----------------------------------------------------------------------------
//  Accessors
//----------------------------------------------------------------------------

    /**
     *  Returns the point's latitude.
     */
    public double getLat()
    {
        return lat;
    }


    /**
     *  Returns the point's longitude.
     */
    public double getLon()
    {
        return lon;
    }


    /**
     *  Returns the point's elevation. May be null.
     */
    public Double getElevation()
    {
        return elevation;
    }


    /**
     *  Returns the point's elevation as a primitive value, with missing
     *  elevation replaced by 0.
     */
    public double getElevationOrZero()
    {
        return (elevation == null)
             ? 0.0
             : elevation.doubleValue();
    }


    /**
     *  Returns the point's timestamp. May be null.
     */
    public Instant getTimestamp()
    {
        return timestamp;
    }


    /**
     *  Returns the point's timestamp as a string in ISO-8601 "Zulu"
     *  format. May be null.
     */
    public String getTimestampAsString()
    {
        return (timestamp == null)
             ? null
             : timestamp.toString();
    }


    /**
     *  Returns the point's timestamp as milliseconds since the epoch, with
     *  missing timestamps replaced by 0.
     */
    public long getTimestampMillis()
    {
        return (timestamp == null)
             ? 0
             : timestamp.toEpochMilli();
    }

//----------------------------------------------------------------------------
//  Overrides
//----------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        // this hashcode calculation attempts to minimize the co-hashing of
        // nearby points; it essentially truncates the whole degrees

        return ((int)(lat * 128 * 65536) & 65535) * 65536
             + ((int)(lon * 256 * 65536) & 65535);
    }


    /**
     *  Two points are equal if all fields (lat, lon, elevation, timestamp) are
     *  equal. For elevation and timestamp, null values are considered equal.
     */
    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
            return true;

        if (obj instanceof Point)
        {
            Point that = (Point)obj;
            return this.lat == that.lat
                && this.lon == that.lon
                && ObjectUtil.equals(this.elevation, that.elevation)
                && ObjectUtil.equals(this.timestamp, that.timestamp);
        }
        return false;
    }


    /**
     *  String representation is "(lat, lon)".
     */
    @Override
    public String toString()
    {
        return "(" + getLat() + "," + getLon() + ")";
    }


    /**
     *  Instances are comparable, for compatibility with {@link TimestampedPoint}. One
     *  instance is larger than another if (1) it has a greater latitude, or (2) has
     *  the same latitude and a greater longitude (ie, ordered to the northeast).
     */
    @Override
    public int compareTo(Point that)
    {
        return (this.lat > that.lat) ? 1
             : (this.lat < that.lat) ? -1
             : (this.lon > that.lon) ? 1
             : (this.lon < that.lon) ? -1
             : 0;
    }
}
