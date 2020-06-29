package uk.gov.justice.digital.hmpps.cmd.api.controllers

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.json.BasicJsonTester
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlConfig
import org.springframework.test.context.jdbc.SqlGroup
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SqlGroup(
        Sql(scripts = ["classpath:snooze/before-test.sql"], config = SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)),
        Sql(scripts = ["classpath:snooze/after-test.sql"], config = SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED), executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
)
@ActiveProfiles(value = ["test"])
@DisplayName("Integration Tests for SnoozePreferencesController")
class SnoozePreferencesControllerIntegrationTest(
        @Autowired val testRestTemplate: TestRestTemplate,
        @Autowired val entityBuilder: EntityWithJwtAuthorisationBuilder
) {
    val jsonTester = BasicJsonTester(this.javaClass)

    @Test
    fun `It returns a notification preference`() {
        val response = getNotificationPreference(A_USER)
        with(response) {
            assertThat(statusCode).isEqualTo(HttpStatus.OK)
            // we use an insert of CURRENT_DATE + 1 in the test data.
            assertThat(jsonTester.from(body)).extractingJsonPathStringValue("$.snoozeDate").isEqualTo(LocalDate.now().plusDays(1).toString())
        }
    }

    @Test
    fun `It doesn't return a notification preference`() {
        val response = getNotificationPreference(A_USER_NO_PREFERENCE)
        with(response) {
            assertThat(statusCode).isEqualTo(HttpStatus.OK)
            assertThat(jsonTester.from(body)).extractingJsonPathStringValue("$.snoozeDate").isNull()
        }
    }

    fun getNotificationPreference(user: String): ResponseEntity<String> =
            testRestTemplate.exchange(
                    NOTIFICATION_PREFERENCES_TEMPLATE,
                    HttpMethod.GET,
                    entityBuilder.entityWithJwtAuthorisation(user, NO_ROLES),
                    String::class.java)

    companion object {
        private const val NOTIFICATION_PREFERENCES_TEMPLATE = "/preferences/notifications"

        private const val A_USER = "API_TEST_USER"
        private const val A_USER_NO_PREFERENCE = "API_TEST_USER_NP"
        private val NO_ROLES = listOf<String>()
    }
}