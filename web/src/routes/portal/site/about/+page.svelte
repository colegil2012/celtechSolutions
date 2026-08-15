<script>
  import { getContext, onMount } from 'svelte';
  import { portal } from '$lib/api/client.js';
  import { toast } from '$lib/stores/ui.js';

  const ctx = getContext('portal');
  let site = $derived(ctx.activeSite);
  let cfg = $derived(site?.config ?? {});

  let bioCount = $derived(cfg.bioSectionCount ?? 3);
  let serviceHeadersEnabled = $derived(cfg.serviceHeadersEnabled ?? false);
  let maxServiceHeaders = $derived(cfg.serviceHeaderCount ?? 0);
  let aboutImageEnabled = $derived(cfg.aboutImageEnabled ?? true);

  let meta = $state(null);
  let ready = $state(false);
  let busy = $state(false);
  let uploading = $state(false);

  // Pad/trim the bio list to the configured count so every rendered field has
  // a bind target; hide the rest entirely.
  function normalize(m) {
    const n = cfg.bioSectionCount ?? 3;
    const bios = m.bioSections ?? [];
    return {
      ...m,
      aboutHeader: m.aboutHeader ?? '',
      bioSections: Array.from({ length: n }, (_, i) =>
        bios[i] ?? { header: '', section: '' }),
      serviceHeader: m.serviceHeader ?? []
    };
  }

  async function refresh() {
    if (!site?.id) return;
    meta = normalize(await portal.meta(site.id, true));
  }
  onMount(async () => { await refresh(); ready = true; });
  $effect(() => { if (site?.id) refresh(); });

  function addServiceHeader() {
    if (meta.serviceHeader.length >= maxServiceHeaders) return;
    meta.serviceHeader = [...meta.serviceHeader, { header: '', section: '' }];
  }
  function removeServiceHeader(i) {
    meta.serviceHeader = meta.serviceHeader.filter((_, idx) => idx !== i);
  }

  async function save() {
    if (!site?.id) return;
    busy = true;
    try {
      meta = normalize(await portal.updateMeta(site.id, {
        aboutHeader: meta.aboutHeader,
        // Send exactly the configured number of bios (keep order/blanks so
        // section 2 staying empty doesn't shift section 3 up).
        bioSections: meta.bioSections.slice(0, bioCount),
        serviceHeader: serviceHeadersEnabled
          ? meta.serviceHeader
              .slice(0, maxServiceHeaders)
              .filter((s) => s.header?.trim() || s.section?.trim())
          : [],
        aboutImageCaption: aboutImageEnabled ? (meta.aboutImageCaption ?? '') : '',
        aboutImageAltText: aboutImageEnabled ? (meta.aboutImageAltText ?? '') : ''
      }));
      toast('About page saved.');
    } catch (e) {
      toast(e?.message ?? 'Could not save.', 'error');
    } finally {
      busy = false;
    }
  }

  async function onImage(e) {
    const file = e.target.files?.[0];
    if (!file || !site?.id) return;
    uploading = true;
    try {
      meta = normalize(await portal.uploadMetaImage(site.id, file));
      toast('About image updated.');
    } catch (err) {
      toast(err?.message ?? 'Upload failed.', 'error');
    } finally {
      uploading = false;
      e.target.value = '';
    }
  }
</script>

<svelte:head><title>About page | Client Portal</title></svelte:head>

<header class="pp__head">
  <h1 class="pp__title">About page</h1>
  <p class="pp__lede">Edit the content on {site?.name ?? 'your'}’s About page. Leave a section blank to hide it.</p>
</header>

{#if ready && meta}
  <!-- About header always renders -->
  <section class="pp__panel">
    <h2 class="pp__panel-title">Page header</h2>
    <label class="field">
      <span class="field__label">About header</span>
      <input class="field__input" bind:value={meta.aboutHeader} placeholder="About Us" />
    </label>
  </section>

  <!-- As many bio sections as the config defines -->
  {#each meta.bioSections as bio, i (i)}
    <section class="pp__panel">
      <h2 class="pp__panel-title">Section {i + 1}</h2>
      <label class="field">
        <span class="field__label">Header</span>
        <input class="field__input" bind:value={bio.header} />
      </label>
      <label class="field" style="margin-top:var(--s-4);">
        <span class="field__label">Body</span>
        <textarea class="field__input" rows="4" bind:value={bio.section}></textarea>
      </label>
    </section>
  {/each}

  <!-- Service headers only when enabled, capped at the configured count -->
  {#if serviceHeadersEnabled}
    <section class="pp__panel">
      <h2 class="pp__panel-title">
        Service headers <span class="pp__panel-sub">{meta.serviceHeader.length}/{maxServiceHeaders}</span>
      </h2>
      <p class="pp__panel-sub" style="margin-bottom:var(--s-3);">
        Small label / value pairs, e.g. “Founded” → “2025”, “Our Crew” → “Mom, Dad &amp; Daughter”.
      </p>
      {#each meta.serviceHeader as sh, i (i)}
        <div class="pp__form-row" style="align-items:flex-end;">
          <label class="field">
            <span class="field__label">Label</span>
            <input class="field__input" bind:value={sh.header} placeholder="Founded" />
          </label>
          <label class="field field--grow">
            <span class="field__label">Value</span>
            <input class="field__input" bind:value={sh.section} placeholder="2025" />
          </label>
          <button class="btn" onclick={() => removeServiceHeader(i)}>Remove</button>
        </div>
      {/each}
      {#if meta.serviceHeader.length < maxServiceHeaders}
        <div class="pp__form-row">
          <button class="btn" onclick={addServiceHeader}>Add header</button>
        </div>
      {/if}
    </section>
  {/if}

  <!-- About image only when enabled -->
  {#if aboutImageEnabled}
    <section class="pp__panel">
      <h2 class="pp__panel-title">About image</h2>
      {#if meta.aboutImageUrl}
        <img class="pp__thumb" style="max-width:280px;" src={meta.aboutImageUrl}
             alt={meta.aboutImageAltText ?? ''} />
      {/if}
      <div class="pp__form-row">
        <label class="btn btn--solid" style="display:inline-block;">
          {uploading ? 'Uploading…' : (meta.aboutImageUrl ? 'Replace image' : 'Upload image')}
          <input type="file" accept="image/*" hidden onchange={onImage} disabled={uploading} />
        </label>
      </div>
      <label class="field" style="margin-top:var(--s-4);">
        <span class="field__label">Caption</span>
        <input class="field__input" bind:value={meta.aboutImageCaption} />
      </label>
      <label class="field" style="margin-top:var(--s-4);">
        <span class="field__label">Alt text</span>
        <input class="field__input" bind:value={meta.aboutImageAltText} />
      </label>
    </section>
  {/if}

  <div class="pp__form-row">
    <button class="btn btn--solid" onclick={save} disabled={busy}>
      {busy ? 'Saving…' : 'Save about page'}
    </button>
  </div>
{:else if ready}
  <p class="pp__empty">Select a site first.</p>
{/if}