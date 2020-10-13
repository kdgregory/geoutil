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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Element;

import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.practicalxml.DomUtil;
import net.sf.practicalxml.builder.XmlBuilder;


public class TestDocument
{
    @Test
    public void testAccessors() throws Exception
    {
        // same tests as for Folder

        Document d = new Document();

        assertNull("getId() initial value",                                 d.getId());
        assertEquals("setId()",                     d,                      d.setId("argle"));
        assertEquals("getId()",                     "argle",                d.getId());

        Placemark p1 = new Placemark();
        Placemark p2 = new Placemark();

        assertEquals("getFeatures() initial size",  0,                      d.getFeatures().size());
        assertEquals("addFeature()",                d,                      d.addFeature(p1));
        assertEquals("getFeatures() 1",             Arrays.asList(p1),      d.getFeatures());
        assertEquals("setFeatures()",               d,                      d.setFeatures(Arrays.asList(p2, p1)));
        assertEquals("getFeatures() 2",             Arrays.asList(p2, p1),  d.getFeatures());

        d.setFeatures(null);
        assertEquals("null clears features",        0,                      d.getFeatures().size());

        ArrayList<Placemark> orig = new ArrayList<>(Arrays.asList(p1, p2));
        d.setFeatures(orig);
        List<Feature<?>> ret = d.getFeatures();

        try
        {
            ret.add(p1);
            fail("able to add feature to returned list");
        }
        catch (UnsupportedOperationException ex)
        {
            // success
        }
    }


    @Test
    public void testAppendAsXmlMinimal() throws Exception
    {
        Document d = new Document();

        Element parent = DomUtil.newDocument("irrelevant");
        Element child = d.appendAsXml(parent);

        assertEquals("added single child to existing parent",   1,                                  DomUtil.getChildren(parent).size());
        assertSame("returned child",                            child,                              DomUtil.getChildren(parent).get(0));
        assertEquals("child namespace",                         "http://www.opengis.net/kml/2.2",   child.getNamespaceURI());
        assertEquals("child name",                              "Document",                         child.getNodeName());

        assertEquals("number of data elements",                 0,                                  DomUtil.getChildren(child).size());
    }


    @Test
    public void testAppendAsXmlComplete() throws Exception
    {
        Placemark m1 = new Placemark().setName("first feature");
        Placemark m2 = new Placemark().setName("second feature");

        Document d = new Document()
                   .setId("some-unique-id")
                   .setName("folder name")  // this serves as a proxy for all Feature fields
                   .addFeature(m1)
                   .addFeature(m2);

        Element parent = DomUtil.newDocument("irrelevant");
        Element child = d.appendAsXml(parent);

        assertEquals("added single child to existing parent",   1,                                  DomUtil.getChildren(parent).size());
        assertSame("returned child",                            child,                              DomUtil.getChildren(parent).get(0));
        assertEquals("child namespace",                         "http://www.opengis.net/kml/2.2",   child.getNamespaceURI());
        assertEquals("child name",                              "Document",                         child.getNodeName());

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
        Element root = XmlBuilder.element("http://earth.google.com/kml/2.1", "Document")
                       .toDOM().getDocumentElement();

        Document d = Document.fromXml(root);

        assertNull("name",          d.getName());
        assertNull("visibility",    d.getVisibility());
        assertNull("description",   d.getDescription());
        assertTrue("features",      d.getFeatures().isEmpty());
    }


    @Test
    public void testFromXmlComplete() throws Exception
    {
        Element root = XmlBuilder.element("http://earth.google.com/kml/2.1", "Document",
                            XmlBuilder.attribute("id", "uniqueId"),
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "name",               XmlBuilder.text("my folder")),
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "visibility",         XmlBuilder.text("1")),
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "description",        XmlBuilder.text("its description")),
                            XmlBuilder.element("http://earth.google.com/kml/2.1", "Placemark",
                                XmlBuilder.element("http://earth.google.com/kml/2.1", "name",    XmlBuilder.text("contained feature"))))
                       .toDOM().getDocumentElement();

        Document d = Document.fromXml(root);

        assertEquals("id",                  "uniqueId",             d.getId());
        assertEquals("name",                "my folder",            d.getName());
        assertEquals("visibility",          Boolean.TRUE,           d.getVisibility());
        assertEquals("description",         "its description",      d.getDescription());
        assertEquals("feature count",       1,                      d.getFeatures().size());

        Placemark pm = (Placemark)d.getFeatures().get(0);

        assertEquals("placemark name",      "contained feature",    pm.getName());
    }

    // TODO - test alternate features


    @Test
    public void testFromXmlInvalidName() throws Exception
    {
        Element root = XmlBuilder.element("http://earth.google.com/kml/2.1", "SomethingElse")
                       .toDOM().getDocumentElement();

        try
        {
            Document.fromXml(root);
            fail("should not have parsed successfully");
        }
        catch (IllegalArgumentException ex)
        {
            assertTrue("exception message (was: " + ex.getMessage() + ")", ex.getMessage().contains("SomethingElse"));
        }
    }
}
