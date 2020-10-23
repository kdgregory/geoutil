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

import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.practicalxml.DomUtil;
import net.sf.practicalxml.builder.XmlBuilder;


public class TestFolder
{
    @Test
    public void testAccessors() throws Exception
    {
        Folder f = new Folder();

        // this just tests accessors defined by Object and Container; those defined by
        // Features are tested in the XML conversion

        assertNull("getId() initial value",                                 f.getId());
        assertEquals("setId()",                     f,                      f.setId("argle"));
        assertEquals("getId()",                     "argle",                f.getId());

        Placemark p1 = new Placemark();
        Placemark p2 = new Placemark();

        assertEquals("getFeatures() initial size",  0,                      f.getFeatures().size());
        assertEquals("addFeature()",                f,                      f.addFeature(p1));
        assertEquals("getFeatures() 1",             Arrays.asList(p1),      f.getFeatures());
        assertEquals("setFeatures()",               f,                      f.setFeatures(Arrays.asList(p2, p1)));
        assertEquals("getFeatures() 2",             Arrays.asList(p2, p1),  f.getFeatures());

        f.setFeatures(null);
        assertEquals("null clears features",        0,                      f.getFeatures().size());
    }


    @Test
    public void testFindByName() throws Exception
    {
        Placemark pm1 = new Placemark().setName("pm1");
        Placemark pm2 = new Placemark().setName("pm2");
        Placemark pm3a = new Placemark().setName("pm3");
        Placemark pm3b = new Placemark().setName("pm3");

        Folder root = new Folder()
                      .addFeature(pm1)
                      .addFeature(pm3a)
                      .addFeature(new Folder()
                                  .addFeature(pm2)
                                  .addFeature(pm3b));

        assertEquals("top level",   Arrays.asList(pm1),         root.findByName("pm1"));
        assertEquals("nested",      Arrays.asList(pm2),         root.findByName("pm2"));
        assertEquals("split",       Arrays.asList(pm3a, pm3b),  root.findByName("pm3"));
    }


    @Test
    public void testFindByType() throws Exception
    {
        Placemark pm1 = new Placemark().setName("pm1");
        Placemark pm2 = new Placemark().setName("pm2");
        Placemark pm3a = new Placemark().setName("pm3");
        Placemark pm3b = new Placemark().setName("pm3");

        Folder nested = new Folder()
                        .addFeature(pm2)
                        .addFeature(pm3b);

        Folder root = new Folder()
                      .addFeature(pm1)
                      .addFeature(pm3a)
                      .addFeature(nested);

        assertEquals("Folder",      Arrays.asList(nested),                  root.findByType(Folder.class));
        assertEquals("Placemark",   Arrays.asList(pm1, pm3a, pm2, pm3b),    root.findByType(Placemark.class));
    }


    @Test
    public void testAppendAsXmlMinimal() throws Exception
    {
        Folder f = new Folder();

        Element parent = DomUtil.newDocument("irrelevant");
        Element child = f.appendAsXml(parent);

        assertEquals("added single child to existing parent",   1,                                  DomUtil.getChildren(parent).size());
        assertSame("returned child",                            child,                              DomUtil.getChildren(parent).get(0));
        assertEquals("child namespace",                         "http://www.opengis.net/kml/2.2",   child.getNamespaceURI());
        assertEquals("child name",                              "Folder",                           child.getNodeName());

        assertEquals("number of data elements",                 0,                                  DomUtil.getChildren(child).size());
    }


