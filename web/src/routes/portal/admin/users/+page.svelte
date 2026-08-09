<script>
  import { onMount } from 'svelte';
  import { portal } from '$lib/api/client.js';
  import Modal from '$components/Modal.svelte';
  import { toast, confirmDialog } from '$lib/stores/ui.js';

  let users = $state([]);
  let sites = $state([]);
  let ready = $state(false);
  let error = $state('');
  let busy = $state(false);

  let form = $state({ email: '', displayName: '', password: '', role: 'CLIENT', siteIds: [] });

  let sitesModal = $state(null);   // { user, siteIds:[] } | null
  let pwModal = $state(null);      // { user, value:'' } | null

  async function refresh() {
    [users, sites] = await Promise.all([portal.admin.users(), portal.admin.sites()]);
  }
  onMount(async () => { await refresh(); ready = true; });

  let siteName = $derived(Object.fromEntries((sites ?? []).map((s) => [s.id, s.name])));

  function toggleFormSite(id) {
    form.siteIds = form.siteIds.includes(id)
      ? form.siteIds.filter((x) => x !== id)
      : [...form.siteIds, id];
  }

  async function createUser() {
    if (!form.email.trim() || form.password.length < 8) {
      error = 'Email and a password of at least 8 characters are required.';
      return;
    }
    busy = true; error = '';
    try {
      await portal.admin.createUser({ ...form, email: form.email.trim() });
      form = { email: '', displayName: '', password: '', role: 'CLIENT', siteIds: [] };
      await refresh();
      toast('User created.');
    } catch (e) {
      error = e?.message ?? 'Could not create user.';
    } finally {
      busy = false;
    }
  }

  async function toggleEnabled(u) {
    const ok = await confirmDialog(`${u.enabled ? 'Disable' : 'Enable'} ${u.email}?`);
    if (!ok) return;
    try {
      await portal.admin.updateUser(u.id, { enabled: !u.enabled });
      await refresh();
      toast(`${u.email} ${u.enabled ? 'disabled' : 'enabled'}.`);
    } catch (e) { toast(e?.message ?? 'Update failed.', 'error'); }
  }
  async function toggleRole(u) {
    const role = u.role === 'ADMIN' ? 'CLIENT' : 'ADMIN';
    const ok = await confirmDialog(`Change ${u.email} to ${role}?`);
    if (!ok) return;
    try {
      await portal.admin.updateUser(u.id, { role });
      await refresh();
      toast(`${u.email} is now ${role}.`);
    } catch (e) { toast(e?.message ?? 'Update failed.', 'error'); }
  }

  function openSites(u) { sitesModal = { user: u, siteIds: [...(u.siteIds ?? [])] }; }
  function toggleModalSite(id) {
    sitesModal.siteIds = sitesModal.siteIds.includes(id)
      ? sitesModal.siteIds.filter((x) => x !== id)
      : [...sitesModal.siteIds, id];
  }
  async function saveSites() {
    try {
      await portal.admin.updateUser(sitesModal.user.id, { siteIds: sitesModal.siteIds });
      sitesModal = null;
      await refresh();
      toast('Site assignments updated.');
    } catch (e) { toast(e?.message ?? 'Update failed.', 'error'); }
  }

  function openPw(u) { pwModal = { user: u, value: '' }; }
  async function savePw() {
    if (pwModal.value.length < 8) { toast('Password must be at least 8 characters.', 'error'); return; }
    try {
      await portal.admin.resetUserPassword(pwModal.user.id, pwModal.value);
      pwModal = null;
      toast('Password reset.');
    } catch (e) { toast(e?.message ?? 'Reset failed.', 'error'); }
  }
</script>

<svelte:head><title>Users | Client Portal</title></svelte:head>

<header class="pp__head">
  <h1 class="pp__title">Users</h1>
  <p class="pp__lede">Create client logins, assign them to sites, reset passwords, and manage admin access.</p>
</header>

