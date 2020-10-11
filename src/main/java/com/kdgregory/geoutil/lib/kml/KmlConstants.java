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


    /** Folder element name. */
    public final static String  E_FOLDER                = "Folder";
    
    /** Folder attribute: ID. */
    public final static String  A_FOLDER_ID             = "id";


    /** Placemark element name. */
    public final static String  E_PLACEMARK             = "Placemark";


    /** Feature child element: name. */
    public final static String  E_FEATURE_NAME          = "name";

    /** Feature child element: visibility. */
    public final static String  E_FEATURE_VISIBILITY    = "visibility";

    /** Feature child element: description. */
    public final static String  E_FEATURE_DESCRIPTION   = "description";


    /** Point element name. */
    public final static String  E_POINT                 = "Point";

    /** Point child element: extrude. */
    public final static String  E_POINT_EXTRUDE         = "extrude";

    /** Point child element: altitudeMode. */
    public final static String  E_POINT_ALTMODE         = "altitudeMode";

    /** Point child element: coordinates. */
    public final static String  E_POINT_COORD           = "coordinates";

}
