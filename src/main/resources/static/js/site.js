/* Celtech Solutions — site bootstrap */
(function () {
    'use strict';

    const prefersReducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;

    function sleep(ms) {
        return new Promise(resolve => setTimeout(resolve, ms));
    }

    async function typeInto(el, text, perChar = 28) {
        for (let i = 0; i < text.length; i++) {
            el.textContent += text.charAt(i);
            // tiny jitter so it feels human, not metronomic
            await sleep(perChar + Math.random() * 25);
        }
    }

    async function playTerminal(terminal) {
        const lines = terminal.querySelectorAll('.line');
        if (!lines.length) return;

        // If reduced motion is requested, just show everything immediately.
        if (prefersReducedMotion) {
            lines.forEach(line => {
                if (line.dataset.type) line.textContent = line.dataset.type;
                if (line.dataset.text) line.innerHTML   = line.dataset.text;
            });
            return;
        }

        // Caret on the prompt line while typing.
        const prompt = lines[0];
        prompt.classList.add('is-typing');
        await sleep(400); // brief pause before "typing" starts
        await typeInto(prompt, prompt.dataset.type || '');
        prompt.classList.remove('is-typing');

        // Reveal the rest of the lines one at a time.
        for (let i = 1; i < lines.length; i++) {
            const line = lines[i];
            const delay = parseInt(line.dataset.delay, 10) || 300;
            await sleep(delay);
            line.innerHTML = line.dataset.text || '';
            line.classList.add('is-visible');
        }
    }

    function watchTerminals() {
        const terminals = document.querySelectorAll('[data-terminal]');
        if (!terminals.length) return;

        if (!('IntersectionObserver' in window)) {
            terminals.forEach(playTerminal);
            return;
        }

        const observer = new IntersectionObserver((entries, obs) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    // Let the GSAP hero intro breathe before typing kicks in.
                    setTimeout(() => playTerminal(entry.target), 900);
                    obs.unobserve(entry.target);
                }
            });
        }, { threshold: 0.4 });

        terminals.forEach(t => observer.observe(t));
    }

    function init() {
        if (window.lucide && typeof window.lucide.createIcons === 'function') {
            window.lucide.createIcons();
        }

        if (window.AOS && typeof window.AOS.init === 'function') {
            window.AOS.init({
                duration: 600,
                easing: 'ease-out-quart',
                once: true,
                offset: 40
            });
        }

        if (window.gsap && document.querySelector('.hero')) {
            const tl = window.gsap.timeline({ defaults: { ease: 'power3.out' } });
            tl.from('.hero__eyebrow', { y: 20, opacity: 0, duration: 0.5 })
                .from('.hero__title',   { y: 30, opacity: 0, duration: 0.7 }, '-=0.2')
                .from('.hero__sub',     { y: 20, opacity: 0, duration: 0.6 }, '-=0.4')
                .from('.hero__cta',     { y: 20, opacity: 0, duration: 0.6 }, '-=0.4')
                .from('.hero__card',    { y: 30, opacity: 0, scale: 0.98, duration: 0.7 }, '-=0.6');
        }

        watchTerminals();
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})();