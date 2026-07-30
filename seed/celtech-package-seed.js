// =============================================================================
// celtech-solutions — Service Packages Seed
// =============================================================================
// Collection: service_package
//
//   Fully DB-driven: the Packages page and each package detail page render
//   entirely from these documents. Everything a page needs lives here — price,
//   summary, what's included, add-ons, timeline, CTA — so marketing edits are a
//   re-seed, not a code change.
//
// Run local:
//   mongosh mongodb://localhost:27017/celtech-solutions seed/celtech-package-seed.js
// Run prod:
//   mongosh "mongodb+srv://.../celtech-solutions?tls=true&authSource=admin&replicaSet=celtech-dev" seed/celtech-package-seed.js
//
// PRICING MODEL
//   priceType: "fixed"    -> priceFrom is the price ("$X")
//              "from"     -> "Starting at $X" (custom work above a floor)
//              "quote"    -> "Contact for pricing", priceFrom ignored
//   priceFrom is in whole dollars (number) so the page can format it; keep the
//   real numbers here, not strings, so you can sort/compare later.
// =============================================================================

const dbName = "celtech-solutions";
const targetDb = db.getSiblingDB(dbName);

print(`\nSeeding service packages into "${targetDb.getName()}"...\n`);

targetDb.service_package.drop();
// Let the Spring app own indexes (see note in the entry seed). If you index
// anything here it'd be { slug: 1 } unique and { order: 1 } — but the app
// should declare those via @Indexed instead.

const LQIP =
  "data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciLz4=";

// Helper keeps each package declaration honest about the full shape.
function pkg({
  slug,               // URL segment + stable id: /packages/<slug>
  name,
  tagline,            // one line under the name
  order,              // display order on the packages page
  priceType,          // "fixed" | "from" | "quote"
  priceFrom = null,   // dollars (number) or null
  priceNote = "",     // "one-time", "per month", "+ hardware", etc.
  summary,            // 2-3 sentence overview
  includes = [],      // bullet list — the "what's included"
  addOns = [],        // optional {name, note} upsells
  timeline = "",      // "2-3 weeks", etc.
  bestFor = "",       // who it's for
  file = null,        // hero/thumbnail image key basis
  featured = false,
  ctaLabel = "Start a conversation"
}) {
  return {
    slug, name, tagline, order,
    priceType, priceFrom, priceNote,
    summary, includes, addOns, timeline, bestFor,
    imageKey: file ? "full/"  + file : null,
    thumbKey: file ? "thumb/" + file : null,
    lqip: LQIP,
    featured,
    ctaLabel,
    createdAt: new Date()
  };
}

