package us.faerman.investing.nearby.managers.location

interface PositionManagerListener {
    fun onGpsProviderDisable()
    fun onNoProviderAvailable()
    fun onInitializeProviderError()
    fun onPermissionError()
    fun onPlayServiceDisable()
    fun onReady()
}