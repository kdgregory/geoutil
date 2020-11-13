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

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

import com.kdgregory.geoutil.lib.kml.fieldtypes.Coordinates;
import com.kdgregory.geoutil.lib.shared.Point;


public class TestCoordinates
{
    @Test
    public void testBasicConstructors() throws Exception
    {
        Coordinates c1 = new Coordinates(12, 34);

        assertEquals("c1 latitude",     12,     c1.getLat(), 0.0);
        assertEquals("c1 longitude",    34,     c1.getLon(), 0.0);
        assertNull("c1 altitude",               c1.getElevation());

        Coordinates c2 = new Coordinates(12, 34, 56);

        assertEquals("c2 latitude",     12,     c2.getLat(), 0.0);
        assertEquals("c2 longitude",    34,     c2.getLon(), 0.0);
        assertEquals("c2 altitude",     56,     c2.getElevation().doubleValue(), 0.0);
    }


    @Test
    public void testCopyConstructor() throws Exception
    {
        Coordinates c1 = new Coordinates(12, 34, 56);
        Coordinates c2 = new Coordinates(c1);

        assertEquals("c2 latitude",     12,     c2.getLat(), 0.0);
        assertEquals("c2 longitude",    34,     c2.getLon(), 0.0);
        assertEquals("c2 altitude",     56,     c2.getElevation().doubleValue(), 0.0);
    }


    @Test
    public void testStringSerialization() throws Exception
    {
        String c1s = new Coordinates(12, 34).toString();
        assertEquals("c1 string", "34.0,12.0", c1s);

        Coordinates c1 = Coordinates.fromString(c1s);

        assertEquals("c1 latitude",     12,     c1.getLat(), 0.0);
        assertEquals("c1 longitude",    34,     c1.getLon(), 0.0);
        assertNull("c1 altitude",               c1.getElevation());

        String c2s = new Coordinates(12, 34, 56).toString();
        assertEquals("c12 string", "34.0,12.0,56.0", c2s);

        Point c2 = Coordinates.fromString(c2s);

        assertEquals("c2 latitude",     12,     c2.getLat(), 0.0);
        assertEquals("c2 longitude",    34,     c2.getLon(), 0.0);
        assertEquals("c2 altitude",     56,     c2.getElevation().doubleValue(), 0.0);
    }


    @Test
    public void testStringListSerialization() throws Exception
    {
        Coordinates c1 = new Coordinates(12, 34);
        Coordinates c2 = new Coordinates(34, 56, 78);

        List<Coordinates> orig = Arrays.asList(c1, c2);

        assertEquals("serialization",   "34.0,12.0 56.0,34.0,78.0",     Coordinates.stringify(orig));
        assertEquals("deserialization", orig,                           Coordinates.fromStringList("34.0,12.0 56.0,34.0,78.0"));
    }
}
