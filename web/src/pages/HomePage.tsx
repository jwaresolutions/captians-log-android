import React, { useState, useEffect, useRef } from 'react'
import { useNavigate } from 'react-router-dom'
import styled, { keyframes } from 'styled-components'

// ---------------------------------------------------------------------------
// LCARS-themed HomePage - Authentic TNG-era interface with starfield
// ---------------------------------------------------------------------------

// -- Keyframe Animations ----------------------------------------------------

const pulse = keyframes`
  0%, 100% { opacity: 1; }
  50% { opacity: 0.4; }
`

const logoGlow = keyframes`
  0%, 100% {
    filter: drop-shadow(0 0 20px rgba(255, 153, 51, 0.6));
  }
  50% {
    filter: drop-shadow(0 0 40px rgba(255, 153, 51, 0.9)) drop-shadow(0 0 60px rgba(255, 153, 51, 0.4));
  }
`

const ticker = keyframes`
  0% { transform: translateX(0); }
  100% { transform: translateX(-50%); }
`

const blink = keyframes`
  0%, 49% { opacity: 1; }
  50%, 100% { opacity: 0; }
`

const fadeIn = keyframes`
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
`

const slideInFromLeft = keyframes`
  from { transform: translateX(-200px); opacity: 0; }
  to { transform: translateX(0); opacity: 0.7; }
`

// -- Styled Components ------------------------------------------------------

const Container = styled.div`
  min-height: 100vh;
  background: #000;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  position: relative;
  padding: 2rem;
`

const StarfieldCanvas = styled.canvas`
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
  opacity: 0.4;
`

const Logo = styled.img`
  width: 400px;
  max-width: 80vw;
  height: auto;
  margin-bottom: 3rem;
  animation: ${logoGlow} 3s ease-in-out infinite, ${fadeIn} 1s ease;
  cursor: pointer;
  transition: transform 0.3s ease;
  z-index: 1;

  &:hover {
    transform: scale(1.05);
  }

  @media (max-width: 768px) {
    width: 250px;
    margin-bottom: 2rem;
  }

  @media (max-width: 480px) {
    width: 200px;
    margin-bottom: 1.5rem;
  }
`

// -- Data Readouts ----------------------------------------------------------

const ReadoutsGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 1.5rem;
  width: 100%;
  max-width: 1000px;
  margin: 2rem 0;
  z-index: 1;
  animation: ${fadeIn} 1s ease 0.3s backwards;
  position: relative;

  @media (max-width: 768px) {
    grid-template-columns: 1fr;
    gap: 1rem;
  }
`

const Readout = styled.div<{ $color: string }>`
  background: rgba(0, 0, 0, 0.8);
  border: 2px solid ${p => p.$color};
  border-radius: 0 16px 16px 0;
  padding: 1rem 1.5rem;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    left: 0;
    top: 0;
    bottom: 0;
    width: 6px;
    background: ${p => p.$color};
  }
`

const ReadoutLabel = styled.div`
  color: #99CCFF;
  font-family: 'Antonio', sans-serif;
  font-size: 0.875rem;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  margin-bottom: 0.5rem;
`

const ReadoutValue = styled.div<{ $color: string }>`
  color: ${p => p.$color};
  font-family: 'Antonio', sans-serif;
  font-size: 1.5rem;
  font-weight: bold;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin-bottom: 0.5rem;
`

const ReadoutMeter = styled.div`
  width: 100%;
  height: 6px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 3px;
  overflow: hidden;
  margin-top: 0.5rem;
`

const ReadoutMeterBar = styled.div<{ $color: string; $percentage: number }>`
  height: 100%;
  background: ${p => p.$color};
  width: ${p => p.$percentage}%;
  transition: width 0.3s ease;
  box-shadow: 0 0 8px ${p => p.$color};
`

// -- Decorative Sliding Bars ------------------------------------------------

const DecorativeBar = styled.div<{ $color: string; $top: string; $delay: number }>`
  position: absolute;
  left: -100px;
  top: ${p => p.$top};
  width: 80px;
  height: 4px;
  background: ${p => p.$color};
  border-radius: 2px;
  opacity: 0;
  animation: ${slideInFromLeft} 1.5s ease-in-out ${p => p.$delay}s forwards;
  z-index: 0;
