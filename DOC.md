# jplot Technical Documentation

This document provides the complete API reference for **jplot**, structured specifically for developers, AI agents, and automated code generation workflows.

---

## 1. The `Plotter` Facade (`ui.Plotter`)

`Plotter` is the single entry point and unified facade for the rendering engine. It instantiates the underlying mathematical models, manages series configurations, and handles both interactive Swing rendering and headless image export.

### Constructor

```java
public Plotter(String title, String chartType, String xTitle, String yTitle)
```

* **`title`**: Main title displayed at the top of the chart.
* **`chartType`**: Target visualization engine (must match one of the `Plotter` chart constants; defaults to `LINE_CHART` if unrecognized).
* **`xTitle`**: Label for the horizontal X-axis (ignored in non-Cartesian charts like `PIE_CHART`).
* **`yTitle`**: Label for the vertical Y-axis (ignored in non-Cartesian charts like `PIE_CHART`).

---

### Supported Chart Constants

Pass any of these constants as the `chartType` argument in the `Plotter` constructor:

* `Plotter.LINE_CHART` (`"LINE"`) — Continuous lines, scatter points, and mathematical functions.
* `Plotter.POLYLINE_PLOT` (`"POLYLINE"`) — Connected sequences of vertices and geometric paths.
* `Plotter.STEP_PLOT` (`"STEP"`) — Discrete step transitions.
* `Plotter.STEM_PLOT` (`"STEM"`) — Discrete signal stems from a baseline.
* `Plotter.BAR_CHART` (`"BAR"`) — Vertical categorical or numeric bars.
* `Plotter.HBAR_CHART` (`"HBAR"`) — Horizontal categorical bars.
* `Plotter.STACKED_BAR_CHART` (`"STACKED_BAR"`) — Multi-series stacked vertical bars.
* `Plotter.HISTOGRAM_PLOT` (`"HISTOGRAM"`) — Frequency distribution bins.
* `Plotter.PIE_CHART` (`"PIE"`) — Proportional sector charts.
* `Plotter.BUBBLE_CHART` (`"BUBBLE"`) — 3D scalar data mapped to `(x, y, size)`.
* `Plotter.HEATMAP` (`"HEATMAP"`) — 2D intensity matrices (automatically disables grid and sets dark background `#1C1647`).
* `Plotter.CONFUSION_MATRIX` (`"CONFUSION"`) — Classification evaluation matrices.

---

### Series & Data Methods

* **`void create(Color color, String... info)`**
  Registers a new data series in the active dataset.
    * `color`: Primary `java.awt.Color` for the series.
    * `info`: Key-value pairs defining series metadata and styling (e.g., `"name", "SeriesA", "style", "SOLID"`). Supported keys depend on the active `chartType`.

* **`void add(String series, double... x)`**
  Appends a numeric data point to the specified series. The required number of coordinates in `x` depends on the chart's dimensionality (1D, 2D, or 3D).

* **`void add(String name, String category, double value)`**
  Appends a categorical data point to the specified series. Primarily used for bar, stacked bar, horizontal bar, pie, and confusion charts.

* **`void add(String seriesName, double minX, double maxX, int numPoints, DoubleUnaryOperator function)`**
  Evaluates a mathematical lambda expression (e.g., `x -> Math.sin(x)`) across the domain `[minX, maxX]` divided into `numPoints` steps, automatically feeding the generated `(x, y)` coordinates into `seriesName`. Requires `numPoints > 0` and `maxX > minX`.

---

### Visual Configuration Methods

* **`void grid(boolean grid)`**
  Enables (`true`) or disables (`false`) both horizontal and vertical grid lines simultaneously. Enabled by default except on `HEATMAP`.

* **`void grid(boolean h, boolean v)`**
  Independently toggles horizontal (`h`) and vertical (`v`) grid lines.

* **`void crosshair(boolean showX, boolean showY)`**
  Enables or disables interactive cursor tracking lines along the X and Y axes in Swing UI mode.

* **`void setBackground(Color color)`**
  Overrides the background color of the chart rendering canvas.

---

### Rendering & Export Methods

* **`void plot()`**
  Opens an interactive native Swing window (`ChartFrame`, default size `800x600`, minimum `600x500`) displaying the chart.

