<script>
  import { onMount } from 'svelte';
  import { portal } from '$lib/api/client.js';
  import Modal from '$components/Modal.svelte';
  import { toast, confirmDialog } from '$lib/stores/ui.js';

  let sites = $state([]);
  let ready = $state(false);
  let name = $state('');
  let storageSlug = $state('');
  let notifyEmail = $state('');
  let busy = $state(false);
  let error = $state('');

  // Edit modal now covers name + notify email (slug is immutable).
  let editModal = $state(null); // { site, name, notifyEmail } | null

  async function refresh() { sites = (await portal.admin.sites()) ?? []; }
  onMount(async () => { await refresh(); ready = true; });

  function slugify(s) {
    return s.trim().toLowerCase().replace(/[^a-z0-9]+/g, '-').replace(/(^-|-$)/g, '');
  }
  $effect(() => { if (name && !storageSlug) storageSlug = slugify(name); });

  async function createSite() {
    if (!name.trim() || !storageSlug.trim()) return;
    busy = true; error = '';
    try {
      await portal.admin.createSite(name.trim(), storageSlug.trim(), notifyEmail.trim() || null);
      name = ''; storageSlug = ''; notifyEmail = '';
      await refresh();
      toast('Site created.');
    } catch (e) {
      error = e?.message ?? 'Could not create site.';
    } finally {
      busy = false;
    }
  }

  async function toggleEnabled(site) {
    const ok = await confirmDialog(`${site.enabled ? 'Disable' : 'Enable'} ${site.name}?`);
    if (!ok) return;
    try {
      await portal.admin.updateSite(site.id, { enabled: !site.enabled });
      await refresh();
      toast(`${site.name} ${site.enabled ? 'disabled' : 'enabled'}.`);
    } catch (e) { toast(e?.message ?? 'Update failed.', 'error'); }
  }

  function openEdit(site) {
    // Fall back to defaults so blank/legacy sites open with editable values;
    // saving writes this config into the same site document.
    const cfg = site.config ?? {};
    editModal = {
      site,
      name: site.name,
      notifyEmail: site.notifyEmail ?? '',
      config: {
        bioSectionCount: cfg.bioSectionCount ?? 3,
        serviceHeadersEnabled: cfg.serviceHeadersEnabled ?? false,
        serviceHeaderCount: cfg.serviceHeaderCount ?? 0,
        aboutImageEnabled: cfg.aboutImageEnabled ?? true,
        albumsEnabled: cfg.albumsEnabled ?? false
      }
    };
  }
  async function saveEdit() {
    const patch = {};
    if (editModal.name.trim() && editModal.name.trim() !== editModal.site.name) {
      patch.name = editModal.name.trim();
    }
    const nextEmail = editModal.notifyEmail.trim();
    if (nextEmail && nextEmail !== (editModal.site.notifyEmail ?? '')) {
      patch.notifyEmail = nextEmail;
    }
    // Always send config on save — cheap, and guarantees a blank site gets one.
    patch.config = editModal.config;
    if (Object.keys(patch).length === 0) { editModal = null; return; }
    try {
      await portal.admin.updateSite(editModal.site.id, patch);
      editModal = null;
      await refresh();
      toast('Site updated.');
    } catch (e) { toast(e?.message ?? 'Update failed.', 'error'); }
  }
</script>

<svelte:head><title>Sites | Client Portal</title></svelte:head>

<header class="pp__head">
  <h1 class="pp__title">Sites</h1>
  <p class="pp__lede">Create and manage client sites. The storage slug is the Spaces folder and public URL segment — it can't be changed later. Notify email is where that site's lead notifications are sent.</p>
</header>

