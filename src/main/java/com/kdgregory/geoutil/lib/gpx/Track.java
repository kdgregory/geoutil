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

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.w3c.dom.Element;

import net.sf.practicalxml.DomUtil;

import com.kdgregory.geoutil.lib.shared.XmlUtils;


/**
 *  Represents a track: a named collection of segments containing points.
 *  <p>
 *  All setter methods return the track object, as a convenience for
 *  chaining calls.
 *  <p>
 *  Tracks have identity equality semantics: there's no good reason to be
 *  comparing tracks or using them as keys in a map.
 */
public class Track
{
    private String name;
    private String description;
    private List<TrackSegment> segments = new ArrayList<>();


    /**
     *  Base constructor.
     */
    public Track()
    {
        // nothing here
    }


    /**
     *  Constructs an instance from an XML node tree structured as a trkType
     *  per https://www.topografix.com/GPX/1/1/.
     *
     *  Does not validate the provided element's name or namespace, or whether
     *  it contains unexpected content.
     */
    public Track(Element elem)
    {
        Map<String,Element> children = XmlUtils.listToMap(DomUtil.getChildren(elem));
        XmlUtils.optSetString(children.get(GpxConstants.E_TRK_NAME),           GpxConstants.NAMESPACE, this::setName);
        XmlUtils.optSetString(children.get(GpxConstants.E_TRK_DESCRIPTION),    GpxConstants.NAMESPACE, this::setDescription);

        for (Element eSeg : DomUtil.getChildren(elem, GpxConstants.NAMESPACE, GpxConstants.E_TRKSEG))
        {
            addSegment(new TrackSegment(eSeg));
        }
    }

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
     *  Returns an unmodifiable list of the segments that comprise this track.
     *  May be empty, but will never be null. Note that the segments in this
     *  list are modifiable.
     */
    public List<TrackSegment> getSegments()
    {
        return Collections.unmodifiableList(segments);
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
//  Other Public Methods
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

        for (TrackSegment seg : segments)
        {
            seg.appendAsXml(elem);
        }

        return elem;
    }


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
     *  Combines all existing segments into a single segment.
     */
    public void combineSegments()
    {
        TrackSegment seg = new TrackSegment();
        for (TrackSegment old : segments)
        {
            seg.addAll(old.getPoints());
        }
        segments.clear();
        segments.add(seg);
    }
}