{#if ready}
  <section class="pp__panel">
    <h2 class="pp__panel-title">New user</h2>
    <div class="pp__form-row">
      <label class="field field--grow">
        <span class="field__label">Email</span>
        <input class="field__input" type="email" bind:value={form.email} />
      </label>
      <label class="field">
        <span class="field__label">Name</span>
        <input class="field__input" bind:value={form.displayName} />
      </label>
    </div>
    <div class="pp__form-row">
      <label class="field field--grow">
        <span class="field__label">Temp password</span>
        <input class="field__input" type="text" bind:value={form.password} placeholder="min 8 chars" />
      </label>
      <label class="field">
        <span class="field__label">Role</span>
        <select class="field__input" bind:value={form.role}>
          <option value="CLIENT">Client</option>
          <option value="ADMIN">Admin</option>
        </select>
      </label>
    </div>
    <div>
      <span class="field__label">Assigned sites</span>
      <div class="pp__chips" style="margin-top:var(--s-2);">
        {#each sites as s (s.id)}
          <button type="button" class="pp__filter"
                  class:pp__filter--on={form.siteIds.includes(s.id)}
                  onclick={() => toggleFormSite(s.id)}>{s.name}</button>
        {/each}
      </div>
    </div>
    {#if error}<p class="form__alert" role="alert">{error}</p>{/if}
    <div class="pp__form-row">
      <button class="btn btn--solid" onclick={createUser} disabled={busy}>Create user</button>
    </div>
  </section>

  <section class="pp__panel">
    <h2 class="pp__panel-title">All users <span class="pp__panel-sub">{users.length}</span></h2>
    <table class="pp__table">
      <thead>
        <tr><th>Email</th><th>Role</th><th>Sites</th><th>Status</th><th></th></tr>
      </thead>
      <tbody>
        {#each users as u (u.id)}
          <tr class:pp__row--off={!u.enabled}>
            <td>{u.email}<br /><span class="pp__panel-sub">{u.displayName ?? ''}</span></td>
            <td>{u.role}</td>
            <td class="pp__mono">
              {(u.siteIds ?? []).map((id) => siteName[id] ?? id).join(', ') || '—'}
            </td>
            <td>{u.enabled ? 'Active' : 'Disabled'}</td>
            <td class="pp__row-actions">
              <button class="btn" onclick={() => openSites(u)}>Sites</button>
              <button class="btn" onclick={() => toggleRole(u)}>
                {u.role === 'ADMIN' ? 'Make client' : 'Make admin'}
              </button>
              <button class="btn" onclick={() => openPw(u)}>Reset pw</button>
              <button class="btn" onclick={() => toggleEnabled(u)}>
                {u.enabled ? 'Disable' : 'Enable'}
              </button>
            </td>
          </tr>
        {/each}
      </tbody>
    </table>
  </section>
{/if}

<Modal open={!!sitesModal} title="Assign sites" onclose={() => (sitesModal = null)}>
  {#if sitesModal}
    <p class="pp__panel-sub" style="margin-bottom:var(--s-3);">{sitesModal.user.email}</p>
    <div class="pp__chips">
      {#each sites as s (s.id)}
        <button type="button" class="pp__filter"
                class:pp__filter--on={sitesModal.siteIds.includes(s.id)}
                onclick={() => toggleModalSite(s.id)}>{s.name}</button>
      {/each}
    </div>
    <div style="display:flex; gap:var(--s-3); justify-content:flex-end; margin-top:var(--s-5);">
      <button class="btn" onclick={() => (sitesModal = null)}>Cancel</button>
      <button class="btn btn--solid" onclick={saveSites}>Save</button>
    </div>
  {/if}
</Modal>

<Modal open={!!pwModal} title="Reset password" onclose={() => (pwModal = null)}>
  {#if pwModal}
    <p class="pp__panel-sub" style="margin-bottom:var(--s-3);">{pwModal.user.email}</p>
    <label class="field">
      <span class="field__label">New password</span>
      <input class="field__input" type="text" bind:value={pwModal.value} placeholder="min 8 chars" />
    </label>
    <div style="display:flex; gap:var(--s-3); justify-content:flex-end; margin-top:var(--s-5);">
      <button class="btn" onclick={() => (pwModal = null)}>Cancel</button>
      <button class="btn btn--solid" onclick={savePw}>Reset</button>
    </div>
  {/if}
</Modal>