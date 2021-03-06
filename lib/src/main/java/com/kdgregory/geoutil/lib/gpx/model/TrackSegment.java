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

package com.kdgregory.geoutil.lib.gpx.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.w3c.dom.Element;

import net.sf.practicalxml.DomUtil;

import com.kdgregory.geoutil.lib.core.Point;
import com.kdgregory.geoutil.lib.core.SegmentUtil;
import com.kdgregory.geoutil.lib.gpx.GpxConstants;


/**
 *  A list of points within a track.
 */
public class TrackSegment
{
    private List<GpxPoint> points = new ArrayList<>();

//----------------------------------------------------------------------------
//  Accessors
//----------------------------------------------------------------------------

    /**
     *  Returns whether or not this segment has any points.
     */
    public boolean isEmpty()
    {
        return points.isEmpty();
    }


    /**
     *  Returns the number of points in this segment.
     */
    public int size()
    {
        return points.size();
    }


    /**
     *  Returns the list of points in this segment.
     */
    public List<GpxPoint> getPoints()
    {
        return points;
    }


    /**
     *  Adds a single point to the end of the list.
     */
    public TrackSegment add(GpxPoint p)
    {
        points.add(p);
        return this;
    }


    /**
     *  Adds multiple points to the end of the list.
     */
    public TrackSegment addAll(Collection<GpxPoint> ps)
    {
        points.addAll(ps);
        return this;
    }


    /**
     *  Clears the existing list of points
     */
    public void clear()
    {
        points.clear();
    }

//----------------------------------------------------------------------------
//  XML conversion
//----------------------------------------------------------------------------

    /**
     *  Appends this segment to a parent element, IFF it contains points.
     */
    public void appendAsXml(Element parent)
    {
        if (points.isEmpty())
            return;

        Element eSeg = DomUtil.appendChild(parent, GpxConstants.NAMESPACE, GpxConstants.E_TRKSEG);
        for (GpxPoint p : points)
        {
            p.appendAsXml(eSeg, GpxConstants.E_TRKPOINT);
        }
    }


    /**
     *  Parses a segment from its XML representation.
     *
     *  Does not validate the provided element's name or namespace, or whether
     *  it contains unexpected content.
     */
    public static TrackSegment fromXml(Element elem)
    {
        TrackSegment segment = new TrackSegment();
        for (Element ePoint : DomUtil.getChildren(elem, GpxConstants.NAMESPACE, GpxConstants.E_TRKPOINT))
        {
            segment.add(GpxPoint.fromXml(ePoint));
        }
        return segment;
    }

//----------------------------------------------------------------------------
//  Other public methods
//----------------------------------------------------------------------------

    /**
     *  Filters all points in this segment, using a Java8 predicate.
     */
    public void filter(Predicate<GpxPoint> pred)
    {
        List<GpxPoint> filtered = points.stream()
                               .filter(pred)
                               .collect(Collectors.toList());
        points = new ArrayList<>(filtered);
    }


    /**
     *  Filters all points in this segment by inclusive timestamp.
     */
    public void filter(Instant start, Instant finish)
    {
        filter(p -> p.isBetween(start, finish));
    }


    /**
     *  Trims points off the beginning and end of the segment that don't show
     *  movement (<code>minSeparation</code> meters between points).
     */
    public void trim(double minSeparation)
    {
        List<Point> corePoints = new ArrayList<>();
        IdentityHashMap<Point,GpxPoint> coreLookup = new IdentityHashMap<>();

        for (GpxPoint point : points)
        {
            Point corePoint = point.getPoint();
            corePoints.add(corePoint);
            coreLookup.put(corePoint, point);
        }

        corePoints = SegmentUtil.trim(corePoints, minSeparation);

        points.clear();
        for (Point corePoint : corePoints)
        {
            points.add(coreLookup.get(corePoint));
        }
    }


    /**
     *  Sorts the points in this segment using the underlying <code>Point</code>.
     *  This is primarily used when combining segments from different tracks.
     */
    public void sortPoints()
    {
        Collections.sort(points, (p1, p2) -> p1.getPoint().compareTo(p2.getPoint()));
    }


    /**
     *  Calculates the length of this segment, in meters, using latitude-compensated
     *  Pythagorean distance.
     */
    public double distance()
    {
        List<Point> temp = points.stream()
                           .map(GpxPoint::getPoint)
                           .sorted()
                           .collect(Collectors.toList());
        return SegmentUtil.pythagoreanDistance(temp);
    }
}
