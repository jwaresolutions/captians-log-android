package com.captainslog.qr

/**
 * QR Code Multi-Part Assembler
 *
 * Handles sequential scanning and assembly of multi-QR payloads.
 * Maintains state across scans until all parts are collected.
 */
class QrAssembler {
    // Current assembly state
    private var expectedId: String? = null
    private var expectedTotal: Int = 0
    private var currentType: String? = null
    private var currentGeneratedAt: String? = null
    private var currentVersion: Int? = null
    private val parts: MutableMap<Int, String> = mutableMapOf()

    /**
     * Assembly result after adding a QR part
     */
    sealed class AssemblyResult {
        /**
         * More parts needed
         * @param collected Number of parts collected so far
         * @param total Total parts expected
         */
        data class NeedMore(val collected: Int, val total: Int) : AssemblyResult()

        /**
         * All parts collected, ready to decode
         * @param fullBase64Data Concatenated data from all parts
         * @param type QR type ("trip" or "boat")
         * @param id QR envelope ID
         * @param generatedAt ISO timestamp
         * @param version Protocol version
         */
        data class Complete(
            val fullBase64Data: String,
            val type: String,
            val id: String,
            val generatedAt: String,
            val version: Int
        ) : AssemblyResult()

        /**
         * Assembly error (mismatched ID, invalid part number, etc.)
         * @param message Error description
         */
        data class Error(val message: String) : AssemblyResult()
    }

    /**
     * Add a scanned QR part to the assembly
     * @param envelope Parsed QR envelope from a scan
     * @return Assembly result (NeedMore, Complete, or Error)
     */
    fun addPart(envelope: QrProtocol.QrEnvelope): AssemblyResult {
        // First scan - initialize state
        if (expectedId == null) {
            if (envelope.part != 1) {
                return AssemblyResult.Error("First scan must be part 1, got part ${envelope.part}")
            }
            if (envelope.total < 1) {
                return AssemblyResult.Error("Invalid total parts: ${envelope.total}")
            }

            expectedId = envelope.id
            expectedTotal = envelope.total
            currentType = envelope.type
            currentGeneratedAt = envelope.generatedAt
            currentVersion = envelope.version
            parts[envelope.part] = envelope.data

            return if (envelope.total == 1) {
                // Single-part QR - complete immediately
                AssemblyResult.Complete(
                    fullBase64Data = envelope.data,
                    type = envelope.type,
                    id = envelope.id,
                    generatedAt = envelope.generatedAt,
                    version = envelope.version
                )
            } else {
                AssemblyResult.NeedMore(collected = 1, total = envelope.total)
            }
        }

        // Subsequent scans - validate consistency
        if (envelope.id != expectedId) {
            return AssemblyResult.Error(
                "QR code mismatch. Expected ID $expectedId, got ${envelope.id}. " +
                "Please scan parts from the same QR sequence or reset."
            )
        }
        if (envelope.total != expectedTotal) {
            return AssemblyResult.Error(
                "Part count mismatch. Expected $expectedTotal parts, envelope claims ${envelope.total}"
            )
        }
        if (envelope.part < 1 || envelope.part > expectedTotal) {
            return AssemblyResult.Error(
                "Invalid part number: ${envelope.part} (expected 1-$expectedTotal)"
            )
        }

        // Add part (idempotent - duplicate scans are ignored)
        if (parts.containsKey(envelope.part)) {
            // Already scanned this part - no-op
            return AssemblyResult.NeedMore(collected = parts.size, total = expectedTotal)
        }

        parts[envelope.part] = envelope.data

        // Check if complete
        return if (parts.size == expectedTotal) {
            // All parts collected - concatenate in order
            val fullData = (1..expectedTotal).joinToString("") { partNum ->
                parts[partNum] ?: error("Missing part $partNum (should not happen)")
            }

            AssemblyResult.Complete(
                fullBase64Data = fullData,
                type = currentType!!,
                id = expectedId!!,
                generatedAt = currentGeneratedAt!!,
                version = currentVersion!!
            )
        } else {
            AssemblyResult.NeedMore(collected = parts.size, total = expectedTotal)
        }
    }

    /**
     * Reset assembler state (start fresh with new QR sequence)
     */
    fun reset() {
        expectedId = null
        expectedTotal = 0
        currentType = null
        currentGeneratedAt = null
        currentVersion = null
        parts.clear()
    }

    /**
     * Get current assembly progress
     * @return Pair of (collected parts, total parts)
     */
    fun getProgress(): Pair<Int, Int> {
        return Pair(parts.size, expectedTotal)
    }

    /**
     * Check if assembly is in progress (waiting for more parts)
     */
    fun isInProgress(): Boolean {
        return expectedId != null && parts.size < expectedTotal
    }

    /**
     * Get the expected QR ID for the current assembly
     */
    fun getExpectedId(): String? = expectedId
}
