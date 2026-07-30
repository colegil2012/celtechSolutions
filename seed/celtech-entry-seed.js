// =============================================================================
// celtech-solutions — Entry Seed (portfolio + directory)
// =============================================================================
// Collection: entry
//
//   ONE collection, discriminated by `kind`:
//     kind: "portfolio"  -> work we built (ddarty, ells, celtechgs, ...)
//     kind: "directory"  -> local businesses we like to work with (external)
//
//   Modeling both as one collection is deliberate: they share shape (name, url,
//   blurb, category, thumbnail) and it lets the site grow into a real local
//   directory later by adding fields, never by restructuring. The Portfolio
//   page filters kind:"portfolio"; the Directory page filters kind:"directory".
//
// Run local:
//   mongosh mongodb://localhost:27017/celtech-solutions seed/celtech-entry-seed.js
// Run prod (managed cluster, celtech-solutions db):
//   mongosh "mongodb+srv://USER:PASS@celtech-dev-....mongo.ondigitalocean.com/celtech-solutions?tls=true&authSource=admin&replicaSet=celtech-dev" seed/celtech-entry-seed.js
//
// ---------------------------------------------------------------------------
// IMAGE KEYS (same convention as the artist/landscaping sites)
//   Files live under the Space at:  celtech-solutions/full/<file>
//                                    celtech-solutions/thumb/<file>
//   SPACES_CDN_BASE ends in /celtech-solutions, so imageKey = "full/<file>"
//   and thumbKey = "thumb/<file>".  Locally they resolve under local-images/.
// =============================================================================

const dbName = "celtech-solutions";
const targetDb = db.getSiblingDB(dbName);

print(`\nSeeding entries into "${targetDb.getName()}"...\n`);

targetDb.entry.drop();

// Indexes the app filters on. (If the Spring app also declares @Indexed on
// these fields, let the app create them and REMOVE these lines — two creators
// with different index names collide with IndexOptionsConflict / error 85.)
// Kept here commented as documentation of what the app should own:
//   targetDb.entry.createIndex({ kind: 1 });
//   targetDb.entry.createIndex({ category: 1 });
//   targetDb.entry.createIndex({ featured: 1 });

// ---- Category vocabulary (reused across portfolio + directory) --------------
const CAT = {
  WEB:         "web",
  ECOMMERCE:   "ecommerce",
  HOSPITALITY: "hospitality",   // food trucks, restaurants
  TRADES:      "trades",        // landscaping, contractors
  CREATIVE:    "creative",      // artists, studios
  RETAIL:      "retail",
  SYSTEMS:     "systems"        // POS / integrated hardware
};

const LQIP =
  "data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciLz4=";

// ---- Helpers ----------------------------------------------------------------
function portfolio({ name, url, file, blurb, category, stack,
                     launchedYear, featured = false, summary = "" }) {
  return {
    kind: "portfolio",
    name,
    url,
    blurb,
    summary,                       // longer "what we did", shown on detail/hover
    category,
    stack,                         // technologies — portfolio only
    builtByUs: true,
    imageKey: "full/"  + file,
    thumbKey: "thumb/" + file,
    lqip: LQIP,
    featured,
    launchedYear: launchedYear ? String(launchedYear) : null,
    createdAt: new Date()
  };
}

function directory({ name, url, file, blurb, category, featured = false }) {
  return {
    kind: "directory",
    name,
    url,
    blurb,
    summary: "",
    category,
    stack: [],                     // external site — we didn't build it
    builtByUs: false,
    imageKey: file ? "full/"  + file : null,
    thumbKey: file ? "thumb/" + file : null,
    lqip: LQIP,
    featured,
    launchedYear: null,
    createdAt: new Date()
  };
}

