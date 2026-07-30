<script>
  import './EntryCard.css';
  /** One portfolio or directory entry. */
  let { entry, index = 0 } = $props();
  let hovered = $state(false);
</script>

<a href={entry.url} target="_blank" rel="noopener noreferrer"
   class="ecard" class:ecard--hovered={hovered} style="--i: {index}"
   onmouseenter={() => (hovered = true)} onmouseleave={() => (hovered = false)}
   onfocusin={() => (hovered = true)} onfocusout={() => (hovered = false)}>
  <div class="ecard__media">
    {#if entry.thumbUrl}
      <img src={entry.thumbUrl} alt={entry.name} loading="lazy" decoding="async" />
    {:else}
      <div class="ecard__media-fallback" aria-hidden="true"></div>
    {/if}
  </div>
  <div class="ecard__body">
    <div class="ecard__head">
      <h3 class="ecard__name">{entry.name}</h3>
      {#if entry.launchedYear}<span class="ecard__year">{entry.launchedYear}</span>{/if}
    </div>
    <p class="ecard__blurb">{entry.blurb}</p>
    {#if entry.stack && entry.stack.length}
      <ul class="ecard__stack">
        {#each entry.stack as t}<li class="ecard__tag">{t}</li>{/each}
      </ul>
    {/if}
    <span class="ecard__visit">
      Visit
      <svg viewBox="0 0 24 12" aria-hidden="true"><path d="M0 6h21M16 1l5 5-5 5" fill="none" stroke="currentColor" stroke-width="1.5"/></svg>
    </span>
  </div>
</a>
