/**
 * Thin wrapper over fetch for the celtech-solutions Spring API.
 *
 * Uses $env/dynamic/public so PUBLIC_API_BASE resolves at RUN time — changing
 * the API origin is an env edit + restart, not a rebuild. In dev it's empty
 * and Vite proxies /api to :8080.
 */

import { env } from '$env/dynamic/public';

const BASE = env.PUBLIC_API_BASE ?? '';

export class ApiError extends Error {
  constructor(message, status, body) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.body = body;
  }
}

async function request(path, options = {}) {
  let response;
  try {
    response = await fetch(`${BASE}${path}`, {
      headers: { 'Content-Type': 'application/json', ...options.headers },
      ...options
    });
  } catch {
    throw new ApiError('Could not reach the server. Check your connection and try again.', 0, null);
  }

  const isJson = response.headers.get('content-type')?.includes('application/json');
  const body = isJson ? await response.json().catch(() => null) : null;

  if (!response.ok) {
    throw new ApiError(body?.message ?? 'Something went wrong. Try again in a moment.', response.status, body);
  }
  return body;
}

export const api = {
  /** @param {{kind?: string, random?: boolean, featured?: boolean, category?: string, limit?: number}} opts */
  entries(opts = {}) {
    const p = new URLSearchParams();
    if (opts.kind) p.set('kind', opts.kind);
    if (opts.random) p.set('random', 'true');
    if (opts.featured) p.set('featured', 'true');
    if (opts.category) p.set('category', opts.category);
    if (opts.limit) p.set('limit', String(opts.limit));
    const qs = p.toString();
    return request(`/api/entries${qs ? `?${qs}` : ''}`);
  },

  packages(opts = {}) {
    const qs = opts.featured ? '?featured=true' : '';
    return request(`/api/packages${qs}`);
  },

  package(slug) {
    return request(`/api/packages/${encodeURIComponent(slug)}`);
  },

  submitInquiry(payload) {
    return request('/api/contact', { method: 'POST', body: JSON.stringify(payload) });
  }
};
