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

package com.kdgregory.geoutil.lib.shared;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 *  Functions that work with segments (aka poly-lines): lists of points.
 */
public class SegmentUtil
{
    /**
     *  Computes and sums the Pythagorean distance (in meters) between the points
     *  in a segment.
     */
    public static double pythagoreanDistance(List<? extends Point> segment)
    {
        if ((segment == null) || segment.isEmpty())
            return 0;

        Iterator<? extends Point> itx = segment.iterator();
        Point prev = itx.next();
        double sum = 0;
        while (itx.hasNext())
        {
            Point cur = itx.next();
            sum += PointUtil.pythagoreanDistance(prev, cur);
            prev = cur;
        }

        return sum;
    }


    /**
     *  Trims the past segment, removing points at the start and end that show movement
     *  that's less than a given threshold (using Pythagorean distance).
     *
     *  @param  segment The source segment.
     *  @param  minDist The minimum distance that indicates movement between two points.
     *
     *  @return A new list excluding points at the start and end that don't show movement.
     *          This list may be modified by the caller.
     */
    public static List<Point> trim(List<? extends Point> segment, double minDist)
    {
        ArrayList<Point> result = new ArrayList<>();
        if ((segment == null) || segment.isEmpty())
            return result;

        result.ensureCapacity(segment.size());

        Iterator<? extends Point> startItx = segment.iterator();
        Point prev = startItx.next();
        while ((prev != null) && startItx.hasNext())
        {
            Point cur = startItx.next();
            if (PointUtil.pythagoreanDistance(prev, cur) >= minDist)
            {
                result.add(prev);
                result.add(cur);
                break;
            }
            else
            {
                prev = cur;
            }
        }

        while (startItx.hasNext())
        {
            result.add(startItx.next());
        }

        // could do this with a ListIterator, but direct indexes are simplest

        int prevIdx = result.size() - 1;
        int curIdx = prevIdx - 1;
        while (curIdx > 0)
        {
            if (PointUtil.pythagoreanDistance(result.get(prevIdx), result.get(curIdx)) >= minDist)
                break;
            result.remove(prevIdx);
            prevIdx = curIdx;
            curIdx--;
        }

        return result;
    }


    /**
     *  Splits a segment into multiple segments based on a gap in timestamp.
     *  <p>
     *  Points without timestamp can not be tested, so are added to the existing split.
     *  However, the last known timestamp from the split is retained, so any subsequent
     *  points with timestamps greater than the gap can be tested.
     */
    public static List<List<Point>> split(List<? extends Point> segment, Duration minGap)
    {
        List<List<Point>> result = new ArrayList<>();
        if ((segment == null) || segment.isEmpty())
            return result;

        List<Point> split = new ArrayList<>();
        result.add(split);

        Instant prevTimestamp = null;
        for (Point cur : segment)
        {
            if ((prevTimestamp != null) && (cur.getTimestamp() != null))
            {
                Duration actual = Duration.between(prevTimestamp, cur.getTimestamp());
                if (actual.compareTo(minGap) >= 0)
                {
                    split = new ArrayList<>();
                    result.add(split);
                }
            }

            split.add(cur);
            prevTimestamp = (cur.getTimestamp() != null) ? cur.getTimestamp() : prevTimestamp;
        }
        return result;
    }


    /**
     *  Simplifies a segment by removing points that are less than a specified distance apart
     *  (using Pythagorean distance). The returned segment is a newly-created mutable list.
     */
    public static List<Point> simplify(List<? extends Point> segment, double minDistanceMeters)
    {
        if ((segment == null) || segment.isEmpty())
            return new ArrayList<Point>();

        List<Point> result = new ArrayList<Point>(segment.size());

        Iterator<? extends Point> itx = segment.iterator();
        Point prev = itx.next();
        result.add(prev);
        while (itx.hasNext())
        {
            Point cur = itx.next();
            if (PointUtil.pythagoreanDistance(prev, cur) > minDistanceMeters)
            {
                result.add(cur);
                prev = cur;
            }
        }

        return result;
    }


