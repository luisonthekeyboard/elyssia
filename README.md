# A companion for your Elite: Dangerous journey

This project is starting as a statistics companion for an exploration journey in Elite: Dangerous, specifically with the question "_How far from the bubble do I need to travel to find systems never before explored_".

Other questions I hope to answer by the end of this journey include:
- What's the monetary difference between journeying with short jump vs long jump (which I can estimate I've done Angya before);
- If we scan the most profitable system bodies, how long does it take to get to a certain amount of money;
- If we want to keep this infrastructure on a personal AWS account, how much does it cost;

## Events and their semantics

`StartJump` -- Signals that the jump has started, tells me where I'm going to.

    { 
        "timestamp":"2022-09-04T09:31:53Z", 
        "event":"StartJump", 
        "JumpType":"Hyperspace", 
        "StarSystem":"Aucoks HW-C d12", 
        "SystemAddress":422978767363, 
        "StarClass":"F" 
    }

`FSDJump` after `StartJump` -- Tells me I have arrived and the system I have arrived at. It is also the guarantee that I didn't have a blue tunnel or a disconnect. I think.

    { 
        "timestamp":"2022-09-04T09:32:15Z", 
        "event":"FSDJump", 
        "Taxi":false, 
        "Multicrew":false, 
        "StarSystem":"Aucoks HW-C d12", 
        "SystemAddress":422978767363, 
        "StarPos":[818.71875,-80.90625,1560.78125], 
        "SystemAllegiance":"", 
        "SystemEconomy":"$economy_None;", 
        "SystemEconomy_Localised":"None", 
        "SystemSecondEconomy":"$economy_None;", 
        "SystemSecondEconomy_Localised":"None", 
        "SystemGovernment":"$government_None;", 
        "SystemGovernment_Localised":"None", 
        "SystemSecurity":"$GAlAXY_MAP_INFO_state_anarchy;", 
        "SystemSecurity_Localised":"Anarchy", 
        "Population":0, 
        "Body":"Aucoks HW-C d12 A", 
        "BodyID":3, 
        "BodyType":"Star", 
        "JumpDist":9.744, 
        "FuelUsed":0.192190, 
        "FuelLevel":7.807810 
    }

`ScanBaryCentre` seems to be triggered on arrival and is the automated scan of reaching the baricentre of a star system

     { 
        "timestamp":"2022-09-04T09:32:20Z", 
        "event":"ScanBaryCentre", 
        "StarSystem":"Aucoks HW-C d12", 
        "SystemAddress":422978767363, 
        "BodyID":2, 
        "SemiMajorAxis":3216982841.491699, 
        "Eccentricity":0.092411, 
        "OrbitalInclination":3.568207, 
        "Periapsis":280.251650, 
        "OrbitalPeriod":817042.255402, 
        "AscendingNode":128.082962, 
        "MeanAnomaly":345.644171 
    }

`Scan` with `"ScanType":"AutoScan"`, is triggered when I automatically find system bodies simply because I'm very close to them.

    { 
        "timestamp":"2022-09-04T09:32:20Z", 
        "event":"Scan", 
        "ScanType":"AutoScan", 
        "BodyName":"Aucoks HW-C d12 B", 
        "BodyID":4, 
        "Parents":[ {"Null":2}, {"Null":1}, {"Null":0} ], 
        "StarSystem":"Aucoks HW-C d12", 
        "SystemAddress":422978767363, 
        "DistanceFromArrivalLS":6.458494, 
        "StarType":"K", 
        "Subclass":5, 
        "StellarMass":0.671875, 
        "Radius":535866944.000000, 
        "AbsoluteMagnitude":6.556274, 
        "Age_MY":1880, 
        "SurfaceTemperature":4424.000000, 
        "Luminosity":"Va", 
        "SemiMajorAxis":1217311501.502991, 
        "Eccentricity":0.003756, 
        "OrbitalInclination":-3.931702, 
        "Periapsis":284.202180, 
        "OrbitalPeriod":34774.039984, 
        "AscendingNode":-48.373831, 
        "MeanAnomaly":320.213477, 
        "RotationPeriod":291120.426432, 
        "AxialTilt":0.000000, 
        "WasDiscovered":true, 
        "WasMapped":false 
    }

