package org.fog.test.unit;

import org.fog.mobilitydata.Location;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for Location class - testing mathematical functions
 * without running any simulations.
 */
public class LocationTest {
    
    private static final double DELTA = 0.01; // Tolerance for double comparisons (10 meters)
    
    @Before
    public void setUp() {
        // Reset to default values before each test
        Location.refreshConfigValues();
    }
    
    @Test
    public void testHaversineDistanceKnownLocations() {
        // Melbourne CBD to Melbourne Airport (real-world distance ~20km)
        Location cbd = new Location(-37.8136, 144.9631, -1);
        Location airport = new Location(-37.6690, 144.8410, -1);
        
        double distance = cbd.calculateDistance(airport);
        
        // Should be approximately 20km
        assertTrue("Distance should be around 20km", distance > 18 && distance < 22);
    }
    
    @Test
    public void testDistanceToSelfIsZero() {
        Location loc = new Location(-37.8136, 144.9631, -1);
        
        double distance = loc.calculateDistance(loc);
        
        assertEquals("Distance to self should be 0", 0.0, distance, DELTA);
    }
    
    @Test
    public void testDistanceIsSymmetric() {
        Location loc1 = new Location(-37.8136, 144.9631, -1);
        Location loc2 = new Location(-37.8200, 144.9700, -1);
        
        double distance1to2 = loc1.calculateDistance(loc2);
        double distance2to1 = loc2.calculateDistance(loc1);
        
        assertEquals("Distance should be symmetric", distance1to2, distance2to1, DELTA);
    }
    
    @Test
    public void testVeryShortDistance() {
        // Two locations 100 meters apart (approximately)
        Location loc1 = new Location(-37.8136, 144.9631, -1);
        Location loc2 = new Location(-37.8145, 144.9631, -1); // ~100m north
        
        double distance = loc1.calculateDistance(loc2);
        
        assertTrue("Distance should be approximately 100m (0.1km)", 
                   distance > 0.09 && distance < 0.11);
    }
    
    @Test
    public void testMovedTowardsHalfway() {
        Location start = new Location(-37.8000, 144.9000, -1);
        Location end = new Location(-37.8100, 144.9100, -1);
        
        double totalDistance = start.calculateDistance(end);
        Location halfway = start.movedTowards(end, totalDistance / 2);
        
        double distanceToHalfway = start.calculateDistance(halfway);
        
        assertEquals("Should move exactly halfway", totalDistance / 2, distanceToHalfway, DELTA);
    }
    
    @Test
    public void testMovedTowardsBeyondDestination() {
        Location start = new Location(-37.8000, 144.9000, -1);
        Location end = new Location(-37.8100, 144.9100, -1);
        
        double totalDistance = start.calculateDistance(end);
        // Try to move twice the distance - should stop at destination
        Location result = start.movedTowards(end, totalDistance * 2);
        
        assertEquals("Should not move beyond destination (lat)", 
                     end.latitude, result.latitude, DELTA);
        assertEquals("Should not move beyond destination (lon)", 
                     end.longitude, result.longitude, DELTA);
    }
    
    @Test
    public void testBearingNorth() {
        Location start = new Location(-37.8100, 144.9000, -1);
        Location north = new Location(-37.8000, 144.9000, -1); // Same longitude, north
        
        double bearing = start.getBearing(north);
        
        assertEquals("Bearing to north should be ~0 degrees", 0.0, bearing, 5.0);
    }
    
    @Test
    public void testBearingEast() {
        Location start = new Location(-37.8000, 144.9000, -1);
        Location east = new Location(-37.8000, 144.9100, -1); // Same latitude, east
        
        double bearing = start.getBearing(east);
        
        assertEquals("Bearing to east should be ~90 degrees", 90.0, bearing, 5.0);
    }
    
    @Test
    public void testBearingSouth() {
        Location start = new Location(-37.8000, 144.9000, -1);
        Location south = new Location(-37.8100, 144.9000, -1); // Same longitude, south
        
        double bearing = start.getBearing(south);
        
        assertEquals("Bearing to south should be ~180 degrees", 180.0, bearing, 5.0);
    }
    
    @Test
    public void testPointInPolygonInside() {
        // Simple square boundary
        double[][] boundary = {
            {-37.82, 144.95},  // Bottom-left
            {-37.82, 144.98},  // Bottom-right
            {-37.80, 144.98},  // Top-right
            {-37.80, 144.95}   // Top-left
        };
        
        // Point clearly inside
        boolean inside = Location.isPointInPolygon(-37.81, 144.96, boundary);
        
        assertTrue("Point should be inside polygon", inside);
    }
    
