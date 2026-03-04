package xyz.fakestore.payments.messagequeue

import xyz.fakestore.payments.enumz.Topic

interface MessageSender {
    fun sendMessage(
        key: String,
        headers: Map<String, String>,
        topic: Topic,
        payload: String
    )

    fun close()
}

