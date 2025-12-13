# iFogSim-Placement
A simulation framework designed for experimenting with self-adaptive approaches to address the service-placement problem in Edge Computing. Service placement is a 
complex multi-objective optimisation problem defined by conflicting requirements and dynamic environments. The final goal of service-placement approaches is to decide
which Edge node hosts which software service to support the deployment of applications in Edge architectures.

iFogSim-Placement extends the newest version of [iFogSim](https://github.com/Cloudslab/iFogSim) [1] with several components to recreate advanced service placement scenarios. 

# How to run iFogSim-Placement?

## Getting Started

### Prerequisites

1. **JDK 11**: Ensure you have JDK 11 installed on your machine. You can verify your Java version by running:
   ```bash
   java -version
   ```
   If you don't have JDK 11, download it from [Oracle](https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html) or use a package manager like Homebrew (macOS) or apt (Linux).

### Installation

2. **Clone this repository to desired folder. If you are in the anonymised GitHub repository, please download the repository from [here](https://anonymous.4open.science/api/repo/iFogSim-Placement-C94E/zip). You will need to unzip the downloaded file to continue with the installation below.**
    ```bash
    git clone https://anonymous.4open.science/r/iFogSim-Placement-C94E
    ```

3. **Install Dependencies** (in the repository's root directory)
This repository uses Maven for package management. The repository includes a Maven wraper you can use to install all project dependencies using the following commands:

   - **On macOS/Linux**:
     ```bash
     chmod +x ./mvnw
     ./mvnw clean install
     ```
   - **On Windows**:
     ```bash
     ./mvnw.cmd clean install
     ```

### Running the Simulation

4. **Run a Service Placement Scenario**: This framework includes three main scenarios to get started:

   | Scenario | Description | Command |
   |----------|-------------|---------|
   | `SPPExperiment` | Main research platform with YAML-based configuration | `./mvnw exec:java` |
   | `OnlinePOC` | Online placement with periodic requests | `./mvnw exec:java -Dexec.mainClass="org.fog.test.perfeval.OnlinePOC"` |
   | `MobilityPOC` | Mobility framework demonstration | `./mvnw exec:java -Dexec.mainClass="org.fog.test.mobility.MobilityPOC"` |

   For example, to run the main research platform:
   ```bash
   ./mvnw exec:java 2>&1 | tee output/SPPExperiment.txt
   ```

   **Already have Maven installed?** If you have Maven 3.6.3+ installed, you can replace `./mvnw` with `mvn` in all commands.

## Service Placement Scenarios

Each scenario is explained in detail below:

### 1. SPPExperiment - Main Research Platform

`SPPExperiment.java` is the primary research platform that allows researchers to configure and run complex simulations using YAML configuration files.

#### Key Features:
- **YAML-based Configuration**: Define multiple simulation scenarios in a single YAML file
- **Comprehensive Metrics**: Tracks latency, CPU utilization, energy consumption, memory usage, and failure rates
- **Multiple Placement Algorithms**: Supports ACO, Best-Fit, Closest-Fit, ILP, Max-Fit, and more
- **Dynamic Location Generation**: Option to generate random or grid-based location distributions
- **User Type Support**: Different user types (generic, ambulance, opera, immobile) with distinct behaviors
- **Performance Monitoring**: Real-time memory and execution time tracking

#### Configuration File Structure:
```yaml
constants:
  geographicArea: "MELBOURNE"                              # Geographic area name
  locationConfigFile: "./dataset/location_config_melbourne.json"
  resourcesLocationPath: "./dataset/edgeResources.csv"     # Edge server locations
  usersLocationPath: "./dataset/usersLocation.csv"         # User locations
  outputFilePath: "./output/my_results.csv"                # Custom output path (optional)

experiments:
  - numberOfEdge: 100                    # Number of edge servers
    placementLogic: "ACO"               # Placement algorithm
    numberOfApplications: 1300          # Total applications to create
    appLoopLength: 1                    # Microservices per application
    placementProcessInterval: 4.0       # Placement processing interval (seconds)
    usersPerType:                       # User distribution
      genericUser: 100
      operaUser: 80
      ambulanceUser: 20
    intervalValues:                     # Event generation intervals (seconds)
      genericUser: 300
      ambulanceUser: 180
      operaUser: 600
      immobileUser: 900
    randomSeeds:                        # Reproducible randomness
      experimentSeed: 33
      locationSeed: 42
      mobilityStrategySeed: 123
      heuristicSeed: 456
    areaName: "melbourne"               # Geographic area
```

#### Usage:
1. **Configure your experiment** in `./dataset/SPPExperimentConfigs.yaml`
2. **Run the simulation**:
   ```bash
   ./mvnw exec:java 2>&1 | tee output/SPPExperiment.txt
   ```
3. **View results**:
   - CSV metrics: `./output/MiH_Melbourne.csv` (or your configured `outputFilePath`)
   - Console log: `./output/SPPExperiment.txt` (if using `tee` as shown above)

#### Available Configuration Files (Feel free to make your own):
- `./dataset/SPPExperimentConfigs.yaml` - Main experiment configurations

### 2. OnlinePOC - Online Placement Proof of Concept

`OnlinePOC.java` demonstrates online microservice placement with periodic placement requests.

#### Features:
- **Online Placement**: Real-time placement request processing
- **Periodic Generation**: Placement requests generated at configurable intervals
- **Immobile Users**: Focus on stationary user devices
- **Simple Application Model**: 4-microservice application with deterministic flow

#### Usage:
```bash
./mvnw exec:java -Dexec.mainClass="org.fog.test.perfeval.OnlinePOC"
```

### 3. MobilityPOC - Mobility Framework Proof of Concept

`MobilityPOC.java` demonstrates the mobility framework without placement requests.

#### Features:
- **Pure Mobility**: Focus on device movement without application placement
- **Strategy Pattern**: Flexible mobility strategy implementation
- **Multiple User Types**: Generic, Opera, and Ambulance users with different mobility patterns
- **Location Data Integration**: Uses real Melbourne CBD location data

#### Usage:
```bash
./mvnw exec:java -Dexec.mainClass="org.fog.test.mobility.MobilityPOC"
```

## Configuration Options

### Dynamic Location Generation
Enable automatic location generation instead of using pre-defined CSV files:

```java
// In SPPExperiment.java
SPPExperiment.setUseDynamicLocations(true);
SPPExperiment.setOutputDirectory("./dataset/generated/");
```

### Placement Algorithms
Available placement algorithms (use string names in YAML):
- `"ACO"` - Ant Colony Optimization
- `"BEST_FIT"` - Best Fit Heuristic
- `"CLOSEST_FIT"` - Closest Fit Heuristic
- `"ILP"` - Integer Linear Programming
- `"MAX_FIT"` - Maximum Fit Heuristic

### User Types
Supported user types with distinct characteristics as defined by Cabrera et al. [2]:
- `genericUser` - Standard mobile users
- `ambulanceUser` - Emergency vehicle users (higher priority)
- `operaUser` - Event-based users (periodic high activity)
- `immobileUser` - Stationary users

## Output and Metrics

SPPExperiment produces two types of output.

### 1. Primary Results (CSV)

The **CSV file containing simulation metrics** is the primary output containing structured experiment results. This is what you should use for data analysis and visualization.

| Property | Description |
|----------|-------------|
| **Default Path** | `./output/MiH_Melbourne.csv` |
| **Configurable** | Yes, via `outputFilePath` in YAML constants section |
| **Format** | CSV with headers |
| **Generated By** | SPPExperiment.java automatically |

**Contents:**
- Simulation index and configuration
- Resource utilization (average and standard deviation)
- Latency metrics (average and standard deviation)
- Failure ratios
- Execution time and memory usage
- Energy consumption (cloud and edge devices)

**To configure a custom output path**, add to your config YAML file. (The config YAML filepath is specified in SPPExperiment itself, by default this is `./dataset/SPPExperimentConfigs.yaml`).
```yaml
constants:
  outputFilePath: "./output/my_experiment_results.csv"
  # ... other constants
```

### 2. Console Log (Execution Details)

The **console log** contains verbose execution details useful for debugging and monitoring simulation progress. This is NOT automatically saved to a file.

| Property | Description |
|----------|-------------|
| **Default** | Printed to terminal only |
| **Saved When** | You redirect output using `tee` |
| **Format** | Human-readable text |

**Contents:**
- Configuration loading messages
- Per-simulation progress updates
- Memory usage statistics
- Entity/device creation details
- Error messages and warnings

**To save the console log**, use output redirection:
```bash
./mvnw exec:java 2>&1 | tee output/SPPExperimentLogs.txt
```

This captures both stdout and stderr to `output/SPPExperimentLogs.txt` while still displaying output in real-time.

### Temporary Files
During simulation, temporary files are created in `./temp_metrics/`:
- `sim_X_metrics.csv` - Detailed per-request performance metrics
- `sim_X_failed.csv` - Failed placement request details

These are automatically deleted after each simulation completes.

## Customization

### Modifying SPPExperiment
You can modify `SPPExperiment.java` to:
- Add new metrics collection
- Implement custom placement algorithms
- Change the application model
- Add new user types
- Modify the simulation flow

### Adding New Configuration Parameters
To add new parameters to the YAML configuration:
1. Update the `SimulationConfig` class
2. Modify the YAML loading logic in `loadConfigurationsFromYaml()`
3. Use the new parameters in your simulation logic

## Troubleshooting

### Common Issues:
1. **Memory Issues**: Large simulations may require increased heap size:
   ```bash
   MAVEN_OPTS="-Xmx8g" ./mvnw exec:java
   ```

2. **CSV File Not Found**: Ensure location data files exist in `./dataset/`

3. **YAML Parsing Errors**: Check YAML syntax and indentation

4. **Placement Algorithm Not Found**: Verify algorithm names match those in `PlacementLogicFactory`

## References
 [1] Redowan Mahmud, Samodha Pallewatta, Mohammad Goudarzi, and Rajkumar Buyya, <A href="https://arxiv.org/abs/2109.05636">iFogSim2: An Extended iFogSim Simulator for Mobility, Clustering, and Microservice Management in Edge and Fog Computing Environments</A>, Journal of Systems and Software (JSS), Volume 190, Pages: 1-17, ISSN:0164-1212, Elsevier Press, Amsterdam, The Netherlands, August 2022.
 [2] Cabrera, C., Svorobej, S., Palade, A., Kazmi, A., & Clarke, S. (2022). <A href="https://arxiv.org/abs/2109.05636">MAACO: A dynamic service placement model for smart cities</A>, IEEE Transactions on Services Computing, 16(1), 424-437.
