package ch.heigvd.fitmeet.ui.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
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

    LaunchedEffect(latitude, longitude) {
        mapRef?.animateCamera(
            CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), DEFAULT_ZOOM)
        )
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
                    map.setStyle(Style.Builder().fromUri(STYLE_URL)) {
                        map.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(latitude, longitude), DEFAULT_ZOOM
                            )
                        )
                    }
                    map.addOnMapClickListener { latLng ->
                        currentOnMapClick?.invoke(latLng.latitude, latLng.longitude)
                        true
                    }
                    map.addOnMarkerClickListener { marker ->
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

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasPermission = granted }

    LaunchedEffect(Unit) {
        if (!hasPermission) permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    DisposableEffect(hasPermission) {
        if (!hasPermission) return@DisposableEffect onDispose {}
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val listener = LocationListener { loc -> callback(loc.latitude, loc.longitude) }
        try {
            // emit last known location immediately so the camera does not wait
            val last = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                ?: lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            last?.let { callback(it.latitude, it.longitude) }
            // then keep updating so every visit to the map tab centres on the
            // current position, not a stale one
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5_000L, 10f, listener)
        } catch (_: SecurityException) {}
        onDispose { lm.removeUpdates(listener) }
    }
}
