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

import java.io.File;
import java.util.Arrays;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.practicalxml.DomUtil;
import net.sf.practicalxml.builder.XmlBuilder;


public class TestGpxFile
{
    @Test
    public void testAccessors() throws Exception
    {
        GpxFile gpx = new GpxFile()
                      .setCreator("example");

        assertEquals("version is set by default",       "1.1",      gpx.getVersion());
        assertEquals("creator set explicitly",          "example",  gpx.getCreator());
        assertTrue("default to empty list of tracks",               gpx.getTracks().isEmpty());

        Track t1 = new Track();
        Track t2 = new Track();

        gpx.addTrack(t1);

        assertEquals("added a single track",            1,  gpx.getTracks().size());
        assertSame("returns same track",                t1, gpx.getTracks().get(0));

        gpx.setTracks(Arrays.asList(t1, t2));
        assertEquals("replaced list of tracks",         2,  gpx.getTracks().size());
        assertSame("tracks added in order provided",    t1, gpx.getTracks().get(0));
        assertSame("tracks added in order provided",    t2, gpx.getTracks().get(1));

        gpx.setTracks(null);
        assertTrue("setting null clears list", gpx.getTracks().isEmpty());
    }


    @Test
    public void testConstructFromXml() throws Exception
    {
        Document dom = XmlBuilder.element("http://www.topografix.com/GPX/1/1", "gpx",
                            XmlBuilder.attribute("version", "1.1"),
                            XmlBuilder.attribute("creator", "somebody"),
                            XmlBuilder.element("http://www.topografix.com/GPX/1/1", "trk",
                                XmlBuilder.element("http://www.topografix.com/GPX/1/1", "name",         XmlBuilder.text("empty")),
                                XmlBuilder.element("http://www.topografix.com/GPX/1/1", "desc",         XmlBuilder.text("an empty track"))))
                       .toDOM();

        GpxFile gpx = new GpxFile(dom);

        assertEquals("version",             "1.1",              gpx.getVersion());
        assertEquals("creator",             "somebody",         gpx.getCreator());
        assertEquals("number of tracks",    1,                  gpx.getTracks().size());
        assertEquals("track name",          "empty",            gpx.getTracks().get(0).getName());
        assertEquals("track description",   "an empty track",   gpx.getTracks().get(0).getDescription());
    }


    @Test
    public void testConvertToXml() throws Exception
    {
        // we only care about top-level conversion; we'll rely on nested objects doing their thing
        GpxFile gpx = new GpxFile()
                      .setCreator("somebody")
                      .addTrack(
                          new Track().setDescription("some track"));

        Document dom = gpx.toXml();
        Element eGpx = dom.getDocumentElement();

        assertEquals("root element namespace",  "http://www.topografix.com/GPX/1/1",    eGpx.getNamespaceURI());
        assertEquals("root element name",       "gpx",                                  eGpx.getNodeName());
        assertEquals("version",                 "1.1",                                  eGpx.getAttribute("version"));
        assertEquals("creator",                 "somebody",                             eGpx.getAttribute("creator"));
        assertEquals("number of children",      1,                                      DomUtil.getChildren(eGpx).size());

        assertEquals("child 1 namespace",       "http://www.topografix.com/GPX/1/1",    DomUtil.getChildren(eGpx).get(0).getNamespaceURI());
        assertEquals("child 1 name",            "trk",                                  DomUtil.getChildren(eGpx).get(0).getNodeName());
        assertEquals("child 1 description",     "some track",                           new Track(DomUtil.getChildren(eGpx).get(0)).getDescription());
    }


    @Test
    public void testWriteAndRead() throws Exception
    {
        File file = File.createTempFile(getClass().getSimpleName() + "-testWriteAndRead", ".xml");

        GpxFile orig = new GpxFile()
                      .setCreator("somebody")
                      .addTrack(
                          new Track().setDescription("some track"));
        orig.write(file);
        GpxFile rslt = new GpxFile(file);

        // one assertion should be sufficient to prove that we converted successfully
        assertEquals("child 1 description",     "some track",                           rslt.getTracks().get(0).getDescription());
    }

}
