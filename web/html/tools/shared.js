/**
 * Shared QR code rendering utilities for Captain's Log tools.
 * Depends on qrcodejs (QRCode) loaded via CDN.
 */
(function(window) {
  'use strict';

  var currentQrIndex = 0;
  var currentPayloads = [];
  var qrInstances = [];
  var containerRef = null;

  /**
   * Render QR codes into a container element.
   * Shows one QR at a time with prev/next navigation if multiple.
   *
   * @param {HTMLElement} containerEl - The container to render into
   * @param {string[]} payloads - Array of QR payload strings
   */
  function renderQrCodes(containerEl, payloads) {
    containerRef = containerEl;
    currentPayloads = payloads;
    currentQrIndex = 0;
    qrInstances = [];

    containerEl.innerHTML = '';
    containerEl.style.display = 'block';

    // Title
    var title = document.createElement('h3');
    title.textContent = 'Your QR Code' + (payloads.length > 1 ? 's' : '');
    containerEl.appendChild(title);

    // QR display area (visible one at a time)
    var displayArea = document.createElement('div');
    displayArea.id = 'qr-display-area';
    containerEl.appendChild(displayArea);

    // Create all QR codes, show only the first
    for (var i = 0; i < payloads.length; i++) {
      var page = document.createElement('div');
      page.className = 'qr-code-page';
      page.style.display = i === 0 ? 'block' : 'none';

      var wrapper = document.createElement('div');
      wrapper.className = 'qr-canvas-wrapper';
      page.appendChild(wrapper);

      // Print label (visible only in print)
      var printLabel = document.createElement('div');
      printLabel.className = 'qr-print-label';
      printLabel.textContent = 'QR ' + (i + 1) + ' of ' + payloads.length;
      printLabel.style.display = 'none';
      page.appendChild(printLabel);

      displayArea.appendChild(page);

      // Generate QR code
      var qr = new QRCode(wrapper, {
        text: payloads[i],
        width: 280,
        height: 280,
        colorDark: '#000000',
        colorLight: '#ffffff',
        correctLevel: QRCode.CorrectLevel.M
      });
      qrInstances.push(qr);
    }

    // Navigation (only if multiple QR codes)
    if (payloads.length > 1) {
      var nav = document.createElement('div');
      nav.className = 'qr-nav';

      var prevBtn = document.createElement('button');
      prevBtn.textContent = '\u2190 Prev';
      prevBtn.id = 'qr-prev';
      prevBtn.disabled = true;
      prevBtn.addEventListener('click', function() { navigateQr(-1); });

      var indicator = document.createElement('span');
      indicator.className = 'qr-indicator';
      indicator.id = 'qr-indicator';
      indicator.textContent = 'QR 1 of ' + payloads.length;

      var nextBtn = document.createElement('button');
      nextBtn.textContent = 'Next \u2192';
      nextBtn.id = 'qr-next';
      nextBtn.disabled = payloads.length <= 1;
      nextBtn.addEventListener('click', function() { navigateQr(1); });

      nav.appendChild(prevBtn);
      nav.appendChild(indicator);
      nav.appendChild(nextBtn);
      containerEl.appendChild(nav);
    }
  }

  /**
   * Navigate between QR codes.
   * @param {number} delta - Direction: -1 for prev, +1 for next
   */
  function navigateQr(delta) {
    var pages = containerRef.querySelectorAll('.qr-code-page');
    pages[currentQrIndex].style.display = 'none';

    currentQrIndex += delta;
    if (currentQrIndex < 0) currentQrIndex = 0;
    if (currentQrIndex >= currentPayloads.length) currentQrIndex = currentPayloads.length - 1;

    pages[currentQrIndex].style.display = 'block';

    // Update navigation state
    var prevBtn = document.getElementById('qr-prev');
    var nextBtn = document.getElementById('qr-next');
    var indicator = document.getElementById('qr-indicator');

    if (prevBtn) prevBtn.disabled = currentQrIndex === 0;
    if (nextBtn) nextBtn.disabled = currentQrIndex === currentPayloads.length - 1;
    if (indicator) indicator.textContent = 'QR ' + (currentQrIndex + 1) + ' of ' + currentPayloads.length;
  }

  /**
   * Open the print dialog for QR codes.
   * Shows all QR codes for printing (one per page).
   */
  function printQrCodes() {
    if (!containerRef) return;

    // Temporarily show all pages and print labels for printing
    var pages = containerRef.querySelectorAll('.qr-code-page');
    var labels = containerRef.querySelectorAll('.qr-print-label');

    for (var i = 0; i < pages.length; i++) {
      pages[i].style.display = 'block';
    }
    for (var j = 0; j < labels.length; j++) {
      labels[j].style.display = 'block';
    }

    window.print();

    // Restore single-view after print
    for (var k = 0; k < pages.length; k++) {
      pages[k].style.display = k === currentQrIndex ? 'block' : 'none';
    }
    for (var l = 0; l < labels.length; l++) {
      labels[l].style.display = 'none';
    }
  }

  window.ToolsShared = {
    renderQrCodes: renderQrCodes,
    printQrCodes: printQrCodes
  };

})(window);