`FSSDiscoveryScan` is the honk. `Progress` says if we discovered the whole system with a single honk, if it returns `1.000000`.

    { 
        "timestamp":"2022-09-04T09:29:03Z", 
        "event":"FSSDiscoveryScan", 
        "Progress":1.000000, 
        "BodyCount":2, 
        "NonBodyCount":1, 
        "SystemName":"Aucoks SY-D c1-21", 
        "SystemAddress":5857697797130 
    }

If honking discovers the whole system, then there will be `Scan`events with `"ScanType":"Detailed"` triggered immediately afterwards.

    { 
        "timestamp":"2022-09-04T09:29:03Z", 
        "event":"Scan",
        "ScanType":"Detailed", 
        "BodyName":"Aucoks SY-D c1-21 B", 
        "BodyID":2, 
        "Parents":[ {"Null":0} ], 
        "StarSystem":"Aucoks SY-D c1-21", 
        "SystemAddress":5857697797130, 
        "DistanceFromArrivalLS":53813.541062, 
        "StarType":"M", 
        "Subclass":7, 
        "StellarMass":0.253906, 
        "Radius":267179968.000000, 
        "AbsoluteMagnitude":10.531631, 
        "Age_MY":13029, 
        "SurfaceTemperature":2509.000000, 
        "Luminosity":"Va", 
        "SemiMajorAxis":9705914258956.910156, 
        "Eccentricity":0.233948, 
        "OrbitalInclination":-24.365401, 
        "Periapsis":342.736085, 
        "OrbitalPeriod":27233371138.572693, 
        "AscendingNode":-25.544498, 
        "MeanAnomaly":150.655116, 
        "RotationPeriod":130067.860223, 
        "AxialTilt":0.000000, 
        "WasDiscovered":false, 
        "WasMapped":false 
    }

Otherwise, we will trigger the Full-Spectrum Scanner (FSS). There's no event that I've seen except for a change in music, event `Music` with `"MusicTrack":SystemAndSurfaceScanner"`.

    { 
        "timestamp":"2022-09-04T09:33:37Z", 
        "event":"Music", 
        "MusicTrack":"SystemAndSurfaceScanner" 
    }

While inside the FSS, there will be more `Scan` events with `"ScanType":"Detailed"` triggered.

    { 
        "timestamp":"2022-09-04T09:33:46Z", 
        "event":"Scan", 
        "ScanType":"Detailed", 
        "BodyName":"Aucoks HW-C d12 ABC 4", 
        "BodyID":10, 
        "Parents":[ {"Null":1}, {"Null":0} ], 
        "StarSystem":"Aucoks HW-C d12", 
        "SystemAddress":422978767363, 
        "DistanceFromArrivalLS":443.159636, 
        "TidalLock":false, 
        "TerraformState":"", 
        "PlanetClass":"High metal content body", 
        "Atmosphere":"hot thick carbon dioxide atmosphere", 
        "AtmosphereType":"CarbonDioxide", 
        "AtmosphereComposition":[ { "Name":"CarbonDioxide", "Percent":92.182846 }, { "Name":"SulphurDioxide", "Percent":7.817152 } ], 
        "Volcanism":"", 
        "MassEM":0.506022, 
        "Radius":4946300.000000, 
        "SurfaceGravity":8.243612, 
        "SurfaceTemperature":740.383240, 
        "SurfacePressure":1240073.375000, 
        "Landable":false, 
        "Composition":{ "Ice":0.000000, "Rock":0.668323, "Metal":0.331677 }, 
        "SemiMajorAxis":133175414800.643921, 
        "Eccentricity":0.000199, 
        "OrbitalInclination":0.046829, 
        "Periapsis":178.507018, 
        "OrbitalPeriod":17793311.476707, 
        "AscendingNode":-105.467782, 
        "MeanAnomaly":126.110580, 
        "RotationPeriod":60409.782179, 
        "AxialTilt":0.216435, 
        "WasDiscovered":true, 
        "WasMapped":false 
    }

Until we get a `FSSAllBodiesFound` event, we have not yet scanned all of them via the FSS. 

So, it could be that we leave the FSS (`Music` event is triggered with `"MusicTrack":"Supercruise"` -- or just not `"MusicTrack":"SystemAndSurfaceScanner"`, if we want to be less strict) without scanning all bodies. We will then return to the FSS again (music change again) and finally eventually trigger `FSSAllBodiesFound`.

    {
        "timestamp":"2022-09-04T09:36:01Z",
        "event":"FSSAllBodiesFound",
        "SystemName":"Aucoks HW-C d12",
        "SystemAddress":422978767363,
        "Count":12
    }