    @Test
    public void testAppendAsXmlComplete() throws Exception
    {
        Placemark m1 = new Placemark().setName("first feature");
        Placemark m2 = new Placemark().setName("second feature");

        Folder f = new Folder()
                   .setId("some-unique-id")
                   .setName("folder name")  // this serves as a proxy for all Feature fields
                   .addFeature(m1)
                   .addFeature(m2);

        Element parent = DomUtil.newDocument("irrelevant");
        Element child = f.appendAsXml(parent);

        assertEquals("added single child to existing parent",   1,                                  DomUtil.getChildren(parent).size());
        assertSame("returned child",                            child,                              DomUtil.getChildren(parent).get(0));
        assertEquals("child namespace",                         "http://www.opengis.net/kml/2.2",   child.getNamespaceURI());
        assertEquals("child name",                              "Folder",                           child.getNodeName());

        // we care about order, so will retrieve all children and access via index
        List<Element> dataElements = DomUtil.getChildren(child);

        assertEquals("number of data elements",     3,                                  dataElements.size());

        assertEquals("name namespace",              "http://www.opengis.net/kml/2.2",   dataElements.get(0).getNamespaceURI());
        assertEquals("name name",                   "name",                             dataElements.get(0).getNodeName());
        assertEquals("name value",                  "folder name",                      dataElements.get(0).getTextContent());

        assertEquals("feature 1 namespace",         "http://www.opengis.net/kml/2.2",   dataElements.get(1).getNamespaceURI());
        assertEquals("feature 1 name",              "Placemark",                        dataElements.get(1).getNodeName());

        assertEquals("feature 2 namespace",         "http://www.opengis.net/kml/2.2",   dataElements.get(2).getNamespaceURI());
        assertEquals("feature 2 name",              "Placemark",                        dataElements.get(2).getNodeName());

        // verify the feature contents by converting them, using name to assert correct order

        Feature<?> f1 = Placemark.fromXml(dataElements.get(1));
        Feature<?> f2 = Placemark.fromXml(dataElements.get(2));

        assertEquals("feature 1",                   "first feature",                    f1.getName());
        assertEquals("feature 2",                   "second feature",                   f2.getName());
    }

    // TODO - test conversion to XML with a different geometry


    @Test
    public void testFromXmlMinimal() throws Exception
    {
        Document dom = XmlBuilder.element("http://earth.google.com/kml/2.1", "Folder")
                       .toDOM();

        Folder f = Folder.fromXml(dom.getDocumentElement());

        assertNull("name",          f.getName());
        assertNull("visibility",    f.getVisibility());
        assertNull("description",   f.getDescription());
        assertTrue("features",      f.getFeatures().isEmpty());
    }


    @Test
    public void testFromXmlComplete() throws Exception
    {
        // TODO - addition feature types
        Document dom = XmlBuilder.element("http://earth.google.com/kml/2.1", "Folder",
                            XmlBuilder.attribute("id", "uniqueId"),
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "name",               XmlBuilder.text("my folder")),
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "visibility",         XmlBuilder.text("1")),
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "description",        XmlBuilder.text("its description")),
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "Placemark",
                                XmlBuilder.element("http://earth.google.com/kml/2.1", "name",    XmlBuilder.text("contained feature"))))
                       .toDOM();

        Folder f = Folder.fromXml(dom.getDocumentElement());

        assertEquals("id",                  "uniqueId",             f.getId());
        assertEquals("name",                "my folder",            f.getName());
        assertEquals("visibility",          Boolean.TRUE,           f.getVisibility());
        assertEquals("description",         "its description",      f.getDescription());
        assertEquals("feature count",       1,                      f.getFeatures().size());

        Placemark pm = (Placemark)f.getFeatures().get(0);

        assertEquals("placemark name",      "contained feature",    pm.getName());
    }


    @Test
    public void testFromXmlNested() throws Exception
    {
        Document dom = XmlBuilder.element("http://earth.google.com/kml/2.1", "Folder",
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "name",               XmlBuilder.text("top-level")),
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "Folder",
                                XmlBuilder.element("http://earth.google.com/kml/2.1", "name",           XmlBuilder.text("nested")),
                                XmlBuilder.element("http://earth.google.com/kml/2.1", "Placemark",
                                    XmlBuilder.element("http://earth.google.com/kml/2.1", "name",       XmlBuilder.text("contained feature")))))
                       .toDOM();

        Folder tl = Folder.fromXml(dom.getDocumentElement());

        assertEquals("top-level name",                  "top-level",            tl.getName());
        assertEquals("top-level feature count",         1,                      tl.getFeatures().size());

        Folder nf = (Folder)tl.getFeatures().get(0);

        assertEquals("nested name",                     "nested",               nf.getName());
        assertEquals("nested feature count",            1,                      nf.getFeatures().size());

        Placemark pm = (Placemark)nf.getFeatures().get(0);

        assertEquals("placemark name",                  "contained feature",    pm.getName());
    }



    @Test
    public void testFromXmlInvalidName() throws Exception
    {
        Document dom = XmlBuilder.element("http://earth.google.com/kml/2.1", "SomethingElse").toDOM();

        try
        {
            Folder.fromXml(dom.getDocumentElement());
            fail("should not have parsed successfully");
        }
        catch (IllegalArgumentException ex)
        {
            assertTrue("exception message (was: " + ex.getMessage() + ")", ex.getMessage().contains("SomethingElse"));
        }
    }
}
