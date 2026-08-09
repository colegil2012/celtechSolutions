<script>
  let { open = false, title = '', onclose = () => {}, children } = $props();

  function backdrop(e) {
    if (e.target === e.currentTarget) onclose();
  }
  function onkey(e) {
    if (e.key === 'Escape') onclose();
  }
</script>

<svelte:window onkeydown={open ? onkey : undefined} />

{#if open}
  <div class="modal__backdrop" onclick={backdrop} role="presentation">
    <div class="modal" role="dialog" aria-modal="true" aria-label={title}>
      {#if title}<h2 class="modal__title">{title}</h2>{/if}
      <div class="modal__body">{@render children()}</div>
    </div>
  </div>
{/if}