`

// -- Elbow Decorations ------------------------------------------------------

const ElbowTopLeft = styled.div`
  position: absolute;
  top: 100px;
  left: 40px;
  width: 150px;
  height: 150px;
  border-top: 12px solid #FFCC99;
  border-left: 12px solid #FFCC99;
  border-radius: 40px 0 0 0;
  z-index: 0;
  opacity: 0.6;
  animation: ${fadeIn} 1s ease 0.5s backwards;

  @media (max-width: 768px) {
    width: 80px;
    height: 80px;
    top: 60px;
    left: 20px;
    border-width: 8px;
  }
`

const ElbowBottomRight = styled.div`
  position: absolute;
  bottom: 100px;
  right: 40px;
  width: 150px;
  height: 150px;
  border-bottom: 12px solid #CC99CC;
  border-right: 12px solid #CC99CC;
  border-radius: 0 0 40px 0;
  z-index: 0;
  opacity: 0.6;
  animation: ${fadeIn} 1s ease 0.7s backwards;

  @media (max-width: 768px) {
    width: 80px;
    height: 80px;
    bottom: 60px;
    right: 20px;
    border-width: 8px;
  }
`

// -- Indicator Lights -------------------------------------------------------

const IndicatorContainer = styled.div`
  display: flex;
  gap: 1rem;
  margin: 2rem 0;
  z-index: 1;
  animation: ${fadeIn} 1s ease 0.5s backwards;
`

const Indicator = styled.div<{ $color: string; $delay: string }>`
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: ${p => p.$color};
  box-shadow: 0 0 10px ${p => p.$color};
  animation: ${pulse} 2s ease-in-out ${p => p.$delay} infinite;
`

// -- Quick Access Buttons ---------------------------------------------------

const ButtonGrid = styled.div`
  display: flex;
  gap: 1rem;
  margin-top: 2rem;
  z-index: 1;
  flex-wrap: wrap;
  justify-content: center;
  animation: ${fadeIn} 1s ease 0.7s backwards;

  @media (max-width: 768px) {
    flex-direction: column;
    width: 100%;
    max-width: 300px;
  }
`

const QuickButton = styled.button<{ $color: string }>`
  background: ${p => p.$color};
  color: #000;
  border: none;
  padding: 1rem 2rem;
  font-family: 'Antonio', sans-serif;
  font-size: 1.125rem;
  font-weight: bold;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  border-radius: 0 24px 24px 0;
  cursor: pointer;
  transition: all 0.2s ease;
  position: relative;
  overflow: hidden;

  &::after {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(255, 255, 255, 0.3);
    transform: translateX(-100%);
    transition: transform 0.3s ease;
  }

  &:hover {
    filter: brightness(1.2);
    box-shadow: 0 0 20px ${p => p.$color};
  }

  &:hover::after {
    transform: translateX(0);
  }

  &:active {
    transform: scale(0.98);
  }
`

// -- Scrolling Ticker -------------------------------------------------------

const TickerContainer = styled.div`
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 50px;
  background: rgba(102, 68, 102, 0.9);
  border-top: 3px solid #FFCC66;
  overflow: hidden;
  display: flex;
  align-items: center;
  z-index: 2;
`

const TickerContent = styled.div`
  display: flex;
  white-space: nowrap;
  animation: ${ticker} 40s linear infinite;
  gap: 3rem;
`

const TickerItem = styled.span`
  color: #FFCC99;
  font-family: 'Antonio', sans-serif;
  font-size: 1rem;
  text-transform: uppercase;
  letter-spacing: 0.15em;
  font-weight: 600;

  &::before {
    content: '●';
    color: #FF9933;
    margin-right: 1rem;
    animation: ${blink} 1.5s ease-in-out infinite;
  }
`

// -- Stardate Display -------------------------------------------------------

const StardateDisplay = styled.div`
  position: absolute;
  top: 2rem;
  right: 2rem;
  background: rgba(0, 0, 0, 0.8);
  border: 2px solid #99CCFF;
  border-radius: 0 16px 16px 0;
  padding: 0.75rem 1.5rem;
  z-index: 1;
  animation: ${fadeIn} 1s ease 0.9s backwards;

  @media (max-width: 768px) {
    top: 1rem;
    right: 1rem;
    padding: 0.5rem 1rem;
  }
