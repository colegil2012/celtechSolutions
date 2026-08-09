<script>
  import '$styles/root.css';
  import './layout.css';
  import { page } from '$app/stores';
  import SiteNav from '$components/SiteNav.svelte';
  import SiteFooter from '$components/SiteFooter.svelte';
  let { children } = $props();

  // The portal has its own chrome (.padmin) and should not show the marketing
  // site nav/footer. Hide them on any /portal route.
  let isPortal = $derived($page.url.pathname.startsWith('/portal'));
</script>

<a href="#main" class="skip-link">Skip to content</a>
{#if !isPortal}<SiteNav />{/if}
<main id="main">{@render children()}</main>
{#if !isPortal}<SiteFooter />{/if}