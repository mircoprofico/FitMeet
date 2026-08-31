package ch.heigvd.fitmeet.ui.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style

private const val STYLE_URL = "https://tiles.openfreemap.org/styles/liberty"
private const val DEFAULT_ZOOM = 15.0

@Composable
actual fun PlatformMap(latitude: Double, longitude: Double, modifier: Modifier) {
    val context = LocalContext.current
    var mapRef by remember { mutableStateOf<MapLibreMap?>(null) }
    val mapViewRef = remember { mutableStateOf<MapView?>(null) }

    LaunchedEffect(latitude, longitude) {
        mapRef?.animateCamera(
            CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), DEFAULT_ZOOM)
        )
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

    LaunchedEffect(hasPermission) {
        if (!hasPermission) return@LaunchedEffect
        try {
            val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                ?: lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            location?.let { onLocation(it.latitude, it.longitude) }
        } catch (_: SecurityException) {}
    }
}