* **`void img(String path)`**
  Exports the chart to disk at the default resolution (`800x600`) in pure headless mode without opening a GUI window.

* **`void img(int width, int height, String path)`**
  Exports the chart to disk at a custom `width` and `height` in pixels using headless matrix pre-calculation. Automatically detects the image format from the file extension (`.png`, `.jpg`; defaults to `png`).

* **`ChartPanel getPanel()`**
  Returns the configured `ChartPanel` instance for embedding into custom Swing containers or `MultiPlotter` dashboards.

---

## 2. Chart Types & Data Specifications

Each chart type implements its own `Dataset` and `Point` logic, accepting specific key-value parameters in `create(...)` and specific coordinate dimensions in `add(...)`.

### 2.1. Line Chart (`Plotter.LINE_CHART`)
* **Dimensionality:** 2D Cartesian coordinates `(x, y)`
* **`create()` Parameters:**
    * `"name"` (`String`): Identifier for the series (defaults to a timestamp-based fallback).
    * `"type"` (`String`): Dataset rendering behavior. Options:
        * `"DOT_SET"` (Default): Standard scatter/line point collection.
        * `"FUNCTION"`: Optimized for mathematical function curves.
    * `"style"` (`String`): Visual representation style. Options:
        * `"SOLID"` (Default when type is `FUNCTION`)
        * `"DASHED"`
        * `"CIRCLE"` (Default when type is `DOT_SET`)
        * `"CROSS"`
        * `"TRIANGLE"`
* **`add()` Usage:**
    * `plotter.add(String seriesName, double x, double y);`
    * Requires exactly 2 numeric values representing the `(x, y)` coordinates.

---

### 2.2. Polyline Plot (`Plotter.POLYLINE_PLOT`)
* **Dimensionality:** 2D Cartesian coordinates `(x, y)` (supports both numeric coordinates and categorical X-axis mapping)
* **`create()` Parameters:**
    * `"name"` (`String`): Identifier for the series (defaults to a timestamp-based fallback).
    * `"marker"` (`String`): Vertex marker style. Options:
        * `"CIRCLE"` (Default)
        * `"NONE"`
* **`add()` Usage:**
    * Numeric: `plotter.add(String seriesName, double x, double y);` (Requires exactly 2 numeric values).
    * Categorical: `plotter.add(String seriesName, String category, double value);` (Automatically maps text categories to sequential numeric indices).

---

### 2.3. Step Plot (`Plotter.STEP_PLOT`)
* **Dimensionality:** 2D Cartesian coordinates `(x, y)` (supports both numeric coordinates and categorical X-axis mapping)
* **`create()` Parameters:**
    * `"name"` (`String`): Identifier for the series (defaults to a timestamp-based fallback).
* **`add()` Usage:**
    * Numeric: `plotter.add(String seriesName, double x, double y);` (Requires at least 2 numeric values representing `x` and `y`).
    * Categorical: `plotter.add(String seriesName, String category, double value);` (Automatically maps text categories to sequential numeric indices).

---

### 2.4. Stem Plot (`Plotter.STEM_PLOT`)
* **Dimensionality:** 2D Cartesian coordinates `(x, y)` (automatically forces baseline inclusion at $y = 0$)
* **`create()` Parameters:**
    * `"name"` (`String`): Identifier for the series (defaults to a timestamp-based fallback).
    * `"style"` (`String`): Marker style at the top of each stem. Options:
        * `"CIRCLE_SOLID"` (Default)
        * `"CIRCLE_HOLLOW"`
        * `"SQUARE_SOLID"`
        * `"SQUARE_HOLLOW"`
        * `"LOZENGE_SOLID"`
        * `"LOZENGE_HOLLOW"`
* **`add()` Usage:**
    * `plotter.add(String seriesName, double x, double y);` (Requires exactly 2 numeric values representing `x` and `y`).
---

### 2.5. Bar Chart (`Plotter.BAR_CHART`)
* **Dimensionality:** Categorical X-axis with numeric Y values (`String category`, `double value`)
* **`create()` Parameters:**
    * `"name"` (`String`): Identifier for the series (defaults to a timestamp-based fallback).
* **`add()` Usage:**
    * `plotter.add(String seriesName, String category, double value);` (Maps text categories to vertical bars with an automatic 0-baseline starting point and a 15% upper margin scaling).

