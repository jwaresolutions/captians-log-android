export interface AISVessel {
  mmsi: number
  name: string
  latitude: number
  longitude: number
  heading: number
  speed: number
  shipType: number
  timestamp: number
}

type AISCallback = (vessels: AISVessel[]) => void

export class AISStreamService {
  private ws: WebSocket | null = null
  private vessels: Map<number, AISVessel> = new Map()
  private callback: AISCallback | null = null

  connect(
    apiKey: string,
    boundingBox: [[number, number], [number, number]],
    onUpdate: AISCallback
  ) {
    this.callback = onUpdate
    this.disconnect()

    this.ws = new WebSocket('wss://stream.aisstream.io/v0/stream')

    this.ws.onopen = () => {
      this.ws?.send(JSON.stringify({
        APIKey: apiKey,
        BoundingBoxes: [boundingBox],
      }))
    }

    this.ws.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data)
        if (data.Message?.PositionReport) {
          const report = data.Message.PositionReport
          const meta = data.MetaData
          const vessel: AISVessel = {
            mmsi: meta.MMSI,
            name: meta.ShipName?.trim() || `MMSI ${meta.MMSI}`,
            latitude: report.Latitude,
            longitude: report.Longitude,
            heading: report.TrueHeading ?? report.Cog ?? 0,
            speed: report.Sog ?? 0,
            shipType: meta.ShipType ?? 0,
            timestamp: Date.now(),
          }
          this.vessels.set(vessel.mmsi, vessel)

          // Prune stale vessels (older than 10 minutes)
          const cutoff = Date.now() - 600_000
          for (const [mmsi, v] of this.vessels) {
            if (v.timestamp < cutoff) this.vessels.delete(mmsi)
          }

          this.callback?.(Array.from(this.vessels.values()))
        }
      } catch { /* ignore parse errors */ }
    }

    this.ws.onerror = () => {
      // Silent reconnect after 5s
      setTimeout(() => {
        if (this.callback) this.connect(apiKey, boundingBox, this.callback)
      }, 5000)
    }

    this.ws.onclose = () => {
      // No-op; reconnect handled by onerror if unexpected
    }
  }

  disconnect() {
    this.ws?.close()
    this.ws = null
    this.vessels.clear()
  }

  getVessels(): AISVessel[] {
    return Array.from(this.vessels.values())
  }
}
