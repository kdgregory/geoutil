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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sf.practicalxml.DomUtil;
import net.sf.practicalxml.OutputUtil;
import net.sf.practicalxml.ParseUtil;

/**
 *  Top-level clsss for reading and writing GPX files, as defined by
 *  https://www.topografix.com/GPX/1/1/.
 */
public class GpxFile
{
    private String version = "1.1";
    private String creator;
    private List<Track> tracks = new ArrayList<>();

    /**
     *  Base constructor: an empty file.
     */
    public GpxFile()
    {
        // nothing here
    }


    /**
     *  Constructs from an existing XML DOM representation.
     */
    public GpxFile(Document dom)
    {
        Element eGpx = dom.getDocumentElement();

        if (! GpxConstants.NAMESPACE.equals(eGpx.getNamespaceURI()))
        {
            throw new IllegalArgumentException("invalid root namespace: " + eGpx.getNamespaceURI());
        }

        version = eGpx.getAttribute(GpxConstants.A_GPX_VERSION);
        // TODO - verify version, throw if missing or not supported version

        creator = eGpx.getAttribute(GpxConstants.A_GPX_CREATOR);

        for (Element eTrack : DomUtil.getChildren(eGpx, GpxConstants.NAMESPACE, GpxConstants.E_TRK))
        {
            addTrack(new Track(eTrack));
        }
    }


    /**
     *  Constructs from an existing file.
     */
    public GpxFile(File file)
    {
        this(ParseUtil.parse(file));
    }

//----------------------------------------------------------------------------
//  Accessors
//----------------------------------------------------------------------------

    /**
     *  Returns the file's version (note: this is not settable).
     */
    public String getVersion()
    {
        return version;
    }


    /**
     *  Returns the file's creator. May be null.
     */
    public String getCreator()
    {
        return creator;
    }


    /**
     *  Sets the file's creator.
     */
    public GpxFile setCreator(String value)
    {
        creator = value;
        return this;
    }


    /**
     *  Returns all tracks in this file, as an unmodifiable list.
     */
    public List<Track> getTracks()
    {
        return Collections.unmodifiableList(tracks);
    }


    /**
     *  Sets the tracks in this file from a passed list. Passing null clears
     *  the current list.
     */
    public GpxFile setTracks(Collection<Track> value)
    {
        tracks.clear();
        if (value != null)
        {
            tracks.addAll(value);
        }
        return this;
    }


    /**
     *  Adds a track to this file.
     */
    public GpxFile addTrack(Track value)
    {
        tracks.add(value);
        return this;
    }

//----------------------------------------------------------------------------
//  Other public methods
//----------------------------------------------------------------------------

    /**
     *  Converts this object to its XML representation.
     */
    public Document toXml()
    {
        Element eGpx = DomUtil.newDocument(GpxConstants.NAMESPACE, GpxConstants.E_GPX);
        eGpx.setAttribute(GpxConstants.A_GPX_VERSION, getVersion());
        eGpx.setAttribute(GpxConstants.A_GPX_CREATOR, getCreator());
        for (Track track : tracks)
        {
            track.appendAsXml(eGpx);
        }
        return eGpx.getOwnerDocument();
    }


    /**
     *  Writes this object to the specified file, overwriting any existing
     *  content.
     */
    public void write(File file)
    {
        OutputUtil.compact(new DOMSource(toXml()), new StreamResult(file));
    }
}
