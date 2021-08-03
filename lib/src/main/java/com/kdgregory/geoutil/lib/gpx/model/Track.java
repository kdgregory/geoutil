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

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.w3c.dom.Element;

import net.sf.practicalxml.DomUtil;

import com.kdgregory.geoutil.lib.core.Point;
import com.kdgregory.geoutil.lib.core.SegmentUtil;
import com.kdgregory.geoutil.lib.gpx.GpxConstants;
import com.kdgregory.geoutil.lib.internal.ObjectUtils;
import com.kdgregory.geoutil.lib.internal.XmlUtils;


/**
 *  Represents a track: a named collection of segments containing points.
 *  <p>
 *  All setter methods return the track object, as a convenience for chained calls.
 *  <p>
 *  Tracks have identity equality semantics: there's no good reason to be
 *  comparing tracks or using them as keys in a map.
 */
public class Track
{
    private String name;
    private String description;
    private String type;
    private List<TrackSegment> segments = new ArrayList<>();

//----------------------------------------------------------------------------
// Accessors
//----------------------------------------------------------------------------

    /**
     *  Returns the name of this track. May be null.
     */
    public String getName()
    {
        return name;
    }


    /**
     *  Sets the name of this track.
     */
    public Track setName(String value)
    {
        name = value;
        return this;
    }


    /**
     *  Returns the description of this track. May be null.
     */
    public String getDescription()
    {
        return description;
    }


    /**
     *  Sets the description of this track.
     */
    public Track setDescription(String value)
    {
        description = value;
        return this;
    }


    /**
     *  Returns the type of this track. May be null.
     */
    public String getType()
    {
        return type;
    }


    /**
     *  Sets the type of this track.
     */
    public Track setType(String value)
    {
        type = value;
        return this;
    }


    /**
     *  Returns the segments that comprise this track. May be empty, but will never
     *  be null.
     */
    public List<TrackSegment> getSegments()
    {
        return segments;
    }


    /**
     *  Sets the list of segments from the passed list. Makes a defensive copy
     *  of the list; changes to the list are not reflected in this object. Passing
     *  null clears the internal list.
     */
    public Track setSegments(List<TrackSegment> value)
    {
        segments.clear();
        if (value != null)
        {
            segments.addAll(value);
        }
        return this;
    }


    /**
     *  Adds a segment to the end of the list.
     */
    public Track addSegment(TrackSegment value)
    {
        if (value != null)
        {
            segments.add(value);
        }
        return this;
    }

//----------------------------------------------------------------------------
//  XML conversion
//----------------------------------------------------------------------------

    /**
     *  Appends this object as a "trk" element to the provided parent (which is
     *  normally the root of the GPX file).
     *
     *  @return The newly created element.
     */
    public Element appendAsXml(Element parent)
    {
        Element elem = DomUtil.appendChild(parent, GpxConstants.NAMESPACE, GpxConstants.E_TRK);

        XmlUtils.optAppendDataElement(elem, GpxConstants.NAMESPACE, GpxConstants.E_TRK_NAME,          getName());
        XmlUtils.optAppendDataElement(elem, GpxConstants.NAMESPACE, GpxConstants.E_TRK_DESCRIPTION,   getDescription());
        XmlUtils.optAppendDataElement(elem, GpxConstants.NAMESPACE, GpxConstants.E_TRK_TYPE,          getType());

        for (TrackSegment seg : segments)
        {
            seg.appendAsXml(elem);
        }

        return elem;
    }


    /**
     *  Parses a track from its XML representation.
     *
     *  Does not validate the provided element's name or namespace, or whether
     *  it contains unexpected content.
     */
    public static Track fromXml(Element elem)
    {
        Track track = new Track();
        for (Element child : DomUtil.getChildren(elem))
        {
            String childNamespace = child.getNamespaceURI();
            String childName = DomUtil.getLocalName(child);

            if (! GpxConstants.NAMESPACE.equals(childNamespace))
                throw new IllegalArgumentException("invalid namespace: " + childNamespace);

            switch (childName)
            {
                case GpxConstants.E_TRK_NAME:
                    ObjectUtils.optSetString(child.getTextContent(), track::setName);
                    break;
                case GpxConstants.E_TRK_DESCRIPTION:
                    ObjectUtils.optSetString(child.getTextContent(), track::setDescription);
                    break;
                case GpxConstants.E_TRK_TYPE:
                    ObjectUtils.optSetString(child.getTextContent(), track::setType);
                    break;
                case GpxConstants.E_TRKSEG:
                    track.addSegment(TrackSegment.fromXml(child));
                    break;
                default:
                    throw new IllegalArgumentException("unsupported element: " + childName);
            }
        }
        return track;
    }

//----------------------------------------------------------------------------
//  Other public methods
//----------------------------------------------------------------------------

    /**
     *  Filters the points of all segments in this track, using a Java8 predicate.
     *  Segments that have no points after filtering are removed.
     */
    public void filter(Predicate<GpxPoint> pred)
    {
        segments.stream().forEach(seg -> seg.filter(pred));
        List<TrackSegment> filtered = segments.stream()
                                      .filter(s -> ! s.isEmpty())
                                      .collect(Collectors.toList());
        segments = new ArrayList<>(filtered);
    }


    /**
     *  Filters all segments in this track by inclusive timestamp.
     */
    public void filter(Instant start, Instant finish)
    {
        filter(p -> p.isBetween(start, finish));
    }


    /**
     *  Combines all existing segments into a single segment, returning it.
     */
    public TrackSegment combineSegments()
    {
        TrackSegment result = new TrackSegment();
        for (TrackSegment old : segments)
        {
            result.addAll(old.getPoints());
        }
        segments.clear();
        segments.add(result);
        return result;
    }


    /**
     *  Splits (or re-splits) segments based on a gap in time greater than <code>maxGap</code>.
     */
    public void splitSegments(Duration maxGap)
    {
        List<Point> corePoints = new ArrayList<>();
        IdentityHashMap<Point,GpxPoint> coreLookup = new IdentityHashMap<>();

        for (TrackSegment seg : segments)
        {
            for (GpxPoint point : seg.getPoints())
            {
                Point corePoint = point.getPoint();
                corePoints.add(corePoint);
                coreLookup.put(corePoint, point);
            }
        }

        segments.clear();

        for (List<Point> split : SegmentUtil.split(corePoints, maxGap))
        {
            TrackSegment seg = new TrackSegment();
            for (Point corePoint : split)
            {
                seg.add(coreLookup.get(corePoint));
            }
            segments.add(seg);
        }
    }

}
