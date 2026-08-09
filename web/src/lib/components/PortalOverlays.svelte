<script>
  import Modal from './Modal.svelte';
  import { toasts, dismissToast, confirmState } from '$lib/stores/ui.js';

  function answer(v) {
    const s = $confirmState;
    if (s) { s.resolve(v); confirmState.set(null); }
  }
</script>

<div class="toasts" aria-live="polite">
  {#each $toasts as t (t.id)}
    <button type="button" class="toast toast--{t.kind}"
            onclick={() => dismissToast(t.id)}>{t.message}</button>
  {/each}
</div>

<Modal open={!!$confirmState} title="Please confirm" onclose={() => answer(false)}>
  <p style="margin-bottom: var(--s-4);">{$confirmState?.message}</p>
  <div style="display:flex; gap:var(--s-3); justify-content:flex-end;">
    <button class="btn" onclick={() => answer(false)}>Cancel</button>
    <button class="btn btn--solid" onclick={() => answer(true)}>Confirm</button>
  </div>
</Modal>