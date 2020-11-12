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

package com.kdgregory.geoutil.lib.kml;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Element;

import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.practicalxml.DomUtil;
import net.sf.practicalxml.builder.XmlBuilder;

import com.kdgregory.geoutil.lib.internal.XmlUtils;


public class TestKmlFile
{
    @Test
    public void testAccessors() throws Exception
    {
        KmlFile file = new KmlFile();

        Placemark p1 = new Placemark();
        Placemark p2 = new Placemark();

        assertEquals("getFeatures() initial size",  0,                      file.getFeatures().size());
        assertEquals("addFeature()",                file,                   file.addFeature(p1));
        assertEquals("getFeatures() 1",             Arrays.asList(p1),      file.getFeatures());
        assertEquals("setFeatures()",               file,                   file.setFeatures(Arrays.asList(p2, p1)));
        assertEquals("getFeatures() 2",             Arrays.asList(p2, p1),  file.getFeatures());
    }


    @Test
    public void testToXml() throws Exception
    {
        KmlFile file = new KmlFile()
                       .addFeature(new Folder()
                                   .setDescription("some descriptive text"));

        Element root = file.toXml().getDocumentElement();

        assertEquals("root namespace",                         "http://www.opengis.net/kml/2.2",    root.getNamespaceURI());
        assertEquals("root name",                              "kml",                               root.getNodeName());

        List<Element> children = DomUtil.getChildren(root);

        assertEquals("root hase one child",                    1,                                   children.size());

        Element child = children.get(0);

        assertEquals("child namespace",                        "http://www.opengis.net/kml/2.2",    child.getNamespaceURI());
        assertEquals("child name",                             "Folder",                            child.getNodeName());

        // the folder's description will serve as a test that we're properly invoking child conversions

        assertEquals("folder description",                      "some descriptive text",            XmlUtils.getChildText(child, "description"));
    }


    @Test
    public void testFromXml() throws Exception
    {
        // note that this is not the namespace that we used to write the file
        Element root = XmlBuilder.element("http://earth.google.com/kml/2.1", "kml",
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "Folder",
                                XmlBuilder.element("http://earth.google.com/kml/2.1", "description", XmlBuilder.text("folder description"))),
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "Document",
                                XmlBuilder.element("http://earth.google.com/kml/2.1", "description", XmlBuilder.text("document description"))))
                       .toDOM().getDocumentElement();

        KmlFile file = KmlFile.fromXml(root.getOwnerDocument());

        List<Feature<?>> features = file.getFeatures();

        assertEquals("number of features",          2,                          features.size());
        assertEquals("first feature type",          Folder.class,               features.get(0).getClass());
        assertEquals("first feature attribute",     "folder description",       features.get(0).getDescription());
        assertEquals("second feature type",         Document.class,             features.get(1).getClass());
        assertEquals("second feature attribute",    "document description",     features.get(1).getDescription());
    }


    @Test
    public void testWriteAndRead() throws Exception
    {
        KmlFile orig = new KmlFile()
                       .addFeature(new Folder()
                                   .setDescription("some descriptive text"));

        File file = File.createTempFile(getClass().getSimpleName() + "-testWriteAndRead", ".xml");

        orig.write(file);
        KmlFile rslt = KmlFile.parse(file);

        // if we can dig into the file, we'll declare success

        assertEquals("successful parse", orig.getFeatures().get(0).getDescription(),
                                         rslt.getFeatures().get(0).getDescription());
    }

}
