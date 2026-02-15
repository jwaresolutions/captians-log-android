import { createGlobalStyle } from 'styled-components'

export const GlobalStyles = createGlobalStyle`
  /* Import Antonio font from Google Fonts - full weight range for LCARS */
  @import url('https://fonts.googleapis.com/css2?family=Antonio:wght@100;200;300;400;500;600;700&display=swap');

  /* CSS Reset */
  *, *::before, *::after {
    box-sizing: border-box;
  }

  * {
    margin: 0;
    padding: 0;
  }

  html, body {
    height: 100%;
    overflow-x: hidden;
  }

  /* Body - Authentic LCARS styling */
  body {
    font-family: 'Antonio', sans-serif;
    font-size: 14px;
    line-height: 1.4;
    color: #FF9933;
    background-color: #000000;
    text-transform: uppercase;
    letter-spacing: 1px;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
  }

  #root {
    min-height: 100vh;
  }

  /* Headings - LCARS style */
  h1, h2, h3, h4, h5, h6 {
    font-family: 'Antonio', sans-serif;
    font-weight: 700;
    text-transform: uppercase;
    letter-spacing: 1.5px;
    color: #FF9933;
  }

  h1 {
    font-size: 2.5rem;
    letter-spacing: 2px;
  }

  h2 {
    font-size: 2rem;
  }

  h3 {
    font-size: 1.75rem;
  }

  h4 {
    font-size: 1.5rem;
  }

  h5, h6 {
    font-size: 1.25rem;
  }

  /* Button reset */
  button {
    font-family: 'Antonio', sans-serif;
    font-size: inherit;
    text-transform: uppercase;
    border: none;
    background: none;
    cursor: pointer;
    color: inherit;
  }

  /* Form inputs - LCARS style */
  input, textarea, select {
    font-family: 'Antonio', sans-serif;
    font-size: inherit;
    text-transform: none;
    color: #FFCC99;
    background-color: #0A0A0A;
    border: 2px solid #664466;
    border-radius: 0;
    padding: 8px 16px;
    transition: border-color 0.2s ease, box-shadow 0.2s ease;

    &:focus {
      outline: none;
      border-color: #FF9933;
      box-shadow: 0 0 8px rgba(255, 153, 51, 0.4);
    }

    &::placeholder {
      color: #664466;
    }
  }

  /* Links - LCARS blue */
  a {
    color: #99CCFF;
    text-decoration: none;
    transition: color 0.2s ease;

    &:hover {
      color: #FFCC99;
    }
  }

  /* Scrollbar - LCARS flat design */
  ::-webkit-scrollbar {
    width: 10px;
  }

  ::-webkit-scrollbar-track {
    background: #0A0A0A;
  }

  ::-webkit-scrollbar-thumb {
    background: #664466;
    border-radius: 0;

    &:hover {
      background: #CC99CC;
    }
  }

  /* Selection styling */
  ::selection {
    background-color: #FF9933;
    color: #000000;
  }

  ::-moz-selection {
    background-color: #FF9933;
    color: #000000;
  }

  /* LCARS Animations */
  @keyframes fadeIn {
    from {
      opacity: 0;
    }
    to {
      opacity: 1;
    }
  }

  @keyframes slideIn {
    from {
      transform: translateX(-100%);
      opacity: 0;
    }
    to {
      transform: translateX(0);
      opacity: 1;
    }
  }

  @keyframes lcars-blink {
    0%, 50% {
      opacity: 1;
    }
    51%, 100% {
      opacity: 0.3;
    }
  }

  @keyframes lcars-sweep {
    0% {
      background-position: -100% 0;
    }
    100% {
      background-position: 200% 0;
    }
  }

  @keyframes lcars-pulse {
    0%, 100% {
      box-shadow: 0 0 4px rgba(255, 153, 51, 0.4);
    }
    50% {
      box-shadow: 0 0 12px rgba(255, 153, 51, 0.8);
    }
  }

  /* Animation utility classes */
  .fade-in {
    animation: fadeIn 0.3s ease-in-out;
  }

  .slide-in {
    animation: slideIn 0.4s ease-out;
  }

  .lcars-blink {
    animation: lcars-blink 1s infinite;
  }

  .lcars-sweep {
    background: linear-gradient(
      90deg,
      transparent 0%,
      rgba(255, 153, 51, 0.3) 50%,
      transparent 100%
    );
    background-size: 200% 100%;
    animation: lcars-sweep 2s linear infinite;
  }

  .lcars-pulse {
    animation: lcars-pulse 2s ease-in-out infinite;
  }

  /* Utility classes */
  .sr-only {
    position: absolute;
    width: 1px;
    height: 1px;
    padding: 0;
    margin: -1px;
    overflow: hidden;
    clip: rect(0, 0, 0, 0);
    white-space: nowrap;
    border: 0;
  }

  .text-uppercase {
    text-transform: uppercase;
  }

  .text-center {
    text-align: center;
  }

  .text-right {
    text-align: right;
  }

  /* Responsive adjustments */
  @media (max-width: 768px) {
    body {
      font-size: 13px;
    }

    h1 {
      font-size: 2rem;
    }

    h2 {
      font-size: 1.75rem;
    }

    h3 {
      font-size: 1.5rem;
    }
  }

  @media (max-width: 480px) {
    body {
      font-size: 12px;
      letter-spacing: 0.5px;
    }

    h1 {
      font-size: 1.75rem;
    }

    h2 {
      font-size: 1.5rem;
    }
  }
`
