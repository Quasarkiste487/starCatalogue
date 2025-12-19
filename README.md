# starCatalogue
a project for the module Mobile Computing at HTWK!

## SIMBAD

### Unformatierter Output
```
set limit 10

format object "\nId: %MAIN_ID\n" +
# Coordinates (right ascencion and declination
"Coordinates: %COO(C)\n" +
# Visible Flux in mag
"Flux: %FLUXLIST(V;F)\n" +
# Spectral Type
"Spectral Type: %SP(S)\n" +
# Some Measurements
"Measurements: %MEASLIST(diameter, distance;F)"

query sample Vmag < 6
```

### JSON-ish
Gott weiß wer diesen parser entwurfen hat, aber die Anführungszeichen funktionieren so flawless. Newlines aber nicht wirklich kontrollierbar
```
set limit 10

format object "{"id": "%MAIN_ID",\n" +
# Coordinates (right ascencion and declination
" "coordinates": "%COO(C)",\n" +
# Visible Flux in mag
" "flux": "%FLUXLIST(V;F)",\n" +
# Spectral Type
" "spectral Type": "%SP(S)",\n" +
# Some Measurements
" "measurements": "%MEASLIST(diameter, distance;F)"},"

echodata {
query sample Vmag < 6
echodata }
```

### VOTable

[Java Libary](https://www.star.bristol.ac.uk/~mbt/stil/javadocs/uk/ac/starlink/votable/package-summary.html) sollte auch in kotlin verwendbar sein