Well, technically, it could be that I never return to the FSS, and I just leave with a body undiscovered. Gotta find out if I would still have a list of scanned vs not scanned bodies, as that's relevant for the statistics.

But eventually we find all of them. If there's valuable bodies, we might start Detailed-Surface Scanning (DSS) them.

The DSS has the same music as the FSS, which is really not optimal. But we can detect DSS scan events by the `SAAScanComplete` event.

    { 
        "timestamp":"2022-09-04T09:38:33Z", 
        "event":"SAAScanComplete", 
        "BodyName":"Aucoks HW-C d12 ABC 5", 
        "SystemAddress":422978767363, 
        "BodyID":11, 
        "ProbesUsed":5, 
        "EfficiencyTarget":7 
    }

This event will generate more `Scan` events with `"ScanType":"Detailed"`, potentially with more data, but so far I haven't seen it.

    { 
        "timestamp":"2022-09-04T09:43:31Z", 
        "event":"Scan", 
        "ScanType":"Detailed", 
        "BodyName":"Aucoks HW-C d12 ABC 6", 
        "BodyID":13, 
        "Parents":[ {"Null":1}, {"Null":0} ], 
        "StarSystem":"Aucoks HW-C d12", 
        "SystemAddress":422978767363, 
        "DistanceFromArrivalLS":1705.628175, 
        "TidalLock":false, 
        "TerraformState":"", 
        "PlanetClass":"Water world", 
        "Atmosphere":"thick carbon dioxide atmosphere", 
        "AtmosphereType":"CarbonDioxide", 
        "AtmosphereComposition":[ { "Name":"CarbonDioxide", "Percent":53.588646 }, { "Name":"Nitrogen", "Percent":42.660973 }, { "Name":"Water", "Percent":2.818544 } ], 
        "Volcanism":"major silicate vapour geysers volcanism", 
        "MassEM":3.743956, 
        "Radius":8866084.000000, 
        "SurfaceGravity":18.983504, 
        "SurfaceTemperature":478.529602, 
        "SurfacePressure":3171423.500000, 
        "Landable":false, 
        "Composition":{ "Ice":0.000000, "Rock":0.662947, "Metal":0.337053 }, 
        "SemiMajorAxis":509729665517.807007, 
        "Eccentricity":0.004646, 
        "OrbitalInclination":0.791508, 
        "Periapsis":123.741772, 
        "OrbitalPeriod":133238750.696182, 
        "AscendingNode":115.698210, 
        "MeanAnomaly":58.800059, 
        "RotationPeriod":162164.530040, 
        "AxialTilt":-0.446747, 
        "WasDiscovered":true, 
        "WasMapped":true 
    }

Now several things can happen. We can jump into another system, and that's the event `StartJump`. 

We can also close for the day. Sometimes we take the ship down to a planet and we land. No idea on the events in this case.

Other times we just jump into normal space and hang in there. That's `SupercruiseExit` event.

    { 
        "timestamp":"2022-09-04T09:43:42Z", 
        "event":"SupercruiseExit", 
        "Taxi":false, 
        "Multicrew":false, 
        "StarSystem":"Aucoks HW-C d12", 
        "SystemAddress":422978767363, 
        "Body":"Aucoks HW-C d12 ABC 6", 
        "BodyID":13, 
        "BodyType":"Planet" 
    }

Either way, we'll eventually trigger a `Music` with `"MusicTrack":"MainMenu"`.

    { 
        "timestamp":"2022-09-04T09:44:03Z", 
        "event":"Music", 
        "MusicTrack":"MainMenu" 
    }

And if we exit the game normally, we'll trigger a `Shutdown` event.

    {
        "timestamp":"2022-09-04T09:44:15Z", 
        "event":"Shutdown" 
    }

`Shutdown` signifies the end of the session, unless we start a new session on the same day. Sessions will be constrained by the day they are started, not by the day they are finished. If we start 2 sessions on the same day, they will be considered the same session.

`Shutdown` will also mean the end of a system visit, in particular the last system visited in a session.

Otherwise, system visits are enclosed in `StartJump` events.