    /**
     *  Attempts to find two sections of the passed segments that align.
     *  <p>
     *  This process starts by finding the first points in the two lists that are
     *  no more than <code>maxSeparation</code> apart. That point in the first list
     *  is then used to find the closest subsequent point in the second list (which
     *  may be the one we've already found). These two points become the first pair
     *  returned.
     *  <p>
     *  The first list is then iterated to find a point at least <code>minSeparation</code>
     *  from the first, and this is then matched to the second list.
     *  <p>
     *  This repeats until one of the lists has no more elements, or until the
     *  distance between the first and second lists exceeds <code>maxDivergence</code>.
     */
    public static List<Point[]> align(List<? extends Point> s1, List<? extends Point> s2, double minIncrement, double maxSeparation)
    {
        List<Point[]> result = new ArrayList<Point[]>();

        AlignHelper itx1 = new AlignHelper(s1);
        AlignHelper itx2 = new AlignHelper(s2);

        // these points are outside the loop so that their previous values can be used inside
        Point p1 = null;
        Point p2 = null;
        while (itx1.hasNext() && itx2.hasNext())
        {
            p1 = itx1.next(p1, minIncrement);

            // no more points in the segment that obey separation rule
            if (p1 == null)
                return result;

            p2 = itx2.findMatchingPoint(p1, maxSeparation);
            if (p2 != null)
            {
                result.add(new Point[] {p1, p2});
            }

            // this is the end of the line ... unless we haven't started a line!
            if (result.isEmpty())
            {
                itx2.returnToMark();
            }
        }

        return result;
    }

//----------------------------------------------------------------------------
//  Internals
//----------------------------------------------------------------------------

    /**
     *  This class holds the segments for matching. It behaves like an iterator,
     *  but adds functionality to seek and backtrack.
     */
    private static class AlignHelper
    implements Iterator<Point>
    {
        private ArrayList<? extends Point> segment;
        private int idx;
        private int mark;

        public AlignHelper(List<? extends Point> segment)
        {
            // null segments are converted to empty segments -- no null check in parent
            this.segment = (segment != null)
                         ? new ArrayList<Point>(segment)
                         : new ArrayList<Point>();
        }


        /**
         *  Returns <code>true</code> if there are any more points in the segment.
         */
        @Override
        public boolean hasNext()
        {
            return idx < segment.size();
        }


        /**
         *  Returns the next point in the segment, <code>null</code> if there aren't
         *  any more (note that this differs from a normal iterator, which throws).
         */
        @Override
        public Point next()
        {
            return hasNext()
                 ? segment.get(idx++)
                 : null;
        }


        /**
         *  Returns the next point in the segment that's at least <code>minSeparation</code>
         *  from the provided point, <code>null</code> if there isn't one.
         */
        public Point next(Point p, double minSeparation)
        {
            // this will be true when called at the start of a segment
            if (p == null)
                return next();

            while (hasNext())
            {
                Point x = next();
                if (PointUtil.pythagoreanDistance(p, x) >= minSeparation)
                    return x;
            }

            return null;
        }


        /**
         *  Marks the current iterator location. This is used when exploring the point space.
         */
        public void mark()
        {
            mark = idx;
        }


        /**
         *  Resets the iterator to its mark. This is used when exploring the point space.
         */
        public void returnToMark()
        {
            idx = mark;
        }


        /**
         *  Finds the next point in this segment that's closest to the given point, but
         *  less than <code>maxSeparation</code> apart.
         */
        public Point findMatchingPoint(Point p, double maxSeparation)
        {
            while (hasNext())
            {
                Point x = next();
                double dx = PointUtil.pythagoreanDistance(p, x);
                if (dx < maxSeparation)
                {
                    mark();
                    while (hasNext())
                    {
                        Point y = next();
                        double dy = PointUtil.pythagoreanDistance(p, y);
                        if (dy > dx)
                        {
                            returnToMark();
                            return x;
                        }
                        else
                        {
                            x = y;
                            dx = dy;
                            mark();
                        }
                    }
                    return x;
                }
            }
            return null;
        }
    }
}
