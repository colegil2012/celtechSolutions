/* Celtech Solutions — site bootstrap */
(function () {
    'use strict';

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
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})();