{#if ready}
  <section class="pp__panel">
    <h2 class="pp__panel-title">New site</h2>
    <div class="pp__form-row">
      <label class="field field--grow">
        <span class="field__label">Name</span>
        <input class="field__input" bind:value={name} placeholder="Ell's Landscaping" />
      </label>
      <label class="field">
        <span class="field__label">Storage slug</span>
        <input class="field__input" bind:value={storageSlug} placeholder="ells-landscaping" />
      </label>
    </div>
    <div class="pp__form-row">
      <label class="field field--grow">
        <span class="field__label">Notify email <span class="field__opt">optional</span></span>
        <input class="field__input" type="email" bind:value={notifyEmail} placeholder="owner@example.com" />
      </label>
      <button class="btn btn--solid" onclick={createSite} disabled={busy}>Create</button>
    </div>
    {#if error}<p class="form__alert" role="alert">{error}</p>{/if}
  </section>

  <section class="pp__panel">
    <h2 class="pp__panel-title">All sites <span class="pp__panel-sub">{sites.length}</span></h2>
    <table class="pp__table">
      <thead>
        <tr><th>Name</th><th>Slug</th><th>Notify email</th><th>Status</th><th></th></tr>
      </thead>
      <tbody>
        {#each sites as s (s.id)}
          <tr class:pp__row--off={!s.enabled}>
            <td>{s.name}</td>
            <td class="pp__mono">{s.storageSlug}</td>
            <td class="pp__mono">{s.notifyEmail || '—'}</td>
            <td>{s.enabled ? 'Active' : 'Disabled'}</td>
            <td class="pp__row-actions">
              <button class="btn" onclick={() => openEdit(s)}>Edit</button>
              <button class="btn" onclick={() => toggleEnabled(s)}>
                {s.enabled ? 'Disable' : 'Enable'}
              </button>
            </td>
          </tr>
        {/each}
      </tbody>
    </table>
  </section>
{/if}

<Modal open={!!editModal} title="Edit site" onclose={() => (editModal = null)}>
  {#if editModal}
    <p class="pp__panel-sub" style="margin-bottom:var(--s-3);">
      Slug: <span class="pp__mono">{editModal.site.storageSlug}</span> (cannot change)
    </p>
    <label class="field">
      <span class="field__label">Name</span>
      <input class="field__input" bind:value={editModal.name} />
    </label>
    <label class="field" style="margin-top:var(--s-4);">
      <span class="field__label">Notify email</span>
      <input class="field__input" type="email" bind:value={editModal.notifyEmail}
             placeholder="owner@example.com" />
    </label>

        <h3 class="pp__panel-title" style="margin-top:var(--s-5);">Portal config</h3>
        <p class="pp__panel-sub" style="margin-bottom:var(--s-3);">
          Match these to the fields this site's live app renders. Changing them only
          affects which fields appear in this client's portal editor.
        </p>

        <label class="field">
          <span class="field__label">Bio sections</span>
          <input class="field__input" type="number" min="0" max="10"
                 bind:value={editModal.config.bioSectionCount} />
        </label>

        <label class="field" style="margin-top:var(--s-4); flex-direction:row; gap:var(--s-3); align-items:center;">
          <input type="checkbox" bind:checked={editModal.config.serviceHeadersEnabled} />
          <span class="field__label">Service headers enabled</span>
        </label>

        {#if editModal.config.serviceHeadersEnabled}
          <label class="field" style="margin-top:var(--s-4);">
            <span class="field__label">Service header count</span>
            <input class="field__input" type="number" min="0" max="12"
                   bind:value={editModal.config.serviceHeaderCount} />
          </label>
        {/if}

        <label class="field" style="margin-top:var(--s-4); flex-direction:row; gap:var(--s-3); align-items:center;">
          <input type="checkbox" bind:checked={editModal.config.aboutImageEnabled} />
          <span class="field__label">About image enabled</span>
        </label>

        <label class="field" style="margin-top:var(--s-4); flex-direction:row; gap:var(--s-3); align-items:center;">
          <input type="checkbox" bind:checked={editModal.config.albumsEnabled} />
          <span class="field__label">Gallery albums enabled</span>
        </label>

    <div style="display:flex; gap:var(--s-3); justify-content:flex-end; margin-top:var(--s-5);">
      <button class="btn" onclick={() => (editModal = null)}>Cancel</button>
      <button class="btn btn--solid" onclick={saveEdit}>Save</button>
    </div>
  {/if}
</Modal>