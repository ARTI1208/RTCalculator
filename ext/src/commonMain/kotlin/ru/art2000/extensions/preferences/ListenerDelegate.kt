package ru.art2000.extensions.preferences

expect class ListenerDelegate<V>(property: IPreferenceDelegate<V>) {

    fun listen(listener: AppPreferences.Listener<V>)

    fun stopListening()

    fun stopListening(listener: AppPreferences.Listener<V>)

}