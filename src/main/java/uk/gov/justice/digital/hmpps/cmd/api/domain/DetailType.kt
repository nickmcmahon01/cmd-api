package uk.gov.justice.digital.hmpps.cmd.api.uk.gov.justice.digital.hmpps.cmd.api.domain

import java.util.*

enum class DetailType(val description: String) {
    UNSPECIFIC("Unspecific"),
    BREAK("Break"),
    ILLNESS("Illness"),
    HOLIDAY("Annual Leave"),
    ABSENCE("Absence"),
    MEETING("Meeting"),
    ONCALL("On Call"),
    NONE("None"),
    SHIFT("Shift"),
    REST_DAY("Rest Day"),
    TRAINING_EXTERNAL("Training - External"),
    TRAINING_INTERNAL("Training - Internal");

    companion object {
        fun from(value: String): DetailType {
            return Arrays.stream(values())
                    .filter { type -> value.contains(type.description, true) }
                    .findFirst().orElse(NONE)
        }
    }
} 