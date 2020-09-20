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

package com.kdgregory.geoutil.lib.gpx;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sf.kdgcommons.lang.StringUtil;
import net.sf.practicalxml.DomUtil;
import net.sf.practicalxml.ParseUtil;
import net.sf.practicalxml.xpath.XPathWrapperFactory;
import net.sf.practicalxml.xpath.XPathWrapperFactory.CacheType;

import com.kdgregory.geoutil.lib.shared.TimestampedPoint;


/**
 *  Wraps a GPX file and provides access to retrieve or update its contents.
 */
public class GPXReader
{
    private Document dom;

    private XPathWrapperFactory xpathFact = new XPathWrapperFactory(CacheType.SIMPLE)
                                            .bindNamespace("ns", "http://www.topografix.com/GPX/1/1");


    /**
     *  Constructs from a file on the filesystem.
     */
    public GPXReader(File file)
    {
        dom = ParseUtil.parse(file);
    }


    /**
     *  Constructs from an arbitrary <code>InputStream</code>. The caller is
     *  responsible for closing the stream.
     */
    public GPXReader(InputStream stream)
    {
        dom = ParseUtil.parse(stream);
    }

//----------------------------------------------------------------------------
//  Public methods
//----------------------------------------------------------------------------

    /**
     *  Returns the DOM element trees for all tracks in the file.
     */
    public List<Element> getTracks()
    {
        return xpathFact.newXPath("/ns:gpx/ns:trk").evaluate(dom, Element.class);
    }


    /**
     *  Returns all named tracks, by name. Note that tracks are not required to
     *  have a name; those tracks are ignored by this call.
     */
    public Map<String,Element> getTracksByName()
    {
        Map<String,Element> result = new HashMap<>();
        for (Element track : getTracks())
        {
            Element eName = DomUtil.getChild(track, "name");
            if (eName != null)
            {
                String name = DomUtil.getText(eName);
                if (! StringUtil.isEmpty(name))
                {
                    result.put(name, track);
                }
            }
        }
        return result;
    }


    /**
     *  Returns all points from this file, combining all tracks in order.
     *  <p>
     *  This discards all information other than latitude, longitude, and timestamp.
     *  <p>
     *  Note that points may not be ordered by timestamp (they will be ordered as
     *  they appear in the file).
     */
    public List<TimestampedPoint> getPoints()
    {
        List<TimestampedPoint> result = new ArrayList<>();
        for (Element track : getTracks())
        {
            result.addAll(getPoints(track));
        }
        return result;
    }


    /**
     *  Returns all points from the provided track, combining segments.
     *  <p>
     *  This discards all information other than latitude, longitude, and timestamp.
     */
    public List<TimestampedPoint> getPoints(Element track)
    {
        List<Element> points = xpathFact.newXPath("ns:trkseg/ns:trkpt").evaluate(track, Element.class);
        List<TimestampedPoint> result = new ArrayList<>(points.size());
        for (Element point : points)
        {
            double lat = Double.parseDouble(point.getAttribute("lat"));
            double lon = Double.parseDouble(point.getAttribute("lon"));
            String time = DomUtil.getChild(point, "time").getTextContent();     // TODO - gracefully handle missing timestamp
            result.add(new TimestampedPoint(time, lat, lon));
        }
        return result;
    }
}