// ---- PORTFOLIO — work we built ---------------------------------------------
// REPLACE file names with the real screenshots you upload to
// local-images/full + thumb (and the Space under celtech-solutions/).
const portfolioEntries = [
  portfolio({
    name:        "ddarty.com",
    url:         "https://ddarty.com",
    file:        "ddarty.png",
    blurb:       "Digital art portfolio and commission requests for a Louisville artist.",
    summary:     "A gallery-driven portfolio with a random-sample homepage strip, "
               + "tag-filtered gallery, and a commission request pipeline. ",
    category:    [CAT.WEB, CAT.CREATIVE],
    stack:       ["Spring Boot", "SvelteKit", "MongoDB", "DO Spaces"],
    launchedYear: 2026,
    featured:    true
  }),
  portfolio({
    name:        "Ells Landscaping",
    url:         "https://ells4u.com",
    file:        "ells.png",
    blurb:       "Portfolio and service-request site for a local landscaping crew.",
    summary:     "Project portfolio with before/after comparisons, service-type "
               + "filtering, and a lead-capture request form routed to the crew. "
               + "Same stack, tuned for a trades business.",
    category:    [CAT.WEB, CAT.TRADES],
    stack:       ["Spring Boot", "SvelteKit", "MongoDB", "DO SW3 Spaces"],
    launchedYear: 2026,
    featured:    true
  }),
  portfolio({
    name:        "Celtech General Store",
    url:         "https://celtechgs.com",
    file:        "celtechgs.png",
    blurb:       "Online storefront for Celtech General Store, ecommerce site for all locally supplied produce and goods.",
    summary:     "Online Ecommerce application centering around locally supplied produce and goods."
               + "Complete with user authentication, cart management, guest checkout, secure payments through stripe integration"
               + " - IN PROGRESS: Developing Raspi 5 powered driver tablet with GNSS chip to allow for real-time tracking of deliveries and inventory management.",
    category:    [CAT.WEB, CAT.RETAIL],
    stack:       ["Spring Boot", "Thymeleaf", "MongoDB", "Raspberry Pi", "DO SW3 Spaces"],
    launchedYear: 2025,
    featured:    true
  }),
  portfolio({
    name:        "Celtech General Store — Mobile Kitchen",
    url:         "https://celtechgs.kitchen",
    file:        "celtechgs-kitchen.png",
    blurb:       "Web ordering and kitchen-display system for food service.",
    summary:     "Web ordering application for Celtech Mobile Kitchen."
               + "Event based architecture, online checkout closes when the event closes."
               + "Fully managed through Admin Portal Login, Manage your users, orders, menu, menu options, and schedule events for your business."
               + "Paired with Raspi 5 POS tablet with custom case, and 2 Raspi Zero 2W Boards powering an Expo Board for Kitchen Ticket Reference,"
               + "and customer-facing digital menu board.",
    category:    [CAT.WEB, CAT.RETAIL, CAT.SYSTEMS],
    stack:       ["Spring Boot", "MongoDB", "Raspberry Pi"],
    launchedYear: 2025,
    featured:    true
  })
];

// ---- DIRECTORY — local businesses we like to work with ----------------------
// External sites. Add a screenshot/logo file if you have one, else leave file
// out and the card uses a text/placeholder treatment.
const directoryEntries = [
  directory({
    name:     "J Mo's Food Truck",
    url:      "https://www.jmosfoodtruck.com",
    file:     "jmos.png",
    blurb:    "Full-service landscaping, hardscape, and lawn care in greater Louisville.",
    category: [CAT.TRADES],
    featured: true
  })
  // Add the businesses you'd list here — same shape, kind:"directory".
];

const all = [...portfolioEntries, ...directoryEntries];
targetDb.entry.insertMany(all);

// ---- Summary ----------------------------------------------------------------
const byKind = all.reduce((m, e) => (m[e.kind] = (m[e.kind] || 0) + 1, m), {});
const cats = [...new Set(all.flatMap(e => e.category))].sort();
print(`Done! ${all.length} entries — portfolio: ${byKind.portfolio || 0}, directory: ${byKind.directory || 0}`);
print(`Categories in use: ${cats.join(", ")}\n`);
