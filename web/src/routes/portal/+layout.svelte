<script>
  import { onMount, setContext } from 'svelte';
  import { page } from '$app/stores';
  import { goto } from '$app/navigation';
  import { portal, ApiError } from '$lib/api/client.js';
  import PortalOverlays from '$components/PortalOverlays.svelte';
  import './portal.css';

  let { children } = $props();

  let me = $state(null);
  let activeSite = $state(null);
  let ready = $state(false);
  let galleryOpen = $state(false);
  let clientsOpen = $state(false);
  let accountOpen = $state(false);

  // Login page is the only portal route that must NOT require auth.
  let isLogin = $derived($page.url.pathname === '/portal/login');
  let isAdmin = $derived(me?.role === 'ADMIN');

  // Share auth + active site with child pages instead of re-fetching.
  setContext('portal', {
    get me() { return me; },
    get activeSite() { return activeSite; },
    setActiveSite: (s) => (activeSite = s)
  });

  onMount(async () => {
    if (isLogin) { ready = true; return; }
    try {
      me = await portal.me();
      activeSite = me?.sites?.[0] ?? null;
    } catch (e) {
      if (e instanceof ApiError && e.status === 401) return goto('/portal/login');
      throw e;
    }
    ready = true;
  });

  async function logout() {
    try { await portal.logout(); } catch { /* clear anyway */ }
    me = null;            // drop in-memory identity immediately
    activeSite = null;
    goto('/portal/login', { replaceState: true });
  }
</script>

{#if isLogin}
  {@render children()}
{:else if ready}
  <header class="padmin">
    <div class="padmin__inner">
      <a href="/" class="padmin__back">← Celtech Solutions</a>

      <nav class="padmin__nav" aria-label="Portal">
        <div class="padmin__menu" role="menu"
             onmouseenter={() => (galleryOpen = true)}
             onmouseleave={() => (galleryOpen = false)}>
          <button class="padmin__menubtn" aria-expanded={galleryOpen}
                  onclick={() => (galleryOpen = !galleryOpen)}>Gallery ▾</button>
          {#if galleryOpen}
            <div class="padmin__dropdown">
              <a href="/portal/gallery/upload"
                 class:padmin__item--on={$page.url.pathname === '/portal/gallery/upload'}>Upload</a>
              <a href="/portal/gallery/organize"
                 class:padmin__item--on={$page.url.pathname === '/portal/gallery/organize'}>Organize</a>
            </div>
          {/if}
        </div>

        {#if isAdmin}
          <div class="padmin__menu" role="menu"
               onmouseenter={() => (clientsOpen = true)}
               onmouseleave={() => (clientsOpen = false)}>
            <button class="padmin__menubtn" aria-expanded={clientsOpen}
                    onclick={() => (clientsOpen = !clientsOpen)}>Clients ▾</button>
            {#if clientsOpen}
              <div class="padmin__dropdown">
                <a href="/portal/admin/sites"
                   class:padmin__item--on={$page.url.pathname === '/portal/admin/sites'}>Sites</a>
                <a href="/portal/admin/users"
                   class:padmin__item--on={$page.url.pathname === '/portal/admin/users'}>Users</a>
              </div>
            {/if}
          </div>
        {/if}
        <a href="/portal/inbox" class="padmin__menubtn"
           class:padmin__item--on={$page.url.pathname === '/portal/inbox'}>Inbox</a>
      </nav>

      <div class="padmin__right">
        {#if me?.sites?.length > 1}
          <select class="padmin__site"
                  onchange={(e) => (activeSite = me.sites.find((s) => s.id === e.target.value))}>
            {#each me.sites as s}
              <option value={s.id} selected={s.id === activeSite?.id}>{s.name}</option>
            {/each}
          </select>
        {:else if activeSite}
          <span class="padmin__site-label">{activeSite.name}</span>
        {/if}

        <div class="padmin__menu" role="menu"
             onmouseenter={() => (accountOpen = true)}
             onmouseleave={() => (accountOpen = false)}>
          <button class="padmin__menubtn padmin__menubtn--account" aria-expanded={accountOpen}
                  onclick={() => (accountOpen = !accountOpen)}>
            {me?.displayName || me?.email || 'Account'} ▾
          </button>
          {#if accountOpen}
            <div class="padmin__dropdown padmin__dropdown--right">
              <a href="/portal/account"
                 class:padmin__item--on={$page.url.pathname === '/portal/account'}>Account</a>
              <button class="padmin__dropdown-btn" onclick={logout}>Sign out</button>
            </div>
          {/if}
        </div>
      </div>
    </div>
  </header>

  <main class="padmin__body">
    {@render children()}
  </main>
  <PortalOverlays />
{/if}