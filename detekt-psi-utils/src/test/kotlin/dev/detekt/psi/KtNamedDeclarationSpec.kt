package dev.detekt.psi

import dev.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class KtNamedDeclarationSpec {

    @Nested
    inner class IsSingleUnderscore {
        @Test
        fun `is true for an anonymous function parameter named with a single underscore`() {
            val code = """
                val lambda = fun(_: Int) = Unit
            """.trimIndent()

            assertThat(firstParameter(code).isSingleUnderscore()).isTrue()
        }

        @Test
        fun `is false for an anonymous function parameter with a regular name`() {
            val code = """
                val lambda = fun(value: Int) = Unit
            """.trimIndent()

            assertThat(firstParameter(code).isSingleUnderscore()).isFalse()
        }

        @Test
        fun `is false for a backtick-escaped underscore parameter`() {
            val code = """
                val lambda = fun(`_`: Int) = Unit
            """.trimIndent()

            assertThat(firstParameter(code).isSingleUnderscore()).isFalse()
        }

        @Test
        fun `is false for a function type parameter that has no name identifier`() {
            val code = """
                val callback: (Int) -> Unit = { }
            """.trimIndent()

            assertThat(firstParameter(code).isSingleUnderscore()).isFalse()
        }

        @Test
        fun `is true for a local property named with a single underscore`() {
            val code = """
                fun foo() {
                    val _ = 5
                }
            """.trimIndent()

            assertThat(firstProperty(code).isSingleUnderscore()).isTrue()
        }

        @Test
        fun `is false for a backtick-escaped underscore property`() {
            val code = "val `_` = 3"

            assertThat(firstProperty(code).isSingleUnderscore()).isFalse()
        }

        @Test
        fun `is false for a property with a regular name`() {
            val code = "val a = 3"

            assertThat(firstProperty(code).isSingleUnderscore()).isFalse()
        }
    }

    private fun firstParameter(@Language("kotlin") code: String): KtParameter =
        checkNotNull(compileContentForTest(code).findDescendantOfType<KtParameter>())

    private fun firstProperty(@Language("kotlin") code: String): KtNamedDeclaration =
        checkNotNull(compileContentForTest(code).findDescendantOfType<KtProperty>())
}