    @Test
    public void testPointInPolygonOutside() {
        // Simple square boundary
        double[][] boundary = {
            {-37.82, 144.95},  // Bottom-left
            {-37.82, 144.98},  // Bottom-right
            {-37.80, 144.98},  // Top-right
            {-37.80, 144.95}   // Top-left
        };
        
        // Point clearly outside (north of boundary)
        boolean inside = Location.isPointInPolygon(-37.79, 144.96, boundary);
        
        assertFalse("Point should be outside polygon", inside);
    }
    
    @Test
    public void testPointInPolygonOnEdge() {
        // Simple square boundary
        double[][] boundary = {
            {-37.82, 144.95},
            {-37.82, 144.98},
            {-37.80, 144.98},
            {-37.80, 144.95}
        };
        
        // Point exactly on edge - behavior may vary, just test it doesn't crash
        boolean inside = Location.isPointInPolygon(-37.81, 144.95, boundary);
        
        // Either inside or outside is acceptable for edge case
        assertNotNull("Should return a boolean value", inside);
    }
    
    @Test
    public void testRandomLocationDeterministic() {
        // Set seed and get two locations
        Location.setDefaultRandomSeed(12345L);
        Location loc1 = Location.getRandomLocation();
        Location loc2 = Location.getRandomLocation();
        
        // Reset seed and get two more locations - should be same sequence
        Location.setDefaultRandomSeed(12345L);
        Location loc3 = Location.getRandomLocation();
        Location loc4 = Location.getRandomLocation();
        
        assertEquals("Same seed should produce same first location (lat)", 
                     loc1.latitude, loc3.latitude, 0.000001);
        assertEquals("Same seed should produce same first location (lon)", 
                     loc1.longitude, loc3.longitude, 0.000001);
        assertEquals("Same seed should produce same second location (lat)", 
                     loc2.latitude, loc4.latitude, 0.000001);
        assertEquals("Same seed should produce same second location (lon)", 
                     loc2.longitude, loc4.longitude, 0.000001);
        
        // But consecutive calls should produce DIFFERENT locations
        assertFalse("Consecutive calls should produce different locations",
                    loc1.latitude == loc2.latitude && loc1.longitude == loc2.longitude);
    }
    
    @Test
    public void testRandomLocationWithinRadiusActuallyWithinRadius() {
        double centerLat = -37.8136;
        double centerLon = 144.9631;
        double radiusMeters = 500.0; // 500 meters
        
        Location.setDefaultRandomSeed(42L);
        Location center = new Location(centerLat, centerLon, -1);
        Location random = Location.getRandomLocationWithinRadius(centerLat, centerLon, radiusMeters);
        
        double actualDistance = center.calculateDistance(random);
        
        assertTrue("Random location should be within specified radius", 
                   actualDistance <= (radiusMeters / 1000.0)); // Convert to km
    }
    
    @Test
    public void testRandomLocationWithinRadiusDeterministic() {
        double centerLat = -37.8136;
        double centerLon = 144.9631;
        double radiusMeters = 500.0;
        
        // Get locations with seed
        Location.setDefaultRandomSeed(42L);
        Location loc1 = Location.getRandomLocationWithinRadius(centerLat, centerLon, radiusMeters);
        Location loc2 = Location.getRandomLocationWithinRadius(centerLat, centerLon, radiusMeters);
        
        // Reset and get again - should be same sequence
        Location.setDefaultRandomSeed(42L);
        Location loc3 = Location.getRandomLocationWithinRadius(centerLat, centerLon, radiusMeters);
        Location loc4 = Location.getRandomLocationWithinRadius(centerLat, centerLon, radiusMeters);
        
        assertEquals("Same seed should produce same first location", 
                     loc1.latitude, loc3.latitude, 0.000001);
        assertEquals("Same seed should produce same second location", 
                     loc2.latitude, loc4.latitude, 0.000001);
        
        // But consecutive calls should produce DIFFERENT locations
        assertFalse("Consecutive calls should produce different locations",
                    loc1.latitude == loc2.latitude && loc1.longitude == loc2.longitude);
    }
    
    @Test
    public void testToStringContainsCoordinates() {
        Location loc = new Location(-37.8136, 144.9631, -1);
        
        String str = loc.toString();
        
        assertTrue("toString should contain latitude", str.contains("-37.8136"));
        assertTrue("toString should contain longitude", str.contains("144.9631"));
    }
    
    @Test
    public void testGettersReturnCorrectValues() {
        double expectedLat = -37.8136;
        double expectedLon = 144.9631;
        Location loc = new Location(expectedLat, expectedLon, 5);
        
        assertEquals("getLatitude should return correct value", 
                     expectedLat, loc.getLatitude(), 0.000001);
        assertEquals("getLongitude should return correct value", 
                     expectedLon, loc.getLongitude(), 0.000001);
    }
}

