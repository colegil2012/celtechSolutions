<script>
  import { getContext } from 'svelte';
  import { goto } from '$app/navigation';

  const ctx = getContext('portal');
  let { children } = $props();

  $effect(() => {
    const me = ctx?.me;
    if (me && me.role !== 'ADMIN') {
      goto('/portal/gallery/organize', { replaceState: true });
    }
  });
</script>

{#if ctx?.me?.role === 'ADMIN'}
  {@render children()}
{:else}
  <p class="pp__empty">Redirecting…</p>
{/if}