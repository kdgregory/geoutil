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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import net.sf.practicalxml.DomUtil;

import com.kdgregory.geoutil.lib.gpx.Constants;
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
     */
    public Track(Element elem)
    {
        Map<String,Element> children = XmlUtils.listToMap(DomUtil.getChildren(elem));
        XmlUtils.optSetString(children.get(Constants.E_TRK_NAME),           Constants.NAMESPACE, this::setName);
        XmlUtils.optSetString(children.get(Constants.E_TRK_DESCRIPTION),    Constants.NAMESPACE, this::setDescription);

        for (Element eSeg : DomUtil.getChildren(elem, Constants.NAMESPACE, Constants.E_TRKSEG))
        {
            addSegment(new TrackSegment(eSeg));
        }
    }

//----------------------------------------------------------------------------
// Setters
//----------------------------------------------------------------------------

    /**
     *  Sets the name of this track.
     */
    public Track setName(String value)
    {
        name = value;
        return this;
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
// Getters
//----------------------------------------------------------------------------

    /**
     *  Returns the name of this track. May be null.
     */
    public String getName()
    {
        return name;
    }


    /**
     *  Returns the description of this track. May be null.
     */
    public String getDescription()
    {
        return description;
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
        Element elem = DomUtil.appendChild(parent, Constants.NAMESPACE, Constants.E_TRK);

        XmlUtils.optAppendDataElement(elem, Constants.NAMESPACE, Constants.E_TRK_NAME,          getName());
        XmlUtils.optAppendDataElement(elem, Constants.NAMESPACE, Constants.E_TRK_DESCRIPTION,   getDescription());

        for (TrackSegment seg : getSegments())
        {
            seg.appendAsXml(elem);
        }

        return elem;
    }

    // TODO: filter, combineSegments
}
