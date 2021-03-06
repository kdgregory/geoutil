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


/**
 *  String constants used to build KML documents.
 */
public class KmlConstants
{
    /**
     *  The namespace used to write XML content.
     *  <p>
     *  Note that files written by Google Earth use the namespace "http://earth.google.com/kml/2.1",
     *  and the file format is compatible with files using the namespace "http://www.opengis.net/kml/2.2".
     */
    public final static String  NAMESPACE               = "http://www.opengis.net/kml/2.2";


    /** Object attribute: ID. */
    public final static String  A_OBJECT_ID             = "id";


    /** Root element name. */
    public final static String  E_ROOT                  = "kml";


    /** Document element name. */
    public final static String  E_DOCUMENT              = "Document";


    /** Folder element name. */
    public final static String  E_FOLDER                = "Folder";


    /** Placemark element name. */
    public final static String  E_PLACEMARK             = "Placemark";


    /** Feature child element: name. */
    public final static String  E_FEATURE_NAME          = "name";

    /** Feature child element: visibility. */
    public final static String  E_FEATURE_VISIBILITY    = "visibility";

    /** Feature child element: description. */
    public final static String  E_FEATURE_DESCRIPTION   = "description";

    /** Feature child element: style reference. */
    public final static String  E_FEATURE_STYLEREF      = "styleUrl";


    /** Point element name. */
    public final static String  E_POINT                 = "Point";

    /** Linestring element name. */
    public final static String  E_LINESTRING            = "LineString";


    /** Common geomery element: altitudeMode. */
    public final static String  E_GEOMETRY_ALTMODE      = "altitudeMode";

    /** Common geomery element: coordinates. */
    public final static String  E_GEOMETRY_COORD        = "coordinates";

    /** Common geomery element: extrude. */
    public final static String  E_GEOMETRY_EXTRUDE      = "extrude";

    /** Common geomery element: tessellate. */
    public final static String  E_GEOMETRY_TESSELLATE   = "tessellate";


    /** Timestamp element name. */
    public final static String  E_TIMESTAMP             = "TimeStamp";

    /** Timestamp child element: when. */
    public final static String  E_TIMESTAMP_WHEN        = "when";


    /** Timespan element name. */
    public final static String  E_TIMESPAN              = "TimeSpan";

    /** Timespan child element: begin. */
    public final static String  E_TIMESPAN_BEGIN        = "begin";

    /** Timespan child element: end. */
    public final static String  E_TIMESPAN_END          = "end";


    /** Style element name. */
    public final static String  E_STYLE                 = "Style";

    /** IconStyle element name. */
    public final static String  E_ICONSTYLE             = "IconStyle";

    /** LabelStyle element name. */
    public final static String  E_LABELSTYLE            = "LabelStyle";

    /** LineStyle element name. */
    public final static String  E_LINESTYLE             = "LineStyle";

    /** PolyStyle element name. */
    public final static String  E_POLYSTYLE             = "PolyStyle";

    /** BalloonStyle element name. */
    public final static String  E_BALLOONSTYLE          = "BalloonStyle";

    /** ListStyle element name. */
    public final static String  E_LISTSTYLE             = "ListStyle";


    /** Shared style child elemnt: color */
    public final static String  E_COLORSTYLE_COLOR      = "color";

    /** Shared style child elemnt: colorMode */
    public final static String  E_COLORSTYLE_MODE       = "colorMode";


    /** IconStyle child element: scale */
    public final static String  E_ICONSTYLE_SCALE       = "scale";

    /** IconStyle child element: heading */
    public final static String  E_ICONSTYLE_HEADING     = "heading";

    /** IconStyle child element: Icon */
    public final static String  E_ICONSTYLE_ICON        = "Icon";

    /** IconStyle child element: Icon reference */
    public final static String  E_ICONSTYLE_ICON_HREF   = "href";


    /** LineStyle child elemnt: width */
    public final static String  E_LINESTYLE_WIDTH       = "width";
}
