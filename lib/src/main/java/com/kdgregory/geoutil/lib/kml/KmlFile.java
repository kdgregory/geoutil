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

import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Element;

import net.sf.practicalxml.DomUtil;
import net.sf.practicalxml.OutputUtil;
import net.sf.practicalxml.ParseUtil;

import com.kdgregory.geoutil.lib.kml.model.Container;
import com.kdgregory.geoutil.lib.kml.model.Document;
import com.kdgregory.geoutil.lib.kml.model.Feature;
import com.kdgregory.geoutil.lib.kml.model.Folder;


/**
 *  Top-level clsss for reading and writing KML files. Although this is not part
 *  the KML model, it behaves like a Container so I have chosen to implement it
 *  as one.
 */
public class KmlFile
extends Container<KmlFile>
{
    /**
     *  Base constructor: an empty file.
     */
    public KmlFile()
    {
        // nothing here
    }

//----------------------------------------------------------------------------
//  Other public methods
//----------------------------------------------------------------------------

    /**
     *  Creates an instance from a file representation.
     */
    public static KmlFile parse(File file)
    {
        return KmlFile.fromXml(ParseUtil.parse(file));
    }


    /**
     *  Creates a new instance from an XML DOM.
     */
    public static KmlFile fromXml(org.w3c.dom.Document dom)
    {
        Element root = dom.getDocumentElement();

        if (! KmlConstants.E_ROOT.equals(DomUtil.getLocalName(root)))
        {
            throw new IllegalArgumentException("invalid root element name: " + DomUtil.getLocalName(root));
        }

        KmlFile file = new KmlFile();

        for (Element child : DomUtil.getChildren(root))
        {
            String childName = DomUtil.getLocalName(child);
            switch (childName)
            {
                case KmlConstants.E_FOLDER:
                    file.addFeature(Folder.fromXml(child));
                    break;

                case KmlConstants.E_DOCUMENT:
                    file.addFeature(Document.fromXml(child));
                    break;

                default :
                    throw new IllegalArgumentException("unrecognized child element: " + childName);
            }
        }

        return file;
    }


    /**
     *  Converts this object to its XML representation.
     */
    public org.w3c.dom.Document toXml()
    {
        Element root = DomUtil.newDocument(KmlConstants.NAMESPACE, KmlConstants.E_ROOT);
        for (Feature<?> feature : getFeatures())
        {
            feature.appendAsXml(root);
        }
        return root.getOwnerDocument();
    }


    /**
     *  Writes this object to the specified file, overwriting any existing
     *  content.
     */
    public void write(File file)
    {
        OutputUtil.compact(new DOMSource(toXml()), new StreamResult(file));
    }

//----------------------------------------------------------------------------
//  Internals
//----------------------------------------------------------------------------

    @Override
    public Element appendAsXml(Element parent)
    {
        throw new UnsupportedOperationException("KmlFile is the root of the DOM");
    }
}
