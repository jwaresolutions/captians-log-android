/**
 * Site-wide view toggle: Solid (default) vs Starfield (transparent).
 * Persists preference in localStorage.
 */
(function() {
  'use strict';

  function init() {
    var btn = document.createElement('button');
    btn.className = 'view-toggle';
    btn.setAttribute('aria-label', 'Toggle starfield view');
    btn.setAttribute('title', 'Toggle between solid and starfield background');

    // Clean up old keys
    localStorage.removeItem('starfieldView');
    localStorage.removeItem('highContrast');

    var stored = localStorage.getItem('viewMode');
    var isStarfield = stored === null ? true : stored === 'starfield';
    if (isStarfield) {
      document.body.classList.add('starfield-view');
    }
    btn.textContent = isStarfield ? 'Solid View' : 'Starfield View';

    btn.addEventListener('click', function() {
      document.body.classList.toggle('starfield-view');
      var active = document.body.classList.contains('starfield-view');
      localStorage.setItem('viewMode', active ? 'starfield' : 'solid');
      btn.textContent = active ? 'Solid View' : 'Starfield View';
    });

    document.body.appendChild(btn);
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }
})();