`

const StardateLabel = styled.div`
  color: #99CCFF;
  font-family: 'Antonio', sans-serif;
  font-size: 0.75rem;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  margin-bottom: 0.25rem;
`

const StardateValue = styled.div`
  color: #FFCC66;
  font-family: 'Antonio', sans-serif;
  font-size: 1.25rem;
  font-weight: bold;
  letter-spacing: 0.05em;
`

const LCARSIndicator = styled.div`
  position: absolute;
  top: 2rem;
  left: 2rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: rgba(0, 0, 0, 0.8);
  border: 2px solid #FF9933;
  border-radius: 0 16px 16px 0;
  padding: 0.5rem 1rem;
  z-index: 1;
  animation: ${fadeIn} 1s ease 0.9s backwards;

  @media (max-width: 768px) {
    top: 1rem;
    left: 1rem;
    padding: 0.4rem 0.8rem;
  }
`

const LCARSText = styled.span`
  color: #FF9933;
  font-family: 'Antonio', sans-serif;
  font-size: 0.875rem;
  font-weight: bold;
  text-transform: uppercase;
  letter-spacing: 0.15em;
`

const LCARSDot = styled.div`
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #FF9933;
  box-shadow: 0 0 10px #FF9933;
  animation: ${blink} 1s ease-in-out infinite;
