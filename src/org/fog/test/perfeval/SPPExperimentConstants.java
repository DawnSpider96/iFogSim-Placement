package org.fog.test.perfeval;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds constant parameters that apply to all experiment iterations in a configuration file.
 * This includes location data, file paths, and event configurations that are typically
 * shared across multiple simulation runs.
 * 
 * @author Dawn
 */
public class SPPExperimentConstants {
    // Default values for optional fields
    private static final String DEFAULT_OSM_FILE = "./melbourne.osm.pbf";
    private static final String DEFAULT_GRAPH_FOLDER = "./output/graphhopper_melbourne";
    
    // Location configuration
    private final String locationConfigFile;
    private final String resourcesLocationPath;
    private final String usersLocationPath;
    
    // Dynamic location generation flag
    private final boolean useDynamicLocations;
    
    // GraphHopper / OSM configuration
    private final String osmFilePath;
    private final String graphHopperFolder;
    
    // Event configurations
    private final Map<String, EventConfig> events;
    
    // Geographic area name (e.g., "MELBOURNE", "DUBLIN")
    private final String geographicArea;
    
    /**
     * Configuration for a simulation event (e.g., opera accident)
     */
    public static class EventConfig {
        private final String eventType;
        private final double timestamp;
        private final Map<String, Object> parameters;
        
        public EventConfig(String eventType, double timestamp, Map<String, Object> parameters) {
            this.eventType = eventType;
            this.timestamp = timestamp;
            this.parameters = parameters != null ? parameters : new HashMap<>();
        }
        
        public String getEventType() {
            return eventType;
        }
        
        public double getTimestamp() {
            return timestamp;
        }
        
        public Map<String, Object> getParameters() {
            return parameters;
        }
    }
    
    /**
     * Constructor with all parameters (all required)
     */
    public SPPExperimentConstants(String locationConfigFile, 
                               String resourcesLocationPath,
                               String usersLocationPath,
                               boolean useDynamicLocations,
                               String osmFilePath,
                               String graphHopperFolder,
                               Map<String, EventConfig> events,
                               String geographicArea) {
        // Validate required fields
        if (locationConfigFile == null || geographicArea == null) {
            throw new IllegalArgumentException(
                "Required constants missing: locationConfigFile and geographicArea are required");
        }
        
        // Validate CSV paths based on useDynamicLocations flag
        if (!useDynamicLocations) {
            // If NOT using dynamic locations, CSV files MUST be provided
            if (resourcesLocationPath == null || usersLocationPath == null) {
                throw new IllegalArgumentException(
                    "When useDynamicLocations=false, resourcesLocationPath and usersLocationPath MUST be provided");
            }
            
            // Check that the CSV files actually exist
            java.io.File resourcesFile = new java.io.File(resourcesLocationPath);
            java.io.File usersFile = new java.io.File(usersLocationPath);
            
            if (!resourcesFile.exists()) {
                throw new IllegalArgumentException(
                    "When useDynamicLocations=false, resourcesLocationPath must point to an existing file. " +
                    "File not found: " + resourcesLocationPath);
            }
            
            if (!usersFile.exists()) {
                throw new IllegalArgumentException(
                    "When useDynamicLocations=false, usersLocationPath must point to an existing file. " +
                    "File not found: " + usersLocationPath);
            }
        }
        
        // Check that location config file exists (always required)
        java.io.File locationConfigFileObj = new java.io.File(locationConfigFile);
        if (!locationConfigFileObj.exists()) {
            throw new IllegalArgumentException(
                "locationConfigFile must point to an existing file. File not found: " + locationConfigFile);
        }
        
        this.locationConfigFile = locationConfigFile;
        this.resourcesLocationPath = resourcesLocationPath;
        this.usersLocationPath = usersLocationPath;
        this.useDynamicLocations = useDynamicLocations;
        this.osmFilePath = osmFilePath != null ? osmFilePath : DEFAULT_OSM_FILE;
        this.graphHopperFolder = graphHopperFolder != null ? graphHopperFolder : DEFAULT_GRAPH_FOLDER;
        this.events = events != null ? events : new HashMap<>();
        this.geographicArea = geographicArea;
        
        // Note: OSM file validation happens in SPPExperiment.validateOsmFileForMobileUsers()
        // after all configs are loaded, so we can check if mobile users are actually used
    }
    
    // Getters
    
    public String getLocationConfigFile() {
        return locationConfigFile;
    }
    
    public String getResourcesLocationPath() {
        return resourcesLocationPath;
    }
    
    public String getUsersLocationPath() {
        return usersLocationPath;
    }
    
    public String getOsmFilePath() {
        return osmFilePath;
    }
    
    public String getGraphHopperFolder() {
        return graphHopperFolder;
    }
    
    public Map<String, EventConfig> getEvents() {
        return events;
    }
    
    public String getGeographicArea() {
        return geographicArea;
    }
    
    public boolean getUseDynamicLocations() {
        return useDynamicLocations;
    }
    
    /**
     * Gets a specific event configuration by name
     */
    public EventConfig getEvent(String eventName) {
        return events.get(eventName);
    }
    
    @Override
    public String toString() {
        return String.format("ExperimentConstants{area=%s, locationConfig=%s, useDynamic=%s, resourcesCSV=%s, " +
                           "usersCSV=%s, osmFile=%s, events=%d}",
                           geographicArea, locationConfigFile, useDynamicLocations, resourcesLocationPath, 
                           usersLocationPath, osmFilePath, events.size());
    }
}
