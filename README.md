# Celtech Solutions

Technical-consultancy site for local small businesses: a **portfolio** of work
we've built, a **directory** of local businesses we recommend, and productized
**service packages** (Starter Site, E-Commerce, the Food Truck Starter Pack,
and custom systems).

Same stack as the ddarty and ells sites — Java 21 / Spring Boot API, SvelteKit
front end (adapter-node), MongoDB, DigitalOcean Spaces — with the gallery
machinery replaced by an `entry` collection and a `service_package` collection.

## Layout

```
api/    Spring Boot API — Entry, ServicePackage, Inquiry + image storage
web/    SvelteKit front end — hero, portfolio, directory, packages, contact
seed/   mongosh seed scripts for entries and packages
.do/    App Platform spec
```

## Run locally

Two terminals:

```bash
# API — from api/
mvn spring-boot:run          # :8080, "local" profile

# Web — from web/
npm install
npm run dev                  # :5173, proxies /api and /images to :8080
```

Or the whole stack in Docker (includes Mongo, no local installs needed):

```bash
docker compose up --build    # web :3000, api :8080
```

Compose has no hot reload — use `npm run dev` while editing the front end.

Seed the database once Mongo is up:

```bash
mongosh mongodb://localhost:27017/celtech-solutions seed/celtech-entry-seed.js
mongosh mongodb://localhost:27017/celtech-solutions seed/celtech-package-seed.js
```

## Data model

- **entry** — one collection, discriminated by `kind`:
    - `"portfolio"` — work we built (carries `stack`, `builtByUs`, `launchedYear`)
    - `"directory"` — local businesses we recommend (external sites)

  Modeling both as one collection lets the site grow into a full local
  directory by adding fields, not by restructuring. The Portfolio page filters
  `kind:"portfolio"`; the Directory page filters `kind:"directory"`; the
  homepage pulls a random sample of each.

- **service_package** — fully drives the packages pages: price, tagline,
  summary, `includes[]`, `addOns[]`, timeline, `bestFor`, CTA. Editing a
  package is a re-seed, not a code change.

- **inquiry** — contact submissions, with an `interest` field (a package slug
  or `"general"`) so you know which offering prompted the message.

## API

```
GET  /api/entries?kind=portfolio                     all portfolio
GET  /api/entries?kind=directory&random=true&limit=3 random directory strip
GET  /api/entries?kind=portfolio&featured=true       featured work
GET  /api/entries?category=web                       filter by category
GET  /api/packages                                   all packages, ordered
GET  /api/packages?featured=true                     featured only
GET  /api/packages/{slug}                            one package (detail page)
POST /api/contact                                    submit inquiry (honeypot-guarded)
GET  /actuator/health                                health
```

## Images

Documents store storage *keys* (`full/<file>`, `thumb/<file>`); the API resolves
them to URLs per profile — local disk in dev, Spaces CDN in prod. The front end
never builds image paths itself, so the same DTO works in both environments.

- **Local:** put files in `local-images/full/` and `local-images/thumb/`.
- **Prod:** upload to the Space under `celtech-solutions/full/` and
  `celtech-solutions/thumb/`, and set `SPACES_CDN_BASE` to
  `https://<bucket>.nyc3.cdn.digitaloceanspaces.com/celtech-solutions`.

The portfolio cards scroll from the top of each screenshot to the bottom on
hover, so use tall full-page screenshots.

## What YOU still need to do

None of this ships wired to fake data — these are the real inputs:

1. **Logo** → `web/static/site/logo.png`. The hero shows a "CT" fallback until
   it exists.
2. **`--c-gold` token** — the EntryCard visit-link uses `var(--c-gold)`, which
   is not yet defined in `web/src/lib/styles/root.css`. Add a value that passes
   contrast on cream (e.g. `--c-gold: #9A7B2E;`) or switch that line back to
   `--c-blue`. A light gold fails as small text on cream.
3. **Package prices** — the seed has placeholder floors marked `REPLACE`. Set
   real numbers in `seed/celtech-package-seed.js`, then re-seed.
4. **Portfolio blurbs** — `celtechgs` and `celtechgs.kitchen` entries have
   `REPLACE` text. Fill them in.
5. **Directory entries** — add the local businesses you want listed
   (`kind:"directory"` in the entry seed). One stub is there now.
6. **Screenshots** — one per portfolio/package entry, in `local-images/` and
   the Space, named to match the `file:` fields in the seeds (`ddarty.png`,
   `ells.png`, `celtechgs.png`, `celtechgs-kitchen.png`, `pkg-foodtruck.png`,
   `pkg-starter.png`, `pkg-ecommerce.png`, `pkg-custom.png`).

## Deploy

See **DEPLOYMENT.md**. Uses the shared `celtech-dev` cluster, the
`celtech-solutions` database, and the shared Spaces bucket under the
`celtech-solutions/` prefix. All the deploy fixes from the earlier sites are
already baked into `application.yml` / `application-prod.yml` and `.do/app.yaml`.

## Design

Cream (`#FAF0DA`) ground with a blueprint-grid texture, deep-turf headings, and
a schematic accent. Fresh green is used for fills and borders only — it fails
contrast as small text on cream (2.77:1), a constraint enforced by how the
tokens are structured in `web/src/lib/styles/root.css`.