`

// ---------------------------------------------------------------------------
// Helper Functions
// ---------------------------------------------------------------------------

function getStardate(): string {
  const now = new Date()
  const year = now.getFullYear()
  const startOfYear = new Date(year, 0, 1).getTime()
  const endOfYear = new Date(year + 1, 0, 1).getTime()
  const dayFraction = (now.getTime() - startOfYear) / (endOfYear - startOfYear)
  const stardate = (year - 2323) * 1000 + dayFraction * 1000
  return stardate.toFixed(5)
}

function randomRange(min: number, max: number): number {
  return min + Math.random() * (max - min)
}

// ---------------------------------------------------------------------------
// Starfield Component
// ---------------------------------------------------------------------------

interface Star {
  x: number
  y: number
  z: number
  prevX?: number
  prevY?: number
}

const Starfield: React.FC = () => {
  const canvasRef = useRef<HTMLCanvasElement>(null)
  const starsRef = useRef<Star[]>([])
  const animationFrameRef = useRef<number>()

  useEffect(() => {
    const canvas = canvasRef.current
    if (!canvas) return

    const ctx = canvas.getContext('2d')
    if (!ctx) return

    const resizeCanvas = () => {
      canvas.width = window.innerWidth
      canvas.height = window.innerHeight
    }

    resizeCanvas()
    window.addEventListener('resize', resizeCanvas)

    // Initialize stars
    const numStars = 200
    const stars: Star[] = []
    for (let i = 0; i < numStars; i++) {
      stars.push({
        x: randomRange(-canvas.width, canvas.width),
        y: randomRange(-canvas.height, canvas.height),
        z: randomRange(0, canvas.width),
      })
    }
    starsRef.current = stars

    // Animation loop
    const animate = () => {
      const width = canvas.width
      const height = canvas.height
      const centerX = width / 2
      const centerY = height / 2

      ctx.fillStyle = 'rgba(0, 0, 0, 0.1)'
      ctx.fillRect(0, 0, width, height)

      stars.forEach(star => {
        star.z -= 2

        if (star.z <= 0) {
          star.x = randomRange(-width, width)
          star.y = randomRange(-height, height)
          star.z = width
          star.prevX = undefined
          star.prevY = undefined
        }

        const k = 128 / star.z
        const px = star.x * k + centerX
        const py = star.y * k + centerY

        if (px >= 0 && px <= width && py >= 0 && py <= height) {
          const size = (1 - star.z / width) * 2
          const brightness = Math.floor((1 - star.z / width) * 255)
          const alpha = 0.5 + (1 - star.z / width) * 0.5

          if (star.prevX !== undefined && star.prevY !== undefined) {
            ctx.strokeStyle = `rgba(${brightness}, ${brightness}, 255, ${alpha * 0.5})`
            ctx.lineWidth = size * 0.5
            ctx.beginPath()
            ctx.moveTo(star.prevX, star.prevY)
            ctx.lineTo(px, py)
            ctx.stroke()
          }

          ctx.fillStyle = `rgba(${brightness}, ${brightness}, 255, ${alpha})`
          ctx.beginPath()
          ctx.arc(px, py, size, 0, Math.PI * 2)
          ctx.fill()

          star.prevX = px
          star.prevY = py
        }
      })

      animationFrameRef.current = requestAnimationFrame(animate)
    }

    animate()

    return () => {
      window.removeEventListener('resize', resizeCanvas)
      if (animationFrameRef.current) {
        cancelAnimationFrame(animationFrameRef.current)
      }
    }
  }, [])

  return <StarfieldCanvas ref={canvasRef} />
}

// ---------------------------------------------------------------------------
// Component
// ---------------------------------------------------------------------------

export const HomePage: React.FC = () => {
  const navigate = useNavigate()
  const [stardate, setStardate] = useState(getStardate())

  // Dynamic readout values
  const [shieldFreq, setShieldFreq] = useState(257.4)
  const [antiMatter, setAntiMatter] = useState(1.247)
  const [commSignal, setCommSignal] = useState(97.3)
  const [warpOutput, setWarpOutput] = useState(1547.2)
  const [sensorRes, setSensorRes] = useState(0.0042)
  const [lifeSupport, setLifeSupport] = useState(99.7)

  // Decorative bars state
  const [decorativeBars, setDecorativeBars] = useState<Array<{ color: string; top: string; delay: number }>>([])

  // Update stardate every 3 seconds
  useEffect(() => {
    const interval = setInterval(() => {
      setStardate(getStardate())
    }, 3000)
    return () => clearInterval(interval)
  }, [])

  // Shield Harmonic Frequency: 250-280 MHz, every 500ms
  useEffect(() => {
    const interval = setInterval(() => {
      setShieldFreq(randomRange(250, 280))
    }, 500)
    return () => clearInterval(interval)
  }, [])

  // Anti-Matter Injection: 1.1-1.4 cm³/s, every 300ms
  useEffect(() => {
    const interval = setInterval(() => {
      setAntiMatter(randomRange(1.1, 1.4))
    }, 300)
    return () => clearInterval(interval)
  }, [])

  // Communications Uplink: 94-100%, every 600ms
  useEffect(() => {
    const interval = setInterval(() => {
      setCommSignal(randomRange(94, 100))
    }, 600)
    return () => clearInterval(interval)
  }, [])

  // Warp Core Output: 1500-1600 TW, every 400ms
  useEffect(() => {
    const interval = setInterval(() => {
      setWarpOutput(randomRange(1500, 1600))
    }, 400)
    return () => clearInterval(interval)
  }, [])

  // Sensor Array Resolution: 0.003-0.006 arc-sec, every 700ms
  useEffect(() => {
    const interval = setInterval(() => {
      setSensorRes(randomRange(0.003, 0.006))
    }, 700)
    return () => clearInterval(interval)
  }, [])

  // Life Support Efficiency: 98.5-100%, every 550ms
  useEffect(() => {
    const interval = setInterval(() => {
      setLifeSupport(randomRange(98.5, 100))
    }, 550)
    return () => clearInterval(interval)
  }, [])

  // Add decorative sliding bars at random intervals
  useEffect(() => {
    const colors = ['#FFCC99', '#99CCFF', '#CC99CC', '#FFCC66', '#FF9933']
    const addBar = () => {
      const newBar = {
        color: colors[Math.floor(Math.random() * colors.length)],
        top: `${randomRange(20, 80)}%`,
        delay: 0,
      }
      setDecorativeBars(prev => [...prev, newBar].slice(-6)) // Keep max 6 bars
    }

    const interval = setInterval(addBar, randomRange(3000, 6000))
    return () => clearInterval(interval)
  }, [])

  const tickerMessages = [
    'All systems nominal',
    'Warp core stable',
    'Navigation array calibrated',
    'Subspace communications active',
    'Deflector shields online',
    'Sensors operating at peak efficiency',
    'Life support systems optimal',
    'Transporter standing by',
    'Quantum slipstream drive ready',
    'Temporal sensors synchronized',
  ]

  const readouts = [
    {
      label: 'Shield Harmonic Frequency',
      value: `${shieldFreq.toFixed(1)} MHz`,
      color: '#99CCFF',
      percentage: ((shieldFreq - 250) / (280 - 250)) * 100,
    },
    {
      label: 'Anti-Matter Injection Flow',
      value: `${antiMatter.toFixed(3)} cm³/s`,
      color: '#FFCC66',
      percentage: ((antiMatter - 1.1) / (1.4 - 1.1)) * 100,
    },
    {
      label: 'Communications Uplink Signal',
      value: `${commSignal.toFixed(1)}%`,
      color: '#99CCFF',
      percentage: ((commSignal - 94) / (100 - 94)) * 100,
    },
    {
      label: 'Warp Core Output',
      value: `${warpOutput.toLocaleString('en-US', { minimumFractionDigits: 1, maximumFractionDigits: 1 })} TW`,
      color: '#FFCC99',
      percentage: ((warpOutput - 1500) / (1600 - 1500)) * 100,
    },
    {
      label: 'Sensor Array Resolution',
      value: `${sensorRes.toFixed(4)} arc-sec`,
      color: '#99CCFF',
      percentage: ((sensorRes - 0.003) / (0.006 - 0.003)) * 100,
    },
    {
      label: 'Life Support Efficiency',
      value: `${lifeSupport.toFixed(1)}%`,
      color: '#CC99CC',
      percentage: ((lifeSupport - 98.5) / (100 - 98.5)) * 100,
    },
  ]

  const indicators = [
    { color: '#FF9933', delay: '0s' },
    { color: '#99CCFF', delay: '0.3s' },
    { color: '#CC99CC', delay: '0.6s' },
    { color: '#FFCC66', delay: '0.9s' },
    { color: '#99CCFF', delay: '1.2s' },
  ]

  return (
    <Container>
      {/* Starfield Background */}
      <Starfield />

      {/* Stardate Display */}
      <StardateDisplay>
        <StardateLabel>Stardate</StardateLabel>
        <StardateValue>{stardate}</StardateValue>
      </StardateDisplay>

      {/* LCARS Activity Indicator */}
      <LCARSIndicator>
        <LCARSText>LCARS</LCARSText>
        <LCARSDot />
      </LCARSIndicator>

      {/* Decorative Elbows */}
      <ElbowTopLeft />
      <ElbowBottomRight />

      {/* Logo */}
      <Logo
        src="/assets/captains-log-logo.png"
        alt="Captain's Log"
        onClick={() => navigate('/dashboard')}
      />

      {/* Indicator Lights */}
      <IndicatorContainer>
        {indicators.map((ind, i) => (
          <Indicator key={i} $color={ind.color} $delay={ind.delay} />
        ))}
      </IndicatorContainer>

      {/* Data Readouts */}
      <ReadoutsGrid>
        {decorativeBars.map((bar, i) => (
          <DecorativeBar key={i} $color={bar.color} $top={bar.top} $delay={bar.delay} />
        ))}
        {readouts.map((readout, i) => (
          <Readout key={i} $color={readout.color}>
            <ReadoutLabel>{readout.label}</ReadoutLabel>
            <ReadoutValue $color={readout.color}>{readout.value}</ReadoutValue>
            <ReadoutMeter>
              <ReadoutMeterBar $color={readout.color} $percentage={readout.percentage} />
            </ReadoutMeter>
          </Readout>
        ))}
      </ReadoutsGrid>

      {/* Quick Access Buttons */}
      <ButtonGrid>
        <QuickButton $color="#FFCC99" onClick={() => navigate('/dashboard')}>
          Dashboard
        </QuickButton>
        <QuickButton $color="#99CCFF" onClick={() => navigate('/trips')}>
          Trip Log
        </QuickButton>
        <QuickButton $color="#CC99CC" onClick={() => navigate('/boats')}>
          Vessels
        </QuickButton>
      </ButtonGrid>

      {/* Scrolling Ticker */}
      <TickerContainer>
        <TickerContent>
          {tickerMessages.concat(tickerMessages).map((msg, i) => (
            <TickerItem key={i}>{msg}</TickerItem>
          ))}
        </TickerContent>
      </TickerContainer>
    </Container>
  )
}