const packages = [

  // ---- Food Truck Starter Pack — the flagship productized offering ----------
  pkg({
    slug:      "food-truck-starter-pack",
    name:      "Food Truck Starter Pack",
    tagline:   "Web ordering, POS, and kitchen displays — installed and running.",
    order:     1,
    priceType: "from",
    priceFrom: 6500,
    priceNote: "+ hardware, one-time build",
    summary:   "Everything a food truck or small food operation needs to take "
             + "orders and run service, as one package. A web ordering app "
             + "backed by a hosted database, a Raspberry Pi POS terminal, a "
             + "kitchen expo board for order reference, and a customer-facing "
             + "menu display — configured, branded, and handed over working.",
    includes: [
      "Web ordering application (mobile-friendly, your branding)",
      "Hosted MongoDB database for menu and orders",
      "Raspberry Pi 5 POS terminal",
      "Raspberry Pi Zero 2 W kitchen expo board (order reference)",
      "Raspberry Pi Zero 2 W customer-facing menu display",
      "Menu setup and initial configuration",
      "On-site or remote install and handover"
    ],
    addOns: [
      { name: "Online payments", note: "Stripe integration for card orders" },
      { name: "Ongoing support",  note: "Monthly maintenance + updates retainer" },
      { name: "Extra display",    note: "Additional customer or kitchen screen" }
    ],
    timeline: "2–4 weeks from deposit to install",
    bestFor:  "Food trucks and small food operations that need ordering, POS, "
            + "and kitchen coordination without stitching together separate "
            + "vendors.",
    file:     "pkg-foodtruck.png",
    featured: true,
    ctaLabel: "Get the Starter Pack"
  }),

  // ---- Starter Site — the ddarty/ells stack, productized --------------------
  pkg({
    slug:      "starter-site",
    name:      "Starter Site",
    tagline:   "A fast, custom-branded site on a stack we own end to end.",
    order:     2,
    priceType: "from",
    priceFrom: 2500,
    priceNote: "one-time build",
    summary:   "A professional, mobile-first website built on the same stack we "
             + "run our own projects on — SvelteKit front end, Spring Boot API, "
             + "MongoDB, and cloud image storage. Custom branding, a contact/lead "
             + "form wired to your inbox, and content we can manage for you.",
    includes: [
      "Custom-branded, mobile-first SvelteKit site",
      "Spring Boot API backend",
      "MongoDB database (hosted)",
      "Cloud image storage (DO Spaces) with thumbnails",
      "Contact / lead-capture form routed to your inbox",
      "Deployed on DigitalOcean, domain + TLS configured",
      "Handover and basic training"
    ],
    addOns: [
      { name: "Gallery / portfolio", note: "Filterable image gallery like our art & trades sites" },
      { name: "Content updates",     note: "We manage your content on a retainer" },
      { name: "SEO pass",            note: "Metadata, sitemap, performance tuning" }
    ],
    timeline: "2–3 weeks",
    bestFor:  "Local businesses that want a real, fast, custom site — not a "
            + "template builder — with someone to run the technical side.",
    file:     "pkg-starter.png",
    featured: true,
    ctaLabel: "Start your site"
  }),

  // ---- E-Commerce — Stripe + admin + item storage ---------------------------
  pkg({
    slug:      "ecommerce",
    name:      "E-Commerce Build",
    tagline:   "Sell online with real payments, inventory, and an admin portal.",
    order:     3,
    priceType: "from",
    priceFrom: 5500,                       // REPLACE with your real floor
    priceNote: "one-time build",
    summary:   "Everything in the Starter Site, plus online payments and the "
             + "back office to run a store. Stripe integration for card "
             + "payments, MongoDB item and inventory storage, and a secured "
             + "admin portal for managing products and orders.",
    includes: [
      "Everything in the Starter Site package",
      "Stripe integration for online card payments",
      "MongoDB product & inventory storage",
      "Secured admin portal (Spring Security) for products and orders",
      "Order management and status tracking",
      "Customer order confirmation emails"
    ],
    addOns: [
      { name: "Shipping integration", note: "Rates and labels from a carrier API" },
      { name: "Discount codes",       note: "Promo / coupon engine" },
      { name: "Ongoing support",      note: "Monthly maintenance retainer" }
    ],
    timeline: "4–6 weeks",
    bestFor:  "Retailers and makers ready to sell online and manage inventory "
            + "themselves through a clean admin portal.",
    file:     "pkg-ecommerce.png",
    featured: true,
    ctaLabel: "Build your store"
  }),

  // ---- Custom / Integrated Systems — the catch-all consulting offer ---------
  pkg({
    slug:      "custom-systems",
    name:      "Custom & Integrated Systems",
    tagline:   "Full-stack builds, systems design, and hardware integration.",
    order:     4,
    priceType: "quote",
    priceNote: "scoped per project",
    summary:   "For anything beyond a package: custom web and full-stack "
             + "applications, systems design, and integrated hardware/software "
             + "like the POS and kitchen systems we build. We scope the work, "
             + "then build and support it.",
    includes: [
      "Discovery and systems design",
      "Custom full-stack application development",
      "Integrated hardware (Raspberry Pi, kiosks, displays)",
      "Third-party and API integrations",
      "Database design and hosting",
      "Deployment, monitoring, and ongoing support"
    ],
    addOns: [],
    timeline: "Scoped per project",
    bestFor:  "Businesses with a specific problem that doesn't fit a package — "
            + "custom software, integrations, or hardware.",
    file:     "pkg-custom.png",
    featured: false,
    ctaLabel: "Tell us about your project"
  })
];

targetDb.service_package.insertMany(packages);

// ---- Summary ----------------------------------------------------------------
print(`Done! ${packages.length} packages:`);
packages
  .sort((a, b) => a.order - b.order)
  .forEach(p => {
    const price = p.priceType === "quote"
      ? "Contact for pricing"
      : (p.priceType === "from" ? `From $${p.priceFrom}` : `$${p.priceFrom}`);
    print(`  ${p.order}. ${p.name.padEnd(28)} ${price} ${p.priceNote}`);
  });
print("");
