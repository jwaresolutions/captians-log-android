/**
 * QR Code Protocol for Captain's Log Android
 *
 * Client-side module for encoding trip and boat data into QR code payloads.
 * Handles compression, chunking, and reassembly of data.
 *
 * Dependencies: pako (gzip compression library, loaded via CDN)
 */

(function(window) {
  'use strict';

  // Protocol constants
  const PROTOCOL_VERSION = 1;
  const MAX_QR_CHARS = 2000;

  /**
   * Generate a simple UUID v4
   * @returns {string} UUID string
   */
  function generateUUID() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
      const r = Math.random() * 16 | 0;
      const v = c === 'x' ? r : (r & 0x3 | 0x8);
      return v.toString(16);
    });
  }

  /**
   * Encode data into one or more QR code payload strings
   *
   * @param {string} type - "trip" or "boat"
   * @param {Object|Array} data - The data to encode
   * @returns {string[]} Array of QR payload strings (JSON envelopes)
   * @throws {Error} If pako is not available or encoding fails
   */
  function encodePayload(type, data) {
    // Validate pako is available
    if (typeof pako === 'undefined') {
      throw new Error('pako library is not loaded. Include pako via CDN script tag.');
    }

    // Validate type
    if (type !== 'trip' && type !== 'boat') {
      throw new Error('Type must be "trip" or "boat"');
    }

    // Generate metadata
    const id = generateUUID();
    const generatedAt = new Date().toISOString();

    // Serialize to JSON
    const jsonString = JSON.stringify(data);

    // Compress with pako (gzip)
    const compressed = pako.gzip(jsonString);

    // Base64 encode the compressed bytes
    const base64Data = btoa(String.fromCharCode.apply(null, compressed));

    // Create envelope(s)
    const envelopes = [];
    const baseEnvelope = {
      v: PROTOCOL_VERSION,
      type: type,
      id: id,
      generatedAt: generatedAt
    };

    // Check if we need to chunk the data
    const testEnvelope = JSON.stringify({
      ...baseEnvelope,
      part: 1,
      total: 1,
      data: base64Data
    });

    if (testEnvelope.length <= MAX_QR_CHARS) {
      // Single QR code is sufficient
      envelopes.push(JSON.stringify({
        ...baseEnvelope,
        part: 1,
        total: 1,
        data: base64Data
      }));
    } else {
      // Need to chunk the base64 data
      // Calculate overhead for envelope structure
      const envelopeOverhead = JSON.stringify({
        ...baseEnvelope,
        part: 999,
        total: 999,
        data: ''
      }).length;

      const maxDataPerChunk = MAX_QR_CHARS - envelopeOverhead;
      const totalChunks = Math.ceil(base64Data.length / maxDataPerChunk);

      for (let i = 0; i < totalChunks; i++) {
        const start = i * maxDataPerChunk;
        const end = Math.min(start + maxDataPerChunk, base64Data.length);
        const chunk = base64Data.substring(start, end);

        const envelope = JSON.stringify({
          ...baseEnvelope,
          part: i + 1,
          total: totalChunks,
          data: chunk
        });

        // Safety check
        if (envelope.length > MAX_QR_CHARS) {
          throw new Error(`Chunk ${i + 1} exceeds MAX_QR_CHARS limit. This should not happen.`);
        }

        envelopes.push(envelope);
      }
    }

    return envelopes;
  }

  /**
   * Decode and reassemble QR code payloads
   *
   * @param {string[]} qrStrings - Array of QR JSON envelope strings
   * @returns {Object} Decoded payload with {type, data, id, generatedAt, version}
   * @throws {Error} If validation fails or decoding fails
   */
  function decodePayload(qrStrings) {
    // Validate pako is available
    if (typeof pako === 'undefined') {
      throw new Error('pako library is not loaded. Include pako via CDN script tag.');
    }

    if (!Array.isArray(qrStrings) || qrStrings.length === 0) {
      throw new Error('qrStrings must be a non-empty array');
    }

    // Parse all envelopes
    const envelopes = qrStrings.map((str, idx) => {
      try {
        return JSON.parse(str);
      } catch (e) {
        throw new Error(`Failed to parse QR string at index ${idx}: ${e.message}`);
      }
    });

    // Validate all envelopes
    const firstEnvelope = envelopes[0];
    const { v, type, id, generatedAt, total } = firstEnvelope;

    // Check protocol version
    if (v !== PROTOCOL_VERSION) {
      throw new Error(`Unsupported protocol version: ${v}. Expected ${PROTOCOL_VERSION}`);
    }

    // Check type
    if (type !== 'trip' && type !== 'boat') {
      throw new Error(`Invalid type: ${type}. Must be "trip" or "boat"`);
    }

    // Validate all envelopes match
    for (let i = 0; i < envelopes.length; i++) {
      const env = envelopes[i];

      if (env.v !== v) {
        throw new Error(`Version mismatch at part ${i + 1}`);
      }
      if (env.type !== type) {
        throw new Error(`Type mismatch at part ${i + 1}`);
      }
      if (env.id !== id) {
        throw new Error(`ID mismatch at part ${i + 1}. Parts do not belong to the same payload.`);
      }
      if (env.total !== total) {
        throw new Error(`Total mismatch at part ${i + 1}`);
      }
    }

    // Check we have all parts
    if (envelopes.length !== total) {
      throw new Error(`Missing parts. Expected ${total} parts, got ${envelopes.length}`);
    }

    // Sort by part number
    envelopes.sort((a, b) => a.part - b.part);

    // Validate part numbers are sequential
    for (let i = 0; i < envelopes.length; i++) {
      if (envelopes[i].part !== i + 1) {
        throw new Error(`Missing part ${i + 1}`);
      }
    }

    // Reassemble base64 data
    const base64Data = envelopes.map(env => env.data).join('');

    // Base64 decode
    let compressed;
    try {
      const binaryString = atob(base64Data);
      const len = binaryString.length;
      compressed = new Uint8Array(len);
      for (let i = 0; i < len; i++) {
        compressed[i] = binaryString.charCodeAt(i);
      }
    } catch (e) {
      throw new Error(`Failed to decode base64 data: ${e.message}`);
    }

    // Decompress with pako
    let jsonString;
    try {
      jsonString = pako.ungzip(compressed, { to: 'string' });
    } catch (e) {
      throw new Error(`Failed to decompress data: ${e.message}`);
    }

    // Parse JSON
    let data;
    try {
      data = JSON.parse(jsonString);
    } catch (e) {
      throw new Error(`Failed to parse decompressed JSON: ${e.message}`);
    }

    return {
      version: v,
      type: type,
      id: id,
      generatedAt: generatedAt,
      data: data
    };
  }

  // Export to window
  window.QrProtocol = {
    PROTOCOL_VERSION: PROTOCOL_VERSION,
    MAX_QR_CHARS: MAX_QR_CHARS,
    generateUUID: generateUUID,
    encodePayload: encodePayload,
    decodePayload: decodePayload
  };

})(window);
