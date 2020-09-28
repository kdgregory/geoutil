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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.Element;

import net.sf.practicalxml.DomUtil;

import com.kdgregory.geoutil.lib.gpx.Constants;


/**
 *  A list of points within a track.
 */
public class TrackSegment
{
    private List<Point> points = new ArrayList<>();
    

    /**
     *  Base constructor.  
     */
    public TrackSegment()
    {
        // nothing happening here
    }
    
    
    /**
     *  Constructs an instance from an XML node tree structured as a trksegType
     *  per https://www.topografix.com/GPX/1/1/.
     */
    public TrackSegment(Element elem)
    {
        for (Element ePoint : DomUtil.getChildren(elem, Constants.NAMESPACE, Constants.E_TRKPOINT))
        {
            add(new Point(ePoint));
        }
    }
    
//----------------------------------------------------------------------------
//  Accessors
//----------------------------------------------------------------------------

    /**
     *  Returns the list of points, as an immutable list.
     */
    public List<Point> getPoints()
    {
        return Collections.unmodifiableList(points);
    }


    /**
     *  Adds a single point to the end of the list.
     */
    public TrackSegment add(Point p)
    {
        points.add(p);
        return this;
    }


    /**
     *  Adds multiple points to the end of the list.
     */
    public TrackSegment addAll(Collection<Point> ps)
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
//  Other Public Methods
//----------------------------------------------------------------------------
    
    /**
     *  Appends this segment to a parent element, IFF it contains points.
     */
    public void appendAsXml(Element parent)
    {
        if (points.isEmpty())
            return;

        Element eSeg = DomUtil.appendChild(parent, Constants.NAMESPACE, Constants.E_TRKSEG);
        for (Point p : points)
        {
            p.appendAsXml(eSeg, Constants.E_TRKPOINT);
        }
    }
    
    // TODO: filter
}
