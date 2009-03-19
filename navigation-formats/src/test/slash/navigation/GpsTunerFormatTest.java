/*
    This file is part of RouteConverter.

    RouteConverter is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    RouteConverter is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with RouteConverter; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

    Copyright (C) 2007 Christian Pesch. All Rights Reserved.
*/

package slash.navigation;

import java.text.DateFormat;
import java.util.Calendar;

public class GpsTunerFormatTest extends NavigationTestCase {
    GpsTunerFormat format = new GpsTunerFormat();

    public void testIsValidLine() {
        assertTrue(format.isValidLine("50.3965966666667;7.53247333333333;74.4000015258789;77.56176;1172932595;1;279"));
        assertTrue(format.isValidLine(" 50.3965966666667 ; 7.53247333333333 ; 74.4000015258789 ; 77.56176 ; 1172932595 ; 1 ; 279 "));
        assertTrue(format.isValidLine("GPS Tracklog - Generated by GPS Tuner 5.0"));
        assertTrue(format.isValidLine("Latitude(Degree);Longitude(Degree);Altitude(m);Speed(kmph);Date(Unix TimeStamp);Segment;Heading(Degree)"));
    }

    public void testIsPosition() {
        assertTrue(format.isPosition("50.3965966666667;7.53247333333333;74.4000015258789;77.56176;1172932595;1;279"));

        assertFalse(format.isPosition("GPS Tracklog - Generated by GPS Tuner 5.0"));
        assertFalse(format.isPosition("Latitude(Degree);Longitude(Degree);Altitude(m);Speed(kmph);Date(Unix TimeStamp);Segment;Heading(Degree)"));
    }

    public void testParsePosition() {
        Wgs84Position position = format.parsePosition("50.3965966666667;7.53247333333333;74.4000015258789;77.56176;1172932595;1;279", null);
        assertEquals(7.53247333333333, position.getLongitude());
        assertEquals(50.3965966666667, position.getLatitude());
        assertEquals(74.4000015258789, position.getElevation());
        String actual = DateFormat.getDateTimeInstance().format(position.getTime().getTime());
        Calendar expectedCal = calendar(1172932595000L);
        expectedCal.setLenient(true);
        String expected = DateFormat.getDateTimeInstance().format(expectedCal.getTime());
        assertEquals(expected,  actual);
        assertEquals(expectedCal, position.getTime());
        assertNull(position.getComment());
    }

    public void testParseNegativePosition() {
        Wgs84Position position = format.parsePosition("-50.3965966666667;-7.53247333333333;-74.4000015258789;77.56176;1172932595;1;279", null);
        assertEquals(-7.53247333333333, position.getLongitude());
        assertEquals(-50.3965966666667, position.getLatitude());
        assertEquals(-74.4000015258789, position.getElevation());
    }
}
