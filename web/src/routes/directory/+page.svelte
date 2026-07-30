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
      entries = (await api.entries({ kind: 'directory' })) ?? [];
      status = entries.length ? 'ready' : 'empty';
    } catch { status = 'error'; }
  });

  function label(c) { return c === 'all' ? 'All' : c.charAt(0).toUpperCase() + c.slice(1); }
</script>

<svelte:head><title>Our Friends | Celtech Solutions</title><meta name="description" content="Local businesses we know, trust, and love to work with." /></svelte:head>

<header class="listhead shell">
  <span class="eyebrow">Local Directory</span>
  <h1 class="listhead__title">Our Friends</h1>
  <p class="listhead__lede">A growing directory of local businesses we recommend.</p>
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
