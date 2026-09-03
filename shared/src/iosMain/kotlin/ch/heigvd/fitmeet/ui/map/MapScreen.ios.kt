package ch.heigvd.fitmeet.ui.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import ch.heigvd.fitmeet.model.Activity
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCAction
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGRectMake
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.Foundation.NSSelectorFromString
import platform.MapKit.MKCoordinateRegionMakeWithDistance
import platform.MapKit.MKMapTypeStandard
import platform.MapKit.MKMapView
import platform.MapKit.MKPointAnnotation
import platform.UIKit.UITapGestureRecognizer

private const val DEFAULT_ZOOM_DISTANCE_METERS = 1_000.0

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private class FitMeetMapView : MKMapView(frame = CGRectMake(0.0, 0.0, 0.0, 0.0)) {
    var onMapClick: ((Double, Double) -> Unit)? = null
    private var selectedAnnotation: MKPointAnnotation? = null
    private val tapGesture = UITapGestureRecognizer(
        target = this,
        action = NSSelectorFromString(this::selectLocation.name),
    )

    init {
        mapType = MKMapTypeStandard
        addGestureRecognizer(tapGesture)
    }

    @ObjCAction
    fun selectLocation() {
        convertPoint(tapGesture.locationInView(this), toCoordinateFromView = this).useContents {
            onMapClick?.invoke(latitude, longitude)
        }
    }

    fun showLocation(latitude: Double, longitude: Double, animated: Boolean) {
        setRegion(
            MKCoordinateRegionMakeWithDistance(
                CLLocationCoordinate2DMake(latitude, longitude),
                DEFAULT_ZOOM_DISTANCE_METERS,
                DEFAULT_ZOOM_DISTANCE_METERS,
            ),
            animated = animated,
        )
    }

    fun updateSelectedLocation(latitude: Double?, longitude: Double?) {
        selectedAnnotation?.let(::removeAnnotation)
        selectedAnnotation = if (latitude != null && longitude != null) {
            MKPointAnnotation().also { annotation ->
                annotation.setCoordinate(CLLocationCoordinate2DMake(latitude, longitude))
                addAnnotation(annotation)
            }
        } else {
            null
        }
    }
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
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
    val currentOnMapClick = rememberUpdatedState(onMapClick)

    UIKitView(
        factory = { FitMeetMapView() },
        modifier = modifier,
        update = { mapView ->
            mapView.onMapClick = currentOnMapClick.value
            mapView.showLocation(latitude, longitude, animated = true)
            mapView.updateSelectedLocation(selectedLat, selectedLng)
        },
    )
}

@Composable
actual fun LocationEffect(onLocation: (Double, Double) -> Unit) {
    // Lausanne par défaut jusqu'à l'implémentation de CLLocationManager
    LaunchedEffect(Unit) {
        onLocation(46.5197, 6.6323)
    }
}
