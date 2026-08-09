<script>
  import { getContext } from 'svelte';
  import { goto } from '$app/navigation';

  const ctx = getContext('portal');

  $effect(() => {
    const me = ctx?.me;
    if (!me) return;                         // wait until auth resolves
    const dest = me.role === 'ADMIN'
      ? '/portal/admin/sites'                // admins land on Sites (management home)
      : '/portal/gallery/organize';          // clients land on their gallery
    goto(dest, { replaceState: true });
  });
</script>

<p class="pp__empty">Loading…</p>