<script>
  import { goto } from '$app/navigation';
  import { portal } from '$lib/api/client.js';

  let email = $state('');
  let password = $state('');
  let error = $state('');
  let busy = $state(false);

  async function submit(e) {
    e.preventDefault();
    busy = true; error = '';
    try {
      const me = await portal.login(email, password); // returns MeResponse
      // Redirect straight to the right place using the login response — avoids
      // the /portal index page racing the layout's own me() fetch.
      const dest = me?.role === 'ADMIN'
        ? '/portal/admin/sites'
        : '/portal/gallery/organize';
      goto(dest, { replaceState: true });
    } catch {
      error = 'Invalid email or password.';
    } finally {
      busy = false;
    }
  }
</script>

<svelte:head><title>Sign in | Client Portal</title></svelte:head>

<section class="pp__login">
  <h1 class="pp__title">Client Portal</h1>
  <form class="pp__login-form" onsubmit={submit}>
    <label class="field">
      <span class="field__label">Email</span>
      <input class="field__input" type="email" bind:value={email} required autocomplete="username" />
    </label>
    <label class="field">
      <span class="field__label">Password</span>
      <input class="field__input" type="password" bind:value={password} required autocomplete="current-password" />
    </label>
    {#if error}<p class="form__alert" role="alert">{error}</p>{/if}
    <button class="btn btn--solid" type="submit" disabled={busy}>
      {busy ? 'Signing in…' : 'Sign in'}
    </button>
  </form>
</section>