// Copyright (c) Keith D Gregory
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.kdgregory.geoutil.util.gpx;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.kdgcommons.lang.StringUtil;
import net.sf.practicalxml.DomUtil;
import net.sf.practicalxml.OutputUtil;
import net.sf.practicalxml.ParseUtil;
import net.sf.practicalxml.xpath.XPathWrapper;

/**
 *  Removes all tracks from a GPX file other than those belonging to a
 *  specific date (used to cleanup a Garmin GPX file when the track log
 *  wasn't reset).
 *  <p>
 *  Invocation:
 *
 *      GPXCleanup FILENAME DESIRED_DATE NAME
 */
public class GPXCleanup
{
    private static Logger logger = LoggerFactory.getLogger(GPXCleanup.class);


    public static void main(String[] argv)
    throws Exception
    {
        File file = new File(argv[0]);
        String desiredDate = argv[1];
        String trackName = argv[2];

        Document dom = ParseUtil.parse(file);

        deletePointsWithUndesiredDate(dom, desiredDate);
        // TODO - delete points at end of track that don't show movement
        deleteEmptyNodes(dom, "//ns:trkseg", "ns:trkpt", "segment");
        mergeTracks(dom);
        deleteEmptyNodes(dom, "//ns:trk", "ns:trkseg", "track");
        updateTrackTitle(dom, trackName);

        try (FileOutputStream out = new FileOutputStream(file))
        {
            OutputUtil.compactStream(dom, out);
        }
    }


    private static XPathWrapper xpath(String path)
    {
         return new XPathWrapper(path)
                .bindNamespace("ns", "http://www.topografix.com/GPX/1/1");
    }


    private static void deletePointsWithUndesiredDate(Document dom, String desiredDate)
    {
        int nodesDeleted = 0;

        List<Node> timeNodes = xpath("//ns:trkseg/ns:trkpt/ns:time").evaluate(dom);
        logger.info("examining {} timestamp nodes", timeNodes.size());

        for (Node node : timeNodes)
        {
            String timestamp = DomUtil.getText((Element)node);
            if (! StringUtil.isBlank(timestamp) && timestamp.startsWith(desiredDate))
                continue;

            Node trkptNode = node.getParentNode();
            Node segmentNode = trkptNode.getParentNode();
            segmentNode.removeChild(trkptNode);
            nodesDeleted++;
        }
        logger.info("deleted {} timestamp nodes", nodesDeleted);
    }


    private static void mergeTracks(Document dom)
    {
        List<Node> trackNodes = xpath("//ns:trk").evaluate(dom);
        if (trackNodes.size() < 2)
            return;

        logger.info("merging {} tracks", trackNodes.size());
        Node firstTrack = trackNodes.get(0);
        trackNodes.remove(0);

        for (Node trackNode : trackNodes)
        {
            List<Node> segmentNodes = xpath("ns:trkseg").evaluate(trackNode);
            for (Node segmentNode : segmentNodes)
            {
                firstTrack.appendChild(segmentNode);
            }
        }
    }


    private static void deleteEmptyNodes(Document dom, String nodeSelector, String childSelector, String type)
    {
        int nodesDeleted = 0;

        List<Node> nodes = xpath(nodeSelector).evaluate(dom);
        logger.info("examining {} {} nodes", nodes.size(), type);

        for (Node node : nodes)
        {
            List<Node> childNodes = xpath(childSelector).evaluate(node);
            if (childNodes.size() > 0)
                continue;

            Node parent = node.getParentNode();
            parent.removeChild(node);
            nodesDeleted++;
        }
        logger.info("deleted {} empty {}s", nodesDeleted, type);
    }


    private static void updateTrackTitle(Document dom, String trackName)
    {
        logger.info("setting track name to \"{}\"", trackName);
        Element nameNode = xpath("//ns:trk[1]/ns:name").evaluateAsElement(dom);
        DomUtil.setText(nameNode, trackName);
    }
}
