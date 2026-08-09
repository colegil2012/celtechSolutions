<script>
  import { getContext } from 'svelte';
  import { portal } from '$lib/api/client.js';
  import { toast } from '$lib/stores/ui.js';

  const ctx = getContext('portal');
  let me = $derived(ctx?.me);

  let currentPassword = $state('');
  let newPassword = $state('');
  let confirmPassword = $state('');
  let busy = $state(false);
  let error = $state('');

  async function changePassword(e) {
    e.preventDefault();
    error = '';
    if (newPassword.length < 8) { error = 'New password must be at least 8 characters.'; return; }
    if (newPassword !== confirmPassword) { error = 'New passwords do not match.'; return; }
    if (newPassword === currentPassword) { error = 'New password must differ from the current one.'; return; }

    busy = true;
    try {
      await portal.changePassword(currentPassword, newPassword);
      currentPassword = newPassword = confirmPassword = '';
      toast('Password changed.');
    } catch (err) {
      // 403 from the backend = current password wrong.
      error = err?.status === 403
        ? 'Your current password is incorrect.'
        : (err?.message ?? 'Could not change password.');
    } finally {
      busy = false;
    }
  }
</script>

<svelte:head><title>Account | Client Portal</title></svelte:head>

<header class="pp__head">
  <h1 class="pp__title">Account</h1>
  <p class="pp__lede">Manage your login credentials.</p>
</header>

{#if me}
  <!-- Profile summary (read-only) -->
  <section class="pp__panel">
    <h2 class="pp__panel-title">Profile</h2>
    <dl class="pp__deflist">
      <div><dt>Name</dt><dd>{me.displayName || '—'}</dd></div>
      <div><dt>Email</dt><dd class="pp__mono">{me.email}</dd></div>
      <div><dt>Role</dt><dd>{me.role}</dd></div>
      <div>
        <dt>Sites</dt>
        <dd>{(me.sites ?? []).map((s) => s.name).join(', ') || '—'}</dd>
      </div>
    </dl>
    <p class="pp__panel-sub" style="margin-top:var(--s-3);">
      Need your name, email, or site access changed? Contact your administrator.
    </p>
  </section>

  <!-- Change password -->
  <section class="pp__panel">
    <h2 class="pp__panel-title">Change password</h2>
    <form class="pp__account-form" onsubmit={changePassword}>
      <label class="field">
        <span class="field__label">Current password</span>
        <input class="field__input" type="password" bind:value={currentPassword}
               required autocomplete="current-password" />
      </label>
      <label class="field">
        <span class="field__label">New password</span>
        <input class="field__input" type="password" bind:value={newPassword}
               required autocomplete="new-password" placeholder="min 8 characters" />
      </label>
      <label class="field">
        <span class="field__label">Confirm new password</span>
        <input class="field__input" type="password" bind:value={confirmPassword}
               required autocomplete="new-password" />
      </label>
      {#if error}<p class="form__alert" role="alert">{error}</p>{/if}
      <div>
        <button class="btn btn--solid" type="submit" disabled={busy}>
          {busy ? 'Saving…' : 'Change password'}
        </button>
      </div>
    </form>
  </section>
{:else}
  <p class="pp__empty">Loading…</p>
{/if}