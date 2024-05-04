package macc.ar.presentation.map.events



sealed class LocationGrantedEvent {
    data object LocationGranted : LocationGrantedEvent()
}