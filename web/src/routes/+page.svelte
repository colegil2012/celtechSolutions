<script>
  import { onMount } from 'svelte';
  import { fade } from 'svelte/transition';
  import { api } from '$lib/api/client.js';
  import { reveal } from '$scripts/reveal.js';
  import EntryCard from '$components/EntryCard.svelte';
  import './page.css';

  let mounted = $state(false);
  let work = $state([]);
  let friends = $state([]);
  let packages = $state([]);
  let loaded = $state(false);

  onMount(async () => {
    mounted = true;
    try {
      const [w, f, p] = await Promise.all([
        api.entries({ kind: 'portfolio', random: true, limit: 3 }),
        api.entries({ kind: 'directory', random: true, limit: 3 }),
        api.packages({ featured: true })
      ]);
      work = w ?? []; friends = f ?? []; packages = p ?? [];
    } catch { /* sections show their empty state */ }
    loaded = true;
  });

  function priceLabel(p) {
    if (p.priceType === 'quote') return 'Contact for pricing';
    if (p.priceType === 'from') return `From $${p.priceFrom.toLocaleString()}`;
    return `$${p.priceFrom.toLocaleString()}`;
  }
</script>

<svelte:head>
  <title>Celtech Solutions | Web, Systems & Integrated Builds for Local Business</title>
  <meta name="description" content="Full-stack web development, systems design, and integrated hardware for local small businesses. Louisville, KY." />
</svelte:head>

<!-- HERO: blueprint field with a float-in logo centered -->
<section class="hero" class:hero--in={mounted}>
  <div class="hero__grid" aria-hidden="true"></div>
  <div class="hero__inner shell">
    <div class="hero__logo">
      <!-- Drop the real logo at static/site/logo.png; falls back to a mark. -->
      <img src="/site/logo.png" alt="Celtech Solutions"
           onerror={(e) => { e.currentTarget.style.display='none'; e.currentTarget.nextElementSibling.style.display='flex'; }} />
      <div class="hero__logo-fallback" style="display:none">CT</div>
    </div>
    <p class="eyebrow hero__eyebrow">Louisville, Kentucky</p>
    <h1 class="hero__title">Technology in service<br />of Nature and Community.</h1>
    <p class="hero__lede">From simple sites to custom built integrated systems. Built to compliment your small business, deployed, and supported.</p>
    <div class="hero__actions">
      <a href="/packages" class="btn btn--solid">See packages</a>
      <a href="/portfolio" class="btn">Our work</a>
    </div>
  </div>
  <a href="#work" class="hero__scroll" aria-label="Scroll to content"><span></span></a>
</section>

<!-- OUR WORK: random portfolio strip -->
<section id="work" class="strip shell" use:reveal>
  <div class="strip__head">
    <div>
      <span class="eyebrow">Our Work</span>
      <h2 class="strip__title">Things we've built</h2>
    </div>
    <a href="/portfolio" class="strip__all">All work →</a>
  </div>
  {#if loaded && work.length}
    <div class="strip__grid" in:fade={{ duration: 300 }}>
      {#each work as e, i}<EntryCard entry={e} index={i} />{/each}
    </div>
  {:else if loaded}
    <p class="strip__empty">Portfolio coming soon.</p>
  {:else}
    <div class="strip__grid">{#each Array(3) as _}<div class="strip__skel"></div>{/each}</div>
  {/if}
</section>

<!-- PACKAGES: featured -->
<section class="pkgs" use:reveal>
  <div class="shell">
    <div class="strip__head">
      <div>
        <span class="eyebrow">Packages</span>
        <h2 class="strip__title">Productized offerings</h2>
      </div>
      <a href="/packages" class="strip__all">All packages →</a>
    </div>
    {#if loaded && packages.length}
      <div class="pkgs__grid" in:fade={{ duration: 300 }}>
        {#each packages as p}
          <a class="pkgcard" href={`/packages/${p.slug}`}>
            <div class="pkgcard__head">
              <h3 class="pkgcard__name">{p.name}</h3>
              <span class="pkgcard__price">{priceLabel(p)}</span>
            </div>
            <p class="pkgcard__tag">{p.tagline}</p>
            <span class="pkgcard__cta">{p.ctaLabel} →</span>
          </a>
        {/each}
      </div>
    {:else if loaded}
      <p class="strip__empty">Packages coming soon.</p>
    {/if}
  </div>
</section>

<!-- OUR FRIENDS: random directory strip -->
<section class="strip shell" use:reveal>
  <div class="strip__head">
    <div>
      <span class="eyebrow">Our Friends</span>
      <h2 class="strip__title">Local businesses we love</h2>
    </div>
    <a href="/directory" class="strip__all">The directory →</a>
  </div>
  {#if loaded && friends.length}
    <div class="strip__grid" in:fade={{ duration: 300 }}>
      {#each friends as e, i}<EntryCard entry={e} index={i} />{/each}
    </div>
  {:else if loaded}
    <p class="strip__empty">Directory coming soon.</p>
  {/if}
</section>

<!-- CLOSING -->
<section class="closing" use:reveal>
  <div class="closing__inner shell">
    <h2 class="closing__title">Have something in mind?</h2>
    <p class="closing__text">Tell us what your business needs. We'll tell you the fastest way to build it.</p>
    <a href="/contact" class="btn btn--solid">Start a project</a>
  </div>
</section>
