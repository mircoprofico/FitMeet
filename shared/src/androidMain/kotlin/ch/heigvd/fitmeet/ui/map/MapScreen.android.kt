package ch.heigvd.fitmeet.ui.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import android.location.LocationListener
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import ch.heigvd.fitmeet.model.Activity
import org.maplibre.android.MapLibre
import org.maplibre.android.annotations.Marker
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style

private const val STYLE_URL = "https://tiles.openfreemap.org/styles/liberty"
private const val DEFAULT_ZOOM = 15.0

@Composable
actual fun PlatformMap(
    latitude: Double,
    longitude: Double,
    modifier: Modifier,
    onMapClick: ((Double, Double) -> Unit)?,
    selectedLat: Double?,
    selectedLng: Double?,
    activities: List<Activity>,
    onActivityClick: ((Activity) -> Unit)?,
) {
    val context = LocalContext.current
    var mapRef by remember { mutableStateOf<MapLibreMap?>(null) }
    val mapViewRef = remember { mutableStateOf<MapView?>(null) }
    var selectedMarker by remember { mutableStateOf<Marker?>(null) }
    val activityMarkers = remember { mutableMapOf<String, Marker>() }
    val currentOnMapClick by rememberUpdatedState(onMapClick)
    val currentOnActivityClick by rememberUpdatedState(onActivityClick)
    val currentActivitiesById by rememberUpdatedState(activities.associateBy { it.id })

    // Center the camera once the map is ready. Using mapRef as key guarantees
    // this runs after getMapAsync, when the map can actually receive commands.
    val currentLat by rememberUpdatedState(latitude)
    val currentLng by rememberUpdatedState(longitude)
    LaunchedEffect(mapRef) {
        val map = mapRef ?: return@LaunchedEffect
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(currentLat, currentLng), DEFAULT_ZOOM))
    }

    LaunchedEffect(selectedLat, selectedLng, mapRef) {
        val map = mapRef ?: return@LaunchedEffect
        selectedMarker?.let { map.removeMarker(it) }
        selectedMarker = if (selectedLat != null && selectedLng != null) {
            map.addMarker(MarkerOptions().position(LatLng(selectedLat, selectedLng)))
        } else null
    }

    LaunchedEffect(activities, mapRef) {
        val map = mapRef ?: return@LaunchedEffect
        activityMarkers.values.forEach { map.removeMarker(it) }
        activityMarkers.clear()
        activities.forEach { activity ->
            val lat = activity.latitude ?: return@forEach
            val lng = activity.longitude ?: return@forEach
            val marker = map.addMarker(
                MarkerOptions().position(LatLng(lat, lng)).title(activity.id),
            )
            activityMarkers[activity.id] = marker
        }
    }

    AndroidView(
        factory = {
            MapLibre.getInstance(context)
            MapView(context).also { mapView ->
                mapViewRef.value = mapView
                mapView.onCreate(null)
                mapView.onStart()
                mapView.onResume()
                mapView.getMapAsync { map ->
                    mapRef = map
                    map.setStyle(Style.Builder().fromUri(STYLE_URL))
                    map.addOnMapClickListener { latLng ->
                        currentOnMapClick?.invoke(latLng.latitude, latLng.longitude)
                        true
                    }
                    map.setOnMarkerClickListener { marker: Marker ->
                        val activity = currentActivitiesById[marker.title]
                        if (activity != null) {
                            currentOnActivityClick?.invoke(activity)
                            true
                        } else false
                    }
                }
            }
        },
        modifier = modifier,
    )

    DisposableEffect(Unit) {
        onDispose {
            mapViewRef.value?.onPause()
            mapViewRef.value?.onStop()
            mapViewRef.value?.onDestroy()
        }
    }
}

@Composable
actual fun LocationEffect(onLocation: (Double, Double) -> Unit) {
    val context = LocalContext.current
    val callback by rememberUpdatedState(onLocation)

    var hasPermission by remember {
        mutableStateOf(
            context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
        )
    }

    // Re-check on every resume: covers the case where the user grants the
    // permission from system Settings and then comes back to the app.
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasPermission = context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasPermission = granted }

    LaunchedEffect(Unit) {
        if (!hasPermission) permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    DisposableEffect(hasPermission) {
        if (!hasPermission) return@DisposableEffect onDispose {}
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val listener = LocationListener { loc ->
            Log.d("FitMeet/Loc", "live update [${loc.provider}] -> ${loc.latitude},${loc.longitude} acc=${loc.accuracy}m")
            callback(loc.latitude, loc.longitude)
        }
        try {
            val freshCutoff = System.currentTimeMillis() - 60_000L
            val candidates = listOf(
                LocationManager.GPS_PROVIDER,
                LocationManager.NETWORK_PROVIDER,
                LocationManager.PASSIVE_PROVIDER,
            ).mapNotNull { provider ->
                lm.getLastKnownLocation(provider)?.also { loc ->
                    val ageS = (System.currentTimeMillis() - loc.time) / 1000
                    Log.d("FitMeet/Loc", "last[$provider] = ${loc.latitude},${loc.longitude} acc=${loc.accuracy}m age=${ageS}s")
                }
            }
            val fresh = candidates.filter { it.time >= freshCutoff }.minByOrNull { it.accuracy }
            if (fresh != null) {
                Log.d("FitMeet/Loc", "using cached -> ${fresh.latitude},${fresh.longitude}")
                callback(fresh.latitude, fresh.longitude)
            } else {
                Log.d("FitMeet/Loc", "no fresh cache, waiting for live fix")
            }
            if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, listener)
                Log.d("FitMeet/Loc", "registered NETWORK updates")
            }
            if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2_000L, 0f, listener)
                Log.d("FitMeet/Loc", "registered GPS updates")
            }
        } catch (_: SecurityException) {}
        onDispose { lm.removeUpdates(listener) }
    }
}
