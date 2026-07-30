<script>
  import './page.css';
</script>

<svelte:head><title>About | Celtech Solutions</title>
  <meta name="description" content="A Louisville technical consultancy building web, systems, and integrated hardware for local small businesses." /></svelte:head>

<article class="about shell">
  <header class="about__head">
    <span class="eyebrow">About</span>
    <h1 class="about__title">We build the technical side, so you can run your business.</h1>
    <p class="about__lede">CelTech Solutions is a Louisville technical consultancy for local small businesses. We design, build, and support the systems your business runs on — from a custom website to a full point-of-sale setup.</p>
  </header>

  <div class="about__cols">
    <section class="about__col">
      <h2 class="about__h2">What we do</h2>
      <p>Full-stack web development, systems design, and integrated hardware. We work the way a larger firm would, but at the scale and price a local business actually needs. Every project is custom-built and owned end to end — design, build, deployment, and ongoing support.</p>
    </section>
    <section class="about__col">
      <h2 class="about__h2">How we work</h2>
      <p>We start by understanding the business, not the tech. Then we scope the smallest thing that solves the real problem, build it on a stack we run and maintain ourselves, and stay on to support it. No handing you a login and disappearing.</p>
    </section>
  </div>

  <section class="about__stack">
    <h2 class="about__h2">The stack we build on</h2>
    <ul class="about__tags">
      <li>SvelteKit</li><li>Spring Boot</li><li>Java 21</li><li>MongoDB</li>
      <li>DigitalOcean</li><li>Stripe</li><li>Raspberry Pi</li><li>Object storage</li>
    </ul>
    <p class="about__note">The same stack behind every site and system we ship — so we know it cold, and can support it for years.</p>
  </section>

  <section class="about__cta">
    <h2 class="about__cta-title">Let's build something.</h2>
    <a href="/contact" class="btn btn--solid">Start a project</a>
  </section>
</article>
