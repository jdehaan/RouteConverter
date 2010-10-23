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

package slash.navigation.gopal;

import slash.common.io.CompactCalendar;
import slash.common.io.Transfer;
import slash.navigation.base.BaseNavigationPosition;
import slash.navigation.base.RouteCharacteristics;
import slash.navigation.gopal.binding5.ObjectFactory;
import slash.navigation.gopal.binding5.Tour;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * Reads and writes GoPal Route 5 (.xml) files.
 *
 * @author Christian Pesch
 */

public class GoPal5RouteFormat extends GoPalRouteFormat<GoPal5Route> {
    private static final Preferences preferences = Preferences.userNodeForPackage(GoPal5RouteFormat.class);
    private static final String ROUTE_OPTIONS_SPEED_UNIT = "km_h";
    private static final String VERSION_PREFIX = "v5";

    public String getName() {
        return "GoPal Route 5 (*" + getExtension() + ")";
    }

    @SuppressWarnings("unchecked")
    public <P extends BaseNavigationPosition> GoPal5Route createRoute(RouteCharacteristics characteristics, String name, List<P> positions) {
        return new GoPal5Route(name, (List<GoPalPosition>) positions);
    }

    private GoPal5Route process(Tour tour) {
        List<GoPalPosition> positions = new ArrayList<GoPalPosition>();
        Tour.Start start = tour.getStart();
        if(start != null) {
            Short country = start.getCountry() != null ? start.getCountry().getCode() : null;
            String state = start.getState() != null ? start.getState().getName() : null;
            String city = start.getCity() != null ? start.getCity().getName() : null;
            if(state != null) {
                city = city != null ? state + " " + city : state;
            }
            positions.add(new GoPalPosition(start.getCoordinates().getMercatorx(), start.getCoordinates().getMercatory(),
                    country, null, city, null, null));
        }
        for (Tour.Destination destination : tour.getDestination()) {
            Short country = destination.getCountry() != null ? destination.getCountry().getCode() : null;
            String zip = destination.getZip() != null && destination.getZip().getCode() != 0 ?
                    Integer.toString(destination.getZip().getCode()) : null;
            String city = destination.getCity() != null ? destination.getCity().getName() : null;
            String street = destination.getStreet() != null ? destination.getStreet().getName() : null;
            Short houseNumber = destination.getHouseNumber() != null && destination.getHouseNumber().getValue() != 0 ?
                    destination.getHouseNumber().getValue() : null;
            positions.add(new GoPalPosition(destination.getCoordinates().getMercatorx(), destination.getCoordinates().getMercatory(),
                    country, zip, city, street, houseNumber));
        }
        return new GoPal5Route(null, tour.getRouteOptions(), positions);
    }

    public List<GoPal5Route> read(InputStream source, CompactCalendar startDate) throws IOException {
        try {
            Tour tour = GoPalUtil.unmarshal5(source);
            return Arrays.asList(process(tour));
        } catch (JAXBException e) {
            return null;
        }
    }

