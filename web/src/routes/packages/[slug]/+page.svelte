<script>
  import { onMount } from 'svelte';
  import { page } from '$app/stores';
  import { api } from '$lib/api/client.js';
  import './page.css';

  let pkg = $state(null);
  let status = $state('loading');

  onMount(async () => {
    try {
      pkg = await api.package($page.params.slug);
      status = 'ready';
    } catch (e) {
      status = e?.status === 404 ? 'missing' : 'error';
    }
  });

  function priceLabel(p) {
    if (p.priceType === 'quote') return 'Contact for pricing';
    if (p.priceType === 'from') return `Starting at $${p.priceFrom.toLocaleString()}`;
    return `$${p.priceFrom.toLocaleString()}`;
  }
</script>

<svelte:head><title>{pkg ? `${pkg.name} | Celtech Solutions` : 'Package — CelTech Solutions'}</title>
  {#if pkg}<meta name="description" content={pkg.tagline} />{/if}</svelte:head>

{#if status === 'loading'}
  <div class="pdetail shell"><div class="pd-skel"></div></div>
{:else if status === 'missing'}
  <div class="pdetail shell"><p class="notice">That package doesn't exist. <a href="/packages">See all packages →</a></p></div>
{:else if status === 'error'}
  <div class="pdetail shell"><p class="notice">Couldn't load this package. Refresh to try again.</p></div>
{:else}
  <article class="pdetail shell">
    <a href="/packages" class="pd-back">← All packages</a>

    <header class="pd-head">
      <div class="pd-head__text">
        <span class="eyebrow">Package</span>
        <h1 class="pd-title">{pkg.name}</h1>
        <p class="pd-tag">{pkg.tagline}</p>
        <div class="pd-price">
          <span class="pd-amt">{priceLabel(pkg)}</span>
          {#if pkg.priceNote}<span class="pd-note">{pkg.priceNote}</span>{/if}
        </div>
        <a href={`/contact?interest=${pkg.slug}`} class="btn btn--solid pd-cta">{pkg.ctaLabel}</a>
      </div>
      {#if pkg.imageUrl}
        <div class="pd-media"><img src={pkg.imageUrl} alt={pkg.name} /></div>
      {:else}
        <div class="pd-media pd-media--blueprint" aria-hidden="true"></div>
      {/if}
    </header>

    <div class="pd-body">
      <section class="pd-summary">
        <h2 class="pd-h2">Overview</h2>
        <p>{pkg.summary}</p>
        {#if pkg.bestFor}
          <div class="pd-bestfor"><span class="pd-bestfor__label">Best for</span><p>{pkg.bestFor}</p></div>
        {/if}
        {#if pkg.timeline}
          <div class="pd-meta"><span class="pd-meta__k">Typical timeline</span><span class="pd-meta__v">{pkg.timeline}</span></div>
        {/if}
      </section>

      <section class="pd-includes">
        <h2 class="pd-h2">What's included</h2>
        <ul class="pd-inclist">
          {#each pkg.includes as inc}<li>{inc}</li>{/each}
        </ul>

        {#if pkg.addOns && pkg.addOns.length}
          <h3 class="pd-h3">Optional add-ons</h3>
          <ul class="pd-addons">
            {#each pkg.addOns as a}
              <li><strong>{a.name}</strong>{#if a.note} — <span>{a.note}</span>{/if}</li>
            {/each}
          </ul>
        {/if}
      </section>
    </div>

    <div class="pd-foot">
      <p class="pd-foot__text">Interested in the {pkg.name}?</p>
      <a href={`/contact?interest=${pkg.slug}`} class="btn btn--solid">{pkg.ctaLabel}</a>
    </div>
  </article>
{/if}
