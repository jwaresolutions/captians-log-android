package com.captainslog.util

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll

/**
 * Property-based tests for input validation
 * **Feature: boat-tracking-system, Property 54: Input Validation**
 */
class InputValidationPropertyTest : StringSpec({

    /**
     * **Feature: boat-tracking-system, Property 54: Input Validation**
     * **Validates: Requirements 19.6**
     * 
     * For any input data, the system should reject invalid inputs appropriately
     * and accept valid inputs without error.
     */
    "Property 54: Input Validation - should reject inputs with missing required fields".config(
        invocations = 100
    ) {
        checkAll(
            Arb.string(1..10)
        ) { fieldName: String ->
            // Create data missing the required field
            val requiredFields = listOf(fieldName)
            val invalidData = mapOf<String, Any?>() // Empty data, missing required field
            
            // Should return false for missing required fields
            InputValidator.validateRequired(invalidData, requiredFields) shouldBe false
        }
    }

    "Property 54: Input Validation - should accept inputs with all required fields present".config(
        invocations = 100
    ) {
        checkAll(
            Arb.string(1..10)
        ) { fieldName: String ->
            // Create data with the required field present
            val requiredFields = listOf(fieldName)
            val validData = mapOf<String, Any?>(fieldName to "valid_value")
            
            // Should return true for valid data with all required fields
            InputValidator.validateRequired(validData, requiredFields) shouldBe true
        }
    }

    "Property 54: Input Validation - should reject strings that exceed maximum length".config(
        invocations = 100
    ) {
        checkAll(
            Arb.string(101..200), // Strings longer than 100 chars
            Arb.int(1..100) // Max length between 1-100
        ) { longString, maxLength ->
            // Should return false for strings exceeding max length
            InputValidator.validateStringLength(longString, "testField", null, maxLength) shouldBe false
        }
    }

    "Property 54: Input Validation - should reject strings shorter than minimum length".config(
        invocations = 100
    ) {
        checkAll(
            Arb.string(0..4), // Short strings
            Arb.int(5..20) // Min length between 5-20
        ) { shortString, minLength ->
            // Should return false for strings below min length
            InputValidator.validateStringLength(shortString, "testField", minLength, null) shouldBe false
        }
    }

    "Property 54: Input Validation - should accept strings within valid length range".config(
        invocations = 100
    ) {
        checkAll(
            Arb.int(5..50),
            Arb.int(51..100)
        ) { minLength, maxLength ->
            val validString = "a".repeat(minLength + (maxLength - minLength) / 2)
            
            // Should return true for strings within valid range
            InputValidator.validateStringLength(validString, "testField", minLength, maxLength) shouldBe true
        }
    }

    "Property 54: Input Validation - should reject invalid email formats".config(
        invocations = 100
    ) {
        checkAll(
            Arb.choice(
                Arb.string().filter { !it.contains('@') }, // No @ symbol
                Arb.string().filter { it.contains('@') && !it.contains('.') }, // @ but no dot
                Arb.constant(""), // Empty string
                Arb.constant("@"), // Just @
                Arb.constant("user@"), // Missing domain
                Arb.constant("@domain.com"), // Missing user
                Arb.constant("user@domain") // Missing TLD
            )
        ) { invalidEmail ->
            // Should return false for invalid email formats
            InputValidator.validateEmail(invalidEmail) shouldBe false
        }
    }

    "Property 54: Input Validation - should accept valid email formats".config(
        invocations = 100
    ) {
        checkAll(
            Arb.string(1..10).filter { it.matches(Regex("[a-zA-Z0-9]+")) },
            Arb.string(1..10).filter { it.matches(Regex("[a-zA-Z0-9]+")) },
            Arb.choice(Arb.constant("com"), Arb.constant("org"), Arb.constant("net"))
        ) { user: String, domain: String, tld: String ->
            val validEmail = "$user@$domain.$tld"
            
            // Should return true for valid email formats
            InputValidator.validateEmail(validEmail) shouldBe true
        }
    }

    "Property 54: Input Validation - should reject numbers outside valid range".config(
        invocations = 100
    ) {
        checkAll(
            Arb.double(-1000.0..-1.0), // Numbers below range
            Arb.int(0..100), // Valid range min
            Arb.int(101..200) // Valid range max
        ) { belowRange: Double, rangeMin: Int, rangeMax: Int ->
            // Test number below range
            InputValidator.validateNumberRange(belowRange, "testField", rangeMin.toDouble(), rangeMax.toDouble()) shouldBe false
            
            // Test number above range
            val aboveRange = rangeMax + kotlin.math.abs(belowRange)
            InputValidator.validateNumberRange(aboveRange, "testField", rangeMin.toDouble(), rangeMax.toDouble()) shouldBe false
        }
    }

    "Property 54: Input Validation - should accept numbers within valid range".config(
        invocations = 100
    ) {
        checkAll(
            Arb.int(0..50),
            Arb.int(51..100)
        ) { rangeMin: Int, rangeMax: Int ->
            val validNumber = (rangeMin + (rangeMax - rangeMin) / 2).toDouble()
            
            // Should return true for numbers within valid range
            InputValidator.validateNumberRange(validNumber, "testField", rangeMin.toDouble(), rangeMax.toDouble()) shouldBe true
        }
    }

    "Property 54: Input Validation - should reject invalid GPS coordinates".config(
        invocations = 100
    ) {
        checkAll(
            Arb.choice(
                Arb.double(-180.0..-90.1), // Invalid latitude (too low)
                Arb.double(90.1..180.0) // Invalid latitude (too high)
            ),
            Arb.choice(
                Arb.double(-360.0..-180.1), // Invalid longitude (too low)
                Arb.double(180.1..360.0) // Invalid longitude (too high)
            )
        ) { invalidLat: Double, invalidLon: Double ->
            // Should return false for invalid coordinates
            InputValidator.validateGPSCoordinates(invalidLat, 0.0) shouldBe false
            InputValidator.validateGPSCoordinates(0.0, invalidLon) shouldBe false
        }
    }

    "Property 54: Input Validation - should accept valid GPS coordinates".config(
        invocations = 100
    ) {
        checkAll(
            Arb.double(-90.0..90.0), // Valid latitude
            Arb.double(-180.0..180.0) // Valid longitude
        ) { validLat: Double, validLon: Double ->
            // Filter out NaN and infinite values
            if (!validLat.isNaN() && !validLat.isInfinite() && !validLon.isNaN() && !validLon.isInfinite()) {
                // Should return true for valid coordinates
                InputValidator.validateGPSCoordinates(validLat, validLon) shouldBe true
            }
        }
    }

    "Property 54: Input Validation - should reject invalid enum values".config(
        invocations = 100
    ) {
        checkAll(
            Arb.string().filter { !listOf("option1", "option2", "option3").contains(it) }
        ) { invalidValue: String ->
            val allowedValues = listOf("option1", "option2", "option3")
            
            // Should return false for invalid enum values
            InputValidator.validateEnum(invalidValue, allowedValues, "testField") shouldBe false
        }
    }

    "Property 54: Input Validation - should accept valid enum values".config(
        invocations = 100
    ) {
        checkAll(
            Arb.choice(Arb.constant("option1"), Arb.constant("option2"), Arb.constant("option3"))
        ) { validValue: String ->
            val allowedValues = listOf("option1", "option2", "option3")
            
            // Should return true for valid enum values
            InputValidator.validateEnum(validValue, allowedValues, "testField") shouldBe true
        }
    }

    "Property 54: Input Validation - should sanitize potentially dangerous strings".config(
        invocations = 100
    ) {
        checkAll(
            Arb.string(),
            Arb.list(Arb.choice(Arb.constant("<"), Arb.constant(">"), Arb.constant("<script>"), Arb.constant("</script>")), 0..5)
        ) { baseString: String, dangerousChars: List<String> ->
            val dangerousString = baseString + dangerousChars.joinToString("")
            val sanitized = InputValidator.sanitizeString(dangerousString)
            
            // Sanitized string should not contain < or > characters
            sanitized.contains('<') shouldBe false
            sanitized.contains('>') shouldBe false
        }
    }

    "Property 54: Input Validation - should validate boat data correctly".config(
        invocations = 100
    ) {
        checkAll(
            Arb.string(1..100)
        ) { name: String ->
            val boatData = mapOf<String, Any?>("name" to name)
            
            // Valid name should pass validation
            InputValidator.validateBoatData(boatData) shouldBe true
        }
    }

    "Property 54: Input Validation - should reject invalid boat data".config(
        invocations = 100
    ) {
        checkAll(
            Arb.string(101..200)
        ) { longName: String ->
            val boatData = mapOf<String, Any?>("name" to longName)
            
            // Name too long should fail validation
            InputValidator.validateBoatData(boatData) shouldBe false
        }
    }

    "Property 54: Input Validation - should validate trip data correctly".config(
        invocations = 100
    ) {
        checkAll(
            Arb.string(1..50).filter { it.isNotBlank() },
            Arb.string(1..50).filter { it.isNotBlank() }
        ) { boatId: String, startTime: String ->
            val tripData = mapOf<String, Any?>(
                "boatId" to boatId,
                "startTime" to startTime,
                "waterType" to "inland",
                "role" to "captain"
            )
            
            // Valid trip data should pass validation
            InputValidator.validateTripData(tripData) shouldBe true
        }
    }

    "Property 54: Input Validation - should reject invalid trip data".config(
        invocations = 100
    ) {
        checkAll(
            Arb.string(1..50)
        ) { startTime: String ->
            val tripData = mapOf<String, Any?>(
                "startTime" to startTime
                // Missing required boatId
            )
            
            // Missing required field should fail validation
            InputValidator.validateTripData(tripData) shouldBe false
        }
    }

    "Property 54: Input Validation - should validate GPS point data correctly".config(
        invocations = 100
    ) {
        checkAll(
            Arb.double(-90.0..90.0),
            Arb.double(-180.0..180.0),
            Arb.string(1..50).filter { it.isNotBlank() }
        ) { latitude: Double, longitude: Double, timestamp: String ->
            // Filter out NaN and infinite values
            if (!latitude.isNaN() && !latitude.isInfinite() && !longitude.isNaN() && !longitude.isInfinite()) {
                val gpsData = mapOf<String, Any?>(
                    "latitude" to latitude,
                    "longitude" to longitude,
                    "timestamp" to timestamp,
                    "speed" to 10.0,
                    "heading" to 180.0
                )
                
                // Valid GPS data should pass validation
                InputValidator.validateGPSPointData(gpsData) shouldBe true
            }
        }
    }

    "Property 54: Input Validation - should reject invalid GPS point data".config(
        invocations = 100
    ) {
        checkAll(
            Arb.string(1..50)
        ) { timestamp: String ->
            val gpsData = mapOf<String, Any?>(
                "timestamp" to timestamp
                // Missing required latitude and longitude
            )
            
            // Missing required fields should fail validation
            InputValidator.validateGPSPointData(gpsData) shouldBe false
        }
    }
})