    private Tour.RouteOptions createRouteOptions(GoPal5Route route) {
        Tour.RouteOptions options = route.getOptions();
        if (options == null) {
            ObjectFactory objectFactory = new ObjectFactory();
            options = objectFactory.createTourRouteOptions();

            Tour.RouteOptions.NaviMode naviMode = objectFactory.createTourRouteOptionsNaviMode();
            naviMode.setValue(preferences.get(VERSION_PREFIX + "naviMode", "motorbike")); // bicycle, car, motorbike, pedestrian
            options.setNaviMode(naviMode);
            Tour.RouteOptions.OptimizationMode optimizationMode = objectFactory.createTourRouteOptionsOptimizationMode();
            optimizationMode.setValue(preferences.get(VERSION_PREFIX + "optimizationMode", "short")); // fast, short
            options.setOptimizationMode(optimizationMode);
            Tour.RouteOptions.TTIBypass bypass = objectFactory.createTourRouteOptionsTTIBypass();
            optimizationMode.setValue(preferences.get(VERSION_PREFIX + "ttiBypass", "automatic")); // automatic, disabled, manual
            options.setTTIBypass(bypass);

            Tour.RouteOptions.RoadUsageTypes usageTypes = objectFactory.createTourRouteOptionsRoadUsageTypes();
            Tour.RouteOptions.RoadUsageTypes.CarTrains carTrains = objectFactory.createTourRouteOptionsRoadUsageTypesCarTrains();
            carTrains.setMode(preferences.get(VERSION_PREFIX + "carTrains", "use")); // avoid, use, prohibit
            usageTypes.setCarTrains(carTrains);
            Tour.RouteOptions.RoadUsageTypes.Ferries ferries = objectFactory.createTourRouteOptionsRoadUsageTypesFerries();
            ferries.setMode(preferences.get(VERSION_PREFIX + "ferries", "avoid")); // avoid, use, prohibit
            usageTypes.setFerries(ferries);
            Tour.RouteOptions.RoadUsageTypes.IPDRoads ipdRoads = objectFactory.createTourRouteOptionsRoadUsageTypesIPDRoads();
            ipdRoads.setMode(preferences.get(VERSION_PREFIX + "ipdRoads", "use")); // avoid, use, prohibit
            usageTypes.setIPDRoads(ipdRoads);
            Tour.RouteOptions.RoadUsageTypes.MotorWays motorWays = objectFactory.createTourRouteOptionsRoadUsageTypesMotorWays();
            motorWays.setMode(preferences.get(VERSION_PREFIX + "motorWays", "avoid")); // avoid, use, prohibit
            usageTypes.setMotorWays(motorWays);
            Tour.RouteOptions.RoadUsageTypes.SeasonalRestrictedRoads seasonalRestrictedRoads = objectFactory.createTourRouteOptionsRoadUsageTypesSeasonalRestrictedRoads();
            seasonalRestrictedRoads.setMode(preferences.get(VERSION_PREFIX + "seasonalRestrictedRoads", "use")); // avoid, use, prohibit
            usageTypes.setSeasonalRestrictedRoads(seasonalRestrictedRoads);
            Tour.RouteOptions.RoadUsageTypes.SpecialChargeRoads specialChargeRoads = objectFactory.createTourRouteOptionsRoadUsageTypesSpecialChargeRoads();
            specialChargeRoads.setMode(preferences.get(VERSION_PREFIX + "specialChargeRoads", "avoid")); // avoid, use, prohibit
            usageTypes.setSpecialChargeRoads(specialChargeRoads);
            Tour.RouteOptions.RoadUsageTypes.TimeRestrictedRoads timeRestrictedRoads = objectFactory.createTourRouteOptionsRoadUsageTypesTimeRestrictedRoads();
            timeRestrictedRoads.setMode(preferences.get(VERSION_PREFIX + "timeRestrictedRoads", "use")); // avoid, use, prohibit
            usageTypes.setTimeRestrictedRoads(timeRestrictedRoads);
            Tour.RouteOptions.RoadUsageTypes.TollRoads tollRoads = objectFactory.createTourRouteOptionsRoadUsageTypesTollRoads();
            tollRoads.setMode(preferences.get(VERSION_PREFIX + "tollRoads", "avoid")); // avoid, use, prohibit
            usageTypes.setTollRoads(tollRoads);
            Tour.RouteOptions.RoadUsageTypes.TrafficFlowInfo trafficFlowInfo = objectFactory.createTourRouteOptionsRoadUsageTypesTrafficFlowInfo();
            trafficFlowInfo.setMode(preferences.get(VERSION_PREFIX + "trafficFlowInfo", "use")); // avoid, use, prohibit
            usageTypes.setTrafficFlowInfo(trafficFlowInfo);
            Tour.RouteOptions.RoadUsageTypes.Tunnels tunnels = objectFactory.createTourRouteOptionsRoadUsageTypesTunnels();
            tunnels.setMode(preferences.get(VERSION_PREFIX + "tunnels", "use")); // avoid, use, prohibit
            usageTypes.setTunnels(tunnels);
            Tour.RouteOptions.RoadUsageTypes.UnpavedRoads unpavedRoads = objectFactory.createTourRouteOptionsRoadUsageTypesUnpavedRoads();
            unpavedRoads.setMode(preferences.get(VERSION_PREFIX + "unpavedRoads", "avoid")); // avoid, use, prohibit
            usageTypes.setUnpavedRoads(unpavedRoads);
            options.setRoadUsageTypes(usageTypes);

            Tour.RouteOptions.TravelSpeeds travelSpeeds = objectFactory.createTourRouteOptionsTravelSpeeds();
            Tour.RouteOptions.TravelSpeeds.Bicycle bicycle = objectFactory.createTourRouteOptionsTravelSpeedsBicycle();
            bicycle.setSpeed(Transfer.formatSpeed(preferences.getDouble(VERSION_PREFIX + "bicycleSpeed", 14.4))); // Km/h
            bicycle.setUnit(ROUTE_OPTIONS_SPEED_UNIT);
            travelSpeeds.setBicycle(bicycle);
            Tour.RouteOptions.TravelSpeeds.Pedestrian pedestrian = objectFactory.createTourRouteOptionsTravelSpeedsPedestrian();
            pedestrian.setSpeed(Transfer.formatSpeed(preferences.getDouble(VERSION_PREFIX + "pedestrianSpeed", 3.6))); // Km/h
            pedestrian.setUnit(ROUTE_OPTIONS_SPEED_UNIT);
            travelSpeeds.setPedestrian(pedestrian);
            Tour.RouteOptions.TravelSpeeds.Vehicle vehicle = objectFactory.createTourRouteOptionsTravelSpeedsVehicle();
            vehicle.setSpeed(Transfer.formatSpeed(preferences.getDouble(VERSION_PREFIX + "vehicleSpeed", 80.0))); // Km/h
            vehicle.setUnit(ROUTE_OPTIONS_SPEED_UNIT);
            Tour.RouteOptions.TravelSpeeds.Vehicle.MotorWay motorWay = objectFactory.createTourRouteOptionsTravelSpeedsVehicleMotorWay();
            motorWay.setSpeed(Transfer.formatSpeed(preferences.getDouble(VERSION_PREFIX + "vehicleSpeedMotorWay", 120.0))); // Km/h
            motorWay.setUnit(ROUTE_OPTIONS_SPEED_UNIT);
            vehicle.setMotorWay(motorWay);
            Tour.RouteOptions.TravelSpeeds.Vehicle.PedestrianArea pedestrianArea = objectFactory.createTourRouteOptionsTravelSpeedsVehiclePedestrianArea();
            pedestrianArea.setSpeed(Transfer.formatSpeed(preferences.getDouble(VERSION_PREFIX + "vehicleSpeedPedestrianArea", 7.2))); // Km/h
            pedestrianArea.setUnit(ROUTE_OPTIONS_SPEED_UNIT);
            vehicle.setPedestrianArea(pedestrianArea);
            travelSpeeds.setVehicle(vehicle);
            options.setTravelSpeeds(travelSpeeds);
        }
        return options;
    }

