<script>
  import { page } from '$app/stores';
  import './SiteNav.css';

  const links = [
    { href: '/portfolio', label: 'Work' },
    { href: '/directory', label: 'Friends' },
    { href: '/packages',  label: 'Packages' },
    { href: '/about',     label: 'About' },
    { href: '/portal',    label: 'Portal' },
    { href: '/contact',   label: 'Contact' }
  ];
  let scrolled = $state(false);
  let open = $state(false);
  function onScroll() { scrolled = window.scrollY > 24; }
</script>

<svelte:window on:scroll={onScroll} />

<header class="nav" class:nav--scrolled={scrolled}>
  <div class="nav__inner shell">
    <a href="/" class="nav__mark" onclick={() => (open = false)}>
      <span class="nav__mark-name">Celtech</span>
      <span class="nav__mark-sub">Solutions</span>
    </a>

    <button class="nav__toggle" aria-expanded={open} aria-controls="nav-links" onclick={() => (open = !open)}>
      <span class="visually-hidden">{open ? 'Close menu' : 'Open menu'}</span>
      <span class="nav__bar" class:nav__bar--x={open}></span>
    </button>

    <nav id="nav-links" class="nav__links" class:nav__links--open={open}>
      {#each links as link}
        <a href={link.href} class="nav__link"
           class:nav__link--cta={link.href === '/contact'}
           class:nav__link--current={$page.url.pathname.startsWith(link.href)}
           onclick={() => (open = false)}>{link.label}</a>
      {/each}
    </nav>
  </div>
</header>
