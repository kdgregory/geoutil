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

package com.kdgregory.geoutil.lib.kml.fieldtypes;

import java.util.ArrayList;
import java.util.List;

import com.kdgregory.geoutil.lib.core.Point;


/**
 *  A point on the Earth, with optional altitude. Supports KML serialization
 *  format.
 */
public class Coordinates
extends Point
{
    /**
     *  Constructs an instance with latitude, longitude, and altitude.
     */
    public Coordinates(double lat, double lon, double alt)
    {
        super(lat, lon, Double.valueOf(alt), null);
    }


    /**
     *  Constructs an instance with latitude and longitude, no altitude.
     */
    public Coordinates(double lat, double lon)
    {
        super(lat, lon);
    }


    /**
     *  Constructs an instance from an existing <code>Point</code> object
     *  (including subclasses such as <code>Coordinate</code> itself).
     */
    public Coordinates(Point point)
    {
        super(point);
    }

//----------------------------------------------------------------------------
//  Overrides
//----------------------------------------------------------------------------

    /**
     *  Writes the KML serialized representation of these coordinates.
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder(128)
                           .append(getLon()).append(",")
                           .append(getLat());
        if (getElevation() != null)
        {
            sb.append(",").append(getElevation());
        }
        return sb.toString();
    }

//----------------------------------------------------------------------------
//  Other public methods
//----------------------------------------------------------------------------

    /**
     *  Creates an instance from the serialized representation.
     */
    public static Coordinates fromString(String value)
    {
        String[] split = value.split(",");
        return (split.length == 2)
             ? new Coordinates(Double.parseDouble(split[1]), Double.parseDouble(split[0]))
             : new Coordinates(Double.parseDouble(split[1]), Double.parseDouble(split[0]), Double.valueOf(split[2]));
    }


    /**
     *  Creates an instance from the stringified representation of a list of coordinates.
     */
    public static List<Coordinates> fromStringList(String value)
    {
        List<Coordinates> result = new ArrayList<>();
        for (String s : value.split(" "))
        {
            s = s.trim();
            if (! s.isEmpty())
            {
                result.add(Coordinates.fromString(s));
            }
        }
        return result;
    }


    /**
     *  Creates the string representation of a list of coordinates.
     */
    public static String stringify(List<Coordinates> coords)
    {
        StringBuilder sb = new StringBuilder(256);
        for (Coordinates coord : coords)
        {
            if (sb.length() > 0) sb.append(" ");
            sb.append(coord.toString());
        }
        return sb.toString();
    }
}