    private Integer parseZip(String zip) {
        try {
            return Transfer.parseInt(zip);
        }
        catch(NumberFormatException e) {
            // intentionally left empty
        }
        return null;
    }

    private Tour createGoPal(GoPal5Route route) {
        ObjectFactory objectFactory = new ObjectFactory();
        Tour tour = objectFactory.createTour();
        tour.setRouteOptions(createRouteOptions(route));
        for (GoPalPosition position : route.getPositions()) {
            Tour.Destination destination = objectFactory.createTourDestination();
            Tour.Destination.Coordinates coordinates = objectFactory.createTourDestinationCoordinates();
            if (position.getX() != null)
                coordinates.setMercatorx(position.getX());
            if (position.getY() != null)
                coordinates.setMercatory(position.getY());
            destination.setCoordinates(coordinates);

            Tour.Destination.City city = objectFactory.createTourDestinationCity();
            city.setName(position.getCity());
            destination.setCity(city);
            Tour.Destination.Country country = objectFactory.createTourDestinationCountry();
            if (position.getCountry() != null)
                country.setCode(position.getCountry());
            destination.setCountry(country);
            Tour.Destination.HouseNumber houseNumber = objectFactory.createTourDestinationHouseNumber();
            if (position.getHouseNumber() != null)
                houseNumber.setValue(position.getHouseNumber());
            destination.setHouseNumber(houseNumber);
            Tour.Destination.Street street = objectFactory.createTourDestinationStreet();
            street.setName(position.getStreet());
            destination.setStreet(street);
            Tour.Destination.Zip zip = objectFactory.createTourDestinationZip();
            Integer zipValue = parseZip(position.getZipCode()); // TODO eliminate Zip as String
            if (zipValue != null)
                zip.setCode(zipValue);
            destination.setZip(zip);

            tour.getDestination().add(destination);
        }
        return tour;
    }

    public void write(GoPal5Route route, OutputStream target, int startIndex, int endIndex) throws IOException {
        try {
            GoPalUtil.marshal5(createGoPal(route), target);
        } catch (JAXBException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
