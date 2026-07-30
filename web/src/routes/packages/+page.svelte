<script>
  import { onMount } from 'svelte';
  import { api } from '$lib/api/client.js';
  import './page.css';

  let packages = $state([]);
  let status = $state('loading');

  onMount(async () => {
    try {
      packages = (await api.packages()) ?? [];
      status = packages.length ? 'ready' : 'empty';
    } catch { status = 'error'; }
  });

  function priceLabel(p) {
    if (p.priceType === 'quote') return 'Contact for pricing';
    if (p.priceType === 'from') return `From $${p.priceFrom.toLocaleString()}`;
    return `$${p.priceFrom.toLocaleString()}`;
  }
</script>

<svelte:head><title>Packages | Celtech Solutions</title>
  <meta name="description" content="Productized offerings: starter sites, e-commerce builds, the Food Truck Starter Pack, and custom systems." /></svelte:head>

<header class="listhead shell">
  <span class="eyebrow">Packages</span>
  <h1 class="listhead__title">Ways to work with us</h1>
  <p class="listhead__lede">Fixed offerings for common needs, plus fully custom work when your business needs something specific.</p>
</header>

<div class="pkglist shell">
  {#if status === 'loading'}
    <div class="pgrid">{#each Array(4) as _}<div class="pskel"></div>{/each}</div>
  {:else if status === 'ready'}
    <div class="pgrid">
      {#each packages as p (p.slug)}
        <a class="pcard" class:pcard--featured={p.featured} href={`/packages/${p.slug}`}>
          {#if p.featured}<span class="pcard__badge">Popular</span>{/if}
          <div class="pcard__top">
            <h2 class="pcard__name">{p.name}</h2>
            <p class="pcard__tag">{p.tagline}</p>
          </div>
          <div class="pcard__price">
            <span class="pcard__amt">{priceLabel(p)}</span>
            {#if p.priceNote}<span class="pcard__note">{p.priceNote}</span>{/if}
          </div>
          <ul class="pcard__inc">
            {#each p.includes.slice(0, 4) as inc}<li>{inc}</li>{/each}
            {#if p.includes.length > 4}<li class="pcard__more">+ {p.includes.length - 4} more</li>{/if}
          </ul>
          <span class="pcard__cta">{p.ctaLabel} →</span>
        </a>
      {/each}
    </div>
  {:else if status === 'empty'}
    <p class="notice">Packages coming soon.</p>
  {:else}
    <p class="notice">Couldn't load packages. Refresh to try again.</p>
  {/if}
</div>
