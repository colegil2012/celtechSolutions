<script>
  import './SiteFooter.css';
  const year = new Date().getFullYear();
</script>

<footer class="footer">
  <div class="footer__inner shell">
    <div class="footer__brand">
      <span class="footer__name">CelTech Solutions</span>
      <p class="footer__line">Full-stack web, systems design, and integrated hardware for local small businesses in Louisville and beyond.</p>
    </div>
    <nav class="footer__col" aria-label="Footer">
      <span class="footer__coltitle">Explore</span>
      <a href="/portfolio" class="footer__link">Our Work</a>
      <a href="/packages" class="footer__link">Packages</a>
      <a href="/directory" class="footer__link">Our Friends</a>
      <a href="/about" class="footer__link">About</a>
    </nav>
    <div class="footer__col">
      <span class="footer__coltitle">Get in touch</span>
      <a href="/contact" class="footer__link">Start a project</a>
      <a href="mailto:hello@celtech-solutions.tech" class="footer__link">hello@celtech-solutions.tech</a>
    </div>
  </div>
  <div class="footer__base shell">
    <span>&copy; {year} Celtech Solutions</span>
    <span class="footer__mono">Louisville, KY</span>
  </div>
</footer>
