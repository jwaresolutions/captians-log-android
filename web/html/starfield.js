(function() {
    const canvas = document.getElementById('starfield');
    const ctx = canvas.getContext('2d');

    function resize() {
        canvas.width = window.innerWidth;
        canvas.height = window.innerHeight;
    }
    resize();
    window.addEventListener('resize', resize);

    const NUM_STARS = 200;
    const stars = [];
    for (let i = 0; i < NUM_STARS; i++) {
        stars.push({
            x: (Math.random() - 0.5) * 2 * canvas.width,
            y: (Math.random() - 0.5) * 2 * canvas.height,
            z: Math.random() * canvas.width,
            prevX: undefined,
            prevY: undefined
        });
    }

    function animate() {
        const w = canvas.width;
        const h = canvas.height;
        const cx = w / 2;
        const cy = h / 2;

        ctx.fillStyle = 'rgba(0, 0, 0, 0.1)';
        ctx.fillRect(0, 0, w, h);

        for (let i = 0; i < stars.length; i++) {
            const star = stars[i];
            star.z -= 2;

            if (star.z <= 0) {
                star.x = (Math.random() - 0.5) * 2 * w;
                star.y = (Math.random() - 0.5) * 2 * h;
                star.z = w;
                star.prevX = undefined;
                star.prevY = undefined;
                continue;
            }

            const k = 128 / star.z;
            const px = star.x * k + cx;
            const py = star.y * k + cy;

            if (px >= 0 && px <= w && py >= 0 && py <= h) {
                const depth = 1 - star.z / w;
                const size = depth * 2;
                const brightness = Math.floor(depth * 255);
                const alpha = 0.5 + depth * 0.5;

                if (star.prevX !== undefined && star.prevY !== undefined) {
                    ctx.strokeStyle = 'rgba(' + brightness + ',' + brightness + ',255,' + (alpha * 0.5) + ')';
                    ctx.lineWidth = size * 0.5;
                    ctx.beginPath();
                    ctx.moveTo(star.prevX, star.prevY);
                    ctx.lineTo(px, py);
                    ctx.stroke();
                }

                ctx.fillStyle = 'rgba(' + brightness + ',' + brightness + ',255,' + alpha + ')';
                ctx.beginPath();
                ctx.arc(px, py, size, 0, Math.PI * 2);
                ctx.fill();

                star.prevX = px;
                star.prevY = py;
            }
        }

        requestAnimationFrame(animate);
    }

    animate();
})();
