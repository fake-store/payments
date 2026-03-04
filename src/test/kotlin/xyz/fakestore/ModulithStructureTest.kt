package xyz.fakestore

import org.junit.jupiter.api.Test
import org.springframework.modulith.core.ApplicationModules
import xyz.fakestore.payments.Application

class ModulithStructureTest {

    @Test
    fun `verify module structure`() {
        val modules = ApplicationModules.of(Application::class.java)
        modules.verify()
    }

    @Test
    fun `print module structure`() {
        val modules = ApplicationModules.of(Application::class.java)
        modules.forEach { println(it) }
    }
}

