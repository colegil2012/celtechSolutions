<script>
  import { getContext } from 'svelte';
  import { portal } from '$lib/api/client.js';

  const ctx = getContext('portal');
  let site = $derived(ctx.activeSite);

  let queue = $state([]); // [{ name, status: 'pending'|'done'|'error' }]
  let busy = $state(false);
  let recent = $state([]);

  async function onFiles(e) {
    const files = Array.from(e.target.files ?? []);
    if (!files.length || !site?.id) return;
    busy = true;
    queue = files.map((f) => ({ name: f.name, status: 'pending' }));

    for (let i = 0; i < files.length; i++) {
      try {
        const saved = await portal.upload(site.id, files[i]);
        queue[i].status = 'done';
        recent = [saved, ...recent];
      } catch {
        queue[i].status = 'error';
      }
    }
    busy = false;
    e.target.value = '';
  }
</script>

<svelte:head><title>Upload | Client Portal</title></svelte:head>

<header class="pp__head">
  <h1 class="pp__title">Upload images</h1>
  <p class="pp__lede">Add images to {site?.name ?? 'your gallery'}. JPG, PNG, or WebP, up to 15&nbsp;MB each.</p>
</header>

<label class="btn btn--solid" style="display:inline-block;">
  {busy ? 'Uploading…' : 'Choose images'}
  <input type="file" accept="image/*" multiple hidden onchange={onFiles} disabled={busy} />
</label>

{#if queue.length}
  <ul class="pp__queue">
    {#each queue as item}
      <li><span>{item.name}</span><span class="is-{item.status}">{item.status}</span></li>
    {/each}
  </ul>
{/if}

{#if recent.length}
  <div class="pp__grid">
    {#each recent as img (img.id)}
      <figure class="pp__tile"><img class="pp__thumb" src={img.thumbUrl} alt={img.altText ?? ''} loading="lazy" /></figure>
    {/each}
  </div>
{/if}