package com.lingyunchi.fartlek.utils

interface ListenHandler {
    fun unRegister()
}

typealias EventListener<T> = (T) -> Unit

class EventSource<T> {
    private var listeners = HashMap<ListenHandler, EventListener<T>>()

    inner class ListenerHandler: ListenHandler {
        override fun unRegister() {
            unregister(this)
        }
    }

    fun emit(data: T) {
        listeners.forEach {
            it.value.invoke(data)
        }
    }

    fun register(eventListener: EventListener<T>): ListenerHandler {
        val handler = ListenerHandler()
        listeners[handler] = eventListener
        return handler
    }

    fun unregister(handler: ListenHandler) {
        listeners.remove(handler)
    }
}