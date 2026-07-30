<script>
  import { onMount } from 'svelte';
  import { fade } from 'svelte/transition';
  import { api } from '$lib/api/client.js';
  import EntryCard from '$components/EntryCard.svelte';
  import './page.css';

  let entries = $state([]);
  let status = $state('loading');
  let activeCat = $state('all');

  const cats = $derived(['all', ...new Set(entries.flatMap(e => e.category ?? []))]);
  const visible = $derived(activeCat === 'all' ? entries : entries.filter(e => e.category?.includes(activeCat)));

  onMount(async () => {
    try {
      entries = (await api.entries({ kind: 'portfolio' })) ?? [];
      status = entries.length ? 'ready' : 'empty';
    } catch { status = 'error'; }
  });

  function label(c) { return c === 'all' ? 'All' : c.charAt(0).toUpperCase() + c.slice(1); }
</script>

<svelte:head><title>Our Work | Celtech Solutions</title><meta name="description" content="Full-stack sites and systems we've built for local businesses." /></svelte:head>

<header class="listhead shell">
  <span class="eyebrow">Portfolio</span>
  <h1 class="listhead__title">Our Work</h1>
  <p class="listhead__lede">Every project here we designed, built, and deployed. Filter by what it's made of.</p>
  {#if status === 'ready'}
    <div class="filters" role="group" aria-label="Filter">
      {#each cats as c}
        <button class="filters__tag" class:filters__tag--on={activeCat === c} onclick={() => (activeCat = c)}>{label(c)}</button>
      {/each}
    </div>
  {/if}
</header>

<div class="listgrid shell">
  {#if status === 'loading'}
    <div class="grid">{#each Array(6) as _}<div class="skel"></div>{/each}</div>
  {:else if status === 'ready'}
    <div class="grid">
      {#each visible as e, i (e.id)}<EntryCard entry={e} index={i} />{/each}
    </div>
  {:else if status === 'empty'}
    <p class="notice">Nothing here yet — check back soon.</p>
  {:else}
    <p class="notice">Couldn't load. Refresh to try again.</p>
  {/if}
</div>