---

### 2.6. Horizontal Bar Chart (`Plotter.HBAR_CHART`)
* **Dimensionality:** Categorical Y-axis with numeric X values (`String category`, `double value`)
* **`create()` Parameters:**
    * `"name"` (`String`): Identifier for the series (defaults to a timestamp-based fallback).
* **`add()` Usage:**
    * `plotter.add(String seriesName, String category, double value);` (Maps text categories to horizontal bars with an automatic 0-baseline starting point and a 15% upper margin scaling on the X-axis).

---

### 2.7. Stacked Bar Chart (`Plotter.STACKED_BAR_CHART`)
* **Dimensionality:** Categorical X-axis with cumulative multi-series vertical stacking (`String category`, `double value`)
* **`create()` Parameters:**
    * `"name"` (`String`): Identifier for the series (defaults to a timestamp-based fallback).
* **`add()` Usage:**
    * `plotter.add(String seriesName, String category, double value);` (Automatically accumulates values across series per category to dynamically scale the Y-axis with a 15% upper margin).

---

### 2.8. Histogram Plot (`Plotter.HISTOGRAM_PLOT`)
* **Dimensionality:** Frequency distribution supporting both auto-binning (1D raw values) and manual binning (2D center-frequency points)
* **`create()` Parameters:**
    * `"name"` (`String`): Identifier for the series (defaults to a timestamp-based fallback).
    * `"bins"` (`String` parsed as `int`): Number of distribution bins/bars (defaults to `40`).
* **`add()` Usage:**
    * Auto-binning mode (1 value): `plotter.add(String seriesName, double rawValue);` (Accumulates raw data points and automatically computes frequency distributions and bin centers upon compilation).
    * Manual mode (2 values): `plotter.add(String seriesName, double binCenter, double frequency);` (Directly registers pre-calculated bin coordinates).

---

### 2.9. Pie Chart (`Plotter.PIE_CHART`)
* **Dimensionality:** Proportional sector slices (`String category`, `double value`) with auto-calculated percentages and legend box rendering
* **`create()` Parameters:**
    * `"name"` (`String`): Identifier for the data slice series (defaults to a timestamp-based fallback).
* **`add()` Usage:**
    * `plotter.add(String seriesName, String category, double value);` (Registers proportional values mapped to categorical slices, automatically suppressing Cartesian axis lines/grids and rendering an anti-aliased Signed Distance Field pie chart with value percentages).

---

### 2.10. Bubble Chart (`Plotter.BUBBLE_CHART`)
* **Dimensionality:** 3D scalar data mapped to 2D Cartesian coordinates `(x, y, size)`
* **`create()` Parameters:**
    * `"name"` (`String`): Identifier for the series (defaults to a timestamp-based fallback).
* **`add()` Usage:**
    * `plotter.add(String seriesName, double x, double y, double size);` (Requires at least 3 numeric values: X-coordinate, Y-coordinate, and Z-size/weight for the bubble radius).

---

### 2.11. Heatmap (`Plotter.HEATMAP`)
* **Dimensionality:** 3D scalar intensity data mapped to 2D Cartesian coordinates `(x, y, z)` with spatial interpolation nodes
* **`create()` Parameters:**
    * `"name"` (`String`): Identifier for the series (defaults to a timestamp-based fallback).
* **`add()` Usage:**
    * `plotter.add(String seriesName, double x, double y, double z);` (Requires 3 numeric values: X-coordinate, Y-coordinate, and Z-intensity/weight used by the Signed Distance Field and quadrant influence algorithms to blend continuous heat intensity fields).

---

### 2.12. Confusion Matrix (`Plotter.CONFUSION_MATRIX`)
* **Dimensionality:** Classification matrix mapping actual vs. predicted text classes with numeric evaluation values (`String actualClass, String predictedClass, double value`)
* **`create()` Parameters:**
    * Accepts a base `Color` to tint the classification intensity gradient (defaults to an internal `"Matrix"` series).
* **`add()` Usage:**
    * `plotter.add(String actualClass, String predictedClass, double value);` (Registers performance counts or frequency weights between actual and predicted classes, automatically rendering a dynamic cell intensity grid and embedded text overlays with contrast-aware coloring).