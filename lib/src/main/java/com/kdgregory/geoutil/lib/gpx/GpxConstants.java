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


/**
 *  Defines strings holding the GPX namespace and element names, to avoid typos.
 */
public class GpxConstants
{
    /** The GPX namespace */
    public final static String NAMESPACE    = "http://www.topografix.com/GPX/1/1";


    /** The top-level GPX element */
    public final static String E_GPX                    = "gpx";

    /** gpxType attribute: version */
    public final static String  A_GPX_VERSION           = "version";

    /** gpxType attribute: creator */
    public final static String  A_GPX_CREATOR           = "creator";


    /** container: track */
    public final static String  E_TRK                   = "trk";

    /** trkType data field: name */
    public final static String  E_TRK_NAME              = "name";

    /** trkType data field: name */
    public final static String  E_TRK_DESCRIPTION       = "desc";

    /** trkType data field: type */
    public final static String  E_TRK_TYPE              = "type";


    /** container: track segment */
    public final static String  E_TRKSEG                = "trkseg";


    /** container: trackpoint */
    public final static String  E_TRKPOINT              = "trkpt";

    /** wtpType attribute: latitude */
    public final static String A_WPT_LAT                = "lat";

    /** wptType attribute: longitude */
    public final static String A_WPT_LON                = "lon";

    /** wptType data field: elevation */
    public final static String E_WPT_ELEVATION          = "ele";

    /** wptType data field: timestamp */
    public final static String E_WPT_TIMESTAMP          = "time";

    /** wptType data field: magnetic variance */
    public final static String E_WPT_VARIANCE           = "magvar";

    /** wptType data field: geoid height */
    public final static String E_WPT_GEOID_HEIGHT       = "geoidheight";

    /** wptType data field: name */
    public final static String E_WPT_NAME               = "name";

    /** wptType data field: comment */
    public final static String E_WPT_COMMENT            = "cmt";

    /** wptType data field: description */
    public final static String E_WPT_DESCRIPTION        = "desc";

    /** wptType data field: extensions */
    public final static String E_WPT_EXTENSIONS         = "extensions";
}
