<script>
  import { getContext, onMount } from 'svelte';
  import { portal } from '$lib/api/client.js';

  const ctx = getContext('portal');
  let site = $derived(ctx.activeSite);

  let images = $state([]);
  let tags = $state([]);
  let ready = $state(false);

  let editing = $state(null);
  let draft = $state({ caption: '', altText: '', tagIds: [] });

  let filterTagId = $state('all');

  let newTagLabel = $state('');
  let newTagKind = $state('category');
  let tagBusy = $state(false);

  async function refresh() {
    if (!site) return;
    const data = await portal.gallery(site.storageSlug, true); // bypass cache in the portal
    tags = data?.tags ?? [];
    images = data?.images ?? [];
  }

  onMount(async () => { await refresh(); ready = true; });
  $effect(() => { if (site) refresh(); });

  let tagById = $derived(Object.fromEntries(tags.map((t) => [t.id, t])));
  let categoryTags = $derived(tags.filter((t) => t.kind !== 'album'));
  let albumTags = $derived(tags.filter((t) => t.kind === 'album'));

  let visible = $derived(
    filterTagId === 'all'
      ? images
      : filterTagId === 'untagged'
        ? images.filter((i) => !i.tagIds || i.tagIds.length === 0)
        : images.filter((i) => (i.tagIds ?? []).includes(filterTagId))
  );

  function startEdit(img) {
    editing = img.id;
    draft = { caption: img.caption ?? '', altText: img.altText ?? '', tagIds: [...(img.tagIds ?? [])] };
  }
  function toggleDraftTag(tagId) {
    draft.tagIds = draft.tagIds.includes(tagId)
      ? draft.tagIds.filter((t) => t !== tagId)
      : [...draft.tagIds, tagId];
  }
  async function saveEdit(img) {
    await portal.updateImage(img.id, { caption: draft.caption, altText: draft.altText, tagIds: draft.tagIds });
    editing = null;
    await refresh();
  }
  async function remove(img) {
    if (!confirm('Delete this image? This removes it from storage too.')) return;
    await portal.deleteImage(img.id);
    images = images.filter((i) => i.id !== img.id);
  }

  async function addTag() {
    if (!newTagLabel.trim() || !site?.id) return;
    tagBusy = true;
    try {
      await portal.createTag(site.id, { label: newTagLabel.trim(), kind: newTagKind });
      newTagLabel = '';
      await refresh();
    } catch (e) {
      alert(e?.message ?? 'Could not create tag.');
    } finally {
      tagBusy = false;
    }
  }
  async function renameTag(tag) {
    const label = prompt('Rename tag', tag.label);
    if (!label || label === tag.label) return;
    await portal.updateTag(tag.id, { label });
    await refresh();
  }
  async function deleteTag(tag) {
    if (!confirm(`Delete tag "${tag.label}"? It will be removed from all images.`)) return;
    await portal.deleteTag(tag.id);
    if (filterTagId === tag.id) filterTagId = 'all';
    await refresh();
  }
</script>

<svelte:head><title>Organize | Client Portal</title></svelte:head>

<header class="pp__head">
  <h1 class="pp__title">Organize gallery</h1>
  <p class="pp__lede">Manage tags, then tag images to build galleries and albums for {site?.name ?? 'your site'}.</p>
</header>

