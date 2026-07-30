/**
 * scripts/reveal.js — Svelte action: fade/rise a section in when it scrolls
 * into view. One-shot (disconnects after first reveal). Falls back to visible
 * when IntersectionObserver is unavailable.
 *
 *   <div use:reveal>  or  <div use:reveal={{ delay: 120 }}>
 */
export function reveal(node, options = {}) {
  const { threshold = 0.15, delay = 0 } = options;

  if (typeof IntersectionObserver === 'undefined') {
    node.classList.add('revealed');
    return {};
  }
  node.classList.add('reveal');
  let timer;
  const obs = new IntersectionObserver((entries) => {
    for (const e of entries) {
      if (e.isIntersecting) {
        timer = setTimeout(() => node.classList.add('revealed'), delay);
        obs.disconnect();
      }
    }
  }, { threshold });
  obs.observe(node);
  return { destroy() { clearTimeout(timer); obs.disconnect(); } };
}
