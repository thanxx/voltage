# Voltage

<img src="https://github.com/thanxx/voltage/assets/21316174/4c76583f-05a2-43da-9dd8-96082d672ffc" width=33% height=33%>
<img src="https://github.com/thanxx/voltage/assets/21316174/bca18310-a5f3-4a33-9bfd-85606d1f1349" width=33% height=33%>
<img src="https://github.com/thanxx/voltage/assets/21316174/7b85c7dd-b00f-4725-94a6-836a03a0cfbd" width=33% height=33%>

An Android app for reading ECU data (battery, etc) from the GM Chevrolet Volt 2 via a generic ELM327 Bluetooth adapter. The current version was tested with the 2016 Volt.
So users of 2017, and 2018 years, please confirm it is working with your vehicle.

## Current functions:
- Reading of current battery state: SoC displayed, SoC Raw HD, Capacity, Cells 1-96 voltage, Min, Max, Avg
- Storage of every record in a time-series database (for the capacity chart & future aggregations)
- Historical chart for battery capacity

## TODO / With the help of the community and contributors, it would be cool to:
- Collect other useful PIDs
- Implement `VehicleScanResultsProvider` for Volt 1, Bolt, etc.
- Select Vehicle by VIN in Settings (in case when a user has more than one)
- Historical and statistical aggregation of collected cell data. Visualize other useful values
- Prediction of cell degradation, BECM, and other failures
- your ideas are welcome