{#if ready}
  <!-- Tag manager -->
  <section class="pp__panel">
    <h2 class="pp__panel-title">Tags</h2>

    <div class="pp__taglist">
      <div class="pp__taggroup">
        <span class="pp__taggroup-label">Categories</span>
        <div class="pp__chips">
          {#each categoryTags as t (t.id)}
            <span class="pp__chip pp__chip--category">
              {t.label}
              <button class="pp__chip-btn" onclick={() => renameTag(t)} title="Rename">✎</button>
              <button class="pp__chip-btn" onclick={() => deleteTag(t)} title="Delete">✕</button>
            </span>
          {/each}
          {#if categoryTags.length === 0}<span class="pp__panel-sub">None yet</span>{/if}
        </div>
      </div>

      <div class="pp__taggroup">
        <span class="pp__taggroup-label">Albums</span>
        <div class="pp__chips">
          {#each albumTags as t (t.id)}
            <span class="pp__chip pp__chip--album">
              {t.label}
              <button class="pp__chip-btn" onclick={() => renameTag(t)} title="Rename">✎</button>
              <button class="pp__chip-btn" onclick={() => deleteTag(t)} title="Delete">✕</button>
            </span>
          {/each}
          {#if albumTags.length === 0}<span class="pp__panel-sub">None yet</span>{/if}
        </div>
      </div>
    </div>

    <div class="pp__form-row">
      <label class="field field--grow">
        <span class="field__label">New tag</span>
        <input class="field__input" placeholder="e.g. Hardscaping or Miller Backyard"
               bind:value={newTagLabel} onkeydown={(e) => e.key === 'Enter' && addTag()} />
      </label>
      <label class="field">
        <span class="field__label">Kind</span>
        <select class="field__input" bind:value={newTagKind}>
          <option value="category">Category</option>
          <option value="album">Album</option>
        </select>
      </label>
      <button class="btn btn--solid" onclick={addTag} disabled={tagBusy}>Add tag</button>
    </div>
  </section>

  <!-- Filters -->
  <div class="pp__filters">
    <button class="pp__filter" class:pp__filter--on={filterTagId === 'all'}
            onclick={() => (filterTagId = 'all')}>All</button>
    <button class="pp__filter" class:pp__filter--on={filterTagId === 'untagged'}
            onclick={() => (filterTagId = 'untagged')}>Untagged</button>
    {#each categoryTags as t (t.id)}
      <button class="pp__filter" class:pp__filter--on={filterTagId === t.id}
              onclick={() => (filterTagId = t.id)}>{t.label}</button>
    {/each}
    {#each albumTags as t (t.id)}
      <button class="pp__filter pp__filter--album" class:pp__filter--on={filterTagId === t.id}
              onclick={() => (filterTagId = t.id)}>{t.label}</button>
    {/each}
  </div>

  <!-- Image grid -->
  <div class="pp__grid">
    {#each visible as img (img.id)}
      <figure class="pp__tile">
        <img class="pp__thumb" src={img.thumbUrl} alt={img.altText ?? ''} loading="lazy" />

        {#if editing === img.id}
          <input class="field__input" placeholder="Caption" bind:value={draft.caption} />
          <input class="field__input" placeholder="Alt text" bind:value={draft.altText} />
          <div class="pp__edit-tags">
            {#each tags as t (t.id)}
              <button type="button"
                      class="pp__filter {t.kind === 'album' ? 'pp__filter--album' : ''}"
                      class:pp__filter--on={draft.tagIds.includes(t.id)}
                      onclick={() => toggleDraftTag(t.id)}>{t.label}</button>
            {/each}
          </div>
          <div class="pp__tile-actions">
            <button class="btn btn--solid" onclick={() => saveEdit(img)}>Save</button>
            <button class="btn" onclick={() => (editing = null)}>Cancel</button>
          </div>
        {:else}
          <figcaption class="pp__caption">{img.caption || '—'}</figcaption>
          <div class="pp__tile-tags">
            {#each (img.tagIds ?? []) as tid}
              {#if tagById[tid]}<span class="pp__tile-tag">{tagById[tid].label}</span>{/if}
            {/each}
          </div>
          <div class="pp__tile-actions">
            <button class="btn" onclick={() => startEdit(img)}>Edit</button>
            <button class="btn" onclick={() => remove(img)}>Delete</button>
          </div>
        {/if}
      </figure>
    {/each}
    {#if visible.length === 0}<p class="pp__empty">No images in this view.</p>{/if}
  </div>
{/if}